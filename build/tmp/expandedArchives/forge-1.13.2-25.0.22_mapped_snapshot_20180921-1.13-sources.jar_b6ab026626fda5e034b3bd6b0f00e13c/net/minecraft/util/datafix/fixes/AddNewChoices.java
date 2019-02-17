package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;

public class AddNewChoices extends DataFix {
   private final String field_206292_a;
   private final TypeReference field_206293_b;

   public AddNewChoices(Schema p_i49692_1_, String p_i49692_2_, TypeReference p_i49692_3_) {
      super(p_i49692_1_, true);
      this.field_206292_a = p_i49692_2_;
      this.field_206293_b = p_i49692_3_;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType<?> taggedchoicetype = this.getInputSchema().findChoiceType(this.field_206293_b);
      TaggedChoiceType<?> taggedchoicetype1 = this.getOutputSchema().findChoiceType(this.field_206293_b);
      return this.cap(this.field_206292_a, taggedchoicetype, taggedchoicetype1);
   }

   protected final <K> TypeRewriteRule cap(String p_206290_1_, TaggedChoiceType<K> p_206290_2_, TaggedChoiceType<?> p_206290_3_) {
      if (p_206290_2_.getKeyType() != p_206290_3_.getKeyType()) {
         throw new IllegalStateException("Could not inject: key type is not the same");
      } else {
         return this.fixTypeEverywhere(p_206290_1_, p_206290_2_, p_206290_3_, (p_209687_2_) -> {
            return (p_206291_2_) -> {
               if (!((TaggedChoiceType)p_206290_3_).hasType(p_206291_2_.getFirst())) {
                  throw new IllegalArgumentException(String.format("Unknown type %s in %s ", p_206291_2_.getFirst(), this.field_206293_b));
               } else {
                  return (com.mojang.datafixers.util.Pair)p_206291_2_;
               }
            };
         });
      }
   }
}