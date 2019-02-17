package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.EnumFacing;

public class VoxelShapeSplit extends VoxelShape {
   private final VoxelShape shape;
   private final EnumFacing.Axis axis;
   private final DoubleList field_197778_c = new DoubleRangeList(1);

   public VoxelShapeSplit(VoxelShape shapeIn, EnumFacing.Axis axis, int p_i47682_3_) {
      super(makeShapePart(shapeIn.part, axis, p_i47682_3_));
      this.shape = shapeIn;
      this.axis = axis;
   }

   private static VoxelShapePart makeShapePart(VoxelShapePart shapePartIn, EnumFacing.Axis axis, int p_197775_2_) {
      return new VoxelShapePartSplit(shapePartIn, axis.getCoordinate(p_197775_2_, 0, 0), axis.getCoordinate(0, p_197775_2_, 0), axis.getCoordinate(0, 0, p_197775_2_), axis.getCoordinate(p_197775_2_ + 1, shapePartIn.xSize, shapePartIn.xSize), axis.getCoordinate(shapePartIn.ySize, p_197775_2_ + 1, shapePartIn.ySize), axis.getCoordinate(shapePartIn.zSize, shapePartIn.zSize, p_197775_2_ + 1));
   }

   protected DoubleList getValues(EnumFacing.Axis axis) {
      return axis == this.axis ? this.field_197778_c : this.shape.getValues(axis);
   }
}