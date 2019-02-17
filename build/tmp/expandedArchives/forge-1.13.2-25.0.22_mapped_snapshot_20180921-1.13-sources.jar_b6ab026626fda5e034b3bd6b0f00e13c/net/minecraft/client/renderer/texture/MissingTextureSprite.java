package net.minecraft.client.renderer.texture;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class MissingTextureSprite extends TextureAtlasSprite {
   private static final ResourceLocation LOCATION = new ResourceLocation("missingno");
   @Nullable
   private static DynamicTexture dynamicTexture;
   private static final NativeImage IMAGE = new NativeImage(16, 16, false);
   private static final MissingTextureSprite INSTANCE = Util.make(() -> {
      MissingTextureSprite missingtexturesprite = new MissingTextureSprite();
      int i = -16777216;
      int j = -524040;

      for(int k = 0; k < 16; ++k) {
         for(int l = 0; l < 16; ++l) {
            if (k < 8 ^ l < 8) {
               IMAGE.setPixelRGBA(l, k, -524040);
            } else {
               IMAGE.setPixelRGBA(l, k, -16777216);
            }
         }
      }

      IMAGE.untrack();
      return missingtexturesprite;
   });

   private MissingTextureSprite() {
      super(LOCATION, 16, 16);
      this.frames = new NativeImage[1];
      this.frames[0] = IMAGE;
   }

   public static MissingTextureSprite getSprite() {
      return INSTANCE;
   }

   public static ResourceLocation getLocation() {
      return LOCATION;
   }

   public void clearFramesTextureData() {
      for(int i = 1; i < this.frames.length; ++i) {
         this.frames[i].close();
      }

      this.frames = new NativeImage[1];
      this.frames[0] = IMAGE;
   }

   public static DynamicTexture getDynamicTexture() {
      if (dynamicTexture == null) {
         dynamicTexture = new DynamicTexture(IMAGE);
         Minecraft.getInstance().getTextureManager().loadTexture(LOCATION, dynamicTexture);
      }

      return dynamicTexture;
   }
}