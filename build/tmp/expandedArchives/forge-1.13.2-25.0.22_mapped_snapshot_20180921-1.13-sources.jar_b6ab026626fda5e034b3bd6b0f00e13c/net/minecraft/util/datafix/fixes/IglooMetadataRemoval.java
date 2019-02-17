package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.TypeReferences;

public class IglooMetadataRemoval extends DataFix {
   public IglooMetadataRemoval(Schema p_i49783_1_, boolean p_i49783_2_) {
      super(p_i49783_1_, p_i49783_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
      Type<?> type1 = this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
      return this.writeFixAndRead("IglooMetadataRemovalFix", type, type1, IglooMetadataRemoval::fixTag);
   }

   private static <T> Dynamic<T> fixTag(Dynamic<T> p_211926_0_) {
      boolean flag = p_211926_0_.get("Children").flatMap(Dynamic::getStream).map((p_211928_0_) -> {
         return p_211928_0_.allMatch(IglooMetadataRemoval::isIglooPiece);
      }).orElse(false);
      return flag ? p_211926_0_.set("id", p_211926_0_.createString("Igloo")).remove("Children") : p_211926_0_.update("Children", IglooMetadataRemoval::removeIglooPieces);
   }

   private static <T> Dynamic<T> removeIglooPieces(Dynamic<T> p_211929_0_) {
      return p_211929_0_.getStream().map((p_211925_0_) -> {
         return p_211925_0_.filter((p_211927_0_) -> {
            return !isIglooPiece(p_211927_0_);
         });
      }).map(p_211929_0_::createList).orElse(p_211929_0_);
   }

   private static boolean isIglooPiece(Dynamic<?> p_211930_0_) {
      return p_211930_0_.getString("id").equals("Iglu");
   }
}