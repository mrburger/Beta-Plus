package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugRendererWorldGenAttempts implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   /**
    * Locations for each entry.
    *  
    * All other lists have the same length as this one, and are used to get additional information corresponding to the
    * element at the same index.
    */
   private final List<BlockPos> locations = Lists.newArrayList();
   /** Size to use for each entry, as the side length for a cube (diameter, not radius). */
   private final List<Float> sizes = Lists.newArrayList();
   /** Alpha value to use for each entry, from 0.0 to 1.0. */
   private final List<Float> alphas = Lists.newArrayList();
   /** Red value to use for each entry, from 0.0 to 1.0. */
   private final List<Float> reds = Lists.newArrayList();
   /** Green value to use for each entry, from 0.0 to 1.0. */
   private final List<Float> greens = Lists.newArrayList();
   /** Blue value to use for each entry, from 0.0 to 1.0. */
   private final List<Float> blues = Lists.newArrayList();

   public DebugRendererWorldGenAttempts(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void addAttempt(BlockPos pos, float size, float red, float green, float blue, float alpha) {
      this.locations.add(pos);
      this.sizes.add(size);
      this.alphas.add(alpha);
      this.reds.add(red);
      this.greens.add(green);
      this.blues.add(blue);
   }

   public void render(float partialTicks, long finishTimeNano) {
      EntityPlayer entityplayer = this.minecraft.player;
      IBlockReader iblockreader = this.minecraft.world;
      double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)partialTicks;
      double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)partialTicks;
      double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)partialTicks;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture2D();
      new BlockPos(entityplayer.posX, 0.0D, entityplayer.posZ);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

      for(int i = 0; i < this.locations.size(); ++i) {
         BlockPos blockpos = this.locations.get(i);
         Float f = this.sizes.get(i);
         float f1 = f / 2.0F;
         WorldRenderer.addChainedFilledBoxVertices(bufferbuilder, (double)((float)blockpos.getX() + 0.5F - f1) - d0, (double)((float)blockpos.getY() + 0.5F - f1) - d1, (double)((float)blockpos.getZ() + 0.5F - f1) - d2, (double)((float)blockpos.getX() + 0.5F + f1) - d0, (double)((float)blockpos.getY() + 0.5F + f1) - d1, (double)((float)blockpos.getZ() + 0.5F + f1) - d2, this.reds.get(i), this.greens.get(i), this.blues.get(i), this.alphas.get(i));
      }

      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.popMatrix();
   }
}