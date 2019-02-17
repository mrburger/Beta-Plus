package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockClay extends Block {
   public BlockClay(Block.Properties builder) {
      super(builder);
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.CLAY_BALL;
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 4;
   }
}