package net.minecraft.command.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.Vec2Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.WorldServer;

public class SpreadPlayersCommand {
   private static final Dynamic4CommandExceptionType field_198723_a = new Dynamic4CommandExceptionType((p_208910_0_, p_208910_1_, p_208910_2_, p_208910_3_) -> {
      return new TextComponentTranslation("commands.spreadplayers.failed.teams", p_208910_0_, p_208910_1_, p_208910_2_, p_208910_3_);
   });
   private static final Dynamic4CommandExceptionType field_198724_b = new Dynamic4CommandExceptionType((p_208912_0_, p_208912_1_, p_208912_2_, p_208912_3_) -> {
      return new TextComponentTranslation("commands.spreadplayers.failed.entities", p_208912_0_, p_208912_1_, p_208912_2_, p_208912_3_);
   });

   public static void register(CommandDispatcher<CommandSource> p_198716_0_) {
      p_198716_0_.register(Commands.literal("spreadplayers").requires((p_198721_0_) -> {
         return p_198721_0_.hasPermissionLevel(2);
      }).then(Commands.argument("center", Vec2Argument.vec2()).then(Commands.argument("spreadDistance", FloatArgumentType.floatArg(0.0F)).then(Commands.argument("maxRange", FloatArgumentType.floatArg(1.0F)).then(Commands.argument("respectTeams", BoolArgumentType.bool()).then(Commands.argument("targets", EntityArgument.multipleEntities()).executes((p_198718_0_) -> {
         return func_198722_a(p_198718_0_.getSource(), Vec2Argument.getVec2f(p_198718_0_, "center"), FloatArgumentType.getFloat(p_198718_0_, "spreadDistance"), FloatArgumentType.getFloat(p_198718_0_, "maxRange"), BoolArgumentType.getBool(p_198718_0_, "respectTeams"), EntityArgument.getEntities(p_198718_0_, "targets"));
      })))))));
   }

   private static int func_198722_a(CommandSource p_198722_0_, Vec2f p_198722_1_, float p_198722_2_, float p_198722_3_, boolean p_198722_4_, Collection<? extends Entity> p_198722_5_) throws CommandSyntaxException {
      Random random = new Random();
      double d0 = (double)(p_198722_1_.x - p_198722_3_);
      double d1 = (double)(p_198722_1_.y - p_198722_3_);
      double d2 = (double)(p_198722_1_.x + p_198722_3_);
      double d3 = (double)(p_198722_1_.y + p_198722_3_);
      SpreadPlayersCommand.Position[] aspreadplayerscommand$position = func_198720_a(random, p_198722_4_ ? func_198715_a(p_198722_5_) : p_198722_5_.size(), d0, d1, d2, d3);
      func_198717_a(p_198722_1_, (double)p_198722_2_, p_198722_0_.getWorld(), random, d0, d1, d2, d3, aspreadplayerscommand$position, p_198722_4_);
      double d4 = func_198719_a(p_198722_5_, p_198722_0_.getWorld(), aspreadplayerscommand$position, p_198722_4_);
      p_198722_0_.sendFeedback(new TextComponentTranslation("commands.spreadplayers.success." + (p_198722_4_ ? "teams" : "entities"), aspreadplayerscommand$position.length, p_198722_1_.x, p_198722_1_.y, String.format(Locale.ROOT, "%.2f", d4)), true);
      return aspreadplayerscommand$position.length;
   }

   private static int func_198715_a(Collection<? extends Entity> p_198715_0_) {
      Set<Team> set = Sets.newHashSet();

      for(Entity entity : p_198715_0_) {
         if (entity instanceof EntityPlayer) {
            set.add(entity.getTeam());
         } else {
            set.add((Team)null);
         }
      }

      return set.size();
   }

