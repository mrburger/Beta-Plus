package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentUtils;

public class TellRawCommand {
   public static void register(CommandDispatcher<CommandSource> p_198818_0_) {
      p_198818_0_.register(Commands.literal("tellraw").requires((p_198820_0_) -> {
         return p_198820_0_.hasPermissionLevel(2);
      }).then(Commands.argument("targets", EntityArgument.multiplePlayers()).then(Commands.argument("message", ComponentArgument.component()).executes((p_198819_0_) -> {
         int i = 0;

         for(EntityPlayerMP entityplayermp : EntityArgument.getPlayers(p_198819_0_, "targets")) {
            entityplayermp.sendMessage(TextComponentUtils.updateForEntity(p_198819_0_.getSource(), ComponentArgument.getComponent(p_198819_0_, "message"), entityplayermp));
            ++i;
         }

         return i;
      }))));
   }
}