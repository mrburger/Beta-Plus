package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

@Deprecated //Forge: Do not use, use IBlockState.hasTileEntity/Blocks.createTileEntity
public interface ITileEntityProvider {
   @Nullable
   TileEntity createNewTileEntity(IBlockReader worldIn);
}