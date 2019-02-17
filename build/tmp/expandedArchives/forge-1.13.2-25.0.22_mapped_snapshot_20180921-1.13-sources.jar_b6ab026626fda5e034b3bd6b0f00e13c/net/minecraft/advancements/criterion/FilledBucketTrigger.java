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

public class FilledBucketTrigger implements ICriterionTrigger<FilledBucketTrigger.Instance> {
   private static final ResourceLocation field_204818_a = new ResourceLocation("filled_bucket");
   private final Map<PlayerAdvancements, FilledBucketTrigger.Listeners> field_204819_b = Maps.newHashMap();

   public ResourceLocation getId() {
      return field_204818_a;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<FilledBucketTrigger.Instance> listener) {
      FilledBucketTrigger.Listeners filledbuckettrigger$listeners = this.field_204819_b.get(playerAdvancementsIn);
      if (filledbuckettrigger$listeners == null) {
         filledbuckettrigger$listeners = new FilledBucketTrigger.Listeners(playerAdvancementsIn);
         this.field_204819_b.put(playerAdvancementsIn, filledbuckettrigger$listeners);
      }

      filledbuckettrigger$listeners.func_204852_a(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<FilledBucketTrigger.Instance> listener) {
      FilledBucketTrigger.Listeners filledbuckettrigger$listeners = this.field_204819_b.get(playerAdvancementsIn);
      if (filledbuckettrigger$listeners != null) {
         filledbuckettrigger$listeners.func_204855_b(listener);
         if (filledbuckettrigger$listeners.func_204853_a()) {
            this.field_204819_b.remove(playerAdvancementsIn);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
      this.field_204819_b.remove(playerAdvancementsIn);
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public FilledBucketTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      return new FilledBucketTrigger.Instance(itempredicate);
   }

   public void func_204817_a(EntityPlayerMP p_204817_1_, ItemStack p_204817_2_) {
      FilledBucketTrigger.Listeners filledbuckettrigger$listeners = this.field_204819_b.get(p_204817_1_.getAdvancements());
      if (filledbuckettrigger$listeners != null) {
         filledbuckettrigger$listeners.func_204854_a(p_204817_2_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate field_204828_a;

      public Instance(ItemPredicate p_i48918_1_) {
         super(FilledBucketTrigger.field_204818_a);
         this.field_204828_a = p_i48918_1_;
      }

      public static FilledBucketTrigger.Instance func_204827_a(ItemPredicate p_204827_0_) {
         return new FilledBucketTrigger.Instance(p_204827_0_);
      }

      public boolean func_204826_a(ItemStack p_204826_1_) {
         return this.field_204828_a.test(p_204826_1_);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.field_204828_a.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements field_204856_a;
      private final Set<ICriterionTrigger.Listener<FilledBucketTrigger.Instance>> field_204857_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i48919_1_) {
         this.field_204856_a = p_i48919_1_;
      }

      public boolean func_204853_a() {
         return this.field_204857_b.isEmpty();
      }

      public void func_204852_a(ICriterionTrigger.Listener<FilledBucketTrigger.Instance> p_204852_1_) {
         this.field_204857_b.add(p_204852_1_);
      }

      public void func_204855_b(ICriterionTrigger.Listener<FilledBucketTrigger.Instance> p_204855_1_) {
         this.field_204857_b.remove(p_204855_1_);
      }

      public void func_204854_a(ItemStack p_204854_1_) {
         List<ICriterionTrigger.Listener<FilledBucketTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<FilledBucketTrigger.Instance> listener : this.field_204857_b) {
            if (listener.getCriterionInstance().func_204826_a(p_204854_1_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<FilledBucketTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.field_204856_a);
            }
         }

      }
   }
}