package net.minecraft.world.gen;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.PhantomSpawner;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.SwampHutStructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkGeneratorOverworld extends AbstractChunkGenerator<OverworldGenSettings> {
   private static final Logger LOGGER = LogManager.getLogger();
   private NoiseGeneratorOctaves minLimitPerlinNoise;
   private NoiseGeneratorOctaves maxLimitPerlinNoise;
   private NoiseGeneratorOctaves mainPerlinNoise;
   private NoiseGeneratorPerlin surfaceNoise;
   private final OverworldGenSettings settings;
   private NoiseGeneratorOctaves scaleNoise;
   private NoiseGeneratorOctaves depthNoise;
   private final WorldType terrainType;
   private final float[] biomeWeights;
   private final PhantomSpawner phantomSpawner = new PhantomSpawner();
   private final IBlockState defaultBlock;
   private final IBlockState defaultFluid;

   public ChunkGeneratorOverworld(IWorld worldIn, BiomeProvider provider, OverworldGenSettings settingsIn) {
      super(worldIn, provider);
      this.terrainType = worldIn.getWorldInfo().getTerrainType();
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom(this.seed);
      this.minLimitPerlinNoise = new NoiseGeneratorOctaves(sharedseedrandom, 16);
      this.maxLimitPerlinNoise = new NoiseGeneratorOctaves(sharedseedrandom, 16);
      this.mainPerlinNoise = new NoiseGeneratorOctaves(sharedseedrandom, 8);
      this.surfaceNoise = new NoiseGeneratorPerlin(sharedseedrandom, 4);
      this.scaleNoise = new NoiseGeneratorOctaves(sharedseedrandom, 10);
      this.depthNoise = new NoiseGeneratorOctaves(sharedseedrandom, 16);
      this.biomeWeights = new float[25];

      for(int i = -2; i <= 2; ++i) {
         for(int j = -2; j <= 2; ++j) {
            float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
            this.biomeWeights[i + 2 + (j + 2) * 5] = f;
         }
      }

      this.settings = settingsIn;
      this.defaultBlock = this.settings.getDefaultBlock();
      this.defaultFluid = this.settings.getDefaultFluid();
      
      net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextOverworld ctx =
              new net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextOverworld(minLimitPerlinNoise, maxLimitPerlinNoise, mainPerlinNoise, surfaceNoise, scaleNoise, depthNoise);
      ctx = net.minecraftforge.event.terraingen.TerrainGen.getModdedNoiseGenerators(worldIn, sharedseedrandom, ctx);
      this.minLimitPerlinNoise = ctx.getLPerlin1();
      this.maxLimitPerlinNoise = ctx.getLPerlin2();
      this.mainPerlinNoise = ctx.getPerlin();
      this.surfaceNoise = ctx.getHeight();
      this.scaleNoise = ctx.getScale();
      this.depthNoise = ctx.getDepth();
   }

   public void makeBase(IChunk chunkIn) {
      ChunkPos chunkpos = chunkIn.getPos();
      int i = chunkpos.x;
      int j = chunkpos.z;
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      sharedseedrandom.setBaseChunkSeed(i, j);
      Biome[] abiome = this.biomeProvider.getBiomeBlock(i * 16, j * 16, 16, 16);
      chunkIn.setBiomes(abiome);
      this.setBlocksInChunk(i, j, chunkIn);
      chunkIn.createHeightMap(Heightmap.Type.WORLD_SURFACE_WG, Heightmap.Type.OCEAN_FLOOR_WG);
      this.buildSurface(chunkIn, abiome, sharedseedrandom, this.world.getSeaLevel());
      this.makeBedrock(chunkIn, sharedseedrandom);
      chunkIn.createHeightMap(Heightmap.Type.WORLD_SURFACE_WG, Heightmap.Type.OCEAN_FLOOR_WG);
      chunkIn.setStatus(ChunkStatus.BASE);
   }

   public void spawnMobs(WorldGenRegion region) {
      int i = region.getMainChunkX();
      int j = region.getMainChunkZ();
      Biome biome = region.getChunk(i, j).getBiomes()[0];
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      sharedseedrandom.setDecorationSeed(region.getSeed(), i << 4, j << 4);
      WorldEntitySpawner.performWorldGenSpawning(region, biome, i, j, sharedseedrandom);
   }

   public void setBlocksInChunk(int x, int z, IChunk primer) {
      Biome[] abiome = this.biomeProvider.getBiomes(primer.getPos().x * 4 - 2, primer.getPos().z * 4 - 2, 10, 10);
      double[] adouble = new double[825];
      this.generateHeightMap(abiome, primer.getPos().x * 4, 0, primer.getPos().z * 4, adouble);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i = 0; i < 4; ++i) {
         int j = i * 5;
         int k = (i + 1) * 5;

         for(int l = 0; l < 4; ++l) {
            int i1 = (j + l) * 33;
            int j1 = (j + l + 1) * 33;
            int k1 = (k + l) * 33;
            int l1 = (k + l + 1) * 33;

            for(int i2 = 0; i2 < 32; ++i2) {
               double d0 = 0.125D;
               double d1 = adouble[i1 + i2];
               double d2 = adouble[j1 + i2];
               double d3 = adouble[k1 + i2];
               double d4 = adouble[l1 + i2];
               double d5 = (adouble[i1 + i2 + 1] - d1) * 0.125D;
               double d6 = (adouble[j1 + i2 + 1] - d2) * 0.125D;
               double d7 = (adouble[k1 + i2 + 1] - d3) * 0.125D;
               double d8 = (adouble[l1 + i2 + 1] - d4) * 0.125D;

               for(int j2 = 0; j2 < 8; ++j2) {
                  double d9 = 0.25D;
                  double d10 = d1;
                  double d11 = d2;
                  double d12 = (d3 - d1) * 0.25D;
                  double d13 = (d4 - d2) * 0.25D;

                  for(int k2 = 0; k2 < 4; ++k2) {
                     double d14 = 0.25D;
                     double d16 = (d11 - d10) * 0.25D;
                     double lvt_48_1_ = d10 - d16;

                     for(int l2 = 0; l2 < 4; ++l2) {
                        blockpos$mutableblockpos.setPos(i * 4 + k2, i2 * 8 + j2, l * 4 + l2);
                        if ((lvt_48_1_ += d16) > 0.0D) {
                           primer.setBlockState(blockpos$mutableblockpos, this.defaultBlock, false);
                        } else if (i2 * 8 + j2 < this.settings.getSeaLevel()) {
                           primer.setBlockState(blockpos$mutableblockpos, this.defaultFluid, false);
                        }
                     }

                     d10 += d12;
                     d11 += d13;
                  }

                  d1 += d5;
                  d2 += d6;
                  d3 += d7;
                  d4 += d8;
               }
            }
         }
      }

   }

   private void generateHeightMap(Biome[] p_202108_1_, int x, int y, int z, double[] p_202108_5_) {
      double[] adouble = this.depthNoise.func_202646_a(x, z, 5, 5, this.settings.getDepthNoiseScaleX(), this.settings.getDepthNoiseScaleZ(), this.settings.getDepthNoiseScaleExponent());
      float f = this.settings.getCoordinateScale();
      float f1 = this.settings.getHeightScale();
      double[] adouble1 = this.mainPerlinNoise.func_202647_a(x, y, z, 5, 33, 5, (double)(f / this.settings.getMainNoiseScaleX()), (double)(f1 / this.settings.getMainNoiseScaleY()), (double)(f / this.settings.getMainNoiseScaleZ()));
      double[] adouble2 = this.minLimitPerlinNoise.func_202647_a(x, y, z, 5, 33, 5, (double)f, (double)f1, (double)f);
      double[] adouble3 = this.maxLimitPerlinNoise.func_202647_a(x, y, z, 5, 33, 5, (double)f, (double)f1, (double)f);
      int i = 0;
      int j = 0;

      for(int k = 0; k < 5; ++k) {
         for(int l = 0; l < 5; ++l) {
            float f2 = 0.0F;
            float f3 = 0.0F;
            float f4 = 0.0F;
            int i1 = 2;
            Biome biome = p_202108_1_[k + 2 + (l + 2) * 10];

            for(int j1 = -2; j1 <= 2; ++j1) {
               for(int k1 = -2; k1 <= 2; ++k1) {
                  Biome biome1 = p_202108_1_[k + j1 + 2 + (l + k1 + 2) * 10];
                  float f5 = this.settings.func_202203_v() + biome1.getDepth() * this.settings.func_202202_w();
                  float f6 = this.settings.func_202204_x() + biome1.getScale() * this.settings.func_202205_y();
                  if (this.terrainType == WorldType.AMPLIFIED && f5 > 0.0F) {
                     f5 = 1.0F + f5 * 2.0F;
                     f6 = 1.0F + f6 * 4.0F;
                  }

                  float f7 = this.biomeWeights[j1 + 2 + (k1 + 2) * 5] / (f5 + 2.0F);
                  if (biome1.getDepth() > biome.getDepth()) {
                     f7 /= 2.0F;
                  }

                  f2 += f6 * f7;
                  f3 += f5 * f7;
                  f4 += f7;
               }
            }

            f2 = f2 / f4;
            f3 = f3 / f4;
            f2 = f2 * 0.9F + 0.1F;
            f3 = (f3 * 4.0F - 1.0F) / 8.0F;
            double d7 = adouble[j] / 8000.0D;
            if (d7 < 0.0D) {
               d7 = -d7 * 0.3D;
            }

            d7 = d7 * 3.0D - 2.0D;
            if (d7 < 0.0D) {
               d7 = d7 / 2.0D;
               if (d7 < -1.0D) {
                  d7 = -1.0D;
               }

               d7 = d7 / 1.4D;
               d7 = d7 / 2.0D;
            } else {
               if (d7 > 1.0D) {
                  d7 = 1.0D;
               }

               d7 = d7 / 8.0D;
            }

            ++j;
            double d8 = (double)f3;
            double d9 = (double)f2;
            d8 = d8 + d7 * 0.2D;
            d8 = d8 * this.settings.func_202201_z() / 8.0D;
            double d0 = this.settings.func_202201_z() + d8 * 4.0D;

            for(int l1 = 0; l1 < 33; ++l1) {
               double d1 = ((double)l1 - d0) * this.settings.func_202206_A() * 128.0D / 256.0D / d9;
               if (d1 < 0.0D) {
                  d1 *= 4.0D;
               }

               double d2 = adouble2[i] / this.settings.getLowerLimitScale();
               double d3 = adouble3[i] / this.settings.getUpperLimitScale();
               double d4 = (adouble1[i] / 10.0D + 1.0D) / 2.0D;
               double d5 = MathHelper.clampedLerp(d2, d3, d4) - d1;
               if (l1 > 29) {
                  double d6 = (double)((float)(l1 - 29) / 3.0F);
                  d5 = d5 * (1.0D - d6) - 10.0D * d6;
               }

               p_202108_5_[i] = d5;
               ++i;
            }
         }
      }

   }

   public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      Biome biome = this.world.getBiome(pos);
      if (creatureType == EnumCreatureType.MONSTER && ((SwampHutStructure)Feature.SWAMP_HUT).func_202383_b(this.world, pos)) {
         return Feature.SWAMP_HUT.getSpawnList();
      } else {
         return creatureType == EnumCreatureType.MONSTER && Feature.OCEAN_MONUMENT.isPositionInStructure(this.world, pos) ? Feature.OCEAN_MONUMENT.getSpawnList() : biome.getSpawns(creatureType);
      }
   }

   public int spawnMobs(World worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
      int i = 0;
      i = i + this.phantomSpawner.spawnMobs(worldIn, spawnHostileMobs, spawnPeacefulMobs);
      return i;
   }

   public OverworldGenSettings getSettings() {
      return this.settings;
   }

   public double[] generateNoiseRegion(int x, int z) {
      double d0 = 0.03125D;
      return this.surfaceNoise.generateRegion((double)(x << 4), (double)(z << 4), 16, 16, 0.0625D, 0.0625D, 1.0D);
   }

   public int getGroundHeight() {
      return this.world.getSeaLevel() + 1;
   }
}