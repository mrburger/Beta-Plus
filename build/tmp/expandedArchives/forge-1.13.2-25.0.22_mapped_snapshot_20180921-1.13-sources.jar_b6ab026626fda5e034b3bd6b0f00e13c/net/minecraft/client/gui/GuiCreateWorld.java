package net.minecraft.client.gui;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SharedConstants;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class GuiCreateWorld extends GuiScreen {
   private final GuiScreen parentScreen;
   private GuiTextField worldNameField;
   private GuiTextField worldSeedField;
   private String saveDirName;
   private String gameMode = "survival";
   /** Used to save away the game mode when the current "debug" world type is chosen (forcing it to spectator mode) */
   private String savedGameMode;
   private boolean generateStructuresEnabled = true;
   /** If cheats are allowed */
   private boolean allowCheats;
   /**
    * User explicitly clicked "Allow Cheats" at some point
    * Prevents value changes due to changing game mode
    */
   private boolean allowCheatsWasSetByUser;
   private boolean bonusChestEnabled;
   /** Set to true when "hardcore" is the currently-selected gamemode */
   private boolean hardCoreMode;
   private boolean alreadyGenerated;
   private boolean inMoreWorldOptionsDisplay;
   private GuiButton field_195355_B;
   private GuiButton btnGameMode;
   private GuiButton btnMoreOptions;
   private GuiButton btnMapFeatures;
   private GuiButton btnBonusItems;
   private GuiButton btnMapType;
   private GuiButton btnAllowCommands;
   private GuiButton btnCustomizeType;
   private String gameModeDesc1;
   private String gameModeDesc2;
   private String worldSeed;
   private String worldName;
   private int selectedIndex;
   public NBTTagCompound chunkProviderSettingsJson = new NBTTagCompound();
   /** These filenames are known to be restricted on one or more OS's. */
   private static final String[] DISALLOWED_FILENAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

   public GuiCreateWorld(GuiScreen p_i46320_1_) {
      this.parentScreen = p_i46320_1_;
      this.worldSeed = "";
      this.worldName = I18n.format("selectWorld.newWorld");
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      this.worldNameField.tick();
      this.worldSeedField.tick();
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      this.field_195355_B = this.addButton(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("selectWorld.create")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCreateWorld.this.func_195352_j();
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCreateWorld.this.mc.displayGuiScreen(GuiCreateWorld.this.parentScreen);
         }
      });
      this.btnGameMode = this.addButton(new GuiButton(2, this.width / 2 - 75, 115, 150, 20, I18n.format("selectWorld.gameMode")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            if ("survival".equals(GuiCreateWorld.this.gameMode)) {
               if (!GuiCreateWorld.this.allowCheatsWasSetByUser) {
                  GuiCreateWorld.this.allowCheats = false;
               }

               GuiCreateWorld.this.hardCoreMode = false;
               GuiCreateWorld.this.gameMode = "hardcore";
               GuiCreateWorld.this.hardCoreMode = true;
               GuiCreateWorld.this.btnAllowCommands.enabled = false;
               GuiCreateWorld.this.btnBonusItems.enabled = false;
               GuiCreateWorld.this.updateDisplayState();
            } else if ("hardcore".equals(GuiCreateWorld.this.gameMode)) {
               if (!GuiCreateWorld.this.allowCheatsWasSetByUser) {
                  GuiCreateWorld.this.allowCheats = true;
               }

               GuiCreateWorld.this.hardCoreMode = false;
               GuiCreateWorld.this.gameMode = "creative";
               GuiCreateWorld.this.updateDisplayState();
               GuiCreateWorld.this.hardCoreMode = false;
               GuiCreateWorld.this.btnAllowCommands.enabled = true;
               GuiCreateWorld.this.btnBonusItems.enabled = true;
            } else {
               if (!GuiCreateWorld.this.allowCheatsWasSetByUser) {
                  GuiCreateWorld.this.allowCheats = false;
               }

               GuiCreateWorld.this.gameMode = "survival";
               GuiCreateWorld.this.updateDisplayState();
               GuiCreateWorld.this.btnAllowCommands.enabled = true;
               GuiCreateWorld.this.btnBonusItems.enabled = true;
               GuiCreateWorld.this.hardCoreMode = false;
            }

            GuiCreateWorld.this.updateDisplayState();
         }
      });
      this.btnMoreOptions = this.addButton(new GuiButton(3, this.width / 2 - 75, 187, 150, 20, I18n.format("selectWorld.moreWorldOptions")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCreateWorld.this.toggleMoreWorldOptions();
         }
      });
      this.btnMapFeatures = this.addButton(new GuiButton(4, this.width / 2 - 155, 100, 150, 20, I18n.format("selectWorld.mapFeatures")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCreateWorld.this.generateStructuresEnabled = !GuiCreateWorld.this.generateStructuresEnabled;
            GuiCreateWorld.this.updateDisplayState();
         }
      });
      this.btnMapFeatures.visible = false;
      this.btnBonusItems = this.addButton(new GuiButton(7, this.width / 2 + 5, 151, 150, 20, I18n.format("selectWorld.bonusItems")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCreateWorld.this.bonusChestEnabled = !GuiCreateWorld.this.bonusChestEnabled;
            GuiCreateWorld.this.updateDisplayState();
         }
      });
      this.btnBonusItems.visible = false;
      this.btnMapType = this.addButton(new GuiButton(5, this.width / 2 + 5, 100, 150, 20, I18n.format("selectWorld.mapType")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCreateWorld.this.selectedIndex++;
            if (GuiCreateWorld.this.selectedIndex >= WorldType.WORLD_TYPES.length) {
               GuiCreateWorld.this.selectedIndex = 0;
            }

            while(!GuiCreateWorld.this.canSelectCurWorldType()) {
               GuiCreateWorld.this.selectedIndex++;
               if (GuiCreateWorld.this.selectedIndex >= WorldType.WORLD_TYPES.length) {
                  GuiCreateWorld.this.selectedIndex = 0;
               }
            }

            GuiCreateWorld.this.chunkProviderSettingsJson = new NBTTagCompound();
            GuiCreateWorld.this.updateDisplayState();
            GuiCreateWorld.this.showMoreWorldOptions(GuiCreateWorld.this.inMoreWorldOptionsDisplay);
         }
      });
      this.btnMapType.visible = false;
      this.btnAllowCommands = this.addButton(new GuiButton(6, this.width / 2 - 155, 151, 150, 20, I18n.format("selectWorld.allowCommands")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCreateWorld.this.allowCheatsWasSetByUser = true;
            GuiCreateWorld.this.allowCheats = !GuiCreateWorld.this.allowCheats;
            GuiCreateWorld.this.updateDisplayState();
         }
      });
      this.btnAllowCommands.visible = false;
      this.btnCustomizeType = this.addButton(new GuiButton(8, this.width / 2 + 5, 120, 150, 20, I18n.format("selectWorld.customizeType")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            WorldType.WORLD_TYPES[GuiCreateWorld.this.selectedIndex].onCustomizeButton(GuiCreateWorld.this.mc, GuiCreateWorld.this);
         }
      });
      this.btnCustomizeType.visible = false;
      this.worldNameField = new GuiTextField(9, this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
      this.worldNameField.setFocused(true);
      this.worldNameField.setText(this.worldName);
      this.worldSeedField = new GuiTextField(10, this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
      this.worldSeedField.setText(this.worldSeed);
      this.showMoreWorldOptions(this.inMoreWorldOptionsDisplay);
      this.calcSaveDirName();
      this.updateDisplayState();
   }

   /**
    * Determine a save-directory name from the world name
    */
   private void calcSaveDirName() {
      this.saveDirName = this.worldNameField.getText().trim();

      for(char c0 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
         this.saveDirName = this.saveDirName.replace(c0, '_');
      }

      if (StringUtils.isEmpty(this.saveDirName)) {
         this.saveDirName = "World";
      }

      this.saveDirName = getUncollidingSaveDirName(this.mc.getSaveLoader(), this.saveDirName);
   }

   /**
    * Sets displayed GUI elements according to the current settings state
    */
   private void updateDisplayState() {
      this.btnGameMode.displayString = I18n.format("selectWorld.gameMode") + ": " + I18n.format("selectWorld.gameMode." + this.gameMode);
      this.gameModeDesc1 = I18n.format("selectWorld.gameMode." + this.gameMode + ".line1");
      this.gameModeDesc2 = I18n.format("selectWorld.gameMode." + this.gameMode + ".line2");
      this.btnMapFeatures.displayString = I18n.format("selectWorld.mapFeatures") + " ";
      if (this.generateStructuresEnabled) {
         this.btnMapFeatures.displayString = this.btnMapFeatures.displayString + I18n.format("options.on");
      } else {
         this.btnMapFeatures.displayString = this.btnMapFeatures.displayString + I18n.format("options.off");
      }

      this.btnBonusItems.displayString = I18n.format("selectWorld.bonusItems") + " ";
      if (this.bonusChestEnabled && !this.hardCoreMode) {
         this.btnBonusItems.displayString = this.btnBonusItems.displayString + I18n.format("options.on");
      } else {
         this.btnBonusItems.displayString = this.btnBonusItems.displayString + I18n.format("options.off");
      }

      this.btnMapType.displayString = I18n.format("selectWorld.mapType") + " " + I18n.format(WorldType.WORLD_TYPES[this.selectedIndex].getTranslationKey());
      this.btnAllowCommands.displayString = I18n.format("selectWorld.allowCommands") + " ";
      if (this.allowCheats && !this.hardCoreMode) {
         this.btnAllowCommands.displayString = this.btnAllowCommands.displayString + I18n.format("options.on");
      } else {
         this.btnAllowCommands.displayString = this.btnAllowCommands.displayString + I18n.format("options.off");
      }

   }

   /**
    * Ensures that a proposed directory name doesn't collide with existing names.
    * Returns the name, possibly modified to avoid collisions.
    */
   public static String getUncollidingSaveDirName(ISaveFormat saveLoader, String name) {
      name = name.replaceAll("[\\./\"]", "_");

      for(String s : DISALLOWED_FILENAMES) {
         if (name.equalsIgnoreCase(s)) {
            name = "_" + name + "_";
         }
      }

      while(saveLoader.getWorldInfo(name) != null) {
         name = name + "-";
      }

      return name;
   }

   /**
    * Called when the screen is unloaded. Used to disable keyboard repeat events
    */
   public void onGuiClosed() {
      this.mc.keyboardListener.enableRepeatEvents(false);
   }

   private void func_195352_j() {
      this.mc.displayGuiScreen((GuiScreen)null);
      if (!this.alreadyGenerated) {
         this.alreadyGenerated = true;
         long i = (new Random()).nextLong();
         String s = this.worldSeedField.getText();
         if (!StringUtils.isEmpty(s)) {
            try {
               long j = Long.parseLong(s);
               if (j != 0L) {
                  i = j;
               }
            } catch (NumberFormatException var6) {
               i = (long)s.hashCode();
            }
         }
         WorldType.WORLD_TYPES[this.selectedIndex].onGUICreateWorldPress();

         WorldSettings worldsettings = new WorldSettings(i, GameType.getByName(this.gameMode), this.generateStructuresEnabled, this.hardCoreMode, WorldType.WORLD_TYPES[this.selectedIndex]);
         worldsettings.setGeneratorOptions(Dynamic.convert(NBTDynamicOps.INSTANCE, JsonOps.INSTANCE, this.chunkProviderSettingsJson));
         if (this.bonusChestEnabled && !this.hardCoreMode) {
            worldsettings.enableBonusChest();
         }

         if (this.allowCheats && !this.hardCoreMode) {
            worldsettings.enableCommands();
         }

         this.mc.launchIntegratedServer(this.saveDirName, this.worldNameField.getText().trim(), worldsettings);
      }
   }

   /**
    * Returns whether the currently-selected world type is actually acceptable for selection
    * Used to hide the "debug" world type unless the shift key is depressed.
    */
   private boolean canSelectCurWorldType() {
      WorldType worldtype = WorldType.WORLD_TYPES[this.selectedIndex];
      if (worldtype != null && worldtype.canBeCreated()) {
         return worldtype == WorldType.DEBUG_ALL_BLOCK_STATES ? isShiftKeyDown() : true;
      } else {
         return false;
      }
   }

   /**
    * Toggles between initial world-creation display, and "more options" display.
    * Called when user clicks "More World Options..." or "Done" (same button, different labels depending on current
    * display).
    */
   private void toggleMoreWorldOptions() {
      this.showMoreWorldOptions(!this.inMoreWorldOptionsDisplay);
   }

   /**
    * Shows additional world-creation options if toggle is true, otherwise shows main world-creation elements
    */
   private void showMoreWorldOptions(boolean toggle) {
      this.inMoreWorldOptionsDisplay = toggle;
      if (WorldType.WORLD_TYPES[this.selectedIndex] == WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.btnGameMode.visible = !this.inMoreWorldOptionsDisplay;
         this.btnGameMode.enabled = false;
         if (this.savedGameMode == null) {
            this.savedGameMode = this.gameMode;
         }

         this.gameMode = "spectator";
         this.btnMapFeatures.visible = false;
         this.btnBonusItems.visible = false;
         this.btnMapType.visible = this.inMoreWorldOptionsDisplay;
         this.btnAllowCommands.visible = false;
         this.btnCustomizeType.visible = false;
      } else {
         this.btnGameMode.visible = !this.inMoreWorldOptionsDisplay;
         this.btnGameMode.enabled = true;
         if (this.savedGameMode != null) {
            this.gameMode = this.savedGameMode;
            this.savedGameMode = null;
         }

         this.btnMapFeatures.visible = this.inMoreWorldOptionsDisplay && WorldType.WORLD_TYPES[this.selectedIndex] != WorldType.CUSTOMIZED;
         this.btnBonusItems.visible = this.inMoreWorldOptionsDisplay;
         this.btnMapType.visible = this.inMoreWorldOptionsDisplay;
         this.btnAllowCommands.visible = this.inMoreWorldOptionsDisplay;
         this.btnCustomizeType.visible = this.inMoreWorldOptionsDisplay && WorldType.WORLD_TYPES[this.selectedIndex].hasCustomOptions();
      }

      this.updateDisplayState();
      if (this.inMoreWorldOptionsDisplay) {
         this.btnMoreOptions.displayString = I18n.format("gui.done");
      } else {
         this.btnMoreOptions.displayString = I18n.format("selectWorld.moreWorldOptions");
      }

   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      if (this.worldNameField.isFocused() && !this.inMoreWorldOptionsDisplay) {
         this.worldNameField.charTyped(p_charTyped_1_, p_charTyped_2_);
         this.worldName = this.worldNameField.getText();
         this.field_195355_B.enabled = !this.worldNameField.getText().isEmpty();
         this.calcSaveDirName();
         return true;
      } else if (this.worldSeedField.isFocused() && this.inMoreWorldOptionsDisplay) {
         this.worldSeedField.charTyped(p_charTyped_1_, p_charTyped_2_);
         this.worldSeed = this.worldSeedField.getText();
         return true;
      } else {
         return super.charTyped(p_charTyped_1_, p_charTyped_2_);
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.worldNameField.isFocused() && !this.inMoreWorldOptionsDisplay) {
         this.worldNameField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
         this.worldName = this.worldNameField.getText();
         this.field_195355_B.enabled = !this.worldNameField.getText().isEmpty();
         this.calcSaveDirName();
      } else if (this.worldSeedField.isFocused() && this.inMoreWorldOptionsDisplay) {
         this.worldSeedField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
         this.worldSeed = this.worldSeedField.getText();
      }

      if (this.field_195355_B.enabled && (p_keyPressed_1_ == 257 || p_keyPressed_1_ == 335)) {
         this.func_195352_j();
      }

      return true;
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         return this.inMoreWorldOptionsDisplay ? this.worldSeedField.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) : this.worldNameField.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.format("selectWorld.create"), this.width / 2, 20, -1);
      if (this.inMoreWorldOptionsDisplay) {
         this.drawString(this.fontRenderer, I18n.format("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
         this.drawString(this.fontRenderer, I18n.format("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
         if (this.btnMapFeatures.visible) {
            this.drawString(this.fontRenderer, I18n.format("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
         }

         if (this.btnAllowCommands.visible) {
            this.drawString(this.fontRenderer, I18n.format("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
         }

         this.worldSeedField.drawTextField(mouseX, mouseY, partialTicks);
         if (WorldType.WORLD_TYPES[this.selectedIndex].hasInfoNotice()) {
            this.fontRenderer.drawSplitString(I18n.format(WorldType.WORLD_TYPES[this.selectedIndex].getInfoTranslationKey()), this.btnMapType.x + 2, this.btnMapType.y + 22, this.btnMapType.getWidth(), 10526880);
         }
      } else {
         this.drawString(this.fontRenderer, I18n.format("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
         this.drawString(this.fontRenderer, I18n.format("selectWorld.resultFolder") + " " + this.saveDirName, this.width / 2 - 100, 85, -6250336);
         this.worldNameField.drawTextField(mouseX, mouseY, partialTicks);
         this.drawCenteredString(this.fontRenderer, this.gameModeDesc1, this.width / 2, 137, -6250336);
         this.drawCenteredString(this.fontRenderer, this.gameModeDesc2, this.width / 2, 149, -6250336);
      }

      super.render(mouseX, mouseY, partialTicks);
   }

   /**
    * Set the initial values of a new world to create, from the values from an existing world.
    *  
    * Called after construction when a user selects the "Recreate" button.
    */
   public void recreateFromExistingWorld(WorldInfo original) {
      this.worldName = I18n.format("selectWorld.newWorld.copyOf", original.getWorldName());
      this.worldSeed = original.getSeed() + "";
      WorldType worldtype = original.getTerrainType() == WorldType.CUSTOMIZED ? WorldType.DEFAULT : original.getTerrainType();
      this.selectedIndex = worldtype.getId();
      this.chunkProviderSettingsJson = original.getGeneratorOptions();
      this.generateStructuresEnabled = original.isMapFeaturesEnabled();
      this.allowCheats = original.areCommandsAllowed();
      if (original.isHardcore()) {
         this.gameMode = "hardcore";
      } else if (original.getGameType().isSurvivalOrAdventure()) {
         this.gameMode = "survival";
      } else if (original.getGameType().isCreative()) {
         this.gameMode = "creative";
      }

   }
}