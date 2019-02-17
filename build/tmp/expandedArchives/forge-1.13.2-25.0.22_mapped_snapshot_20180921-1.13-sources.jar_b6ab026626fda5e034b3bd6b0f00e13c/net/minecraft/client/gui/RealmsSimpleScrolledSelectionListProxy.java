package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsSimpleScrolledSelectionListProxy extends GuiSlot {
   private final RealmsSimpleScrolledSelectionList field_207727_v;

   public RealmsSimpleScrolledSelectionListProxy(RealmsSimpleScrolledSelectionList p_i49324_1_, int p_i49324_2_, int p_i49324_3_, int p_i49324_4_, int p_i49324_5_, int p_i49324_6_) {
      super(Minecraft.getInstance(), p_i49324_2_, p_i49324_3_, p_i49324_4_, p_i49324_5_, p_i49324_6_);
      this.field_207727_v = p_i49324_1_;
   }

   protected int getSize() {
      return this.field_207727_v.getItemCount();
   }

   /**
    * Called when the mouse is clicked onto an entry.
    *  
    * @return true if the entry did something with the click and it should be selected.
    *  
    * @param index Index of the entry
    * @param button The mouse button that was pressed.
    * @param mouseX The mouse X coordinate.
    * @param mouseY The mouse Y coordinate.
    */
   protected boolean mouseClicked(int index, int button, double mouseX, double mouseY) {
      return this.field_207727_v.selectItem(index, button, mouseX, mouseY);
   }

   /**
    * Returns true if the element passed in is currently selected
    */
   protected boolean isSelected(int slotIndex) {
      return this.field_207727_v.isSelectedItem(slotIndex);
   }

   protected void drawBackground() {
      this.field_207727_v.renderBackground();
   }

   protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
      this.field_207727_v.renderItem(slotIndex, xPos, yPos, heightIn, mouseXIn, mouseYIn);
   }

   public int width() {
      return this.width;
   }

   /**
    * Return the height of the content being scrolled
    */
   protected int getContentHeight() {
      return this.field_207727_v.getMaxPosition();
   }

   protected int getScrollBarX() {
      return this.field_207727_v.getScrollbarPosition();
   }

   public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks) {
      if (this.visible) {
         this.drawBackground();
         int i = this.getScrollBarX();
         int j = i + 6;
         this.bindAmountScrolled();
         GlStateManager.disableLighting();
         GlStateManager.disableFog();
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         int k = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
         int l = this.top + 4 - (int)this.amountScrolled;
         if (this.hasListHeader) {
            this.drawListHeader(k, l, tessellator);
         }

         this.drawSelectionBox(k, l, mouseXIn, mouseYIn, partialTicks);
         GlStateManager.disableDepthTest();
         this.overlayBackground(0, this.top, 255, 255);
         this.overlayBackground(this.bottom, this.height, 255, 255);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         GlStateManager.disableAlphaTest();
         GlStateManager.shadeModel(7425);
         GlStateManager.disableTexture2D();
         int i1 = this.getMaxScroll();
         if (i1 > 0) {
            int j1 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
            j1 = MathHelper.clamp(j1, 32, this.bottom - this.top - 8);
            int k1 = (int)this.amountScrolled * (this.bottom - this.top - j1) / i1 + this.top;
            if (k1 < this.top) {
               k1 = this.top;
            }

            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)i, (double)this.bottom, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)j, (double)this.bottom, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)j, (double)this.top, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)i, (double)this.top, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)i, (double)(k1 + j1), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)j, (double)(k1 + j1), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)j, (double)k1, 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)i, (double)k1, 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            tessellator.draw();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)i, (double)(k1 + j1 - 1), 0.0D).tex(0.0D, 1.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos((double)(j - 1), (double)(k1 + j1 - 1), 0.0D).tex(1.0D, 1.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos((double)(j - 1), (double)k1, 0.0D).tex(1.0D, 0.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos((double)i, (double)k1, 0.0D).tex(0.0D, 0.0D).color(192, 192, 192, 255).endVertex();
            tessellator.draw();
         }

         this.renderDecorations(mouseXIn, mouseYIn);
         GlStateManager.enableTexture2D();
         GlStateManager.shadeModel(7424);
         GlStateManager.enableAlphaTest();
         GlStateManager.disableBlend();
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_) {
      return this.field_207727_v.mouseScrolled(p_mouseScrolled_1_) ? true : super.mouseScrolled(p_mouseScrolled_1_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.field_207727_v.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      return this.field_207727_v.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return this.field_207727_v.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
   }
}