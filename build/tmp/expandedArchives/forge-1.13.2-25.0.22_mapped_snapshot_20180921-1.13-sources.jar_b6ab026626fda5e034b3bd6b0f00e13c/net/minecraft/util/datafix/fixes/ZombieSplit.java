package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class ZombieSplit extends EntityRenameHelper {
   public ZombieSplit(Schema outputSchema, boolean changesType) {
      super("EntityZombieSplitFix", outputSchema, changesType);
   }

   protected Pair<String, Dynamic<?>> getNewNameAndTag(String p_209758_1_, Dynamic<?> p_209758_2_) {
      if (Objects.equals("Zombie", p_209758_1_)) {
         String s = "Zombie";
         int i = p_209758_2_.getInt("ZombieType");
         switch(i) {
         case 0:
         default:
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
            s = "ZombieVillager";
            p_209758_2_ = p_209758_2_.set("Profession", p_209758_2_.createInt(i - 1));
            break;
         case 6:
            s = "Husk";
         }

         p_209758_2_ = p_209758_2_.remove("ZombieType");
         return Pair.of(s, p_209758_2_);
      } else {
         return Pair.of(p_209758_1_, p_209758_2_);
      }
   }
}