   private static void func_198717_a(Vec2f p_198717_0_, double p_198717_1_, WorldServer p_198717_3_, Random p_198717_4_, double p_198717_5_, double p_198717_7_, double p_198717_9_, double p_198717_11_, SpreadPlayersCommand.Position[] p_198717_13_, boolean p_198717_14_) throws CommandSyntaxException {
      boolean flag = true;
      double d0 = (double)Float.MAX_VALUE;

      int i;
      for(i = 0; i < 10000 && flag; ++i) {
         flag = false;
         d0 = (double)Float.MAX_VALUE;

         for(int j = 0; j < p_198717_13_.length; ++j) {
            SpreadPlayersCommand.Position spreadplayerscommand$position = p_198717_13_[j];
            int k = 0;
            SpreadPlayersCommand.Position spreadplayerscommand$position1 = new SpreadPlayersCommand.Position();

            for(int l = 0; l < p_198717_13_.length; ++l) {
               if (j != l) {
                  SpreadPlayersCommand.Position spreadplayerscommand$position2 = p_198717_13_[l];
                  double d1 = spreadplayerscommand$position.func_198708_a(spreadplayerscommand$position2);
                  d0 = Math.min(d1, d0);
                  if (d1 < p_198717_1_) {
                     ++k;
                     spreadplayerscommand$position1.field_198713_a = spreadplayerscommand$position1.field_198713_a + (spreadplayerscommand$position2.field_198713_a - spreadplayerscommand$position.field_198713_a);
                     spreadplayerscommand$position1.field_198714_b = spreadplayerscommand$position1.field_198714_b + (spreadplayerscommand$position2.field_198714_b - spreadplayerscommand$position.field_198714_b);
                  }
               }
            }

            if (k > 0) {
               spreadplayerscommand$position1.field_198713_a = spreadplayerscommand$position1.field_198713_a / (double)k;
               spreadplayerscommand$position1.field_198714_b = spreadplayerscommand$position1.field_198714_b / (double)k;
               double d2 = (double)spreadplayerscommand$position1.func_198712_b();
               if (d2 > 0.0D) {
                  spreadplayerscommand$position1.func_198707_a();
                  spreadplayerscommand$position.func_198705_b(spreadplayerscommand$position1);
               } else {
                  spreadplayerscommand$position.func_198711_a(p_198717_4_, p_198717_5_, p_198717_7_, p_198717_9_, p_198717_11_);
               }

               flag = true;
            }

            if (spreadplayerscommand$position.func_198709_a(p_198717_5_, p_198717_7_, p_198717_9_, p_198717_11_)) {
               flag = true;
            }
         }

         if (!flag) {
            for(SpreadPlayersCommand.Position spreadplayerscommand$position3 : p_198717_13_) {
               if (!spreadplayerscommand$position3.func_198706_b(p_198717_3_)) {
                  spreadplayerscommand$position3.func_198711_a(p_198717_4_, p_198717_5_, p_198717_7_, p_198717_9_, p_198717_11_);
                  flag = true;
               }
            }
         }
      }

      if (d0 == (double)Float.MAX_VALUE) {
         d0 = 0.0D;
      }

      if (i >= 10000) {
         if (p_198717_14_) {
            throw field_198723_a.create(p_198717_13_.length, p_198717_0_.x, p_198717_0_.y, String.format(Locale.ROOT, "%.2f", d0));
         } else {
            throw field_198724_b.create(p_198717_13_.length, p_198717_0_.x, p_198717_0_.y, String.format(Locale.ROOT, "%.2f", d0));
         }
      }
   }

   private static double func_198719_a(Collection<? extends Entity> p_198719_0_, WorldServer p_198719_1_, SpreadPlayersCommand.Position[] p_198719_2_, boolean p_198719_3_) {
      double d0 = 0.0D;
      int i = 0;
      Map<Team, SpreadPlayersCommand.Position> map = Maps.newHashMap();

      for(Entity entity : p_198719_0_) {
         SpreadPlayersCommand.Position spreadplayerscommand$position;
         if (p_198719_3_) {
            Team team = entity instanceof EntityPlayer ? entity.getTeam() : null;
            if (!map.containsKey(team)) {
               map.put(team, p_198719_2_[i++]);
            }

            spreadplayerscommand$position = map.get(team);
         } else {
            spreadplayerscommand$position = p_198719_2_[i++];
         }

         entity.setPositionAndUpdate((double)((float)MathHelper.floor(spreadplayerscommand$position.field_198713_a) + 0.5F), (double)spreadplayerscommand$position.func_198710_a(p_198719_1_), (double)MathHelper.floor(spreadplayerscommand$position.field_198714_b) + 0.5D);
         double d2 = Double.MAX_VALUE;

         for(SpreadPlayersCommand.Position spreadplayerscommand$position1 : p_198719_2_) {
            if (spreadplayerscommand$position != spreadplayerscommand$position1) {
               double d1 = spreadplayerscommand$position.func_198708_a(spreadplayerscommand$position1);
               d2 = Math.min(d1, d2);
            }
         }

         d0 += d2;
      }

      if (p_198719_0_.size() < 2) {
         return 0.0D;
      } else {
         d0 = d0 / (double)p_198719_0_.size();
         return d0;
      }
   }

