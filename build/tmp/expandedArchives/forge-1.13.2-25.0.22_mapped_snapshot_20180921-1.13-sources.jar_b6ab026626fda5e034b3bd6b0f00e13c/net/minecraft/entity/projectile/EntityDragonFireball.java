package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityDragonFireball extends EntityFireball {
   public EntityDragonFireball(World worldIn) {
      super(EntityType.DRAGON_FIREBALL, worldIn, 1.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public EntityDragonFireball(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
      super(EntityType.DRAGON_FIREBALL, x, y, z, accelX, accelY, accelZ, worldIn, 1.0F, 1.0F);
   }

   public EntityDragonFireball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
      super(EntityType.DRAGON_FIREBALL, shooter, accelX, accelY, accelZ, worldIn, 1.0F, 1.0F);
   }

   /**
    * Called when this EntityFireball hits a block or entity.
    */
   protected void onImpact(RayTraceResult result) {
      if (result.entity == null || !result.entity.isEntityEqual(this.shootingEntity)) {
         if (!this.world.isRemote) {
            List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getBoundingBox().grow(4.0D, 2.0D, 4.0D));
            EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.posX, this.posY, this.posZ);
            entityareaeffectcloud.setOwner(this.shootingEntity);
            entityareaeffectcloud.func_195059_a(Particles.DRAGON_BREATH);
            entityareaeffectcloud.setRadius(3.0F);
            entityareaeffectcloud.setDuration(600);
            entityareaeffectcloud.setRadiusPerTick((7.0F - entityareaeffectcloud.getRadius()) / (float)entityareaeffectcloud.getDuration());
            entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, 1));
            if (!list.isEmpty()) {
               for(EntityLivingBase entitylivingbase : list) {
                  double d0 = this.getDistanceSq(entitylivingbase);
                  if (d0 < 16.0D) {
                     entityareaeffectcloud.setPosition(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ);
                     break;
                  }
               }
            }

            this.world.playEvent(2006, new BlockPos(this.posX, this.posY, this.posZ), 0);
            this.world.spawnEntity(entityareaeffectcloud);
            this.remove();
         }

      }
   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return false;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      return false;
   }

   protected IParticleData func_195057_f() {
      return Particles.DRAGON_BREATH;
   }

   protected boolean isFireballFiery() {
      return false;
   }
}