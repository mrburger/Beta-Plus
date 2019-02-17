package net.minecraft.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;

public class EntityPredicate {
   /** The predicate that matches any entity. */
   public static final EntityPredicate ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, MobEffectsPredicate.ANY, NBTPredicate.ANY);
   public static final EntityPredicate[] ANY_ARRAY = new EntityPredicate[0];
   private final EntityTypePredicate type;
   private final DistancePredicate distance;
   private final LocationPredicate location;
   private final MobEffectsPredicate effects;
   private final NBTPredicate nbt;

   private EntityPredicate(EntityTypePredicate p_i49391_1_, DistancePredicate p_i49391_2_, LocationPredicate p_i49391_3_, MobEffectsPredicate p_i49391_4_, NBTPredicate p_i49391_5_) {
      this.type = p_i49391_1_;
      this.distance = p_i49391_2_;
      this.location = p_i49391_3_;
      this.effects = p_i49391_4_;
      this.nbt = p_i49391_5_;
   }

   public boolean test(EntityPlayerMP player, @Nullable Entity entity) {
      if (this == ANY) {
         return true;
      } else if (entity == null) {
         return false;
      } else if (!this.type.test(entity.getType())) {
         return false;
      } else if (!this.distance.test(player.posX, player.posY, player.posZ, entity.posX, entity.posY, entity.posZ)) {
         return false;
      } else if (!this.location.test(player.getServerWorld(), entity.posX, entity.posY, entity.posZ)) {
         return false;
      } else if (!this.effects.test(entity)) {
         return false;
      } else {
         return this.nbt.test(entity);
      }
   }

   public static EntityPredicate deserialize(@Nullable JsonElement element) {
      if (element != null && !element.isJsonNull()) {
         JsonObject jsonobject = JsonUtils.getJsonObject(element, "entity");
         EntityTypePredicate entitytypepredicate = EntityTypePredicate.deserialize(jsonobject.get("type"));
         DistancePredicate distancepredicate = DistancePredicate.deserialize(jsonobject.get("distance"));
         LocationPredicate locationpredicate = LocationPredicate.deserialize(jsonobject.get("location"));
         MobEffectsPredicate mobeffectspredicate = MobEffectsPredicate.deserialize(jsonobject.get("effects"));
         NBTPredicate nbtpredicate = NBTPredicate.deserialize(jsonobject.get("nbt"));
         return (new EntityPredicate.Builder()).func_209366_a(entitytypepredicate).func_203997_a(distancepredicate).func_203999_a(locationpredicate).func_209367_a(mobeffectspredicate).func_209365_a(nbtpredicate).build();
      } else {
         return ANY;
      }
   }

   public static EntityPredicate[] deserializeArray(@Nullable JsonElement array) {
      if (array != null && !array.isJsonNull()) {
         JsonArray jsonarray = JsonUtils.getJsonArray(array, "entities");
         EntityPredicate[] aentitypredicate = new EntityPredicate[jsonarray.size()];

         for(int i = 0; i < jsonarray.size(); ++i) {
            aentitypredicate[i] = deserialize(jsonarray.get(i));
         }

         return aentitypredicate;
      } else {
         return ANY_ARRAY;
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("type", this.type.serialize());
         jsonobject.add("distance", this.distance.serialize());
         jsonobject.add("location", this.location.serialize());
         jsonobject.add("effects", this.effects.serialize());
         jsonobject.add("nbt", this.nbt.serialize());
         return jsonobject;
      }
   }

   public static JsonElement serializeArray(EntityPredicate[] predicates) {
      if (predicates == ANY_ARRAY) {
         return JsonNull.INSTANCE;
      } else {
         JsonArray jsonarray = new JsonArray();

         for(int i = 0; i < predicates.length; ++i) {
            JsonElement jsonelement = predicates[i].serialize();
            if (!jsonelement.isJsonNull()) {
               jsonarray.add(jsonelement);
            }
         }

         return jsonarray;
      }
   }

   public static class Builder {
      private EntityTypePredicate field_204001_a = EntityTypePredicate.ANY;
      private DistancePredicate field_204002_b = DistancePredicate.ANY;
      private LocationPredicate field_204003_c = LocationPredicate.ANY;
      private MobEffectsPredicate field_204004_d = MobEffectsPredicate.ANY;
      private NBTPredicate field_204005_e = NBTPredicate.ANY;

      public static EntityPredicate.Builder func_203996_a() {
         return new EntityPredicate.Builder();
      }

      public EntityPredicate.Builder func_203998_a(EntityType<?> p_203998_1_) {
         this.field_204001_a = new EntityTypePredicate(p_203998_1_);
         return this;
      }

      public EntityPredicate.Builder func_209366_a(EntityTypePredicate p_209366_1_) {
         this.field_204001_a = p_209366_1_;
         return this;
      }

      public EntityPredicate.Builder func_203997_a(DistancePredicate p_203997_1_) {
         this.field_204002_b = p_203997_1_;
         return this;
      }

      public EntityPredicate.Builder func_203999_a(LocationPredicate p_203999_1_) {
         this.field_204003_c = p_203999_1_;
         return this;
      }

      public EntityPredicate.Builder func_209367_a(MobEffectsPredicate p_209367_1_) {
         this.field_204004_d = p_209367_1_;
         return this;
      }

      public EntityPredicate.Builder func_209365_a(NBTPredicate p_209365_1_) {
         this.field_204005_e = p_209365_1_;
         return this;
      }

      public EntityPredicate build() {
         return this.field_204001_a == EntityTypePredicate.ANY && this.field_204002_b == DistancePredicate.ANY && this.field_204003_c == LocationPredicate.ANY && this.field_204004_d == MobEffectsPredicate.ANY && this.field_204005_e == NBTPredicate.ANY ? EntityPredicate.ANY : new EntityPredicate(this.field_204001_a, this.field_204002_b, this.field_204003_c, this.field_204004_d, this.field_204005_e);
      }
   }
}