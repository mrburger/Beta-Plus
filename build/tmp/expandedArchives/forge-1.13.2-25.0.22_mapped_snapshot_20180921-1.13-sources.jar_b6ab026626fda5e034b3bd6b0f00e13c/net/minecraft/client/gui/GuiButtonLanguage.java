package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiButtonLanguage extends GuiButton {
   public GuiButtonLanguage(int buttonID, int xPos, int yPos) {
      super(buttonID, xPos, yPos, 20, 20, "");
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         Minecraft.getInstance().getTextureManager().bindTexture(GuiButton.BUTTON_TEXTURES);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         int i = 106;
         if (flag) {
            i += this.height;
         }

         this.drawTexturedModalRect(this.x, this.y, 0, i, this.width, this.height);
      }
   }
}