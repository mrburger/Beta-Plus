package com.mrburgerus.betaplus.client.render;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
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
	public static WorldType worldType;

	public BakedModelWrapperAlpha(final IBakedModel existing, final IBakedModel alpha)
	{
		modelDefault = existing;
		modelAlpha = alpha;
	}

	/* Returns the type of IBakedModel to use */
	/* It works, but slows the rendering. */
	private IBakedModel getModelToUse()
	{
		if (worldType != null && worldType instanceof WorldTypeAlphaPlus)
		{
			return modelAlpha;
		}
		return modelDefault;
	}

	/* Overrides */

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random)
	{
		IBakedModel model = getModelToUse();
		return model.getQuads(blockState, direction, random);
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
