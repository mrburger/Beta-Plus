package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class RecipeUnlockedTrigger implements ICriterionTrigger<RecipeUnlockedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("recipe_unlocked");
   private final Map<PlayerAdvancements, RecipeUnlockedTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> listener) {
      RecipeUnlockedTrigger.Listeners recipeunlockedtrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (recipeunlockedtrigger$listeners == null) {
         recipeunlockedtrigger$listeners = new RecipeUnlockedTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, recipeunlockedtrigger$listeners);
      }

      recipeunlockedtrigger$listeners.add(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> listener) {
      RecipeUnlockedTrigger.Listeners recipeunlockedtrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (recipeunlockedtrigger$listeners != null) {
         recipeunlockedtrigger$listeners.remove(listener);
         if (recipeunlockedtrigger$listeners.isEmpty()) {
            this.listeners.remove(playerAdvancementsIn);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
      this.listeners.remove(playerAdvancementsIn);
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public RecipeUnlockedTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(json, "recipe"));
      return new RecipeUnlockedTrigger.Instance(resourcelocation);
   }

   public void trigger(EntityPlayerMP player, IRecipe recipe) {
      RecipeUnlockedTrigger.Listeners recipeunlockedtrigger$listeners = this.listeners.get(player.getAdvancements());
      if (recipeunlockedtrigger$listeners != null) {
         recipeunlockedtrigger$listeners.trigger(recipe);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final ResourceLocation field_212243_a;

      public Instance(ResourceLocation p_i48179_1_) {
         super(RecipeUnlockedTrigger.ID);
         this.field_212243_a = p_i48179_1_;
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("recipe", this.field_212243_a.toString());
         return jsonobject;
      }

      public boolean test(IRecipe recipe) {
         return this.field_212243_a.equals(recipe.getId());
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements playerAdvancementsIn) {
         this.playerAdvancements = playerAdvancementsIn;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> listener) {
         this.listeners.add(listener);
      }

      public void remove(ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> listener) {
         this.listeners.remove(listener);
      }

      public void trigger(IRecipe recipe) {
         List<ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(recipe)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}