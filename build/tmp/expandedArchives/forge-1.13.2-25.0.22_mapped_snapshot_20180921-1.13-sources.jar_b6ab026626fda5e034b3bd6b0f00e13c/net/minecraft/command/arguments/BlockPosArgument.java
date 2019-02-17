package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class BlockPosArgument implements ArgumentType<ILocationArgument> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
   public static final SimpleCommandExceptionType field_197278_b = new SimpleCommandExceptionType(new TextComponentTranslation("argument.pos.unloaded"));
   public static final SimpleCommandExceptionType field_197279_c = new SimpleCommandExceptionType(new TextComponentTranslation("argument.pos.outofworld"));

   public static BlockPosArgument blockPos() {
      return new BlockPosArgument();
   }

   public static BlockPos getLoadedBlockPos(CommandContext<CommandSource> context, String p_197273_1_) throws CommandSyntaxException {
      BlockPos blockpos = context.getArgument(p_197273_1_, ILocationArgument.class).getBlockPos(context.getSource());
      if (!context.getSource().getWorld().isBlockLoaded(blockpos)) {
         throw field_197278_b.create();
      } else {
         context.getSource().getWorld();
         if (!WorldServer.isValid(blockpos)) {
            throw field_197279_c.create();
         } else {
            return blockpos;
         }
      }
   }

   public static BlockPos getBlockPos(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return context.getArgument(name, ILocationArgument.class).getBlockPos(context.getSource());
   }

   public ILocationArgument parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return (ILocationArgument)(p_parse_1_.canRead() && p_parse_1_.peek() == '^' ? LocalLocationArgument.func_200142_a(p_parse_1_) : LocationInput.func_200148_a(p_parse_1_));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      if (!(p_listSuggestions_1_.getSource() instanceof ISuggestionProvider)) {
         return Suggestions.empty();
      } else {
         String s = p_listSuggestions_2_.getRemaining();
         Collection<ISuggestionProvider.Coordinates> collection;
         if (!s.isEmpty() && s.charAt(0) == '^') {
            collection = Collections.singleton(ISuggestionProvider.Coordinates.DEFAULT_LOCAL);
         } else {
            collection = ((ISuggestionProvider)p_listSuggestions_1_.getSource()).getCoordinates(false);
         }

         return ISuggestionProvider.func_209000_a(s, collection, p_listSuggestions_2_, Commands.func_212590_a(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}