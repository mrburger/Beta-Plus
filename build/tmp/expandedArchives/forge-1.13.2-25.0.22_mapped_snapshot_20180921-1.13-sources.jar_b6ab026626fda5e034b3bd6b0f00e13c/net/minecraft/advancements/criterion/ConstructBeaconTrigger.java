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
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;

public class ConstructBeaconTrigger implements ICriterionTrigger<ConstructBeaconTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("construct_beacon");
   private final Map<PlayerAdvancements, ConstructBeaconTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> listener) {
      ConstructBeaconTrigger.Listeners constructbeacontrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (constructbeacontrigger$listeners == null) {
         constructbeacontrigger$listeners = new ConstructBeaconTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, constructbeacontrigger$listeners);
      }

      constructbeacontrigger$listeners.add(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> listener) {
      ConstructBeaconTrigger.Listeners constructbeacontrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (constructbeacontrigger$listeners != null) {
         constructbeacontrigger$listeners.remove(listener);
         if (constructbeacontrigger$listeners.isEmpty()) {
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
   public ConstructBeaconTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("level"));
      return new ConstructBeaconTrigger.Instance(minmaxbounds$intbound);
   }

   public void trigger(EntityPlayerMP player, TileEntityBeacon beacon) {
      ConstructBeaconTrigger.Listeners constructbeacontrigger$listeners = this.listeners.get(player.getAdvancements());
      if (constructbeacontrigger$listeners != null) {
         constructbeacontrigger$listeners.trigger(beacon);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final MinMaxBounds.IntBound level;

      public Instance(MinMaxBounds.IntBound p_i49736_1_) {
         super(ConstructBeaconTrigger.ID);
         this.level = p_i49736_1_;
      }

      public static ConstructBeaconTrigger.Instance func_203912_a(MinMaxBounds.IntBound p_203912_0_) {
         return new ConstructBeaconTrigger.Instance(p_203912_0_);
      }

      public boolean test(TileEntityBeacon beacon) {
         return this.level.test(beacon.getLevels());
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("level", this.level.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements playerAdvancementsIn) {
         this.playerAdvancements = playerAdvancementsIn;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> listener) {
         this.listeners.add(listener);
      }

      public void remove(ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> listener) {
         this.listeners.remove(listener);
      }

      public void trigger(TileEntityBeacon beacon) {
         List<ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(beacon)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}