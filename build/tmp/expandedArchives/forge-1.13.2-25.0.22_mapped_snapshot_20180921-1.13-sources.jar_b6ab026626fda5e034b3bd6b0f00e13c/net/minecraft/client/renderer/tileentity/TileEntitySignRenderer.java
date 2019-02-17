package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelSign;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntitySignRenderer extends TileEntityRenderer<TileEntitySign> {
   private static final ResourceLocation SIGN_TEXTURE = new ResourceLocation("textures/entity/sign.png");
   /** The ModelSign instance for use in this renderer */
   private final ModelSign model = new ModelSign();

   public void render(TileEntitySign tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      IBlockState iblockstate = tileEntityIn.getBlockState();
      GlStateManager.pushMatrix();
      float f = 0.6666667F;
      if (iblockstate.getBlock() == Blocks.SIGN) {
         GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
         GlStateManager.rotatef(-((float)(iblockstate.get(BlockStandingSign.ROTATION) * 360) / 16.0F), 0.0F, 1.0F, 0.0F);
         this.model.getSignStick().showModel = true;
      } else {
         GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
         GlStateManager.rotatef(-iblockstate.get(BlockWallSign.FACING).getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(0.0F, -0.3125F, -0.4375F);
         this.model.getSignStick().showModel = false;
      }

      if (destroyStage >= 0) {
         this.bindTexture(DESTROY_STAGES[destroyStage]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 2.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         this.bindTexture(SIGN_TEXTURE);
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.pushMatrix();
      GlStateManager.scalef(0.6666667F, -0.6666667F, -0.6666667F);
      this.model.renderSign();
      GlStateManager.popMatrix();
      FontRenderer fontrenderer = this.getFontRenderer();
      float f1 = 0.010416667F;
      GlStateManager.translatef(0.0F, 0.33333334F, 0.046666667F);
      GlStateManager.scalef(0.010416667F, -0.010416667F, 0.010416667F);
      GlStateManager.normal3f(0.0F, 0.0F, -0.010416667F);
      GlStateManager.depthMask(false);
      if (destroyStage < 0) {
         for(int i = 0; i < 4; ++i) {
            String s = tileEntityIn.func_212364_a(i, (p_212491_1_) -> {
               List<ITextComponent> list = GuiUtilRenderComponents.splitText(p_212491_1_, 90, fontrenderer, false, true);
               return list.isEmpty() ? "" : list.get(0).getFormattedText();
            });
            if (s != null) {
               if (i == tileEntityIn.lineBeingEdited) {
                  s = "> " + s + " <";
               }

               fontrenderer.drawString(s, (float)(-fontrenderer.getStringWidth(s) / 2), (float)(i * 10 - tileEntityIn.signText.length * 5), 0);
            }
         }
      }

      GlStateManager.depthMask(true);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
      if (destroyStage >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

   }
}