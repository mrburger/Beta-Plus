package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextComponentTranslation;

public class KillCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("kill").requires((p_198521_0_) -> {
         return p_198521_0_.hasPermissionLevel(2);
      }).then(Commands.argument("targets", EntityArgument.multipleEntities()).executes((p_198520_0_) -> {
         return killEntities(p_198520_0_.getSource(), EntityArgument.getEntities(p_198520_0_, "targets"));
      })));
   }

   private static int killEntities(CommandSource source, Collection<? extends Entity> targets) {
      for(Entity entity : targets) {
         entity.onKillCommand();
      }

      if (targets.size() == 1) {
         source.sendFeedback(new TextComponentTranslation("commands.kill.success.single", targets.iterator().next().getDisplayName()), true);
      } else {
         source.sendFeedback(new TextComponentTranslation("commands.kill.success.multiple", targets.size()), true);
      }

      return targets.size();
   }
}