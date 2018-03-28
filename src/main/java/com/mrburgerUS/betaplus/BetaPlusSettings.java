package com.mrburgerUS.betaplus;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.*;
import net.minecraft.util.JsonUtils;

import java.lang.reflect.Type;

//Based Heavily on ChunkGeneratorSettings.Class
public class BetaPlusSettings
{
	// Whether to generate dungeons
	public final boolean useDungeons;
	//Chance for Dungeons
	public final int dungeonChance;
	// Whether to generate mineshafts
	public final boolean useMineShafts;
	// Whether to generate Strongholds
	public final boolean useStrongholds;
	// Whether to generate Pyramids (Scattered Features)
	public final boolean useTemples;
	//Max Jungle Temple, Desert Pyramid, and other structures distances.
	public final int maxDistanceBetweenPyramids;

	//World Features
	// Whether to generate ravines
	public final boolean useRavines;
	//Water Lake Generation?
	public final boolean useWaterLakes;
	//Water Lake Chances
	public final int waterLakeChance;
	//Lava Lake Generation?
	public final boolean useLavaLakes;
	//Lava Lake cahnces
	public final int lavaLakeChance;
	// Minimum depth to be considered "Deep Ocean"
	public final int seaDepth;


	private BetaPlusSettings(BetaPlusSettings.Factory settingsFactory)
	{
		useDungeons = settingsFactory.useDungeons;
		dungeonChance = settingsFactory.dungeonChance;
		useMineShafts = settingsFactory.useMineShafts;
		useStrongholds = settingsFactory.useStrongholds;
		useTemples = settingsFactory.useTemples;
		maxDistanceBetweenPyramids = settingsFactory.maxDistanceBetweenPyramids;
		useRavines = settingsFactory.useRavines;
		useWaterLakes = settingsFactory.useWaterLakes;
		waterLakeChance = settingsFactory.waterLakeChance;
		useLavaLakes = settingsFactory.useLavaLakes;
		lavaLakeChance = settingsFactory.lavaLakeChance;
		seaDepth = settingsFactory.seaDepth;
	}

	public static class Factory
	{
		@VisibleForTesting
		static final Gson JSON_ADAPTER = (new GsonBuilder()).registerTypeAdapter(BetaPlusSettings.Factory.class, new BetaPlusSettings.SerializerBeta()).create();
		public boolean useDungeons = true;
		public int dungeonChance = 8;
		public boolean useMineShafts = true;
		public boolean useStrongholds = true;
		public boolean useTemples = true;
		public int maxDistanceBetweenPyramids = 20;
		public boolean useRavines = true;
		public boolean useWaterLakes = true;
		public int waterLakeChance = 20;
		public boolean useLavaLakes = true;
		public int lavaLakeChance = 256;
		public int seaDepth = 5;

		public Factory()
		{
			setDefaults();
		}

		public void setDefaults()
		{
			useDungeons = true;
			dungeonChance = 8;
			useMineShafts = true;
			useStrongholds = true;
			useTemples = true;
			maxDistanceBetweenPyramids = 24;
			useRavines = true;
			useWaterLakes = true;
			waterLakeChance = 20;
			useLavaLakes = true;
			lavaLakeChance = 256;
			seaDepth = 5;
		}

		public static Factory jsonToSettings(String options)
		{
			if (options.isEmpty())
			{
				return new Factory();
			}
			else
			{
				try
				{
					return JsonUtils.gsonDeserialize(JSON_ADAPTER, options, BetaPlusSettings.Factory.class);
				}
				catch (Exception e)
				{
					System.out.println("EXCEPTION OPTIONS");
					return new Factory();
				}
			}
		}

		public BetaPlusSettings build()
		{
			//Testing
			return new BetaPlusSettings(new Factory());
		}
	}

