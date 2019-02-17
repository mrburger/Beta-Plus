package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderShulker;
import net.minecraft.client.renderer.entity.model.ModelShulker;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityShulkerBoxRenderer extends TileEntityRenderer<TileEntityShulkerBox> {
   private final ModelShulker model;

   public TileEntityShulkerBoxRenderer(ModelShulker modelIn) {
      this.model = modelIn;
   }

   public void render(TileEntityShulkerBox tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      EnumFacing enumfacing = EnumFacing.UP;
      if (tileEntityIn.hasWorld()) {
         IBlockState iblockstate = this.getWorld().getBlockState(tileEntityIn.getPos());
         if (iblockstate.getBlock() instanceof BlockShulkerBox) {
            enumfacing = iblockstate.get(BlockShulkerBox.FACING);
         }
      }

      GlStateManager.enableDepthTest();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      GlStateManager.disableCull();
      if (destroyStage >= 0) {
         this.bindTexture(DESTROY_STAGES[destroyStage]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 4.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         EnumDyeColor enumdyecolor = tileEntityIn.getColor();
         if (enumdyecolor == null) {
            this.bindTexture(RenderShulker.field_204402_a);
         } else {
            this.bindTexture(RenderShulker.SHULKER_ENDERGOLEM_TEXTURE[enumdyecolor.getId()]);
         }
      }

      GlStateManager.pushMatrix();
      GlStateManager.enableRescaleNormal();
      if (destroyStage < 0) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      GlStateManager.translatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
      GlStateManager.scalef(1.0F, -1.0F, -1.0F);
      GlStateManager.translatef(0.0F, 1.0F, 0.0F);
      float f = 0.9995F;
      GlStateManager.scalef(0.9995F, 0.9995F, 0.9995F);
      GlStateManager.translatef(0.0F, -1.0F, 0.0F);
      switch(enumfacing) {
      case DOWN:
         GlStateManager.translatef(0.0F, 2.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
      case UP:
      default:
         break;
      case NORTH:
         GlStateManager.translatef(0.0F, 1.0F, 1.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         break;
      case SOUTH:
         GlStateManager.translatef(0.0F, 1.0F, -1.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         break;
      case WEST:
         GlStateManager.translatef(-1.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
         break;
      case EAST:
         GlStateManager.translatef(1.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

      this.model.getBase().render(0.0625F);
      GlStateManager.translatef(0.0F, -tileEntityIn.getProgress(partialTicks) * 0.5F, 0.0F);
      GlStateManager.rotatef(270.0F * tileEntityIn.getProgress(partialTicks), 0.0F, 1.0F, 0.0F);
      this.model.getLid().render(0.0625F);
      GlStateManager.enableCull();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (destroyStage >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

   }
}