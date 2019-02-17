package net.minecraft.command.impl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.UserListEntryBan;
import net.minecraft.util.text.TextComponentTranslation;

public class BanListCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("banlist").requires((p_198233_0_) -> {
         return (p_198233_0_.getServer().getPlayerList().getBannedPlayers().isLanServer() || p_198233_0_.getServer().getPlayerList().getBannedIPs().isLanServer()) && p_198233_0_.hasPermissionLevel(3);
      }).executes((p_198231_0_) -> {
         PlayerList playerlist = p_198231_0_.getSource().getServer().getPlayerList();
         return sendBanList(p_198231_0_.getSource(), Lists.newArrayList(Iterables.concat(playerlist.getBannedPlayers().getEntries(), playerlist.getBannedIPs().getEntries())));
      }).then(Commands.literal("ips").executes((p_198228_0_) -> {
         return sendBanList(p_198228_0_.getSource(), p_198228_0_.getSource().getServer().getPlayerList().getBannedIPs().getEntries());
      })).then(Commands.literal("players").executes((p_198232_0_) -> {
         return sendBanList(p_198232_0_.getSource(), p_198232_0_.getSource().getServer().getPlayerList().getBannedPlayers().getEntries());
      })));
   }

   private static int sendBanList(CommandSource source, Collection<? extends UserListEntryBan<?>> bannedPlayerList) {
      if (bannedPlayerList.isEmpty()) {
         source.sendFeedback(new TextComponentTranslation("commands.banlist.none"), false);
      } else {
         source.sendFeedback(new TextComponentTranslation("commands.banlist.list", bannedPlayerList.size()), false);

         for(UserListEntryBan<?> userlistentryban : bannedPlayerList) {
            source.sendFeedback(new TextComponentTranslation("commands.banlist.entry", userlistentryban.getDisplayName(), userlistentryban.getBannedBy(), userlistentryban.getBanReason()), false);
         }
      }

      return bannedPlayerList.size();
   }
}