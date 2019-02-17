package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Barrier extends Particle {
   protected Barrier(World p_i48192_1_, double p_i48192_2_, double p_i48192_4_, double p_i48192_6_, IItemProvider p_i48192_8_) {
      super(p_i48192_1_, p_i48192_2_, p_i48192_4_, p_i48192_6_, 0.0D, 0.0D, 0.0D);
      this.setParticleTexture(Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(p_i48192_8_));
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      this.particleGravity = 0.0F;
      this.maxAge = 80;
      this.canCollide = false;
   }

   /**
    * Retrieve what effect layer (what texture) the particle should be rendered with. 0 for the particle sprite sheet, 1
    * for the main Texture atlas, and 3 for a custom texture
    */
   public int getFXLayer() {
      return 1;
   }

   /**
    * Renders the particle
    */
   public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float f = this.particleTexture.getMinU();
      float f1 = this.particleTexture.getMaxU();
      float f2 = this.particleTexture.getMinV();
      float f3 = this.particleTexture.getMaxV();
      float f4 = 0.5F;
      float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
      float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
      float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
      int i = this.getBrightnessForRender(partialTicks);
      int j = i >> 16 & '\uffff';
      int k = i & '\uffff';
      buffer.pos((double)(f5 - rotationX * 0.5F - rotationXY * 0.5F), (double)(f6 - rotationZ * 0.5F), (double)(f7 - rotationYZ * 0.5F - rotationXZ * 0.5F)).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
      buffer.pos((double)(f5 - rotationX * 0.5F + rotationXY * 0.5F), (double)(f6 + rotationZ * 0.5F), (double)(f7 - rotationYZ * 0.5F + rotationXZ * 0.5F)).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
      buffer.pos((double)(f5 + rotationX * 0.5F + rotationXY * 0.5F), (double)(f6 + rotationZ * 0.5F), (double)(f7 + rotationYZ * 0.5F + rotationXZ * 0.5F)).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
      buffer.pos((double)(f5 + rotationX * 0.5F - rotationXY * 0.5F), (double)(f6 - rotationZ * 0.5F), (double)(f7 + rotationYZ * 0.5F - rotationXZ * 0.5F)).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new Barrier(worldIn, x, y, z, Blocks.BARRIER.asItem());
      }
   }
}