package net.minecraft.world.biome.provider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;

public abstract class BiomeProvider implements ITickable {
   public static final List<Biome> BIOMES_TO_SPAWN_IN = Lists.newArrayList(Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.WOODED_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS);
   protected final Map<Structure<?>, Boolean> hasStructureCache = Maps.newHashMap();
   protected final Set<IBlockState> topBlocksCache = Sets.newHashSet();

   /**
    * Gets the list of valid biomes for the player to spawn in.
    */
   public List<Biome> getBiomesToSpawnIn() {
      return BIOMES_TO_SPAWN_IN;
   }

   public void tick() {
   }

   @Nullable
   public abstract Biome getBiome(BlockPos pos, @Nullable Biome defaultBiome);

   public abstract Biome[] getBiomes(int startX, int startZ, int xSize, int zSize);

   public Biome[] getBiomeBlock(int p_201539_1_, int p_201539_2_, int p_201539_3_, int p_201539_4_) {
      return this.getBiomes(p_201539_1_, p_201539_2_, p_201539_3_, p_201539_4_, true);
   }

   public abstract Biome[] getBiomes(int x, int z, int width, int length, boolean cacheFlag);

   public abstract Set<Biome> getBiomesInSquare(int centerX, int centerZ, int sideLength);

   @Nullable
   public abstract BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random);

   public float getHeightValue(int p_201536_1_, int p_201536_2_, int p_201536_3_, int p_201536_4_) {
      return 0.0F;
   }

   public abstract boolean hasStructure(Structure<?> structureIn);

   public abstract Set<IBlockState> getSurfaceBlocks();
}