package com.mrburgerus.betaplus.client;

import com.mrburgerus.betaplus.ServerProxy;
import com.mrburgerus.betaplus.client.color.ReedColorBetaPlus;
import com.mrburgerus.betaplus.util.ResourceHelper;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientProxy extends ServerProxy
{
	// FIELDS //
	public static final ResourceLocation GRASS_BLOCK_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_grass_block"));
	public static final ResourceLocation GRASS_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_grass"));
	public static final ResourceLocation OAK_LEAVES_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_oak_leaves"));
	public static final ResourceLocation SPRUCE_LEAVES_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_spruce_leaves"));


	public ClientProxy()
	{}

	@Override
	public void init()
	{
		// Register Client
		MinecraftForge.EVENT_BUS.register(this);
		// Added
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerColors);

		// Load Sugarcane color
	}

	@SubscribeEvent
	public void registerColors(final FMLLoadCompleteEvent event)
	{
		Minecraft.getInstance().getBlockColors().register(new ReedColorBetaPlus(), Blocks.SUGAR_CANE);
	}


}
