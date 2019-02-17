package net.minecraft.command.arguments;

import com.google.common.collect.Streams;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.dimension.DimensionType;

public class DimensionArgument implements ArgumentType<DimensionType> {
   private static final Collection<String> field_212597_b = Stream.of(DimensionType.OVERWORLD, DimensionType.NETHER).map((p_212593_0_) -> {
      return DimensionType.func_212678_a(p_212593_0_).toString();
   }).collect(Collectors.toList());
   public static final DynamicCommandExceptionType field_212596_a = new DynamicCommandExceptionType((p_212594_0_) -> {
      return new TextComponentTranslation("argument.dimension.invalid", p_212594_0_);
   });

   public <S> DimensionType parse(StringReader p_parse_1_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = ResourceLocation.read(p_parse_1_);
      DimensionType dimensiontype = DimensionType.byName(resourcelocation);
      if (dimensiontype == null) {
         throw field_212596_a.create(resourcelocation);
      } else {
         return dimensiontype;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.func_212476_a(Streams.stream(DimensionType.func_212681_b()).map(DimensionType::func_212678_a), p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return field_212597_b;
   }

   public static DimensionArgument func_212595_a() {
      return new DimensionArgument();
   }

   public static DimensionType func_212592_a(CommandContext<CommandSource> p_212592_0_, String p_212592_1_) {
      return p_212592_0_.getArgument(p_212592_1_, DimensionType.class);
   }
}