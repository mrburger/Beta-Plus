package net.minecraft.client.renderer.texture;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DynamicTexture extends AbstractTexture implements AutoCloseable {
   @Nullable
   private NativeImage dynamicTextureData;

   public DynamicTexture(NativeImage nativeImageIn) {
      this.dynamicTextureData = nativeImageIn;
      TextureUtil.allocateTexture(this.getGlTextureId(), this.dynamicTextureData.getWidth(), this.dynamicTextureData.getHeight());
      this.updateDynamicTexture();
   }

   public DynamicTexture(int widthIn, int heightIn, boolean clearIn) {
      this.dynamicTextureData = new NativeImage(widthIn, heightIn, clearIn);
      TextureUtil.allocateTexture(this.getGlTextureId(), this.dynamicTextureData.getWidth(), this.dynamicTextureData.getHeight());
   }

   public void loadTexture(IResourceManager manager) throws IOException {
   }

   public void updateDynamicTexture() {
      this.bindTexture();
      this.dynamicTextureData.uploadTextureSub(0, 0, 0, false);
   }

   @Nullable
   public NativeImage getTextureData() {
      return this.dynamicTextureData;
   }

   public void setTextureData(NativeImage nativeImageIn) throws Exception {
      this.dynamicTextureData.close();
      this.dynamicTextureData = nativeImageIn;
   }

   public void close() {
      this.dynamicTextureData.close();
      this.deleteGlTexture();
      this.dynamicTextureData = null;
   }
}