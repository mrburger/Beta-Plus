package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

public interface IChunkGenerator<C extends IChunkGenSettings> {
   void makeBase(IChunk chunkIn);

   void carve(WorldGenRegion region, GenerationStage.Carving carvingStage);

   void decorate(WorldGenRegion region);

   void spawnMobs(WorldGenRegion region);

   List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos);

   @Nullable
   BlockPos findNearestStructure(World worldIn, String name, BlockPos pos, int radius, boolean p_211403_5_);

   C getSettings();

   int spawnMobs(World worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs);

   boolean hasStructure(Biome biomeIn, Structure<? extends IFeatureConfig> structureIn);

   @Nullable
   IFeatureConfig getStructureConfig(Biome biomeIn, Structure<? extends IFeatureConfig> structureIn);

   Long2ObjectMap<StructureStart> getStructureReferenceToStartMap(Structure<? extends IFeatureConfig> structureIn);

   Long2ObjectMap<LongSet> getStructurePositionToReferenceMap(Structure<? extends IFeatureConfig> structureIn);

   BiomeProvider getBiomeProvider();

   long getSeed();

   int getGroundHeight();

   int getMaxHeight();
}