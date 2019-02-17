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
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.UserListWhitelist;
import net.minecraft.server.management.UserListWhitelistEntry;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;

public class WhitelistCommand {
   private static final SimpleCommandExceptionType field_198887_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.whitelist.alreadyOn"));
   private static final SimpleCommandExceptionType field_198888_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.whitelist.alreadyOff"));
   private static final SimpleCommandExceptionType field_198889_c = new SimpleCommandExceptionType(new TextComponentTranslation("commands.whitelist.add.failed"));
   private static final SimpleCommandExceptionType field_198890_d = new SimpleCommandExceptionType(new TextComponentTranslation("commands.whitelist.remove.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198873_0_) {
      p_198873_0_.register(Commands.literal("whitelist").requires((p_198877_0_) -> {
         return p_198877_0_.hasPermissionLevel(3);
      }).then(Commands.literal("on").executes((p_198872_0_) -> {
         return func_198884_b(p_198872_0_.getSource());
      })).then(Commands.literal("off").executes((p_198874_0_) -> {
         return func_198885_c(p_198874_0_.getSource());
      })).then(Commands.literal("list").executes((p_198878_0_) -> {
         return func_198886_d(p_198878_0_.getSource());
      })).then(Commands.literal("add").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198879_0_, p_198879_1_) -> {
         PlayerList playerlist = p_198879_0_.getSource().getServer().getPlayerList();
         return ISuggestionProvider.suggest(playerlist.getPlayers().stream().filter((p_198871_1_) -> {
            return !playerlist.getWhitelistedPlayers().isWhitelisted(p_198871_1_.getGameProfile());
         }).map((p_200567_0_) -> {
            return p_200567_0_.getGameProfile().getName();
         }), p_198879_1_);
      }).executes((p_198875_0_) -> {
         return func_198880_a(p_198875_0_.getSource(), GameProfileArgument.getGameProfiles(p_198875_0_, "targets"));
      }))).then(Commands.literal("remove").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198881_0_, p_198881_1_) -> {
         return ISuggestionProvider.suggest(p_198881_0_.getSource().getServer().getPlayerList().getWhitelistedPlayerNames(), p_198881_1_);
      }).executes((p_198870_0_) -> {
         return func_198876_b(p_198870_0_.getSource(), GameProfileArgument.getGameProfiles(p_198870_0_, "targets"));
      }))).then(Commands.literal("reload").executes((p_198882_0_) -> {
         return func_198883_a(p_198882_0_.getSource());
      })));
   }

   private static int func_198883_a(CommandSource p_198883_0_) {
      p_198883_0_.getServer().getPlayerList().reloadWhitelist();
      p_198883_0_.sendFeedback(new TextComponentTranslation("commands.whitelist.reloaded"), true);
      p_198883_0_.getServer().kickPlayersNotWhitelisted(p_198883_0_);
      return 1;
   }

   private static int func_198880_a(CommandSource p_198880_0_, Collection<GameProfile> p_198880_1_) throws CommandSyntaxException {
      UserListWhitelist userlistwhitelist = p_198880_0_.getServer().getPlayerList().getWhitelistedPlayers();
      int i = 0;

      for(GameProfile gameprofile : p_198880_1_) {
         if (!userlistwhitelist.isWhitelisted(gameprofile)) {
            UserListWhitelistEntry userlistwhitelistentry = new UserListWhitelistEntry(gameprofile);
            userlistwhitelist.addEntry(userlistwhitelistentry);
            p_198880_0_.sendFeedback(new TextComponentTranslation("commands.whitelist.add.success", TextComponentUtils.getDisplayName(gameprofile)), true);
            ++i;
         }
      }

      if (i == 0) {
         throw field_198889_c.create();
      } else {
         return i;
      }
   }

   private static int func_198876_b(CommandSource p_198876_0_, Collection<GameProfile> p_198876_1_) throws CommandSyntaxException {
      UserListWhitelist userlistwhitelist = p_198876_0_.getServer().getPlayerList().getWhitelistedPlayers();
      int i = 0;

      for(GameProfile gameprofile : p_198876_1_) {
         if (userlistwhitelist.isWhitelisted(gameprofile)) {
            UserListWhitelistEntry userlistwhitelistentry = new UserListWhitelistEntry(gameprofile);
            userlistwhitelist.removeEntry(userlistwhitelistentry);
            p_198876_0_.sendFeedback(new TextComponentTranslation("commands.whitelist.remove.success", TextComponentUtils.getDisplayName(gameprofile)), true);
            ++i;
         }
      }

      if (i == 0) {
         throw field_198890_d.create();
      } else {
         p_198876_0_.getServer().kickPlayersNotWhitelisted(p_198876_0_);
         return i;
      }
   }

   private static int func_198884_b(CommandSource p_198884_0_) throws CommandSyntaxException {
      PlayerList playerlist = p_198884_0_.getServer().getPlayerList();
      if (playerlist.isWhiteListEnabled()) {
         throw field_198887_a.create();
      } else {
         playerlist.setWhiteListEnabled(true);
         p_198884_0_.sendFeedback(new TextComponentTranslation("commands.whitelist.enabled"), true);
         p_198884_0_.getServer().kickPlayersNotWhitelisted(p_198884_0_);
         return 1;
      }
   }

   private static int func_198885_c(CommandSource p_198885_0_) throws CommandSyntaxException {
      PlayerList playerlist = p_198885_0_.getServer().getPlayerList();
      if (!playerlist.isWhiteListEnabled()) {
         throw field_198888_b.create();
      } else {
         playerlist.setWhiteListEnabled(false);
         p_198885_0_.sendFeedback(new TextComponentTranslation("commands.whitelist.disabled"), true);
         return 1;
      }
   }

   private static int func_198886_d(CommandSource p_198886_0_) {
      String[] astring = p_198886_0_.getServer().getPlayerList().getWhitelistedPlayerNames();
      if (astring.length == 0) {
         p_198886_0_.sendFeedback(new TextComponentTranslation("commands.whitelist.none"), false);
      } else {
         p_198886_0_.sendFeedback(new TextComponentTranslation("commands.whitelist.list", astring.length, String.join(", ", astring)), false);
      }

      return astring.length;
   }
}