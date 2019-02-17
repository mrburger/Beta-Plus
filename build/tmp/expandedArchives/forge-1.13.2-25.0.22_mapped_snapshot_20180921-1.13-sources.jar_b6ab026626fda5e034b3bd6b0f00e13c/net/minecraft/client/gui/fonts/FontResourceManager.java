package net.minecraft.client.gui.fonts;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.providers.DefaultGlyphProvider;
import net.minecraft.client.gui.fonts.providers.GlyphProviderTypes;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FontResourceManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, FontRenderer> fontRenderers = Maps.newHashMap();
   private final TextureManager textureManager;
   private boolean forceUnicodeFont;

   public FontResourceManager(TextureManager textureManagerIn, boolean forceUnicodeFontIn) {
      this.textureManager = textureManagerIn;
      this.forceUnicodeFont = forceUnicodeFontIn;
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
      Map<ResourceLocation, List<IGlyphProvider>> map = Maps.newHashMap();

      for(ResourceLocation resourcelocation : resourceManager.getAllResourceLocations("font", (p_211506_0_) -> {
         return p_211506_0_.endsWith(".json");
      })) {
         String s = resourcelocation.getPath();
         ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring("font/".length(), s.length() - ".json".length()));
         List<IGlyphProvider> list = map.computeIfAbsent(resourcelocation1, (p_211507_0_) -> {
            return Lists.newArrayList(new DefaultGlyphProvider());
         });

         try {
            for(IResource iresource : resourceManager.getAllResources(resourcelocation)) {
               try (InputStream inputstream = iresource.getInputStream()) {
                  JsonArray jsonarray = JsonUtils.getJsonArray(JsonUtils.fromJson(gson, IOUtils.toString(inputstream, StandardCharsets.UTF_8), JsonObject.class), "providers");

                  for(int i = jsonarray.size() - 1; i >= 0; --i) {
                     JsonObject jsonobject = JsonUtils.getJsonObject(jsonarray.get(i), "providers[" + i + "]");

                     try {
                        GlyphProviderTypes glyphprovidertypes = GlyphProviderTypes.byName(JsonUtils.getString(jsonobject, "type"));
                        if (!this.forceUnicodeFont || glyphprovidertypes == GlyphProviderTypes.LEGACY_UNICODE || !resourcelocation1.equals(Minecraft.DEFAULT_FONT_RENDERER_NAME)) {
                           IGlyphProvider iglyphprovider = glyphprovidertypes.getFactory(jsonobject).create(resourceManager);
                           if (iglyphprovider != null) {
                              list.add(iglyphprovider);
                           }
                        }
                     } catch (RuntimeException runtimeexception) {
                        LOGGER.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", resourcelocation1, iresource.getPackName(), runtimeexception.getMessage());
                     }
                  }
               } catch (RuntimeException runtimeexception1) {
                  LOGGER.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", resourcelocation1, iresource.getPackName(), runtimeexception1.getMessage());
               }
            }
         } catch (IOException ioexception) {
            LOGGER.warn("Unable to load font '{}' in fonts.json: {}", resourcelocation1, ioexception.getMessage());
         }
      }

      Stream.concat(this.fontRenderers.keySet().stream(), map.keySet().stream()).distinct().forEach((p_211508_2_) -> {
         List<IGlyphProvider> list1 = map.getOrDefault(p_211508_2_, Collections.emptyList());
         Collections.reverse(list1);
         this.fontRenderers.computeIfAbsent(p_211508_2_, (p_211505_1_) -> {
            return new FontRenderer(this.textureManager, new Font(this.textureManager, p_211505_1_));
         }).setGlyphProviders(list1);
      });
   }

   @Nullable
   public FontRenderer getFontRenderer(ResourceLocation id) {
      return this.fontRenderers.computeIfAbsent(id, (p_212318_1_) -> {
         FontRenderer fontrenderer = new FontRenderer(this.textureManager, new Font(this.textureManager, p_212318_1_));
         fontrenderer.setGlyphProviders(Lists.newArrayList(new DefaultGlyphProvider()));
         return fontrenderer;
      });
   }

   public void setForceUnicodeFont(boolean forceUnicodeFontIn) {
      if (forceUnicodeFontIn != this.forceUnicodeFont) {
         this.forceUnicodeFont = forceUnicodeFontIn;
         this.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
      }
   }
}