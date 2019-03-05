package com.mrburgerUS.betaplus.beta_plus.feature.decoration;

import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenFlowersBeta
{
	//Fields
	static int flowerChance = 1;

	public static void generateFlowers(World world, Random random, int posX, int posZ)
	{
		for (int i = 0; i < flowerChance; ++i)
		{
			int xRand = random.nextInt(16) + 8;
			int zRand = random.nextInt(16) + 8;
			int yRandBound = world.getHeight(new BlockPos(posX, 0, posZ).add(xRand, 0, zRand)).getY() + 32;

			if (yRandBound > 0)
			{
				int yRand = random.nextInt(yRandBound);
				BlockPos blockPos = new BlockPos(posX, 0, posZ).add(xRand, yRand, zRand);
				//Dandelion
				generate(world, random, blockPos, Blocks.YELLOW_FLOWER, BlockFlower.EnumFlowerType.DANDELION, 32);
				//Poppy
				generate(world, random, blockPos, Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.POPPY, 32);
				//Orchid
				if (random.nextInt(8) == 0)
					generate(world, random, blockPos, Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.BLUE_ORCHID, 6);
				//Allium
				generate(world, random, blockPos, Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.ALLIUM, 12);
				//Azure Bluet (Houstonia)
				generate(world, random, blockPos, Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.HOUSTONIA, 12);
				//Red Tulip
				generate(world, random, blockPos, Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.RED_TULIP, 14);
				//Orange Tulip
				generate(world, random, blockPos, Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.ORANGE_TULIP, 14);
				//White Tulip
				generate(world, random, blockPos, Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.WHITE_TULIP, 14);
				//Pink Tulip
				generate(world, random, blockPos, Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.PINK_TULIP, 14);
				//Oxeye Daisy
				generate(world, random, blockPos, Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.OXEYE_DAISY, 11);
			}
		}
	}

	private static void generate(World world, Random random, BlockPos pos, BlockFlower flower, BlockFlower.EnumFlowerType type, int passes)
	{
		for (int i = 0; i < passes; ++i)
		{
			BlockPos blockPos = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

			if (!world.isAirBlock(blockPos) || !flower.canBlockStay(world, blockPos, flower.getDefaultState()))
				continue;
			world.setBlockState(blockPos, flower.getDefaultState().withProperty(flower.getTypeProperty(), type));
		}
	}
}
