package net.minecraft.world.gen;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.ITickList;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldGenTickList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenRegion implements IWorld {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkPrimer[] chunkPrimers;
   private final int mainChunkX;
   private final int mainChunkZ;
   private final int sizeX;
   private final int sizeZ;
   private final World world;
   private final long seed;
   private final int seaLevel;
   private final WorldInfo worldInfo;
   private final Random random;
   private final Dimension dimension;
   private final IChunkGenSettings chunkGenSettings;
   private final ITickList<Block> pendingBlockTickList = new WorldGenTickList<>((p_205335_1_) -> {
      return this.getChunkDefault(p_205335_1_).getBlocksToBeTicked();
   });
   private final ITickList<Fluid> pendingFluidTickList = new WorldGenTickList<>((p_205334_1_) -> {
      return this.getChunkDefault(p_205334_1_).func_212247_j();
   });

   public WorldGenRegion(ChunkPrimer[] chunkPrimersIn, int sizeXIn, int sizeZIn, int mainChunkXIn, int mainChunkZIn, World worldIn) {
      this.chunkPrimers = chunkPrimersIn;
      this.mainChunkX = mainChunkXIn;
      this.mainChunkZ = mainChunkZIn;
      this.sizeX = sizeXIn;
      this.sizeZ = sizeZIn;
      this.world = worldIn;
      this.seed = worldIn.getSeed();
      this.chunkGenSettings = worldIn.getChunkProvider().getChunkGenerator().getSettings();
      this.seaLevel = worldIn.getSeaLevel();
      this.worldInfo = worldIn.getWorldInfo();
      this.random = worldIn.getRandom();
      this.dimension = worldIn.getDimension();
   }

   public int getMainChunkX() {
      return this.mainChunkX;
   }

   public int getMainChunkZ() {
      return this.mainChunkZ;
   }

   public boolean isChunkInBounds(int chunkX, int chunkZ) {
      IChunk ichunk = this.chunkPrimers[0];
      IChunk ichunk1 = this.chunkPrimers[this.chunkPrimers.length - 1];
      return chunkX >= ichunk.getPos().x && chunkX <= ichunk1.getPos().x && chunkZ >= ichunk.getPos().z && chunkZ <= ichunk1.getPos().z;
   }

   /**
    * Gets the chunk at the specified location.
    */
   public IChunk getChunk(int chunkX, int chunkZ) {
      if (this.isChunkInBounds(chunkX, chunkZ)) {
         int i = chunkX - this.chunkPrimers[0].getPos().x;
         int j = chunkZ - this.chunkPrimers[0].getPos().z;
         return this.chunkPrimers[i + j * this.sizeX];
      } else {
         IChunk ichunk = this.chunkPrimers[0];
         IChunk ichunk1 = this.chunkPrimers[this.chunkPrimers.length - 1];
         LOGGER.error("Requested chunk : {} {}", chunkX, chunkZ);
         LOGGER.error("Region bounds : {} {} | {} {}", ichunk.getPos().x, ichunk.getPos().z, ichunk1.getPos().x, ichunk1.getPos().z);
         throw new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", chunkX, chunkZ));
      }
   }

   public IBlockState getBlockState(BlockPos pos) {
      return this.getChunkDefault(pos).getBlockState(pos);
   }

   public IFluidState getFluidState(BlockPos pos) {
      return this.getChunkDefault(pos).getFluidState(pos);
   }

   @Nullable
   public EntityPlayer getClosestPlayer(double x, double y, double z, double distance, Predicate<Entity> predicate) {
      return null;
   }

   public int getSkylightSubtracted() {
      return 0;
   }

   /**
    * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
    * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
    */
   public boolean isAirBlock(BlockPos pos) {
      return this.getBlockState(pos).isAir(this, pos);
   }

   public Biome getBiome(BlockPos pos) {
      Biome biome = this.getChunkDefault(pos).getBiomes()[pos.getX() & 15 | (pos.getZ() & 15) << 4];
      if (biome == null) {
         throw new RuntimeException(String.format("Biome is null @ %s", pos));
      } else {
         return biome;
      }
   }

   public int getLightFor(EnumLightType type, BlockPos pos) {
      IChunk ichunk = this.getChunkDefault(pos);
      return ichunk.getLight(type, pos, this.getDimension().hasSkyLight());
   }

   public int getLightSubtracted(BlockPos pos, int amount) {
      return this.getChunkDefault(pos).getLightSubtracted(pos, amount, this.getDimension().hasSkyLight());
   }

   public boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
      return this.isChunkInBounds(x, z);
   }

   /**
    * Sets a block to air, but also plays the sound and particles and can spawn drops
    */
   public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
      IBlockState iblockstate = this.getBlockState(pos);
      if (iblockstate.isAir()) {
         return false;
      } else {
         if (dropBlock) {
            iblockstate.dropBlockAsItem(this.world, pos, 0);
         }

         return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
      }
   }

   public boolean canSeeSky(BlockPos pos) {
      return this.getChunkDefault(pos).canSeeSky(pos);
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos) {
      IChunk ichunk = this.getChunkDefault(pos);
      TileEntity tileentity = ichunk.getTileEntity(pos);
      if (tileentity != null) {
         return tileentity;
      } else {
         NBTTagCompound nbttagcompound = ichunk.getDeferredTileEntity(pos);
         if (nbttagcompound != null) {
            if ("DUMMY".equals(nbttagcompound.getString("id"))) {
               tileentity = this.getBlockState(pos).createTileEntity(this.world);
            } else {
               tileentity = TileEntity.create(nbttagcompound);
            }

            if (tileentity != null) {
               ichunk.addTileEntity(pos, tileentity);
               return tileentity;
            }
         }

         if (ichunk.getBlockState(pos).hasTileEntity()) {
            LOGGER.warn("Tried to access a block entity before it was created. {}", (Object)pos);
         }

         return null;
      }
   }

   /**
    * Sets a block state into this world.Flags are as follows:
    * 1 will cause a block update.
    * 2 will send the change to clients.
    * 4 will prevent the block from being re-rendered.
    * 8 will force any re-renders to run on the main thread instead
    * 16 will prevent neighbor reactions (e.g. fences connecting, observers pulsing).
    * 32 will prevent neighbor reactions from spawning drops.
    * 64 will signify the block is being moved.
    * Flags can be OR-ed
    */
   public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
      IChunk ichunk = this.getChunkDefault(pos);
      IBlockState iblockstate = ichunk.setBlockState(pos, newState, false);
      Block block = newState.getBlock();
      if (newState.hasTileEntity()) {
         if (ichunk.getStatus().getType() == ChunkStatus.Type.LEVELCHUNK) {
            ichunk.addTileEntity(pos, newState.createTileEntity(this));
         } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInt("x", pos.getX());
            nbttagcompound.setInt("y", pos.getY());
            nbttagcompound.setInt("z", pos.getZ());
            nbttagcompound.setString("id", "DUMMY");
            ichunk.addTileEntity(nbttagcompound);
         }
      } else if (iblockstate != null && iblockstate.hasTileEntity()) {
         ichunk.removeTileEntity(pos);
      }

      if (newState.blockNeedsPostProcessing(this, pos)) {
         this.markBlockForPostprocessing(pos);
      }

      return true;
   }

   private void markBlockForPostprocessing(BlockPos pos) {
      this.getChunkDefault(pos).markBlockForPostprocessing(pos);
   }

   /**
    * Called when an entity is spawned in the world. This includes players.
    */
   public boolean spawnEntity(Entity entityIn) {
      int i = MathHelper.floor(entityIn.posX / 16.0D);
      int j = MathHelper.floor(entityIn.posZ / 16.0D);
      this.getChunk(i, j).addEntity(entityIn);
      return true;
   }

   public boolean removeBlock(BlockPos pos) {
      return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
   }

   public void setLightFor(EnumLightType type, BlockPos pos, int lightValue) {
      this.getChunkDefault(pos).setLightFor(type, this.dimension.hasSkyLight(), pos, lightValue);
   }

   public WorldBorder getWorldBorder() {
      return this.world.getWorldBorder();
   }

   public boolean checkNoEntityCollision(@Nullable Entity entityIn, VoxelShape shape) {
      return true;
   }

   public int getStrongPower(BlockPos pos, EnumFacing direction) {
      return this.getBlockState(pos).getStrongPower(this, pos, direction);
   }

   public boolean isRemote() {
      return false;
   }

   @Deprecated
   public World getWorld() {
      return this.world;
   }

   /**
    * Returns the world's WorldInfo object
    */
   public WorldInfo getWorldInfo() {
      return this.worldInfo;
   }

   public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
      if (!this.isChunkInBounds(pos.getX() >> 4, pos.getZ() >> 4)) {
         throw new RuntimeException("We are asking a region for a chunk out of bound");
      } else {
         return new DifficultyInstance(this.world.getDifficulty(), this.world.getDayTime(), 0L, this.world.getCurrentMoonPhaseFactor());
      }
   }

   @Nullable
   public WorldSavedDataStorage getMapStorage() {
      return this.world.getMapStorage();
   }

   /**
    * gets the world's chunk provider
    */
   public IChunkProvider getChunkProvider() {
      return this.world.getChunkProvider();
   }

   /**
    * Returns this world's current save handler
    */
   public ISaveHandler getSaveHandler() {
      return this.world.getSaveHandler();
   }

   /**
    * gets the random world seed
    */
   public long getSeed() {
      return this.seed;
   }

   public ITickList<Block> getPendingBlockTicks() {
      return this.pendingBlockTickList;
   }

   public ITickList<Fluid> getPendingFluidTicks() {
      return this.pendingFluidTickList;
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   public Random getRandom() {
      return this.random;
   }

   public void notifyNeighbors(BlockPos pos, Block blockIn) {
   }

   public int getHeight(Heightmap.Type heightmapType, int x, int z) {
      return this.getChunk(x >> 4, z >> 4).getTopBlockY(heightmapType, x & 15, z & 15) + 1;
   }

   /**
    * Plays the specified sound for a player at the center of the given block position.
    */
   public void playSound(@Nullable EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
   }

   public void spawnParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
   }

   /**
    * Gets the spawn point in the world
    */
   public BlockPos getSpawnPoint() {
      return this.world.getSpawnPoint();
   }

   public Dimension getDimension() {
      return this.dimension;
   }
}