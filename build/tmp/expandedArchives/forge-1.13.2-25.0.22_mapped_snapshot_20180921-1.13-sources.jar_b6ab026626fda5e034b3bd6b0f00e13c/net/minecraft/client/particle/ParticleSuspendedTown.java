package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleSuspendedTown extends Particle {
   protected ParticleSuspendedTown(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double speedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, speedIn);
      float f = this.rand.nextFloat() * 0.1F + 0.2F;
      this.particleRed = f;
      this.particleGreen = f;
      this.particleBlue = f;
      this.setParticleTextureIndex(0);
      this.setSize(0.02F, 0.02F);
      this.particleScale *= this.rand.nextFloat() * 0.6F + 0.5F;
      this.motionX *= (double)0.02F;
      this.motionY *= (double)0.02F;
      this.motionZ *= (double)0.02F;
      this.maxAge = (int)(20.0D / (Math.random() * 0.8D + 0.2D));
   }

   public void move(double x, double y, double z) {
      this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
      this.resetPositionToBB();
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.99D;
      this.motionY *= 0.99D;
      this.motionZ *= 0.99D;
      if (this.maxAge-- <= 0) {
         this.setExpired();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class DolphinSpeedFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Particle particle = new ParticleSuspendedTown(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         particle.setColor(0.3F, 0.5F, 1.0F);
         particle.setAlphaF(1.0F - worldIn.rand.nextFloat() * 0.7F);
         particle.setMaxAge(particle.getMaxAge() / 2);
         return particle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleSuspendedTown(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class HappyVillagerFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Particle particle = new ParticleSuspendedTown(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         particle.setParticleTextureIndex(82);
         particle.setColor(1.0F, 1.0F, 1.0F);
         return particle;
      }
   }
}