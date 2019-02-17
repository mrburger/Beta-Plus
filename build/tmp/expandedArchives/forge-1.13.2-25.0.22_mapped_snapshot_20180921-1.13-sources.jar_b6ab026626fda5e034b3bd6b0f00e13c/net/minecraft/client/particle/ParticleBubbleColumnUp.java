package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleBubbleColumnUp extends Particle {
   protected ParticleBubbleColumnUp(World p_i48833_1_, double p_i48833_2_, double p_i48833_4_, double p_i48833_6_, double p_i48833_8_, double p_i48833_10_, double p_i48833_12_) {
      super(p_i48833_1_, p_i48833_2_, p_i48833_4_, p_i48833_6_, p_i48833_8_, p_i48833_10_, p_i48833_12_);
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.setParticleTextureIndex(32);
      this.setSize(0.02F, 0.02F);
      this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
      this.motionX = p_i48833_8_ * (double)0.2F + (Math.random() * 2.0D - 1.0D) * (double)0.02F;
      this.motionY = p_i48833_10_ * (double)0.2F + (Math.random() * 2.0D - 1.0D) * (double)0.02F;
      this.motionZ = p_i48833_12_ * (double)0.2F + (Math.random() * 2.0D - 1.0D) * (double)0.02F;
      this.maxAge = (int)(40.0D / (Math.random() * 0.8D + 0.2D));
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY += 0.005D;
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= (double)0.85F;
      this.motionY *= (double)0.85F;
      this.motionZ *= (double)0.85F;
      if (!this.world.getFluidState(new BlockPos(this.posX, this.posY, this.posZ)).isTagged(FluidTags.WATER)) {
         this.setExpired();
      }

      if (this.maxAge-- <= 0) {
         this.setExpired();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleBubbleColumnUp(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}