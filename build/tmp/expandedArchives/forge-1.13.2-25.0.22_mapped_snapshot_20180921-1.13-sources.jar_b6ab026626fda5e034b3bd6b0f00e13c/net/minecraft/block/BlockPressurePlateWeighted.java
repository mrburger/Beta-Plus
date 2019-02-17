package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate {
   public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
   private final int maxWeight;

   protected BlockPressurePlateWeighted(int p_i48295_1_, Block.Properties p_i48295_2_) {
      super(p_i48295_2_);
      this.setDefaultState(this.stateContainer.getBaseState().with(POWER, Integer.valueOf(0)));
      this.maxWeight = p_i48295_1_;
   }

   protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
      int i = Math.min(worldIn.getEntitiesWithinAABB(Entity.class, PRESSURE_AABB.offset(pos)).size(), this.maxWeight);
      if (i > 0) {
         float f = (float)Math.min(this.maxWeight, i) / (float)this.maxWeight;
         return MathHelper.ceil(f * 15.0F);
      } else {
         return 0;
      }
   }

   protected void playClickOnSound(IWorld worldIn, BlockPos pos) {
      worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.90000004F);
   }

   protected void playClickOffSound(IWorld worldIn, BlockPos pos) {
      worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.75F);
   }

   protected int getRedstoneStrength(IBlockState state) {
      return state.get(POWER);
   }

   protected IBlockState setRedstoneStrength(IBlockState state, int strength) {
      return state.with(POWER, Integer.valueOf(strength));
   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReaderBase worldIn) {
      return 10;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(POWER);
   }
}