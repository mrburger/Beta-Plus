package com.mrburgerus.betaplus.world.alpha_plus.generators;

import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;
import java.util.Set;


/* UNUSED */
public class WorldGenAlphaTrees extends AbstractTreeFeature<NoFeatureConfig>
{
	//Fields
	int minTreeHeight = 4;

	public WorldGenAlphaTrees(boolean notify)
	{
		super(notify);
	}

	@Override
	public boolean place(Set<BlockPos> changedBlocks, IWorld worldIn, Random rand, BlockPos position) {
		int i = this.getHeight(rand);
		boolean flag = true;
		if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getWorld().getHeight()) {
			for(int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
				int k = 1;
				if (j == position.getY()) {
					k = 0;
				}

				if (j >= position.getY() + 1 + i - 2) {
					k = 2;
				}

				BlockPos.MutableBlockPos mutableBlock = new BlockPos.MutableBlockPos();

				for(int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
					for(int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
						if (j >= 0 && j < worldIn.getWorld().getHeight()) {
							if (!this.canGrowInto(worldIn, mutableBlock.setPos(l, j, i1))) {
								flag = false;
							}
						} else {
							flag = false;
						}
					}
				}
			}

			if (!flag) {
				return false;
			} else {
				if (worldIn.getBlockState(position.down()).canSustainPlant(worldIn, position.down(), net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling) Blocks.OAK_SAPLING) && position.getY() < worldIn.getWorld().getHeight() - i - 1) {
					this.setDirtAt(worldIn, position.down(), position);
					int k2 = 3;
					int l2 = 0;

					for(int i3 = position.getY() - 3 + i; i3 <= position.getY() + i; ++i3) {
						int i4 = i3 - (position.getY() + i);
						int j1 = 1 - i4 / 2;

						for(int k1 = position.getX() - j1; k1 <= position.getX() + j1; ++k1) {
							int l1 = k1 - position.getX();

							for(int i2 = position.getZ() - j1; i2 <= position.getZ() + j1; ++i2) {
								int j2 = i2 - position.getZ();
								if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || rand.nextInt(2) != 0 && i4 != 0) {
									BlockPos blockpos = new BlockPos(k1, i3, i2);
									IBlockState iblockstate = worldIn.getBlockState(blockpos);
									Material material = iblockstate.getMaterial();
									if (iblockstate.canBeReplacedByLeaves(worldIn, blockpos) || material == Material.VINE) {
										this.setBlockState(worldIn, blockpos, Blocks.OAK_LEAVES.getDefaultState());
									}
								}
							}
						}
					}

					for(int j3 = 0; j3 < i; ++j3) {
						IBlockState iblockstate1 = worldIn.getBlockState(position.up(j3));
						Material material1 = iblockstate1.getMaterial();
						if (iblockstate1.canBeReplacedByLeaves(worldIn, position.up(j3)) || material1 == Material.VINE) {
							this.func_208520_a(changedBlocks, worldIn, position.up(j3), Blocks.OAK_LOG.getDefaultState());
						}
					}

					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}

	protected int getHeight(Random p_208534_1_) {
		return this.minTreeHeight + p_208534_1_.nextInt(3);
	}

}
