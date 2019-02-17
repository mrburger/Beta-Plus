package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ColorArgument;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.ScoreHolderArgument;
import net.minecraft.command.arguments.TeamArgument;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;

public class TeamCommand {
   private static final SimpleCommandExceptionType field_198793_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.add.duplicate"));
   private static final DynamicCommandExceptionType field_198794_b = new DynamicCommandExceptionType((p_208916_0_) -> {
      return new TextComponentTranslation("commands.team.add.longName", p_208916_0_);
   });
   private static final SimpleCommandExceptionType field_198796_d = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.empty.unchanged"));
   private static final SimpleCommandExceptionType field_211921_d = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.name.unchanged"));
   private static final SimpleCommandExceptionType field_198797_e = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.color.unchanged"));
   private static final SimpleCommandExceptionType field_198798_f = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.friendlyfire.alreadyEnabled"));
   private static final SimpleCommandExceptionType field_198799_g = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.friendlyfire.alreadyDisabled"));
   private static final SimpleCommandExceptionType field_198800_h = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.seeFriendlyInvisibles.alreadyEnabled"));
   private static final SimpleCommandExceptionType field_198801_i = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.seeFriendlyInvisibles.alreadyDisabled"));
   private static final SimpleCommandExceptionType field_198802_j = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.nametagVisibility.unchanged"));
   private static final SimpleCommandExceptionType field_198803_k = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.deathMessageVisibility.unchanged"));
   private static final SimpleCommandExceptionType field_198804_l = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.collisionRule.unchanged"));

