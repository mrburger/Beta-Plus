package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBiped extends ModelBase {
   public ModelRenderer bipedHead;
   /** The Biped's Headwear. Used for the outer layer of player skins. */
   public ModelRenderer bipedHeadwear;
   public ModelRenderer bipedBody;
   /** The Biped's Right Arm */
   public ModelRenderer bipedRightArm;
   /** The Biped's Left Arm */
   public ModelRenderer bipedLeftArm;
   /** The Biped's Right Leg */
   public ModelRenderer bipedRightLeg;
   /** The Biped's Left Leg */
   public ModelRenderer bipedLeftLeg;
   public ModelBiped.ArmPose leftArmPose = ModelBiped.ArmPose.EMPTY;
   public ModelBiped.ArmPose rightArmPose = ModelBiped.ArmPose.EMPTY;
   public boolean isSneak;
   public float field_205061_a;

   public ModelBiped() {
      this(0.0F);
   }

   public ModelBiped(float modelSize) {
      this(modelSize, 0.0F, 64, 32);
   }

   public ModelBiped(float modelSize, float p_i1149_2_, int textureWidthIn, int textureHeightIn) {
      this.textureWidth = textureWidthIn;
      this.textureHeight = textureHeightIn;
      this.bipedHead = new ModelRenderer(this, 0, 0);
      this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize);
      this.bipedHead.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
      this.bipedHeadwear = new ModelRenderer(this, 32, 0);
      this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize + 0.5F);
      this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
      this.bipedBody = new ModelRenderer(this, 16, 16);
      this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize);
      this.bipedBody.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
      this.bipedRightArm = new ModelRenderer(this, 40, 16);
      this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
      this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + p_i1149_2_, 0.0F);
      this.bipedLeftArm = new ModelRenderer(this, 40, 16);
      this.bipedLeftArm.mirror = true;
      this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
      this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + p_i1149_2_, 0.0F);
      this.bipedRightLeg = new ModelRenderer(this, 0, 16);
      this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
      this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + p_i1149_2_, 0.0F);
      this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
      this.bipedLeftLeg.mirror = true;
      this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
      this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + p_i1149_2_, 0.0F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      GlStateManager.pushMatrix();
      if (this.isChild) {
         float f = 2.0F;
         GlStateManager.scalef(0.75F, 0.75F, 0.75F);
         GlStateManager.translatef(0.0F, 16.0F * scale, 0.0F);
         this.bipedHead.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.bipedBody.render(scale);
         this.bipedRightArm.render(scale);
         this.bipedLeftArm.render(scale);
         this.bipedRightLeg.render(scale);
         this.bipedLeftLeg.render(scale);
         this.bipedHeadwear.render(scale);
      } else {
         if (entityIn.isSneaking()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         this.bipedHead.render(scale);
         this.bipedBody.render(scale);
         this.bipedRightArm.render(scale);
         this.bipedLeftArm.render(scale);
         this.bipedRightLeg.render(scale);
         this.bipedLeftLeg.render(scale);
         this.bipedHeadwear.render(scale);
      }

      GlStateManager.popMatrix();
   }

   /**
    * Used for easily adding entity-dependent animations. The second and third float params here are the same second and
    * third as in the setRotationAngles method.
    */
   public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
      this.field_205061_a = entitylivingbaseIn.getSwimAnimation(partialTickTime);
      super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
   }

   /**
    * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
    * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how "far"
    * arms and legs can swing at most.
    */
   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      boolean flag = entityIn instanceof EntityLivingBase && ((EntityLivingBase)entityIn).getTicksElytraFlying() > 4;
      boolean flag1 = entityIn.isSwimming();
      this.bipedHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      if (flag) {
         this.bipedHead.rotateAngleX = (-(float)Math.PI / 4F);
      } else if (this.field_205061_a > 0.0F) {
         if (flag1) {
            this.bipedHead.rotateAngleX = this.func_205060_a(this.bipedHead.rotateAngleX, (-(float)Math.PI / 4F), this.field_205061_a);
         } else {
            this.bipedHead.rotateAngleX = this.func_205060_a(this.bipedHead.rotateAngleX, headPitch * ((float)Math.PI / 180F), this.field_205061_a);
         }
      } else {
         this.bipedHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      }

      this.bipedBody.rotateAngleY = 0.0F;
      this.bipedRightArm.rotationPointZ = 0.0F;
      this.bipedRightArm.rotationPointX = -5.0F;
      this.bipedLeftArm.rotationPointZ = 0.0F;
      this.bipedLeftArm.rotationPointX = 5.0F;
      float f = 1.0F;
      if (flag) {
         f = (float)(entityIn.motionX * entityIn.motionX + entityIn.motionY * entityIn.motionY + entityIn.motionZ * entityIn.motionZ);
         f = f / 0.2F;
         f = f * f * f;
      }

      if (f < 1.0F) {
         f = 1.0F;
      }

      this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
      this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
      this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount / f;
      this.bipedRightLeg.rotateAngleY = 0.0F;
      this.bipedLeftLeg.rotateAngleY = 0.0F;
      this.bipedRightLeg.rotateAngleZ = 0.0F;
      this.bipedLeftLeg.rotateAngleZ = 0.0F;
      if (this.isRiding) {
         this.bipedRightArm.rotateAngleX += (-(float)Math.PI / 5F);
         this.bipedLeftArm.rotateAngleX += (-(float)Math.PI / 5F);
         this.bipedRightLeg.rotateAngleX = -1.4137167F;
         this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 10F);
         this.bipedRightLeg.rotateAngleZ = 0.07853982F;
         this.bipedLeftLeg.rotateAngleX = -1.4137167F;
         this.bipedLeftLeg.rotateAngleY = (-(float)Math.PI / 10F);
         this.bipedLeftLeg.rotateAngleZ = -0.07853982F;
      }

      this.bipedRightArm.rotateAngleY = 0.0F;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      switch(this.leftArmPose) {
      case EMPTY:
         this.bipedLeftArm.rotateAngleY = 0.0F;
         break;
      case BLOCK:
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.9424779F;
         this.bipedLeftArm.rotateAngleY = ((float)Math.PI / 6F);
         break;
      case ITEM:
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
         this.bipedLeftArm.rotateAngleY = 0.0F;
      }

      switch(this.rightArmPose) {
      case EMPTY:
         this.bipedRightArm.rotateAngleY = 0.0F;
         break;
      case BLOCK:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.9424779F;
         this.bipedRightArm.rotateAngleY = (-(float)Math.PI / 6F);
         break;
      case ITEM:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
         this.bipedRightArm.rotateAngleY = 0.0F;
         break;
      case THROW_SPEAR:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - (float)Math.PI;
         this.bipedRightArm.rotateAngleY = 0.0F;
      }

      if (this.leftArmPose == ModelBiped.ArmPose.THROW_SPEAR && this.rightArmPose != ModelBiped.ArmPose.BLOCK && this.rightArmPose != ModelBiped.ArmPose.THROW_SPEAR && this.rightArmPose != ModelBiped.ArmPose.BOW_AND_ARROW) {
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - (float)Math.PI;
         this.bipedLeftArm.rotateAngleY = 0.0F;
      }

      if (this.swingProgress > 0.0F) {
         EnumHandSide enumhandside = this.getMainHand(entityIn);
         ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
         float f1 = this.swingProgress;
         this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * ((float)Math.PI * 2F)) * 0.2F;
         if (enumhandside == EnumHandSide.LEFT) {
            this.bipedBody.rotateAngleY *= -1.0F;
         }

         this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
         this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
         this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
         f1 = 1.0F - this.swingProgress;
         f1 = f1 * f1;
         f1 = f1 * f1;
         f1 = 1.0F - f1;
         float f2 = MathHelper.sin(f1 * (float)Math.PI);
         float f3 = MathHelper.sin(this.swingProgress * (float)Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
         modelrenderer.rotateAngleX = (float)((double)modelrenderer.rotateAngleX - ((double)f2 * 1.2D + (double)f3));
         modelrenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
         modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress * (float)Math.PI) * -0.4F;
      }

      if (this.isSneak) {
         this.bipedBody.rotateAngleX = 0.5F;
         this.bipedRightArm.rotateAngleX += 0.4F;
         this.bipedLeftArm.rotateAngleX += 0.4F;
         this.bipedRightLeg.rotationPointZ = 4.0F;
         this.bipedLeftLeg.rotationPointZ = 4.0F;
         this.bipedRightLeg.rotationPointY = 9.0F;
         this.bipedLeftLeg.rotationPointY = 9.0F;
         this.bipedHead.rotationPointY = 1.0F;
      } else {
         this.bipedBody.rotateAngleX = 0.0F;
         this.bipedRightLeg.rotationPointZ = 0.1F;
         this.bipedLeftLeg.rotationPointZ = 0.1F;
         this.bipedRightLeg.rotationPointY = 12.0F;
         this.bipedLeftLeg.rotationPointY = 12.0F;
         this.bipedHead.rotationPointY = 0.0F;
      }

      this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
      this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
      this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
      this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
      if (this.rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
         this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
         this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
         this.bipedRightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = (-(float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
      } else if (this.leftArmPose == ModelBiped.ArmPose.BOW_AND_ARROW && this.rightArmPose != ModelBiped.ArmPose.THROW_SPEAR && this.rightArmPose != ModelBiped.ArmPose.BLOCK) {
         this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY - 0.4F;
         this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY;
         this.bipedRightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = (-(float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
      }

      if (this.field_205061_a > 0.0F) {
         float f4 = limbSwing % 26.0F;
         float f5 = this.swingProgress > 0.0F ? 0.0F : this.field_205061_a;
         if (f4 < 14.0F) {
            this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, 0.0F, this.field_205061_a);
            this.bipedRightArm.rotateAngleX = this.func_205059_b(this.bipedRightArm.rotateAngleX, 0.0F, f5);
            this.bipedLeftArm.rotateAngleY = this.func_205060_a(this.bipedLeftArm.rotateAngleY, (float)Math.PI, this.field_205061_a);
            this.bipedRightArm.rotateAngleY = this.func_205059_b(this.bipedRightArm.rotateAngleY, (float)Math.PI, f5);
            this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, (float)Math.PI + 1.8707964F * this.func_203068_a(f4) / this.func_203068_a(14.0F), this.field_205061_a);
            this.bipedRightArm.rotateAngleZ = this.func_205059_b(this.bipedRightArm.rotateAngleZ, (float)Math.PI - 1.8707964F * this.func_203068_a(f4) / this.func_203068_a(14.0F), f5);
         } else if (f4 >= 14.0F && f4 < 22.0F) {
            float f7 = (f4 - 14.0F) / 8.0F;
            this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, ((float)Math.PI / 2F) * f7, this.field_205061_a);
            this.bipedRightArm.rotateAngleX = this.func_205059_b(this.bipedRightArm.rotateAngleX, ((float)Math.PI / 2F) * f7, f5);
            this.bipedLeftArm.rotateAngleY = this.func_205060_a(this.bipedLeftArm.rotateAngleY, (float)Math.PI, this.field_205061_a);
            this.bipedRightArm.rotateAngleY = this.func_205059_b(this.bipedRightArm.rotateAngleY, (float)Math.PI, f5);
            this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, 5.012389F - 1.8707964F * f7, this.field_205061_a);
            this.bipedRightArm.rotateAngleZ = this.func_205059_b(this.bipedRightArm.rotateAngleZ, 1.2707963F + 1.8707964F * f7, f5);
         } else if (f4 >= 22.0F && f4 < 26.0F) {
            float f6 = (f4 - 22.0F) / 4.0F;
            this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f6, this.field_205061_a);
            this.bipedRightArm.rotateAngleX = this.func_205059_b(this.bipedRightArm.rotateAngleX, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f6, f5);
            this.bipedLeftArm.rotateAngleY = this.func_205060_a(this.bipedLeftArm.rotateAngleY, (float)Math.PI, this.field_205061_a);
            this.bipedRightArm.rotateAngleY = this.func_205059_b(this.bipedRightArm.rotateAngleY, (float)Math.PI, f5);
            this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, (float)Math.PI, this.field_205061_a);
            this.bipedRightArm.rotateAngleZ = this.func_205059_b(this.bipedRightArm.rotateAngleZ, (float)Math.PI, f5);
         }

         float f8 = 0.3F;
         float f9 = 0.33333334F;
         this.bipedLeftLeg.rotateAngleX = this.func_205059_b(this.bipedLeftLeg.rotateAngleX, 0.3F * MathHelper.cos(limbSwing * 0.33333334F + (float)Math.PI), this.field_205061_a);
         this.bipedRightLeg.rotateAngleX = this.func_205059_b(this.bipedRightLeg.rotateAngleX, 0.3F * MathHelper.cos(limbSwing * 0.33333334F), this.field_205061_a);
      }

      copyModelAngles(this.bipedHead, this.bipedHeadwear);
   }

   protected float func_205060_a(float p_205060_1_, float p_205060_2_, float p_205060_3_) {
      float f;
      for(f = p_205060_2_ - p_205060_1_; f < -(float)Math.PI; f += ((float)Math.PI * 2F)) {
         ;
      }

      while(f >= (float)Math.PI) {
         f -= ((float)Math.PI * 2F);
      }

      return p_205060_1_ + p_205060_3_ * f;
   }

   private float func_205059_b(float p_205059_1_, float p_205059_2_, float p_205059_3_) {
      return p_205059_1_ + (p_205059_2_ - p_205059_1_) * p_205059_3_;
   }

   private float func_203068_a(float p_203068_1_) {
      return -65.0F * p_203068_1_ + p_203068_1_ * p_203068_1_;
   }

   public void setModelAttributes(ModelBase model) {
      super.setModelAttributes(model);
      if (model instanceof ModelBiped) {
         ModelBiped modelbiped = (ModelBiped)model;
         this.leftArmPose = modelbiped.leftArmPose;
         this.rightArmPose = modelbiped.rightArmPose;
         this.isSneak = modelbiped.isSneak;
      }

   }

   public void setVisible(boolean visible) {
      this.bipedHead.showModel = visible;
      this.bipedHeadwear.showModel = visible;
      this.bipedBody.showModel = visible;
      this.bipedRightArm.showModel = visible;
      this.bipedLeftArm.showModel = visible;
      this.bipedRightLeg.showModel = visible;
      this.bipedLeftLeg.showModel = visible;
   }

   public void postRenderArm(float scale, EnumHandSide side) {
      this.getArmForSide(side).postRender(scale);
   }

   protected ModelRenderer getArmForSide(EnumHandSide side) {
      return side == EnumHandSide.LEFT ? this.bipedLeftArm : this.bipedRightArm;
   }

   protected EnumHandSide getMainHand(Entity entityIn) {
      if (entityIn instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)entityIn;
         EnumHandSide enumhandside = entitylivingbase.getPrimaryHand();
         return entitylivingbase.swingingHand == EnumHand.MAIN_HAND ? enumhandside : enumhandside.opposite();
      } else {
         return EnumHandSide.RIGHT;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ArmPose {
      EMPTY,
      ITEM,
      BLOCK,
      BOW_AND_ARROW,
      THROW_SPEAR;
   }
}