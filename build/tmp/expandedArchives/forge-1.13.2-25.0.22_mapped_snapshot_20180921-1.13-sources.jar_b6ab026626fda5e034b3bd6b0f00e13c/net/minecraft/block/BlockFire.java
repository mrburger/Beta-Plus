package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.EndDimension;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockFire extends Block {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_15;
   public static final BooleanProperty NORTH = BlockSixWay.NORTH;
   public static final BooleanProperty EAST = BlockSixWay.EAST;
   public static final BooleanProperty SOUTH = BlockSixWay.SOUTH;
   public static final BooleanProperty WEST = BlockSixWay.WEST;
   public static final BooleanProperty UP = BlockSixWay.UP;
   private static final Map<EnumFacing, BooleanProperty> field_196449_B = BlockSixWay.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((p_199776_0_) -> {
      return p_199776_0_.getKey() != EnumFacing.DOWN;
   }).collect(Util.toMapCollector());
   private final Object2IntMap<Block> encouragements = new Object2IntOpenHashMap<>();
   private final Object2IntMap<Block> flammabilities = new Object2IntOpenHashMap<>();

   protected BlockFire(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(UP, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return VoxelShapes.empty();
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
      return this.isValidPosition(stateIn, worldIn, currentPos) ? this.getStateForPlacement(worldIn, currentPos).with(AGE, stateIn.get(AGE)) : Blocks.AIR.getDefaultState();
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getStateForPlacement(context.getWorld(), context.getPos());
   }

   public IBlockState getStateForPlacement(IBlockReader p_196448_1_, BlockPos p_196448_2_) {
      IBlockState iblockstate = p_196448_1_.getBlockState(p_196448_2_.down());
      if (!iblockstate.isTopSolid() && !this.canCatchFire(p_196448_1_, p_196448_2_, EnumFacing.UP)) {
         IBlockState iblockstate1 = this.getDefaultState();

         for(EnumFacing enumfacing : EnumFacing.values()) {
            BooleanProperty booleanproperty = field_196449_B.get(enumfacing);
            if (booleanproperty != null) {
               iblockstate1 = iblockstate1.with(booleanproperty, Boolean.valueOf(this.canCatchFire(p_196448_1_, p_196448_2_.offset(enumfacing), enumfacing.getOpposite())));
            }
         }

         return iblockstate1;
      } else {
         return this.getDefaultState();
      }
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).isTopSolid() || this.func_196447_a(worldIn, pos);
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 0;
   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReaderBase worldIn) {
      return 30;
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (worldIn.getGameRules().getBoolean("doFireTick")) {
         if (!worldIn.isAreaLoaded(pos, 2)) return; // Forge: prevent loading unloaded chunks when spreading fire
         if (!state.isValidPosition(worldIn, pos)) {
            worldIn.removeBlock(pos);
         }

         IBlockState other = worldIn.getBlockState(pos.down());
         boolean flag = other.isFireSource(worldIn, pos.down(), EnumFacing.UP);
         int i = state.get(AGE);
         if (!flag && worldIn.isRaining() && this.canDie(worldIn, pos) && random.nextFloat() < 0.2F + (float)i * 0.03F) {
            worldIn.removeBlock(pos);
         } else {
            int j = Math.min(15, i + random.nextInt(3) / 2);
            if (i != j) {
               state = state.with(AGE, Integer.valueOf(j));
               worldIn.setBlockState(pos, state, 4);
            }

            if (!flag) {
               worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn) + random.nextInt(10));
               if (!this.func_196447_a(worldIn, pos)) {
                  if (worldIn.getBlockState(pos.down()).getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) != BlockFaceShape.SOLID || i > 3) {
                     worldIn.removeBlock(pos);
                  }

                  return;
               }

               if (i == 15 && random.nextInt(4) == 0 && !this.canCatchFire(worldIn, pos.down(), EnumFacing.UP)) {
                  worldIn.removeBlock(pos);
                  return;
               }
            }

            boolean flag1 = worldIn.isBlockinHighHumidity(pos);
            int k = flag1 ? -50 : 0;
            this.tryCatchFire(worldIn, pos.east(), 300 + k, random, i, EnumFacing.WEST);
            this.tryCatchFire(worldIn, pos.west(), 300 + k, random, i, EnumFacing.EAST);
            this.tryCatchFire(worldIn, pos.down(), 250 + k, random, i, EnumFacing.UP);
            this.tryCatchFire(worldIn, pos.up(), 250 + k, random, i, EnumFacing.DOWN);
            this.tryCatchFire(worldIn, pos.north(), 300 + k, random, i, EnumFacing.SOUTH);
            this.tryCatchFire(worldIn, pos.south(), 300 + k, random, i, EnumFacing.NORTH);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int l = -1; l <= 1; ++l) {
               for(int i1 = -1; i1 <= 1; ++i1) {
                  for(int j1 = -1; j1 <= 4; ++j1) {
                     if (l != 0 || j1 != 0 || i1 != 0) {
                        int k1 = 100;
                        if (j1 > 1) {
                           k1 += (j1 - 1) * 100;
                        }

                        blockpos$mutableblockpos.setPos(pos).move(l, j1, i1);
                        int l1 = this.getNeighborEncouragement(worldIn, blockpos$mutableblockpos);
                        if (l1 > 0) {
                           int i2 = (l1 + 40 + worldIn.getDifficulty().getId() * 7) / (i + 30);
                           if (flag1) {
                              i2 /= 2;
                           }

                           if (i2 > 0 && random.nextInt(k1) <= i2 && (!worldIn.isRaining() || !this.canDie(worldIn, blockpos$mutableblockpos))) {
                              int j2 = Math.min(15, i + random.nextInt(5) / 4);
                              worldIn.setBlockState(blockpos$mutableblockpos, this.getStateForPlacement(worldIn, blockpos$mutableblockpos).with(AGE, Integer.valueOf(j2)), 3);
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   protected boolean canDie(World worldIn, BlockPos pos) {
      return worldIn.isRainingAt(pos) || worldIn.isRainingAt(pos.west()) || worldIn.isRainingAt(pos.east()) || worldIn.isRainingAt(pos.north()) || worldIn.isRainingAt(pos.south());
   }

   @Deprecated //Forge: Use IForgeBlockState.getFlammability
   public int getFlammability(Block blockIn) {
      return this.flammabilities.getInt(blockIn);
   }

   @Deprecated //Forge: Use IForgeBlockState.getFireSpreadSpeed
   public int getEncouragement(Block blockIn) {
      return this.encouragements.getInt(blockIn);
   }

   private void tryCatchFire(World worldIn, BlockPos pos, int chance, Random random, int age, EnumFacing face) {
      int i = worldIn.getBlockState(pos).getFlammability(worldIn, pos, face);
      if (random.nextInt(chance) < i) {
         IBlockState iblockstate = worldIn.getBlockState(pos);
         if (random.nextInt(age + 10) < 5 && !worldIn.isRainingAt(pos)) {
            int j = Math.min(age + random.nextInt(5) / 4, 15);
            worldIn.setBlockState(pos, this.getStateForPlacement(worldIn, pos).with(AGE, Integer.valueOf(j)), 3);
         } else {
            worldIn.removeBlock(pos);
         }

         Block block = iblockstate.getBlock();
         if (block instanceof BlockTNT) {
            ((BlockTNT)block).explode(worldIn, pos);
         }
      }

   }

   private boolean func_196447_a(IBlockReader p_196447_1_, BlockPos p_196447_2_) {
      for(EnumFacing enumfacing : EnumFacing.values()) {
         if (this.canCatchFire(p_196447_1_, p_196447_2_.offset(enumfacing), enumfacing.getOpposite())) {
            return true;
         }
      }

      return false;
   }

   private int getNeighborEncouragement(IWorldReaderBase worldIn, BlockPos pos) {
      if (!worldIn.isAirBlock(pos)) {
         return 0;
      } else {
         int i = 0;

         for(EnumFacing enumfacing : EnumFacing.values()) {
            i = Math.max(worldIn.getBlockState(pos.offset(enumfacing)).getFlammability(worldIn, pos.offset(enumfacing), enumfacing.getOpposite()), i);
         }

         return i;
      }
   }

   /**
    * Returns if this block is collidable. Only used by fire, although stairs return that of the block that the stair is
    * made of (though nobody's going to make fire stairs, right?)
    */
   public boolean isCollidable() {
      return false;
   }

   @Deprecated //Forge: Use canCatchFire with more context
   public boolean canBurn(IBlockState p_196446_1_) {
      return this.getEncouragement(p_196446_1_.getBlock()) > 0;
   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      if (oldState.getBlock() != state.getBlock()) {
         if (worldIn.dimension.getType() != DimensionType.OVERWORLD && worldIn.dimension.getType() != DimensionType.NETHER || !((BlockPortal)Blocks.NETHER_PORTAL).trySpawnPortal(worldIn, pos)) {
            if (!state.isValidPosition(worldIn, pos)) {
               worldIn.removeBlock(pos);
            } else {
               worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn) + worldIn.rand.nextInt(10));
            }
         }
      }
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      if (rand.nextInt(24) == 0) {
         worldIn.playSound((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
      }

      if (worldIn.getBlockState(pos.down()).getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) != BlockFaceShape.SOLID && !this.canCatchFire(worldIn, pos.down(), EnumFacing.UP)) {
         if (this.canCatchFire(worldIn, pos.west(), EnumFacing.EAST)) {
            for(int j = 0; j < 2; ++j) {
               double d3 = (double)pos.getX() + rand.nextDouble() * (double)0.1F;
               double d8 = (double)pos.getY() + rand.nextDouble();
               double d13 = (double)pos.getZ() + rand.nextDouble();
               worldIn.spawnParticle(Particles.LARGE_SMOKE, d3, d8, d13, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canCatchFire(worldIn, pos.east(), EnumFacing.WEST)) {
            for(int k = 0; k < 2; ++k) {
               double d4 = (double)(pos.getX() + 1) - rand.nextDouble() * (double)0.1F;
               double d9 = (double)pos.getY() + rand.nextDouble();
               double d14 = (double)pos.getZ() + rand.nextDouble();
               worldIn.spawnParticle(Particles.LARGE_SMOKE, d4, d9, d14, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canCatchFire(worldIn, pos.north(), EnumFacing.SOUTH)) {
            for(int l = 0; l < 2; ++l) {
               double d5 = (double)pos.getX() + rand.nextDouble();
               double d10 = (double)pos.getY() + rand.nextDouble();
               double d15 = (double)pos.getZ() + rand.nextDouble() * (double)0.1F;
               worldIn.spawnParticle(Particles.LARGE_SMOKE, d5, d10, d15, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canCatchFire(worldIn, pos.south(), EnumFacing.NORTH)) {
            for(int i1 = 0; i1 < 2; ++i1) {
               double d6 = (double)pos.getX() + rand.nextDouble();
               double d11 = (double)pos.getY() + rand.nextDouble();
               double d16 = (double)(pos.getZ() + 1) - rand.nextDouble() * (double)0.1F;
               worldIn.spawnParticle(Particles.LARGE_SMOKE, d6, d11, d16, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canCatchFire(worldIn, pos.up(), EnumFacing.DOWN)) {
            for(int j1 = 0; j1 < 2; ++j1) {
               double d7 = (double)pos.getX() + rand.nextDouble();
               double d12 = (double)(pos.getY() + 1) - rand.nextDouble() * (double)0.1F;
               double d17 = (double)pos.getZ() + rand.nextDouble();
               worldIn.spawnParticle(Particles.LARGE_SMOKE, d7, d12, d17, 0.0D, 0.0D, 0.0D);
            }
         }
      } else {
         for(int i = 0; i < 3; ++i) {
            double d0 = (double)pos.getX() + rand.nextDouble();
            double d1 = (double)pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
            double d2 = (double)pos.getZ() + rand.nextDouble();
            worldIn.spawnParticle(Particles.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
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

   public void setFireInfo(Block blockIn, int encouragement, int flammability) {
      if (blockIn == Blocks.AIR) throw new IllegalArgumentException("Tried to set air on fire... This is bad.");
      this.encouragements.put(blockIn, encouragement);
      this.flammabilities.put(blockIn, flammability);
   }

   /**
    * Side sensitive version that calls the block function.
    *
    * @param world The current world
    * @param pos Block position
    * @param face The side the fire is coming from
    * @return True if the face can catch fire.
    */
   public boolean canCatchFire(IBlockReader world, BlockPos pos, EnumFacing face) {
      return world.getBlockState(pos).isFlammable(world, pos, face);
   }

   public static void init() {
      BlockFire blockfire = (BlockFire)Blocks.FIRE;
      blockfire.setFireInfo(Blocks.OAK_PLANKS, 5, 20);
      blockfire.setFireInfo(Blocks.SPRUCE_PLANKS, 5, 20);
      blockfire.setFireInfo(Blocks.BIRCH_PLANKS, 5, 20);
      blockfire.setFireInfo(Blocks.JUNGLE_PLANKS, 5, 20);
      blockfire.setFireInfo(Blocks.ACACIA_PLANKS, 5, 20);
      blockfire.setFireInfo(Blocks.DARK_OAK_PLANKS, 5, 20);
      blockfire.setFireInfo(Blocks.OAK_SLAB, 5, 20);
      blockfire.setFireInfo(Blocks.SPRUCE_SLAB, 5, 20);
      blockfire.setFireInfo(Blocks.BIRCH_SLAB, 5, 20);
      blockfire.setFireInfo(Blocks.JUNGLE_SLAB, 5, 20);
      blockfire.setFireInfo(Blocks.ACACIA_SLAB, 5, 20);
      blockfire.setFireInfo(Blocks.DARK_OAK_SLAB, 5, 20);
      blockfire.setFireInfo(Blocks.OAK_FENCE_GATE, 5, 20);
      blockfire.setFireInfo(Blocks.SPRUCE_FENCE_GATE, 5, 20);
      blockfire.setFireInfo(Blocks.BIRCH_FENCE_GATE, 5, 20);
      blockfire.setFireInfo(Blocks.JUNGLE_FENCE_GATE, 5, 20);
      blockfire.setFireInfo(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
      blockfire.setFireInfo(Blocks.ACACIA_FENCE_GATE, 5, 20);
      blockfire.setFireInfo(Blocks.OAK_FENCE, 5, 20);
      blockfire.setFireInfo(Blocks.SPRUCE_FENCE, 5, 20);
      blockfire.setFireInfo(Blocks.BIRCH_FENCE, 5, 20);
      blockfire.setFireInfo(Blocks.JUNGLE_FENCE, 5, 20);
      blockfire.setFireInfo(Blocks.DARK_OAK_FENCE, 5, 20);
      blockfire.setFireInfo(Blocks.ACACIA_FENCE, 5, 20);
      blockfire.setFireInfo(Blocks.OAK_STAIRS, 5, 20);
      blockfire.setFireInfo(Blocks.BIRCH_STAIRS, 5, 20);
      blockfire.setFireInfo(Blocks.SPRUCE_STAIRS, 5, 20);
      blockfire.setFireInfo(Blocks.JUNGLE_STAIRS, 5, 20);
      blockfire.setFireInfo(Blocks.ACACIA_STAIRS, 5, 20);
      blockfire.setFireInfo(Blocks.DARK_OAK_STAIRS, 5, 20);
      blockfire.setFireInfo(Blocks.OAK_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.SPRUCE_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.BIRCH_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.JUNGLE_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.ACACIA_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.DARK_OAK_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_OAK_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_OAK_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.OAK_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.SPRUCE_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.BIRCH_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.JUNGLE_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.ACACIA_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.DARK_OAK_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.OAK_LEAVES, 30, 60);
      blockfire.setFireInfo(Blocks.SPRUCE_LEAVES, 30, 60);
      blockfire.setFireInfo(Blocks.BIRCH_LEAVES, 30, 60);
      blockfire.setFireInfo(Blocks.JUNGLE_LEAVES, 30, 60);
      blockfire.setFireInfo(Blocks.ACACIA_LEAVES, 30, 60);
      blockfire.setFireInfo(Blocks.DARK_OAK_LEAVES, 30, 60);
      blockfire.setFireInfo(Blocks.BOOKSHELF, 30, 20);
      blockfire.setFireInfo(Blocks.TNT, 15, 100);
      blockfire.setFireInfo(Blocks.GRASS, 60, 100);
      blockfire.setFireInfo(Blocks.FERN, 60, 100);
      blockfire.setFireInfo(Blocks.DEAD_BUSH, 60, 100);
      blockfire.setFireInfo(Blocks.SUNFLOWER, 60, 100);
      blockfire.setFireInfo(Blocks.LILAC, 60, 100);
      blockfire.setFireInfo(Blocks.ROSE_BUSH, 60, 100);
      blockfire.setFireInfo(Blocks.PEONY, 60, 100);
      blockfire.setFireInfo(Blocks.TALL_GRASS, 60, 100);
      blockfire.setFireInfo(Blocks.LARGE_FERN, 60, 100);
      blockfire.setFireInfo(Blocks.DANDELION, 60, 100);
      blockfire.setFireInfo(Blocks.POPPY, 60, 100);
      blockfire.setFireInfo(Blocks.BLUE_ORCHID, 60, 100);
      blockfire.setFireInfo(Blocks.ALLIUM, 60, 100);
      blockfire.setFireInfo(Blocks.AZURE_BLUET, 60, 100);
      blockfire.setFireInfo(Blocks.RED_TULIP, 60, 100);
      blockfire.setFireInfo(Blocks.ORANGE_TULIP, 60, 100);
      blockfire.setFireInfo(Blocks.WHITE_TULIP, 60, 100);
      blockfire.setFireInfo(Blocks.PINK_TULIP, 60, 100);
      blockfire.setFireInfo(Blocks.OXEYE_DAISY, 60, 100);
      blockfire.setFireInfo(Blocks.WHITE_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.ORANGE_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.MAGENTA_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.LIGHT_BLUE_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.YELLOW_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.LIME_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.PINK_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.GRAY_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.LIGHT_GRAY_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.CYAN_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.PURPLE_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.BLUE_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.BROWN_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.GREEN_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.RED_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.BLACK_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.VINE, 15, 100);
      blockfire.setFireInfo(Blocks.COAL_BLOCK, 5, 5);
      blockfire.setFireInfo(Blocks.HAY_BLOCK, 60, 20);
      blockfire.setFireInfo(Blocks.WHITE_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.ORANGE_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.MAGENTA_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.LIGHT_BLUE_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.YELLOW_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.LIME_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.PINK_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.GRAY_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.LIGHT_GRAY_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.CYAN_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.PURPLE_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.BLUE_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.BROWN_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.GREEN_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.RED_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.BLACK_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.DRIED_KELP_BLOCK, 30, 60);
   }
}