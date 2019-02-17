package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BlockSourceImpl;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockDispenser extends BlockContainer {
   public static final DirectionProperty FACING = BlockDirectional.FACING;
   public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
   /** Registry for all dispense behaviors. */
   private static final Map<Item, IBehaviorDispenseItem> DISPENSE_BEHAVIOR_REGISTRY = Util.make(new Object2ObjectOpenHashMap<>(), (p_212564_0_) -> {
      p_212564_0_.defaultReturnValue(new BehaviorDefaultDispenseItem());
   });

   public static void registerDispenseBehavior(IItemProvider itemIn, IBehaviorDispenseItem behavior) {
      DISPENSE_BEHAVIOR_REGISTRY.put(itemIn.asItem(), behavior);
   }

   protected BlockDispenser(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(TRIGGERED, Boolean.valueOf(false)));
   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReaderBase worldIn) {
      return 4;
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      } else {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityDispenser) {
            player.displayGUIChest((TileEntityDispenser)tileentity);
            if (tileentity instanceof TileEntityDropper) {
               player.addStat(StatList.INSPECT_DROPPER);
            } else {
               player.addStat(StatList.INSPECT_DISPENSER);
            }
         }

         return true;
      }
   }

   protected void dispense(World worldIn, BlockPos pos) {
      BlockSourceImpl blocksourceimpl = new BlockSourceImpl(worldIn, pos);
      TileEntityDispenser tileentitydispenser = blocksourceimpl.getBlockTileEntity();
      int i = tileentitydispenser.getDispenseSlot();
      if (i < 0) {
         worldIn.playEvent(1001, pos, 0);
      } else {
         ItemStack itemstack = tileentitydispenser.getStackInSlot(i);
         IBehaviorDispenseItem ibehaviordispenseitem = this.getBehavior(itemstack);
         if (ibehaviordispenseitem != IBehaviorDispenseItem.NOOP) {
            tileentitydispenser.setInventorySlotContents(i, ibehaviordispenseitem.dispense(blocksourceimpl, itemstack));
         }

      }
   }

   protected IBehaviorDispenseItem getBehavior(ItemStack stack) {
      return DISPENSE_BEHAVIOR_REGISTRY.get(stack.getItem());
   }

   /**
    * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
    * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
    * block, etc.
    */
   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());
      boolean flag1 = state.get(TRIGGERED);
      if (flag && !flag1) {
         worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
         worldIn.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(true)), 4);
      } else if (!flag && flag1) {
         worldIn.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(false)), 4);
      }

   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (!worldIn.isRemote) {
         this.dispense(worldIn, pos);
      }

   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityDispenser();
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityDispenser) {
            ((TileEntityDispenser)tileentity).setCustomName(stack.getDisplayName());
         }
      }

   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityDispenser) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityDispenser)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
         }

         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   /**
    * Get the position where the dispenser at the given Coordinates should dispense to.
    */
   public static IPosition getDispensePosition(IBlockSource coords) {
      EnumFacing enumfacing = coords.getBlockState().get(FACING);
      double d0 = coords.getX() + 0.7D * (double)enumfacing.getXOffset();
      double d1 = coords.getY() + 0.7D * (double)enumfacing.getYOffset();
      double d2 = coords.getZ() + 0.7D * (double)enumfacing.getZOffset();
      return new PositionImpl(d0, d1, d2);
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
      return Container.calcRedstone(worldIn.getTileEntity(pos));
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
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
      builder.add(FACING, TRIGGERED);
   }
}