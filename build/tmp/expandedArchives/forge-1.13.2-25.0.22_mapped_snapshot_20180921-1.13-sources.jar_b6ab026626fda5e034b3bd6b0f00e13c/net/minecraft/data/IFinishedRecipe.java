package net.minecraft.data;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public interface IFinishedRecipe {
   /**
    * Gets the JSON for the recipe.
    */
   JsonObject getRecipeJson();

   /**
    * Gets the ID for the recipe.
    */
   ResourceLocation getID();

   /**
    * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
    */
   @Nullable
   JsonObject getAdvancementJson();

   /**
    * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson} is
    * non-null.
    */
   @Nullable
   ResourceLocation getAdvancementID();
}