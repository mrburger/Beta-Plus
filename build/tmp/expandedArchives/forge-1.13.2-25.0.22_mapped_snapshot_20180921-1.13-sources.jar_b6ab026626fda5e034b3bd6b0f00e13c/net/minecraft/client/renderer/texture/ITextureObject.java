package net.minecraft.client.renderer.texture;

import java.io.IOException;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ITextureObject {
   void setBlurMipmap(boolean blurIn, boolean mipmapIn);

   void restoreLastBlurMipmap();

   void loadTexture(IResourceManager manager) throws IOException;

   int getGlTextureId();

   default void bindTexture() {
      GlStateManager.bindTexture(this.getGlTextureId());
   }
}