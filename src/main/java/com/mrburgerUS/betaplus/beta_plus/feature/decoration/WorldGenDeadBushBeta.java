package com.mrburgerUS.betaplus.beta_plus.feature.decoration;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenDeadBushBeta
{
	public static void generateBush(World worldIn, Random rand, BlockPos position)
	{
		for (IBlockState iblockstate = worldIn.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, worldIn, position) || iblockstate.getBlock().isLeaves(iblockstate, worldIn, position)) && position.getY() > 0; iblockstate = worldIn.getBlockState(position))
		{
			position = position.down();
		}

		for (int i = 0; i < 4; ++i)
		{
			BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

			if (worldIn.isAirBlock(blockpos) && Blocks.DEADBUSH.canBlockStay(worldIn, blockpos, Blocks.DEADBUSH.getDefaultState()))
			{
				worldIn.setBlockState(blockpos, Blocks.DEADBUSH.getDefaultState(), 2);
			}
		}
	}


	public static void generateBushOld(World world, Random random, int baseX, int baseY, int baseZ)
	{
		Block block;
		while (((block = world.getBlockState(new BlockPos(baseX, baseY, baseZ)).getBlock()) == Blocks.AIR || block == Blocks.LEAVES) && baseY > 0)
		{
			--baseY;
		}
		for (int i = 0; i < 4; ++i)
		{
			int y;
			int z;
			int x = baseX + random.nextInt(8) - random.nextInt(8);
			if (!world.isAirBlock(new BlockPos(x, y = baseY + random.nextInt(4) - random.nextInt(4), z = baseZ + random.nextInt(8) - random.nextInt(8))) || !Blocks.DEADBUSH.canBlockStay(world, new BlockPos(x, y, z), Blocks.DEADBUSH.getDefaultState()))
				continue;
			world.setBlockState(new BlockPos(x, y, z), Blocks.DEADBUSH.getDefaultState());
		}
	}
}
