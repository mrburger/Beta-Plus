package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockAnvil extends BlockFalling {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   private static final VoxelShape field_196436_c = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
   private static final VoxelShape field_196439_y = Block.makeCuboidShape(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D);
   private static final VoxelShape field_196440_z = Block.makeCuboidShape(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D);
   private static final VoxelShape field_196434_A = Block.makeCuboidShape(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D);
   private static final VoxelShape field_196435_B = Block.makeCuboidShape(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D);
   private static final VoxelShape field_196437_C = Block.makeCuboidShape(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D);
   private static final VoxelShape field_196438_D = Block.makeCuboidShape(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D);
   private static final VoxelShape X_AXIS_AABB = VoxelShapes.or(field_196436_c, VoxelShapes.or(field_196439_y, VoxelShapes.or(field_196440_z, field_196434_A)));
   private static final VoxelShape Z_AXIS_AABB = VoxelShapes.or(field_196436_c, VoxelShapes.or(field_196435_B, VoxelShapes.or(field_196437_C, field_196438_D)));

   public BlockAnvil(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH));
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
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

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().rotateY());
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote) {
         player.displayGui(new BlockAnvil.Anvil(worldIn, pos));
      }

      return true;
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      EnumFacing enumfacing = state.get(FACING);
      return enumfacing.getAxis() == EnumFacing.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
   }

   protected void onStartFalling(EntityFallingBlock fallingEntity) {
      fallingEntity.setHurtEntities(true);
   }

   public void onEndFalling(World worldIn, BlockPos pos, IBlockState fallingState, IBlockState hitState) {
      worldIn.playEvent(1031, pos, 0);
   }

   public void onBroken(World worldIn, BlockPos pos) {
      worldIn.playEvent(1029, pos, 0);
   }

   @Nullable
   public static IBlockState damage(IBlockState p_196433_0_) {
      Block block = p_196433_0_.getBlock();
      if (block == Blocks.ANVIL) {
         return Blocks.CHIPPED_ANVIL.getDefaultState().with(FACING, p_196433_0_.get(FACING));
      } else {
         return block == Blocks.CHIPPED_ANVIL ? Blocks.DAMAGED_ANVIL.getDefaultState().with(FACING, p_196433_0_.get(FACING)) : null;
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

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(FACING);
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }

   public static class Anvil implements IInteractionObject {
      private final World world;
      private final BlockPos position;

      public Anvil(World worldIn, BlockPos pos) {
         this.world = worldIn;
         this.position = pos;
      }

      public ITextComponent getName() {
         return new TextComponentTranslation(Blocks.ANVIL.getTranslationKey());
      }

      public boolean hasCustomName() {
         return false;
      }

      @Nullable
      public ITextComponent getCustomName() {
         return null;
      }

      public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
         return new ContainerRepair(playerInventory, this.world, this.position, playerIn);
      }

      public String getGuiID() {
         return "minecraft:anvil";
      }
   }
}