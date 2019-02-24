package com.mrburgerus.betaplus.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/* UNUSED */
@Mod.EventBusSubscriber(Dist.CLIENT)
public class RenderAlphaBlocks
{
	@SubscribeEvent
	public static void register(final RegistryEvent.Register<Block> event)
	{

	}
}
