package net.minecraft.world.dimension;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.CheckerboardBiomeProvider;
import net.minecraft.world.biome.provider.CheckerboardBiomeProviderSettings;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGeneratorDebug;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.ChunkGeneratorNether;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.DebugGenSettings;
import net.minecraft.world.gen.EndGenSettings;
import net.minecraft.world.gen.FlatGenSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NetherGenSettings;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OverworldDimension extends Dimension {
   private final DimensionType type;
   public OverworldDimension() { this(DimensionType.OVERWORLD); }
   public OverworldDimension(DimensionType type) {
      this.type = type;
   }

   public DimensionType getType() {
      return type;
   }

   /**
    * Called to determine if the chunk at the given chunk coordinates within the provider's world can be dropped. Used
    * in WorldProviderSurface to prevent spawn chunks from being unloaded.
    */
   public boolean canDropChunk(int x, int z) {
      return (this.type != DimensionType.OVERWORLD || !this.world.isSpawnChunk(x, z)) && super.canDropChunk(x, z);
   }

   /**
    * Creates a new {@link BiomeProvider} for the WorldProvider, and also sets the values of {@link #hasSkylight} and
    * {@link #hasNoSky} appropriately.
    *  
    * Note that subclasses generally override this method without calling the parent version.
    */
   protected void init() {
      this.hasSkyLight = true;
   }

   public IChunkGenerator<? extends IChunkGenSettings> createChunkGenerator() {
      WorldType worldtype = this.world.getWorldInfo().getTerrainType();
      ChunkGeneratorType<FlatGenSettings, ChunkGeneratorFlat> chunkgeneratortype = ChunkGeneratorType.FLAT;
      ChunkGeneratorType<DebugGenSettings, ChunkGeneratorDebug> chunkgeneratortype1 = ChunkGeneratorType.DEBUG;
      ChunkGeneratorType<NetherGenSettings, ChunkGeneratorNether> chunkgeneratortype2 = ChunkGeneratorType.CAVES;
      ChunkGeneratorType<EndGenSettings, ChunkGeneratorEnd> chunkgeneratortype3 = ChunkGeneratorType.FLOATING_ISLANDS;
      ChunkGeneratorType<OverworldGenSettings, ChunkGeneratorOverworld> chunkgeneratortype4 = ChunkGeneratorType.SURFACE;
      BiomeProviderType<SingleBiomeProviderSettings, SingleBiomeProvider> biomeprovidertype = BiomeProviderType.FIXED;
      BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider> biomeprovidertype1 = BiomeProviderType.VANILLA_LAYERED;
      BiomeProviderType<CheckerboardBiomeProviderSettings, CheckerboardBiomeProvider> biomeprovidertype2 = BiomeProviderType.CHECKERBOARD;
      if (worldtype == WorldType.FLAT) {
         FlatGenSettings flatgensettings = FlatGenSettings.createFlatGenerator(new Dynamic<>(NBTDynamicOps.INSTANCE, this.world.getWorldInfo().getGeneratorOptions()));
         SingleBiomeProviderSettings singlebiomeprovidersettings1 = biomeprovidertype.createSettings().setBiome(flatgensettings.getBiome());
         return chunkgeneratortype.create(this.world, biomeprovidertype.create(singlebiomeprovidersettings1), flatgensettings);
      } else if (worldtype == WorldType.DEBUG_ALL_BLOCK_STATES) {
         SingleBiomeProviderSettings singlebiomeprovidersettings = biomeprovidertype.createSettings().setBiome(Biomes.PLAINS);
         return chunkgeneratortype1.create(this.world, biomeprovidertype.create(singlebiomeprovidersettings), chunkgeneratortype1.createSettings());
      } else if (worldtype != WorldType.BUFFET) {
         OverworldGenSettings overworldgensettings = chunkgeneratortype4.createSettings();
         OverworldBiomeProviderSettings overworldbiomeprovidersettings = biomeprovidertype1.createSettings().setWorldInfo(this.world.getWorldInfo()).setGeneratorSettings(overworldgensettings);
         return chunkgeneratortype4.create(this.world, biomeprovidertype1.create(overworldbiomeprovidersettings), overworldgensettings);
      } else {
         BiomeProvider biomeprovider = null;
         JsonElement jsonelement = Dynamic.convert(NBTDynamicOps.INSTANCE, JsonOps.INSTANCE, this.world.getWorldInfo().getGeneratorOptions());
         JsonObject jsonobject = jsonelement.getAsJsonObject();
         if (jsonobject.has("biome_source") && jsonobject.getAsJsonObject("biome_source").has("type") && jsonobject.getAsJsonObject("biome_source").has("options")) {
            ResourceLocation resourcelocation = new ResourceLocation(jsonobject.getAsJsonObject("biome_source").getAsJsonPrimitive("type").getAsString());
            JsonObject jsonobject1 = jsonobject.getAsJsonObject("biome_source").getAsJsonObject("options");
            Biome[] abiome = new Biome[]{Biomes.OCEAN};
            if (jsonobject1.has("biomes")) {
               JsonArray jsonarray = jsonobject1.getAsJsonArray("biomes");
               abiome = jsonarray.size() > 0 ? new Biome[jsonarray.size()] : new Biome[]{Biomes.OCEAN};

               for(int i = 0; i < jsonarray.size(); ++i) {
                  Biome biome = IRegistry.field_212624_m.func_212608_b(new ResourceLocation(jsonarray.get(i).getAsString()));
                  abiome[i] = biome != null ? biome : Biomes.OCEAN;
               }
            }

            if (BiomeProviderType.FIXED.getKey().equals(resourcelocation)) {
               SingleBiomeProviderSettings singlebiomeprovidersettings2 = biomeprovidertype.createSettings().setBiome(abiome[0]);
               biomeprovider = biomeprovidertype.create(singlebiomeprovidersettings2);
            }

            if (BiomeProviderType.CHECKERBOARD.getKey().equals(resourcelocation)) {
               int j = jsonobject1.has("size") ? jsonobject1.getAsJsonPrimitive("size").getAsInt() : 2;
               CheckerboardBiomeProviderSettings checkerboardbiomeprovidersettings = biomeprovidertype2.createSettings().func_206860_a(abiome).setSize(j);
               biomeprovider = biomeprovidertype2.create(checkerboardbiomeprovidersettings);
            }

            if (BiomeProviderType.VANILLA_LAYERED.getKey().equals(resourcelocation)) {
               OverworldBiomeProviderSettings overworldbiomeprovidersettings1 = biomeprovidertype1.createSettings().setGeneratorSettings(new OverworldGenSettings()).setWorldInfo(this.world.getWorldInfo());
               biomeprovider = biomeprovidertype1.create(overworldbiomeprovidersettings1);
            }
         }

         if (biomeprovider == null) {
            biomeprovider = biomeprovidertype.create(biomeprovidertype.createSettings().setBiome(Biomes.OCEAN));
         }

         IBlockState iblockstate = Blocks.STONE.getDefaultState();
         IBlockState iblockstate1 = Blocks.WATER.getDefaultState();
         if (jsonobject.has("chunk_generator") && jsonobject.getAsJsonObject("chunk_generator").has("options")) {
            if (jsonobject.getAsJsonObject("chunk_generator").getAsJsonObject("options").has("default_block")) {
               String s = jsonobject.getAsJsonObject("chunk_generator").getAsJsonObject("options").getAsJsonPrimitive("default_block").getAsString();
               Block block = IRegistry.field_212618_g.get(new ResourceLocation(s));
               if (block != null) {
                  iblockstate = block.getDefaultState();
               }
            }

            if (jsonobject.getAsJsonObject("chunk_generator").getAsJsonObject("options").has("default_fluid")) {
               String s1 = jsonobject.getAsJsonObject("chunk_generator").getAsJsonObject("options").getAsJsonPrimitive("default_fluid").getAsString();
               Block block1 = IRegistry.field_212618_g.get(new ResourceLocation(s1));
               if (block1 != null) {
                  iblockstate1 = block1.getDefaultState();
               }
            }
         }

         if (jsonobject.has("chunk_generator") && jsonobject.getAsJsonObject("chunk_generator").has("type")) {
            ResourceLocation resourcelocation1 = new ResourceLocation(jsonobject.getAsJsonObject("chunk_generator").getAsJsonPrimitive("type").getAsString());
            if (ChunkGeneratorType.CAVES.getId().equals(resourcelocation1)) {
               NetherGenSettings nethergensettings = chunkgeneratortype2.createSettings();
               nethergensettings.setDefautBlock(iblockstate);
               nethergensettings.setDefaultFluid(iblockstate1);
               return chunkgeneratortype2.create(this.world, biomeprovider, nethergensettings);
            }

            if (ChunkGeneratorType.FLOATING_ISLANDS.getId().equals(resourcelocation1)) {
               EndGenSettings endgensettings = chunkgeneratortype3.createSettings();
               endgensettings.setSpawnPos(new BlockPos(0, 64, 0));
               endgensettings.setDefautBlock(iblockstate);
               endgensettings.setDefaultFluid(iblockstate1);
               return chunkgeneratortype3.create(this.world, biomeprovider, endgensettings);
            }
         }

         OverworldGenSettings overworldgensettings1 = chunkgeneratortype4.createSettings();
         overworldgensettings1.setDefautBlock(iblockstate);
         overworldgensettings1.setDefaultFluid(iblockstate1);
         return chunkgeneratortype4.create(this.world, biomeprovider, overworldgensettings1);
      }
   }

   @Nullable
   public BlockPos findSpawn(ChunkPos p_206920_1_, boolean checkValid) {
      for(int i = p_206920_1_.getXStart(); i <= p_206920_1_.getXEnd(); ++i) {
         for(int j = p_206920_1_.getZStart(); j <= p_206920_1_.getZEnd(); ++j) {
            BlockPos blockpos = this.findSpawn(i, j, checkValid);
            if (blockpos != null) {
               return blockpos;
            }
         }
      }

      return null;
   }

   @Nullable
   public BlockPos findSpawn(int p_206921_1_, int p_206921_2_, boolean checkValid) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_206921_1_, 0, p_206921_2_);
      Biome biome = this.world.getBiome(blockpos$mutableblockpos);
      IBlockState iblockstate = biome.getSurfaceBuilderConfig().getTop();
      if (checkValid && !iblockstate.getBlock().isIn(BlockTags.VALID_SPAWN)) {
         return null;
      } else {
         Chunk chunk = this.world.getChunk(p_206921_1_ >> 4, p_206921_2_ >> 4);
         int i = chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, p_206921_1_ & 15, p_206921_2_ & 15);
         if (i < 0) {
            return null;
         } else if (chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE, p_206921_1_ & 15, p_206921_2_ & 15) > chunk.getTopBlockY(Heightmap.Type.OCEAN_FLOOR, p_206921_1_ & 15, p_206921_2_ & 15)) {
            return null;
         } else {
            for(int j = i + 1; j >= 0; --j) {
               blockpos$mutableblockpos.setPos(p_206921_1_, j, p_206921_2_);
               IBlockState iblockstate1 = this.world.getBlockState(blockpos$mutableblockpos);
               if (!iblockstate1.getFluidState().isEmpty()) {
                  break;
               }

               if (iblockstate1.equals(iblockstate)) {
                  return blockpos$mutableblockpos.up().toImmutable();
               }
            }

            return null;
         }
      }
   }

   /**
    * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
    */
   public float calculateCelestialAngle(long worldTime, float partialTicks) {
      int i = (int)(worldTime % 24000L);
      float f = ((float)i + partialTicks) / 24000.0F - 0.25F;
      if (f < 0.0F) {
         ++f;
      }

      if (f > 1.0F) {
         --f;
      }

      float f1 = 1.0F - (float)((Math.cos((double)f * Math.PI) + 1.0D) / 2.0D);
      f = f + (f1 - f) / 3.0F;
      return f;
   }

   /**
    * Returns 'true' if in the "main surface world", but 'false' if in the Nether or End dimensions.
    */
   public boolean isSurfaceWorld() {
      return true;
   }

   /**
    * Return Vec3D with biome specific fog color
    */
   @OnlyIn(Dist.CLIENT)
   public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
      float f = MathHelper.cos(p_76562_1_ * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      float f1 = 0.7529412F;
      float f2 = 0.84705883F;
      float f3 = 1.0F;
      f1 = f1 * (f * 0.94F + 0.06F);
      f2 = f2 * (f * 0.94F + 0.06F);
      f3 = f3 * (f * 0.91F + 0.09F);
      return new Vec3d((double)f1, (double)f2, (double)f3);
   }

   /**
    * True if the player can respawn in this dimension (true = overworld, false = nether).
    */
   public boolean canRespawnHere() {
      return true;
   }

   /**
    * Returns true if the given X,Z coordinate should show environmental fog.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean doesXZShowFog(int x, int z) {
      return false;
   }
}