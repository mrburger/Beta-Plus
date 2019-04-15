package com.mrburgerus.betaplus.world.beta_plus;

import com.mrburgerus.betaplus.world.beta_plus.noise.NoiseGeneratorOctavesBeta;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.Random;

public class BetaChunkGenerator extends ChunkGenerator<BetaChunkGeneratorConfig>
{
	private Random rand;
	private final BetaChunkGeneratorConfig settings;
	private BetaBiomeProvider biomeProviderS;
	private Biome[] biomesForGeneration;


	//Noise Generators
	private NoiseGeneratorOctavesBeta octaves1;
	private NoiseGeneratorOctavesBeta octaves2;
	private NoiseGeneratorOctavesBeta octaves3;
	private NoiseGeneratorOctavesBeta beachBlockNoise; // Formerly scaleNoise, used for Gravel and Sand, so probably beaches.
	private NoiseGeneratorOctavesBeta surfaceNoise; // Formerly octaves7
	private NoiseGeneratorOctavesBeta scaleNoise; // Formerly octaves6, renamed using ChunkGeneratorOverworld
	private NoiseGeneratorOctavesBeta octaves7;

	public BetaChunkGenerator(IWorld iWorld_1, BiomeSource biomeProvider, BetaChunkGeneratorConfig settingsIn)
	{
		super(iWorld_1, biomeProvider, settingsIn);

		this.settings = settingsIn;

		rand = new Random(seed);
		octaves1 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves2 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves3 = new NoiseGeneratorOctavesBeta(rand, 8);
		beachBlockNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		surfaceNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		scaleNoise = new NoiseGeneratorOctavesBeta(rand, 10);
		octaves7 = new NoiseGeneratorOctavesBeta(rand, 16);
		biomeProviderS = (BetaBiomeProvider) biomeProvider;
	}

	// Like makeBase
	@Override
	public void buildSurface(Chunk chunk)
	{
		int x = chunk.getPos().x;
		int z = chunk.getPos().z;
		rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
		biomesForGeneration = biomeProviderS.sampleBiomes(x * 16, z * 16, 16, 16);


	}

	@Override
	public int getSpawnHeight()
	{
		return 0;
	}

	@Override
	public void populateNoise(IWorld iWorld, Chunk chunk)
	{

	}

	// Possibly can be ignored
	@Override
	public int getHeightOnGround(int i, int i1, Heightmap.Type type)
	{
		return 0;
	}
}
