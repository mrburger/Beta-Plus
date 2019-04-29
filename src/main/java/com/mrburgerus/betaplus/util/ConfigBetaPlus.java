package com.mrburgerus.betaplus.util;

import com.mrburgerus.betaplus.BetaPlus;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigBetaPlus
{
	private static final String transSuffix = "." + BetaPlus.MOD_NAME + ".config";

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig(BUILDER);
	public static final WorldConfig WORLD_CONFIG = new WorldConfig(BUILDER);
	public static final ForgeConfigSpec SPEC = BUILDER.build();

	public static int seaLevel;
	public static int smoothSize;
	public static double biomeScale;
	public static double humidityScale;
	public static double oceanYScale;
	public static double noiseScale;

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
	}



	public static class GeneralConfig
	{
		public final ForgeConfigSpec.ConfigValue<Boolean> modEnabled;


		public GeneralConfig(ForgeConfigSpec.Builder builder)
		{
			builder.push("General");
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
			oceanDepthScale = builder.comment("Scale value for increasing ocean depth (default: 3.25")
					.translation("oceanDepth" + transSuffix)
					.defineInRange("oceanDepthScale", 3.25, 1.0, 4.0);
			seaLevel = builder.comment("The \"Sea Level\" of the world (default: 64)")
					.translation("sealevel" + transSuffix)
					.defineInRange("seaLevel", 64, 1, 128);
			noiseFactor = builder.comment("Noise scaling factor (default: 684.412")
					.translation("noisefactor" + transSuffix)
					.defineInRange("noiseFactor", 684.412, 600.0, 800.0);

			//Finalize
			builder.pop();
		}
	}
}
