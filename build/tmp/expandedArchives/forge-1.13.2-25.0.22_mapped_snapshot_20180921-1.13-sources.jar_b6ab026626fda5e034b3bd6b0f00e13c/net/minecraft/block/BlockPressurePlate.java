package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockPressurePlate extends BlockBasePressurePlate {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private final BlockPressurePlate.Sensitivity sensitivity;

   protected BlockPressurePlate(BlockPressurePlate.Sensitivity p_i48348_1_, Block.Properties p_i48348_2_) {
      super(p_i48348_2_);
      this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, Boolean.valueOf(false)));
      this.sensitivity = p_i48348_1_;
   }

   protected int getRedstoneStrength(IBlockState state) {
      return state.get(POWERED) ? 15 : 0;
   }

   protected IBlockState setRedstoneStrength(IBlockState state, int strength) {
      return state.with(POWERED, Boolean.valueOf(strength > 0));
   }

   protected void playClickOnSound(IWorld worldIn, BlockPos pos) {
      if (this.material == Material.WOOD) {
         worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
      } else {
         worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
      }

   }

   protected void playClickOffSound(IWorld worldIn, BlockPos pos) {
      if (this.material == Material.WOOD) {
         worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.7F);
      } else {
         worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
      }

   }

   protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
      AxisAlignedBB axisalignedbb = PRESSURE_AABB.offset(pos);
      List<? extends Entity> list;
      switch(this.sensitivity) {
      case EVERYTHING:
         list = worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb);
         break;
      case MOBS:
         list = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
         break;
      default:
         return 0;
      }

      if (!list.isEmpty()) {
         for(Entity entity : list) {
            if (!entity.doesEntityNotTriggerPressurePlate()) {
               return 15;
            }
         }
      }

      return 0;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(POWERED);
   }

   public static enum Sensitivity {
      EVERYTHING,
      MOBS;
   }
}