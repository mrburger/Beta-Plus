package net.minecraft.advancements.criterion;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class MobEffectsPredicate {
   /** The predicate that matches any set of effects. */
   public static final MobEffectsPredicate ANY = new MobEffectsPredicate(Collections.emptyMap());
   private final Map<Potion, MobEffectsPredicate.InstancePredicate> effects;

   public MobEffectsPredicate(Map<Potion, MobEffectsPredicate.InstancePredicate> effects) {
      this.effects = effects;
   }

   public static MobEffectsPredicate func_204014_a() {
      return new MobEffectsPredicate(Maps.newHashMap());
   }

   public MobEffectsPredicate func_204015_a(Potion p_204015_1_) {
      this.effects.put(p_204015_1_, new MobEffectsPredicate.InstancePredicate());
      return this;
   }

   public boolean test(Entity entityIn) {
      if (this == ANY) {
         return true;
      } else {
         return entityIn instanceof EntityLivingBase ? this.test(((EntityLivingBase)entityIn).getActivePotionMap()) : false;
      }
   }

   public boolean test(EntityLivingBase entityIn) {
      return this == ANY ? true : this.test(entityIn.getActivePotionMap());
   }

   public boolean test(Map<Potion, PotionEffect> potions) {
      if (this == ANY) {
         return true;
      } else {
         for(Entry<Potion, MobEffectsPredicate.InstancePredicate> entry : this.effects.entrySet()) {
            PotionEffect potioneffect = potions.get(entry.getKey());
            if (!entry.getValue().test(potioneffect)) {
               return false;
            }
         }

         return true;
      }
   }

   public static MobEffectsPredicate deserialize(@Nullable JsonElement element) {
      if (element != null && !element.isJsonNull()) {
         JsonObject jsonobject = JsonUtils.getJsonObject(element, "effects");
         Map<Potion, MobEffectsPredicate.InstancePredicate> map = Maps.newHashMap();

         for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            ResourceLocation resourcelocation = new ResourceLocation(entry.getKey());
            Potion potion = IRegistry.field_212631_t.func_212608_b(resourcelocation);
            if (potion == null) {
               throw new JsonSyntaxException("Unknown effect '" + resourcelocation + "'");
            }

            MobEffectsPredicate.InstancePredicate mobeffectspredicate$instancepredicate = MobEffectsPredicate.InstancePredicate.deserialize(JsonUtils.getJsonObject(entry.getValue(), entry.getKey()));
            map.put(potion, mobeffectspredicate$instancepredicate);
         }

         return new MobEffectsPredicate(map);
      } else {
         return ANY;
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();

         for(Entry<Potion, MobEffectsPredicate.InstancePredicate> entry : this.effects.entrySet()) {
            jsonobject.add(IRegistry.field_212631_t.getKey(entry.getKey()).toString(), entry.getValue().serialize());
         }

         return jsonobject;
      }
   }

   public static class InstancePredicate {
      private final MinMaxBounds.IntBound amplifier;
      private final MinMaxBounds.IntBound duration;
      @Nullable
      private final Boolean ambient;
      @Nullable
      private final Boolean visible;

      public InstancePredicate(MinMaxBounds.IntBound p_i49709_1_, MinMaxBounds.IntBound p_i49709_2_, @Nullable Boolean p_i49709_3_, @Nullable Boolean p_i49709_4_) {
         this.amplifier = p_i49709_1_;
         this.duration = p_i49709_2_;
         this.ambient = p_i49709_3_;
         this.visible = p_i49709_4_;
      }

      public InstancePredicate() {
         this(MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, (Boolean)null, (Boolean)null);
      }

      public boolean test(@Nullable PotionEffect effect) {
         if (effect == null) {
            return false;
         } else if (!this.amplifier.test(effect.getAmplifier())) {
            return false;
         } else if (!this.duration.test(effect.getDuration())) {
            return false;
         } else if (this.ambient != null && this.ambient != effect.isAmbient()) {
            return false;
         } else {
            return this.visible == null || this.visible == effect.doesShowParticles();
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("amplifier", this.amplifier.serialize());
         jsonobject.add("duration", this.duration.serialize());
         jsonobject.addProperty("ambient", this.ambient);
         jsonobject.addProperty("visible", this.visible);
         return jsonobject;
      }

      public static MobEffectsPredicate.InstancePredicate deserialize(JsonObject object) {
         MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(object.get("amplifier"));
         MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(object.get("duration"));
         Boolean obool = object.has("ambient") ? JsonUtils.getBoolean(object, "ambient") : null;
         Boolean obool1 = object.has("visible") ? JsonUtils.getBoolean(object, "visible") : null;
         return new MobEffectsPredicate.InstancePredicate(minmaxbounds$intbound, minmaxbounds$intbound1, obool, obool1);
      }
   }
}