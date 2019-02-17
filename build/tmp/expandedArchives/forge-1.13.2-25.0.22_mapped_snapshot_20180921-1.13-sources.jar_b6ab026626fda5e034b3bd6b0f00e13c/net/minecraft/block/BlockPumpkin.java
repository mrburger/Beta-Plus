package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPumpkin extends BlockStemGrown {
   protected BlockPumpkin(Block.Properties builder) {
      super(builder);
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (itemstack.getItem() == Items.SHEARS) {
         if (!worldIn.isRemote) {
            EnumFacing enumfacing = side.getAxis() == EnumFacing.Axis.Y ? player.getHorizontalFacing().getOpposite() : side;
            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            worldIn.setBlockState(pos, Blocks.CARVED_PUMPKIN.getDefaultState().with(BlockCarvedPumpkin.FACING, enumfacing), 11);
            EntityItem entityitem = new EntityItem(worldIn, (double)pos.getX() + 0.5D + (double)enumfacing.getXOffset() * 0.65D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D + (double)enumfacing.getZOffset() * 0.65D, new ItemStack(Items.PUMPKIN_SEEDS, 4));
            entityitem.motionX = 0.05D * (double)enumfacing.getXOffset() + worldIn.rand.nextDouble() * 0.02D;
            entityitem.motionY = 0.05D;
            entityitem.motionZ = 0.05D * (double)enumfacing.getZOffset() + worldIn.rand.nextDouble() * 0.02D;
            worldIn.spawnEntity(entityitem);
            itemstack.damageItem(1, player);
         }

         return true;
      } else {
         return super.onBlockActivated(state, worldIn, pos, player, hand, side, hitX, hitY, hitZ);
      }
   }

   public BlockStem getStem() {
      return (BlockStem)Blocks.PUMPKIN_STEM;
   }

   public BlockAttachedStem getAttachedStem() {
      return (BlockAttachedStem)Blocks.ATTACHED_PUMPKIN_STEM;
   }
}