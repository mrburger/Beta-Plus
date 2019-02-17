package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;

public class AdvancementCommand {
   private static final SuggestionProvider<CommandSource> field_198218_a = (p_198206_0_, p_198206_1_) -> {
      Collection<Advancement> collection = p_198206_0_.getSource().getServer().getAdvancementManager().getAllAdvancements();
      return ISuggestionProvider.func_212476_a(collection.stream().map(Advancement::getId), p_198206_1_);
   };

   public static void register(CommandDispatcher<CommandSource> p_198199_0_) {
      p_198199_0_.register(Commands.literal("advancement").requires((p_198205_0_) -> {
         return p_198205_0_.hasPermissionLevel(2);
      }).then(Commands.literal("grant").then(Commands.argument("targets", EntityArgument.multiplePlayers()).then(Commands.literal("only").then(Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(field_198218_a).executes((p_198202_0_) -> {
         return func_198214_a(p_198202_0_.getSource(), EntityArgument.getPlayers(p_198202_0_, "targets"), AdvancementCommand.Action.GRANT, func_198216_a(ResourceLocationArgument.getAdvancement(p_198202_0_, "advancement"), AdvancementCommand.Mode.ONLY));
      }).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((p_198209_0_, p_198209_1_) -> {
         return ISuggestionProvider.suggest(ResourceLocationArgument.getAdvancement(p_198209_0_, "advancement").getCriteria().keySet(), p_198209_1_);
      }).executes((p_198212_0_) -> {
         return func_198203_a(p_198212_0_.getSource(), EntityArgument.getPlayers(p_198212_0_, "targets"), AdvancementCommand.Action.GRANT, ResourceLocationArgument.getAdvancement(p_198212_0_, "advancement"), StringArgumentType.getString(p_198212_0_, "criterion"));
      })))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(field_198218_a).executes((p_198215_0_) -> {
         return func_198214_a(p_198215_0_.getSource(), EntityArgument.getPlayers(p_198215_0_, "targets"), AdvancementCommand.Action.GRANT, func_198216_a(ResourceLocationArgument.getAdvancement(p_198215_0_, "advancement"), AdvancementCommand.Mode.FROM));
      }))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(field_198218_a).executes((p_198204_0_) -> {
         return func_198214_a(p_198204_0_.getSource(), EntityArgument.getPlayers(p_198204_0_, "targets"), AdvancementCommand.Action.GRANT, func_198216_a(ResourceLocationArgument.getAdvancement(p_198204_0_, "advancement"), AdvancementCommand.Mode.UNTIL));
      }))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(field_198218_a).executes((p_198211_0_) -> {
         return func_198214_a(p_198211_0_.getSource(), EntityArgument.getPlayers(p_198211_0_, "targets"), AdvancementCommand.Action.GRANT, func_198216_a(ResourceLocationArgument.getAdvancement(p_198211_0_, "advancement"), AdvancementCommand.Mode.THROUGH));
      }))).then(Commands.literal("everything").executes((p_198217_0_) -> {
         return func_198214_a(p_198217_0_.getSource(), EntityArgument.getPlayers(p_198217_0_, "targets"), AdvancementCommand.Action.GRANT, p_198217_0_.getSource().getServer().getAdvancementManager().getAllAdvancements());
      })))).then(Commands.literal("revoke").then(Commands.argument("targets", EntityArgument.multiplePlayers()).then(Commands.literal("only").then(Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(field_198218_a).executes((p_198198_0_) -> {
         return func_198214_a(p_198198_0_.getSource(), EntityArgument.getPlayers(p_198198_0_, "targets"), AdvancementCommand.Action.REVOKE, func_198216_a(ResourceLocationArgument.getAdvancement(p_198198_0_, "advancement"), AdvancementCommand.Mode.ONLY));
      }).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((p_198210_0_, p_198210_1_) -> {
         return ISuggestionProvider.suggest(ResourceLocationArgument.getAdvancement(p_198210_0_, "advancement").getCriteria().keySet(), p_198210_1_);
      }).executes((p_198200_0_) -> {
         return func_198203_a(p_198200_0_.getSource(), EntityArgument.getPlayers(p_198200_0_, "targets"), AdvancementCommand.Action.REVOKE, ResourceLocationArgument.getAdvancement(p_198200_0_, "advancement"), StringArgumentType.getString(p_198200_0_, "criterion"));
      })))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(field_198218_a).executes((p_198208_0_) -> {
         return func_198214_a(p_198208_0_.getSource(), EntityArgument.getPlayers(p_198208_0_, "targets"), AdvancementCommand.Action.REVOKE, func_198216_a(ResourceLocationArgument.getAdvancement(p_198208_0_, "advancement"), AdvancementCommand.Mode.FROM));
      }))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(field_198218_a).executes((p_198201_0_) -> {
         return func_198214_a(p_198201_0_.getSource(), EntityArgument.getPlayers(p_198201_0_, "targets"), AdvancementCommand.Action.REVOKE, func_198216_a(ResourceLocationArgument.getAdvancement(p_198201_0_, "advancement"), AdvancementCommand.Mode.UNTIL));
      }))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(field_198218_a).executes((p_198197_0_) -> {
         return func_198214_a(p_198197_0_.getSource(), EntityArgument.getPlayers(p_198197_0_, "targets"), AdvancementCommand.Action.REVOKE, func_198216_a(ResourceLocationArgument.getAdvancement(p_198197_0_, "advancement"), AdvancementCommand.Mode.THROUGH));
      }))).then(Commands.literal("everything").executes((p_198213_0_) -> {
         return func_198214_a(p_198213_0_.getSource(), EntityArgument.getPlayers(p_198213_0_, "targets"), AdvancementCommand.Action.REVOKE, p_198213_0_.getSource().getServer().getAdvancementManager().getAllAdvancements());
      })))));
   }

   private static int func_198214_a(CommandSource p_198214_0_, Collection<EntityPlayerMP> p_198214_1_, AdvancementCommand.Action p_198214_2_, Collection<Advancement> p_198214_3_) {
      int i = 0;

      for(EntityPlayerMP entityplayermp : p_198214_1_) {
         i += p_198214_2_.func_198180_a(entityplayermp, p_198214_3_);
      }

      if (i == 0) {
         if (p_198214_3_.size() == 1) {
            if (p_198214_1_.size() == 1) {
               throw new CommandException(new TextComponentTranslation(p_198214_2_.func_198181_a() + ".one.to.one.failure", p_198214_3_.iterator().next().getDisplayText(), p_198214_1_.iterator().next().getDisplayName()));
            } else {
               throw new CommandException(new TextComponentTranslation(p_198214_2_.func_198181_a() + ".one.to.many.failure", p_198214_3_.iterator().next().getDisplayText(), p_198214_1_.size()));
            }
         } else if (p_198214_1_.size() == 1) {
            throw new CommandException(new TextComponentTranslation(p_198214_2_.func_198181_a() + ".many.to.one.failure", p_198214_3_.size(), p_198214_1_.iterator().next().getDisplayName()));
         } else {
            throw new CommandException(new TextComponentTranslation(p_198214_2_.func_198181_a() + ".many.to.many.failure", p_198214_3_.size(), p_198214_1_.size()));
         }
      } else {
         if (p_198214_3_.size() == 1) {
            if (p_198214_1_.size() == 1) {
               p_198214_0_.sendFeedback(new TextComponentTranslation(p_198214_2_.func_198181_a() + ".one.to.one.success", p_198214_3_.iterator().next().getDisplayText(), p_198214_1_.iterator().next().getDisplayName()), true);
            } else {
               p_198214_0_.sendFeedback(new TextComponentTranslation(p_198214_2_.func_198181_a() + ".one.to.many.success", p_198214_3_.iterator().next().getDisplayText(), p_198214_1_.size()), true);
            }
         } else if (p_198214_1_.size() == 1) {
            p_198214_0_.sendFeedback(new TextComponentTranslation(p_198214_2_.func_198181_a() + ".many.to.one.success", p_198214_3_.size(), p_198214_1_.iterator().next().getDisplayName()), true);
         } else {
            p_198214_0_.sendFeedback(new TextComponentTranslation(p_198214_2_.func_198181_a() + ".many.to.many.success", p_198214_3_.size(), p_198214_1_.size()), true);
         }

         return i;
      }
   }

   private static int func_198203_a(CommandSource p_198203_0_, Collection<EntityPlayerMP> p_198203_1_, AdvancementCommand.Action p_198203_2_, Advancement p_198203_3_, String p_198203_4_) {
      int i = 0;
      if (!p_198203_3_.getCriteria().containsKey(p_198203_4_)) {
         throw new CommandException(new TextComponentTranslation("commands.advancement.criterionNotFound", p_198203_3_.getDisplayText(), p_198203_4_));
      } else {
         for(EntityPlayerMP entityplayermp : p_198203_1_) {
            if (p_198203_2_.func_198182_a(entityplayermp, p_198203_3_, p_198203_4_)) {
               ++i;
            }
         }

         if (i == 0) {
            if (p_198203_1_.size() == 1) {
               throw new CommandException(new TextComponentTranslation(p_198203_2_.func_198181_a() + ".criterion.to.one.failure", p_198203_4_, p_198203_3_.getDisplayText(), p_198203_1_.iterator().next().getDisplayName()));
            } else {
               throw new CommandException(new TextComponentTranslation(p_198203_2_.func_198181_a() + ".criterion.to.many.failure", p_198203_4_, p_198203_3_.getDisplayText(), p_198203_1_.size()));
            }
         } else {
            if (p_198203_1_.size() == 1) {
               p_198203_0_.sendFeedback(new TextComponentTranslation(p_198203_2_.func_198181_a() + ".criterion.to.one.success", p_198203_4_, p_198203_3_.getDisplayText(), p_198203_1_.iterator().next().getDisplayName()), true);
            } else {
               p_198203_0_.sendFeedback(new TextComponentTranslation(p_198203_2_.func_198181_a() + ".criterion.to.many.success", p_198203_4_, p_198203_3_.getDisplayText(), p_198203_1_.size()), true);
            }

            return i;
         }
      }
   }

   private static List<Advancement> func_198216_a(Advancement p_198216_0_, AdvancementCommand.Mode p_198216_1_) {
      List<Advancement> list = Lists.newArrayList();
      if (p_198216_1_.field_198194_f) {
         for(Advancement advancement = p_198216_0_.getParent(); advancement != null; advancement = advancement.getParent()) {
            list.add(advancement);
         }
      }

      list.add(p_198216_0_);
      if (p_198216_1_.field_198195_g) {
         func_198207_a(p_198216_0_, list);
      }

      return list;
   }

   private static void func_198207_a(Advancement p_198207_0_, List<Advancement> p_198207_1_) {
      for(Advancement advancement : p_198207_0_.getChildren()) {
         p_198207_1_.add(advancement);
         func_198207_a(advancement, p_198207_1_);
      }

   }

   static enum Action {
      GRANT("grant") {
         protected boolean func_198179_a(EntityPlayerMP p_198179_1_, Advancement p_198179_2_) {
            AdvancementProgress advancementprogress = p_198179_1_.getAdvancements().getProgress(p_198179_2_);
            if (advancementprogress.isDone()) {
               return false;
            } else {
               for(String s : advancementprogress.getRemaningCriteria()) {
                  p_198179_1_.getAdvancements().grantCriterion(p_198179_2_, s);
               }

               return true;
            }
         }

         protected boolean func_198182_a(EntityPlayerMP p_198182_1_, Advancement p_198182_2_, String p_198182_3_) {
            return p_198182_1_.getAdvancements().grantCriterion(p_198182_2_, p_198182_3_);
         }
      },
      REVOKE("revoke") {
         protected boolean func_198179_a(EntityPlayerMP p_198179_1_, Advancement p_198179_2_) {
            AdvancementProgress advancementprogress = p_198179_1_.getAdvancements().getProgress(p_198179_2_);
            if (!advancementprogress.hasProgress()) {
               return false;
            } else {
               for(String s : advancementprogress.getCompletedCriteria()) {
                  p_198179_1_.getAdvancements().revokeCriterion(p_198179_2_, s);
               }

               return true;
            }
         }

         protected boolean func_198182_a(EntityPlayerMP p_198182_1_, Advancement p_198182_2_, String p_198182_3_) {
            return p_198182_1_.getAdvancements().revokeCriterion(p_198182_2_, p_198182_3_);
         }
      };

      private final String field_198186_c;

      private Action(String p_i48092_3_) {
         this.field_198186_c = "commands.advancement." + p_i48092_3_;
      }

      public int func_198180_a(EntityPlayerMP p_198180_1_, Iterable<Advancement> p_198180_2_) {
         int i = 0;

         for(Advancement advancement : p_198180_2_) {
            if (this.func_198179_a(p_198180_1_, advancement)) {
               ++i;
            }
         }

         return i;
      }

      protected abstract boolean func_198179_a(EntityPlayerMP p_198179_1_, Advancement p_198179_2_);

      protected abstract boolean func_198182_a(EntityPlayerMP p_198182_1_, Advancement p_198182_2_, String p_198182_3_);

      protected String func_198181_a() {
         return this.field_198186_c;
      }
   }

   static enum Mode {
      ONLY(false, false),
      THROUGH(true, true),
      FROM(false, true),
      UNTIL(true, false),
      EVERYTHING(true, true);

      private final boolean field_198194_f;
      private final boolean field_198195_g;

      private Mode(boolean p_i48091_3_, boolean p_i48091_4_) {
         this.field_198194_f = p_i48091_3_;
         this.field_198195_g = p_i48091_4_;
      }
   }
}