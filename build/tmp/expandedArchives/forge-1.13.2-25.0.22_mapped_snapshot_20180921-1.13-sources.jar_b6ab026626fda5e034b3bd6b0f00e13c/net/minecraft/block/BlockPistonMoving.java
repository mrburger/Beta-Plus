package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockPistonMoving extends BlockContainer {
   public static final DirectionProperty FACING = BlockPistonExtension.FACING;
   public static final EnumProperty<PistonType> TYPE = BlockPistonExtension.TYPE;

   public BlockPistonMoving(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(TYPE, PistonType.DEFAULT));
   }

   @Nullable
   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return null;
   }

   public static TileEntity createTilePiston(IBlockState p_196343_0_, EnumFacing p_196343_1_, boolean p_196343_2_, boolean p_196343_3_) {
      return new TileEntityPiston(p_196343_0_, p_196343_1_, p_196343_2_, p_196343_3_);
   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityPiston) {
            ((TileEntityPiston)tileentity).clearPistonTileEntity();
         } else {
            super.onReplaced(state, worldIn, pos, newState, isMoving);
         }

      }
   }

   /**
    * Called after a player destroys this Block - the posiiton pos may no longer hold the state indicated.
    */
   public void onPlayerDestroy(IWorld worldIn, BlockPos pos, IBlockState state) {
      BlockPos blockpos = pos.offset(state.get(FACING).getOpposite());
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      if (iblockstate.getBlock() instanceof BlockPistonBase && iblockstate.get(BlockPistonBase.EXTENDED)) {
         worldIn.removeBlock(blockpos);
      }

   }

   public boolean isSolid(IBlockState state) {
      return false;
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null) {
         worldIn.removeBlock(pos);
         return true;
      } else {
         return false;
      }
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.AIR;
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
      if (false && !worldIn.isRemote) { //Forge: Noop this out
         TileEntityPiston tileentitypiston = this.getTilePistonAt(worldIn, pos);
         if (tileentitypiston != null) {
            tileentitypiston.getPistonState().dropBlockAsItem(worldIn, pos, 0);
         }
      }
      super.dropBlockAsItemWithChance(state, worldIn, pos, 1, fortune); // mimic vanilla behavior from above and ignore chance
   }

   @Override
   public void getDrops(IBlockState state, net.minecraft.util.NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
      TileEntityPiston te = this.getTilePistonAt(world, pos);
      if (te != null)
         te.getPistonState().getDrops(drops, world, pos, fortune);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return VoxelShapes.empty();
   }

   public VoxelShape getCollisionShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      TileEntityPiston tileentitypiston = this.getTilePistonAt(worldIn, pos);
      return tileentitypiston != null ? tileentitypiston.getCollisionShape(worldIn, pos) : VoxelShapes.empty();
   }

   @Nullable
   private TileEntityPiston getTilePistonAt(IBlockReader p_196342_1_, BlockPos p_196342_2_) {
      TileEntity tileentity = p_196342_1_.getTileEntity(p_196342_2_);
      return tileentity instanceof TileEntityPiston ? (TileEntityPiston)tileentity : null;
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      return ItemStack.EMPTY;
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
      builder.add(FACING, TYPE);
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