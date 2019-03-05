package com.mrburgerUS.betaplus.beta_plus.biome.support;

import com.shinoow.abyssalcraft.api.biome.ACBiomes;

public class ACSupport
{
	public static void init()
	{
		try
		{
			SupportBiome.coldBiomes.add(ACBiomes.darklands);
			SupportBiome.coldBiomes.add(ACBiomes.darklands_mountains);
			SupportBiome.wetBiomes.add(ACBiomes.coralium_infested_swamp);
		}
		catch (Exception e)
		{
			System.out.println("AbyssalCraft Biomes not loaded!");
		}
	}
}
