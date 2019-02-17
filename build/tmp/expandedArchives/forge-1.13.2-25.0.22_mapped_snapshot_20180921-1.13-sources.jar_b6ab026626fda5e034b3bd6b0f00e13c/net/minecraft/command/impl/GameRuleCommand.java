package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map.Entry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameRules;

public class GameRuleCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("gamerule").requires((p_198491_0_) -> {
         return p_198491_0_.hasPermissionLevel(2);
      });

      for(Entry<String, GameRules.ValueDefinition> entry : GameRules.getDefinitions().entrySet()) {
         literalargumentbuilder.then(Commands.literal(entry.getKey()).executes((p_198489_1_) -> {
            return queryRule(p_198489_1_.getSource(), entry.getKey());
         }).then(entry.getValue().getType().createArgument("value").executes((p_198490_1_) -> {
            return setRule(p_198490_1_.getSource(), entry.getKey(), p_198490_1_);
         })));
      }

      dispatcher.register(literalargumentbuilder);
   }

   private static int setRule(CommandSource source, String rule, CommandContext<CommandSource> context) {
      GameRules.Value gamerules$value = source.getServer().getGameRules().get(rule);
      gamerules$value.getType().updateValue(context, "value", gamerules$value);
      source.sendFeedback(new TextComponentTranslation("commands.gamerule.set", rule, gamerules$value.getString()), true);
      return gamerules$value.getInt();
   }

   private static int queryRule(CommandSource source, String rule) {
      GameRules.Value gamerules$value = source.getServer().getGameRules().get(rule);
      source.sendFeedback(new TextComponentTranslation("commands.gamerule.query", rule, gamerules$value.getString()), false);
      return gamerules$value.getInt();
   }
}