package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelTropicalFishA extends ModelBase {
   private final ModelRenderer field_204235_a;
   private final ModelRenderer field_204236_b;
   private final ModelRenderer field_204237_c;
   private final ModelRenderer field_204238_d;
   private final ModelRenderer field_204239_e;

   public ModelTropicalFishA() {
      this(0.0F);
   }

   public ModelTropicalFishA(float p_i48892_1_) {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 22;
      this.field_204235_a = new ModelRenderer(this, 0, 0);
      this.field_204235_a.addBox(-1.0F, -1.5F, -3.0F, 2, 3, 6, p_i48892_1_);
      this.field_204235_a.setRotationPoint(0.0F, 22.0F, 0.0F);
      this.field_204236_b = new ModelRenderer(this, 22, -6);
      this.field_204236_b.addBox(0.0F, -1.5F, 0.0F, 0, 3, 6, p_i48892_1_);
      this.field_204236_b.setRotationPoint(0.0F, 22.0F, 3.0F);
      this.field_204237_c = new ModelRenderer(this, 2, 16);
      this.field_204237_c.addBox(-2.0F, -1.0F, 0.0F, 2, 2, 0, p_i48892_1_);
      this.field_204237_c.setRotationPoint(-1.0F, 22.5F, 0.0F);
      this.field_204237_c.rotateAngleY = ((float)Math.PI / 4F);
      this.field_204238_d = new ModelRenderer(this, 2, 12);
      this.field_204238_d.addBox(0.0F, -1.0F, 0.0F, 2, 2, 0, p_i48892_1_);
      this.field_204238_d.setRotationPoint(1.0F, 22.5F, 0.0F);
      this.field_204238_d.rotateAngleY = (-(float)Math.PI / 4F);
      this.field_204239_e = new ModelRenderer(this, 10, -5);
      this.field_204239_e.addBox(0.0F, -3.0F, 0.0F, 0, 3, 6, p_i48892_1_);
      this.field_204239_e.setRotationPoint(0.0F, 20.5F, -3.0F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      this.field_204235_a.render(scale);
      this.field_204236_b.render(scale);
      this.field_204237_c.render(scale);
      this.field_204238_d.render(scale);
      this.field_204239_e.render(scale);
   }

   /**
    * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
    * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how "far"
    * arms and legs can swing at most.
    */
   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      float f = 1.0F;
      if (!entityIn.isInWater()) {
         f = 1.5F;
      }

      this.field_204236_b.rotateAngleY = -f * 0.45F * MathHelper.sin(0.6F * ageInTicks);
   }
}