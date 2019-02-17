package net.minecraft.block;

import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockStairs extends Block implements IBucketPickupHandler, ILiquidContainer {
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
   public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   /**
    * B: .. T: xx
    * B: .. T: xx
    */
   protected static final VoxelShape AABB_SLAB_TOP = BlockSlab.TOP_SHAPE;
   /**
    * B: xx T: ..
    * B: xx T: ..
    */
   protected static final VoxelShape AABB_SLAB_BOTTOM = BlockSlab.BOTTOM_SHAPE;
   protected static final VoxelShape field_196512_A = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
   protected static final VoxelShape field_196513_B = Block.makeCuboidShape(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
   protected static final VoxelShape field_196514_C = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
   protected static final VoxelShape field_196515_D = Block.makeCuboidShape(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
   protected static final VoxelShape field_196516_E = Block.makeCuboidShape(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
   protected static final VoxelShape field_196517_F = Block.makeCuboidShape(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
   protected static final VoxelShape field_196518_G = Block.makeCuboidShape(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
   protected static final VoxelShape field_196519_H = Block.makeCuboidShape(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape[] field_196520_I = func_199779_a(AABB_SLAB_TOP, field_196512_A, field_196516_E, field_196513_B, field_196517_F);
   protected static final VoxelShape[] field_196521_J = func_199779_a(AABB_SLAB_BOTTOM, field_196514_C, field_196518_G, field_196515_D, field_196519_H);
   private static final int[] field_196522_K = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
   private final Block modelBlock;
   private final IBlockState modelState;

   private static VoxelShape[] func_199779_a(VoxelShape p_199779_0_, VoxelShape p_199779_1_, VoxelShape p_199779_2_, VoxelShape p_199779_3_, VoxelShape p_199779_4_) {
      return IntStream.range(0, 16).mapToObj((p_199780_5_) -> {
         return func_199781_a(p_199780_5_, p_199779_0_, p_199779_1_, p_199779_2_, p_199779_3_, p_199779_4_);
      }).toArray((p_199778_0_) -> {
         return new VoxelShape[p_199778_0_];
      });
   }

   private static VoxelShape func_199781_a(int p_199781_0_, VoxelShape p_199781_1_, VoxelShape p_199781_2_, VoxelShape p_199781_3_, VoxelShape p_199781_4_, VoxelShape p_199781_5_) {
      VoxelShape voxelshape = p_199781_1_;
      if ((p_199781_0_ & 1) != 0) {
         voxelshape = VoxelShapes.or(p_199781_1_, p_199781_2_);
      }

      if ((p_199781_0_ & 2) != 0) {
         voxelshape = VoxelShapes.or(voxelshape, p_199781_3_);
      }

      if ((p_199781_0_ & 4) != 0) {
         voxelshape = VoxelShapes.or(voxelshape, p_199781_4_);
      }

      if ((p_199781_0_ & 8) != 0) {
         voxelshape = VoxelShapes.or(voxelshape, p_199781_5_);
      }

      return voxelshape;
   }

   protected BlockStairs(IBlockState p_i48321_1_, Block.Properties p_i48321_2_) {
      super(p_i48321_2_);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(HALF, Half.BOTTOM).with(SHAPE, StairsShape.STRAIGHT).with(WATERLOGGED, Boolean.valueOf(false)));
      this.modelBlock = p_i48321_1_.getBlock();
      this.modelState = p_i48321_1_;
   }

   public int getOpacity(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return worldIn.getMaxLightLevel();
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return (state.get(HALF) == Half.TOP ? field_196520_I : field_196521_J)[field_196522_K[this.func_196511_x(state)]];
   }

   private int func_196511_x(IBlockState p_196511_1_) {
      return p_196511_1_.get(SHAPE).ordinal() * 4 + p_196511_1_.get(FACING).getHorizontalIndex();
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
      if (face.getAxis() == EnumFacing.Axis.Y) {
         return face == EnumFacing.UP == (state.get(HALF) == Half.TOP) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
      } else {
         StairsShape stairsshape = state.get(SHAPE);
         if (stairsshape != StairsShape.OUTER_LEFT && stairsshape != StairsShape.OUTER_RIGHT) {
            EnumFacing enumfacing = state.get(FACING);
            switch(stairsshape) {
            case STRAIGHT:
               return enumfacing == face ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
            case INNER_LEFT:
               return enumfacing != face && enumfacing != face.rotateY() ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
            case INNER_RIGHT:
               return enumfacing != face && enumfacing != face.rotateYCCW() ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
            default:
               return BlockFaceShape.UNDEFINED;
            }
         } else {
            return BlockFaceShape.UNDEFINED;
         }
      }
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      this.modelBlock.animateTick(stateIn, worldIn, pos, rand);
   }

   public void onBlockClicked(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) {
      this.modelState.onBlockClicked(worldIn, pos, player);
   }

   /**
    * Called after a player destroys this Block - the posiiton pos may no longer hold the state indicated.
    */
   public void onPlayerDestroy(IWorld worldIn, BlockPos pos, IBlockState state) {
      this.modelBlock.onPlayerDestroy(worldIn, pos, state);
   }

   /**
    * @deprecated call via {@link IBlockState#getPackedLightmapCoords(IBlockAccess,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   @OnlyIn(Dist.CLIENT)
   public int getPackedLightmapCoords(IBlockState state, IWorldReader source, BlockPos pos) {
      return this.modelState.getPackedLightmapCoords(source, pos);
   }

   /**
    * Returns how much this block can resist explosions from the passed in entity.
    */
   public float getExplosionResistance() {
      return this.modelBlock.getExplosionResistance();
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return this.modelBlock.getRenderLayer();
   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReaderBase worldIn) {
      return this.modelBlock.tickRate(worldIn);
   }

   /**
    * Returns if this block is collidable. Only used by fire, although stairs return that of the block that the stair is
    * made of (though nobody's going to make fire stairs, right?)
    */
   public boolean isCollidable() {
      return this.modelBlock.isCollidable();
   }

   public boolean isCollidable(IBlockState state) {
      return this.modelBlock.isCollidable(state);
   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      if (state.getBlock() != state.getBlock()) {
         this.modelState.neighborChanged(worldIn, pos, Blocks.AIR, pos);
         this.modelBlock.onBlockAdded(this.modelState, worldIn, pos, oldState);
      }
   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         this.modelState.onReplaced(worldIn, pos, newState, isMoving);
      }
   }

   /**
    * Called when the given entity walks on this Block
    */
   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
      this.modelBlock.onEntityWalk(worldIn, pos, entityIn);
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      this.modelBlock.tick(state, worldIn, pos, random);
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      return this.modelState.onBlockActivated(worldIn, pos, player, hand, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
   }

   /**
    * Called when this Block is destroyed by an Explosion
    */
   public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
      this.modelBlock.onExplosionDestroy(worldIn, pos, explosionIn);
   }

   /**
    * Determines if the block is solid enough on the top side to support other blocks, like redstone components.
    * @deprecated prefer calling {@link IBlockState#isTopSolid()} wherever possible
    */
   public boolean isTopSolid(IBlockState state) {
      return state.get(HALF) == Half.TOP;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      EnumFacing enumfacing = context.getFace();
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      IBlockState iblockstate = this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing()).with(HALF, enumfacing != EnumFacing.DOWN && (enumfacing == EnumFacing.UP || !((double)context.getHitY() > 0.5D)) ? Half.BOTTOM : Half.TOP).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
      return iblockstate.with(SHAPE, func_208064_n(iblockstate, context.getWorld(), context.getPos()));
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
      if (stateIn.get(WATERLOGGED)) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return facing.getAxis().isHorizontal() ? stateIn.with(SHAPE, func_208064_n(stateIn, worldIn, currentPos)) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   private static StairsShape func_208064_n(IBlockState p_208064_0_, IBlockReader p_208064_1_, BlockPos p_208064_2_) {
      EnumFacing enumfacing = p_208064_0_.get(FACING);
      IBlockState iblockstate = p_208064_1_.getBlockState(p_208064_2_.offset(enumfacing));
      if (isBlockStairs(iblockstate) && p_208064_0_.get(HALF) == iblockstate.get(HALF)) {
         EnumFacing enumfacing1 = iblockstate.get(FACING);
         if (enumfacing1.getAxis() != p_208064_0_.get(FACING).getAxis() && isDifferentStairs(p_208064_0_, p_208064_1_, p_208064_2_, enumfacing1.getOpposite())) {
            if (enumfacing1 == enumfacing.rotateYCCW()) {
               return StairsShape.OUTER_LEFT;
            }

            return StairsShape.OUTER_RIGHT;
         }
      }

      IBlockState iblockstate1 = p_208064_1_.getBlockState(p_208064_2_.offset(enumfacing.getOpposite()));
      if (isBlockStairs(iblockstate1) && p_208064_0_.get(HALF) == iblockstate1.get(HALF)) {
         EnumFacing enumfacing2 = iblockstate1.get(FACING);
         if (enumfacing2.getAxis() != p_208064_0_.get(FACING).getAxis() && isDifferentStairs(p_208064_0_, p_208064_1_, p_208064_2_, enumfacing2)) {
            if (enumfacing2 == enumfacing.rotateYCCW()) {
               return StairsShape.INNER_LEFT;
            }

            return StairsShape.INNER_RIGHT;
         }
      }

      return StairsShape.STRAIGHT;
   }

   private static boolean isDifferentStairs(IBlockState p_185704_0_, IBlockReader p_185704_1_, BlockPos p_185704_2_, EnumFacing p_185704_3_) {
      IBlockState iblockstate = p_185704_1_.getBlockState(p_185704_2_.offset(p_185704_3_));
      return !isBlockStairs(iblockstate) || iblockstate.get(FACING) != p_185704_0_.get(FACING) || iblockstate.get(HALF) != p_185704_0_.get(HALF);
   }

   public static boolean isBlockStairs(IBlockState state) {
      return state.getBlock() instanceof BlockStairs;
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public IBlockState rotate(IBlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      EnumFacing enumfacing = state.get(FACING);
      StairsShape stairsshape = state.get(SHAPE);
      switch(mirrorIn) {
      case LEFT_RIGHT:
         if (enumfacing.getAxis() == EnumFacing.Axis.Z) {
            switch(stairsshape) {
            case INNER_LEFT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_RIGHT);
            case INNER_RIGHT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_LEFT);
            case OUTER_LEFT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_LEFT);
            default:
               return state.rotate(Rotation.CLOCKWISE_180);
            }
         }
         break;
      case FRONT_BACK:
         if (enumfacing.getAxis() == EnumFacing.Axis.X) {
            switch(stairsshape) {
            case STRAIGHT:
               return state.rotate(Rotation.CLOCKWISE_180);
            case INNER_LEFT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_LEFT);
            case INNER_RIGHT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_RIGHT);
            case OUTER_LEFT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_LEFT);
            }
         }
      }

      return super.mirror(state, mirrorIn);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(FACING, HALF, SHAPE, WATERLOGGED);
   }

   public Fluid pickupFluid(IWorld worldIn, BlockPos pos, IBlockState state) {
      if (state.get(WATERLOGGED)) {
         worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(false)), 3);
         return Fluids.WATER;
      } else {
         return Fluids.EMPTY;
      }
   }

   public IFluidState getFluidState(IBlockState state) {
      return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
   }

   public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, IBlockState state, Fluid fluidIn) {
      return !state.get(WATERLOGGED) && fluidIn == Fluids.WATER;
   }

   public boolean receiveFluid(IWorld worldIn, BlockPos pos, IBlockState state, IFluidState fluidStateIn) {
      if (!state.get(WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER) {
         if (!worldIn.isRemote()) {
            worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(true)), 3);
            worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}