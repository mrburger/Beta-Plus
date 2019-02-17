package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiLabel extends Gui implements IGuiEventListener {
   protected int width;
   protected int height;
   public int x;
   public int y;
   private final List<String> labels;
   private boolean centered;
   public boolean visible;
   private boolean labelBgEnabled;
   private final int textColor;
   private int backColor;
   private int ulColor;
   private int brColor;
   private final FontRenderer fontRenderer;
   private int border;

   public GuiLabel(List<String> text, int color, FontRenderer font) {
       this.labels = text;
       this.textColor = color;
       this.fontRenderer = font;
   }

   public void render(int p_194997_1_, int p_194997_2_, float p_194997_3_) {
      if (this.visible) {
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         this.func_194996_b(p_194997_1_, p_194997_2_, p_194997_3_);
         int i = this.y + this.height / 2 + this.border / 2;
         int j = i - this.labels.size() * 10 / 2;

         for(int k = 0; k < this.labels.size(); ++k) {
            if (this.centered) {
               this.drawCenteredString(this.fontRenderer, this.labels.get(k), this.x + this.width / 2, j + k * 10, this.textColor);
            } else {
               this.drawString(this.fontRenderer, this.labels.get(k), this.x, j + k * 10, this.textColor);
            }
         }

      }
   }

   protected void func_194996_b(int p_194996_1_, int p_194996_2_, float p_194996_3_) {
      if (this.labelBgEnabled) {
         int i = this.width + this.border * 2;
         int j = this.height + this.border * 2;
         int k = this.x - this.border;
         int l = this.y - this.border;
         drawRect(k, l, k + i, l + j, this.backColor);
         this.drawHorizontalLine(k, k + i, l, this.ulColor);
         this.drawHorizontalLine(k, k + i, l + j, this.brColor);
         this.drawVerticalLine(k, l, l + j, this.ulColor);
         this.drawVerticalLine(k + i, l, l + j, this.brColor);
      }

   }
}