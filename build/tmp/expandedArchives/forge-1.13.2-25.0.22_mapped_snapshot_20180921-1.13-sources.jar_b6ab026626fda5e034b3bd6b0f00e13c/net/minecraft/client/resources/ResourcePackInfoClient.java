package net.minecraft.client.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResourcePackInfoClient extends ResourcePackInfo {
   @Nullable
   private final NativeImage field_195809_a;
   @Nullable
   private ResourceLocation field_195810_b;

   public ResourcePackInfoClient(String p_i48113_1_, boolean p_i48113_2_, Supplier<IResourcePack> p_i48113_3_, IResourcePack p_i48113_4_, PackMetadataSection p_i48113_5_, ResourcePackInfo.Priority p_i48113_6_) {
      super(p_i48113_1_, p_i48113_2_, p_i48113_3_, p_i48113_4_, p_i48113_5_, p_i48113_6_);
      NativeImage nativeimage = null;

      try (InputStream inputstream = p_i48113_4_.getRootResourceStream("pack.png")) {
         nativeimage = NativeImage.read(inputstream);
      } catch (IllegalArgumentException | IOException var21) {
         ;
      }

      this.field_195809_a = nativeimage;
   }

   public ResourcePackInfoClient(String p_i48114_1_, boolean p_i48114_2_, Supplier<IResourcePack> p_i48114_3_, ITextComponent p_i48114_4_, ITextComponent p_i48114_5_, PackCompatibility p_i48114_6_, ResourcePackInfo.Priority p_i48114_7_, boolean p_i48114_8_, @Nullable NativeImage p_i48114_9_) {
      super(p_i48114_1_, p_i48114_2_, p_i48114_3_, p_i48114_4_, p_i48114_5_, p_i48114_6_, p_i48114_7_, p_i48114_8_);
      this.field_195809_a = p_i48114_9_;
   }

   public void func_195808_a(TextureManager p_195808_1_) {
      if (this.field_195810_b == null) {
         if (this.field_195809_a == null) {
            this.field_195810_b = new ResourceLocation("textures/misc/unknown_pack.png");
         } else {
            this.field_195810_b = p_195808_1_.getDynamicTextureLocation("texturepackicon", new DynamicTexture(this.field_195809_a));
         }
      }

      p_195808_1_.bindTexture(this.field_195810_b);
   }
}