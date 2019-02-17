package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.FlatGenSettings;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiFlatPresets extends GuiScreen {
   private static final List<GuiFlatPresets.LayerItem> FLAT_WORLD_PRESETS = Lists.newArrayList();
   /** The parent GUI */
   private final GuiCreateFlatWorld parentScreen;
   private String presetsTitle;
   private String presetsShare;
   private String listText;
   private GuiFlatPresets.ListSlot list;
   private GuiButton btnSelect;
   private GuiTextField export;

   public GuiFlatPresets(GuiCreateFlatWorld parent) {
      this.parentScreen = parent;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      this.presetsTitle = I18n.format("createWorld.customize.presets.title");
      this.presetsShare = I18n.format("createWorld.customize.presets.share");
      this.listText = I18n.format("createWorld.customize.presets.list");
      this.export = new GuiTextField(2, this.fontRenderer, 50, 40, this.width - 100, 20);
      this.list = new GuiFlatPresets.ListSlot();
      this.children.add(this.list);
      this.export.setMaxStringLength(1230);
      this.export.setText(this.parentScreen.getPreset());
      this.children.add(this.export);
      this.btnSelect = this.addButton(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("createWorld.customize.presets.select")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiFlatPresets.this.parentScreen.setPreset(GuiFlatPresets.this.export.getText());
            GuiFlatPresets.this.mc.displayGuiScreen(GuiFlatPresets.this.parentScreen);
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiFlatPresets.this.mc.displayGuiScreen(GuiFlatPresets.this.parentScreen);
         }
      });
      this.updateButtonValidity();
      this.setFocused(this.list);
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_) {
      return this.list.mouseScrolled(p_mouseScrolled_1_);
   }

   /**
    * Called when the GUI is resized in order to update the world and the resolution
    */
   public void onResize(Minecraft mcIn, int w, int h) {
      String s = this.export.getText();
      this.setWorldAndResolution(mcIn, w, h);
      this.export.setText(s);
   }

   /**
    * Called when the screen is unloaded. Used to disable keyboard repeat events
    */
   public void onGuiClosed() {
      this.mc.keyboardListener.enableRepeatEvents(false);
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.list.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRenderer, this.presetsTitle, this.width / 2, 8, 16777215);
      this.drawString(this.fontRenderer, this.presetsShare, 50, 30, 10526880);
      this.drawString(this.fontRenderer, this.listText, 50, 70, 10526880);
      this.export.drawTextField(mouseX, mouseY, partialTicks);
      super.render(mouseX, mouseY, partialTicks);
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      this.export.tick();
      super.tick();
   }

   public void updateButtonValidity() {
      this.btnSelect.enabled = this.hasValidSelection();
   }

   private boolean hasValidSelection() {
      return this.list.selected > -1 && this.list.selected < FLAT_WORLD_PRESETS.size() || this.export.getText().length() > 1;
   }

   private static void addPreset(String p_199709_0_, IItemProvider itemIn, Biome biomeIn, List<String> options, FlatLayerInfo... layers) {
      FlatGenSettings flatgensettings = ChunkGeneratorType.FLAT.createSettings();

      for(int i = layers.length - 1; i >= 0; --i) {
         flatgensettings.getFlatLayers().add(layers[i]);
      }

      flatgensettings.setBiome(biomeIn);
      flatgensettings.updateLayers();

      for(String s : options) {
         flatgensettings.getWorldFeatures().put(s, Maps.newHashMap());
      }

      FLAT_WORLD_PRESETS.add(new GuiFlatPresets.LayerItem(itemIn.asItem(), p_199709_0_, flatgensettings.toString()));
   }

   static {
      addPreset(I18n.format("createWorld.customize.preset.classic_flat"), Blocks.GRASS_BLOCK, Biomes.PLAINS, Arrays.asList("village"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(2, Blocks.DIRT), new FlatLayerInfo(1, Blocks.BEDROCK));
      addPreset(I18n.format("createWorld.customize.preset.tunnelers_dream"), Blocks.STONE, Biomes.MOUNTAINS, Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(230, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      addPreset(I18n.format("createWorld.customize.preset.water_world"), Items.WATER_BUCKET, Biomes.DEEP_OCEAN, Arrays.asList("biome_1", "oceanmonument"), new FlatLayerInfo(90, Blocks.WATER), new FlatLayerInfo(5, Blocks.SAND), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(5, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      addPreset(I18n.format("createWorld.customize.preset.overworld"), Blocks.GRASS, Biomes.PLAINS, Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      addPreset(I18n.format("createWorld.customize.preset.snowy_kingdom"), Blocks.SNOW, Biomes.SNOWY_TUNDRA, Arrays.asList("village", "biome_1"), new FlatLayerInfo(1, Blocks.SNOW), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      addPreset(I18n.format("createWorld.customize.preset.bottomless_pit"), Items.FEATHER, Biomes.PLAINS, Arrays.asList("village", "biome_1"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(2, Blocks.COBBLESTONE));
      addPreset(I18n.format("createWorld.customize.preset.desert"), Blocks.SAND, Biomes.DESERT, Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"), new FlatLayerInfo(8, Blocks.SAND), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      addPreset(I18n.format("createWorld.customize.preset.redstone_ready"), Items.REDSTONE, Biomes.DESERT, Collections.emptyList(), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      addPreset(I18n.format("createWorld.customize.preset.the_void"), Blocks.BARRIER, Biomes.THE_VOID, Arrays.asList("decoration"), new FlatLayerInfo(1, Blocks.AIR));
   }

   @OnlyIn(Dist.CLIENT)
   static class LayerItem {
      public Item icon;
      public String name;
      public String generatorInfo;

      public LayerItem(Item iconIn, String nameIn, String info) {
         this.icon = iconIn;
         this.name = nameIn;
         this.generatorInfo = info;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ListSlot extends GuiSlot {
      public int selected = -1;

      public ListSlot() {
         super(GuiFlatPresets.this.mc, GuiFlatPresets.this.width, GuiFlatPresets.this.height, 80, GuiFlatPresets.this.height - 37, 24);
      }

      private void func_195101_a(int p_195101_1_, int p_195101_2_, Item p_195101_3_) {
         this.blitSlotBg(p_195101_1_ + 1, p_195101_2_ + 1);
         GlStateManager.enableRescaleNormal();
         RenderHelper.enableGUIStandardItemLighting();
         GuiFlatPresets.this.itemRender.renderItemIntoGUI(new ItemStack(p_195101_3_), p_195101_1_ + 2, p_195101_2_ + 2);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableRescaleNormal();
      }

      private void blitSlotBg(int p_148173_1_, int p_148173_2_) {
         this.blitSlotIcon(p_148173_1_, p_148173_2_, 0, 0);
      }

      private void blitSlotIcon(int p_148171_1_, int p_148171_2_, int p_148171_3_, int p_148171_4_) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(Gui.STAT_ICONS);
         float f = 0.0078125F;
         float f1 = 0.0078125F;
         int i = 18;
         int j = 18;
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
         bufferbuilder.pos((double)(p_148171_1_ + 0), (double)(p_148171_2_ + 18), (double)this.zLevel).tex((double)((float)(p_148171_3_ + 0) * 0.0078125F), (double)((float)(p_148171_4_ + 18) * 0.0078125F)).endVertex();
         bufferbuilder.pos((double)(p_148171_1_ + 18), (double)(p_148171_2_ + 18), (double)this.zLevel).tex((double)((float)(p_148171_3_ + 18) * 0.0078125F), (double)((float)(p_148171_4_ + 18) * 0.0078125F)).endVertex();
         bufferbuilder.pos((double)(p_148171_1_ + 18), (double)(p_148171_2_ + 0), (double)this.zLevel).tex((double)((float)(p_148171_3_ + 18) * 0.0078125F), (double)((float)(p_148171_4_ + 0) * 0.0078125F)).endVertex();
         bufferbuilder.pos((double)(p_148171_1_ + 0), (double)(p_148171_2_ + 0), (double)this.zLevel).tex((double)((float)(p_148171_3_ + 0) * 0.0078125F), (double)((float)(p_148171_4_ + 0) * 0.0078125F)).endVertex();
         tessellator.draw();
      }

      protected int getSize() {
         return GuiFlatPresets.FLAT_WORLD_PRESETS.size();
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
         this.selected = index;
         GuiFlatPresets.this.updateButtonValidity();
         GuiFlatPresets.this.export.setText((GuiFlatPresets.FLAT_WORLD_PRESETS.get(GuiFlatPresets.this.list.selected)).generatorInfo);
         GuiFlatPresets.this.export.setCursorPositionZero();
         return true;
      }

      /**
       * Returns true if the element passed in is currently selected
       */
      protected boolean isSelected(int slotIndex) {
         return slotIndex == this.selected;
      }

      protected void drawBackground() {
      }

      protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
         GuiFlatPresets.LayerItem guiflatpresets$layeritem = GuiFlatPresets.FLAT_WORLD_PRESETS.get(slotIndex);
         this.func_195101_a(xPos, yPos, guiflatpresets$layeritem.icon);
         GuiFlatPresets.this.fontRenderer.drawString(guiflatpresets$layeritem.name, (float)(xPos + 18 + 5), (float)(yPos + 6), 16777215);
      }
   }
}