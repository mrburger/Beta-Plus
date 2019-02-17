package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockRedstoneWire extends Block {
   public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.REDSTONE_NORTH;
   public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.REDSTONE_EAST;
   public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.REDSTONE_SOUTH;
   public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.REDSTONE_WEST;
   public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
   public static final Map<EnumFacing, EnumProperty<RedstoneSide>> FACING_PROPERTY_MAP = Maps.newEnumMap(ImmutableMap.of(EnumFacing.NORTH, NORTH, EnumFacing.EAST, EAST, EnumFacing.SOUTH, SOUTH, EnumFacing.WEST, WEST));
   protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};
   private boolean canProvidePower = true;
   /** List of blocks to update with redstone. */
   private final Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();

   public BlockRedstoneWire(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, RedstoneSide.NONE).with(EAST, RedstoneSide.NONE).with(SOUTH, RedstoneSide.NONE).with(WEST, RedstoneSide.NONE).with(POWER, Integer.valueOf(0)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPES[getAABBIndex(state)];
   }

   private static int getAABBIndex(IBlockState state) {
      int i = 0;
      boolean flag = state.get(NORTH) != RedstoneSide.NONE;
      boolean flag1 = state.get(EAST) != RedstoneSide.NONE;
      boolean flag2 = state.get(SOUTH) != RedstoneSide.NONE;
      boolean flag3 = state.get(WEST) != RedstoneSide.NONE;
      if (flag || flag2 && !flag && !flag1 && !flag3) {
         i |= 1 << EnumFacing.NORTH.getHorizontalIndex();
      }

      if (flag1 || flag3 && !flag && !flag1 && !flag2) {
         i |= 1 << EnumFacing.EAST.getHorizontalIndex();
      }

      if (flag2 || flag && !flag1 && !flag2 && !flag3) {
         i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
      }

      if (flag3 || flag1 && !flag && !flag2 && !flag3) {
         i |= 1 << EnumFacing.WEST.getHorizontalIndex();
      }

      return i;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockReader iblockreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      return this.getDefaultState().with(WEST, this.getSide(iblockreader, blockpos, EnumFacing.WEST)).with(EAST, this.getSide(iblockreader, blockpos, EnumFacing.EAST)).with(NORTH, this.getSide(iblockreader, blockpos, EnumFacing.NORTH)).with(SOUTH, this.getSide(iblockreader, blockpos, EnumFacing.SOUTH));
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    *  
    * @param facingState The state that is currently at the position offset of the provided face to the stateIn at
    * currentPos
    */
   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (facing == EnumFacing.DOWN) {
         return stateIn;
      } else {
         return facing == EnumFacing.UP ? stateIn.with(WEST, this.getSide(worldIn, currentPos, EnumFacing.WEST)).with(EAST, this.getSide(worldIn, currentPos, EnumFacing.EAST)).with(NORTH, this.getSide(worldIn, currentPos, EnumFacing.NORTH)).with(SOUTH, this.getSide(worldIn, currentPos, EnumFacing.SOUTH)) : stateIn.with(FACING_PROPERTY_MAP.get(facing), this.getSide(worldIn, currentPos, facing));
      }
   }

   /**
    * performs updates on diagonal neighbors of the target position and passes in the flags. The flags can be referenced
    * from the docs for {@link IWorldWriter#setBlockState(IBlockState, BlockPos, int)}.
    */
   public void updateDiagonalNeighbors(IBlockState state, IWorld worldIn, BlockPos pos, int flags) {
      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            RedstoneSide redstoneside = state.get(FACING_PROPERTY_MAP.get(enumfacing));
            if (redstoneside != RedstoneSide.NONE && worldIn.getBlockState(blockpos$pooledmutableblockpos.setPos(pos).move(enumfacing)).getBlock() != this) {
               blockpos$pooledmutableblockpos.move(EnumFacing.DOWN);
               IBlockState iblockstate = worldIn.getBlockState(blockpos$pooledmutableblockpos);
               if (iblockstate.getBlock() != Blocks.OBSERVER) {
                  BlockPos blockpos = blockpos$pooledmutableblockpos.offset(enumfacing.getOpposite());
                  IBlockState iblockstate1 = iblockstate.updatePostPlacement(enumfacing.getOpposite(), worldIn.getBlockState(blockpos), worldIn, blockpos$pooledmutableblockpos, blockpos);
                  replaceBlock(iblockstate, iblockstate1, worldIn, blockpos$pooledmutableblockpos, flags);
               }

               blockpos$pooledmutableblockpos.setPos(pos).move(enumfacing).move(EnumFacing.UP);
               IBlockState iblockstate3 = worldIn.getBlockState(blockpos$pooledmutableblockpos);
               if (iblockstate3.getBlock() != Blocks.OBSERVER) {
                  BlockPos blockpos1 = blockpos$pooledmutableblockpos.offset(enumfacing.getOpposite());
                  IBlockState iblockstate2 = iblockstate3.updatePostPlacement(enumfacing.getOpposite(), worldIn.getBlockState(blockpos1), worldIn, blockpos$pooledmutableblockpos, blockpos1);
                  replaceBlock(iblockstate3, iblockstate2, worldIn, blockpos$pooledmutableblockpos, flags);
               }
            }
         }
      }

   }

   private RedstoneSide getSide(IBlockReader worldIn, BlockPos pos, EnumFacing face) {
      BlockPos blockpos = pos.offset(face);
      IBlockState iblockstate = worldIn.getBlockState(pos.offset(face));
      IBlockState iblockstate1 = worldIn.getBlockState(pos.up());
      if (!iblockstate1.isNormalCube()) {
         boolean flag = iblockstate.getBlockFaceShape(worldIn, blockpos, EnumFacing.UP) == BlockFaceShape.SOLID || iblockstate.getBlock() == Blocks.GLOWSTONE;
         if (flag && canConnectTo(worldIn.getBlockState(blockpos.up()), worldIn, blockpos.up(), null)) {
            if (iblockstate.isBlockNormalCube()) {
               return RedstoneSide.UP;
            }

            return RedstoneSide.SIDE;
         }
      }

      return !canConnectTo(worldIn.getBlockState(blockpos), worldIn, blockpos, face) && (iblockstate.isNormalCube() || !canConnectTo(worldIn.getBlockState(blockpos.down()), worldIn, blockpos.down(), null)) ? RedstoneSide.NONE : RedstoneSide.SIDE;
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos.down());
      return iblockstate.isTopSolid() || iblockstate.getBlock() == Blocks.GLOWSTONE || iblockstate.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID;
   }

   private IBlockState updateSurroundingRedstone(World worldIn, BlockPos pos, IBlockState state) {
      state = this.func_212568_b(worldIn, pos, state);
      List<BlockPos> list = Lists.newArrayList(this.blocksNeedingUpdate);
      this.blocksNeedingUpdate.clear();

      for(BlockPos blockpos : list) {
         worldIn.notifyNeighborsOfStateChange(blockpos, this);
      }

      return state;
   }

   private IBlockState func_212568_b(World p_212568_1_, BlockPos p_212568_2_, IBlockState p_212568_3_) {
      IBlockState iblockstate = p_212568_3_;
      int i = p_212568_3_.get(POWER);
      int j = 0;
      j = this.func_212567_a(j, p_212568_3_);
      this.canProvidePower = false;
      int k = p_212568_1_.getRedstonePowerFromNeighbors(p_212568_2_);
      this.canProvidePower = true;
      if (k > 0 && k > j - 1) {
         j = k;
      }

      int l = 0;

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockpos = p_212568_2_.offset(enumfacing);
         boolean flag = blockpos.getX() != p_212568_2_.getX() || blockpos.getZ() != p_212568_2_.getZ();
         IBlockState iblockstate1 = p_212568_1_.getBlockState(blockpos);
         if (flag) {
            l = this.func_212567_a(l, iblockstate1);
         }

         if (iblockstate1.isNormalCube() && !p_212568_1_.getBlockState(p_212568_2_.up()).isNormalCube()) {
            if (flag && p_212568_2_.getY() >= p_212568_2_.getY()) {
               l = this.func_212567_a(l, p_212568_1_.getBlockState(blockpos.up()));
            }
         } else if (!iblockstate1.isNormalCube() && flag && p_212568_2_.getY() <= p_212568_2_.getY()) {
            l = this.func_212567_a(l, p_212568_1_.getBlockState(blockpos.down()));
         }
      }

      if (l > j) {
         j = l - 1;
      } else if (j > 0) {
         --j;
      } else {
         j = 0;
      }

      if (k > j - 1) {
         j = k;
      }

      if (i != j) {
         p_212568_3_ = p_212568_3_.with(POWER, Integer.valueOf(j));
         if (p_212568_1_.getBlockState(p_212568_2_) == iblockstate) {
            p_212568_1_.setBlockState(p_212568_2_, p_212568_3_, 2);
         }

         this.blocksNeedingUpdate.add(p_212568_2_);

         for(EnumFacing enumfacing1 : EnumFacing.values()) {
            this.blocksNeedingUpdate.add(p_212568_2_.offset(enumfacing1));
         }
      }

      return p_212568_3_;
   }

   /**
    * Calls World.notifyNeighborsOfStateChange() for all neighboring blocks, but only if the given block is a redstone
    * wire.
    */
   private void notifyWireNeighborsOfStateChange(World worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).getBlock() == this) {
         worldIn.notifyNeighborsOfStateChange(pos, this);

         for(EnumFacing enumfacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
         }

      }
   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      if (oldState.getBlock() != state.getBlock() && !worldIn.isRemote) {
         this.updateSurroundingRedstone(worldIn, pos, state);

         for(EnumFacing enumfacing : EnumFacing.Plane.VERTICAL) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
         }

         for(EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
            this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(enumfacing1));
         }

         for(EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.offset(enumfacing2);
            if (worldIn.getBlockState(blockpos).isNormalCube()) {
               this.notifyWireNeighborsOfStateChange(worldIn, blockpos.up());
            } else {
               this.notifyWireNeighborsOfStateChange(worldIn, blockpos.down());
            }
         }

      }
   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (!isMoving && state.getBlock() != newState.getBlock()) {
         super.onReplaced(state, worldIn, pos, newState, isMoving);
         if (!worldIn.isRemote) {
            for(EnumFacing enumfacing : EnumFacing.values()) {
               worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }

            this.updateSurroundingRedstone(worldIn, pos, state);

            for(EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
               this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(enumfacing1));
            }

            for(EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL) {
               BlockPos blockpos = pos.offset(enumfacing2);
               if (worldIn.getBlockState(blockpos).isNormalCube()) {
                  this.notifyWireNeighborsOfStateChange(worldIn, blockpos.up());
               } else {
                  this.notifyWireNeighborsOfStateChange(worldIn, blockpos.down());
               }
            }

         }
      }
   }

   private int func_212567_a(int p_212567_1_, IBlockState p_212567_2_) {
      if (p_212567_2_.getBlock() != this) {
         return p_212567_1_;
      } else {
         int i = p_212567_2_.get(POWER);
         return i > p_212567_1_ ? i : p_212567_1_;
      }
   }

   /**
    * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
    * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
    * block, etc.
    */
   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!worldIn.isRemote) {
         if (state.isValidPosition(worldIn, pos)) {
            this.updateSurroundingRedstone(worldIn, pos, state);
         } else {
            state.dropBlockAsItem(worldIn, pos, 0);
            worldIn.removeBlock(pos);
         }

      }
   }

   /**
    * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getStrongPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      return !this.canProvidePower ? 0 : blockState.getWeakPower(blockAccess, pos, side);
   }

   /**
    * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      if (!this.canProvidePower) {
         return 0;
      } else {
         int i = blockState.get(POWER);
         if (i == 0) {
            return 0;
         } else if (side == EnumFacing.UP) {
            return i;
         } else {
            EnumSet<EnumFacing> enumset = EnumSet.noneOf(EnumFacing.class);

            for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
               if (this.isPowerSourceAt(blockAccess, pos, enumfacing)) {
                  enumset.add(enumfacing);
               }
            }

            if (side.getAxis().isHorizontal() && enumset.isEmpty()) {
               return i;
            } else if (enumset.contains(side) && !enumset.contains(side.rotateYCCW()) && !enumset.contains(side.rotateY())) {
               return i;
            } else {
               return 0;
            }
         }
      }
   }

   private boolean isPowerSourceAt(IBlockReader worldIn, BlockPos pos, EnumFacing side) {
      BlockPos blockpos = pos.offset(side);
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      boolean flag = iblockstate.isNormalCube();
      boolean flag1 = worldIn.getBlockState(pos.up()).isNormalCube();
      if (!flag1 && flag && canConnectTo(worldIn.getBlockState(blockpos.up()), worldIn, blockpos.up(), null)) {
         return true;
      } else if (canConnectTo(iblockstate, worldIn, blockpos, side)) {
         return true;
      } else if (iblockstate.getBlock() == Blocks.REPEATER && iblockstate.get(BlockRedstoneDiode.POWERED) && iblockstate.get(BlockRedstoneDiode.HORIZONTAL_FACING) == side) {
         return true;
      } else {
         return !flag && canConnectTo(worldIn.getBlockState(blockpos.down()), worldIn, blockpos.down(), null);
      }
   }

   protected boolean canConnectTo(IBlockState blockState, IBlockReader world, BlockPos pos, @Nullable EnumFacing side) {
      Block block = blockState.getBlock();
      if (block == Blocks.REDSTONE_WIRE) {
         return true;
      } else if (blockState.getBlock() == Blocks.REPEATER) {
         EnumFacing enumfacing = blockState.get(BlockRedstoneRepeater.HORIZONTAL_FACING);
         return enumfacing == side || enumfacing.getOpposite() == side;
      } else if (Blocks.OBSERVER == blockState.getBlock()) {
         return side == blockState.get(BlockObserver.FACING);
      } else {
         return blockState.canConnectRedstone(world, pos, side) && side != null;
      }
   }

   /**
    * Can this block provide power. Only wire currently seems to have this change based on its state.
    * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
    */
   public boolean canProvidePower(IBlockState state) {
      return this.canProvidePower;
   }

   @OnlyIn(Dist.CLIENT)
   public static int colorMultiplier(int p_176337_0_) {
      float f = (float)p_176337_0_ / 15.0F;
      float f1 = f * 0.6F + 0.4F;
      if (p_176337_0_ == 0) {
         f1 = 0.3F;
      }

      float f2 = f * f * 0.7F - 0.5F;
      float f3 = f * f * 0.6F - 0.7F;
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      int i = MathHelper.clamp((int)(f1 * 255.0F), 0, 255);
      int j = MathHelper.clamp((int)(f2 * 255.0F), 0, 255);
      int k = MathHelper.clamp((int)(f3 * 255.0F), 0, 255);
      return -16777216 | i << 16 | j << 8 | k;
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      int i = stateIn.get(POWER);
      if (i != 0) {
         double d0 = (double)pos.getX() + 0.5D + ((double)rand.nextFloat() - 0.5D) * 0.2D;
         double d1 = (double)((float)pos.getY() + 0.0625F);
         double d2 = (double)pos.getZ() + 0.5D + ((double)rand.nextFloat() - 0.5D) * 0.2D;
         float f = (float)i / 15.0F;
         float f1 = f * 0.6F + 0.4F;
         float f2 = Math.max(0.0F, f * f * 0.7F - 0.5F);
         float f3 = Math.max(0.0F, f * f * 0.6F - 0.7F);
         worldIn.spawnParticle(new RedstoneParticleData(f1, f2, f3, 1.0F), d0, d1, d2, 0.0D, 0.0D, 0.0D);
      }
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public IBlockState rotate(IBlockState state, Rotation rot) {
      switch(rot) {
      case CLOCKWISE_180:
         return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
      case COUNTERCLOCKWISE_90:
         return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
      case CLOCKWISE_90:
         return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
      default:
         return state;
      }
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      switch(mirrorIn) {
      case LEFT_RIGHT:
         return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
      case FRONT_BACK:
         return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
      default:
         return super.mirror(state, mirrorIn);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(NORTH, EAST, SOUTH, WEST, POWER);
   }

   /**
    * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
    * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
    * <p>
    * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that does
    * not fit the other descriptions and will generally cause other things not to connect to the face.
    * 
    * @return an approximation of the form of the given face
    * @deprecated call via {@link IBlockState#getBlockFaceShape(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}