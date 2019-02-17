package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockTurtleEgg extends Block {
   private static final VoxelShape field_203172_c = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 12.0D, 7.0D, 12.0D);
   private static final VoxelShape field_206843_t = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D);
   public static final IntegerProperty HATCH = BlockStateProperties.HATCH_0_2;
   public static final IntegerProperty EGGS = BlockStateProperties.EGGS_1_4;

   public BlockTurtleEgg(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(HATCH, Integer.valueOf(0)).with(EGGS, Integer.valueOf(1)));
   }

   /**
    * Called when the given entity walks on this Block
    */
   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
      this.tryTrample(worldIn, pos, entityIn, 100);
      super.onEntityWalk(worldIn, pos, entityIn);
   }

   /**
    * Block's chance to react to a living entity falling on it.
    */
   public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
      if (!(entityIn instanceof EntityZombie)) {
         this.tryTrample(worldIn, pos, entityIn, 3);
      }

      super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
   }

   private void tryTrample(World p_203167_1_, BlockPos p_203167_2_, Entity p_203167_3_, int p_203167_4_) {
      if (!this.func_212570_a(p_203167_1_, p_203167_3_)) {
         super.onEntityWalk(p_203167_1_, p_203167_2_, p_203167_3_);
      } else {
         if (!p_203167_1_.isRemote && p_203167_1_.rand.nextInt(p_203167_4_) == 0) {
            this.removeOneEgg(p_203167_1_, p_203167_2_, p_203167_1_.getBlockState(p_203167_2_));
         }

      }
   }

   private void removeOneEgg(World p_203166_1_, BlockPos p_203166_2_, IBlockState p_203166_3_) {
      p_203166_1_.playSound((EntityPlayer)null, p_203166_2_, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + p_203166_1_.rand.nextFloat() * 0.2F);
      int i = p_203166_3_.get(EGGS);
      if (i <= 1) {
         p_203166_1_.destroyBlock(p_203166_2_, false);
      } else {
         p_203166_1_.setBlockState(p_203166_2_, p_203166_3_.with(EGGS, Integer.valueOf(i - 1)), 2);
         p_203166_1_.playEvent(2001, p_203166_2_, Block.getStateId(p_203166_3_));
      }

   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (this.canGrow(worldIn) && this.hasProperHabitat(worldIn, pos)) {
         int i = state.get(HATCH);
         if (i < 2) {
            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            worldIn.setBlockState(pos, state.with(HATCH, Integer.valueOf(i + 1)), 2);
         } else {
            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            worldIn.removeBlock(pos);
            if (!worldIn.isRemote) {
               for(int j = 0; j < state.get(EGGS); ++j) {
                  worldIn.playEvent(2001, pos, Block.getStateId(state));
                  EntityTurtle entityturtle = new EntityTurtle(worldIn);
                  entityturtle.setGrowingAge(-24000);
                  entityturtle.setHome(pos);
                  entityturtle.setLocationAndAngles((double)pos.getX() + 0.3D + (double)j * 0.2D, (double)pos.getY(), (double)pos.getZ() + 0.3D, 0.0F, 0.0F);
                  worldIn.spawnEntity(entityturtle);
               }
            }
         }
      }

   }

   private boolean hasProperHabitat(IBlockReader p_203168_1_, BlockPos p_203168_2_) {
      return p_203168_1_.getBlockState(p_203168_2_.down()).getBlock() == Blocks.SAND;
   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      if (this.hasProperHabitat(worldIn, pos) && !worldIn.isRemote) {
         worldIn.playEvent(2005, pos, 0);
      }

   }

   private boolean canGrow(World p_203169_1_) {
      float f = p_203169_1_.getCelestialAngle(1.0F);
      if ((double)f < 0.69D && (double)f > 0.65D) {
         return true;
      } else {
         return p_203169_1_.rand.nextInt(500) == 0;
      }
   }

   protected boolean canSilkHarvest() {
      return true;
   }

   /**
    * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
    * Block.removedByPlayer
    */
   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      super.harvestBlock(worldIn, player, pos, state, te, stack);
      this.removeOneEgg(worldIn, pos, state);
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.AIR;
   }

   public boolean isReplaceable(IBlockState state, BlockItemUseContext useContext) {
      return useContext.getItem().getItem() == this.asItem() && state.get(EGGS) < 4 ? true : super.isReplaceable(state, useContext);
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockState iblockstate = context.getWorld().getBlockState(context.getPos());
      return iblockstate.getBlock() == this ? iblockstate.with(EGGS, Integer.valueOf(Math.min(4, iblockstate.get(EGGS) + 1))) : super.getStateForPlacement(context);
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.get(EGGS) > 1 ? field_206843_t : field_203172_c;
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

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(HATCH, EGGS);
   }

   private boolean func_212570_a(World p_212570_1_, Entity p_212570_2_) {
      if (p_212570_2_ instanceof EntityTurtle) {
         return false;
      } else {
         return p_212570_2_ instanceof EntityLivingBase && !(p_212570_2_ instanceof EntityPlayer) ? p_212570_1_.getGameRules().getBoolean("mobGriefing") : true;
      }
   }
}