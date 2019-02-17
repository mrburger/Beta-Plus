package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class BlockMushroom extends BlockBush implements IGrowable {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

   public BlockMushroom(Block.Properties builder) {
      super(builder);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (random.nextInt(25) == 0) {
         int i = 5;
         int j = 4;

         for(BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
            if (worldIn.getBlockState(blockpos).getBlock() == this) {
               --i;
               if (i <= 0) {
                  return;
               }
            }
         }

         BlockPos blockpos1 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

         for(int k = 0; k < 4; ++k) {
            if (worldIn.isAirBlock(blockpos1) && state.isValidPosition(worldIn, blockpos1)) {
               pos = blockpos1;
            }

            blockpos1 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
         }

         if (worldIn.isAirBlock(blockpos1) && state.isValidPosition(worldIn, blockpos1)) {
            worldIn.setBlockState(blockpos1, state, 2);
         }
      }

   }

   protected boolean isValidGround(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.isOpaqueCube(worldIn, pos);
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      BlockPos blockpos = pos.down();
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      Block block = iblockstate.getBlock();
      if (block != Blocks.MYCELIUM && block != Blocks.PODZOL) {
         return worldIn.getLightSubtracted(pos, 0) < 13 && iblockstate.canSustainPlant(worldIn, blockpos, net.minecraft.util.EnumFacing.UP, this);
      } else {
         return true;
      }
   }

   public boolean generateBigMushroom(IWorld worldIn, BlockPos pos, IBlockState state, Random rand) {
      worldIn.removeBlock(pos);
      Feature<NoFeatureConfig> feature = null;
      if (this == Blocks.BROWN_MUSHROOM) {
         feature = Feature.BIG_BROWN_MUSHROOM;
      } else if (this == Blocks.RED_MUSHROOM) {
         feature = Feature.BIG_RED_MUSHROOM;
      }

      if (feature != null && feature.func_212245_a(worldIn, worldIn.getChunkProvider().getChunkGenerator(), rand, pos, IFeatureConfig.NO_FEATURE_CONFIG)) {
         return true;
      } else {
         worldIn.setBlockState(pos, state, 3);
         return false;
      }
   }

   /**
    * Whether this IGrowable can grow
    */
   public boolean canGrow(IBlockReader worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return true;
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return (double)rand.nextFloat() < 0.4D;
   }

   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      this.generateBigMushroom(worldIn, pos, state, rand);
   }

   public boolean needsPostProcessing(IBlockState p_201783_1_, IBlockReader worldIn, BlockPos pos) {
      return true;
   }
}