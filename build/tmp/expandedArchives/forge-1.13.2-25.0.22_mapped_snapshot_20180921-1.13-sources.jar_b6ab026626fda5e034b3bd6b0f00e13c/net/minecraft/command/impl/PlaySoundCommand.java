package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;

public class PlaySoundCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.playsound.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      RequiredArgumentBuilder<CommandSource, ResourceLocation> requiredargumentbuilder = Commands.argument("sound", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.AVAILABLE_SOUNDS);

      for(SoundCategory soundcategory : SoundCategory.values()) {
         requiredargumentbuilder.then(buildCategorySubcommand(soundcategory));
      }

      dispatcher.register(Commands.literal("playsound").requires((p_198576_0_) -> {
         return p_198576_0_.hasPermissionLevel(2);
      }).then(requiredargumentbuilder));
   }

   private static LiteralArgumentBuilder<CommandSource> buildCategorySubcommand(SoundCategory category) {
      return Commands.literal(category.getName()).then(Commands.argument("targets", EntityArgument.multiplePlayers()).executes((p_198575_1_) -> {
         return playSound(p_198575_1_.getSource(), EntityArgument.getPlayers(p_198575_1_, "targets"), ResourceLocationArgument.getResourceLocation(p_198575_1_, "sound"), category, p_198575_1_.getSource().getPos(), 1.0F, 1.0F, 0.0F);
      }).then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_198578_1_) -> {
         return playSound(p_198578_1_.getSource(), EntityArgument.getPlayers(p_198578_1_, "targets"), ResourceLocationArgument.getResourceLocation(p_198578_1_, "sound"), category, Vec3Argument.getVec3(p_198578_1_, "pos"), 1.0F, 1.0F, 0.0F);
      }).then(Commands.argument("volume", FloatArgumentType.floatArg(0.0F)).executes((p_198571_1_) -> {
         return playSound(p_198571_1_.getSource(), EntityArgument.getPlayers(p_198571_1_, "targets"), ResourceLocationArgument.getResourceLocation(p_198571_1_, "sound"), category, Vec3Argument.getVec3(p_198571_1_, "pos"), p_198571_1_.getArgument("volume", Float.class), 1.0F, 0.0F);
      }).then(Commands.argument("pitch", FloatArgumentType.floatArg(0.0F, 2.0F)).executes((p_198574_1_) -> {
         return playSound(p_198574_1_.getSource(), EntityArgument.getPlayers(p_198574_1_, "targets"), ResourceLocationArgument.getResourceLocation(p_198574_1_, "sound"), category, Vec3Argument.getVec3(p_198574_1_, "pos"), p_198574_1_.getArgument("volume", Float.class), p_198574_1_.getArgument("pitch", Float.class), 0.0F);
      }).then(Commands.argument("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F)).executes((p_198570_1_) -> {
         return playSound(p_198570_1_.getSource(), EntityArgument.getPlayers(p_198570_1_, "targets"), ResourceLocationArgument.getResourceLocation(p_198570_1_, "sound"), category, Vec3Argument.getVec3(p_198570_1_, "pos"), p_198570_1_.getArgument("volume", Float.class), p_198570_1_.getArgument("pitch", Float.class), p_198570_1_.getArgument("minVolume", Float.class));
      }))))));
   }

   private static int playSound(CommandSource source, Collection<EntityPlayerMP> targets, ResourceLocation soundIn, SoundCategory category, Vec3d pos, float volume, float pitch, float minVolume) throws CommandSyntaxException {
      double d0 = Math.pow(volume > 1.0F ? (double)(volume * 16.0F) : 16.0D, 2.0D);
      int i = 0;
      Iterator iterator = targets.iterator();

      while(true) {
         EntityPlayerMP entityplayermp;
         Vec3d vec3d;
         float f;
         while(true) {
            if (!iterator.hasNext()) {
               if (i == 0) {
                  throw FAILED_EXCEPTION.create();
               }

               if (targets.size() == 1) {
                  source.sendFeedback(new TextComponentTranslation("commands.playsound.success.single", soundIn, targets.iterator().next().getDisplayName()), true);
               } else {
                  source.sendFeedback(new TextComponentTranslation("commands.playsound.success.single", soundIn, targets.iterator().next().getDisplayName()), true);
               }

               return i;
            }

            entityplayermp = (EntityPlayerMP)iterator.next();
            double d1 = pos.x - entityplayermp.posX;
            double d2 = pos.y - entityplayermp.posY;
            double d3 = pos.z - entityplayermp.posZ;
            double d4 = d1 * d1 + d2 * d2 + d3 * d3;
            vec3d = pos;
            f = volume;
            if (!(d4 > d0)) {
               break;
            }

            if (!(minVolume <= 0.0F)) {
               double d5 = (double)MathHelper.sqrt(d4);
               vec3d = new Vec3d(entityplayermp.posX + d1 / d5 * 2.0D, entityplayermp.posY + d2 / d5 * 2.0D, entityplayermp.posZ + d3 / d5 * 2.0D);
               f = minVolume;
               break;
            }
         }

         entityplayermp.connection.sendPacket(new SPacketCustomSound(soundIn, category, vec3d, f, pitch));
         ++i;
      }
   }
}