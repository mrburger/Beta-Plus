package com.mrburgerus.betaplus.world.beta_plus;

import com.mrburgerus.betaplus.client.gui.CreateBetaWorldScreen;
import com.mrburgerus.betaplus.world.biome.support.BOPSupport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;

public class WorldTypeBetaPlus extends WorldType
{
	// Fields
	public static final String OLD_CAVES_TAG = "useOldCaves";
	public static final String BIOME_PROVIDER_TAG = "provider";

	public WorldTypeBetaPlus(String name)
	{
		super(name);
	}

	// Register World Type
	public static void register()
	{
		new WorldTypeBetaPlus("BETA_PLUS");
	}


	@Override
	@SuppressWarnings("deprecation")
	public ChunkGenerator<?> createChunkGenerator(World world)
	{
		ChunkGenerator<?> generator;
		BetaPlusGenSettings settings = BetaPlusGenSettings.createSettings(world.getWorldInfo().getGeneratorOptions());
		// Overworld
		// TODO allow for BOP detection
		if (world.dimension.getType() != DimensionType.OVERWORLD)
		{
			if (world.dimension.getType() == DimensionType.THE_NETHER)
			{
				if (ModList.get().isLoaded(BOPSupport.BOP_MOD_NAME))
				{
					// Use the BOP generator
					// TODO
					generator = world.dimension.createChunkGenerator();
				}
				else
				{
					generator = world.dimension.createChunkGenerator(); // PLACE
				}
			}
			// The end
			if (world.dimension.getType() == DimensionType.THE_END)
			{
				if (ModList.get().isLoaded(BOPSupport.BOP_MOD_NAME))
				{
					// Use the BOP generator
					generator = world.dimension.createChunkGenerator();
				}
				else
				{
					generator = world.dimension.createChunkGenerator(); // PLACE
				}
			}
			else
			{
				// Frigg deprecations
				generator = world.dimension.createChunkGenerator();
			}
		}
		else
		{
			generator = new ChunkGeneratorBetaPlus(world, new BiomeProviderBetaPlus(world, settings), settings);
		}

		return generator;
	}

	@Override
	public boolean hasCustomOptions()
	{
		return true;
	}

	@Override
	public boolean hasInfoNotice()
	{
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onCustomizeButton(Minecraft mc, CreateWorldScreen gui)
	{
		mc.displayGuiScreen(new CreateBetaWorldScreen(gui, gui.chunkProviderSettingsJson));
	}
}
