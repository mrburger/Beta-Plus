package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class ChanneledLightningTrigger implements ICriterionTrigger<ChanneledLightningTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("channeled_lightning");
   private final Map<PlayerAdvancements, ChanneledLightningTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<ChanneledLightningTrigger.Instance> listener) {
      ChanneledLightningTrigger.Listeners channeledlightningtrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (channeledlightningtrigger$listeners == null) {
         channeledlightningtrigger$listeners = new ChanneledLightningTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, channeledlightningtrigger$listeners);
      }

      channeledlightningtrigger$listeners.addListener(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<ChanneledLightningTrigger.Instance> listener) {
      ChanneledLightningTrigger.Listeners channeledlightningtrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (channeledlightningtrigger$listeners != null) {
         channeledlightningtrigger$listeners.removeListener(listener);
         if (channeledlightningtrigger$listeners.isEmpty()) {
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
   public ChanneledLightningTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      EntityPredicate[] aentitypredicate = EntityPredicate.deserializeArray(json.get("victims"));
      return new ChanneledLightningTrigger.Instance(aentitypredicate);
   }

   public void trigger(EntityPlayerMP p_204814_1_, Collection<? extends Entity> p_204814_2_) {
      ChanneledLightningTrigger.Listeners channeledlightningtrigger$listeners = this.listeners.get(p_204814_1_.getAdvancements());
      if (channeledlightningtrigger$listeners != null) {
         channeledlightningtrigger$listeners.trigger(p_204814_1_, p_204814_2_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final EntityPredicate[] victims;

      public Instance(EntityPredicate[] victims) {
         super(ChanneledLightningTrigger.ID);
         this.victims = victims;
      }

      public static ChanneledLightningTrigger.Instance func_204824_a(EntityPredicate... p_204824_0_) {
         return new ChanneledLightningTrigger.Instance(p_204824_0_);
      }

      public boolean func_204823_a(EntityPlayerMP p_204823_1_, Collection<? extends Entity> p_204823_2_) {
         for(EntityPredicate entitypredicate : this.victims) {
            boolean flag = false;

            for(Entity entity : p_204823_2_) {
               if (entitypredicate.test(p_204823_1_, entity)) {
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               return false;
            }
         }

         return true;
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("victims", EntityPredicate.serializeArray(this.victims));
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements player;
      private final Set<ICriterionTrigger.Listener<ChanneledLightningTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i48922_1_) {
         this.player = p_i48922_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(ICriterionTrigger.Listener<ChanneledLightningTrigger.Instance> p_204843_1_) {
         this.listeners.add(p_204843_1_);
      }

      public void removeListener(ICriterionTrigger.Listener<ChanneledLightningTrigger.Instance> p_204845_1_) {
         this.listeners.remove(p_204845_1_);
      }

      public void trigger(EntityPlayerMP p_204846_1_, Collection<? extends Entity> p_204846_2_) {
         List<ICriterionTrigger.Listener<ChanneledLightningTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<ChanneledLightningTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().func_204823_a(p_204846_1_, p_204846_2_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<ChanneledLightningTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.player);
            }
         }

      }
   }
}