package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderWorker implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkRenderDispatcher chunkRenderDispatcher;
   private final RegionRenderCacheBuilder regionRenderCacheBuilder;
   private boolean shouldRun = true;

   public ChunkRenderWorker(ChunkRenderDispatcher chunkRenderDispatcherIn) {
      this(chunkRenderDispatcherIn, (RegionRenderCacheBuilder)null);
   }

   public ChunkRenderWorker(ChunkRenderDispatcher chunkRenderDispatcherIn, @Nullable RegionRenderCacheBuilder regionRenderCacheBuilderIn) {
      this.chunkRenderDispatcher = chunkRenderDispatcherIn;
      this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
   }

   public void run() {
      while(this.shouldRun) {
         try {
            this.processTask(this.chunkRenderDispatcher.getNextChunkUpdate());
         } catch (InterruptedException var3) {
            LOGGER.debug("Stopping chunk worker due to interrupt");
            return;
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Batching chunks");
            Minecraft.getInstance().crashed(Minecraft.getInstance().addGraphicsAndWorldToCrashReport(crashreport));
            return;
         }
      }

   }

   protected void processTask(final ChunkRenderTask generator) throws InterruptedException {
      generator.getLock().lock();

      try {
         if (generator.getStatus() != ChunkRenderTask.Status.PENDING) {
            if (!generator.isFinished()) {
               LOGGER.warn("Chunk render task was {} when I expected it to be pending; ignoring task", (Object)generator.getStatus());
            }

            return;
         }

         BlockPos blockpos = new BlockPos(Minecraft.getInstance().player);
         BlockPos blockpos1 = generator.getRenderChunk().getPosition();
         int i = 16;
         int j = 8;
         int k = 24;
         if (blockpos1.add(8, 8, 8).distanceSq(blockpos) > 576.0D) {
            World world = generator.getRenderChunk().getWorld();
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(blockpos1);
            if (!this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.WEST, 16), world) || !this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.NORTH, 16), world) || !this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.EAST, 16), world) || !this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.SOUTH, 16), world)) {
               return;
            }
         }

         generator.setStatus(ChunkRenderTask.Status.COMPILING);
      } finally {
         generator.getLock().unlock();
      }

      Entity entity = Minecraft.getInstance().getRenderViewEntity();
      if (entity == null) {
         generator.finish();
      } else {
         generator.setRegionRenderCacheBuilder(this.getRegionRenderCacheBuilder());
         Vec3d vec3d = ActiveRenderInfo.projectViewFromEntity(entity, 1.0D);
         float f = (float)vec3d.x;
         float f1 = (float)vec3d.y;
         float f2 = (float)vec3d.z;
         ChunkRenderTask.Type chunkrendertask$type = generator.getType();
         if (chunkrendertask$type == ChunkRenderTask.Type.REBUILD_CHUNK) {
            generator.getRenderChunk().rebuildChunk(f, f1, f2, generator);
         } else if (chunkrendertask$type == ChunkRenderTask.Type.RESORT_TRANSPARENCY) {
            generator.getRenderChunk().resortTransparency(f, f1, f2, generator);
         }

         generator.getLock().lock();

         try {
            if (generator.getStatus() != ChunkRenderTask.Status.COMPILING) {
               if (!generator.isFinished()) {
                  LOGGER.warn("Chunk render task was {} when I expected it to be compiling; aborting task", (Object)generator.getStatus());
               }

               this.freeRenderBuilder(generator);
               return;
            }

            generator.setStatus(ChunkRenderTask.Status.UPLOADING);
         } finally {
            generator.getLock().unlock();
         }

         final CompiledChunk compiledchunk = generator.getCompiledChunk();
         ArrayList lvt_9_1_ = Lists.newArrayList();
         if (chunkrendertask$type == ChunkRenderTask.Type.REBUILD_CHUNK) {
            for(BlockRenderLayer blockrenderlayer : BlockRenderLayer.values()) {
               if (compiledchunk.isLayerStarted(blockrenderlayer)) {
                  lvt_9_1_.add(this.chunkRenderDispatcher.uploadChunk(blockrenderlayer, generator.getRegionRenderCacheBuilder().getBuilder(blockrenderlayer), generator.getRenderChunk(), compiledchunk, generator.getDistanceSq()));
               }
            }
         } else if (chunkrendertask$type == ChunkRenderTask.Type.RESORT_TRANSPARENCY) {
            lvt_9_1_.add(this.chunkRenderDispatcher.uploadChunk(BlockRenderLayer.TRANSLUCENT, generator.getRegionRenderCacheBuilder().getBuilder(BlockRenderLayer.TRANSLUCENT), generator.getRenderChunk(), compiledchunk, generator.getDistanceSq()));
         }

         ListenableFuture<List<Object>> listenablefuture = Futures.allAsList(lvt_9_1_);
         generator.addFinishRunnable(() -> {
            listenablefuture.cancel(false);
         });
         Futures.addCallback(listenablefuture, new FutureCallback<List<Object>>() {
            public void onSuccess(@Nullable List<Object> p_onSuccess_1_) {
               ChunkRenderWorker.this.freeRenderBuilder(generator);
               generator.getLock().lock();

               label49: {
                  try {
                     if (generator.getStatus() == ChunkRenderTask.Status.UPLOADING) {
                        generator.setStatus(ChunkRenderTask.Status.DONE);
                        break label49;
                     }

                     if (!generator.isFinished()) {
                        ChunkRenderWorker.LOGGER.warn("Chunk render task was {} when I expected it to be uploading; aborting task", (Object)generator.getStatus());
                     }
                  } finally {
                     generator.getLock().unlock();
                  }

                  return;
               }

               generator.getRenderChunk().setCompiledChunk(compiledchunk);
            }

            public void onFailure(Throwable p_onFailure_1_) {
               ChunkRenderWorker.this.freeRenderBuilder(generator);
               if (!(p_onFailure_1_ instanceof CancellationException) && !(p_onFailure_1_ instanceof InterruptedException)) {
                  Minecraft.getInstance().crashed(CrashReport.makeCrashReport(p_onFailure_1_, "Rendering chunk"));
               }

            }
         });
      }
   }

   private boolean isChunkExisting(BlockPos pos, World worldIn) {
      return !worldIn.getChunk(pos.getX() >> 4, pos.getZ() >> 4).isEmpty();
   }

   private RegionRenderCacheBuilder getRegionRenderCacheBuilder() throws InterruptedException {
      return this.regionRenderCacheBuilder != null ? this.regionRenderCacheBuilder : this.chunkRenderDispatcher.allocateRenderBuilder();
   }

   private void freeRenderBuilder(ChunkRenderTask taskGenerator) {
      if (this.regionRenderCacheBuilder == null) {
         this.chunkRenderDispatcher.freeRenderBuilder(taskGenerator.getRegionRenderCacheBuilder());
      }

   }

   public void notifyToStop() {
      this.shouldRun = false;
   }
}