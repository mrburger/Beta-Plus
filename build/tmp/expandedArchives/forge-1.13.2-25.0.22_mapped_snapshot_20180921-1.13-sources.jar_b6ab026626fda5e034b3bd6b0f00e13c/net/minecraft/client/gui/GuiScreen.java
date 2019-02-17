package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class GuiScreen extends GuiEventHandler implements GuiYesNoCallback {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set<String> PROTOCOLS = Sets.newHashSet("http", "https");
   protected final List<IGuiEventListener> children = Lists.newArrayList();
   /** Reference to the Minecraft object. */
   public Minecraft mc;
   /** Holds a instance of RenderItem, used to draw the achievement icons on screen (is based on ItemStack) */
   protected ItemRenderer itemRender;
   /** The width of the screen object. */
   public int width;
   /** The height of the screen object. */
   public int height;
   /** A list of all the buttons in this container. */
   protected final List<GuiButton> buttons = Lists.newArrayList();
   /** A list of all the labels in this container. */
   protected final List<GuiLabel> labels = Lists.newArrayList();
   public boolean allowUserInput;
   /** The FontRenderer used by GuiScreen */
   protected FontRenderer fontRenderer;
   private URI clickedLinkURI;

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      for(int i = 0; i < this.buttons.size(); ++i) {
         this.buttons.get(i).render(mouseX, mouseY, partialTicks);
      }

      for(int j = 0; j < this.labels.size(); ++j) {
         this.labels.get(j).render(mouseX, mouseY, partialTicks);
      }

   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256 && this.allowCloseWithEscape()) {
         this.close();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   /**
    * Called when escape is pressed in this gui.
    *  
    * @return true if the GUI is allowed to close from this press.
    */
   public boolean allowCloseWithEscape() {
      return true;
   }

   public void close() {
      this.mc.displayGuiScreen((GuiScreen)null);
   }

   /**
    * Adds a control to this GUI's button list. Any type that subclasses button may be added (particularly, GuiSlider,
    * but not text fields).
    *  
    * @return The control passed in.
    */
   protected <T extends GuiButton> T addButton(T buttonIn) {
      this.buttons.add(buttonIn);
      this.children.add(buttonIn);
      return buttonIn;
   }

   protected void renderToolTip(ItemStack stack, int x, int y) {
      FontRenderer font = stack.getItem().getFontRenderer(stack);
      net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
      this.drawHoveringText(this.getItemToolTip(stack), x, y, (font == null ? fontRenderer : font));
      net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
   }

   public List<String> getItemToolTip(ItemStack p_191927_1_) {
      List<ITextComponent> list = p_191927_1_.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
      List<String> list1 = Lists.newArrayList();

      for(ITextComponent itextcomponent : list) {
         list1.add(itextcomponent.getFormattedText());
      }

      return list1;
   }

   /**
    * Draws the given text as a tooltip.
    */
   public void drawHoveringText(String text, int x, int y) {
      this.drawHoveringText(Arrays.asList(text), x, y);
   }

   /**
    * Draws a List of strings as a tooltip. Every entry is drawn on a seperate line.
    */
   public void drawHoveringText(List<String> textLines, int x, int y) {
      drawHoveringText(textLines, x, y, fontRenderer);
   }

   public void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font) {
      net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(textLines, x, y, width, height, -1, font);
      if (false && !textLines.isEmpty()) {
         GlStateManager.disableRescaleNormal();
         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableLighting();
         GlStateManager.disableDepthTest();
         int i = 0;

         for(String s : textLines) {
            int j = this.fontRenderer.getStringWidth(s);
            if (j > i) {
               i = j;
            }
         }

         int l1 = x + 12;
         int i2 = y - 12;
         int k = 8;
         if (textLines.size() > 1) {
            k += 2 + (textLines.size() - 1) * 10;
         }

         if (l1 + i > this.width) {
            l1 -= 28 + i;
         }

         if (i2 + k + 6 > this.height) {
            i2 = this.height - k - 6;
         }

         this.zLevel = 300.0F;
         this.itemRender.zLevel = 300.0F;
         int l = -267386864;
         this.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
         this.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
         this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
         this.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
         this.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
         int i1 = 1347420415;
         int j1 = 1344798847;
         this.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
         this.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
         this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
         this.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);

         for(int k1 = 0; k1 < textLines.size(); ++k1) {
            String s1 = textLines.get(k1);
            this.fontRenderer.drawStringWithShadow(s1, (float)l1, (float)i2, -1);
            if (k1 == 0) {
               i2 += 2;
            }

            i2 += 10;
         }

         this.zLevel = 0.0F;
         this.itemRender.zLevel = 0.0F;
         GlStateManager.enableLighting();
         GlStateManager.enableDepthTest();
         RenderHelper.enableStandardItemLighting();
         GlStateManager.enableRescaleNormal();
      }
   }

   /**
    * Draws the hover event specified by the given chat component
    */
   protected void handleComponentHover(ITextComponent component, int x, int y) {
      if (component != null && component.getStyle().getHoverEvent() != null) {
         HoverEvent hoverevent = component.getStyle().getHoverEvent();
         if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack itemstack = ItemStack.EMPTY;

            try {
               INBTBase inbtbase = JsonToNBT.getTagFromJson(hoverevent.getValue().getString());
               if (inbtbase instanceof NBTTagCompound) {
                  itemstack = ItemStack.read((NBTTagCompound)inbtbase);
               }
            } catch (CommandSyntaxException var10) {
               ;
            }

            if (itemstack.isEmpty()) {
               this.drawHoveringText(TextFormatting.RED + "Invalid Item!", x, y);
            } else {
               this.renderToolTip(itemstack, x, y);
            }
         } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            if (this.mc.gameSettings.advancedItemTooltips) {
               try {
                  NBTTagCompound nbttagcompound = JsonToNBT.getTagFromJson(hoverevent.getValue().getString());
                  List<String> list = Lists.newArrayList();
                  ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(nbttagcompound.getString("name"));
                  if (itextcomponent != null) {
                     list.add(itextcomponent.getFormattedText());
                  }

                  if (nbttagcompound.contains("type", 8)) {
                     String s = nbttagcompound.getString("type");
                     list.add("Type: " + s);
                  }

                  list.add(nbttagcompound.getString("id"));
                  this.drawHoveringText(list, x, y);
               } catch (CommandSyntaxException | JsonSyntaxException var9) {
                  this.drawHoveringText(TextFormatting.RED + "Invalid Entity!", x, y);
               }
            }
         } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT) {
            this.drawHoveringText(this.mc.fontRenderer.listFormattedStringToWidth(hoverevent.getValue().getFormattedText(), Math.max(this.width / 2, 200)), x, y);
         }

         GlStateManager.disableLighting();
      }
   }

   /**
    * Sets the text of the chat
    */
   protected void setText(String newChatText, boolean shouldOverwrite) {
   }

   /**
    * Executes the click event specified by the given chat component
    */
   public boolean handleComponentClick(ITextComponent component) {
      if (component == null) {
         return false;
      } else {
         ClickEvent clickevent = component.getStyle().getClickEvent();
         if (isShiftKeyDown()) {
            if (component.getStyle().getInsertion() != null) {
               this.setText(component.getStyle().getInsertion(), false);
            }
         } else if (clickevent != null) {
            if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
               if (!this.mc.gameSettings.chatLinks) {
                  return false;
               }

               try {
                  URI uri = new URI(clickevent.getValue());
                  String s = uri.getScheme();
                  if (s == null) {
                     throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                  }

                  if (!PROTOCOLS.contains(s.toLowerCase(Locale.ROOT))) {
                     throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase(Locale.ROOT));
                  }

                  if (this.mc.gameSettings.chatLinksPrompt) {
                     this.clickedLinkURI = uri;
                     this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, clickevent.getValue(), 31102009, false));
                  } else {
                     this.openWebLink(uri);
                  }
               } catch (URISyntaxException urisyntaxexception) {
                  LOGGER.error("Can't open url for {}", clickevent, urisyntaxexception);
               }
            } else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE) {
               URI uri1 = (new File(clickevent.getValue())).toURI();
               this.openWebLink(uri1);
            } else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
               this.setText(clickevent.getValue(), true);
            } else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
               this.sendChatMessage(clickevent.getValue(), false);
            } else {
               LOGGER.error("Don't know how to handle {}", (Object)clickevent);
            }

            return true;
         }

         return false;
      }
   }

   /**
    * Used to add chat messages to the client's GuiChat.
    */
   public void sendChatMessage(String msg) {
      this.sendChatMessage(msg, true);
   }

   public void sendChatMessage(String msg, boolean addToChat) {
      msg = net.minecraftforge.event.ForgeEventFactory.onClientSendMessage(msg);
      if (msg.isEmpty()) return;
      if (addToChat) {
         this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
      }
      //if (net.minecraftforge.client.ClientCommandHandler.instance.executeCommand(mc.player, msg) != 0) return; //Forge: TODO Client command re-write

      this.mc.player.sendChatMessage(msg);
   }

   /**
    * Causes the screen to lay out its subcomponents again. This is the equivalent of the Java call Container.validate()
    */
   public void setWorldAndResolution(Minecraft mc, int width, int height) {
      this.mc = mc;
      this.itemRender = mc.getItemRenderer();
      this.fontRenderer = mc.fontRenderer;
      this.width = width;
      this.height = height;
      java.util.function.Consumer<GuiButton> remove = (b) -> { buttons.remove(b); children.remove(b); };
      if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Pre(this, this.buttons, this::addButton, remove))) {
      this.buttons.clear();
      this.children.clear();
      this.initGui();
      }
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post(this, this.buttons, this::addButton, remove));
   }

   /**
    * Gets a mutable list of child listeners. For a {@link GuiListExtended}, this is a list of the entries of the list
    * (in the order they are displayed); for a {@link GuiScreen} this is the sub-controls.
    */
   public List<? extends IGuiEventListener> getChildren() {
      return this.children;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.children.addAll(this.labels);
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
   }

   /**
    * Called when the screen is unloaded. Used to disable keyboard repeat events
    */
   public void onGuiClosed() {
   }

   /**
    * Draws either a gradient over the background world (if there is a world), or a dirt screen if there is no world.
    *  
    * This method should usually be called before doing any other rendering; otherwise weird results will occur if there
    * is no world, and the world will not be tinted if there is.
    *  
    * Do not call after having already done other rendering, as it will draw over it.
    */
   public void drawDefaultBackground() {
      this.drawWorldBackground(0);
   }

   /**
    * Draws either a gradient over the background world (if there is a world), or a dirt screen if there is no world.
    *  
    * This method should usually be called before doing any other rendering; otherwise weird results will occur if there
    * is no world, and the world will not be tinted if there is.
    *  
    * Do not call after having already done other rendering, as it will draw over it.
    */
   public void drawWorldBackground(int tint) {
      if (this.mc.world != null) {
         this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
      } else {
         this.drawBackground(tint);
      }

   }

   /**
    * Draws a dirt background (using {@link #OPTIONS_BACKGROUND}).
    */
   public void drawBackground(int tint) {
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      this.mc.getTextureManager().bindTexture(OPTIONS_BACKGROUND);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.pos(0.0D, (double)this.height, 0.0D).tex(0.0D, (double)((float)this.height / 32.0F + (float)tint)).color(64, 64, 64, 255).endVertex();
      bufferbuilder.pos((double)this.width, (double)this.height, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)this.height / 32.0F + (float)tint)).color(64, 64, 64, 255).endVertex();
      bufferbuilder.pos((double)this.width, 0.0D, 0.0D).tex((double)((float)this.width / 32.0F), (double)tint).color(64, 64, 64, 255).endVertex();
      bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double)tint).color(64, 64, 64, 255).endVertex();
      tessellator.draw();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
   }

   /**
    * Returns true if this GUI should pause the game when it is displayed in single-player
    */
   public boolean doesGuiPauseGame() {
      return true;
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_2_ == 31102009) {
         if (p_confirmResult_1_) {
            this.openWebLink(this.clickedLinkURI);
         }

         this.clickedLinkURI = null;
         this.mc.displayGuiScreen(this);
      }

   }

   private void openWebLink(URI url) {
      Util.getOSType().openURI(url);
   }

   /**
    * Returns true if either windows ctrl key is down or if either mac meta key is down
    */
   public static boolean isCtrlKeyDown() {
      if (Minecraft.IS_RUNNING_ON_MAC) {
         return InputMappings.isKeyDown(343) || InputMappings.isKeyDown(347);
      } else {
         return InputMappings.isKeyDown(341) || InputMappings.isKeyDown(345);
      }
   }

   /**
    * Returns true if either shift key is down
    */
   public static boolean isShiftKeyDown() {
      return InputMappings.isKeyDown(340) || InputMappings.isKeyDown(344);
   }

   /**
    * Returns true if either alt key is down
    */
   public static boolean isAltKeyDown() {
      return InputMappings.isKeyDown(342) || InputMappings.isKeyDown(346);
   }

   public static boolean isKeyComboCtrlX(int keyID) {
      return keyID == 88 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static boolean isKeyComboCtrlV(int keyID) {
      return keyID == 86 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static boolean isKeyComboCtrlC(int keyID) {
      return keyID == 67 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static boolean isKeyComboCtrlA(int keyID) {
      return keyID == 65 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   /**
    * Called when the GUI is resized in order to update the world and the resolution
    */
   public void onResize(Minecraft mcIn, int w, int h) {
      this.setWorldAndResolution(mcIn, w, h);
   }

   public static void runOrMakeCrashReport(Runnable runnable, String description, String screenName) {
      try {
         runnable.run();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, description);
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
         crashreportcategory.addDetail("Screen name", () -> {
            return screenName;
         });
         throw new ReportedException(crashreport);
      }
   }
}