	public static class SerializerBeta implements JsonDeserializer<BetaPlusSettings.Factory>, JsonSerializer<BetaPlusSettings.Factory>
	{
		public BetaPlusSettings.Factory deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			JsonObject jsonobject = jsonElement.getAsJsonObject();
			BetaPlusSettings.Factory betaPlusFactory = new BetaPlusSettings.Factory();
			try
			{
				//chunkgeneratorsettings$factory.useCaves = JsonUtils.getBoolean(jsonobject, "useCaves", chunkgeneratorsettings$factory.useCaves);
				betaPlusFactory.useDungeons = JsonUtils.getBoolean(jsonobject, "useDungeons", betaPlusFactory.useDungeons);
				betaPlusFactory.dungeonChance = JsonUtils.getInt(jsonobject, "dungeonChance", betaPlusFactory.dungeonChance);
				betaPlusFactory.useStrongholds = JsonUtils.getBoolean(jsonobject, "useStrongholds", betaPlusFactory.useStrongholds);
				//betaPlusFactory.useVillages = JsonUtils.getBoolean(jsonobject, "useVillages", betaPlusFactory.useVillages);
				betaPlusFactory.useMineShafts = JsonUtils.getBoolean(jsonobject, "useMineShafts", betaPlusFactory.useMineShafts);
				betaPlusFactory.useTemples = JsonUtils.getBoolean(jsonobject, "useTemples", betaPlusFactory.useTemples);
				//betaPlusFactory.useMonuments = JsonUtils.getBoolean(jsonobject, "useMonuments", betaPlusFactory.useMonuments);
				//betaPlusFactory.useMansions = JsonUtils.getBoolean(jsonobject, "useMansions", betaPlusFactory.useMansions);
				betaPlusFactory.useRavines = JsonUtils.getBoolean(jsonobject, "useRavines", betaPlusFactory.useRavines);
				betaPlusFactory.useWaterLakes = JsonUtils.getBoolean(jsonobject, "useWaterLakes", betaPlusFactory.useWaterLakes);
				betaPlusFactory.waterLakeChance = JsonUtils.getInt(jsonobject, "waterLakeChance", betaPlusFactory.waterLakeChance);
				betaPlusFactory.useLavaLakes = JsonUtils.getBoolean(jsonobject, "useLavaLakes", betaPlusFactory.useLavaLakes);
				betaPlusFactory.lavaLakeChance = JsonUtils.getInt(jsonobject, "lavaLakeChance", betaPlusFactory.lavaLakeChance);
			}
			catch (Exception e)
			{
				System.out.println("ERROR Parsing");
			}

			return betaPlusFactory;
		}

		public JsonElement serialize(BetaPlusSettings.Factory factory, Type type, JsonSerializationContext co)
		{
			JsonObject jsonobject = new JsonObject();

			jsonobject.addProperty("useDungeons", Boolean.valueOf(factory.useDungeons));
			jsonobject.addProperty("dungeonChance", Integer.valueOf(factory.dungeonChance));
			jsonobject.addProperty("useStrongholds", Boolean.valueOf(factory.useStrongholds));
			//jsonobject.addProperty("useVillages", Boolean.valueOf(settingsFactory.useVillages));
			jsonobject.addProperty("useMineShafts", Boolean.valueOf(factory.useMineShafts));
			jsonobject.addProperty("useTemples", Boolean.valueOf(factory.useTemples));
			//jsonobject.addProperty("useMonuments", Boolean.valueOf(settingsFactory.useMonuments));
			//jsonobject.addProperty("useMansions", Boolean.valueOf(settingsFactory.useMansions));
			jsonobject.addProperty("useRavines", Boolean.valueOf(factory.useRavines));
			jsonobject.addProperty("useWaterLakes", Boolean.valueOf(factory.useWaterLakes));
			jsonobject.addProperty("waterLakeChance", Integer.valueOf(factory.waterLakeChance));
			jsonobject.addProperty("useLavaLakes", Boolean.valueOf(factory.useLavaLakes));
			jsonobject.addProperty("lavaLakeChance", Integer.valueOf(factory.lavaLakeChance));
			return jsonobject;
		}
	}
}