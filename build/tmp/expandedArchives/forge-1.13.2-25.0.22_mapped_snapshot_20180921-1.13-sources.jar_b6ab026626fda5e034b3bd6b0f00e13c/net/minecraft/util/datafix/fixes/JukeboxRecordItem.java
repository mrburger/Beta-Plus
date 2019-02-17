package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.TypeReferences;

public class JukeboxRecordItem extends NamedEntityFix {
   public JukeboxRecordItem(Schema p_i49683_1_, boolean p_i49683_2_) {
      super(p_i49683_1_, p_i49683_2_, "BlockEntityJukeboxFix", TypeReferences.BLOCK_ENTITY, "minecraft:jukebox");
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:jukebox");
      Type<?> type1 = type.findFieldType("RecordItem");
      OpticFinder<?> opticfinder = DSL.fieldFinder("RecordItem", type1);
      Dynamic<?> dynamic = p_207419_1_.get(DSL.remainderFinder());
      int i = dynamic.getInt("Record");
      if (i > 0) {
         dynamic.remove("Record");
         String s = ItemStackDataFlattening.updateItem(ItemIntIDToString.getItem(i), 0);
         if (s != null) {
            Dynamic<?> dynamic1 = dynamic.emptyMap();
            dynamic1 = dynamic1.set("id", dynamic1.createString(s));
            dynamic1 = dynamic1.set("Count", dynamic1.createByte((byte)1));
            return p_207419_1_.set(opticfinder, type1.readTyped(dynamic1).getSecond().orElseThrow(() -> {
               return new IllegalStateException("Could not create record item stack.");
            })).set(DSL.remainderFinder(), dynamic);
         }
      }

      return p_207419_1_;
   }
}