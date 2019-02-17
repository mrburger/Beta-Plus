package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelHorseArmorChests extends ModelHorseArmorBase {
   private final ModelRenderer field_199057_c = new ModelRenderer(this, 26, 21);
   private final ModelRenderer field_199058_d;

   public ModelHorseArmorChests() {
      this.field_199057_c.addBox(-4.0F, 0.0F, -2.0F, 8, 8, 3);
      this.field_199058_d = new ModelRenderer(this, 26, 21);
      this.field_199058_d.addBox(-4.0F, 0.0F, -2.0F, 8, 8, 3);
      this.field_199057_c.rotateAngleY = (-(float)Math.PI / 2F);
      this.field_199058_d.rotateAngleY = ((float)Math.PI / 2F);
      this.field_199057_c.setRotationPoint(6.0F, -8.0F, 0.0F);
      this.field_199058_d.setRotationPoint(-6.0F, -8.0F, 0.0F);
      this.field_199049_a.addChild(this.field_199057_c);
      this.field_199049_a.addChild(this.field_199058_d);
   }

   protected void func_199047_a(ModelRenderer p_199047_1_) {
      ModelRenderer modelrenderer = new ModelRenderer(this, 0, 12);
      modelrenderer.addBox(-1.0F, -7.0F, 0.0F, 2, 7, 1);
      modelrenderer.setRotationPoint(1.25F, -10.0F, 4.0F);
      ModelRenderer modelrenderer1 = new ModelRenderer(this, 0, 12);
      modelrenderer1.addBox(-1.0F, -7.0F, 0.0F, 2, 7, 1);
      modelrenderer1.setRotationPoint(-1.25F, -10.0F, 4.0F);
      modelrenderer.rotateAngleX = 0.2617994F;
      modelrenderer.rotateAngleZ = 0.2617994F;
      modelrenderer1.rotateAngleX = 0.2617994F;
      modelrenderer1.rotateAngleZ = -0.2617994F;
      p_199047_1_.addChild(modelrenderer);
      p_199047_1_.addChild(modelrenderer1);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (((AbstractChestHorse)entityIn).hasChest()) {
         this.field_199057_c.showModel = true;
         this.field_199058_d.showModel = true;
      } else {
         this.field_199057_c.showModel = false;
         this.field_199058_d.showModel = false;
      }

      super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
   }
}