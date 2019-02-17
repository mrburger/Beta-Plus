package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;

public class SwimNodeProcessor extends NodeProcessor {
   private final boolean field_205202_j;

   public SwimNodeProcessor(boolean p_i48927_1_) {
      this.field_205202_j = p_i48927_1_;
   }

   public PathPoint getStart() {
      return super.openPoint(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY + 0.5D), MathHelper.floor(this.entity.getBoundingBox().minZ));
   }

   /**
    * Returns PathPoint for given coordinates
    */
   public PathPoint getPathPointToCoords(double x, double y, double z) {
      return super.openPoint(MathHelper.floor(x - (double)(this.entity.width / 2.0F)), MathHelper.floor(y + 0.5D), MathHelper.floor(z - (double)(this.entity.width / 2.0F)));
   }

   public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
      int i = 0;

      for(EnumFacing enumfacing : EnumFacing.values()) {
         PathPoint pathpoint = this.getWaterNode(currentPoint.x + enumfacing.getXOffset(), currentPoint.y + enumfacing.getYOffset(), currentPoint.z + enumfacing.getZOffset());
         if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint;
         }
      }

      return i;
   }

   public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z, EntityLiving entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
      return this.getPathNodeType(blockaccessIn, x, y, z);
   }

   public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z) {
      BlockPos blockpos = new BlockPos(x, y, z);
      IFluidState ifluidstate = blockaccessIn.getFluidState(blockpos);
      IBlockState iblockstate = blockaccessIn.getBlockState(blockpos);
      if (ifluidstate.isEmpty() && iblockstate.allowsMovement(blockaccessIn, blockpos.down(), PathType.WATER) && iblockstate.isAir()) {
         return PathNodeType.BREACH;
      } else {
         return ifluidstate.isTagged(FluidTags.WATER) && iblockstate.allowsMovement(blockaccessIn, blockpos, PathType.WATER) ? PathNodeType.WATER : PathNodeType.BLOCKED;
      }
   }

   @Nullable
   private PathPoint getWaterNode(int p_186328_1_, int p_186328_2_, int p_186328_3_) {
      PathNodeType pathnodetype = this.isFree(p_186328_1_, p_186328_2_, p_186328_3_);
      return (!this.field_205202_j || pathnodetype != PathNodeType.BREACH) && pathnodetype != PathNodeType.WATER ? null : this.openPoint(p_186328_1_, p_186328_2_, p_186328_3_);
   }

   /**
    * Returns a mapped point or creates and adds one
    */
   @Nullable
   protected PathPoint openPoint(int x, int y, int z) {
      PathPoint pathpoint = null;
      PathNodeType pathnodetype = this.getPathNodeType(this.entity.world, x, y, z);
      float f = this.entity.getPathPriority(pathnodetype);
      if (f >= 0.0F) {
         pathpoint = super.openPoint(x, y, z);
         pathpoint.nodeType = pathnodetype;
         pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
         if (this.blockaccess.getFluidState(new BlockPos(x, y, z)).isEmpty()) {
            pathpoint.costMalus += 8.0F;
         }
      }

      return pathnodetype == PathNodeType.OPEN ? pathpoint : pathpoint;
   }

   private PathNodeType isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i = p_186327_1_; i < p_186327_1_ + this.entitySizeX; ++i) {
         for(int j = p_186327_2_; j < p_186327_2_ + this.entitySizeY; ++j) {
            for(int k = p_186327_3_; k < p_186327_3_ + this.entitySizeZ; ++k) {
               IFluidState ifluidstate = this.blockaccess.getFluidState(blockpos$mutableblockpos.setPos(i, j, k));
               IBlockState iblockstate = this.blockaccess.getBlockState(blockpos$mutableblockpos.setPos(i, j, k));
               if (ifluidstate.isEmpty() && iblockstate.allowsMovement(this.blockaccess, blockpos$mutableblockpos.down(), PathType.WATER) && iblockstate.isAir()) {
                  return PathNodeType.BREACH;
               }

               if (!ifluidstate.isTagged(FluidTags.WATER)) {
                  return PathNodeType.BLOCKED;
               }
            }
         }
      }

      IBlockState iblockstate1 = this.blockaccess.getBlockState(blockpos$mutableblockpos);
      if (iblockstate1.allowsMovement(this.blockaccess, blockpos$mutableblockpos, PathType.WATER)) {
         return PathNodeType.WATER;
      } else {
         return PathNodeType.BLOCKED;
      }
   }
}