package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ObjectiveArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TextComponentTranslation;

public class TriggerCommand {
   private static final SimpleCommandExceptionType field_198857_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.trigger.failed.unprimed"));
   private static final SimpleCommandExceptionType field_198858_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.trigger.failed.invalid"));

   public static void register(CommandDispatcher<CommandSource> p_198852_0_) {
      p_198852_0_.register(Commands.literal("trigger").then(Commands.argument("objective", ObjectiveArgument.objective()).suggests((p_198853_0_, p_198853_1_) -> {
         return func_198850_a(p_198853_0_.getSource(), p_198853_1_);
      }).executes((p_198854_0_) -> {
         return func_201477_a(p_198854_0_.getSource(), func_198848_a(p_198854_0_.getSource().asPlayer(), ObjectiveArgument.getObjective(p_198854_0_, "objective")));
      }).then(Commands.literal("add").then(Commands.argument("value", IntegerArgumentType.integer()).executes((p_198849_0_) -> {
         return func_201479_a(p_198849_0_.getSource(), func_198848_a(p_198849_0_.getSource().asPlayer(), ObjectiveArgument.getObjective(p_198849_0_, "objective")), IntegerArgumentType.getInteger(p_198849_0_, "value"));
      }))).then(Commands.literal("set").then(Commands.argument("value", IntegerArgumentType.integer()).executes((p_198855_0_) -> {
         return func_201478_b(p_198855_0_.getSource(), func_198848_a(p_198855_0_.getSource().asPlayer(), ObjectiveArgument.getObjective(p_198855_0_, "objective")), IntegerArgumentType.getInteger(p_198855_0_, "value"));
      })))));
   }

   public static CompletableFuture<Suggestions> func_198850_a(CommandSource p_198850_0_, SuggestionsBuilder p_198850_1_) {
      Entity entity = p_198850_0_.getEntity();
      List<String> list = Lists.newArrayList();
      if (entity != null) {
         Scoreboard scoreboard = p_198850_0_.getServer().getWorldScoreboard();
         String s = entity.getScoreboardName();

         for(ScoreObjective scoreobjective : scoreboard.getScoreObjectives()) {
            if (scoreobjective.getCriteria() == ScoreCriteria.TRIGGER && scoreboard.entityHasObjective(s, scoreobjective)) {
               Score score = scoreboard.getOrCreateScore(s, scoreobjective);
               if (!score.isLocked()) {
                  list.add(scoreobjective.getName());
               }
            }
         }
      }

      return ISuggestionProvider.suggest(list, p_198850_1_);
   }

   private static int func_201479_a(CommandSource p_201479_0_, Score p_201479_1_, int p_201479_2_) {
      p_201479_1_.increaseScore(p_201479_2_);
      p_201479_0_.sendFeedback(new TextComponentTranslation("commands.trigger.add.success", p_201479_1_.getObjective().func_197890_e(), p_201479_2_), true);
      return p_201479_1_.getScorePoints();
   }

   private static int func_201478_b(CommandSource p_201478_0_, Score p_201478_1_, int p_201478_2_) {
      p_201478_1_.setScorePoints(p_201478_2_);
      p_201478_0_.sendFeedback(new TextComponentTranslation("commands.trigger.set.success", p_201478_1_.getObjective().func_197890_e(), p_201478_2_), true);
      return p_201478_2_;
   }

   private static int func_201477_a(CommandSource p_201477_0_, Score p_201477_1_) {
      p_201477_1_.increaseScore(1);
      p_201477_0_.sendFeedback(new TextComponentTranslation("commands.trigger.simple.success", p_201477_1_.getObjective().func_197890_e()), true);
      return p_201477_1_.getScorePoints();
   }

   private static Score func_198848_a(EntityPlayerMP p_198848_0_, ScoreObjective p_198848_1_) throws CommandSyntaxException {
      if (p_198848_1_.getCriteria() != ScoreCriteria.TRIGGER) {
         throw field_198858_b.create();
      } else {
         Scoreboard scoreboard = p_198848_0_.getWorldScoreboard();
         String s = p_198848_0_.getScoreboardName();
         if (!scoreboard.entityHasObjective(s, p_198848_1_)) {
            throw field_198857_a.create();
         } else {
            Score score = scoreboard.getOrCreateScore(s, p_198848_1_);
            if (score.isLocked()) {
               throw field_198857_a.create();
            } else {
               score.setLocked(true);
               return score;
            }
         }
      }
   }
}