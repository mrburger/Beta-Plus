package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRailState {
   private final World world;
   private final BlockPos pos;
   private final BlockRailBase block;
   private IBlockState newState;
   private final boolean disableCorners;
   private final List<BlockPos> connectedRails = Lists.newArrayList();
   private final boolean canMakeSlopes;

   public BlockRailState(World p_i47755_1_, BlockPos p_i47755_2_, IBlockState p_i47755_3_) {
      this.world = p_i47755_1_;
      this.pos = p_i47755_2_;
      this.newState = p_i47755_3_;
      this.block = (BlockRailBase)p_i47755_3_.getBlock();
      RailShape railshape = this.block.getRailDirection(newState, p_i47755_1_, p_i47755_2_, null);
      this.disableCorners = this.block.isFlexibleRail(newState, p_i47755_1_, p_i47755_2_);
      this.canMakeSlopes = this.block.canMakeSlopes(newState, p_i47755_1_, p_i47755_2_);
      this.reset(railshape);
   }

   public List<BlockPos> getConnectedRails() {
      return this.connectedRails;
   }

   private void reset(RailShape p_208509_1_) {
      this.connectedRails.clear();
      switch(p_208509_1_) {
      case NORTH_SOUTH:
         this.connectedRails.add(this.pos.north());
         this.connectedRails.add(this.pos.south());
         break;
      case EAST_WEST:
         this.connectedRails.add(this.pos.west());
         this.connectedRails.add(this.pos.east());
         break;
      case ASCENDING_EAST:
         this.connectedRails.add(this.pos.west());
         this.connectedRails.add(this.pos.east().up());
         break;
      case ASCENDING_WEST:
         this.connectedRails.add(this.pos.west().up());
         this.connectedRails.add(this.pos.east());
         break;
      case ASCENDING_NORTH:
         this.connectedRails.add(this.pos.north().up());
         this.connectedRails.add(this.pos.south());
         break;
      case ASCENDING_SOUTH:
         this.connectedRails.add(this.pos.north());
         this.connectedRails.add(this.pos.south().up());
         break;
      case SOUTH_EAST:
         this.connectedRails.add(this.pos.east());
         this.connectedRails.add(this.pos.south());
         break;
      case SOUTH_WEST:
         this.connectedRails.add(this.pos.west());
         this.connectedRails.add(this.pos.south());
         break;
      case NORTH_WEST:
         this.connectedRails.add(this.pos.west());
         this.connectedRails.add(this.pos.north());
         break;
      case NORTH_EAST:
         this.connectedRails.add(this.pos.east());
         this.connectedRails.add(this.pos.north());
      }

   }

   private void checkConnected() {
      for(int i = 0; i < this.connectedRails.size(); ++i) {
         BlockRailState blockrailstate = this.createForAdjacent(this.connectedRails.get(i));
         if (blockrailstate != null && blockrailstate.isConnectedTo(this)) {
            this.connectedRails.set(i, blockrailstate.pos);
         } else {
            this.connectedRails.remove(i--);
         }
      }

   }

   private boolean isAdjacentRail(BlockPos p_196902_1_) {
      return BlockRailBase.isRail(this.world, p_196902_1_) || BlockRailBase.isRail(this.world, p_196902_1_.up()) || BlockRailBase.isRail(this.world, p_196902_1_.down());
   }

   @Nullable
   private BlockRailState createForAdjacent(BlockPos p_196908_1_) {
      IBlockState iblockstate = this.world.getBlockState(p_196908_1_);
      if (BlockRailBase.isRail(iblockstate)) {
         return new BlockRailState(this.world, p_196908_1_, iblockstate);
      } else {
         BlockPos lvt_2_1_ = p_196908_1_.up();
         iblockstate = this.world.getBlockState(lvt_2_1_);
         if (BlockRailBase.isRail(iblockstate)) {
            return new BlockRailState(this.world, lvt_2_1_, iblockstate);
         } else {
            lvt_2_1_ = p_196908_1_.down();
            iblockstate = this.world.getBlockState(lvt_2_1_);
            return BlockRailBase.isRail(iblockstate) ? new BlockRailState(this.world, lvt_2_1_, iblockstate) : null;
         }
      }
   }

   private boolean isConnectedTo(BlockRailState p_196919_1_) {
      return this.isConnectedTo(p_196919_1_.pos);
   }

   private boolean isConnectedTo(BlockPos p_196904_1_) {
      for(int i = 0; i < this.connectedRails.size(); ++i) {
         BlockPos blockpos = this.connectedRails.get(i);
         if (blockpos.getX() == p_196904_1_.getX() && blockpos.getZ() == p_196904_1_.getZ()) {
            return true;
         }
      }

      return false;
   }

   protected int countAdjacentRails() {
      int i = 0;

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         if (this.isAdjacentRail(this.pos.offset(enumfacing))) {
            ++i;
         }
      }

      return i;
   }

   private boolean func_196905_c(BlockRailState p_196905_1_) {
      return this.isConnectedTo(p_196905_1_) || this.connectedRails.size() != 2;
   }

   private void func_208510_c(BlockRailState p_208510_1_) {
      this.connectedRails.add(p_208510_1_.pos);
      BlockPos blockpos = this.pos.north();
      BlockPos blockpos1 = this.pos.south();
      BlockPos blockpos2 = this.pos.west();
      BlockPos blockpos3 = this.pos.east();
      boolean flag = this.isConnectedTo(blockpos);
      boolean flag1 = this.isConnectedTo(blockpos1);
      boolean flag2 = this.isConnectedTo(blockpos2);
      boolean flag3 = this.isConnectedTo(blockpos3);
      RailShape railshape = null;
      if (flag || flag1) {
         railshape = RailShape.NORTH_SOUTH;
      }

      if (flag2 || flag3) {
         railshape = RailShape.EAST_WEST;
      }

      if (!this.disableCorners) {
         if (flag1 && flag3 && !flag && !flag2) {
            railshape = RailShape.SOUTH_EAST;
         }

         if (flag1 && flag2 && !flag && !flag3) {
            railshape = RailShape.SOUTH_WEST;
         }

         if (flag && flag2 && !flag1 && !flag3) {
            railshape = RailShape.NORTH_WEST;
         }

         if (flag && flag3 && !flag1 && !flag2) {
            railshape = RailShape.NORTH_EAST;
         }
      }

      if (railshape == RailShape.NORTH_SOUTH && canMakeSlopes) {
         if (BlockRailBase.isRail(this.world, blockpos.up())) {
            railshape = RailShape.ASCENDING_NORTH;
         }

         if (BlockRailBase.isRail(this.world, blockpos1.up())) {
            railshape = RailShape.ASCENDING_SOUTH;
         }
      }

      if (railshape == RailShape.EAST_WEST && canMakeSlopes) {
         if (BlockRailBase.isRail(this.world, blockpos3.up())) {
            railshape = RailShape.ASCENDING_EAST;
         }

         if (BlockRailBase.isRail(this.world, blockpos2.up())) {
            railshape = RailShape.ASCENDING_WEST;
         }
      }

      if (railshape == null) {
         railshape = RailShape.NORTH_SOUTH;
      }

      this.newState = this.newState.with(this.block.getShapeProperty(), railshape);
      this.world.setBlockState(this.pos, this.newState, 3);
   }

   private boolean func_208512_d(BlockPos p_208512_1_) {
      BlockRailState blockrailstate = this.createForAdjacent(p_208512_1_);
      if (blockrailstate == null) {
         return false;
      } else {
         blockrailstate.checkConnected();
         return blockrailstate.func_196905_c(this);
      }
   }

   public BlockRailState update(boolean powered, boolean placing) {
      BlockPos blockpos = this.pos.north();
      BlockPos blockpos1 = this.pos.south();
      BlockPos blockpos2 = this.pos.west();
      BlockPos blockpos3 = this.pos.east();
      boolean flag = this.func_208512_d(blockpos);
      boolean flag1 = this.func_208512_d(blockpos1);
      boolean flag2 = this.func_208512_d(blockpos2);
      boolean flag3 = this.func_208512_d(blockpos3);
      RailShape railshape = null;
      if ((flag || flag1) && !flag2 && !flag3) {
         railshape = RailShape.NORTH_SOUTH;
      }

      if ((flag2 || flag3) && !flag && !flag1) {
         railshape = RailShape.EAST_WEST;
      }

      if (!this.disableCorners) {
         if (flag1 && flag3 && !flag && !flag2) {
            railshape = RailShape.SOUTH_EAST;
         }

         if (flag1 && flag2 && !flag && !flag3) {
            railshape = RailShape.SOUTH_WEST;
         }

         if (flag && flag2 && !flag1 && !flag3) {
            railshape = RailShape.NORTH_WEST;
         }

         if (flag && flag3 && !flag1 && !flag2) {
            railshape = RailShape.NORTH_EAST;
         }
      }

      if (railshape == null) {
         if (flag || flag1) {
            railshape = RailShape.NORTH_SOUTH;
         }

         if (flag2 || flag3) {
            railshape = RailShape.EAST_WEST;
         }

         if (!this.disableCorners) {
            if (powered) {
               if (flag1 && flag3) {
                  railshape = RailShape.SOUTH_EAST;
               }

               if (flag2 && flag1) {
                  railshape = RailShape.SOUTH_WEST;
               }

               if (flag3 && flag) {
                  railshape = RailShape.NORTH_EAST;
               }

               if (flag && flag2) {
                  railshape = RailShape.NORTH_WEST;
               }
            } else {
               if (flag && flag2) {
                  railshape = RailShape.NORTH_WEST;
               }

               if (flag3 && flag) {
                  railshape = RailShape.NORTH_EAST;
               }

               if (flag2 && flag1) {
                  railshape = RailShape.SOUTH_WEST;
               }

               if (flag1 && flag3) {
                  railshape = RailShape.SOUTH_EAST;
               }
            }
         }
      }

      if (railshape == RailShape.NORTH_SOUTH && canMakeSlopes) {
         if (BlockRailBase.isRail(this.world, blockpos.up())) {
            railshape = RailShape.ASCENDING_NORTH;
         }

         if (BlockRailBase.isRail(this.world, blockpos1.up())) {
            railshape = RailShape.ASCENDING_SOUTH;
         }
      }

      if (railshape == RailShape.EAST_WEST && canMakeSlopes) {
         if (BlockRailBase.isRail(this.world, blockpos3.up())) {
            railshape = RailShape.ASCENDING_EAST;
         }

         if (BlockRailBase.isRail(this.world, blockpos2.up())) {
            railshape = RailShape.ASCENDING_WEST;
         }
      }

      if (railshape == null) {
         railshape = RailShape.NORTH_SOUTH;
      }

      this.reset(railshape);
      this.newState = this.newState.with(this.block.getShapeProperty(), railshape);
      if (placing || this.world.getBlockState(this.pos) != this.newState) {
         this.world.setBlockState(this.pos, this.newState, 3);

         for(int i = 0; i < this.connectedRails.size(); ++i) {
            BlockRailState blockrailstate = this.createForAdjacent(this.connectedRails.get(i));
            if (blockrailstate != null) {
               blockrailstate.checkConnected();
               if (blockrailstate.func_196905_c(this)) {
                  blockrailstate.func_208510_c(this);
               }
            }
         }
      }

      return this;
   }

   public IBlockState getNewState() {
      return this.newState;
   }
}