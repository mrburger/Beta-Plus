package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.regex.Matcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.server.management.UserListIPBans;
import net.minecraft.util.text.TextComponentTranslation;

public class PardonIpCommand {
   private static final SimpleCommandExceptionType IP_INVALID_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.pardonip.invalid"));
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.pardonip.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("pardon-ip").requires((p_198556_0_) -> {
         return p_198556_0_.getServer().getPlayerList().getBannedIPs().isLanServer() && p_198556_0_.hasPermissionLevel(3);
      }).then(Commands.argument("target", StringArgumentType.word()).suggests((p_198554_0_, p_198554_1_) -> {
         return ISuggestionProvider.suggest(p_198554_0_.getSource().getServer().getPlayerList().getBannedIPs().getKeys(), p_198554_1_);
      }).executes((p_198555_0_) -> {
         return unbanIp(p_198555_0_.getSource(), StringArgumentType.getString(p_198555_0_, "target"));
      })));
   }

   private static int unbanIp(CommandSource source, String ipAddress) throws CommandSyntaxException {
      Matcher matcher = BanIpCommand.IP_PATTERN.matcher(ipAddress);
      if (!matcher.matches()) {
         throw IP_INVALID_EXCEPTION.create();
      } else {
         UserListIPBans userlistipbans = source.getServer().getPlayerList().getBannedIPs();
         if (!userlistipbans.isBanned(ipAddress)) {
            throw FAILED_EXCEPTION.create();
         } else {
            userlistipbans.removeEntry(ipAddress);
            source.sendFeedback(new TextComponentTranslation("commands.pardonip.success", ipAddress), true);
            return 1;
         }
      }
   }
}