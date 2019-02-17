package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockPotato extends BlockCrops {
   private static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D)};

   public BlockPotato(Block.Properties builder) {
      super(builder);
   }

   protected IItemProvider getSeedsItem() {
      return Items.POTATO;
   }

   protected IItemProvider getCropsItem() {
      return Items.POTATO;
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
      super.dropBlockAsItemWithChance(state, worldIn, pos, chancePerItem, fortune);
      if (false && !worldIn.isRemote) { //Forge: Moved to getDrops
         if (this.isMaxAge(state) && worldIn.rand.nextInt(50) == 0) {
            spawnAsEntity(worldIn, pos, new ItemStack(Items.POISONOUS_POTATO));
         }

      }
   }

   @Override
   public void getDrops(IBlockState state, net.minecraft.util.NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
      super.getDrops(state, drops, world, pos, fortune);
      if (this.isMaxAge(state) && world.rand.nextInt(50) == 0)
          drops.add(new ItemStack(Items.POISONOUS_POTATO));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPES[state.get(this.getAgeProperty())];
   }
}