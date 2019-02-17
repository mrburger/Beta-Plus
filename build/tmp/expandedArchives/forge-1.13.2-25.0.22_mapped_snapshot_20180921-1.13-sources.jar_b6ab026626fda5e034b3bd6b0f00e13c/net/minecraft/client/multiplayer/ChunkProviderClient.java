package net.minecraft.client.multiplayer;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkProviderClient implements IChunkProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   /**
    * The completely empty chunk used by ChunkProviderClient when chunkMapping doesn't contain the requested
    * coordinates.
    */
   private final Chunk blankChunk;
   /** The mapping between ChunkCoordinates and Chunks that ChunkProviderClient maintains. */
   private final Long2ObjectMap<Chunk> loadedChunks = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<Chunk>(8192) {
      protected void rehash(int p_rehash_1_) {
         if (p_rehash_1_ > this.key.length) {
            super.rehash(p_rehash_1_);
         }

      }
   });
   /** Reference to the World object. */
   private final World world;

   public ChunkProviderClient(World worldIn) {
      this.blankChunk = new EmptyChunk(worldIn, 0, 0);
      this.world = worldIn;
   }

   /**
    * Unload chunk from ChunkProviderClient's hashmap. Called in response to a Packet50PreChunk with its mode field set
    * to false
    */
   public void unloadChunk(int x, int z) {
      Chunk chunk = this.loadedChunks.remove(ChunkPos.asLong(x, z));
      if (chunk != null) {
         chunk.onUnload();
      }

   }

   @Nullable
   public Chunk provideChunk(int x, int z, boolean p_186025_3_, boolean p_186025_4_) {
      Chunk chunk = this.loadedChunks.get(ChunkPos.asLong(x, z));
      return p_186025_4_ && chunk == null ? this.blankChunk : chunk;
   }

   public Chunk func_212474_a(int p_212474_1_, int p_212474_2_, PacketBuffer p_212474_3_, int p_212474_4_, boolean p_212474_5_) {
      synchronized(this.loadedChunks) {
         long i = ChunkPos.asLong(p_212474_1_, p_212474_2_);
         Chunk chunk = this.loadedChunks.computeIfAbsent(i, (p_212475_3_) -> {
            return new Chunk(this.world, p_212474_1_, p_212474_2_, new Biome[256]);
         });
         chunk.read(p_212474_3_, p_212474_4_, p_212474_5_);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(chunk));
         chunk.markLoaded(true);
         return chunk;
      }
   }

   /**
    * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
    *  
    * The return value is ignored, and is always false.
    */
   public boolean tick(BooleanSupplier p_73156_1_) {
      long i = Util.milliTime();

      for(Chunk chunk : this.loadedChunks.values()) {
         chunk.tick(Util.milliTime() - i > 5L);
      }

      if (Util.milliTime() - i > 100L) {
         LOGGER.info("Warning: Clientside chunk ticking took {} ms", (long)(Util.milliTime() - i));
      }

      return false;
   }

   /**
    * Converts the instance data to a readable string.
    */
   public String makeString() {
      return "MultiplayerChunkCache: " + this.loadedChunks.size() + ", " + this.loadedChunks.size();
   }

   public IChunkGenerator<?> getChunkGenerator() {
      return null;
   }
}