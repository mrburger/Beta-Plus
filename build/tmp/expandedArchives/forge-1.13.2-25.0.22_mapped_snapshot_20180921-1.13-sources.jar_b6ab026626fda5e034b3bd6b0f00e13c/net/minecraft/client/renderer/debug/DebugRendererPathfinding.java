package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugRendererPathfinding implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private final Map<Integer, Path> pathMap = Maps.newHashMap();
   private final Map<Integer, Float> pathMaxDistance = Maps.newHashMap();
   private final Map<Integer, Long> creationMap = Maps.newHashMap();
   private EntityPlayer player;
   private double xo;
   private double yo;
   private double zo;

   public DebugRendererPathfinding(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void addPath(int eid, Path pathIn, float distance) {
      this.pathMap.put(eid, pathIn);
      this.creationMap.put(eid, Util.milliTime());
      this.pathMaxDistance.put(eid, distance);
   }

   public void render(float partialTicks, long finishTimeNano) {
      if (!this.pathMap.isEmpty()) {
         long i = Util.milliTime();
         this.player = this.minecraft.player;
         this.xo = this.player.lastTickPosX + (this.player.posX - this.player.lastTickPosX) * (double)partialTicks;
         this.yo = this.player.lastTickPosY + (this.player.posY - this.player.lastTickPosY) * (double)partialTicks;
         this.zo = this.player.lastTickPosZ + (this.player.posZ - this.player.lastTickPosZ) * (double)partialTicks;
         GlStateManager.pushMatrix();
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
         GlStateManager.disableTexture2D();
         GlStateManager.lineWidth(6.0F);

         for(Integer integer : this.pathMap.keySet()) {
            Path path = this.pathMap.get(integer);
            float f = this.pathMaxDistance.get(integer);
            this.renderPathLine(partialTicks, path);
            PathPoint pathpoint = path.getTarget();
            if (!(this.addDistanceToPlayer(pathpoint) > 40.0F)) {
               WorldRenderer.renderFilledBox((new AxisAlignedBB((double)((float)pathpoint.x + 0.25F), (double)((float)pathpoint.y + 0.25F), (double)pathpoint.z + 0.25D, (double)((float)pathpoint.x + 0.75F), (double)((float)pathpoint.y + 0.75F), (double)((float)pathpoint.z + 0.75F))).offset(-this.xo, -this.yo, -this.zo), 0.0F, 1.0F, 0.0F, 0.5F);

               for(int j = 0; j < path.getCurrentPathLength(); ++j) {
                  PathPoint pathpoint1 = path.getPathPointFromIndex(j);
                  if (!(this.addDistanceToPlayer(pathpoint1) > 40.0F)) {
                     float f1 = j == path.getCurrentPathIndex() ? 1.0F : 0.0F;
                     float f2 = j == path.getCurrentPathIndex() ? 0.0F : 1.0F;
                     WorldRenderer.renderFilledBox((new AxisAlignedBB((double)((float)pathpoint1.x + 0.5F - f), (double)((float)pathpoint1.y + 0.01F * (float)j), (double)((float)pathpoint1.z + 0.5F - f), (double)((float)pathpoint1.x + 0.5F + f), (double)((float)pathpoint1.y + 0.25F + 0.01F * (float)j), (double)((float)pathpoint1.z + 0.5F + f))).offset(-this.xo, -this.yo, -this.zo), f1, 0.0F, f2, 0.5F);
                  }
               }
            }
         }

         for(Integer integer1 : this.pathMap.keySet()) {
            Path path1 = this.pathMap.get(integer1);

            for(PathPoint pathpoint3 : path1.getClosedSet()) {
               if (!(this.addDistanceToPlayer(pathpoint3) > 40.0F)) {
                  DebugRenderer.renderDebugText(String.format("%s", pathpoint3.nodeType), (double)pathpoint3.x + 0.5D, (double)pathpoint3.y + 0.75D, (double)pathpoint3.z + 0.5D, partialTicks, -65536);
                  DebugRenderer.renderDebugText(String.format(Locale.ROOT, "%.2f", pathpoint3.costMalus), (double)pathpoint3.x + 0.5D, (double)pathpoint3.y + 0.25D, (double)pathpoint3.z + 0.5D, partialTicks, -65536);
               }
            }

            for(PathPoint pathpoint4 : path1.getOpenSet()) {
               if (!(this.addDistanceToPlayer(pathpoint4) > 40.0F)) {
                  DebugRenderer.renderDebugText(String.format("%s", pathpoint4.nodeType), (double)pathpoint4.x + 0.5D, (double)pathpoint4.y + 0.75D, (double)pathpoint4.z + 0.5D, partialTicks, -16776961);
                  DebugRenderer.renderDebugText(String.format(Locale.ROOT, "%.2f", pathpoint4.costMalus), (double)pathpoint4.x + 0.5D, (double)pathpoint4.y + 0.25D, (double)pathpoint4.z + 0.5D, partialTicks, -16776961);
               }
            }

            for(int k = 0; k < path1.getCurrentPathLength(); ++k) {
               PathPoint pathpoint2 = path1.getPathPointFromIndex(k);
               if (!(this.addDistanceToPlayer(pathpoint2) > 40.0F)) {
                  DebugRenderer.renderDebugText(String.format("%s", pathpoint2.nodeType), (double)pathpoint2.x + 0.5D, (double)pathpoint2.y + 0.75D, (double)pathpoint2.z + 0.5D, partialTicks, -1);
                  DebugRenderer.renderDebugText(String.format(Locale.ROOT, "%.2f", pathpoint2.costMalus), (double)pathpoint2.x + 0.5D, (double)pathpoint2.y + 0.25D, (double)pathpoint2.z + 0.5D, partialTicks, -1);
               }
            }
         }

         for(Integer integer2 : this.creationMap.keySet().toArray(new Integer[0])) {
            if (i - this.creationMap.get(integer2) > 20000L) {
               this.pathMap.remove(integer2);
               this.creationMap.remove(integer2);
            }
         }

         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
      }
   }

   public void renderPathLine(float finishTimeNano, Path pathIn) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

      for(int i = 0; i < pathIn.getCurrentPathLength(); ++i) {
         PathPoint pathpoint = pathIn.getPathPointFromIndex(i);
         if (!(this.addDistanceToPlayer(pathpoint) > 40.0F)) {
            float f = (float)i / (float)pathIn.getCurrentPathLength() * 0.33F;
            int j = i == 0 ? 0 : MathHelper.hsvToRGB(f, 0.9F, 0.9F);
            int k = j >> 16 & 255;
            int l = j >> 8 & 255;
            int i1 = j & 255;
            bufferbuilder.pos((double)pathpoint.x - this.xo + 0.5D, (double)pathpoint.y - this.yo + 0.5D, (double)pathpoint.z - this.zo + 0.5D).color(k, l, i1, 255).endVertex();
         }
      }

      tessellator.draw();
   }

   private float addDistanceToPlayer(PathPoint point) {
      return (float)(Math.abs((double)point.x - this.player.posX) + Math.abs((double)point.y - this.player.posY) + Math.abs((double)point.z - this.player.posZ));
   }
}