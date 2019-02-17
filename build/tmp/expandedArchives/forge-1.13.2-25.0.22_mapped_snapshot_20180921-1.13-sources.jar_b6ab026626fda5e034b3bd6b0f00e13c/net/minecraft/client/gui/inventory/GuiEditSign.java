package net.minecraft.client.gui.inventory;

import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiEditSign extends GuiScreen {
   /** Reference to the sign object. */
   private final TileEntitySign tileSign;
   /** Counts the number of screen updates. */
   private int updateCounter;
   /** The index of the line that is being edited. */
   private int editLine;
   /** "Done" button for the GUI. */
   private GuiButton doneBtn;

   public GuiEditSign(TileEntitySign teSign) {
      this.tileSign = teSign;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      this.doneBtn = this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, I18n.format("gui.done")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiEditSign.this.func_195269_h();
         }
      });
      this.tileSign.setEditable(false);
   }

   /**
    * Called when the screen is unloaded. Used to disable keyboard repeat events
    */
   public void onGuiClosed() {
      this.mc.keyboardListener.enableRepeatEvents(false);
      NetHandlerPlayClient nethandlerplayclient = this.mc.getConnection();
      if (nethandlerplayclient != null) {
         nethandlerplayclient.sendPacket(new CPacketUpdateSign(this.tileSign.getPos(), this.tileSign.func_212366_a(0), this.tileSign.func_212366_a(1), this.tileSign.func_212366_a(2), this.tileSign.func_212366_a(3)));
      }

      this.tileSign.setEditable(true);
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      ++this.updateCounter;
   }

   private void func_195269_h() {
      this.tileSign.markDirty();
      this.mc.displayGuiScreen((GuiScreen)null);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      String s = this.tileSign.func_212366_a(this.editLine).getString();
      if (SharedConstants.isAllowedCharacter(p_charTyped_1_) && this.fontRenderer.getStringWidth(s + p_charTyped_1_) <= 90) {
         s = s + p_charTyped_1_;
      }

      this.tileSign.func_212365_a(this.editLine, new TextComponentString(s));
      return true;
   }

   public void close() {
      this.func_195269_h();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 265) {
         this.editLine = this.editLine - 1 & 3;
         return true;
      } else if (p_keyPressed_1_ != 264 && p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         if (p_keyPressed_1_ == 259) {
            String s = this.tileSign.func_212366_a(this.editLine).getString();
            if (!s.isEmpty()) {
               s = s.substring(0, s.length() - 1);
               this.tileSign.func_212365_a(this.editLine, new TextComponentString(s));
            }

            return true;
         } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
         }
      } else {
         this.editLine = this.editLine + 1 & 3;
         return true;
      }
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.format("sign.edit"), this.width / 2, 40, 16777215);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)(this.width / 2), 0.0F, 50.0F);
      float f = 93.75F;
      GlStateManager.scalef(-93.75F, -93.75F, -93.75F);
      GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
      IBlockState iblockstate = this.tileSign.getBlockState();
      float f1;
      if (iblockstate.getBlock() == Blocks.SIGN) {
         f1 = (float)(iblockstate.get(BlockStandingSign.ROTATION) * 360) / 16.0F;
      } else {
         f1 = iblockstate.get(BlockWallSign.FACING).getHorizontalAngle();
      }

      GlStateManager.rotatef(f1, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(0.0F, -1.0625F, 0.0F);
      if (this.updateCounter / 6 % 2 == 0) {
         this.tileSign.lineBeingEdited = this.editLine;
      }

      TileEntityRendererDispatcher.instance.render(this.tileSign, -0.5D, -0.75D, -0.5D, 0.0F);
      this.tileSign.lineBeingEdited = -1;
      GlStateManager.popMatrix();
      super.render(mouseX, mouseY, partialTicks);
   }
}