package net.minecraft.client.gui.advancements;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiScreenAdvancements extends GuiScreen implements ClientAdvancementManager.IListener {
   private static final ResourceLocation WINDOW = new ResourceLocation("textures/gui/advancements/window.png");
   private static final ResourceLocation TABS = new ResourceLocation("textures/gui/advancements/tabs.png");
   private final ClientAdvancementManager clientAdvancementManager;
   private final Map<Advancement, GuiAdvancementTab> tabs = Maps.newLinkedHashMap();
   private GuiAdvancementTab selectedTab;
   private boolean isScrolling;
   private static int tabPage, maxPages;

   public GuiScreenAdvancements(ClientAdvancementManager p_i47383_1_) {
      this.clientAdvancementManager = p_i47383_1_;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.tabs.clear();
      this.selectedTab = null;
      this.clientAdvancementManager.setListener(this);
      if (this.selectedTab == null && !this.tabs.isEmpty()) {
         this.clientAdvancementManager.setSelectedTab(this.tabs.values().iterator().next().getAdvancement(), true);
      } else {
         this.clientAdvancementManager.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getAdvancement(), true);
      }
      if (this.tabs.size() > AdvancementTabType.MAX_TABS) {
          int guiLeft = (this.width - 252) / 2;
          int guiTop = (this.height - 140) / 2;
          addButton(new net.minecraft.client.gui.GuiButton(101, guiLeft, guiTop - 50, 20, 20, "<") {
             @Override
             public void onClick(double mouseX, double mouseY) {
                tabPage = Math.max(tabPage - 1, 0);
             }
          });
          addButton(new net.minecraft.client.gui.GuiButton(102, guiLeft + 252 - 20, guiTop - 50, 20, 20, ">") {
             @Override
             public void onClick(double mouseX, double mouseY) {
                tabPage = Math.min(tabPage + 1, maxPages);
             }
          });
          maxPages = this.tabs.size() / AdvancementTabType.MAX_TABS;
      }
   }

   /**
    * Called when the screen is unloaded. Used to disable keyboard repeat events
    */
   public void onGuiClosed() {
      this.clientAdvancementManager.setListener((ClientAdvancementManager.IListener)null);
      NetHandlerPlayClient nethandlerplayclient = this.mc.getConnection();
      if (nethandlerplayclient != null) {
         nethandlerplayclient.sendPacket(CPacketSeenAdvancements.closedScreen());
      }

   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ == 0) {
         int i = (this.width - 252) / 2;
         int j = (this.height - 140) / 2;

         for(GuiAdvancementTab guiadvancementtab : this.tabs.values()) {
            if (guiadvancementtab.getPage() == tabPage && guiadvancementtab.func_195627_a(i, j, p_mouseClicked_1_, p_mouseClicked_3_)) {
               this.clientAdvancementManager.setSelectedTab(guiadvancementtab.getAdvancement(), true);
               break;
            }
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.mc.gameSettings.keyBindAdvancements.matchesKey(p_keyPressed_1_, p_keyPressed_2_)) {
         this.mc.displayGuiScreen((GuiScreen)null);
         this.mc.mouseHelper.grabMouse();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      int i = (this.width - 252) / 2;
      int j = (this.height - 140) / 2;
      this.drawDefaultBackground();
      this.renderInside(mouseX, mouseY, i, j);
      this.renderWindow(i, j);
      this.renderToolTips(mouseX, mouseY, i, j);
      if (maxPages != 0) {
         String page = String.format("%d / %d", tabPage + 1, maxPages + 1);
         int width = this.fontRenderer.getStringWidth(page);
         GlStateManager.disableLighting();
         this.fontRenderer.drawStringWithShadow(page, i + (252 / 2) - (width / 2), j - 44, -1);
      }
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (p_mouseDragged_5_ != 0) {
         this.isScrolling = false;
         return false;
      } else {
         if (!this.isScrolling) {
            this.isScrolling = true;
         } else if (this.selectedTab != null) {
            this.selectedTab.func_195626_a(p_mouseDragged_6_, p_mouseDragged_8_);
         }

         return true;
      }
   }

   private void renderInside(int p_191936_1_, int p_191936_2_, int p_191936_3_, int p_191936_4_) {
      GuiAdvancementTab guiadvancementtab = this.selectedTab;
      if (guiadvancementtab == null) {
         drawRect(p_191936_3_ + 9, p_191936_4_ + 18, p_191936_3_ + 9 + 234, p_191936_4_ + 18 + 113, -16777216);
         String s = I18n.format("advancements.empty");
         int i = this.fontRenderer.getStringWidth(s);
         this.fontRenderer.drawString(s, (float)(p_191936_3_ + 9 + 117 - i / 2), (float)(p_191936_4_ + 18 + 56 - this.fontRenderer.FONT_HEIGHT / 2), -1);
         this.fontRenderer.drawString(":(", (float)(p_191936_3_ + 9 + 117 - this.fontRenderer.getStringWidth(":(") / 2), (float)(p_191936_4_ + 18 + 113 - this.fontRenderer.FONT_HEIGHT), -1);
      } else {
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)(p_191936_3_ + 9), (float)(p_191936_4_ + 18), -400.0F);
         GlStateManager.enableDepthTest();
         guiadvancementtab.drawContents();
         GlStateManager.popMatrix();
         GlStateManager.depthFunc(515);
         GlStateManager.disableDepthTest();
      }
   }

   public void renderWindow(int p_191934_1_, int p_191934_2_) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
      RenderHelper.disableStandardItemLighting();
      this.mc.getTextureManager().bindTexture(WINDOW);
      this.drawTexturedModalRect(p_191934_1_, p_191934_2_, 0, 0, 252, 140);
      if (this.tabs.size() > 1) {
         this.mc.getTextureManager().bindTexture(TABS);

         for(GuiAdvancementTab guiadvancementtab : this.tabs.values()) {
            if (guiadvancementtab.getPage() == tabPage)
            guiadvancementtab.drawTab(p_191934_1_, p_191934_2_, guiadvancementtab == this.selectedTab);
         }

         GlStateManager.enableRescaleNormal();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         RenderHelper.enableGUIStandardItemLighting();

         for(GuiAdvancementTab guiadvancementtab1 : this.tabs.values()) {
            if (guiadvancementtab1.getPage() == tabPage)
            guiadvancementtab1.drawIcon(p_191934_1_, p_191934_2_, this.itemRender);
         }

         GlStateManager.disableBlend();
      }

      this.fontRenderer.drawString(I18n.format("gui.advancements"), (float)(p_191934_1_ + 8), (float)(p_191934_2_ + 6), 4210752);
   }

   private void renderToolTips(int p_191937_1_, int p_191937_2_, int p_191937_3_, int p_191937_4_) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.selectedTab != null) {
         GlStateManager.pushMatrix();
         GlStateManager.enableDepthTest();
         GlStateManager.translatef((float)(p_191937_3_ + 9), (float)(p_191937_4_ + 18), 400.0F);
         this.selectedTab.drawToolTips(p_191937_1_ - p_191937_3_ - 9, p_191937_2_ - p_191937_4_ - 18, p_191937_3_, p_191937_4_);
         GlStateManager.disableDepthTest();
         GlStateManager.popMatrix();
      }

      if (this.tabs.size() > 1) {
         for(GuiAdvancementTab guiadvancementtab : this.tabs.values()) {
            if (guiadvancementtab.getPage() == tabPage && guiadvancementtab.func_195627_a(p_191937_3_, p_191937_4_, (double)p_191937_1_, (double)p_191937_2_)) {
               this.drawHoveringText(guiadvancementtab.getTitle(), p_191937_1_, p_191937_2_);
            }
         }
      }

   }

   public void rootAdvancementAdded(Advancement advancementIn) {
      GuiAdvancementTab guiadvancementtab = GuiAdvancementTab.create(this.mc, this, this.tabs.size(), advancementIn);
      if (guiadvancementtab != null) {
         this.tabs.put(advancementIn, guiadvancementtab);
      }
   }

   public void rootAdvancementRemoved(Advancement advancementIn) {
   }

   public void nonRootAdvancementAdded(Advancement advancementIn) {
      GuiAdvancementTab guiadvancementtab = this.getTab(advancementIn);
      if (guiadvancementtab != null) {
         guiadvancementtab.addAdvancement(advancementIn);
      }

   }

   public void nonRootAdvancementRemoved(Advancement advancementIn) {
   }

   public void onUpdateAdvancementProgress(Advancement advancementIn, AdvancementProgress progress) {
      GuiAdvancement guiadvancement = this.getAdvancementGui(advancementIn);
      if (guiadvancement != null) {
         guiadvancement.setAdvancementProgress(progress);
      }

   }

   public void setSelectedTab(@Nullable Advancement advancementIn) {
      this.selectedTab = this.tabs.get(advancementIn);
   }

   public void advancementsCleared() {
      this.tabs.clear();
      this.selectedTab = null;
   }

   @Nullable
   public GuiAdvancement getAdvancementGui(Advancement p_191938_1_) {
      GuiAdvancementTab guiadvancementtab = this.getTab(p_191938_1_);
      return guiadvancementtab == null ? null : guiadvancementtab.getAdvancementGui(p_191938_1_);
   }

   @Nullable
   private GuiAdvancementTab getTab(Advancement p_191935_1_) {
      while(p_191935_1_.getParent() != null) {
         p_191935_1_ = p_191935_1_.getParent();
      }

      return this.tabs.get(p_191935_1_);
   }
}