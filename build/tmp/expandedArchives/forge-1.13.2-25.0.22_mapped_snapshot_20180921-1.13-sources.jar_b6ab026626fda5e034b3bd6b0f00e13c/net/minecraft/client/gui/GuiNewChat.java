package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiNewChat extends Gui {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft mc;
   /** A list of messages previously sent through the chat GUI */
   private final List<String> sentMessages = Lists.newArrayList();
   /** Chat lines to be displayed in the chat box */
   private final List<ChatLine> chatLines = Lists.newArrayList();
   /** List of the ChatLines currently drawn */
   private final List<ChatLine> drawnChatLines = Lists.newArrayList();
   private int scrollPos;
   private boolean isScrolled;

   public GuiNewChat(Minecraft mcIn) {
      this.mc = mcIn;
   }

   public void drawChat(int updateCounter) {
      if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
         int i = this.getLineCount();
         int j = this.drawnChatLines.size();
         double d0 = this.mc.gameSettings.chatOpacity * (double)0.9F + (double)0.1F;
         if (j > 0) {
            boolean flag = false;
            if (this.getChatOpen()) {
               flag = true;
            }

            double d1 = this.getScale();
            int k = MathHelper.ceil((double)this.getChatWidth() / d1);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(2.0F, 8.0F, 0.0F);
            GlStateManager.scaled(d1, d1, 1.0D);
            int l = 0;

            for(int i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; ++i1) {
               ChatLine chatline = this.drawnChatLines.get(i1 + this.scrollPos);
               if (chatline != null) {
                  int j1 = updateCounter - chatline.getUpdatedCounter();
                  if (j1 < 200 || flag) {
                     double d2 = (double)j1 / 200.0D;
                     d2 = 1.0D - d2;
                     d2 = d2 * 10.0D;
                     d2 = MathHelper.clamp(d2, 0.0D, 1.0D);
                     d2 = d2 * d2;
                     int l1 = (int)(255.0D * d2);
                     if (flag) {
                        l1 = 255;
                     }

                     l1 = (int)((double)l1 * d0);
                     ++l;
                     if (l1 > 3) {
                        int i2 = 0;
                        int j2 = -i1 * 9;
                        drawRect(-2, j2 - 9, 0 + k + 4, j2, l1 / 2 << 24);
                        String s = chatline.getChatComponent().getFormattedText();
                        GlStateManager.enableBlend();
                        this.mc.fontRenderer.drawStringWithShadow(s, 0.0F, (float)(j2 - 8), 16777215 + (l1 << 24));
                        GlStateManager.disableAlphaTest();
                        GlStateManager.disableBlend();
                     }
                  }
               }
            }

            if (flag) {
               int k2 = this.mc.fontRenderer.FONT_HEIGHT;
               GlStateManager.translatef(-3.0F, 0.0F, 0.0F);
               int l2 = j * k2 + j;
               int i3 = l * k2 + l;
               int j3 = this.scrollPos * i3 / j;
               int k1 = i3 * i3 / l2;
               if (l2 != i3) {
                  int k3 = j3 > 0 ? 170 : 96;
                  int l3 = this.isScrolled ? 13382451 : 3355562;
                  drawRect(0, -j3, 2, -j3 - k1, l3 + (k3 << 24));
                  drawRect(2, -j3, 1, -j3 - k1, 13421772 + (k3 << 24));
               }
            }

            GlStateManager.popMatrix();
         }
      }
   }

   /**
    * Clears the chat.
    */
   public void clearChatMessages(boolean p_146231_1_) {
      this.drawnChatLines.clear();
      this.chatLines.clear();
      if (p_146231_1_) {
         this.sentMessages.clear();
      }

   }

   public void printChatMessage(ITextComponent chatComponent) {
      this.printChatMessageWithOptionalDeletion(chatComponent, 0);
   }

   /**
    * prints the ChatComponent to Chat. If the ID is not 0, deletes an existing Chat Line of that ID from the GUI
    */
   public void printChatMessageWithOptionalDeletion(ITextComponent chatComponent, int chatLineId) {
      this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getTicks(), false);
      LOGGER.info("[CHAT] {}", (Object)chatComponent.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
   }

   private void setChatLine(ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
      if (chatLineId != 0) {
         this.deleteChatLine(chatLineId);
      }

      int i = MathHelper.floor((double)this.getChatWidth() / this.getScale());
      List<ITextComponent> list = GuiUtilRenderComponents.splitText(chatComponent, i, this.mc.fontRenderer, false, false);
      boolean flag = this.getChatOpen();

      for(ITextComponent itextcomponent : list) {
         if (flag && this.scrollPos > 0) {
            this.isScrolled = true;
            this.func_194813_a(1.0D);
         }

         this.drawnChatLines.add(0, new ChatLine(updateCounter, itextcomponent, chatLineId));
      }

      while(this.drawnChatLines.size() > 100) {
         this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
      }

      if (!displayOnly) {
         this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));

         while(this.chatLines.size() > 100) {
            this.chatLines.remove(this.chatLines.size() - 1);
         }
      }

   }

   public void refreshChat() {
      this.drawnChatLines.clear();
      this.resetScroll();

      for(int i = this.chatLines.size() - 1; i >= 0; --i) {
         ChatLine chatline = this.chatLines.get(i);
         this.setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
      }

   }

   /**
    * Gets the list of messages previously sent through the chat GUI
    */
   public List<String> getSentMessages() {
      return this.sentMessages;
   }

   /**
    * Adds this string to the list of sent messages, for recall using the up/down arrow keys
    */
   public void addToSentMessages(String message) {
      if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(message)) {
         this.sentMessages.add(message);
      }

   }

   /**
    * Resets the chat scroll (executed when the GUI is closed, among others)
    */
   public void resetScroll() {
      this.scrollPos = 0;
      this.isScrolled = false;
   }

   public void func_194813_a(double p_194813_1_) {
      this.scrollPos = (int)((double)this.scrollPos + p_194813_1_);
      int i = this.drawnChatLines.size();
      if (this.scrollPos > i - this.getLineCount()) {
         this.scrollPos = i - this.getLineCount();
      }

      if (this.scrollPos <= 0) {
         this.scrollPos = 0;
         this.isScrolled = false;
      }

   }

   @Nullable
   public ITextComponent func_194817_a(double p_194817_1_, double p_194817_3_) {
      if (!this.getChatOpen()) {
         return null;
      } else {
         double d0 = this.getScale();
         double d1 = p_194817_1_ - 2.0D;
         double d2 = (double)this.mc.mainWindow.getScaledHeight() - p_194817_3_ - 40.0D;
         d1 = (double)MathHelper.floor(d1 / d0);
         d2 = (double)MathHelper.floor(d2 / d0);
         if (!(d1 < 0.0D) && !(d2 < 0.0D)) {
            int i = Math.min(this.getLineCount(), this.drawnChatLines.size());
            if (d1 <= (double)MathHelper.floor((double)this.getChatWidth() / this.getScale()) && d2 < (double)(this.mc.fontRenderer.FONT_HEIGHT * i + i)) {
               int j = (int)(d2 / (double)this.mc.fontRenderer.FONT_HEIGHT + (double)this.scrollPos);
               if (j >= 0 && j < this.drawnChatLines.size()) {
                  ChatLine chatline = this.drawnChatLines.get(j);
                  int k = 0;

                  for(ITextComponent itextcomponent : chatline.getChatComponent()) {
                     if (itextcomponent instanceof TextComponentString) {
                        k += this.mc.fontRenderer.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(((TextComponentString)itextcomponent).getText(), false));
                        if ((double)k > d1) {
                           return itextcomponent;
                        }
                     }
                  }
               }

               return null;
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   /**
    * Returns true if the chat GUI is open
    */
   public boolean getChatOpen() {
      return this.mc.currentScreen instanceof GuiChat;
   }

   /**
    * finds and deletes a Chat line by ID
    */
   public void deleteChatLine(int id) {
      Iterator<ChatLine> iterator = this.drawnChatLines.iterator();

      while(iterator.hasNext()) {
         ChatLine chatline = iterator.next();
         if (chatline.getChatLineID() == id) {
            iterator.remove();
         }
      }

      iterator = this.chatLines.iterator();

      while(iterator.hasNext()) {
         ChatLine chatline1 = iterator.next();
         if (chatline1.getChatLineID() == id) {
            iterator.remove();
            break;
         }
      }

   }

   public int getChatWidth() {
      return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
   }

   public int getChatHeight() {
      return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
   }

   public double getScale() {
      return this.mc.gameSettings.chatScale;
   }

   public static int calculateChatboxWidth(double p_194814_0_) {
      int i = 320;
      int j = 40;
      return MathHelper.floor(p_194814_0_ * 280.0D + 40.0D);
   }

   public static int calculateChatboxHeight(double p_194816_0_) {
      int i = 180;
      int j = 20;
      return MathHelper.floor(p_194816_0_ * 160.0D + 20.0D);
   }

   public int getLineCount() {
      return this.getChatHeight() / 9;
   }
}