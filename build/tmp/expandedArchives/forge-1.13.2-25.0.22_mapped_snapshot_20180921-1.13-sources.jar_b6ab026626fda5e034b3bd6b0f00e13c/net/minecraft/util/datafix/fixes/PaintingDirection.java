package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.TypeReferences;

public class PaintingDirection extends DataFix {
   private static final int[][] field_210992_a = new int[][]{{0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {1, 0, 0}};

   public PaintingDirection(Schema p_i49660_1_, boolean p_i49660_2_) {
      super(p_i49660_1_, p_i49660_2_);
   }

   private Dynamic<?> doFix(Dynamic<?> p_209748_1_, boolean p_209748_2_, boolean p_209748_3_) {
      if ((p_209748_2_ || p_209748_3_) && !p_209748_1_.get("Facing").flatMap(Dynamic::getNumberValue).isPresent()) {
         int i;
         if (p_209748_1_.get("Direction").flatMap(Dynamic::getNumberValue).isPresent()) {
            i = p_209748_1_.getByte("Direction") % field_210992_a.length;
            int[] aint = field_210992_a[i];
            p_209748_1_ = p_209748_1_.set("TileX", p_209748_1_.createInt(p_209748_1_.getInt("TileX") + aint[0]));
            p_209748_1_ = p_209748_1_.set("TileY", p_209748_1_.createInt(p_209748_1_.getInt("TileY") + aint[1]));
            p_209748_1_ = p_209748_1_.set("TileZ", p_209748_1_.createInt(p_209748_1_.getInt("TileZ") + aint[2]));
            p_209748_1_ = p_209748_1_.remove("Direction");
            if (p_209748_3_ && p_209748_1_.get("ItemRotation").flatMap(Dynamic::getNumberValue).isPresent()) {
               p_209748_1_ = p_209748_1_.set("ItemRotation", p_209748_1_.createByte((byte)(p_209748_1_.getByte("ItemRotation") * 2)));
            }
         } else {
            i = p_209748_1_.getByte("Dir") % field_210992_a.length;
            p_209748_1_ = p_209748_1_.remove("Dir");
         }

         p_209748_1_ = p_209748_1_.set("Facing", p_209748_1_.createByte((byte)i));
      }

      return p_209748_1_;
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "Painting");
      OpticFinder<?> opticfinder = DSL.namedChoice("Painting", type);
      Type<?> type1 = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "ItemFrame");
      OpticFinder<?> opticfinder1 = DSL.namedChoice("ItemFrame", type1);
      Type<?> type2 = this.getInputSchema().getType(TypeReferences.ENTITY);
      TypeRewriteRule typerewriterule = this.fixTypeEverywhereTyped("EntityPaintingFix", type2, (p_206332_3_) -> {
         return p_206332_3_.updateTyped(opticfinder, type, (p_206330_1_) -> {
            return p_206330_1_.update(DSL.remainderFinder(), (p_207457_1_) -> {
               return this.doFix(p_207457_1_, true, false);
            });
         });
      });
      TypeRewriteRule typerewriterule1 = this.fixTypeEverywhereTyped("EntityItemFrameFix", type2, (p_206331_3_) -> {
         return p_206331_3_.updateTyped(opticfinder1, type1, (p_206329_1_) -> {
            return p_206329_1_.update(DSL.remainderFinder(), (p_207455_1_) -> {
               return this.doFix(p_207455_1_, false, true);
            });
         });
      });
      return TypeRewriteRule.seq(typerewriterule, typerewriterule1);
   }
}