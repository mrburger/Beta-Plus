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
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class UsedTotemTrigger implements ICriterionTrigger<UsedTotemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("used_totem");
   private final Map<PlayerAdvancements, UsedTotemTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<UsedTotemTrigger.Instance> listener) {
      UsedTotemTrigger.Listeners usedtotemtrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (usedtotemtrigger$listeners == null) {
         usedtotemtrigger$listeners = new UsedTotemTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, usedtotemtrigger$listeners);
      }

      usedtotemtrigger$listeners.add(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<UsedTotemTrigger.Instance> listener) {
      UsedTotemTrigger.Listeners usedtotemtrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (usedtotemtrigger$listeners != null) {
         usedtotemtrigger$listeners.remove(listener);
         if (usedtotemtrigger$listeners.isEmpty()) {
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
   public UsedTotemTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      return new UsedTotemTrigger.Instance(itempredicate);
   }

   public void trigger(EntityPlayerMP player, ItemStack item) {
      UsedTotemTrigger.Listeners usedtotemtrigger$listeners = this.listeners.get(player.getAdvancements());
      if (usedtotemtrigger$listeners != null) {
         usedtotemtrigger$listeners.trigger(item);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate item;

      public Instance(ItemPredicate item) {
         super(UsedTotemTrigger.ID);
         this.item = item;
      }

      public static UsedTotemTrigger.Instance func_203941_a(IItemProvider p_203941_0_) {
         return new UsedTotemTrigger.Instance(ItemPredicate.Builder.create().func_200308_a(p_203941_0_).build());
      }

      public boolean test(ItemStack item) {
         return this.item.test(item);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<UsedTotemTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements playerAdvancementsIn) {
         this.playerAdvancements = playerAdvancementsIn;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<UsedTotemTrigger.Instance> listener) {
         this.listeners.add(listener);
      }

      public void remove(ICriterionTrigger.Listener<UsedTotemTrigger.Instance> listener) {
         this.listeners.remove(listener);
      }

      public void trigger(ItemStack item) {
         List<ICriterionTrigger.Listener<UsedTotemTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<UsedTotemTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(item)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<UsedTotemTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}