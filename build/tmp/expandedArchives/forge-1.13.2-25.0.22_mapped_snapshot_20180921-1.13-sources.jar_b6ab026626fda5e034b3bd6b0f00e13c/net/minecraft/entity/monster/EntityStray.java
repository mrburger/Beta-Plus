package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityStray extends AbstractSkeleton {
   public EntityStray(World worldIn) {
      super(EntityType.STRAY, worldIn);
   }

   public boolean canSpawn(IWorld worldIn, boolean p_205020_2_) {
      return super.canSpawn(worldIn, p_205020_2_) && (p_205020_2_ || worldIn.canSeeSky(new BlockPos(this)));
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_STRAY;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_STRAY_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_STRAY_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_STRAY_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_STRAY_STEP;
   }

   protected EntityArrow getArrow(float p_190726_1_) {
      EntityArrow entityarrow = super.getArrow(p_190726_1_);
      if (entityarrow instanceof EntityTippedArrow) {
         ((EntityTippedArrow)entityarrow).addEffect(new PotionEffect(MobEffects.SLOWNESS, 600));
      }

      return entityarrow;
   }
}