package com.mrburgerUS.betaplus.beta.feature.structure;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureStart;

import java.util.List;
import java.util.Random;

public class WorldGenVillage extends MapGenStructure
{
	private static List<Biome> VILLAGE_SPAWN_BIOMES = MapGenVillage.VILLAGE_SPAWN_BIOMES;

	private int size;
	private int distance;

	public WorldGenVillage(int dist)
	{
		this.distance = dist;
		//Temporary
		this.size = 0;
	}

	public String getStructureName()
	{
		return "Village";
	}

	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
	{
		int i = chunkX;
		int j = chunkZ;

		if (chunkX < 0)
		{
			chunkX -= this.distance - 1;
		}

		if (chunkZ < 0)
		{
			chunkZ -= this.distance - 1;
		}

		int k = chunkX / this.distance;
		int l = chunkZ / this.distance;
		Random random = this.world.setRandomSeed(k, l, 10387312);
		k = k * this.distance;
		l = l * this.distance;
		k = k + random.nextInt(this.distance - 8);
		l = l + random.nextInt(this.distance - 8);

		if (i == k && j == l)
		{
			for (Biome biome : VILLAGE_SPAWN_BIOMES)
			{
				if (world.getBiome(new BlockPos(i * 16 + 8, 0, j * 16 + 8)) == biome)
				{
					return true;
				}
			}
		}

		return false;
	}

	public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored)
	{
		this.world = worldIn;
		return findNearestStructurePosBySpacing(worldIn, this, pos, this.distance, 8, 10387312, false, 100, findUnexplored);
	}

	protected StructureStart getStructureStart(int chunkX, int chunkZ)
	{
		return new MapGenVillage.Start(this.world, this.rand, chunkX, chunkZ, this.size);
	}
}
