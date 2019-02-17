package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockTorchWall;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class EndPodiumFeature extends Feature<NoFeatureConfig> {
   public static final BlockPos END_PODIUM_LOCATION = BlockPos.ORIGIN;
   private final boolean activePortal;

   public EndPodiumFeature(boolean activePortalIn) {
      this.activePortal = activePortalIn;
   }

   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(new BlockPos(p_212245_4_.getX() - 4, p_212245_4_.getY() - 1, p_212245_4_.getZ() - 4), new BlockPos(p_212245_4_.getX() + 4, p_212245_4_.getY() + 32, p_212245_4_.getZ() + 4))) {
         double d0 = blockpos$mutableblockpos.getDistance(p_212245_4_.getX(), blockpos$mutableblockpos.getY(), p_212245_4_.getZ());
         if (d0 <= 3.5D) {
            if (blockpos$mutableblockpos.getY() < p_212245_4_.getY()) {
               if (d0 <= 2.5D) {
                  this.setBlockState(p_212245_1_, blockpos$mutableblockpos, Blocks.BEDROCK.getDefaultState());
               } else if (blockpos$mutableblockpos.getY() < p_212245_4_.getY()) {
                  this.setBlockState(p_212245_1_, blockpos$mutableblockpos, Blocks.END_STONE.getDefaultState());
               }
            } else if (blockpos$mutableblockpos.getY() > p_212245_4_.getY()) {
               this.setBlockState(p_212245_1_, blockpos$mutableblockpos, Blocks.AIR.getDefaultState());
            } else if (d0 > 2.5D) {
               this.setBlockState(p_212245_1_, blockpos$mutableblockpos, Blocks.BEDROCK.getDefaultState());
            } else if (this.activePortal) {
               this.setBlockState(p_212245_1_, new BlockPos(blockpos$mutableblockpos), Blocks.END_PORTAL.getDefaultState());
            } else {
               this.setBlockState(p_212245_1_, new BlockPos(blockpos$mutableblockpos), Blocks.AIR.getDefaultState());
            }
         }
      }

      for(int i = 0; i < 4; ++i) {
         this.setBlockState(p_212245_1_, p_212245_4_.up(i), Blocks.BEDROCK.getDefaultState());
      }

      BlockPos blockpos = p_212245_4_.up(2);

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         this.setBlockState(p_212245_1_, blockpos.offset(enumfacing), Blocks.WALL_TORCH.getDefaultState().with(BlockTorchWall.HORIZONTAL_FACING, enumfacing));
      }

      return true;
   }
}