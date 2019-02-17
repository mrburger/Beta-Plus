package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleSpell extends Particle {
   private static final Random RANDOM = new Random();
   /** Base spell texture index */
   private int baseSpellTextureIndex = 128;

   protected ParticleSpell(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double p_i1229_8_, double ySpeed, double p_i1229_12_) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.5D - RANDOM.nextDouble(), ySpeed, 0.5D - RANDOM.nextDouble());
      this.motionY *= (double)0.2F;
      if (p_i1229_8_ == 0.0D && p_i1229_12_ == 0.0D) {
         this.motionX *= (double)0.1F;
         this.motionZ *= (double)0.1F;
      }

      this.particleScale *= 0.75F;
      this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.canCollide = false;
   }

   public boolean shouldDisableDepth() {
      return true;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      }

      this.setParticleTextureIndex(this.baseSpellTextureIndex + 7 - this.age * 8 / this.maxAge);
      this.motionY += 0.004D;
      this.move(this.motionX, this.motionY, this.motionZ);
      if (this.posY == this.prevPosY) {
         this.motionX *= 1.1D;
         this.motionZ *= 1.1D;
      }

      this.motionX *= (double)0.96F;
      this.motionY *= (double)0.96F;
      this.motionZ *= (double)0.96F;
      if (this.onGround) {
         this.motionX *= (double)0.7F;
         this.motionZ *= (double)0.7F;
      }

   }

   /**
    * Sets the base spell texture index
    */
   public void setBaseSpellTextureIndex(int baseSpellTextureIndexIn) {
      this.baseSpellTextureIndex = baseSpellTextureIndexIn;
   }

   @OnlyIn(Dist.CLIENT)
   public static class AmbientMobFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Particle particle = new ParticleSpell(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         particle.setAlphaF(0.15F);
         particle.setColor((float)xSpeed, (float)ySpeed, (float)zSpeed);
         return particle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleSpell(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class InstantFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Particle particle = new ParticleSpell(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         ((ParticleSpell)particle).setBaseSpellTextureIndex(144);
         return particle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class MobFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Particle particle = new ParticleSpell(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         particle.setColor((float)xSpeed, (float)ySpeed, (float)zSpeed);
         return particle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WitchFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Particle particle = new ParticleSpell(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         ((ParticleSpell)particle).setBaseSpellTextureIndex(144);
         float f = worldIn.rand.nextFloat() * 0.5F + 0.35F;
         particle.setColor(1.0F * f, 0.0F * f, 1.0F * f);
         return particle;
      }
   }
}