package net.minecraft.world.biome.provider;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;

public class SingleBiomeProvider extends BiomeProvider {
   /** The biome generator object. */
   private final Biome biome;

   public SingleBiomeProvider(SingleBiomeProviderSettings settings) {
      this.biome = settings.getBiome();
   }

   public Biome getBiome(BlockPos pos, @Nullable Biome defaultBiome) {
      return this.biome;
   }

   public Biome[] getBiomes(int startX, int startZ, int xSize, int zSize) {
      return this.getBiomeBlock(startX, startZ, xSize, zSize);
   }

   public Biome[] getBiomes(int x, int z, int width, int length, boolean cacheFlag) {
      Biome[] abiome = new Biome[width * length];
      Arrays.fill(abiome, 0, width * length, this.biome);
      return abiome;
   }

   @Nullable
   public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random) {
      return biomes.contains(this.biome) ? new BlockPos(x - range + random.nextInt(range * 2 + 1), 0, z - range + random.nextInt(range * 2 + 1)) : null;
   }

   public boolean hasStructure(Structure<?> structureIn) {
      return this.hasStructureCache.computeIfAbsent(structureIn, this.biome::hasStructure);
   }

   public Set<IBlockState> getSurfaceBlocks() {
      if (this.topBlocksCache.isEmpty()) {
         this.topBlocksCache.add(this.biome.getSurfaceBuilderConfig().getTop());
      }

      return this.topBlocksCache;
   }

   public Set<Biome> getBiomesInSquare(int centerX, int centerZ, int sideLength) {
      return Sets.newHashSet(this.biome);
   }
}