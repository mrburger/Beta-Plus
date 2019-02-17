package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public interface IBlockReader {
   @Nullable
   TileEntity getTileEntity(BlockPos pos);

   IBlockState getBlockState(BlockPos pos);

   IFluidState getFluidState(BlockPos pos);

   default int getMaxLightLevel() {
      return 15;
   }
}