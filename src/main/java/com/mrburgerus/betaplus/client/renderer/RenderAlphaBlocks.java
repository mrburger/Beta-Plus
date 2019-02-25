package com.mrburgerus.betaplus.client.renderer;

import com.google.common.collect.Lists;
import com.mrburgerus.betaplus.util.ResourceHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.ModelBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/* UNUSED */
@Mod.EventBusSubscriber(Dist.CLIENT)
public class RenderAlphaBlocks
{
	@SubscribeEvent
	public static void register(final RegistryEvent.Register<Block> event)
	{

	}

	/*
	public static ModelBlock buildAlphaGrassBlock()
	{
		ResourceLocation newGrass = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("alpha_grass_block"));
		List<BlockPart> partList = Lists.newArrayList();


		ModelBlock blockReturn = new ModelBlock(newGrass, partList,)
	}
	*/
}