   public static void register(CommandDispatcher<CommandSource> p_198771_0_) {
      p_198771_0_.register(Commands.literal("team").requires((p_198780_0_) -> {
         return p_198780_0_.hasPermissionLevel(2);
      }).then(Commands.literal("list").executes((p_198760_0_) -> {
         return func_198792_a(p_198760_0_.getSource());
      }).then(Commands.argument("team", TeamArgument.team()).executes((p_198763_0_) -> {
         return func_198782_c(p_198763_0_.getSource(), TeamArgument.getTeam(p_198763_0_, "team"));
      }))).then(Commands.literal("add").then(Commands.argument("team", StringArgumentType.word()).executes((p_198767_0_) -> {
         return func_211916_a(p_198767_0_.getSource(), StringArgumentType.getString(p_198767_0_, "team"));
      }).then(Commands.argument("displayName", ComponentArgument.component()).executes((p_198779_0_) -> {
         return func_211917_a(p_198779_0_.getSource(), StringArgumentType.getString(p_198779_0_, "team"), ComponentArgument.getComponent(p_198779_0_, "displayName"));
      })))).then(Commands.literal("remove").then(Commands.argument("team", TeamArgument.team()).executes((p_198773_0_) -> {
         return func_198784_b(p_198773_0_.getSource(), TeamArgument.getTeam(p_198773_0_, "team"));
      }))).then(Commands.literal("empty").then(Commands.argument("team", TeamArgument.team()).executes((p_198785_0_) -> {
         return func_198788_a(p_198785_0_.getSource(), TeamArgument.getTeam(p_198785_0_, "team"));
      }))).then(Commands.literal("join").then(Commands.argument("team", TeamArgument.team()).executes((p_198758_0_) -> {
         return func_198768_a(p_198758_0_.getSource(), TeamArgument.getTeam(p_198758_0_, "team"), Collections.singleton(p_198758_0_.getSource().assertIsEntity().getScoreboardName()));
      }).then(Commands.argument("members", ScoreHolderArgument.multipleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).executes((p_198755_0_) -> {
         return func_198768_a(p_198755_0_.getSource(), TeamArgument.getTeam(p_198755_0_, "team"), ScoreHolderArgument.getScoreHolder(p_198755_0_, "members"));
      })))).then(Commands.literal("leave").then(Commands.argument("members", ScoreHolderArgument.multipleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).executes((p_198765_0_) -> {
         return func_198786_a(p_198765_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198765_0_, "members"));
      }))).then(Commands.literal("modify").then(Commands.argument("team", TeamArgument.team()).then(Commands.literal("displayName").then(Commands.argument("displayName", ComponentArgument.component()).executes((p_211919_0_) -> {
         return func_211920_a(p_211919_0_.getSource(), TeamArgument.getTeam(p_211919_0_, "team"), ComponentArgument.getComponent(p_211919_0_, "displayName"));
      }))).then(Commands.literal("color").then(Commands.argument("value", ColorArgument.color()).executes((p_198762_0_) -> {
         return setColor(p_198762_0_.getSource(), TeamArgument.getTeam(p_198762_0_, "team"), ColorArgument.getColor(p_198762_0_, "value"));
      }))).then(Commands.literal("friendlyFire").then(Commands.argument("allowed", BoolArgumentType.bool()).executes((p_198775_0_) -> {
         return func_198781_b(p_198775_0_.getSource(), TeamArgument.getTeam(p_198775_0_, "team"), BoolArgumentType.getBool(p_198775_0_, "allowed"));
      }))).then(Commands.literal("seeFriendlyInvisibles").then(Commands.argument("allowed", BoolArgumentType.bool()).executes((p_198770_0_) -> {
         return func_198783_a(p_198770_0_.getSource(), TeamArgument.getTeam(p_198770_0_, "team"), BoolArgumentType.getBool(p_198770_0_, "allowed"));
      }))).then(Commands.literal("nametagVisibility").then(Commands.literal("never").executes((p_198778_0_) -> {
         return setNameTagVisibility(p_198778_0_.getSource(), TeamArgument.getTeam(p_198778_0_, "team"), Team.EnumVisible.NEVER);
      })).then(Commands.literal("hideForOtherTeams").executes((p_198764_0_) -> {
         return setNameTagVisibility(p_198764_0_.getSource(), TeamArgument.getTeam(p_198764_0_, "team"), Team.EnumVisible.HIDE_FOR_OTHER_TEAMS);
      })).then(Commands.literal("hideForOwnTeam").executes((p_198766_0_) -> {
         return setNameTagVisibility(p_198766_0_.getSource(), TeamArgument.getTeam(p_198766_0_, "team"), Team.EnumVisible.HIDE_FOR_OWN_TEAM);
      })).then(Commands.literal("always").executes((p_198759_0_) -> {
         return setNameTagVisibility(p_198759_0_.getSource(), TeamArgument.getTeam(p_198759_0_, "team"), Team.EnumVisible.ALWAYS);
      }))).then(Commands.literal("deathMessageVisibility").then(Commands.literal("never").executes((p_198789_0_) -> {
         return setDeathMessageVisibility(p_198789_0_.getSource(), TeamArgument.getTeam(p_198789_0_, "team"), Team.EnumVisible.NEVER);
      })).then(Commands.literal("hideForOtherTeams").executes((p_198791_0_) -> {
         return setDeathMessageVisibility(p_198791_0_.getSource(), TeamArgument.getTeam(p_198791_0_, "team"), Team.EnumVisible.HIDE_FOR_OTHER_TEAMS);
      })).then(Commands.literal("hideForOwnTeam").executes((p_198769_0_) -> {
         return setDeathMessageVisibility(p_198769_0_.getSource(), TeamArgument.getTeam(p_198769_0_, "team"), Team.EnumVisible.HIDE_FOR_OWN_TEAM);
      })).then(Commands.literal("always").executes((p_198774_0_) -> {
         return setDeathMessageVisibility(p_198774_0_.getSource(), TeamArgument.getTeam(p_198774_0_, "team"), Team.EnumVisible.ALWAYS);
      }))).then(Commands.literal("collisionRule").then(Commands.literal("never").executes((p_198761_0_) -> {
         return setCollisionRule(p_198761_0_.getSource(), TeamArgument.getTeam(p_198761_0_, "team"), Team.CollisionRule.NEVER);
      })).then(Commands.literal("pushOwnTeam").executes((p_198756_0_) -> {
         return setCollisionRule(p_198756_0_.getSource(), TeamArgument.getTeam(p_198756_0_, "team"), Team.CollisionRule.PUSH_OWN_TEAM);
      })).then(Commands.literal("pushOtherTeams").executes((p_198754_0_) -> {
         return setCollisionRule(p_198754_0_.getSource(), TeamArgument.getTeam(p_198754_0_, "team"), Team.CollisionRule.PUSH_OTHER_TEAMS);
      })).then(Commands.literal("always").executes((p_198790_0_) -> {
         return setCollisionRule(p_198790_0_.getSource(), TeamArgument.getTeam(p_198790_0_, "team"), Team.CollisionRule.ALWAYS);
      }))).then(Commands.literal("prefix").then(Commands.argument("prefix", ComponentArgument.component()).executes((p_207514_0_) -> {
         return setPrefix(p_207514_0_.getSource(), TeamArgument.getTeam(p_207514_0_, "team"), ComponentArgument.getComponent(p_207514_0_, "prefix"));
      }))).then(Commands.literal("suffix").then(Commands.argument("suffix", ComponentArgument.component()).executes((p_207516_0_) -> {
         return setSuffix(p_207516_0_.getSource(), TeamArgument.getTeam(p_207516_0_, "team"), ComponentArgument.getComponent(p_207516_0_, "suffix"));
      }))))));
   }

   private static int func_198786_a(CommandSource p_198786_0_, Collection<String> p_198786_1_) {
      Scoreboard scoreboard = p_198786_0_.getServer().getWorldScoreboard();

      for(String s : p_198786_1_) {
         scoreboard.removePlayerFromTeams(s);
      }

      if (p_198786_1_.size() == 1) {
         p_198786_0_.sendFeedback(new TextComponentTranslation("commands.team.leave.success.single", p_198786_1_.iterator().next()), true);
      } else {
         p_198786_0_.sendFeedback(new TextComponentTranslation("commands.team.leave.success.multiple", p_198786_1_.size()), true);
      }

      return p_198786_1_.size();
   }

   private static int func_198768_a(CommandSource p_198768_0_, ScorePlayerTeam p_198768_1_, Collection<String> p_198768_2_) {
      Scoreboard scoreboard = p_198768_0_.getServer().getWorldScoreboard();

      for(String s : p_198768_2_) {
         scoreboard.addPlayerToTeam(s, p_198768_1_);
      }

      if (p_198768_2_.size() == 1) {
         p_198768_0_.sendFeedback(new TextComponentTranslation("commands.team.join.success.single", p_198768_2_.iterator().next(), p_198768_1_.getCommandName()), true);
      } else {
         p_198768_0_.sendFeedback(new TextComponentTranslation("commands.team.join.success.multiple", p_198768_2_.size(), p_198768_1_.getCommandName()), true);
      }

      return p_198768_2_.size();
   }

   private static int setNameTagVisibility(CommandSource p_198777_0_, ScorePlayerTeam p_198777_1_, Team.EnumVisible p_198777_2_) throws CommandSyntaxException {
      if (p_198777_1_.getNameTagVisibility() == p_198777_2_) {
         throw field_198802_j.create();
      } else {
         p_198777_1_.setNameTagVisibility(p_198777_2_);
         p_198777_0_.sendFeedback(new TextComponentTranslation("commands.team.option.nametagVisibility.success", p_198777_1_.getCommandName(), p_198777_2_.func_197910_b()), true);
         return 0;
      }
   }

   private static int setDeathMessageVisibility(CommandSource p_198776_0_, ScorePlayerTeam p_198776_1_, Team.EnumVisible p_198776_2_) throws CommandSyntaxException {
      if (p_198776_1_.getDeathMessageVisibility() == p_198776_2_) {
         throw field_198803_k.create();
      } else {
         p_198776_1_.setDeathMessageVisibility(p_198776_2_);
         p_198776_0_.sendFeedback(new TextComponentTranslation("commands.team.option.deathMessageVisibility.success", p_198776_1_.getCommandName(), p_198776_2_.func_197910_b()), true);
         return 0;
      }
   }

   private static int setCollisionRule(CommandSource p_198787_0_, ScorePlayerTeam p_198787_1_, Team.CollisionRule p_198787_2_) throws CommandSyntaxException {
      if (p_198787_1_.getCollisionRule() == p_198787_2_) {
         throw field_198804_l.create();
      } else {
         p_198787_1_.setCollisionRule(p_198787_2_);
         p_198787_0_.sendFeedback(new TextComponentTranslation("commands.team.option.collisionRule.success", p_198787_1_.getCommandName(), p_198787_2_.func_197907_b()), true);
         return 0;
      }
   }

   private static int func_198783_a(CommandSource p_198783_0_, ScorePlayerTeam p_198783_1_, boolean p_198783_2_) throws CommandSyntaxException {
      if (p_198783_1_.getSeeFriendlyInvisiblesEnabled() == p_198783_2_) {
         if (p_198783_2_) {
            throw field_198800_h.create();
         } else {
            throw field_198801_i.create();
         }
      } else {
         p_198783_1_.setSeeFriendlyInvisiblesEnabled(p_198783_2_);
         p_198783_0_.sendFeedback(new TextComponentTranslation("commands.team.option.seeFriendlyInvisibles." + (p_198783_2_ ? "enabled" : "disabled"), p_198783_1_.getCommandName()), true);
         return 0;
      }
   }

   private static int func_198781_b(CommandSource p_198781_0_, ScorePlayerTeam p_198781_1_, boolean p_198781_2_) throws CommandSyntaxException {
      if (p_198781_1_.getAllowFriendlyFire() == p_198781_2_) {
         if (p_198781_2_) {
            throw field_198798_f.create();
         } else {
            throw field_198799_g.create();
         }
      } else {
         p_198781_1_.setAllowFriendlyFire(p_198781_2_);
         p_198781_0_.sendFeedback(new TextComponentTranslation("commands.team.option.friendlyfire." + (p_198781_2_ ? "enabled" : "disabled"), p_198781_1_.getCommandName()), true);
         return 0;
      }
   }

   private static int func_211920_a(CommandSource p_211920_0_, ScorePlayerTeam p_211920_1_, ITextComponent p_211920_2_) throws CommandSyntaxException {
      if (p_211920_1_.getDisplayName().equals(p_211920_2_)) {
         throw field_211921_d.create();
      } else {
         p_211920_1_.setDisplayName(p_211920_2_);
         p_211920_0_.sendFeedback(new TextComponentTranslation("commands.team.option.name.success", p_211920_1_.getCommandName()), true);
         return 0;
      }
   }

   private static int setColor(CommandSource p_198757_0_, ScorePlayerTeam p_198757_1_, TextFormatting p_198757_2_) throws CommandSyntaxException {
      if (p_198757_1_.getColor() == p_198757_2_) {
         throw field_198797_e.create();
      } else {
         p_198757_1_.setColor(p_198757_2_);
         p_198757_0_.sendFeedback(new TextComponentTranslation("commands.team.option.color.success", p_198757_1_.getCommandName(), p_198757_2_.getFriendlyName()), true);
         return 0;
      }
   }

   private static int func_198788_a(CommandSource p_198788_0_, ScorePlayerTeam p_198788_1_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_198788_0_.getServer().getWorldScoreboard();
      Collection<String> collection = Lists.newArrayList(p_198788_1_.getMembershipCollection());
      if (collection.isEmpty()) {
         throw field_198796_d.create();
      } else {
         for(String s : collection) {
            scoreboard.removePlayerFromTeam(s, p_198788_1_);
         }

         p_198788_0_.sendFeedback(new TextComponentTranslation("commands.team.empty.success", collection.size(), p_198788_1_.getCommandName()), true);
         return collection.size();
      }
   }

   private static int func_198784_b(CommandSource p_198784_0_, ScorePlayerTeam p_198784_1_) {
      Scoreboard scoreboard = p_198784_0_.getServer().getWorldScoreboard();
      scoreboard.removeTeam(p_198784_1_);
      p_198784_0_.sendFeedback(new TextComponentTranslation("commands.team.remove.success", p_198784_1_.getCommandName()), true);
      return scoreboard.getTeams().size();
   }

   private static int func_211916_a(CommandSource p_211916_0_, String p_211916_1_) throws CommandSyntaxException {
      return func_211917_a(p_211916_0_, p_211916_1_, new TextComponentString(p_211916_1_));
   }

   private static int func_211917_a(CommandSource p_211917_0_, String p_211917_1_, ITextComponent p_211917_2_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_211917_0_.getServer().getWorldScoreboard();
      if (scoreboard.getTeam(p_211917_1_) != null) {
         throw field_198793_a.create();
      } else if (p_211917_1_.length() > 16) {
         throw field_198794_b.create(16);
      } else {
         ScorePlayerTeam scoreplayerteam = scoreboard.createTeam(p_211917_1_);
         scoreplayerteam.setDisplayName(p_211917_2_);
         p_211917_0_.sendFeedback(new TextComponentTranslation("commands.team.add.success", scoreplayerteam.getCommandName()), true);
         return scoreboard.getTeams().size();
      }
   }

   private static int func_198782_c(CommandSource p_198782_0_, ScorePlayerTeam p_198782_1_) {
      Collection<String> collection = p_198782_1_.getMembershipCollection();
      if (collection.isEmpty()) {
         p_198782_0_.sendFeedback(new TextComponentTranslation("commands.team.list.members.empty", p_198782_1_.getCommandName()), false);
      } else {
         p_198782_0_.sendFeedback(new TextComponentTranslation("commands.team.list.members.success", p_198782_1_.getCommandName(), collection.size(), TextComponentUtils.makeGreenSortedList(collection)), false);
      }

      return collection.size();
   }

   private static int func_198792_a(CommandSource p_198792_0_) {
      Collection<ScorePlayerTeam> collection = p_198792_0_.getServer().getWorldScoreboard().getTeams();
      if (collection.isEmpty()) {
         p_198792_0_.sendFeedback(new TextComponentTranslation("commands.team.list.teams.empty"), false);
      } else {
         p_198792_0_.sendFeedback(new TextComponentTranslation("commands.team.list.teams.success", collection.size(), TextComponentUtils.makeList(collection, ScorePlayerTeam::getCommandName)), false);
      }

      return collection.size();
   }

   private static int setPrefix(CommandSource p_207515_0_, ScorePlayerTeam p_207515_1_, ITextComponent p_207515_2_) {
      p_207515_1_.setPrefix(p_207515_2_);
      p_207515_0_.sendFeedback(new TextComponentTranslation("commands.team.option.prefix.success", p_207515_2_), false);
      return 1;
   }

   private static int setSuffix(CommandSource p_207517_0_, ScorePlayerTeam p_207517_1_, ITextComponent p_207517_2_) {
      p_207517_1_.setSuffix(p_207517_2_);
      p_207517_0_.sendFeedback(new TextComponentTranslation("commands.team.option.suffix.success", p_207517_2_), false);
      return 1;
   }
}