package com.mrburgerus.betaplus.world.alpha_plus;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.client.gui.GuiCreateAlphaWorld;
import com.mrburgerus.betaplus.world.biome.BiomeProviderAlphaPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldTypeAlphaPlus extends WorldType
{
	private AlphaPlusGenSettings settings;

	public WorldTypeAlphaPlus(String alpha)
	{
		super(alpha);
		/* Culprit? */
		BetaPlus.LOGGER.info("Created Alpha world type");
		settings = new AlphaPlusGenSettings();
	}

	// Register World Type
	public static void register()
	{
		new WorldTypeAlphaPlus("ALPHA");
	}

	@Override
	public IChunkGenerator<?> createChunkGenerator(World world)
	{
		return new ChunkGeneratorAlphaPlus(world, new BiomeProviderAlphaPlus(settings.getSnowy()), settings);
	}

	/* Since we have snow as an option, It has custom Options */
	/* Disabled until I figure out how to save the snowy variable into the world */
	@Override
	public boolean hasCustomOptions()
	{
		return false;
	}

	@Override
	public boolean hasInfoNotice()
	{
		return true;
	}

	@Override
	public void onCustomizeButton(Minecraft mc, GuiCreateWorld gui)
	{
		mc.displayGuiScreen(new GuiCreateAlphaWorld(gui, settings));
	}

}
