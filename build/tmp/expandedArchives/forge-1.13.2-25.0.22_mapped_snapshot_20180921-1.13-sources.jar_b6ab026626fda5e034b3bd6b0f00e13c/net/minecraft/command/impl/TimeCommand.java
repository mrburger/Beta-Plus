package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class TimeCommand {
   public static void register(CommandDispatcher<CommandSource> p_198823_0_) {
      p_198823_0_.register(Commands.literal("time").requires((p_198828_0_) -> {
         return p_198828_0_.hasPermissionLevel(2);
      }).then(Commands.literal("set").then(Commands.literal("day").executes((p_198832_0_) -> {
         return func_198829_a(p_198832_0_.getSource(), 1000);
      })).then(Commands.literal("noon").executes((p_198825_0_) -> {
         return func_198829_a(p_198825_0_.getSource(), 6000);
      })).then(Commands.literal("night").executes((p_198822_0_) -> {
         return func_198829_a(p_198822_0_.getSource(), 13000);
      })).then(Commands.literal("midnight").executes((p_200563_0_) -> {
         return func_198829_a(p_200563_0_.getSource(), 18000);
      })).then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((p_200564_0_) -> {
         return func_198829_a(p_200564_0_.getSource(), IntegerArgumentType.getInteger(p_200564_0_, "time"));
      }))).then(Commands.literal("add").then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((p_198830_0_) -> {
         return func_198826_b(p_198830_0_.getSource(), IntegerArgumentType.getInteger(p_198830_0_, "time"));
      }))).then(Commands.literal("query").then(Commands.literal("daytime").executes((p_198827_0_) -> {
         return func_198824_c(p_198827_0_.getSource(), func_198833_a(p_198827_0_.getSource().getWorld()));
      })).then(Commands.literal("gametime").executes((p_198821_0_) -> {
         return func_198824_c(p_198821_0_.getSource(), (int)(p_198821_0_.getSource().getWorld().getGameTime() % 2147483647L));
      })).then(Commands.literal("day").executes((p_198831_0_) -> {
         return func_198824_c(p_198831_0_.getSource(), (int)(p_198831_0_.getSource().getWorld().getDayTime() / 24000L % 2147483647L));
      }))));
   }

   private static int func_198833_a(WorldServer p_198833_0_) {
      return (int)(p_198833_0_.getDayTime() % 24000L);
   }

   private static int func_198824_c(CommandSource p_198824_0_, int p_198824_1_) {
      p_198824_0_.sendFeedback(new TextComponentTranslation("commands.time.query", p_198824_1_), false);
      return p_198824_1_;
   }

   public static int func_198829_a(CommandSource p_198829_0_, int p_198829_1_) {
      for(WorldServer worldserver : p_198829_0_.getServer().func_212370_w()) {
         worldserver.setDayTime((long)p_198829_1_);
      }

      p_198829_0_.sendFeedback(new TextComponentTranslation("commands.time.set", p_198829_1_), true);
      return func_198833_a(p_198829_0_.getWorld());
   }

   public static int func_198826_b(CommandSource p_198826_0_, int p_198826_1_) {
      for(WorldServer worldserver : p_198826_0_.getServer().func_212370_w()) {
         worldserver.setDayTime(worldserver.getDayTime() + (long)p_198826_1_);
      }

      int i = func_198833_a(p_198826_0_.getWorld());
      p_198826_0_.sendFeedback(new TextComponentTranslation("commands.time.set", i), true);
      return i;
   }
}