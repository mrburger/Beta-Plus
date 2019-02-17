package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;

public class ListCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("list").executes((p_198523_0_) -> {
         return listNames(p_198523_0_.getSource());
      }).then(Commands.literal("uuids").executes((p_208202_0_) -> {
         return listUUIDs(p_208202_0_.getSource());
      })));
   }

   private static int listNames(CommandSource source) {
      return listPlayers(source, EntityPlayer::getDisplayName);
   }

   private static int listUUIDs(CommandSource source) {
      return listPlayers(source, EntityPlayer::func_208017_dF);
   }

   private static int listPlayers(CommandSource source, Function<EntityPlayerMP, ITextComponent> nameExtractor) {
      PlayerList playerlist = source.getServer().getPlayerList();
      List<EntityPlayerMP> list = playerlist.getPlayers();
      ITextComponent itextcomponent = TextComponentUtils.makeList(list, nameExtractor);
      source.sendFeedback(new TextComponentTranslation("commands.list.players", list.size(), playerlist.getMaxPlayers(), itextcomponent), false);
      return list.size();
   }
}