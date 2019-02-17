package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMelon extends BlockStemGrown {
   protected BlockMelon(Block.Properties builder) {
      super(builder);
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.MELON_SLICE;
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 3 + random.nextInt(5);
   }

   public int getItemsToDropCount(IBlockState state, int fortune, World worldIn, BlockPos pos, Random random) {
      return Math.min(9, this.quantityDropped(state, random) + random.nextInt(1 + fortune));
   }

   public BlockStem getStem() {
      return (BlockStem)Blocks.MELON_STEM;
   }

   public BlockAttachedStem getAttachedStem() {
      return (BlockAttachedStem)Blocks.ATTACHED_MELON_STEM;
   }
}