package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiChat extends GuiScreen {
   private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
   private String historyBuffer = "";
   /**
    * keeps position of which chat message you will select when you press up, (does not increase for duplicated messages
    * sent immediately after each other)
    */
   private int sentHistoryCursor = -1;
   /** Chat entry field */
   protected GuiTextField inputField;
   /** is the text that appears when you press the chat key and the input box appears pre-filled */
   private String defaultInputFieldText = "";
   protected final List<String> commandUsage = Lists.newArrayList();
   protected int commandUsagePosition;
   protected int commandUsageWidth;
   private ParseResults<ISuggestionProvider> currentParse;
   private CompletableFuture<Suggestions> pendingSuggestions;
   private GuiChat.SuggestionsList suggestions;
   private boolean hasEdits;
   private boolean field_212338_z;

   public GuiChat() {
   }

   public GuiChat(String defaultText) {
      this.defaultInputFieldText = defaultText;
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.inputField;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
      this.inputField = new GuiTextField(0, this.fontRenderer, 4, this.height - 12, this.width - 4, 12);
      this.inputField.setMaxStringLength(256);
      this.inputField.setEnableBackgroundDrawing(false);
      this.inputField.setFocused(true);
      this.inputField.setText(this.defaultInputFieldText);
      this.inputField.setCanLoseFocus(false);
      this.inputField.setTextFormatter(this::formatMessage);
      this.inputField.setTextAcceptHandler(this::acceptMessage);
      this.children.add(this.inputField);
      this.updateSuggestion();
   }

   /**
    * Called when the GUI is resized in order to update the world and the resolution
    */
   public void onResize(Minecraft mcIn, int w, int h) {
      String s = this.inputField.getText();
      this.setWorldAndResolution(mcIn, w, h);
      this.setChatLine(s);
      this.updateSuggestion();
   }

   /**
    * Called when the screen is unloaded. Used to disable keyboard repeat events
    */
   public void onGuiClosed() {
      this.mc.keyboardListener.enableRepeatEvents(false);
      this.mc.ingameGUI.getChatGUI().resetScroll();
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      this.inputField.tick();
   }

   private void acceptMessage(int p_195128_1_, String p_195128_2_) {
      String s = this.inputField.getText();
      this.hasEdits = !s.equals(this.defaultInputFieldText);
      this.updateSuggestion();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.suggestions != null && this.suggestions.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (p_keyPressed_1_ == 256) {
         this.mc.displayGuiScreen((GuiScreen)null);
         return true;
      } else if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         if (p_keyPressed_1_ == 265) {
            this.getSentHistory(-1);
            return true;
         } else if (p_keyPressed_1_ == 264) {
            this.getSentHistory(1);
            return true;
         } else if (p_keyPressed_1_ == 266) {
            this.mc.ingameGUI.getChatGUI().func_194813_a((double)(this.mc.ingameGUI.getChatGUI().getLineCount() - 1));
            return true;
         } else if (p_keyPressed_1_ == 267) {
            this.mc.ingameGUI.getChatGUI().func_194813_a((double)(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1));
            return true;
         } else {
            if (p_keyPressed_1_ == 258) {
               this.hasEdits = true;
               this.showSuggestions();
            }

            return this.inputField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
         }
      } else {
         String s = this.inputField.getText().trim();
         if (!s.isEmpty()) {
            this.sendChatMessage(s);
         }

         this.mc.displayGuiScreen((GuiScreen)null);
         return true;
      }
   }

   public void showSuggestions() {
      if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
         int i = 0;
         Suggestions suggestions = this.pendingSuggestions.join();
         if (!suggestions.getList().isEmpty()) {
            for(Suggestion suggestion : suggestions.getList()) {
               i = Math.max(i, this.fontRenderer.getStringWidth(suggestion.getText()));
            }

            int j = MathHelper.clamp(this.inputField.func_195611_j(suggestions.getRange().getStart()), 0, this.width - i);
            this.suggestions = new GuiChat.SuggestionsList(j, this.height - 12, i, suggestions);
         }
      }

   }

   private static int getLastWordIndex(String p_208603_0_) {
      if (Strings.isNullOrEmpty(p_208603_0_)) {
         return 0;
      } else {
         int i = 0;

         for(Matcher matcher = WHITESPACE_PATTERN.matcher(p_208603_0_); matcher.find(); i = matcher.end()) {
            ;
         }

         return i;
      }
   }

   private void updateSuggestion() {
      this.currentParse = null;
      if (!this.field_212338_z) {
         this.inputField.setSuggestion((String)null);
         this.suggestions = null;
      }

      this.commandUsage.clear();
      String s = this.inputField.getText();
      StringReader stringreader = new StringReader(s);
      if (stringreader.canRead() && stringreader.peek() == '/') {
         stringreader.skip();
         CommandDispatcher<ISuggestionProvider> commanddispatcher = this.mc.player.connection.func_195515_i();
         this.currentParse = commanddispatcher.parse(stringreader, this.mc.player.connection.getSuggestionProvider());
         if (this.suggestions == null || !this.field_212338_z) {
            StringReader stringreader1 = new StringReader(s.substring(0, Math.min(s.length(), this.inputField.getCursorPosition())));
            if (stringreader1.canRead() && stringreader1.peek() == '/') {
               stringreader1.skip();
               ParseResults<ISuggestionProvider> parseresults = commanddispatcher.parse(stringreader1, this.mc.player.connection.getSuggestionProvider());
               this.pendingSuggestions = commanddispatcher.getCompletionSuggestions(parseresults);
               this.pendingSuggestions.thenRun(() -> {
                  if (this.pendingSuggestions.isDone()) {
                     this.updateUsageInfo();
                  }
               });
            }
         }
      } else {
         int i = getLastWordIndex(s);
         Collection<String> collection = this.mc.player.connection.getSuggestionProvider().getPlayerNames();
         this.pendingSuggestions = ISuggestionProvider.suggest(collection, new SuggestionsBuilder(s, i));
      }

   }

   private void updateUsageInfo() {
      if (this.pendingSuggestions.join().isEmpty() && !this.currentParse.getExceptions().isEmpty() && this.inputField.getCursorPosition() == this.inputField.getText().length()) {
         int i = 0;

         for(Entry<CommandNode<ISuggestionProvider>, CommandSyntaxException> entry : this.currentParse.getExceptions().entrySet()) {
            CommandSyntaxException commandsyntaxexception = entry.getValue();
            if (commandsyntaxexception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
               ++i;
            } else {
               this.commandUsage.add(commandsyntaxexception.getMessage());
            }
         }

         if (i > 0) {
            this.commandUsage.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
         }
      }

      this.commandUsagePosition = 0;
      this.commandUsageWidth = this.width;
      if (this.commandUsage.isEmpty()) {
         this.fillNodeUsage(TextFormatting.GRAY);
      }

      this.suggestions = null;
      if (this.hasEdits && this.mc.gameSettings.autoSuggestions) {
         this.showSuggestions();
      }

   }

   private String formatMessage(String p_195130_1_, int p_195130_2_) {
      return this.currentParse != null ? func_212336_a(this.currentParse, p_195130_1_, p_195130_2_) : p_195130_1_;
   }

   public static String func_212336_a(ParseResults<ISuggestionProvider> p_212336_0_, String p_212336_1_, int p_212336_2_) {
      TextFormatting[] atextformatting = new TextFormatting[]{TextFormatting.AQUA, TextFormatting.YELLOW, TextFormatting.GREEN, TextFormatting.LIGHT_PURPLE, TextFormatting.GOLD};
      String s = TextFormatting.GRAY.toString();
      StringBuilder stringbuilder = new StringBuilder(s);
      int i = 0;
      int j = -1;
      CommandContextBuilder<ISuggestionProvider> commandcontextbuilder = p_212336_0_.getContext().getLastChild();

      for(ParsedArgument<ISuggestionProvider, ?> parsedargument : commandcontextbuilder.getArguments().values()) {
         ++j;
         if (j >= atextformatting.length) {
            j = 0;
         }

         int k = Math.max(parsedargument.getRange().getStart() - p_212336_2_, 0);
         if (k >= p_212336_1_.length()) {
            break;
         }

         int l = Math.min(parsedargument.getRange().getEnd() - p_212336_2_, p_212336_1_.length());
         if (l > 0) {
            stringbuilder.append((CharSequence)p_212336_1_, i, k);
            stringbuilder.append((Object)atextformatting[j]);
            stringbuilder.append((CharSequence)p_212336_1_, k, l);
            stringbuilder.append(s);
            i = l;
         }
      }

      if (p_212336_0_.getReader().canRead()) {
         int i1 = Math.max(p_212336_0_.getReader().getCursor() - p_212336_2_, 0);
         if (i1 < p_212336_1_.length()) {
            int j1 = Math.min(i1 + p_212336_0_.getReader().getRemainingLength(), p_212336_1_.length());
            stringbuilder.append((CharSequence)p_212336_1_, i, i1);
            stringbuilder.append((Object)TextFormatting.RED);
            stringbuilder.append((CharSequence)p_212336_1_, i1, j1);
            i = j1;
         }
      }

      stringbuilder.append((CharSequence)p_212336_1_, i, p_212336_1_.length());
      return stringbuilder.toString();
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_) {
      if (p_mouseScrolled_1_ > 1.0D) {
         p_mouseScrolled_1_ = 1.0D;
      }

      if (p_mouseScrolled_1_ < -1.0D) {
         p_mouseScrolled_1_ = -1.0D;
      }

      if (this.suggestions != null && this.suggestions.mouseScrolled(p_mouseScrolled_1_)) {
         return true;
      } else {
         if (!isShiftKeyDown()) {
            p_mouseScrolled_1_ *= 7.0D;
         }

         this.mc.ingameGUI.getChatGUI().func_194813_a(p_mouseScrolled_1_);
         return true;
      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.suggestions != null && this.suggestions.mouseClicked((int)p_mouseClicked_1_, (int)p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         if (p_mouseClicked_5_ == 0) {
            ITextComponent itextcomponent = this.mc.ingameGUI.getChatGUI().func_194817_a(p_mouseClicked_1_, p_mouseClicked_3_);
            if (itextcomponent != null && this.handleComponentClick(itextcomponent)) {
               return true;
            }
         }

         return this.inputField.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   /**
    * Sets the text of the chat
    */
   protected void setText(String newChatText, boolean shouldOverwrite) {
      if (shouldOverwrite) {
         this.inputField.setText(newChatText);
      } else {
         this.inputField.writeText(newChatText);
      }

   }

   /**
    * input is relative and is applied directly to the sentHistoryCursor so -1 is the previous message, 1 is the next
    * message from the current cursor position
    */
   public void getSentHistory(int msgPos) {
      int i = this.sentHistoryCursor + msgPos;
      int j = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
      i = MathHelper.clamp(i, 0, j);
      if (i != this.sentHistoryCursor) {
         if (i == j) {
            this.sentHistoryCursor = j;
            this.inputField.setText(this.historyBuffer);
         } else {
            if (this.sentHistoryCursor == j) {
               this.historyBuffer = this.inputField.getText();
            }

            this.inputField.setText(this.mc.ingameGUI.getChatGUI().getSentMessages().get(i));
            this.suggestions = null;
            this.sentHistoryCursor = i;
            this.hasEdits = false;
         }
      }
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
      this.inputField.drawTextField(mouseX, mouseY, partialTicks);
      if (this.suggestions != null) {
         this.suggestions.render(mouseX, mouseY);
      } else {
         int i = 0;

         for(String s : this.commandUsage) {
            drawRect(this.commandUsagePosition - 1, this.height - 14 - 13 - 12 * i, this.commandUsagePosition + this.commandUsageWidth + 1, this.height - 2 - 13 - 12 * i, -16777216);
            this.fontRenderer.drawStringWithShadow(s, (float)this.commandUsagePosition, (float)(this.height - 14 - 13 + 2 - 12 * i), -1);
            ++i;
         }
      }

      ITextComponent itextcomponent = this.mc.ingameGUI.getChatGUI().func_194817_a((double)mouseX, (double)mouseY);
      if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null) {
         this.handleComponentHover(itextcomponent, mouseX, mouseY);
      }

      super.render(mouseX, mouseY, partialTicks);
   }

   /**
    * Returns true if this GUI should pause the game when it is displayed in single-player
    */
   public boolean doesGuiPauseGame() {
      return false;
   }

   private void fillNodeUsage(TextFormatting p_195132_1_) {
      CommandContextBuilder<ISuggestionProvider> commandcontextbuilder = this.currentParse.getContext();
      CommandContextBuilder<ISuggestionProvider> commandcontextbuilder1 = commandcontextbuilder.getLastChild();
      if (!commandcontextbuilder1.getNodes().isEmpty()) {
         CommandNode<ISuggestionProvider> commandnode;
         int i;
         if (this.currentParse.getReader().canRead()) {
            Entry<CommandNode<ISuggestionProvider>, StringRange> entry = Iterables.getLast(commandcontextbuilder1.getNodes().entrySet());
            commandnode = entry.getKey();
            i = entry.getValue().getEnd() + 1;
         } else if (commandcontextbuilder1.getNodes().size() > 1) {
            Entry<CommandNode<ISuggestionProvider>, StringRange> entry2 = Iterables.get(commandcontextbuilder1.getNodes().entrySet(), commandcontextbuilder1.getNodes().size() - 2);
            commandnode = entry2.getKey();
            i = entry2.getValue().getEnd() + 1;
         } else {
            if (commandcontextbuilder == commandcontextbuilder1 || commandcontextbuilder1.getNodes().isEmpty()) {
               return;
            }

            Entry<CommandNode<ISuggestionProvider>, StringRange> entry3 = Iterables.getLast(commandcontextbuilder1.getNodes().entrySet());
            commandnode = entry3.getKey();
            i = entry3.getValue().getEnd() + 1;
         }

         Map<CommandNode<ISuggestionProvider>, String> map = this.mc.player.connection.func_195515_i().getSmartUsage(commandnode, this.mc.player.connection.getSuggestionProvider());
         List<String> list = Lists.newArrayList();
         int j = 0;

         for(Entry<CommandNode<ISuggestionProvider>, String> entry1 : map.entrySet()) {
            if (!(entry1.getKey() instanceof LiteralCommandNode)) {
               list.add(p_195132_1_ + (String)entry1.getValue());
               j = Math.max(j, this.fontRenderer.getStringWidth(entry1.getValue()));
            }
         }

         if (!list.isEmpty()) {
            this.commandUsage.addAll(list);
            this.commandUsagePosition = MathHelper.clamp(this.inputField.func_195611_j(i) + this.fontRenderer.getStringWidth(" "), 0, this.width - j);
            this.commandUsageWidth = j;
         }

      }
   }

   @Nullable
   private static String calculateSuggestionSuffix(String p_208602_0_, String p_208602_1_) {
      return p_208602_1_.startsWith(p_208602_0_) ? p_208602_1_.substring(p_208602_0_.length()) : null;
   }

   private void setChatLine(String p_208604_1_) {
      this.inputField.setText(p_208604_1_);
   }

   @OnlyIn(Dist.CLIENT)
   class SuggestionsList {
      private final Rectangle2d field_198505_b;
      private final Suggestions suggestions;
      private final String field_212466_d;
      private int field_198507_d;
      private int field_198508_e;
      private Vec2f field_198509_f = Vec2f.ZERO;
      private boolean field_199880_h;

      private SuggestionsList(int p_i47700_2_, int p_i47700_3_, int p_i47700_4_, Suggestions p_i47700_5_) {
         this.field_198505_b = new Rectangle2d(p_i47700_2_ - 1, p_i47700_3_ - 3 - Math.min(p_i47700_5_.getList().size(), 10) * 12, p_i47700_4_ + 1, Math.min(p_i47700_5_.getList().size(), 10) * 12);
         this.suggestions = p_i47700_5_;
         this.field_212466_d = GuiChat.this.inputField.getText();
         this.select(0);
      }

      public void render(int p_198500_1_, int p_198500_2_) {
         int i = Math.min(this.suggestions.getList().size(), 10);
         int j = -5592406;
         boolean flag = this.field_198507_d > 0;
         boolean flag1 = this.suggestions.getList().size() > this.field_198507_d + i;
         boolean flag2 = flag || flag1;
         boolean flag3 = this.field_198509_f.x != (float)p_198500_1_ || this.field_198509_f.y != (float)p_198500_2_;
         if (flag3) {
            this.field_198509_f = new Vec2f((float)p_198500_1_, (float)p_198500_2_);
         }

         if (flag2) {
            Gui.drawRect(this.field_198505_b.getX(), this.field_198505_b.getY() - 1, this.field_198505_b.getX() + this.field_198505_b.getWidth(), this.field_198505_b.getY(), -805306368);
            Gui.drawRect(this.field_198505_b.getX(), this.field_198505_b.getY() + this.field_198505_b.getHeight(), this.field_198505_b.getX() + this.field_198505_b.getWidth(), this.field_198505_b.getY() + this.field_198505_b.getHeight() + 1, -805306368);
            if (flag) {
               for(int k = 0; k < this.field_198505_b.getWidth(); ++k) {
                  if (k % 2 == 0) {
                     Gui.drawRect(this.field_198505_b.getX() + k, this.field_198505_b.getY() - 1, this.field_198505_b.getX() + k + 1, this.field_198505_b.getY(), -1);
                  }
               }
            }

            if (flag1) {
               for(int i1 = 0; i1 < this.field_198505_b.getWidth(); ++i1) {
                  if (i1 % 2 == 0) {
                     Gui.drawRect(this.field_198505_b.getX() + i1, this.field_198505_b.getY() + this.field_198505_b.getHeight(), this.field_198505_b.getX() + i1 + 1, this.field_198505_b.getY() + this.field_198505_b.getHeight() + 1, -1);
                  }
               }
            }
         }

         boolean flag4 = false;

         for(int l = 0; l < i; ++l) {
            Suggestion suggestion = this.suggestions.getList().get(l + this.field_198507_d);
            Gui.drawRect(this.field_198505_b.getX(), this.field_198505_b.getY() + 12 * l, this.field_198505_b.getX() + this.field_198505_b.getWidth(), this.field_198505_b.getY() + 12 * l + 12, -805306368);
            if (p_198500_1_ > this.field_198505_b.getX() && p_198500_1_ < this.field_198505_b.getX() + this.field_198505_b.getWidth() && p_198500_2_ > this.field_198505_b.getY() + 12 * l && p_198500_2_ < this.field_198505_b.getY() + 12 * l + 12) {
               if (flag3) {
                  this.select(l + this.field_198507_d);
               }

               flag4 = true;
            }

            GuiChat.this.fontRenderer.drawStringWithShadow(suggestion.getText(), (float)(this.field_198505_b.getX() + 1), (float)(this.field_198505_b.getY() + 2 + 12 * l), l + this.field_198507_d == this.field_198508_e ? -256 : -5592406);
         }

         if (flag4) {
            Message message = this.suggestions.getList().get(this.field_198508_e).getTooltip();
            if (message != null) {
               GuiChat.this.drawHoveringText(TextComponentUtils.toTextComponent(message).getFormattedText(), p_198500_1_, p_198500_2_);
            }
         }

      }

      public boolean mouseClicked(int p_198499_1_, int p_198499_2_, int p_198499_3_) {
         if (!this.field_198505_b.contains(p_198499_1_, p_198499_2_)) {
            return false;
         } else {
            int i = (p_198499_2_ - this.field_198505_b.getY()) / 12 + this.field_198507_d;
            if (i >= 0 && i < this.suggestions.getList().size()) {
               this.select(i);
               this.useSuggestion();
            }

            return true;
         }
      }

      public boolean mouseScrolled(double p_198498_1_) {
         int i = (int)(GuiChat.this.mc.mouseHelper.getMouseX() * (double)GuiChat.this.mc.mainWindow.getScaledWidth() / (double)GuiChat.this.mc.mainWindow.getWidth());
         int j = (int)(GuiChat.this.mc.mouseHelper.getMouseY() * (double)GuiChat.this.mc.mainWindow.getScaledHeight() / (double)GuiChat.this.mc.mainWindow.getHeight());
         if (this.field_198505_b.contains(i, j)) {
            this.field_198507_d = MathHelper.clamp((int)((double)this.field_198507_d - p_198498_1_), 0, Math.max(this.suggestions.getList().size() - 10, 0));
            return true;
         } else {
            return false;
         }
      }

      public boolean keyPressed(int p_198503_1_, int p_198503_2_, int p_198503_3_) {
         if (p_198503_1_ == 265) {
            this.cycle(-1);
            this.field_199880_h = false;
            return true;
         } else if (p_198503_1_ == 264) {
            this.cycle(1);
            this.field_199880_h = false;
            return true;
         } else if (p_198503_1_ == 258) {
            if (this.field_199880_h) {
               this.cycle(GuiScreen.isShiftKeyDown() ? -1 : 1);
            }

            this.useSuggestion();
            return true;
         } else if (p_198503_1_ == 256) {
            this.hide();
            return true;
         } else {
            return false;
         }
      }

      public void cycle(int p_199879_1_) {
         this.select(this.field_198508_e + p_199879_1_);
         int i = this.field_198507_d;
         int j = this.field_198507_d + 10 - 1;
         if (this.field_198508_e < i) {
            this.field_198507_d = MathHelper.clamp(this.field_198508_e, 0, Math.max(this.suggestions.getList().size() - 10, 0));
         } else if (this.field_198508_e > j) {
            this.field_198507_d = MathHelper.clamp(this.field_198508_e + 1 - 10, 0, Math.max(this.suggestions.getList().size() - 10, 0));
         }

      }

      public void select(int p_199675_1_) {
         this.field_198508_e = p_199675_1_;
         if (this.field_198508_e < 0) {
            this.field_198508_e += this.suggestions.getList().size();
         }

         if (this.field_198508_e >= this.suggestions.getList().size()) {
            this.field_198508_e -= this.suggestions.getList().size();
         }

         Suggestion suggestion = this.suggestions.getList().get(this.field_198508_e);
         GuiChat.this.inputField.setSuggestion(GuiChat.calculateSuggestionSuffix(GuiChat.this.inputField.getText(), suggestion.apply(this.field_212466_d)));
      }

      public void useSuggestion() {
         Suggestion suggestion = this.suggestions.getList().get(this.field_198508_e);
         GuiChat.this.field_212338_z = true;
         GuiChat.this.setChatLine(suggestion.apply(this.field_212466_d));
         int i = suggestion.getRange().getStart() + suggestion.getText().length();
         GuiChat.this.inputField.func_212422_f(i);
         GuiChat.this.inputField.setSelectionPos(i);
         this.select(this.field_198508_e);
         GuiChat.this.field_212338_z = false;
         this.field_199880_h = true;
      }

      public void hide() {
         GuiChat.this.suggestions = null;
      }
   }
}