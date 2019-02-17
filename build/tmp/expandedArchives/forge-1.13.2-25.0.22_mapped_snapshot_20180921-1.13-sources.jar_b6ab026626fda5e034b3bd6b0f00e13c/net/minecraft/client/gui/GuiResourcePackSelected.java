package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiResourcePackSelected extends GuiResourcePackList {
   public GuiResourcePackSelected(Minecraft p_i47647_1_, int p_i47647_2_, int p_i47647_3_) {
      super(p_i47647_1_, p_i47647_2_, p_i47647_3_);
   }

   protected String getListHeader() {
      return I18n.format("resourcePack.selected.title");
   }
}