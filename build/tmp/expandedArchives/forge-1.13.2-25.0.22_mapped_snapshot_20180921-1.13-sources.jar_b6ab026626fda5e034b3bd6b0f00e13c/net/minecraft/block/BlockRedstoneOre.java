package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockRedstoneOre extends Block {
   public static final BooleanProperty LIT = BlockRedstoneTorch.LIT;

   public BlockRedstoneOre(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.getDefaultState().with(LIT, Boolean.valueOf(false)));
   }

   /**
    * Amount of light emitted
    * @deprecated prefer calling {@link IBlockState#getLightValue()}
    */
   public int getLightValue(IBlockState state) {
      return state.get(LIT) ? super.getLightValue(state) : 0;
   }

   public void onBlockClicked(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) {
      activate(state, worldIn, pos);
      super.onBlockClicked(state, worldIn, pos, player);
   }

   /**
    * Called when the given entity walks on this Block
    */
   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
      activate(worldIn.getBlockState(pos), worldIn, pos);
      super.onEntityWalk(worldIn, pos, entityIn);
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      activate(state, worldIn, pos);
      return super.onBlockActivated(state, worldIn, pos, player, hand, side, hitX, hitY, hitZ);
   }

   private static void activate(IBlockState p_196500_0_, World p_196500_1_, BlockPos p_196500_2_) {
      spawnParticles(p_196500_1_, p_196500_2_);
      if (!p_196500_0_.get(LIT)) {
         p_196500_1_.setBlockState(p_196500_2_, p_196500_0_.with(LIT, Boolean.valueOf(true)), 3);
      }

   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (state.get(LIT)) {
         worldIn.setBlockState(pos, state.with(LIT, Boolean.valueOf(false)), 3);
      }

   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.REDSTONE;
   }

   public int getItemsToDropCount(IBlockState state, int fortune, World worldIn, BlockPos pos, Random random) {
      return this.quantityDropped(state, random) + random.nextInt(fortune + 1);
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 4 + random.nextInt(2);
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
      super.dropBlockAsItemWithChance(state, worldIn, pos, chancePerItem, fortune);
      if (false && this.getItemDropped(state, worldIn, pos, fortune) != this) {
         int i = 1 + worldIn.rand.nextInt(5);
         this.dropXpOnBlockBreak(worldIn, pos, i);
      }
   }

   @Override
   public int getExpDrop(IBlockState state, net.minecraft.world.IWorldReader world, BlockPos pos, int fortune) {
      if (!(world instanceof World) || getItemDropped(state, (World)world, pos, fortune) != this)
         return 1 + RANDOM.nextInt(5);
      return 0;
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      if (stateIn.get(LIT)) {
         spawnParticles(worldIn, pos);
      }

   }

   private static void spawnParticles(World p_180691_0_, BlockPos worldIn) {
      double d0 = 0.5625D;
      Random random = p_180691_0_.rand;

      for(EnumFacing enumfacing : EnumFacing.values()) {
         BlockPos blockpos = worldIn.offset(enumfacing);
         if (!p_180691_0_.getBlockState(blockpos).isOpaqueCube(p_180691_0_, blockpos)) {
            EnumFacing.Axis enumfacing$axis = enumfacing.getAxis();
            double d1 = enumfacing$axis == EnumFacing.Axis.X ? 0.5D + 0.5625D * (double)enumfacing.getXOffset() : (double)random.nextFloat();
            double d2 = enumfacing$axis == EnumFacing.Axis.Y ? 0.5D + 0.5625D * (double)enumfacing.getYOffset() : (double)random.nextFloat();
            double d3 = enumfacing$axis == EnumFacing.Axis.Z ? 0.5D + 0.5625D * (double)enumfacing.getZOffset() : (double)random.nextFloat();
            p_180691_0_.spawnParticle(RedstoneParticleData.REDSTONE_DUST, (double)worldIn.getX() + d1, (double)worldIn.getY() + d2, (double)worldIn.getZ() + d3, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(LIT);
   }
}