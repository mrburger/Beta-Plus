package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class EndGatewayFeature extends Feature<EndGatewayConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, EndGatewayConfig p_212245_5_) {
      for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(p_212245_4_.add(-1, -2, -1), p_212245_4_.add(1, 2, 1))) {
         boolean flag = blockpos$mutableblockpos.getX() == p_212245_4_.getX();
         boolean flag1 = blockpos$mutableblockpos.getY() == p_212245_4_.getY();
         boolean flag2 = blockpos$mutableblockpos.getZ() == p_212245_4_.getZ();
         boolean flag3 = Math.abs(blockpos$mutableblockpos.getY() - p_212245_4_.getY()) == 2;
         if (flag && flag1 && flag2) {
            BlockPos blockpos = blockpos$mutableblockpos.toImmutable();
            this.setBlockState(p_212245_1_, blockpos, Blocks.END_GATEWAY.getDefaultState());
            if (p_212245_5_.func_209959_a()) {
               TileEntity tileentity = p_212245_1_.getTileEntity(blockpos);
               if (tileentity instanceof TileEntityEndGateway) {
                  TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway)tileentity;
                  tileentityendgateway.setExitPortal(EndDimension.field_209958_g);
               }
            }
         } else if (flag1) {
            this.setBlockState(p_212245_1_, blockpos$mutableblockpos, Blocks.AIR.getDefaultState());
         } else if (flag3 && flag && flag2) {
            this.setBlockState(p_212245_1_, blockpos$mutableblockpos, Blocks.BEDROCK.getDefaultState());
         } else if ((flag || flag2) && !flag3) {
            this.setBlockState(p_212245_1_, blockpos$mutableblockpos, Blocks.BEDROCK.getDefaultState());
         } else {
            this.setBlockState(p_212245_1_, blockpos$mutableblockpos, Blocks.AIR.getDefaultState());
         }
      }

      return true;
   }
}