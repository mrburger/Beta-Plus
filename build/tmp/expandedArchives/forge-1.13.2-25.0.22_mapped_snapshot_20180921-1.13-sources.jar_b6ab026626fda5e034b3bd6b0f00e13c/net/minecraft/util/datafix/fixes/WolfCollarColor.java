package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class WolfCollarColor extends NamedEntityFix {
   public WolfCollarColor(Schema p_i49649_1_, boolean p_i49649_2_) {
      super(p_i49649_1_, p_i49649_2_, "EntityWolfColorFix", TypeReferences.ENTITY, "minecraft:wolf");
   }

   public Dynamic<?> fixTag(Dynamic<?> p_209655_1_) {
      return p_209655_1_.update("CollarColor", (p_209654_0_) -> {
         return p_209654_0_.createByte((byte)(15 - p_209654_0_.getNumberValue(0).intValue()));
      });
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), this::fixTag);
   }
}