   private static SpreadPlayersCommand.Position[] func_198720_a(Random p_198720_0_, int p_198720_1_, double p_198720_2_, double p_198720_4_, double p_198720_6_, double p_198720_8_) {
      SpreadPlayersCommand.Position[] aspreadplayerscommand$position = new SpreadPlayersCommand.Position[p_198720_1_];

      for(int i = 0; i < aspreadplayerscommand$position.length; ++i) {
         SpreadPlayersCommand.Position spreadplayerscommand$position = new SpreadPlayersCommand.Position();
         spreadplayerscommand$position.func_198711_a(p_198720_0_, p_198720_2_, p_198720_4_, p_198720_6_, p_198720_8_);
         aspreadplayerscommand$position[i] = spreadplayerscommand$position;
      }

      return aspreadplayerscommand$position;
   }

   static class Position {
      private double field_198713_a;
      private double field_198714_b;

      double func_198708_a(SpreadPlayersCommand.Position p_198708_1_) {
         double d0 = this.field_198713_a - p_198708_1_.field_198713_a;
         double d1 = this.field_198714_b - p_198708_1_.field_198714_b;
         return Math.sqrt(d0 * d0 + d1 * d1);
      }

      void func_198707_a() {
         double d0 = (double)this.func_198712_b();
         this.field_198713_a /= d0;
         this.field_198714_b /= d0;
      }

      float func_198712_b() {
         return MathHelper.sqrt(this.field_198713_a * this.field_198713_a + this.field_198714_b * this.field_198714_b);
      }

      public void func_198705_b(SpreadPlayersCommand.Position p_198705_1_) {
         this.field_198713_a -= p_198705_1_.field_198713_a;
         this.field_198714_b -= p_198705_1_.field_198714_b;
      }

      public boolean func_198709_a(double p_198709_1_, double p_198709_3_, double p_198709_5_, double p_198709_7_) {
         boolean flag = false;
         if (this.field_198713_a < p_198709_1_) {
            this.field_198713_a = p_198709_1_;
            flag = true;
         } else if (this.field_198713_a > p_198709_5_) {
            this.field_198713_a = p_198709_5_;
            flag = true;
         }

         if (this.field_198714_b < p_198709_3_) {
            this.field_198714_b = p_198709_3_;
            flag = true;
         } else if (this.field_198714_b > p_198709_7_) {
            this.field_198714_b = p_198709_7_;
            flag = true;
         }

         return flag;
      }

      public int func_198710_a(IBlockReader p_198710_1_) {
         BlockPos blockpos = new BlockPos(this.field_198713_a, 256.0D, this.field_198714_b);

         while(blockpos.getY() > 0) {
            blockpos = blockpos.down();
            if (!p_198710_1_.getBlockState(blockpos).isAir()) {
               return blockpos.getY() + 1;
            }
         }

         return 257;
      }

      public boolean func_198706_b(IBlockReader p_198706_1_) {
         BlockPos blockpos = new BlockPos(this.field_198713_a, 256.0D, this.field_198714_b);

         while(blockpos.getY() > 0) {
            blockpos = blockpos.down();
            IBlockState iblockstate = p_198706_1_.getBlockState(blockpos);
            if (!iblockstate.isAir()) {
               Material material = iblockstate.getMaterial();
               return !material.isLiquid() && material != Material.FIRE;
            }
         }

         return false;
      }

      public void func_198711_a(Random p_198711_1_, double p_198711_2_, double p_198711_4_, double p_198711_6_, double p_198711_8_) {
         this.field_198713_a = MathHelper.nextDouble(p_198711_1_, p_198711_2_, p_198711_6_);
         this.field_198714_b = MathHelper.nextDouble(p_198711_1_, p_198711_4_, p_198711_8_);
      }
   }
}