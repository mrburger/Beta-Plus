package net.minecraft.world.storage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ISaveFormat {
   DateTimeFormatter BACKUP_DATE_FORMAT = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();

   /**
    * Creates a backup of the given world.
    *  
    * @return The size of the created backup in bytes
    */
   @OnlyIn(Dist.CLIENT)
   default long createBackup(String worldName) throws IOException {
      final Path path = this.getWorldFolder(worldName);
      String s = LocalDateTime.now().format(BACKUP_DATE_FORMAT) + "_" + worldName;
      int i = 0;
      Path path1 = this.getBackupsFolder();

      try {
         Files.createDirectories(Files.exists(path1) ? path1.toRealPath() : path1);
      } catch (IOException ioexception) {
         throw new RuntimeException(ioexception);
      }

      Path path2;
      while(true) {
         path2 = path1.resolve(s + (i++ > 0 ? "_" + i : "") + ".zip");
         if (!Files.exists(path2)) {
            break;
         }
      }

      try (final ZipOutputStream zipoutputstream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(path2.toFile())))) {
         final Path path3 = Paths.get(worldName);
         Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path p_visitFile_1_, BasicFileAttributes p_visitFile_2_) throws IOException {
               String s1 = path3.resolve(path.relativize(p_visitFile_1_)).toString();
               ZipEntry zipentry = new ZipEntry(s1);
               zipoutputstream.putNextEntry(zipentry);
               com.google.common.io.Files.asByteSource(p_visitFile_1_.toFile()).copyTo(zipoutputstream);
               zipoutputstream.closeEntry();
               return FileVisitResult.CONTINUE;
            }
         });
         zipoutputstream.close();
      }

      return Files.size(path2);
   }

   @OnlyIn(Dist.CLIENT)
   String getName();

   ISaveHandler getSaveLoader(String saveName, @Nullable MinecraftServer server);

   @OnlyIn(Dist.CLIENT)
   List<WorldSummary> getSaveList() throws AnvilConverterException;

   @OnlyIn(Dist.CLIENT)
   void flushCache();

   /**
    * Returns the world's WorldInfo object
    */
   @Nullable
   WorldInfo getWorldInfo(String saveName);

   @OnlyIn(Dist.CLIENT)
   boolean isNewLevelIdAcceptable(String saveName);

   /**
    * Deletes a world directory.
    */
   @OnlyIn(Dist.CLIENT)
   boolean deleteWorldDirectory(String saveName);

   /**
    * Renames the world by storing the new name in level.dat. It does *not* rename the directory containing the world
    * data.
    */
   @OnlyIn(Dist.CLIENT)
   void renameWorld(String dirName, String newName);

   @OnlyIn(Dist.CLIENT)
   boolean isConvertible(String saveName);

   /**
    * gets if the map is old chunk saving (true) or McRegion (false)
    */
   boolean isOldMapFormat(String saveName);

   /**
    * converts the map to mcRegion
    */
   boolean convertMapFormat(String filename, IProgressUpdate progressCallback);

   /**
    * Return whether the given world can be loaded.
    */
   @OnlyIn(Dist.CLIENT)
   boolean canLoadWorld(String saveName);

   /**
    * Gets a file within the given world.
    *  
    * @param saveName Name of the world
    * @param filePath Path to the file, relative to the world's folder
    */
   File getFile(String saveName, String filePath);

   /**
    * Gets the folder for the given world.
    */
   @OnlyIn(Dist.CLIENT)
   Path getWorldFolder(String saveName);

   /**
    * Gets the folder where backups are stored
    */
   @OnlyIn(Dist.CLIENT)
   Path getBackupsFolder();
}