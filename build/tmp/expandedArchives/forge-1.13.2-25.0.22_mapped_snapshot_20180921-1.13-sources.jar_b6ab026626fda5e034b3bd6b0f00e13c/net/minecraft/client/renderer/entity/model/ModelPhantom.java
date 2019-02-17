package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelPhantom extends ModelBase {
   private final ModelRenderer field_203070_a;
   private final ModelRenderer field_203071_b;
   private final ModelRenderer field_203072_c;
   private final ModelRenderer field_203073_d;
   private final ModelRenderer field_203074_e;
   private final ModelRenderer field_203075_f;
   private final ModelRenderer field_204233_g;
   private final ModelRenderer field_204234_h;

   public ModelPhantom() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.field_203070_a = new ModelRenderer(this, 0, 8);
      this.field_203070_a.addBox(-3.0F, -2.0F, -8.0F, 5, 3, 9);
      this.field_204233_g = new ModelRenderer(this, 3, 20);
      this.field_204233_g.addBox(-2.0F, 0.0F, 0.0F, 3, 2, 6);
      this.field_204233_g.setRotationPoint(0.0F, -2.0F, 1.0F);
      this.field_203070_a.addChild(this.field_204233_g);
      this.field_204234_h = new ModelRenderer(this, 4, 29);
      this.field_204234_h.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 6);
      this.field_204234_h.setRotationPoint(0.0F, 0.5F, 6.0F);
      this.field_204233_g.addChild(this.field_204234_h);
      this.field_203071_b = new ModelRenderer(this, 23, 12);
      this.field_203071_b.addBox(0.0F, 0.0F, 0.0F, 6, 2, 9);
      this.field_203071_b.setRotationPoint(2.0F, -2.0F, -8.0F);
      this.field_203072_c = new ModelRenderer(this, 16, 24);
      this.field_203072_c.addBox(0.0F, 0.0F, 0.0F, 13, 1, 9);
      this.field_203072_c.setRotationPoint(6.0F, 0.0F, 0.0F);
      this.field_203071_b.addChild(this.field_203072_c);
      this.field_203073_d = new ModelRenderer(this, 23, 12);
      this.field_203073_d.mirror = true;
      this.field_203073_d.addBox(-6.0F, 0.0F, 0.0F, 6, 2, 9);
      this.field_203073_d.setRotationPoint(-3.0F, -2.0F, -8.0F);
      this.field_203074_e = new ModelRenderer(this, 16, 24);
      this.field_203074_e.mirror = true;
      this.field_203074_e.addBox(-13.0F, 0.0F, 0.0F, 13, 1, 9);
      this.field_203074_e.setRotationPoint(-6.0F, 0.0F, 0.0F);
      this.field_203073_d.addChild(this.field_203074_e);
      this.field_203071_b.rotateAngleZ = 0.1F;
      this.field_203072_c.rotateAngleZ = 0.1F;
      this.field_203073_d.rotateAngleZ = -0.1F;
      this.field_203074_e.rotateAngleZ = -0.1F;
      this.field_203070_a.rotateAngleX = -0.1F;
      this.field_203075_f = new ModelRenderer(this, 0, 0);
      this.field_203075_f.addBox(-4.0F, -2.0F, -5.0F, 7, 3, 5);
      this.field_203075_f.setRotationPoint(0.0F, 1.0F, -7.0F);
      this.field_203075_f.rotateAngleX = 0.2F;
      this.field_203070_a.addChild(this.field_203075_f);
      this.field_203070_a.addChild(this.field_203071_b);
      this.field_203070_a.addChild(this.field_203073_d);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.field_203070_a.render(scale);
   }

   /**
    * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
    * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how "far"
    * arms and legs can swing at most.
    */
   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      float f = ((float)(entityIn.getEntityId() * 3) + ageInTicks) * 0.13F;
      float f1 = 16.0F;
      this.field_203071_b.rotateAngleZ = (0.0F + MathHelper.cos(f) * 16.0F) * ((float)Math.PI / 180F);
      this.field_203072_c.rotateAngleZ = (0.0F + MathHelper.cos(f) * 16.0F) * ((float)Math.PI / 180F);
      this.field_203073_d.rotateAngleZ = -this.field_203071_b.rotateAngleZ;
      this.field_203074_e.rotateAngleZ = -this.field_203072_c.rotateAngleZ;
      this.field_204233_g.rotateAngleX = -(5.0F + MathHelper.cos(f * 2.0F) * 5.0F) * ((float)Math.PI / 180F);
      this.field_204234_h.rotateAngleX = -(5.0F + MathHelper.cos(f * 2.0F) * 5.0F) * ((float)Math.PI / 180F);
   }
}