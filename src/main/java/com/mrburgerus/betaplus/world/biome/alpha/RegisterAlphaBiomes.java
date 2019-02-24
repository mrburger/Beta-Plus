package com.mrburgerus.betaplus.world.biome.alpha;

import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;

public class RegisterAlphaBiomes
{

	/* Works */
	public static void register(RegistryEvent.Register<Biome> event)
	{
		/* For Biomes */
		if (event.getRegistry().getRegistrySuperType() == Biome.class)
		{
			Biome frozenOcean = new BiomeAlphaFrozenOcean().setRegistryName(BetaPlus.MOD_NAME, BiomeAlphaFrozenOcean.NAME);
			Biome alphaLand = new BiomeAlphaLand().setRegistryName(BetaPlus.MOD_NAME, BiomeAlphaLand.NAME);

			/* 0 Weight because it should never Generate naturally */
			BiomeManager.addBiome(BiomeManager.BiomeType.ICY, new BiomeManager.BiomeEntry(frozenOcean, 0));
			BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(alphaLand, 0));
			event.getRegistry().registerAll(frozenOcean, alphaLand);
		}
	}
}
