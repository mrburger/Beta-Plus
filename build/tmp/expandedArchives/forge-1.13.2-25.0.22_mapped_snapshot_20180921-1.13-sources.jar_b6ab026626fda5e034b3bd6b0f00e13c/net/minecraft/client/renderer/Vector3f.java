package net.minecraft.client.renderer;

import java.util.Arrays;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class Vector3f {
   private final float[] components;

   public Vector3f(Vector3f vec) {
      this.components = Arrays.copyOf(vec.components, 3);
   }

   public Vector3f() {
      this.components = new float[3];
   }

   public Vector3f(float x, float y, float z) {
      this.components = new float[]{x, y, z};
   }

   public Vector3f(EnumFacing direction) {
      Vec3i vec3i = direction.getDirectionVec();
      this.components = new float[]{(float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ()};
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Vector3f vector3f = (Vector3f)p_equals_1_;
         return Arrays.equals(this.components, vector3f.components);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.components);
   }

   public float getX() {
      return this.components[0];
   }

   public float getY() {
      return this.components[1];
   }

   public float getZ() {
      return this.components[2];
   }

   public void mul(float multiplier) {
      for(int i = 0; i < 3; ++i) {
         this.components[i] *= multiplier;
      }

   }

   public void clamp(float min, float max) {
      this.components[0] = MathHelper.clamp(this.components[0], min, max);
      this.components[1] = MathHelper.clamp(this.components[1], min, max);
      this.components[2] = MathHelper.clamp(this.components[2], min, max);
   }

   public void set(float x, float y, float z) {
      this.components[0] = x;
      this.components[1] = y;
      this.components[2] = z;
   }

   public void add(float x, float y, float z) {
      this.components[0] += x;
      this.components[1] += y;
      this.components[2] += z;
   }

   public void sub(Vector3f vec) {
      for(int i = 0; i < 3; ++i) {
         this.components[i] -= vec.components[i];
      }

   }

   public float dot(Vector3f vec) {
      float f = 0.0F;

      for(int i = 0; i < 3; ++i) {
         f += this.components[i] * vec.components[i];
      }

      return f;
   }

   public void normalize() {
      float f = 0.0F;

      for(int i = 0; i < 3; ++i) {
         f += this.components[i] * this.components[i];
      }

      for(int j = 0; j < 3; ++j) {
         this.components[j] /= f;
      }

   }

   public void cross(Vector3f vec) {
      float f = this.components[0];
      float f1 = this.components[1];
      float f2 = this.components[2];
      float f3 = vec.getX();
      float f4 = vec.getY();
      float f5 = vec.getZ();
      this.components[0] = f1 * f5 - f2 * f4;
      this.components[1] = f2 * f3 - f * f5;
      this.components[2] = f * f4 - f1 * f3;
   }
}