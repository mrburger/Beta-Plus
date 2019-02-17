package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.EnumFacing;

public final class VoxelShapeInt extends VoxelShape {
   private final int x;
   private final int y;
   private final int z;

   public VoxelShapeInt(VoxelShapePart p_i47679_1_, int x, int y, int z) {
      super(p_i47679_1_);
      this.x = x;
      this.y = y;
      this.z = z;
   }

   protected DoubleList getValues(EnumFacing.Axis axis) {
      return new IntRangeList(this.part.getSize(axis), axis.getCoordinate(this.x, this.y, this.z));
   }
}