package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.JsonUtils;

public class DamageSourcePredicate {
   public static final DamageSourcePredicate ANY = DamageSourcePredicate.Builder.damageType().build();
   private final Boolean isProjectile;
   private final Boolean isExplosion;
   private final Boolean bypassesArmor;
   private final Boolean bypassesInvulnerability;
   private final Boolean bypassesMagic;
   private final Boolean isFire;
   private final Boolean isMagic;
   private final EntityPredicate directEntity;
   private final EntityPredicate sourceEntity;

   public DamageSourcePredicate(@Nullable Boolean isProjectile, @Nullable Boolean isExplosion, @Nullable Boolean bypassesArmor, @Nullable Boolean bypassesInvulnerability, @Nullable Boolean bypassesMagic, @Nullable Boolean isFire, @Nullable Boolean isMagic, EntityPredicate directEntity, EntityPredicate sourceEntity) {
      this.isProjectile = isProjectile;
      this.isExplosion = isExplosion;
      this.bypassesArmor = bypassesArmor;
      this.bypassesInvulnerability = bypassesInvulnerability;
      this.bypassesMagic = bypassesMagic;
      this.isFire = isFire;
      this.isMagic = isMagic;
      this.directEntity = directEntity;
      this.sourceEntity = sourceEntity;
   }

   public boolean test(EntityPlayerMP player, DamageSource source) {
      if (this == ANY) {
         return true;
      } else if (this.isProjectile != null && this.isProjectile != source.isProjectile()) {
         return false;
      } else if (this.isExplosion != null && this.isExplosion != source.isExplosion()) {
         return false;
      } else if (this.bypassesArmor != null && this.bypassesArmor != source.isUnblockable()) {
         return false;
      } else if (this.bypassesInvulnerability != null && this.bypassesInvulnerability != source.canHarmInCreative()) {
         return false;
      } else if (this.bypassesMagic != null && this.bypassesMagic != source.isDamageAbsolute()) {
         return false;
      } else if (this.isFire != null && this.isFire != source.isFireDamage()) {
         return false;
      } else if (this.isMagic != null && this.isMagic != source.isMagicDamage()) {
         return false;
      } else if (!this.directEntity.test(player, source.getImmediateSource())) {
         return false;
      } else {
         return this.sourceEntity.test(player, source.getTrueSource());
      }
   }

   public static DamageSourcePredicate deserialize(@Nullable JsonElement element) {
      if (element != null && !element.isJsonNull()) {
         JsonObject jsonobject = JsonUtils.getJsonObject(element, "damage type");
         Boolean obool = optionalBoolean(jsonobject, "is_projectile");
         Boolean obool1 = optionalBoolean(jsonobject, "is_explosion");
         Boolean obool2 = optionalBoolean(jsonobject, "bypasses_armor");
         Boolean obool3 = optionalBoolean(jsonobject, "bypasses_invulnerability");
         Boolean obool4 = optionalBoolean(jsonobject, "bypasses_magic");
         Boolean obool5 = optionalBoolean(jsonobject, "is_fire");
         Boolean obool6 = optionalBoolean(jsonobject, "is_magic");
         EntityPredicate entitypredicate = EntityPredicate.deserialize(jsonobject.get("direct_entity"));
         EntityPredicate entitypredicate1 = EntityPredicate.deserialize(jsonobject.get("source_entity"));
         return new DamageSourcePredicate(obool, obool1, obool2, obool3, obool4, obool5, obool6, entitypredicate, entitypredicate1);
      } else {
         return ANY;
      }
   }

   @Nullable
   private static Boolean optionalBoolean(JsonObject object, String memberName) {
      return object.has(memberName) ? JsonUtils.getBoolean(object, memberName) : null;
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         this.addProperty(jsonobject, "is_projectile", this.isProjectile);
         this.addProperty(jsonobject, "is_explosion", this.isExplosion);
         this.addProperty(jsonobject, "bypasses_armor", this.bypassesArmor);
         this.addProperty(jsonobject, "bypasses_invulnerability", this.bypassesInvulnerability);
         this.addProperty(jsonobject, "bypasses_magic", this.bypassesMagic);
         this.addProperty(jsonobject, "is_fire", this.isFire);
         this.addProperty(jsonobject, "is_magic", this.isMagic);
         jsonobject.add("direct_entity", this.directEntity.serialize());
         jsonobject.add("source_entity", this.sourceEntity.serialize());
         return jsonobject;
      }
   }

   /**
    * Adds a property if the value is not null.
    */
   private void addProperty(JsonObject obj, String key, @Nullable Boolean value) {
      if (value != null) {
         obj.addProperty(key, value);
      }

   }

   public static class Builder {
      private Boolean isProjectile;
      private Boolean isExplosion;
      private Boolean bypassesArmor;
      private Boolean bypassesInvulnerability;
      private Boolean bypassesMagic;
      private Boolean isFire;
      private Boolean isMagic;
      private EntityPredicate directEntity = EntityPredicate.ANY;
      private EntityPredicate sourceEntity = EntityPredicate.ANY;

      public static DamageSourcePredicate.Builder damageType() {
         return new DamageSourcePredicate.Builder();
      }

      public DamageSourcePredicate.Builder isProjectile(Boolean p_203978_1_) {
         this.isProjectile = p_203978_1_;
         return this;
      }

      public DamageSourcePredicate.Builder direct(EntityPredicate.Builder p_203980_1_) {
         this.directEntity = p_203980_1_.build();
         return this;
      }

      public DamageSourcePredicate build() {
         return new DamageSourcePredicate(this.isProjectile, this.isExplosion, this.bypassesArmor, this.bypassesInvulnerability, this.bypassesMagic, this.isFire, this.isMagic, this.directEntity, this.sourceEntity);
      }
   }
}