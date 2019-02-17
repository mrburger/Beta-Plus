package net.minecraft.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.server.management.UserListBans;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;

public class PardonCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.pardon.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("pardon").requires((p_198551_0_) -> {
         return p_198551_0_.getServer().getPlayerList().getBannedIPs().isLanServer() && p_198551_0_.hasPermissionLevel(3);
      }).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198549_0_, p_198549_1_) -> {
         return ISuggestionProvider.suggest(p_198549_0_.getSource().getServer().getPlayerList().getBannedPlayers().getKeys(), p_198549_1_);
      }).executes((p_198550_0_) -> {
         return unbanPlayers(p_198550_0_.getSource(), GameProfileArgument.getGameProfiles(p_198550_0_, "targets"));
      })));
   }

   private static int unbanPlayers(CommandSource source, Collection<GameProfile> gameProfiles) throws CommandSyntaxException {
      UserListBans userlistbans = source.getServer().getPlayerList().getBannedPlayers();
      int i = 0;

      for(GameProfile gameprofile : gameProfiles) {
         if (userlistbans.isBanned(gameprofile)) {
            userlistbans.removeEntry(gameprofile);
            ++i;
            source.sendFeedback(new TextComponentTranslation("commands.pardon.success", TextComponentUtils.getDisplayName(gameprofile)), true);
         }
      }

      if (i == 0) {
         throw FAILED_EXCEPTION.create();
      } else {
         return i;
      }
   }
}