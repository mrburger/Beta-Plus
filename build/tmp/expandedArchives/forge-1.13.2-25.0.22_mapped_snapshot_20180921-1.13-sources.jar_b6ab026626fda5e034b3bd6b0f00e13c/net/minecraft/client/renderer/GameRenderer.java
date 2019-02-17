package net.minecraft.client.renderer;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.resources.SimpleResource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GameRenderer implements AutoCloseable, IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation RAIN_TEXTURES = new ResourceLocation("textures/environment/rain.png");
   private static final ResourceLocation SNOW_TEXTURES = new ResourceLocation("textures/environment/snow.png");
   /** A reference to the Minecraft object. */
   private final Minecraft mc;
   private final IResourceManager resourceManager;
   private final Random random = new Random();
   private float farPlaneDistance;
   public final FirstPersonRenderer itemRenderer;
   private final MapItemRenderer mapItemRenderer;
   /** Entity renderer update count */
   private int rendererUpdateCount;
   /** Pointed entity */
   private Entity pointedEntity;
   private final float thirdPersonDistance = 4.0F;
   /** Previous third person distance */
   private float thirdPersonDistancePrev = 4.0F;
   /** FOV modifier hand */
   private float fovModifierHand;
   /** FOV modifier hand prev */
   private float fovModifierHandPrev;
   private float bossColorModifier;
   private float bossColorModifierPrev;
   private boolean renderHand = true;
   private boolean drawBlockOutline = true;
   private long timeWorldIcon;
   /** Previous frame time in milliseconds */
   private long prevFrameTime = Util.milliTime();
   /** The texture id of the blocklight/skylight texture used for lighting effects */
   private final LightTexture lightmapTexture;
   /** Rain sound counter */
   private int rainSoundCounter;
   private final float[] rainXCoords = new float[1024];
   private final float[] rainYCoords = new float[1024];
   private final FogRenderer fogRenderer;
   private boolean debugView;
   private double cameraZoom = 1.0D;
   private double cameraYaw;
   private double cameraPitch;
   private ItemStack itemActivationItem;
   private int itemActivationTicks;
   private float itemActivationOffX;
   private float itemActivationOffY;
   private ShaderGroup shaderGroup;
   private float renderEyeHeight;
   /** field_203000_X renderEyeHeight */
   private float renderEyeHeigsf;
   private static final ResourceLocation[] SHADERS_TEXTURES = new ResourceLocation[]{new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json")};
   public static final int SHADER_COUNT = SHADERS_TEXTURES.length;
   private int shaderIndex = SHADER_COUNT;
   private boolean useShader;
   private int frameCount;

   public GameRenderer(Minecraft mcIn, IResourceManager resourceManagerIn) {
      this.mc = mcIn;
      this.resourceManager = resourceManagerIn;
      this.itemRenderer = mcIn.getFirstPersonRenderer();
      this.mapItemRenderer = new MapItemRenderer(mcIn.getTextureManager());
      this.lightmapTexture = new LightTexture(this);
      this.fogRenderer = new FogRenderer(this);
      this.shaderGroup = null;

      for(int i = 0; i < 32; ++i) {
         for(int j = 0; j < 32; ++j) {
            float f = (float)(j - 16);
            float f1 = (float)(i - 16);
            float f2 = MathHelper.sqrt(f * f + f1 * f1);
            this.rainXCoords[i << 5 | j] = -f1 / f2;
            this.rainYCoords[i << 5 | j] = f / f2;
         }
      }

   }

   public void close() {
      this.lightmapTexture.close();
      this.mapItemRenderer.close();
      this.stopUseShader();
   }

   public boolean isShaderActive() {
      return OpenGlHelper.shadersSupported && this.shaderGroup != null;
   }

   public void stopUseShader() {
      if (this.shaderGroup != null) {
         this.shaderGroup.close();
      }

      this.shaderGroup = null;
      this.shaderIndex = SHADER_COUNT;
   }

   public void switchUseShader() {
      this.useShader = !this.useShader;
   }

   /**
    * What shader to use when spectating this entity
    */
   public void loadEntityShader(@Nullable Entity entityIn) {
      if (OpenGlHelper.shadersSupported) {
         if (this.shaderGroup != null) {
            this.shaderGroup.close();
         }

         this.shaderGroup = null;
         if (entityIn instanceof EntityCreeper) {
            this.loadShader(new ResourceLocation("shaders/post/creeper.json"));
         } else if (entityIn instanceof EntitySpider) {
            this.loadShader(new ResourceLocation("shaders/post/spider.json"));
         } else if (entityIn instanceof EntityEnderman) {
            this.loadShader(new ResourceLocation("shaders/post/invert.json"));
         } else {
            net.minecraftforge.client.ForgeHooksClient.loadEntityShader(entityIn, this);
         }

      }
   }

   public void loadShader(ResourceLocation resourceLocationIn) {
      if (this.shaderGroup != null) {
         this.shaderGroup.close();
      }

      try {
         this.shaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), resourceLocationIn);
         this.shaderGroup.createBindFramebuffers(this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight());
         this.useShader = true;
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to load shader: {}", resourceLocationIn, ioexception);
         this.shaderIndex = SHADER_COUNT;
         this.useShader = false;
      } catch (JsonSyntaxException jsonsyntaxexception) {
         LOGGER.warn("Failed to load shader: {}", resourceLocationIn, jsonsyntaxexception);
         this.shaderIndex = SHADER_COUNT;
         this.useShader = false;
      }

   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      if (this.shaderGroup != null) {
         this.shaderGroup.close();
      }

      this.shaderGroup = null;
      if (this.shaderIndex == SHADER_COUNT) {
         this.loadEntityShader(this.mc.getRenderViewEntity());
      } else {
         this.loadShader(SHADERS_TEXTURES[this.shaderIndex]);
      }

   }

   /**
    * Updates the entity renderer
    */
   public void tick() {
      if (OpenGlHelper.shadersSupported && ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
         ShaderLinkHelper.setNewStaticShaderLinkHelper();
      }

      this.updateFovModifierHand();
      this.lightmapTexture.tick();
      this.thirdPersonDistancePrev = 4.0F;
      if (this.mc.getRenderViewEntity() == null) {
         this.mc.setRenderViewEntity(this.mc.player);
      }

      this.renderEyeHeigsf = this.renderEyeHeight;
      this.renderEyeHeight += (this.mc.getRenderViewEntity().getEyeHeight() - this.renderEyeHeight) * 0.5F;
      ++this.rendererUpdateCount;
      this.itemRenderer.tick();
      this.addRainParticles();
      this.bossColorModifierPrev = this.bossColorModifier;
      if (this.mc.ingameGUI.getBossOverlay().shouldDarkenSky()) {
         this.bossColorModifier += 0.05F;
         if (this.bossColorModifier > 1.0F) {
            this.bossColorModifier = 1.0F;
         }
      } else if (this.bossColorModifier > 0.0F) {
         this.bossColorModifier -= 0.0125F;
      }

      if (this.itemActivationTicks > 0) {
         --this.itemActivationTicks;
         if (this.itemActivationTicks == 0) {
            this.itemActivationItem = null;
         }
      }

   }

   public ShaderGroup getShaderGroup() {
      return this.shaderGroup;
   }

   public void updateShaderGroupSize(int width, int height) {
      if (OpenGlHelper.shadersSupported) {
         if (this.shaderGroup != null) {
            this.shaderGroup.createBindFramebuffers(width, height);
         }

         this.mc.renderGlobal.createBindEntityOutlineFbs(width, height);
      }
   }

   /**
    * Gets the block or object that is being moused over.
    */
   public void getMouseOver(float partialTicks) {
      Entity entity = this.mc.getRenderViewEntity();
      if (entity != null) {
         if (this.mc.world != null) {
            this.mc.profiler.startSection("pick");
            this.mc.pointedEntity = null;
            double d0 = (double)this.mc.playerController.getBlockReachDistance();
            this.mc.objectMouseOver = entity.rayTrace(d0, partialTicks, RayTraceFluidMode.NEVER);
            Vec3d vec3d = entity.getEyePosition(partialTicks);
            boolean flag = false;
            int i = 3;
            double d1 = d0;
            if (this.mc.playerController.extendedReach()) {
               d1 = 6.0D;
               d0 = d1;
            } else {
               if (d0 > 3.0D) {
                  flag = true;
               }

               d0 = d0;
            }

            if (this.mc.objectMouseOver != null) {
               d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3d);
            }

            Vec3d vec3d1 = entity.getLook(1.0F);
            Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
            this.pointedEntity = null;
            Vec3d vec3d3 = null;
            float f = 1.0F;
            List<Entity> list = this.mc.world.getEntitiesInAABBexcluding(entity, entity.getBoundingBox().expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0D, 1.0D, 1.0D), EntitySelectors.NOT_SPECTATING.and(Entity::canBeCollidedWith));
            double d2 = d1;

            for(int j = 0; j < list.size(); ++j) {
               Entity entity1 = list.get(j);
               AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)entity1.getCollisionBorderSize());
               RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
               if (axisalignedbb.contains(vec3d)) {
                  if (d2 >= 0.0D) {
                     this.pointedEntity = entity1;
                     vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                     d2 = 0.0D;
                  }
               } else if (raytraceresult != null) {
                  double d3 = vec3d.distanceTo(raytraceresult.hitVec);
                  if (d3 < d2 || d2 == 0.0D) {
                     if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract()) {
                        if (d2 == 0.0D) {
                           this.pointedEntity = entity1;
                           vec3d3 = raytraceresult.hitVec;
                        }
                     } else {
                        this.pointedEntity = entity1;
                        vec3d3 = raytraceresult.hitVec;
                        d2 = d3;
                     }
                  }
               }
            }

            if (this.pointedEntity != null && flag && vec3d.distanceTo(vec3d3) > 3.0D) {
               this.pointedEntity = null;
               this.mc.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, (EnumFacing)null, new BlockPos(vec3d3));
            }

            if (this.pointedEntity != null && (d2 < d1 || this.mc.objectMouseOver == null)) {
               this.mc.objectMouseOver = new RayTraceResult(this.pointedEntity, vec3d3);
               if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
                  this.mc.pointedEntity = this.pointedEntity;
               }
            }

            this.mc.profiler.endSection();
         }
      }
   }

   /**
    * Update FOV modifier hand
    */
   private void updateFovModifierHand() {
      float f = 1.0F;
      if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer) {
         AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)this.mc.getRenderViewEntity();
         f = abstractclientplayer.getFovModifier();
      }

      this.fovModifierHandPrev = this.fovModifierHand;
      this.fovModifierHand += (f - this.fovModifierHand) * 0.5F;
      if (this.fovModifierHand > 1.5F) {
         this.fovModifierHand = 1.5F;
      }

      if (this.fovModifierHand < 0.1F) {
         this.fovModifierHand = 0.1F;
      }

   }

   private double getFOVModifier(float partialTicks, boolean useFOVSetting) {
      if (this.debugView) {
         return 90.0D;
      } else {
         Entity entity = this.mc.getRenderViewEntity();
         double d0 = 70.0D;
         if (useFOVSetting) {
            d0 = this.mc.gameSettings.fovSetting;
            d0 = d0 * (double)(this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * partialTicks);
         }

         if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getHealth() <= 0.0F) {
            float f = (float)((EntityLivingBase)entity).deathTime + partialTicks;
            d0 /= (double)((1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F);
         }

         IFluidState ifluidstate = ActiveRenderInfo.getFluidStateAtEntityViewpoint(this.mc.world, entity, partialTicks);
         if (!ifluidstate.isEmpty()) {
            d0 = d0 * 60.0D / 70.0D;
         }

         return net.minecraftforge.client.ForgeHooksClient.getFOVModifier(this, entity, ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks), ifluidstate, partialTicks, d0);
      }
   }

   private void hurtCameraEffect(float partialTicks) {
      if (this.mc.getRenderViewEntity() instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)this.mc.getRenderViewEntity();
         float f = (float)entitylivingbase.hurtTime - partialTicks;
         if (entitylivingbase.getHealth() <= 0.0F) {
            float f1 = (float)entitylivingbase.deathTime + partialTicks;
            GlStateManager.rotatef(40.0F - 8000.0F / (f1 + 200.0F), 0.0F, 0.0F, 1.0F);
         }

         if (f < 0.0F) {
            return;
         }

         f = f / (float)entitylivingbase.maxHurtTime;
         f = MathHelper.sin(f * f * f * f * (float)Math.PI);
         float f2 = entitylivingbase.attackedAtYaw;
         GlStateManager.rotatef(-f2, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(-f * 14.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(f2, 0.0F, 1.0F, 0.0F);
      }

   }

   /**
    * Updates the bobbing render effect of the player.
    */
   private void applyBobbing(float partialTicks) {
      if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
         float f = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
         float f1 = -(entityplayer.distanceWalkedModified + f * partialTicks);
         float f2 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * partialTicks;
         float f3 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * partialTicks;
         GlStateManager.translatef(MathHelper.sin(f1 * (float)Math.PI) * f2 * 0.5F, -Math.abs(MathHelper.cos(f1 * (float)Math.PI) * f2), 0.0F);
         GlStateManager.rotatef(MathHelper.sin(f1 * (float)Math.PI) * f2 * 3.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(Math.abs(MathHelper.cos(f1 * (float)Math.PI - 0.2F) * f2) * 5.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(f3, 1.0F, 0.0F, 0.0F);
      }
   }

   /**
    * sets up player's eye (or camera in third person mode)
    */
   private void orientCamera(float partialTicks) {
      Entity entity = this.mc.getRenderViewEntity();
      float f = this.renderEyeHeigsf + (this.renderEyeHeight - this.renderEyeHeigsf) * partialTicks;
      double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
      double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + (double)entity.getEyeHeight();
      double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
      if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping()) {
         f = (float)((double)f + 1.0D);
         GlStateManager.translatef(0.0F, 0.3F, 0.0F);
         if (!this.mc.gameSettings.debugCamEnable) {
            BlockPos blockpos = new BlockPos(entity);
            IBlockState iblockstate = this.mc.world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            if (block instanceof BlockBed) {
               GlStateManager.rotatef(iblockstate.get(BlockBed.HORIZONTAL_FACING).getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
            GlStateManager.rotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
         }
      } else if (this.mc.gameSettings.thirdPersonView > 0) {
         double d3 = (double)(this.thirdPersonDistancePrev + (4.0F - this.thirdPersonDistancePrev) * partialTicks);
         if (this.mc.gameSettings.debugCamEnable) {
            GlStateManager.translatef(0.0F, 0.0F, (float)(-d3));
         } else {
            float f1 = entity.rotationYaw;
            float f2 = entity.rotationPitch;
            if (this.mc.gameSettings.thirdPersonView == 2) {
               f2 += 180.0F;
            }

            double d4 = (double)(-MathHelper.sin(f1 * ((float)Math.PI / 180F)) * MathHelper.cos(f2 * ((float)Math.PI / 180F))) * d3;
            double d5 = (double)(MathHelper.cos(f1 * ((float)Math.PI / 180F)) * MathHelper.cos(f2 * ((float)Math.PI / 180F))) * d3;
            double d6 = (double)(-MathHelper.sin(f2 * ((float)Math.PI / 180F))) * d3;

            for(int i = 0; i < 8; ++i) {
               float f3 = (float)((i & 1) * 2 - 1);
               float f4 = (float)((i >> 1 & 1) * 2 - 1);
               float f5 = (float)((i >> 2 & 1) * 2 - 1);
               f3 = f3 * 0.1F;
               f4 = f4 * 0.1F;
               f5 = f5 * 0.1F;
               RayTraceResult raytraceresult = this.mc.world.rayTraceBlocks(new Vec3d(d0 + (double)f3, d1 + (double)f4, d2 + (double)f5), new Vec3d(d0 - d4 + (double)f3 + (double)f5, d1 - d6 + (double)f4, d2 - d5 + (double)f5));
               if (raytraceresult != null) {
                  double d7 = raytraceresult.hitVec.distanceTo(new Vec3d(d0, d1, d2));
                  if (d7 < d3) {
                     d3 = d7;
                  }
               }
            }

            if (this.mc.gameSettings.thirdPersonView == 2) {
               GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotatef(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
            GlStateManager.translatef(0.0F, 0.0F, (float)(-d3));
            GlStateManager.rotatef(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
         }
      } else if (!this.debugView) {
         GlStateManager.translatef(0.0F, 0.0F, 0.05F);
      }

      if (!this.mc.gameSettings.debugCamEnable) {
         float yaw = entity.getYaw(partialTicks) + 180F;
         float pitch = entity.getPitch(partialTicks);
         float roll = 0.0F;
         IBlockState state = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);
         IFluidState fluidState = ActiveRenderInfo.getFluidStateAtEntityViewpoint(this.mc.world, entity, partialTicks);
         net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup event = new net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup(this, entity, state, fluidState, partialTicks, yaw, pitch, roll);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
         GlStateManager.rotatef(event.getRoll(), 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(event.getPitch(), 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(event.getYaw(), 0.0F, 1.0F, 0.0F);
      }

      GlStateManager.translatef(0.0F, -f, 0.0F);
   }

   private void setupCameraTransform(float partialTicks) {
      this.farPlaneDistance = (float)(this.mc.gameSettings.renderDistanceChunks * 16);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      if (this.cameraZoom != 1.0D) {
         GlStateManager.translatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
         GlStateManager.scaled(this.cameraZoom, this.cameraZoom, 1.0D);
      }

      GlStateManager.multMatrixf(Matrix4f.perspective(this.getFOVModifier(partialTicks, true), (float)this.mc.mainWindow.getFramebufferWidth() / (float)this.mc.mainWindow.getFramebufferHeight(), 0.05F, this.farPlaneDistance * MathHelper.SQRT_2));
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      this.hurtCameraEffect(partialTicks);
      if (this.mc.gameSettings.viewBobbing) {
         this.applyBobbing(partialTicks);
      }

      float f = this.mc.player.prevTimeInPortal + (this.mc.player.timeInPortal - this.mc.player.prevTimeInPortal) * partialTicks;
      if (f > 0.0F) {
         int i = 20;
         if (this.mc.player.isPotionActive(MobEffects.NAUSEA)) {
            i = 7;
         }

         float f1 = 5.0F / (f * f + 5.0F) - f * 0.04F;
         f1 = f1 * f1;
         GlStateManager.rotatef(((float)this.rendererUpdateCount + partialTicks) * (float)i, 0.0F, 1.0F, 1.0F);
         GlStateManager.scalef(1.0F / f1, 1.0F, 1.0F);
         GlStateManager.rotatef(-((float)this.rendererUpdateCount + partialTicks) * (float)i, 0.0F, 1.0F, 1.0F);
      }

      this.orientCamera(partialTicks);
   }

   private void renderHand(float partialTicks) {
      if (!this.debugView) {
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrixf(Matrix4f.perspective(this.getFOVModifier(partialTicks, false), (float)this.mc.mainWindow.getFramebufferWidth() / (float)this.mc.mainWindow.getFramebufferHeight(), 0.05F, this.farPlaneDistance * 2.0F));
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.pushMatrix();
         this.hurtCameraEffect(partialTicks);
         if (this.mc.gameSettings.viewBobbing) {
            this.applyBobbing(partialTicks);
         }

         boolean flag = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
         if (!net.minecraftforge.client.ForgeHooksClient.renderFirstPersonHand(mc.renderGlobal, partialTicks))
         if (this.mc.gameSettings.thirdPersonView == 0 && !flag && !this.mc.gameSettings.hideGUI && this.mc.playerController.getCurrentGameType() != GameType.SPECTATOR) {
            this.enableLightmap();
            this.itemRenderer.renderItemInFirstPerson(partialTicks);
            this.disableLightmap();
         }

         GlStateManager.popMatrix();
         if (this.mc.gameSettings.thirdPersonView == 0 && !flag) {
            this.itemRenderer.renderOverlays(partialTicks);
            this.hurtCameraEffect(partialTicks);
         }

         if (this.mc.gameSettings.viewBobbing) {
            this.applyBobbing(partialTicks);
         }

      }
   }

   public void disableLightmap() {
      this.lightmapTexture.disableLightmap();
   }

   public void enableLightmap() {
      this.lightmapTexture.enableLightmap();
   }

   public float getNightVisionBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks) {
      int i = entitylivingbaseIn.getActivePotionEffect(MobEffects.NIGHT_VISION).getDuration();
      return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)i - partialTicks) * (float)Math.PI * 0.2F) * 0.3F;
   }

   public void updateCameraAndRender(float partialTicks, long nanoTime, boolean renderWorldIn) {
      if (!this.mc.isGameFocused() && this.mc.gameSettings.pauseOnLostFocus && (!this.mc.gameSettings.touchscreen || !this.mc.mouseHelper.isRightDown())) {
         if (Util.milliTime() - this.prevFrameTime > 500L) {
            this.mc.displayInGameMenu();
         }
      } else {
         this.prevFrameTime = Util.milliTime();
      }

      if (!this.mc.skipRenderWorld) {
         int i = (int)(this.mc.mouseHelper.getMouseX() * (double)this.mc.mainWindow.getScaledWidth() / (double)this.mc.mainWindow.getWidth());
         int j = (int)(this.mc.mouseHelper.getMouseY() * (double)this.mc.mainWindow.getScaledHeight() / (double)this.mc.mainWindow.getHeight());
         int k = this.mc.gameSettings.limitFramerate;
         if (renderWorldIn && this.mc.world != null) {
            this.mc.profiler.startSection("level");
            int l = Math.min(Minecraft.getDebugFPS(), k);
            l = Math.max(l, 60);
            long i1 = Util.nanoTime() - nanoTime;
            long j1 = Math.max((long)(1000000000 / l / 4) - i1, 0L);
            this.renderWorld(partialTicks, Util.nanoTime() + j1);
            if (this.mc.isSingleplayer() && this.timeWorldIcon < Util.milliTime() - 1000L) {
               this.timeWorldIcon = Util.milliTime();
               if (!this.mc.getIntegratedServer().isWorldIconSet()) {
                  this.createWorldIcon();
               }
            }

            if (OpenGlHelper.shadersSupported) {
               this.mc.renderGlobal.renderEntityOutlineFramebuffer();
               if (this.shaderGroup != null && this.useShader) {
                  GlStateManager.matrixMode(5890);
                  GlStateManager.pushMatrix();
                  GlStateManager.loadIdentity();
                  this.shaderGroup.render(partialTicks);
                  GlStateManager.popMatrix();
               }

               this.mc.getFramebuffer().bindFramebuffer(true);
            }

            this.mc.profiler.endStartSection("gui");
            if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null) {
               GlStateManager.alphaFunc(516, 0.1F);
               this.mc.mainWindow.setupOverlayRendering();
               this.renderItemActivation(this.mc.mainWindow.getScaledWidth(), this.mc.mainWindow.getScaledHeight(), partialTicks);
               this.mc.ingameGUI.renderGameOverlay(partialTicks);
            }

            this.mc.profiler.endSection();
         } else {
            GlStateManager.viewport(0, 0, this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight());
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            this.mc.mainWindow.setupOverlayRendering();
            // Forge: Fix MC-112292
            net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher.instance.textureManager = this.mc.getTextureManager();
            // Forge: also fix rendering text before entering world (not part of MC-112292, but the same reason)
            net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher.instance.fontRenderer = this.mc.fontRenderer;
         }

         if (this.mc.currentScreen != null) {
            GlStateManager.clear(256);

            try {
               net.minecraftforge.client.ForgeHooksClient.drawScreen(this.mc.currentScreen, i, j, this.mc.getTickLength());
            } catch (Throwable throwable) {
               CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering screen");
               CrashReportCategory crashreportcategory = crashreport.makeCategory("Screen render details");
               crashreportcategory.addDetail("Screen name", () -> {
                  return this.mc.currentScreen.getClass().getCanonicalName();
               });
               crashreportcategory.addDetail("Mouse location", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.mc.mouseHelper.getMouseX(), this.mc.mouseHelper.getMouseY());
               });
               crashreportcategory.addDetail("Screen size", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.mc.mainWindow.getScaledWidth(), this.mc.mainWindow.getScaledHeight(), this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight(), this.mc.mainWindow.getGuiScaleFactor());
               });
               throw new ReportedException(crashreport);
            }
         }

      }
   }

   private void createWorldIcon() {
      if (this.mc.renderGlobal.getRenderedChunks() > 10 && this.mc.renderGlobal.hasNoChunkUpdates() && !this.mc.getIntegratedServer().isWorldIconSet()) {
         NativeImage nativeimage = ScreenShotHelper.createScreenshot(this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight(), this.mc.getFramebuffer());
         SimpleResource.RESOURCE_IO_EXECUTOR.execute(() -> {
            int i = nativeimage.getWidth();
            int j = nativeimage.getHeight();
            int k = 0;
            int l = 0;
            if (i > j) {
               k = (i - j) / 2;
               i = j;
            } else {
               l = (j - i) / 2;
               j = i;
            }

            try (NativeImage nativeimage1 = new NativeImage(64, 64, false)) {
               nativeimage.resizeSubRectTo(k, l, i, j, nativeimage1);
               nativeimage1.write(this.mc.getIntegratedServer().getWorldIconFile());
            } catch (IOException ioexception) {
               LOGGER.warn("Couldn't save auto screenshot", (Throwable)ioexception);
            } finally {
               nativeimage.close();
            }

         });
      }

   }

   public void renderStreamIndicator(float partialTicks) {
      this.mc.mainWindow.setupOverlayRendering();
   }

   private boolean isDrawBlockOutline() {
      if (!this.drawBlockOutline) {
         return false;
      } else {
         Entity entity = this.mc.getRenderViewEntity();
         boolean flag = entity instanceof EntityPlayer && !this.mc.gameSettings.hideGUI;
         if (flag && !((EntityPlayer)entity).abilities.allowEdit) {
            ItemStack itemstack = ((EntityPlayer)entity).getHeldItemMainhand();
            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.type == RayTraceResult.Type.BLOCK) {
               BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
               IBlockState state = this.mc.world.getBlockState(blockpos);
               if (this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR) {
                  flag = state.hasTileEntity() && this.mc.world.getTileEntity(blockpos) instanceof IInventory;
               } else {
                  BlockWorldState blockworldstate = new BlockWorldState(this.mc.world, blockpos, false);
                  flag = !itemstack.isEmpty() && (itemstack.canDestroy(this.mc.world.getTags(), blockworldstate) || itemstack.canPlaceOn(this.mc.world.getTags(), blockworldstate));
               }
            }
         }

         return flag;
      }
   }

   public void renderWorld(float partialTicks, long finishTimeNano) {
      this.lightmapTexture.updateLightmap(partialTicks);
      if (this.mc.getRenderViewEntity() == null) {
         this.mc.setRenderViewEntity(this.mc.player);
      }

      this.getMouseOver(partialTicks);
      GlStateManager.enableDepthTest();
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.5F);
      this.mc.profiler.startSection("center");
      this.updateCameraAndRender(partialTicks, finishTimeNano);
      this.mc.profiler.endSection();
   }

   private void updateCameraAndRender(float partialTicks, long nanoTime) {
      WorldRenderer worldrenderer = this.mc.renderGlobal;
      ParticleManager particlemanager = this.mc.particles;
      boolean flag = this.isDrawBlockOutline();
      GlStateManager.enableCull();
      this.mc.profiler.endStartSection("clear");
      GlStateManager.viewport(0, 0, this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight());
      this.fogRenderer.updateFogColor(partialTicks);
      GlStateManager.clear(16640);
      this.mc.profiler.endStartSection("camera");
      this.setupCameraTransform(partialTicks);
      ActiveRenderInfo.updateRenderInfo(this.mc.getRenderViewEntity(), this.mc.gameSettings.thirdPersonView == 2, this.farPlaneDistance);
      this.mc.profiler.endStartSection("frustum");
      ClippingHelperImpl.getInstance();
      this.mc.profiler.endStartSection("culling");
      ICamera icamera = new Frustum();
      Entity entity = this.mc.getRenderViewEntity();
      double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
      double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
      double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
      icamera.setPosition(d0, d1, d2);
      if (this.mc.gameSettings.renderDistanceChunks >= 4) {
         this.fogRenderer.setupFog(-1, partialTicks);
         this.mc.profiler.endStartSection("sky");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrixf(Matrix4f.perspective(this.getFOVModifier(partialTicks, true), (float)this.mc.mainWindow.getFramebufferWidth() / (float)this.mc.mainWindow.getFramebufferHeight(), 0.05F, this.farPlaneDistance * 2.0F));
         GlStateManager.matrixMode(5888);
         worldrenderer.renderSky(partialTicks);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrixf(Matrix4f.perspective(this.getFOVModifier(partialTicks, true), (float)this.mc.mainWindow.getFramebufferWidth() / (float)this.mc.mainWindow.getFramebufferHeight(), 0.05F, this.farPlaneDistance * MathHelper.SQRT_2));
         GlStateManager.matrixMode(5888);
      }

      this.fogRenderer.setupFog(0, partialTicks);
      GlStateManager.shadeModel(7425);
      if (entity.posY + (double)entity.getEyeHeight() < 128.0D) {
         this.renderCloudsCheck(worldrenderer, partialTicks, d0, d1, d2);
      }

      this.mc.profiler.endStartSection("prepareterrain");
      this.fogRenderer.setupFog(0, partialTicks);
      this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      RenderHelper.disableStandardItemLighting();
      this.mc.profiler.endStartSection("terrain_setup");
      worldrenderer.setupTerrain(entity, partialTicks, icamera, this.frameCount++, this.mc.player.isSpectator());
      this.mc.profiler.endStartSection("updatechunks");
      this.mc.renderGlobal.updateChunks(nanoTime);
      this.mc.profiler.endStartSection("terrain");
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.disableAlphaTest();
      worldrenderer.renderBlockLayer(BlockRenderLayer.SOLID, (double)partialTicks, entity);
      GlStateManager.enableAlphaTest();
      this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, this.mc.gameSettings.mipmapLevels > 0); // FORGE: fix flickering leaves when mods mess up the blurMipmap settings
      worldrenderer.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, (double)partialTicks, entity);
      this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      worldrenderer.renderBlockLayer(BlockRenderLayer.CUTOUT, (double)partialTicks, entity);
      this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      GlStateManager.shadeModel(7424);
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      RenderHelper.enableStandardItemLighting();
      this.mc.profiler.endStartSection("entities");
      net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
      worldrenderer.renderEntities(entity, icamera, partialTicks);
      net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
      RenderHelper.disableStandardItemLighting();
      this.disableLightmap();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      if (flag && this.mc.objectMouseOver != null) {
         EntityPlayer entityplayer = (EntityPlayer)entity;
         GlStateManager.disableAlphaTest();
         this.mc.profiler.endStartSection("outline");
         if (!net.minecraftforge.client.ForgeHooksClient.onDrawBlockHighlight(worldrenderer, entityplayer, mc.objectMouseOver, 0, partialTicks))
         worldrenderer.drawSelectionBox(entityplayer, this.mc.objectMouseOver, 0, partialTicks);
         GlStateManager.enableAlphaTest();
      }

      if (this.mc.debugRenderer.shouldRender()) {
         this.mc.debugRenderer.renderDebug(partialTicks, nanoTime);
      }

      this.mc.profiler.endStartSection("destroyProgress");
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      worldrenderer.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), entity, partialTicks);
      this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      GlStateManager.disableBlend();
      this.enableLightmap();
      this.mc.profiler.endStartSection("litParticles");
      particlemanager.renderLitParticles(entity, partialTicks);
      RenderHelper.disableStandardItemLighting();
      this.fogRenderer.setupFog(0, partialTicks);
      this.mc.profiler.endStartSection("particles");
      particlemanager.renderParticles(entity, partialTicks);
      this.disableLightmap();
      GlStateManager.depthMask(false);
      GlStateManager.enableCull();
      this.mc.profiler.endStartSection("weather");
      this.renderRainSnow(partialTicks);
      GlStateManager.depthMask(true);
      worldrenderer.renderWorldBorder(entity, partialTicks);
      GlStateManager.disableBlend();
      GlStateManager.enableCull();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.alphaFunc(516, 0.1F);
      this.fogRenderer.setupFog(0, partialTicks);
      GlStateManager.enableBlend();
      GlStateManager.depthMask(false);
      this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      GlStateManager.shadeModel(7425);
      this.mc.profiler.endStartSection("translucent");
      worldrenderer.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, (double)partialTicks, entity);
      GlStateManager.shadeModel(7424);
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.disableFog();
      if (entity.posY + (double)entity.getEyeHeight() >= 128.0D) {
         this.mc.profiler.endStartSection("aboveClouds");
         this.renderCloudsCheck(worldrenderer, partialTicks, d0, d1, d2);
      }

      this.mc.profiler.endStartSection("forge_render_last");
      net.minecraftforge.client.ForgeHooksClient.dispatchRenderLast(worldrenderer, partialTicks);

      this.mc.profiler.endStartSection("hand");
      if (this.renderHand) {
         GlStateManager.clear(256);
         this.renderHand(partialTicks);
      }

   }

   private void renderCloudsCheck(WorldRenderer renderGlobalIn, float partialTicks, double viewEntityX, double viewEntityY, double viewEntityZ) {
      if (this.mc.gameSettings.shouldRenderClouds() != 0) {
         this.mc.profiler.endStartSection("clouds");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrixf(Matrix4f.perspective(this.getFOVModifier(partialTicks, true), (float)this.mc.mainWindow.getFramebufferWidth() / (float)this.mc.mainWindow.getFramebufferHeight(), 0.05F, this.farPlaneDistance * 4.0F));
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         this.fogRenderer.setupFog(0, partialTicks);
         renderGlobalIn.renderClouds(partialTicks, viewEntityX, viewEntityY, viewEntityZ);
         GlStateManager.disableFog();
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrixf(Matrix4f.perspective(this.getFOVModifier(partialTicks, true), (float)this.mc.mainWindow.getFramebufferWidth() / (float)this.mc.mainWindow.getFramebufferHeight(), 0.05F, this.farPlaneDistance * MathHelper.SQRT_2));
         GlStateManager.matrixMode(5888);
      }

   }

   private void addRainParticles() {
      float f = this.mc.world.getRainStrength(1.0F);
      if (!this.mc.gameSettings.fancyGraphics) {
         f /= 2.0F;
      }

      if (f != 0.0F) {
         this.random.setSeed((long)this.rendererUpdateCount * 312987231L);
         Entity entity = this.mc.getRenderViewEntity();
         IWorldReaderBase iworldreaderbase = this.mc.world;
         BlockPos blockpos = new BlockPos(entity);
         int i = 10;
         double d0 = 0.0D;
         double d1 = 0.0D;
         double d2 = 0.0D;
         int j = 0;
         int k = (int)(100.0F * f * f);
         if (this.mc.gameSettings.particleSetting == 1) {
            k >>= 1;
         } else if (this.mc.gameSettings.particleSetting == 2) {
            k = 0;
         }

         for(int l = 0; l < k; ++l) {
            BlockPos blockpos1 = iworldreaderbase.getHeight(Heightmap.Type.MOTION_BLOCKING, blockpos.add(this.random.nextInt(10) - this.random.nextInt(10), 0, this.random.nextInt(10) - this.random.nextInt(10)));
            Biome biome = iworldreaderbase.getBiome(blockpos1);
            BlockPos blockpos2 = blockpos1.down();
            if (blockpos1.getY() <= blockpos.getY() + 10 && blockpos1.getY() >= blockpos.getY() - 10 && biome.getPrecipitation() == Biome.RainType.RAIN && biome.getTemperature(blockpos1) >= 0.15F) {
               double d3 = this.random.nextDouble();
               double d4 = this.random.nextDouble();
               IBlockState iblockstate = iworldreaderbase.getBlockState(blockpos2);
               IFluidState ifluidstate = iworldreaderbase.getFluidState(blockpos1);
               VoxelShape voxelshape = iblockstate.getCollisionShape(iworldreaderbase, blockpos2);
               double d7 = voxelshape.max(EnumFacing.Axis.Y, d3, d4);
               double d8 = (double)ifluidstate.getHeight();
               double d5;
               double d6;
               if (d7 >= d8) {
                  d5 = d7;
                  d6 = voxelshape.min(EnumFacing.Axis.Y, d3, d4);
               } else {
                  d5 = 0.0D;
                  d6 = 0.0D;
               }

               if (d5 > -Double.MAX_VALUE) {
                  if (!ifluidstate.isTagged(FluidTags.LAVA) && iblockstate.getBlock() != Blocks.MAGMA_BLOCK) {
                     ++j;
                     if (this.random.nextInt(j) == 0) {
                        d0 = (double)blockpos2.getX() + d3;
                        d1 = (double)((float)blockpos2.getY() + 0.1F) + d5 - 1.0D;
                        d2 = (double)blockpos2.getZ() + d4;
                     }

                     this.mc.world.spawnParticle(Particles.RAIN, (double)blockpos2.getX() + d3, (double)((float)blockpos2.getY() + 0.1F) + d5, (double)blockpos2.getZ() + d4, 0.0D, 0.0D, 0.0D);
                  } else {
                     this.mc.world.spawnParticle(Particles.SMOKE, (double)blockpos1.getX() + d3, (double)((float)blockpos1.getY() + 0.1F) - d6, (double)blockpos1.getZ() + d4, 0.0D, 0.0D, 0.0D);
                  }
               }
            }
         }

         if (j > 0 && this.random.nextInt(3) < this.rainSoundCounter++) {
            this.rainSoundCounter = 0;
            if (d1 > (double)(blockpos.getY() + 1) && iworldreaderbase.getHeight(Heightmap.Type.MOTION_BLOCKING, blockpos).getY() > MathHelper.floor((float)blockpos.getY())) {
               this.mc.world.playSound(d0, d1, d2, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F, 0.5F, false);
            } else {
               this.mc.world.playSound(d0, d1, d2, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F, false);
            }
         }

      }
   }

   /**
    * Render rain and snow
    */
   protected void renderRainSnow(float partialTicks) {
      net.minecraftforge.client.IRenderHandler renderer = this.mc.world.getDimension().getWeatherRenderer();
      if (renderer != null) {
         renderer.render(partialTicks, this.mc.world, mc);
         return;
      }
      float f = this.mc.world.getRainStrength(partialTicks);
      if (!(f <= 0.0F)) {
         this.enableLightmap();
         Entity entity = this.mc.getRenderViewEntity();
         World world = this.mc.world;
         int i = MathHelper.floor(entity.posX);
         int j = MathHelper.floor(entity.posY);
         int k = MathHelper.floor(entity.posZ);
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         GlStateManager.disableCull();
         GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.alphaFunc(516, 0.1F);
         double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
         double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
         double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
         int l = MathHelper.floor(d1);
         int i1 = 5;
         if (this.mc.gameSettings.fancyGraphics) {
            i1 = 10;
         }

         int j1 = -1;
         float f1 = (float)this.rendererUpdateCount + partialTicks;
         bufferbuilder.setTranslation(-d0, -d1, -d2);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k1 = k - i1; k1 <= k + i1; ++k1) {
            for(int l1 = i - i1; l1 <= i + i1; ++l1) {
               int i2 = (k1 - k + 16) * 32 + l1 - i + 16;
               double d3 = (double)this.rainXCoords[i2] * 0.5D;
               double d4 = (double)this.rainYCoords[i2] * 0.5D;
               blockpos$mutableblockpos.setPos(l1, 0, k1);
               Biome biome = world.getBiome(blockpos$mutableblockpos);
               if (biome.getPrecipitation() != Biome.RainType.NONE) {
                  int j2 = world.getHeight(Heightmap.Type.MOTION_BLOCKING, blockpos$mutableblockpos).getY();
                  int k2 = j - i1;
                  int l2 = j + i1;
                  if (k2 < j2) {
                     k2 = j2;
                  }

                  if (l2 < j2) {
                     l2 = j2;
                  }

                  int i3 = j2;
                  if (j2 < l) {
                     i3 = l;
                  }

                  if (k2 != l2) {
                     this.random.setSeed((long)(l1 * l1 * 3121 + l1 * 45238971 ^ k1 * k1 * 418711 + k1 * 13761));
                     blockpos$mutableblockpos.setPos(l1, k2, k1);
                     float f2 = biome.getTemperature(blockpos$mutableblockpos);
                     if (f2 >= 0.15F) {
                        if (j1 != 0) {
                           if (j1 >= 0) {
                              tessellator.draw();
                           }

                           j1 = 0;
                           this.mc.getTextureManager().bindTexture(RAIN_TEXTURES);
                           bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                        }

                        double d5 = -((double)(this.rendererUpdateCount + l1 * l1 * 3121 + l1 * 45238971 + k1 * k1 * 418711 + k1 * 13761 & 31) + (double)partialTicks) / 32.0D * (3.0D + this.random.nextDouble());
                        double d6 = (double)((float)l1 + 0.5F) - entity.posX;
                        double d7 = (double)((float)k1 + 0.5F) - entity.posZ;
                        float f3 = MathHelper.sqrt(d6 * d6 + d7 * d7) / (float)i1;
                        float f4 = ((1.0F - f3 * f3) * 0.5F + 0.5F) * f;
                        blockpos$mutableblockpos.setPos(l1, i3, k1);
                        int j3 = world.getCombinedLight(blockpos$mutableblockpos, 0);
                        int k3 = j3 >> 16 & '\uffff';
                        int l3 = j3 & '\uffff';
                        bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)l2, (double)k1 - d4 + 0.5D).tex(0.0D, (double)k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                        bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)l2, (double)k1 + d4 + 0.5D).tex(1.0D, (double)k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                        bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)k2, (double)k1 + d4 + 0.5D).tex(1.0D, (double)l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                        bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)k2, (double)k1 - d4 + 0.5D).tex(0.0D, (double)l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                     } else {
                        if (j1 != 1) {
                           if (j1 >= 0) {
                              tessellator.draw();
                           }

                           j1 = 1;
                           this.mc.getTextureManager().bindTexture(SNOW_TEXTURES);
                           bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                        }

                        double d8 = (double)(-((float)(this.rendererUpdateCount & 511) + partialTicks) / 512.0F);
                        double d9 = this.random.nextDouble() + (double)f1 * 0.01D * (double)((float)this.random.nextGaussian());
                        double d10 = this.random.nextDouble() + (double)(f1 * (float)this.random.nextGaussian()) * 0.001D;
                        double d11 = (double)((float)l1 + 0.5F) - entity.posX;
                        double d12 = (double)((float)k1 + 0.5F) - entity.posZ;
                        float f6 = MathHelper.sqrt(d11 * d11 + d12 * d12) / (float)i1;
                        float f5 = ((1.0F - f6 * f6) * 0.3F + 0.5F) * f;
                        blockpos$mutableblockpos.setPos(l1, i3, k1);
                        int i4 = (world.getCombinedLight(blockpos$mutableblockpos, 0) * 3 + 15728880) / 4;
                        int j4 = i4 >> 16 & '\uffff';
                        int k4 = i4 & '\uffff';
                        bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)l2, (double)k1 - d4 + 0.5D).tex(0.0D + d9, (double)k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                        bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)l2, (double)k1 + d4 + 0.5D).tex(1.0D + d9, (double)k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                        bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)k2, (double)k1 + d4 + 0.5D).tex(1.0D + d9, (double)l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                        bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)k2, (double)k1 - d4 + 0.5D).tex(0.0D + d9, (double)l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                     }
                  }
               }
            }
         }

         if (j1 >= 0) {
            tessellator.draw();
         }

         bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
         GlStateManager.enableCull();
         GlStateManager.disableBlend();
         GlStateManager.alphaFunc(516, 0.1F);
         this.disableLightmap();
      }
   }

   public void setupFogColor(boolean black) {
      this.fogRenderer.applyFog(black);
   }

   public void resetData() {
      this.itemActivationItem = null;
      this.mapItemRenderer.clearLoadedMaps();
   }

   public MapItemRenderer getMapItemRenderer() {
      return this.mapItemRenderer;
   }

   public static void drawNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef(x, y, z);
      GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-viewerYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
      GlStateManager.scalef(-0.025F, -0.025F, 0.025F);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(false);
      if (!isSneaking) {
         GlStateManager.disableDepthTest();
      }

      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      int i = fontRendererIn.getStringWidth(str) / 2;
      GlStateManager.disableTexture2D();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos((double)(-i - 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      bufferbuilder.pos((double)(-i - 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      bufferbuilder.pos((double)(i + 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      bufferbuilder.pos((double)(i + 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      if (!isSneaking) {
         fontRendererIn.drawString(str, (float)(-fontRendererIn.getStringWidth(str) / 2), (float)verticalShift, 553648127);
         GlStateManager.enableDepthTest();
      }

      GlStateManager.depthMask(true);
      fontRendererIn.drawString(str, (float)(-fontRendererIn.getStringWidth(str) / 2), (float)verticalShift, isSneaking ? 553648127 : -1);
      GlStateManager.enableLighting();
      GlStateManager.disableBlend();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   public void displayItemActivation(ItemStack stack) {
      this.itemActivationItem = stack;
      this.itemActivationTicks = 40;
      this.itemActivationOffX = this.random.nextFloat() * 2.0F - 1.0F;
      this.itemActivationOffY = this.random.nextFloat() * 2.0F - 1.0F;
   }

   private void renderItemActivation(int widthsp, int heightScaled, float partialTicks) {
      if (this.itemActivationItem != null && this.itemActivationTicks > 0) {
         int i = 40 - this.itemActivationTicks;
         float f = ((float)i + partialTicks) / 40.0F;
         float f1 = f * f;
         float f2 = f * f1;
         float f3 = 10.25F * f2 * f1 - 24.95F * f1 * f1 + 25.5F * f2 - 13.8F * f1 + 4.0F * f;
         float f4 = f3 * (float)Math.PI;
         float f5 = this.itemActivationOffX * (float)(widthsp / 4);
         float f6 = this.itemActivationOffY * (float)(heightScaled / 4);
         GlStateManager.enableAlphaTest();
         GlStateManager.pushMatrix();
         GlStateManager.pushLightingAttrib();
         GlStateManager.enableDepthTest();
         GlStateManager.disableCull();
         RenderHelper.enableStandardItemLighting();
         GlStateManager.translatef((float)(widthsp / 2) + f5 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), (float)(heightScaled / 2) + f6 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), -50.0F);
         float f7 = 50.0F + 175.0F * MathHelper.sin(f4);
         GlStateManager.scalef(f7, -f7, f7);
         GlStateManager.rotatef(900.0F * MathHelper.abs(MathHelper.sin(f4)), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(6.0F * MathHelper.cos(f * 8.0F), 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(6.0F * MathHelper.cos(f * 8.0F), 0.0F, 0.0F, 1.0F);
         this.mc.getItemRenderer().renderItem(this.itemActivationItem, ItemCameraTransforms.TransformType.FIXED);
         GlStateManager.popAttrib();
         GlStateManager.popMatrix();
         RenderHelper.disableStandardItemLighting();
         GlStateManager.enableCull();
         GlStateManager.disableDepthTest();
      }
   }

   public Minecraft getMinecraft() {
      return this.mc;
   }

   public float getBossColorModifier(float partialTicks) {
      return this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
   }

   public float getFarPlaneDistance() {
      return this.farPlaneDistance;
   }

   @Override
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.SHADERS;
   }
}