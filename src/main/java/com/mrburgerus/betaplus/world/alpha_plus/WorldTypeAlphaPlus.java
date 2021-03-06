package com.mrburgerus.betaplus.world.alpha_plus;

import com.mrburgerus.betaplus.client.gui.CreateAlphaWorldScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


/* Line 99, or where the Buffet World Type is assigned is the best location to get info on Customized Worlds. */
public class WorldTypeAlphaPlus extends WorldType
{
	//Fields
	public static final String SNOW_WORLD_TAG = "snowWorld";

	public WorldTypeAlphaPlus(String alpha)
	{
		super(alpha);
		/* Culprit? */
		/* Called EVERY time game launches, thats bad */
	}

	// Register World Type
	public static void register()
	{
		new WorldTypeAlphaPlus("ALPHA");
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator(World world)
	{
		AlphaPlusGenSettings settings = AlphaPlusGenSettings.createSettings(world.getWorldInfo().getGeneratorOptions());
		if (world.dimension.getType() != DimensionType.OVERWORLD)
		{
			return world.dimension.createChunkGenerator();
		}
		return new ChunkGeneratorAlphaPlus(world, new BiomeProviderAlphaPlus(world), settings);
	}

	/* Since we have snow as an option, It has custom Options */
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
		mc.displayGuiScreen(new CreateAlphaWorldScreen(gui, gui.chunkProviderSettingsJson));
	}
}
