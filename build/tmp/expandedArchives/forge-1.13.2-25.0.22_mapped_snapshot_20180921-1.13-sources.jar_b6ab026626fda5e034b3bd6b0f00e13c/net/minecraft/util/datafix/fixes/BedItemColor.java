package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class BedItemColor extends DataFix {
   public BedItemColor(Schema p_i49689_1_, boolean p_i49689_2_) {
      super(p_i49689_1_, p_i49689_2_);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
      return this.fixTypeEverywhereTyped("BedItemColorFix", this.getInputSchema().getType(TypeReferences.ITEM_STACK), (p_207435_1_) -> {
         Optional<Pair<String, String>> optional = p_207435_1_.getOptional(opticfinder);
         if (optional.isPresent() && Objects.equals(optional.get().getSecond(), "minecraft:bed")) {
            Dynamic<?> dynamic = p_207435_1_.get(DSL.remainderFinder());
            if (dynamic.getShort("Damage") == 0) {
               return p_207435_1_.set(DSL.remainderFinder(), dynamic.set("Damage", dynamic.createShort((short)14)));
            }
         }

         return p_207435_1_;
      });
   }
}