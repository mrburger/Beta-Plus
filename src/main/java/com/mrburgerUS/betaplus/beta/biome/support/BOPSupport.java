package com.mrburgerUS.betaplus.beta.biome.support;

import biomesoplenty.api.biome.BOPBiomes;

public class BOPSupport
{
	public static void init()
	{
		try
		{
			SupportBiome.snowBiomes.add(BOPBiomes.alps.get());
			SupportBiome.snowBiomes.add(BOPBiomes.snowy_coniferous_forest.get());
			SupportBiome.snowBiomes.add(BOPBiomes.snowy_forest.get());

			SupportBiome.coldBiomes.add(BOPBiomes.bog.get());
			SupportBiome.coldBiomes.add(BOPBiomes.boreal_forest.get());
			SupportBiome.coldBiomes.add(BOPBiomes.cherry_blossom_grove.get());
			SupportBiome.coldBiomes.add(BOPBiomes.coniferous_forest.get());
			SupportBiome.coldBiomes.add(BOPBiomes.crag.get());
			SupportBiome.coldBiomes.add(BOPBiomes.dead_forest.get());
			SupportBiome.coldBiomes.add(BOPBiomes.dead_swamp.get());
			SupportBiome.coldBiomes.add(BOPBiomes.fen.get());
			SupportBiome.coldBiomes.add(BOPBiomes.flower_field.get());
			SupportBiome.coldBiomes.add(BOPBiomes.grassland.get());
			SupportBiome.coldBiomes.add(BOPBiomes.grove.get());
			SupportBiome.coldBiomes.add(BOPBiomes.highland.get());
			SupportBiome.coldBiomes.add(BOPBiomes.overgrown_cliffs.get());
			SupportBiome.coldBiomes.add(BOPBiomes.maple_woods.get());
			SupportBiome.coldBiomes.add(BOPBiomes.meadow.get());
			SupportBiome.coldBiomes.add(BOPBiomes.moor.get());
			SupportBiome.coldBiomes.add(BOPBiomes.mountain.get());
			SupportBiome.coldBiomes.add(BOPBiomes.redwood_forest.get());
			SupportBiome.coldBiomes.add(BOPBiomes.shield.get());
			SupportBiome.coldBiomes.add(BOPBiomes.tundra.get());
			SupportBiome.coldBiomes.add(BOPBiomes.woodland.get());

			SupportBiome.hotBiomes.add(BOPBiomes.brushland.get());
			SupportBiome.hotBiomes.add(BOPBiomes.chaparral.get());
			SupportBiome.hotBiomes.add(BOPBiomes.steppe.get());
			SupportBiome.hotBiomes.add(BOPBiomes.seasonal_forest.get());
			SupportBiome.hotBiomes.add(BOPBiomes.shrubland.get());
			SupportBiome.hotBiomes.add(BOPBiomes.outback.get());
			SupportBiome.hotBiomes.add(BOPBiomes.prairie.get());
			SupportBiome.hotBiomes.add(BOPBiomes.lavender_fields.get());
			SupportBiome.hotBiomes.add(BOPBiomes.lush_desert.get());

			SupportBiome.wetBiomes.add(BOPBiomes.bamboo_forest.get());
			SupportBiome.wetBiomes.add(BOPBiomes.bayou.get());
			SupportBiome.wetBiomes.add(BOPBiomes.lush_swamp.get());
			SupportBiome.wetBiomes.add(BOPBiomes.marsh.get());
			SupportBiome.wetBiomes.add(BOPBiomes.mystic_grove.get());
			SupportBiome.wetBiomes.add(BOPBiomes.rainforest.get());
			SupportBiome.wetBiomes.add(BOPBiomes.sacred_springs.get());
			SupportBiome.wetBiomes.add(BOPBiomes.temperate_rainforest.get());
			SupportBiome.wetBiomes.add(BOPBiomes.tropical_rainforest.get());
			SupportBiome.wetBiomes.add(BOPBiomes.wetland.get());
		}
		catch (Exception e)
		{
			System.out.println("BOP not loaded!");
		}
	}

}
