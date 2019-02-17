package net.minecraft.world.gen;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;

public class ChunkGeneratorDebug extends AbstractChunkGenerator<DebugGenSettings> {
   /** A list of all valid block states. */
   private static final List<IBlockState> ALL_VALID_STATES = StreamSupport.stream(IRegistry.field_212618_g.spliterator(), false).flatMap((p_199812_0_) -> {
      return p_199812_0_.getStateContainer().getValidStates().stream();
   }).collect(Collectors.toList());
   private static final int GRID_WIDTH = MathHelper.ceil(MathHelper.sqrt((float)ALL_VALID_STATES.size()));
   private static final int GRID_HEIGHT = MathHelper.ceil((float)ALL_VALID_STATES.size() / (float)GRID_WIDTH);
   protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
   protected static final IBlockState BARRIER = Blocks.BARRIER.getDefaultState();
   private final DebugGenSettings settings;

   public ChunkGeneratorDebug(IWorld p_i48959_1_, BiomeProvider p_i48959_2_, DebugGenSettings p_i48959_3_) {
      super(p_i48959_1_, p_i48959_2_);
      this.settings = p_i48959_3_;
   }

   public void makeBase(IChunk chunkIn) {
      ChunkPos chunkpos = chunkIn.getPos();
      int i = chunkpos.x;
      int j = chunkpos.z;
      Biome[] abiome = this.biomeProvider.getBiomeBlock(i * 16, j * 16, 16, 16);
      chunkIn.setBiomes(abiome);
      chunkIn.createHeightMap(Heightmap.Type.WORLD_SURFACE_WG, Heightmap.Type.OCEAN_FLOOR_WG);
      chunkIn.setStatus(ChunkStatus.BASE);
   }

   public void carve(WorldGenRegion region, GenerationStage.Carving carvingStage) {
   }

   public DebugGenSettings getSettings() {
      return this.settings;
   }

   public double[] generateNoiseRegion(int x, int z) {
      return new double[0];
   }

   public int getGroundHeight() {
      return this.world.getSeaLevel() + 1;
   }

   public void decorate(WorldGenRegion region) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i = region.getMainChunkX();
      int j = region.getMainChunkZ();

      for(int k = 0; k < 16; ++k) {
         for(int l = 0; l < 16; ++l) {
            int i1 = (i << 4) + k;
            int j1 = (j << 4) + l;
            region.setBlockState(blockpos$mutableblockpos.setPos(i1, 60, j1), BARRIER, 2);
            IBlockState iblockstate = getBlockStateFor(i1, j1);
            if (iblockstate != null) {
               region.setBlockState(blockpos$mutableblockpos.setPos(i1, 70, j1), iblockstate, 2);
            }
         }
      }

   }

   public void spawnMobs(WorldGenRegion region) {
   }

   public static IBlockState getBlockStateFor(int p_177461_0_, int p_177461_1_) {
      IBlockState iblockstate = AIR;
      if (p_177461_0_ > 0 && p_177461_1_ > 0 && p_177461_0_ % 2 != 0 && p_177461_1_ % 2 != 0) {
         p_177461_0_ = p_177461_0_ / 2;
         p_177461_1_ = p_177461_1_ / 2;
         if (p_177461_0_ <= GRID_WIDTH && p_177461_1_ <= GRID_HEIGHT) {
            int i = MathHelper.abs(p_177461_0_ * GRID_WIDTH + p_177461_1_);
            if (i < ALL_VALID_STATES.size()) {
               iblockstate = ALL_VALID_STATES.get(i);
            }
         }
      }

      return iblockstate;
   }

   public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      Biome biome = this.world.getBiome(pos);
      return biome.getSpawns(creatureType);
   }

   public int spawnMobs(World worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
      return 0;
   }
}