package net.minecraft.client.gui;

import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.network.play.client.CPacketUpdateCommandMinecart;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiEditCommandBlockMinecart extends GuiCommandBlockBase {
   private final CommandBlockBaseLogic commandBlockLogic;

   public GuiEditCommandBlockMinecart(CommandBlockBaseLogic p_i46595_1_) {
      this.commandBlockLogic = p_i46595_1_;
   }

   public CommandBlockBaseLogic getLogic() {
      return this.commandBlockLogic;
   }

   int func_195236_i() {
      return 150;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      super.initGui();
      this.field_195238_s = this.getLogic().shouldTrackOutput();
      this.updateTrackOutput();
      this.commandTextField.setText(this.getLogic().getCommand());
   }

   protected void func_195235_a(CommandBlockBaseLogic p_195235_1_) {
      if (p_195235_1_ instanceof EntityMinecartCommandBlock.MinecartCommandLogic) {
         EntityMinecartCommandBlock.MinecartCommandLogic entityminecartcommandblock$minecartcommandlogic = (EntityMinecartCommandBlock.MinecartCommandLogic)p_195235_1_;
         this.mc.getConnection().sendPacket(new CPacketUpdateCommandMinecart(entityminecartcommandblock$minecartcommandlogic.func_210167_g().getEntityId(), this.commandTextField.getText(), p_195235_1_.shouldTrackOutput()));
      }

   }
}