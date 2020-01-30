package com.mrburgerus.betaplus.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.mrburgerus.betaplus.BetaPlus.*;

public class ConfigRetroPlus
{
	private static final String PREFIX = MOD_NAME + ".config.";


	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig(BUILDER);
	public static final WorldConfig WORLD_CONFIG = new WorldConfig(BUILDER);
	public static final BiomeConfig BIOME_CONFIG = new BiomeConfig(BUILDER);
	public static final ForgeConfigSpec SPEC = BUILDER.build();

	// Default values
	private static final int smoothDefault = 7;


	private static Map<String, Biome> biomeNames = Maps.newHashMap();

	public static int seaLevel;
	public static int seaDepth;
	public static int smoothSize;
	public static double biomeScale;
	public static double humidityScale;
	public static double oceanYScale;
	public static double noiseScale;
	public static double coldTh;


	public static List<Biome> islandBiomeList;
	public static List<Biome> frozenBiomesList;
	public static List<Biome> frozenHillBiomesList;

	// Finalizes (bakes) config options
	public static void bake()
	{
		LOGGER.debug("Loading Beta+ CONFIG");

		seaLevel = WORLD_CONFIG.seaLevel.get();
		// Force odd!
		if (WORLD_CONFIG.oceanSmoothSize.get() % 2 != 0)
			smoothSize = WORLD_CONFIG.oceanSmoothSize.get();
		else
			smoothSize = smoothDefault;

		biomeScale = WORLD_CONFIG.biomeScale.get();
		humidityScale = WORLD_CONFIG.humidityScale.get();
		oceanYScale = WORLD_CONFIG.oceanDepthScale.get();
		noiseScale = WORLD_CONFIG.noiseFactor.get();
		coldTh = WORLD_CONFIG.coldTh.get();
		seaDepth = WORLD_CONFIG.depthTh.get();

		// Biome Lists
		islandBiomeList = getBiomeList(BIOME_CONFIG.islandBiomeList.get());
		frozenBiomesList = getBiomeList(BIOME_CONFIG.frozenBiomeList.get());
		frozenHillBiomesList = getBiomeList(BIOME_CONFIG.frozenHillBiomeList.get());

	}

	// Converts a String List into a Biome List.
	private static List<Biome> getBiomeList(List<String> strings)
	{
		List<Biome> retList = Lists.newArrayList();
		for (String str : strings)
		{
			retList.add(getBiomeFromString(str));
		}
		return retList;
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
			modEnabled = builder.comment("Enables Beta+ (default: TRUE *DOES NOTHING*)")
					.translation("enable" + PREFIX)
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
			biomeScale = builder.comment("Defines biome size in Beta+, larger values increase biome size" +
					"\n(For faithful Beta 1.7.3 biome size: 39.999999404)")
					.translation("biomescale" + PREFIX)
					.defineInRange("biomeScale", 39.999999404, 1.0, 256.0);
			humidityScale = builder.comment("Defines the scale of humidity noise in relation to other factors" +
					"\n(For faithful Beta 1.7.3 biome size: 2)")
					.translation("humidityscale" + PREFIX)
					.define("humidityScale", 2.0);
			oceanSmoothSize = builder.comment("An *ODD* Integer that determines the size of smoothing for deepened oceans, larger values increase process time" +
					"\nMUST BE ODD!!! (default: 7)")
					.translation("smoothsize" + PREFIX)
					.defineInRange("smoothingSize", smoothDefault, 1, 11);
			oceanDepthScale = builder.comment("Scale value for increasing ocean depth (default: 3.25)")
					.translation("oceanDepth" + PREFIX)
					.defineInRange("oceanDepthScale", 3.25, 1.0, 4.0);
			seaLevel = builder.comment("The \"Sea Level\" of the world, changes may cause issues! (default: 63)")
					.translation("sealevel" + PREFIX)
					.defineInRange("seaLevel", 63, 1, 128);
			noiseFactor = builder.comment("Noise scaling factor in range [600, 800) (default: 684.412)")
					.translation("noisefactor" + PREFIX)
					.defineInRange("noiseFactor", 684.412, 600.0, 800.0);
			coldTh = builder.comment("Threshold for \"cold\" biomes to spawn in range [0,1) (default: 0.5)")
					.translation("coldTh" + PREFIX)
					.defineInRange("coldTh", 0.5, 0.0, 1.0);
			depthTh = builder.comment("Minimum depth for Oceans to be considered Deep Oceans (default: 20)")
					.translation("depthTh" + PREFIX)
					.defineInRange("depthTh", 20, 12, 32);

			//Finalize
			builder.pop();
		}
	}

	public static class BiomeConfig
	{
		private static List<String> biomes = new ArrayList<>();

		// Lists
		// TODO:
		// Possibly, a list of Biomes with the temperature and humidity values provided could be used?
		// And then, find the closest match(es) and select one?
		final ForgeConfigSpec.ConfigValue<List<String>> islandBiomeList;
		final ForgeConfigSpec.ConfigValue<List<String>> frozenBiomeList;
		final ForgeConfigSpec.ConfigValue<List<String>> frozenHillBiomeList;
		//final ForgeConfigSpec.ConfigValue<List<String>> coldBiomeList;

		public BiomeConfig(ForgeConfigSpec.Builder builder)
		{
			builder.push("Biome List Mappings");

			islandBiomeList = builder.comment("List of Possible Island Biomes")
					.translation(PREFIX + "islandList")
					.define("islandList", defaultIslandBiomes);
			frozenBiomeList = builder.comment("List of Possible Normal Frozen Biomes")
					.translation(PREFIX + "frozenList")
					.define("frozenList", defaultFrozenBiomes);
			frozenHillBiomeList = builder.comment("List of Possible Hilly Frozen Biomes")
					.translation(PREFIX + "frozenHillList")
					.define("frozenHillList", defaultFrozenHillBiomes);

			builder.pop();
		}

		// Default Values (REMEMBER BIOMES O PLENTY IF ENABLED)
		private static List<String> defaultIslandBiomes = Lists.newArrayList();
		private static List<String> defaultFrozenBiomes = Lists.newArrayList();
		private static List<String> defaultFrozenHillBiomes = Lists.newArrayList();

		static
		{
			final Biome[] rawBiomes = ForgeRegistries.BIOMES.getValues().toArray(new Biome[0]);
			for (Biome biome : rawBiomes)
			{
				biomes.add(biome.getTranslationKey());
			}
		}
	}

	static
	{
		final Biome[] rawBiomes = ForgeRegistries.BIOMES.getValues().toArray(new Biome[0]);
		for (Biome b : rawBiomes)
		{
			biomeNames.put(b.getTranslationKey(), b);
		}
	}
}
