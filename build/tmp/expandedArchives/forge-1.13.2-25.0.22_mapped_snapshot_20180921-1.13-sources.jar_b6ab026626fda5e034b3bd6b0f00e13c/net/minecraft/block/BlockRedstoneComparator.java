package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ComparatorMode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

public class BlockRedstoneComparator extends BlockRedstoneDiode implements ITileEntityProvider {
   public static final EnumProperty<ComparatorMode> MODE = BlockStateProperties.COMPARATOR_MODE;

   public BlockRedstoneComparator(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, EnumFacing.NORTH).with(POWERED, Boolean.valueOf(false)).with(MODE, ComparatorMode.COMPARE));
   }

   protected int getDelay(IBlockState p_196346_1_) {
      return 2;
   }

   protected int getActiveSignal(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity instanceof TileEntityComparator ? ((TileEntityComparator)tileentity).getOutputSignal() : 0;
   }

   private int calculateOutput(World worldIn, BlockPos pos, IBlockState state) {
      return state.get(MODE) == ComparatorMode.SUBTRACT ? Math.max(this.calculateInputStrength(worldIn, pos, state) - this.getPowerOnSides(worldIn, pos, state), 0) : this.calculateInputStrength(worldIn, pos, state);
   }

   protected boolean shouldBePowered(World worldIn, BlockPos pos, IBlockState state) {
      int i = this.calculateInputStrength(worldIn, pos, state);
      if (i >= 15) {
         return true;
      } else if (i == 0) {
         return false;
      } else {
         return i >= this.getPowerOnSides(worldIn, pos, state);
      }
   }

   protected void func_211326_a(World p_211326_1_, BlockPos p_211326_2_) {
      p_211326_1_.removeTileEntity(p_211326_2_);
   }

   protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
      int i = super.calculateInputStrength(worldIn, pos, state);
      EnumFacing enumfacing = state.get(HORIZONTAL_FACING);
      BlockPos blockpos = pos.offset(enumfacing);
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      if (iblockstate.hasComparatorInputOverride()) {
         i = iblockstate.getComparatorInputOverride(worldIn, blockpos);
      } else if (i < 15 && iblockstate.isNormalCube()) {
         blockpos = blockpos.offset(enumfacing);
         iblockstate = worldIn.getBlockState(blockpos);
         if (iblockstate.hasComparatorInputOverride()) {
            i = iblockstate.getComparatorInputOverride(worldIn, blockpos);
         } else if (iblockstate.isAir()) {
            EntityItemFrame entityitemframe = this.findItemFrame(worldIn, enumfacing, blockpos);
            if (entityitemframe != null) {
               i = entityitemframe.getAnalogOutput();
            }
         }
      }

      return i;
   }

   @Nullable
   private EntityItemFrame findItemFrame(World worldIn, EnumFacing facing, BlockPos pos) {
      List<EntityItemFrame> list = worldIn.getEntitiesWithinAABB(EntityItemFrame.class, new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)), (p_210304_1_) -> {
         return p_210304_1_ != null && p_210304_1_.getHorizontalFacing() == facing;
      });
      return list.size() == 1 ? list.get(0) : null;
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!player.abilities.allowEdit) {
         return false;
      } else {
         state = state.cycle(MODE);
         float f = state.get(MODE) == ComparatorMode.SUBTRACT ? 0.55F : 0.5F;
         worldIn.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, f);
         worldIn.setBlockState(pos, state, 2);
         this.onStateChange(worldIn, pos, state);
         return true;
      }
   }

   protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.getPendingBlockTicks().isTickPending(pos, this)) {
         int i = this.calculateOutput(worldIn, pos, state);
         TileEntity tileentity = worldIn.getTileEntity(pos);
         int j = tileentity instanceof TileEntityComparator ? ((TileEntityComparator)tileentity).getOutputSignal() : 0;
         if (i != j || state.get(POWERED) != this.shouldBePowered(worldIn, pos, state)) {
            TickPriority tickpriority = this.isFacingTowardsRepeater(worldIn, pos, state) ? TickPriority.HIGH : TickPriority.NORMAL;
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2, tickpriority);
         }

      }
   }

   private void onStateChange(World worldIn, BlockPos pos, IBlockState state) {
      int i = this.calculateOutput(worldIn, pos, state);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      int j = 0;
      if (tileentity instanceof TileEntityComparator) {
         TileEntityComparator tileentitycomparator = (TileEntityComparator)tileentity;
         j = tileentitycomparator.getOutputSignal();
         tileentitycomparator.setOutputSignal(i);
      }

      if (j != i || state.get(MODE) == ComparatorMode.COMPARE) {
         boolean flag1 = this.shouldBePowered(worldIn, pos, state);
         boolean flag = state.get(POWERED);
         if (flag && !flag1) {
            worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(false)), 2);
         } else if (!flag && flag1) {
            worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(true)), 2);
         }

         this.notifyNeighbors(worldIn, pos, state);
      }

   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      this.onStateChange(worldIn, pos, state);
   }

   /**
    * Called on server when World#addBlockEvent is called. If server returns true, then also called on the client. On
    * the Server, this may perform additional changes to the world, like pistons replacing the block with an extended
    * base. On the client, the update may involve replacing tile entities or effects such as sounds or particles
    * @deprecated call via {@link IBlockState#onBlockEventReceived(World,BlockPos,int,int)} whenever possible.
    * Implementing/overriding is fine.
    */
   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      super.eventReceived(state, worldIn, pos, id, param);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity != null && tileentity.receiveClientEvent(id, param);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityComparator();
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(HORIZONTAL_FACING, MODE, POWERED);
   }

   @Override
   public boolean getWeakChanges(IBlockState state, net.minecraft.world.IWorldReader world, BlockPos pos) {
      return true;
   }

   @Override
   public void onNeighborChange(IBlockState state, net.minecraft.world.IWorldReader world, BlockPos pos, BlockPos neighbor) {
      if (pos.getY() == neighbor.getY() && world instanceof World && !((World)world).isRemote()) {
         neighborChanged(state, (World)world, pos, world.getBlockState(neighbor).getBlock(), neighbor);
      }
   }
}