package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockPortal extends Block {
   public static final EnumProperty<EnumFacing.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
   protected static final VoxelShape X_AABB = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
   protected static final VoxelShape Z_AABB = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

   public BlockPortal(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(AXIS, EnumFacing.Axis.X));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      switch((EnumFacing.Axis)state.get(AXIS)) {
      case Z:
         return Z_AABB;
      case X:
      default:
         return X_AABB;
      }
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (worldIn.dimension.isSurfaceWorld() && worldIn.getGameRules().getBoolean("doMobSpawning") && random.nextInt(2000) < worldIn.getDifficulty().getId()) {
         int i = pos.getY();

         BlockPos blockpos;
         for(blockpos = pos; !worldIn.getBlockState(blockpos).isTopSolid() && blockpos.getY() > 0; blockpos = blockpos.down()) {
            ;
         }

         if (i > 0 && !worldIn.getBlockState(blockpos.up()).isNormalCube()) {
            Entity entity = EntityType.ZOMBIE_PIGMAN.spawnEntity(worldIn, (NBTTagCompound)null, (ITextComponent)null, (EntityPlayer)null, blockpos.up(), false, false);
            if (entity != null) {
               entity.timeUntilPortal = entity.getPortalCooldown();
            }
         }
      }

   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean trySpawnPortal(IWorld worldIn, BlockPos pos) {
      BlockPortal.Size blockportal$size = this.isPortal(worldIn, pos);
      if (blockportal$size != null && !net.minecraftforge.event.ForgeEventFactory.onTrySpawnPortal(worldIn, pos, blockportal$size)) {
         blockportal$size.placePortalBlocks();
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public BlockPortal.Size isPortal(IWorld p_201816_1_, BlockPos p_201816_2_) {
      BlockPortal.Size blockportal$size = new BlockPortal.Size(p_201816_1_, p_201816_2_, EnumFacing.Axis.X);
      if (blockportal$size.isValid() && blockportal$size.portalBlockCount == 0) {
         return blockportal$size;
      } else {
         BlockPortal.Size blockportal$size1 = new BlockPortal.Size(p_201816_1_, p_201816_2_, EnumFacing.Axis.Z);
         return blockportal$size1.isValid() && blockportal$size1.portalBlockCount == 0 ? blockportal$size1 : null;
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
      EnumFacing.Axis enumfacing$axis = facing.getAxis();
      EnumFacing.Axis enumfacing$axis1 = stateIn.get(AXIS);
      boolean flag = enumfacing$axis1 != enumfacing$axis && enumfacing$axis.isHorizontal();
      return !flag && facingState.getBlock() != this && !(new BlockPortal.Size(worldIn, currentPos, enumfacing$axis1)).func_208508_f() ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
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

   public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      if (!entityIn.isPassenger() && !entityIn.isBeingRidden() && entityIn.isNonBoss()) {
         entityIn.setPortal(pos);
      }

   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      if (rand.nextInt(100) == 0) {
         worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
      }

      for(int i = 0; i < 4; ++i) {
         double d0 = (double)((float)pos.getX() + rand.nextFloat());
         double d1 = (double)((float)pos.getY() + rand.nextFloat());
         double d2 = (double)((float)pos.getZ() + rand.nextFloat());
         double d3 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
         double d4 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
         double d5 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
         int j = rand.nextInt(2) * 2 - 1;
         if (worldIn.getBlockState(pos.west()).getBlock() != this && worldIn.getBlockState(pos.east()).getBlock() != this) {
            d0 = (double)pos.getX() + 0.5D + 0.25D * (double)j;
            d3 = (double)(rand.nextFloat() * 2.0F * (float)j);
         } else {
            d2 = (double)pos.getZ() + 0.5D + 0.25D * (double)j;
            d5 = (double)(rand.nextFloat() * 2.0F * (float)j);
         }

         worldIn.spawnParticle(Particles.PORTAL, d0, d1, d2, d3, d4, d5);
      }

   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      return ItemStack.EMPTY;
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public IBlockState rotate(IBlockState state, Rotation rot) {
      switch(rot) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch((EnumFacing.Axis)state.get(AXIS)) {
         case Z:
            return state.with(AXIS, EnumFacing.Axis.X);
         case X:
            return state.with(AXIS, EnumFacing.Axis.Z);
         default:
            return state;
         }
      default:
         return state;
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(AXIS);
   }

   public BlockPattern.PatternHelper createPatternHelper(IWorld worldIn, BlockPos p_181089_2_) {
      EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.Z;
      BlockPortal.Size blockportal$size = new BlockPortal.Size(worldIn, p_181089_2_, EnumFacing.Axis.X);
      LoadingCache<BlockPos, BlockWorldState> loadingcache = BlockPattern.createLoadingCache(worldIn, true);
      if (!blockportal$size.isValid()) {
         enumfacing$axis = EnumFacing.Axis.X;
         blockportal$size = new BlockPortal.Size(worldIn, p_181089_2_, EnumFacing.Axis.Z);
      }

      if (!blockportal$size.isValid()) {
         return new BlockPattern.PatternHelper(p_181089_2_, EnumFacing.NORTH, EnumFacing.UP, loadingcache, 1, 1, 1);
      } else {
         int[] aint = new int[EnumFacing.AxisDirection.values().length];
         EnumFacing enumfacing = blockportal$size.rightDir.rotateYCCW();
         BlockPos blockpos = blockportal$size.bottomLeft.up(blockportal$size.getHeight() - 1);

         for(EnumFacing.AxisDirection enumfacing$axisdirection : EnumFacing.AxisDirection.values()) {
            BlockPattern.PatternHelper blockpattern$patternhelper = new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == enumfacing$axisdirection ? blockpos : blockpos.offset(blockportal$size.rightDir, blockportal$size.getWidth() - 1), EnumFacing.getFacingFromAxis(enumfacing$axisdirection, enumfacing$axis), EnumFacing.UP, loadingcache, blockportal$size.getWidth(), blockportal$size.getHeight(), 1);

            for(int i = 0; i < blockportal$size.getWidth(); ++i) {
               for(int j = 0; j < blockportal$size.getHeight(); ++j) {
                  BlockWorldState blockworldstate = blockpattern$patternhelper.translateOffset(i, j, 1);
                  if (!blockworldstate.getBlockState().isAir()) {
                     ++aint[enumfacing$axisdirection.ordinal()];
                  }
               }
            }
         }

         EnumFacing.AxisDirection enumfacing$axisdirection1 = EnumFacing.AxisDirection.POSITIVE;

         for(EnumFacing.AxisDirection enumfacing$axisdirection2 : EnumFacing.AxisDirection.values()) {
            if (aint[enumfacing$axisdirection2.ordinal()] < aint[enumfacing$axisdirection1.ordinal()]) {
               enumfacing$axisdirection1 = enumfacing$axisdirection2;
            }
         }

         return new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == enumfacing$axisdirection1 ? blockpos : blockpos.offset(blockportal$size.rightDir, blockportal$size.getWidth() - 1), EnumFacing.getFacingFromAxis(enumfacing$axisdirection1, enumfacing$axis), EnumFacing.UP, loadingcache, blockportal$size.getWidth(), blockportal$size.getHeight(), 1);
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

   public static class Size {
      private final IWorld world;
      private final EnumFacing.Axis axis;
      private final EnumFacing rightDir;
      private final EnumFacing leftDir;
      private int portalBlockCount;
      private BlockPos bottomLeft;
      private int height;
      private int width;

      public Size(IWorld p_i48740_1_, BlockPos p_i48740_2_, EnumFacing.Axis p_i48740_3_) {
         this.world = p_i48740_1_;
         this.axis = p_i48740_3_;
         if (p_i48740_3_ == EnumFacing.Axis.X) {
            this.leftDir = EnumFacing.EAST;
            this.rightDir = EnumFacing.WEST;
         } else {
            this.leftDir = EnumFacing.NORTH;
            this.rightDir = EnumFacing.SOUTH;
         }

         for(BlockPos blockpos = p_i48740_2_; p_i48740_2_.getY() > blockpos.getY() - 21 && p_i48740_2_.getY() > 0 && this.func_196900_a(p_i48740_1_.getBlockState(p_i48740_2_.down())); p_i48740_2_ = p_i48740_2_.down()) {
            ;
         }

         int i = this.getDistanceUntilEdge(p_i48740_2_, this.leftDir) - 1;
         if (i >= 0) {
            this.bottomLeft = p_i48740_2_.offset(this.leftDir, i);
            this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
            if (this.width < 2 || this.width > 21) {
               this.bottomLeft = null;
               this.width = 0;
            }
         }

         if (this.bottomLeft != null) {
            this.height = this.calculatePortalHeight();
         }

      }

      protected int getDistanceUntilEdge(BlockPos p_180120_1_, EnumFacing p_180120_2_) {
         int i;
         for(i = 0; i < 22; ++i) {
            BlockPos blockpos = p_180120_1_.offset(p_180120_2_, i);
            if (!this.func_196900_a(this.world.getBlockState(blockpos)) || this.world.getBlockState(blockpos.down()).getBlock() != Blocks.OBSIDIAN) {
               break;
            }
         }

         Block block = this.world.getBlockState(p_180120_1_.offset(p_180120_2_, i)).getBlock();
         return block == Blocks.OBSIDIAN ? i : 0;
      }

      public int getHeight() {
         return this.height;
      }

      public int getWidth() {
         return this.width;
      }

      protected int calculatePortalHeight() {
         label56:
         for(this.height = 0; this.height < 21; ++this.height) {
            for(int i = 0; i < this.width; ++i) {
               BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
               IBlockState iblockstate = this.world.getBlockState(blockpos);
               if (!this.func_196900_a(iblockstate)) {
                  break label56;
               }

               Block block = iblockstate.getBlock();
               if (block == Blocks.NETHER_PORTAL) {
                  ++this.portalBlockCount;
               }

               if (i == 0) {
                  block = this.world.getBlockState(blockpos.offset(this.leftDir)).getBlock();
                  if (block != Blocks.OBSIDIAN) {
                     break label56;
                  }
               } else if (i == this.width - 1) {
                  block = this.world.getBlockState(blockpos.offset(this.rightDir)).getBlock();
                  if (block != Blocks.OBSIDIAN) {
                     break label56;
                  }
               }
            }
         }

         for(int j = 0; j < this.width; ++j) {
            if (this.world.getBlockState(this.bottomLeft.offset(this.rightDir, j).up(this.height)).getBlock() != Blocks.OBSIDIAN) {
               this.height = 0;
               break;
            }
         }

         if (this.height <= 21 && this.height >= 3) {
            return this.height;
         } else {
            this.bottomLeft = null;
            this.width = 0;
            this.height = 0;
            return 0;
         }
      }

      protected boolean func_196900_a(IBlockState p_196900_1_) {
         Block block = p_196900_1_.getBlock();
         return p_196900_1_.isAir() || block == Blocks.FIRE || block == Blocks.NETHER_PORTAL;
      }

      public boolean isValid() {
         return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
      }

      public void placePortalBlocks() {
         for(int i = 0; i < this.width; ++i) {
            BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);

            for(int j = 0; j < this.height; ++j) {
               this.world.setBlockState(blockpos.up(j), Blocks.NETHER_PORTAL.getDefaultState().with(BlockPortal.AXIS, this.axis), 18);
            }
         }

      }

      private boolean func_196899_f() {
         return this.portalBlockCount >= this.width * this.height;
      }

      public boolean func_208508_f() {
         return this.isValid() && this.func_196899_f();
      }
   }
}