package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiResourcePackList extends GuiListExtended<ResourcePackListEntryFound> {
   protected final Minecraft mc;

   public GuiResourcePackList(Minecraft p_i47648_1_, int p_i47648_2_, int p_i47648_3_) {
      super(p_i47648_1_, p_i47648_2_, p_i47648_3_, 32, p_i47648_3_ - 55 + 4, 36);
      this.mc = p_i47648_1_;
      this.centerListVertically = false;
      this.setHasListHeader(true, (int)((float)p_i47648_1_.fontRenderer.FONT_HEIGHT * 1.5F));
   }

   /**
    * Handles drawing a list's header row.
    */
   protected void drawListHeader(int insideLeft, int insideTop, Tessellator tessellatorIn) {
      String s = TextFormatting.UNDERLINE + "" + TextFormatting.BOLD + this.getListHeader();
      this.mc.fontRenderer.drawString(s, (float)(insideLeft + this.width / 2 - this.mc.fontRenderer.getStringWidth(s) / 2), (float)Math.min(this.top + 3, insideTop), 16777215);
   }

   protected abstract String getListHeader();

   /**
    * Gets the width of the list
    */
   public int getListWidth() {
      return this.width;
   }

   protected int getScrollBarX() {
      return this.right - 6;
   }

   public void func_195095_a(ResourcePackListEntryFound p_195095_1_) {
      super.addEntry(p_195095_1_);
   }
}