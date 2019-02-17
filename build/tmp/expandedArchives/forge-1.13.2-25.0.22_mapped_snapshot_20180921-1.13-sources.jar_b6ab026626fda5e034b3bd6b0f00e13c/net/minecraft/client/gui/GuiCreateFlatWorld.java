package net.minecraft.client.gui;

import com.mojang.datafixers.Dynamic;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.FlatGenSettings;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCreateFlatWorld extends GuiScreen {
   private final GuiCreateWorld createWorldGui;
   private FlatGenSettings generatorInfo = FlatGenSettings.getDefaultFlatGenerator();
   /** The title given to the flat world currently in creation */
   private String flatWorldTitle;
   /** The text used to identify the material for a layer */
   private String materialText;
   /** The text used to identify the height of a layer */
   private String heightText;
   private GuiCreateFlatWorld.Details createFlatWorldListSlotGui;
   /** The (unused and permenantly hidden) add layer button */
   private GuiButton addLayerButton;
   /** The (unused and permenantly hidden) edit layer button */
   private GuiButton editLayerButton;
   /** The remove layer button */
   private GuiButton removeLayerButton;

   public GuiCreateFlatWorld(GuiCreateWorld parent, NBTTagCompound generatorOptions) {
      this.createWorldGui = parent;
      this.setGeneratorOptions(generatorOptions);
   }

   /**
    * Gets the superflat preset string for the generator; see
    * https://minecraft.gamepedia.com/Superflat#Preset_code_format
    */
   public String getPreset() {
      return this.generatorInfo.toString();
   }

   /**
    * Gets the NBT data for the generator (which has the same use as the preset)
    */
   public NBTTagCompound getGeneratorOptions() {
      return (NBTTagCompound)this.generatorInfo.func_210834_a(NBTDynamicOps.INSTANCE).getValue();
   }

   /**
    * Sets the generator config based off of the given preset.
    *  
    * Implementation note: {@link GuiFlatPresets} calls this method and not {@link #getGeneratorOptions} when the done
    * button is used.
    *  
    * @param preset The preset; see https://minecraft.gamepedia.com/Superflat#Preset_code_format
    */
   public void setPreset(String preset) {
      this.generatorInfo = FlatGenSettings.createFlatGeneratorFromString(preset);
   }

   /**
    * Sets the generator config based on the given NBT.
    */
   public void setGeneratorOptions(NBTTagCompound nbt) {
      this.generatorInfo = FlatGenSettings.createFlatGenerator(new Dynamic<>(NBTDynamicOps.INSTANCE, nbt));
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.flatWorldTitle = I18n.format("createWorld.customize.flat.title");
      this.materialText = I18n.format("createWorld.customize.flat.tile");
      this.heightText = I18n.format("createWorld.customize.flat.height");
      this.createFlatWorldListSlotGui = new GuiCreateFlatWorld.Details();
      this.children.add(this.createFlatWorldListSlotGui);
      this.addLayerButton = this.addButton(new GuiButton(2, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("createWorld.customize.flat.addLayer") + " (NYI)") {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCreateFlatWorld.this.generatorInfo.updateLayers();
            GuiCreateFlatWorld.this.onLayersChanged();
         }
      });
      this.editLayerButton = this.addButton(new GuiButton(3, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("createWorld.customize.flat.editLayer") + " (NYI)") {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCreateFlatWorld.this.generatorInfo.updateLayers();
            GuiCreateFlatWorld.this.onLayersChanged();
         }
      });
      this.removeLayerButton = this.addButton(new GuiButton(4, this.width / 2 - 155, this.height - 52, 150, 20, I18n.format("createWorld.customize.flat.removeLayer")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            if (GuiCreateFlatWorld.this.hasSelectedLayer()) {
               List<FlatLayerInfo> list = GuiCreateFlatWorld.this.generatorInfo.getFlatLayers();
               int i = list.size() - GuiCreateFlatWorld.this.createFlatWorldListSlotGui.selectedLayer - 1;
               list.remove(i);
               GuiCreateFlatWorld.this.createFlatWorldListSlotGui.selectedLayer = Math.min(GuiCreateFlatWorld.this.createFlatWorldListSlotGui.selectedLayer, list.size() - 1);
               GuiCreateFlatWorld.this.generatorInfo.updateLayers();
               GuiCreateFlatWorld.this.onLayersChanged();
            }
         }
      });
      this.addButton(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCreateFlatWorld.this.createWorldGui.chunkProviderSettingsJson = GuiCreateFlatWorld.this.getGeneratorOptions();
            GuiCreateFlatWorld.this.mc.displayGuiScreen(GuiCreateFlatWorld.this.createWorldGui);
            GuiCreateFlatWorld.this.generatorInfo.updateLayers();
            GuiCreateFlatWorld.this.onLayersChanged();
         }
      });
      this.addButton(new GuiButton(5, this.width / 2 + 5, this.height - 52, 150, 20, I18n.format("createWorld.customize.presets")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCreateFlatWorld.this.mc.displayGuiScreen(new GuiFlatPresets(GuiCreateFlatWorld.this));
            GuiCreateFlatWorld.this.generatorInfo.updateLayers();
            GuiCreateFlatWorld.this.onLayersChanged();
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCreateFlatWorld.this.mc.displayGuiScreen(GuiCreateFlatWorld.this.createWorldGui);
            GuiCreateFlatWorld.this.generatorInfo.updateLayers();
            GuiCreateFlatWorld.this.onLayersChanged();
         }
      });
      this.addLayerButton.visible = false;
      this.editLayerButton.visible = false;
      this.generatorInfo.updateLayers();
      this.onLayersChanged();
   }

   /**
    * Would update whether or not the edit and remove buttons are enabled, but is currently disabled and always disables
    * the buttons (which are invisible anyways)
    */
   public void onLayersChanged() {
      boolean flag = this.hasSelectedLayer();
      this.removeLayerButton.enabled = flag;
      this.editLayerButton.enabled = flag;
      this.editLayerButton.enabled = false;
      this.addLayerButton.enabled = false;
   }

   /**
    * Returns whether there is a valid layer selection
    */
   private boolean hasSelectedLayer() {
      return this.createFlatWorldListSlotGui.selectedLayer > -1 && this.createFlatWorldListSlotGui.selectedLayer < this.generatorInfo.getFlatLayers().size();
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.createFlatWorldListSlotGui;
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.createFlatWorldListSlotGui.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRenderer, this.flatWorldTitle, this.width / 2, 8, 16777215);
      int i = this.width / 2 - 92 - 16;
      this.drawString(this.fontRenderer, this.materialText, i, 32, 16777215);
      this.drawString(this.fontRenderer, this.heightText, i + 2 + 213 - this.fontRenderer.getStringWidth(this.heightText), 32, 16777215);
      super.render(mouseX, mouseY, partialTicks);
   }

   @OnlyIn(Dist.CLIENT)
   class Details extends GuiSlot {
      /**
       * The currently selected layer; -1 if there is no selection. This is in the order that it is displayed on-screen,
       * with the topmost layer having index 0.
       */
      public int selectedLayer = -1;

      public Details() {
         super(GuiCreateFlatWorld.this.mc, GuiCreateFlatWorld.this.width, GuiCreateFlatWorld.this.height, 43, GuiCreateFlatWorld.this.height - 60, 24);
      }

      /**
       * Draws an item with a background at the given coordinates. The item and its background are 20 pixels tall/wide
       * (though only the inner 18x18 is actually drawn on)
       */
      private void drawItem(int x, int z, ItemStack itemToDraw) {
         this.drawItemBackground(x + 1, z + 1);
         GlStateManager.enableRescaleNormal();
         if (!itemToDraw.isEmpty()) {
            RenderHelper.enableGUIStandardItemLighting();
            GuiCreateFlatWorld.this.itemRender.renderItemIntoGUI(itemToDraw, x + 2, z + 2);
            RenderHelper.disableStandardItemLighting();
         }

         GlStateManager.disableRescaleNormal();
      }

      /**
       * Draws the background icon for an item, with the indented texture from stats.png
       */
      private void drawItemBackground(int x, int y) {
         this.drawItemBackground(x, y, 0, 0);
      }

      /**
       * Draws the background icon for an item, using a texture from stats.png with the given coords
       */
      private void drawItemBackground(int x, int z, int textureX, int textureY) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(STAT_ICONS);
         float f = 0.0078125F;
         float f1 = 0.0078125F;
         int i = 18;
         int j = 18;
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
         bufferbuilder.pos((double)(x + 0), (double)(z + 18), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.0078125F), (double)((float)(textureY + 18) * 0.0078125F)).endVertex();
         bufferbuilder.pos((double)(x + 18), (double)(z + 18), (double)this.zLevel).tex((double)((float)(textureX + 18) * 0.0078125F), (double)((float)(textureY + 18) * 0.0078125F)).endVertex();
         bufferbuilder.pos((double)(x + 18), (double)(z + 0), (double)this.zLevel).tex((double)((float)(textureX + 18) * 0.0078125F), (double)((float)(textureY + 0) * 0.0078125F)).endVertex();
         bufferbuilder.pos((double)(x + 0), (double)(z + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.0078125F), (double)((float)(textureY + 0) * 0.0078125F)).endVertex();
         tessellator.draw();
      }

      protected int getSize() {
         return GuiCreateFlatWorld.this.generatorInfo.getFlatLayers().size();
      }

      /**
       * Called when the mouse is clicked onto an entry.
       *  
       * @return true if the entry did something with the click and it should be selected.
       *  
       * @param index Index of the entry
       * @param button The mouse button that was pressed.
       * @param mouseX The mouse X coordinate.
       * @param mouseY The mouse Y coordinate.
       */
      protected boolean mouseClicked(int index, int button, double mouseX, double mouseY) {
         this.selectedLayer = index;
         GuiCreateFlatWorld.this.onLayersChanged();
         return true;
      }

      /**
       * Returns true if the element passed in is currently selected
       */
      protected boolean isSelected(int slotIndex) {
         return slotIndex == this.selectedLayer;
      }

      protected void drawBackground() {
      }

      protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
         FlatLayerInfo flatlayerinfo = GuiCreateFlatWorld.this.generatorInfo.getFlatLayers().get(GuiCreateFlatWorld.this.generatorInfo.getFlatLayers().size() - slotIndex - 1);
         IBlockState iblockstate = flatlayerinfo.getLayerMaterial();
         Block block = iblockstate.getBlock();
         Item item = block.asItem();
         if (item == Items.AIR) {
            if (block == Blocks.WATER) {
               item = Items.WATER_BUCKET;
            } else if (block == Blocks.LAVA) {
               item = Items.LAVA_BUCKET;
            }
         }

         ItemStack itemstack = new ItemStack(item);
         String s = item.getDisplayName(itemstack).getFormattedText();
         this.drawItem(xPos, yPos, itemstack);
         GuiCreateFlatWorld.this.fontRenderer.drawString(s, (float)(xPos + 18 + 5), (float)(yPos + 3), 16777215);
         String s1;
         if (slotIndex == 0) {
            s1 = I18n.format("createWorld.customize.flat.layer.top", flatlayerinfo.getLayerCount());
         } else if (slotIndex == GuiCreateFlatWorld.this.generatorInfo.getFlatLayers().size() - 1) {
            s1 = I18n.format("createWorld.customize.flat.layer.bottom", flatlayerinfo.getLayerCount());
         } else {
            s1 = I18n.format("createWorld.customize.flat.layer", flatlayerinfo.getLayerCount());
         }

         GuiCreateFlatWorld.this.fontRenderer.drawString(s1, (float)(xPos + 2 + 213 - GuiCreateFlatWorld.this.fontRenderer.getStringWidth(s1)), (float)(yPos + 3), 16777215);
      }

      protected int getScrollBarX() {
         return this.width - 70;
      }
   }
}