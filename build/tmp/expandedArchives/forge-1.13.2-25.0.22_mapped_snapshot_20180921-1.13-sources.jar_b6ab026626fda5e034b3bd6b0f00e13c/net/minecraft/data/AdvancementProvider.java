package net.minecraft.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.advancements.AdventureAdvancements;
import net.minecraft.data.advancements.EndAdvancements;
import net.minecraft.data.advancements.HusbandryAdvancements;
import net.minecraft.data.advancements.NetherAdvancements;
import net.minecraft.data.advancements.StoryAdvancements;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementProvider implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;
   /**
    * A list of advancement groups, which each register several advancements. (Each group takes a Consumer<Advancement>
    * as a parameter)
    */
   private final List<Consumer<Consumer<Advancement>>> advancements = ImmutableList.of(new EndAdvancements(), new HusbandryAdvancements(), new AdventureAdvancements(), new NetherAdvancements(), new StoryAdvancements());

   public AdvancementProvider(DataGenerator generatorIn) {
      this.generator = generatorIn;
   }

   /**
    * Performs this provider's action.
    */
   public void act(DirectoryCache cache) throws IOException {
      Path path = this.generator.getOutputFolder();
      Set<ResourceLocation> set = Sets.newHashSet();
      Consumer<Advancement> consumer = (p_204017_4_) -> {
         if (!set.add(p_204017_4_.getId())) {
            throw new IllegalStateException("Duplicate advancement " + p_204017_4_.getId());
         } else {
            this.saveAdvancement(cache, p_204017_4_.copy().serialize(), path.resolve("data/" + p_204017_4_.getId().getNamespace() + "/advancements/" + p_204017_4_.getId().getPath() + ".json"));
         }
      };

      for(Consumer<Consumer<Advancement>> consumer1 : this.advancements) {
         consumer1.accept(consumer);
      }

   }

   private void saveAdvancement(DirectoryCache cache, JsonObject advancementJson, Path pathIn) {
      try {
         String s = GSON.toJson((JsonElement)advancementJson);
         String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
         if (!Objects.equals(cache.getPreviousHash(pathIn), s1) || !Files.exists(pathIn)) {
            Files.createDirectories(pathIn.getParent());

            try (BufferedWriter bufferedwriter = Files.newBufferedWriter(pathIn)) {
               bufferedwriter.write(s);
            }
         }

         cache.func_208316_a(pathIn, s1);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't save advancement {}", pathIn, ioexception);
      }

   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public String getName() {
      return "Advancements";
   }
}