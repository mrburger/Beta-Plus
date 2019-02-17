package net.minecraft.client.renderer.entity.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureOffset {
   /** The x coordinate offset of the texture */
   public final int textureOffsetX;
   /** The y coordinate offset of the texture */
   public final int textureOffsetY;

   public TextureOffset(int textureOffsetXIn, int textureOffsetYIn) {
      this.textureOffsetX = textureOffsetXIn;
      this.textureOffsetY = textureOffsetYIn;
   }
}