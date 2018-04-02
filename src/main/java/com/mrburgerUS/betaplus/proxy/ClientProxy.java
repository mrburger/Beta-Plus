package com.mrburgerUS.betaplus.proxy;


import com.mrburgerUS.betaplus.beta.biome.color.ReedColorBeta;
import com.mrburgerUS.betaplus.beta.biome.color.WaterColorBeta;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	private static final Minecraft minecraft = Minecraft.getMinecraft();

	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		ClientProxy.generateBetaColors();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
	}


	//Methods
	public static void generateBetaColors()
	{
		BlockColors colors = minecraft.getBlockColors();
		registerColors(colors);
	}

	private static void registerColors(BlockColors colors)
	{
		//colors.registerBlockColorHandler(new GrassColorBeta(), Blocks.GRASS);
		//colors.registerBlockColorHandler(new GrassColorBeta(), Blocks.TALLGRASS);
		//colors.registerBlockColorHandler(new GrassColorBeta(), Blocks.DOUBLE_PLANT);
		colors.registerBlockColorHandler(new ReedColorBeta(), Blocks.REEDS);
		colors.registerBlockColorHandler(new WaterColorBeta(), Blocks.WATER);
		//colors.registerBlockColorHandler(new LeavesColorBeta(), Blocks.LEAVES);
		//colors.registerBlockColorHandler(new LeavesColorBeta2(), Blocks.LEAVES2);
	}
}
