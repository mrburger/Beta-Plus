package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ITickList;
import net.minecraft.world.TickPriority;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

public class ChunkPrimerTickList<T> implements ITickList<T> {
   protected final Predicate<T> filter;
   protected final Function<T, ResourceLocation> serializer;
   protected final Function<ResourceLocation, T> deserializer;
   private final ChunkPos chunkPos;
   private final ShortList[] packedPositions = new ShortList[16];

   public ChunkPrimerTickList(Predicate<T> filter, Function<T, ResourceLocation> toIdFunction, Function<ResourceLocation, T> fromIdFunction, ChunkPos chunkPosIn) {
      this.filter = filter;
      this.serializer = toIdFunction;
      this.deserializer = fromIdFunction;
      this.chunkPos = chunkPosIn;
   }

   public NBTTagList write() {
      return AnvilChunkLoader.listArrayToTag(this.packedPositions);
   }

   public void readToBeTickedListFromNBT(NBTTagList nbt) {
      for(int i = 0; i < nbt.size(); ++i) {
         NBTTagList nbttaglist = nbt.getList(i);

         for(int j = 0; j < nbttaglist.size(); ++j) {
            ChunkPrimer.getOrCreate(this.packedPositions, i).add(nbttaglist.getShort(j));
         }
      }

   }

   public void postProcess(ITickList<T> tickList, Function<BlockPos, T> func) {
      for(int i = 0; i < this.packedPositions.length; ++i) {
         if (this.packedPositions[i] != null) {
            for(Short oshort : this.packedPositions[i]) {
               BlockPos blockpos = ChunkPrimer.unpackToWorld(oshort, i, this.chunkPos);
               tickList.scheduleTick(blockpos, func.apply(blockpos), 0);
            }

            this.packedPositions[i].clear();
         }
      }

   }

   public boolean isTickScheduled(BlockPos pos, T itemIn) {
      return false;
   }

   public void scheduleTick(BlockPos pos, T itemIn, int scheduledTime, TickPriority priority) {
      ChunkPrimer.getOrCreate(this.packedPositions, pos.getY() >> 4).add(ChunkPrimer.packToLocal(pos));
   }

   /**
    * Checks if this position/item is scheduled to be updated this tick
    */
   public boolean isTickPending(BlockPos pos, T obj) {
      return false;
   }
}