package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldOptimizer {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setDaemon(true).build();
   private final String worldName;
   private final ISaveHandler worldStorage;
   private final WorldSavedDataStorage field_212222_d;
   private final Thread thread;
   private volatile boolean active = true;
   private volatile boolean field_212225_g = false;
   private volatile float totalProgress;
   private volatile int totalChunks;
   private volatile int converted = 0;
   private volatile int skipped = 0;
   private final Object2FloatMap<DimensionType> field_212544_m = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap<>(Util.func_212443_g()));
   private volatile ITextComponent statusText = new TextComponentTranslation("optimizeWorld.stage.counting");

   public WorldOptimizer(String p_i49804_1_, ISaveFormat p_i49804_2_, WorldInfo p_i49804_3_) {
      this.worldName = p_i49804_3_.getWorldName();
      this.worldStorage = p_i49804_2_.getSaveLoader(p_i49804_1_, (MinecraftServer)null);
      this.worldStorage.saveWorldInfo(p_i49804_3_);
      this.field_212222_d = new WorldSavedDataStorage(this.worldStorage);
      this.thread = THREAD_FACTORY.newThread(this::optimize);
      this.thread.setUncaughtExceptionHandler(this::handleException);
      this.thread.start();
   }

   private void handleException(Thread p_212206_1_, Throwable p_212206_2_) {
      LOGGER.error("Error upgrading world", p_212206_2_);
      this.active = false;
      this.statusText = new TextComponentTranslation("optimizeWorld.stage.failed");
   }

   public void cancel() {
      this.active = false;

      try {
         this.thread.join();
      } catch (InterruptedException var2) {
         ;
      }

   }

   private void optimize() {
      File file1 = this.worldStorage.getWorldDirectory();
      WorldChunkEnumerator worldchunkenumerator = new WorldChunkEnumerator(file1);
      Builder<DimensionType, AnvilChunkLoader> builder = ImmutableMap.builder();

      for(DimensionType dimensiontype : DimensionType.func_212681_b()) {
         builder.put(dimensiontype, new AnvilChunkLoader(dimensiontype.func_212679_a(file1), this.worldStorage.getFixer()));
      }

      Map<DimensionType, AnvilChunkLoader> map = builder.build();
      long i = Util.milliTime();
      this.totalChunks = 0;
      Builder<DimensionType, ListIterator<ChunkPos>> builder1 = ImmutableMap.builder();

      for(DimensionType dimensiontype1 : DimensionType.func_212681_b()) {
         List<ChunkPos> list = worldchunkenumerator.func_212541_a(dimensiontype1);
         builder1.put(dimensiontype1, list.listIterator());
         this.totalChunks += list.size();
      }

      ImmutableMap<DimensionType, ListIterator<ChunkPos>> immutablemap = builder1.build();
      float f1 = (float)this.totalChunks;
      this.statusText = new TextComponentTranslation("optimizeWorld.stage.structures");

      for(Entry<DimensionType, AnvilChunkLoader> entry : map.entrySet()) {
         entry.getValue().func_212429_a(entry.getKey(), this.field_212222_d);
      }

      this.field_212222_d.saveAllData();
      this.statusText = new TextComponentTranslation("optimizeWorld.stage.upgrading");
      if (f1 <= 0.0F) {
         for(DimensionType dimensiontype3 : DimensionType.func_212681_b()) {
            this.field_212544_m.put(dimensiontype3, 1.0F / (float)map.size());
         }
      }

      while(this.active) {
         boolean flag = false;
         float f2 = 0.0F;

         for(DimensionType dimensiontype2 : DimensionType.func_212681_b()) {
            ListIterator<ChunkPos> listiterator = immutablemap.get(dimensiontype2);
            flag |= this.func_212542_a(map.get(dimensiontype2), listiterator, dimensiontype2);
            if (f1 > 0.0F) {
               float f = (float)listiterator.nextIndex() / f1;
               this.field_212544_m.put(dimensiontype2, f);
               f2 += f;
            }
         }

         this.totalProgress = f2;
         if (!flag) {
            this.active = false;
         }
      }

      this.statusText = new TextComponentTranslation("optimizeWorld.stage.finished");
      i = Util.milliTime() - i;
      LOGGER.info("World optimizaton finished after {} ms", (long)i);
      map.values().forEach(AnvilChunkLoader::flush);
      this.field_212222_d.saveAllData();
      this.worldStorage.flush();
      this.field_212225_g = true;
   }

   private boolean func_212542_a(AnvilChunkLoader p_212542_1_, ListIterator<ChunkPos> p_212542_2_, DimensionType p_212542_3_) {
      if (p_212542_2_.hasNext()) {
         boolean flag;
         synchronized(p_212542_1_) {
            flag = p_212542_1_.convert(p_212542_2_.next(), p_212542_3_, this.field_212222_d);
         }

         if (flag) {
            ++this.converted;
         } else {
            ++this.skipped;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isFinished() {
      return this.field_212225_g;
   }

   @OnlyIn(Dist.CLIENT)
   public float func_212543_a(DimensionType p_212543_1_) {
      return this.field_212544_m.getFloat(p_212543_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public float getTotalProgress() {
      return this.totalProgress;
   }

   public int getTotalChunks() {
      return this.totalChunks;
   }

   public int getConverted() {
      return this.converted;
   }

   public int getSkipped() {
      return this.skipped;
   }

   public ITextComponent getStatusText() {
      return this.statusText;
   }

   @OnlyIn(Dist.CLIENT)
   public String getWorldName() {
      return this.worldName;
   }
}