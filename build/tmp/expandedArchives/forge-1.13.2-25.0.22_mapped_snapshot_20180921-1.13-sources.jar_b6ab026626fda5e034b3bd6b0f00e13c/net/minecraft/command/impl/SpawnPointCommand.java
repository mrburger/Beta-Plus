package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class SpawnPointCommand {
   public static void register(CommandDispatcher<CommandSource> p_198695_0_) {
      p_198695_0_.register(Commands.literal("spawnpoint").requires((p_198699_0_) -> {
         return p_198699_0_.hasPermissionLevel(2);
      }).executes((p_198697_0_) -> {
         return func_198696_a(p_198697_0_.getSource(), Collections.singleton(p_198697_0_.getSource().asPlayer()), new BlockPos(p_198697_0_.getSource().getPos()));
      }).then(Commands.argument("targets", EntityArgument.multiplePlayers()).executes((p_198694_0_) -> {
         return func_198696_a(p_198694_0_.getSource(), EntityArgument.getPlayers(p_198694_0_, "targets"), new BlockPos(p_198694_0_.getSource().getPos()));
      }).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_198698_0_) -> {
         return func_198696_a(p_198698_0_.getSource(), EntityArgument.getPlayers(p_198698_0_, "targets"), BlockPosArgument.getBlockPos(p_198698_0_, "pos"));
      }))));
   }

   private static int func_198696_a(CommandSource p_198696_0_, Collection<EntityPlayerMP> p_198696_1_, BlockPos p_198696_2_) {
      for(EntityPlayerMP entityplayermp : p_198696_1_) {
         entityplayermp.setSpawnPoint(p_198696_2_, true);
      }

      if (p_198696_1_.size() == 1) {
         p_198696_0_.sendFeedback(new TextComponentTranslation("commands.spawnpoint.success.single", p_198696_2_.getX(), p_198696_2_.getY(), p_198696_2_.getZ(), p_198696_1_.iterator().next().getDisplayName()), true);
      } else {
         p_198696_0_.sendFeedback(new TextComponentTranslation("commands.spawnpoint.success.multiple", p_198696_2_.getX(), p_198696_2_.getY(), p_198696_2_.getZ(), p_198696_1_.size()), true);
      }

      return p_198696_1_.size();
   }
}