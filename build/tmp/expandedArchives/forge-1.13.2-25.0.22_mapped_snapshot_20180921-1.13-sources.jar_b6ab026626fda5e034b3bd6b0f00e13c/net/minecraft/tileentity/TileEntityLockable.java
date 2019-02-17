package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public abstract class TileEntityLockable extends TileEntity implements ILockableContainer {
   private LockCode code = LockCode.EMPTY_CODE;

   protected TileEntityLockable(TileEntityType<?> typeIn) {
      super(typeIn);
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      this.code = LockCode.fromNBT(compound);
   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      if (this.code != null) {
         this.code.toNBT(compound);
      }

      return compound;
   }

   public boolean isLocked() {
      return this.code != null && !this.code.isEmpty();
   }

   public LockCode getLockCode() {
      return this.code;
   }

   public void setLockCode(LockCode code) {
      this.code = code;
   }

   private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> createUnSidedHandler());
   protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
      return new net.minecraftforge.items.wrapper.InvWrapper(this);
   }

   @javax.annotation.Nullable
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, @javax.annotation.Nullable net.minecraft.util.EnumFacing side) {
      if (!this.removed && cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ) {
         return itemHandler.cast();
      }
      return super.getCapability(cap, side);
   }

   /**
    * invalidates a tile entity
    */
   @Override
   public void remove() {
      super.remove();
      itemHandler.invalidate();
   }
}