package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Particles;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class PhaseLanding extends PhaseBase {
   private Vec3d targetLocation;

   public PhaseLanding(EntityDragon dragonIn) {
      super(dragonIn);
   }

   /**
    * Generates particle effects appropriate to the phase (or sometimes sounds).
    * Called by dragon's onLivingUpdate. Only used when worldObj.isRemote.
    */
   public void clientTick() {
      Vec3d vec3d = this.dragon.getHeadLookVec(1.0F).normalize();
      vec3d.rotateYaw((-(float)Math.PI / 4F));
      double d0 = this.dragon.dragonPartHead.posX;
      double d1 = this.dragon.dragonPartHead.posY + (double)(this.dragon.dragonPartHead.height / 2.0F);
      double d2 = this.dragon.dragonPartHead.posZ;

      for(int i = 0; i < 8; ++i) {
         double d3 = d0 + this.dragon.getRNG().nextGaussian() / 2.0D;
         double d4 = d1 + this.dragon.getRNG().nextGaussian() / 2.0D;
         double d5 = d2 + this.dragon.getRNG().nextGaussian() / 2.0D;
         this.dragon.world.spawnParticle(Particles.DRAGON_BREATH, d3, d4, d5, -vec3d.x * (double)0.08F + this.dragon.motionX, -vec3d.y * (double)0.3F + this.dragon.motionY, -vec3d.z * (double)0.08F + this.dragon.motionZ);
         vec3d.rotateYaw(0.19634955F);
      }

   }

   /**
    * Gives the phase a chance to update its status.
    * Called by dragon's onLivingUpdate. Only used when !worldObj.isRemote.
    */
   public void serverTick() {
      if (this.targetLocation == null) {
         this.targetLocation = new Vec3d(this.dragon.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION));
      }

      if (this.targetLocation.squareDistanceTo(this.dragon.posX, this.dragon.posY, this.dragon.posZ) < 1.0D) {
         this.dragon.getPhaseManager().getPhase(PhaseType.SITTING_FLAMING).resetFlameCount();
         this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
      }

   }

   /**
    * Returns the maximum amount dragon may rise or fall during this phase
    */
   public float getMaxRiseOrFall() {
      return 1.5F;
   }

   public float getYawFactor() {
      float f = MathHelper.sqrt(this.dragon.motionX * this.dragon.motionX + this.dragon.motionZ * this.dragon.motionZ) + 1.0F;
      float f1 = Math.min(f, 40.0F);
      return f1 / f;
   }

   /**
    * Called when this phase is set to active
    */
   public void initPhase() {
      this.targetLocation = null;
   }

   /**
    * Returns the location the dragon is flying toward
    */
   @Nullable
   public Vec3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<PhaseLanding> getType() {
      return PhaseType.LANDING;
   }
}