package net.minecraft.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface GuiYesNoCallback {
   void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_);
}