package com.mrburgerus.betaplus.world.beta_plus.sim;

import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.util.AbstractWorldSimulator;
import com.mrburgerus.betaplus.util.ConfigRetroPlus;
import com.mrburgerus.betaplus.world.beta_plus.BetaPlusGenSettings;
import com.mrburgerus.betaplus.world.beta_plus.BiomeProviderBetaPlus;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesBeta;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesBiome;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.Random;

import static com.mrburgerus.betaplus.world.beta_plus.ChunkGeneratorBetaPlus.CHUNK_SIZE;

public class BetaPlusSimulator extends AbstractWorldSimulator
{
	// Added for testing 0.5c
	private NoiseGeneratorOctavesBiome temperatureOctave;
	private NoiseGeneratorOctavesBiome humidityOctave;
	private double[] temps;
	private double[] humidities;
	private double scaleVal;
	private double mult;

	public BetaPlusSimulator(World world, BiomeProviderBetaPlus providerIn, BetaPlusGenSettings settings)
	{
		super(world);
		// Remember to assign values EXACTLY the same way, otherwise the .next[X]() value order will be disturbed.
		octaves1 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves2 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves3 = new NoiseGeneratorOctavesBeta(rand, 8);
		beachNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		surfaceNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		scaleNoise = new NoiseGeneratorOctavesBeta(rand, 10);
		octaves7 = new NoiseGeneratorOctavesBeta(rand, 16);

		// Testing 0.5c
		temperatureOctave = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 9871), 4);
		humidityOctave = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 39811), 4);
		scaleVal = settings.getScale();
		mult = settings.getMultiplierBiome();
	}

	@Override
	protected double[] generateOctaves(double[] values, int xPos, int yValueZero, int zPos, int sizeX, int sizeY, int sizeZ)
	{
		if (values == null)
		{
			values = new double[sizeX * sizeY * sizeZ];
		}
		double noiseFactor = ConfigRetroPlus.noiseScale;
		// Added 0.5c
		temps = temperatureOctave.generateOctaves(temps, (double) xPos * CHUNK_SIZE, (double) zPos * CHUNK_SIZE, sizeX * CHUNK_SIZE, sizeX * CHUNK_SIZE, scaleVal, scaleVal, 0.25);//provider.temperatures; //temperatureOctave.generateOctaves(temps, (double) xPos, (double) zPos, size1, size1, scaleVal, scaleVal, 0.25);
		humidities = humidityOctave.generateOctaves(humidities, (double) xPos * CHUNK_SIZE, (double) zPos * CHUNK_SIZE, sizeX * CHUNK_SIZE, sizeX * CHUNK_SIZE, scaleVal * mult, scaleVal * mult, 0.3333333333333333); //provider.humidities; //humidityOctave.generateOctaves(humidities, (double) xPos, (double) zPos, size1, size1, scaleVal * mult, scaleVal * mult, 0.3333333333333333);
		// Old stuff
		octaveArr4 = scaleNoise.generateNoiseOctaves(octaveArr4, xPos, zPos, sizeX, sizeZ, 1.121, 1.121, 0.5);
		octaveArr5 = octaves7.generateNoiseOctaves(octaveArr5, xPos, zPos, sizeX, sizeZ, 200.0, 200.0, 0.5);
		octaveArr1 = octaves3.generateNoiseOctaves(octaveArr1, xPos, 0, zPos, sizeX, sizeY, sizeZ, noiseFactor / 80.0, noiseFactor / 160.0, noiseFactor / 80.0);
		octaveArr2 = octaves1.generateNoiseOctaves(octaveArr2, xPos, 0, zPos, sizeX, sizeY, sizeZ, noiseFactor, noiseFactor, noiseFactor);
		octaveArr3 = octaves2.generateNoiseOctaves(octaveArr3, xPos, 0, zPos, sizeX, sizeY, sizeZ, noiseFactor, noiseFactor, noiseFactor);
		int incrementer1 = 0;
		int incrementer2 = 0;
		int var16 = 16 / sizeX;
		for (int i = 0; i < sizeX; ++i)
		{
			int var18 = i * var16 + var16 / 2;
			for (int j = 0; j < sizeZ; ++j)
			{
				double var29;
				int var20 = j * var16 + var16 / 2;
				double var21 = temps[var18 * 16 + var20];
				double var23 = humidities[var18 * 16 + var20] * var21;
				double var25 = 1.0 - var23;
				var25 *= var25;
				var25 *= var25;
				var25 = 1.0 - var25;
				double var27 = (octaveArr4[incrementer2] + 256.0) / 512.0;
				if ((var27 *= var25) > 1.0)
				{
					var27 = 1.0;
				}
				if ((var29 = octaveArr5[incrementer2] / 8000.0) < 0.0)
				{
					var29 = (-var29) * 0.3;
				}
				if ((var29 = var29 * 3.0 - 2.0) < 0.0)
				{
					if ((var29 /= 2.0) < -1.0)
					{
						var29 = -1.0;
					}
					var29 /= 1.4;
					var29 /= 2.0;
					var27 = 0.0;
				}
				else
				{
					if (var29 > 1.0)
					{
						var29 = 1.0;
					}
					var29 /= 8.0;
				}
				if (var27 < 0.0)
				{
					var27 = 0.0;
				}
				var27 += 0.5;
				var29 = var29 * (double) sizeY / 16.0;
				double var31 = (double) sizeY / 2.0 + var29 * 4.0;
				++incrementer2;
				for (int k = 0; k < sizeY; ++k)
				{
					double var34;
					double var36 = ((double) k - var31) * 12.0 / var27;
					if (var36 < 0.0)
					{
						var36 *= 4.0;
					}
					double var38 = octaveArr2[incrementer1] / 512.0;
					double var40 = octaveArr3[incrementer1] / 512.0;
					double var42 = (octaveArr1[incrementer1] / 10.0 + 1.0) / 2.0;
					var34 = var42 < 0.0 ? var38 : (var42 > 1.0 ? var40 : var38 + (var40 - var38) * var42);
					var34 -= var36;
					if (k > sizeY - 4)
					{
						double var44 = (float) (k - (sizeY - 4)) / 3.0f;
						var34 = var34 * (1.0 - var44) + -10.0 * var44;
					}
					values[incrementer1] = var34;
					++incrementer1;
				}
			}
		}
		return values;
	}

	@Override
	public Pair<int[][], Boolean> simulateChunkYFast(ChunkPos pos)
	{
		int[][] output = new int[4][4];
		heightNoise = generateOctaves(heightNoise, pos.x * 4, 0,pos.z * 4, 5, 17, 5);
		for (int cX = 0; cX < 4; ++cX)
		{
			for (int cZ = 0; cZ < 4; ++cZ)
			{
				for (int cY = 0; cY < 16; ++cY)
				{
					double eigth = 0.125;
					double noise1 = heightNoise[(cX * 5 + cZ) * 17 + cY];
					double eightNoise1 = (heightNoise[(cX * 5 + cZ) * 17 + cY + 1] - noise1) * eigth;
					for (int cY2 = 0; cY2 < 8; ++cY2)
					{
						double stonePosPrime = noise1;
						int y = cY * 8 + cY2;
						if (stonePosPrime > 0.0)
						{
							output[cX][cZ] = y;
						}
						noise1 += eightNoise1;
					}
				}
			}
		}
		return Pair.of(output, landValExists(output));
	}
}
