package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderTropicalFish;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelTropicalFishA;
import net.minecraft.client.renderer.entity.model.ModelTropicalFishB;
import net.minecraft.entity.passive.EntityTropicalFish;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerTropicalFishPattern implements LayerRenderer<EntityTropicalFish> {
   private final RenderTropicalFish fishRenderer;
   private final ModelTropicalFishA modelA;
   private final ModelTropicalFishB modelB;

   public LayerTropicalFishPattern(RenderTropicalFish fishRenderer) {
      this.fishRenderer = fishRenderer;
      this.modelA = new ModelTropicalFishA(0.008F);
      this.modelB = new ModelTropicalFishB(0.008F);
   }

   public void render(EntityTropicalFish entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (!entitylivingbaseIn.isInvisible()) {
         ModelBase modelbase = (ModelBase)(entitylivingbaseIn.getSize() == 0 ? this.modelA : this.modelB);
         this.fishRenderer.bindTexture(entitylivingbaseIn.getPatternTexture());
         float[] afloat = entitylivingbaseIn.func_204222_dD();
         GlStateManager.color3f(afloat[0], afloat[1], afloat[2]);
         modelbase.setModelAttributes(this.fishRenderer.getMainModel());
         modelbase.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
         modelbase.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      }
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}