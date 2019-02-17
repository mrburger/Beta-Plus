package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityChest extends TileEntityLockableLoot implements IChestLid, ITickable {
   private NonNullList<ItemStack> chestContents = NonNullList.withSize(27, ItemStack.EMPTY);
   /** The current angle of the lid (between 0 and 1) */
   protected float lidAngle;
   /** The angle of the lid last tick */
   protected float prevLidAngle;
   /** The number of players currently using this chest */
   protected int numPlayersUsing;
   /**
    * A counter that is incremented once each tick. Used to determine when to recompute {@link #numPlayersUsing}; this
    * is done every 200 ticks (but staggered between different chests). However, the new value isn't actually sent to
    * clients when it is changed.
    */
   private int ticksSinceSync;
   private net.minecraftforge.common.util.LazyOptional<net.minecraftforge.items.IItemHandlerModifiable> chestHandler;

   protected TileEntityChest(TileEntityType<?> typeIn) {
      super(typeIn);
   }

   public TileEntityChest() {
      this(TileEntityType.CHEST);
   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return 27;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.chestContents) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      return (ITextComponent)(itextcomponent != null ? itextcomponent : new TextComponentTranslation("container.chest"));
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(compound)) {
         ItemStackHelper.loadAllItems(compound, this.chestContents);
      }

      if (compound.contains("CustomName", 8)) {
         this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
      }

   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      if (!this.checkLootAndWrite(compound)) {
         ItemStackHelper.saveAllItems(compound, this.chestContents);
      }

      ITextComponent itextcomponent = this.getCustomName();
      if (itextcomponent != null) {
         compound.setString("CustomName", ITextComponent.Serializer.toJson(itextcomponent));
      }

      return compound;
   }

   /**
    * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
    */
   public int getInventoryStackLimit() {
      return 64;
   }

   public void tick() {
      int i = this.pos.getX();
      int j = this.pos.getY();
      int k = this.pos.getZ();
      ++this.ticksSinceSync;
      if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
         this.numPlayersUsing = 0;
         float f = 5.0F;

         for(EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double)((float)i - 5.0F), (double)((float)j - 5.0F), (double)((float)k - 5.0F), (double)((float)(i + 1) + 5.0F), (double)((float)(j + 1) + 5.0F), (double)((float)(k + 1) + 5.0F)))) {
            if (entityplayer.openContainer instanceof ContainerChest) {
               IInventory iinventory = ((ContainerChest)entityplayer.openContainer).getLowerChestInventory();
               if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest)iinventory).isPartOfLargeChest(this)) {
                  ++this.numPlayersUsing;
               }
            }
         }
      }

      this.prevLidAngle = this.lidAngle;
      float f1 = 0.1F;
      if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
         this.playSound(SoundEvents.BLOCK_CHEST_OPEN);
      }

      if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
         float f2 = this.lidAngle;
         if (this.numPlayersUsing > 0) {
            this.lidAngle += 0.1F;
         } else {
            this.lidAngle -= 0.1F;
         }

         if (this.lidAngle > 1.0F) {
            this.lidAngle = 1.0F;
         }

         float f3 = 0.5F;
         if (this.lidAngle < 0.5F && f2 >= 0.5F) {
            this.playSound(SoundEvents.BLOCK_CHEST_CLOSE);
         }

         if (this.lidAngle < 0.0F) {
            this.lidAngle = 0.0F;
         }
      }

   }

   private void playSound(SoundEvent soundIn) {
      ChestType chesttype = this.getBlockState().get(BlockChest.TYPE);
      if (chesttype != ChestType.LEFT) {
         double d0 = (double)this.pos.getX() + 0.5D;
         double d1 = (double)this.pos.getY() + 0.5D;
         double d2 = (double)this.pos.getZ() + 0.5D;
         if (chesttype == ChestType.RIGHT) {
            EnumFacing enumfacing = BlockChest.getDirectionToAttached(this.getBlockState());
            d0 += (double)enumfacing.getXOffset() * 0.5D;
            d2 += (double)enumfacing.getZOffset() * 0.5D;
         }

         this.world.playSound((EntityPlayer)null, d0, d1, d2, soundIn, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
      }
   }

   /**
    * See {@link Block#eventReceived} for more information. This must return true serverside before it is called
    * clientside.
    */
   public boolean receiveClientEvent(int id, int type) {
      if (id == 1) {
         this.numPlayersUsing = type;
         return true;
      } else {
         return super.receiveClientEvent(id, type);
      }
   }

   public void openInventory(EntityPlayer player) {
      if (!player.isSpectator()) {
         if (this.numPlayersUsing < 0) {
            this.numPlayersUsing = 0;
         }

         ++this.numPlayersUsing;
         this.onOpenOrClose();
      }

   }

   public void closeInventory(EntityPlayer player) {
      if (!player.isSpectator()) {
         --this.numPlayersUsing;
         this.onOpenOrClose();
      }

   }

   protected void onOpenOrClose() {
      Block block = this.getBlockState().getBlock();
      if (block instanceof BlockChest) {
         this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
         this.world.notifyNeighborsOfStateChange(this.pos, block);
      }

   }

   public String getGuiID() {
      return "minecraft:chest";
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      this.fillWithLoot(playerIn);
      return new ContainerChest(playerInventory, this, playerIn);
   }

   protected NonNullList<ItemStack> getItems() {
      return this.chestContents;
   }

   protected void setItems(NonNullList<ItemStack> itemsIn) {
      this.chestContents = itemsIn;
   }

   @OnlyIn(Dist.CLIENT)
   public float getLidAngle(float partialTicks) {
      return this.prevLidAngle + (this.lidAngle - this.prevLidAngle) * partialTicks;
   }

   public static int getPlayersUsing(IBlockReader reader, BlockPos posIn) {
      IBlockState iblockstate = reader.getBlockState(posIn);
      if (iblockstate.hasTileEntity()) {
         TileEntity tileentity = reader.getTileEntity(posIn);
         if (tileentity instanceof TileEntityChest) {
            return ((TileEntityChest)tileentity).numPlayersUsing;
         }
      }

      return 0;
   }

   public static void swapContents(TileEntityChest chest, TileEntityChest otherChest) {
      NonNullList<ItemStack> nonnulllist = chest.getItems();
      chest.setItems(otherChest.getItems());
      otherChest.setItems(nonnulllist);
   }

   @Override
   public void updateContainingBlockInfo() {
      super.updateContainingBlockInfo();
      if (this.chestHandler != null) {
         this.chestHandler.invalidate();
         this.chestHandler = null;
      }
   }

   @Override
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, EnumFacing side) {
       if (!this.removed && cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
          if (this.chestHandler == null) {
             this.chestHandler = net.minecraftforge.common.util.LazyOptional.of(this::createHandler);
          }
          return this.chestHandler.cast();
       }
       return super.getCapability(cap, side);
   }

   private net.minecraftforge.items.IItemHandlerModifiable createHandler() {
      IBlockState state = this.getBlockState();
      if (!(state.getBlock() instanceof BlockChest)) {
         return new net.minecraftforge.items.wrapper.InvWrapper(this);
      }
      ChestType type = state.get(BlockChest.TYPE);
      if (type != ChestType.SINGLE) {
         BlockPos opos = this.getPos().offset(BlockChest.getDirectionToAttached(state));
         IBlockState ostate = this.getWorld().getBlockState(opos);
         if (state.getBlock() == ostate.getBlock()) {
            ChestType otype = ostate.get(BlockChest.TYPE);
            if (otype != ChestType.SINGLE && type != otype && state.get(BlockChest.FACING) == ostate.get(BlockChest.FACING)) {
               TileEntity ote = this.getWorld().getTileEntity(opos);
               if (ote instanceof TileEntityChest) {
                  IInventory top    = type == ChestType.RIGHT ? this : (IInventory)ote;
                  IInventory bottom = type == ChestType.RIGHT ? (IInventory)ote : this;
                  return new net.minecraftforge.items.wrapper.CombinedInvWrapper(
                     new net.minecraftforge.items.wrapper.InvWrapper(top),
                     new net.minecraftforge.items.wrapper.InvWrapper(bottom));
               }
            }
         }
      }
      return new net.minecraftforge.items.wrapper.InvWrapper(this);
   }

   /**
    * invalidates a tile entity
    */
   @Override
   public void remove() {
      super.remove();
      if (chestHandler != null)
        chestHandler.invalidate();
   }
}