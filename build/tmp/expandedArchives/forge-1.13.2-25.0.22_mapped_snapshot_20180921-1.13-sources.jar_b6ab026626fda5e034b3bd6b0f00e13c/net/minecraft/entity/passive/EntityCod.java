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

public class EntityCod extends AbstractGroupFish {
   public EntityCod(World p_i48854_1_) {
      super(EntityType.COD, p_i48854_1_);
      this.setSize(0.5F, 0.3F);
   }

   protected ItemStack getFishBucket() {
      return new ItemStack(Items.COD_BUCKET);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_COD;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_COD_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_COD_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_COD_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_COD_FLOP;
   }
}