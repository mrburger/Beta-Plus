package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.Feature;

public class ChunkGeneratorNether extends AbstractChunkGenerator<NetherGenSettings> {
   protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
   protected static final IBlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
   protected static final IBlockState LAVA = Blocks.LAVA.getDefaultState();
   private NoiseGeneratorOctaves lperlinNoise1;
   private NoiseGeneratorOctaves lperlinNoise2;
   private NoiseGeneratorOctaves perlinNoise1;
   /** Determines whether slowsand or gravel can be generated at a location */
   private NoiseGeneratorOctaves slowsandGravelNoiseGen;
   private NoiseGeneratorOctaves scaleNoise;
   private NoiseGeneratorOctaves depthNoise;
   private final NetherGenSettings settings;
   private final IBlockState defaultBlock;
   private final IBlockState defaultFluid;

   public ChunkGeneratorNether(World p_i48694_1_, BiomeProvider p_i48694_2_, NetherGenSettings p_i48694_3_) {
      super(p_i48694_1_, p_i48694_2_);
      this.settings = p_i48694_3_;
      this.defaultBlock = this.settings.getDefaultBlock();
      this.defaultFluid = this.settings.getDefaultFluid();
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom(this.seed);
      this.lperlinNoise1 = new NoiseGeneratorOctaves(sharedseedrandom, 16);
      this.lperlinNoise2 = new NoiseGeneratorOctaves(sharedseedrandom, 16);
      this.perlinNoise1 = new NoiseGeneratorOctaves(sharedseedrandom, 8);
      sharedseedrandom.skip(1048);
      this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(sharedseedrandom, 4);
      this.scaleNoise = new NoiseGeneratorOctaves(sharedseedrandom, 10);
      this.depthNoise = new NoiseGeneratorOctaves(sharedseedrandom, 16);
      p_i48694_1_.setSeaLevel(63);

      net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextHell ctx =
              new net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextHell(lperlinNoise1, lperlinNoise2, perlinNoise1, slowsandGravelNoiseGen, scaleNoise, depthNoise);
      ctx = net.minecraftforge.event.terraingen.TerrainGen.getModdedNoiseGenerators(p_i48694_1_, sharedseedrandom, ctx);
      this.lperlinNoise1 = ctx.getLPerlin1();
      this.lperlinNoise2 = ctx.getLPerlin2();
      this.perlinNoise1 = ctx.getPerlin();
      this.slowsandGravelNoiseGen = ctx.getPerlin2();
      this.scaleNoise = ctx.getScale();
      this.depthNoise = ctx.getDepth();
   }

