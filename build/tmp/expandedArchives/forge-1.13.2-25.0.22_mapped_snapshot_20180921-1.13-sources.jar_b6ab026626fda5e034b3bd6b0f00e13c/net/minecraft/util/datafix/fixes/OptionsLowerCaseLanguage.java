package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class OptionsLowerCaseLanguage extends DataFix {
   public OptionsLowerCaseLanguage(Schema p_i49619_1_, boolean p_i49619_2_) {
      super(p_i49619_1_, p_i49619_2_);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsLowerCaseLanguageFix", this.getInputSchema().getType(TypeReferences.OPTIONS), (p_206281_0_) -> {
         return p_206281_0_.update(DSL.remainderFinder(), (p_207428_0_) -> {
            Optional<String> optional = p_207428_0_.get("lang").flatMap(Dynamic::getStringValue);
            return optional.isPresent() ? p_207428_0_.set("lang", p_207428_0_.createString(optional.get().toLowerCase(Locale.ROOT))) : p_207428_0_;
         });
      });
   }
}