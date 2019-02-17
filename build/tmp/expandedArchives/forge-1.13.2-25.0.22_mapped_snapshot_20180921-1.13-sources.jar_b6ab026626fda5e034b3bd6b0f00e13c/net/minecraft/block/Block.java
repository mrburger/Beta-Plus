package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.trees.AcaciaTree;
import net.minecraft.block.trees.BirchTree;
import net.minecraft.block.trees.DarkOakTree;
import net.minecraft.block.trees.JungleTree;
import net.minecraft.block.trees.OakTree;
import net.minecraft.block.trees.SpruceTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.StatList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block extends net.minecraftforge.registries.ForgeRegistryEntry<Block> implements IItemProvider, net.minecraftforge.common.extensions.IForgeBlock {
   protected static final Logger LOGGER = LogManager.getLogger();
   @Deprecated //Forge: Do not use, use GameRegistry
   public static final ObjectIntIdentityMap<IBlockState> BLOCK_STATE_IDS = net.minecraftforge.registries.GameData.getBlockStateIDMap();
   private static final EnumFacing[] field_212556_a = new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.DOWN, EnumFacing.UP};
   /** Amount of light emitted */
   protected final int lightValue;
   /** Indicates how many hits it takes to break a block. */
   protected final float blockHardness;
   /** Indicates how much this block can resist explosions */
   protected final float blockResistance;
   /**
    * Flags whether or not this block is of a type that needs random ticking. Ref-counted by ExtendedBlockStorage in
    * order to broadly cull a chunk from the random chunk update list for efficiency's sake.
    */
   protected final boolean needsRandomTick;
   protected final SoundType soundType;
   protected final Material material;
   /** The Block's MapColor */
   protected final MaterialColor blockMapColor;
   /** Determines how much velocity is maintained while moving on top of this block */
   private final float slipperiness;
   protected final StateContainer<Block, IBlockState> stateContainer;
   private IBlockState defaultState;
   protected final boolean blocksMovement;
   private final boolean variableOpacity;
   @Nullable
   private String translationKey;
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>> SHOULD_SIDE_RENDER_CACHE = ThreadLocal.withInitial(() -> {
      Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>(200) {
         protected void rehash(int p_rehash_1_) {
         }
      };
      object2bytelinkedopenhashmap.defaultReturnValue((byte)127);
      return object2bytelinkedopenhashmap;
   });

   public static int getStateId(@Nullable IBlockState state) {
      if (state == null) {
         return 0;
      } else {
         int i = BLOCK_STATE_IDS.get(state);
         return i == -1 ? 0 : i;
      }
   }

   public static IBlockState getStateById(int id) {
      IBlockState iblockstate = BLOCK_STATE_IDS.getByValue(id);
      return iblockstate == null ? Blocks.AIR.getDefaultState() : iblockstate;
   }

   public static Block getBlockFromItem(@Nullable Item itemIn) {
      return itemIn instanceof ItemBlock ? ((ItemBlock)itemIn).getBlock() : Blocks.AIR;
   }

   public static IBlockState nudgeEntitiesWithNewState(IBlockState oldState, IBlockState newState, World worldIn, BlockPos pos) {
      VoxelShape voxelshape = VoxelShapes.combine(oldState.getCollisionShape(worldIn, pos), newState.getCollisionShape(worldIn, pos), IBooleanFunction.ONLY_SECOND).withOffset((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());

      for(Entity entity : worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, voxelshape.getBoundingBox())) {
         double d0 = VoxelShapes.func_212437_a(EnumFacing.Axis.Y, entity.getBoundingBox().offset(0.0D, 1.0D, 0.0D), Stream.of(voxelshape), -1.0D);
         entity.setPositionAndUpdate(entity.posX, entity.posY + 1.0D + d0, entity.posZ);
      }

      return newState;
   }

   public static VoxelShape makeCuboidShape(double x1, double y1, double z1, double x2, double y2, double z2) {
      return VoxelShapes.create(x1 / 16.0D, y1 / 16.0D, z1 / 16.0D, x2 / 16.0D, y2 / 16.0D, z2 / 16.0D);
   }

   /**
    * @return true if the passed entity is allowed to spawn on this block.
    * @deprecated prefer calling {@link IBlockState#canEntitySpawn(Entity)}
    */
   @Deprecated
   public boolean canEntitySpawn(IBlockState state, Entity entityIn) {
      return true;
   }

   @Deprecated
   public boolean isAir(IBlockState state) {
      return false;
   }

   /**
    * Amount of light emitted
    * @deprecated prefer calling {@link IBlockState#getLightValue()}
    */
   @Deprecated
   public int getLightValue(IBlockState state) {
      return this.lightValue;
   }

   /**
    * Get a material of block
    * @deprecated call via {@link IBlockState#getMaterial()} whenever possible. Implementing/overriding is fine.
    */
   @Deprecated
   public Material getMaterial(IBlockState state) {
      return this.material;
   }

   /**
    * Get the MapColor for this Block and the given BlockState
    * @deprecated call via {@link IBlockState#getMapColor(IBlockAccess,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   @Deprecated
   public MaterialColor getMapColor(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return this.blockMapColor;
   }

   /**
    * For all neighbors, have them react to this block's existence, potentially updating their states as needed. For
    * example, fences make their connections to this block if possible and observers pulse if this block was placed in
    * front of their detector
    */
   @Deprecated
   public void updateNeighbors(IBlockState stateIn, IWorld worldIn, BlockPos pos, int flags) {
      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(EnumFacing enumfacing : field_212556_a) {
            blockpos$pooledmutableblockpos.setPos(pos).move(enumfacing);
            IBlockState iblockstate = worldIn.getBlockState(blockpos$pooledmutableblockpos);
            IBlockState iblockstate1 = iblockstate.updatePostPlacement(enumfacing.getOpposite(), stateIn, worldIn, blockpos$pooledmutableblockpos, pos);
            replaceBlock(iblockstate, iblockstate1, worldIn, blockpos$pooledmutableblockpos, flags);
         }
      }

   }

   public boolean isIn(Tag<Block> tagIn) {
      return tagIn.contains(this);
   }

   /**
    * With the provided block state, performs neighbor checks for all neighboring blocks to get an "adjusted" blockstate
    * for placement in the world, if the current state is not valid.
    */
   public static IBlockState getValidBlockForPosition(IBlockState currentState, IWorld worldIn, BlockPos pos) {
      IBlockState iblockstate = currentState;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(EnumFacing enumfacing : field_212556_a) {
         blockpos$mutableblockpos.setPos(pos).move(enumfacing);
         iblockstate = iblockstate.updatePostPlacement(enumfacing, worldIn.getBlockState(blockpos$mutableblockpos), worldIn, pos, blockpos$mutableblockpos);
      }

      return iblockstate;
   }

   /**
    * Replaces oldState with newState, possibly playing effects and creating drops. Flags are as in {@link
    * World#setBlockState}
    *  
    * @param flags Traditional block change flags
    */
   public static void replaceBlock(IBlockState oldState, IBlockState newState, IWorld worldIn, BlockPos pos, int flags) {
      if (newState != oldState) {
         if (newState.isAir()) {
            if (!worldIn.isRemote()) {
               worldIn.destroyBlock(pos, (flags & 32) == 0);
            }
         } else {
            worldIn.setBlockState(pos, newState, flags & -33);
         }
      }

   }

   /**
    * performs updates on diagonal neighbors of the target position and passes in the flags. The flags can be referenced
    * from the docs for {@link IWorldWriter#setBlockState(IBlockState, BlockPos, int)}.
    */
   @Deprecated
   public void updateDiagonalNeighbors(IBlockState state, IWorld worldIn, BlockPos pos, int flags) {
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
   @Deprecated
   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return stateIn;
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   @Deprecated
   public IBlockState rotate(IBlockState state, Rotation rot) {
      return state;
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   @Deprecated
   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      return state;
   }

   public Block(Block.Properties properties) {
      StateContainer.Builder<Block, IBlockState> builder = new StateContainer.Builder<>(this);
      this.fillStateContainer(builder);
      this.stateContainer = builder.create(BlockState::new);
      this.setDefaultState(this.stateContainer.getBaseState());
      this.material = properties.material;
      this.blockMapColor = properties.mapColor;
      this.blocksMovement = properties.blocksMovement;
      this.soundType = properties.soundType;
      this.lightValue = properties.lightValue;
      this.blockResistance = properties.resistance;
      this.blockHardness = properties.hardness;
      this.needsRandomTick = properties.needsRandomTick;
      this.slipperiness = properties.slipperiness;
      this.variableOpacity = properties.variableOpacity;
   }

   protected static boolean isExceptionBlockForAttaching(Block attachBlock) {
      return attachBlock instanceof BlockShulkerBox || attachBlock instanceof BlockLeaves || attachBlock.isIn(BlockTags.TRAPDOORS) || attachBlock instanceof BlockStainedGlass || attachBlock == Blocks.BEACON || attachBlock == Blocks.CAULDRON || attachBlock == Blocks.GLASS || attachBlock == Blocks.GLOWSTONE || attachBlock == Blocks.ICE || attachBlock == Blocks.SEA_LANTERN || attachBlock == Blocks.CONDUIT;
   }

   public static boolean isExceptBlockForAttachWithPiston(Block attachBlock) {
      return isExceptionBlockForAttaching(attachBlock) || attachBlock == Blocks.PISTON || attachBlock == Blocks.STICKY_PISTON || attachBlock == Blocks.PISTON_HEAD;
   }

   /**
    * Indicate if a material is a normal solid opaque cube
    * @deprecated call via {@link IBlockState#isBlockNormalCube()} whenever possible. Implementing/overriding is fine.
    */
   @Deprecated
   public boolean isBlockNormalCube(IBlockState state) {
      return state.getMaterial().blocksMovement() && state.isFullCube();
   }

   /**
    * Used for nearly all game logic (non-rendering) purposes. Use Forge-provided isNormalCube(IBlockAccess, BlockPos)
    * instead.
    * @deprecated call via {@link IBlockState#isNormalCube()} whenever possible. Implementing/overriding is fine.
    */
   @Deprecated
   public boolean isNormalCube(IBlockState state) {
      return state.getMaterial().isOpaque() && state.isFullCube() && !state.canProvidePower();
   }

   /**
    * @deprecated call via {@link IBlockState#causesSuffocation()} whenever possible. Implementing/overriding is fine.
    */
   @Deprecated
   public boolean causesSuffocation(IBlockState state) {
      return this.material.blocksMovement() && state.isFullCube();
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   @Deprecated
   public boolean isFullCube(IBlockState state) {
      return true;
   }

   /**
    * Determines if the block is solid enough on the top side to support other blocks, like redstone components.
    * @deprecated prefer calling {@link IBlockState#isTopSolid()} wherever possible
    */
   @Deprecated
   public boolean isTopSolid(IBlockState state) {
      return state.getMaterial().isOpaque() && state.isFullCube();
   }

   /**
    * @deprecated call via {@link IBlockState#hasCustomBreakingProgress()} whenever possible. Implementing/overriding is
    * fine.
    */
   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomBreakingProgress(IBlockState state) {
      return false;
   }

   @Deprecated
   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      switch(type) {
      case LAND:
         return !isOpaque(this.getCollisionShape(state, worldIn, pos));
      case WATER:
         return worldIn.getFluidState(pos).isTagged(FluidTags.WATER);
      case AIR:
         return !isOpaque(this.getCollisionShape(state, worldIn, pos));
      default:
         return false;
      }
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   @Deprecated
   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   @Deprecated
   public boolean isReplaceable(IBlockState state, BlockItemUseContext useContext) {
      return state.getMaterial().isReplaceable() && useContext.getItem().getItem() != this.asItem();
   }

   /**
    * @deprecated call via {@link IBlockState#getBlockHardness(World,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   @Deprecated
   public float getBlockHardness(IBlockState blockState, IBlockReader worldIn, BlockPos pos) {
      return this.blockHardness;
   }

   /**
    * Returns whether or not this block is of a type that needs random ticking. Called for ref-counting purposes by
    * ExtendedBlockStorage in order to broadly cull a chunk from the random chunk update list for efficiency's sake.
    */
   public boolean getTickRandomly(IBlockState p_149653_1_) {
      return this.needsRandomTick;
   }

   @Deprecated //Forge: New State sensitive version.
   public boolean hasTileEntity() {
      return hasTileEntity(getDefaultState());
   }

   @Deprecated
   public boolean needsPostProcessing(IBlockState p_201783_1_, IBlockReader worldIn, BlockPos pos) {
      return false;
   }

   /**
    * @deprecated call via {@link IBlockState#getPackedLightmapCoords(IBlockAccess,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public int getPackedLightmapCoords(IBlockState state, IWorldReader source, BlockPos pos) {
      int i = source.getCombinedLight(pos, state.getLightValue(source, pos));
      if (i == 0 && state.getBlock() instanceof BlockSlab) {
         pos = pos.down();
         state = source.getBlockState(pos);
         return source.getCombinedLight(pos, state.getLightValue(source, pos));
      } else {
         return i;
      }
   }

   /**
    * @deprecated call via {@link IBlockState#shouldSideBeRendered(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   @OnlyIn(Dist.CLIENT)
   public static boolean shouldSideBeRendered(IBlockState adjacentState, IBlockReader blockState, BlockPos blockAccess, EnumFacing pos) {
      BlockPos blockpos = blockAccess.offset(pos);
      IBlockState iblockstate = blockState.getBlockState(blockpos);
      if (adjacentState.isSideInvisible(iblockstate, pos)) {
         return false;
      } else if (iblockstate.isSolid()) {
         Block.RenderSideCacheKey block$rendersidecachekey = new Block.RenderSideCacheKey(adjacentState, iblockstate, pos);
         Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = SHOULD_SIDE_RENDER_CACHE.get();
         byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$rendersidecachekey);
         if (b0 != 127) {
            return b0 != 0;
         } else {
            VoxelShape voxelshape = adjacentState.getRenderShape(blockState, blockAccess);
            VoxelShape voxelshape1 = iblockstate.getRenderShape(blockState, blockpos);
            boolean flag = !VoxelShapes.isCubeSideCovered(voxelshape, voxelshape1, pos);
            if (object2bytelinkedopenhashmap.size() == 200) {
               object2bytelinkedopenhashmap.removeLastByte();
            }

            object2bytelinkedopenhashmap.putAndMoveToFirst(block$rendersidecachekey, (byte)(flag ? 1 : 0));
            return flag;
         }
      } else {
         return true;
      }
   }

   @Deprecated
   public boolean isSolid(IBlockState state) {
      return this.blocksMovement && state.getBlock().getRenderLayer() == BlockRenderLayer.SOLID;
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public boolean isSideInvisible(IBlockState state, IBlockState adjacentBlockState, EnumFacing side) {
      return false;
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
   @Deprecated
   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.SOLID;
   }

   @Deprecated
   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return VoxelShapes.fullCube();
   }

   @Deprecated
   public VoxelShape getCollisionShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return this.blocksMovement ? state.getShape(worldIn, pos) : VoxelShapes.empty();
   }

   @Deprecated
   public VoxelShape getRenderShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.getShape(worldIn, pos);
   }

   @Deprecated
   public VoxelShape getRaytraceShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return VoxelShapes.empty();
   }

   public static boolean doesSideFillSquare(VoxelShape shape, EnumFacing side) {
      VoxelShape voxelshape = shape.func_212434_a(side);
      return isOpaque(voxelshape);
   }

   /**
    * Gets whether the provided {@link VoxelShape} is opaque
    */
   public static boolean isOpaque(VoxelShape shape) {
      return !VoxelShapes.compare(VoxelShapes.fullCube(), shape, IBooleanFunction.ONLY_FIRST);
   }

   @Deprecated
   public final boolean isOpaqueCube(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      boolean flag = state.isSolid();
      VoxelShape voxelshape = flag ? state.getRenderShape(worldIn, pos) : VoxelShapes.empty();
      return isOpaque(voxelshape);
   }

   public boolean propagatesSkylightDown(IBlockState state, IBlockReader reader, BlockPos pos) {
      return !isOpaque(state.getShape(reader, pos)) && state.getFluidState().isEmpty();
   }

   @Deprecated
   public int getOpacity(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      if (state.isOpaqueCube(worldIn, pos)) {
         return worldIn.getMaxLightLevel();
      } else {
         return state.propagatesSkylightDown(worldIn, pos) ? 0 : 1;
      }
   }

   @Deprecated
   public final boolean useNeighborBrightness(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return !state.isOpaqueCube(worldIn, pos) && state.getOpacity(worldIn, pos) == worldIn.getMaxLightLevel();
   }

   public boolean isCollidable(IBlockState state) {
      return this.isCollidable();
   }

   /**
    * Returns if this block is collidable. Only used by fire, although stairs return that of the block that the stair is
    * made of (though nobody's going to make fire stairs, right?)
    */
   public boolean isCollidable() {
      return true;
   }

   @Deprecated
   public void randomTick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      this.tick(state, worldIn, pos, random);
   }

   @Deprecated
   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
   }

   /**
    * Called after a player destroys this Block - the posiiton pos may no longer hold the state indicated.
    */
   public void onPlayerDestroy(IWorld worldIn, BlockPos pos, IBlockState state) {
   }

   /**
    * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
    * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
    * block, etc.
    */
   @Deprecated
   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReaderBase worldIn) {
      return 10;
   }

   @Deprecated
   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
   }

   @Deprecated
   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
       if (hasTileEntity(state) && !(this instanceof BlockContainer)) {
           worldIn.removeTileEntity(pos);
       }
   }

   @Deprecated //Forge: Use fortune/location sensitive version
   public int quantityDropped(IBlockState state, Random random) {
      return 1;
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return this;
   }

   /**
    * Get the hardness of this Block relative to the ability of the given player
    * @deprecated call via {@link IBlockState#getPlayerRelativeBlockHardness(EntityPlayer,World,BlockPos)} whenever
    * possible. Implementing/overriding is fine.
    */
   @Deprecated
   public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, IBlockReader worldIn, BlockPos pos) {
      float f = state.getBlockHardness(worldIn, pos);
      if (f == -1.0F) {
         return 0.0F;
      } else {
         int i = net.minecraftforge.common.ForgeHooks.canHarvestBlock(state, player, worldIn, pos) ? 30 : 100;
         return player.getDigSpeed(state, pos) / f / (float)i;
      }
   }

   @Deprecated
   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
      if (!worldIn.isRemote && !worldIn.restoringBlockSnapshots) { // do not drop items while restoring blockstates, prevents item dupe
         NonNullList<ItemStack> drops = NonNullList.create();
         getDrops(state, drops, worldIn, pos, fortune);
         chancePerItem = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(drops, worldIn, pos, state, fortune, chancePerItem, false, harvesters.get());
         for (ItemStack stack : drops) {
            if (worldIn.rand.nextFloat() <= chancePerItem)
               spawnAsEntity(worldIn, pos, stack);
         }
      }
   }

   /**
    * Spawns the given ItemStack as an EntityItem into the World at the given position
    */
   public static void spawnAsEntity(World worldIn, BlockPos pos, ItemStack stack) {
      if (!worldIn.isRemote && !stack.isEmpty() && worldIn.getGameRules().getBoolean("doTileDrops") && !worldIn.restoringBlockSnapshots) {// do not drop items while restoring blockstates, prevents item dupe
         if (captureDrops.get()) {
            capturedDrops.get().add(stack);
            return;
         }
         float f = 0.5F;
         double d0 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
         double d1 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
         double d2 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
         EntityItem entityitem = new EntityItem(worldIn, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, stack);
         entityitem.setDefaultPickupDelay();
         worldIn.spawnEntity(entityitem);
      }
   }

   /**
    * Spawns the given amount of experience into the World as XP orb entities
    */
   public void dropXpOnBlockBreak(World worldIn, BlockPos pos, int amount) {
      if (!worldIn.isRemote && worldIn.getGameRules().getBoolean("doTileDrops")) {
         while(amount > 0) {
            int i = EntityXPOrb.getXPSplit(amount);
            amount -= i;
            worldIn.spawnEntity(new EntityXPOrb(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, i));
         }
      }

   }

   /**
    * Returns how much this block can resist explosions from the passed in entity.
    */
   @Deprecated //Forge: State sensitive version
   public float getExplosionResistance() {
      return this.blockResistance;
   }

   /**
    * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
    * @deprecated call via {@link IBlockState#collisionRayTrace(World,BlockPos,Vec3d,Vec3d)} whenever possible.
    * Implementing/overriding is fine.
    */
   @Nullable
   public static RayTraceResult collisionRayTrace(IBlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
      RayTraceResult raytraceresult = state.getShape(worldIn, pos).func_212433_a(start, end, pos);
      if (raytraceresult != null) {
         RayTraceResult raytraceresult1 = state.getRaytraceShape(worldIn, pos).func_212433_a(start, end, pos);
         if (raytraceresult1 != null && raytraceresult1.hitVec.subtract(start).lengthSquared() < raytraceresult.hitVec.subtract(start).lengthSquared()) {
            raytraceresult.sideHit = raytraceresult1.sideHit;
         }
      }

      return state.getBlock().getRayTraceResult(state, worldIn, pos, start, end, raytraceresult);
   }

   /**
    * Called when this Block is destroyed by an Explosion
    */
   public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.SOLID;
   }

   @Deprecated
   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      return true;
   }

   @Deprecated
   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      return false;
   }

   /**
    * Called when the given entity walks on this Block
    */
   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState();
   }

   @Deprecated
   public void onBlockClicked(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) {
   }

   /**
    * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   @Deprecated
   public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      return 0;
   }

   /**
    * Can this block provide power. Only wire currently seems to have this change based on its state.
    * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
    */
   @Deprecated
   public boolean canProvidePower(IBlockState state) {
      return false;
   }

   @Deprecated
   public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
   }

   /**
    * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   @Deprecated
   public int getStrongPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      return 0;
   }

   /**
    * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
    * Block.removedByPlayer
    */
   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      player.addStat(StatList.BLOCK_MINED.get(this));
      player.addExhaustion(0.005F);
      if (this.canSilkHarvest(state, worldIn, pos, player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
         NonNullList<ItemStack> items = NonNullList.create();
         ItemStack itemstack = this.getSilkTouchDrop(state);
         if (!itemstack.isEmpty()) items.add(itemstack);
         net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
         items.forEach(e -> spawnAsEntity(worldIn, pos, e));
      } else {
         harvesters.set(player);
         int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
         state.dropBlockAsItem(worldIn, pos, i);
         harvesters.set(null);
      }

   }

   @Deprecated //Use state sensitive version
   protected boolean canSilkHarvest() {
      return this.getDefaultState().isFullCube() && !this.hasTileEntity();
   }

   protected ItemStack getSilkTouchDrop(IBlockState state) {
      return new ItemStack(this);
   }

   public int getItemsToDropCount(IBlockState state, int fortune, World worldIn, BlockPos pos, Random random) {
      return this.quantityDropped(state, random);
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
   }

   /**
    * Return true if an entity can be spawned inside the block (used to get the player's bed spawn location)
    */
   public boolean canSpawnInBlock() {
      return !this.material.isSolid() && !this.material.isLiquid();
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getNameTextComponent() {
      return new TextComponentTranslation(this.getTranslationKey());
   }

   /**
    * Returns the unlocalized name of the block with "tile." appended to the front.
    */
   public String getTranslationKey() {
      if (this.translationKey == null) {
         this.translationKey = Util.makeTranslationKey("block", IRegistry.field_212618_g.getKey(this));
      }

      return this.translationKey;
   }

   /**
    * Called on server when World#addBlockEvent is called. If server returns true, then also called on the client. On
    * the Server, this may perform additional changes to the world, like pistons replacing the block with an extended
    * base. On the client, the update may involve replacing tile entities or effects such as sounds or particles
    * @deprecated call via {@link IBlockState#onBlockEventReceived(World,BlockPos,int,int)} whenever possible.
    * Implementing/overriding is fine.
    */
   @Deprecated
   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      return false;
   }

   /**
    * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
    */
   @Deprecated
   public EnumPushReaction getPushReaction(IBlockState state) {
      return this.material.getPushReaction();
   }

   /**
    * @deprecated call via {@link IBlockState#getAmbientOcclusionLightValue()} whenever possible.
    * Implementing/overriding is fine.
    */
   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public float getAmbientOcclusionLightValue(IBlockState state) {
      return state.isBlockNormalCube() ? 0.2F : 1.0F;
   }

   /**
    * Block's chance to react to a living entity falling on it.
    */
   public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
      entityIn.fall(fallDistance, 1.0F);
   }

   /**
    * Called when an Entity lands on this Block. This method *must* update motionY because the entity will not do that
    * on its own
    */
   public void onLanded(IBlockReader worldIn, Entity entityIn) {
      entityIn.motionY = 0.0D;
   }

   @Deprecated // Forge: Use more sensitive version below: getPickBlock
   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(this);
   }

   /**
    * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
    */
   public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
      items.add(new ItemStack(this));
   }

   @Deprecated
   public IFluidState getFluidState(IBlockState state) {
      return Fluids.EMPTY.getDefaultState();
   }

   public float getSlipperiness() {
      return this.slipperiness;
   }

   /**
    * Return a random long to be passed to {@link IBakedModel#getQuads}, used for random model rotations
    */
   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public long getPositionRandom(IBlockState state, BlockPos pos) {
      return MathHelper.getPositionRandom(pos);
   }

   /**
    * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect
    * this block
    */
   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      worldIn.playEvent(player, 2001, pos, getStateId(state));
   }

   /**
    * Called similar to random ticks, but only when it is raining.
    */
   public void fillWithRain(World worldIn, BlockPos pos) {
   }

   /**
    * Return whether this block can drop from an explosion.
    */
   public boolean canDropFromExplosion(Explosion explosionIn) {
      return true;
   }

   /**
    * @deprecated call via {@link IBlockState#hasComparatorInputOverride()} whenever possible. Implementing/overriding
    * is fine.
    */
   @Deprecated
   public boolean hasComparatorInputOverride(IBlockState state) {
      return false;
   }

   /**
    * @deprecated call via {@link IBlockState#getComparatorInputOverride(World,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   @Deprecated
   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      return 0;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
   }

   public StateContainer<Block, IBlockState> getStateContainer() {
      return this.stateContainer;
   }

   protected final void setDefaultState(IBlockState state) {
      this.defaultState = state;
   }

   /**
    * Gets the default state for this block
    */
   public final IBlockState getDefaultState() {
      return this.defaultState;
   }

   /**
    * Get the OffsetType for this Block. Determines if the model is rendered slightly offset.
    */
   public Block.EnumOffsetType getOffsetType() {
      return Block.EnumOffsetType.NONE;
   }

   /**
    * @deprecated call via {@link IBlockState#getOffset(IBlockAccess,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   @Deprecated
   public Vec3d getOffset(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      Block.EnumOffsetType block$enumoffsettype = this.getOffsetType();
      if (block$enumoffsettype == Block.EnumOffsetType.NONE) {
         return Vec3d.ZERO;
      } else {
         long i = MathHelper.getCoordinateRandom(pos.getX(), 0, pos.getZ());
         return new Vec3d(((double)((float)(i & 15L) / 15.0F) - 0.5D) * 0.5D, block$enumoffsettype == Block.EnumOffsetType.XYZ ? ((double)((float)(i >> 4 & 15L) / 15.0F) - 1.0D) * 0.2D : 0.0D, ((double)((float)(i >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D);
      }
   }

   @Deprecated //Forge: Use more sensitive version {@link IForgeBlockState#getSoundType(IWorldReader, BlockPos, Entity) }
   public SoundType getSoundType() {
      return this.soundType;
   }

   public Item asItem() {
      return Item.getItemFromBlock(this);
   }

   public boolean isVariableOpacity() {
      return this.variableOpacity;
   }

   public String toString() {
      return "Block{" + IRegistry.field_212618_g.getKey(this) + "}";
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
   }

   public static boolean isRock(Block blockIn) {
      return net.minecraftforge.common.Tags.Blocks.STONE.contains(blockIn);
   }

   public static boolean isDirt(Block blockIn) {
      return net.minecraftforge.common.Tags.Blocks.DIRT.contains(blockIn);
   }

   public static void registerBlocks() {
      Block block = new BlockAir(Block.Properties.create(Material.AIR).doesNotBlockMovement());
      register(IRegistry.field_212618_g.func_212609_b(), block);
      Block block1 = new BlockStone(Block.Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(1.5F, 6.0F));
      register("stone", block1);
      register("granite", new Block(Block.Properties.create(Material.ROCK, MaterialColor.DIRT).hardnessAndResistance(1.5F, 6.0F)));
      register("polished_granite", new Block(Block.Properties.create(Material.ROCK, MaterialColor.DIRT).hardnessAndResistance(1.5F, 6.0F)));
      register("diorite", new Block(Block.Properties.create(Material.ROCK, MaterialColor.QUARTZ).hardnessAndResistance(1.5F, 6.0F)));
      register("polished_diorite", new Block(Block.Properties.create(Material.ROCK, MaterialColor.QUARTZ).hardnessAndResistance(1.5F, 6.0F)));
      register("andesite", new Block(Block.Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(1.5F, 6.0F)));
      register("polished_andesite", new Block(Block.Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(1.5F, 6.0F)));
      register("grass_block", new BlockGrass(Block.Properties.create(Material.GRASS).needsRandomTick().hardnessAndResistance(0.6F).sound(SoundType.PLANT)));
      register("dirt", new Block(Block.Properties.create(Material.GROUND, MaterialColor.DIRT).hardnessAndResistance(0.5F).sound(SoundType.GROUND)));
      register("coarse_dirt", new Block(Block.Properties.create(Material.GROUND, MaterialColor.DIRT).hardnessAndResistance(0.5F).sound(SoundType.GROUND)));
      register("podzol", new BlockDirtSnowy(Block.Properties.create(Material.GROUND, MaterialColor.OBSIDIAN).hardnessAndResistance(0.5F).sound(SoundType.GROUND)));
      Block block2 = new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 6.0F));
      register("cobblestone", block2);
      Block block3 = new Block(Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD));
      Block block4 = new Block(Block.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD));
      Block block5 = new Block(Block.Properties.create(Material.WOOD, MaterialColor.SAND).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD));
      Block block6 = new Block(Block.Properties.create(Material.WOOD, MaterialColor.DIRT).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD));
      Block block7 = new Block(Block.Properties.create(Material.WOOD, MaterialColor.ADOBE).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD));
      Block block8 = new Block(Block.Properties.create(Material.WOOD, MaterialColor.BROWN).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD));
      register("oak_planks", block3);
      register("spruce_planks", block4);
      register("birch_planks", block5);
      register("jungle_planks", block6);
      register("acacia_planks", block7);
      register("dark_oak_planks", block8);
      Block block9 = new BlockSapling(new OakTree(), Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block10 = new BlockSapling(new SpruceTree(), Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block11 = new BlockSapling(new BirchTree(), Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block12 = new BlockSapling(new JungleTree(), Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block13 = new BlockSapling(new AcaciaTree(), Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block14 = new BlockSapling(new DarkOakTree(), Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.PLANT));
      register("oak_sapling", block9);
      register("spruce_sapling", block10);
      register("birch_sapling", block11);
      register("jungle_sapling", block12);
      register("acacia_sapling", block13);
      register("dark_oak_sapling", block14);
      register("bedrock", new BlockEmptyDrops(Block.Properties.create(Material.ROCK).hardnessAndResistance(-1.0F, 3600000.0F)));
      register("water", new BlockFlowingFluid(Fluids.WATER, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F)));
      register("lava", new BlockFlowingFluid(Fluids.LAVA, Block.Properties.create(Material.LAVA).doesNotBlockMovement().needsRandomTick().hardnessAndResistance(100.0F).lightValue(15)));
      register("sand", new BlockSand(14406560, Block.Properties.create(Material.SAND, MaterialColor.SAND).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("red_sand", new BlockSand(11098145, Block.Properties.create(Material.SAND, MaterialColor.ADOBE).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("gravel", new BlockGravel(Block.Properties.create(Material.SAND, MaterialColor.STONE).hardnessAndResistance(0.6F).sound(SoundType.GROUND)));
      register("gold_ore", new BlockOre(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F, 3.0F)));
      register("iron_ore", new BlockOre(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F, 3.0F)));
      register("coal_ore", new BlockOre(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F, 3.0F)));
      register("oak_log", new BlockLog(MaterialColor.WOOD, Block.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("spruce_log", new BlockLog(MaterialColor.OBSIDIAN, Block.Properties.create(Material.WOOD, MaterialColor.BROWN).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("birch_log", new BlockLog(MaterialColor.SAND, Block.Properties.create(Material.WOOD, MaterialColor.QUARTZ).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("jungle_log", new BlockLog(MaterialColor.DIRT, Block.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("acacia_log", new BlockLog(MaterialColor.ADOBE, Block.Properties.create(Material.WOOD, MaterialColor.STONE).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("dark_oak_log", new BlockLog(MaterialColor.BROWN, Block.Properties.create(Material.WOOD, MaterialColor.BROWN).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("stripped_spruce_log", new BlockLog(MaterialColor.OBSIDIAN, Block.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("stripped_birch_log", new BlockLog(MaterialColor.SAND, Block.Properties.create(Material.WOOD, MaterialColor.SAND).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("stripped_jungle_log", new BlockLog(MaterialColor.DIRT, Block.Properties.create(Material.WOOD, MaterialColor.DIRT).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("stripped_acacia_log", new BlockLog(MaterialColor.ADOBE, Block.Properties.create(Material.WOOD, MaterialColor.ADOBE).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("stripped_dark_oak_log", new BlockLog(MaterialColor.BROWN, Block.Properties.create(Material.WOOD, MaterialColor.BROWN).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("stripped_oak_log", new BlockLog(MaterialColor.WOOD, Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("oak_wood", new BlockRotatedPillar(Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("spruce_wood", new BlockRotatedPillar(Block.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("birch_wood", new BlockRotatedPillar(Block.Properties.create(Material.WOOD, MaterialColor.SAND).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("jungle_wood", new BlockRotatedPillar(Block.Properties.create(Material.WOOD, MaterialColor.DIRT).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("acacia_wood", new BlockRotatedPillar(Block.Properties.create(Material.WOOD, MaterialColor.ADOBE).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("dark_oak_wood", new BlockRotatedPillar(Block.Properties.create(Material.WOOD, MaterialColor.BROWN).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("stripped_oak_wood", new BlockRotatedPillar(Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("stripped_spruce_wood", new BlockRotatedPillar(Block.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("stripped_birch_wood", new BlockRotatedPillar(Block.Properties.create(Material.WOOD, MaterialColor.SAND).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("stripped_jungle_wood", new BlockRotatedPillar(Block.Properties.create(Material.WOOD, MaterialColor.DIRT).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("stripped_acacia_wood", new BlockRotatedPillar(Block.Properties.create(Material.WOOD, MaterialColor.ADOBE).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("stripped_dark_oak_wood", new BlockRotatedPillar(Block.Properties.create(Material.WOOD, MaterialColor.BROWN).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
      register("oak_leaves", new BlockLeaves(Block.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).needsRandomTick().sound(SoundType.PLANT)));
      register("spruce_leaves", new BlockLeaves(Block.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).needsRandomTick().sound(SoundType.PLANT)));
      register("birch_leaves", new BlockLeaves(Block.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).needsRandomTick().sound(SoundType.PLANT)));
      register("jungle_leaves", new BlockLeaves(Block.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).needsRandomTick().sound(SoundType.PLANT)));
      register("acacia_leaves", new BlockLeaves(Block.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).needsRandomTick().sound(SoundType.PLANT)));
      register("dark_oak_leaves", new BlockLeaves(Block.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).needsRandomTick().sound(SoundType.PLANT)));
      register("sponge", new BlockSponge(Block.Properties.create(Material.SPONGE).hardnessAndResistance(0.6F).sound(SoundType.PLANT)));
      register("wet_sponge", new BlockWetSponge(Block.Properties.create(Material.SPONGE).hardnessAndResistance(0.6F).sound(SoundType.PLANT)));
      register("glass", new BlockGlass(Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("lapis_ore", new BlockOre(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F, 3.0F)));
      register("lapis_block", new Block(Block.Properties.create(Material.IRON, MaterialColor.LAPIS).hardnessAndResistance(3.0F, 3.0F)));
      register("dispenser", new BlockDispenser(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.5F)));
      Block block15 = new Block(Block.Properties.create(Material.ROCK, MaterialColor.SAND).hardnessAndResistance(0.8F));
      register("sandstone", block15);
      register("chiseled_sandstone", new Block(Block.Properties.create(Material.ROCK, MaterialColor.SAND).hardnessAndResistance(0.8F)));
      register("cut_sandstone", new Block(Block.Properties.create(Material.ROCK, MaterialColor.SAND).hardnessAndResistance(0.8F)));
      register("note_block", new BlockNote(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(0.8F)));
      register("white_bed", new BlockBed(EnumDyeColor.WHITE, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("orange_bed", new BlockBed(EnumDyeColor.ORANGE, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("magenta_bed", new BlockBed(EnumDyeColor.MAGENTA, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("light_blue_bed", new BlockBed(EnumDyeColor.LIGHT_BLUE, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("yellow_bed", new BlockBed(EnumDyeColor.YELLOW, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("lime_bed", new BlockBed(EnumDyeColor.LIME, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("pink_bed", new BlockBed(EnumDyeColor.PINK, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("gray_bed", new BlockBed(EnumDyeColor.GRAY, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("light_gray_bed", new BlockBed(EnumDyeColor.LIGHT_GRAY, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("cyan_bed", new BlockBed(EnumDyeColor.CYAN, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("purple_bed", new BlockBed(EnumDyeColor.PURPLE, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("blue_bed", new BlockBed(EnumDyeColor.BLUE, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("brown_bed", new BlockBed(EnumDyeColor.BROWN, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("green_bed", new BlockBed(EnumDyeColor.GREEN, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("red_bed", new BlockBed(EnumDyeColor.RED, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("black_bed", new BlockBed(EnumDyeColor.BLACK, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD).hardnessAndResistance(0.2F)));
      register("powered_rail", new BlockRailPowered(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL)));
      register("detector_rail", new BlockRailDetector(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL)));
      register("sticky_piston", new BlockPistonBase(true, Block.Properties.create(Material.PISTON).hardnessAndResistance(0.5F)));
      register("cobweb", new BlockWeb(Block.Properties.create(Material.WEB).doesNotBlockMovement().hardnessAndResistance(4.0F)));
      Block block16 = new BlockTallGrass(Block.Properties.create(Material.VINE).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block17 = new BlockTallGrass(Block.Properties.create(Material.VINE).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block18 = new BlockDeadBush(Block.Properties.create(Material.VINE, MaterialColor.WOOD).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT));
      register("grass", block16);
      register("fern", block17);
      register("dead_bush", block18);
      Block block19 = new BlockSeaGrass(Block.Properties.create(Material.SEA_GRASS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS));
      register("seagrass", block19);
      register("tall_seagrass", new BlockSeaGrassTall(block19, Block.Properties.create(Material.SEA_GRASS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      register("piston", new BlockPistonBase(false, Block.Properties.create(Material.PISTON).hardnessAndResistance(0.5F)));
      register("piston_head", new BlockPistonExtension(Block.Properties.create(Material.PISTON).hardnessAndResistance(0.5F)));
      register("white_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.SNOW).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("orange_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.ADOBE).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("magenta_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.MAGENTA).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("light_blue_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.LIGHT_BLUE).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("yellow_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.YELLOW).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("lime_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.LIME).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("pink_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.PINK).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("gray_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.GRAY).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("light_gray_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.LIGHT_GRAY).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("cyan_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.CYAN).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("purple_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.PURPLE).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("blue_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.BLUE).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("brown_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.BROWN).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("green_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.GREEN).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("red_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.RED).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("black_wool", new Block(Block.Properties.create(Material.CLOTH, MaterialColor.BLACK).hardnessAndResistance(0.8F).sound(SoundType.CLOTH)));
      register("moving_piston", new BlockPistonMoving(Block.Properties.create(Material.PISTON).hardnessAndResistance(-1.0F).variableOpacity()));
      Block block20 = new BlockFlower(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block21 = new BlockFlower(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block22 = new BlockFlower(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block23 = new BlockFlower(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block24 = new BlockFlower(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block25 = new BlockFlower(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block26 = new BlockFlower(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block27 = new BlockFlower(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block28 = new BlockFlower(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT));
      Block block29 = new BlockFlower(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT));
      register("dandelion", block20);
      register("poppy", block21);
      register("blue_orchid", block22);
      register("allium", block23);
      register("azure_bluet", block24);
      register("red_tulip", block25);
      register("orange_tulip", block26);
      register("white_tulip", block27);
      register("pink_tulip", block28);
      register("oxeye_daisy", block29);
      Block block30 = new BlockMushroom(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.PLANT).lightValue(1));
      Block block31 = new BlockMushroom(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.PLANT));
      register("brown_mushroom", block30);
      register("red_mushroom", block31);
      register("gold_block", new Block(Block.Properties.create(Material.IRON, MaterialColor.GOLD).hardnessAndResistance(3.0F, 6.0F).sound(SoundType.METAL)));
      register("iron_block", new Block(Block.Properties.create(Material.IRON, MaterialColor.IRON).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)));
      Block block32 = new Block(Block.Properties.create(Material.ROCK, MaterialColor.RED).hardnessAndResistance(2.0F, 6.0F));
      register("bricks", block32);
      register("tnt", new BlockTNT(Block.Properties.create(Material.TNT).zeroHardnessAndResistance().sound(SoundType.PLANT)));
      register("bookshelf", new BlockBookshelf(Block.Properties.create(Material.WOOD).hardnessAndResistance(1.5F).sound(SoundType.WOOD)));
      register("mossy_cobblestone", new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 6.0F)));
      register("obsidian", new Block(Block.Properties.create(Material.ROCK, MaterialColor.BLACK).hardnessAndResistance(50.0F, 1200.0F)));
      register("torch", new BlockTorch(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().zeroHardnessAndResistance().lightValue(14).sound(SoundType.WOOD)));
      register("wall_torch", new BlockTorchWall(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().zeroHardnessAndResistance().lightValue(14).sound(SoundType.WOOD)));
      register("fire", new BlockFire(Block.Properties.create(Material.FIRE, MaterialColor.TNT).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().lightValue(15).sound(SoundType.CLOTH)));
      register("spawner", new BlockMobSpawner(Block.Properties.create(Material.ROCK).hardnessAndResistance(5.0F).sound(SoundType.METAL)));
      register("oak_stairs", new BlockStairs(block3.getDefaultState(), Block.Properties.from(block3)));
      register("chest", new BlockChest(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD)));
      register("redstone_wire", new BlockRedstoneWire(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().zeroHardnessAndResistance()));
      register("diamond_ore", new BlockOre(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F, 3.0F)));
      register("diamond_block", new Block(Block.Properties.create(Material.IRON, MaterialColor.DIAMOND).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)));
      register("crafting_table", new BlockWorkbench(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD)));
      register("wheat", new BlockCrops(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.PLANT)));
      Block block33 = new BlockFarmland(Block.Properties.create(Material.GROUND).needsRandomTick().hardnessAndResistance(0.6F).sound(SoundType.GROUND));
      register("farmland", block33);
      register("furnace", new BlockFurnace(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.5F).lightValue(13)));
      register("sign", new BlockStandingSign(Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("oak_door", new BlockDoor(Block.Properties.create(Material.WOOD, block3.blockMapColor).hardnessAndResistance(3.0F).sound(SoundType.WOOD)));
      register("ladder", new BlockLadder(Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(0.4F).sound(SoundType.LADDER)));
      register("rail", new BlockRail(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL)));
      register("cobblestone_stairs", new BlockStairs(block2.getDefaultState(), Block.Properties.from(block2)));
      register("wall_sign", new BlockWallSign(Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("lever", new BlockLever(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
      register("stone_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.MOBS, Block.Properties.create(Material.ROCK).doesNotBlockMovement().hardnessAndResistance(0.5F)));
      register("iron_door", new BlockDoor(Block.Properties.create(Material.IRON, MaterialColor.IRON).hardnessAndResistance(5.0F).sound(SoundType.METAL)));
      register("oak_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING, Block.Properties.create(Material.WOOD, block3.blockMapColor).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
      register("spruce_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING, Block.Properties.create(Material.WOOD, block4.blockMapColor).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
      register("birch_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING, Block.Properties.create(Material.WOOD, block5.blockMapColor).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
      register("jungle_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING, Block.Properties.create(Material.WOOD, block6.blockMapColor).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
      register("acacia_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING, Block.Properties.create(Material.WOOD, block7.blockMapColor).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
      register("dark_oak_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING, Block.Properties.create(Material.WOOD, block8.blockMapColor).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
      register("redstone_ore", new BlockRedstoneOre(Block.Properties.create(Material.ROCK).needsRandomTick().lightValue(9).hardnessAndResistance(3.0F, 3.0F)));
      register("redstone_torch", new BlockRedstoneTorch(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().zeroHardnessAndResistance().lightValue(7).sound(SoundType.WOOD)));
      register("redstone_wall_torch", new BlockRedstoneTorchWall(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().zeroHardnessAndResistance().lightValue(7).sound(SoundType.WOOD)));
      register("stone_button", new BlockButtonStone(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0.5F)));
      register("snow", new BlockSnowLayer(Block.Properties.create(Material.SNOW).needsRandomTick().hardnessAndResistance(0.1F).sound(SoundType.SNOW)));
      register("ice", new BlockIce(Block.Properties.create(Material.ICE).slipperiness(0.98F).needsRandomTick().hardnessAndResistance(0.5F).sound(SoundType.GLASS)));
      register("snow_block", new BlockSnow(Block.Properties.create(Material.CRAFTED_SNOW).needsRandomTick().hardnessAndResistance(0.2F).sound(SoundType.SNOW)));
      Block block34 = new BlockCactus(Block.Properties.create(Material.CACTUS).needsRandomTick().hardnessAndResistance(0.4F).sound(SoundType.CLOTH));
      register("cactus", block34);
      register("clay", new BlockClay(Block.Properties.create(Material.CLAY).hardnessAndResistance(0.6F).sound(SoundType.GROUND)));
      register("sugar_cane", new BlockReed(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.PLANT)));
      register("jukebox", new BlockJukebox(Block.Properties.create(Material.WOOD, MaterialColor.DIRT).hardnessAndResistance(2.0F, 6.0F)));
      register("oak_fence", new BlockFence(Block.Properties.create(Material.WOOD, block3.blockMapColor).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      BlockStemGrown blockstemgrown = new BlockPumpkin(Block.Properties.create(Material.GOURD, MaterialColor.ADOBE).hardnessAndResistance(1.0F).sound(SoundType.WOOD));
      register("pumpkin", blockstemgrown);
      register("netherrack", new Block(Block.Properties.create(Material.ROCK, MaterialColor.NETHERRACK).hardnessAndResistance(0.4F)));
      register("soul_sand", new BlockSoulSand(Block.Properties.create(Material.SAND, MaterialColor.BROWN).needsRandomTick().hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("glowstone", new BlockGlowstone(Block.Properties.create(Material.GLASS, MaterialColor.SAND).hardnessAndResistance(0.3F).sound(SoundType.GLASS).lightValue(15)));
      register("nether_portal", new BlockPortal(Block.Properties.create(Material.PORTAL).doesNotBlockMovement().needsRandomTick().hardnessAndResistance(-1.0F).sound(SoundType.GLASS).lightValue(11)));
      register("carved_pumpkin", new BlockCarvedPumpkin(Block.Properties.create(Material.GOURD, MaterialColor.ADOBE).hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("jack_o_lantern", new BlockCarvedPumpkin(Block.Properties.create(Material.GOURD, MaterialColor.ADOBE).hardnessAndResistance(1.0F).sound(SoundType.WOOD).lightValue(15)));
      register("cake", new BlockCake(Block.Properties.create(Material.CAKE).hardnessAndResistance(0.5F).sound(SoundType.CLOTH)));
      register("repeater", new BlockRedstoneRepeater(Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance().sound(SoundType.WOOD)));
      register("white_stained_glass", new BlockStainedGlass(EnumDyeColor.WHITE, Block.Properties.create(Material.GLASS, EnumDyeColor.WHITE).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("orange_stained_glass", new BlockStainedGlass(EnumDyeColor.ORANGE, Block.Properties.create(Material.GLASS, EnumDyeColor.ORANGE).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("magenta_stained_glass", new BlockStainedGlass(EnumDyeColor.MAGENTA, Block.Properties.create(Material.GLASS, EnumDyeColor.MAGENTA).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("light_blue_stained_glass", new BlockStainedGlass(EnumDyeColor.LIGHT_BLUE, Block.Properties.create(Material.GLASS, EnumDyeColor.LIGHT_BLUE).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("yellow_stained_glass", new BlockStainedGlass(EnumDyeColor.YELLOW, Block.Properties.create(Material.GLASS, EnumDyeColor.YELLOW).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("lime_stained_glass", new BlockStainedGlass(EnumDyeColor.LIME, Block.Properties.create(Material.GLASS, EnumDyeColor.LIME).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("pink_stained_glass", new BlockStainedGlass(EnumDyeColor.PINK, Block.Properties.create(Material.GLASS, EnumDyeColor.PINK).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("gray_stained_glass", new BlockStainedGlass(EnumDyeColor.GRAY, Block.Properties.create(Material.GLASS, EnumDyeColor.GRAY).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("light_gray_stained_glass", new BlockStainedGlass(EnumDyeColor.LIGHT_GRAY, Block.Properties.create(Material.GLASS, EnumDyeColor.LIGHT_GRAY).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("cyan_stained_glass", new BlockStainedGlass(EnumDyeColor.CYAN, Block.Properties.create(Material.GLASS, EnumDyeColor.CYAN).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("purple_stained_glass", new BlockStainedGlass(EnumDyeColor.PURPLE, Block.Properties.create(Material.GLASS, EnumDyeColor.PURPLE).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("blue_stained_glass", new BlockStainedGlass(EnumDyeColor.BLUE, Block.Properties.create(Material.GLASS, EnumDyeColor.BLUE).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("brown_stained_glass", new BlockStainedGlass(EnumDyeColor.BROWN, Block.Properties.create(Material.GLASS, EnumDyeColor.BROWN).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("green_stained_glass", new BlockStainedGlass(EnumDyeColor.GREEN, Block.Properties.create(Material.GLASS, EnumDyeColor.GREEN).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("red_stained_glass", new BlockStainedGlass(EnumDyeColor.RED, Block.Properties.create(Material.GLASS, EnumDyeColor.RED).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("black_stained_glass", new BlockStainedGlass(EnumDyeColor.BLACK, Block.Properties.create(Material.GLASS, EnumDyeColor.BLACK).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("oak_trapdoor", new BlockTrapDoor(Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(3.0F).sound(SoundType.WOOD)));
      register("spruce_trapdoor", new BlockTrapDoor(Block.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(3.0F).sound(SoundType.WOOD)));
      register("birch_trapdoor", new BlockTrapDoor(Block.Properties.create(Material.WOOD, MaterialColor.SAND).hardnessAndResistance(3.0F).sound(SoundType.WOOD)));
      register("jungle_trapdoor", new BlockTrapDoor(Block.Properties.create(Material.WOOD, MaterialColor.DIRT).hardnessAndResistance(3.0F).sound(SoundType.WOOD)));
      register("acacia_trapdoor", new BlockTrapDoor(Block.Properties.create(Material.WOOD, MaterialColor.ADOBE).hardnessAndResistance(3.0F).sound(SoundType.WOOD)));
      register("dark_oak_trapdoor", new BlockTrapDoor(Block.Properties.create(Material.WOOD, MaterialColor.BROWN).hardnessAndResistance(3.0F).sound(SoundType.WOOD)));
      Block block35 = new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F));
      Block block36 = new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F));
      Block block37 = new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F));
      Block block38 = new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F));
      register("infested_stone", new BlockSilverfish(block1, Block.Properties.create(Material.CLAY).hardnessAndResistance(0.0F, 0.75F)));
      register("infested_cobblestone", new BlockSilverfish(block2, Block.Properties.create(Material.CLAY).hardnessAndResistance(0.0F, 0.75F)));
      register("infested_stone_bricks", new BlockSilverfish(block35, Block.Properties.create(Material.CLAY).hardnessAndResistance(0.0F, 0.75F)));
      register("infested_mossy_stone_bricks", new BlockSilverfish(block36, Block.Properties.create(Material.CLAY).hardnessAndResistance(0.0F, 0.75F)));
      register("infested_cracked_stone_bricks", new BlockSilverfish(block37, Block.Properties.create(Material.CLAY).hardnessAndResistance(0.0F, 0.75F)));
      register("infested_chiseled_stone_bricks", new BlockSilverfish(block38, Block.Properties.create(Material.CLAY).hardnessAndResistance(0.0F, 0.75F)));
      register("stone_bricks", block35);
      register("mossy_stone_bricks", block36);
      register("cracked_stone_bricks", block37);
      register("chiseled_stone_bricks", block38);
      Block block39 = new BlockHugeMushroom(block30, Block.Properties.create(Material.WOOD, MaterialColor.DIRT).hardnessAndResistance(0.2F).sound(SoundType.WOOD));
      register("brown_mushroom_block", block39);
      Block block40 = new BlockHugeMushroom(block31, Block.Properties.create(Material.WOOD, MaterialColor.RED).hardnessAndResistance(0.2F).sound(SoundType.WOOD));
      register("red_mushroom_block", block40);
      register("mushroom_stem", new BlockHugeMushroom((Block)null, Block.Properties.create(Material.WOOD, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(0.2F).sound(SoundType.WOOD)));
      register("iron_bars", new BlockPane(Block.Properties.create(Material.IRON, MaterialColor.AIR).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)));
      register("glass_pane", new BlockGlassPane(Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      BlockStemGrown blockstemgrown1 = new BlockMelon(Block.Properties.create(Material.GOURD, MaterialColor.LIME).hardnessAndResistance(1.0F).sound(SoundType.WOOD));
      register("melon", blockstemgrown1);
      register("attached_pumpkin_stem", new BlockAttachedStem(blockstemgrown, Block.Properties.create(Material.PLANTS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WOOD)));
      register("attached_melon_stem", new BlockAttachedStem(blockstemgrown1, Block.Properties.create(Material.PLANTS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WOOD)));
      register("pumpkin_stem", new BlockStem(blockstemgrown, Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.WOOD)));
      register("melon_stem", new BlockStem(blockstemgrown1, Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.WOOD)));
      register("vine", new BlockVine(Block.Properties.create(Material.VINE).doesNotBlockMovement().needsRandomTick().hardnessAndResistance(0.2F).sound(SoundType.PLANT)));
      register("oak_fence_gate", new BlockFenceGate(Block.Properties.create(Material.WOOD, block3.blockMapColor).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("brick_stairs", new BlockStairs(block32.getDefaultState(), Block.Properties.from(block32)));
      register("stone_brick_stairs", new BlockStairs(block35.getDefaultState(), Block.Properties.from(block35)));
      register("mycelium", new BlockMycelium(Block.Properties.create(Material.GRASS, MaterialColor.PURPLE).needsRandomTick().hardnessAndResistance(0.6F).sound(SoundType.PLANT)));
      register("lily_pad", new BlockLilyPad(Block.Properties.create(Material.PLANTS).zeroHardnessAndResistance().sound(SoundType.PLANT)));
      Block block41 = new Block(Block.Properties.create(Material.ROCK, MaterialColor.NETHERRACK).hardnessAndResistance(2.0F, 6.0F));
      register("nether_bricks", block41);
      register("nether_brick_fence", new BlockFence(Block.Properties.create(Material.ROCK, MaterialColor.NETHERRACK).hardnessAndResistance(2.0F, 6.0F)));
      register("nether_brick_stairs", new BlockStairs(block41.getDefaultState(), Block.Properties.from(block41)));
      register("nether_wart", new BlockNetherWart(Block.Properties.create(Material.PLANTS, MaterialColor.RED).doesNotBlockMovement().needsRandomTick()));
      register("enchanting_table", new BlockEnchantmentTable(Block.Properties.create(Material.ROCK, MaterialColor.RED).hardnessAndResistance(5.0F, 1200.0F)));
      register("brewing_stand", new BlockBrewingStand(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F).lightValue(1)));
      register("cauldron", new BlockCauldron(Block.Properties.create(Material.IRON, MaterialColor.STONE).hardnessAndResistance(2.0F)));
      register("end_portal", new BlockEndPortal(Block.Properties.create(Material.PORTAL, MaterialColor.BLACK).doesNotBlockMovement().lightValue(15).hardnessAndResistance(-1.0F, 3600000.0F)));
      register("end_portal_frame", new BlockEndPortalFrame(Block.Properties.create(Material.ROCK, MaterialColor.GREEN).sound(SoundType.GLASS).lightValue(1).hardnessAndResistance(-1.0F, 3600000.0F)));
      register("end_stone", new Block(Block.Properties.create(Material.ROCK, MaterialColor.SAND).hardnessAndResistance(3.0F, 9.0F)));
      register("dragon_egg", new BlockDragonEgg(Block.Properties.create(Material.DRAGON_EGG, MaterialColor.BLACK).hardnessAndResistance(3.0F, 9.0F).lightValue(1)));
      register("redstone_lamp", new BlockRedstoneLamp(Block.Properties.create(Material.REDSTONE_LIGHT).lightValue(15).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("cocoa", new BlockCocoa(Block.Properties.create(Material.PLANTS).needsRandomTick().hardnessAndResistance(0.2F, 3.0F).sound(SoundType.WOOD)));
      register("sandstone_stairs", new BlockStairs(block15.getDefaultState(), Block.Properties.from(block15)));
      register("emerald_ore", new BlockOre(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F, 3.0F)));
      register("ender_chest", new BlockEnderChest(Block.Properties.create(Material.ROCK).hardnessAndResistance(22.5F, 600.0F).lightValue(7)));
      BlockTripWireHook blocktripwirehook = new BlockTripWireHook(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement());
      register("tripwire_hook", blocktripwirehook);
      register("tripwire", new BlockTripWire(blocktripwirehook, Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement()));
      register("emerald_block", new Block(Block.Properties.create(Material.IRON, MaterialColor.EMERALD).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)));
      register("spruce_stairs", new BlockStairs(block4.getDefaultState(), Block.Properties.from(block4)));
      register("birch_stairs", new BlockStairs(block5.getDefaultState(), Block.Properties.from(block5)));
      register("jungle_stairs", new BlockStairs(block6.getDefaultState(), Block.Properties.from(block6)));
      register("command_block", new BlockCommandBlock(Block.Properties.create(Material.IRON, MaterialColor.BROWN).hardnessAndResistance(-1.0F, 3600000.0F)));
      register("beacon", new BlockBeacon(Block.Properties.create(Material.GLASS, MaterialColor.DIAMOND).hardnessAndResistance(3.0F).lightValue(15)));
      register("cobblestone_wall", new BlockWall(Block.Properties.from(block2)));
      register("mossy_cobblestone_wall", new BlockWall(Block.Properties.from(block2)));
      register("flower_pot", new BlockFlowerPot(block, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_oak_sapling", new BlockFlowerPot(block9, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_spruce_sapling", new BlockFlowerPot(block10, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_birch_sapling", new BlockFlowerPot(block11, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_jungle_sapling", new BlockFlowerPot(block12, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_acacia_sapling", new BlockFlowerPot(block13, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_dark_oak_sapling", new BlockFlowerPot(block14, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_fern", new BlockFlowerPot(block17, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_dandelion", new BlockFlowerPot(block20, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_poppy", new BlockFlowerPot(block21, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_blue_orchid", new BlockFlowerPot(block22, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_allium", new BlockFlowerPot(block23, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_azure_bluet", new BlockFlowerPot(block24, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_red_tulip", new BlockFlowerPot(block25, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_orange_tulip", new BlockFlowerPot(block26, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_white_tulip", new BlockFlowerPot(block27, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_pink_tulip", new BlockFlowerPot(block28, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_oxeye_daisy", new BlockFlowerPot(block29, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_red_mushroom", new BlockFlowerPot(block31, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_brown_mushroom", new BlockFlowerPot(block30, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_dead_bush", new BlockFlowerPot(block18, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("potted_cactus", new BlockFlowerPot(block34, Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance()));
      register("carrots", new BlockCarrot(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.PLANT)));
      register("potatoes", new BlockPotato(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.PLANT)));
      register("oak_button", new BlockButtonWood(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
      register("spruce_button", new BlockButtonWood(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
      register("birch_button", new BlockButtonWood(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
      register("jungle_button", new BlockButtonWood(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
      register("acacia_button", new BlockButtonWood(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
      register("dark_oak_button", new BlockButtonWood(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
      register("skeleton_wall_skull", new BlockSkullWall(BlockSkull.Types.SKELETON, Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(1.0F)));
      register("skeleton_skull", new BlockSkull(BlockSkull.Types.SKELETON, Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(1.0F)));
      register("wither_skeleton_wall_skull", new BlockSkullWitherWall(Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(1.0F)));
      register("wither_skeleton_skull", new BlockSkullWither(Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(1.0F)));
      register("zombie_wall_head", new BlockSkullWall(BlockSkull.Types.ZOMBIE, Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(1.0F)));
      register("zombie_head", new BlockSkull(BlockSkull.Types.ZOMBIE, Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(1.0F)));
      register("player_wall_head", new BlockSkullWallPlayer(Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(1.0F)));
      register("player_head", new BlockSkullPlayer(Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(1.0F)));
      register("creeper_wall_head", new BlockSkullWall(BlockSkull.Types.CREEPER, Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(1.0F)));
      register("creeper_head", new BlockSkull(BlockSkull.Types.CREEPER, Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(1.0F)));
      register("dragon_wall_head", new BlockSkullWall(BlockSkull.Types.DRAGON, Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(1.0F)));
      register("dragon_head", new BlockSkull(BlockSkull.Types.DRAGON, Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(1.0F)));
      register("anvil", new BlockAnvil(Block.Properties.create(Material.ANVIL, MaterialColor.IRON).hardnessAndResistance(5.0F, 1200.0F).sound(SoundType.ANVIL)));
      register("chipped_anvil", new BlockAnvil(Block.Properties.create(Material.ANVIL, MaterialColor.IRON).hardnessAndResistance(5.0F, 1200.0F).sound(SoundType.ANVIL)));
      register("damaged_anvil", new BlockAnvil(Block.Properties.create(Material.ANVIL, MaterialColor.IRON).hardnessAndResistance(5.0F, 1200.0F).sound(SoundType.ANVIL)));
      register("trapped_chest", new BlockTrappedChest(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD)));
      register("light_weighted_pressure_plate", new BlockPressurePlateWeighted(15, Block.Properties.create(Material.IRON, MaterialColor.GOLD).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
      register("heavy_weighted_pressure_plate", new BlockPressurePlateWeighted(150, Block.Properties.create(Material.IRON).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
      register("comparator", new BlockRedstoneComparator(Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance().sound(SoundType.WOOD)));
      register("daylight_detector", new BlockDaylightDetector(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.2F).sound(SoundType.WOOD)));
      register("redstone_block", new BlockRedstone(Block.Properties.create(Material.IRON, MaterialColor.TNT).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)));
      register("nether_quartz_ore", new BlockOre(Block.Properties.create(Material.ROCK, MaterialColor.NETHERRACK).hardnessAndResistance(3.0F, 3.0F)));
      register("hopper", new BlockHopper(Block.Properties.create(Material.IRON, MaterialColor.STONE).hardnessAndResistance(3.0F, 4.8F).sound(SoundType.METAL)));
      Block block42 = new Block(Block.Properties.create(Material.ROCK, MaterialColor.QUARTZ).hardnessAndResistance(0.8F));
      register("quartz_block", block42);
      register("chiseled_quartz_block", new Block(Block.Properties.create(Material.ROCK, MaterialColor.QUARTZ).hardnessAndResistance(0.8F)));
      register("quartz_pillar", new BlockRotatedPillar(Block.Properties.create(Material.ROCK, MaterialColor.QUARTZ).hardnessAndResistance(0.8F)));
      register("quartz_stairs", new BlockStairs(block42.getDefaultState(), Block.Properties.from(block42)));
      register("activator_rail", new BlockRailPowered(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), true));
      register("dropper", new BlockDropper(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.5F)));
      register("white_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("orange_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.ORANGE_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("magenta_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.MAGENTA_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("light_blue_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.LIGHT_BLUE_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("yellow_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.YELLOW_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("lime_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.LIME_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("pink_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.PINK_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("gray_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.GRAY_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("light_gray_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.LIGHT_GRAY_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("cyan_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.CYAN_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("purple_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.PURPLE_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("blue_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.BLUE_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("brown_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.BROWN_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("green_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.GREEN_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("red_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.RED_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("black_terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.BLACK_TERRACOTTA).hardnessAndResistance(1.25F, 4.2F)));
      register("white_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.WHITE, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("orange_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.ORANGE, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("magenta_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.MAGENTA, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("light_blue_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.LIGHT_BLUE, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("yellow_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.YELLOW, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("lime_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.LIME, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("pink_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.PINK, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("gray_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.GRAY, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("light_gray_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.LIGHT_GRAY, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("cyan_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.CYAN, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("purple_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.PURPLE, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("blue_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.BLUE, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("brown_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.BROWN, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("green_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.GREEN, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("red_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.RED, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("black_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.BLACK, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS)));
      register("acacia_stairs", new BlockStairs(block7.getDefaultState(), Block.Properties.from(block7)));
      register("dark_oak_stairs", new BlockStairs(block8.getDefaultState(), Block.Properties.from(block8)));
      register("slime_block", new BlockSlime(Block.Properties.create(Material.CLAY, MaterialColor.GRASS).slipperiness(0.8F).sound(SoundType.SLIME)));
      register("barrier", new BlockBarrier(Block.Properties.create(Material.BARRIER).hardnessAndResistance(-1.0F, 3600000.8F)));
      register("iron_trapdoor", new BlockTrapDoor(Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F).sound(SoundType.METAL)));
      Block block43 = new Block(Block.Properties.create(Material.ROCK, MaterialColor.CYAN).hardnessAndResistance(1.5F, 6.0F));
      register("prismarine", block43);
      Block block44 = new Block(Block.Properties.create(Material.ROCK, MaterialColor.DIAMOND).hardnessAndResistance(1.5F, 6.0F));
      register("prismarine_bricks", block44);
      Block block45 = new Block(Block.Properties.create(Material.ROCK, MaterialColor.DIAMOND).hardnessAndResistance(1.5F, 6.0F));
      register("dark_prismarine", block45);
      register("prismarine_stairs", new BlockStairs(block43.getDefaultState(), Block.Properties.from(block43)));
      register("prismarine_brick_stairs", new BlockStairs(block44.getDefaultState(), Block.Properties.from(block44)));
      register("dark_prismarine_stairs", new BlockStairs(block45.getDefaultState(), Block.Properties.from(block45)));
      register("prismarine_slab", new BlockSlab(Block.Properties.create(Material.ROCK, MaterialColor.CYAN).hardnessAndResistance(1.5F, 6.0F)));
      register("prismarine_brick_slab", new BlockSlab(Block.Properties.create(Material.ROCK, MaterialColor.DIAMOND).hardnessAndResistance(1.5F, 6.0F)));
      register("dark_prismarine_slab", new BlockSlab(Block.Properties.create(Material.ROCK, MaterialColor.DIAMOND).hardnessAndResistance(1.5F, 6.0F)));
      register("sea_lantern", new BlockSeaLantern(Block.Properties.create(Material.GLASS, MaterialColor.QUARTZ).hardnessAndResistance(0.3F).sound(SoundType.GLASS).lightValue(15)));
      register("hay_block", new BlockHay(Block.Properties.create(Material.GRASS, MaterialColor.YELLOW).hardnessAndResistance(0.5F).sound(SoundType.PLANT)));
      register("white_carpet", new BlockCarpet(EnumDyeColor.WHITE, Block.Properties.create(Material.CARPET, MaterialColor.SNOW).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("orange_carpet", new BlockCarpet(EnumDyeColor.ORANGE, Block.Properties.create(Material.CARPET, MaterialColor.ADOBE).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("magenta_carpet", new BlockCarpet(EnumDyeColor.MAGENTA, Block.Properties.create(Material.CARPET, MaterialColor.MAGENTA).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("light_blue_carpet", new BlockCarpet(EnumDyeColor.LIGHT_BLUE, Block.Properties.create(Material.CARPET, MaterialColor.LIGHT_BLUE).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("yellow_carpet", new BlockCarpet(EnumDyeColor.YELLOW, Block.Properties.create(Material.CARPET, MaterialColor.YELLOW).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("lime_carpet", new BlockCarpet(EnumDyeColor.LIME, Block.Properties.create(Material.CARPET, MaterialColor.LIME).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("pink_carpet", new BlockCarpet(EnumDyeColor.PINK, Block.Properties.create(Material.CARPET, MaterialColor.PINK).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("gray_carpet", new BlockCarpet(EnumDyeColor.GRAY, Block.Properties.create(Material.CARPET, MaterialColor.GRAY).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("light_gray_carpet", new BlockCarpet(EnumDyeColor.LIGHT_GRAY, Block.Properties.create(Material.CARPET, MaterialColor.LIGHT_GRAY).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("cyan_carpet", new BlockCarpet(EnumDyeColor.CYAN, Block.Properties.create(Material.CARPET, MaterialColor.CYAN).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("purple_carpet", new BlockCarpet(EnumDyeColor.PURPLE, Block.Properties.create(Material.CARPET, MaterialColor.PURPLE).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("blue_carpet", new BlockCarpet(EnumDyeColor.BLUE, Block.Properties.create(Material.CARPET, MaterialColor.BLUE).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("brown_carpet", new BlockCarpet(EnumDyeColor.BROWN, Block.Properties.create(Material.CARPET, MaterialColor.BROWN).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("green_carpet", new BlockCarpet(EnumDyeColor.GREEN, Block.Properties.create(Material.CARPET, MaterialColor.GREEN).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("red_carpet", new BlockCarpet(EnumDyeColor.RED, Block.Properties.create(Material.CARPET, MaterialColor.RED).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("black_carpet", new BlockCarpet(EnumDyeColor.BLACK, Block.Properties.create(Material.CARPET, MaterialColor.BLACK).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
      register("terracotta", new Block(Block.Properties.create(Material.ROCK, MaterialColor.ADOBE).hardnessAndResistance(1.25F, 4.2F)));
      register("coal_block", new Block(Block.Properties.create(Material.ROCK, MaterialColor.BLACK).hardnessAndResistance(5.0F, 6.0F)));
      register("packed_ice", new BlockPackedIce(Block.Properties.create(Material.PACKED_ICE).slipperiness(0.98F).hardnessAndResistance(0.5F).sound(SoundType.GLASS)));
      register("sunflower", new BlockTallFlower(Block.Properties.create(Material.VINE).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT)));
      register("lilac", new BlockTallFlower(Block.Properties.create(Material.VINE).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT)));
      register("rose_bush", new BlockTallFlower(Block.Properties.create(Material.VINE).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT)));
      register("peony", new BlockTallFlower(Block.Properties.create(Material.VINE).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT)));
      register("tall_grass", new BlockShearableDoublePlant(block16, Block.Properties.create(Material.VINE).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT)));
      register("large_fern", new BlockShearableDoublePlant(block17, Block.Properties.create(Material.VINE).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT)));
      register("white_banner", new BlockBanner(EnumDyeColor.WHITE, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("orange_banner", new BlockBanner(EnumDyeColor.ORANGE, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("magenta_banner", new BlockBanner(EnumDyeColor.MAGENTA, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("light_blue_banner", new BlockBanner(EnumDyeColor.LIGHT_BLUE, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("yellow_banner", new BlockBanner(EnumDyeColor.YELLOW, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("lime_banner", new BlockBanner(EnumDyeColor.LIME, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("pink_banner", new BlockBanner(EnumDyeColor.PINK, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("gray_banner", new BlockBanner(EnumDyeColor.GRAY, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("light_gray_banner", new BlockBanner(EnumDyeColor.LIGHT_GRAY, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("cyan_banner", new BlockBanner(EnumDyeColor.CYAN, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("purple_banner", new BlockBanner(EnumDyeColor.PURPLE, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("blue_banner", new BlockBanner(EnumDyeColor.BLUE, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("brown_banner", new BlockBanner(EnumDyeColor.BROWN, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("green_banner", new BlockBanner(EnumDyeColor.GREEN, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("red_banner", new BlockBanner(EnumDyeColor.RED, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("black_banner", new BlockBanner(EnumDyeColor.BLACK, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("white_wall_banner", new BlockBannerWall(EnumDyeColor.WHITE, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("orange_wall_banner", new BlockBannerWall(EnumDyeColor.ORANGE, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("magenta_wall_banner", new BlockBannerWall(EnumDyeColor.MAGENTA, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("light_blue_wall_banner", new BlockBannerWall(EnumDyeColor.LIGHT_BLUE, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("yellow_wall_banner", new BlockBannerWall(EnumDyeColor.YELLOW, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("lime_wall_banner", new BlockBannerWall(EnumDyeColor.LIME, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("pink_wall_banner", new BlockBannerWall(EnumDyeColor.PINK, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("gray_wall_banner", new BlockBannerWall(EnumDyeColor.GRAY, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("light_gray_wall_banner", new BlockBannerWall(EnumDyeColor.LIGHT_GRAY, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("cyan_wall_banner", new BlockBannerWall(EnumDyeColor.CYAN, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("purple_wall_banner", new BlockBannerWall(EnumDyeColor.PURPLE, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("blue_wall_banner", new BlockBannerWall(EnumDyeColor.BLUE, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("brown_wall_banner", new BlockBannerWall(EnumDyeColor.BROWN, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("green_wall_banner", new BlockBannerWall(EnumDyeColor.GREEN, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("red_wall_banner", new BlockBannerWall(EnumDyeColor.RED, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("black_wall_banner", new BlockBannerWall(EnumDyeColor.BLACK, Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      Block block46 = new Block(Block.Properties.create(Material.ROCK, MaterialColor.ADOBE).hardnessAndResistance(0.8F));
      register("red_sandstone", block46);
      register("chiseled_red_sandstone", new Block(Block.Properties.create(Material.ROCK, MaterialColor.ADOBE).hardnessAndResistance(0.8F)));
      register("cut_red_sandstone", new Block(Block.Properties.create(Material.ROCK, MaterialColor.ADOBE).hardnessAndResistance(0.8F)));
      register("red_sandstone_stairs", new BlockStairs(block46.getDefaultState(), Block.Properties.from(block46)));
      register("oak_slab", new BlockSlab(Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("spruce_slab", new BlockSlab(Block.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("birch_slab", new BlockSlab(Block.Properties.create(Material.WOOD, MaterialColor.SAND).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("jungle_slab", new BlockSlab(Block.Properties.create(Material.WOOD, MaterialColor.DIRT).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("acacia_slab", new BlockSlab(Block.Properties.create(Material.WOOD, MaterialColor.ADOBE).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("dark_oak_slab", new BlockSlab(Block.Properties.create(Material.WOOD, MaterialColor.BROWN).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("stone_slab", new BlockSlab(Block.Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(2.0F, 6.0F)));
      register("sandstone_slab", new BlockSlab(Block.Properties.create(Material.ROCK, MaterialColor.SAND).hardnessAndResistance(2.0F, 6.0F)));
      register("petrified_oak_slab", new BlockSlab(Block.Properties.create(Material.ROCK, MaterialColor.WOOD).hardnessAndResistance(2.0F, 6.0F)));
      register("cobblestone_slab", new BlockSlab(Block.Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(2.0F, 6.0F)));
      register("brick_slab", new BlockSlab(Block.Properties.create(Material.ROCK, MaterialColor.RED).hardnessAndResistance(2.0F, 6.0F)));
      register("stone_brick_slab", new BlockSlab(Block.Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(2.0F, 6.0F)));
      register("nether_brick_slab", new BlockSlab(Block.Properties.create(Material.ROCK, MaterialColor.NETHERRACK).hardnessAndResistance(2.0F, 6.0F)));
      register("quartz_slab", new BlockSlab(Block.Properties.create(Material.ROCK, MaterialColor.QUARTZ).hardnessAndResistance(2.0F, 6.0F)));
      register("red_sandstone_slab", new BlockSlab(Block.Properties.create(Material.ROCK, MaterialColor.ADOBE).hardnessAndResistance(2.0F, 6.0F)));
      register("purpur_slab", new BlockSlab(Block.Properties.create(Material.ROCK, MaterialColor.MAGENTA).hardnessAndResistance(2.0F, 6.0F)));
      register("smooth_stone", new Block(Block.Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(2.0F, 6.0F)));
      register("smooth_sandstone", new Block(Block.Properties.create(Material.ROCK, MaterialColor.SAND).hardnessAndResistance(2.0F, 6.0F)));
      register("smooth_quartz", new Block(Block.Properties.create(Material.ROCK, MaterialColor.QUARTZ).hardnessAndResistance(2.0F, 6.0F)));
      register("smooth_red_sandstone", new Block(Block.Properties.create(Material.ROCK, MaterialColor.ADOBE).hardnessAndResistance(2.0F, 6.0F)));
      register("spruce_fence_gate", new BlockFenceGate(Block.Properties.create(Material.WOOD, block4.blockMapColor).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("birch_fence_gate", new BlockFenceGate(Block.Properties.create(Material.WOOD, block5.blockMapColor).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("jungle_fence_gate", new BlockFenceGate(Block.Properties.create(Material.WOOD, block6.blockMapColor).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("acacia_fence_gate", new BlockFenceGate(Block.Properties.create(Material.WOOD, block7.blockMapColor).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("dark_oak_fence_gate", new BlockFenceGate(Block.Properties.create(Material.WOOD, block8.blockMapColor).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("spruce_fence", new BlockFence(Block.Properties.create(Material.WOOD, block4.blockMapColor).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("birch_fence", new BlockFence(Block.Properties.create(Material.WOOD, block5.blockMapColor).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("jungle_fence", new BlockFence(Block.Properties.create(Material.WOOD, block6.blockMapColor).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("acacia_fence", new BlockFence(Block.Properties.create(Material.WOOD, block7.blockMapColor).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("dark_oak_fence", new BlockFence(Block.Properties.create(Material.WOOD, block8.blockMapColor).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
      register("spruce_door", new BlockDoor(Block.Properties.create(Material.WOOD, block4.blockMapColor).hardnessAndResistance(3.0F).sound(SoundType.WOOD)));
      register("birch_door", new BlockDoor(Block.Properties.create(Material.WOOD, block5.blockMapColor).hardnessAndResistance(3.0F).sound(SoundType.WOOD)));
      register("jungle_door", new BlockDoor(Block.Properties.create(Material.WOOD, block6.blockMapColor).hardnessAndResistance(3.0F).sound(SoundType.WOOD)));
      register("acacia_door", new BlockDoor(Block.Properties.create(Material.WOOD, block7.blockMapColor).hardnessAndResistance(3.0F).sound(SoundType.WOOD)));
      register("dark_oak_door", new BlockDoor(Block.Properties.create(Material.WOOD, block8.blockMapColor).hardnessAndResistance(3.0F).sound(SoundType.WOOD)));
      register("end_rod", new BlockEndRod(Block.Properties.create(Material.CIRCUITS).zeroHardnessAndResistance().lightValue(14).sound(SoundType.WOOD)));
      BlockChorusPlant blockchorusplant = new BlockChorusPlant(Block.Properties.create(Material.PLANTS, MaterialColor.PURPLE).hardnessAndResistance(0.4F).sound(SoundType.WOOD));
      register("chorus_plant", blockchorusplant);
      register("chorus_flower", new BlockChorusFlower(blockchorusplant, Block.Properties.create(Material.PLANTS, MaterialColor.PURPLE).needsRandomTick().hardnessAndResistance(0.4F).sound(SoundType.WOOD)));
      Block block47 = new Block(Block.Properties.create(Material.ROCK, MaterialColor.MAGENTA).hardnessAndResistance(1.5F, 6.0F));
      register("purpur_block", block47);
      register("purpur_pillar", new BlockRotatedPillar(Block.Properties.create(Material.ROCK, MaterialColor.MAGENTA).hardnessAndResistance(1.5F, 6.0F)));
      register("purpur_stairs", new BlockStairs(block47.getDefaultState(), Block.Properties.from(block47)));
      register("end_stone_bricks", new Block(Block.Properties.create(Material.ROCK, MaterialColor.SAND).hardnessAndResistance(0.8F)));
      register("beetroots", new BlockBeetroot(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.PLANT)));
      Block block48 = new BlockGrassPath(Block.Properties.create(Material.GROUND).hardnessAndResistance(0.65F).sound(SoundType.PLANT));
      register("grass_path", block48);
      register("end_gateway", new BlockEndGateway(Block.Properties.create(Material.PORTAL, MaterialColor.BLACK).doesNotBlockMovement().lightValue(15).hardnessAndResistance(-1.0F, 3600000.0F)));
      register("repeating_command_block", new BlockCommandBlock(Block.Properties.create(Material.IRON, MaterialColor.PURPLE).hardnessAndResistance(-1.0F, 3600000.0F)));
      register("chain_command_block", new BlockCommandBlock(Block.Properties.create(Material.IRON, MaterialColor.GREEN).hardnessAndResistance(-1.0F, 3600000.0F)));
      register("frosted_ice", new BlockFrostedIce(Block.Properties.create(Material.ICE).slipperiness(0.98F).needsRandomTick().hardnessAndResistance(0.5F).sound(SoundType.GLASS)));
      register("magma_block", new BlockMagma(Block.Properties.create(Material.ROCK, MaterialColor.NETHERRACK).lightValue(3).needsRandomTick().hardnessAndResistance(0.5F)));
      register("nether_wart_block", new Block(Block.Properties.create(Material.GRASS, MaterialColor.RED).hardnessAndResistance(1.0F).sound(SoundType.WOOD)));
      register("red_nether_bricks", new Block(Block.Properties.create(Material.ROCK, MaterialColor.NETHERRACK).hardnessAndResistance(2.0F, 6.0F)));
      register("bone_block", new BlockRotatedPillar(Block.Properties.create(Material.ROCK, MaterialColor.SAND).hardnessAndResistance(2.0F)));
      register("structure_void", new BlockStructureVoid(Block.Properties.create(Material.STRUCTURE_VOID).doesNotBlockMovement()));
      register("observer", new BlockObserver(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F)));
      register("shulker_box", new BlockShulkerBox((EnumDyeColor)null, Block.Properties.create(Material.ROCK, MaterialColor.PURPLE).hardnessAndResistance(2.0F).variableOpacity()));
      register("white_shulker_box", new BlockShulkerBox(EnumDyeColor.WHITE, Block.Properties.create(Material.ROCK, MaterialColor.SNOW).hardnessAndResistance(2.0F).variableOpacity()));
      register("orange_shulker_box", new BlockShulkerBox(EnumDyeColor.ORANGE, Block.Properties.create(Material.ROCK, MaterialColor.ADOBE).hardnessAndResistance(2.0F).variableOpacity()));
      register("magenta_shulker_box", new BlockShulkerBox(EnumDyeColor.MAGENTA, Block.Properties.create(Material.ROCK, MaterialColor.MAGENTA).hardnessAndResistance(2.0F).variableOpacity()));
      register("light_blue_shulker_box", new BlockShulkerBox(EnumDyeColor.LIGHT_BLUE, Block.Properties.create(Material.ROCK, MaterialColor.LIGHT_BLUE).hardnessAndResistance(2.0F).variableOpacity()));
      register("yellow_shulker_box", new BlockShulkerBox(EnumDyeColor.YELLOW, Block.Properties.create(Material.ROCK, MaterialColor.YELLOW).hardnessAndResistance(2.0F).variableOpacity()));
      register("lime_shulker_box", new BlockShulkerBox(EnumDyeColor.LIME, Block.Properties.create(Material.ROCK, MaterialColor.LIME).hardnessAndResistance(2.0F).variableOpacity()));
      register("pink_shulker_box", new BlockShulkerBox(EnumDyeColor.PINK, Block.Properties.create(Material.ROCK, MaterialColor.PINK).hardnessAndResistance(2.0F).variableOpacity()));
      register("gray_shulker_box", new BlockShulkerBox(EnumDyeColor.GRAY, Block.Properties.create(Material.ROCK, MaterialColor.GRAY).hardnessAndResistance(2.0F).variableOpacity()));
      register("light_gray_shulker_box", new BlockShulkerBox(EnumDyeColor.LIGHT_GRAY, Block.Properties.create(Material.ROCK, MaterialColor.LIGHT_GRAY).hardnessAndResistance(2.0F).variableOpacity()));
      register("cyan_shulker_box", new BlockShulkerBox(EnumDyeColor.CYAN, Block.Properties.create(Material.ROCK, MaterialColor.CYAN).hardnessAndResistance(2.0F).variableOpacity()));
      register("purple_shulker_box", new BlockShulkerBox(EnumDyeColor.PURPLE, Block.Properties.create(Material.ROCK, MaterialColor.PURPLE_TERRACOTTA).hardnessAndResistance(2.0F).variableOpacity()));
      register("blue_shulker_box", new BlockShulkerBox(EnumDyeColor.BLUE, Block.Properties.create(Material.ROCK, MaterialColor.BLUE).hardnessAndResistance(2.0F).variableOpacity()));
      register("brown_shulker_box", new BlockShulkerBox(EnumDyeColor.BROWN, Block.Properties.create(Material.ROCK, MaterialColor.BROWN).hardnessAndResistance(2.0F).variableOpacity()));
      register("green_shulker_box", new BlockShulkerBox(EnumDyeColor.GREEN, Block.Properties.create(Material.ROCK, MaterialColor.GREEN).hardnessAndResistance(2.0F).variableOpacity()));
      register("red_shulker_box", new BlockShulkerBox(EnumDyeColor.RED, Block.Properties.create(Material.ROCK, MaterialColor.RED).hardnessAndResistance(2.0F).variableOpacity()));
      register("black_shulker_box", new BlockShulkerBox(EnumDyeColor.BLACK, Block.Properties.create(Material.ROCK, MaterialColor.BLACK).hardnessAndResistance(2.0F).variableOpacity()));
      register("white_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.WHITE).hardnessAndResistance(1.4F)));
      register("orange_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.ORANGE).hardnessAndResistance(1.4F)));
      register("magenta_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.MAGENTA).hardnessAndResistance(1.4F)));
      register("light_blue_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.LIGHT_BLUE).hardnessAndResistance(1.4F)));
      register("yellow_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.YELLOW).hardnessAndResistance(1.4F)));
      register("lime_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.LIME).hardnessAndResistance(1.4F)));
      register("pink_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.PINK).hardnessAndResistance(1.4F)));
      register("gray_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.GRAY).hardnessAndResistance(1.4F)));
      register("light_gray_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.LIGHT_GRAY).hardnessAndResistance(1.4F)));
      register("cyan_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.CYAN).hardnessAndResistance(1.4F)));
      register("purple_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.PURPLE).hardnessAndResistance(1.4F)));
      register("blue_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.BLUE).hardnessAndResistance(1.4F)));
      register("brown_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.BROWN).hardnessAndResistance(1.4F)));
      register("green_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.GREEN).hardnessAndResistance(1.4F)));
      register("red_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.RED).hardnessAndResistance(1.4F)));
      register("black_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.create(Material.ROCK, EnumDyeColor.BLACK).hardnessAndResistance(1.4F)));
      Block block49 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.WHITE).hardnessAndResistance(1.8F));
      Block block50 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.ORANGE).hardnessAndResistance(1.8F));
      Block block51 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.MAGENTA).hardnessAndResistance(1.8F));
      Block block52 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.LIGHT_BLUE).hardnessAndResistance(1.8F));
      Block block53 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.YELLOW).hardnessAndResistance(1.8F));
      Block block54 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.LIME).hardnessAndResistance(1.8F));
      Block block55 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.PINK).hardnessAndResistance(1.8F));
      Block block56 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.GRAY).hardnessAndResistance(1.8F));
      Block block57 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.LIGHT_GRAY).hardnessAndResistance(1.8F));
      Block block58 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.CYAN).hardnessAndResistance(1.8F));
      Block block59 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.PURPLE).hardnessAndResistance(1.8F));
      Block block60 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.BLUE).hardnessAndResistance(1.8F));
      Block block61 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.BROWN).hardnessAndResistance(1.8F));
      Block block62 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.GREEN).hardnessAndResistance(1.8F));
      Block block63 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.RED).hardnessAndResistance(1.8F));
      Block block64 = new Block(Block.Properties.create(Material.ROCK, EnumDyeColor.BLACK).hardnessAndResistance(1.8F));
      register("white_concrete", block49);
      register("orange_concrete", block50);
      register("magenta_concrete", block51);
      register("light_blue_concrete", block52);
      register("yellow_concrete", block53);
      register("lime_concrete", block54);
      register("pink_concrete", block55);
      register("gray_concrete", block56);
      register("light_gray_concrete", block57);
      register("cyan_concrete", block58);
      register("purple_concrete", block59);
      register("blue_concrete", block60);
      register("brown_concrete", block61);
      register("green_concrete", block62);
      register("red_concrete", block63);
      register("black_concrete", block64);
      register("white_concrete_powder", new BlockConcretePowder(block49, Block.Properties.create(Material.SAND, EnumDyeColor.WHITE).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("orange_concrete_powder", new BlockConcretePowder(block50, Block.Properties.create(Material.SAND, EnumDyeColor.ORANGE).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("magenta_concrete_powder", new BlockConcretePowder(block51, Block.Properties.create(Material.SAND, EnumDyeColor.MAGENTA).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("light_blue_concrete_powder", new BlockConcretePowder(block52, Block.Properties.create(Material.SAND, EnumDyeColor.LIGHT_BLUE).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("yellow_concrete_powder", new BlockConcretePowder(block53, Block.Properties.create(Material.SAND, EnumDyeColor.YELLOW).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("lime_concrete_powder", new BlockConcretePowder(block54, Block.Properties.create(Material.SAND, EnumDyeColor.LIME).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("pink_concrete_powder", new BlockConcretePowder(block55, Block.Properties.create(Material.SAND, EnumDyeColor.PINK).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("gray_concrete_powder", new BlockConcretePowder(block56, Block.Properties.create(Material.SAND, EnumDyeColor.GRAY).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("light_gray_concrete_powder", new BlockConcretePowder(block57, Block.Properties.create(Material.SAND, EnumDyeColor.LIGHT_GRAY).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("cyan_concrete_powder", new BlockConcretePowder(block58, Block.Properties.create(Material.SAND, EnumDyeColor.CYAN).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("purple_concrete_powder", new BlockConcretePowder(block59, Block.Properties.create(Material.SAND, EnumDyeColor.PURPLE).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("blue_concrete_powder", new BlockConcretePowder(block60, Block.Properties.create(Material.SAND, EnumDyeColor.BLUE).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("brown_concrete_powder", new BlockConcretePowder(block61, Block.Properties.create(Material.SAND, EnumDyeColor.BROWN).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("green_concrete_powder", new BlockConcretePowder(block62, Block.Properties.create(Material.SAND, EnumDyeColor.GREEN).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("red_concrete_powder", new BlockConcretePowder(block63, Block.Properties.create(Material.SAND, EnumDyeColor.RED).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      register("black_concrete_powder", new BlockConcretePowder(block64, Block.Properties.create(Material.SAND, EnumDyeColor.BLACK).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
      BlockKelpTop blockkelptop = new BlockKelpTop(Block.Properties.create(Material.OCEAN_PLANT).doesNotBlockMovement().needsRandomTick().zeroHardnessAndResistance().sound(SoundType.WET_GRASS));
      register("kelp", blockkelptop);
      register("kelp_plant", new BlockKelp(blockkelptop, Block.Properties.create(Material.OCEAN_PLANT).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      register("dried_kelp_block", new Block(Block.Properties.create(Material.GRASS, MaterialColor.BROWN).hardnessAndResistance(0.5F, 2.5F).sound(SoundType.PLANT)));
      register("turtle_egg", new BlockTurtleEgg(Block.Properties.create(Material.DRAGON_EGG, MaterialColor.LIGHT_GRAY).hardnessAndResistance(0.5F).sound(SoundType.METAL).needsRandomTick()));
      Block block65 = new Block(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).hardnessAndResistance(1.5F, 6.0F));
      Block block66 = new Block(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).hardnessAndResistance(1.5F, 6.0F));
      Block block67 = new Block(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).hardnessAndResistance(1.5F, 6.0F));
      Block block68 = new Block(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).hardnessAndResistance(1.5F, 6.0F));
      Block block69 = new Block(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).hardnessAndResistance(1.5F, 6.0F));
      register("dead_tube_coral_block", block65);
      register("dead_brain_coral_block", block66);
      register("dead_bubble_coral_block", block67);
      register("dead_fire_coral_block", block68);
      register("dead_horn_coral_block", block69);
      register("tube_coral_block", new BlockCoral(block65, Block.Properties.create(Material.ROCK, MaterialColor.BLUE).hardnessAndResistance(1.5F, 6.0F).sound(SoundType.CORAL)));
      register("brain_coral_block", new BlockCoral(block66, Block.Properties.create(Material.ROCK, MaterialColor.PINK).hardnessAndResistance(1.5F, 6.0F).sound(SoundType.CORAL)));
      register("bubble_coral_block", new BlockCoral(block67, Block.Properties.create(Material.ROCK, MaterialColor.PURPLE).hardnessAndResistance(1.5F, 6.0F).sound(SoundType.CORAL)));
      register("fire_coral_block", new BlockCoral(block68, Block.Properties.create(Material.ROCK, MaterialColor.RED).hardnessAndResistance(1.5F, 6.0F).sound(SoundType.CORAL)));
      register("horn_coral_block", new BlockCoral(block69, Block.Properties.create(Material.ROCK, MaterialColor.YELLOW).hardnessAndResistance(1.5F, 6.0F).sound(SoundType.CORAL)));
      Block block70 = new BlockCoralPlantDead(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).doesNotBlockMovement().zeroHardnessAndResistance());
      Block block71 = new BlockCoralPlantDead(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).doesNotBlockMovement().zeroHardnessAndResistance());
      Block block72 = new BlockCoralPlantDead(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).doesNotBlockMovement().zeroHardnessAndResistance());
      Block block73 = new BlockCoralPlantDead(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).doesNotBlockMovement().zeroHardnessAndResistance());
      Block block74 = new BlockCoralPlantDead(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).doesNotBlockMovement().zeroHardnessAndResistance());
      register("dead_tube_coral", block70);
      register("dead_brain_coral", block71);
      register("dead_bubble_coral", block72);
      register("dead_fire_coral", block73);
      register("dead_horn_coral", block74);
      register("tube_coral", new BlockCoralPlant(block70, Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.BLUE).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      register("brain_coral", new BlockCoralPlant(block71, Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.PINK).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      register("bubble_coral", new BlockCoralPlant(block72, Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.PURPLE).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      register("fire_coral", new BlockCoralPlant(block73, Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.RED).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      register("horn_coral", new BlockCoralPlant(block74, Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.YELLOW).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      Block block75 = new BlockCoralWallFanDead(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).doesNotBlockMovement().zeroHardnessAndResistance());
      Block block76 = new BlockCoralWallFanDead(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).doesNotBlockMovement().zeroHardnessAndResistance());
      Block block77 = new BlockCoralWallFanDead(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).doesNotBlockMovement().zeroHardnessAndResistance());
      Block block78 = new BlockCoralWallFanDead(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).doesNotBlockMovement().zeroHardnessAndResistance());
      Block block79 = new BlockCoralWallFanDead(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).doesNotBlockMovement().zeroHardnessAndResistance());
      register("dead_tube_coral_wall_fan", block75);
      register("dead_brain_coral_wall_fan", block76);
      register("dead_bubble_coral_wall_fan", block77);
      register("dead_fire_coral_wall_fan", block78);
      register("dead_horn_coral_wall_fan", block79);
      register("tube_coral_wall_fan", new BlockCoralWallFan(block75, Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.BLUE).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      register("brain_coral_wall_fan", new BlockCoralWallFan(block76, Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.PINK).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      register("bubble_coral_wall_fan", new BlockCoralWallFan(block77, Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.PURPLE).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      register("fire_coral_wall_fan", new BlockCoralWallFan(block78, Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.RED).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      register("horn_coral_wall_fan", new BlockCoralWallFan(block79, Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.YELLOW).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      Block block80 = new BlockCoralFan(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).doesNotBlockMovement().zeroHardnessAndResistance());
      Block block81 = new BlockCoralFan(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).doesNotBlockMovement().zeroHardnessAndResistance());
      Block block82 = new BlockCoralFan(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).doesNotBlockMovement().zeroHardnessAndResistance());
      Block block83 = new BlockCoralFan(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).doesNotBlockMovement().zeroHardnessAndResistance());
      Block block84 = new BlockCoralFan(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).doesNotBlockMovement().zeroHardnessAndResistance());
      register("dead_tube_coral_fan", block80);
      register("dead_brain_coral_fan", block81);
      register("dead_bubble_coral_fan", block82);
      register("dead_fire_coral_fan", block83);
      register("dead_horn_coral_fan", block84);
      register("tube_coral_fan", new BlockCoralFin(block80, Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.BLUE).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      register("brain_coral_fan", new BlockCoralFin(block81, Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.PINK).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      register("bubble_coral_fan", new BlockCoralFin(block82, Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.PURPLE).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      register("fire_coral_fan", new BlockCoralFin(block83, Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.RED).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      register("horn_coral_fan", new BlockCoralFin(block84, Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.YELLOW).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)));
      register("sea_pickle", new BlockSeaPickle(Block.Properties.create(Material.OCEAN_PLANT, MaterialColor.GREEN).lightValue(3).sound(SoundType.SLIME)));
      register("blue_ice", new BlockBlueIce(Block.Properties.create(Material.PACKED_ICE).hardnessAndResistance(2.8F).slipperiness(0.989F).sound(SoundType.GLASS)));
      register("conduit", new BlockConduit(Block.Properties.create(Material.GLASS, MaterialColor.DIAMOND).hardnessAndResistance(3.0F).lightValue(15)));
      register("void_air", new BlockAir(Block.Properties.create(Material.AIR).doesNotBlockMovement()));
      register("cave_air", new BlockAir(Block.Properties.create(Material.AIR).doesNotBlockMovement()));
      register("bubble_column", new BlockBubbleColumn(Block.Properties.create(Material.BUBBLE_COLUMN).doesNotBlockMovement()));
      register("structure_block", new BlockStructure(Block.Properties.create(Material.IRON, MaterialColor.LIGHT_GRAY).hardnessAndResistance(-1.0F, 3600000.0F)));

      if(false) // Processed in GameData.BlockCallbacks#onBake
      for(Block block85 : IRegistry.field_212618_g) {
         for(IBlockState iblockstate : block85.getStateContainer().getValidStates()) {
            BLOCK_STATE_IDS.add(iblockstate);
         }
      }

   }

   private static void register(ResourceLocation key, Block blockIn) {
      IRegistry.field_212618_g.put(key, blockIn);
   }

   private static void register(String key, Block blockIn) {
      register(new ResourceLocation(key), blockIn);
   }

   public static enum EnumOffsetType {
      NONE,
      XZ,
      XYZ;
   }

   public static class Properties {
      private Material material;
      private MaterialColor mapColor;
      private boolean blocksMovement = true;
      private SoundType soundType = SoundType.STONE;
      private int lightValue;
      private float resistance;
      private float hardness;
      private boolean needsRandomTick;
      private float slipperiness = 0.6F;
      private boolean variableOpacity;

      private Properties(Material materialIn, MaterialColor mapColorIn) {
         this.material = materialIn;
         this.mapColor = mapColorIn;
      }

      public static Block.Properties create(Material materialIn) {
         return create(materialIn, materialIn.getColor());
      }

      public static Block.Properties create(Material materialIn, EnumDyeColor color) {
         return create(materialIn, color.getMapColor());
      }

      public static Block.Properties create(Material materialIn, MaterialColor mapColorIn) {
         return new Block.Properties(materialIn, mapColorIn);
      }

      public static Block.Properties from(Block blockIn) {
         Block.Properties block$properties = new Block.Properties(blockIn.material, blockIn.blockMapColor);
         block$properties.material = blockIn.material;
         block$properties.hardness = blockIn.blockHardness;
         block$properties.resistance = blockIn.blockResistance;
         block$properties.blocksMovement = blockIn.blocksMovement;
         block$properties.needsRandomTick = blockIn.needsRandomTick;
         block$properties.lightValue = blockIn.lightValue;
         block$properties.material = blockIn.material;
         block$properties.mapColor = blockIn.blockMapColor;
         block$properties.soundType = blockIn.soundType;
         block$properties.slipperiness = blockIn.getSlipperiness();
         block$properties.variableOpacity = blockIn.variableOpacity;
         return block$properties;
      }

      public Block.Properties doesNotBlockMovement() {
         this.blocksMovement = false;
         return this;
      }

      public Block.Properties slipperiness(float slipperinessIn) {
         this.slipperiness = slipperinessIn;
         return this;
      }

      public Block.Properties sound(SoundType soundTypeIn) {
         this.soundType = soundTypeIn;
         return this;
      }

      public Block.Properties lightValue(int lightValueIn) {
         this.lightValue = lightValueIn;
         return this;
      }

      public Block.Properties hardnessAndResistance(float hardnessIn, float resistanceIn) {
         this.hardness = hardnessIn;
         this.resistance = Math.max(0.0F, resistanceIn);
         return this;
      }

      protected Block.Properties zeroHardnessAndResistance() {
         return this.hardnessAndResistance(0.0F);
      }

      public Block.Properties hardnessAndResistance(float hardnessAndResistance) {
         this.hardnessAndResistance(hardnessAndResistance, hardnessAndResistance);
         return this;
      }

      public Block.Properties needsRandomTick() {
         this.needsRandomTick = true;
         return this;
      }

      public Block.Properties variableOpacity() {
         this.variableOpacity = true;
         return this;
      }
   }

   public static final class RenderSideCacheKey {
      private final IBlockState state;
      private final IBlockState adjacentState;
      private final EnumFacing side;

      public RenderSideCacheKey(IBlockState state, IBlockState adjacentState, EnumFacing side) {
         this.state = state;
         this.adjacentState = adjacentState;
         this.side = side;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (!(p_equals_1_ instanceof Block.RenderSideCacheKey)) {
            return false;
         } else {
            Block.RenderSideCacheKey block$rendersidecachekey = (Block.RenderSideCacheKey)p_equals_1_;
            return this.state == block$rendersidecachekey.state && this.adjacentState == block$rendersidecachekey.adjacentState && this.side == block$rendersidecachekey.side;
         }
      }

      public int hashCode() {
         return Objects.hash(this.state, this.adjacentState, this.side);
      }
   }
   /* ======================================== FORGE START =====================================*/
   protected Random RANDOM = new Random();
   protected ThreadLocal<EntityPlayer> harvesters = new ThreadLocal<>();
   private net.minecraftforge.common.ToolType harvestTool;
   private int harvestLevel;
   protected static ThreadLocal<Boolean> captureDrops = ThreadLocal.withInitial(() -> false);
   protected static ThreadLocal<NonNullList<ItemStack>> capturedDrops = ThreadLocal.withInitial(NonNullList::create);
   protected NonNullList<ItemStack> captureDrops(boolean start)
   {
      if (start) {
         captureDrops.set(true);
         capturedDrops.get().clear();
         return NonNullList.create();
      } else {
         captureDrops.set(false);
         return capturedDrops.get();
      }
   }

   @Override
   public float getSlipperiness(IBlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
      return this.slipperiness;
   }

   @Override
   public boolean canSilkHarvest(IBlockState state, IWorldReader world, BlockPos pos, EntityPlayer player) {
      return this.canSilkHarvest() && !state.hasTileEntity();
   }

   @Nullable
   @Override
   public net.minecraftforge.common.ToolType getHarvestTool(IBlockState state) {
      return harvestTool; //TODO: RE-Evaluate
   }

   @Override
   public int getHarvestLevel(IBlockState state) {
      return harvestLevel; //TODO: RE-Evaluate
   }

   @Override
   public boolean canSustainPlant(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing facing, net.minecraftforge.common.IPlantable plantable) {
       IBlockState plant = plantable.getPlant(world, pos.offset(facing));
       net.minecraftforge.common.EnumPlantType type = plantable.getPlantType(world, pos.offset(facing));

       if (plant.getBlock() == Blocks.CACTUS)
           return this.getBlock() == Blocks.CACTUS || this.getBlock() == Blocks.SAND || this.getBlock() == Blocks.RED_SAND;

       if (plant.getBlock() == Blocks.SUGAR_CANE && this == Blocks.SUGAR_CANE)
           return true;

       if (plantable instanceof BlockBush && ((BlockBush)plantable).isValidGround(state, world, pos))
           return true;

       switch (type) {
           case Desert: return this.getBlock() == Blocks.SAND || this.getBlock() == Blocks.TERRACOTTA || this.getBlock() instanceof BlockGlazedTerracotta;
           case Nether: return this.getBlock() == Blocks.SOUL_SAND;
           case Crop: return this.getBlock() == Blocks.FARMLAND;
           case Cave: return state.isTopSolid();
           case Plains: return this.getBlock() == Blocks.GRASS_BLOCK || Block.isDirt(this) || this.getBlock() == Blocks.FARMLAND;
           case Water: return state.getMaterial() == Material.WATER; //&& state.getValue(BlockLiquidWrapper)
           case Beach:
               boolean isBeach = this.getBlock() == Blocks.GRASS_BLOCK || Block.isDirt(this) || this.getBlock() == Blocks.SAND;
               boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
                       world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
                       world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
                       world.getBlockState(pos.south()).getMaterial() == Material.WATER);
               return isBeach && hasWater;
       }
       return false;
   }

   static {
      net.minecraftforge.common.ForgeHooks.setBlockToolSetter((block, tool, level) -> {
         block.harvestTool = tool;
         block.harvestLevel = level;
      });
   }
   /* ========================================= FORGE END ======================================*/
}