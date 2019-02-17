package net.minecraft.client.renderer.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockBannerWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBanner;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityBannerRenderer extends TileEntityRenderer<TileEntityBanner> {
   private final ModelBanner bannerModel = new ModelBanner();

   public void render(TileEntityBanner tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      float f = 0.6666667F;
      boolean flag = tileEntityIn.getWorld() == null;
      GlStateManager.pushMatrix();
      ModelRenderer modelrenderer = this.bannerModel.func_205057_b();
      long i;
      if (flag) {
         i = 0L;
         GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
         modelrenderer.showModel = true;
      } else {
         i = tileEntityIn.getWorld().getGameTime();
         IBlockState iblockstate = tileEntityIn.getBlockState();
         if (iblockstate.getBlock() instanceof BlockBanner) {
            GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
            GlStateManager.rotatef((float)(-iblockstate.get(BlockBanner.ROTATION) * 360) / 16.0F, 0.0F, 1.0F, 0.0F);
            modelrenderer.showModel = true;
         } else {
            GlStateManager.translatef((float)x + 0.5F, (float)y - 0.16666667F, (float)z + 0.5F);
            GlStateManager.rotatef(-iblockstate.get(BlockBannerWall.HORIZONTAL_FACING).getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
            GlStateManager.translatef(0.0F, -0.3125F, -0.4375F);
            modelrenderer.showModel = false;
         }
      }

      BlockPos blockpos = tileEntityIn.getPos();
      float f1 = (float)((long)(blockpos.getX() * 7 + blockpos.getY() * 9 + blockpos.getZ() * 13) + i) + partialTicks;
      this.bannerModel.func_205056_c().rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(f1 * (float)Math.PI * 0.02F)) * (float)Math.PI;
      GlStateManager.enableRescaleNormal();
      ResourceLocation resourcelocation = this.getBannerResourceLocation(tileEntityIn);
      if (resourcelocation != null) {
         this.bindTexture(resourcelocation);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.6666667F, -0.6666667F, -0.6666667F);
         this.bannerModel.renderBanner();
         GlStateManager.popMatrix();
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   @Nullable
   private ResourceLocation getBannerResourceLocation(TileEntityBanner bannerObj) {
      return BannerTextures.BANNER_DESIGNS.getResourceLocation(bannerObj.getPatternResourceLocation(), bannerObj.getPatternList(), bannerObj.getColorList());
   }
}