package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiButtonToggle extends GuiButton {
   protected ResourceLocation resourceLocation;
   protected boolean stateTriggered;
   protected int xTexStart;
   protected int yTexStart;
   protected int xDiffTex;
   protected int yDiffTex;

   public GuiButtonToggle(int buttonId, int xIn, int yIn, int widthIn, int heightIn, boolean buttonText) {
      super(buttonId, xIn, yIn, widthIn, heightIn, "");
      this.stateTriggered = buttonText;
   }

   public void initTextureValues(int xTexStartIn, int yTexStartIn, int xDiffTexIn, int yDiffTexIn, ResourceLocation resourceLocationIn) {
      this.xTexStart = xTexStartIn;
      this.yTexStart = yTexStartIn;
      this.xDiffTex = xDiffTexIn;
      this.yDiffTex = yDiffTexIn;
      this.resourceLocation = resourceLocationIn;
   }

   public void setStateTriggered(boolean p_191753_1_) {
      this.stateTriggered = p_191753_1_;
   }

   public boolean isStateTriggered() {
      return this.stateTriggered;
   }

   public void setPosition(int xIn, int yIn) {
      this.x = xIn;
      this.y = yIn;
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         Minecraft minecraft = Minecraft.getInstance();
         minecraft.getTextureManager().bindTexture(this.resourceLocation);
         GlStateManager.disableDepthTest();
         int i = this.xTexStart;
         int j = this.yTexStart;
         if (this.stateTriggered) {
            i += this.xDiffTex;
         }

         if (this.hovered) {
            j += this.yDiffTex;
         }

         this.drawTexturedModalRect(this.x, this.y, i, j, this.width, this.height);
         GlStateManager.enableDepthTest();
      }
   }
}