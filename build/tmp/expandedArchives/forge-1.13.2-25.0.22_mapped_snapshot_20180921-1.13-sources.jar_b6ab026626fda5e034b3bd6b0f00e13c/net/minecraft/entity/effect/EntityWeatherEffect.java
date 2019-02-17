package net.minecraft.entity.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public abstract class EntityWeatherEffect extends Entity {
   public EntityWeatherEffect(EntityType<?> type, World p_i48557_2_) {
      super(type, p_i48557_2_);
   }
}