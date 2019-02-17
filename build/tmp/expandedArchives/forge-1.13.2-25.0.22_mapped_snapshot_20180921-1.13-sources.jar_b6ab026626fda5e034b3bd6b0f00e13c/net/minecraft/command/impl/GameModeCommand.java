package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;

public class GameModeCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("gamemode").requires((p_198485_0_) -> {
         return p_198485_0_.hasPermissionLevel(2);
      });

      for(GameType gametype : GameType.values()) {
         if (gametype != GameType.NOT_SET) {
            literalargumentbuilder.then(Commands.literal(gametype.getName()).executes((p_198483_1_) -> {
               return setGameMode(p_198483_1_, Collections.singleton(p_198483_1_.getSource().asPlayer()), gametype);
            }).then(Commands.argument("target", EntityArgument.multiplePlayers()).executes((p_198486_1_) -> {
               return setGameMode(p_198486_1_, EntityArgument.getPlayers(p_198486_1_, "target"), gametype);
            })));
         }
      }

      dispatcher.register(literalargumentbuilder);
   }

   private static void sendGameModeFeedback(CommandSource source, EntityPlayerMP player, GameType gameTypeIn) {
      ITextComponent itextcomponent = new TextComponentTranslation("gameMode." + gameTypeIn.getName());
      if (source.getEntity() == player) {
         source.sendFeedback(new TextComponentTranslation("commands.gamemode.success.self", itextcomponent), true);
      } else {
         if (source.getWorld().getGameRules().getBoolean("sendCommandFeedback")) {
            player.sendMessage(new TextComponentTranslation("gameMode.changed", itextcomponent));
         }

         source.sendFeedback(new TextComponentTranslation("commands.gamemode.success.other", player.getDisplayName(), itextcomponent), true);
      }

   }

   private static int setGameMode(CommandContext<CommandSource> source, Collection<EntityPlayerMP> players, GameType gameTypeIn) {
      int i = 0;

      for(EntityPlayerMP entityplayermp : players) {
         if (entityplayermp.interactionManager.getGameType() != gameTypeIn) {
            entityplayermp.setGameType(gameTypeIn);
            sendGameModeFeedback(source.getSource(), entityplayermp, gameTypeIn);
            ++i;
         }
      }

      return i;
   }
}