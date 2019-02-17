package net.minecraft.data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;

public class BlockListReport implements IDataProvider {
   private final DataGenerator generator;

   public BlockListReport(DataGenerator generatorIn) {
      this.generator = generatorIn;
   }

   /**
    * Performs this provider's action.
    */
   public void act(DirectoryCache cache) throws IOException {
      JsonObject jsonobject = new JsonObject();

      for(Block block : IRegistry.field_212618_g) {
         ResourceLocation resourcelocation = IRegistry.field_212618_g.getKey(block);
         JsonObject jsonobject1 = new JsonObject();
         StateContainer<Block, IBlockState> statecontainer = block.getStateContainer();
         if (!statecontainer.getProperties().isEmpty()) {
            JsonObject jsonobject2 = new JsonObject();

            for(IProperty<?> iproperty : statecontainer.getProperties()) {
               JsonArray jsonarray = new JsonArray();

               for(Comparable<?> comparable : iproperty.getAllowedValues()) {
                  jsonarray.add(Util.getValueName(iproperty, comparable));
               }

               jsonobject2.add(iproperty.getName(), jsonarray);
            }

            jsonobject1.add("properties", jsonobject2);
         }

         JsonArray jsonarray1 = new JsonArray();

         for(IBlockState iblockstate : statecontainer.getValidStates()) {
            JsonObject jsonobject3 = new JsonObject();
            JsonObject jsonobject4 = new JsonObject();

            for(IProperty<?> iproperty1 : statecontainer.getProperties()) {
               jsonobject4.addProperty(iproperty1.getName(), Util.getValueName(iproperty1, iblockstate.get(iproperty1)));
            }

            if (jsonobject4.size() > 0) {
               jsonobject3.add("properties", jsonobject4);
            }

            jsonobject3.addProperty("id", Block.getStateId(iblockstate));
            if (iblockstate == block.getDefaultState()) {
               jsonobject3.addProperty("default", true);
            }

            jsonarray1.add(jsonobject3);
         }

         jsonobject1.add("states", jsonarray1);
         jsonobject.add(resourcelocation.toString(), jsonobject1);
      }

      Path path = this.generator.getOutputFolder().resolve("reports/blocks.json");
      Files.createDirectories(path.getParent());

      try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
         String s = (new GsonBuilder()).setPrettyPrinting().create().toJson((JsonElement)jsonobject);
         bufferedwriter.write(s);
      }

   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public String getName() {
      return "Block List";
   }
}