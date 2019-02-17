package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.UserListIPBans;
import net.minecraft.server.management.UserListIPBansEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class BanIpCommand {
   public static final Pattern IP_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
   private static final SimpleCommandExceptionType IP_INVALID = new SimpleCommandExceptionType(new TextComponentTranslation("commands.banip.invalid"));
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.banip.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("ban-ip").requires((p_198222_0_) -> {
         return p_198222_0_.getServer().getPlayerList().getBannedIPs().isLanServer() && p_198222_0_.hasPermissionLevel(3);
      }).then(Commands.argument("target", StringArgumentType.word()).executes((p_198219_0_) -> {
         return banUsernameOrIp(p_198219_0_.getSource(), StringArgumentType.getString(p_198219_0_, "target"), (ITextComponent)null);
      }).then(Commands.argument("reason", MessageArgument.message()).executes((p_198221_0_) -> {
         return banUsernameOrIp(p_198221_0_.getSource(), StringArgumentType.getString(p_198221_0_, "target"), MessageArgument.getMessage(p_198221_0_, "reason"));
      }))));
   }

   private static int banUsernameOrIp(CommandSource source, String username, @Nullable ITextComponent reason) throws CommandSyntaxException {
      Matcher matcher = IP_PATTERN.matcher(username);
      if (matcher.matches()) {
         return banIpAddress(source, username, reason);
      } else {
         EntityPlayerMP entityplayermp = source.getServer().getPlayerList().getPlayerByUsername(username);
         if (entityplayermp != null) {
            return banIpAddress(source, entityplayermp.getPlayerIP(), reason);
         } else {
            throw IP_INVALID.create();
         }
      }
   }

   private static int banIpAddress(CommandSource source, String ip, @Nullable ITextComponent reason) throws CommandSyntaxException {
      UserListIPBans userlistipbans = source.getServer().getPlayerList().getBannedIPs();
      if (userlistipbans.isBanned(ip)) {
         throw FAILED_EXCEPTION.create();
      } else {
         List<EntityPlayerMP> list = source.getServer().getPlayerList().getPlayersMatchingAddress(ip);
         UserListIPBansEntry userlistipbansentry = new UserListIPBansEntry(ip, (Date)null, source.getName(), (Date)null, reason == null ? null : reason.getString());
         userlistipbans.addEntry(userlistipbansentry);
         source.sendFeedback(new TextComponentTranslation("commands.banip.success", ip, userlistipbansentry.getBanReason()), true);
         if (!list.isEmpty()) {
            source.sendFeedback(new TextComponentTranslation("commands.banip.info", list.size(), EntitySelector.joinNames(list)), true);
         }

         for(EntityPlayerMP entityplayermp : list) {
            entityplayermp.connection.disconnect(new TextComponentTranslation("multiplayer.disconnect.ip_banned"));
         }

         return list.size();
      }
   }
}