package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityDrowned;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelDrowned extends ModelZombie {
   public ModelDrowned(float p_i48915_1_, float p_i48915_2_, int p_i48915_3_, int p_i48915_4_) {
      super(p_i48915_1_, p_i48915_2_, p_i48915_3_, p_i48915_4_);
      this.bipedRightArm = new ModelRenderer(this, 32, 48);
      this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, p_i48915_1_);
      this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + p_i48915_2_, 0.0F);
      this.bipedRightLeg = new ModelRenderer(this, 16, 48);
      this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, p_i48915_1_);
      this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + p_i48915_2_, 0.0F);
   }

   public ModelDrowned(float p_i49398_1_, boolean p_i49398_2_) {
      super(p_i49398_1_, 0.0F, 64, p_i49398_2_ ? 32 : 64);
   }

   /**
    * Used for easily adding entity-dependent animations. The second and third float params here are the same second and
    * third as in the setRotationAngles method.
    */
   public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
      this.rightArmPose = ModelBiped.ArmPose.EMPTY;
      this.leftArmPose = ModelBiped.ArmPose.EMPTY;
      ItemStack itemstack = entitylivingbaseIn.getHeldItem(EnumHand.MAIN_HAND);
      if (itemstack.getItem() == Items.TRIDENT && ((EntityDrowned)entitylivingbaseIn).isArmsRaised()) {
         if (entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT) {
            this.rightArmPose = ModelBiped.ArmPose.THROW_SPEAR;
         } else {
            this.leftArmPose = ModelBiped.ArmPose.THROW_SPEAR;
         }
      }

      super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
   }

   /**
    * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
    * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how "far"
    * arms and legs can swing at most.
    */
   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      if (this.leftArmPose == ModelBiped.ArmPose.THROW_SPEAR) {
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - (float)Math.PI;
         this.bipedLeftArm.rotateAngleY = 0.0F;
      }

      if (this.rightArmPose == ModelBiped.ArmPose.THROW_SPEAR) {
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - (float)Math.PI;
         this.bipedRightArm.rotateAngleY = 0.0F;
      }

      if (this.field_205061_a > 0.0F) {
         this.bipedRightArm.rotateAngleX = this.func_205060_a(this.bipedRightArm.rotateAngleX, -2.5132742F, this.field_205061_a) + this.field_205061_a * 0.35F * MathHelper.sin(0.1F * ageInTicks);
         this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, -2.5132742F, this.field_205061_a) - this.field_205061_a * 0.35F * MathHelper.sin(0.1F * ageInTicks);
         this.bipedRightArm.rotateAngleZ = this.func_205060_a(this.bipedRightArm.rotateAngleZ, -0.15F, this.field_205061_a);
         this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, 0.15F, this.field_205061_a);
         this.bipedLeftLeg.rotateAngleX -= this.field_205061_a * 0.55F * MathHelper.sin(0.1F * ageInTicks);
         this.bipedRightLeg.rotateAngleX += this.field_205061_a * 0.55F * MathHelper.sin(0.1F * ageInTicks);
         this.bipedHead.rotateAngleX = 0.0F;
      }

   }
}