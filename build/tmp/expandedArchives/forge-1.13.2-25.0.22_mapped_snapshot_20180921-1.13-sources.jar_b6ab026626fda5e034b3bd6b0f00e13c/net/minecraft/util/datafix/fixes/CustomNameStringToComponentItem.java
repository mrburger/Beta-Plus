package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class CustomNameStringToComponentItem extends DataFix {
   public CustomNameStringToComponentItem(Schema p_i49644_1_, boolean p_i49644_2_) {
      super(p_i49644_1_, p_i49644_2_);
   }

   private Dynamic<?> fixTag(Dynamic<?> p_209621_1_) {
      Optional<? extends Dynamic<?>> optional = p_209621_1_.get("display");
      if (optional.isPresent()) {
         Dynamic<?> dynamic = optional.get();
         Optional<String> optional1 = dynamic.get("Name").flatMap(Dynamic::getStringValue);
         if (optional1.isPresent()) {
            dynamic = dynamic.set("Name", dynamic.createString(ITextComponent.Serializer.toJson(new TextComponentString(optional1.get()))));
         } else {
            Optional<String> optional2 = dynamic.get("LocName").flatMap(Dynamic::getStringValue);
            if (optional2.isPresent()) {
               dynamic = dynamic.set("Name", dynamic.createString(ITextComponent.Serializer.toJson(new TextComponentTranslation(optional2.get()))));
               dynamic = dynamic.remove("LocName");
            }
         }

         return p_209621_1_.set("display", dynamic);
      } else {
         return p_209621_1_;
      }
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder<?> opticfinder = type.findField("tag");
      return this.fixTypeEverywhereTyped("ItemCustomNameToComponentFix", type, (p_207467_2_) -> {
         return p_207467_2_.updateTyped(opticfinder, (p_207469_1_) -> {
            return p_207469_1_.update(DSL.remainderFinder(), this::fixTag);
         });
      });
   }
}