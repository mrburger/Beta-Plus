package net.minecraft.client.gui;

import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiScreenLoading extends GuiScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation field_195190_f = new ResourceLocation("textures/gui/title/mojang.png");
   private ResourceLocation field_195191_g;

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      try {
         InputStream inputstream = this.mc.getPackFinder().getVanillaPack().getResourceStream(ResourcePackType.CLIENT_RESOURCES, field_195190_f);
         this.field_195191_g = this.mc.getTextureManager().getDynamicTextureLocation("logo", new DynamicTexture(NativeImage.read(inputstream)));
      } catch (IOException ioexception) {
         LOGGER.error("Unable to load logo: {}", field_195190_f, ioexception);
      }

   }

   /**
    * Called when the screen is unloaded. Used to disable keyboard repeat events
    */
   public void onGuiClosed() {
      this.mc.getTextureManager().deleteTexture(this.field_195191_g);
      this.field_195191_g = null;
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      Framebuffer framebuffer = new Framebuffer(this.width, this.height, true);
      framebuffer.bindFramebuffer(false);
      this.mc.getTextureManager().bindTexture(this.field_195191_g);
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      GlStateManager.disableDepthTest();
      GlStateManager.enableTexture2D();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.pos(0.0D, (double)this.mc.mainWindow.getFramebufferHeight(), 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
      bufferbuilder.pos((double)this.mc.mainWindow.getFramebufferWidth(), (double)this.mc.mainWindow.getFramebufferHeight(), 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
      bufferbuilder.pos((double)this.mc.mainWindow.getFramebufferWidth(), 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
      bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
      tessellator.draw();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      int i = 256;
      int j = 256;
      this.mc.draw((this.mc.mainWindow.getScaledWidth() - 256) / 2, (this.mc.mainWindow.getScaledHeight() - 256) / 2, 0, 0, 256, 256, 255, 255, 255, 255);
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      framebuffer.unbindFramebuffer();
      framebuffer.framebufferRender(this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight());
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.1F);
   }
}