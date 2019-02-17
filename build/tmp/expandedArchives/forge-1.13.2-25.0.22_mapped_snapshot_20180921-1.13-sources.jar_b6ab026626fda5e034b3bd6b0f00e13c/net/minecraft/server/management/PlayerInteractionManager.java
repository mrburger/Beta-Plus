package net.minecraft.server.management;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class PlayerInteractionManager {
   /** The world object that this object is connected to. */
   public World world;
   /** The EntityPlayerMP object that this object is connected to. */
   public EntityPlayerMP player;
   private GameType gameType = GameType.NOT_SET;
   /** True if the player is destroying a block */
   private boolean isDestroyingBlock;
   private int initialDamage;
   private BlockPos destroyPos = BlockPos.ORIGIN;
   private int ticks;
   /**
    * Set to true when the "finished destroying block" packet is received but the block wasn't fully damaged yet. The
    * block will not be destroyed while this is false.
    */
   private boolean receivedFinishDiggingPacket;
   private BlockPos delayedDestroyPos = BlockPos.ORIGIN;
   private int initialBlockDamage;
   private int durabilityRemainingOnBlock = -1;

   public PlayerInteractionManager(World worldIn) {
      this.world = worldIn;
   }

   public void setGameType(GameType type) {
      this.gameType = type;
      type.configurePlayerCapabilities(this.player.abilities);
      this.player.sendPlayerAbilities();
      this.player.server.getPlayerList().sendPacketToAllPlayers(new SPacketPlayerListItem(SPacketPlayerListItem.Action.UPDATE_GAME_MODE, this.player));
      this.world.updateAllPlayersSleepingFlag();
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public boolean survivalOrAdventure() {
      return this.gameType.isSurvivalOrAdventure();
   }

   /**
    * Get if we are in creative game mode.
    */
   public boolean isCreative() {
      return this.gameType.isCreative();
   }

   /**
    * if the gameType is currently NOT_SET then change it to par1
    */
   public void initializeGameType(GameType type) {
      if (this.gameType == GameType.NOT_SET) {
         this.gameType = type;
      }

      this.setGameType(this.gameType);
   }

   public void tick() {
      ++this.ticks;
      if (this.receivedFinishDiggingPacket) {
         int i = this.ticks - this.initialBlockDamage;
         IBlockState iblockstate = this.world.getBlockState(this.delayedDestroyPos);
         if (iblockstate.isAir(world, this.delayedDestroyPos)) {
            this.receivedFinishDiggingPacket = false;
         } else {
            float f = iblockstate.getPlayerRelativeBlockHardness(this.player, this.player.world, this.delayedDestroyPos) * (float)(i + 1);
            int j = (int)(f * 10.0F);
            if (j != this.durabilityRemainingOnBlock) {
               this.world.sendBlockBreakProgress(this.player.getEntityId(), this.delayedDestroyPos, j);
               this.durabilityRemainingOnBlock = j;
            }

            if (f >= 1.0F) {
               this.receivedFinishDiggingPacket = false;
               this.tryHarvestBlock(this.delayedDestroyPos);
            }
         }
      } else if (this.isDestroyingBlock) {
         IBlockState iblockstate1 = this.world.getBlockState(this.destroyPos);
         if (iblockstate1.isAir(world, this.destroyPos)) {
            this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, -1);
            this.durabilityRemainingOnBlock = -1;
            this.isDestroyingBlock = false;
         } else {
            int k = this.ticks - this.initialDamage;
            float f1 = iblockstate1.getPlayerRelativeBlockHardness(this.player, this.player.world, this.destroyPos) * (float)(k + 1); // Forge: Fix network break progress using wrong position
            int l = (int)(f1 * 10.0F);
            if (l != this.durabilityRemainingOnBlock) {
               this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, l);
               this.durabilityRemainingOnBlock = l;
            }
         }
      }

   }

   /**
    * If not creative, it calls sendBlockBreakProgress until the block is broken first. tryHarvestBlock can also be the
    * result of this call.
    */
   public void startDestroyBlock(BlockPos pos, EnumFacing side) {
      double reachDist = player.getAttribute(EntityPlayer.REACH_DISTANCE).getValue();
      net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event = net.minecraftforge.common.ForgeHooks.onLeftClickBlock(player, pos, side, net.minecraftforge.common.ForgeHooks.rayTraceEyeHitVec(player, reachDist + 1));
      if (event.isCanceled()) { // Restore block and te data
         player.connection.sendPacket(new SPacketBlockChange(world, pos));
         world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
         return;
      }
      if (this.isCreative()) {
         if (!this.world.extinguishFire((EntityPlayer)null, pos, side)) {
            this.tryHarvestBlock(pos);
         }

      } else {
         if (this.gameType.hasLimitedInteractions()) {
            if (this.gameType == GameType.SPECTATOR) {
               return;
            }

            if (!this.player.isAllowEdit()) {
               ItemStack itemstack = this.player.getHeldItemMainhand();
               if (itemstack.isEmpty()) {
                  return;
               }

               BlockWorldState blockworldstate = new BlockWorldState(this.world, pos, false);
               if (!itemstack.canDestroy(this.world.getTags(), blockworldstate)) {
                  return;
               }
            }
         }

         this.initialDamage = this.ticks;
         float f = 1.0F;
         IBlockState iblockstate = this.world.getBlockState(pos);
         if (!iblockstate.isAir(world, pos)) {
            if (event.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY) {
            iblockstate.onBlockClicked(this.world, pos, this.player);
               this.world.extinguishFire((EntityPlayer)null, pos, side);
            } else { // Restore block and te data
               player.connection.sendPacket(new SPacketBlockChange(world, pos));
               world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            }
            f = iblockstate.getPlayerRelativeBlockHardness(this.player, this.player.world, pos);
         }


         if (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY) {
            if (f >= 1.0F) { // Restore block and te data
               player.connection.sendPacket(new SPacketBlockChange(world, pos));
               world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            }
            return;
         }

         if (!iblockstate.isAir(world, pos) && f >= 1.0F) {
            this.tryHarvestBlock(pos);
         } else {
            this.isDestroyingBlock = true;
            this.destroyPos = pos;
            int i = (int)(f * 10.0F);
            this.world.sendBlockBreakProgress(this.player.getEntityId(), pos, i);
            this.player.connection.sendPacket(new SPacketBlockChange(this.world, pos));
            this.durabilityRemainingOnBlock = i;
         }

      }
   }

   public void stopDestroyBlock(BlockPos pos) {
      if (pos.equals(this.destroyPos)) {
         int i = this.ticks - this.initialDamage;
         IBlockState iblockstate = this.world.getBlockState(pos);
         if (!iblockstate.isAir(world, pos)) {
            float f = iblockstate.getPlayerRelativeBlockHardness(this.player, this.player.world, pos) * (float)(i + 1);
            if (f >= 0.7F) {
               this.isDestroyingBlock = false;
               this.world.sendBlockBreakProgress(this.player.getEntityId(), pos, -1);
               this.tryHarvestBlock(pos);
            } else if (!this.receivedFinishDiggingPacket) {
               this.isDestroyingBlock = false;
               this.receivedFinishDiggingPacket = true;
               this.delayedDestroyPos = pos;
               this.initialBlockDamage = this.initialDamage;
            }
         }
      }

   }

   /**
    * Stops the block breaking process
    */
   public void abortDestroyBlock() {
      this.isDestroyingBlock = false;
      this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, -1);
   }

   /**
    * Removes a block and triggers the appropriate events
    */
   private boolean removeBlock(BlockPos pos) {
      return removeBlock(pos, false);
   }
   private boolean removeBlock(BlockPos pos, boolean canHarvest) {
      IBlockState iblockstate = this.world.getBlockState(pos);
      boolean flag = iblockstate.removedByPlayer(world, pos, player, canHarvest, world.getFluidState(pos));

      if (flag) {
         iblockstate.getBlock().onPlayerDestroy(this.world, pos, iblockstate);
      }

      return flag;
   }

   /**
    * Attempts to harvest a block
    */
   public boolean tryHarvestBlock(BlockPos pos) {
      IBlockState iblockstate = this.world.getBlockState(pos);
      int exp = net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(world, gameType, player, pos);
      if (exp == -1) {
         return false;
      } else {
         TileEntity tileentity = this.world.getTileEntity(pos);
         Block block = iblockstate.getBlock();
         if ((block instanceof BlockCommandBlock || block instanceof BlockStructure) && !this.player.canUseCommandBlock()) {
            this.world.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
            return false;
         } else {
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.onBlockStartBreak(pos, player)) {
               return false;
            }

            if (this.gameType.hasLimitedInteractions()) {
               if (this.gameType == GameType.SPECTATOR) {
                  return false;
               }

               if (!this.player.isAllowEdit()) {
                  ItemStack itemstack = this.player.getHeldItemMainhand();
                  if (itemstack.isEmpty()) {
                     return false;
                  }

                  BlockWorldState blockworldstate = new BlockWorldState(this.world, pos, false);
                  if (!itemstack.canDestroy(this.world.getTags(), blockworldstate)) {
                     return false;
                  }
               }
            }

            boolean flag1 = false;
            if (!this.isCreative()) {
               ItemStack itemstack2 = this.player.getHeldItemMainhand();
               ItemStack copy = itemstack2.copy();
               boolean flag = iblockstate.canHarvestBlock(world, pos, player);
               itemstack2.onBlockDestroyed(this.world, iblockstate, pos, this.player);
               if (itemstack2.isEmpty() && !copy.isEmpty()) {
                  net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(this.player, copy, EnumHand.MAIN_HAND);
               }
               flag1 = this.removeBlock(pos);
               if (flag1 && flag) {
                  ItemStack itemstack1 = itemstack2.isEmpty() ? ItemStack.EMPTY : itemstack2.copy();
                  iblockstate.getBlock().harvestBlock(this.world, this.player, pos, iblockstate, tileentity, itemstack1);
               }
            } else {
               flag1 = this.removeBlock(pos);
            }

            if (!this.isCreative() && flag1 && exp > 0) { // Drop experience
               iblockstate.getBlock().dropXpOnBlockBreak(world, pos, exp);
            }

            return flag1;
         }
      }
   }

   public EnumActionResult processRightClick(EntityPlayer player, World worldIn, ItemStack stack, EnumHand hand) {
      if (this.gameType == GameType.SPECTATOR) {
         return EnumActionResult.PASS;
      } else if (player.getCooldownTracker().hasCooldown(stack.getItem())) {
         return EnumActionResult.PASS;
      } else {
         EnumActionResult cancelResult = net.minecraftforge.common.ForgeHooks.onItemRightClick(player, hand);
         if (cancelResult != null) return cancelResult;
         int i = stack.getCount();
         int j = stack.getDamage();
         ItemStack copyBeforeUse = stack.copy();
         ActionResult<ItemStack> actionresult = stack.useItemRightClick(worldIn, player, hand);
         ItemStack itemstack = actionresult.getResult();
         if (itemstack == stack && itemstack.getCount() == i && itemstack.getUseDuration() <= 0 && itemstack.getDamage() == j) {
            return actionresult.getType();
         } else if (actionresult.getType() == EnumActionResult.FAIL && itemstack.getUseDuration() > 0 && !player.isHandActive()) {
            return actionresult.getType();
         } else {
            player.setHeldItem(hand, itemstack);
            if (this.isCreative()) {
               itemstack.setCount(i);
               if (itemstack.isDamageable()) {
                  itemstack.setDamage(j);
               }
            }

            if (itemstack.isEmpty()) {
               net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, hand);
               player.setHeldItem(hand, ItemStack.EMPTY);
            }

            if (!player.isHandActive()) {
               ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
            }

            return actionresult.getType();
         }
      }
   }

   public EnumActionResult processRightClickBlock(EntityPlayer player, World worldIn, ItemStack stack, EnumHand hand, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ) {
      IBlockState iblockstate = worldIn.getBlockState(pos);
      if (this.gameType == GameType.SPECTATOR) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof ILockableContainer) {
            Block block = iblockstate.getBlock();
            ILockableContainer ilockablecontainer = (ILockableContainer)tileentity;
            if (ilockablecontainer instanceof TileEntityChest && block instanceof BlockChest) {
               ilockablecontainer = ((BlockChest)block).getContainer(iblockstate, worldIn, pos, false);
            }

            if (ilockablecontainer != null) {
               player.displayGUIChest(ilockablecontainer);
               return EnumActionResult.SUCCESS;
            }
         } else if (tileentity instanceof IInventory) {
            player.displayGUIChest((IInventory)tileentity);
            return EnumActionResult.SUCCESS;
         }

         return EnumActionResult.PASS;
      } else {
         double reachDist = player.getAttribute(EntityPlayer.REACH_DISTANCE).getValue();
         net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event = net.minecraftforge.common.ForgeHooks
                 .onRightClickBlock(player, hand, pos, facing, net.minecraftforge.common.ForgeHooks.rayTraceEyeHitVec(player, reachDist + 1));
         if (event.isCanceled()) return event.getCancellationResult();

         EnumActionResult result = EnumActionResult.PASS;
         if (event.getUseItem() != net.minecraftforge.eventbus.api.Event.Result.DENY) {
            result = stack.onItemUseFirst(new ItemUseContext(player, stack, pos, facing, hitX, hitY, hitZ));
            if (result != EnumActionResult.PASS) {
               return result;
            }
         }

         boolean flag = !(player.getHeldItemMainhand().doesSneakBypassUse(worldIn, pos, player) && player.getHeldItemOffhand().doesSneakBypassUse(worldIn, pos, player));
         boolean flag1 = player.isSneaking() && flag;

         if ((!flag1 || event.getUseBlock() == net.minecraftforge.eventbus.api.Event.Result.ALLOW) &&
              event.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY &&
              iblockstate.onBlockActivated(worldIn, pos, player, hand, facing, hitX, hitY, hitZ)) {
            result = EnumActionResult.SUCCESS;
         }

         if (!stack.isEmpty() && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
            ItemUseContext itemusecontext = new ItemUseContext(player, player.getHeldItem(hand), pos, facing, hitX, hitY, hitZ);
            if (this.isCreative()) {
               int i = stack.getCount();
               if ((result != EnumActionResult.SUCCESS && event.getUseItem() != net.minecraftforge.eventbus.api.Event.Result.DENY) ||
                   (result == EnumActionResult.SUCCESS && event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.ALLOW)) {
               EnumActionResult enumactionresult = stack.onItemUse(itemusecontext);
               stack.setCount(i);
               return enumactionresult;
               } else {
                  return result;
               }
            } else {
               if ((result != EnumActionResult.SUCCESS && event.getUseItem() != net.minecraftforge.eventbus.api.Event.Result.DENY) ||
                   (result == EnumActionResult.SUCCESS && event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.ALLOW)) {
                  ItemStack copyBeforeUse = stack.copy();
                  result = stack.onItemUse(itemusecontext);
                  if (stack.isEmpty()) {
                     net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, hand);
                  }
               }
               return result;
            }
         } else {
            return EnumActionResult.PASS;
         }
      }
   }

   /**
    * Sets the world instance.
    */
   public void setWorld(WorldServer serverWorld) {
      this.world = serverWorld;
   }
}