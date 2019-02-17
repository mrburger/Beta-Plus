package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlockGlowstone extends Block {
   public BlockGlowstone(Block.Properties builder) {
      super(builder);
   }

   public int getItemsToDropCount(IBlockState state, int fortune, World worldIn, BlockPos pos, Random random) {
      return MathHelper.clamp(this.quantityDropped(state, random) + random.nextInt(fortune + 1), 1, 4);
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 2 + random.nextInt(3);
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.GLOWSTONE_DUST;
   }
}