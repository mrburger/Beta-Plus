package net.minecraft.util.math.shapes;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class VoxelShape {
   protected final VoxelShapePart part;

   VoxelShape(VoxelShapePart part) {
      this.part = part;
   }

   public double getStart(EnumFacing.Axis axis) {
      int i = this.part.getStart(axis);
      return i >= this.part.getSize(axis) ? Double.POSITIVE_INFINITY : this.getValueUnchecked(axis, i);
   }

   public double getEnd(EnumFacing.Axis axis) {
      int i = this.part.getEnd(axis);
      return i <= 0 ? Double.NEGATIVE_INFINITY : this.getValueUnchecked(axis, i);
   }

   public AxisAlignedBB getBoundingBox() {
      if (this.isEmpty()) {
         throw new UnsupportedOperationException("No bounds for empty shape.");
      } else {
         return new AxisAlignedBB(this.getStart(EnumFacing.Axis.X), this.getStart(EnumFacing.Axis.Y), this.getStart(EnumFacing.Axis.Z), this.getEnd(EnumFacing.Axis.X), this.getEnd(EnumFacing.Axis.Y), this.getEnd(EnumFacing.Axis.Z));
      }
   }

   protected double getValueUnchecked(EnumFacing.Axis axis, int index) {
      return this.getValues(axis).getDouble(index);
   }

   protected abstract DoubleList getValues(EnumFacing.Axis axis);

   public boolean isEmpty() {
      return this.part.isEmpty();
   }

   public VoxelShape withOffset(double xOffset, double yOffset, double zOffset) {
      return (VoxelShape)(this.isEmpty() ? VoxelShapes.empty() : new VoxelShapeArray(this.part, (DoubleList)(new OffsetDoubleList(this.getValues(EnumFacing.Axis.X), xOffset)), (DoubleList)(new OffsetDoubleList(this.getValues(EnumFacing.Axis.Y), yOffset)), (DoubleList)(new OffsetDoubleList(this.getValues(EnumFacing.Axis.Z), zOffset))));
   }

   public VoxelShape simplify() {
      VoxelShape[] avoxelshape = new VoxelShape[]{VoxelShapes.empty()};
      this.forEachBox((p_197763_1_, p_197763_3_, p_197763_5_, p_197763_7_, p_197763_9_, p_197763_11_) -> {
         avoxelshape[0] = VoxelShapes.combine(avoxelshape[0], VoxelShapes.create(p_197763_1_, p_197763_3_, p_197763_5_, p_197763_7_, p_197763_9_, p_197763_11_), IBooleanFunction.OR);
      });
      return avoxelshape[0];
   }

   @OnlyIn(Dist.CLIENT)
   public void forEachEdge(VoxelShapes.LineConsumer action) {
      this.part.forEachEdge((p_197750_2_, p_197750_3_, p_197750_4_, p_197750_5_, p_197750_6_, p_197750_7_) -> {
         action.consume(this.getValueUnchecked(EnumFacing.Axis.X, p_197750_2_), this.getValueUnchecked(EnumFacing.Axis.Y, p_197750_3_), this.getValueUnchecked(EnumFacing.Axis.Z, p_197750_4_), this.getValueUnchecked(EnumFacing.Axis.X, p_197750_5_), this.getValueUnchecked(EnumFacing.Axis.Y, p_197750_6_), this.getValueUnchecked(EnumFacing.Axis.Z, p_197750_7_));
      }, true);
   }

   public void forEachBox(VoxelShapes.LineConsumer action) {
      this.part.forEachBox((p_197765_2_, p_197765_3_, p_197765_4_, p_197765_5_, p_197765_6_, p_197765_7_) -> {
         action.consume(this.getValueUnchecked(EnumFacing.Axis.X, p_197765_2_), this.getValueUnchecked(EnumFacing.Axis.Y, p_197765_3_), this.getValueUnchecked(EnumFacing.Axis.Z, p_197765_4_), this.getValueUnchecked(EnumFacing.Axis.X, p_197765_5_), this.getValueUnchecked(EnumFacing.Axis.Y, p_197765_6_), this.getValueUnchecked(EnumFacing.Axis.Z, p_197765_7_));
      }, true);
   }

   public List<AxisAlignedBB> toBoundingBoxList() {
      List<AxisAlignedBB> list = Lists.newArrayList();
      this.forEachBox((p_203431_1_, p_203431_3_, p_203431_5_, p_203431_7_, p_203431_9_, p_203431_11_) -> {
         list.add(new AxisAlignedBB(p_203431_1_, p_203431_3_, p_203431_5_, p_203431_7_, p_203431_9_, p_203431_11_));
      });
      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public double min(EnumFacing.Axis axis, double p_197764_2_, double p_197764_4_) {
      EnumFacing.Axis enumfacing$axis = AxisRotation.FORWARD.rotate(axis);
      EnumFacing.Axis enumfacing$axis1 = AxisRotation.BACKWARD.rotate(axis);
      int i = this.getClosestIndex(enumfacing$axis, p_197764_2_);
      int j = this.getClosestIndex(enumfacing$axis1, p_197764_4_);
      int k = this.part.firstFilled(axis, i, j);
      return k >= this.part.getSize(axis) ? Double.POSITIVE_INFINITY : this.getValueUnchecked(axis, k);
   }

   @OnlyIn(Dist.CLIENT)
   public double max(EnumFacing.Axis p_197760_1_, double p_197760_2_, double p_197760_4_) {
      EnumFacing.Axis enumfacing$axis = AxisRotation.FORWARD.rotate(p_197760_1_);
      EnumFacing.Axis enumfacing$axis1 = AxisRotation.BACKWARD.rotate(p_197760_1_);
      int i = this.getClosestIndex(enumfacing$axis, p_197760_2_);
      int j = this.getClosestIndex(enumfacing$axis1, p_197760_4_);
      int k = this.part.lastFilled(p_197760_1_, i, j);
      return k <= 0 ? Double.NEGATIVE_INFINITY : this.getValueUnchecked(p_197760_1_, k);
   }

   protected int getClosestIndex(EnumFacing.Axis axis, double position) {
      return MathHelper.binarySearch(0, this.part.getSize(axis) + 1, (p_197761_4_) -> {
         if (p_197761_4_ < 0) {
            return false;
         } else if (p_197761_4_ > this.part.getSize(axis)) {
            return true;
         } else {
            return position < this.getValueUnchecked(axis, p_197761_4_);
         }
      }) - 1;
   }

   protected boolean contains(double x, double y, double z) {
      return this.part.contains(this.getClosestIndex(EnumFacing.Axis.X, x), this.getClosestIndex(EnumFacing.Axis.Y, y), this.getClosestIndex(EnumFacing.Axis.Z, z));
   }

   @Nullable
   public RayTraceResult func_212433_a(Vec3d p_212433_1_, Vec3d p_212433_2_, BlockPos p_212433_3_) {
      if (this.isEmpty()) {
         return null;
      } else {
         Vec3d vec3d = p_212433_2_.subtract(p_212433_1_);
         if (vec3d.lengthSquared() < 1.0E-7D) {
            return null;
         } else {
            Vec3d vec3d1 = p_212433_1_.add(vec3d.scale(0.001D));
            Vec3d vec3d2 = p_212433_1_.add(vec3d.scale(0.001D)).subtract((double)p_212433_3_.getX(), (double)p_212433_3_.getY(), (double)p_212433_3_.getZ());
            return this.contains(vec3d2.x, vec3d2.y, vec3d2.z) ? new RayTraceResult(vec3d1, EnumFacing.getFacingFromVector(vec3d.x, vec3d.y, vec3d.z), p_212433_3_) : AxisAlignedBB.rayTrace(this.toBoundingBoxList(), p_212433_1_, p_212433_2_, p_212433_3_);
         }
      }
   }

   public VoxelShape func_212434_a(EnumFacing p_212434_1_) {
      if (!this.isEmpty() && this != VoxelShapes.fullCube()) {
         EnumFacing.Axis enumfacing$axis = p_212434_1_.getAxis();
         EnumFacing.AxisDirection enumfacing$axisdirection = p_212434_1_.getAxisDirection();
         DoubleList doublelist = this.getValues(enumfacing$axis);
         if (doublelist.size() == 2 && DoubleMath.fuzzyEquals(doublelist.getDouble(0), 0.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(doublelist.getDouble(1), 1.0D, 1.0E-7D)) {
            return this;
         } else {
            int i = this.getClosestIndex(enumfacing$axis, enumfacing$axisdirection == EnumFacing.AxisDirection.POSITIVE ? 0.9999999D : 1.0E-7D);
            return new VoxelShapeSplit(this, enumfacing$axis, i);
         }
      } else {
         return this;
      }
   }

   public double func_212430_a(EnumFacing.Axis p_212430_1_, AxisAlignedBB p_212430_2_, double p_212430_3_) {
      return this.func_212431_a(AxisRotation.from(p_212430_1_, EnumFacing.Axis.X), p_212430_2_, p_212430_3_);
   }

   protected double func_212431_a(AxisRotation p_212431_1_, AxisAlignedBB p_212431_2_, double p_212431_3_) {
      if (this.isEmpty()) {
         return p_212431_3_;
      } else if (Math.abs(p_212431_3_) < 1.0E-7D) {
         return 0.0D;
      } else {
         AxisRotation axisrotation = p_212431_1_.reverse();
         EnumFacing.Axis enumfacing$axis = axisrotation.rotate(EnumFacing.Axis.X);
         EnumFacing.Axis enumfacing$axis1 = axisrotation.rotate(EnumFacing.Axis.Y);
         EnumFacing.Axis enumfacing$axis2 = axisrotation.rotate(EnumFacing.Axis.Z);
         double d0 = p_212431_2_.getMax(enumfacing$axis);
         double d1 = p_212431_2_.getMin(enumfacing$axis);
         int i = this.getClosestIndex(enumfacing$axis, d1 + 1.0E-7D);
         int j = this.getClosestIndex(enumfacing$axis, d0 - 1.0E-7D);
         int k = Math.max(0, this.getClosestIndex(enumfacing$axis1, p_212431_2_.getMin(enumfacing$axis1) + 1.0E-7D));
         int l = Math.min(this.part.getSize(enumfacing$axis1), this.getClosestIndex(enumfacing$axis1, p_212431_2_.getMax(enumfacing$axis1) - 1.0E-7D) + 1);
         int i1 = Math.max(0, this.getClosestIndex(enumfacing$axis2, p_212431_2_.getMin(enumfacing$axis2) + 1.0E-7D));
         int j1 = Math.min(this.part.getSize(enumfacing$axis2), this.getClosestIndex(enumfacing$axis2, p_212431_2_.getMax(enumfacing$axis2) - 1.0E-7D) + 1);
         int k1 = this.part.getSize(enumfacing$axis);
         if (p_212431_3_ > 0.0D) {
            for(int l1 = j + 1; l1 < k1; ++l1) {
               for(int i2 = k; i2 < l; ++i2) {
                  for(int j2 = i1; j2 < j1; ++j2) {
                     if (this.part.containsWithRotation(axisrotation, l1, i2, j2)) {
                        double d2 = this.getValueUnchecked(enumfacing$axis, l1) - d0;
                        if (d2 >= -1.0E-7D) {
                           p_212431_3_ = Math.min(p_212431_3_, d2);
                        }

                        return p_212431_3_;
                     }
                  }
               }
            }
         } else if (p_212431_3_ < 0.0D) {
            for(int k2 = i - 1; k2 >= 0; --k2) {
               for(int l2 = k; l2 < l; ++l2) {
                  for(int i3 = i1; i3 < j1; ++i3) {
                     if (this.part.containsWithRotation(axisrotation, k2, l2, i3)) {
                        double d3 = this.getValueUnchecked(enumfacing$axis, k2 + 1) - d1;
                        if (d3 <= 1.0E-7D) {
                           p_212431_3_ = Math.max(p_212431_3_, d3);
                        }

                        return p_212431_3_;
                     }
                  }
               }
            }
         }

         return p_212431_3_;
      }
   }

   public String toString() {
      return this.isEmpty() ? "EMPTY" : "VoxelShape[" + this.getBoundingBox() + "]";
   }
}