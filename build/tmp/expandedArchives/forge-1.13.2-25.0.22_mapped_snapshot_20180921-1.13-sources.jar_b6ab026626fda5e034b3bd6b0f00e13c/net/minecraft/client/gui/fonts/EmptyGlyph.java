package net.minecraft.client.gui.fonts;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EmptyGlyph extends TexturedGlyph {
   public EmptyGlyph() {
      super(new ResourceLocation(""), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void render(TextureManager textureManagerIn, boolean isItalic, float x, float y, BufferBuilder buffer, float red, float green, float blue, float alpha) {
   }

   @Nullable
   public ResourceLocation getTextureLocation() {
      return null;
   }
}