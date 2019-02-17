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

public class ColumnPosArgument implements ArgumentType<ILocationArgument> {
   private static final Collection<String> field_212605_b = Arrays.asList("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
   public static final SimpleCommandExceptionType field_212604_a = new SimpleCommandExceptionType(new TextComponentTranslation("argument.pos2d.incomplete"));

   public static ColumnPosArgument func_212603_a() {
      return new ColumnPosArgument();
   }

   public static ColumnPosArgument.ColumnPos func_212602_a(CommandContext<CommandSource> p_212602_0_, String p_212602_1_) {
      BlockPos blockpos = p_212602_0_.getArgument(p_212602_1_, ILocationArgument.class).getBlockPos(p_212602_0_.getSource());
      return new ColumnPosArgument.ColumnPos(blockpos.getX(), blockpos.getZ());
   }

   public ILocationArgument parse(StringReader p_parse_1_) throws CommandSyntaxException {
      int i = p_parse_1_.getCursor();
      if (!p_parse_1_.canRead()) {
         throw field_212604_a.createWithContext(p_parse_1_);
      } else {
         LocationPart locationpart = LocationPart.func_197307_a(p_parse_1_);
         if (p_parse_1_.canRead() && p_parse_1_.peek() == ' ') {
            p_parse_1_.skip();
            LocationPart locationpart1 = LocationPart.func_197307_a(p_parse_1_);
            return new LocationInput(locationpart, new LocationPart(true, 0.0D), locationpart1);
         } else {
            p_parse_1_.setCursor(i);
            throw field_212604_a.createWithContext(p_parse_1_);
         }
      }
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

         return ISuggestionProvider.func_211269_a(s, collection, p_listSuggestions_2_, Commands.func_212590_a(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return field_212605_b;
   }

   public static class ColumnPos {
      public final int field_212600_a;
      public final int field_212601_b;

      public ColumnPos(int p_i49829_1_, int p_i49829_2_) {
         this.field_212600_a = p_i49829_1_;
         this.field_212601_b = p_i49829_2_;
      }

      public String toString() {
         return "[" + this.field_212600_a + ", " + this.field_212601_b + "]";
      }
   }
}