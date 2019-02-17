package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSkyboxCube {
   private final ResourceLocation[] locations = new ResourceLocation[6];

   public RenderSkyboxCube(ResourceLocation texture) {
      for(int i = 0; i < 6; ++i) {
         this.locations[i] = new ResourceLocation(texture.getNamespace(), texture.getPath() + '_' + i + ".png");
      }

   }

   public void render(Minecraft mc, float rotX, float rotY) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.multMatrixf(Matrix4f.perspective(85.0D, (float)mc.mainWindow.getFramebufferWidth() / (float)mc.mainWindow.getFramebufferHeight(), 0.05F, 10.0F));
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.enableBlend();
      GlStateManager.disableAlphaTest();
      GlStateManager.disableCull();
      GlStateManager.depthMask(false);
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      int i = 2;

      for(int j = 0; j < 4; ++j) {
         GlStateManager.pushMatrix();
         float f = ((float)(j % 2) / 2.0F - 0.5F) / 256.0F;
         float f1 = ((float)(j / 2) / 2.0F - 0.5F) / 256.0F;
         float f2 = 0.0F;
         GlStateManager.translatef(f, f1, 0.0F);
         GlStateManager.rotatef(rotX, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(rotY, 0.0F, 1.0F, 0.0F);

         for(int k = 0; k < 6; ++k) {
            mc.getTextureManager().bindTexture(this.locations[k]);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            int l = 255 / (j + 1);
            if (k == 0) {
               bufferbuilder.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, l).endVertex();
            }

            if (k == 1) {
               bufferbuilder.pos(1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(1.0D, 1.0D, -1.0D).tex(1.0D, 1.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(1.0D, -1.0D, -1.0D).tex(1.0D, 0.0D).color(255, 255, 255, l).endVertex();
            }

            if (k == 2) {
               bufferbuilder.pos(1.0D, -1.0D, -1.0D).tex(0.0D, 0.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(1.0D, 1.0D, -1.0D).tex(0.0D, 1.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(-1.0D, 1.0D, -1.0D).tex(1.0D, 1.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(-1.0D, -1.0D, -1.0D).tex(1.0D, 0.0D).color(255, 255, 255, l).endVertex();
            }

            if (k == 3) {
               bufferbuilder.pos(-1.0D, -1.0D, -1.0D).tex(0.0D, 0.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(-1.0D, 1.0D, -1.0D).tex(0.0D, 1.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(-1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(-1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, l).endVertex();
            }

            if (k == 4) {
               bufferbuilder.pos(-1.0D, -1.0D, -1.0D).tex(0.0D, 0.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(1.0D, -1.0D, -1.0D).tex(1.0D, 0.0D).color(255, 255, 255, l).endVertex();
            }

            if (k == 5) {
               bufferbuilder.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(-1.0D, 1.0D, -1.0D).tex(0.0D, 1.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(1.0D, 1.0D, -1.0D).tex(1.0D, 1.0D).color(255, 255, 255, l).endVertex();
               bufferbuilder.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, l).endVertex();
            }

            tessellator.draw();
         }

         GlStateManager.popMatrix();
         GlStateManager.colorMask(true, true, true, false);
      }

      bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.matrixMode(5889);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.enableDepthTest();
   }
}