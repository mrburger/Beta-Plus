package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleSquidInk extends ParticleSimpleAnimated {
   protected ParticleSquidInk(World p_i48831_1_, double p_i48831_2_, double p_i48831_4_, double p_i48831_6_, double p_i48831_8_, double p_i48831_10_, double p_i48831_12_) {
      super(p_i48831_1_, p_i48831_2_, p_i48831_4_, p_i48831_6_, 0, 8, 0.0F);
      this.particleScale = 5.0F;
      this.setAlphaF(1.0F);
      this.setColor(0.0F, 0.0F, 0.0F);
      this.setParticleTextureIndex(0);
      this.maxAge = (int)((double)(this.particleScale * 12.0F) / (Math.random() * (double)0.8F + (double)0.2F));
      this.canCollide = false;
      this.motionX = p_i48831_8_;
      this.motionY = p_i48831_10_;
      this.motionZ = p_i48831_12_;
      this.setBaseAirFriction(0.0F);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      }

      if (this.age > this.maxAge / 2) {
         this.setAlphaF(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
      }

      this.setParticleTextureIndex(this.textureIdx + this.numAgingFrames - 1 - this.age * this.numAgingFrames / this.maxAge);
      this.move(this.motionX, this.motionY, this.motionZ);
      if (this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).isAir()) {
         this.motionY -= (double)0.008F;
      }

      this.motionX *= (double)0.92F;
      this.motionY *= (double)0.92F;
      this.motionZ *= (double)0.92F;
      if (this.onGround) {
         this.motionX *= (double)0.7F;
         this.motionZ *= (double)0.7F;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleSquidInk(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}