package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketStopSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;

public class StopSoundCommand {
   public static void register(CommandDispatcher<CommandSource> p_198730_0_) {
      RequiredArgumentBuilder<CommandSource, EntitySelector> requiredargumentbuilder = Commands.argument("targets", EntityArgument.multiplePlayers()).executes((p_198729_0_) -> {
         return func_198733_a(p_198729_0_.getSource(), EntityArgument.getPlayers(p_198729_0_, "targets"), (SoundCategory)null, (ResourceLocation)null);
      }).then(Commands.literal("*").then(Commands.argument("sound", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes((p_198732_0_) -> {
         return func_198733_a(p_198732_0_.getSource(), EntityArgument.getPlayers(p_198732_0_, "targets"), (SoundCategory)null, ResourceLocationArgument.getResourceLocation(p_198732_0_, "sound"));
      })));

      for(SoundCategory soundcategory : SoundCategory.values()) {
         requiredargumentbuilder.then(Commands.literal(soundcategory.getName()).executes((p_198731_1_) -> {
            return func_198733_a(p_198731_1_.getSource(), EntityArgument.getPlayers(p_198731_1_, "targets"), soundcategory, (ResourceLocation)null);
         }).then(Commands.argument("sound", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes((p_198728_1_) -> {
            return func_198733_a(p_198728_1_.getSource(), EntityArgument.getPlayers(p_198728_1_, "targets"), soundcategory, ResourceLocationArgument.getResourceLocation(p_198728_1_, "sound"));
         })));
      }

      p_198730_0_.register(Commands.literal("stopsound").requires((p_198734_0_) -> {
         return p_198734_0_.hasPermissionLevel(2);
      }).then(requiredargumentbuilder));
   }

   private static int func_198733_a(CommandSource p_198733_0_, Collection<EntityPlayerMP> p_198733_1_, @Nullable SoundCategory p_198733_2_, @Nullable ResourceLocation p_198733_3_) {
      SPacketStopSound spacketstopsound = new SPacketStopSound(p_198733_3_, p_198733_2_);

      for(EntityPlayerMP entityplayermp : p_198733_1_) {
         entityplayermp.connection.sendPacket(spacketstopsound);
      }

      if (p_198733_2_ != null) {
         if (p_198733_3_ != null) {
            p_198733_0_.sendFeedback(new TextComponentTranslation("commands.stopsound.success.source.sound", p_198733_3_, p_198733_2_.getName()), true);
         } else {
            p_198733_0_.sendFeedback(new TextComponentTranslation("commands.stopsound.success.source.any", p_198733_2_.getName()), true);
         }
      } else if (p_198733_3_ != null) {
         p_198733_0_.sendFeedback(new TextComponentTranslation("commands.stopsound.success.sourceless.sound", p_198733_3_), true);
      } else {
         p_198733_0_.sendFeedback(new TextComponentTranslation("commands.stopsound.success.sourceless.any"), true);
      }

      return p_198733_1_.size();
   }
}