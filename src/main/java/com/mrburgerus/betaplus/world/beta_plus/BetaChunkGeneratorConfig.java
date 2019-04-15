package com.mrburgerus.betaplus.world.beta_plus;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class BetaChunkGeneratorConfig extends OverworldChunkGeneratorConfig
{
	private boolean isBuffet;
	private double scale = 0.015;
	private double multiplierBiome = 1.75;
	private int seaLevel = 64;


	public void initBuffet(boolean b)
	{
		isBuffet = b;
	}

	public double getScale()
	{
		return scale;
	}

	public double getMultiplierBiome()
	{
		return multiplierBiome;
	}

	public Integer getSeaLevel()
	{
		return seaLevel;
	}

	public BetaChunkGeneratorConfig(){
		super();

		//stronghold - distance
		strongholdDistance = 48;
		//stronghold - count
		strongholdCount = 196;
		//stronghold - spread
		strongholdSpread = 5;

	}

	public BetaChunkGeneratorConfig(CompoundTag tags){
		super();
		//stronghold - distance
		strongholdDistance = 48;
		//stronghold - count
		strongholdCount = 196;
		//stronghold - spread
		strongholdSpread = 5;

	}
}
