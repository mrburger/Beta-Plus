package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.command.arguments.ObjectiveArgument;
import net.minecraft.command.arguments.RangeArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.RotationArgument;
import net.minecraft.command.arguments.ScoreHolderArgument;
import net.minecraft.command.arguments.SwizzleArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.command.impl.data.DataCommand;
import net.minecraft.command.impl.data.IDataAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.CustomBossEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class ExecuteCommand {
   private static final Dynamic2CommandExceptionType field_198421_a = new Dynamic2CommandExceptionType((p_208885_0_, p_208885_1_) -> {
      return new TextComponentTranslation("commands.execute.blocks.toobig", p_208885_0_, p_208885_1_);
   });
   private static final SimpleCommandExceptionType field_210456_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.execute.conditional.fail"));
   private static final DynamicCommandExceptionType field_210457_c = new DynamicCommandExceptionType((p_210446_0_) -> {
      return new TextComponentTranslation("commands.execute.conditional.fail_count", p_210446_0_);
   });
   private static final BinaryOperator<ResultConsumer<CommandSource>> field_209957_b = (p_209937_0_, p_209937_1_) -> {
      return (p_209939_2_, p_209939_3_, p_209939_4_) -> {
         p_209937_0_.onCommandComplete(p_209939_2_, p_209939_3_, p_209939_4_);
         p_209937_1_.onCommandComplete(p_209939_2_, p_209939_3_, p_209939_4_);
      };
   };

   public static void register(CommandDispatcher<CommandSource> p_198378_0_) {
      LiteralCommandNode<CommandSource> literalcommandnode = p_198378_0_.register(Commands.literal("execute").requires((p_198409_0_) -> {
         return p_198409_0_.hasPermissionLevel(2);
      }));
      p_198378_0_.register(Commands.literal("execute").requires((p_198387_0_) -> {
         return p_198387_0_.hasPermissionLevel(2);
      }).then(Commands.literal("run").redirect(p_198378_0_.getRoot())).then(func_198394_a(literalcommandnode, Commands.literal("if"), true)).then(func_198394_a(literalcommandnode, Commands.literal("unless"), false)).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.multipleEntities()).fork(literalcommandnode, (p_198385_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getEntitiesAllowingNone(p_198385_0_, "targets")) {
            list.add(p_198385_0_.getSource().withEntity(entity));
         }

         return list;
      }))).then(Commands.literal("at").then(Commands.argument("targets", EntityArgument.multipleEntities()).fork(literalcommandnode, (p_198384_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getEntitiesAllowingNone(p_198384_0_, "targets")) {
            list.add(p_198384_0_.getSource().withWorld((WorldServer)entity.world).withPos(entity.getPositionVector()).withPitchYaw(entity.getPitchYaw()));
         }

         return list;
      }))).then(Commands.literal("store").then(func_198392_a(literalcommandnode, Commands.literal("result"), true)).then(func_198392_a(literalcommandnode, Commands.literal("success"), false))).then(Commands.literal("positioned").then(Commands.argument("pos", Vec3Argument.vec3()).redirect(literalcommandnode, (p_198382_0_) -> {
         return p_198382_0_.getSource().withPos(Vec3Argument.getVec3(p_198382_0_, "pos"));
      })).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.multipleEntities()).fork(literalcommandnode, (p_201092_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getEntitiesAllowingNone(p_201092_0_, "targets")) {
            list.add(p_201092_0_.getSource().withPos(entity.getPositionVector()));
         }

         return list;
      })))).then(Commands.literal("rotated").then(Commands.argument("rot", RotationArgument.rotation()).redirect(literalcommandnode, (p_201100_0_) -> {
         return p_201100_0_.getSource().withPitchYaw(RotationArgument.getRotation(p_201100_0_, "rot").getRotation(p_201100_0_.getSource()));
      })).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.multipleEntities()).fork(literalcommandnode, (p_201087_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getEntitiesAllowingNone(p_201087_0_, "targets")) {
            list.add(p_201087_0_.getSource().withPitchYaw(entity.getPitchYaw()));
         }

         return list;
      })))).then(Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.multipleEntities()).then(Commands.argument("anchor", EntityAnchorArgument.entityAnchor()).fork(literalcommandnode, (p_201083_0_) -> {
         List<CommandSource> list = Lists.newArrayList();
         EntityAnchorArgument.Type entityanchorargument$type = EntityAnchorArgument.getEntityAnchor(p_201083_0_, "anchor");

         for(Entity entity : EntityArgument.getEntitiesAllowingNone(p_201083_0_, "targets")) {
            list.add(p_201083_0_.getSource().withPitchYaw(entity, entityanchorargument$type));
         }

         return list;
      })))).then(Commands.argument("pos", Vec3Argument.vec3()).redirect(literalcommandnode, (p_201090_0_) -> {
         return p_201090_0_.getSource().withPitchYaw(Vec3Argument.getVec3(p_201090_0_, "pos"));
      }))).then(Commands.literal("align").then(Commands.argument("axes", SwizzleArgument.swizzle()).redirect(literalcommandnode, (p_198381_0_) -> {
         return p_198381_0_.getSource().withPos(p_198381_0_.getSource().getPos().align(SwizzleArgument.getSwizzle(p_198381_0_, "axes")));
      }))).then(Commands.literal("anchored").then(Commands.argument("anchor", EntityAnchorArgument.entityAnchor()).redirect(literalcommandnode, (p_201091_0_) -> {
         return p_201091_0_.getSource().withEntityAnchorType(EntityAnchorArgument.getEntityAnchor(p_201091_0_, "anchor"));
      }))).then(Commands.literal("in").then(Commands.argument("dimension", DimensionArgument.func_212595_a()).redirect(literalcommandnode, (p_201089_0_) -> {
         return p_201089_0_.getSource().withWorld(p_201089_0_.getSource().getServer().getWorld(DimensionArgument.func_212592_a(p_201089_0_, "dimension")));
      }))));
   }

   private static ArgumentBuilder<CommandSource, ?> func_198392_a(LiteralCommandNode<CommandSource> p_198392_0_, LiteralArgumentBuilder<CommandSource> p_198392_1_, boolean p_198392_2_) {
      p_198392_1_.then(Commands.literal("score").then(Commands.argument("targets", ScoreHolderArgument.multipleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.argument("objective", ObjectiveArgument.objective()).redirect(p_198392_0_, (p_198412_1_) -> {
         return func_209930_a(p_198412_1_.getSource(), ScoreHolderArgument.getScoreHolder(p_198412_1_, "targets"), ObjectiveArgument.getObjective(p_198412_1_, "objective"), p_198392_2_);
      }))));
      p_198392_1_.then(Commands.literal("bossbar").then(Commands.argument("id", ResourceLocationArgument.resourceLocation()).suggests(BossBarCommand.SUGGESTIONS_PROVIDER).then(Commands.literal("value").redirect(p_198392_0_, (p_201468_1_) -> {
         return func_209952_a(p_201468_1_.getSource(), BossBarCommand.getBossbar(p_201468_1_), true, p_198392_2_);
      })).then(Commands.literal("max").redirect(p_198392_0_, (p_201457_1_) -> {
         return func_209952_a(p_201457_1_.getSource(), BossBarCommand.getBossbar(p_201457_1_), false, p_198392_2_);
      }))));

      for(DataCommand.IDataProvider datacommand$idataprovider : DataCommand.DATA_PROVIDERS) {
         datacommand$idataprovider.func_198920_a(p_198392_1_, (p_198408_3_) -> {
            return p_198408_3_.then(Commands.argument("path", NBTPathArgument.nbtPath()).then(Commands.literal("int").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_201106_2_) -> {
               return func_198397_a(p_201106_2_.getSource(), datacommand$idataprovider.func_198919_a(p_201106_2_), NBTPathArgument.getNBTPath(p_201106_2_, "path"), (p_198379_1_) -> {
                  return new NBTTagInt((int)((double)p_198379_1_ * DoubleArgumentType.getDouble(p_201106_2_, "scale")));
               }, p_198392_2_);
            }))).then(Commands.literal("float").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_198375_2_) -> {
               return func_198397_a(p_198375_2_.getSource(), datacommand$idataprovider.func_198919_a(p_198375_2_), NBTPathArgument.getNBTPath(p_198375_2_, "path"), (p_198410_1_) -> {
                  return new NBTTagFloat((float)((double)p_198410_1_ * DoubleArgumentType.getDouble(p_198375_2_, "scale")));
               }, p_198392_2_);
            }))).then(Commands.literal("short").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_198390_2_) -> {
               return func_198397_a(p_198390_2_.getSource(), datacommand$idataprovider.func_198919_a(p_198390_2_), NBTPathArgument.getNBTPath(p_198390_2_, "path"), (p_198386_1_) -> {
                  return new NBTTagShort((short)((int)((double)p_198386_1_ * DoubleArgumentType.getDouble(p_198390_2_, "scale"))));
               }, p_198392_2_);
            }))).then(Commands.literal("long").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_198420_2_) -> {
               return func_198397_a(p_198420_2_.getSource(), datacommand$idataprovider.func_198919_a(p_198420_2_), NBTPathArgument.getNBTPath(p_198420_2_, "path"), (p_198414_1_) -> {
                  return new NBTTagLong((long)((double)p_198414_1_ * DoubleArgumentType.getDouble(p_198420_2_, "scale")));
               }, p_198392_2_);
            }))).then(Commands.literal("double").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_198367_2_) -> {
               return func_198397_a(p_198367_2_.getSource(), datacommand$idataprovider.func_198919_a(p_198367_2_), NBTPathArgument.getNBTPath(p_198367_2_, "path"), (p_198393_1_) -> {
                  return new NBTTagDouble((double)p_198393_1_ * DoubleArgumentType.getDouble(p_198367_2_, "scale"));
               }, p_198392_2_);
            }))).then(Commands.literal("byte").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_198405_2_) -> {
               return func_198397_a(p_198405_2_.getSource(), datacommand$idataprovider.func_198919_a(p_198405_2_), NBTPathArgument.getNBTPath(p_198405_2_, "path"), (p_198418_1_) -> {
                  return new NBTTagByte((byte)((int)((double)p_198418_1_ * DoubleArgumentType.getDouble(p_198405_2_, "scale"))));
               }, p_198392_2_);
            }))));
         });
      }

      return p_198392_1_;
   }

   private static CommandSource func_209930_a(CommandSource p_209930_0_, Collection<String> p_209930_1_, ScoreObjective p_209930_2_, boolean p_209930_3_) {
      Scoreboard scoreboard = p_209930_0_.getServer().getWorldScoreboard();
      return p_209930_0_.withResultConsumer((p_198404_4_, p_198404_5_, p_198404_6_) -> {
         for(String s : p_209930_1_) {
            Score score = scoreboard.getOrCreateScore(s, p_209930_2_);
            int i = p_209930_3_ ? p_198404_6_ : (p_198404_5_ ? 1 : 0);
            score.setScorePoints(i);
         }

      }, field_209957_b);
   }

   private static CommandSource func_209952_a(CommandSource p_209952_0_, CustomBossEvent p_209952_1_, boolean p_209952_2_, boolean p_209952_3_) {
      return p_209952_0_.withResultConsumer((p_201459_3_, p_201459_4_, p_201459_5_) -> {
         int i = p_209952_3_ ? p_201459_5_ : (p_201459_4_ ? 1 : 0);
         if (p_209952_2_) {
            p_209952_1_.setValue(i);
         } else {
            p_209952_1_.setMax(i);
         }

      }, field_209957_b);
   }

   private static CommandSource func_198397_a(CommandSource p_198397_0_, IDataAccessor p_198397_1_, NBTPathArgument.NBTPath p_198397_2_, IntFunction<INBTBase> p_198397_3_, boolean p_198397_4_) {
      return p_198397_0_.withResultConsumer((p_198372_4_, p_198372_5_, p_198372_6_) -> {
         try {
            NBTTagCompound nbttagcompound = p_198397_1_.getData();
            int i = p_198397_4_ ? p_198372_6_ : (p_198372_5_ ? 1 : 0);
            p_198397_2_.func_197142_a(nbttagcompound, p_198397_3_.apply(i));
            p_198397_1_.mergeData(nbttagcompound);
         } catch (CommandSyntaxException var9) {
            ;
         }

      }, field_209957_b);
   }

   private static ArgumentBuilder<CommandSource, ?> func_198394_a(CommandNode<CommandSource> p_198394_0_, LiteralArgumentBuilder<CommandSource> p_198394_1_, boolean p_198394_2_) {
      return p_198394_1_.then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(func_210415_a(p_198394_0_, Commands.argument("block", BlockPredicateArgument.blockPredicateArgument()), p_198394_2_, (p_210434_0_) -> {
         return BlockPredicateArgument.getBlockPredicate(p_210434_0_, "block").test(new BlockWorldState(((CommandSource)p_210434_0_.getSource()).getWorld(), BlockPosArgument.getLoadedBlockPos(p_210434_0_, "pos"), true));
      })))).then(Commands.literal("score").then(Commands.argument("target", ScoreHolderArgument.singleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.literal("=").then(Commands.argument("source", ScoreHolderArgument.singleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).then(func_210415_a(p_198394_0_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_198394_2_, (p_210438_0_) -> {
         return func_198371_a(p_210438_0_, Integer::equals);
      })))).then(Commands.literal("<").then(Commands.argument("source", ScoreHolderArgument.singleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).then(func_210415_a(p_198394_0_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_198394_2_, (p_210442_0_) -> {
         return func_198371_a(p_210442_0_, (p_199669_0_, p_199669_1_) -> {
            return p_199669_0_ < p_199669_1_;
         });
      })))).then(Commands.literal("<=").then(Commands.argument("source", ScoreHolderArgument.singleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).then(func_210415_a(p_198394_0_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_198394_2_, (p_210418_0_) -> {
         return func_198371_a(p_210418_0_, (p_199672_0_, p_199672_1_) -> {
            return p_199672_0_ <= p_199672_1_;
         });
      })))).then(Commands.literal(">").then(Commands.argument("source", ScoreHolderArgument.singleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).then(func_210415_a(p_198394_0_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_198394_2_, (p_210422_0_) -> {
         return func_198371_a(p_210422_0_, (p_199651_0_, p_199651_1_) -> {
            return p_199651_0_ > p_199651_1_;
         });
      })))).then(Commands.literal(">=").then(Commands.argument("source", ScoreHolderArgument.singleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).then(func_210415_a(p_198394_0_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_198394_2_, (p_210424_0_) -> {
         return func_198371_a(p_210424_0_, (p_199650_0_, p_199650_1_) -> {
            return p_199650_0_ >= p_199650_1_;
         });
      })))).then(Commands.literal("matches").then(func_210415_a(p_198394_0_, Commands.argument("range", RangeArgument.func_211371_a()), p_198394_2_, (p_201088_0_) -> {
         return func_201115_a(p_201088_0_, RangeArgument.IntRange.getIntRange(p_201088_0_, "range"));
      })))))).then(Commands.literal("blocks").then(Commands.argument("start", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(Commands.argument("destination", BlockPosArgument.blockPos()).then(func_212178_a(p_198394_0_, Commands.literal("all"), p_198394_2_, false)).then(func_212178_a(p_198394_0_, Commands.literal("masked"), p_198394_2_, true)))))).then(Commands.literal("entity").then(Commands.argument("entities", EntityArgument.multipleEntities()).fork(p_198394_0_, (p_210428_1_) -> {
         return func_198411_a(p_210428_1_, p_198394_2_, !EntityArgument.getEntitiesAllowingNone(p_210428_1_, "entities").isEmpty());
      }).executes(p_198394_2_ ? (p_198380_0_) -> {
         int i = EntityArgument.getEntitiesAllowingNone(p_198380_0_, "entities").size();
         if (i > 0) {
            p_198380_0_.getSource().sendFeedback(new TextComponentTranslation("commands.execute.conditional.pass_count", i), false);
            return i;
         } else {
            throw field_210456_b.create();
         }
      } : (p_210451_0_) -> {
         int i = EntityArgument.getEntitiesAllowingNone(p_210451_0_, "entities").size();
         if (i == 0) {
            p_210451_0_.getSource().sendFeedback(new TextComponentTranslation("commands.execute.conditional.pass"), false);
            return 1;
         } else {
            throw field_210457_c.create(i);
         }
      })));
   }

   private static boolean func_198371_a(CommandContext<CommandSource> p_198371_0_, BiPredicate<Integer, Integer> p_198371_1_) throws CommandSyntaxException {
      String s = ScoreHolderArgument.func_197211_a(p_198371_0_, "target");
      ScoreObjective scoreobjective = ObjectiveArgument.getObjective(p_198371_0_, "targetObjective");
      String s1 = ScoreHolderArgument.func_197211_a(p_198371_0_, "source");
      ScoreObjective scoreobjective1 = ObjectiveArgument.getObjective(p_198371_0_, "sourceObjective");
      Scoreboard scoreboard = p_198371_0_.getSource().getServer().getWorldScoreboard();
      if (scoreboard.entityHasObjective(s, scoreobjective) && scoreboard.entityHasObjective(s1, scoreobjective1)) {
         Score score = scoreboard.getOrCreateScore(s, scoreobjective);
         Score score1 = scoreboard.getOrCreateScore(s1, scoreobjective1);
         return p_198371_1_.test(score.getScorePoints(), score1.getScorePoints());
      } else {
         return false;
      }
   }

   private static boolean func_201115_a(CommandContext<CommandSource> p_201115_0_, MinMaxBounds.IntBound p_201115_1_) throws CommandSyntaxException {
      String s = ScoreHolderArgument.func_197211_a(p_201115_0_, "target");
      ScoreObjective scoreobjective = ObjectiveArgument.getObjective(p_201115_0_, "targetObjective");
      Scoreboard scoreboard = p_201115_0_.getSource().getServer().getWorldScoreboard();
      return !scoreboard.entityHasObjective(s, scoreobjective) ? false : p_201115_1_.test(scoreboard.getOrCreateScore(s, scoreobjective).getScorePoints());
   }

   private static Collection<CommandSource> func_198411_a(CommandContext<CommandSource> p_198411_0_, boolean p_198411_1_, boolean p_198411_2_) {
      return (Collection<CommandSource>)(p_198411_2_ == p_198411_1_ ? Collections.singleton(p_198411_0_.getSource()) : Collections.emptyList());
   }

   private static ArgumentBuilder<CommandSource, ?> func_210415_a(CommandNode<CommandSource> p_210415_0_, ArgumentBuilder<CommandSource, ?> p_210415_1_, boolean p_210415_2_, ExecuteCommand.ExecuteTest p_210415_3_) {
      return p_210415_1_.fork(p_210415_0_, (p_210448_2_) -> {
         return func_198411_a(p_210448_2_, p_210415_2_, p_210415_3_.test(p_210448_2_));
      }).executes((p_210436_2_) -> {
         if (p_210415_2_ == p_210415_3_.test(p_210436_2_)) {
            ((CommandSource)p_210436_2_.getSource()).sendFeedback(new TextComponentTranslation("commands.execute.conditional.pass"), false);
            return 1;
         } else {
            throw field_210456_b.create();
         }
      });
   }

   private static ArgumentBuilder<CommandSource, ?> func_212178_a(CommandNode<CommandSource> p_212178_0_, ArgumentBuilder<CommandSource, ?> p_212178_1_, boolean p_212178_2_, boolean p_212178_3_) {
      return p_212178_1_.fork(p_212178_0_, (p_212171_2_) -> {
         return func_198411_a(p_212171_2_, p_212178_2_, func_212169_c(p_212171_2_, p_212178_3_).isPresent());
      }).executes(p_212178_2_ ? (p_212176_1_) -> {
         return func_212175_a(p_212176_1_, p_212178_3_);
      } : (p_212170_1_) -> {
         return func_212173_b(p_212170_1_, p_212178_3_);
      });
   }

   private static int func_212175_a(CommandContext<CommandSource> p_212175_0_, boolean p_212175_1_) throws CommandSyntaxException {
      OptionalInt optionalint = func_212169_c(p_212175_0_, p_212175_1_);
      if (optionalint.isPresent()) {
         p_212175_0_.getSource().sendFeedback(new TextComponentTranslation("commands.execute.conditional.pass_count", optionalint.getAsInt()), false);
         return optionalint.getAsInt();
      } else {
         throw field_210456_b.create();
      }
   }

   private static int func_212173_b(CommandContext<CommandSource> p_212173_0_, boolean p_212173_1_) throws CommandSyntaxException {
      OptionalInt optionalint = func_212169_c(p_212173_0_, p_212173_1_);
      if (!optionalint.isPresent()) {
         p_212173_0_.getSource().sendFeedback(new TextComponentTranslation("commands.execute.conditional.pass"), false);
         return 1;
      } else {
         throw field_210457_c.create(optionalint.getAsInt());
      }
   }

   private static OptionalInt func_212169_c(CommandContext<CommandSource> p_212169_0_, boolean p_212169_1_) throws CommandSyntaxException {
      return func_198395_a(p_212169_0_.getSource().getWorld(), BlockPosArgument.getLoadedBlockPos(p_212169_0_, "start"), BlockPosArgument.getLoadedBlockPos(p_212169_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_212169_0_, "destination"), p_212169_1_);
   }

   private static OptionalInt func_198395_a(WorldServer p_198395_0_, BlockPos p_198395_1_, BlockPos p_198395_2_, BlockPos p_198395_3_, boolean p_198395_4_) throws CommandSyntaxException {
      MutableBoundingBox mutableboundingbox = new MutableBoundingBox(p_198395_1_, p_198395_2_);
      MutableBoundingBox mutableboundingbox1 = new MutableBoundingBox(p_198395_3_, p_198395_3_.add(mutableboundingbox.getLength()));
      BlockPos blockpos = new BlockPos(mutableboundingbox1.minX - mutableboundingbox.minX, mutableboundingbox1.minY - mutableboundingbox.minY, mutableboundingbox1.minZ - mutableboundingbox.minZ);
      int i = mutableboundingbox.getXSize() * mutableboundingbox.getYSize() * mutableboundingbox.getZSize();
      if (i > 32768) {
         throw field_198421_a.create(32768, i);
      } else {
         int j = 0;

         for(int k = mutableboundingbox.minZ; k <= mutableboundingbox.maxZ; ++k) {
            for(int l = mutableboundingbox.minY; l <= mutableboundingbox.maxY; ++l) {
               for(int i1 = mutableboundingbox.minX; i1 <= mutableboundingbox.maxX; ++i1) {
                  BlockPos blockpos1 = new BlockPos(i1, l, k);
                  BlockPos blockpos2 = blockpos1.add(blockpos);
                  IBlockState iblockstate = p_198395_0_.getBlockState(blockpos1);
                  if (!p_198395_4_ || iblockstate.getBlock() != Blocks.AIR) {
                     if (iblockstate != p_198395_0_.getBlockState(blockpos2)) {
                        return OptionalInt.empty();
                     }

                     TileEntity tileentity = p_198395_0_.getTileEntity(blockpos1);
                     TileEntity tileentity1 = p_198395_0_.getTileEntity(blockpos2);
                     if (tileentity != null) {
                        if (tileentity1 == null) {
                           return OptionalInt.empty();
                        }

                        NBTTagCompound nbttagcompound = tileentity.write(new NBTTagCompound());
                        nbttagcompound.removeTag("x");
                        nbttagcompound.removeTag("y");
                        nbttagcompound.removeTag("z");
                        NBTTagCompound nbttagcompound1 = tileentity1.write(new NBTTagCompound());
                        nbttagcompound1.removeTag("x");
                        nbttagcompound1.removeTag("y");
                        nbttagcompound1.removeTag("z");
                        if (!nbttagcompound.equals(nbttagcompound1)) {
                           return OptionalInt.empty();
                        }
                     }

                     ++j;
                  }
               }
            }
         }

         return OptionalInt.of(j);
      }
   }

   @FunctionalInterface
   interface ExecuteTest {
      boolean test(CommandContext<CommandSource> p_test_1_) throws CommandSyntaxException;
   }
}