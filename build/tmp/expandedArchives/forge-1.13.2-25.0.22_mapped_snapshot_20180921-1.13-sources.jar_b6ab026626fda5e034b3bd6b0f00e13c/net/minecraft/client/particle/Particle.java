package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Particle {
   private static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   protected World world;
   protected double prevPosX;
   protected double prevPosY;
   protected double prevPosZ;
   protected double posX;
   protected double posY;
   protected double posZ;
   protected double motionX;
   protected double motionY;
   protected double motionZ;
   private AxisAlignedBB boundingBox = EMPTY_AABB;
   protected boolean onGround;
   /** Determines if particle to block collision is to be used */
   protected boolean canCollide;
   protected boolean isExpired;
   protected float width = 0.6F;
   protected float height = 1.8F;
   protected Random rand = new Random();
   protected int particleTextureIndexX;
   protected int particleTextureIndexY;
   protected float particleTextureJitterX;
   protected float particleTextureJitterY;
   protected int age;
   protected int maxAge;
   protected float particleScale;
   protected float particleGravity;
   /** The red amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0. */
   protected float particleRed;
   /** The green amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0. */
   protected float particleGreen;
   /** The blue amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0. */
   protected float particleBlue;
   /** Particle alpha */
   protected float particleAlpha = 1.0F;
   protected TextureAtlasSprite particleTexture;
   /** The amount the particle will be rotated in rendering. */
   protected float particleAngle;
   /** The particle angle from the last tick. Appears to be used for calculating the rendered angle with partial ticks. */
   protected float prevParticleAngle;
   public static double interpPosX;
   public static double interpPosY;
   public static double interpPosZ;
   public static Vec3d cameraViewDir;

   protected Particle(World worldIn, double posXIn, double posYIn, double posZIn) {
      this.world = worldIn;
      this.setSize(0.2F, 0.2F);
      this.setPosition(posXIn, posYIn, posZIn);
      this.prevPosX = posXIn;
      this.prevPosY = posYIn;
      this.prevPosZ = posZIn;
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
      this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
      this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
      this.maxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
      this.age = 0;
      this.canCollide = true;
   }

   public Particle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      this(worldIn, xCoordIn, yCoordIn, zCoordIn);
      this.motionX = xSpeedIn + (Math.random() * 2.0D - 1.0D) * (double)0.4F;
      this.motionY = ySpeedIn + (Math.random() * 2.0D - 1.0D) * (double)0.4F;
      this.motionZ = zSpeedIn + (Math.random() * 2.0D - 1.0D) * (double)0.4F;
      float f = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
      float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      this.motionX = this.motionX / (double)f1 * (double)f * (double)0.4F;
      this.motionY = this.motionY / (double)f1 * (double)f * (double)0.4F + (double)0.1F;
      this.motionZ = this.motionZ / (double)f1 * (double)f * (double)0.4F;
   }

   public Particle multiplyVelocity(float multiplier) {
      this.motionX *= (double)multiplier;
      this.motionY = (this.motionY - (double)0.1F) * (double)multiplier + (double)0.1F;
      this.motionZ *= (double)multiplier;
      return this;
   }

   public Particle multipleParticleScaleBy(float scale) {
      this.setSize(0.2F * scale, 0.2F * scale);
      this.particleScale *= scale;
      return this;
   }

   public void setColor(float particleRedIn, float particleGreenIn, float particleBlueIn) {
      this.particleRed = particleRedIn;
      this.particleGreen = particleGreenIn;
      this.particleBlue = particleBlueIn;
   }

   /**
    * Sets the particle alpha (float)
    */
   public void setAlphaF(float alpha) {
      this.particleAlpha = alpha;
   }

   public boolean shouldDisableDepth() {
      return false;
   }

   public float getRedColorF() {
      return this.particleRed;
   }

   public float getGreenColorF() {
      return this.particleGreen;
   }

   public float getBlueColorF() {
      return this.particleBlue;
   }

   public void setMaxAge(int particleLifeTime) {
      this.maxAge = particleLifeTime;
   }

   public int getMaxAge() {
      return this.maxAge;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      }

      this.motionY -= 0.04D * (double)this.particleGravity;
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= (double)0.98F;
      this.motionY *= (double)0.98F;
      this.motionZ *= (double)0.98F;
      if (this.onGround) {
         this.motionX *= (double)0.7F;
         this.motionZ *= (double)0.7F;
      }

   }

   /**
    * Renders the particle
    */
   public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float f = (float)this.particleTextureIndexX / 32.0F;
      float f1 = f + 0.03121875F;
      float f2 = (float)this.particleTextureIndexY / 32.0F;
      float f3 = f2 + 0.03121875F;
      float f4 = 0.1F * this.particleScale;
      if (this.particleTexture != null) {
         f = this.particleTexture.getMinU();
         f1 = this.particleTexture.getMaxU();
         f2 = this.particleTexture.getMinV();
         f3 = this.particleTexture.getMaxV();
      }

      float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
      float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
      float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
      int i = this.getBrightnessForRender(partialTicks);
      int j = i >> 16 & '\uffff';
      int k = i & '\uffff';
      Vec3d[] avec3d = new Vec3d[]{new Vec3d((double)(-rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(-rotationYZ * f4 - rotationXZ * f4)), new Vec3d((double)(-rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(-rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double)(rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double)(rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(rotationYZ * f4 - rotationXZ * f4))};
      if (this.particleAngle != 0.0F) {
         float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
         float f9 = MathHelper.cos(f8 * 0.5F);
         float f10 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.x;
         float f11 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.y;
         float f12 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.z;
         Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

         for(int l = 0; l < 4; ++l) {
            avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale((double)(2.0F * f9)));
         }
      }

      buffer.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      buffer.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      buffer.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      buffer.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
   }

   /**
    * Retrieve what effect layer (what texture) the particle should be rendered with. 0 for the particle sprite sheet, 1
    * for the main Texture atlas, and 3 for a custom texture
    */
   public int getFXLayer() {
      return 0;
   }

   /**
    * Sets the texture used by the particle.
    */
   public void setParticleTexture(TextureAtlasSprite texture) {
      int i = this.getFXLayer();
      if (i == 1) {
         this.particleTexture = texture;
      } else {
         throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
      }
   }

   /**
    * Public method to set private field particleTextureIndex.
    */
   public void setParticleTextureIndex(int particleTextureIndex) {
      if (this.getFXLayer() != 0) {
         throw new RuntimeException("Invalid call to Particle.setMiscTex");
      } else {
         this.particleTextureIndexX = particleTextureIndex % 16;
         this.particleTextureIndexY = particleTextureIndex / 16;
      }
   }

   public void nextTextureIndexX() {
      ++this.particleTextureIndexX;
   }

   public String toString() {
      return this.getClass().getSimpleName() + ", Pos (" + this.posX + "," + this.posY + "," + this.posZ + "), RGBA (" + this.particleRed + "," + this.particleGreen + "," + this.particleBlue + "," + this.particleAlpha + "), Age " + this.age;
   }

   /**
    * Called to indicate that this particle effect has expired and should be discontinued.
    */
   public void setExpired() {
      this.isExpired = true;
   }

   protected void setSize(float particleWidth, float particleHeight) {
      if (particleWidth != this.width || particleHeight != this.height) {
         this.width = particleWidth;
         this.height = particleHeight;
         AxisAlignedBB axisalignedbb = this.getBoundingBox();
         double d0 = (axisalignedbb.minX + axisalignedbb.maxX - (double)particleWidth) / 2.0D;
         double d1 = (axisalignedbb.minZ + axisalignedbb.maxZ - (double)particleWidth) / 2.0D;
         this.setBoundingBox(new AxisAlignedBB(d0, axisalignedbb.minY, d1, d0 + (double)this.width, axisalignedbb.minY + (double)this.height, d1 + (double)this.width));
      }

   }

   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      float f = this.width / 2.0F;
      float f1 = this.height;
      this.setBoundingBox(new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
   }

   public void move(double x, double y, double z) {
      double d0 = y;
      double origX = x;
      double origZ = z;
      if (this.canCollide && (x != 0.0D || y != 0.0D || z != 0.0D)) {
         ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream<>(this.world.getCollisionBoxes((Entity)null, this.getBoundingBox(), x, y, z));
         y = VoxelShapes.func_212437_a(EnumFacing.Axis.Y, this.getBoundingBox(), reuseablestream.func_212761_a(), y);
         this.setBoundingBox(this.getBoundingBox().offset(0.0D, y, 0.0D));
         x = VoxelShapes.func_212437_a(EnumFacing.Axis.X, this.getBoundingBox(), reuseablestream.func_212761_a(), x);
         if (x != 0.0D) {
            this.setBoundingBox(this.getBoundingBox().offset(x, 0.0D, 0.0D));
         }

         z = VoxelShapes.func_212437_a(EnumFacing.Axis.Z, this.getBoundingBox(), reuseablestream.func_212761_a(), z);
         if (z != 0.0D) {
            this.setBoundingBox(this.getBoundingBox().offset(0.0D, 0.0D, z));
         }
      } else {
         this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
      }

      this.resetPositionToBB();
      this.onGround = d0 != y && d0 < 0.0D;
      if (origX != x) {
         this.motionX = 0.0D;
      }

      if (origZ != z) {
         this.motionZ = 0.0D;
      }

   }

   protected void resetPositionToBB() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
      this.posY = axisalignedbb.minY;
      this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;
   }

   public int getBrightnessForRender(float partialTick) {
      BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
      return this.world.isBlockLoaded(blockpos) ? this.world.getCombinedLight(blockpos, 0) : 0;
   }

   /**
    * Returns true if this effect has not yet expired. "I feel happy! I feel happy!"
    */
   public boolean isAlive() {
      return !this.isExpired;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   public void setBoundingBox(AxisAlignedBB bb) {
      this.boundingBox = bb;
   }
}