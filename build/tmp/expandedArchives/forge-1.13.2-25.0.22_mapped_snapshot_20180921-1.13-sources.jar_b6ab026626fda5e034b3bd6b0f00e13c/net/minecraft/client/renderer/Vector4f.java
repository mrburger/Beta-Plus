package net.minecraft.client.renderer;

import java.util.Arrays;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Vector4f {
   private final float[] components;

   public Vector4f(Vector4f vec) {
      this.components = Arrays.copyOf(vec.components, 4);
   }

   public Vector4f() {
      this.components = new float[4];
   }

   public Vector4f(float x, float y, float z, float w) {
      this.components = new float[]{x, y, z, w};
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Vector4f vector4f = (Vector4f)p_equals_1_;
         return Arrays.equals(this.components, vector4f.components);
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

   public float getW() {
      return this.components[3];
   }

   public void scale(Vector3f vec) {
      this.components[0] *= vec.getX();
      this.components[1] *= vec.getY();
      this.components[2] *= vec.getZ();
   }

   public void set(float x, float y, float z, float w) {
      this.components[0] = x;
      this.components[1] = y;
      this.components[2] = z;
      this.components[3] = w;
   }

   public void func_195908_a(Matrix4f matrix) {
      float[] afloat = Arrays.copyOf(this.components, 4);

      for(int i = 0; i < 4; ++i) {
         this.components[i] = 0.0F;

         for(int j = 0; j < 4; ++j) {
            this.components[i] += matrix.get(i, j) * afloat[j];
         }
      }

   }

   public void func_195912_a(Quaternion quaternionIn) {
      Quaternion quaternion = new Quaternion(quaternionIn);
      quaternion.multiply(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
      Quaternion quaternion1 = new Quaternion(quaternionIn);
      quaternion1.conjugate();
      quaternion.multiply(quaternion1);
      this.set(quaternion.getX(), quaternion.getY(), quaternion.getZ(), this.getW());
   }
}