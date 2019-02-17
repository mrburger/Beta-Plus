package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.client.CPacketEditBook;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiScreenBook extends GuiScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation BOOK_GUI_TEXTURES = new ResourceLocation("textures/gui/book.png");
   /** The player editing the book */
   private final EntityPlayer editingPlayer;
   private final ItemStack book;
   /** Whether the book is signed or can still be edited */
   private final boolean bookIsUnsigned;
   /** Whether the book's title or contents has been modified since being opened */
   private boolean bookIsModified;
   /** Determines if the signing screen is open */
   private boolean bookGettingSigned;
   /** Update ticks since the gui was opened */
   private int ticks;
   private final int bookImageWidth = 192;
   private final int bookImageHeight = 192;
   private int bookTotalPages = 1;
   private int currPage;
   private NBTTagList bookPages;
   private String bookTitle = "";
   private List<ITextComponent> cachedComponents;
   private int cachedPage = -1;
   private GuiScreenBook.NextPageButton buttonNextPage;
   private GuiScreenBook.NextPageButton buttonPreviousPage;
   private GuiButton buttonDone;
   /** The GuiButton to sign this book. */
   private GuiButton buttonSign;
   private GuiButton buttonFinalize;
   private GuiButton buttonCancel;
   private final EnumHand field_212343_J;

   public GuiScreenBook(EntityPlayer p_i49849_1_, ItemStack p_i49849_2_, boolean p_i49849_3_, EnumHand p_i49849_4_) {
      this.editingPlayer = p_i49849_1_;
      this.book = p_i49849_2_;
      this.bookIsUnsigned = p_i49849_3_;
      this.field_212343_J = p_i49849_4_;
      if (p_i49849_2_.hasTag()) {
         NBTTagCompound nbttagcompound = p_i49849_2_.getTag();
         this.bookPages = nbttagcompound.getList("pages", 8).copy();
         this.bookTotalPages = this.bookPages.size();
         if (this.bookTotalPages < 1) {
            this.bookPages.add((INBTBase)(new NBTTagString("")));
            this.bookTotalPages = 1;
         }
      }

      if (this.bookPages == null && p_i49849_3_) {
         this.bookPages = new NBTTagList();
         this.bookPages.add((INBTBase)(new NBTTagString("")));
         this.bookTotalPages = 1;
      }

   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      super.tick();
      ++this.ticks;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      if (this.bookIsUnsigned) {
         this.buttonSign = this.addButton(new GuiButton(3, this.width / 2 - 100, 196, 98, 20, I18n.format("book.signButton")) {
            /**
             * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
             */
            public void onClick(double mouseX, double mouseY) {
               GuiScreenBook.this.bookGettingSigned = true;
               GuiScreenBook.this.updateButtons();
            }
         });
         this.buttonDone = this.addButton(new GuiButton(0, this.width / 2 + 2, 196, 98, 20, I18n.format("gui.done")) {
            /**
             * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
             */
            public void onClick(double mouseX, double mouseY) {
               GuiScreenBook.this.mc.displayGuiScreen((GuiScreen)null);
               GuiScreenBook.this.sendBookToServer(false);
            }
         });
         this.buttonFinalize = this.addButton(new GuiButton(5, this.width / 2 - 100, 196, 98, 20, I18n.format("book.finalizeButton")) {
            /**
             * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
             */
            public void onClick(double mouseX, double mouseY) {
               if (GuiScreenBook.this.bookGettingSigned) {
                  GuiScreenBook.this.sendBookToServer(true);
                  GuiScreenBook.this.mc.displayGuiScreen((GuiScreen)null);
               }

            }
         });
         this.buttonCancel = this.addButton(new GuiButton(4, this.width / 2 + 2, 196, 98, 20, I18n.format("gui.cancel")) {
            /**
             * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
             */
            public void onClick(double mouseX, double mouseY) {
               if (GuiScreenBook.this.bookGettingSigned) {
                  GuiScreenBook.this.bookGettingSigned = false;
               }

               GuiScreenBook.this.updateButtons();
            }
         });
      } else {
         this.buttonDone = this.addButton(new GuiButton(0, this.width / 2 - 100, 196, 200, 20, I18n.format("gui.done")) {
            /**
             * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
             */
            public void onClick(double mouseX, double mouseY) {
               GuiScreenBook.this.mc.displayGuiScreen((GuiScreen)null);
               GuiScreenBook.this.sendBookToServer(false);
            }
         });
      }

      int i = (this.width - 192) / 2;
      int j = 2;
      this.buttonNextPage = this.addButton(new GuiScreenBook.NextPageButton(1, i + 120, 156, true) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            if (GuiScreenBook.this.currPage < GuiScreenBook.this.bookTotalPages - 1) {
               GuiScreenBook.this.currPage++;
            } else if (GuiScreenBook.this.bookIsUnsigned) {
               GuiScreenBook.this.addNewPage();
               if (GuiScreenBook.this.currPage < GuiScreenBook.this.bookTotalPages - 1) {
                  GuiScreenBook.this.currPage++;
               }
            }

            GuiScreenBook.this.updateButtons();
         }
      });
      this.buttonPreviousPage = this.addButton(new GuiScreenBook.NextPageButton(2, i + 38, 156, false) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            if (GuiScreenBook.this.currPage > 0) {
               GuiScreenBook.this.currPage--;
            }

            GuiScreenBook.this.updateButtons();
         }
      });
      this.updateButtons();
   }

   /**
    * Called when the screen is unloaded. Used to disable keyboard repeat events
    */
   public void onGuiClosed() {
      this.mc.keyboardListener.enableRepeatEvents(false);
   }

   private void updateButtons() {
      this.buttonNextPage.visible = !this.bookGettingSigned && (this.currPage < this.bookTotalPages - 1 || this.bookIsUnsigned);
      this.buttonPreviousPage.visible = !this.bookGettingSigned && this.currPage > 0;
      this.buttonDone.visible = !this.bookIsUnsigned || !this.bookGettingSigned;
      if (this.bookIsUnsigned) {
         this.buttonSign.visible = !this.bookGettingSigned;
         this.buttonCancel.visible = this.bookGettingSigned;
         this.buttonFinalize.visible = this.bookGettingSigned;
         this.buttonFinalize.enabled = !this.bookTitle.trim().isEmpty();
      }

   }

   private void sendBookToServer(boolean publish) {
      if (this.bookIsUnsigned && this.bookIsModified) {
         if (this.bookPages != null) {
            while(this.bookPages.size() > 1) {
               String s = this.bookPages.getString(this.bookPages.size() - 1);
               if (!s.isEmpty()) {
                  break;
               }

               this.bookPages.remove(this.bookPages.size() - 1);
            }

            this.book.setTagInfo("pages", this.bookPages);
            if (publish) {
               this.book.setTagInfo("author", new NBTTagString(this.editingPlayer.getGameProfile().getName()));
               this.book.setTagInfo("title", new NBTTagString(this.bookTitle.trim()));
            }

            this.mc.getConnection().sendPacket(new CPacketEditBook(this.book, publish, this.field_212343_J));
         }

      }
   }

   private void addNewPage() {
      if (this.bookPages != null && this.bookPages.size() < 50) {
         this.bookPages.add((INBTBase)(new NBTTagString("")));
         ++this.bookTotalPages;
         this.bookIsModified = true;
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (this.bookIsUnsigned) {
         return this.bookGettingSigned ? this.func_195267_b(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) : this.func_195259_a(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      } else {
         return false;
      }
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      if (super.charTyped(p_charTyped_1_, p_charTyped_2_)) {
         return true;
      } else if (this.bookIsUnsigned) {
         if (this.bookGettingSigned) {
            if (this.bookTitle.length() < 16 && SharedConstants.isAllowedCharacter(p_charTyped_1_)) {
               this.bookTitle = this.bookTitle + Character.toString(p_charTyped_1_);
               this.updateButtons();
               this.bookIsModified = true;
               return true;
            } else {
               return false;
            }
         } else if (SharedConstants.isAllowedCharacter(p_charTyped_1_)) {
            this.pageInsertIntoCurrent(Character.toString(p_charTyped_1_));
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean func_195259_a(int p_195259_1_, int p_195259_2_, int p_195259_3_) {
      if (GuiScreen.isKeyComboCtrlV(p_195259_1_)) {
         this.pageInsertIntoCurrent(this.mc.keyboardListener.getClipboardString());
         return true;
      } else {
         switch(p_195259_1_) {
         case 257:
         case 335:
            this.pageInsertIntoCurrent("\n");
            return true;
         case 259:
            String s = this.pageGetCurrent();
            if (!s.isEmpty()) {
               this.pageSetCurrent(s.substring(0, s.length() - 1));
            }

            return true;
         default:
            return false;
         }
      }
   }

   private boolean func_195267_b(int p_195267_1_, int p_195267_2_, int p_195267_3_) {
      switch(p_195267_1_) {
      case 257:
      case 335:
         if (!this.bookTitle.isEmpty()) {
            this.sendBookToServer(true);
            this.mc.displayGuiScreen((GuiScreen)null);
         }

         return true;
      case 259:
         if (!this.bookTitle.isEmpty()) {
            this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
            this.updateButtons();
         }

         return true;
      default:
         return false;
      }
   }

   /**
    * Returns the entire text of the current page as determined by currPage
    */
   private String pageGetCurrent() {
      return this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.size() ? this.bookPages.getString(this.currPage) : "";
   }

   /**
    * Sets the text of the current page as determined by currPage
    */
   private void pageSetCurrent(String p_146457_1_) {
      if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.size()) {
         this.bookPages.set(this.currPage, (INBTBase)(new NBTTagString(p_146457_1_)));
         this.bookIsModified = true;
      }

   }

   /**
    * Processes any text getting inserted into the current page, enforcing the page size limit
    */
   private void pageInsertIntoCurrent(String p_146459_1_) {
      String s = this.pageGetCurrent();
      String s1 = s + p_146459_1_;
      int i = this.fontRenderer.getWordWrappedHeight(s1 + "" + TextFormatting.BLACK + "_", 118);
      if (i <= 128 && s1.length() < 256) {
         this.pageSetCurrent(s1);
      }

   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(BOOK_GUI_TEXTURES);
      int i = (this.width - 192) / 2;
      int j = 2;
      this.drawTexturedModalRect(i, 2, 0, 0, 192, 192);
      if (this.bookGettingSigned) {
         String s = this.bookTitle;
         if (this.bookIsUnsigned) {
            if (this.ticks / 6 % 2 == 0) {
               s = s + "" + TextFormatting.BLACK + "_";
            } else {
               s = s + "" + TextFormatting.GRAY + "_";
            }
         }

         String s1 = I18n.format("book.editTitle");
         int k = this.fontRenderer.getStringWidth(s1);
         this.fontRenderer.drawString(s1, (float)(i + 36 + (116 - k) / 2), 34.0F, 0);
         int l = this.fontRenderer.getStringWidth(s);
         this.fontRenderer.drawString(s, (float)(i + 36 + (116 - l) / 2), 50.0F, 0);
         String s2 = I18n.format("book.byAuthor", this.editingPlayer.getName().getString());
         int i1 = this.fontRenderer.getStringWidth(s2);
         this.fontRenderer.drawString(TextFormatting.DARK_GRAY + s2, (float)(i + 36 + (116 - i1) / 2), 60.0F, 0);
         String s3 = I18n.format("book.finalizeWarning");
         this.fontRenderer.drawSplitString(s3, i + 36, 82, 116, 0);
      } else {
         String s4 = I18n.format("book.pageIndicator", this.currPage + 1, this.bookTotalPages);
         String s5 = "";
         if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.size()) {
            s5 = this.bookPages.getString(this.currPage);
         }

         if (this.bookIsUnsigned) {
            if (this.fontRenderer.getBidiFlag()) {
               s5 = s5 + "_";
            } else if (this.ticks / 6 % 2 == 0) {
               s5 = s5 + "" + TextFormatting.BLACK + "_";
            } else {
               s5 = s5 + "" + TextFormatting.GRAY + "_";
            }
         } else if (this.cachedPage != this.currPage) {
            if (ItemWrittenBook.validBookTagContents(this.book.getTag())) {
               try {
                  ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(s5);
                  this.cachedComponents = itextcomponent != null ? GuiUtilRenderComponents.splitText(itextcomponent, 116, this.fontRenderer, true, true) : null;
               } catch (JsonParseException var13) {
                  this.cachedComponents = null;
               }
            } else {
               this.cachedComponents = Lists.newArrayList((new TextComponentTranslation("book.invalid.tag")).applyTextStyle(TextFormatting.DARK_RED));
            }

            this.cachedPage = this.currPage;
         }

         int j1 = this.fontRenderer.getStringWidth(s4);
         this.fontRenderer.drawString(s4, (float)(i - j1 + 192 - 44), 18.0F, 0);
         if (this.cachedComponents == null) {
            this.fontRenderer.drawSplitString(s5, i + 36, 34, 116, 0);
         } else {
            int k1 = Math.min(128 / this.fontRenderer.FONT_HEIGHT, this.cachedComponents.size());

            for(int l1 = 0; l1 < k1; ++l1) {
               ITextComponent itextcomponent2 = this.cachedComponents.get(l1);
               this.fontRenderer.drawString(itextcomponent2.getFormattedText(), (float)(i + 36), (float)(34 + l1 * this.fontRenderer.FONT_HEIGHT), 0);
            }

            ITextComponent itextcomponent1 = this.func_195260_a((double)mouseX, (double)mouseY);
            if (itextcomponent1 != null) {
               this.handleComponentHover(itextcomponent1, mouseX, mouseY);
            }
         }
      }

      super.render(mouseX, mouseY, partialTicks);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ == 0) {
         ITextComponent itextcomponent = this.func_195260_a(p_mouseClicked_1_, p_mouseClicked_3_);
         if (itextcomponent != null && this.handleComponentClick(itextcomponent)) {
            return true;
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   /**
    * Executes the click event specified by the given chat component
    */
   public boolean handleComponentClick(ITextComponent component) {
      ClickEvent clickevent = component.getStyle().getClickEvent();
      if (clickevent == null) {
         return false;
      } else if (clickevent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
         String s = clickevent.getValue();

         try {
            int i = Integer.parseInt(s) - 1;
            if (i >= 0 && i < this.bookTotalPages && i != this.currPage) {
               this.currPage = i;
               this.updateButtons();
               return true;
            }
         } catch (Throwable var5) {
            ;
         }

         return false;
      } else {
         boolean flag = super.handleComponentClick(component);
         if (flag && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.mc.displayGuiScreen((GuiScreen)null);
         }

         return flag;
      }
   }

   @Nullable
   public ITextComponent func_195260_a(double p_195260_1_, double p_195260_3_) {
      if (this.cachedComponents == null) {
         return null;
      } else {
         int i = MathHelper.floor(p_195260_1_ - (double)((this.width - 192) / 2) - 36.0D);
         int j = MathHelper.floor(p_195260_3_ - 2.0D - 16.0D - 16.0D);
         if (i >= 0 && j >= 0) {
            int k = Math.min(128 / this.fontRenderer.FONT_HEIGHT, this.cachedComponents.size());
            if (i <= 116 && j < this.mc.fontRenderer.FONT_HEIGHT * k + k) {
               int l = j / this.mc.fontRenderer.FONT_HEIGHT;
               if (l >= 0 && l < this.cachedComponents.size()) {
                  ITextComponent itextcomponent = this.cachedComponents.get(l);
                  int i1 = 0;

                  for(ITextComponent itextcomponent1 : itextcomponent) {
                     if (itextcomponent1 instanceof TextComponentString) {
                        i1 += this.mc.fontRenderer.getStringWidth(itextcomponent1.getFormattedText());
                        if (i1 > i) {
                           return itextcomponent1;
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

   @OnlyIn(Dist.CLIENT)
   abstract static class NextPageButton extends GuiButton {
      private final boolean isForward;

      public NextPageButton(int buttonId, int x, int y, boolean isForwardIn) {
         super(buttonId, x, y, 23, 13, "");
         this.isForward = isForwardIn;
      }

      public void render(int mouseX, int mouseY, float partialTicks) {
         if (this.visible) {
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bindTexture(GuiScreenBook.BOOK_GUI_TEXTURES);
            int i = 0;
            int j = 192;
            if (flag) {
               i += 23;
            }

            if (!this.isForward) {
               j += 13;
            }

            this.drawTexturedModalRect(this.x, this.y, i, j, 23, 13);
         }
      }
   }
}