package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityJukebox;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockJukebox extends BlockContainer {
   public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;

   protected BlockJukebox(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(HAS_RECORD, Boolean.valueOf(false)));
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (state.get(HAS_RECORD)) {
         this.func_203419_a(worldIn, pos);
         state = state.with(HAS_RECORD, Boolean.valueOf(false));
         worldIn.setBlockState(pos, state, 2);
         return true;
      } else {
         return false;
      }
   }

   public void insertRecord(IWorld worldIn, BlockPos pos, IBlockState state, ItemStack recordStack) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityJukebox) {
         ((TileEntityJukebox)tileentity).setRecord(recordStack.copy());
         worldIn.setBlockState(pos, state.with(HAS_RECORD, Boolean.valueOf(true)), 2);
      }
   }

   private void func_203419_a(World p_203419_1_, BlockPos p_203419_2_) {
      if (!p_203419_1_.isRemote) {
         TileEntity tileentity = p_203419_1_.getTileEntity(p_203419_2_);
         if (tileentity instanceof TileEntityJukebox) {
            TileEntityJukebox tileentityjukebox = (TileEntityJukebox)tileentity;
            ItemStack itemstack = tileentityjukebox.getRecord();
            if (!itemstack.isEmpty()) {
               p_203419_1_.playEvent(1010, p_203419_2_, 0);
               p_203419_1_.playRecord(p_203419_2_, (SoundEvent)null);
               tileentityjukebox.setRecord(ItemStack.EMPTY);
               float f = 0.7F;
               double d0 = (double)(p_203419_1_.rand.nextFloat() * 0.7F) + (double)0.15F;
               double d1 = (double)(p_203419_1_.rand.nextFloat() * 0.7F) + (double)0.060000002F + 0.6D;
               double d2 = (double)(p_203419_1_.rand.nextFloat() * 0.7F) + (double)0.15F;
               ItemStack itemstack1 = itemstack.copy();
               EntityItem entityitem = new EntityItem(p_203419_1_, (double)p_203419_2_.getX() + d0, (double)p_203419_2_.getY() + d1, (double)p_203419_2_.getZ() + d2, itemstack1);
               entityitem.setDefaultPickupDelay();
               p_203419_1_.spawnEntity(entityitem);
            }
         }
      }
   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         this.func_203419_a(worldIn, pos);
         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
      if (!worldIn.isRemote) {
         super.dropBlockAsItemWithChance(state, worldIn, pos, chancePerItem, 0);
      }
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityJukebox();
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
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityJukebox) {
         Item item = ((TileEntityJukebox)tileentity).getRecord().getItem();
         if (item instanceof ItemRecord) {
            return ((ItemRecord)item).getComparatorValue();
         }
      }

      return 0;
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(HAS_RECORD);
   }
}