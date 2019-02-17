package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockBeetroot extends BlockCrops {
   public static final IntegerProperty BEETROOT_AGE = BlockStateProperties.AGE_0_3;
   private static final VoxelShape[] SHAPE = new VoxelShape[]{Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D)};

   public BlockBeetroot(Block.Properties builder) {
      super(builder);
   }

   public IntegerProperty getAgeProperty() {
      return BEETROOT_AGE;
   }

   public int getMaxAge() {
      return 3;
   }

   protected IItemProvider getSeedsItem() {
      return Items.BEETROOT_SEEDS;
   }

   protected IItemProvider getCropsItem() {
      return Items.BEETROOT;
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (random.nextInt(3) != 0) {
         super.tick(state, worldIn, pos, random);
      }

   }

   protected int getBonemealAgeIncrease(World worldIn) {
      return super.getBonemealAgeIncrease(worldIn) / 3;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(BEETROOT_AGE);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE[state.get(this.getAgeProperty())];
   }
}