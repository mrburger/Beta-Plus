package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.datafix.TypeReferences;

public class RecipesRenaming extends DataFix {
   private static final Map<String, String> field_211869_a = ImmutableMap.<String, String>builder().put("minecraft:acacia_bark", "minecraft:acacia_wood").put("minecraft:birch_bark", "minecraft:birch_wood").put("minecraft:dark_oak_bark", "minecraft:dark_oak_wood").put("minecraft:jungle_bark", "minecraft:jungle_wood").put("minecraft:oak_bark", "minecraft:oak_wood").put("minecraft:spruce_bark", "minecraft:spruce_wood").build();

   public RecipesRenaming(Schema p_i49780_1_, boolean p_i49780_2_) {
      super(p_i49780_1_, p_i49780_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, String>> type = DSL.named(TypeReferences.RECIPE.typeName(), DSL.namespacedString());
      if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.RECIPE))) {
         throw new IllegalStateException("Recipe type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("Recipes renamening fix", type, (p_211866_0_) -> {
            return (p_211867_0_) -> {
               return p_211867_0_.mapSecond((p_211868_0_) -> {
                  return field_211869_a.getOrDefault(p_211868_0_, p_211868_0_);
               });
            };
         });
      }
   }
}