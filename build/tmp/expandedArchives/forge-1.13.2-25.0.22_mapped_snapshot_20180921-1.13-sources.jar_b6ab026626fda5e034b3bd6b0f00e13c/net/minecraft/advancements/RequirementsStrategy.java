package net.minecraft.advancements;

import java.util.Collection;

public interface RequirementsStrategy {
   RequirementsStrategy AND = (p_lambda$static$0_0_) -> {
      String[][] astring = new String[p_lambda$static$0_0_.size()][];
      int i = 0;

      for(String s : p_lambda$static$0_0_) {
         astring[i++] = new String[]{s};
      }

      return astring;
   };
   RequirementsStrategy OR = (p_lambda$static$1_0_) -> {
      return new String[][]{p_lambda$static$1_0_.toArray(new String[0])};
   };

   String[][] createRequirements(Collection<String> p_createRequirements_1_);
}