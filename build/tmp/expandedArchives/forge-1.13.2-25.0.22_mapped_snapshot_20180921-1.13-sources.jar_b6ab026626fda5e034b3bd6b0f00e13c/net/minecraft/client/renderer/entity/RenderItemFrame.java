package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderItemFrame extends Render<EntityItemFrame> {
   private static final ResourceLocation MAP_BACKGROUND_TEXTURES = new ResourceLocation("textures/map/map_background.png");
   private static final ModelResourceLocation LOCATION_MODEL = new ModelResourceLocation("item_frame", "map=false");
   private static final ModelResourceLocation LOCATION_MODEL_MAP = new ModelResourceLocation("item_frame", "map=true");
   private final Minecraft mc = Minecraft.getInstance();
   private final ItemRenderer itemRenderer;

   public RenderItemFrame(RenderManager renderManagerIn, ItemRenderer itemRendererIn) {
      super(renderManagerIn);
      this.itemRenderer = itemRendererIn;
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(EntityItemFrame entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      BlockPos blockpos = entity.getHangingPosition();
      double d0 = (double)blockpos.getX() - entity.posX + x;
      double d1 = (double)blockpos.getY() - entity.posY + y;
      double d2 = (double)blockpos.getZ() - entity.posZ + z;
      GlStateManager.translated(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D);
      GlStateManager.rotatef(entity.rotationPitch, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(180.0F - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
      this.renderManager.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
      ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
      ModelResourceLocation modelresourcelocation = entity.getDisplayedItem().getItem() instanceof ItemMap ? LOCATION_MODEL_MAP : LOCATION_MODEL;
      GlStateManager.pushMatrix();
      GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(entity));
      }

      blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(modelmanager.getModel(modelresourcelocation), 1.0F, 1.0F, 1.0F, 1.0F);
      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
      if (entity.getDisplayedItem().getItem() == Items.FILLED_MAP) {
         GlStateManager.pushLightingAttrib();
         RenderHelper.enableStandardItemLighting();
      }

      GlStateManager.translatef(0.0F, 0.0F, 0.4375F);
      this.renderItem(entity);
      if (entity.getDisplayedItem().getItem() == Items.FILLED_MAP) {
         RenderHelper.disableStandardItemLighting();
         GlStateManager.popAttrib();
      }

      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
      this.renderName(entity, x + (double)((float)entity.facingDirection.getXOffset() * 0.3F), y - 0.25D, z + (double)((float)entity.facingDirection.getZOffset() * 0.3F));
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   @Nullable
   protected ResourceLocation getEntityTexture(EntityItemFrame entity) {
      return null;
   }

   private void renderItem(EntityItemFrame itemFrame) {
      ItemStack itemstack = itemFrame.getDisplayedItem();
      if (!itemstack.isEmpty()) {
         GlStateManager.pushMatrix();
         MapData mapdata = ItemMap.getMapData(itemstack, itemFrame.world);
         int i = mapdata != null ? itemFrame.getRotation() % 4 * 2 : itemFrame.getRotation();
         GlStateManager.rotatef((float)i * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
         if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderItemInFrameEvent(itemFrame, this))) {
         if (mapdata != null) {
            GlStateManager.disableLighting();
            this.renderManager.textureManager.bindTexture(MAP_BACKGROUND_TEXTURES);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            float f = 0.0078125F;
            GlStateManager.scalef(0.0078125F, 0.0078125F, 0.0078125F);
            GlStateManager.translatef(-64.0F, -64.0F, 0.0F);
            GlStateManager.translatef(0.0F, 0.0F, -1.0F);
            if (mapdata != null) {
               this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, true);
            }
         } else {
            GlStateManager.scalef(0.5F, 0.5F, 0.5F);
            this.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED);
         }
         }

         GlStateManager.popMatrix();
      }
   }

   protected void renderName(EntityItemFrame entity, double x, double y, double z) {
      if (Minecraft.isGuiEnabled() && !entity.getDisplayedItem().isEmpty() && entity.getDisplayedItem().hasDisplayName() && this.renderManager.pointedEntity == entity) {
         double d0 = entity.getDistanceSq(this.renderManager.renderViewEntity);
         float f = entity.isSneaking() ? 32.0F : 64.0F;
         if (!(d0 >= (double)(f * f))) {
            String s = entity.getDisplayedItem().getDisplayName().getFormattedText();
            this.renderLivingLabel(entity, s, x, y, z, 64);
         }
      }
   }
}