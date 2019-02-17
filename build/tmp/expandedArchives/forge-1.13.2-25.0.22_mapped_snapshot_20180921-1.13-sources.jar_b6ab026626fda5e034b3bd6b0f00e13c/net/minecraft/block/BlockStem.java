package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockStem extends BlockBush implements IGrowable {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;
   protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 4.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 12.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
   private final BlockStemGrown crop;

   protected BlockStem(BlockStemGrown p_i48318_1_, Block.Properties p_i48318_2_) {
      super(p_i48318_2_);
      this.crop = p_i48318_1_;
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPES[state.get(AGE)];
   }

   protected boolean isValidGround(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.getBlock() == Blocks.FARMLAND;
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      super.tick(state, worldIn, pos, random);
      if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
      if (worldIn.getLightSubtracted(pos.up(), 0) >= 9) {
         float f = BlockCrops.getGrowthChance(this, worldIn, pos);
         if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0)) {
            int i = state.get(AGE);
            if (i < 7) {
               worldIn.setBlockState(pos, state.with(AGE, i + 1), 2);
            } else {
               EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(random);
               BlockPos blockpos = pos.offset(enumfacing);
               IBlockState soil = worldIn.getBlockState(blockpos.down());
               Block block = soil.getBlock();
               if (worldIn.getBlockState(blockpos).isAir(worldIn, blockpos) && (soil.canSustainPlant(worldIn, blockpos.down(), EnumFacing.UP, this) || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.GRASS_BLOCK)) {
                  worldIn.setBlockState(blockpos, this.crop.getDefaultState());
                  worldIn.setBlockState(pos, this.crop.getAttachedStem().getDefaultState().with(BlockHorizontal.HORIZONTAL_FACING, enumfacing));
               }
            }
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
         }

      }
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
      super.dropBlockAsItemWithChance(state, worldIn, pos, chancePerItem, fortune);
   }

   @Override
   public void getDrops(IBlockState state, net.minecraft.util.NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
      {
         Item item = this.getSeedItem();
         if (item != null) {
            int i = state.get(AGE);

            for(int j = 0; j < 3; ++j) {
               if (world.rand.nextInt(15) <= i) {
                  drops.add(new ItemStack(item));
               }
            }

         }
      }
   }

   @Nullable
   protected Item getSeedItem() {
      if (this.crop == Blocks.PUMPKIN) {
         return Items.PUMPKIN_SEEDS;
      } else {
         return this.crop == Blocks.MELON ? Items.MELON_SEEDS : null;
      }
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.AIR;
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      Item item = this.getSeedItem();
      return item == null ? ItemStack.EMPTY : new ItemStack(item);
   }

   /**
    * Whether this IGrowable can grow
    */
   public boolean canGrow(IBlockReader worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return state.get(AGE) != 7;
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      int i = Math.min(7, state.get(AGE) + MathHelper.nextInt(worldIn.rand, 2, 5));
      IBlockState iblockstate = state.with(AGE, Integer.valueOf(i));
      worldIn.setBlockState(pos, iblockstate, 2);
      if (i == 7) {
         iblockstate.tick(worldIn, pos, worldIn.rand);
      }

   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(AGE);
   }

   public BlockStemGrown getCrop() {
      return this.crop;
   }
}