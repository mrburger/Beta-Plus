package com.mrburgerUS.betaplus.beta.feature.structure;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenDungeons extends WorldGenerator
{
	//Fields
	private static final ResourceLocation[] SPAWNERTYPES = new ResourceLocation[]{EntityList.getKey(EntitySkeleton.class), EntityList.getKey(EntityZombie.class), EntityList.getKey(EntityZombie.class), EntityList.getKey(EntitySpider.class)};

	//Methods
	public static void generateDungeons(World world, Random random, BlockPos pos)
	{
		for (int i = 0; i < 8; ++i)
		{
			int xRand = pos.getX() + random.nextInt(16) + 8;
			int yRand = random.nextInt(128);
			int zRand = pos.getZ() + random.nextInt(16) + 8;
			new WorldGenDungeons().generate(world, random, new BlockPos(xRand, yRand, zRand));
		}
	}

	//Overrides
	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position)
	{
		//Definitions
		int baseX = position.getX();
		int baseY = position.getY();
		int baseZ = position.getZ();

		int x;
		int y;
		int z;
		int var6 = 3;
		int var7 = rand.nextInt(2) + 2;
		int var8 = rand.nextInt(2) + 2;
		int var9 = 0;
		for (x = baseX - var7 - 1; x <= baseX + var7 + 1; ++x)
		{
			for (y = baseY - 1; y <= baseY + var6 + 1; ++y)
			{
				for (z = baseZ - var8 - 1; z <= baseZ + var8 + 1; ++z)
				{
					Material material = worldIn.getBlockState(new BlockPos(x, y, z)).getMaterial();
					if (y == baseY - 1 && !material.isSolid())
					{
						return false;
					}
					if (y == baseY + var6 + 1 && !material.isSolid())
					{
						return false;
					}
					if (x != baseX - var7 - 1 && x != baseX + var7 + 1 && z != baseZ - var8 - 1 && z != baseZ + var8 + 1 || y != baseY || !worldIn.isAirBlock(new BlockPos(x, y, z)) || !worldIn.isAirBlock(new BlockPos(x, y + 1, z)))
						continue;
					++var9;
				}
			}
		}
		if (var9 >= 1 && var9 <= 5)
		{
			for (x = baseX - var7 - 1; x <= baseX + var7 + 1; ++x)
			{
				for (y = baseY + var6; y >= baseY - 1; --y)
				{
					for (z = baseZ - var8 - 1; z <= baseZ + var8 + 1; ++z)
					{
						BlockPos pos = new BlockPos(x, y, z);
						if (x != baseX - var7 - 1 && y != baseY - 1 && z != baseZ - var8 - 1 && x != baseX + var7 + 1 && y != baseY + var6 + 1 && z != baseZ + var8 + 1)
						{
							worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
							continue;
						}
						if (y >= 0 && !worldIn.getBlockState(new BlockPos(x, y - 1, z)).getMaterial().isSolid())
						{
							worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
							continue;
						}
						if (!worldIn.getBlockState(pos).getMaterial().isSolid()) continue;
						if (y == baseY - 1 && rand.nextInt(4) != 0)
						{
							worldIn.setBlockState(pos, Blocks.MOSSY_COBBLESTONE.getDefaultState());
							continue;
						}
						worldIn.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
					}
				}
			}
			block6:
			for (int i = 0; i < 2; ++i)
			{
				for (int j = 0; j < 3; ++j)
				{
					int z2;
					int x2 = baseX + rand.nextInt(var7 * 2 + 1) - var7;
					if (!worldIn.isAirBlock(new BlockPos(x2, baseY, z2 = baseZ + rand.nextInt(var8 * 2 + 1) - var8)))
						continue;
					int var15 = 0;
					if (worldIn.getBlockState(new BlockPos(x2 - 1, baseY, z2)).getMaterial().isSolid())
					{
						++var15;
					}
					if (worldIn.getBlockState(new BlockPos(x2 + 1, baseY, z2)).getMaterial().isSolid())
					{
						++var15;
					}
					if (worldIn.getBlockState(new BlockPos(x2, baseY, z2 - 1)).getMaterial().isSolid())
					{
						++var15;
					}
					if (worldIn.getBlockState(new BlockPos(x2, baseY, z2 + 1)).getMaterial().isSolid())
					{
						++var15;
					}
					if (var15 != 1) continue;
					worldIn.setBlockState(new BlockPos(x2, baseY, z2), Blocks.CHEST.getDefaultState());
					TileEntityChest chest = (TileEntityChest) worldIn.getTileEntity(new BlockPos(x2, baseY, z2));
					for (int count = 0; count < 8; ++count)
					{
						// TODO IMPLEMENT LOGIC FOR CHEST FILLING
					}


					continue block6;
				}
			}

			// Set Spawner Type
			worldIn.setBlockState(position, Blocks.MOB_SPAWNER.getDefaultState(), 2);
			TileEntity tileentity = worldIn.getTileEntity(position);

			if (tileentity instanceof TileEntityMobSpawner)
			{
				((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setEntityId(pickMobSpawner(rand));
			}
			return true;
		}
		return false;
	}

	private static ResourceLocation pickMobSpawner(Random random)
	{
		int i = random.nextInt(4);
		return SPAWNERTYPES[i];
	}
}
