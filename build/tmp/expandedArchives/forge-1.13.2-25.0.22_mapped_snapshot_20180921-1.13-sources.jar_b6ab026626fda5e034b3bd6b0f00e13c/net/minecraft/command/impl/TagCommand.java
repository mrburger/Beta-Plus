package net.minecraft.command.impl;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Set;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;

public class TagCommand {
   private static final SimpleCommandExceptionType field_198752_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.tag.add.failed"));
   private static final SimpleCommandExceptionType field_198753_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.tag.remove.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198743_0_) {
      p_198743_0_.register(Commands.literal("tag").requires((p_198751_0_) -> {
         return p_198751_0_.hasPermissionLevel(2);
      }).then(Commands.argument("targets", EntityArgument.multipleEntities()).then(Commands.literal("add").then(Commands.argument("name", StringArgumentType.word()).executes((p_198746_0_) -> {
         return func_198749_a(p_198746_0_.getSource(), EntityArgument.getEntities(p_198746_0_, "targets"), StringArgumentType.getString(p_198746_0_, "name"));
      }))).then(Commands.literal("remove").then(Commands.argument("name", StringArgumentType.word()).suggests((p_198745_0_, p_198745_1_) -> {
         return ISuggestionProvider.suggest(func_198748_a(EntityArgument.getEntities(p_198745_0_, "targets")), p_198745_1_);
      }).executes((p_198742_0_) -> {
         return func_198750_b(p_198742_0_.getSource(), EntityArgument.getEntities(p_198742_0_, "targets"), StringArgumentType.getString(p_198742_0_, "name"));
      }))).then(Commands.literal("list").executes((p_198747_0_) -> {
         return func_198744_a(p_198747_0_.getSource(), EntityArgument.getEntities(p_198747_0_, "targets"));
      }))));
   }

   private static Collection<String> func_198748_a(Collection<? extends Entity> p_198748_0_) {
      Set<String> set = Sets.newHashSet();

      for(Entity entity : p_198748_0_) {
         set.addAll(entity.getTags());
      }

      return set;
   }

   private static int func_198749_a(CommandSource p_198749_0_, Collection<? extends Entity> p_198749_1_, String p_198749_2_) throws CommandSyntaxException {
      int i = 0;

      for(Entity entity : p_198749_1_) {
         if (entity.addTag(p_198749_2_)) {
            ++i;
         }
      }

      if (i == 0) {
         throw field_198752_a.create();
      } else {
         if (p_198749_1_.size() == 1) {
            p_198749_0_.sendFeedback(new TextComponentTranslation("commands.tag.add.success.single", p_198749_2_, p_198749_1_.iterator().next().getDisplayName()), true);
         } else {
            p_198749_0_.sendFeedback(new TextComponentTranslation("commands.tag.add.success.multiple", p_198749_2_, p_198749_1_.size()), true);
         }

         return i;
      }
   }

   private static int func_198750_b(CommandSource p_198750_0_, Collection<? extends Entity> p_198750_1_, String p_198750_2_) throws CommandSyntaxException {
      int i = 0;

      for(Entity entity : p_198750_1_) {
         if (entity.removeTag(p_198750_2_)) {
            ++i;
         }
      }

      if (i == 0) {
         throw field_198753_b.create();
      } else {
         if (p_198750_1_.size() == 1) {
            p_198750_0_.sendFeedback(new TextComponentTranslation("commands.tag.remove.success.single", p_198750_2_, p_198750_1_.iterator().next().getDisplayName()), true);
         } else {
            p_198750_0_.sendFeedback(new TextComponentTranslation("commands.tag.remove.success.multiple", p_198750_2_, p_198750_1_.size()), true);
         }

         return i;
      }
   }

   private static int func_198744_a(CommandSource p_198744_0_, Collection<? extends Entity> p_198744_1_) {
      Set<String> set = Sets.newHashSet();

      for(Entity entity : p_198744_1_) {
         set.addAll(entity.getTags());
      }

      if (p_198744_1_.size() == 1) {
         Entity entity1 = p_198744_1_.iterator().next();
         if (set.isEmpty()) {
            p_198744_0_.sendFeedback(new TextComponentTranslation("commands.tag.list.single.empty", entity1.getDisplayName()), false);
         } else {
            p_198744_0_.sendFeedback(new TextComponentTranslation("commands.tag.list.single.success", entity1.getDisplayName(), set.size(), TextComponentUtils.makeGreenSortedList(set)), false);
         }
      } else if (set.isEmpty()) {
         p_198744_0_.sendFeedback(new TextComponentTranslation("commands.tag.list.multiple.empty", p_198744_1_.size()), false);
      } else {
         p_198744_0_.sendFeedback(new TextComponentTranslation("commands.tag.list.multiple.success", p_198744_1_.size(), set.size(), TextComponentUtils.makeGreenSortedList(set)), false);
      }

      return set.size();
   }
}