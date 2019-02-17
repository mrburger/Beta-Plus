package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerSpinAttackEffect implements LayerRenderer<AbstractClientPlayer> {
   public static final ResourceLocation field_204836_a = new ResourceLocation("textures/entity/trident_riptide.png");
   private final RenderPlayer playerRenderer;
   private final LayerSpinAttackEffect.Model model;

   public LayerSpinAttackEffect(RenderPlayer playerRenderer) {
      this.playerRenderer = playerRenderer;
      this.model = new LayerSpinAttackEffect.Model();
   }

   public void render(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (entitylivingbaseIn.isSpinAttacking()) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.playerRenderer.bindTexture(field_204836_a);

         for(int i = 0; i < 3; ++i) {
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(ageInTicks * (float)(-(45 + i * 5)), 0.0F, 1.0F, 0.0F);
            float f = 0.75F * (float)i;
            GlStateManager.scalef(f, f, f);
            GlStateManager.translatef(0.0F, -0.2F + 0.6F * (float)i, 0.0F);
            this.model.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.popMatrix();
         }

      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   static class Model extends ModelBase {
      private final ModelRenderer field_204834_a;

      public Model() {
         this.textureWidth = 64;
         this.textureHeight = 64;
         this.field_204834_a = new ModelRenderer(this, 0, 0);
         this.field_204834_a.addBox(-8.0F, -16.0F, -8.0F, 16, 32, 16);
      }

      /**
       * Sets the models various rotation angles then renders the model.
       */
      public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
         this.field_204834_a.render(scale);
      }
   }
}