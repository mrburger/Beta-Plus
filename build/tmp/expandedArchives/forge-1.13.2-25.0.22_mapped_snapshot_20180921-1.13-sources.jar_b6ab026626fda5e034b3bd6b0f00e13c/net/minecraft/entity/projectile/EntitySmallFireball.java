package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySmallFireball extends EntityFireball {
   public EntitySmallFireball(World worldIn) {
      super(EntityType.SMALL_FIREBALL, worldIn, 0.3125F, 0.3125F);
   }

   public EntitySmallFireball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
      super(EntityType.SMALL_FIREBALL, shooter, accelX, accelY, accelZ, worldIn, 0.3125F, 0.3125F);
   }

   public EntitySmallFireball(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
      super(EntityType.SMALL_FIREBALL, x, y, z, accelX, accelY, accelZ, worldIn, 0.3125F, 0.3125F);
   }

   /**
    * Called when this EntityFireball hits a block or entity.
    */
   protected void onImpact(RayTraceResult result) {
      if (!this.world.isRemote) {
         if (result.entity != null) {
            if (!result.entity.isImmuneToFire()) {
               result.entity.setFire(5);
               boolean flag = result.entity.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0F);
               if (flag) {
                  this.applyEnchantments(this.shootingEntity, result.entity);
               }
            }
         } else {
            boolean flag1 = true;
            if (this.shootingEntity != null && this.shootingEntity instanceof EntityLiving) {
               flag1 = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
            }

            if (flag1) {
               BlockPos blockpos = result.getBlockPos().offset(result.sideHit);
               if (this.world.isAirBlock(blockpos)) {
                  this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
               }
            }
         }

         this.remove();
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
}