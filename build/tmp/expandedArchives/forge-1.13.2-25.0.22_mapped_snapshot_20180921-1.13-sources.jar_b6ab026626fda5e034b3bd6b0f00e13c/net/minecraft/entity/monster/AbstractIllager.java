package net.minecraft.entity.monster;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractIllager extends EntityMob {
   protected static final DataParameter<Byte> AGGRESSIVE = EntityDataManager.createKey(AbstractIllager.class, DataSerializers.BYTE);

   protected AbstractIllager(EntityType<?> type, World p_i48556_2_) {
      super(type, p_i48556_2_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(AGGRESSIVE, (byte)0);
   }

   @OnlyIn(Dist.CLIENT)
   protected boolean isAggressive(int mask) {
      int i = this.dataManager.get(AGGRESSIVE);
      return (i & mask) != 0;
   }

   protected void setAggressive(int mask, boolean value) {
      int i = this.dataManager.get(AGGRESSIVE);
      if (value) {
         i = i | mask;
      } else {
         i = i & ~mask;
      }

      this.dataManager.set(AGGRESSIVE, (byte)(i & 255));
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.ILLAGER;
   }

   @OnlyIn(Dist.CLIENT)
   public AbstractIllager.IllagerArmPose getArmPose() {
      return AbstractIllager.IllagerArmPose.CROSSED;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum IllagerArmPose {
      CROSSED,
      ATTACKING,
      SPELLCASTING,
      BOW_AND_ARROW;
   }
}