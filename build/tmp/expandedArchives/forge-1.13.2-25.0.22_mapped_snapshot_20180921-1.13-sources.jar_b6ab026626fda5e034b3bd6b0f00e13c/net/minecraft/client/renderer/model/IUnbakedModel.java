package net.minecraft.client.renderer.model;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IUnbakedModel extends net.minecraftforge.client.model.IModel<IUnbakedModel> {
   Collection<ResourceLocation> getOverrideLocations();

   Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors);

   /**
    * @deprecated Use {@link #bake(Function, Function, IModelState, boolean)}.
    */
   @Nullable
   @Deprecated
   default IBakedModel bake(Function<ResourceLocation, IUnbakedModel> modelGetter, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ModelRotation rotationIn, boolean uvlock) {
       return bake(modelGetter, spriteGetter, rotationIn, uvlock, net.minecraft.client.renderer.vertex.DefaultVertexFormats.BLOCK);
   }
}