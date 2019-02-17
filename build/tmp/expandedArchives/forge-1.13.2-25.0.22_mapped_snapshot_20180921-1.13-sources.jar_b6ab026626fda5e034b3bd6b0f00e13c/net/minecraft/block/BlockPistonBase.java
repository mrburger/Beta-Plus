package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BlockPistonBase extends BlockDirectional {
   public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
   protected static final VoxelShape PISTON_BASE_EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_BASE_WEST_AABB = Block.makeCuboidShape(4.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_BASE_SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 12.0D);
   protected static final VoxelShape PISTON_BASE_NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_BASE_UP_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
   protected static final VoxelShape PISTON_BASE_DOWN_AABB = Block.makeCuboidShape(0.0D, 4.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   /** This piston is the sticky one? */
   private final boolean isSticky;

   public BlockPistonBase(boolean sticky, Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(EXTENDED, Boolean.valueOf(false)));
      this.isSticky = sticky;
   }

   /**
    * @deprecated call via {@link IBlockState#causesSuffocation()} whenever possible. Implementing/overriding is fine.
    */
   public boolean causesSuffocation(IBlockState state) {
      return !state.get(EXTENDED);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      if (state.get(EXTENDED)) {
         switch((EnumFacing)state.get(FACING)) {
         case DOWN:
            return PISTON_BASE_DOWN_AABB;
         case UP:
         default:
            return PISTON_BASE_UP_AABB;
         case NORTH:
            return PISTON_BASE_NORTH_AABB;
         case SOUTH:
            return PISTON_BASE_SOUTH_AABB;
         case WEST:
            return PISTON_BASE_WEST_AABB;
         case EAST:
            return PISTON_BASE_EAST_AABB;
         }
      } else {
         return VoxelShapes.fullCube();
      }
   }

   /**
    * Determines if the block is solid enough on the top side to support other blocks, like redstone components.
    * @deprecated prefer calling {@link IBlockState#isTopSolid()} wherever possible
    */
   public boolean isTopSolid(IBlockState state) {
      return !state.get(EXTENDED) || state.get(FACING) == EnumFacing.DOWN;
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (!worldIn.isRemote) {
         this.checkForMove(worldIn, pos, state);
      }

   }

   /**
    * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
    * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
    * block, etc.
    */
   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!worldIn.isRemote) {
         this.checkForMove(worldIn, pos, state);
      }

   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      if (oldState.getBlock() != state.getBlock()) {
         if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null) {
            this.checkForMove(worldIn, pos, state);
         }

      }
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite()).with(EXTENDED, Boolean.valueOf(false));
   }

   private void checkForMove(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing enumfacing = state.get(FACING);
      boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);
      if (flag && !state.get(EXTENDED)) {
         if ((new BlockPistonStructureHelper(worldIn, pos, enumfacing, true)).canMove()) {
            worldIn.addBlockEvent(pos, this, 0, enumfacing.getIndex());
         }
      } else if (!flag && state.get(EXTENDED)) {
         BlockPos blockpos = pos.offset(enumfacing, 2);
         IBlockState iblockstate = worldIn.getBlockState(blockpos);
         int i = 1;
         if (iblockstate.getBlock() == Blocks.MOVING_PISTON && iblockstate.get(FACING) == enumfacing) {
            TileEntity tileentity = worldIn.getTileEntity(blockpos);
            if (tileentity instanceof TileEntityPiston) {
               TileEntityPiston tileentitypiston = (TileEntityPiston)tileentity;
               if (tileentitypiston.isExtending() && (tileentitypiston.getProgress(0.0F) < 0.5F || worldIn.getGameTime() == tileentitypiston.getLastTicked() || ((WorldServer)worldIn).isInsideTick())) {
                  i = 2;
               }
            }
         }

         worldIn.addBlockEvent(pos, this, i, enumfacing.getIndex());
      }

   }

   private boolean shouldBeExtended(World worldIn, BlockPos pos, EnumFacing facing) {
      for(EnumFacing enumfacing : EnumFacing.values()) {
         if (enumfacing != facing && worldIn.isSidePowered(pos.offset(enumfacing), enumfacing)) {
            return true;
         }
      }

      if (worldIn.isSidePowered(pos, EnumFacing.DOWN)) {
         return true;
      } else {
         BlockPos blockpos = pos.up();

         for(EnumFacing enumfacing1 : EnumFacing.values()) {
            if (enumfacing1 != EnumFacing.DOWN && worldIn.isSidePowered(blockpos.offset(enumfacing1), enumfacing1)) {
               return true;
            }
         }

         return false;
      }
   }

   /**
    * Called on server when World#addBlockEvent is called. If server returns true, then also called on the client. On
    * the Server, this may perform additional changes to the world, like pistons replacing the block with an extended
    * base. On the client, the update may involve replacing tile entities or effects such as sounds or particles
    * @deprecated call via {@link IBlockState#onBlockEventReceived(World,BlockPos,int,int)} whenever possible.
    * Implementing/overriding is fine.
    */
   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      EnumFacing enumfacing = state.get(FACING);
      if (!worldIn.isRemote) {
         boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);
         if (flag && (id == 1 || id == 2)) {
            worldIn.setBlockState(pos, state.with(EXTENDED, Boolean.valueOf(true)), 2);
            return false;
         }

         if (!flag && id == 0) {
            return false;
         }
      }

      if (id == 0) {
         if (!this.doMove(worldIn, pos, enumfacing, true)) {
            return false;
         }

         worldIn.setBlockState(pos, state.with(EXTENDED, Boolean.valueOf(true)), 67);
         worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
      } else if (id == 1 || id == 2) {
         TileEntity tileentity1 = worldIn.getTileEntity(pos.offset(enumfacing));
         if (tileentity1 instanceof TileEntityPiston) {
            ((TileEntityPiston)tileentity1).clearPistonTileEntity();
         }

         worldIn.setBlockState(pos, Blocks.MOVING_PISTON.getDefaultState().with(BlockPistonMoving.FACING, enumfacing).with(BlockPistonMoving.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT), 3);
         worldIn.setTileEntity(pos, BlockPistonMoving.createTilePiston(this.getDefaultState().with(FACING, EnumFacing.byIndex(param & 7)), enumfacing, false, true));
         if (this.isSticky) {
            BlockPos blockpos = pos.add(enumfacing.getXOffset() * 2, enumfacing.getYOffset() * 2, enumfacing.getZOffset() * 2);
            IBlockState iblockstate = worldIn.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            boolean flag1 = false;
            if (block == Blocks.MOVING_PISTON) {
               TileEntity tileentity = worldIn.getTileEntity(blockpos);
               if (tileentity instanceof TileEntityPiston) {
                  TileEntityPiston tileentitypiston = (TileEntityPiston)tileentity;
                  if (tileentitypiston.func_212363_d() == enumfacing && tileentitypiston.isExtending()) {
                     tileentitypiston.clearPistonTileEntity();
                     flag1 = true;
                  }
               }
            }

            if (!flag1) {
               if (id != 1 || iblockstate.isAir() || !canPush(iblockstate, worldIn, blockpos, enumfacing.getOpposite(), false, enumfacing) || iblockstate.getPushReaction() != EnumPushReaction.NORMAL && block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
                  worldIn.removeBlock(pos.offset(enumfacing));
               } else {
                  this.doMove(worldIn, pos, enumfacing, false);
               }
            }
         } else {
            worldIn.removeBlock(pos.offset(enumfacing));
         }

         worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.15F + 0.6F);
      }

      return true;
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   /**
    * Checks if the piston can push the given BlockState.
    */
   public static boolean canPush(IBlockState blockStateIn, World worldIn, BlockPos pos, EnumFacing facing, boolean destroyBlocks, EnumFacing p_185646_5_) {
      Block block = blockStateIn.getBlock();
      if (block == Blocks.OBSIDIAN) {
         return false;
      } else if (!worldIn.getWorldBorder().contains(pos)) {
         return false;
      } else if (pos.getY() >= 0 && (facing != EnumFacing.DOWN || pos.getY() != 0)) {
         if (pos.getY() <= worldIn.getHeight() - 1 && (facing != EnumFacing.UP || pos.getY() != worldIn.getHeight() - 1)) {
            if (block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
               if (blockStateIn.getBlockHardness(worldIn, pos) == -1.0F) {
                  return false;
               }

               switch(blockStateIn.getPushReaction()) {
               case BLOCK:
                  return false;
               case DESTROY:
                  return destroyBlocks;
               case PUSH_ONLY:
                  return facing == p_185646_5_;
               }
            } else if (blockStateIn.get(EXTENDED)) {
               return false;
            }

            return !blockStateIn.hasTileEntity();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean doMove(World worldIn, BlockPos pos, EnumFacing direction, boolean extending) {
      BlockPos blockpos = pos.offset(direction);
      if (!extending && worldIn.getBlockState(blockpos).getBlock() == Blocks.PISTON_HEAD) {
         worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 20);
      }

      BlockPistonStructureHelper blockpistonstructurehelper = new BlockPistonStructureHelper(worldIn, pos, direction, extending);
      if (!blockpistonstructurehelper.canMove()) {
         return false;
      } else {
         List<BlockPos> list = blockpistonstructurehelper.getBlocksToMove();
         List<IBlockState> list1 = Lists.newArrayList();

         for(int i = 0; i < list.size(); ++i) {
            BlockPos blockpos1 = list.get(i);
            list1.add(worldIn.getBlockState(blockpos1));
         }

         List<BlockPos> list2 = blockpistonstructurehelper.getBlocksToDestroy();
         int k = list.size() + list2.size();
         IBlockState[] aiblockstate = new IBlockState[k];
         EnumFacing enumfacing = extending ? direction : direction.getOpposite();
         Set<BlockPos> set = Sets.newHashSet(list);

         for(int j = list2.size() - 1; j >= 0; --j) {
            BlockPos blockpos2 = list2.get(j);
            IBlockState iblockstate = worldIn.getBlockState(blockpos2);
            // Forge: With our change to how snowballs are dropped this needs to disallow to mimic vanilla behavior.
            float chance = iblockstate.getBlock() instanceof BlockSnow ? -1.0f : 1.0f;
            iblockstate.dropBlockAsItemWithChance(worldIn, blockpos2, chance, 0);
            worldIn.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 18);
            --k;
            aiblockstate[k] = iblockstate;
         }

         for(int l = list.size() - 1; l >= 0; --l) {
            BlockPos blockpos3 = list.get(l);
            IBlockState iblockstate3 = worldIn.getBlockState(blockpos3);
            blockpos3 = blockpos3.offset(enumfacing);
            set.remove(blockpos3);
            worldIn.setBlockState(blockpos3, Blocks.MOVING_PISTON.getDefaultState().with(FACING, direction), 68);
            worldIn.setTileEntity(blockpos3, BlockPistonMoving.createTilePiston(list1.get(l), direction, extending, false));
            --k;
            aiblockstate[k] = iblockstate3;
         }

         if (extending) {
            PistonType pistontype = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
            IBlockState iblockstate1 = Blocks.PISTON_HEAD.getDefaultState().with(BlockPistonExtension.FACING, direction).with(BlockPistonExtension.TYPE, pistontype);
            IBlockState iblockstate4 = Blocks.MOVING_PISTON.getDefaultState().with(BlockPistonMoving.FACING, direction).with(BlockPistonMoving.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            set.remove(blockpos);
            worldIn.setBlockState(blockpos, iblockstate4, 68);
            worldIn.setTileEntity(blockpos, BlockPistonMoving.createTilePiston(iblockstate1, direction, true, true));
         }

         for(BlockPos blockpos4 : set) {
            worldIn.setBlockState(blockpos4, Blocks.AIR.getDefaultState(), 66);
         }

         for(int i1 = list2.size() - 1; i1 >= 0; --i1) {
            IBlockState iblockstate2 = aiblockstate[k++];
            BlockPos blockpos5 = list2.get(i1);
            iblockstate2.updateDiagonalNeighbors(worldIn, blockpos5, 2);
            worldIn.notifyNeighborsOfStateChange(blockpos5, iblockstate2.getBlock());
         }

         for(int j1 = list.size() - 1; j1 >= 0; --j1) {
            worldIn.notifyNeighborsOfStateChange(list.get(j1), aiblockstate[k++].getBlock());
         }

         if (extending) {
            worldIn.notifyNeighborsOfStateChange(blockpos, Blocks.PISTON_HEAD);
         }

         return true;
      }
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

   public IBlockState rotate(IBlockState state, net.minecraft.world.IWorld world, BlockPos pos, Rotation direction) {
       return state.get(EXTENDED) ? state : super.rotate(state, world, pos, direction);
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(FACING, EXTENDED);
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
      return state.get(FACING) != face.getOpposite() && state.get(EXTENDED) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
   }

   public int getOpacity(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return 0;
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}