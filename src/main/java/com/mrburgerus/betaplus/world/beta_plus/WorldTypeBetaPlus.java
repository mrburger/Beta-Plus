package com.mrburgerus.betaplus.world.beta_plus;

import com.mrburgerus.betaplus.client.gui.CreateAlphaWorldScreen;
import com.mrburgerus.betaplus.client.gui.CreateBetaWorldScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
		BetaPlusGenSettings settings = BetaPlusGenSettings.createSettings(world.getWorldInfo().getGeneratorOptions());
		// Overworld
		if (world.dimension.getType() != DimensionType.OVERWORLD)
		{
			return world.dimension.createChunkGenerator();
		}
		return new ChunkGeneratorBetaPlus(world, new BiomeProviderBetaPlus(world, settings), settings);
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
