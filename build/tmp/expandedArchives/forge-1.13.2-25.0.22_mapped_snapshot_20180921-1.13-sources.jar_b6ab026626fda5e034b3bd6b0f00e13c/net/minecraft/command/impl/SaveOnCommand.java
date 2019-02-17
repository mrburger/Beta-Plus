package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class SaveOnCommand {
   private static final SimpleCommandExceptionType SAVE_ALREADY_ON_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.save.alreadyOn"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("save-on").requires((p_198623_0_) -> {
         return p_198623_0_.hasPermissionLevel(4);
      }).executes((p_198622_0_) -> {
         CommandSource commandsource = p_198622_0_.getSource();
         boolean flag = false;

         for(WorldServer worldserver : commandsource.getServer().func_212370_w()) {
            if (worldserver != null && worldserver.disableLevelSaving) {
               worldserver.disableLevelSaving = false;
               flag = true;
            }
         }

         if (!flag) {
            throw SAVE_ALREADY_ON_EXCEPTION.create();
         } else {
            commandsource.sendFeedback(new TextComponentTranslation("commands.save.enabled"), true);
            return 1;
         }
      }));
   }
}