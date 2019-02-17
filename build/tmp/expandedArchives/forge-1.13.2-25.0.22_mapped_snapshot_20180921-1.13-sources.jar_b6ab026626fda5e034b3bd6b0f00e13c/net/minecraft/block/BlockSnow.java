package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.World;

public class BlockSnow extends Block {
   protected BlockSnow(Block.Properties builder) {
      super(builder);
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.SNOWBALL;
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 4;
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (worldIn.getLightFor(EnumLightType.BLOCK, pos) > 11) {
         state.dropBlockAsItem(worldIn, pos, 0);
         worldIn.removeBlock(pos);
      }

   }
}