package net.minecraft.util.math.shapes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class VoxelShapes {
   private static final VoxelShape EMPTY = new VoxelShapeArray(new VoxelShapePartBitSet(0, 0, 0), (DoubleList)(new DoubleArrayList(new double[]{0.0D})), (DoubleList)(new DoubleArrayList(new double[]{0.0D})), (DoubleList)(new DoubleArrayList(new double[]{0.0D})));
   private static final VoxelShape FULL_CUBE = Util.make(() -> {
      VoxelShapePart voxelshapepart = new VoxelShapePartBitSet(1, 1, 1);
      voxelshapepart.setFilled(0, 0, 0, true, true);
      return new VoxelShapeCube(voxelshapepart);
   });

   public static VoxelShape empty() {
      return EMPTY;
   }

   public static VoxelShape fullCube() {
      return FULL_CUBE;
   }

   public static VoxelShape create(double x1, double y1, double z1, double x2, double y2, double z2) {
      return create(new AxisAlignedBB(x1, y1, z1, x2, y2, z2));
   }

   public static VoxelShape create(AxisAlignedBB aabb) {
      int i = getPrecisionBits(aabb.minX, aabb.maxX);
      int j = getPrecisionBits(aabb.minY, aabb.maxY);
      int k = getPrecisionBits(aabb.minZ, aabb.maxZ);
      if (i >= 0 && j >= 0 && k >= 0) {
         if (i == 0 && j == 0 && k == 0) {
            return aabb.contains(0.5D, 0.5D, 0.5D) ? fullCube() : empty();
         } else {
            int l = 1 << i;
            int i1 = 1 << j;
            int j1 = 1 << k;
            int k1 = (int)Math.round(aabb.minX * (double)l);
            int l1 = (int)Math.round(aabb.maxX * (double)l);
            int i2 = (int)Math.round(aabb.minY * (double)i1);
            int j2 = (int)Math.round(aabb.maxY * (double)i1);
            int k2 = (int)Math.round(aabb.minZ * (double)j1);
            int l2 = (int)Math.round(aabb.maxZ * (double)j1);
            VoxelShapePartBitSet voxelshapepartbitset = new VoxelShapePartBitSet(l, i1, j1, k1, i2, k2, l1, j2, l2);

            for(long i3 = (long)k1; i3 < (long)l1; ++i3) {
               for(long j3 = (long)i2; j3 < (long)j2; ++j3) {
                  for(long k3 = (long)k2; k3 < (long)l2; ++k3) {
                     voxelshapepartbitset.setFilled((int)i3, (int)j3, (int)k3, false, true);
                  }
               }
            }

            return new VoxelShapeCube(voxelshapepartbitset);
         }
      } else {
         return new VoxelShapeArray(FULL_CUBE.part, new double[]{aabb.minX, aabb.maxX}, new double[]{aabb.minY, aabb.maxY}, new double[]{aabb.minZ, aabb.maxZ});
      }
   }

   private static int getPrecisionBits(double p_197885_0_, double p_197885_2_) {
      if (!(p_197885_0_ < -1.0E-7D) && !(p_197885_2_ > 1.0000001D)) {
         for(int i = 0; i <= 3; ++i) {
            double d0 = p_197885_0_ * (double)(1 << i);
            double d1 = p_197885_2_ * (double)(1 << i);
            boolean flag = Math.abs(d0 - Math.floor(d0)) < 1.0E-7D;
            boolean flag1 = Math.abs(d1 - Math.floor(d1)) < 1.0E-7D;
            if (flag && flag1) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   protected static long lcm(int aa, int bb) {
      return (long)aa * (long)(bb / IntMath.gcd(aa, bb));
   }

   public static VoxelShape or(VoxelShape shape1, VoxelShape shape2) {
      return combineAndSimplify(shape1, shape2, IBooleanFunction.OR);
   }

   public static VoxelShape combineAndSimplify(VoxelShape shape1, VoxelShape shape2, IBooleanFunction function) {
      return combine(shape1, shape2, function).simplify();
   }

   public static VoxelShape combine(VoxelShape shape1, VoxelShape shape2, IBooleanFunction function) {
      if (function.apply(false, false)) {
         throw new IllegalArgumentException();
      } else if (shape1 == shape2) {
         return function.apply(true, true) ? shape1 : empty();
      } else {
         boolean flag = function.apply(true, false);
         boolean flag1 = function.apply(false, true);
         if (shape1.isEmpty()) {
            return flag1 ? shape2 : empty();
         } else if (shape2.isEmpty()) {
            return flag ? shape1 : empty();
         } else {
            IDoubleListMerger idoublelistmerger = makeListMerger(1, shape1.getValues(EnumFacing.Axis.X), shape2.getValues(EnumFacing.Axis.X), flag, flag1);
            IDoubleListMerger idoublelistmerger1 = makeListMerger(idoublelistmerger.func_212435_a().size() - 1, shape1.getValues(EnumFacing.Axis.Y), shape2.getValues(EnumFacing.Axis.Y), flag, flag1);
            IDoubleListMerger idoublelistmerger2 = makeListMerger((idoublelistmerger.func_212435_a().size() - 1) * (idoublelistmerger1.func_212435_a().size() - 1), shape1.getValues(EnumFacing.Axis.Z), shape2.getValues(EnumFacing.Axis.Z), flag, flag1);
            VoxelShapePartBitSet voxelshapepartbitset = VoxelShapePartBitSet.func_197852_a(shape1.part, shape2.part, idoublelistmerger, idoublelistmerger1, idoublelistmerger2, function);
            return (VoxelShape)(idoublelistmerger instanceof DoubleCubeMergingList && idoublelistmerger1 instanceof DoubleCubeMergingList && idoublelistmerger2 instanceof DoubleCubeMergingList ? new VoxelShapeCube(voxelshapepartbitset) : new VoxelShapeArray(voxelshapepartbitset, idoublelistmerger.func_212435_a(), idoublelistmerger1.func_212435_a(), idoublelistmerger2.func_212435_a()));
         }
      }
   }

   public static boolean compare(VoxelShape shape1, VoxelShape shape2, IBooleanFunction function) {
      if (function.apply(false, false)) {
         throw new IllegalArgumentException();
      } else if (shape1 == shape2) {
         return function.apply(true, true);
      } else if (shape1.isEmpty()) {
         return function.apply(false, !shape2.isEmpty());
      } else if (shape2.isEmpty()) {
         return function.apply(!shape1.isEmpty(), false);
      } else {
         boolean flag = function.apply(true, false);
         boolean flag1 = function.apply(false, true);

         for(EnumFacing.Axis enumfacing$axis : AxisRotation.AXES) {
            if (shape1.getEnd(enumfacing$axis) < shape2.getStart(enumfacing$axis) - 1.0E-7D) {
               return flag || flag1;
            }

            if (shape2.getEnd(enumfacing$axis) < shape1.getStart(enumfacing$axis) - 1.0E-7D) {
               return flag || flag1;
            }
         }

         IDoubleListMerger idoublelistmerger = makeListMerger(1, shape1.getValues(EnumFacing.Axis.X), shape2.getValues(EnumFacing.Axis.X), flag, flag1);
         IDoubleListMerger idoublelistmerger1 = makeListMerger(idoublelistmerger.func_212435_a().size() - 1, shape1.getValues(EnumFacing.Axis.Y), shape2.getValues(EnumFacing.Axis.Y), flag, flag1);
         IDoubleListMerger idoublelistmerger2 = makeListMerger((idoublelistmerger.func_212435_a().size() - 1) * (idoublelistmerger1.func_212435_a().size() - 1), shape1.getValues(EnumFacing.Axis.Z), shape2.getValues(EnumFacing.Axis.Z), flag, flag1);
         return func_197874_a(idoublelistmerger, idoublelistmerger1, idoublelistmerger2, shape1.part, shape2.part, function);
      }
   }

   private static boolean func_197874_a(IDoubleListMerger p_197874_0_, IDoubleListMerger p_197874_1_, IDoubleListMerger p_197874_2_, VoxelShapePart p_197874_3_, VoxelShapePart p_197874_4_, IBooleanFunction p_197874_5_) {
      return !p_197874_0_.forMergedIndexes((p_199861_5_, p_199861_6_, p_199861_7_) -> {
         return p_197874_1_.forMergedIndexes((p_199860_6_, p_199860_7_, p_199860_8_) -> {
            return p_197874_2_.forMergedIndexes((p_199862_7_, p_199862_8_, p_199862_9_) -> {
               return !p_197874_5_.apply(p_197874_3_.contains(p_199861_5_, p_199860_6_, p_199862_7_), p_197874_4_.contains(p_199861_6_, p_199860_7_, p_199862_8_));
            });
         });
      });
   }

   public static double func_212437_a(EnumFacing.Axis p_212437_0_, AxisAlignedBB p_212437_1_, Stream<VoxelShape> p_212437_2_, double p_212437_3_) {
      for(Iterator<VoxelShape> iterator = p_212437_2_.iterator(); iterator.hasNext(); p_212437_3_ = iterator.next().func_212430_a(p_212437_0_, p_212437_1_, p_212437_3_)) {
         if (Math.abs(p_212437_3_) < 1.0E-7D) {
            return 0.0D;
         }
      }

      return p_212437_3_;
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean isCubeSideCovered(VoxelShape shape, VoxelShape adjacentShape, EnumFacing side) {
      if (shape == fullCube() && adjacentShape == fullCube()) {
         return true;
      } else if (adjacentShape.isEmpty()) {
         return false;
      } else {
         EnumFacing.Axis enumfacing$axis = side.getAxis();
         EnumFacing.AxisDirection enumfacing$axisdirection = side.getAxisDirection();
         VoxelShape voxelshape = enumfacing$axisdirection == EnumFacing.AxisDirection.POSITIVE ? shape : adjacentShape;
         VoxelShape voxelshape1 = enumfacing$axisdirection == EnumFacing.AxisDirection.POSITIVE ? adjacentShape : shape;
         IBooleanFunction ibooleanfunction = enumfacing$axisdirection == EnumFacing.AxisDirection.POSITIVE ? IBooleanFunction.ONLY_FIRST : IBooleanFunction.ONLY_SECOND;
         return DoubleMath.fuzzyEquals(voxelshape.getEnd(enumfacing$axis), 1.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(voxelshape1.getStart(enumfacing$axis), 0.0D, 1.0E-7D) && !compare(new VoxelShapeSplit(voxelshape, enumfacing$axis, voxelshape.part.getSize(enumfacing$axis) - 1), new VoxelShapeSplit(voxelshape1, enumfacing$axis, 0), ibooleanfunction);
      }
   }

   public static boolean doAdjacentCubeSidesFillSquare(VoxelShape shape, VoxelShape adjacentShape, EnumFacing side) {
      if (shape != fullCube() && adjacentShape != fullCube()) {
         EnumFacing.Axis enumfacing$axis = side.getAxis();
         EnumFacing.AxisDirection enumfacing$axisdirection = side.getAxisDirection();
         VoxelShape voxelshape = enumfacing$axisdirection == EnumFacing.AxisDirection.POSITIVE ? shape : adjacentShape;
         VoxelShape voxelshape1 = enumfacing$axisdirection == EnumFacing.AxisDirection.POSITIVE ? adjacentShape : shape;
         if (!DoubleMath.fuzzyEquals(voxelshape.getEnd(enumfacing$axis), 1.0D, 1.0E-7D)) {
            voxelshape = empty();
         }

         if (!DoubleMath.fuzzyEquals(voxelshape1.getStart(enumfacing$axis), 0.0D, 1.0E-7D)) {
            voxelshape1 = empty();
         }

         return !compare(fullCube(), combine(new VoxelShapeSplit(voxelshape, enumfacing$axis, voxelshape.part.getSize(enumfacing$axis) - 1), new VoxelShapeSplit(voxelshape1, enumfacing$axis, 0), IBooleanFunction.OR), IBooleanFunction.ONLY_FIRST);
      } else {
         return true;
      }
   }

   @VisibleForTesting
   protected static IDoubleListMerger makeListMerger(int p_199410_0_, DoubleList list1, DoubleList list2, boolean p_199410_3_, boolean p_199410_4_) {
      if (list1 instanceof DoubleRangeList && list2 instanceof DoubleRangeList) {
         int i = list1.size() - 1;
         int j = list2.size() - 1;
         long k = lcm(i, j);
         if ((long)p_199410_0_ * k <= 256L) {
            return new DoubleCubeMergingList(i, j);
         }
      }

      if (list1.getDouble(list1.size() - 1) < list2.getDouble(0) - 1.0E-7D) {
         return new NonOverlappingMerger(list1, list2, false);
      } else if (list2.getDouble(list2.size() - 1) < list1.getDouble(0) - 1.0E-7D) {
         return new NonOverlappingMerger(list2, list1, true);
      } else if (Objects.equals(list1, list2)) {
         if (list1 instanceof SimpleDoubleMerger) {
            return (IDoubleListMerger)list1;
         } else {
            return (IDoubleListMerger)(list2 instanceof SimpleDoubleMerger ? (IDoubleListMerger)list2 : new SimpleDoubleMerger(list1));
         }
      } else {
         return new IndirectMerger(list1, list2, p_199410_3_, p_199410_4_);
      }
   }

   public interface LineConsumer {
      void consume(double p_consume_1_, double p_consume_3_, double p_consume_5_, double p_consume_7_, double p_consume_9_, double p_consume_11_);
   }
}