package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class CloneCommand {
   private static final SimpleCommandExceptionType OVERLAP_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.clone.overlap"));
   private static final Dynamic2CommandExceptionType CLONE_TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((p_208796_0_, p_208796_1_) -> {
      return new TextComponentTranslation("commands.clone.toobig", p_208796_0_, p_208796_1_);
   });
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.clone.failed"));
   public static final Predicate<BlockWorldState> NOT_AIR = (p_198275_0_) -> {
      return !p_198275_0_.getBlockState().isAir();
   };

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("clone").requires((p_198271_0_) -> {
         return p_198271_0_.hasPermissionLevel(2);
      }).then(Commands.argument("begin", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(Commands.argument("destination", BlockPosArgument.blockPos()).executes((p_198264_0_) -> {
         return doClone(p_198264_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198264_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198264_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198264_0_, "destination"), (p_198269_0_) -> {
            return true;
         }, CloneCommand.Mode.NORMAL);
      }).then(Commands.literal("replace").executes((p_198268_0_) -> {
         return doClone(p_198268_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198268_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198268_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198268_0_, "destination"), (p_198272_0_) -> {
            return true;
         }, CloneCommand.Mode.NORMAL);
      }).then(Commands.literal("force").executes((p_198277_0_) -> {
         return doClone(p_198277_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198277_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198277_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198277_0_, "destination"), (p_198262_0_) -> {
            return true;
         }, CloneCommand.Mode.FORCE);
      })).then(Commands.literal("move").executes((p_198280_0_) -> {
         return doClone(p_198280_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198280_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198280_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198280_0_, "destination"), (p_198281_0_) -> {
            return true;
         }, CloneCommand.Mode.MOVE);
      })).then(Commands.literal("normal").executes((p_198270_0_) -> {
         return doClone(p_198270_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198270_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198270_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198270_0_, "destination"), (p_198279_0_) -> {
            return true;
         }, CloneCommand.Mode.NORMAL);
      }))).then(Commands.literal("masked").executes((p_198276_0_) -> {
         return doClone(p_198276_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198276_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198276_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198276_0_, "destination"), NOT_AIR, CloneCommand.Mode.NORMAL);
      }).then(Commands.literal("force").executes((p_198282_0_) -> {
         return doClone(p_198282_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198282_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198282_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198282_0_, "destination"), NOT_AIR, CloneCommand.Mode.FORCE);
      })).then(Commands.literal("move").executes((p_198263_0_) -> {
         return doClone(p_198263_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198263_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198263_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198263_0_, "destination"), NOT_AIR, CloneCommand.Mode.MOVE);
      })).then(Commands.literal("normal").executes((p_198266_0_) -> {
         return doClone(p_198266_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198266_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198266_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198266_0_, "destination"), NOT_AIR, CloneCommand.Mode.NORMAL);
      }))).then(Commands.literal("filtered").then(Commands.argument("filter", BlockPredicateArgument.blockPredicateArgument()).executes((p_198273_0_) -> {
         return doClone(p_198273_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198273_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198273_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198273_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198273_0_, "filter"), CloneCommand.Mode.NORMAL);
      }).then(Commands.literal("force").executes((p_198267_0_) -> {
         return doClone(p_198267_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198267_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198267_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198267_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198267_0_, "filter"), CloneCommand.Mode.FORCE);
      })).then(Commands.literal("move").executes((p_198261_0_) -> {
         return doClone(p_198261_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198261_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198261_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198261_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198261_0_, "filter"), CloneCommand.Mode.MOVE);
      })).then(Commands.literal("normal").executes((p_198278_0_) -> {
         return doClone(p_198278_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198278_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198278_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198278_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198278_0_, "filter"), CloneCommand.Mode.NORMAL);
      }))))))));
   }

   private static int doClone(CommandSource source, BlockPos beginPos, BlockPos endPos, BlockPos destPos, Predicate<BlockWorldState> filterPredicate, CloneCommand.Mode cloneMode) throws CommandSyntaxException {
      MutableBoundingBox mutableboundingbox = new MutableBoundingBox(beginPos, endPos);
      MutableBoundingBox mutableboundingbox1 = new MutableBoundingBox(destPos, destPos.add(mutableboundingbox.getLength()));
      if (!cloneMode.allowsOverlap() && mutableboundingbox1.intersectsWith(mutableboundingbox)) {
         throw OVERLAP_EXCEPTION.create();
      } else {
         int i = mutableboundingbox.getXSize() * mutableboundingbox.getYSize() * mutableboundingbox.getZSize();
         if (i > 32768) {
            throw CLONE_TOO_BIG_EXCEPTION.create(32768, i);
         } else {
            WorldServer worldserver = source.getWorld();
            if (worldserver.isAreaLoaded(mutableboundingbox) && worldserver.isAreaLoaded(mutableboundingbox1)) {
               List<CloneCommand.BlockInfo> list = Lists.newArrayList();
               List<CloneCommand.BlockInfo> list1 = Lists.newArrayList();
               List<CloneCommand.BlockInfo> list2 = Lists.newArrayList();
               Deque<BlockPos> deque = Lists.newLinkedList();
               BlockPos blockpos = new BlockPos(mutableboundingbox1.minX - mutableboundingbox.minX, mutableboundingbox1.minY - mutableboundingbox.minY, mutableboundingbox1.minZ - mutableboundingbox.minZ);

               for(int j = mutableboundingbox.minZ; j <= mutableboundingbox.maxZ; ++j) {
                  for(int k = mutableboundingbox.minY; k <= mutableboundingbox.maxY; ++k) {
                     for(int l = mutableboundingbox.minX; l <= mutableboundingbox.maxX; ++l) {
                        BlockPos blockpos1 = new BlockPos(l, k, j);
                        BlockPos blockpos2 = blockpos1.add(blockpos);
                        BlockWorldState blockworldstate = new BlockWorldState(worldserver, blockpos1, false);
                        IBlockState iblockstate = blockworldstate.getBlockState();
                        if (filterPredicate.test(blockworldstate)) {
                           TileEntity tileentity = worldserver.getTileEntity(blockpos1);
                           if (tileentity != null) {
                              NBTTagCompound nbttagcompound = tileentity.write(new NBTTagCompound());
                              list1.add(new CloneCommand.BlockInfo(blockpos2, iblockstate, nbttagcompound));
                              deque.addLast(blockpos1);
                           } else if (!iblockstate.isOpaqueCube(worldserver, blockpos1) && !iblockstate.isFullCube()) {
                              list2.add(new CloneCommand.BlockInfo(blockpos2, iblockstate, (NBTTagCompound)null));
                              deque.addFirst(blockpos1);
                           } else {
                              list.add(new CloneCommand.BlockInfo(blockpos2, iblockstate, (NBTTagCompound)null));
                              deque.addLast(blockpos1);
                           }
                        }
                     }
                  }
               }

               if (cloneMode == CloneCommand.Mode.MOVE) {
                  for(BlockPos blockpos3 : deque) {
                     TileEntity tileentity1 = worldserver.getTileEntity(blockpos3);
                     if (tileentity1 instanceof IInventory) {
                        ((IInventory)tileentity1).clear();
                     }

                     worldserver.setBlockState(blockpos3, Blocks.BARRIER.getDefaultState(), 2);
                  }

                  for(BlockPos blockpos4 : deque) {
                     worldserver.setBlockState(blockpos4, Blocks.AIR.getDefaultState(), 3);
                  }
               }

               List<CloneCommand.BlockInfo> list3 = Lists.newArrayList();
               list3.addAll(list);
               list3.addAll(list1);
               list3.addAll(list2);
               List<CloneCommand.BlockInfo> list4 = Lists.reverse(list3);

               for(CloneCommand.BlockInfo clonecommand$blockinfo : list4) {
                  TileEntity tileentity2 = worldserver.getTileEntity(clonecommand$blockinfo.pos);
                  if (tileentity2 instanceof IInventory) {
                     ((IInventory)tileentity2).clear();
                  }

                  worldserver.setBlockState(clonecommand$blockinfo.pos, Blocks.BARRIER.getDefaultState(), 2);
               }

               int i1 = 0;

               for(CloneCommand.BlockInfo clonecommand$blockinfo1 : list3) {
                  if (worldserver.setBlockState(clonecommand$blockinfo1.pos, clonecommand$blockinfo1.state, 2)) {
                     ++i1;
                  }
               }

               for(CloneCommand.BlockInfo clonecommand$blockinfo2 : list1) {
                  TileEntity tileentity3 = worldserver.getTileEntity(clonecommand$blockinfo2.pos);
                  if (clonecommand$blockinfo2.tag != null && tileentity3 != null) {
                     clonecommand$blockinfo2.tag.setInt("x", clonecommand$blockinfo2.pos.getX());
                     clonecommand$blockinfo2.tag.setInt("y", clonecommand$blockinfo2.pos.getY());
                     clonecommand$blockinfo2.tag.setInt("z", clonecommand$blockinfo2.pos.getZ());
                     tileentity3.read(clonecommand$blockinfo2.tag);
                     tileentity3.markDirty();
                  }

                  worldserver.setBlockState(clonecommand$blockinfo2.pos, clonecommand$blockinfo2.state, 2);
               }

               for(CloneCommand.BlockInfo clonecommand$blockinfo3 : list4) {
                  worldserver.notifyNeighbors(clonecommand$blockinfo3.pos, clonecommand$blockinfo3.state.getBlock());
               }

               worldserver.getPendingBlockTicks().copyTicks(mutableboundingbox, blockpos);
               if (i1 == 0) {
                  throw FAILED_EXCEPTION.create();
               } else {
                  source.sendFeedback(new TextComponentTranslation("commands.clone.success", i1), true);
                  return i1;
               }
            } else {
               throw BlockPosArgument.field_197278_b.create();
            }
         }
      }
   }

   static class BlockInfo {
      public final BlockPos pos;
      public final IBlockState state;
      @Nullable
      public final NBTTagCompound tag;

      public BlockInfo(BlockPos posIn, IBlockState stateIn, @Nullable NBTTagCompound tagIn) {
         this.pos = posIn;
         this.state = stateIn;
         this.tag = tagIn;
      }
   }

   static enum Mode {
      FORCE(true),
      MOVE(true),
      NORMAL(false);

      private final boolean allowOverlap;

      private Mode(boolean p_i47707_3_) {
         this.allowOverlap = p_i47707_3_;
      }

      public boolean allowsOverlap() {
         return this.allowOverlap;
      }
   }
}