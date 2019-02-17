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
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;

public class PositionTrigger implements ICriterionTrigger<PositionTrigger.Instance> {
   private final ResourceLocation id;
   private final Map<PlayerAdvancements, PositionTrigger.Listeners> listeners = Maps.newHashMap();

   public PositionTrigger(ResourceLocation id) {
      this.id = id;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<PositionTrigger.Instance> listener) {
      PositionTrigger.Listeners positiontrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (positiontrigger$listeners == null) {
         positiontrigger$listeners = new PositionTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, positiontrigger$listeners);
      }

      positiontrigger$listeners.add(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<PositionTrigger.Instance> listener) {
      PositionTrigger.Listeners positiontrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (positiontrigger$listeners != null) {
         positiontrigger$listeners.remove(listener);
         if (positiontrigger$listeners.isEmpty()) {
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
   public PositionTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      LocationPredicate locationpredicate = LocationPredicate.deserialize(json);
      return new PositionTrigger.Instance(this.id, locationpredicate);
   }

   public void trigger(EntityPlayerMP player) {
      PositionTrigger.Listeners positiontrigger$listeners = this.listeners.get(player.getAdvancements());
      if (positiontrigger$listeners != null) {
         positiontrigger$listeners.trigger(player.getServerWorld(), player.posX, player.posY, player.posZ);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final LocationPredicate location;

      public Instance(ResourceLocation criterionIn, LocationPredicate location) {
         super(criterionIn);
         this.location = location;
      }

      public static PositionTrigger.Instance func_203932_a(LocationPredicate p_203932_0_) {
         return new PositionTrigger.Instance(CriteriaTriggers.LOCATION.id, p_203932_0_);
      }

      public static PositionTrigger.Instance func_203931_c() {
         return new PositionTrigger.Instance(CriteriaTriggers.SLEPT_IN_BED.id, LocationPredicate.ANY);
      }

      public boolean test(WorldServer world, double x, double y, double z) {
         return this.location.test(world, x, y, z);
      }

      public JsonElement serialize() {
         return this.location.serialize();
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<PositionTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements playerAdvancementsIn) {
         this.playerAdvancements = playerAdvancementsIn;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<PositionTrigger.Instance> listener) {
         this.listeners.add(listener);
      }

      public void remove(ICriterionTrigger.Listener<PositionTrigger.Instance> listener) {
         this.listeners.remove(listener);
      }

      public void trigger(WorldServer world, double x, double y, double z) {
         List<ICriterionTrigger.Listener<PositionTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<PositionTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(world, x, y, z)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<PositionTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}