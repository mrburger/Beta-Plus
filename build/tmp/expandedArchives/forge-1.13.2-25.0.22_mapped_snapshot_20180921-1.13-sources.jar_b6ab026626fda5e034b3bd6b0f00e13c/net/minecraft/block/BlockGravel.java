package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockGravel extends BlockFalling {
   public BlockGravel(Block.Properties builder) {
      super(builder);
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      if (fortune > 3) {
         fortune = 3;
      }

      return (IItemProvider)(worldIn.rand.nextInt(10 - fortune * 3) == 0 ? Items.FLINT : super.getItemDropped(state, worldIn, pos, fortune));
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(IBlockState state) {
      return -8356741;
   }
}