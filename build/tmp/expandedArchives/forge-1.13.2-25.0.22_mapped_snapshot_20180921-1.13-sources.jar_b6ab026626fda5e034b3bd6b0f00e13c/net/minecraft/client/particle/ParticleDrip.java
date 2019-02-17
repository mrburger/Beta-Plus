package net.minecraft.client.particle;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.init.Particles;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleDrip extends Particle {
   private final Fluid field_204502_a;
   /** The height of the current bob */
   private int bobTimer;

   protected ParticleDrip(World p_i49197_1_, double p_i49197_2_, double p_i49197_4_, double p_i49197_6_, Fluid p_i49197_8_) {
      super(p_i49197_1_, p_i49197_2_, p_i49197_4_, p_i49197_6_, 0.0D, 0.0D, 0.0D);
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      if (p_i49197_8_.isIn(FluidTags.WATER)) {
         this.particleRed = 0.0F;
         this.particleGreen = 0.0F;
         this.particleBlue = 1.0F;
      } else {
         this.particleRed = 1.0F;
         this.particleGreen = 0.0F;
         this.particleBlue = 0.0F;
      }

      this.setParticleTextureIndex(113);
      this.setSize(0.01F, 0.01F);
      this.particleGravity = 0.06F;
      this.field_204502_a = p_i49197_8_;
      this.bobTimer = 40;
      this.maxAge = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
   }

   public int getBrightnessForRender(float partialTick) {
      return this.field_204502_a.isIn(FluidTags.WATER) ? super.getBrightnessForRender(partialTick) : 257;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.field_204502_a.isIn(FluidTags.WATER)) {
         this.particleRed = 0.2F;
         this.particleGreen = 0.3F;
         this.particleBlue = 1.0F;
      } else {
         this.particleRed = 1.0F;
         this.particleGreen = 16.0F / (float)(40 - this.bobTimer + 16);
         this.particleBlue = 4.0F / (float)(40 - this.bobTimer + 8);
      }

      this.motionY -= (double)this.particleGravity;
      if (this.bobTimer-- > 0) {
         this.motionX *= 0.02D;
         this.motionY *= 0.02D;
         this.motionZ *= 0.02D;
         this.setParticleTextureIndex(113);
      } else {
         this.setParticleTextureIndex(112);
      }

      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= (double)0.98F;
      this.motionY *= (double)0.98F;
      this.motionZ *= (double)0.98F;
      if (this.maxAge-- <= 0) {
         this.setExpired();
      }

      if (this.onGround) {
         if (this.field_204502_a.isIn(FluidTags.WATER)) {
            this.setExpired();
            this.world.spawnParticle(Particles.SPLASH, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
         } else {
            this.setParticleTextureIndex(114);
         }

         this.motionX *= (double)0.7F;
         this.motionZ *= (double)0.7F;
      }

      BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
      IFluidState ifluidstate = this.world.getFluidState(blockpos);
      if (ifluidstate.getFluid() == this.field_204502_a) {
         double d0 = (double)((float)MathHelper.floor(this.posY) + ifluidstate.getHeight());
         if (this.posY < d0) {
            this.setExpired();
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class LavaFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleDrip(worldIn, x, y, z, Fluids.LAVA);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WaterFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleDrip(worldIn, x, y, z, Fluids.WATER);
      }
   }
}