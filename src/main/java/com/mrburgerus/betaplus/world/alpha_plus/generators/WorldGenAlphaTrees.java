package com.mrburgerus.betaplus.world.alpha_plus.generators;

import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.trees.AbstractTree;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.fml.common.IWorldGenerator;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Set;


/* UNUSED */
public class WorldGenAlphaTrees extends AbstractTree
{
	//Fields
	private int minTreeHeight = 4;

	public WorldGenAlphaTrees()
	{
		super();
	}

	protected int getHeight(Random p_208534_1_) {
		return this.minTreeHeight + p_208534_1_.nextInt(3);
	}

	@Nullable
	@Override
	protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random random)
	{
		return (AbstractTreeFeature<NoFeatureConfig>)(random.nextInt(10) == 0 ? new BigTreeFeature(true) : new TreeFeature(true));
	}
}
