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
import net.minecraft.util.ResourceLocation;

public class EffectsChangedTrigger implements ICriterionTrigger<EffectsChangedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("effects_changed");
   private final Map<PlayerAdvancements, EffectsChangedTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> listener) {
      EffectsChangedTrigger.Listeners effectschangedtrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (effectschangedtrigger$listeners == null) {
         effectschangedtrigger$listeners = new EffectsChangedTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, effectschangedtrigger$listeners);
      }

      effectschangedtrigger$listeners.add(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> listener) {
      EffectsChangedTrigger.Listeners effectschangedtrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (effectschangedtrigger$listeners != null) {
         effectschangedtrigger$listeners.remove(listener);
         if (effectschangedtrigger$listeners.isEmpty()) {
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
   public EffectsChangedTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      MobEffectsPredicate mobeffectspredicate = MobEffectsPredicate.deserialize(json.get("effects"));
      return new EffectsChangedTrigger.Instance(mobeffectspredicate);
   }

   public void trigger(EntityPlayerMP player) {
      EffectsChangedTrigger.Listeners effectschangedtrigger$listeners = this.listeners.get(player.getAdvancements());
      if (effectschangedtrigger$listeners != null) {
         effectschangedtrigger$listeners.trigger(player);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final MobEffectsPredicate effects;

      public Instance(MobEffectsPredicate effects) {
         super(EffectsChangedTrigger.ID);
         this.effects = effects;
      }

      public static EffectsChangedTrigger.Instance func_203917_a(MobEffectsPredicate p_203917_0_) {
         return new EffectsChangedTrigger.Instance(p_203917_0_);
      }

      public boolean test(EntityPlayerMP player) {
         return this.effects.test(player);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("effects", this.effects.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<EffectsChangedTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements playerAdvancementsIn) {
         this.playerAdvancements = playerAdvancementsIn;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> listener) {
         this.listeners.add(listener);
      }

      public void remove(ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> listener) {
         this.listeners.remove(listener);
      }

      public void trigger(EntityPlayerMP player) {
         List<ICriterionTrigger.Listener<EffectsChangedTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(player)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}