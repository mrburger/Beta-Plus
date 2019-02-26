package com.mrburgerus.betaplus.client.renderer.model;

import com.mrburgerus.betaplus.util.ResourceHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BakedModelAlphaGrass implements IBakedModel
{
	private ModelResourceLocation grassLoc;
	private ModelManager manager;
	private IBakedModel grassModel;

	public BakedModelAlphaGrass()
	{
		//Working
		//grassLoc = BlockModelShapes.getModelLocation(Blocks.GRASS_BLOCK.getDefaultState());
		grassLoc = new ModelResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_grass_block"));
		manager = Minecraft.getInstance().getModelManager();
		grassModel = manager.getModel(grassLoc);
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand)
	{

		return grassModel.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return grassModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d()
	{
		return grassModel.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return grassModel.isGui3d();
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return grassModel.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides()
	{
		return grassModel.getOverrides();
	}
}
