package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.Tezzelator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsClickableScrolledSelectionListProxy extends GuiSlot {
   private final RealmsClickableScrolledSelectionList field_207723_v;

   public RealmsClickableScrolledSelectionListProxy(RealmsClickableScrolledSelectionList p_i49326_1_, int p_i49326_2_, int p_i49326_3_, int p_i49326_4_, int p_i49326_5_, int p_i49326_6_) {
      super(Minecraft.getInstance(), p_i49326_2_, p_i49326_3_, p_i49326_4_, p_i49326_5_, p_i49326_6_);
      this.field_207723_v = p_i49326_1_;
   }

   protected int getSize() {
      return this.field_207723_v.getItemCount();
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
      return this.field_207723_v.selectItem(index, button, mouseX, mouseY);
   }

   /**
    * Returns true if the element passed in is currently selected
    */
   protected boolean isSelected(int slotIndex) {
      return this.field_207723_v.isSelectedItem(slotIndex);
   }

   protected void drawBackground() {
      this.field_207723_v.renderBackground();
   }

   protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
      this.field_207723_v.renderItem(slotIndex, xPos, yPos, heightIn, mouseXIn, mouseYIn);
   }

   public int width() {
      return this.width;
   }

   /**
    * Return the height of the content being scrolled
    */
   protected int getContentHeight() {
      return this.field_207723_v.getMaxPosition();
   }

   protected int getScrollBarX() {
      return this.field_207723_v.getScrollbarPosition();
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_) {
      return this.field_207723_v.mouseScrolled(p_mouseScrolled_1_) ? true : super.mouseScrolled(p_mouseScrolled_1_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.field_207723_v.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      return this.field_207723_v.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return this.field_207723_v.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_) ? true : super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
   }

   public void renderSelected(int p_207719_1_, int p_207719_2_, int p_207719_3_, Tezzelator p_207719_4_) {
      this.field_207723_v.renderSelected(p_207719_1_, p_207719_2_, p_207719_3_, p_207719_4_);
   }

   /**
    * Draws the selection box around the selected slot element.
    */
   protected void drawSelectionBox(int insideLeft, int insideTop, int mouseXIn, int mouseYIn, float partialTicks) {
      int i = this.getSize();

      for(int j = 0; j < i; ++j) {
         int k = insideTop + j * this.slotHeight + this.headerPadding;
         int l = this.slotHeight - 4;
         if (k > this.bottom || k + l < this.top) {
            this.updateItemPos(j, insideLeft, k, partialTicks);
         }

         if (this.showSelectionBox && this.isSelected(j)) {
            this.renderSelected(this.width, k, l, Tezzelator.instance);
         }

         this.drawSlot(j, insideLeft, k, l, mouseXIn, mouseYIn, partialTicks);
      }

   }

   public int y0() {
      return this.top;
   }

   public int y1() {
      return this.bottom;
   }

   public int headerHeight() {
      return this.headerPadding;
   }

   public double yo() {
      return this.amountScrolled;
   }

   public int itemHeight() {
      return this.slotHeight;
   }
}