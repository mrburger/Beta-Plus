package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleMobAppearance extends Particle {
   private EntityLivingBase entity;

   protected ParticleMobAppearance(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      this.particleGravity = 0.0F;
      this.maxAge = 30;
   }

   /**
    * Retrieve what effect layer (what texture) the particle should be rendered with. 0 for the particle sprite sheet, 1
    * for the main Texture atlas, and 3 for a custom texture
    */
   public int getFXLayer() {
      return 3;
   }

   public void tick() {
      super.tick();
      if (this.entity == null) {
         EntityElderGuardian entityelderguardian = new EntityElderGuardian(this.world);
         entityelderguardian.setGhost();
         this.entity = entityelderguardian;
      }

   }

   /**
    * Renders the particle
    */
   public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      if (this.entity != null) {
         RenderManager rendermanager = Minecraft.getInstance().getRenderManager();
         rendermanager.setRenderPosition(Particle.interpPosX, Particle.interpPosY, Particle.interpPosZ);
         float f = 0.42553192F;
         float f1 = ((float)this.age + partialTicks) / (float)this.maxAge;
         GlStateManager.depthMask(true);
         GlStateManager.enableBlend();
         GlStateManager.enableDepthTest();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         float f2 = 240.0F;
         OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, 240.0F, 240.0F);
         GlStateManager.pushMatrix();
         float f3 = 0.05F + 0.5F * MathHelper.sin(f1 * (float)Math.PI);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, f3);
         GlStateManager.translatef(0.0F, 1.8F, 0.0F);
         GlStateManager.rotatef(180.0F - entityIn.rotationYaw, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(60.0F - 150.0F * f1 - entityIn.rotationPitch, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(0.0F, -0.4F, -1.5F);
         GlStateManager.scalef(0.42553192F, 0.42553192F, 0.42553192F);
         this.entity.rotationYaw = 0.0F;
         this.entity.rotationYawHead = 0.0F;
         this.entity.prevRotationYaw = 0.0F;
         this.entity.prevRotationYawHead = 0.0F;
         rendermanager.renderEntity(this.entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);
         GlStateManager.popMatrix();
         GlStateManager.enableDepthTest();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleMobAppearance(worldIn, x, y, z);
      }
   }
}