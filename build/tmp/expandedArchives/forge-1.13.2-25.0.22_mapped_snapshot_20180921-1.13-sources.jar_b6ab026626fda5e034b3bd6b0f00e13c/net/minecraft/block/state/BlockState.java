package net.minecraft.block.state;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.state.AbstractStateHolder;
import net.minecraft.state.IProperty;

public class BlockState extends AbstractStateHolder<Block, IBlockState> implements IBlockState {
   public BlockState(Block blockIn, ImmutableMap<IProperty<?>, Comparable<?>> properties) {
      super(blockIn, properties);
   }

   public Block getBlock() {
      return this.object;
   }
}