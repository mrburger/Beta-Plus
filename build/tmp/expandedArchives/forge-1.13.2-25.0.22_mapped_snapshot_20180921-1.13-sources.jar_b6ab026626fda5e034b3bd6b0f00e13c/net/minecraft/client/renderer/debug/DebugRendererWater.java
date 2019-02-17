package net.minecraft.client.renderer.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugRendererWater implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private EntityPlayer player;
   private double xo;
   private double yo;
   private double zo;

   public DebugRendererWater(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void render(float partialTicks, long finishTimeNano) {
      this.player = this.minecraft.player;
      this.xo = this.player.lastTickPosX + (this.player.posX - this.player.lastTickPosX) * (double)partialTicks;
      this.yo = this.player.lastTickPosY + (this.player.posY - this.player.lastTickPosY) * (double)partialTicks;
      this.zo = this.player.lastTickPosZ + (this.player.posZ - this.player.lastTickPosZ) * (double)partialTicks;
      BlockPos blockpos = this.minecraft.player.getPosition();
      IWorldReaderBase iworldreaderbase = this.minecraft.player.world;
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
      GlStateManager.disableTexture2D();
      GlStateManager.lineWidth(6.0F);

      for(BlockPos blockpos1 : BlockPos.getAllInBox(blockpos.add(-10, -10, -10), blockpos.add(10, 10, 10))) {
         IFluidState ifluidstate = iworldreaderbase.getFluidState(blockpos1);
         if (ifluidstate.isTagged(FluidTags.WATER)) {
            double d0 = (double)((float)blockpos1.getY() + ifluidstate.getHeight());
            WorldRenderer.renderFilledBox((new AxisAlignedBB((double)((float)blockpos1.getX() + 0.01F), (double)((float)blockpos1.getY() + 0.01F), (double)((float)blockpos1.getZ() + 0.01F), (double)((float)blockpos1.getX() + 0.99F), d0, (double)((float)blockpos1.getZ() + 0.99F))).offset(-this.xo, -this.yo, -this.zo), 1.0F, 1.0F, 1.0F, 0.2F);
         }
      }

      for(BlockPos blockpos2 : BlockPos.getAllInBox(blockpos.add(-10, -10, -10), blockpos.add(10, 10, 10))) {
         IFluidState ifluidstate1 = iworldreaderbase.getFluidState(blockpos2);
         if (ifluidstate1.isTagged(FluidTags.WATER)) {
            DebugRenderer.renderDebugText(String.valueOf(ifluidstate1.getLevel()), (double)blockpos2.getX() + 0.5D, (double)((float)blockpos2.getY() + ifluidstate1.getHeight()), (double)blockpos2.getZ() + 0.5D, partialTicks, -16777216);
         }
      }

      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }
}