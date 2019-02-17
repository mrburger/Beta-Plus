package net.minecraft.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTToSNBTConverter implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DataGenerator generator;

   public NBTToSNBTConverter(DataGenerator generatorIn) {
      this.generator = generatorIn;
   }

   /**
    * Performs this provider's action.
    */
   public void act(DirectoryCache cache) throws IOException {
      Path path = this.generator.getOutputFolder();

      for(Path path1 : this.generator.getInputFolders()) {
         Files.walk(path1).filter((p_200416_0_) -> {
            return p_200416_0_.toString().endsWith(".nbt");
         }).forEach((p_200415_3_) -> {
            this.convert(p_200415_3_, this.getFileName(path1, p_200415_3_), path);
         });
      }

   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public String getName() {
      return "NBT to SNBT";
   }

   /**
    * Gets the name of the given NBT file, based on its path and the input directory. The result does not have the
    * ".nbt" extension.
    */
   private String getFileName(Path inputFolder, Path fileIn) {
      String s = inputFolder.relativize(fileIn).toString().replaceAll("\\\\", "/");
      return s.substring(0, s.length() - ".nbt".length());
   }

   private void convert(Path fileIn, String name, Path outputFolder) {
      try {
         NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(Files.newInputStream(fileIn));
         ITextComponent itextcomponent = nbttagcompound.toFormattedComponent("    ", 0);
         String s = itextcomponent.getString();
         Path path = outputFolder.resolve(name + ".snbt");
         Files.createDirectories(path.getParent());

         try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
            bufferedwriter.write(s);
         }

         LOGGER.info("Converted {} from NBT to SNBT", (Object)name);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", name, fileIn, ioexception);
      }

   }
}