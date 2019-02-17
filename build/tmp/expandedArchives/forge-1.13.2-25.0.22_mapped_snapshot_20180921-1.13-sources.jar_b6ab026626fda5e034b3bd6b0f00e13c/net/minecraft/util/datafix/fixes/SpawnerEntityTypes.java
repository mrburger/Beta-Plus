package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class SpawnerEntityTypes extends DataFix {
   public SpawnerEntityTypes(Schema p_i49626_1_, boolean p_i49626_2_) {
      super(p_i49626_1_, p_i49626_2_);
   }

   private Dynamic<?> fix(Dynamic<?> p_209659_1_) {
      if (!"MobSpawner".equals(p_209659_1_.getString("id"))) {
         return p_209659_1_;
      } else {
         Optional<String> optional = p_209659_1_.get("EntityId").flatMap(Dynamic::getStringValue);
         if (optional.isPresent()) {
            Dynamic<?> dynamic = DataFixUtils.orElse(p_209659_1_.get("SpawnData"), p_209659_1_.emptyMap());
            dynamic = dynamic.set("id", dynamic.createString(optional.get().isEmpty() ? "Pig" : optional.get()));
            p_209659_1_ = p_209659_1_.set("SpawnData", dynamic);
            p_209659_1_ = p_209659_1_.remove("EntityId");
         }

         Optional<? extends Stream<? extends Dynamic<?>>> optional1 = p_209659_1_.get("SpawnPotentials").flatMap(Dynamic::getStream);
         if (optional1.isPresent()) {
            p_209659_1_ = p_209659_1_.set("SpawnPotentials", p_209659_1_.createList(optional1.get().map((p_209657_0_) -> {
               Optional<String> optional2 = p_209657_0_.get("Type").flatMap(Dynamic::getStringValue);
               if (optional2.isPresent()) {
                  Dynamic<?> dynamic1 = DataFixUtils.orElse(p_209657_0_.get("Properties"), p_209657_0_.emptyMap()).set("id", p_209657_0_.createString(optional2.get()));
                  return p_209657_0_.set("Entity", dynamic1).remove("Type").remove("Properties");
               } else {
                  return p_209657_0_;
               }
            })));
         }

         return p_209659_1_;
      }
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getOutputSchema().getType(TypeReferences.UNTAGGED_SPAWNER);
      return this.fixTypeEverywhereTyped("MobSpawnerEntityIdentifiersFix", this.getInputSchema().getType(TypeReferences.UNTAGGED_SPAWNER), type, (p_206369_2_) -> {
         Dynamic<?> dynamic = p_206369_2_.get(DSL.remainderFinder());
         dynamic = dynamic.set("id", dynamic.createString("MobSpawner"));
         Pair<?, ? extends Optional<? extends Typed<?>>> pair = type.readTyped(this.fix(dynamic));
         return !pair.getSecond().isPresent() ? p_206369_2_ : pair.getSecond().get();
      });
   }
}