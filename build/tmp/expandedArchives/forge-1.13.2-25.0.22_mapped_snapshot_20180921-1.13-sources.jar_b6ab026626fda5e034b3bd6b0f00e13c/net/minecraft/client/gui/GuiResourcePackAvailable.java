package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiResourcePackAvailable extends GuiResourcePackList {
   public GuiResourcePackAvailable(Minecraft p_i47649_1_, int p_i47649_2_, int p_i47649_3_) {
      super(p_i47649_1_, p_i47649_2_, p_i47649_3_);
   }

   protected String getListHeader() {
      return I18n.format("resourcePack.available.title");
   }
}