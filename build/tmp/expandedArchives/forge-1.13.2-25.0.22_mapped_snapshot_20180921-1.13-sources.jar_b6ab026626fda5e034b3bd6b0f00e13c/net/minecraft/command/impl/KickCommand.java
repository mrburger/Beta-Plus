package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class KickCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("kick").requires((p_198517_0_) -> {
         return p_198517_0_.hasPermissionLevel(3);
      }).then(Commands.argument("targets", EntityArgument.multiplePlayers()).executes((p_198513_0_) -> {
         return kickPlayers(p_198513_0_.getSource(), EntityArgument.getPlayers(p_198513_0_, "targets"), new TextComponentTranslation("multiplayer.disconnect.kicked"));
      }).then(Commands.argument("reason", MessageArgument.message()).executes((p_198516_0_) -> {
         return kickPlayers(p_198516_0_.getSource(), EntityArgument.getPlayers(p_198516_0_, "targets"), MessageArgument.getMessage(p_198516_0_, "reason"));
      }))));
   }

   private static int kickPlayers(CommandSource source, Collection<EntityPlayerMP> players, ITextComponent reason) {
      for(EntityPlayerMP entityplayermp : players) {
         entityplayermp.connection.disconnect(reason);
         source.sendFeedback(new TextComponentTranslation("commands.kick.success", entityplayermp.getDisplayName(), reason), true);
      }

      return players.size();
   }
}