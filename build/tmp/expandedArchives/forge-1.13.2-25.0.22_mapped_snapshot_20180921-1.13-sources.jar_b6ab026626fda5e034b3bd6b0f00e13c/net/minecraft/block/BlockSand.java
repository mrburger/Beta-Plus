package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockSand extends BlockFalling {
   private final int dustColor;

   public BlockSand(int p_i48338_1_, Block.Properties p_i48338_2_) {
      super(p_i48338_2_);
      this.dustColor = p_i48338_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(IBlockState state) {
      return this.dustColor;
   }
}