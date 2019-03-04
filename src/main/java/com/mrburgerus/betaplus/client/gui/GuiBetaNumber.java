package com.mrburgerus.betaplus.client.gui;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
public class GuiBetaNumber extends Gui
{
	public GuiBetaNumber()
	{

	}

	private static String beta = "Minecraft Beta+ ";
	private static String alpha = "Minecraft Alpha ";
	private static String version = "1.13.2"; //Minecraft.getInstance().getVersion();

	/* Overlays Version if we use a Beta World Type */
	@SubscribeEvent
	public static void overlayEvent(final RenderGameOverlayEvent.Pre event)
	{
		Minecraft mc = Minecraft.getInstance();
		// Check if we are in Beta+ World
		/* Don't Call Client-side, returns null */
		//mc.world.isRemote &&
		if(mc.world.getWorld().getWorldType() instanceof WorldTypeBetaPlus)
		{
			if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT)
			{
				return;
			}
			mc.fontRenderer.drawStringWithShadow(beta + version, 1.5f, 1.5f, 0xFFFFFF);
		}
		if(mc.world.getWorld().getWorldType() instanceof WorldTypeAlphaPlus)
		{
			if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT)
			{
				return;
			}
			mc.fontRenderer.drawStringWithShadow(alpha + version, 1.5f, 1.5f, 0xFFFFFF);
		}
	}
}
