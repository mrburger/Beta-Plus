package com.mrburgerUS.betaplus.proxy;


import com.mrburgerUS.betaplus.beta.biome.color.GrassColorBeta;
import com.mrburgerUS.betaplus.beta.biome.color.ReedColorBeta;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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


	//Methods
	public static void generateBetaColors()
	{
		BlockColors colors = minecraft.getBlockColors();
		registerColors(colors);
	}

	private static void registerColors(BlockColors c)
	{
		c.registerBlockColorHandler(new GrassColorBeta(), Blocks.GRASS);
		c.registerBlockColorHandler(new GrassColorBeta(), Blocks.TALLGRASS);
		c.registerBlockColorHandler(new GrassColorBeta(), Blocks.DOUBLE_PLANT);
		c.registerBlockColorHandler(new ReedColorBeta(), Blocks.REEDS);
		//c.registerBlockColorHandler(new LeavesColorBeta(), Blocks.LEAVES);
		//c.registerBlockColorHandler(new LeavesColorBeta(), Blocks.LEAVES2);
	}
}
