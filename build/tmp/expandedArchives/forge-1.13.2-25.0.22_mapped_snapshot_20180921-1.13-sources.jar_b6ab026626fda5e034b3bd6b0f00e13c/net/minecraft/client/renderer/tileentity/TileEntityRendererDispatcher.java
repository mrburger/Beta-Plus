package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.model.ModelShulker;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityConduit;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityRendererDispatcher {
   private final Map<Class<? extends TileEntity>, TileEntityRenderer<? extends TileEntity>> renderers = Maps.newHashMap();
   public static TileEntityRendererDispatcher instance = new TileEntityRendererDispatcher();
   public FontRenderer fontRenderer;
   /** The player's current X position (same as playerX) */
   public static double staticPlayerX;
   /** The player's current Y position (same as playerY) */
   public static double staticPlayerY;
   /** The player's current Z position (same as playerZ) */
   public static double staticPlayerZ;
   public TextureManager textureManager;
   public World world;
   public Entity entity;
   public float entityYaw;
   public float entityPitch;
   public RayTraceResult cameraHitResult;
   public double entityX;
   public double entityY;
   public double entityZ;

   private TileEntityRendererDispatcher() {
      this.renderers.put(TileEntitySign.class, new TileEntitySignRenderer());
      this.renderers.put(TileEntityMobSpawner.class, new TileEntityMobSpawnerRenderer());
      this.renderers.put(TileEntityPiston.class, new TileEntityPistonRenderer());
      this.renderers.put(TileEntityChest.class, new TileEntityChestRenderer<>());
      this.renderers.put(TileEntityEnderChest.class, new TileEntityChestRenderer<>());
      this.renderers.put(TileEntityEnchantmentTable.class, new TileEntityEnchantmentTableRenderer());
      this.renderers.put(TileEntityEndPortal.class, new TileEntityEndPortalRenderer());
      this.renderers.put(TileEntityEndGateway.class, new TileEntityEndGatewayRenderer());
      this.renderers.put(TileEntityBeacon.class, new TileEntityBeaconRenderer());
      this.renderers.put(TileEntitySkull.class, new TileEntitySkullRenderer());
      this.renderers.put(TileEntityBanner.class, new TileEntityBannerRenderer());
      this.renderers.put(TileEntityStructure.class, new TileEntityStructureRenderer());
      this.renderers.put(TileEntityShulkerBox.class, new TileEntityShulkerBoxRenderer(new ModelShulker()));
      this.renderers.put(TileEntityBed.class, new TileEntityBedRenderer());
      this.renderers.put(TileEntityConduit.class, new TileEntityConduitRenderer());

      for(TileEntityRenderer<?> tileentityrenderer : this.renderers.values()) {
         tileentityrenderer.setRendererDispatcher(this);
      }

   }

   public <T extends TileEntity> TileEntityRenderer<T> getRenderer(Class<? extends TileEntity> teClass) {
      TileEntityRenderer<? extends TileEntity> tileentityrenderer = this.renderers.get(teClass);
      if (tileentityrenderer == null && teClass != TileEntity.class) {
         tileentityrenderer = this.getRenderer((Class<? extends TileEntity>)teClass.getSuperclass());
         this.renderers.put(teClass, tileentityrenderer);
      }

      return (TileEntityRenderer<T>)tileentityrenderer;
   }

   @Nullable
   public <T extends TileEntity> TileEntityRenderer<T> getRenderer(@Nullable TileEntity tileEntityIn) {
      return tileEntityIn == null || tileEntityIn.isRemoved() ? null : this.getRenderer(tileEntityIn.getClass());
   }

   public void prepare(World worldIn, TextureManager renderEngineIn, FontRenderer fontRendererIn, Entity entityIn, RayTraceResult cameraHitResultIn, float partialTicks) {
      if (this.world != worldIn) {
         this.setWorld(worldIn);
      }

      this.textureManager = renderEngineIn;
      this.entity = entityIn;
      this.fontRenderer = fontRendererIn;
      this.cameraHitResult = cameraHitResultIn;
      this.entityYaw = entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks;
      this.entityPitch = entityIn.prevRotationPitch + (entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks;
      this.entityX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
      this.entityY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
      this.entityZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
   }

   public void render(TileEntity tileentityIn, float partialTicks, int destroyStage) {
      if (tileentityIn.getDistanceSq(this.entityX, this.entityY, this.entityZ) < tileentityIn.getMaxRenderDistanceSquared()) {
         if(!drawingBatch || !tileentityIn.hasFastRenderer())
         {
         RenderHelper.enableStandardItemLighting();
         int i = this.world.getCombinedLight(tileentityIn.getPos(), 0);
         int j = i % 65536;
         int k = i / 65536;
         OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, (float)j, (float)k);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         }
         BlockPos blockpos = tileentityIn.getPos();
         this.render(tileentityIn, (double)blockpos.getX() - staticPlayerX, (double)blockpos.getY() - staticPlayerY, (double)blockpos.getZ() - staticPlayerZ, partialTicks, destroyStage, false);
      }

   }

   /**
    * Render this TileEntity at a given set of coordinates
    */
   public void render(TileEntity tileEntityIn, double x, double y, double z, float partialTicks) {
      this.render(tileEntityIn, x, y, z, partialTicks, -1, false);
   }

   public void renderAsItem(TileEntity tileEntityIn) {
      this.render(tileEntityIn, 0.0D, 0.0D, 0.0D, 0.0F, -1, true);
   }

   public void render(TileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage, boolean hasNoBlock) {
      TileEntityRenderer<TileEntity> tileentityrenderer = this.getRenderer(tileEntityIn);
      if (tileentityrenderer != null) {
         try {
            if (!hasNoBlock && (!tileEntityIn.hasWorld() || !tileEntityIn.getBlockState().hasTileEntity())) {
               return;
            }

            if(drawingBatch && tileEntityIn.hasFastRenderer())
                tileentityrenderer.renderTileEntityFast(tileEntityIn, x, y, z, partialTicks, destroyStage, batchBuffer.getBuffer());
            else
            tileentityrenderer.render(tileEntityIn, x, y, z, partialTicks, destroyStage);
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Block Entity");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block Entity Details");
            tileEntityIn.addInfoToCrashReport(crashreportcategory);
            throw new ReportedException(crashreport);
         }
      }

   }

   public void setWorld(@Nullable World worldIn) {
      this.world = worldIn;
      if (worldIn == null) {
         this.entity = null;
      }

   }

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }

   /**
    * Buffer used for batched TESRs
    */
   private net.minecraft.client.renderer.Tessellator batchBuffer = new net.minecraft.client.renderer.Tessellator(0x200000);
   private boolean drawingBatch = false;

   /**
    * Prepare for a batched TESR rendering.
    * You probably shouldn't call this manually.
    */
   public void preDrawBatch() {
       batchBuffer.getBuffer().begin(org.lwjgl.opengl.GL11.GL_QUADS, net.minecraft.client.renderer.vertex.DefaultVertexFormats.BLOCK);
       drawingBatch = true;
   }

   /**
    * Render all TESRs batched so far.
    * You probably shouldn't call this manually.
    */
   public void drawBatch(int pass) {
      textureManager.bindTexture(net.minecraft.client.renderer.texture.TextureMap.LOCATION_BLOCKS_TEXTURE);
      net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
      GlStateManager.blendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA);
      GlStateManager.enableBlend();
      GlStateManager.disableCull();

      if (net.minecraft.client.Minecraft.isAmbientOcclusionEnabled()) {
         GlStateManager.shadeModel(org.lwjgl.opengl.GL11.GL_SMOOTH);
      } else {
         GlStateManager.shadeModel(org.lwjgl.opengl.GL11.GL_FLAT);
      }

      if(pass > 0) {
         net.minecraft.util.math.Vec3d cameraPos = net.minecraft.client.renderer.ActiveRenderInfo.getCameraPosition();
         batchBuffer.getBuffer().sortVertexData((float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z);
      }
      batchBuffer.draw();

      net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
      drawingBatch = false;
   }

   //Internal, Do not call Use ClientRegistry.
   public synchronized <T extends TileEntity> void setSpecialRenderer(Class<T> tileEntityClass, TileEntityRenderer<? super T> specialRenderer) {
      this.renderers.put(tileEntityClass, specialRenderer);
   }
}