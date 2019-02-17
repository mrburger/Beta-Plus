package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Particles;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleLava extends Particle {
   private final float lavaParticleScale;

   protected ParticleLava(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
      this.motionX *= (double)0.8F;
      this.motionY *= (double)0.8F;
      this.motionZ *= (double)0.8F;
      this.motionY = (double)(this.rand.nextFloat() * 0.4F + 0.05F);
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.particleScale *= this.rand.nextFloat() * 2.0F + 0.2F;
      this.lavaParticleScale = this.particleScale;
      this.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      this.setParticleTextureIndex(49);
   }

   public int getBrightnessForRender(float partialTick) {
      int i = super.getBrightnessForRender(partialTick);
      int j = 240;
      int k = i >> 16 & 255;
      return 240 | k << 16;
   }

   /**
    * Renders the particle
    */
   public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float f = ((float)this.age + partialTicks) / (float)this.maxAge;
      this.particleScale = this.lavaParticleScale * (1.0F - f * f);
      super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      }

      float f = (float)this.age / (float)this.maxAge;
      if (this.rand.nextFloat() > f) {
         this.world.spawnParticle(Particles.SMOKE, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
      }

      this.motionY -= 0.03D;
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= (double)0.999F;
      this.motionY *= (double)0.999F;
      this.motionZ *= (double)0.999F;
      if (this.onGround) {
         this.motionX *= (double)0.7F;
         this.motionZ *= (double)0.7F;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleLava(worldIn, x, y, z);
      }
   }
}