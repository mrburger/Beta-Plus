package net.minecraft.world.storage.loot;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntry.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
   private final Map<ResourceLocation, LootTable> registeredLootTables = Maps.newHashMap();
   public static final int field_195435_a = "loot_tables/".length();
   public static final int field_195436_b = ".json".length();

   public LootTable getLootTableFromLocation(ResourceLocation ressources) {
      return this.registeredLootTables.getOrDefault(ressources, LootTable.EMPTY_LOOT_TABLE);
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.registeredLootTables.clear();

      for(ResourceLocation resourcelocation : resourceManager.getAllResourceLocations("loot_tables", (p_195434_0_) -> {
         return p_195434_0_.endsWith(".json");
      })) {
         String s = resourcelocation.getPath();
         ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(field_195435_a, s.length() - field_195436_b));

         try (IResource iresource = resourceManager.getResource(resourcelocation)) {
            LootTable loottable =net.minecraftforge.common.ForgeHooks.loadLootTable(GSON_INSTANCE, resourcelocation, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), iresource.getPackName().equals("Default"), this);
            if (loottable != null) {
               this.registeredLootTables.put(resourcelocation1, loottable);
            }
         } catch (Throwable throwable) {
            LOGGER.error("Couldn't read loot table {} from {}", resourcelocation1, resourcelocation, throwable);
         }
      }

   }
}