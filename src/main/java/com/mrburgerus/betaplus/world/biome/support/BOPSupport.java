package com.mrburgerus.betaplus.world.biome.support;

import biomesoplenty.api.biome.BOPBiomes;
import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.world.biome.Biomes;

import java.util.Optional;

public class BOPSupport
{
	public static void init()
	{
		try
		{
			addBOP();
		}
		catch (Exception e)
		{
			BetaPlus.LOGGER.error("Beta+ BOP Support Broken!");
		}
	}

	private static void addBOP()
	{
		// Initialize each biome
		// NOTES: IF A BIOME IS MODERATELY HILLY, IT CAN BE DECLARED AS BOTH THE FLAT AND HILLY VARIANT!

		// Alps
		BOPBiomes.alps.ifPresent(biome -> Support.mountainBiomes.add(biome));
		// Skip Alps Foothills
		// Bayou
		BOPBiomes.bayou.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Bog
		BOPBiomes.bog.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Boreal Forest
		BOPBiomes.boreal_forest.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Brushland & Chapparal (They're similar enough, right?)
		if (BOPBiomes.brushland.isPresent() && BOPBiomes.chaparral.isPresent())
		{
			Support.landBiomes.add(Pair.of(BOPBiomes.brushland.get(), Optional.of(BOPBiomes.chaparral.get())));
		}
		// If both are not loaded, load one or other
		else
		{
			BOPBiomes.brushland.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
			BOPBiomes.chaparral.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		}
		// Cherry Blossom
		BOPBiomes.cherry_blossom_grove.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Cold Desert
		BOPBiomes.cold_desert.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.of(biome))));
		// Coniferous Forest (Double Up)
		BOPBiomes.coniferous_forest.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.of(biome))));
		// Dead Forest
		BOPBiomes.dead_forest.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.of(biome))));
		// Skip Fir clearing
		// Flood Plain
		BOPBiomes.floodplain.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Flower Meadow
		BOPBiomes.flower_meadow.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Grassland
		BOPBiomes.grassland.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Highland
		BOPBiomes.highland.ifPresent(biome -> Support.mountainBiomes.add(biome));
		// Highland Moor
		BOPBiomes.highland_moor.ifPresent(biome -> Support.mountainBiomes.add(biome));
		// Lavender Field
		BOPBiomes.lavender_field.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Lush Grassland
		BOPBiomes.lush_grassland.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Lush Swamp
		BOPBiomes.lush_swamp.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Mangrove (WATCH OUT, THIS REQUIRES LOW LAND)
		BOPBiomes.mangrove.ifPresent(biome -> Support.coastBiomes.add(biome));
		// Maple Woods
		BOPBiomes.maple_woods.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.of(biome))));
		// Marsh
		BOPBiomes.marsh.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Meadow
		BOPBiomes.meadow.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Mire
		BOPBiomes.mire.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Mystic Grove
		BOPBiomes.mystic_grove.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.of(biome))));
		// Oasis TODO
		// Ominous Woods
		BOPBiomes.ominous_woods.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.of(biome))));
		// Orchard
		BOPBiomes.orchard.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Origin Island
		BOPBiomes.origin_hills.ifPresent(biome -> Support.islandBiomes.add(biome));
		// Outback (ADDED BADLANDS AS HILL VARIANT)
		BOPBiomes.outback.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.of(Biomes.BADLANDS_PLATEAU))));
		// Overgrown Cliffs
		BOPBiomes.overgrown_cliffs.ifPresent(biome -> Support.mountainBiomes.add(biome));
		// Pasture
		BOPBiomes.pasture.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Prarie
		BOPBiomes.prairie.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Pumpkin Path
		BOPBiomes.pumpkin_patch.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Rainforest
		BOPBiomes.rainforest.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Redwood Forest
		BOPBiomes.redwood_forest.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.of(biome))));
		// Scrubland
		BOPBiomes.scrubland.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Seasonal Forest (DOUBLED UP)
		BOPBiomes.seasonal_forest.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.of(biome))));
		// Shield
		BOPBiomes.shield.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Shrubland
		BOPBiomes.shrubland.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Silk Glade
		BOPBiomes.silkglade.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Skip Snowy Coniferous Biomes for now
		// Snowy Forest
		BOPBiomes.snowy_forest.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.of(biome))));
		// Steppe(r)
		BOPBiomes.steppe.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Temperate Rainforest & Hills
		if (BOPBiomes.temperate_rainforest.isPresent() && BOPBiomes.temperate_rainforest_hills.isPresent())
		{
			Support.landBiomes.add(Pair.of(BOPBiomes.temperate_rainforest.get(), Optional.of(BOPBiomes.temperate_rainforest_hills.get())));
		}
		// If both are not loaded, do not load the hills. That would be dumb...
		else
		{
			BOPBiomes.temperate_rainforest.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		}
		// Tropical Rainforest
		BOPBiomes.tropical_rainforest.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.of(biome))));
		// Tropics (possibly an island)
		BOPBiomes.tropics.ifPresent(biome -> Support.islandBiomes.add(biome));
		// Tundra
		BOPBiomes.tundra.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));
		// Volcano
		BOPBiomes.volcano.ifPresent(biome -> Support.islandBiomes.add(biome));
		// Wasteland
		BOPBiomes.wasteland.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.of(biome))));
		// Wetland
		BOPBiomes.wetland.ifPresent(biome -> Support.coastBiomes.add(biome));
		// White Beach
		BOPBiomes.white_beach.ifPresent(biome -> Support.coastBiomes.add(biome));
		// Woodland
		BOPBiomes.woodland.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.of(biome))));
		// Xeric Shrubland
		BOPBiomes.xeric_shrubland.ifPresent(biome -> Support.landBiomes.add(Pair.of(biome, Optional.empty())));

		// Dead Reef
		BOPBiomes.dead_reef.ifPresent(biome -> Support.oceanBiomes.add(Pair.of(biome, biome)));
	}
}
