package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelTurtle extends ModelQuadruped {
   private final ModelRenderer field_203078_i;

   public ModelTurtle(float p_i48834_1_) {
      super(12, p_i48834_1_);
      this.textureWidth = 128;
      this.textureHeight = 64;
      this.head = new ModelRenderer(this, 3, 0);
      this.head.addBox(-3.0F, -1.0F, -3.0F, 6, 5, 6, 0.0F);
      this.head.setRotationPoint(0.0F, 19.0F, -10.0F);
      this.body = new ModelRenderer(this);
      this.body.setTextureOffset(7, 37).addBox(-9.5F, 3.0F, -10.0F, 19, 20, 6, 0.0F);
      this.body.setTextureOffset(31, 1).addBox(-5.5F, 3.0F, -13.0F, 11, 18, 3, 0.0F);
      this.body.setRotationPoint(0.0F, 11.0F, -10.0F);
      this.field_203078_i = new ModelRenderer(this);
      this.field_203078_i.setTextureOffset(70, 33).addBox(-4.5F, 3.0F, -14.0F, 9, 18, 1, 0.0F);
      this.field_203078_i.setRotationPoint(0.0F, 11.0F, -10.0F);
      int i = 1;
      this.leg1 = new ModelRenderer(this, 1, 23);
      this.leg1.addBox(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
      this.leg1.setRotationPoint(-3.5F, 22.0F, 11.0F);
      this.leg2 = new ModelRenderer(this, 1, 12);
      this.leg2.addBox(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
      this.leg2.setRotationPoint(3.5F, 22.0F, 11.0F);
      this.leg3 = new ModelRenderer(this, 27, 30);
      this.leg3.addBox(-13.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
      this.leg3.setRotationPoint(-5.0F, 21.0F, -4.0F);
      this.leg4 = new ModelRenderer(this, 27, 24);
      this.leg4.addBox(0.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
      this.leg4.setRotationPoint(5.0F, 21.0F, -4.0F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      EntityTurtle entityturtle = (EntityTurtle)entityIn;
      if (this.isChild) {
         float f = 6.0F;
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.16666667F, 0.16666667F, 0.16666667F);
         GlStateManager.translatef(0.0F, 120.0F * scale, 0.0F);
         this.head.render(scale);
         this.body.render(scale);
         this.leg1.render(scale);
         this.leg2.render(scale);
         this.leg3.render(scale);
         this.leg4.render(scale);
         GlStateManager.popMatrix();
      } else {
         GlStateManager.pushMatrix();
         if (entityturtle.hasEgg()) {
            GlStateManager.translatef(0.0F, -0.08F, 0.0F);
         }

         this.head.render(scale);
         this.body.render(scale);
         GlStateManager.pushMatrix();
         this.leg1.render(scale);
         this.leg2.render(scale);
         GlStateManager.popMatrix();
         this.leg3.render(scale);
         this.leg4.render(scale);
         if (entityturtle.hasEgg()) {
            this.field_203078_i.render(scale);
         }

         GlStateManager.popMatrix();
      }

   }

   /**
    * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
    * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how "far"
    * arms and legs can swing at most.
    */
   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      EntityTurtle entityturtle = (EntityTurtle)entityIn;
      this.leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F * 0.6F) * 0.5F * limbSwingAmount;
      this.leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F * 0.6F + (float)Math.PI) * 0.5F * limbSwingAmount;
      this.leg3.rotateAngleZ = MathHelper.cos(limbSwing * 0.6662F * 0.6F + (float)Math.PI) * 0.5F * limbSwingAmount;
      this.leg4.rotateAngleZ = MathHelper.cos(limbSwing * 0.6662F * 0.6F) * 0.5F * limbSwingAmount;
      this.leg3.rotateAngleX = 0.0F;
      this.leg4.rotateAngleX = 0.0F;
      this.leg3.rotateAngleY = 0.0F;
      this.leg4.rotateAngleY = 0.0F;
      this.leg1.rotateAngleY = 0.0F;
      this.leg2.rotateAngleY = 0.0F;
      this.field_203078_i.rotateAngleX = ((float)Math.PI / 2F);
      if (!entityturtle.isInWater() && entityturtle.onGround) {
         float f = entityturtle.func_203023_dy() ? 4.0F : 1.0F;
         float f1 = entityturtle.func_203023_dy() ? 2.0F : 1.0F;
         float f2 = 5.0F;
         this.leg3.rotateAngleY = MathHelper.cos(f * limbSwing * 5.0F + (float)Math.PI) * 8.0F * limbSwingAmount * f1;
         this.leg3.rotateAngleZ = 0.0F;
         this.leg4.rotateAngleY = MathHelper.cos(f * limbSwing * 5.0F) * 8.0F * limbSwingAmount * f1;
         this.leg4.rotateAngleZ = 0.0F;
         this.leg1.rotateAngleY = MathHelper.cos(limbSwing * 5.0F + (float)Math.PI) * 3.0F * limbSwingAmount;
         this.leg1.rotateAngleX = 0.0F;
         this.leg2.rotateAngleY = MathHelper.cos(limbSwing * 5.0F) * 3.0F * limbSwingAmount;
         this.leg2.rotateAngleX = 0.0F;
      }

   }
}