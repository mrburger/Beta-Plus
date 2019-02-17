package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockRedstoneTorch extends BlockTorch {
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   private static final Map<IBlockReader, List<BlockRedstoneTorch.Toggle>> BURNED_TORCHES = new java.util.WeakHashMap<IBlockReader, List<BlockRedstoneTorch.Toggle>>(); // FORGE - fix vanilla MC-101233

   protected BlockRedstoneTorch(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(LIT, Boolean.valueOf(true)));
   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReaderBase worldIn) {
      return 2;
   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      for(EnumFacing enumfacing : EnumFacing.values()) {
         worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
      }

   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (!isMoving) {
         for(EnumFacing enumfacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
         }

      }
   }

   /**
    * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      return blockState.get(LIT) && EnumFacing.UP != side ? 15 : 0;
   }

   protected boolean shouldBeOff(World worldIn, BlockPos pos, IBlockState state) {
      return worldIn.isSidePowered(pos.down(), EnumFacing.DOWN);
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      update(state, worldIn, pos, random, this.shouldBeOff(worldIn, pos, state));
   }

   public static void update(IBlockState p_196527_0_, World p_196527_1_, BlockPos p_196527_2_, Random p_196527_3_, boolean p_196527_4_) {
      List<BlockRedstoneTorch.Toggle> list = BURNED_TORCHES.get(p_196527_1_);

      while(list != null && !list.isEmpty() && p_196527_1_.getGameTime() - (list.get(0)).time > 60L) {
         list.remove(0);
      }

      if (p_196527_0_.get(LIT)) {
         if (p_196527_4_) {
            p_196527_1_.setBlockState(p_196527_2_, p_196527_0_.with(LIT, Boolean.valueOf(false)), 3);
            if (isBurnedOut(p_196527_1_, p_196527_2_, true)) {
               p_196527_1_.playSound((EntityPlayer)null, p_196527_2_, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5F, 2.6F + (p_196527_1_.rand.nextFloat() - p_196527_1_.rand.nextFloat()) * 0.8F);

               for(int i = 0; i < 5; ++i) {
                  double d0 = (double)p_196527_2_.getX() + p_196527_3_.nextDouble() * 0.6D + 0.2D;
                  double d1 = (double)p_196527_2_.getY() + p_196527_3_.nextDouble() * 0.6D + 0.2D;
                  double d2 = (double)p_196527_2_.getZ() + p_196527_3_.nextDouble() * 0.6D + 0.2D;
                  p_196527_1_.spawnParticle(Particles.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
               }

               p_196527_1_.getPendingBlockTicks().scheduleTick(p_196527_2_, p_196527_1_.getBlockState(p_196527_2_).getBlock(), 160);
            }
         }
      } else if (!p_196527_4_ && !isBurnedOut(p_196527_1_, p_196527_2_, false)) {
         p_196527_1_.setBlockState(p_196527_2_, p_196527_0_.with(LIT, Boolean.valueOf(true)), 3);
      }

   }

   /**
    * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
    * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
    * block, etc.
    */
   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (state.get(LIT) == this.shouldBeOff(worldIn, pos, state) && !worldIn.getPendingBlockTicks().isTickPending(pos, this)) {
         worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
      }

   }

   /**
    * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getStrongPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      return side == EnumFacing.DOWN ? blockState.getWeakPower(blockAccess, pos, side) : 0;
   }

   /**
    * Can this block provide power. Only wire currently seems to have this change based on its state.
    * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
    */
   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      if (stateIn.get(LIT)) {
         double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
         double d1 = (double)pos.getY() + 0.7D + (rand.nextDouble() - 0.5D) * 0.2D;
         double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
         worldIn.spawnParticle(RedstoneParticleData.REDSTONE_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D);
      }
   }

   /**
    * Amount of light emitted
    * @deprecated prefer calling {@link IBlockState#getLightValue()}
    */
   public int getLightValue(IBlockState state) {
      return state.get(LIT) ? super.getLightValue(state) : 0;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(LIT);
   }

   private static boolean isBurnedOut(World p_176598_0_, BlockPos worldIn, boolean pos) {
      List<BlockRedstoneTorch.Toggle> list = BURNED_TORCHES.get(p_176598_0_);
      if (list == null) {
         list = Lists.newArrayList();
         BURNED_TORCHES.put(p_176598_0_, list);
      }

      if (pos) {
         list.add(new BlockRedstoneTorch.Toggle(worldIn.toImmutable(), p_176598_0_.getGameTime()));
      }

      int i = 0;

      for(int j = 0; j < list.size(); ++j) {
         BlockRedstoneTorch.Toggle blockredstonetorch$toggle = list.get(j);
         if (blockredstonetorch$toggle.pos.equals(worldIn)) {
            ++i;
            if (i >= 8) {
               return true;
            }
         }
      }

      return false;
   }

   public static class Toggle {
      private final BlockPos pos;
      private final long time;

      public Toggle(BlockPos pos, long time) {
         this.pos = pos;
         this.time = time;
      }
   }
}