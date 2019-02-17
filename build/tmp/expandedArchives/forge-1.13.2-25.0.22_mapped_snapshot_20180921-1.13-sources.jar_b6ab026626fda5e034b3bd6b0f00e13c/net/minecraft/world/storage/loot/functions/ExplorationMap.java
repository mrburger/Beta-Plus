package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Random;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExplorationMap extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String destination;
   private final MapDecoration.Type decoration;
   private final byte zoom;
   private final int searchRadius;
   private final boolean field_212428_f;

   public ExplorationMap(LootCondition[] p_i48873_1_, String p_i48873_2_, MapDecoration.Type p_i48873_3_, byte p_i48873_4_, int p_i48873_5_, boolean p_i48873_6_) {
      super(p_i48873_1_);
      this.destination = p_i48873_2_;
      this.decoration = p_i48873_3_;
      this.zoom = p_i48873_4_;
      this.searchRadius = p_i48873_5_;
      this.field_212428_f = p_i48873_6_;
   }

   public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
      if (stack.getItem() != Items.MAP) {
         return stack;
      } else {
         BlockPos blockpos = context.getPos();
         if (blockpos == null) {
            return stack;
         } else {
            WorldServer worldserver = context.getWorld();
            BlockPos blockpos1 = worldserver.findNearestStructure(this.destination, blockpos, this.searchRadius, this.field_212428_f);
            if (blockpos1 != null) {
               ItemStack itemstack = ItemMap.setupNewMap(worldserver, blockpos1.getX(), blockpos1.getZ(), this.zoom, true, true);
               ItemMap.renderBiomePreviewMap(worldserver, itemstack);
               MapData.addTargetDecoration(itemstack, blockpos1, "+", this.decoration);
               itemstack.setDisplayName(new TextComponentTranslation("filled_map." + this.destination.toLowerCase(Locale.ROOT)));
               return itemstack;
            } else {
               return stack;
            }
         }
      }
   }

   public static class Serializer extends LootFunction.Serializer<ExplorationMap> {
      protected Serializer() {
         super(new ResourceLocation("exploration_map"), ExplorationMap.class);
      }

      public void serialize(JsonObject object, ExplorationMap functionClazz, JsonSerializationContext serializationContext) {
         object.add("destination", serializationContext.serialize(functionClazz.destination));
         object.add("decoration", serializationContext.serialize(functionClazz.decoration.toString().toLowerCase(Locale.ROOT)));
      }

      public ExplorationMap deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
         String s = object.has("destination") ? JsonUtils.getString(object, "destination") : "Buried_Treasure";
         s = Feature.STRUCTURES.containsKey(s.toLowerCase(Locale.ROOT)) ? s : "Buried_Treasure";
         String s1 = object.has("decoration") ? JsonUtils.getString(object, "decoration") : "mansion";
         MapDecoration.Type mapdecoration$type = MapDecoration.Type.MANSION;

         try {
            mapdecoration$type = MapDecoration.Type.valueOf(s1.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var10) {
            ExplorationMap.LOGGER.error("Error while parsing loot table decoration entry. Found {}. Defaulting to MANSION", (Object)s1);
         }

         byte b0 = object.has("zoom") ? JsonUtils.getByte(object, "zoom") : 2;
         int i = object.has("search_radius") ? JsonUtils.getInt(object, "search_radius") : 50;
         boolean flag = object.has("skip_existing_chunks") ? JsonUtils.getBoolean(object, "skip_existing_chunks") : true;
         return new ExplorationMap(conditionsIn, s, mapdecoration$type, b0, i, flag);
      }
   }
}