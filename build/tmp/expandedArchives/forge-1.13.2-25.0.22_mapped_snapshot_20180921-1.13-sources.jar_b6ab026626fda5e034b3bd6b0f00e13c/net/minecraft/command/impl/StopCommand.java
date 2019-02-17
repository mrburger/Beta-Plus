package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;

public class StopCommand {
   public static void register(CommandDispatcher<CommandSource> p_198725_0_) {
      p_198725_0_.register(Commands.literal("stop").requires((p_198727_0_) -> {
         return p_198727_0_.hasPermissionLevel(4);
      }).executes((p_198726_0_) -> {
         p_198726_0_.getSource().sendFeedback(new TextComponentTranslation("commands.stop.stopping"), true);
         p_198726_0_.getSource().getServer().initiateShutdown();
         return 1;
      }));
   }
}