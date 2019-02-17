package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiButtonImage extends GuiButton {
   private final ResourceLocation resourceLocation;
   private final int xTexStart;
   private final int yTexStart;
   private final int yDiffText;

   public GuiButtonImage(int buttonId, int xIn, int yIn, int widthIn, int heightIn, int textureOffestX, int textureOffestY, int p_i47392_8_, ResourceLocation resource) {
      super(buttonId, xIn, yIn, widthIn, heightIn, "");
      this.xTexStart = textureOffestX;
      this.yTexStart = textureOffestY;
      this.yDiffText = p_i47392_8_;
      this.resourceLocation = resource;
   }

   public void setPosition(int p_191746_1_, int p_191746_2_) {
      this.x = p_191746_1_;
      this.y = p_191746_2_;
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         Minecraft minecraft = Minecraft.getInstance();
         minecraft.getTextureManager().bindTexture(this.resourceLocation);
         GlStateManager.disableDepthTest();
         int i = this.yTexStart;
         if (this.hovered) {
            i += this.yDiffText;
         }

         this.drawTexturedModalRect(this.x, this.y, this.xTexStart, i, this.width, this.height);
         GlStateManager.enableDepthTest();
      }
   }
}