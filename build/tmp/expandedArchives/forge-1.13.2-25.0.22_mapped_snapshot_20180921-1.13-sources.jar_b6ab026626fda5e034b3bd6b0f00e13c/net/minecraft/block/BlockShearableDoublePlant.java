package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockShearableDoublePlant extends BlockDoublePlant implements net.minecraftforge.common.IShearable {
   public static final EnumProperty<DoubleBlockHalf> field_208063_b = BlockDoublePlant.HALF;
   private final Block field_196392_b;

   public BlockShearableDoublePlant(Block p_i48335_1_, Block.Properties p_i48335_2_) {
      super(p_i48335_2_);
      this.field_196392_b = p_i48335_1_;
   }

   public boolean isReplaceable(IBlockState state, BlockItemUseContext useContext) {
      boolean flag = super.isReplaceable(state, useContext);
      return flag && useContext.getItem().getItem() == this.asItem() ? false : flag;
   }

   protected void harvest(IBlockState p_196391_1_, World p_196391_2_, BlockPos p_196391_3_, ItemStack p_196391_4_) {
      {
         super.harvest(p_196391_1_, p_196391_2_, p_196391_3_, p_196391_4_);
      }

   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return state.get(field_208063_b) == DoubleBlockHalf.LOWER && this == Blocks.TALL_GRASS && worldIn.rand.nextInt(8) == 0 ? Items.WHEAT_SEEDS : Items.AIR;
   }

   @Override
   public boolean isShearable(ItemStack item, net.minecraft.world.IWorldReader world, BlockPos pos) {
      return false;
   }

   @Override
   public java.util.List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IWorld world, BlockPos pos, int fortune) {
      world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
      return java.util.Arrays.asList(new ItemStack(this.field_196392_b, 2));
   }
}