package net.minecraft.client.renderer.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ModelRotation implements net.minecraftforge.common.model.IModelState, net.minecraftforge.common.model.ITransformation {
   X0_Y0(0, 0),
   X0_Y90(0, 90),
   X0_Y180(0, 180),
   X0_Y270(0, 270),
   X90_Y0(90, 0),
   X90_Y90(90, 90),
   X90_Y180(90, 180),
   X90_Y270(90, 270),
   X180_Y0(180, 0),
   X180_Y90(180, 90),
   X180_Y180(180, 180),
   X180_Y270(180, 270),
   X270_Y0(270, 0),
   X270_Y90(270, 90),
   X270_Y180(270, 180),
   X270_Y270(270, 270);

   private static final Map<Integer, ModelRotation> MAP_ROTATIONS = Arrays.stream(values()).sorted(Comparator.comparingInt((p_199757_0_) -> {
      return p_199757_0_.combinedXY;
   })).collect(Collectors.toMap((p_199756_0_) -> {
      return p_199756_0_.combinedXY;
   }, (p_199758_0_) -> {
      return p_199758_0_;
   }));
   private final int combinedXY;
   private final Quaternion matrix;
   private final int quartersX;
   private final int quartersY;

   private static int combineXY(int x, int y) {
      return x * 360 + y;
   }

   private ModelRotation(int x, int y) {
      this.combinedXY = combineXY(x, y);
      Quaternion quaternion = new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), (float)(-y), true);
      quaternion.multiply(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), (float)(-x), true));
      this.matrix = quaternion;
      this.quartersX = MathHelper.abs(x / 90);
      this.quartersY = MathHelper.abs(y / 90);
   }

   public Quaternion getMatrix() {
      return this.matrix;
   }

   public EnumFacing rotateFace(EnumFacing facing) {
      EnumFacing enumfacing = facing;

      for(int i = 0; i < this.quartersX; ++i) {
         enumfacing = enumfacing.rotateAround(EnumFacing.Axis.X);
      }

      if (enumfacing.getAxis() != EnumFacing.Axis.Y) {
         for(int j = 0; j < this.quartersY; ++j) {
            enumfacing = enumfacing.rotateAround(EnumFacing.Axis.Y);
         }
      }

      return enumfacing;
   }

   public int rotateVertex(EnumFacing facing, int vertexIndex) {
      int i = vertexIndex;
      if (facing.getAxis() == EnumFacing.Axis.X) {
         i = (vertexIndex + this.quartersX) % 4;
      }

      EnumFacing enumfacing = facing;

      for(int j = 0; j < this.quartersX; ++j) {
         enumfacing = enumfacing.rotateAround(EnumFacing.Axis.X);
      }

      if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
         i = (i + this.quartersY) % 4;
      }

      return i;
   }

   public static ModelRotation getModelRotation(int x, int y) {
      return MAP_ROTATIONS.get(combineXY(MathHelper.normalizeAngle(x, 360), MathHelper.normalizeAngle(y, 360)));
   }

   public java.util.Optional<net.minecraftforge.common.model.TRSRTransformation> apply(java.util.Optional<? extends net.minecraftforge.common.model.IModelPart> part) { return net.minecraftforge.client.ForgeHooksClient.applyTransform(this, part); }
   public javax.vecmath.Matrix4f getMatrixVec() { return net.minecraftforge.common.model.TRSRTransformation.from(this).getMatrixVec(); }
   public EnumFacing rotate(EnumFacing facing) { return rotateFace(facing); }
   public int rotate(EnumFacing facing, int vertexIndex) { return rotateVertex(facing, vertexIndex); }
}