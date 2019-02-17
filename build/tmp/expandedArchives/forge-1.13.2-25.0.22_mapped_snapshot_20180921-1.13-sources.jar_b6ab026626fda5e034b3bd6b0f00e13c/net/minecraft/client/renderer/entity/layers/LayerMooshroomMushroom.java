package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderMooshroom;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerMooshroomMushroom implements LayerRenderer<EntityMooshroom> {
   private final RenderMooshroom mooshroomRenderer;

   public LayerMooshroomMushroom(RenderMooshroom mooshroomRendererIn) {
      this.mooshroomRenderer = mooshroomRendererIn;
   }

   public void render(EntityMooshroom entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (!entitylivingbaseIn.isChild() && !entitylivingbaseIn.isInvisible()) {
         BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
         this.mooshroomRenderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         GlStateManager.enableCull();
         GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.0F, -1.0F, 1.0F);
         GlStateManager.translatef(0.2F, 0.35F, 0.5F);
         GlStateManager.rotatef(42.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.pushMatrix();
         GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
         blockrendererdispatcher.renderBlockBrightness(Blocks.RED_MUSHROOM.getDefaultState(), 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.1F, 0.0F, -0.6F);
         GlStateManager.rotatef(42.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
         blockrendererdispatcher.renderBlockBrightness(Blocks.RED_MUSHROOM.getDefaultState(), 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         this.mooshroomRenderer.getMainModel().getHead().postRender(0.0625F);
         GlStateManager.scalef(1.0F, -1.0F, 1.0F);
         GlStateManager.translatef(0.0F, 0.7F, -0.2F);
         GlStateManager.rotatef(12.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
         blockrendererdispatcher.renderBlockBrightness(Blocks.RED_MUSHROOM.getDefaultState(), 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.cullFace(GlStateManager.CullFace.BACK);
         GlStateManager.disableCull();
      }
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}