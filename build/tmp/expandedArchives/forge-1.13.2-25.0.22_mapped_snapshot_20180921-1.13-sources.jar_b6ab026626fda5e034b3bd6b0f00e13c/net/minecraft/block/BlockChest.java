package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockChest extends BlockContainer implements IBucketPickupHandler, ILiquidContainer {
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape field_196316_c = Block.makeCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
   protected static final VoxelShape field_196317_y = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
   protected static final VoxelShape field_196318_z = Block.makeCuboidShape(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
   protected static final VoxelShape field_196313_A = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
   protected static final VoxelShape field_196315_B = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

   protected BlockChest(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(TYPE, ChestType.SINGLE).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   /**
    * @deprecated call via {@link IBlockState#hasCustomBreakingProgress()} whenever possible. Implementing/overriding is
    * fine.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomBreakingProgress(IBlockState state) {
      return true;
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
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

      if (facingState.getBlock() == this && facing.getAxis().isHorizontal()) {
         ChestType chesttype = facingState.get(TYPE);
         if (stateIn.get(TYPE) == ChestType.SINGLE && chesttype != ChestType.SINGLE && stateIn.get(FACING) == facingState.get(FACING) && getDirectionToAttached(facingState) == facing.getOpposite()) {
            return stateIn.with(TYPE, chesttype.opposite());
         }
      } else if (getDirectionToAttached(stateIn) == facing) {
         return stateIn.with(TYPE, ChestType.SINGLE);
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      if (state.get(TYPE) == ChestType.SINGLE) {
         return field_196315_B;
      } else {
         switch(getDirectionToAttached(state)) {
         case NORTH:
         default:
            return field_196316_c;
         case SOUTH:
            return field_196317_y;
         case WEST:
            return field_196318_z;
         case EAST:
            return field_196313_A;
         }
      }
   }

   /**
    * Returns a facing pointing from the given state to its attached double chest
    */
   public static EnumFacing getDirectionToAttached(IBlockState state) {
      EnumFacing enumfacing = state.get(FACING);
      return state.get(TYPE) == ChestType.LEFT ? enumfacing.rotateY() : enumfacing.rotateYCCW();
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      ChestType chesttype = ChestType.SINGLE;
      EnumFacing enumfacing = context.getPlacementHorizontalFacing().getOpposite();
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      boolean flag = context.isPlacerSneaking();
      EnumFacing enumfacing1 = context.getFace();
      if (enumfacing1.getAxis().isHorizontal() && flag) {
         EnumFacing enumfacing2 = this.getDirectionToAttach(context, enumfacing1.getOpposite());
         if (enumfacing2 != null && enumfacing2.getAxis() != enumfacing1.getAxis()) {
            enumfacing = enumfacing2;
            chesttype = enumfacing2.rotateYCCW() == enumfacing1.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
         }
      }

      if (chesttype == ChestType.SINGLE && !flag) {
         if (enumfacing == this.getDirectionToAttach(context, enumfacing.rotateY())) {
            chesttype = ChestType.LEFT;
         } else if (enumfacing == this.getDirectionToAttach(context, enumfacing.rotateYCCW())) {
            chesttype = ChestType.RIGHT;
         }
      }

      return this.getDefaultState().with(FACING, enumfacing).with(TYPE, chesttype).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
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
            worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
         }

         return true;
      } else {
         return false;
      }
   }

   /**
    * Returns facing pointing to a chest to form a double chest with, null otherwise
    */
   @Nullable
   private EnumFacing getDirectionToAttach(BlockItemUseContext p_196312_1_, EnumFacing p_196312_2_) {
      IBlockState iblockstate = p_196312_1_.getWorld().getBlockState(p_196312_1_.getPos().offset(p_196312_2_));
      return iblockstate.getBlock() == this && iblockstate.get(TYPE) == ChestType.SINGLE ? iblockstate.get(FACING) : null;
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityChest) {
            ((TileEntityChest)tileentity).setCustomName(stack.getDisplayName());
         }
      }

   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
         }

         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      } else {
         ILockableContainer ilockablecontainer = this.getContainer(state, worldIn, pos, false);
         if (ilockablecontainer != null) {
            player.displayGUIChest(ilockablecontainer);
            player.addStat(this.getOpenStat());
         }

         return true;
      }
   }

   protected Stat<ResourceLocation> getOpenStat() {
      return StatList.CUSTOM.get(StatList.OPEN_CHEST);
   }

   /**
    * Gets the chest inventory at the given location, returning null if there is no chest at that location or optionally
    * if the chest is blocked. Handles large chests.
    *  
    * @param state The current state
    * @param worldIn The world
    * @param pos The position to check
    * @param allowBlockedChest If false, then if the chest is blocked then <code>null</code> will be returned. If true,
    * then the chest can still be blocked (used by hoppers).
    */
   @Nullable
   public ILockableContainer getContainer(IBlockState state, World worldIn, BlockPos pos, boolean allowBlockedChest) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (!(tileentity instanceof TileEntityChest)) {
         return null;
      } else if (!allowBlockedChest && this.isBlocked(worldIn, pos)) {
         return null;
      } else {
         ILockableContainer ilockablecontainer = (TileEntityChest)tileentity;
         ChestType chesttype = state.get(TYPE);
         if (chesttype == ChestType.SINGLE) {
            return ilockablecontainer;
         } else {
            BlockPos blockpos = pos.offset(getDirectionToAttached(state));
            IBlockState iblockstate = worldIn.getBlockState(blockpos);
            if (iblockstate.getBlock() == this) {
               ChestType chesttype1 = iblockstate.get(TYPE);
               if (chesttype1 != ChestType.SINGLE && chesttype != chesttype1 && iblockstate.get(FACING) == state.get(FACING)) {
                  if (!allowBlockedChest && this.isBlocked(worldIn, blockpos)) {
                     return null;
                  }

                  TileEntity tileentity1 = worldIn.getTileEntity(blockpos);
                  if (tileentity1 instanceof TileEntityChest) {
                     ILockableContainer ilockablecontainer1 = chesttype == ChestType.RIGHT ? ilockablecontainer : (ILockableContainer)tileentity1;
                     ILockableContainer ilockablecontainer2 = chesttype == ChestType.RIGHT ? (ILockableContainer)tileentity1 : ilockablecontainer;
                     ilockablecontainer = new InventoryLargeChest(new TextComponentTranslation("container.chestDouble"), ilockablecontainer1, ilockablecontainer2);
                  }
               }
            }

            return ilockablecontainer;
         }
      }
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityChest();
   }

   private boolean isBlocked(World worldIn, BlockPos pos) {
      return this.isBelowSolidBlock(worldIn, pos) || this.isOcelotSittingOnChest(worldIn, pos);
   }

   private boolean isBelowSolidBlock(IBlockReader worldIn, BlockPos pos) {
       return worldIn.getBlockState(pos.up()).doesSideBlockChestOpening(worldIn, pos.up(), EnumFacing.DOWN);
   }

   private boolean isOcelotSittingOnChest(World worldIn, BlockPos pos) {
      List<EntityOcelot> list = worldIn.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1)));
      if (!list.isEmpty()) {
         for(EntityOcelot entityocelot : list) {
            if (entityocelot.isSitting()) {
               return true;
            }
         }
      }

      return false;
   }

   /**
    * @deprecated call via {@link IBlockState#hasComparatorInputOverride()} whenever possible. Implementing/overriding
    * is fine.
    */
   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   /**
    * @deprecated call via {@link IBlockState#getComparatorInputOverride(World,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      return Container.calcRedstoneFromInventory(this.getContainer(blockState, worldIn, pos, false));
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
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(FACING, TYPE, WATERLOGGED);
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

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}