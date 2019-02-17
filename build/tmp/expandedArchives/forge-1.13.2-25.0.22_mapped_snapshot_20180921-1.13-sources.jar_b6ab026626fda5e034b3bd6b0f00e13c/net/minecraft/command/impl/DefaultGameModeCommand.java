package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;

public class DefaultGameModeCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("defaultgamemode").requires((p_198342_0_) -> {
         return p_198342_0_.hasPermissionLevel(2);
      });

      for(GameType gametype : GameType.values()) {
         if (gametype != GameType.NOT_SET) {
            literalargumentbuilder.then(Commands.literal(gametype.getName()).executes((p_198343_1_) -> {
               return setGameType(p_198343_1_.getSource(), gametype);
            }));
         }
      }

      dispatcher.register(literalargumentbuilder);
   }

   /**
    * Set Gametype of player who ran the command
    *  
    * @param commandSourceIn Source of /gamemode command
    * @param gamemode New gametype
    */
   private static int setGameType(CommandSource commandSourceIn, GameType gamemode) {
      int i = 0;
      MinecraftServer minecraftserver = commandSourceIn.getServer();
      minecraftserver.setGameType(gamemode);
      if (minecraftserver.getForceGamemode()) {
         for(EntityPlayerMP entityplayermp : minecraftserver.getPlayerList().getPlayers()) {
            if (entityplayermp.interactionManager.getGameType() != gamemode) {
               entityplayermp.setGameType(gamemode);
               ++i;
            }
         }
      }

      commandSourceIn.sendFeedback(new TextComponentTranslation("commands.defaultgamemode.success", gamemode.getDisplayName()), true);
      return i;
   }
}