   public void prepareHeights(int p_185936_1_, int p_185936_2_, IChunk primer) {
      int i = 4;
      int j = this.world.getSeaLevel() / 2 + 1;
      int k = 5;
      int l = 17;
      int i1 = 5;
      double[] adouble = this.func_202104_a(p_185936_1_ * 4, 0, p_185936_2_ * 4, 5, 17, 5);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int j1 = 0; j1 < 4; ++j1) {
         for(int k1 = 0; k1 < 4; ++k1) {
            for(int l1 = 0; l1 < 16; ++l1) {
               double d0 = 0.125D;
               double d1 = adouble[((j1 + 0) * 5 + k1 + 0) * 17 + l1 + 0];
               double d2 = adouble[((j1 + 0) * 5 + k1 + 1) * 17 + l1 + 0];
               double d3 = adouble[((j1 + 1) * 5 + k1 + 0) * 17 + l1 + 0];
               double d4 = adouble[((j1 + 1) * 5 + k1 + 1) * 17 + l1 + 0];
               double d5 = (adouble[((j1 + 0) * 5 + k1 + 0) * 17 + l1 + 1] - d1) * 0.125D;
               double d6 = (adouble[((j1 + 0) * 5 + k1 + 1) * 17 + l1 + 1] - d2) * 0.125D;
               double d7 = (adouble[((j1 + 1) * 5 + k1 + 0) * 17 + l1 + 1] - d3) * 0.125D;
               double d8 = (adouble[((j1 + 1) * 5 + k1 + 1) * 17 + l1 + 1] - d4) * 0.125D;

               for(int i2 = 0; i2 < 8; ++i2) {
                  double d9 = 0.25D;
                  double d10 = d1;
                  double d11 = d2;
                  double d12 = (d3 - d1) * 0.25D;
                  double d13 = (d4 - d2) * 0.25D;

                  for(int j2 = 0; j2 < 4; ++j2) {
                     double d14 = 0.25D;
                     double d15 = d10;
                     double d16 = (d11 - d10) * 0.25D;

                     for(int k2 = 0; k2 < 4; ++k2) {
                        IBlockState iblockstate = AIR;
                        if (l1 * 8 + i2 < j) {
                           iblockstate = this.defaultFluid;
                        }

                        if (d15 > 0.0D) {
                           iblockstate = this.defaultBlock;
                        }

                        int l2 = j2 + j1 * 4;
                        int i3 = i2 + l1 * 8;
                        int j3 = k2 + k1 * 4;
                        primer.setBlockState(blockpos$mutableblockpos.setPos(l2, i3, j3), iblockstate, false);
                        d15 += d16;
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

   protected void makeBedrock(IChunk chunkIn, Random random) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i = chunkIn.getPos().getXStart();
      int j = chunkIn.getPos().getZStart();

      for(BlockPos blockpos : BlockPos.getAllInBox(i, 0, j, i + 16, 0, j + 16)) {
         for(int k = 127; k > 122; --k) {
            if (k >= 127 - random.nextInt(5)) {
               chunkIn.setBlockState(blockpos$mutableblockpos.setPos(blockpos.getX(), k, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
            }
         }

         for(int l = 4; l >= 0; --l) {
            if (l <= random.nextInt(5)) {
               chunkIn.setBlockState(blockpos$mutableblockpos.setPos(blockpos.getX(), l, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
            }
         }
      }

   }

   public double[] generateNoiseRegion(int x, int z) {
      double d0 = 0.03125D;
      return this.slowsandGravelNoiseGen.func_202647_a(x << 4, z << 4, 0, 16, 16, 1, 0.0625D, 0.0625D, 0.0625D);
   }

   public void makeBase(IChunk chunkIn) {
      ChunkPos chunkpos = chunkIn.getPos();
      int i = chunkpos.x;
      int j = chunkpos.z;
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      sharedseedrandom.setBaseChunkSeed(i, j);
      Biome[] abiome = this.biomeProvider.getBiomeBlock(i * 16, j * 16, 16, 16);
      chunkIn.setBiomes(abiome);
      this.prepareHeights(i, j, chunkIn);
      this.buildSurface(chunkIn, abiome, sharedseedrandom, this.world.getSeaLevel());
      this.makeBedrock(chunkIn, sharedseedrandom);
      chunkIn.createHeightMap(Heightmap.Type.WORLD_SURFACE_WG, Heightmap.Type.OCEAN_FLOOR_WG);
      chunkIn.setStatus(ChunkStatus.BASE);
   }

   private double[] func_202104_a(int p_202104_1_, int p_202104_2_, int p_202104_3_, int p_202104_4_, int p_202104_5_, int p_202104_6_) {
      double[] adouble = new double[p_202104_4_ * p_202104_5_ * p_202104_6_];
      net.minecraftforge.event.terraingen.ChunkGeneratorEvent.InitNoiseField event = new net.minecraftforge.event.terraingen.ChunkGeneratorEvent.InitNoiseField(this, adouble, p_202104_1_, p_202104_2_, p_202104_3_, p_202104_4_, p_202104_5_, p_202104_6_);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
      if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.DENY) return event.getNoisefield();

      double d0 = 684.412D;
      double d1 = 2053.236D;
      this.scaleNoise.func_202647_a(p_202104_1_, p_202104_2_, p_202104_3_, p_202104_4_, 1, p_202104_6_, 1.0D, 0.0D, 1.0D);
      this.depthNoise.func_202647_a(p_202104_1_, p_202104_2_, p_202104_3_, p_202104_4_, 1, p_202104_6_, 100.0D, 0.0D, 100.0D);
      double[] adouble1 = this.perlinNoise1.func_202647_a(p_202104_1_, p_202104_2_, p_202104_3_, p_202104_4_, p_202104_5_, p_202104_6_, 8.555150000000001D, 34.2206D, 8.555150000000001D);
      double[] adouble2 = this.lperlinNoise1.func_202647_a(p_202104_1_, p_202104_2_, p_202104_3_, p_202104_4_, p_202104_5_, p_202104_6_, 684.412D, 2053.236D, 684.412D);
      double[] adouble3 = this.lperlinNoise2.func_202647_a(p_202104_1_, p_202104_2_, p_202104_3_, p_202104_4_, p_202104_5_, p_202104_6_, 684.412D, 2053.236D, 684.412D);
      double[] adouble4 = new double[p_202104_5_];

      for(int i = 0; i < p_202104_5_; ++i) {
         adouble4[i] = Math.cos((double)i * Math.PI * 6.0D / (double)p_202104_5_) * 2.0D;
         double d2 = (double)i;
         if (i > p_202104_5_ / 2) {
            d2 = (double)(p_202104_5_ - 1 - i);
         }

         if (d2 < 4.0D) {
            d2 = 4.0D - d2;
            adouble4[i] -= d2 * d2 * d2 * 10.0D;
         }
      }

      int l = 0;

      for(int i1 = 0; i1 < p_202104_4_; ++i1) {
         for(int j = 0; j < p_202104_6_; ++j) {
            double d3 = 0.0D;

            for(int k = 0; k < p_202104_5_; ++k) {
               double d4 = adouble4[k];
               double d5 = adouble2[l] / 512.0D;
               double d6 = adouble3[l] / 512.0D;
               double d7 = (adouble1[l] / 10.0D + 1.0D) / 2.0D;
               double d8;
               if (d7 < 0.0D) {
                  d8 = d5;
               } else if (d7 > 1.0D) {
                  d8 = d6;
               } else {
                  d8 = d5 + (d6 - d5) * d7;
               }

               d8 = d8 - d4;
               if (k > p_202104_5_ - 4) {
                  double d9 = (double)((float)(k - (p_202104_5_ - 4)) / 3.0F);
                  d8 = d8 * (1.0D - d9) - 10.0D * d9;
               }

               if ((double)k < 0.0D) {
                  double d10 = (0.0D - (double)k) / 4.0D;
                  d10 = MathHelper.clamp(d10, 0.0D, 1.0D);
                  d8 = d8 * (1.0D - d10) - 10.0D * d10;
               }

               adouble[l] = d8;
               ++l;
            }
         }
      }

      return adouble;
   }

   public void spawnMobs(WorldGenRegion region) {
   }

   public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      if (creatureType == EnumCreatureType.MONSTER) {
         if (Feature.FORTRESS.isPositionInsideStructure(this.world, pos)) {
            return Feature.FORTRESS.getSpawnList();
         }

         if (Feature.FORTRESS.isPositionInStructure(this.world, pos) && this.world.getBlockState(pos.down()).getBlock() == Blocks.NETHER_BRICKS) {
            return Feature.FORTRESS.getSpawnList();
         }
      }

      Biome biome = this.world.getBiome(pos);
      return biome.getSpawns(creatureType);
   }

   public int spawnMobs(World worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
      return 0;
   }

   public NetherGenSettings getSettings() {
      return this.settings;
   }

   public int getGroundHeight() {
      return this.world.getSeaLevel() + 1;
   }

   public int getMaxHeight() {
      return 128;
   }
}