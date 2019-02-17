package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CPacketUpdateCommandBlock;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCommandBlock extends GuiCommandBlockBase {
   private final TileEntityCommandBlock commandBlock;
   private GuiButton modeBtn;
   private GuiButton conditionalBtn;
   private GuiButton autoExecBtn;
   private TileEntityCommandBlock.Mode commandBlockMode = TileEntityCommandBlock.Mode.REDSTONE;
   private boolean conditional;
   private boolean automatic;

   public GuiCommandBlock(TileEntityCommandBlock commandBlockIn) {
      this.commandBlock = commandBlockIn;
   }

   CommandBlockBaseLogic getLogic() {
      return this.commandBlock.getCommandBlockLogic();
   }

   int func_195236_i() {
      return 135;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      super.initGui();
      this.modeBtn = this.addButton(new GuiButton(5, this.width / 2 - 50 - 100 - 4, 165, 100, 20, I18n.format("advMode.mode.sequence")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCommandBlock.this.nextMode();
            GuiCommandBlock.this.updateMode();
         }
      });
      this.conditionalBtn = this.addButton(new GuiButton(6, this.width / 2 - 50, 165, 100, 20, I18n.format("advMode.mode.unconditional")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCommandBlock.this.conditional = !GuiCommandBlock.this.conditional;
            GuiCommandBlock.this.updateConditional();
         }
      });
      this.autoExecBtn = this.addButton(new GuiButton(7, this.width / 2 + 50 + 4, 165, 100, 20, I18n.format("advMode.mode.redstoneTriggered")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCommandBlock.this.automatic = !GuiCommandBlock.this.automatic;
            GuiCommandBlock.this.updateAutoExec();
         }
      });
      this.doneButton.enabled = false;
      this.trackOutputButton.enabled = false;
      this.modeBtn.enabled = false;
      this.conditionalBtn.enabled = false;
      this.autoExecBtn.enabled = false;
   }

   public void updateGui() {
      CommandBlockBaseLogic commandblockbaselogic = this.commandBlock.getCommandBlockLogic();
      this.commandTextField.setText(commandblockbaselogic.getCommand());
      this.field_195238_s = commandblockbaselogic.shouldTrackOutput();
      this.commandBlockMode = this.commandBlock.getMode();
      this.conditional = this.commandBlock.isConditional();
      this.automatic = this.commandBlock.isAuto();
      this.updateTrackOutput();
      this.updateMode();
      this.updateConditional();
      this.updateAutoExec();
      this.doneButton.enabled = true;
      this.trackOutputButton.enabled = true;
      this.modeBtn.enabled = true;
      this.conditionalBtn.enabled = true;
      this.autoExecBtn.enabled = true;
   }

   /**
    * Called when the GUI is resized in order to update the world and the resolution
    */
   public void onResize(Minecraft mcIn, int w, int h) {
      super.onResize(mcIn, w, h);
      this.updateTrackOutput();
      this.updateMode();
      this.updateConditional();
      this.updateAutoExec();
      this.doneButton.enabled = true;
      this.trackOutputButton.enabled = true;
      this.modeBtn.enabled = true;
      this.conditionalBtn.enabled = true;
      this.autoExecBtn.enabled = true;
   }

   protected void func_195235_a(CommandBlockBaseLogic p_195235_1_) {
      this.mc.getConnection().sendPacket(new CPacketUpdateCommandBlock(new BlockPos(p_195235_1_.getPositionVector()), this.commandTextField.getText(), this.commandBlockMode, p_195235_1_.shouldTrackOutput(), this.conditional, this.automatic));
   }

   private void updateMode() {
      switch(this.commandBlockMode) {
      case SEQUENCE:
         this.modeBtn.displayString = I18n.format("advMode.mode.sequence");
         break;
      case AUTO:
         this.modeBtn.displayString = I18n.format("advMode.mode.auto");
         break;
      case REDSTONE:
         this.modeBtn.displayString = I18n.format("advMode.mode.redstone");
      }

   }

   private void nextMode() {
      switch(this.commandBlockMode) {
      case SEQUENCE:
         this.commandBlockMode = TileEntityCommandBlock.Mode.AUTO;
         break;
      case AUTO:
         this.commandBlockMode = TileEntityCommandBlock.Mode.REDSTONE;
         break;
      case REDSTONE:
         this.commandBlockMode = TileEntityCommandBlock.Mode.SEQUENCE;
      }

   }

   private void updateConditional() {
      if (this.conditional) {
         this.conditionalBtn.displayString = I18n.format("advMode.mode.conditional");
      } else {
         this.conditionalBtn.displayString = I18n.format("advMode.mode.unconditional");
      }

   }

   private void updateAutoExec() {
      if (this.automatic) {
         this.autoExecBtn.displayString = I18n.format("advMode.mode.autoexec.bat");
      } else {
         this.autoExecBtn.displayString = I18n.format("advMode.mode.redstoneTriggered");
      }

   }
}