package net.minecraft.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;

public class ItemWallOrFloor extends ItemBlock {
   protected final Block wallBlock;

   public ItemWallOrFloor(Block floorBlock, Block wallBlockIn, Item.Properties p_i48462_3_) {
      super(floorBlock, p_i48462_3_);
      this.wallBlock = wallBlockIn;
   }

   @Nullable
   protected IBlockState getStateForPlacement(BlockItemUseContext p_195945_1_) {
      IBlockState iblockstate = this.wallBlock.getStateForPlacement(p_195945_1_);
      IBlockState iblockstate1 = null;
      IWorldReaderBase iworldreaderbase = p_195945_1_.getWorld();
      BlockPos blockpos = p_195945_1_.getPos();

      for(EnumFacing enumfacing : p_195945_1_.getNearestLookingDirections()) {
         if (enumfacing != EnumFacing.UP) {
            IBlockState iblockstate2 = enumfacing == EnumFacing.DOWN ? this.getBlock().getStateForPlacement(p_195945_1_) : iblockstate;
            if (iblockstate2 != null && iblockstate2.isValidPosition(iworldreaderbase, blockpos)) {
               iblockstate1 = iblockstate2;
               break;
            }
         }
      }

      return iblockstate1 != null && iworldreaderbase.checkNoEntityCollision(iblockstate1, blockpos) ? iblockstate1 : null;
   }

   public void addToBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
      super.addToBlockToItemMap(blockToItemMap, itemIn);
      blockToItemMap.put(this.wallBlock, itemIn);
   }
}