package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.Vec2Argument;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderCommand {
   private static final SimpleCommandExceptionType field_198911_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.center.failed"));
   private static final SimpleCommandExceptionType field_198912_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.set.failed.nochange"));
   private static final SimpleCommandExceptionType field_198913_c = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.set.failed.small."));
   private static final SimpleCommandExceptionType field_198914_d = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.set.failed.big."));
   private static final SimpleCommandExceptionType field_198915_e = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.warning.time.failed"));
   private static final SimpleCommandExceptionType field_198916_f = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.warning.distance.failed"));
   private static final SimpleCommandExceptionType field_198917_g = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.damage.buffer.failed"));
   private static final SimpleCommandExceptionType field_198918_h = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.damage.amount.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198894_0_) {
      p_198894_0_.register(Commands.literal("worldborder").requires((p_198903_0_) -> {
         return p_198903_0_.hasPermissionLevel(2);
      }).then(Commands.literal("add").then(Commands.argument("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F)).executes((p_198908_0_) -> {
         return setSize(p_198908_0_.getSource(), p_198908_0_.getSource().getWorld().getWorldBorder().getDiameter() + (double)FloatArgumentType.getFloat(p_198908_0_, "distance"), 0L);
      }).then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((p_198901_0_) -> {
         return setSize(p_198901_0_.getSource(), p_198901_0_.getSource().getWorld().getWorldBorder().getDiameter() + (double)FloatArgumentType.getFloat(p_198901_0_, "distance"), p_198901_0_.getSource().getWorld().getWorldBorder().getTimeUntilTarget() + (long)IntegerArgumentType.getInteger(p_198901_0_, "time") * 1000L);
      })))).then(Commands.literal("set").then(Commands.argument("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F)).executes((p_198906_0_) -> {
         return setSize(p_198906_0_.getSource(), (double)FloatArgumentType.getFloat(p_198906_0_, "distance"), 0L);
      }).then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((p_198909_0_) -> {
         return setSize(p_198909_0_.getSource(), (double)FloatArgumentType.getFloat(p_198909_0_, "distance"), (long)IntegerArgumentType.getInteger(p_198909_0_, "time") * 1000L);
      })))).then(Commands.literal("center").then(Commands.argument("pos", Vec2Argument.vec2()).executes((p_198893_0_) -> {
         return setCenter(p_198893_0_.getSource(), Vec2Argument.getVec2f(p_198893_0_, "pos"));
      }))).then(Commands.literal("damage").then(Commands.literal("amount").then(Commands.argument("damagePerBlock", FloatArgumentType.floatArg(0.0F)).executes((p_198897_0_) -> {
         return setDamageAmount(p_198897_0_.getSource(), FloatArgumentType.getFloat(p_198897_0_, "damagePerBlock"));
      }))).then(Commands.literal("buffer").then(Commands.argument("distance", FloatArgumentType.floatArg(0.0F)).executes((p_198905_0_) -> {
         return setDamageBuffer(p_198905_0_.getSource(), FloatArgumentType.getFloat(p_198905_0_, "distance"));
      })))).then(Commands.literal("get").executes((p_198900_0_) -> {
         return getSize(p_198900_0_.getSource());
      })).then(Commands.literal("warning").then(Commands.literal("distance").then(Commands.argument("distance", IntegerArgumentType.integer(0)).executes((p_198892_0_) -> {
         return setWarningDistance(p_198892_0_.getSource(), IntegerArgumentType.getInteger(p_198892_0_, "distance"));
      }))).then(Commands.literal("time").then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((p_198907_0_) -> {
         return setWarningTime(p_198907_0_.getSource(), IntegerArgumentType.getInteger(p_198907_0_, "time"));
      })))));
   }

   private static int setDamageBuffer(CommandSource p_198898_0_, float p_198898_1_) throws CommandSyntaxException {
      WorldBorder worldborder = p_198898_0_.getWorld().getWorldBorder();
      if (worldborder.getDamageBuffer() == (double)p_198898_1_) {
         throw field_198917_g.create();
      } else {
         worldborder.setDamageBuffer((double)p_198898_1_);
         p_198898_0_.sendFeedback(new TextComponentTranslation("commands.worldborder.damage.buffer.success", String.format(Locale.ROOT, "%.2f", p_198898_1_)), true);
         return (int)p_198898_1_;
      }
   }

   private static int setDamageAmount(CommandSource p_198904_0_, float p_198904_1_) throws CommandSyntaxException {
      WorldBorder worldborder = p_198904_0_.getWorld().getWorldBorder();
      if (worldborder.getDamageAmount() == (double)p_198904_1_) {
         throw field_198918_h.create();
      } else {
         worldborder.setDamageAmount((double)p_198904_1_);
         p_198904_0_.sendFeedback(new TextComponentTranslation("commands.worldborder.damage.amount.success", String.format(Locale.ROOT, "%.2f", p_198904_1_)), true);
         return (int)p_198904_1_;
      }
   }

   private static int setWarningTime(CommandSource p_198902_0_, int p_198902_1_) throws CommandSyntaxException {
      WorldBorder worldborder = p_198902_0_.getWorld().getWorldBorder();
      if (worldborder.getWarningTime() == p_198902_1_) {
         throw field_198915_e.create();
      } else {
         worldborder.setWarningTime(p_198902_1_);
         p_198902_0_.sendFeedback(new TextComponentTranslation("commands.worldborder.warning.time.success", p_198902_1_), true);
         return p_198902_1_;
      }
   }

   private static int setWarningDistance(CommandSource p_198899_0_, int p_198899_1_) throws CommandSyntaxException {
      WorldBorder worldborder = p_198899_0_.getWorld().getWorldBorder();
      if (worldborder.getWarningDistance() == p_198899_1_) {
         throw field_198916_f.create();
      } else {
         worldborder.setWarningDistance(p_198899_1_);
         p_198899_0_.sendFeedback(new TextComponentTranslation("commands.worldborder.warning.distance.success", p_198899_1_), true);
         return p_198899_1_;
      }
   }

   private static int getSize(CommandSource p_198910_0_) {
      double d0 = p_198910_0_.getWorld().getWorldBorder().getDiameter();
      p_198910_0_.sendFeedback(new TextComponentTranslation("commands.worldborder.get", String.format(Locale.ROOT, "%.0f", d0)), false);
      return MathHelper.floor(d0 + 0.5D);
   }

   private static int setCenter(CommandSource p_198896_0_, Vec2f p_198896_1_) throws CommandSyntaxException {
      WorldBorder worldborder = p_198896_0_.getWorld().getWorldBorder();
      if (worldborder.getCenterX() == (double)p_198896_1_.x && worldborder.getCenterZ() == (double)p_198896_1_.y) {
         throw field_198911_a.create();
      } else {
         worldborder.setCenter((double)p_198896_1_.x, (double)p_198896_1_.y);
         p_198896_0_.sendFeedback(new TextComponentTranslation("commands.worldborder.center.success", String.format(Locale.ROOT, "%.2f", p_198896_1_.x), String.format("%.2f", p_198896_1_.y)), true);
         return 0;
      }
   }

   private static int setSize(CommandSource p_198895_0_, double p_198895_1_, long p_198895_3_) throws CommandSyntaxException {
      WorldBorder worldborder = p_198895_0_.getWorld().getWorldBorder();
      double d0 = worldborder.getDiameter();
      if (d0 == p_198895_1_) {
         throw field_198912_b.create();
      } else if (p_198895_1_ < 1.0D) {
         throw field_198913_c.create();
      } else if (p_198895_1_ > 6.0E7D) {
         throw field_198914_d.create();
      } else {
         if (p_198895_3_ > 0L) {
            worldborder.setTransition(d0, p_198895_1_, p_198895_3_);
            if (p_198895_1_ > d0) {
               p_198895_0_.sendFeedback(new TextComponentTranslation("commands.worldborder.set.grow", String.format(Locale.ROOT, "%.1f", p_198895_1_), Long.toString(p_198895_3_ / 1000L)), true);
            } else {
               p_198895_0_.sendFeedback(new TextComponentTranslation("commands.worldborder.set.shrink", String.format(Locale.ROOT, "%.1f", p_198895_1_), Long.toString(p_198895_3_ / 1000L)), true);
            }
         } else {
            worldborder.setTransition(p_198895_1_);
            p_198895_0_.sendFeedback(new TextComponentTranslation("commands.worldborder.set.immediate", String.format(Locale.ROOT, "%.1f", p_198895_1_)), true);
         }

         return (int)(p_198895_1_ - d0);
      }
   }
}