package com.mrburgerUS.betaplus.beta_plus.feature.structure;

import com.mrburgerUS.betaplus.beta_plus.biome.EnumBetaBiome;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class WorldGenIgloo extends MapGenStructure
{
	//Fields
	private String structureName = "Igloo";
	private int maxDistanceBetweenPyramids;

	public WorldGenIgloo(int distance)
	{
		MapGenStructureIO.registerStructure(Start.class, structureName);
		MapGenStructureIO.registerStructureComponent(Igloo.class, structureName);
		maxDistanceBetweenPyramids = distance;
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
		return findNearestStructurePosBySpacing(worldIn, this, pos, maxDistanceBetweenPyramids, 8, 14357617, false, 100, findUnexplored);
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
	{
		int i = chunkX;
		int j = chunkZ;

		if (chunkX < 0)
		{
			chunkX -= this.maxDistanceBetweenPyramids - 1;
		}

		if (chunkZ < 0)
		{
			chunkZ -= this.maxDistanceBetweenPyramids - 1;
		}

		int k = chunkX / this.maxDistanceBetweenPyramids;
		int l = chunkZ / this.maxDistanceBetweenPyramids;
		Random random = this.world.setRandomSeed(k, l, 14357617);
		k = k * this.maxDistanceBetweenPyramids;
		l = l * this.maxDistanceBetweenPyramids;
		k = k + random.nextInt(this.maxDistanceBetweenPyramids - 8);
		l = l + random.nextInt(this.maxDistanceBetweenPyramids - 8);

		if (i == k && j == l)
		{
			return world.getBiome(new BlockPos(i * 16 + 8, 0, j * 16 + 8)) == EnumBetaBiome.tundra.handle;
		}

		return false;
	}

	@Override
	protected StructureStart getStructureStart(int chunkX, int chunkZ)
	{
		return new Start(this.rand, chunkX, chunkZ, world.getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8)));
	}

	public static class Start extends StructureStart
	{
		public Start(Random random, int chunkX, int chunkZ, Biome biomeIn)
		{
			if (biomeIn == EnumBetaBiome.tundra.handle)
			{
				Igloo iglooPieces = new Igloo(random, chunkX * 16, chunkZ * 16);
				components.add(iglooPieces);
				this.updateBoundingBox();
			}
		}
	}

	public static class Igloo extends FeatureBeta
	{
		private static final ResourceLocation IGLOO_TOP_ID = new ResourceLocation("igloo/igloo_top");
		private static final ResourceLocation IGLOO_MIDDLE_ID = new ResourceLocation("igloo/igloo_middle");
		private static final ResourceLocation IGLOO_BOTTOM_ID = new ResourceLocation("igloo/igloo_bottom");

		public Igloo(Random rand, int x, int z)
		{
			super(rand, x, 64, z, 7, 5, 8);
		}

		/**
		 * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
		 * Mineshafts at the end, it adds Fences...
		 */
		public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
		{
			if (!this.offsetToAverageGroundLevel(worldIn, structureBoundingBoxIn, -1))
			{
				return false;
			}
			else
			{
				StructureBoundingBox structureboundingbox = this.getBoundingBox();
				BlockPos blockpos = new BlockPos(structureboundingbox.minX, structureboundingbox.minY, structureboundingbox.minZ);
				Rotation[] arotation = Rotation.values();
				MinecraftServer minecraftserver = worldIn.getMinecraftServer();
				TemplateManager templatemanager = worldIn.getSaveHandler().getStructureTemplateManager();
				PlacementSettings placementsettings = (new PlacementSettings()).setRotation(arotation[randomIn.nextInt(arotation.length)]).setReplacedBlock(Blocks.STRUCTURE_VOID).setBoundingBox(structureboundingbox);
				Template template = templatemanager.getTemplate(minecraftserver, IGLOO_TOP_ID);
				template.addBlocksToWorldChunk(worldIn, blockpos, placementsettings);

				if (randomIn.nextDouble() < 0.5D)
				{
					Template template1 = templatemanager.getTemplate(minecraftserver, IGLOO_MIDDLE_ID);
					Template template2 = templatemanager.getTemplate(minecraftserver, IGLOO_BOTTOM_ID);
					int i = randomIn.nextInt(8) + 4;

					for (int j = 0; j < i; ++j)
					{
						BlockPos blockpos1 = template.calculateConnectedPos(placementsettings, new BlockPos(3, -1 - j * 3, 5), placementsettings, new BlockPos(1, 2, 1));
						template1.addBlocksToWorldChunk(worldIn, blockpos.add(blockpos1), placementsettings);
					}

					BlockPos blockpos4 = blockpos.add(template.calculateConnectedPos(placementsettings, new BlockPos(3, -1 - i * 3, 5), placementsettings, new BlockPos(3, 5, 7)));
					template2.addBlocksToWorldChunk(worldIn, blockpos4, placementsettings);
					Map<BlockPos, String> map = template2.getDataBlocks(blockpos4, placementsettings);

					for (Map.Entry<BlockPos, String> entry : map.entrySet())
					{
						if ("chest".equals(entry.getValue()))
						{
							BlockPos blockpos2 = entry.getKey();
							worldIn.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3);
							TileEntity tileentity = worldIn.getTileEntity(blockpos2.down());

							if (tileentity instanceof TileEntityChest)
							{
								((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_IGLOO_CHEST, randomIn.nextLong());
							}
						}
					}
				}
				else
				{
					BlockPos blockpos3 = Template.transformedBlockPos(placementsettings, new BlockPos(3, 0, 5));
					worldIn.setBlockState(blockpos.add(blockpos3), Blocks.SNOW.getDefaultState(), 3);
				}

				return true;
			}
		}
	}
}
