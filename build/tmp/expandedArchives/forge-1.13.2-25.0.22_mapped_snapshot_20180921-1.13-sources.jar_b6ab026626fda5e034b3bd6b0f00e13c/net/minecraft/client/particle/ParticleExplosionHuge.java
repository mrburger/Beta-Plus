package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Particles;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleExplosionHuge extends Particle {
   private int timeSinceStart;
   /** the maximum time for the explosion */
   private final int maximumTime = 8;

   protected ParticleExplosionHuge(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double p_i1214_8_, double p_i1214_10_, double p_i1214_12_) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
   }

   /**
    * Renders the particle
    */
   public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
   }

   public void tick() {
      for(int i = 0; i < 6; ++i) {
         double d0 = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
         double d1 = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
         double d2 = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
         this.world.spawnParticle(Particles.EXPLOSION, d0, d1, d2, (double)((float)this.timeSinceStart / (float)this.maximumTime), 0.0D, 0.0D);
      }

      ++this.timeSinceStart;
      if (this.timeSinceStart == this.maximumTime) {
         this.setExpired();
      }

   }

   /**
    * Retrieve what effect layer (what texture) the particle should be rendered with. 0 for the particle sprite sheet, 1
    * for the main Texture atlas, and 3 for a custom texture
    */
   public int getFXLayer() {
      return 1;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleExplosionHuge(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}