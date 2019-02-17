package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

public class SeedCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("seed").requires((p_198673_0_) -> {
         return p_198673_0_.getServer().isSinglePlayer() || p_198673_0_.hasPermissionLevel(2);
      }).executes((p_198672_0_) -> {
         long i = p_198672_0_.getSource().getWorld().getSeed();
         ITextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets((new TextComponentString(String.valueOf(i))).applyTextStyle((p_211752_2_) -> {
            p_211752_2_.setColor(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.valueOf(i))).setInsertion(String.valueOf(i));
         }));
         p_198672_0_.getSource().sendFeedback(new TextComponentTranslation("commands.seed.success", itextcomponent), false);
         return (int)i;
      }));
   }
}