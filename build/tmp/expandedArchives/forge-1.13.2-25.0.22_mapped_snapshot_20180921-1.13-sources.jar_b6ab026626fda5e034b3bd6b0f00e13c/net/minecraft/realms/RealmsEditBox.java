package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsEditBox extends RealmsGuiEventListener {
   private final GuiTextField editBox;

   public RealmsEditBox(int id, int x, int y, int width, int height) {
      this.editBox = new GuiTextField(id, Minecraft.getInstance().fontRenderer, x, y, width, height);
   }

   public String getValue() {
      return this.editBox.getText();
   }

   public void tick() {
      this.editBox.tick();
   }

   public void setValue(String p_setValue_1_) {
      this.editBox.setText(p_setValue_1_);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.editBox.charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   public IGuiEventListener getProxy() {
      return this.editBox;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return this.editBox.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   public boolean isFocused() {
      return this.editBox.isFocused();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.editBox.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      return this.editBox.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return this.editBox.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_) {
      return this.editBox.mouseScrolled(p_mouseScrolled_1_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.editBox.drawTextField(p_render_1_, p_render_2_, p_render_3_);
   }

   public void setMaxLength(int p_setMaxLength_1_) {
      this.editBox.setMaxStringLength(p_setMaxLength_1_);
   }

   public void setIsEditable(boolean p_setIsEditable_1_) {
      this.editBox.setEnabled(p_setIsEditable_1_);
   }
}