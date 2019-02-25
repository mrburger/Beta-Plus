package com.mrburgerus.betaplus.client.renderer.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.ResourceHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;


/* Built with help from TheGreyGhost tutorial */
/* Truthfully, rendering is a strange thing */
public class AlphaGrassBakedModel implements IBakedModel
{
	//Fields
	private IBakedModel modelDefault;
	public static final ModelResourceLocation ALPHA_GRASS_MODEL_LOCATION = new ModelResourceLocation(ResourceHelper.getResourceStringBetaPlus("alpha_grass_block"));

	public AlphaGrassBakedModel(IBakedModel existing)
	{
		modelDefault = existing;
	}

	/* Methods */
	public IBakedModel replaceTextures(@Nullable IBlockState blockState, Random random)
	{
		IBakedModel modelIn = modelDefault;

		if (modelIn != null)
		{
			Map<String, String> newTexMap = Maps.newHashMap();

			BetaPlus.LOGGER.info("Marker!");

			// REMOVES TOP OF BLOCK! YES
			modelIn.getQuads(blockState, EnumFacing.UP, random).clear();
		}
		BetaPlus.LOGGER.info("Somehow we got here...");
		return modelIn;
	}


	/* Needs to be "figured out" */
	/* Taken from TheGreyGhost */
	private IBakedModel handleBlockState(@Nullable IBlockState iBlockState)
	{
		IBakedModel retval = modelDefault;  // default
		IBlockState UNCAMOUFLAGED_BLOCK = Blocks.AIR.getDefaultState();

		/*
		// Extract the block to be copied from the IExtendedBlockState, previously set by Block.getExtendedState()
		// If the block is null, the block is not camouflaged so use the uncamouflaged model.
		if (iBlockState instanceof IExtendedBlockState) {
			IExtendedBlockState iExtendedBlockState = (IExtendedBlockState) iBlockState;
			IBlockState copiedBlockIBlockState = iExtendedBlockState.getValue(BlockCamouflage.COPIEDBLOCK);

			if (copiedBlockIBlockState != UNCAMOUFLAGED_BLOCK) {
				// Retrieve the IBakedModel of the copied block and return it.
				Minecraft mc = Minecraft.getInstance();
				BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
				BlockModelShapes blockModelShapes = blockRendererDispatcher.getBlockModelShapes();
				retval = blockModelShapes.getModel(copiedBlockIBlockState);
			}
		}
		*/
		return retval;
	}

	/* Overrides */
	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand)
	{
		return replaceTextures(state, rand).getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return modelDefault.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d()
	{
		return modelDefault.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return modelDefault.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return modelDefault.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides()
	{
		return modelDefault.getOverrides();
	}
}
