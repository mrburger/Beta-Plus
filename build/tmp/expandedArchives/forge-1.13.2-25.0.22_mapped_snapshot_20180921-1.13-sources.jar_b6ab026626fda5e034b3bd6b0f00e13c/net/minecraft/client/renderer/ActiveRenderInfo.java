package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ActiveRenderInfo {
   /** The current GL modelview matrix */
   private static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
   private static Vec3d position = new Vec3d(0.0D, 0.0D, 0.0D);
   /** The X component of the entity's yaw rotation */
   private static float rotationX;
   /** The combined X and Z components of the entity's pitch rotation */
   private static float rotationXZ;
   /** The Z component of the entity's yaw rotation */
   private static float rotationZ;
   /** The Y component (scaled along the Z axis) of the entity's pitch rotation */
   private static float rotationYZ;
   /** The Y component (scaled along the X axis) of the entity's pitch rotation */
   private static float rotationXY;

   public static void updateRenderInfo(EntityPlayer p_197924_0_, boolean p_197924_1_, float p_197924_2_) {
      updateRenderInfo((Entity) p_197924_0_, p_197924_1_, p_197924_2_);
   }
   
   public static void updateRenderInfo(Entity p_197924_0_, boolean p_197924_1_, float p_197924_2_) {
      MODELVIEW.clear();
      GlStateManager.getFloatv(2982, MODELVIEW);
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.read(MODELVIEW);
      matrix4f.func_195887_c();
      float f = 0.05F;
      float f1 = p_197924_2_ * MathHelper.SQRT_2;
      Vector4f vector4f = new Vector4f(0.0F, 0.0F, -2.0F * f1 * 0.05F / (f1 + 0.05F), 1.0F);
      vector4f.func_195908_a(matrix4f);
      position = new Vec3d((double)vector4f.getX(), (double)vector4f.getY(), (double)vector4f.getZ());
      float f2 = p_197924_0_.rotationPitch;
      float f3 = p_197924_0_.rotationYaw;
      int i = p_197924_1_ ? -1 : 1;
      rotationX = MathHelper.cos(f3 * ((float)Math.PI / 180F)) * (float)i;
      rotationZ = MathHelper.sin(f3 * ((float)Math.PI / 180F)) * (float)i;
      rotationYZ = -rotationZ * MathHelper.sin(f2 * ((float)Math.PI / 180F)) * (float)i;
      rotationXY = rotationX * MathHelper.sin(f2 * ((float)Math.PI / 180F)) * (float)i;
      rotationXZ = MathHelper.cos(f2 * ((float)Math.PI / 180F));
   }

   public static Vec3d projectViewFromEntity(Entity entityIn, double p_178806_1_) {
      double d0 = entityIn.prevPosX + (entityIn.posX - entityIn.prevPosX) * p_178806_1_;
      double d1 = entityIn.prevPosY + (entityIn.posY - entityIn.prevPosY) * p_178806_1_;
      double d2 = entityIn.prevPosZ + (entityIn.posZ - entityIn.prevPosZ) * p_178806_1_;
      double d3 = d0 + position.x;
      double d4 = d1 + position.y;
      double d5 = d2 + position.z;
      return new Vec3d(d3, d4, d5);
   }

   public static IBlockState getBlockStateAtEntityViewpoint(IBlockReader worldIn, Entity entityIn, float p_186703_2_) {
      Vec3d vec3d = projectViewFromEntity(entityIn, (double)p_186703_2_);
      BlockPos blockpos = new BlockPos(vec3d);
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      IFluidState ifluidstate = worldIn.getFluidState(blockpos);
      if (!ifluidstate.isEmpty()) {
         float f = (float)blockpos.getY() + ifluidstate.getHeight() + 0.11111111F;
         if (vec3d.y >= (double)f) {
            iblockstate = worldIn.getBlockState(blockpos.up());
         }
      }

      return iblockstate.getBlock().getStateAtViewpoint(iblockstate, worldIn, blockpos, vec3d);
   }

   public static IFluidState getFluidStateAtEntityViewpoint(IBlockReader p_206243_0_, Entity p_206243_1_, float p_206243_2_) {
      Vec3d vec3d = projectViewFromEntity(p_206243_1_, (double)p_206243_2_);
      BlockPos blockpos = new BlockPos(vec3d);
      IFluidState ifluidstate = p_206243_0_.getFluidState(blockpos);
      if (!ifluidstate.isEmpty()) {
         float f = (float)blockpos.getY() + ifluidstate.getHeight() + 0.11111111F;
         if (vec3d.y >= (double)f) {
            ifluidstate = p_206243_0_.getFluidState(blockpos.up());
         }
      }

      return ifluidstate;
   }

   public static float getRotationX() {
      return rotationX;
   }

   public static float getRotationXZ() {
      return rotationXZ;
   }

   public static float getRotationZ() {
      return rotationZ;
   }

   public static float getRotationYZ() {
      return rotationYZ;
   }

   public static float getRotationXY() {
      return rotationXY;
   }
   
   /* ======================================== FORGE START =====================================*/

   /**
    * Vector from render view entity position (corrected for partialTickTime) to the middle of screen
    */
   public static Vec3d getCameraPosition() {
      return position;
   }
}