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
import net.minecraft.util.math.Vec3d;

public class LevitationTrigger implements ICriterionTrigger<LevitationTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("levitation");
   private final Map<PlayerAdvancements, LevitationTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<LevitationTrigger.Instance> listener) {
      LevitationTrigger.Listeners levitationtrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (levitationtrigger$listeners == null) {
         levitationtrigger$listeners = new LevitationTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, levitationtrigger$listeners);
      }

      levitationtrigger$listeners.add(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<LevitationTrigger.Instance> listener) {
      LevitationTrigger.Listeners levitationtrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (levitationtrigger$listeners != null) {
         levitationtrigger$listeners.remove(listener);
         if (levitationtrigger$listeners.isEmpty()) {
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
   public LevitationTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      DistancePredicate distancepredicate = DistancePredicate.deserialize(json.get("distance"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("duration"));
      return new LevitationTrigger.Instance(distancepredicate, minmaxbounds$intbound);
   }

   public void trigger(EntityPlayerMP player, Vec3d startPos, int duration) {
      LevitationTrigger.Listeners levitationtrigger$listeners = this.listeners.get(player.getAdvancements());
      if (levitationtrigger$listeners != null) {
         levitationtrigger$listeners.trigger(player, startPos, duration);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final DistancePredicate distance;
      private final MinMaxBounds.IntBound duration;

      public Instance(DistancePredicate p_i49729_1_, MinMaxBounds.IntBound p_i49729_2_) {
         super(LevitationTrigger.ID);
         this.distance = p_i49729_1_;
         this.duration = p_i49729_2_;
      }

      public static LevitationTrigger.Instance func_203930_a(DistancePredicate p_203930_0_) {
         return new LevitationTrigger.Instance(p_203930_0_, MinMaxBounds.IntBound.UNBOUNDED);
      }

      public boolean test(EntityPlayerMP player, Vec3d startPos, int durationIn) {
         if (!this.distance.test(startPos.x, startPos.y, startPos.z, player.posX, player.posY, player.posZ)) {
            return false;
         } else {
            return this.duration.test(durationIn);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("distance", this.distance.serialize());
         jsonobject.add("duration", this.duration.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<LevitationTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements playerAdvancementsIn) {
         this.playerAdvancements = playerAdvancementsIn;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<LevitationTrigger.Instance> listener) {
         this.listeners.add(listener);
      }

      public void remove(ICriterionTrigger.Listener<LevitationTrigger.Instance> listener) {
         this.listeners.remove(listener);
      }

      public void trigger(EntityPlayerMP player, Vec3d startPos, int durationIn) {
         List<ICriterionTrigger.Listener<LevitationTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<LevitationTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(player, startPos, durationIn)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<LevitationTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}