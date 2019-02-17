package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityComparator extends TileEntity {
   private int outputSignal;

   public TileEntityComparator() {
      super(TileEntityType.COMPARATOR);
   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      compound.setInt("OutputSignal", this.outputSignal);
      return compound;
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      this.outputSignal = compound.getInt("OutputSignal");
   }

   public int getOutputSignal() {
      return this.outputSignal;
   }

   public void setOutputSignal(int outputSignalIn) {
      this.outputSignal = outputSignalIn;
   }
}