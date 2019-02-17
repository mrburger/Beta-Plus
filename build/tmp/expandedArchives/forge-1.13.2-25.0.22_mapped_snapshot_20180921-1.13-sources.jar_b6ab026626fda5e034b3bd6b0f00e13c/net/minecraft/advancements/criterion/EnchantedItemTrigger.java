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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantedItemTrigger implements ICriterionTrigger<EnchantedItemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("enchanted_item");
   private final Map<PlayerAdvancements, EnchantedItemTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> listener) {
      EnchantedItemTrigger.Listeners enchanteditemtrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (enchanteditemtrigger$listeners == null) {
         enchanteditemtrigger$listeners = new EnchantedItemTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, enchanteditemtrigger$listeners);
      }

      enchanteditemtrigger$listeners.add(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> listener) {
      EnchantedItemTrigger.Listeners enchanteditemtrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (enchanteditemtrigger$listeners != null) {
         enchanteditemtrigger$listeners.remove(listener);
         if (enchanteditemtrigger$listeners.isEmpty()) {
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
   public EnchantedItemTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("levels"));
      return new EnchantedItemTrigger.Instance(itempredicate, minmaxbounds$intbound);
   }

   public void trigger(EntityPlayerMP player, ItemStack item, int levelsSpent) {
      EnchantedItemTrigger.Listeners enchanteditemtrigger$listeners = this.listeners.get(player.getAdvancements());
      if (enchanteditemtrigger$listeners != null) {
         enchanteditemtrigger$listeners.trigger(item, levelsSpent);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.IntBound levels;

      public Instance(ItemPredicate p_i49731_1_, MinMaxBounds.IntBound p_i49731_2_) {
         super(EnchantedItemTrigger.ID);
         this.item = p_i49731_1_;
         this.levels = p_i49731_2_;
      }

      public static EnchantedItemTrigger.Instance func_203918_c() {
         return new EnchantedItemTrigger.Instance(ItemPredicate.ANY, MinMaxBounds.IntBound.UNBOUNDED);
      }

      public boolean test(ItemStack item, int levelsIn) {
         if (!this.item.test(item)) {
            return false;
         } else {
            return this.levels.test(levelsIn);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serialize());
         jsonobject.add("levels", this.levels.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<EnchantedItemTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements playerAdvancementsIn) {
         this.playerAdvancements = playerAdvancementsIn;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> listener) {
         this.listeners.add(listener);
      }

      public void remove(ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> listener) {
         this.listeners.remove(listener);
      }

      public void trigger(ItemStack item, int levelsIn) {
         List<ICriterionTrigger.Listener<EnchantedItemTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(item, levelsIn)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}