package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TileEntityRenderer<T extends TileEntity> {
   public static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[]{new ResourceLocation("textures/" + ModelBakery.LOCATION_DESTROY_STAGE_0.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.LOCATION_DESTROY_STAGE_1.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.LOCATION_DESTROY_STAGE_2.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.LOCATION_DESTROY_STAGE_3.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.LOCATION_DESTROY_STAGE_4.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.LOCATION_DESTROY_STAGE_5.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.LOCATION_DESTROY_STAGE_6.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.LOCATION_DESTROY_STAGE_7.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.LOCATION_DESTROY_STAGE_8.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.LOCATION_DESTROY_STAGE_9.getPath() + ".png")};
   protected TileEntityRendererDispatcher rendererDispatcher;

   public void render(T tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      if (tileEntityIn instanceof INameable && this.rendererDispatcher.cameraHitResult != null && tileEntityIn.getPos().equals(this.rendererDispatcher.cameraHitResult.getBlockPos())) {
         this.setLightmapDisabled(true);
         this.drawNameplate(tileEntityIn, ((INameable)tileEntityIn).getDisplayName().getFormattedText(), x, y, z, 12);
         this.setLightmapDisabled(false);
      }

   }

   /**
    * Sets whether to use the light map when rendering. Disabling this allows rendering ignoring lighting, which can be
    * useful for floating text, e.g.
    */
   protected void setLightmapDisabled(boolean disabled) {
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
      if (disabled) {
         GlStateManager.disableTexture2D();
      } else {
         GlStateManager.enableTexture2D();
      }

      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
   }

   protected void bindTexture(ResourceLocation location) {
      TextureManager texturemanager = this.rendererDispatcher.textureManager;
      if (texturemanager != null) {
         texturemanager.bindTexture(location);
      }

   }

   protected World getWorld() {
      return this.rendererDispatcher.world;
   }

   public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
      this.rendererDispatcher = rendererDispatcherIn;
   }

   public FontRenderer getFontRenderer() {
      return this.rendererDispatcher.getFontRenderer();
   }

   public boolean isGlobalRenderer(T te) {
      return false;
   }

   public void renderTileEntityFast(T te, double x, double y, double z, float partialTicks, int destroyStage, net.minecraft.client.renderer.BufferBuilder buffer) {}

   protected void drawNameplate(T te, String str, double x, double y, double z, int maxDistance) {
      Entity entity = this.rendererDispatcher.entity;
      double d0 = te.getDistanceSq(entity.posX, entity.posY, entity.posZ);
      if (!(d0 > (double)(maxDistance * maxDistance))) {
         float f = this.rendererDispatcher.entityYaw;
         float f1 = this.rendererDispatcher.entityPitch;
         boolean flag = false;
         GameRenderer.drawNameplate(this.getFontRenderer(), str, (float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F, 0, f, f1, false, false);
      }
   }
}