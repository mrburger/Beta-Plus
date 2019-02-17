package net.minecraft.util.math.shapes;

import net.minecraft.util.AxisRotation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class VoxelShapePart {
   private static final EnumFacing.Axis[] AXIS_VALUES = EnumFacing.Axis.values();
   protected final int xSize;
   protected final int ySize;
   protected final int zSize;

   protected VoxelShapePart(int xIn, int yIn, int zIn) {
      this.xSize = xIn;
      this.ySize = yIn;
      this.zSize = zIn;
   }

   public boolean containsWithRotation(AxisRotation p_197824_1_, int x, int y, int z) {
      return this.contains(p_197824_1_.getCoordinate(x, y, z, EnumFacing.Axis.X), p_197824_1_.getCoordinate(x, y, z, EnumFacing.Axis.Y), p_197824_1_.getCoordinate(x, y, z, EnumFacing.Axis.Z));
   }

   public boolean contains(int x, int y, int z) {
      if (x >= 0 && y >= 0 && z >= 0) {
         return x < this.xSize && y < this.ySize && z < this.zSize ? this.isFilled(x, y, z) : false;
      } else {
         return false;
      }
   }

   public boolean isFilledWithRotation(AxisRotation rotationIn, int x, int y, int z) {
      return this.isFilled(rotationIn.getCoordinate(x, y, z, EnumFacing.Axis.X), rotationIn.getCoordinate(x, y, z, EnumFacing.Axis.Y), rotationIn.getCoordinate(x, y, z, EnumFacing.Axis.Z));
   }

   public abstract boolean isFilled(int x, int y, int z);

   public abstract void setFilled(int x, int y, int z, boolean expandBounds, boolean filled);

   public boolean isEmpty() {
      for(EnumFacing.Axis enumfacing$axis : AXIS_VALUES) {
         if (this.getStart(enumfacing$axis) >= this.getEnd(enumfacing$axis)) {
            return true;
         }
      }

      return false;
   }

   public abstract int getStart(EnumFacing.Axis axis);

   public abstract int getEnd(EnumFacing.Axis axis);

   /**
    * gives the index of the first filled part in the column
    */
   @OnlyIn(Dist.CLIENT)
   public int firstFilled(EnumFacing.Axis p_197826_1_, int p_197826_2_, int p_197826_3_) {
      int i = this.getSize(p_197826_1_);
      if (p_197826_2_ >= 0 && p_197826_3_ >= 0) {
         EnumFacing.Axis enumfacing$axis = AxisRotation.FORWARD.rotate(p_197826_1_);
         EnumFacing.Axis enumfacing$axis1 = AxisRotation.BACKWARD.rotate(p_197826_1_);
         if (p_197826_2_ < this.getSize(enumfacing$axis) && p_197826_3_ < this.getSize(enumfacing$axis1)) {
            AxisRotation axisrotation = AxisRotation.from(EnumFacing.Axis.X, p_197826_1_);

            for(int j = 0; j < i; ++j) {
               if (this.isFilledWithRotation(axisrotation, j, p_197826_2_, p_197826_3_)) {
                  return j;
               }
            }

            return i;
         } else {
            return i;
         }
      } else {
         return i;
      }
   }

   /**
    * gives the index of the last filled part in the column
    */
   @OnlyIn(Dist.CLIENT)
   public int lastFilled(EnumFacing.Axis p_197836_1_, int p_197836_2_, int p_197836_3_) {
      if (p_197836_2_ >= 0 && p_197836_3_ >= 0) {
         EnumFacing.Axis enumfacing$axis = AxisRotation.FORWARD.rotate(p_197836_1_);
         EnumFacing.Axis enumfacing$axis1 = AxisRotation.BACKWARD.rotate(p_197836_1_);
         if (p_197836_2_ < this.getSize(enumfacing$axis) && p_197836_3_ < this.getSize(enumfacing$axis1)) {
            int i = this.getSize(p_197836_1_);
            AxisRotation axisrotation = AxisRotation.from(EnumFacing.Axis.X, p_197836_1_);

            for(int j = i - 1; j >= 0; --j) {
               if (this.isFilledWithRotation(axisrotation, j, p_197836_2_, p_197836_3_)) {
                  return j + 1;
               }
            }

            return 0;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public int getSize(EnumFacing.Axis axis) {
      return axis.getCoordinate(this.xSize, this.ySize, this.zSize);
   }

   public int getXSize() {
      return this.getSize(EnumFacing.Axis.X);
   }

   public int getYSize() {
      return this.getSize(EnumFacing.Axis.Y);
   }

   public int getZSize() {
      return this.getSize(EnumFacing.Axis.Z);
   }

   @OnlyIn(Dist.CLIENT)
   public void forEachEdge(VoxelShapePart.LineConsumer consumer, boolean combine) {
      this.forEachEdgeOnAxis(consumer, AxisRotation.NONE, combine);
      this.forEachEdgeOnAxis(consumer, AxisRotation.FORWARD, combine);
      this.forEachEdgeOnAxis(consumer, AxisRotation.BACKWARD, combine);
   }

   @OnlyIn(Dist.CLIENT)
   private void forEachEdgeOnAxis(VoxelShapePart.LineConsumer p_197832_1_, AxisRotation p_197832_2_, boolean p_197832_3_) {
      AxisRotation axisrotation = p_197832_2_.reverse();
      int j = this.getSize(axisrotation.rotate(EnumFacing.Axis.X));
      int k = this.getSize(axisrotation.rotate(EnumFacing.Axis.Y));
      int l = this.getSize(axisrotation.rotate(EnumFacing.Axis.Z));

      for(int i1 = 0; i1 <= j; ++i1) {
         for(int j1 = 0; j1 <= k; ++j1) {
            int i = -1;

            for(int k1 = 0; k1 <= l; ++k1) {
               int l1 = 0;
               int i2 = 0;

               for(int j2 = 0; j2 <= 1; ++j2) {
                  for(int k2 = 0; k2 <= 1; ++k2) {
                     if (this.containsWithRotation(axisrotation, i1 + j2 - 1, j1 + k2 - 1, k1)) {
                        ++l1;
                        i2 ^= j2 ^ k2;
                     }
                  }
               }

               if (l1 == 1 || l1 == 3 || l1 == 2 && (i2 & 1) == 0) {
                  if (p_197832_3_) {
                     if (i == -1) {
                        i = k1;
                     }
                  } else {
                     p_197832_1_.consume(axisrotation.getCoordinate(i1, j1, k1, EnumFacing.Axis.X), axisrotation.getCoordinate(i1, j1, k1, EnumFacing.Axis.Y), axisrotation.getCoordinate(i1, j1, k1, EnumFacing.Axis.Z), axisrotation.getCoordinate(i1, j1, k1 + 1, EnumFacing.Axis.X), axisrotation.getCoordinate(i1, j1, k1 + 1, EnumFacing.Axis.Y), axisrotation.getCoordinate(i1, j1, k1 + 1, EnumFacing.Axis.Z));
                  }
               } else if (i != -1) {
                  p_197832_1_.consume(axisrotation.getCoordinate(i1, j1, i, EnumFacing.Axis.X), axisrotation.getCoordinate(i1, j1, i, EnumFacing.Axis.Y), axisrotation.getCoordinate(i1, j1, i, EnumFacing.Axis.Z), axisrotation.getCoordinate(i1, j1, k1, EnumFacing.Axis.X), axisrotation.getCoordinate(i1, j1, k1, EnumFacing.Axis.Y), axisrotation.getCoordinate(i1, j1, k1, EnumFacing.Axis.Z));
                  i = -1;
               }
            }
         }
      }

   }

   protected boolean isZAxisLineFull(int fromZ, int toZ, int x, int y) {
      for(int i = fromZ; i < toZ; ++i) {
         if (!this.contains(x, y, i)) {
            return false;
         }
      }

      return true;
   }

   protected void setZAxisLine(int fromZ, int toZ, int x, int y, boolean filled) {
      for(int i = fromZ; i < toZ; ++i) {
         this.setFilled(x, y, i, false, filled);
      }

   }

   protected boolean isXZRectangleFull(int fromX, int toX, int fromZ, int toZ, int x) {
      for(int i = fromX; i < toX; ++i) {
         if (!this.isZAxisLineFull(fromZ, toZ, i, x)) {
            return false;
         }
      }

      return true;
   }

   public void forEachBox(VoxelShapePart.LineConsumer consumer, boolean combine) {
      VoxelShapePart voxelshapepart = new VoxelShapePartBitSet(this);

      for(int i = 0; i <= this.xSize; ++i) {
         for(int j = 0; j <= this.ySize; ++j) {
            int k = -1;

            for(int l = 0; l <= this.zSize; ++l) {
               if (voxelshapepart.contains(i, j, l)) {
                  if (combine) {
                     if (k == -1) {
                        k = l;
                     }
                  } else {
                     consumer.consume(i, j, l, i + 1, j + 1, l + 1);
                  }
               } else if (k != -1) {
                  int i1 = i;
                  int j1 = i;
                  int k1 = j;
                  int l1 = j;
                  voxelshapepart.setZAxisLine(k, l, i, j, false);

                  while(voxelshapepart.isZAxisLineFull(k, l, i1 - 1, k1)) {
                     voxelshapepart.setZAxisLine(k, l, i1 - 1, k1, false);
                     --i1;
                  }

                  while(voxelshapepart.isZAxisLineFull(k, l, j1 + 1, k1)) {
                     voxelshapepart.setZAxisLine(k, l, j1 + 1, k1, false);
                     ++j1;
                  }

                  while(voxelshapepart.isXZRectangleFull(i1, j1 + 1, k, l, k1 - 1)) {
                     for(int i2 = i1; i2 <= j1; ++i2) {
                        voxelshapepart.setZAxisLine(k, l, i2, k1 - 1, false);
                     }

                     --k1;
                  }

                  while(voxelshapepart.isXZRectangleFull(i1, j1 + 1, k, l, l1 + 1)) {
                     for(int j2 = i1; j2 <= j1; ++j2) {
                        voxelshapepart.setZAxisLine(k, l, j2, l1 + 1, false);
                     }

                     ++l1;
                  }

                  consumer.consume(i1, k1, k, j1 + 1, l1 + 1, l);
                  k = -1;
               }
            }
         }
      }

   }

   public void forEachFace(VoxelShapePart.FaceConsumer p_211540_1_) {
      this.forEachFaceOnAxis(p_211540_1_, AxisRotation.NONE);
      this.forEachFaceOnAxis(p_211540_1_, AxisRotation.FORWARD);
      this.forEachFaceOnAxis(p_211540_1_, AxisRotation.BACKWARD);
   }

   private void forEachFaceOnAxis(VoxelShapePart.FaceConsumer p_211541_1_, AxisRotation p_211541_2_) {
      AxisRotation axisrotation = p_211541_2_.reverse();
      EnumFacing.Axis enumfacing$axis = axisrotation.rotate(EnumFacing.Axis.Z);
      int i = this.getSize(axisrotation.rotate(EnumFacing.Axis.X));
      int j = this.getSize(axisrotation.rotate(EnumFacing.Axis.Y));
      int k = this.getSize(enumfacing$axis);
      EnumFacing enumfacing = EnumFacing.getFacingFromAxisDirection(enumfacing$axis, EnumFacing.AxisDirection.NEGATIVE);
      EnumFacing enumfacing1 = EnumFacing.getFacingFromAxisDirection(enumfacing$axis, EnumFacing.AxisDirection.POSITIVE);

      for(int l = 0; l < i; ++l) {
         for(int i1 = 0; i1 < j; ++i1) {
            boolean flag = false;

            for(int j1 = 0; j1 <= k; ++j1) {
               boolean flag1 = j1 != k && this.isFilledWithRotation(axisrotation, l, i1, j1);
               if (!flag && flag1) {
                  p_211541_1_.consume(enumfacing, axisrotation.getCoordinate(l, i1, j1, EnumFacing.Axis.X), axisrotation.getCoordinate(l, i1, j1, EnumFacing.Axis.Y), axisrotation.getCoordinate(l, i1, j1, EnumFacing.Axis.Z));
               }

               if (flag && !flag1) {
                  p_211541_1_.consume(enumfacing1, axisrotation.getCoordinate(l, i1, j1 - 1, EnumFacing.Axis.X), axisrotation.getCoordinate(l, i1, j1 - 1, EnumFacing.Axis.Y), axisrotation.getCoordinate(l, i1, j1 - 1, EnumFacing.Axis.Z));
               }

               flag = flag1;
            }
         }
      }

   }

   public interface FaceConsumer {
      void consume(EnumFacing p_consume_1_, int p_consume_2_, int p_consume_3_, int p_consume_4_);
   }

   public interface LineConsumer {
      void consume(int p_consume_1_, int p_consume_2_, int p_consume_3_, int p_consume_4_, int p_consume_5_, int p_consume_6_);
   }
}