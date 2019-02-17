package net.minecraft.client.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResourceIndexFolder extends ResourceIndex {
   private final File baseDir;

   public ResourceIndexFolder(File folder) {
      this.baseDir = folder;
   }

   public File getFile(ResourceLocation location) {
      return new File(this.baseDir, location.toString().replace(':', '/'));
   }

   public File getFile(String p_200009_1_) {
      return new File(this.baseDir, p_200009_1_);
   }

   public Collection<String> getFiles(String p_211685_1_, int p_211685_2_, Predicate<String> p_211685_3_) {
      Path path = this.baseDir.toPath().resolve("minecraft/");

      try (Stream<Path> stream = Files.walk(path.resolve(p_211685_1_), p_211685_2_)) {
         Collection collection = stream.filter((p_211686_0_) -> {
            return Files.isRegularFile(p_211686_0_);
         }).filter((p_211687_0_) -> {
            return !p_211687_0_.endsWith(".mcmeta");
         }).map(path::relativize).map(Object::toString).map((p_211849_0_) -> {
            return p_211849_0_.replaceAll("\\\\", "/");
         }).filter(p_211685_3_).collect(Collectors.toList());
         return collection;
      } catch (NoSuchFileException var20) {
         ;
      } catch (IOException ioexception) {
         LOGGER.warn("Unable to getFiles on {}", p_211685_1_, ioexception);
      }

      return Collections.emptyList();
   }
}