package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockBubbleColumn extends Block implements IBucketPickupHandler {
   public static final BooleanProperty DRAG = BlockStateProperties.DRAG;

   public BlockBubbleColumn(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(DRAG, Boolean.valueOf(true)));
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      IBlockState iblockstate = worldIn.getBlockState(pos.up());
      if (iblockstate.isAir()) {
         entityIn.onEnterBubbleColumnWithAirAbove(state.get(DRAG));
         if (!worldIn.isRemote) {
            WorldServer worldserver = (WorldServer)worldIn;

            for(int i = 0; i < 2; ++i) {
               worldserver.spawnParticle(Particles.SPLASH, (double)((float)pos.getX() + worldIn.rand.nextFloat()), (double)(pos.getY() + 1), (double)((float)pos.getZ() + worldIn.rand.nextFloat()), 1, 0.0D, 0.0D, 0.0D, 1.0D);
               worldserver.spawnParticle(Particles.BUBBLE, (double)((float)pos.getX() + worldIn.rand.nextFloat()), (double)(pos.getY() + 1), (double)((float)pos.getZ() + worldIn.rand.nextFloat()), 1, 0.0D, 0.01D, 0.0D, 0.2D);
            }
         }
      } else {
         entityIn.onEnterBubbleColumn(state.get(DRAG));
      }

   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      placeBubbleColumn(worldIn, pos.up(), getDrag(worldIn, pos.down()));
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      placeBubbleColumn(worldIn, pos.up(), getDrag(worldIn, pos));
   }

   public IFluidState getFluidState(IBlockState state) {
      return Fluids.WATER.getStillFluidState(false);
   }

   public static void placeBubbleColumn(IWorld p_203159_0_, BlockPos p_203159_1_, boolean drag) {
      if (canHoldBubbleColumn(p_203159_0_, p_203159_1_)) {
         p_203159_0_.setBlockState(p_203159_1_, Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, Boolean.valueOf(drag)), 2);
      }

   }

   public static boolean canHoldBubbleColumn(IWorld p_208072_0_, BlockPos p_208072_1_) {
      IFluidState ifluidstate = p_208072_0_.getFluidState(p_208072_1_);
      return p_208072_0_.getBlockState(p_208072_1_).getBlock() == Blocks.WATER && ifluidstate.getLevel() >= 8 && ifluidstate.isSource();
   }

   private static boolean getDrag(IBlockReader p_203157_0_, BlockPos p_203157_1_) {
      IBlockState iblockstate = p_203157_0_.getBlockState(p_203157_1_);
      Block block = iblockstate.getBlock();
      if (block == Blocks.BUBBLE_COLUMN) {
         return iblockstate.get(DRAG);
      } else {
         return block != Blocks.SOUL_SAND;
      }
   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReaderBase worldIn) {
      return 5;
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      double d0 = (double)pos.getX();
      double d1 = (double)pos.getY();
      double d2 = (double)pos.getZ();
      if (stateIn.get(DRAG)) {
         worldIn.addOptionalParticle(Particles.CURRENT_DOWN, d0 + 0.5D, d1 + 0.8D, d2, 0.0D, 0.0D, 0.0D);
         if (rand.nextInt(200) == 0) {
            worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
         }
      } else {
         worldIn.addOptionalParticle(Particles.BUBBLE_COLUMN_UP, d0 + 0.5D, d1, d2 + 0.5D, 0.0D, 0.04D, 0.0D);
         worldIn.addOptionalParticle(Particles.BUBBLE_COLUMN_UP, d0 + (double)rand.nextFloat(), d1 + (double)rand.nextFloat(), d2 + (double)rand.nextFloat(), 0.0D, 0.04D, 0.0D);
         if (rand.nextInt(200) == 0) {
            worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
         }
      }

   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    *  
    * @param facingState The state that is currently at the position offset of the provided face to the stateIn at
    * currentPos
    */
   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (!stateIn.isValidPosition(worldIn, currentPos)) {
         return Blocks.WATER.getDefaultState();
      } else {
         if (facing == EnumFacing.DOWN) {
            worldIn.setBlockState(currentPos, Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, Boolean.valueOf(getDrag(worldIn, facingPos))), 2);
         } else if (facing == EnumFacing.UP && facingState.getBlock() != Blocks.BUBBLE_COLUMN && canHoldBubbleColumn(worldIn, facingPos)) {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, this.tickRate(worldIn));
         }

         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      }
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos.down()).getBlock();
      return block == Blocks.BUBBLE_COLUMN || block == Blocks.MAGMA_BLOCK || block == Blocks.SOUL_SAND;
   }

   /**
    * Returns if this block is collidable. Only used by fire, although stairs return that of the block that the stair is
    * made of (though nobody's going to make fire stairs, right?)
    */
   public boolean isCollidable() {
      return false;
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 0;
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   /**
    * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
    * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
    * <p>
    * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that does
    * not fit the other descriptions and will generally cause other things not to connect to the face.
    * 
    * @return an approximation of the form of the given face
    * @deprecated call via {@link IBlockState#getBlockFaceShape(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(DRAG);
   }

   public Fluid pickupFluid(IWorld worldIn, BlockPos pos, IBlockState state) {
      worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
      return Fluids.WATER;
   }
}