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
@OnlyIn(Dist.CLIENT)
public class AlphaGrassBakedWrapper implements IBakedModel
{
	//Fields
	private final IBakedModel modelDefault;
	private final IBakedModel modelAlpha;
	private final Minecraft MC_INSTANCE = Minecraft.getInstance();

	public AlphaGrassBakedWrapper(final IBakedModel existing, final IBakedModel alpha)
	{
		modelDefault = existing;
		modelAlpha = alpha;

	}

	/* Methods */
	/* A proof of concept, barely works */
	public IBakedModel replaceTextures(@Nullable IBlockState state, EnumFacing side, Random random)
	{
		BetaPlus.LOGGER.info("replace.");
		IBakedModel modelIn = modelDefault;

		if (modelIn != null)
		{
			// Get original quads
			List<BakedQuad> combined = modelIn.getQuads(state, side, random);
			// List of new quads
			List<BakedQuad> newQuads = Lists.newArrayList();


			for (Iterator<BakedQuad> iter = combined.iterator(); iter.hasNext();)
			{
				BakedQuad quad = iter.next();
				// Remove
				iter.remove();
				TextureAtlasSprite sprite = ModelLoader.defaultTextureGetter().apply(ModelHelper.NORMAL_GRASS_BLOCK);
				BakedQuad outQuad = new BakedQuad(quad.getVertexData(), 0, side, sprite, true, DefaultVertexFormats.BLOCK);
				newQuads.add(outQuad);
			}

			BetaPlus.LOGGER.info("size: " + modelIn.getQuads(state, side, random).size());

			/*
			switch (side)
			{
				case UP: newQuads.add()
			}
			*/

			// REMOVES TOP OF BLOCK! YES
			//modelIn.getQuads(state, EnumFacing.UP, random).clear();
			//modelIn.getQuads(state, side, random).clear();


			/* Cannot Do this, Concurrent Modification Exception */
			/*
			for(BakedQuad quad : combined)
			{
				// Replace sprite
				TextureAtlasSprite spriteOut = quad.getSprite();
				TextureAtlasSprite sprite = ModelLoader.defaultTextureGetter().apply(NORMAL_GRASS_BLOCK);
				BetaPlus.LOGGER.info("Sprite Name: " + spriteOut.getName().toString());
				switch (side)
				{
					case UP: BetaPlus.LOGGER.info("Up");
						break;
					case DOWN:
						break;

					default: BetaPlus.LOGGER.info("Something!");
						break;
				}
						//new TextureAtlasSprite(ALPHA_GRASS_BLOCK ,quad.getSprite().getWidth(), quad.getSprite().getHeight());
				// Get the vertex Data, input back in
				BakedQuad outQuad = new BakedQuad(quad.getVertexData(), 0, side, sprite, true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.BLOCK);
				BetaPlus.LOGGER.info("Adding Quad" + modelIn.getQuads(state, side, random).size());
				modelIn.getQuads(state, side, random).add(outQuad);
			}
			*/
			// Does not work, crashes on Load
			/*
			for (Iterator<BakedQuad> iter = combined.iterator(); iter.hasNext();)
			{
				BakedQuad quad = iter.next();
				iter.remove();
				TextureAtlasSprite sprite = ModelLoader.defaultTextureGetter().apply(NORMAL_GRASS_BLOCK);
				BakedQuad outQuad = new BakedQuad(quad.getVertexData(), 0, side, sprite, true, DefaultVertexFormats.BLOCK);
				newQuads.add(outQuad);
				BetaPlus.LOGGER.info("Face: " + outQuad.getFace());
			}
			modelIn.getQuads(state, side, random).addAll(newQuads);
			*/



			//modelIn.getQuads(state, EnumFacing.UP, random).add();
		}
		return modelIn;
	}

	/* Returns the type of IBakedModel to use */
	/* It works */
	private IBakedModel getModelToUse(IBlockState state)
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
		IBakedModel model = getModelToUse(state);
		return model.getQuads(state, side, rand);
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
