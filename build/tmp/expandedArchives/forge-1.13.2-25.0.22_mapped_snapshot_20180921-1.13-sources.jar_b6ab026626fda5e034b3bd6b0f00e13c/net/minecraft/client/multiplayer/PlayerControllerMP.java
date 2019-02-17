package net.minecraft.client.multiplayer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPickItem;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerControllerMP {
   /** The Minecraft instance. */
   private final Minecraft mc;
   private final NetHandlerPlayClient connection;
   private BlockPos currentBlock = new BlockPos(-1, -1, -1);
   /** The Item currently being used to destroy a block */
   private ItemStack currentItemHittingBlock = ItemStack.EMPTY;
   /** Current block damage (MP) */
   private float curBlockDamageMP;
   /** Tick counter, when it hits 4 it resets back to 0 and plays the step sound */
   private float stepSoundTickCounter;
   /** Delays the first damage on the block after the first click on the block */
   private int blockHitDelay;
   /** Tells if the player is hitting a block */
   private boolean isHittingBlock;
   /** Current game type for the player */
   private GameType currentGameType = GameType.SURVIVAL;
   /** Index of the current item held by the player in the inventory hotbar */
   private int currentPlayerItem;

   public PlayerControllerMP(Minecraft mcIn, NetHandlerPlayClient netHandler) {
      this.mc = mcIn;
      this.connection = netHandler;
   }

   public static void clickBlockCreative(Minecraft mcIn, PlayerControllerMP playerController, BlockPos pos, EnumFacing facing) {
      if (!mcIn.world.extinguishFire(mcIn.player, pos, facing)) {
         playerController.onPlayerDestroyBlock(pos);
      }

   }

   /**
    * Sets player capabilities depending on current gametype. params: player
    */
   public void setPlayerCapabilities(EntityPlayer player) {
      this.currentGameType.configurePlayerCapabilities(player.abilities);
   }

   /**
    * Sets the game type for the player.
    */
   public void setGameType(GameType type) {
      this.currentGameType = type;
      this.currentGameType.configurePlayerCapabilities(this.mc.player.abilities);
   }

   /**
    * Flips the player around.
    */
   public void flipPlayer(EntityPlayer playerIn) {
      playerIn.rotationYaw = -180.0F;
   }

   public boolean shouldDrawHUD() {
      return this.currentGameType.isSurvivalOrAdventure();
   }

   public boolean onPlayerDestroyBlock(BlockPos pos) {
      if (this.currentGameType.hasLimitedInteractions()) {
         if (this.currentGameType == GameType.SPECTATOR) {
            return false;
         }

         if (!this.mc.player.isAllowEdit()) {
            ItemStack itemstack = this.mc.player.getHeldItemMainhand();
            if (itemstack.isEmpty()) {
               return false;
            }

            BlockWorldState blockworldstate = new BlockWorldState(this.mc.world, pos, false);
            if (!itemstack.canDestroy(this.mc.world.getTags(), blockworldstate)) {
               return false;
            }
         }
      }

      if (mc.player.getHeldItemMainhand().onBlockStartBreak(pos, mc.player)) return false;

      World world = this.mc.world;
      IBlockState iblockstate = world.getBlockState(pos);
      if (!this.mc.player.getHeldItemMainhand().getItem().canPlayerBreakBlockWhileHolding(iblockstate, world, pos, this.mc.player)) {
         return false;
      } else {
         Block block = iblockstate.getBlock();
         if ((block instanceof BlockCommandBlock || block instanceof BlockStructure) && !this.mc.player.canUseCommandBlock()) {
            return false;
         } else if (iblockstate.isAir()) {
            return false;
         } else {
            IFluidState ifluidstate = world.getFluidState(pos);

            this.currentBlock = new BlockPos(this.currentBlock.getX(), -1, this.currentBlock.getZ());
            if (!this.currentGameType.isCreative()) {
               ItemStack itemstack1 = this.mc.player.getHeldItemMainhand();
               ItemStack copyBeforeUse = itemstack1.copy();
               if (!itemstack1.isEmpty()) {
                  itemstack1.onBlockDestroyed(world, iblockstate, pos, this.mc.player);
                  if (itemstack1.isEmpty()) {
                     net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(this.mc.player, copyBeforeUse, EnumHand.MAIN_HAND);
                     this.mc.player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                  }
               }
            }

            boolean flag = iblockstate.removedByPlayer(world, pos, mc.player, false, ifluidstate);
            if (flag) block.onPlayerDestroy(world, pos, iblockstate);
            return flag;
         }
      }
   }

   /**
    * Called when the player is hitting a block with an item.
    */
   public boolean clickBlock(BlockPos loc, EnumFacing face) {
      if (this.currentGameType.hasLimitedInteractions()) {
         if (this.currentGameType == GameType.SPECTATOR) {
            return false;
         }

         if (!this.mc.player.isAllowEdit()) {
            ItemStack itemstack = this.mc.player.getHeldItemMainhand();
            if (itemstack.isEmpty()) {
               return false;
            }

            BlockWorldState blockworldstate = new BlockWorldState(this.mc.world, loc, false);
            if (!itemstack.canDestroy(this.mc.world.getTags(), blockworldstate)) {
               return false;
            }
         }
      }

      if (!this.mc.world.getWorldBorder().contains(loc)) {
         return false;
      } else {
         if (this.currentGameType.isCreative()) {
            this.mc.getTutorial().onHitBlock(this.mc.world, loc, this.mc.world.getBlockState(loc), 1.0F);
            this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, loc, face));
            if (!net.minecraftforge.common.ForgeHooks.onLeftClickBlock(this.mc.player, loc, face, net.minecraftforge.common.ForgeHooks.rayTraceEyeHitVec(this.mc.player, getBlockReachDistance() + 1)).isCanceled())
            clickBlockCreative(this.mc, this, loc, face);
            this.blockHitDelay = 5;
         } else if (!this.isHittingBlock || !this.isHittingPosition(loc)) {
            if (this.isHittingBlock) {
               this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, face));
            }
            net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event = net.minecraftforge.common.ForgeHooks.onLeftClickBlock(this.mc.player, loc, face, net.minecraftforge.common.ForgeHooks.rayTraceEyeHitVec(this.mc.player, getBlockReachDistance() + 1));

            IBlockState iblockstate = this.mc.world.getBlockState(loc);
            this.mc.getTutorial().onHitBlock(this.mc.world, loc, iblockstate, 0.0F);
            this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, loc, face));
            boolean flag = !iblockstate.isAir();
            if (flag && this.curBlockDamageMP == 0.0F) {
               if (event.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY)
               iblockstate.onBlockClicked(this.mc.world, loc, this.mc.player);
            }

            if (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY) return true;
            if (flag && iblockstate.getPlayerRelativeBlockHardness(this.mc.player, this.mc.player.world, loc) >= 1.0F) {
               this.onPlayerDestroyBlock(loc);
            } else {
               this.isHittingBlock = true;
               this.currentBlock = loc;
               this.currentItemHittingBlock = this.mc.player.getHeldItemMainhand();
               this.curBlockDamageMP = 0.0F;
               this.stepSoundTickCounter = 0.0F;
               this.mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
            }
         }

         return true;
      }
   }

   /**
    * Resets current block damage
    */
   public void resetBlockRemoving() {
      if (this.isHittingBlock) {
         this.mc.getTutorial().onHitBlock(this.mc.world, this.currentBlock, this.mc.world.getBlockState(this.currentBlock), -1.0F);
         this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, EnumFacing.DOWN));
         this.isHittingBlock = false;
         this.curBlockDamageMP = 0.0F;
         this.mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), this.currentBlock, -1);
         this.mc.player.resetCooldown();
      }

   }

   public boolean onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing) {
      this.syncCurrentPlayItem();
      if (this.blockHitDelay > 0) {
         --this.blockHitDelay;
         return true;
      } else if (this.currentGameType.isCreative() && this.mc.world.getWorldBorder().contains(posBlock)) {
         this.blockHitDelay = 5;
         this.mc.getTutorial().onHitBlock(this.mc.world, posBlock, this.mc.world.getBlockState(posBlock), 1.0F);
         this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, posBlock, directionFacing));
         clickBlockCreative(this.mc, this, posBlock, directionFacing);
         return true;
      } else if (this.isHittingPosition(posBlock)) {
         IBlockState iblockstate = this.mc.world.getBlockState(posBlock);
         Block block = iblockstate.getBlock();
         if (iblockstate.isAir()) {
            this.isHittingBlock = false;
            return false;
         } else {
            this.curBlockDamageMP += iblockstate.getPlayerRelativeBlockHardness(this.mc.player, this.mc.player.world, posBlock);
            if (this.stepSoundTickCounter % 4.0F == 0.0F) {
               SoundType soundtype = iblockstate.getSoundType(mc.world, posBlock, mc.player);
               this.mc.getSoundHandler().play(new SimpleSound(soundtype.getHitSound(), SoundCategory.NEUTRAL, (soundtype.getVolume() + 1.0F) / 8.0F, soundtype.getPitch() * 0.5F, posBlock));
            }

            ++this.stepSoundTickCounter;
            this.mc.getTutorial().onHitBlock(this.mc.world, posBlock, iblockstate, MathHelper.clamp(this.curBlockDamageMP, 0.0F, 1.0F));
            if (this.curBlockDamageMP >= 1.0F) {
               this.isHittingBlock = false;
               this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, posBlock, directionFacing));
               this.onPlayerDestroyBlock(posBlock);
               this.curBlockDamageMP = 0.0F;
               this.stepSoundTickCounter = 0.0F;
               this.blockHitDelay = 5;
            }

            this.mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
            return true;
         }
      } else {
         return this.clickBlock(posBlock, directionFacing);
      }
   }

   /**
    * player reach distance = 4F
    */
   public float getBlockReachDistance() {
      float attrib = (float)mc.player.getAttribute(EntityPlayer.REACH_DISTANCE).getValue();
      return this.currentGameType.isCreative() ? attrib : attrib - 0.5F;
   }

   public void tick() {
      this.syncCurrentPlayItem();
      if (this.connection.getNetworkManager().isChannelOpen()) {
         this.connection.getNetworkManager().tick();
      } else {
         this.connection.getNetworkManager().handleDisconnection();
      }

   }

   private boolean isHittingPosition(BlockPos pos) {
      ItemStack itemstack = this.mc.player.getHeldItemMainhand();
      boolean flag = this.currentItemHittingBlock.isEmpty() && itemstack.isEmpty();
      if (!this.currentItemHittingBlock.isEmpty() && !itemstack.isEmpty()) {
         flag = !this.currentItemHittingBlock.shouldCauseBlockBreakReset(itemstack);
      }

      return pos.equals(this.currentBlock) && flag;
   }

   /**
    * Syncs the current player item with the server
    */
   private void syncCurrentPlayItem() {
      int i = this.mc.player.inventory.currentItem;
      if (i != this.currentPlayerItem) {
         this.currentPlayerItem = i;
         this.connection.sendPacket(new CPacketHeldItemChange(this.currentPlayerItem));
      }

   }

   public EnumActionResult processRightClickBlock(EntityPlayerSP player, WorldClient worldIn, BlockPos pos, EnumFacing direction, Vec3d vec, EnumHand hand) {
      this.syncCurrentPlayItem();
      if (!this.mc.world.getWorldBorder().contains(pos)) {
         return EnumActionResult.FAIL;
      } else {
         ItemStack itemstack = player.getHeldItem(hand);
         float f = (float)(vec.x - (double)pos.getX());
         float f1 = (float)(vec.y - (double)pos.getY());
         float f2 = (float)(vec.z - (double)pos.getZ());
         net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event = net.minecraftforge.common.ForgeHooks
                 .onRightClickBlock(player, hand, pos, direction, net.minecraftforge.common.ForgeHooks.rayTraceEyeHitVec(player, getBlockReachDistance() + 1));
         if (event.isCanceled()) {
            // Give the server a chance to fire event as well. That way server event is not dependant on client event.
            this.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
            return event.getCancellationResult();
         }
         if (this.currentGameType == GameType.SPECTATOR) {
            this.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
            return EnumActionResult.SUCCESS;
         } else {
            EnumActionResult ret = itemstack.onItemUseFirst(new ItemUseContext(player, player.getHeldItem(hand), pos, direction, f, f1, f2));
            if (ret != EnumActionResult.PASS) {
               // The server needs to process the item use as well. Otherwise onItemUseFirst won't ever be called on the server without causing weird bugs
               this.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
               return ret;
            }
            boolean flag = !(player.getHeldItemMainhand().doesSneakBypassUse(worldIn, pos, player) && player.getHeldItemOffhand().doesSneakBypassUse(worldIn, pos, player));
            boolean flag1 = player.isSneaking() && flag;
            if (!flag1 || event.getUseBlock() == net.minecraftforge.eventbus.api.Event.Result.ALLOW) {
               if (event.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY) {
                 if (worldIn.getBlockState(pos).onBlockActivated(worldIn, pos, player, hand, direction, f, f1, f2)) {
                    this.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
                    return EnumActionResult.SUCCESS;
                 }
               }
            }
            if (event.getUseItem() != net.minecraftforge.eventbus.api.Event.Result.DENY) {
               this.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
               if (!itemstack.isEmpty() && !player.getCooldownTracker().hasCooldown(itemstack.getItem())) {
                  ItemUseContext itemusecontext = new ItemUseContext(player, player.getHeldItem(hand), pos, direction, f, f1, f2);
                  EnumActionResult enumactionresult;
                  if (this.currentGameType.isCreative()) {
                     int i = itemstack.getCount();
                     enumactionresult = itemstack.onItemUse(itemusecontext);
                     itemstack.setCount(i);
                  } else {
                     ItemStack copyForUse = itemstack.copy();
                     enumactionresult = itemstack.onItemUse(itemusecontext);
                     if (itemstack.isEmpty()) net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyForUse, hand);
                  }

                  return enumactionresult;
               } else {
                  return EnumActionResult.PASS;
               }
            }
            return EnumActionResult.PASS;
         }
      }
   }

   public EnumActionResult processRightClick(EntityPlayer player, World worldIn, EnumHand hand) {
      if (this.currentGameType == GameType.SPECTATOR) {
         return EnumActionResult.PASS;
      } else {
         this.syncCurrentPlayItem();
         this.connection.sendPacket(new CPacketPlayerTryUseItem(hand));
         ItemStack itemstack = player.getHeldItem(hand);
         if (player.getCooldownTracker().hasCooldown(itemstack.getItem())) {
            return EnumActionResult.PASS;
         } else {
            EnumActionResult cancelResult = net.minecraftforge.common.ForgeHooks.onItemRightClick(player, hand);
            if (cancelResult != null) return cancelResult;
            int i = itemstack.getCount();
            ActionResult<ItemStack> actionresult = itemstack.useItemRightClick(worldIn, player, hand);
            ItemStack itemstack1 = actionresult.getResult();
            if (itemstack1 != itemstack || itemstack1.getCount() != i) {
               player.setHeldItem(hand, itemstack1);
               if (itemstack1.isEmpty()) net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, itemstack, hand);
            }

            return actionresult.getType();
         }
      }
   }

   public EntityPlayerSP createPlayer(World p_199681_1_, StatisticsManager p_199681_2_, RecipeBookClient p_199681_3_) {
      return new EntityPlayerSP(this.mc, p_199681_1_, this.connection, p_199681_2_, p_199681_3_);
   }

   /**
    * Attacks an entity
    */
   public void attackEntity(EntityPlayer playerIn, Entity targetEntity) {
      this.syncCurrentPlayItem();
      this.connection.sendPacket(new CPacketUseEntity(targetEntity));
      if (this.currentGameType != GameType.SPECTATOR) {
         playerIn.attackTargetEntityWithCurrentItem(targetEntity);
         playerIn.resetCooldown();
      }

   }

   /**
    * Handles right clicking an entity, sends a packet to the server.
    */
   public EnumActionResult interactWithEntity(EntityPlayer player, Entity target, EnumHand hand) {
      this.syncCurrentPlayItem();
      this.connection.sendPacket(new CPacketUseEntity(target, hand));
      if (this.currentGameType == GameType.SPECTATOR) return EnumActionResult.PASS; // don't fire for spectators to match non-specific EntityInteract
      EnumActionResult cancelResult = net.minecraftforge.common.ForgeHooks.onInteractEntity(player, target, hand);
      if(cancelResult != null) return cancelResult;
      return this.currentGameType == GameType.SPECTATOR ? EnumActionResult.PASS : player.interactOn(target, hand);
   }

   /**
    * Handles right clicking an entity from the entities side, sends a packet to the server.
    */
   public EnumActionResult interactWithEntity(EntityPlayer player, Entity target, RayTraceResult ray, EnumHand hand) {
      this.syncCurrentPlayItem();
      Vec3d vec3d = new Vec3d(ray.hitVec.x - target.posX, ray.hitVec.y - target.posY, ray.hitVec.z - target.posZ);
      this.connection.sendPacket(new CPacketUseEntity(target, hand, vec3d));
      if (this.currentGameType == GameType.SPECTATOR) return EnumActionResult.PASS; // don't fire for spectators to match non-specific EntityInteract
      EnumActionResult cancelResult = net.minecraftforge.common.ForgeHooks.onInteractEntityAt(player, target, ray, hand);
      if(cancelResult != null) return cancelResult;
      return this.currentGameType == GameType.SPECTATOR ? EnumActionResult.PASS : target.applyPlayerInteraction(player, vec3d, hand);
   }

   /**
    * Handles slot clicks, sends a packet to the server.
    */
   public ItemStack windowClick(int windowId, int slotId, int mouseButton, ClickType type, EntityPlayer player) {
      short short1 = player.openContainer.getNextTransactionID(player.inventory);
      ItemStack itemstack = player.openContainer.slotClick(slotId, mouseButton, type, player);
      this.connection.sendPacket(new CPacketClickWindow(windowId, slotId, mouseButton, type, itemstack, short1));
      return itemstack;
   }

   public void func_203413_a(int p_203413_1_, IRecipe p_203413_2_, boolean p_203413_3_) {
      this.connection.sendPacket(new CPacketPlaceRecipe(p_203413_1_, p_203413_2_, p_203413_3_));
   }

   /**
    * GuiEnchantment uses this during multiplayer to tell PlayerControllerMP to send a packet indicating the enchantment
    * action the player has taken.
    */
   public void sendEnchantPacket(int windowID, int button) {
      this.connection.sendPacket(new CPacketEnchantItem(windowID, button));
   }

   /**
    * Used in PlayerControllerMP to update the server with an ItemStack in a slot.
    */
   public void sendSlotPacket(ItemStack itemStackIn, int slotId) {
      if (this.currentGameType.isCreative()) {
         this.connection.sendPacket(new CPacketCreativeInventoryAction(slotId, itemStackIn));
      }

   }

   /**
    * Sends a Packet107 to the server to drop the item on the ground
    */
   public void sendPacketDropItem(ItemStack itemStackIn) {
      if (this.currentGameType.isCreative() && !itemStackIn.isEmpty()) {
         this.connection.sendPacket(new CPacketCreativeInventoryAction(-1, itemStackIn));
      }

   }

   public void onStoppedUsingItem(EntityPlayer playerIn) {
      this.syncCurrentPlayItem();
      this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      playerIn.stopActiveHand();
   }

   public boolean gameIsSurvivalOrAdventure() {
      return this.currentGameType.isSurvivalOrAdventure();
   }

   /**
    * Checks if the player is not creative, used for checking if it should break a block instantly
    */
   public boolean isNotCreative() {
      return !this.currentGameType.isCreative();
   }

   /**
    * returns true if player is in creative mode
    */
   public boolean isInCreativeMode() {
      return this.currentGameType.isCreative();
   }

   /**
    * true for hitting entities far away.
    */
   public boolean extendedReach() {
      return this.currentGameType.isCreative();
   }

   /**
    * Checks if the player is riding a horse, used to chose the GUI to open
    */
   public boolean isRidingHorse() {
      return this.mc.player.isPassenger() && this.mc.player.getRidingEntity() instanceof AbstractHorse;
   }

   public boolean isSpectatorMode() {
      return this.currentGameType == GameType.SPECTATOR;
   }

   public GameType getCurrentGameType() {
      return this.currentGameType;
   }

   /**
    * Return isHittingBlock
    */
   public boolean getIsHittingBlock() {
      return this.isHittingBlock;
   }

   public void pickItem(int index) {
      this.connection.sendPacket(new CPacketPickItem(index));
   }
}