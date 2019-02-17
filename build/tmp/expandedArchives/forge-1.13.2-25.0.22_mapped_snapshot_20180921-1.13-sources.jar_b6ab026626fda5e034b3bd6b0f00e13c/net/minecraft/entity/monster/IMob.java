package net.minecraft.entity.monster;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IAnimal;

public interface IMob extends IAnimal {
   /** Entity selector for IMob types. */
   Predicate<Entity> MOB_SELECTOR = (p_210134_0_) -> {
      return p_210134_0_ instanceof IMob;
   };
   /** Entity selector for IMob types that are not invisible */
   Predicate<Entity> VISIBLE_MOB_SELECTOR = (p_210133_0_) -> {
      return p_210133_0_ instanceof IMob && !p_210133_0_.isInvisible();
   };
}