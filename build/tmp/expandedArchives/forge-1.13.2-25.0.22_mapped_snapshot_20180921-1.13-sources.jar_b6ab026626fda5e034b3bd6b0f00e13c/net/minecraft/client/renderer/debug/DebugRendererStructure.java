package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugRendererStructure implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   /**
    * The main box for each structure in the dimension, keyed by the dimension. Those boxes are rendered in white. Note
    * that Map<String, MutableBoundingBox> is effectively a Set<MutableBoundingBox>, but MutableBoundingBox doesn't
    * define equals/hashcode, only toString.
    *  
    * To reiterate: the outer map is from dimension, and the inner one is basically a list of main boxes, one for each
    * structure.
    */
   private final Map<Integer, Map<String, MutableBoundingBox>> mainBoxes = Maps.newHashMap();
   /**
    * The sub-boxes for all structures, keyed by the dimension. The color of these boxes depends on the value of the
    * flag for it. As with before, the key to the sub-map is the tostring version of its value; it basically acts as a
    * list. Note that the sub-box is _not_ associated back with the main box in any way.
    */
   private final Map<Integer, Map<String, MutableBoundingBox>> subBoxes = Maps.newHashMap();
   /**
    * A flag for each sub-box, keyed by the dimension. The inner map is keyed by the toString of the box, from {@link
    * #subBoxes}. If the flag is true, the box is green; otherwise, it's blue.
    */
   private final Map<Integer, Map<String, Boolean>> subBoxFlags = Maps.newHashMap();

   public DebugRendererStructure(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void render(float partialTicks, long finishTimeNano) {
      EntityPlayer entityplayer = this.minecraft.player;
      IWorld iworld = this.minecraft.world;
      int i = iworld.getWorldInfo().getDimension();
      double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)partialTicks;
      double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)partialTicks;
      double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)partialTicks;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture2D();
      GlStateManager.disableDepthTest();
      BlockPos blockpos = new BlockPos(entityplayer.posX, 0.0D, entityplayer.posZ);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
      GlStateManager.lineWidth(1.0F);
      if (this.mainBoxes.containsKey(i)) {
         for(MutableBoundingBox mutableboundingbox : this.mainBoxes.get(i).values()) {
            if (blockpos.getDistance(mutableboundingbox.minX, mutableboundingbox.minY, mutableboundingbox.minZ) < 500.0D) {
               WorldRenderer.drawBoundingBox(bufferbuilder, (double)mutableboundingbox.minX - d0, (double)mutableboundingbox.minY - d1, (double)mutableboundingbox.minZ - d2, (double)(mutableboundingbox.maxX + 1) - d0, (double)(mutableboundingbox.maxY + 1) - d1, (double)(mutableboundingbox.maxZ + 1) - d2, 1.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      }

      if (this.subBoxes.containsKey(i)) {
         for(Entry<String, MutableBoundingBox> entry : this.subBoxes.get(i).entrySet()) {
            String s = entry.getKey();
            MutableBoundingBox mutableboundingbox1 = entry.getValue();
            Boolean obool = this.subBoxFlags.get(i).get(s);
            if (blockpos.getDistance(mutableboundingbox1.minX, mutableboundingbox1.minY, mutableboundingbox1.minZ) < 500.0D) {
               if (obool) {
                  WorldRenderer.drawBoundingBox(bufferbuilder, (double)mutableboundingbox1.minX - d0, (double)mutableboundingbox1.minY - d1, (double)mutableboundingbox1.minZ - d2, (double)(mutableboundingbox1.maxX + 1) - d0, (double)(mutableboundingbox1.maxY + 1) - d1, (double)(mutableboundingbox1.maxZ + 1) - d2, 0.0F, 1.0F, 0.0F, 1.0F);
               } else {
                  WorldRenderer.drawBoundingBox(bufferbuilder, (double)mutableboundingbox1.minX - d0, (double)mutableboundingbox1.minY - d1, (double)mutableboundingbox1.minZ - d2, (double)(mutableboundingbox1.maxX + 1) - d0, (double)(mutableboundingbox1.maxY + 1) - d1, (double)(mutableboundingbox1.maxZ + 1) - d2, 0.0F, 0.0F, 1.0F, 1.0F);
               }
            }
         }
      }

      tessellator.draw();
      GlStateManager.enableDepthTest();
      GlStateManager.enableTexture2D();
      GlStateManager.popMatrix();
   }

   public void addStructure(MutableBoundingBox mainBox, List<MutableBoundingBox> structureSubBoxes, List<Boolean> structureSubBoxFlags, int dimensionIn) {
      if (!this.mainBoxes.containsKey(dimensionIn)) {
         this.mainBoxes.put(dimensionIn, Maps.newHashMap());
      }

      if (!this.subBoxes.containsKey(dimensionIn)) {
         this.subBoxes.put(dimensionIn, Maps.newHashMap());
         this.subBoxFlags.put(dimensionIn, Maps.newHashMap());
      }

      this.mainBoxes.get(dimensionIn).put(mainBox.toString(), mainBox);

      for(int i = 0; i < structureSubBoxes.size(); ++i) {
         MutableBoundingBox mutableboundingbox = structureSubBoxes.get(i);
         Boolean obool = structureSubBoxFlags.get(i);
         this.subBoxes.get(dimensionIn).put(mutableboundingbox.toString(), mutableboundingbox);
         this.subBoxFlags.get(dimensionIn).put(mutableboundingbox.toString(), obool);
      }

   }
}