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

public class HeightmapRenamingFix extends DataFix {
   public HeightmapRenamingFix(Schema p_i49646_1_, boolean p_i49646_2_) {
      super(p_i49646_1_, p_i49646_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
      OpticFinder<?> opticfinder = type.findField("Level");
      return this.fixTypeEverywhereTyped("HeightmapRenamingFix", type, (p_207306_2_) -> {
         return p_207306_2_.updateTyped(opticfinder, (p_207307_1_) -> {
            return p_207307_1_.update(DSL.remainderFinder(), this::fix);
         });
      });
   }

   private Dynamic<?> fix(Dynamic<?> p_209766_1_) {
      Optional<? extends Dynamic<?>> optional = p_209766_1_.get("Heightmaps");
      if (!optional.isPresent()) {
         return p_209766_1_;
      } else {
         Dynamic<?> dynamic = optional.get();
         Optional<? extends Dynamic<?>> optional1 = dynamic.get("LIQUID");
         if (optional1.isPresent()) {
            dynamic = dynamic.remove("LIQUID");
            dynamic = dynamic.set("WORLD_SURFACE_WG", optional1.get());
         }

         Optional<? extends Dynamic<?>> optional2 = dynamic.get("SOLID");
         if (optional2.isPresent()) {
            dynamic = dynamic.remove("SOLID");
            dynamic = dynamic.set("OCEAN_FLOOR_WG", optional2.get());
            dynamic = dynamic.set("OCEAN_FLOOR", optional2.get());
         }

         Optional<? extends Dynamic<?>> optional3 = dynamic.get("LIGHT");
         if (optional3.isPresent()) {
            dynamic = dynamic.remove("LIGHT");
            dynamic = dynamic.set("LIGHT_BLOCKING", optional3.get());
         }

         Optional<? extends Dynamic<?>> optional4 = dynamic.get("RAIN");
         if (optional4.isPresent()) {
            dynamic = dynamic.remove("RAIN");
            dynamic = dynamic.set("MOTION_BLOCKING", optional4.get());
            dynamic = dynamic.set("MOTION_BLOCKING_NO_LEAVES", optional4.get());
         }

         return p_209766_1_.set("Heightmaps", dynamic);
      }
   }
}