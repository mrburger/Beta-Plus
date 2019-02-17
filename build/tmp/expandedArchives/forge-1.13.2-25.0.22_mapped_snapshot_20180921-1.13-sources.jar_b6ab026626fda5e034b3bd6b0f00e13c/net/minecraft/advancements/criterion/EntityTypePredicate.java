package net.minecraft.advancements.criterion;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class EntityTypePredicate {
   public static final EntityTypePredicate ANY = new EntityTypePredicate();
   private static final Joiner field_209372_b = Joiner.on(", ");
   @Nullable
   private final EntityType<?> type;

   public EntityTypePredicate(EntityType<?> p_i49390_1_) {
      this.type = p_i49390_1_;
   }

   private EntityTypePredicate() {
      this.type = null;
   }

   public boolean test(EntityType<?> p_209368_1_) {
      return this.type == null || this.type == p_209368_1_;
   }

   public static EntityTypePredicate deserialize(@Nullable JsonElement p_209370_0_) {
      if (p_209370_0_ != null && !p_209370_0_.isJsonNull()) {
         String s = JsonUtils.getString(p_209370_0_, "type");
         ResourceLocation resourcelocation = new ResourceLocation(s);
         EntityType<?> entitytype = IRegistry.field_212629_r.func_212608_b(resourcelocation);
         if (entitytype == null) {
            throw new JsonSyntaxException("Unknown entity type '" + resourcelocation + "', valid types are: " + field_209372_b.join(IRegistry.field_212629_r.getKeys()));
         } else {
            return new EntityTypePredicate(entitytype);
         }
      } else {
         return ANY;
      }
   }

   public JsonElement serialize() {
      return (JsonElement)(this.type == null ? JsonNull.INSTANCE : new JsonPrimitive(IRegistry.field_212629_r.getKey(this.type).toString()));
   }
}