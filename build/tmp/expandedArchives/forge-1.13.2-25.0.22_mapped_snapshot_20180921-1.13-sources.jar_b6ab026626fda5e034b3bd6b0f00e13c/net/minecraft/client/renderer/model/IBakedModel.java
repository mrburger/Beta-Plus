package net.minecraft.client.renderer.model;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IBakedModel extends net.minecraftforge.client.extensions.IForgeBakedModel {
   List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand);

   boolean isAmbientOcclusion();

   boolean isGui3d();

   boolean isBuiltInRenderer();

   TextureAtlasSprite getParticleTexture();

   @Deprecated
   default ItemCameraTransforms getItemCameraTransforms() { return ItemCameraTransforms.DEFAULT; }

   ItemOverrideList getOverrides();
}