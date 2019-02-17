package net.minecraft.client.renderer;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IImageBuffer {
   NativeImage parseUserSkin(NativeImage nativeImageIn);

   void skinAvailable();
}