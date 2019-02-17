package net.minecraft.data;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.util.ResourceLocation;

public class CustomRecipeBuilder {
   private final RecipeSerializers.SimpleSerializer<?> serializer;

   public CustomRecipeBuilder(RecipeSerializers.SimpleSerializer<?> serializerIn) {
      this.serializer = serializerIn;
   }

   /**
    * Creates a builder for a recipe with a custom serializer.
    *  
    * @param serializerIn The serializer; see {@link RecipeSerializers}.
    */
   public static CustomRecipeBuilder customRecipe(RecipeSerializers.SimpleSerializer<?> serializerIn) {
      return new CustomRecipeBuilder(serializerIn);
   }

   /**
    * Builds this recipe into an {@link IFinishedRecipe}.
    *  
    * @param consumerIn Consumer to register to.
    * @param id The ID to save to.
    */
   public void build(Consumer<IFinishedRecipe> consumerIn, final String id) {
      consumerIn.accept(new IFinishedRecipe() {
         /**
          * Gets the JSON for the recipe.
          */
         public JsonObject getRecipeJson() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("type", CustomRecipeBuilder.this.serializer.getId());
            return jsonobject;
         }

         /**
          * Gets the ID for the recipe.
          */
         public ResourceLocation getID() {
            return new ResourceLocation(id);
         }

         /**
          * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
          */
         @Nullable
         public JsonObject getAdvancementJson() {
            return null;
         }

         /**
          * Gets the ID for the advancement associated with this recipe. Should not be null if {@link
          * #getAdvancementJson} is non-null.
          */
         @Nullable
         public ResourceLocation getAdvancementID() {
            return new ResourceLocation("");
         }
      });
   }
}