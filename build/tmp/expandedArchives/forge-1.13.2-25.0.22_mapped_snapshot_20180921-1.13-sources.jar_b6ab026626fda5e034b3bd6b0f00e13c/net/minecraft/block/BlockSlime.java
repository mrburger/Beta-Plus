package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockSlime extends BlockBreakable {
   public BlockSlime(Block.Properties builder) {
      super(builder);
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   /**
    * Block's chance to react to a living entity falling on it.
    */
   public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
      if (entityIn.isSneaking()) {
         super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
      } else {
         entityIn.fall(fallDistance, 0.0F);
      }

   }

   /**
    * Called when an Entity lands on this Block. This method *must* update motionY because the entity will not do that
    * on its own
    */
   public void onLanded(IBlockReader worldIn, Entity entityIn) {
      if (entityIn.isSneaking()) {
         super.onLanded(worldIn, entityIn);
      } else if (entityIn.motionY < 0.0D) {
         entityIn.motionY = -entityIn.motionY;
         if (!(entityIn instanceof EntityLivingBase)) {
            entityIn.motionY *= 0.8D;
         }
      }

   }

   /**
    * Called when the given entity walks on this Block
    */
   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
      if (Math.abs(entityIn.motionY) < 0.1D && !entityIn.isSneaking()) {
         double d0 = 0.4D + Math.abs(entityIn.motionY) * 0.2D;
         entityIn.motionX *= d0;
         entityIn.motionZ *= d0;
      }

      super.onEntityWalk(worldIn, pos, entityIn);
   }

   public int getOpacity(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return 0;
   }
}