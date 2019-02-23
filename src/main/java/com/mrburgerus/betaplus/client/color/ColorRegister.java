package com.mrburgerus.betaplus.client.color;

import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ColorRegister
{
	public static void clientSide(final FMLLoadCompleteEvent event)
	{
		BetaPlus.LOGGER.info("Fetus Deletus");
		/* Do Not Enable the Grass Color until Kinks worked out */
		Minecraft.getInstance().getBlockColors().register(new GrassColorBetaPlus(), Blocks.GRASS_BLOCK, Blocks.GRASS, Blocks.TALL_GRASS, Blocks.FERN, Blocks.LARGE_FERN);
		Minecraft.getInstance().getBlockColors().register(new ReedColorBetaPlus(), Blocks.SUGAR_CANE);
	}
}
