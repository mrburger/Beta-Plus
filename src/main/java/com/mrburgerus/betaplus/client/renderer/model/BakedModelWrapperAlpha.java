package com.mrburgerus.betaplus.client.renderer.model;

import com.google.common.collect.Lists;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.ResourceHelper;
import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


/* Built with help from TheGreyGhost tutorial */
/* Truthfully, rendering is a strange thing */
/* KEEP, IT WORKS */
@OnlyIn(Dist.CLIENT)
public class BakedModelWrapperAlpha implements IBakedModel
{
	//Fields
	private final IBakedModel modelDefault;
	private final IBakedModel modelAlpha;
	private final Minecraft MC_INSTANCE = Minecraft.getInstance();

	public BakedModelWrapperAlpha(final IBakedModel existing, final IBakedModel alpha)
	{
		modelDefault = existing;
		modelAlpha = alpha;

	}

	/* Returns the type of IBakedModel to use */
	/* It works, but slows the rendering. */
	private IBakedModel getModelToUse()
	{
		World world = MC_INSTANCE.world;
		if (world != null && world.isRemote() && world.getWorldType() instanceof WorldTypeAlphaPlus)
		{
			return modelAlpha;
		}
		return modelDefault;
	}

	/* Overrides */

	/* Called EVERY time a model is rendered, return ONLY a pre-baked Model! */
	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand)
	{
		IBakedModel model = getModelToUse();
		return model.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return getModelToUse().isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d()
	{
		return getModelToUse().isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		//This could be culprit, if the default model is Grass it will want to use the same renderer, I am guessing.
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return getModelToUse().getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides()
	{
		// Originally original Model
		return getModelToUse().getOverrides();
	}
}
