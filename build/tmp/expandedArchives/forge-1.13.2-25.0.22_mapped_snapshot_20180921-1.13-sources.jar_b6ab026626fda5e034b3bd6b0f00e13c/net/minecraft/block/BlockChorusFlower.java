package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockChorusFlower extends Block {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_5;
   private final BlockChorusPlant field_196405_b;

   protected BlockChorusFlower(BlockChorusPlant p_i48429_1_, Block.Properties builder) {
      super(builder);
      this.field_196405_b = p_i48429_1_;
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.AIR;
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (!state.isValidPosition(worldIn, pos)) {
         worldIn.destroyBlock(pos, true);
      } else {
         BlockPos blockpos = pos.up();
         if (worldIn.isAirBlock(blockpos) && blockpos.getY() < 256) {
            int i = state.get(AGE);
            if (i < 5 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, blockpos, state, true)) {
               boolean flag = false;
               boolean flag1 = false;
               IBlockState iblockstate = worldIn.getBlockState(pos.down());
               Block block = iblockstate.getBlock();
               if (block == Blocks.END_STONE) {
                  flag = true;
               } else if (block == this.field_196405_b) {
                  int j = 1;

                  for(int k = 0; k < 4; ++k) {
                     Block block1 = worldIn.getBlockState(pos.down(j + 1)).getBlock();
                     if (block1 != this.field_196405_b) {
                        if (block1 == Blocks.END_STONE) {
                           flag1 = true;
                        }
                        break;
                     }

                     ++j;
                  }

                  if (j < 2 || j <= random.nextInt(flag1 ? 5 : 4)) {
                     flag = true;
                  }
               } else if (iblockstate.isAir()) {
                  flag = true;
               }

               if (flag && areAllNeighborsEmpty(worldIn, blockpos, (EnumFacing)null) && worldIn.isAirBlock(pos.up(2))) {
                  worldIn.setBlockState(pos, this.field_196405_b.makeConnections(worldIn, pos), 2);
                  this.placeGrownFlower(worldIn, blockpos, i);
               } else if (i < 4) {
                  int l = random.nextInt(4);
                  if (flag1) {
                     ++l;
                  }

                  boolean flag2 = false;

                  for(int i1 = 0; i1 < l; ++i1) {
                     EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(random);
                     BlockPos blockpos1 = pos.offset(enumfacing);
                     if (worldIn.isAirBlock(blockpos1) && worldIn.isAirBlock(blockpos1.down()) && areAllNeighborsEmpty(worldIn, blockpos1, enumfacing.getOpposite())) {
                        this.placeGrownFlower(worldIn, blockpos1, i + 1);
                        flag2 = true;
                     }
                  }

                  if (flag2) {
                     worldIn.setBlockState(pos, this.field_196405_b.makeConnections(worldIn, pos), 2);
                  } else {
                     this.placeDeadFlower(worldIn, pos);
                  }
               } else {
                  this.placeDeadFlower(worldIn, pos);
               }
               net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
            }
         }
      }
   }

   private void placeGrownFlower(World worldIn, BlockPos pos, int age) {
      worldIn.setBlockState(pos, this.getDefaultState().with(AGE, Integer.valueOf(age)), 2);
      worldIn.playEvent(1033, pos, 0);
   }

   private void placeDeadFlower(World worldIn, BlockPos pos) {
      worldIn.setBlockState(pos, this.getDefaultState().with(AGE, Integer.valueOf(5)), 2);
      worldIn.playEvent(1034, pos, 0);
   }

   private static boolean areAllNeighborsEmpty(IWorldReaderBase worldIn, BlockPos pos, @Nullable EnumFacing excludingSide) {
      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         if (enumfacing != excludingSide && !worldIn.isAirBlock(pos.offset(enumfacing))) {
            return false;
         }
      }

      return true;
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
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
      if (facing != EnumFacing.UP && !stateIn.isValidPosition(worldIn, currentPos)) {
         worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos.down());
      Block block = iblockstate.getBlock();
      if (block != this.field_196405_b && block != Blocks.END_STONE) {
         if (!iblockstate.isAir()) {
            return false;
         } else {
            boolean flag = false;

            for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
               IBlockState iblockstate1 = worldIn.getBlockState(pos.offset(enumfacing));
               if (iblockstate1.getBlock() == this.field_196405_b) {
                  if (flag) {
                     return false;
                  }

                  flag = true;
               } else if (!iblockstate1.isAir()) {
                  return false;
               }
            }

            return flag;
         }
      } else {
         return true;
      }
   }

   /**
    * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
    * Block.removedByPlayer
    */
   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      super.harvestBlock(worldIn, player, pos, state, te, stack);
      spawnAsEntity(worldIn, pos, new ItemStack(this));
   }

   protected ItemStack getSilkTouchDrop(IBlockState state) {
      return ItemStack.EMPTY;
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(AGE);
   }

   public static void generatePlant(IWorld worldIn, BlockPos pos, Random rand, int p_185603_3_) {
      worldIn.setBlockState(pos, ((BlockChorusPlant)Blocks.CHORUS_PLANT).makeConnections(worldIn, pos), 2);
      growTreeRecursive(worldIn, pos, rand, pos, p_185603_3_, 0);
   }

   private static void growTreeRecursive(IWorld worldIn, BlockPos p_185601_1_, Random rand, BlockPos p_185601_3_, int p_185601_4_, int p_185601_5_) {
      BlockChorusPlant blockchorusplant = (BlockChorusPlant)Blocks.CHORUS_PLANT;
      int i = rand.nextInt(4) + 1;
      if (p_185601_5_ == 0) {
         ++i;
      }

      for(int j = 0; j < i; ++j) {
         BlockPos blockpos = p_185601_1_.up(j + 1);
         if (!areAllNeighborsEmpty(worldIn, blockpos, (EnumFacing)null)) {
            return;
         }

         worldIn.setBlockState(blockpos, blockchorusplant.makeConnections(worldIn, blockpos), 2);
         worldIn.setBlockState(blockpos.down(), blockchorusplant.makeConnections(worldIn, blockpos.down()), 2);
      }

      boolean flag = false;
      if (p_185601_5_ < 4) {
         int l = rand.nextInt(4);
         if (p_185601_5_ == 0) {
            ++l;
         }

         for(int k = 0; k < l; ++k) {
            EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(rand);
            BlockPos blockpos1 = p_185601_1_.up(i).offset(enumfacing);
            if (Math.abs(blockpos1.getX() - p_185601_3_.getX()) < p_185601_4_ && Math.abs(blockpos1.getZ() - p_185601_3_.getZ()) < p_185601_4_ && worldIn.isAirBlock(blockpos1) && worldIn.isAirBlock(blockpos1.down()) && areAllNeighborsEmpty(worldIn, blockpos1, enumfacing.getOpposite())) {
               flag = true;
               worldIn.setBlockState(blockpos1, blockchorusplant.makeConnections(worldIn, blockpos1), 2);
               worldIn.setBlockState(blockpos1.offset(enumfacing.getOpposite()), blockchorusplant.makeConnections(worldIn, blockpos1.offset(enumfacing.getOpposite())), 2);
               growTreeRecursive(worldIn, blockpos1, rand, p_185601_3_, p_185601_4_, p_185601_5_ + 1);
            }
         }
      }

      if (!flag) {
         worldIn.setBlockState(p_185601_1_.up(i), Blocks.CHORUS_FLOWER.getDefaultState().with(AGE, Integer.valueOf(5)), 2);
      }

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
}