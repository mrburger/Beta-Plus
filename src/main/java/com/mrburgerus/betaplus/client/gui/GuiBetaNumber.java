package com.mrburgerus.betaplus.client.gui;

import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class GuiBetaNumber extends AbstractGui
{
	GuiBetaNumber()
	{

	}

	private static String betaVersion = "Minecraft Beta+ 1.14.4";

	/* Overlays Version if we use a Beta World Type */
	@SubscribeEvent
	public static void overlayEvent(RenderGameOverlayEvent.Post event)
	{
		Minecraft mc = Minecraft.getInstance();
		// Check if we are in Beta+ World
		/* Don't Call Client-side, returns null */
		if(mc.getIntegratedServer().getWorld(DimensionType.OVERWORLD).getWorldType() instanceof WorldTypeBetaPlus)
		{
			if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE)
			{
				return;
			}
			mc.fontRenderer.drawStringWithShadow(betaVersion, 1.5f, 1.5f, 0xFFFFFF);
		}
	}
}
