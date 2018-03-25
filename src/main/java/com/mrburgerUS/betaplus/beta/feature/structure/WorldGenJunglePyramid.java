package com.mrburgerUS.betaplus.beta.feature.structure;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

import javax.annotation.Nullable;

public class WorldGenJunglePyramid extends MapGenStructure
{
	//Fields
	private int maxDistanceBetweenPyramids = 9; //MUST be at least 9
	private String structureName = "Jungle_Temple";

	public WorldGenJunglePyramid()
	{
		//MapGenStructureIO.registerStructure(WorldGenJunglePyramid.Start.class, structureName);
		//MapGenStructureIO.registerStructureComponent(WorldGenJunglePyramid.DesertPyramid.class, structureName);
	}

	@Override
	public String getStructureName()
	{
		return structureName;
	}

	@Nullable
	@Override
	public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored)
	{
		return null;
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
	{
		return false;
	}

	@Override
	protected StructureStart getStructureStart(int chunkX, int chunkZ)
	{
		return null;
	}
}
