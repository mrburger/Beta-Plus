package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;

public class LocationPredicate {
   public static final LocationPredicate ANY = new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, (Biome)null, (String)null, (DimensionType)null);
   private final MinMaxBounds.FloatBound x;
   private final MinMaxBounds.FloatBound y;
   private final MinMaxBounds.FloatBound z;
   @Nullable
   private final Biome biome;
   @Nullable
   private final String feature;
   @Nullable
   private final DimensionType dimension;

   public LocationPredicate(MinMaxBounds.FloatBound p_i49721_1_, MinMaxBounds.FloatBound p_i49721_2_, MinMaxBounds.FloatBound p_i49721_3_, @Nullable Biome p_i49721_4_, @Nullable String p_i49721_5_, @Nullable DimensionType p_i49721_6_) {
      this.x = p_i49721_1_;
      this.y = p_i49721_2_;
      this.z = p_i49721_3_;
      this.biome = p_i49721_4_;
      this.feature = p_i49721_5_;
      this.dimension = p_i49721_6_;
   }

   public static LocationPredicate func_204010_a(Biome p_204010_0_) {
      return new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, p_204010_0_, (String)null, (DimensionType)null);
   }

   public static LocationPredicate func_204008_a(DimensionType p_204008_0_) {
      return new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, (Biome)null, (String)null, p_204008_0_);
   }

   public static LocationPredicate func_204007_a(String p_204007_0_) {
      return new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, (Biome)null, p_204007_0_, (DimensionType)null);
   }

   public boolean test(WorldServer world, double x, double y, double z) {
      return this.test(world, (float)x, (float)y, (float)z);
   }

   public boolean test(WorldServer world, float x, float y, float z) {
      if (!this.x.test(x)) {
         return false;
      } else if (!this.y.test(y)) {
         return false;
      } else if (!this.z.test(z)) {
         return false;
      } else if (this.dimension != null && this.dimension != world.dimension.getType()) {
         return false;
      } else {
         BlockPos blockpos = new BlockPos((double)x, (double)y, (double)z);
         if (this.biome != null && this.biome != world.getBiome(blockpos)) {
            return false;
         } else {
            return this.feature == null || Feature.isPositionInStructureExact(world, this.feature, blockpos);
         }
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (!this.x.isUnbounded() || !this.y.isUnbounded() || !this.z.isUnbounded()) {
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.add("x", this.x.serialize());
            jsonobject1.add("y", this.y.serialize());
            jsonobject1.add("z", this.z.serialize());
            jsonobject.add("position", jsonobject1);
         }

         if (this.dimension != null) {
            jsonobject.addProperty("dimension", DimensionType.func_212678_a(this.dimension).toString());
         }

         if (this.feature != null) {
            jsonobject.addProperty("feature", this.feature);
         }

         if (this.biome != null) {
            jsonobject.addProperty("biome", IRegistry.field_212624_m.getKey(this.biome).toString());
         }

         return jsonobject;
      }
   }

   public static LocationPredicate deserialize(@Nullable JsonElement element) {
      if (element != null && !element.isJsonNull()) {
         JsonObject jsonobject = JsonUtils.getJsonObject(element, "location");
         JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "position", new JsonObject());
         MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromJson(jsonobject1.get("x"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound1 = MinMaxBounds.FloatBound.fromJson(jsonobject1.get("y"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound2 = MinMaxBounds.FloatBound.fromJson(jsonobject1.get("z"));
         DimensionType dimensiontype = jsonobject.has("dimension") ? DimensionType.byName(new ResourceLocation(JsonUtils.getString(jsonobject, "dimension"))) : null;
         String s = jsonobject.has("feature") ? JsonUtils.getString(jsonobject, "feature") : null;
         Biome biome = null;
         if (jsonobject.has("biome")) {
            ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(jsonobject, "biome"));
            biome = IRegistry.field_212624_m.func_212608_b(resourcelocation);
            if (biome == null) {
               throw new JsonSyntaxException("Unknown biome '" + resourcelocation + "'");
            }
         }

         return new LocationPredicate(minmaxbounds$floatbound, minmaxbounds$floatbound1, minmaxbounds$floatbound2, biome, s, dimensiontype);
      } else {
         return ANY;
      }
   }
}