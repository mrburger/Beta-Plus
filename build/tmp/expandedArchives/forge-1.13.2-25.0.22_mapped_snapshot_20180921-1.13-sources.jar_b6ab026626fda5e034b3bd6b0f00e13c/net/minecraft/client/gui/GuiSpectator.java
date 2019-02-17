package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiSpectator extends Gui implements ISpectatorMenuRecipient {
   private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
   public static final ResourceLocation SPECTATOR_WIDGETS = new ResourceLocation("textures/gui/spectator_widgets.png");
   private final Minecraft mc;
   private long lastSelectionTime;
   private SpectatorMenu menu;

   public GuiSpectator(Minecraft mcIn) {
      this.mc = mcIn;
   }

   public void onHotbarSelected(int p_175260_1_) {
      this.lastSelectionTime = Util.milliTime();
      if (this.menu != null) {
         this.menu.selectSlot(p_175260_1_);
      } else {
         this.menu = new SpectatorMenu(this);
      }

   }

   private float getHotbarAlpha() {
      long i = this.lastSelectionTime - Util.milliTime() + 5000L;
      return MathHelper.clamp((float)i / 2000.0F, 0.0F, 1.0F);
   }

   public void renderTooltip(float p_195622_1_) {
      if (this.menu != null) {
         float f = this.getHotbarAlpha();
         if (f <= 0.0F) {
            this.menu.exit();
         } else {
            int i = this.mc.mainWindow.getScaledWidth() / 2;
            float f1 = this.zLevel;
            this.zLevel = -90.0F;
            float f2 = (float)this.mc.mainWindow.getScaledHeight() - 22.0F * f;
            SpectatorDetails spectatordetails = this.menu.getCurrentPage();
            this.func_195624_a(f, i, f2, spectatordetails);
            this.zLevel = f1;
         }
      }
   }

   protected void func_195624_a(float p_195624_1_, int p_195624_2_, float p_195624_3_, SpectatorDetails p_195624_4_) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, p_195624_1_);
      this.mc.getTextureManager().bindTexture(WIDGETS);
      this.drawTexturedModalRect((float)(p_195624_2_ - 91), p_195624_3_, 0, 0, 182, 22);
      if (p_195624_4_.getSelectedSlot() >= 0) {
         this.drawTexturedModalRect((float)(p_195624_2_ - 91 - 1 + p_195624_4_.getSelectedSlot() * 20), p_195624_3_ - 1.0F, 0, 22, 24, 22);
      }

      RenderHelper.enableGUIStandardItemLighting();

      for(int i = 0; i < 9; ++i) {
         this.renderSlot(i, this.mc.mainWindow.getScaledWidth() / 2 - 90 + i * 20 + 2, p_195624_3_ + 3.0F, p_195624_1_, p_195624_4_.getObject(i));
      }

      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
   }

   private void renderSlot(int p_175266_1_, int p_175266_2_, float p_175266_3_, float p_175266_4_, ISpectatorMenuObject p_175266_5_) {
      this.mc.getTextureManager().bindTexture(SPECTATOR_WIDGETS);
      if (p_175266_5_ != SpectatorMenu.EMPTY_SLOT) {
         int i = (int)(p_175266_4_ * 255.0F);
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)p_175266_2_, p_175266_3_, 0.0F);
         float f = p_175266_5_.isEnabled() ? 1.0F : 0.25F;
         GlStateManager.color4f(f, f, f, p_175266_4_);
         p_175266_5_.renderIcon(f, i);
         GlStateManager.popMatrix();
         String s = String.valueOf((Object)this.mc.gameSettings.keyBindsHotbar[p_175266_1_].func_197978_k());
         if (i > 3 && p_175266_5_.isEnabled()) {
            this.mc.fontRenderer.drawStringWithShadow(s, (float)(p_175266_2_ + 19 - 2 - this.mc.fontRenderer.getStringWidth(s)), p_175266_3_ + 6.0F + 3.0F, 16777215 + (i << 24));
         }
      }

   }

   public void renderSelectedItem() {
      int i = (int)(this.getHotbarAlpha() * 255.0F);
      if (i > 3 && this.menu != null) {
         ISpectatorMenuObject ispectatormenuobject = this.menu.getSelectedItem();
         String s = ispectatormenuobject == SpectatorMenu.EMPTY_SLOT ? this.menu.getSelectedCategory().getPrompt().getFormattedText() : ispectatormenuobject.getSpectatorName().getFormattedText();
         if (s != null) {
            int j = (this.mc.mainWindow.getScaledWidth() - this.mc.fontRenderer.getStringWidth(s)) / 2;
            int k = this.mc.mainWindow.getScaledHeight() - 35;
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.mc.fontRenderer.drawStringWithShadow(s, (float)j, (float)k, 16777215 + (i << 24));
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }
      }

   }

   public void onSpectatorMenuClosed(SpectatorMenu menu) {
      this.menu = null;
      this.lastSelectionTime = 0L;
   }

   public boolean isMenuActive() {
      return this.menu != null;
   }

   public void func_195621_a(double p_195621_1_) {
      int i;
      for(i = this.menu.getSelectedSlot() + (int)p_195621_1_; i >= 0 && i <= 8 && (this.menu.getItem(i) == SpectatorMenu.EMPTY_SLOT || !this.menu.getItem(i).isEnabled()); i = (int)((double)i + p_195621_1_)) {
         ;
      }

      if (i >= 0 && i <= 8) {
         this.menu.selectSlot(i);
         this.lastSelectionTime = Util.milliTime();
      }

   }

   public void onMiddleClick() {
      this.lastSelectionTime = Util.milliTime();
      if (this.isMenuActive()) {
         int i = this.menu.getSelectedSlot();
         if (i != -1) {
            this.menu.selectSlot(i);
         }
      } else {
         this.menu = new SpectatorMenu(this);
      }

   }
}