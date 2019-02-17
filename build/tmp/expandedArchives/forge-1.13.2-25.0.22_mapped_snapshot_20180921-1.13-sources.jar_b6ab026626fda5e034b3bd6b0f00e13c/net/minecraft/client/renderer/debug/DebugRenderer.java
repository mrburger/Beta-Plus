package net.minecraft.client.renderer.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugRenderer {
   public final DebugRendererPathfinding pathfinding;
   public final DebugRenderer.IDebugRenderer water;
   public final DebugRenderer.IDebugRenderer chunkBorder;
   public final DebugRenderer.IDebugRenderer heightMap;
   public final DebugRenderer.IDebugRenderer collisionBox;
   public final DebugRenderer.IDebugRenderer neighborsUpdate;
   public final DebugRendererCave cave;
   public final DebugRendererStructure structure;
   public final DebugRenderer.IDebugRenderer light;
   public final DebugRenderer.IDebugRenderer worldGenAttempts;
   public final DebugRenderer.IDebugRenderer solidFace;
   private boolean chunkBorderEnabled;
   private boolean pathfindingEnabled;
   private boolean waterEnabled;
   private boolean heightMapEnabled;
   private boolean collisionBoxEnabled;
   private boolean neighborsUpdateEnabled;
   private boolean caveEnabled;
   private boolean structureEnabled;
   private boolean lightEnabled;
   private boolean worldGenAttemptsEnabled;
   private boolean solidFaceEnabled;

   public DebugRenderer(Minecraft clientIn) {
      this.pathfinding = new DebugRendererPathfinding(clientIn);
      this.water = new DebugRendererWater(clientIn);
      this.chunkBorder = new DebugRendererChunkBorder(clientIn);
      this.heightMap = new DebugRendererHeightMap(clientIn);
      this.collisionBox = new DebugRendererCollisionBox(clientIn);
      this.neighborsUpdate = new DebugRendererNeighborsUpdate(clientIn);
      this.cave = new DebugRendererCave(clientIn);
      this.structure = new DebugRendererStructure(clientIn);
      this.light = new DebugRendererLight(clientIn);
      this.worldGenAttempts = new DebugRendererWorldGenAttempts(clientIn);
      this.solidFace = new DebugRendererSolidFace(clientIn);
   }

   public boolean shouldRender() {
      return this.chunkBorderEnabled || this.pathfindingEnabled || this.waterEnabled || this.heightMapEnabled || this.collisionBoxEnabled || this.neighborsUpdateEnabled || this.lightEnabled || this.worldGenAttemptsEnabled || this.solidFaceEnabled;
   }

   /**
    * Toggles the debug screen's visibility.
    */
   public boolean toggleChunkBorders() {
      this.chunkBorderEnabled = !this.chunkBorderEnabled;
      return this.chunkBorderEnabled;
   }

   public void renderDebug(float partialTicks, long finishTimeNano) {
      if (this.pathfindingEnabled) {
         this.pathfinding.render(partialTicks, finishTimeNano);
      }

      if (this.chunkBorderEnabled && !Minecraft.getInstance().isReducedDebug()) {
         this.chunkBorder.render(partialTicks, finishTimeNano);
      }

      if (this.waterEnabled) {
         this.water.render(partialTicks, finishTimeNano);
      }

      if (this.heightMapEnabled) {
         this.heightMap.render(partialTicks, finishTimeNano);
      }

      if (this.collisionBoxEnabled) {
         this.collisionBox.render(partialTicks, finishTimeNano);
      }

      if (this.neighborsUpdateEnabled) {
         this.neighborsUpdate.render(partialTicks, finishTimeNano);
      }

      if (this.caveEnabled) {
         this.cave.render(partialTicks, finishTimeNano);
      }

      if (this.structureEnabled) {
         this.structure.render(partialTicks, finishTimeNano);
      }

      if (this.lightEnabled) {
         this.light.render(partialTicks, finishTimeNano);
      }

      if (this.worldGenAttemptsEnabled) {
         this.worldGenAttempts.render(partialTicks, finishTimeNano);
      }

      if (this.solidFaceEnabled) {
         this.solidFace.render(partialTicks, finishTimeNano);
      }

   }

   public static void renderDebugText(String str, int x, int y, int z, float partialTicks, int color) {
      renderDebugText(str, (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, partialTicks, color);
   }

   public static void renderDebugText(String str, double x, double y, double z, float partialTicks, int color) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null && minecraft.getRenderManager() != null && minecraft.getRenderManager().options != null) {
         FontRenderer fontrenderer = minecraft.fontRenderer;
         EntityPlayer entityplayer = minecraft.player;
         double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)partialTicks;
         double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)partialTicks;
         double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)partialTicks;
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)(x - d0), (float)(y - d1) + 0.07F, (float)(z - d2));
         GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.scalef(0.02F, -0.02F, 0.02F);
         RenderManager rendermanager = minecraft.getRenderManager();
         GlStateManager.rotatef(-rendermanager.playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef((float)(rendermanager.options.thirdPersonView == 2 ? 1 : -1) * rendermanager.playerViewX, 1.0F, 0.0F, 0.0F);
         GlStateManager.disableLighting();
         GlStateManager.enableTexture2D();
         GlStateManager.enableDepthTest();
         GlStateManager.depthMask(true);
         GlStateManager.scalef(-1.0F, 1.0F, 1.0F);
         fontrenderer.drawString(str, (float)(-fontrenderer.getStringWidth(str) / 2), 0.0F, color);
         GlStateManager.enableLighting();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface IDebugRenderer {
      void render(float partialTicks, long finishTimeNano);
   }
}