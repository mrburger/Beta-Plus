package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ItemArgument implements ArgumentType<ItemInput> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");

   public static ItemArgument itemStack() {
      return new ItemArgument();
   }

   public ItemInput parse(StringReader p_parse_1_) throws CommandSyntaxException {
      ItemParser itemparser = (new ItemParser(p_parse_1_, false)).parse();
      return new ItemInput(itemparser.getItem(), itemparser.getNbt());
   }

   public static <S> ItemInput getItemStack(CommandContext<S> p_197316_0_, String p_197316_1_) {
      return p_197316_0_.getArgument(p_197316_1_, ItemInput.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      StringReader stringreader = new StringReader(p_listSuggestions_2_.getInput());
      stringreader.setCursor(p_listSuggestions_2_.getStart());
      ItemParser itemparser = new ItemParser(stringreader, false);

      try {
         itemparser.parse();
      } catch (CommandSyntaxException var6) {
         ;
      }

      return itemparser.func_197329_a(p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}