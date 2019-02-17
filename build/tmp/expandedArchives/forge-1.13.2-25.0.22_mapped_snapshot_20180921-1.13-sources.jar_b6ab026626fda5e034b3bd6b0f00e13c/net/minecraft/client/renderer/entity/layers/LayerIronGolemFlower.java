package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderIronGolem;
import net.minecraft.client.renderer.entity.model.ModelIronGolem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.init.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerIronGolemFlower implements LayerRenderer<EntityIronGolem> {
   private final RenderIronGolem ironGolemRenderer;

   public LayerIronGolemFlower(RenderIronGolem ironGolemRendererIn) {
      this.ironGolemRenderer = ironGolemRendererIn;
   }

   public void render(EntityIronGolem entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (entitylivingbaseIn.getHoldRoseTick() != 0) {
         BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
         GlStateManager.enableRescaleNormal();
         GlStateManager.pushMatrix();
         GlStateManager.rotatef(5.0F + 180.0F * ((ModelIronGolem)this.ironGolemRenderer.getMainModel()).func_205071_a().rotateAngleX / (float)Math.PI, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(-0.9375F, -0.625F, -0.9375F);
         float f = 0.5F;
         GlStateManager.scalef(0.5F, -0.5F, 0.5F);
         int i = entitylivingbaseIn.getBrightnessForRender();
         int j = i % 65536;
         int k = i / 65536;
         OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, (float)j, (float)k);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.ironGolemRenderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         blockrendererdispatcher.renderBlockBrightness(Blocks.POPPY.getDefaultState(), 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}