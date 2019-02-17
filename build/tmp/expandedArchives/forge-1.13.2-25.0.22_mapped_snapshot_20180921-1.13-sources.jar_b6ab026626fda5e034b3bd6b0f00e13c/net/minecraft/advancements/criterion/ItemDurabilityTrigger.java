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

public class ItemDurabilityTrigger implements ICriterionTrigger<ItemDurabilityTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("item_durability_changed");
   private final Map<PlayerAdvancements, ItemDurabilityTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> listener) {
      ItemDurabilityTrigger.Listeners itemdurabilitytrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (itemdurabilitytrigger$listeners == null) {
         itemdurabilitytrigger$listeners = new ItemDurabilityTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, itemdurabilitytrigger$listeners);
      }

      itemdurabilitytrigger$listeners.add(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> listener) {
      ItemDurabilityTrigger.Listeners itemdurabilitytrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (itemdurabilitytrigger$listeners != null) {
         itemdurabilitytrigger$listeners.remove(listener);
         if (itemdurabilitytrigger$listeners.isEmpty()) {
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
   public ItemDurabilityTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("durability"));
      MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(json.get("delta"));
      return new ItemDurabilityTrigger.Instance(itempredicate, minmaxbounds$intbound, minmaxbounds$intbound1);
   }

   public void trigger(EntityPlayerMP player, ItemStack itemIn, int newDurability) {
      ItemDurabilityTrigger.Listeners itemdurabilitytrigger$listeners = this.listeners.get(player.getAdvancements());
      if (itemdurabilitytrigger$listeners != null) {
         itemdurabilitytrigger$listeners.trigger(itemIn, newDurability);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.IntBound durability;
      private final MinMaxBounds.IntBound delta;

      public Instance(ItemPredicate p_i49703_1_, MinMaxBounds.IntBound p_i49703_2_, MinMaxBounds.IntBound p_i49703_3_) {
         super(ItemDurabilityTrigger.ID);
         this.item = p_i49703_1_;
         this.durability = p_i49703_2_;
         this.delta = p_i49703_3_;
      }

      public static ItemDurabilityTrigger.Instance func_211182_a(ItemPredicate p_211182_0_, MinMaxBounds.IntBound p_211182_1_) {
         return new ItemDurabilityTrigger.Instance(p_211182_0_, p_211182_1_, MinMaxBounds.IntBound.UNBOUNDED);
      }

      public boolean test(ItemStack item, int p_193197_2_) {
         if (!this.item.test(item)) {
            return false;
         } else if (!this.durability.test(item.getMaxDamage() - p_193197_2_)) {
            return false;
         } else {
            return this.delta.test(item.getDamage() - p_193197_2_);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serialize());
         jsonobject.add("durability", this.durability.serialize());
         jsonobject.add("delta", this.delta.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements playerAdvancementsIn) {
         this.playerAdvancements = playerAdvancementsIn;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> listener) {
         this.listeners.add(listener);
      }

      public void remove(ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> listener) {
         this.listeners.remove(listener);
      }

      public void trigger(ItemStack item, int newDurability) {
         List<ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(item, newDurability)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}