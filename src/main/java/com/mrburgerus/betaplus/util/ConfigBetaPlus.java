package com.mrburgerus.betaplus.util;

import com.google.common.collect.Maps;
import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigBetaPlus
{
	private static final String transSuffix = "." + BetaPlus.MOD_NAME + ".config";

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig(BUILDER);
	public static final WorldConfig WORLD_CONFIG = new WorldConfig(BUILDER);
	public static final BiomeConfig BIOME_CONFIG = new BiomeConfig(BUILDER);
	public static final ForgeConfigSpec SPEC = BUILDER.build();

	private static Map<String, Biome> biomeNames = Maps.newHashMap();

	public static int seaLevel;
	public static int seaDepth;
	public static int smoothSize;
	public static double biomeScale;
	public static double humidityScale;
	public static double oceanYScale;
	public static double noiseScale;
	public static double coldTh;


	public static Biome rainforest;
	public static Biome swampland;
	public static Biome seasonalForest;
	public static Biome forest;
	public static Biome savanna;
	public static Biome shrubland;
	public static Biome taiga;

	// Finalizes (bakes) config options
	public static void bake()
	{
		BetaPlus.LOGGER.debug("Loading Beta+ CONFIG");

		seaLevel = WORLD_CONFIG.seaLevel.get();
		// Force odd!
		if (WORLD_CONFIG.oceanSmoothSize.get() % 2 != 0)
			smoothSize = WORLD_CONFIG.oceanSmoothSize.get();
		else
			smoothSize = 7;

		biomeScale = WORLD_CONFIG.biomeScale.get();
		humidityScale = WORLD_CONFIG.humidityScale.get();
		oceanYScale = WORLD_CONFIG.oceanDepthScale.get();
		noiseScale = WORLD_CONFIG.noiseFactor.get();
		coldTh = WORLD_CONFIG.coldTh.get();
		seaDepth = WORLD_CONFIG.depthTh.get();

		// Biome Mappings
		rainforest = getBiomeFromString(BIOME_CONFIG.rainforest.get());
		swampland = getBiomeFromString(BIOME_CONFIG.swampland.get());
		seasonalForest = getBiomeFromString(BIOME_CONFIG.seasonalForest.get());
		forest = getBiomeFromString(BIOME_CONFIG.forest.get());
		savanna = getBiomeFromString(BIOME_CONFIG.savanna.get());
		shrubland = getBiomeFromString(BIOME_CONFIG.shrubland.get());
		taiga = getBiomeFromString(BIOME_CONFIG.taiga.get());
	}

	private static Biome getBiomeFromString(String name)
	{
		return biomeNames.getOrDefault(name, Biomes.DEFAULT);
	}

	public static class GeneralConfig
	{
		public final ForgeConfigSpec.ConfigValue<Boolean> modEnabled;


		public GeneralConfig(ForgeConfigSpec.Builder builder)
		{
			builder.push("General Options");
			// Mod enable
			modEnabled = builder.comment("Enables Beta+ (default: TRUE)")
					.translation("enable" + transSuffix)
					.define("enableMod", true);

			//Finalize
			builder.pop();
		}
	}

	public static class WorldConfig
	{
		public final ForgeConfigSpec.ConfigValue<Double> biomeScale;
		public final ForgeConfigSpec.ConfigValue<Double> humidityScale;
		public final ForgeConfigSpec.ConfigValue<Integer> oceanSmoothSize;
		public final ForgeConfigSpec.ConfigValue<Double> oceanDepthScale;
		public final ForgeConfigSpec.ConfigValue<Integer> seaLevel;
		public final ForgeConfigSpec.ConfigValue<Double> noiseFactor;
		public final ForgeConfigSpec.ConfigValue<Double> coldTh;
		public final ForgeConfigSpec.ConfigValue<Integer> depthTh;

		public WorldConfig(ForgeConfigSpec.Builder builder)
		{
			builder.push("World Options");
			//All options
			biomeScale = builder.comment("Defines biome size in Beta+, larger values increase biome size (default: 75)" +
					"\n(For faithful Beta 1.7.3 biome size: 39.999999404)")
					.translation("biomescale" + transSuffix)
					.defineInRange("biomeScale", 75.0, 1.0, 256.0);
			humidityScale = builder.comment("Defines the scale of humidity noise in relation to other factors (default: 1.75)" +
					"\n(For faithful Beta 1.7.3 biome size: 2)")
					.translation("humidityscale" + transSuffix)
					.define("humidityScale", 1.75);
			oceanSmoothSize = builder.comment("An *ODD* Integer that determines the size of smoothing for deepened oceans, larger values increase process time" +
					"\nMUST BE ODD!!! (default: 7)")
					.translation("smoothsize" + transSuffix)
					.defineInRange("smoothingSize", 7, 1, 11);
			oceanDepthScale = builder.comment("Scale value for increasing ocean depth (default: 3.25)")
					.translation("oceanDepth" + transSuffix)
					.defineInRange("oceanDepthScale", 3.25, 1.0, 4.0);
			seaLevel = builder.comment("The \"Sea Level\" of the world (default: 64)")
					.translation("sealevel" + transSuffix)
					.defineInRange("seaLevel", 64, 1, 128);
			noiseFactor = builder.comment("Noise scaling factor in range [600, 800) (default: 684.412)")
					.translation("noisefactor" + transSuffix)
					.defineInRange("noiseFactor", 684.412, 600.0, 800.0);
			coldTh = builder.comment("Threshold for \"cold\" biomes to spawn in range [0,1) (default: 0.5)")
					.translation("coldTh" + transSuffix)
					.defineInRange("coldTh", 0.5, 0.0, 1.0);
			depthTh = builder.comment("Minimum depth for Oceans to be considered Deep Oceans (default: 20)")
					.translation("depthTh" + transSuffix)
					.defineInRange("depthTh", 20, 12, 32);

			//Finalize
			builder.pop();
		}
	}

	public static class BiomeConfig
	{
		private static List<String> biomes = new ArrayList<>();

		public final ForgeConfigSpec.ConfigValue<String> rainforest;
		public final ForgeConfigSpec.ConfigValue<String> swampland;
		public final ForgeConfigSpec.ConfigValue<String> seasonalForest;
		public final ForgeConfigSpec.ConfigValue<String> forest;
		public final ForgeConfigSpec.ConfigValue<String> savanna;
		public final ForgeConfigSpec.ConfigValue<String> shrubland;
		public final ForgeConfigSpec.ConfigValue<String> taiga;

		public BiomeConfig(ForgeConfigSpec.Builder builder)
		{
			builder.push("Biome Mappings");

			rainforest = builder.comment("Beta Rainforest")
					.translation("rainforest" + transSuffix)
					.defineInList("rainforest", Biomes.JUNGLE.getDisplayName().getString(), biomes);
			swampland = builder.comment("Beta Swampland")
					.translation("swampland" + transSuffix)
					.defineInList("swampland", Biomes.SWAMP.getDisplayName().getString(), biomes);
			seasonalForest = builder.comment("Beta Seasonal Forest")
					.translation("seasonalForest" + transSuffix)
					.defineInList("seasonalForest", Biomes.FLOWER_FOREST.getDisplayName().getString(), biomes);
			forest = builder.comment("Beta Forest")
					.translation("forest" + transSuffix)
					.defineInList("forest", Biomes.FOREST.getDisplayName().getString(), biomes);
			savanna = builder.comment("Beta Savanna")
					.translation("savanna" + transSuffix)
					.defineInList("savanna", Biomes.SAVANNA.getDisplayName().getString(), biomes);
			shrubland = builder.comment("Beta Shrubland")
					.translation("shrubland" + transSuffix)
					.defineInList("shrubland", Biomes.SAVANNA.getDisplayName().getString(), biomes);
			taiga = builder.comment("Beta Taiga (Snowy)")
					.translation("taiga" + transSuffix)
					.defineInList("taiga", Biomes.SNOWY_TAIGA.getDisplayName().getString(), biomes);

			builder.pop();
		}

		static
		{
			final Biome[] rawBiomes = ForgeRegistries.BIOMES.getValues().toArray(new Biome[0]);
			for (Biome biome : rawBiomes)
			{
				biomes.add(biome.getDisplayName().getString());
			}
		}
	}

	static
	{
		final Biome[] rawBiomes = ForgeRegistries.BIOMES.getValues().toArray(new Biome[0]);
		for (Biome b : rawBiomes)
		{
			biomeNames.put(b.getDisplayName().getString(), b);
		}
	}
}
