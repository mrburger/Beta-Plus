package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

public class Main {
   public static void main(String[] p_main_0_) throws IOException {
      OptionParser optionparser = new OptionParser();
      AbstractOptionSpec<Void> abstractoptionspec = optionparser.accepts("help", "Show the help menu").forHelp();
      OptionSpecBuilder optionspecbuilder = optionparser.accepts("server", "Include server generators");
      OptionSpecBuilder optionspecbuilder1 = optionparser.accepts("client", "Include client generators");
      OptionSpecBuilder optionspecbuilder2 = optionparser.accepts("dev", "Include development tools");
      OptionSpecBuilder optionspecbuilder3 = optionparser.accepts("reports", "Include data reports");
      OptionSpecBuilder optionspecbuilder4 = optionparser.accepts("all", "Include all generators");
      ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec = optionparser.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated");
      ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec1 = optionparser.accepts("input", "Input folder").withRequiredArg();
      OptionSet optionset = optionparser.parse(p_main_0_);
      if (!optionset.has(abstractoptionspec) && optionset.hasOptions()) {
         Path path = Paths.get(argumentacceptingoptionspec.value(optionset));
         boolean flag = optionset.has(optionspecbuilder1) || optionset.has(optionspecbuilder4);
         boolean flag1 = optionset.has(optionspecbuilder) || optionset.has(optionspecbuilder4);
         boolean flag2 = optionset.has(optionspecbuilder2) || optionset.has(optionspecbuilder4);
         boolean flag3 = optionset.has(optionspecbuilder3) || optionset.has(optionspecbuilder4);
         DataGenerator datagenerator = makeGenerator(path, optionset.valuesOf(argumentacceptingoptionspec1).stream().map((p_200263_0_) -> {
            return Paths.get(p_200263_0_);
         }).collect(Collectors.toList()), flag, flag1, flag2, flag3);
         datagenerator.run();
      } else {
         optionparser.printHelpOn(System.out);
      }
   }

   /**
    * Creates a data generator based on the given options
    *  
    * @param output Output folder
    * @param inputs Input folders
    * @param client True if client data should be included. This only runs the SNBT to NBT converter.
    * @param server True if server data should be included. This runs the SNBT to NBT converter, the tag providers, the
    * recipe provider, and the advancements provider.
    * @param dev True if dev data should be included. This only runs the NBT to SNBT converter.
    * @param reports True if data reports should be included. This runs the block and item list reports, and the command
    * tree report.
    */
   public static DataGenerator makeGenerator(Path output, Collection<Path> inputs, boolean client, boolean server, boolean dev, boolean reports) {
      DataGenerator datagenerator = new DataGenerator(output, inputs);
      if (client || server) {
         datagenerator.addProvider(new SNBTToNBTConverter(datagenerator));
      }

      if (server) {
         datagenerator.addProvider(new FluidTagsProvider(datagenerator));
         datagenerator.addProvider(new BlockTagsProvider(datagenerator));
         datagenerator.addProvider(new ItemTagsProvider(datagenerator));
         datagenerator.addProvider(new RecipeProvider(datagenerator));
         datagenerator.addProvider(new AdvancementProvider(datagenerator));
      }

      if (dev) {
         datagenerator.addProvider(new NBTToSNBTConverter(datagenerator));
      }

      if (reports) {
         datagenerator.addProvider(new BlockListReport(datagenerator));
         datagenerator.addProvider(new ItemListReport(datagenerator));
         datagenerator.addProvider(new CommandsReport(datagenerator));
      }

      return datagenerator;
   }
}