package com.mrburgerus.betaplus.client.gui;

import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class OverlayRetroVersion extends AbstractGui
{
	OverlayRetroVersion()
	{

	}

	private static String mcName = "Minecraft ";
	private static String betaVersion = "Beta+ ";
	private static String alphaVersion = "Alpha+ ";
	private static String mcVersion = "1.14.4"; //Minecraft.getInstance().getVersion();

	/* Overlays Version if we use a Beta World Type */
	@SubscribeEvent
	public static void overlayEvent(RenderGameOverlayEvent.Post event)
	{
		Minecraft mc = Minecraft.getInstance();
		WorldType wType = mc.getIntegratedServer().getWorld(DimensionType.OVERWORLD).getWorldType();
		// Check if we are in Beta+ World
		/* Don't Call Client-side, returns null */
		if(wType instanceof WorldTypeBetaPlus)
		{
			if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE)
			{
				return;
			}
			mc.fontRenderer.drawStringWithShadow(mcName + betaVersion + mcVersion, 1.5f, 1.5f, 0xFFFFFF);
		}
		else if (wType instanceof WorldTypeAlphaPlus)
		{
			if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE)
			{
				return;
			}
			mc.fontRenderer.drawStringWithShadow(mcName + alphaVersion + mcVersion, 1.5f, 1.5f, 0xFFFFFF);
		}
	}
}
