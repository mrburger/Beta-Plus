package net.minecraft.command.impl;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.ColumnPosArgument;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;

public class ForceLoadCommand {
   private static final Dynamic2CommandExceptionType field_212726_a = new Dynamic2CommandExceptionType((p_212724_0_, p_212724_1_) -> {
      return new TextComponentTranslation("commands.forceload.toobig", p_212724_0_, p_212724_1_);
   });
   private static final Dynamic2CommandExceptionType field_212727_b = new Dynamic2CommandExceptionType((p_212717_0_, p_212717_1_) -> {
      return new TextComponentTranslation("commands.forceload.query.failure", p_212717_0_, p_212717_1_);
   });
   private static final SimpleCommandExceptionType field_212728_c = new SimpleCommandExceptionType(new TextComponentTranslation("commands.forceload.added.failure"));
   private static final SimpleCommandExceptionType field_212729_d = new SimpleCommandExceptionType(new TextComponentTranslation("commands.forceload.removed.failure"));

   public static void func_212712_a(CommandDispatcher<CommandSource> p_212712_0_) {
      p_212712_0_.register(Commands.literal("forceload").requires((p_212716_0_) -> {
         return p_212716_0_.hasPermissionLevel(4);
      }).then(Commands.literal("add").then(Commands.argument("from", ColumnPosArgument.func_212603_a()).executes((p_212725_0_) -> {
         return func_212719_a(p_212725_0_.getSource(), ColumnPosArgument.func_212602_a(p_212725_0_, "from"), ColumnPosArgument.func_212602_a(p_212725_0_, "from"), true);
      }).then(Commands.argument("to", ColumnPosArgument.func_212603_a()).executes((p_212711_0_) -> {
         return func_212719_a(p_212711_0_.getSource(), ColumnPosArgument.func_212602_a(p_212711_0_, "from"), ColumnPosArgument.func_212602_a(p_212711_0_, "to"), true);
      })))).then(Commands.literal("remove").then(Commands.argument("from", ColumnPosArgument.func_212603_a()).executes((p_212714_0_) -> {
         return func_212719_a(p_212714_0_.getSource(), ColumnPosArgument.func_212602_a(p_212714_0_, "from"), ColumnPosArgument.func_212602_a(p_212714_0_, "from"), false);
      }).then(Commands.argument("to", ColumnPosArgument.func_212603_a()).executes((p_212718_0_) -> {
         return func_212719_a(p_212718_0_.getSource(), ColumnPosArgument.func_212602_a(p_212718_0_, "from"), ColumnPosArgument.func_212602_a(p_212718_0_, "to"), false);
      }))).then(Commands.literal("all").executes((p_212715_0_) -> {
         return func_212722_b(p_212715_0_.getSource());
      }))).then(Commands.literal("query").executes((p_212710_0_) -> {
         return func_212721_a(p_212710_0_.getSource());
      }).then(Commands.argument("pos", ColumnPosArgument.func_212603_a()).executes((p_212723_0_) -> {
         return func_212713_a(p_212723_0_.getSource(), ColumnPosArgument.func_212602_a(p_212723_0_, "pos"));
      }))));
   }

   private static int func_212713_a(CommandSource p_212713_0_, ColumnPosArgument.ColumnPos p_212713_1_) throws CommandSyntaxException {
      ChunkPos chunkpos = new ChunkPos(p_212713_1_.field_212600_a >> 4, p_212713_1_.field_212601_b >> 4);
      DimensionType dimensiontype = p_212713_0_.getWorld().getDimension().getType();
      boolean flag = p_212713_0_.getServer().getWorld(dimensiontype).func_212416_f(chunkpos.x, chunkpos.z);
      if (flag) {
         p_212713_0_.sendFeedback(new TextComponentTranslation("commands.forceload.query.success", chunkpos, dimensiontype), false);
         return 1;
      } else {
         throw field_212727_b.create(chunkpos, dimensiontype);
      }
   }

