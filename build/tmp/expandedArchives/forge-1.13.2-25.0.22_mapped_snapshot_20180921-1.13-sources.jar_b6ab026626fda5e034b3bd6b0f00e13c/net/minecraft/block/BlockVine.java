package net.minecraft.block;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.StatList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockVine extends Block implements net.minecraftforge.common.IShearable {
   public static final BooleanProperty UP = BlockSixWay.UP;
   public static final BooleanProperty NORTH = BlockSixWay.NORTH;
   public static final BooleanProperty EAST = BlockSixWay.EAST;
   public static final BooleanProperty SOUTH = BlockSixWay.SOUTH;
   public static final BooleanProperty WEST = BlockSixWay.WEST;
   public static final Map<EnumFacing, BooleanProperty> field_196546_A = BlockSixWay.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((p_199782_0_) -> {
      return p_199782_0_.getKey() != EnumFacing.DOWN;
   }).collect(Util.toMapCollector());
   protected static final VoxelShape UP_AABB = Block.makeCuboidShape(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
   protected static final VoxelShape EAST_AABB = Block.makeCuboidShape(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
   protected static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);

   public BlockVine(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(UP, Boolean.valueOf(false)).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      VoxelShape voxelshape = VoxelShapes.empty();
      if (state.get(UP)) {
         voxelshape = VoxelShapes.or(voxelshape, UP_AABB);
      }

      if (state.get(NORTH)) {
         voxelshape = VoxelShapes.or(voxelshape, NORTH_AABB);
      }

      if (state.get(EAST)) {
         voxelshape = VoxelShapes.or(voxelshape, EAST_AABB);
      }

      if (state.get(SOUTH)) {
         voxelshape = VoxelShapes.or(voxelshape, SOUTH_AABB);
      }

      if (state.get(WEST)) {
         voxelshape = VoxelShapes.or(voxelshape, WEST_AABB);
      }

      return voxelshape;
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      return this.func_196543_i(this.func_196545_h(state, worldIn, pos));
   }

   private boolean func_196543_i(IBlockState p_196543_1_) {
      return this.func_208496_w(p_196543_1_) > 0;
   }

   private int func_208496_w(IBlockState p_208496_1_) {
      int i = 0;

      for(BooleanProperty booleanproperty : field_196546_A.values()) {
         if (p_208496_1_.get(booleanproperty)) {
            ++i;
         }
      }

      return i;
   }

   private boolean func_196541_a(IBlockReader p_196541_1_, BlockPos p_196541_2_, EnumFacing p_196541_3_) {
      if (p_196541_3_ == EnumFacing.DOWN) {
         return false;
      } else {
         BlockPos blockpos = p_196541_2_.offset(p_196541_3_);
         if (this.func_196542_b(p_196541_1_, blockpos, p_196541_3_)) {
            return true;
         } else if (p_196541_3_.getAxis() == EnumFacing.Axis.Y) {
            return false;
         } else {
            BooleanProperty booleanproperty = field_196546_A.get(p_196541_3_);
            IBlockState iblockstate = p_196541_1_.getBlockState(p_196541_2_.up());
            return iblockstate.getBlock() == this && iblockstate.get(booleanproperty);
         }
      }
   }

   private boolean func_196542_b(IBlockReader p_196542_1_, BlockPos p_196542_2_, EnumFacing p_196542_3_) {
      IBlockState iblockstate = p_196542_1_.getBlockState(p_196542_2_);
      return iblockstate.getBlockFaceShape(p_196542_1_, p_196542_2_, p_196542_3_.getOpposite()) == BlockFaceShape.SOLID && !isExceptBlockForAttaching(iblockstate.getBlock());
   }

   protected static boolean isExceptBlockForAttaching(Block p_193397_0_) {
      return p_193397_0_ instanceof BlockShulkerBox || p_193397_0_ instanceof BlockStainedGlass || p_193397_0_ == Blocks.BEACON || p_193397_0_ == Blocks.CAULDRON || p_193397_0_ == Blocks.GLASS || p_193397_0_ == Blocks.PISTON || p_193397_0_ == Blocks.STICKY_PISTON || p_193397_0_ == Blocks.PISTON_HEAD || p_193397_0_.isIn(BlockTags.WOODEN_TRAPDOORS);
   }

   private IBlockState func_196545_h(IBlockState p_196545_1_, IBlockReader p_196545_2_, BlockPos p_196545_3_) {
      BlockPos blockpos = p_196545_3_.up();
      if (p_196545_1_.get(UP)) {
         p_196545_1_ = p_196545_1_.with(UP, Boolean.valueOf(this.func_196542_b(p_196545_2_, blockpos, EnumFacing.DOWN)));
      }

      IBlockState iblockstate = null;

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         BooleanProperty booleanproperty = getPropertyFor(enumfacing);
         if (p_196545_1_.get(booleanproperty)) {
            boolean flag = this.func_196541_a(p_196545_2_, p_196545_3_, enumfacing);
            if (!flag) {
               if (iblockstate == null) {
                  iblockstate = p_196545_2_.getBlockState(blockpos);
               }

               flag = iblockstate.getBlock() == this && iblockstate.get(booleanproperty);
            }

            p_196545_1_ = p_196545_1_.with(booleanproperty, Boolean.valueOf(flag));
         }
      }

      return p_196545_1_;
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
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      } else {
         IBlockState iblockstate = this.func_196545_h(stateIn, worldIn, currentPos);
         return !this.func_196543_i(iblockstate) ? Blocks.AIR.getDefaultState() : iblockstate;
      }
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (!worldIn.isRemote) {
         IBlockState iblockstate = this.func_196545_h(state, worldIn, pos);
         if (iblockstate != state) {
            if (this.func_196543_i(iblockstate)) {
               worldIn.setBlockState(pos, iblockstate, 2);
            } else {
               state.dropBlockAsItem(worldIn, pos, 0);
               worldIn.removeBlock(pos);
            }

         } else if (worldIn.rand.nextInt(4) == 0 && worldIn.isAreaLoaded(pos, 4)) { // Forge: check area to prevent loading unloaded chunks
            EnumFacing enumfacing = EnumFacing.random(random);
            BlockPos blockpos = pos.up();
            if (enumfacing.getAxis().isHorizontal() && !state.get(getPropertyFor(enumfacing))) {
               if (this.func_196539_a(worldIn, pos)) {
                  BlockPos blockpos4 = pos.offset(enumfacing);
                  IBlockState iblockstate5 = worldIn.getBlockState(blockpos4);
                  if (iblockstate5.isAir()) {
                     EnumFacing enumfacing3 = enumfacing.rotateY();
                     EnumFacing enumfacing4 = enumfacing.rotateYCCW();
                     boolean flag = state.get(getPropertyFor(enumfacing3));
                     boolean flag1 = state.get(getPropertyFor(enumfacing4));
                     BlockPos blockpos2 = blockpos4.offset(enumfacing3);
                     BlockPos blockpos3 = blockpos4.offset(enumfacing4);
                     if (flag && this.func_196542_b(worldIn, blockpos2, enumfacing3)) {
                        worldIn.setBlockState(blockpos4, this.getDefaultState().with(getPropertyFor(enumfacing3), Boolean.valueOf(true)), 2);
                     } else if (flag1 && this.func_196542_b(worldIn, blockpos3, enumfacing4)) {
                        worldIn.setBlockState(blockpos4, this.getDefaultState().with(getPropertyFor(enumfacing4), Boolean.valueOf(true)), 2);
                     } else {
                        EnumFacing enumfacing1 = enumfacing.getOpposite();
                        if (flag && worldIn.isAirBlock(blockpos2) && this.func_196542_b(worldIn, pos.offset(enumfacing3), enumfacing1)) {
                           worldIn.setBlockState(blockpos2, this.getDefaultState().with(getPropertyFor(enumfacing1), Boolean.valueOf(true)), 2);
                        } else if (flag1 && worldIn.isAirBlock(blockpos3) && this.func_196542_b(worldIn, pos.offset(enumfacing4), enumfacing1)) {
                           worldIn.setBlockState(blockpos3, this.getDefaultState().with(getPropertyFor(enumfacing1), Boolean.valueOf(true)), 2);
                        } else if ((double)worldIn.rand.nextFloat() < 0.05D && this.func_196542_b(worldIn, blockpos4.up(), EnumFacing.UP)) {
                           worldIn.setBlockState(blockpos4, this.getDefaultState().with(UP, Boolean.valueOf(true)), 2);
                        }
                     }
                  } else if (this.func_196542_b(worldIn, blockpos4, enumfacing)) {
                     worldIn.setBlockState(pos, state.with(getPropertyFor(enumfacing), Boolean.valueOf(true)), 2);
                  }

               }
            } else {
               if (enumfacing == EnumFacing.UP && pos.getY() < 255) {
                  if (this.func_196541_a(worldIn, pos, enumfacing)) {
                     worldIn.setBlockState(pos, state.with(UP, Boolean.valueOf(true)), 2);
                     return;
                  }

                  if (worldIn.isAirBlock(blockpos)) {
                     if (!this.func_196539_a(worldIn, pos)) {
                        return;
                     }

                     IBlockState iblockstate4 = state;

                     for(EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL) {
                        if (random.nextBoolean() || !this.func_196542_b(worldIn, blockpos.offset(enumfacing2), EnumFacing.UP)) {
                           iblockstate4 = iblockstate4.with(getPropertyFor(enumfacing2), Boolean.valueOf(false));
                        }
                     }

                     if (this.func_196540_x(iblockstate4)) {
                        worldIn.setBlockState(blockpos, iblockstate4, 2);
                     }

                     return;
                  }
               }

               if (pos.getY() > 0) {
                  BlockPos blockpos1 = pos.down();
                  IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);
                  if (iblockstate1.isAir() || iblockstate1.getBlock() == this) {
                     IBlockState iblockstate2 = iblockstate1.isAir() ? this.getDefaultState() : iblockstate1;
                     IBlockState iblockstate3 = this.func_196544_a(state, iblockstate2, random);
                     if (iblockstate2 != iblockstate3 && this.func_196540_x(iblockstate3)) {
                        worldIn.setBlockState(blockpos1, iblockstate3, 2);
                     }
                  }
               }

            }
         }
      }
   }

   private IBlockState func_196544_a(IBlockState p_196544_1_, IBlockState p_196544_2_, Random p_196544_3_) {
      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         if (p_196544_3_.nextBoolean()) {
            BooleanProperty booleanproperty = getPropertyFor(enumfacing);
            if (p_196544_1_.get(booleanproperty)) {
               p_196544_2_ = p_196544_2_.with(booleanproperty, Boolean.valueOf(true));
            }
         }
      }

      return p_196544_2_;
   }

   private boolean func_196540_x(IBlockState p_196540_1_) {
      return p_196540_1_.get(NORTH) || p_196540_1_.get(EAST) || p_196540_1_.get(SOUTH) || p_196540_1_.get(WEST);
   }

   private boolean func_196539_a(IBlockReader p_196539_1_, BlockPos p_196539_2_) {
      int i = 4;
      Iterable<BlockPos.MutableBlockPos> iterable = BlockPos.MutableBlockPos.getAllInBoxMutable(p_196539_2_.getX() - 4, p_196539_2_.getY() - 1, p_196539_2_.getZ() - 4, p_196539_2_.getX() + 4, p_196539_2_.getY() + 1, p_196539_2_.getZ() + 4);
      int j = 5;

      for(BlockPos blockpos : iterable) {
         if (p_196539_1_.getBlockState(blockpos).getBlock() == this) {
            --j;
            if (j <= 0) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean isReplaceable(IBlockState state, BlockItemUseContext useContext) {
      IBlockState iblockstate = useContext.getWorld().getBlockState(useContext.getPos());
      if (iblockstate.getBlock() == this) {
         return this.func_208496_w(iblockstate) < field_196546_A.size();
      } else {
         return super.isReplaceable(state, useContext);
      }
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockState iblockstate = context.getWorld().getBlockState(context.getPos());
      boolean flag = iblockstate.getBlock() == this;
      IBlockState iblockstate1 = flag ? iblockstate : this.getDefaultState();

      for(EnumFacing enumfacing : context.getNearestLookingDirections()) {
         if (enumfacing != EnumFacing.DOWN) {
            BooleanProperty booleanproperty = getPropertyFor(enumfacing);
            boolean flag1 = flag && iblockstate.get(booleanproperty);
            if (!flag1 && this.func_196541_a(context.getWorld(), context.getPos(), enumfacing)) {
               return iblockstate1.with(booleanproperty, Boolean.valueOf(true));
            }
         }
      }

      return flag ? iblockstate1 : null;
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.AIR;
   }

   /**
    * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
    * Block.removedByPlayer
    */
   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      if (!worldIn.isRemote && stack.getItem() == Items.SHEARS) {
         player.addStat(StatList.BLOCK_MINED.get(this));
         player.addExhaustion(0.005F);
         spawnAsEntity(worldIn, pos, new ItemStack(Blocks.VINE));
      } else {
         super.harvestBlock(worldIn, player, pos, state, te, stack);
      }

   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(UP, NORTH, EAST, SOUTH, WEST);
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

   public static BooleanProperty getPropertyFor(EnumFacing side) {
      return field_196546_A.get(side);
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

   @Override
   public boolean isLadder(IBlockState state, net.minecraft.world.IWorldReader world, BlockPos pos, net.minecraft.entity.EntityLivingBase entity) {
      return true;
   }

   @Override
   public java.util.List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IWorld world, BlockPos pos, int fortune) {
      world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
      return java.util.Arrays.asList(new ItemStack(this));
   }
}