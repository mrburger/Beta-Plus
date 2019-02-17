package net.minecraft.entity;

import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public interface IEntityMultiPart {
   World getWorld();

   boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage);

   EntityType<?> getType();
}