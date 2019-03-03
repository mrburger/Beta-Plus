package com.mrburgerus.betaplus.world.alpha_plus;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.client.gui.GuiCreateAlphaWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


/* Line 99, or where the Buffet World Type is assigned is the bbest location to get info on Customized Worlds. */
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
	public IChunkGenerator<?> createChunkGenerator(World world)
	{
		AlphaPlusGenSettings settings = AlphaPlusGenSettings.createSettings(world.getWorldInfo().getGeneratorOptions());
		if (world.dimension.getType() != DimensionType.OVERWORLD)
		{
			return world.dimension.createChunkGenerator();
		}
		return new ChunkGeneratorAlphaPlus(world, new BiomeProviderAlphaPlus(world), settings);
	}

	/* Since we have snow as an option, It has custom Options */
	/* Disabled until I figure out how to save the snowy variable into the world */
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
	public void onCustomizeButton(Minecraft mc, GuiCreateWorld gui)
	{
		mc.displayGuiScreen(new GuiCreateAlphaWorld(gui, gui.chunkProviderSettingsJson));
	}

}
