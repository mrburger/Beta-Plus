package net.minecraft.world.gen.tasks;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.ExpiringMap;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.Scheduler;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.SessionLockException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Scheduler.FutureWrapper;

public class ProtoChunkScheduler extends Scheduler<ChunkPos, ChunkStatus, ChunkPrimer> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final World world;
   private final IChunkGenerator<?> chunkGenerator;
   private final IChunkLoader chunkLoader;
   private final IThreadListener threadListener;
   private final Long2ObjectMap<Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper> scheduledChunkMap = new ExpiringMap<Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper>(8192, 5000) {
      /**
       * Returns true if the given item is allowed to expire within {@link #refreshTimes}.
       */
      protected boolean shouldExpire(Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper element) {
         ChunkPrimer chunkprimer = element.getResult();
         return !chunkprimer.isAlive() && !chunkprimer.isModified();
      }
   };

   public ProtoChunkScheduler(int concurrency, World worldIn, IChunkGenerator<?> chunkGeneratorIn, IChunkLoader chunkLoaderIn, IThreadListener threadListenerIn) {
      super("WorldGen", concurrency, ChunkStatus.FINALIZED, () -> {
         return new EnumMap<>(ChunkStatus.class);
      }, () -> {
         return new EnumMap<>(ChunkStatus.class);
      });
      this.world = worldIn;
      this.chunkGenerator = chunkGeneratorIn;
      this.chunkLoader = chunkLoaderIn;
      this.threadListener = threadListenerIn;
   }

   @Nullable
   protected Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper func_212252_a_(ChunkPos p_212252_1_, boolean p_212252_2_) {
      synchronized(this.chunkLoader) {
         return p_212252_2_ ? this.scheduledChunkMap.computeIfAbsent(p_212252_1_.asLong(), (p_212539_2_) -> {
            ChunkPrimer chunkprimer;
            try {
               chunkprimer = this.chunkLoader.loadChunkPrimer(this.world, p_212252_1_.x, p_212252_1_.z, (p_212538_0_) -> {
               });
            } catch (ReportedException reportedexception) {
               throw reportedexception;
            } catch (Exception exception) {
               LOGGER.error("Couldn't load protochunk", (Throwable)exception);
               chunkprimer = null;
            }

            if (chunkprimer != null) {
               chunkprimer.setLastSaveTime(this.world.getGameTime());
               return new Scheduler.FutureWrapper(p_212252_1_, chunkprimer, chunkprimer.getStatus());
            } else {
               return new Scheduler.FutureWrapper(p_212252_1_, new ChunkPrimer(p_212252_1_, UpgradeData.EMPTY), ChunkStatus.EMPTY);
            }
         }) : this.scheduledChunkMap.get(p_212252_1_.asLong());
      }
   }

   protected ChunkPrimer runTask(ChunkPos key, ChunkStatus taskType, Map<ChunkPos, ChunkPrimer> providingMap) {
      return taskType.runTask(this.world, this.chunkGenerator, providingMap, key.x, key.z);
   }

   protected Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper onTaskStart(ChunkPos pos, Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper futureWrapperIn) {
      futureWrapperIn.getResult().addRefCount(1);
      return futureWrapperIn;
   }

   protected void onTaskFinish(ChunkPos key, Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper futureWrapper) {
      futureWrapper.getResult().addRefCount(-1);
   }

   public void save(BooleanSupplier p_208484_1_) {
      synchronized(this.chunkLoader) {
         for(Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper scheduler : this.scheduledChunkMap.values()) {
            ChunkPrimer chunkprimer = scheduler.getResult();
            if (chunkprimer.isModified() && chunkprimer.getStatus().getType() == ChunkStatus.Type.PROTOCHUNK) {
               try {
                  chunkprimer.setLastSaveTime(this.world.getGameTime());
                  this.chunkLoader.saveChunk(this.world, chunkprimer);
                  chunkprimer.setModified(false);
               } catch (IOException ioexception) {
                  LOGGER.error("Couldn't save chunk", (Throwable)ioexception);
               } catch (SessionLockException sessionlockexception) {
                  LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable)sessionlockexception);
               }
            }

            if (!p_208484_1_.getAsBoolean()) {
               return;
            }
         }

      }
   }
}