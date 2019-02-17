package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Particles;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockWetSponge extends Block {
   protected BlockWetSponge(Block.Properties builder) {
      super(builder);
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      EnumFacing enumfacing = EnumFacing.random(rand);
      if (enumfacing != EnumFacing.UP && !worldIn.getBlockState(pos.offset(enumfacing)).isTopSolid()) {
         double d0 = (double)pos.getX();
         double d1 = (double)pos.getY();
         double d2 = (double)pos.getZ();
         if (enumfacing == EnumFacing.DOWN) {
            d1 = d1 - 0.05D;
            d0 += rand.nextDouble();
            d2 += rand.nextDouble();
         } else {
            d1 = d1 + rand.nextDouble() * 0.8D;
            if (enumfacing.getAxis() == EnumFacing.Axis.X) {
               d2 += rand.nextDouble();
               if (enumfacing == EnumFacing.EAST) {
                  ++d0;
               } else {
                  d0 += 0.05D;
               }
            } else {
               d0 += rand.nextDouble();
               if (enumfacing == EnumFacing.SOUTH) {
                  ++d2;
               } else {
                  d2 += 0.05D;
               }
            }
         }

         worldIn.spawnParticle(Particles.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
      }
   }
}