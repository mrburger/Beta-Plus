package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.PotionArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;

public class EffectCommand {
   private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.effect.give.failed"));
   private static final SimpleCommandExceptionType CLEAR_EVERYTHING_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.effect.clear.everything.failed"));
   private static final SimpleCommandExceptionType CLEAR_SPECIFIC_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.effect.clear.specific.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("effect").requires((p_198359_0_) -> {
         return p_198359_0_.hasPermissionLevel(2);
      }).then(Commands.literal("clear").then(Commands.argument("targets", EntityArgument.multipleEntities()).executes((p_198352_0_) -> {
         return clearAllEffects(p_198352_0_.getSource(), EntityArgument.getEntities(p_198352_0_, "targets"));
      }).then(Commands.argument("effect", PotionArgument.mobEffect()).executes((p_198356_0_) -> {
         return clearEffect(p_198356_0_.getSource(), EntityArgument.getEntities(p_198356_0_, "targets"), PotionArgument.getMobEffect(p_198356_0_, "effect"));
      })))).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.multipleEntities()).then(Commands.argument("effect", PotionArgument.mobEffect()).executes((p_198351_0_) -> {
         return addEffect(p_198351_0_.getSource(), EntityArgument.getEntities(p_198351_0_, "targets"), PotionArgument.getMobEffect(p_198351_0_, "effect"), (Integer)null, 0, true);
      }).then(Commands.argument("seconds", IntegerArgumentType.integer(1, 1000000)).executes((p_198357_0_) -> {
         return addEffect(p_198357_0_.getSource(), EntityArgument.getEntities(p_198357_0_, "targets"), PotionArgument.getMobEffect(p_198357_0_, "effect"), IntegerArgumentType.getInteger(p_198357_0_, "seconds"), 0, true);
      }).then(Commands.argument("amplifier", IntegerArgumentType.integer(0, 255)).executes((p_198350_0_) -> {
         return addEffect(p_198350_0_.getSource(), EntityArgument.getEntities(p_198350_0_, "targets"), PotionArgument.getMobEffect(p_198350_0_, "effect"), IntegerArgumentType.getInteger(p_198350_0_, "seconds"), IntegerArgumentType.getInteger(p_198350_0_, "amplifier"), true);
      }).then(Commands.argument("hideParticles", BoolArgumentType.bool()).executes((p_198358_0_) -> {
         return addEffect(p_198358_0_.getSource(), EntityArgument.getEntities(p_198358_0_, "targets"), PotionArgument.getMobEffect(p_198358_0_, "effect"), IntegerArgumentType.getInteger(p_198358_0_, "seconds"), IntegerArgumentType.getInteger(p_198358_0_, "amplifier"), !BoolArgumentType.getBool(p_198358_0_, "hideParticles"));
      }))))))));
   }

   private static int addEffect(CommandSource source, Collection<? extends Entity> targets, Potion effect, @Nullable Integer seconds, int amplifier, boolean showParticles) throws CommandSyntaxException {
      int i = 0;
      int j;
      if (seconds != null) {
         if (effect.isInstant()) {
            j = seconds;
         } else {
            j = seconds * 20;
         }
      } else if (effect.isInstant()) {
         j = 1;
      } else {
         j = 600;
      }

      for(Entity entity : targets) {
         if (entity instanceof EntityLivingBase) {
            PotionEffect potioneffect = new PotionEffect(effect, j, amplifier, false, showParticles);
            if (((EntityLivingBase)entity).addPotionEffect(potioneffect)) {
               ++i;
            }
         }
      }

      if (i == 0) {
         throw GIVE_FAILED_EXCEPTION.create();
      } else {
         if (targets.size() == 1) {
            source.sendFeedback(new TextComponentTranslation("commands.effect.give.success.single", effect.getDisplayName(), targets.iterator().next().getDisplayName(), j / 20), true);
         } else {
            source.sendFeedback(new TextComponentTranslation("commands.effect.give.success.multiple", effect.getDisplayName(), targets.size(), j / 20), true);
         }

         return i;
      }
   }

   private static int clearAllEffects(CommandSource source, Collection<? extends Entity> targets) throws CommandSyntaxException {
      int i = 0;

      for(Entity entity : targets) {
         if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).func_195061_cb()) {
            ++i;
         }
      }

      if (i == 0) {
         throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
      } else {
         if (targets.size() == 1) {
            source.sendFeedback(new TextComponentTranslation("commands.effect.clear.everything.success.single", targets.iterator().next().getDisplayName()), true);
         } else {
            source.sendFeedback(new TextComponentTranslation("commands.effect.clear.everything.success.multiple", targets.size()), true);
         }

         return i;
      }
   }

   private static int clearEffect(CommandSource source, Collection<? extends Entity> targets, Potion effect) throws CommandSyntaxException {
      int i = 0;

      for(Entity entity : targets) {
         if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).removePotionEffect(effect)) {
            ++i;
         }
      }

      if (i == 0) {
         throw CLEAR_SPECIFIC_FAILED_EXCEPTION.create();
      } else {
         if (targets.size() == 1) {
            source.sendFeedback(new TextComponentTranslation("commands.effect.clear.specific.success.single", effect.getDisplayName(), targets.iterator().next().getDisplayName()), true);
         } else {
            source.sendFeedback(new TextComponentTranslation("commands.effect.clear.specific.success.multiple", effect.getDisplayName(), targets.size()), true);
         }

         return i;
      }
   }
}