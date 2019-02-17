package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSign extends ItemWallOrFloor {
   public ItemSign(Item.Properties builder) {
      super(Blocks.SIGN, Blocks.WALL_SIGN, builder);
   }

   protected boolean onBlockPlaced(BlockPos p_195943_1_, World p_195943_2_, @Nullable EntityPlayer p_195943_3_, ItemStack p_195943_4_, IBlockState p_195943_5_) {
      boolean flag = super.onBlockPlaced(p_195943_1_, p_195943_2_, p_195943_3_, p_195943_4_, p_195943_5_);
      if (!p_195943_2_.isRemote && !flag && p_195943_3_ != null) {
         p_195943_3_.openSignEditor((TileEntitySign)p_195943_2_.getTileEntity(p_195943_1_));
      }

      return flag;
   }
}