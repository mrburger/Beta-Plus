package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketUpdateBeacon;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiBeacon extends GuiContainer {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation BEACON_GUI_TEXTURES = new ResourceLocation("textures/gui/container/beacon.png");
   private final IInventory tileBeacon;
   private GuiBeacon.ConfirmButton beaconConfirmButton;
   private boolean buttonsNotDrawn;

   public GuiBeacon(InventoryPlayer playerInventory, IInventory tileBeaconIn) {
      super(new ContainerBeacon(playerInventory, tileBeaconIn));
      this.tileBeacon = tileBeaconIn;
      this.xSize = 230;
      this.ySize = 219;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      super.initGui();
      this.beaconConfirmButton = new GuiBeacon.ConfirmButton(-1, this.guiLeft + 164, this.guiTop + 107);
      this.addButton(this.beaconConfirmButton);
      this.addButton(new GuiBeacon.CancelButton(-2, this.guiLeft + 190, this.guiTop + 107));
      this.buttonsNotDrawn = true;
      this.beaconConfirmButton.enabled = false;
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      super.tick();
      int i = this.tileBeacon.getField(0);
      Potion potion = Potion.getPotionById(this.tileBeacon.getField(1));
      Potion potion1 = Potion.getPotionById(this.tileBeacon.getField(2));
      if (this.buttonsNotDrawn && i >= 0) {
         this.buttonsNotDrawn = false;
         int j = 100;

         for(int k = 0; k <= 2; ++k) {
            int l = TileEntityBeacon.EFFECTS_LIST[k].length;
            int i1 = l * 22 + (l - 1) * 2;

            for(int j1 = 0; j1 < l; ++j1) {
               Potion potion2 = TileEntityBeacon.EFFECTS_LIST[k][j1];
               GuiBeacon.PowerButton guibeacon$powerbutton = new GuiBeacon.PowerButton(j++, this.guiLeft + 76 + j1 * 24 - i1 / 2, this.guiTop + 22 + k * 25, potion2, k);
               this.addButton(guibeacon$powerbutton);
               if (k >= i) {
                  guibeacon$powerbutton.enabled = false;
               } else if (potion2 == potion) {
                  guibeacon$powerbutton.setSelected(true);
               }
            }
         }

         int k1 = 3;
         int l1 = TileEntityBeacon.EFFECTS_LIST[3].length + 1;
         int i2 = l1 * 22 + (l1 - 1) * 2;

         for(int j2 = 0; j2 < l1 - 1; ++j2) {
            Potion potion3 = TileEntityBeacon.EFFECTS_LIST[3][j2];
            GuiBeacon.PowerButton guibeacon$powerbutton2 = new GuiBeacon.PowerButton(j++, this.guiLeft + 167 + j2 * 24 - i2 / 2, this.guiTop + 47, potion3, 3);
            this.addButton(guibeacon$powerbutton2);
            if (3 >= i) {
               guibeacon$powerbutton2.enabled = false;
            } else if (potion3 == potion1) {
               guibeacon$powerbutton2.setSelected(true);
            }
         }

         if (potion != null) {
            GuiBeacon.PowerButton guibeacon$powerbutton1 = new GuiBeacon.PowerButton(j++, this.guiLeft + 167 + (l1 - 1) * 24 - i2 / 2, this.guiTop + 47, potion, 3);
            this.addButton(guibeacon$powerbutton1);
            if (3 >= i) {
               guibeacon$powerbutton1.enabled = false;
            } else if (potion == potion1) {
               guibeacon$powerbutton1.setSelected(true);
            }
         }
      }

      this.beaconConfirmButton.enabled = !this.tileBeacon.getStackInSlot(0).isEmpty() && potion != null;
   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      RenderHelper.disableStandardItemLighting();
      this.drawCenteredString(this.fontRenderer, I18n.format("block.minecraft.beacon.primary"), 62, 10, 14737632);
      this.drawCenteredString(this.fontRenderer, I18n.format("block.minecraft.beacon.secondary"), 169, 10, 14737632);

      for(GuiButton guibutton : this.buttons) {
         if (guibutton.isMouseOver()) {
            guibutton.drawButtonForegroundLayer(mouseX - this.guiLeft, mouseY - this.guiTop);
            break;
         }
      }

      RenderHelper.enableGUIStandardItemLighting();
   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(BEACON_GUI_TEXTURES);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      this.itemRender.zLevel = 100.0F;
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.EMERALD), i + 42, j + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.DIAMOND), i + 42 + 22, j + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
      this.itemRender.zLevel = 0.0F;
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.render(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   @OnlyIn(Dist.CLIENT)
   abstract static class Button extends GuiButton {
      private final ResourceLocation iconTexture;
      private final int iconX;
      private final int iconY;
      private boolean selected;

      protected Button(int buttonId, int x, int y, ResourceLocation iconTextureIn, int iconXIn, int iconYIn) {
         super(buttonId, x, y, 22, 22, "");
         this.iconTexture = iconTextureIn;
         this.iconX = iconXIn;
         this.iconY = iconYIn;
      }

      public void render(int mouseX, int mouseY, float partialTicks) {
         if (this.visible) {
            Minecraft.getInstance().getTextureManager().bindTexture(GuiBeacon.BEACON_GUI_TEXTURES);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = 219;
            int j = 0;
            if (!this.enabled) {
               j += this.width * 2;
            } else if (this.selected) {
               j += this.width * 1;
            } else if (this.hovered) {
               j += this.width * 3;
            }

            this.drawTexturedModalRect(this.x, this.y, j, 219, this.width, this.height);
            if (!GuiBeacon.BEACON_GUI_TEXTURES.equals(this.iconTexture)) {
               Minecraft.getInstance().getTextureManager().bindTexture(this.iconTexture);
            }

            this.drawTexturedModalRect(this.x + 2, this.y + 2, this.iconX, this.iconY, 18, 18);
         }
      }

      public boolean isSelected() {
         return this.selected;
      }

      public void setSelected(boolean selectedIn) {
         this.selected = selectedIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class CancelButton extends GuiBeacon.Button {
      public CancelButton(int buttonId, int x, int y) {
         super(buttonId, x, y, GuiBeacon.BEACON_GUI_TEXTURES, 112, 220);
      }

      /**
       * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
       */
      public void onClick(double mouseX, double mouseY) {
         GuiBeacon.this.mc.player.connection.sendPacket(new CPacketCloseWindow(GuiBeacon.this.mc.player.openContainer.windowId));
         GuiBeacon.this.mc.displayGuiScreen((GuiScreen)null);
      }

      public void drawButtonForegroundLayer(int mouseX, int mouseY) {
         GuiBeacon.this.drawHoveringText(I18n.format("gui.cancel"), mouseX, mouseY);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ConfirmButton extends GuiBeacon.Button {
      public ConfirmButton(int buttonId, int x, int y) {
         super(buttonId, x, y, GuiBeacon.BEACON_GUI_TEXTURES, 90, 220);
      }

      /**
       * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
       */
      public void onClick(double mouseX, double mouseY) {
         GuiBeacon.this.mc.getConnection().sendPacket(new CPacketUpdateBeacon(GuiBeacon.this.tileBeacon.getField(1), GuiBeacon.this.tileBeacon.getField(2)));
         GuiBeacon.this.mc.player.connection.sendPacket(new CPacketCloseWindow(GuiBeacon.this.mc.player.openContainer.windowId));
         GuiBeacon.this.mc.displayGuiScreen((GuiScreen)null);
      }

      public void drawButtonForegroundLayer(int mouseX, int mouseY) {
         GuiBeacon.this.drawHoveringText(I18n.format("gui.done"), mouseX, mouseY);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class PowerButton extends GuiBeacon.Button {
      private final Potion effect;
      private final int tier;

      public PowerButton(int buttonId, int x, int y, Potion effectIn, int tierIn) {
         super(buttonId, x, y, GuiContainer.INVENTORY_BACKGROUND, effectIn.getStatusIconIndex() % 12 * 18, 198 + effectIn.getStatusIconIndex() / 12 * 18);
         this.effect = effectIn;
         this.tier = tierIn;
      }

      /**
       * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
       */
      public void onClick(double mouseX, double mouseY) {
         if (!this.isSelected()) {
            int i = Potion.getIdFromPotion(this.effect);
            if (this.tier < 3) {
               GuiBeacon.this.tileBeacon.setField(1, i);
            } else {
               GuiBeacon.this.tileBeacon.setField(2, i);
            }

            GuiBeacon.this.buttons.clear();
            GuiBeacon.this.children.clear();
            GuiBeacon.this.initGui();
            GuiBeacon.this.tick();
         }
      }

      public void drawButtonForegroundLayer(int mouseX, int mouseY) {
         String s = I18n.format(this.effect.getName());
         if (this.tier >= 3 && this.effect != MobEffects.REGENERATION) {
            s = s + " II";
         }

         GuiBeacon.this.drawHoveringText(s, mouseX, mouseY);
      }
   }
}