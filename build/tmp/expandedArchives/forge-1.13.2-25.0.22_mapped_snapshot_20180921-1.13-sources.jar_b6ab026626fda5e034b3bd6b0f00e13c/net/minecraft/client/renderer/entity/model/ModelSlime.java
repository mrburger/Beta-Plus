package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSlime extends ModelBase {
   /** The slime's bodies, both the inside box and the outside box */
   private final ModelRenderer slimeBodies;
   /** The slime's right eye */
   private final ModelRenderer slimeRightEye;
   /** The slime's left eye */
   private final ModelRenderer slimeLeftEye;
   /** The slime's mouth */
   private final ModelRenderer slimeMouth;

   public ModelSlime(int slimeBodyTexOffY) {
      if (slimeBodyTexOffY > 0) {
         this.slimeBodies = new ModelRenderer(this, 0, slimeBodyTexOffY);
         this.slimeBodies.addBox(-3.0F, 17.0F, -3.0F, 6, 6, 6);
         this.slimeRightEye = new ModelRenderer(this, 32, 0);
         this.slimeRightEye.addBox(-3.25F, 18.0F, -3.5F, 2, 2, 2);
         this.slimeLeftEye = new ModelRenderer(this, 32, 4);
         this.slimeLeftEye.addBox(1.25F, 18.0F, -3.5F, 2, 2, 2);
         this.slimeMouth = new ModelRenderer(this, 32, 8);
         this.slimeMouth.addBox(0.0F, 21.0F, -3.5F, 1, 1, 1);
      } else {
         this.slimeBodies = new ModelRenderer(this, 0, slimeBodyTexOffY);
         this.slimeBodies.addBox(-4.0F, 16.0F, -4.0F, 8, 8, 8);
         this.slimeRightEye = null;
         this.slimeLeftEye = null;
         this.slimeMouth = null;
      }

   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      GlStateManager.translatef(0.0F, 0.001F, 0.0F);
      this.slimeBodies.render(scale);
      if (this.slimeRightEye != null) {
         this.slimeRightEye.render(scale);
         this.slimeLeftEye.render(scale);
         this.slimeMouth.render(scale);
      }

   }
}