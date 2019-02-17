package net.minecraft.client.renderer.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IWorldReaderBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugRendererLight implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;

   public DebugRendererLight(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void render(float partialTicks, long finishTimeNano) {
      EntityPlayer entityplayer = this.minecraft.player;
      IWorldReaderBase iworldreaderbase = this.minecraft.world;
      double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)partialTicks;
      d0 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)partialTicks;
      d0 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)partialTicks;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture2D();
      BlockPos blockpos = new BlockPos(entityplayer.posX, entityplayer.posY, entityplayer.posZ);

      for(BlockPos blockpos1 : BlockPos.getAllInBox(blockpos.add(-5, -5, -5), blockpos.add(5, 5, 5))) {
         int i = iworldreaderbase.getLightFor(EnumLightType.SKY, blockpos1);
         float f = (float)(15 - i) / 15.0F * 0.5F + 0.16F;
         int j = MathHelper.hsvToRGB(f, 0.9F, 0.9F);
         if (i != 15) {
            DebugRenderer.renderDebugText(String.valueOf(i), (double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.25D, (double)blockpos1.getZ() + 0.5D, 1.0F, j);
         }
      }

      GlStateManager.enableTexture2D();
      GlStateManager.popMatrix();
   }
}