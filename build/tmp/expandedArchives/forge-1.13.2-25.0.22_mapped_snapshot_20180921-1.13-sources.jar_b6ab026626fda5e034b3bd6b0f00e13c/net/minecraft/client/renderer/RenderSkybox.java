package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSkybox {
   private final Minecraft mc;
   private final RenderSkyboxCube renderer;
   private float time;

   public RenderSkybox(RenderSkyboxCube rendererIn) {
      this.renderer = rendererIn;
      this.mc = Minecraft.getInstance();
   }

   public void render(float partialTicks) {
      this.time += partialTicks;
      this.renderer.render(this.mc, MathHelper.sin(this.time * 0.001F) * 5.0F + 25.0F, -this.time * 0.1F);
      this.mc.mainWindow.setupOverlayRendering();
   }
}