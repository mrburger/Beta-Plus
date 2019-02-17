package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;

public class WeatherCommand {
   public static void register(CommandDispatcher<CommandSource> p_198862_0_) {
      p_198862_0_.register(Commands.literal("weather").requires((p_198868_0_) -> {
         return p_198868_0_.hasPermissionLevel(2);
      }).then(Commands.literal("clear").executes((p_198861_0_) -> {
         return setClear(p_198861_0_.getSource(), 6000);
      }).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((p_198864_0_) -> {
         return setClear(p_198864_0_.getSource(), IntegerArgumentType.getInteger(p_198864_0_, "duration") * 20);
      }))).then(Commands.literal("rain").executes((p_198860_0_) -> {
         return setRain(p_198860_0_.getSource(), 6000);
      }).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((p_198866_0_) -> {
         return setRain(p_198866_0_.getSource(), IntegerArgumentType.getInteger(p_198866_0_, "duration") * 20);
      }))).then(Commands.literal("thunder").executes((p_198859_0_) -> {
         return setThunder(p_198859_0_.getSource(), 6000);
      }).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((p_198867_0_) -> {
         return setThunder(p_198867_0_.getSource(), IntegerArgumentType.getInteger(p_198867_0_, "duration") * 20);
      }))));
   }

   private static int setClear(CommandSource p_198869_0_, int p_198869_1_) {
      p_198869_0_.getWorld().getWorldInfo().setClearWeatherTime(p_198869_1_);
      p_198869_0_.getWorld().getWorldInfo().setRainTime(0);
      p_198869_0_.getWorld().getWorldInfo().setThunderTime(0);
      p_198869_0_.getWorld().getWorldInfo().setRaining(false);
      p_198869_0_.getWorld().getWorldInfo().setThundering(false);
      p_198869_0_.sendFeedback(new TextComponentTranslation("commands.weather.set.clear"), true);
      return p_198869_1_;
   }

   private static int setRain(CommandSource p_198865_0_, int p_198865_1_) {
      p_198865_0_.getWorld().getWorldInfo().setClearWeatherTime(0);
      p_198865_0_.getWorld().getWorldInfo().setRainTime(p_198865_1_);
      p_198865_0_.getWorld().getWorldInfo().setThunderTime(p_198865_1_);
      p_198865_0_.getWorld().getWorldInfo().setRaining(true);
      p_198865_0_.getWorld().getWorldInfo().setThundering(false);
      p_198865_0_.sendFeedback(new TextComponentTranslation("commands.weather.set.rain"), true);
      return p_198865_1_;
   }

   private static int setThunder(CommandSource p_198863_0_, int p_198863_1_) {
      p_198863_0_.getWorld().getWorldInfo().setClearWeatherTime(0);
      p_198863_0_.getWorld().getWorldInfo().setRainTime(p_198863_1_);
      p_198863_0_.getWorld().getWorldInfo().setThunderTime(p_198863_1_);
      p_198863_0_.getWorld().getWorldInfo().setRaining(true);
      p_198863_0_.getWorld().getWorldInfo().setThundering(true);
      p_198863_0_.sendFeedback(new TextComponentTranslation("commands.weather.set.thunder"), true);
      return p_198863_1_;
   }
}