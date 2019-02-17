package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsScrolledSelectionList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsScrolledSelectionListProxy extends GuiSlot {
   private final RealmsScrolledSelectionList scrolledSelectionList;

   public RealmsScrolledSelectionListProxy(RealmsScrolledSelectionList p_i49325_1_, int p_i49325_2_, int p_i49325_3_, int p_i49325_4_, int p_i49325_5_, int p_i49325_6_) {
      super(Minecraft.getInstance(), p_i49325_2_, p_i49325_3_, p_i49325_4_, p_i49325_5_, p_i49325_6_);
      this.scrolledSelectionList = p_i49325_1_;
   }

   protected int getSize() {
      return this.scrolledSelectionList.getItemCount();
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
      return this.scrolledSelectionList.selectItem(index, button, mouseX, mouseY);
   }

   /**
    * Returns true if the element passed in is currently selected
    */
   protected boolean isSelected(int slotIndex) {
      return this.scrolledSelectionList.isSelectedItem(slotIndex);
   }

   protected void drawBackground() {
      this.scrolledSelectionList.renderBackground();
   }

   protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
      this.scrolledSelectionList.renderItem(slotIndex, xPos, yPos, heightIn, mouseXIn, mouseYIn);
   }

   public int width() {
      return this.width;
   }

   /**
    * Return the height of the content being scrolled
    */
   protected int getContentHeight() {
      return this.scrolledSelectionList.getMaxPosition();
   }

   protected int getScrollBarX() {
      return this.scrolledSelectionList.getScrollbarPosition();
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_) {
      return this.scrolledSelectionList.mouseScrolled(p_mouseScrolled_1_) ? true : super.mouseScrolled(p_mouseScrolled_1_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.scrolledSelectionList.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      return this.scrolledSelectionList.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return this.scrolledSelectionList.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
   }
}