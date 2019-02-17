package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class SkeletonSplit extends EntityRenameHelper {
   public SkeletonSplit(Schema p_i49653_1_, boolean p_i49653_2_) {
      super("EntitySkeletonSplitFix", p_i49653_1_, p_i49653_2_);
   }

   protected Pair<String, Dynamic<?>> getNewNameAndTag(String p_209758_1_, Dynamic<?> p_209758_2_) {
      if (Objects.equals(p_209758_1_, "Skeleton")) {
         int i = p_209758_2_.getInt("SkeletonType");
         if (i == 1) {
            p_209758_1_ = "WitherSkeleton";
         } else if (i == 2) {
            p_209758_1_ = "Stray";
         }
      }

      return Pair.of(p_209758_1_, p_209758_2_);
   }
}