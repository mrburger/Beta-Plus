package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class MessageCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      LiteralCommandNode<CommandSource> literalcommandnode = dispatcher.register(Commands.literal("msg").then(Commands.argument("targets", EntityArgument.multiplePlayers()).then(Commands.argument("message", MessageArgument.message()).executes((p_198539_0_) -> {
         return sendPrivateMessage(p_198539_0_.getSource(), EntityArgument.getPlayers(p_198539_0_, "targets"), MessageArgument.getMessage(p_198539_0_, "message"));
      }))));
      dispatcher.register(Commands.literal("tell").redirect(literalcommandnode));
      dispatcher.register(Commands.literal("w").redirect(literalcommandnode));
   }

   private static int sendPrivateMessage(CommandSource source, Collection<EntityPlayerMP> recipients, ITextComponent message) {
      for(EntityPlayerMP entityplayermp : recipients) {
         entityplayermp.sendMessage((new TextComponentTranslation("commands.message.display.incoming", source.getDisplayName(), message.func_212638_h())).applyTextStyles(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}));
         source.sendFeedback((new TextComponentTranslation("commands.message.display.outgoing", entityplayermp.getDisplayName(), message.func_212638_h())).applyTextStyles(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}), false);
      }

      return recipients.size();
   }
}