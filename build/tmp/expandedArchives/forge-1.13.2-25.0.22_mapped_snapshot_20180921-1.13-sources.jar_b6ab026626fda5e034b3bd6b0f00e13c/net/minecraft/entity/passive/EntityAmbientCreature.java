package net.minecraft.entity.passive;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class EntityAmbientCreature extends EntityLiving implements IAnimal {
   protected EntityAmbientCreature(EntityType<?> type, World p_i48570_2_) {
      super(type, p_i48570_2_);
   }

   public boolean canBeLeashedTo(EntityPlayer player) {
      return false;
   }
}