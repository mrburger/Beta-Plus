package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class FillCommand {
   private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((p_208897_0_, p_208897_1_) -> {
      return new TextComponentTranslation("commands.fill.toobig", p_208897_0_, p_208897_1_);
   });
   private static final BlockStateInput AIR = new BlockStateInput(Blocks.AIR.getDefaultState(), Collections.emptySet(), (NBTTagCompound)null);
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.fill.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("fill").requires((p_198471_0_) -> {
         return p_198471_0_.hasPermissionLevel(2);
      }).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(Commands.argument("block", BlockStateArgument.blockState()).executes((p_198472_0_) -> {
         return doFill(p_198472_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198472_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198472_0_, "to")), BlockStateArgument.getBlockStateInput(p_198472_0_, "block"), FillCommand.Mode.REPLACE, (Predicate<BlockWorldState>)null);
      }).then(Commands.literal("replace").executes((p_198464_0_) -> {
         return doFill(p_198464_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198464_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198464_0_, "to")), BlockStateArgument.getBlockStateInput(p_198464_0_, "block"), FillCommand.Mode.REPLACE, (Predicate<BlockWorldState>)null);
      }).then(Commands.argument("filter", BlockPredicateArgument.blockPredicateArgument()).executes((p_198466_0_) -> {
         return doFill(p_198466_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198466_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198466_0_, "to")), BlockStateArgument.getBlockStateInput(p_198466_0_, "block"), FillCommand.Mode.REPLACE, BlockPredicateArgument.getBlockPredicate(p_198466_0_, "filter"));
      }))).then(Commands.literal("keep").executes((p_198462_0_) -> {
         return doFill(p_198462_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198462_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198462_0_, "to")), BlockStateArgument.getBlockStateInput(p_198462_0_, "block"), FillCommand.Mode.REPLACE, (p_198469_0_) -> {
            return p_198469_0_.getWorld().isAirBlock(p_198469_0_.getPos());
         });
      })).then(Commands.literal("outline").executes((p_198467_0_) -> {
         return doFill(p_198467_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198467_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198467_0_, "to")), BlockStateArgument.getBlockStateInput(p_198467_0_, "block"), FillCommand.Mode.OUTLINE, (Predicate<BlockWorldState>)null);
      })).then(Commands.literal("hollow").executes((p_198461_0_) -> {
         return doFill(p_198461_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198461_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198461_0_, "to")), BlockStateArgument.getBlockStateInput(p_198461_0_, "block"), FillCommand.Mode.HOLLOW, (Predicate<BlockWorldState>)null);
      })).then(Commands.literal("destroy").executes((p_198468_0_) -> {
         return doFill(p_198468_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198468_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198468_0_, "to")), BlockStateArgument.getBlockStateInput(p_198468_0_, "block"), FillCommand.Mode.DESTROY, (Predicate<BlockWorldState>)null);
      }))))));
   }

   private static int doFill(CommandSource source, MutableBoundingBox area, BlockStateInput newBlock, FillCommand.Mode mode, @Nullable Predicate<BlockWorldState> replacingPredicate) throws CommandSyntaxException {
      int i = area.getXSize() * area.getYSize() * area.getZSize();
      if (i > 32768) {
         throw TOO_BIG_EXCEPTION.create(32768, i);
      } else {
         List<BlockPos> list = Lists.newArrayList();
         WorldServer worldserver = source.getWorld();
         int j = 0;

         for(BlockPos blockpos : BlockPos.MutableBlockPos.getAllInBox(area.minX, area.minY, area.minZ, area.maxX, area.maxY, area.maxZ)) {
            if (replacingPredicate == null || replacingPredicate.test(new BlockWorldState(worldserver, blockpos, true))) {
               BlockStateInput blockstateinput = mode.filter.filter(area, blockpos, newBlock, worldserver);
               if (blockstateinput != null) {
                  TileEntity tileentity = worldserver.getTileEntity(blockpos);
                  if (tileentity != null && tileentity instanceof IInventory) {
                     ((IInventory)tileentity).clear();
                  }

                  if (blockstateinput.place(worldserver, blockpos, 2)) {
                     list.add(blockpos.toImmutable());
                     ++j;
                  }
               }
            }
         }

         for(BlockPos blockpos1 : list) {
            Block block = worldserver.getBlockState(blockpos1).getBlock();
            worldserver.notifyNeighbors(blockpos1, block);
         }

         if (j == 0) {
            throw FAILED_EXCEPTION.create();
         } else {
            source.sendFeedback(new TextComponentTranslation("commands.fill.success", j), true);
            return j;
         }
      }
   }

   static enum Mode {
      REPLACE((p_198450_0_, p_198450_1_, p_198450_2_, p_198450_3_) -> {
         return p_198450_2_;
      }),
      OUTLINE((p_198454_0_, p_198454_1_, p_198454_2_, p_198454_3_) -> {
         return p_198454_1_.getX() != p_198454_0_.minX && p_198454_1_.getX() != p_198454_0_.maxX && p_198454_1_.getY() != p_198454_0_.minY && p_198454_1_.getY() != p_198454_0_.maxY && p_198454_1_.getZ() != p_198454_0_.minZ && p_198454_1_.getZ() != p_198454_0_.maxZ ? null : p_198454_2_;
      }),
      HOLLOW((p_198453_0_, p_198453_1_, p_198453_2_, p_198453_3_) -> {
         return p_198453_1_.getX() != p_198453_0_.minX && p_198453_1_.getX() != p_198453_0_.maxX && p_198453_1_.getY() != p_198453_0_.minY && p_198453_1_.getY() != p_198453_0_.maxY && p_198453_1_.getZ() != p_198453_0_.minZ && p_198453_1_.getZ() != p_198453_0_.maxZ ? FillCommand.AIR : p_198453_2_;
      }),
      DESTROY((p_198452_0_, p_198452_1_, p_198452_2_, p_198452_3_) -> {
         p_198452_3_.destroyBlock(p_198452_1_, true);
         return p_198452_2_;
      });

      public final SetBlockCommand.IFilter filter;

      private Mode(SetBlockCommand.IFilter filterIn) {
         this.filter = filterIn;
      }
   }
}