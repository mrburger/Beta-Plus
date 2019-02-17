package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.EnumFacing;

final class VoxelShapeCube extends VoxelShape {
   VoxelShapeCube(VoxelShapePart p_i48182_1_) {
      super(p_i48182_1_);
   }

   protected DoubleList getValues(EnumFacing.Axis axis) {
      return new DoubleRangeList(this.part.getSize(axis));
   }
}