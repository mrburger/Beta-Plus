package net.minecraft.world.chunk.storage;

import java.io.IOException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.storage.SessionLockException;

public interface IChunkLoader {
   @Nullable
   Chunk loadChunk(IWorld worldIn, int x, int z, Consumer<Chunk> consumer) throws IOException;

   @Nullable
   ChunkPrimer loadChunkPrimer(IWorld worldIn, int x, int z, Consumer<IChunk> consumer) throws IOException;

   void saveChunk(World worldIn, IChunk chunkIn) throws IOException, SessionLockException;

   /**
    * Flushes all pending chunks fully back to disk
    */
   void flush();
}