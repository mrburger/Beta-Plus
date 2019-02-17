package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.util.text.TextComponentTranslation;

public class ScoreHolderArgument implements ArgumentType<ScoreHolderArgument.INameProvider> {
   public static final SuggestionProvider<CommandSource> field_201326_a = (p_201323_0_, p_201323_1_) -> {
      StringReader stringreader = new StringReader(p_201323_1_.getInput());
      stringreader.setCursor(p_201323_1_.getStart());
      EntitySelectorParser entityselectorparser = new EntitySelectorParser(stringreader);

      try {
         entityselectorparser.parse();
      } catch (CommandSyntaxException var5) {
         ;
      }

      return entityselectorparser.fillSuggestions(p_201323_1_, (p_201949_1_) -> {
         ISuggestionProvider.suggest(p_201323_0_.getSource().getPlayerNames(), p_201949_1_);
      });
   };
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "*", "@e");
   private static final SimpleCommandExceptionType EMPTY_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("argument.scoreHolder.empty"));
   private final boolean allowMultiple;

   public ScoreHolderArgument(boolean allowMultipleIn) {
      this.allowMultiple = allowMultipleIn;
   }

   public static String func_197211_a(CommandContext<CommandSource> p_197211_0_, String p_197211_1_) throws CommandSyntaxException {
      return func_197213_b(p_197211_0_, p_197211_1_).iterator().next();
   }

   public static Collection<String> func_197213_b(CommandContext<CommandSource> p_197213_0_, String p_197213_1_) throws CommandSyntaxException {
      return func_197210_a(p_197213_0_, p_197213_1_, Collections::emptyList);
   }

   public static Collection<String> getScoreHolder(CommandContext<CommandSource> p_211707_0_, String p_211707_1_) throws CommandSyntaxException {
      ServerScoreboard serverscoreboard = p_211707_0_.getSource().getServer().getWorldScoreboard();
      return func_197210_a(p_211707_0_, p_211707_1_, serverscoreboard::getObjectiveNames);
   }

   public static Collection<String> func_197210_a(CommandContext<CommandSource> p_197210_0_, String p_197210_1_, Supplier<Collection<String>> p_197210_2_) throws CommandSyntaxException {
      Collection<String> collection = p_197210_0_.getArgument(p_197210_1_, ScoreHolderArgument.INameProvider.class).getNames(p_197210_0_.getSource(), p_197210_2_);
      if (collection.isEmpty()) {
         throw EntityArgument.ENTITY_NOT_FOUND.create();
      } else {
         return collection;
      }
   }

   public static ScoreHolderArgument singleScoreHolder() {
      return new ScoreHolderArgument(false);
   }

   public static ScoreHolderArgument multipleScoreHolder() {
      return new ScoreHolderArgument(true);
   }

   public ScoreHolderArgument.INameProvider parse(StringReader p_parse_1_) throws CommandSyntaxException {
      if (p_parse_1_.canRead() && p_parse_1_.peek() == '@') {
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(p_parse_1_);
         EntitySelector entityselector = entityselectorparser.parse();
         if (!this.allowMultiple && entityselector.getLimit() > 1) {
            throw EntityArgument.TOO_MANY_ENTITIES.create();
         } else {
            return new ScoreHolderArgument.NameProvider(entityselector);
         }
      } else {
         int i = p_parse_1_.getCursor();

         while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
            p_parse_1_.skip();
         }

         String s = p_parse_1_.getString().substring(i, p_parse_1_.getCursor());
         if (s.equals("*")) {
            return (p_197208_0_, p_197208_1_) -> {
               Collection<String> collection1 = (Collection)p_197208_1_.get();
               if (collection1.isEmpty()) {
                  throw EMPTY_EXCEPTION.create();
               } else {
                  return collection1;
               }
            };
         } else {
            Collection<String> collection = Collections.singleton(s);
            return (p_197212_1_, p_197212_2_) -> {
               return collection;
            };
         }
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   @FunctionalInterface
   public interface INameProvider {
      Collection<String> getNames(CommandSource p_getNames_1_, Supplier<Collection<String>> p_getNames_2_) throws CommandSyntaxException;
   }

   public static class NameProvider implements ScoreHolderArgument.INameProvider {
      private final EntitySelector field_197205_a;

      public NameProvider(EntitySelector p_i47977_1_) {
         this.field_197205_a = p_i47977_1_;
      }

      public Collection<String> getNames(CommandSource p_getNames_1_, Supplier<Collection<String>> p_getNames_2_) throws CommandSyntaxException {
         List<? extends Entity> list = this.field_197205_a.select(p_getNames_1_);
         if (list.isEmpty()) {
            throw EntityArgument.ENTITY_NOT_FOUND.create();
         } else {
            List<String> list1 = Lists.newArrayList();

            for(Entity entity : list) {
               list1.add(entity.getScoreboardName());
            }

            return list1;
         }
      }
   }

   public static class Serializer implements IArgumentSerializer<ScoreHolderArgument> {
      public void write(ScoreHolderArgument argument, PacketBuffer buffer) {
         byte b0 = 0;
         if (argument.allowMultiple) {
            b0 = (byte)(b0 | 1);
         }

         buffer.writeByte(b0);
      }

      public ScoreHolderArgument read(PacketBuffer buffer) {
         byte b0 = buffer.readByte();
         boolean flag = (b0 & 1) != 0;
         return new ScoreHolderArgument(flag);
      }

      public void func_212244_a(ScoreHolderArgument p_212244_1_, JsonObject p_212244_2_) {
         p_212244_2_.addProperty("amount", p_212244_1_.allowMultiple ? "multiple" : "single");
      }
   }
}