package com.mrburgerus.betaplus.world.biome;

import com.google.common.collect.Sets;
import com.mrburgerus.betaplus.world.beta_plus.BetaPlusGenSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.feature.structure.Structure;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.Set;

/* Backup Biome Provider In Case First cannot be modified to work */
public class BiomeProviderBetaPlus2 extends BiomeProvider
{
	// Fields
	private final BiomeCache cache = new BiomeCache(this);
	private static final Biome[] biomesList = buildBiomesList();

	// New Fields Since First Provider


	public BiomeProviderBetaPlus2(BetaPlusGenSettings settings)
	{
 		/* HOW IT WORKS, PT. 2 */
 		/* As I understand it, the Biome Provider has a GenLayer called "genBiomes"
 		This GenLayer has a method called "generateBiomes", and returns a Biome[]
		This GenLayer is built by LayerUtil.buildOverworldProcedure()
		Possibly, I could make my own OverworldProcedure?
 		 */
	}

	/* BEGIN OVERRIDES */
	@Nullable
	@Override
	public Biome getBiome(BlockPos blockPos, @Nullable Biome biome)
	{
		return null;
	}

	@Override
	public Biome[] getBiomes(int i, int i1, int i2, int i3)
	{
		return new Biome[0];
	}

	@Override
	public Biome[] getBiomes(int i, int i1, int i2, int i3, boolean b)
	{
		return new Biome[0];
	}

	@Override
	public Set<Biome> getBiomesInSquare(int i, int i1, int i2)
	{
		return null;
	}

	@Nullable
	@Override
	public BlockPos findBiomePosition(int i, int i1, int i2, List<Biome> list, Random random)
	{
		return null;
	}

	@Override
	public boolean hasStructure(Structure<?> structure)
	{
		return false;
	}

	@Override
	public Set<IBlockState> getSurfaceBlocks()
	{
		return null;
	}

	/* BEGIN WRITTEN METHODS */
	/* Builds Possible Biome List */
	private static Biome[] buildBiomesList()
	{
		BiomeGenBetaPlus[] betaPlusBiomes = BiomeGenBetaPlus.defaultB.getDeclaringClass().getEnumConstants();
		Set<Biome> biomeSet = Sets.newHashSet();
		for (int i = 0; i < betaPlusBiomes.length; i++)
		{
			biomeSet.add(betaPlusBiomes[i].handle);
		}
		return biomeSet.toArray(new Biome[0]);
	}

	public Biome[] generateBiomes(int xChunk, int zChunk, int xSize, int zSize, @Nullable Biome fallbackBiome) {
		AreaDimension areaDim = new AreaDimension(xChunk, zChunk, xSize, zSize);
		//LazyArea laz = (LazyArea)this.lazyAreaFactory.make(areaDim);
		Biome[] biomeArr = new Biome[xSize * zSize];

		for(int z = 0; z < zSize; ++z) {
			for(int x = 0; x < xSize; ++x) {
				//biomeArr[x + z * xSize] = Biome.getBiome(laz.getValue(x, z), fallbackBiome);
			}
		}

		return biomeArr;
	}
}
