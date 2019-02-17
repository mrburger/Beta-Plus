package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.text.TextComponentTranslation;

public class PublishCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.publish.failed"));
   private static final DynamicCommandExceptionType ALREADY_PUBLISHED_EXCEPTION = new DynamicCommandExceptionType((p_208900_0_) -> {
      return new TextComponentTranslation("commands.publish.alreadyPublished", p_208900_0_);
   });

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("publish").requires((p_198583_0_) -> {
         return p_198583_0_.getServer().isSinglePlayer() && p_198583_0_.hasPermissionLevel(4);
      }).executes((p_198580_0_) -> {
         return openToLan(p_198580_0_.getSource(), HttpUtil.getSuitableLanPort());
      }).then(Commands.argument("port", IntegerArgumentType.integer(0, 65535)).executes((p_198582_0_) -> {
         return openToLan(p_198582_0_.getSource(), IntegerArgumentType.getInteger(p_198582_0_, "port"));
      })));
   }

   private static int openToLan(CommandSource source, int port) throws CommandSyntaxException {
      if (source.getServer().getPublic()) {
         throw ALREADY_PUBLISHED_EXCEPTION.create(source.getServer().getServerPort());
      } else if (!source.getServer().shareToLAN(source.getServer().getGameType(), false, port)) {
         throw FAILED_EXCEPTION.create();
      } else {
         source.sendFeedback(new TextComponentTranslation("commands.publish.success", port), true);
         return port;
      }
   }
}