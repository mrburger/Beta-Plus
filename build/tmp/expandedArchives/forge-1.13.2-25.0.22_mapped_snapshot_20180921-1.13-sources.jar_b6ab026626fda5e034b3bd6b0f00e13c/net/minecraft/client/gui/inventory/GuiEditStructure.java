package net.minecraft.client.gui.inventory;

import com.google.common.collect.Lists;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketUpdateStructureBlock;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiEditStructure extends GuiScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final TileEntityStructure tileStructure;
   private Mirror mirror = Mirror.NONE;
   private Rotation rotation = Rotation.NONE;
   private StructureMode mode = StructureMode.DATA;
   private boolean ignoreEntities;
   private boolean showAir;
   private boolean showBoundingBox;
   private GuiTextField nameEdit;
   private GuiTextField posXEdit;
   private GuiTextField posYEdit;
   private GuiTextField posZEdit;
   private GuiTextField sizeXEdit;
   private GuiTextField sizeYEdit;
   private GuiTextField sizeZEdit;
   private GuiTextField integrityEdit;
   private GuiTextField seedEdit;
   private GuiTextField dataEdit;
   private GuiButton doneButton;
   private GuiButton cancelButton;
   private GuiButton saveButton;
   private GuiButton loadButton;
   private GuiButton rotateZeroDegreesButton;
   private GuiButton rotateNinetyDegreesButton;
   private GuiButton rotate180DegreesButton;
   private GuiButton rotate270DegressButton;
   private GuiButton modeButton;
   private GuiButton detectSizeButton;
   private GuiButton showEntitiesButton;
   private GuiButton mirrorButton;
   private GuiButton showAirButton;
   private GuiButton showBoundingBoxButton;
   private final List<GuiTextField> tabOrder = Lists.newArrayList();
   private final DecimalFormat decimalFormat = new DecimalFormat("0.0###");

   public GuiEditStructure(TileEntityStructure p_i47142_1_) {
      this.tileStructure = p_i47142_1_;
      this.decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      this.nameEdit.tick();
      this.posXEdit.tick();
      this.posYEdit.tick();
      this.posZEdit.tick();
      this.sizeXEdit.tick();
      this.sizeYEdit.tick();
      this.sizeZEdit.tick();
      this.integrityEdit.tick();
      this.seedEdit.tick();
      this.dataEdit.tick();
   }

   private void func_195275_h() {
      if (this.func_210143_a(TileEntityStructure.UpdateCommand.UPDATE_DATA)) {
         this.mc.displayGuiScreen((GuiScreen)null);
      }

   }

   private void func_195272_i() {
      this.tileStructure.setMirror(this.mirror);
      this.tileStructure.setRotation(this.rotation);
      this.tileStructure.setMode(this.mode);
      this.tileStructure.setIgnoresEntities(this.ignoreEntities);
      this.tileStructure.setShowAir(this.showAir);
      this.tileStructure.setShowBoundingBox(this.showBoundingBox);
      this.mc.displayGuiScreen((GuiScreen)null);
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      this.doneButton = this.addButton(new GuiButton(0, this.width / 2 - 4 - 150, 210, 150, 20, I18n.format("gui.done")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiEditStructure.this.func_195275_h();
         }
      });
      this.cancelButton = this.addButton(new GuiButton(1, this.width / 2 + 4, 210, 150, 20, I18n.format("gui.cancel")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiEditStructure.this.func_195272_i();
         }
      });
      this.saveButton = this.addButton(new GuiButton(9, this.width / 2 + 4 + 100, 185, 50, 20, I18n.format("structure_block.button.save")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            if (GuiEditStructure.this.tileStructure.getMode() == StructureMode.SAVE) {
               GuiEditStructure.this.func_210143_a(TileEntityStructure.UpdateCommand.SAVE_AREA);
               GuiEditStructure.this.mc.displayGuiScreen((GuiScreen)null);
            }

         }
      });
      this.loadButton = this.addButton(new GuiButton(10, this.width / 2 + 4 + 100, 185, 50, 20, I18n.format("structure_block.button.load")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            if (GuiEditStructure.this.tileStructure.getMode() == StructureMode.LOAD) {
               GuiEditStructure.this.func_210143_a(TileEntityStructure.UpdateCommand.LOAD_AREA);
               GuiEditStructure.this.mc.displayGuiScreen((GuiScreen)null);
            }

         }
      });
      this.modeButton = this.addButton(new GuiButton(18, this.width / 2 - 4 - 150, 185, 50, 20, "MODE") {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiEditStructure.this.tileStructure.nextMode();
            GuiEditStructure.this.updateMode();
         }
      });
      this.detectSizeButton = this.addButton(new GuiButton(19, this.width / 2 + 4 + 100, 120, 50, 20, I18n.format("structure_block.button.detect_size")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            if (GuiEditStructure.this.tileStructure.getMode() == StructureMode.SAVE) {
               GuiEditStructure.this.func_210143_a(TileEntityStructure.UpdateCommand.SCAN_AREA);
               GuiEditStructure.this.mc.displayGuiScreen((GuiScreen)null);
            }

         }
      });
      this.showEntitiesButton = this.addButton(new GuiButton(20, this.width / 2 + 4 + 100, 160, 50, 20, "ENTITIES") {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiEditStructure.this.tileStructure.setIgnoresEntities(!GuiEditStructure.this.tileStructure.ignoresEntities());
            GuiEditStructure.this.updateEntitiesButton();
         }
      });
      this.mirrorButton = this.addButton(new GuiButton(21, this.width / 2 - 20, 185, 40, 20, "MIRROR") {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            switch(GuiEditStructure.this.tileStructure.getMirror()) {
            case NONE:
               GuiEditStructure.this.tileStructure.setMirror(Mirror.LEFT_RIGHT);
               break;
            case LEFT_RIGHT:
               GuiEditStructure.this.tileStructure.setMirror(Mirror.FRONT_BACK);
               break;
            case FRONT_BACK:
               GuiEditStructure.this.tileStructure.setMirror(Mirror.NONE);
            }

            GuiEditStructure.this.updateMirrorButton();
         }
      });
      this.showAirButton = this.addButton(new GuiButton(22, this.width / 2 + 4 + 100, 80, 50, 20, "SHOWAIR") {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiEditStructure.this.tileStructure.setShowAir(!GuiEditStructure.this.tileStructure.showsAir());
            GuiEditStructure.this.updateToggleAirButton();
         }
      });
      this.showBoundingBoxButton = this.addButton(new GuiButton(23, this.width / 2 + 4 + 100, 80, 50, 20, "SHOWBB") {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiEditStructure.this.tileStructure.setShowBoundingBox(!GuiEditStructure.this.tileStructure.showsBoundingBox());
            GuiEditStructure.this.updateToggleBoundingBox();
         }
      });
      this.rotateZeroDegreesButton = this.addButton(new GuiButton(11, this.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20, "0") {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiEditStructure.this.tileStructure.setRotation(Rotation.NONE);
            GuiEditStructure.this.updateDirectionButtons();
         }
      });
      this.rotateNinetyDegreesButton = this.addButton(new GuiButton(12, this.width / 2 - 1 - 40 - 20, 185, 40, 20, "90") {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiEditStructure.this.tileStructure.setRotation(Rotation.CLOCKWISE_90);
            GuiEditStructure.this.updateDirectionButtons();
         }
      });
      this.rotate180DegreesButton = this.addButton(new GuiButton(13, this.width / 2 + 1 + 20, 185, 40, 20, "180") {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiEditStructure.this.tileStructure.setRotation(Rotation.CLOCKWISE_180);
            GuiEditStructure.this.updateDirectionButtons();
         }
      });
      this.rotate270DegressButton = this.addButton(new GuiButton(14, this.width / 2 + 1 + 40 + 1 + 20, 185, 40, 20, "270") {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiEditStructure.this.tileStructure.setRotation(Rotation.COUNTERCLOCKWISE_90);
            GuiEditStructure.this.updateDirectionButtons();
         }
      });
      this.tabOrder.clear();
      this.nameEdit = new GuiTextField(2, this.fontRenderer, this.width / 2 - 152, 40, 300, 20) {
         public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
            return !GuiEditStructure.func_208402_b(this.getText(), p_charTyped_1_, this.getCursorPosition()) ? false : super.charTyped(p_charTyped_1_, p_charTyped_2_);
         }
      };
      this.nameEdit.setMaxStringLength(64);
      this.nameEdit.setText(this.tileStructure.getName());
      this.tabOrder.add(this.nameEdit);
      BlockPos blockpos = this.tileStructure.getPosition();
      this.posXEdit = new GuiTextField(3, this.fontRenderer, this.width / 2 - 152, 80, 80, 20);
      this.posXEdit.setMaxStringLength(15);
      this.posXEdit.setText(Integer.toString(blockpos.getX()));
      this.tabOrder.add(this.posXEdit);
      this.posYEdit = new GuiTextField(4, this.fontRenderer, this.width / 2 - 72, 80, 80, 20);
      this.posYEdit.setMaxStringLength(15);
      this.posYEdit.setText(Integer.toString(blockpos.getY()));
      this.tabOrder.add(this.posYEdit);
      this.posZEdit = new GuiTextField(5, this.fontRenderer, this.width / 2 + 8, 80, 80, 20);
      this.posZEdit.setMaxStringLength(15);
      this.posZEdit.setText(Integer.toString(blockpos.getZ()));
      this.tabOrder.add(this.posZEdit);
      BlockPos blockpos1 = this.tileStructure.getStructureSize();
      this.sizeXEdit = new GuiTextField(6, this.fontRenderer, this.width / 2 - 152, 120, 80, 20);
      this.sizeXEdit.setMaxStringLength(15);
      this.sizeXEdit.setText(Integer.toString(blockpos1.getX()));
      this.tabOrder.add(this.sizeXEdit);
      this.sizeYEdit = new GuiTextField(7, this.fontRenderer, this.width / 2 - 72, 120, 80, 20);
      this.sizeYEdit.setMaxStringLength(15);
      this.sizeYEdit.setText(Integer.toString(blockpos1.getY()));
      this.tabOrder.add(this.sizeYEdit);
      this.sizeZEdit = new GuiTextField(8, this.fontRenderer, this.width / 2 + 8, 120, 80, 20);
      this.sizeZEdit.setMaxStringLength(15);
      this.sizeZEdit.setText(Integer.toString(blockpos1.getZ()));
      this.tabOrder.add(this.sizeZEdit);
      this.integrityEdit = new GuiTextField(15, this.fontRenderer, this.width / 2 - 152, 120, 80, 20);
      this.integrityEdit.setMaxStringLength(15);
      this.integrityEdit.setText(this.decimalFormat.format((double)this.tileStructure.getIntegrity()));
      this.tabOrder.add(this.integrityEdit);
      this.seedEdit = new GuiTextField(16, this.fontRenderer, this.width / 2 - 72, 120, 80, 20);
      this.seedEdit.setMaxStringLength(31);
      this.seedEdit.setText(Long.toString(this.tileStructure.getSeed()));
      this.tabOrder.add(this.seedEdit);
      this.dataEdit = new GuiTextField(17, this.fontRenderer, this.width / 2 - 152, 120, 240, 20);
      this.dataEdit.setMaxStringLength(128);
      this.dataEdit.setText(this.tileStructure.getMetadata());
      this.tabOrder.add(this.dataEdit);
      this.children.addAll(this.tabOrder);
      this.mirror = this.tileStructure.getMirror();
      this.updateMirrorButton();
      this.rotation = this.tileStructure.getRotation();
      this.updateDirectionButtons();
      this.mode = this.tileStructure.getMode();
      this.updateMode();
      this.ignoreEntities = this.tileStructure.ignoresEntities();
      this.updateEntitiesButton();
      this.showAir = this.tileStructure.showsAir();
      this.updateToggleAirButton();
      this.showBoundingBox = this.tileStructure.showsBoundingBox();
      this.updateToggleBoundingBox();
      this.nameEdit.setFocused(true);
      this.setFocused(this.nameEdit);
   }

   /**
    * Called when the GUI is resized in order to update the world and the resolution
    */
   public void onResize(Minecraft mcIn, int w, int h) {
      String s = this.nameEdit.getText();
      String s1 = this.posXEdit.getText();
      String s2 = this.posYEdit.getText();
      String s3 = this.posZEdit.getText();
      String s4 = this.sizeXEdit.getText();
      String s5 = this.sizeYEdit.getText();
      String s6 = this.sizeZEdit.getText();
      String s7 = this.integrityEdit.getText();
      String s8 = this.seedEdit.getText();
      String s9 = this.dataEdit.getText();
      this.setWorldAndResolution(mcIn, w, h);
      this.nameEdit.setText(s);
      this.posXEdit.setText(s1);
      this.posYEdit.setText(s2);
      this.posZEdit.setText(s3);
      this.sizeXEdit.setText(s4);
      this.sizeYEdit.setText(s5);
      this.sizeZEdit.setText(s6);
      this.integrityEdit.setText(s7);
      this.seedEdit.setText(s8);
      this.dataEdit.setText(s9);
   }

   /**
    * Called when the screen is unloaded. Used to disable keyboard repeat events
    */
   public void onGuiClosed() {
      this.mc.keyboardListener.enableRepeatEvents(false);
   }

   private void updateEntitiesButton() {
      boolean flag = !this.tileStructure.ignoresEntities();
      if (flag) {
         this.showEntitiesButton.displayString = I18n.format("options.on");
      } else {
         this.showEntitiesButton.displayString = I18n.format("options.off");
      }

   }

   private void updateToggleAirButton() {
      boolean flag = this.tileStructure.showsAir();
      if (flag) {
         this.showAirButton.displayString = I18n.format("options.on");
      } else {
         this.showAirButton.displayString = I18n.format("options.off");
      }

   }

   private void updateToggleBoundingBox() {
      boolean flag = this.tileStructure.showsBoundingBox();
      if (flag) {
         this.showBoundingBoxButton.displayString = I18n.format("options.on");
      } else {
         this.showBoundingBoxButton.displayString = I18n.format("options.off");
      }

   }

   private void updateMirrorButton() {
      Mirror mirror = this.tileStructure.getMirror();
      switch(mirror) {
      case NONE:
         this.mirrorButton.displayString = "|";
         break;
      case LEFT_RIGHT:
         this.mirrorButton.displayString = "< >";
         break;
      case FRONT_BACK:
         this.mirrorButton.displayString = "^ v";
      }

   }

   private void updateDirectionButtons() {
      this.rotateZeroDegreesButton.enabled = true;
      this.rotateNinetyDegreesButton.enabled = true;
      this.rotate180DegreesButton.enabled = true;
      this.rotate270DegressButton.enabled = true;
      switch(this.tileStructure.getRotation()) {
      case NONE:
         this.rotateZeroDegreesButton.enabled = false;
         break;
      case CLOCKWISE_180:
         this.rotate180DegreesButton.enabled = false;
         break;
      case COUNTERCLOCKWISE_90:
         this.rotate270DegressButton.enabled = false;
         break;
      case CLOCKWISE_90:
         this.rotateNinetyDegreesButton.enabled = false;
      }

   }

   private void updateMode() {
      this.nameEdit.setFocused(false);
      this.posXEdit.setFocused(false);
      this.posYEdit.setFocused(false);
      this.posZEdit.setFocused(false);
      this.sizeXEdit.setFocused(false);
      this.sizeYEdit.setFocused(false);
      this.sizeZEdit.setFocused(false);
      this.integrityEdit.setFocused(false);
      this.seedEdit.setFocused(false);
      this.dataEdit.setFocused(false);
      this.nameEdit.setVisible(false);
      this.nameEdit.setFocused(false);
      this.posXEdit.setVisible(false);
      this.posYEdit.setVisible(false);
      this.posZEdit.setVisible(false);
      this.sizeXEdit.setVisible(false);
      this.sizeYEdit.setVisible(false);
      this.sizeZEdit.setVisible(false);
      this.integrityEdit.setVisible(false);
      this.seedEdit.setVisible(false);
      this.dataEdit.setVisible(false);
      this.saveButton.visible = false;
      this.loadButton.visible = false;
      this.detectSizeButton.visible = false;
      this.showEntitiesButton.visible = false;
      this.mirrorButton.visible = false;
      this.rotateZeroDegreesButton.visible = false;
      this.rotateNinetyDegreesButton.visible = false;
      this.rotate180DegreesButton.visible = false;
      this.rotate270DegressButton.visible = false;
      this.showAirButton.visible = false;
      this.showBoundingBoxButton.visible = false;
      switch(this.tileStructure.getMode()) {
      case SAVE:
         this.nameEdit.setVisible(true);
         this.posXEdit.setVisible(true);
         this.posYEdit.setVisible(true);
         this.posZEdit.setVisible(true);
         this.sizeXEdit.setVisible(true);
         this.sizeYEdit.setVisible(true);
         this.sizeZEdit.setVisible(true);
         this.saveButton.visible = true;
         this.detectSizeButton.visible = true;
         this.showEntitiesButton.visible = true;
         this.showAirButton.visible = true;
         break;
      case LOAD:
         this.nameEdit.setVisible(true);
         this.posXEdit.setVisible(true);
         this.posYEdit.setVisible(true);
         this.posZEdit.setVisible(true);
         this.integrityEdit.setVisible(true);
         this.seedEdit.setVisible(true);
         this.loadButton.visible = true;
         this.showEntitiesButton.visible = true;
         this.mirrorButton.visible = true;
         this.rotateZeroDegreesButton.visible = true;
         this.rotateNinetyDegreesButton.visible = true;
         this.rotate180DegreesButton.visible = true;
         this.rotate270DegressButton.visible = true;
         this.showBoundingBoxButton.visible = true;
         this.updateDirectionButtons();
         break;
      case CORNER:
         this.nameEdit.setVisible(true);
         break;
      case DATA:
         this.dataEdit.setVisible(true);
      }

      this.modeButton.displayString = I18n.format("structure_block.mode." + this.tileStructure.getMode().getName());
   }

   private boolean func_210143_a(TileEntityStructure.UpdateCommand p_210143_1_) {
      BlockPos blockpos = new BlockPos(this.parseCoordinate(this.posXEdit.getText()), this.parseCoordinate(this.posYEdit.getText()), this.parseCoordinate(this.posZEdit.getText()));
      BlockPos blockpos1 = new BlockPos(this.parseCoordinate(this.sizeXEdit.getText()), this.parseCoordinate(this.sizeYEdit.getText()), this.parseCoordinate(this.sizeZEdit.getText()));
      float f = this.parseIntegrity(this.integrityEdit.getText());
      long i = this.parseSeed(this.seedEdit.getText());
      this.mc.getConnection().sendPacket(new CPacketUpdateStructureBlock(this.tileStructure.getPos(), p_210143_1_, this.tileStructure.getMode(), this.nameEdit.getText(), blockpos, blockpos1, this.tileStructure.getMirror(), this.tileStructure.getRotation(), this.dataEdit.getText(), this.tileStructure.ignoresEntities(), this.tileStructure.showsAir(), this.tileStructure.showsBoundingBox(), f, i));
      return true;
   }

   private long parseSeed(String p_189821_1_) {
      try {
         return Long.valueOf(p_189821_1_);
      } catch (NumberFormatException var3) {
         return 0L;
      }
   }

   private float parseIntegrity(String p_189819_1_) {
      try {
         return Float.valueOf(p_189819_1_);
      } catch (NumberFormatException var3) {
         return 1.0F;
      }
   }

   private int parseCoordinate(String p_189817_1_) {
      try {
         return Integer.parseInt(p_189817_1_);
      } catch (NumberFormatException var3) {
         return 0;
      }
   }

   public void close() {
      this.func_195272_i();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         for(GuiTextField guitextfield : this.tabOrder) {
            guitextfield.setFocused(this.getFocused() == guitextfield);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ != 258) {
         if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
         } else {
            this.func_195275_h();
            return true;
         }
      } else {
         GuiTextField guitextfield = null;
         GuiTextField guitextfield1 = null;

         for(GuiTextField guitextfield2 : this.tabOrder) {
            if (guitextfield != null && guitextfield2.getVisible()) {
               guitextfield1 = guitextfield2;
               break;
            }

            if (guitextfield2.isFocused() && guitextfield2.getVisible()) {
               guitextfield = guitextfield2;
            }
         }

         if (guitextfield != null && guitextfield1 == null) {
            for(GuiTextField guitextfield3 : this.tabOrder) {
               if (guitextfield3.getVisible() && guitextfield3 != guitextfield) {
                  guitextfield1 = guitextfield3;
                  break;
               }
            }
         }

         if (guitextfield1 != null && guitextfield1 != guitextfield) {
            guitextfield.setFocused(false);
            guitextfield1.setFocused(true);
            this.setFocused(guitextfield1);
         }

         return true;
      }
   }

   private static boolean func_208402_b(String p_208402_0_, char p_208402_1_, int p_208402_2_) {
      int i = p_208402_0_.indexOf(58);
      int j = p_208402_0_.indexOf(47);
      if (p_208402_1_ == ':') {
         return (j == -1 || p_208402_2_ <= j) && i == -1;
      } else if (p_208402_1_ == '/') {
         return p_208402_2_ > i;
      } else {
         return p_208402_1_ == '_' || p_208402_1_ == '-' || p_208402_1_ >= 'a' && p_208402_1_ <= 'z' || p_208402_1_ >= '0' && p_208402_1_ <= '9' || p_208402_1_ == '.';
      }
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      StructureMode structuremode = this.tileStructure.getMode();
      this.drawCenteredString(this.fontRenderer, I18n.format(Blocks.STRUCTURE_BLOCK.getTranslationKey()), this.width / 2, 10, 16777215);
      if (structuremode != StructureMode.DATA) {
         this.drawString(this.fontRenderer, I18n.format("structure_block.structure_name"), this.width / 2 - 153, 30, 10526880);
         this.nameEdit.drawTextField(mouseX, mouseY, partialTicks);
      }

      if (structuremode == StructureMode.LOAD || structuremode == StructureMode.SAVE) {
         this.drawString(this.fontRenderer, I18n.format("structure_block.position"), this.width / 2 - 153, 70, 10526880);
         this.posXEdit.drawTextField(mouseX, mouseY, partialTicks);
         this.posYEdit.drawTextField(mouseX, mouseY, partialTicks);
         this.posZEdit.drawTextField(mouseX, mouseY, partialTicks);
         String s = I18n.format("structure_block.include_entities");
         int i = this.fontRenderer.getStringWidth(s);
         this.drawString(this.fontRenderer, s, this.width / 2 + 154 - i, 150, 10526880);
      }

      if (structuremode == StructureMode.SAVE) {
         this.drawString(this.fontRenderer, I18n.format("structure_block.size"), this.width / 2 - 153, 110, 10526880);
         this.sizeXEdit.drawTextField(mouseX, mouseY, partialTicks);
         this.sizeYEdit.drawTextField(mouseX, mouseY, partialTicks);
         this.sizeZEdit.drawTextField(mouseX, mouseY, partialTicks);
         String s2 = I18n.format("structure_block.detect_size");
         int k = this.fontRenderer.getStringWidth(s2);
         this.drawString(this.fontRenderer, s2, this.width / 2 + 154 - k, 110, 10526880);
         String s1 = I18n.format("structure_block.show_air");
         int j = this.fontRenderer.getStringWidth(s1);
         this.drawString(this.fontRenderer, s1, this.width / 2 + 154 - j, 70, 10526880);
      }

      if (structuremode == StructureMode.LOAD) {
         this.drawString(this.fontRenderer, I18n.format("structure_block.integrity"), this.width / 2 - 153, 110, 10526880);
         this.integrityEdit.drawTextField(mouseX, mouseY, partialTicks);
         this.seedEdit.drawTextField(mouseX, mouseY, partialTicks);
         String s3 = I18n.format("structure_block.show_boundingbox");
         int l = this.fontRenderer.getStringWidth(s3);
         this.drawString(this.fontRenderer, s3, this.width / 2 + 154 - l, 70, 10526880);
      }

      if (structuremode == StructureMode.DATA) {
         this.drawString(this.fontRenderer, I18n.format("structure_block.custom_data"), this.width / 2 - 153, 110, 10526880);
         this.dataEdit.drawTextField(mouseX, mouseY, partialTicks);
      }

      String s4 = "structure_block.mode_info." + structuremode.getName();
      this.drawString(this.fontRenderer, I18n.format(s4), this.width / 2 - 153, 174, 10526880);
      super.render(mouseX, mouseY, partialTicks);
   }

   /**
    * Returns true if this GUI should pause the game when it is displayed in single-player
    */
   public boolean doesGuiPauseGame() {
      return false;
   }
}