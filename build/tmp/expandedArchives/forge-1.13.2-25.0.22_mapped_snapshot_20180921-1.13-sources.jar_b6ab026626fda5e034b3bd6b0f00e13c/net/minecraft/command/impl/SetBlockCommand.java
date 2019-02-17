package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class SetBlockCommand {
   private static final SimpleCommandExceptionType field_198689_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.setblock.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198684_0_) {
      p_198684_0_.register(Commands.literal("setblock").requires((p_198688_0_) -> {
         return p_198688_0_.hasPermissionLevel(2);
      }).then(Commands.argument("pos", BlockPosArgument.blockPos()).then(Commands.argument("block", BlockStateArgument.blockState()).executes((p_198682_0_) -> {
         return setBlock(p_198682_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198682_0_, "pos"), BlockStateArgument.getBlockStateInput(p_198682_0_, "block"), SetBlockCommand.Mode.REPLACE, (Predicate<BlockWorldState>)null);
      }).then(Commands.literal("destroy").executes((p_198685_0_) -> {
         return setBlock(p_198685_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198685_0_, "pos"), BlockStateArgument.getBlockStateInput(p_198685_0_, "block"), SetBlockCommand.Mode.DESTROY, (Predicate<BlockWorldState>)null);
      })).then(Commands.literal("keep").executes((p_198681_0_) -> {
         return setBlock(p_198681_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198681_0_, "pos"), BlockStateArgument.getBlockStateInput(p_198681_0_, "block"), SetBlockCommand.Mode.REPLACE, (p_198687_0_) -> {
            return p_198687_0_.getWorld().isAirBlock(p_198687_0_.getPos());
         });
      })).then(Commands.literal("replace").executes((p_198686_0_) -> {
         return setBlock(p_198686_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198686_0_, "pos"), BlockStateArgument.getBlockStateInput(p_198686_0_, "block"), SetBlockCommand.Mode.REPLACE, (Predicate<BlockWorldState>)null);
      })))));
   }

   private static int setBlock(CommandSource p_198683_0_, BlockPos p_198683_1_, BlockStateInput p_198683_2_, SetBlockCommand.Mode p_198683_3_, @Nullable Predicate<BlockWorldState> p_198683_4_) throws CommandSyntaxException {
      WorldServer worldserver = p_198683_0_.getWorld();
      if (p_198683_4_ != null && !p_198683_4_.test(new BlockWorldState(worldserver, p_198683_1_, true))) {
         throw field_198689_a.create();
      } else {
         boolean flag;
         if (p_198683_3_ == SetBlockCommand.Mode.DESTROY) {
            worldserver.destroyBlock(p_198683_1_, true);
            flag = !p_198683_2_.getBlockState().isAir();
         } else {
            TileEntity tileentity = worldserver.getTileEntity(p_198683_1_);
            if (tileentity instanceof IInventory) {
               ((IInventory)tileentity).clear();
            }

            flag = true;
         }

         if (flag && !p_198683_2_.place(worldserver, p_198683_1_, 2)) {
            throw field_198689_a.create();
         } else {
            worldserver.notifyNeighbors(p_198683_1_, p_198683_2_.getBlockState().getBlock());
            p_198683_0_.sendFeedback(new TextComponentTranslation("commands.setblock.success", p_198683_1_.getX(), p_198683_1_.getY(), p_198683_1_.getZ()), true);
            return 1;
         }
      }
   }

   public interface IFilter {
      @Nullable
      BlockStateInput filter(MutableBoundingBox p_filter_1_, BlockPos p_filter_2_, BlockStateInput p_filter_3_, WorldServer p_filter_4_);
   }

   public static enum Mode {
      REPLACE,
      OUTLINE,
      HOLLOW,
      DESTROY;
   }
}