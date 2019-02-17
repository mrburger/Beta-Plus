package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Advancement.Builder.class, (JsonDeserializer<Advancement.Builder>)(p_210124_0_, p_210124_1_, p_210124_2_) -> {
      JsonObject jsonobject = JsonUtils.getJsonObject(p_210124_0_, "advancement");
      return Advancement.Builder.deserialize(jsonobject, p_210124_2_);
   }).registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.Deserializer()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new EnumTypeAdapterFactory()).create();
   private static final AdvancementList ADVANCEMENT_LIST = new AdvancementList();
   public static final int PATH_PREFIX_LENGTH = "advancements/".length();
   public static final int EXTENSION_LENGTH = ".json".length();
   private boolean hasErrored;

   public Map<ResourceLocation, Advancement.Builder> loadCustomAdvancements(IResourceManager p_195439_1_) {
      Map<ResourceLocation, Advancement.Builder> map = Maps.newHashMap();

      for(ResourceLocation resourcelocation : p_195439_1_.getAllResourceLocations("advancements", (p_195440_0_) -> {
         return p_195440_0_.endsWith(".json");
      })) {
         String s = resourcelocation.getPath();
         ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(PATH_PREFIX_LENGTH, s.length() - EXTENSION_LENGTH));

         try (IResource iresource = p_195439_1_.getResource(resourcelocation)) {
            Advancement.Builder advancement$builder = JsonUtils.fromJson(GSON, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), Advancement.Builder.class);
            if (advancement$builder == null) {
               LOGGER.error("Couldn't load custom advancement {} from {} as it's empty or null", resourcelocation1, resourcelocation);
            } else {
               map.put(resourcelocation1, advancement$builder);
            }
         } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
            LOGGER.error("Parsing error loading custom advancement {}: {}", resourcelocation1, jsonparseexception.getMessage());
            this.hasErrored = true;
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't read custom advancement {} from {}", resourcelocation1, resourcelocation, ioexception);
            this.hasErrored = true;
         }
      }

      return map;
   }

   @Nullable
   public Advancement getAdvancement(ResourceLocation id) {
      return ADVANCEMENT_LIST.getAdvancement(id);
   }

   public Collection<Advancement> getAllAdvancements() {
      return ADVANCEMENT_LIST.getAll();
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.hasErrored = false;
      ADVANCEMENT_LIST.clear();
      Map<ResourceLocation, Advancement.Builder> map = this.loadCustomAdvancements(resourceManager);
      ADVANCEMENT_LIST.loadAdvancements(map);

      for(Advancement advancement : ADVANCEMENT_LIST.getRoots()) {
         if (advancement.getDisplay() != null) {
            AdvancementTreeNode.layout(advancement);
         }
      }

   }
}