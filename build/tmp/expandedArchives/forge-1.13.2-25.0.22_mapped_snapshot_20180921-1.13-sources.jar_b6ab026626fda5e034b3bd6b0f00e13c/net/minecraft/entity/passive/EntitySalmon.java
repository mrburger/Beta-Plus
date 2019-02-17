package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySalmon extends AbstractGroupFish {
   public EntitySalmon(World p_i48852_1_) {
      super(EntityType.SALMON, p_i48852_1_);
      this.setSize(0.7F, 0.4F);
   }

   public int getMaxGroupSize() {
      return 5;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_SALMON;
   }

   protected ItemStack getFishBucket() {
      return new ItemStack(Items.SALMON_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SALMON_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SALMON_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_SALMON_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_SALMON_FLOP;
   }
}