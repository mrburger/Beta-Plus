package net.minecraft.data;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FurnaceRecipeBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Item result;
   private final Ingredient ingredient;
   private final float experience;
   private final int cookingTime;
   private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
   /** Currently inaccessible and always null (as of 1.13-pre6) */
   private String group;

   public FurnaceRecipeBuilder(Ingredient ingredientIn, IItemProvider resultIn, float experience, int cookingTimeIn) {
      this.result = resultIn.asItem();
      this.ingredient = ingredientIn;
      this.experience = experience;
      this.cookingTime = cookingTimeIn;
   }

   /**
    * Creates a builder for a furnace recipe.
    */
   public static FurnaceRecipeBuilder furnaceRecipe(Ingredient ingredientIn, IItemProvider result, float experienceIn, int cookingTimeIn) {
      return new FurnaceRecipeBuilder(ingredientIn, result, experienceIn, cookingTimeIn);
   }

   /**
    * Adds a criterion needed to unlock the recipe.
    *  
    * @param name Name for the criterion.
    * @param criterionIn The criterion.
    */
   public FurnaceRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
      this.advancementBuilder.withCriterion(name, criterionIn);
      return this;
   }

   /**
    * Builds this recipe into an {@link IFinishedRecipe}.
    *  
    * @param consumerIn Consumer to register to.
    */
   public void build(Consumer<IFinishedRecipe> consumerIn) {
      this.build(consumerIn, IRegistry.field_212630_s.getKey(this.result));
   }

   /**
    * Builds this recipe into an {@link IFinishedRecipe}. Use {@link #build(Consumer)} if save is the same as the ID for
    * the result.
    *  
    * @param consumerIn Consumer to register to.
    * @param save The ID to save to.
    */
   public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
      ResourceLocation resourcelocation = IRegistry.field_212630_s.getKey(this.result);
      if ((new ResourceLocation(save)).equals(resourcelocation)) {
         throw new IllegalStateException("Smelting Recipe " + save + " should remove its 'save' argument");
      } else {
         this.build(consumerIn, new ResourceLocation(save));
      }
   }

   /**
    * Builds this recipe into an {@link IFinishedRecipe}.
    *  
    * @param consumerIn Consumer to register to.
    * @param id The ID to save to.
    */
   public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
      this.validate(id);
      this.advancementBuilder.withParentId(new ResourceLocation("minecraft:recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(RequirementsStrategy.OR);
      consumerIn.accept(new FurnaceRecipeBuilder.Result(id, this.group == null ? "" : this.group, this.ingredient, this.result, this.experience, this.cookingTime, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getGroup().func_200300_c() + "/" + id.getPath())));
   }

   /**
    * Makes sure that this recipe is valid and obtainable.
    *  
    * @param id ID used for logging.
    */
   private void validate(ResourceLocation id) {
      if (this.advancementBuilder.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + id);
      }
   }

   public static class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final String group;
      private final Ingredient ingredient;
      private final Item result;
      private final float experience;
      private final int cookingTime;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      public Result(ResourceLocation idIn, String groupIn, Ingredient ingredientIn, Item resultIn, float experienceIn, int cookingTimeIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
         this.id = idIn;
         this.group = groupIn;
         this.ingredient = ingredientIn;
         this.result = resultIn;
         this.experience = experienceIn;
         this.cookingTime = cookingTimeIn;
         this.advancementBuilder = advancementBuilderIn;
         this.advancementId = advancementIdIn;
      }

      /**
       * Gets the JSON for the recipe.
       */
      public JsonObject getRecipeJson() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("type", "smelting");
         if (!this.group.isEmpty()) {
            jsonobject.addProperty("group", this.group);
         }

         jsonobject.add("ingredient", this.ingredient.toJson());
         jsonobject.addProperty("result", IRegistry.field_212630_s.getKey(this.result).toString());
         jsonobject.addProperty("experience", this.experience);
         jsonobject.addProperty("cookingtime", this.cookingTime);
         return jsonobject;
      }

      /**
       * Gets the ID for the recipe.
       */
      public ResourceLocation getID() {
         return this.id;
      }

      /**
       * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
       */
      @Nullable
      public JsonObject getAdvancementJson() {
         return this.advancementBuilder.serialize();
      }

      /**
       * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson}
       * is non-null.
       */
      @Nullable
      public ResourceLocation getAdvancementID() {
         return this.advancementId;
      }
   }
}