   private static int func_212721_a(CommandSource p_212721_0_) {
      DimensionType dimensiontype = p_212721_0_.getWorld().getDimension().getType();
      LongSet longset = p_212721_0_.getServer().getWorld(dimensiontype).func_212412_ag();
      int i = longset.size();
      if (i > 0) {
         String s = Joiner.on(", ").join(longset.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
         if (i == 1) {
            p_212721_0_.sendFeedback(new TextComponentTranslation("commands.forceload.list.single", dimensiontype, s), false);
         } else {
            p_212721_0_.sendFeedback(new TextComponentTranslation("commands.forceload.list.multiple", i, dimensiontype, s), false);
         }
      } else {
         p_212721_0_.sendErrorMessage(new TextComponentTranslation("commands.forceload.added.none", dimensiontype));
      }

      return i;
   }

   private static int func_212722_b(CommandSource p_212722_0_) {
      DimensionType dimensiontype = p_212722_0_.getWorld().getDimension().getType();
      WorldServer worldserver = p_212722_0_.getServer().getWorld(dimensiontype);
      LongSet longset = worldserver.func_212412_ag();
      longset.forEach((long p_212720_1_) -> {
         worldserver.func_212414_b(ChunkPos.func_212578_a(p_212720_1_), ChunkPos.func_212579_b(p_212720_1_), false);
      });
      p_212722_0_.sendFeedback(new TextComponentTranslation("commands.forceload.removed.all", dimensiontype), true);
      return 0;
   }

   private static int func_212719_a(CommandSource p_212719_0_, ColumnPosArgument.ColumnPos p_212719_1_, ColumnPosArgument.ColumnPos p_212719_2_, boolean p_212719_3_) throws CommandSyntaxException {
      int i = Math.min(p_212719_1_.field_212600_a, p_212719_2_.field_212600_a);
      int j = Math.min(p_212719_1_.field_212601_b, p_212719_2_.field_212601_b);
      int k = Math.max(p_212719_1_.field_212600_a, p_212719_2_.field_212600_a);
      int l = Math.max(p_212719_1_.field_212601_b, p_212719_2_.field_212601_b);
      if (i >= -30000000 && j >= -30000000 && k < 30000000 && l < 30000000) {
         int i1 = i >> 4;
         int j1 = j >> 4;
         int k1 = k >> 4;
         int l1 = l >> 4;
         long i2 = ((long)(k1 - i1) + 1L) * ((long)(l1 - j1) + 1L);
         if (i2 > 256L) {
            throw field_212726_a.create(256, i2);
         } else {
            DimensionType dimensiontype = p_212719_0_.getWorld().getDimension().getType();
            WorldServer worldserver = p_212719_0_.getServer().getWorld(dimensiontype);
            ChunkPos chunkpos = null;
            int j2 = 0;

            for(int k2 = i1; k2 <= k1; ++k2) {
               for(int l2 = j1; l2 <= l1; ++l2) {
                  boolean flag = worldserver.func_212414_b(k2, l2, p_212719_3_);
                  if (flag) {
                     ++j2;
                     if (chunkpos == null) {
                        chunkpos = new ChunkPos(k2, l2);
                     }
                  }
               }
            }

            if (j2 == 0) {
               throw (p_212719_3_ ? field_212728_c : field_212729_d).create();
            } else {
               if (j2 == 1) {
                  p_212719_0_.sendFeedback(new TextComponentTranslation("commands.forceload." + (p_212719_3_ ? "added" : "removed") + ".single", chunkpos, dimensiontype), true);
               } else {
                  ChunkPos chunkpos1 = new ChunkPos(i1, j1);
                  ChunkPos chunkpos2 = new ChunkPos(k1, l1);
                  p_212719_0_.sendFeedback(new TextComponentTranslation("commands.forceload." + (p_212719_3_ ? "added" : "removed") + ".multiple", j2, dimensiontype, chunkpos1, chunkpos2), true);
               }

               return j2;
            }
         }
      } else {
         throw BlockPosArgument.field_197279_c.create();
      }
   }
}