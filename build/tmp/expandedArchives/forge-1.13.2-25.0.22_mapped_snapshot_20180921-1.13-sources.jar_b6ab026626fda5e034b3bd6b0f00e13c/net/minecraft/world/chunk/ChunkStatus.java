package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.util.ITaskType;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.tasks.BaseChunkTask;
import net.minecraft.world.gen.tasks.CarveChunkTask;
import net.minecraft.world.gen.tasks.ChunkTask;
import net.minecraft.world.gen.tasks.DecorateChunkTask;
import net.minecraft.world.gen.tasks.DummyChunkTask;
import net.minecraft.world.gen.tasks.FinializeChunkTask;
import net.minecraft.world.gen.tasks.LightChunkTask;
import net.minecraft.world.gen.tasks.LiquidCarveChunkTask;
import net.minecraft.world.gen.tasks.SpawnMobsTask;

public enum ChunkStatus implements ITaskType<ChunkPos, ChunkStatus> {
   EMPTY("empty", (ChunkTask)null, -1, false, ChunkStatus.Type.PROTOCHUNK),
   BASE("base", new BaseChunkTask(), 0, false, ChunkStatus.Type.PROTOCHUNK),
   CARVED("carved", new CarveChunkTask(), 0, false, ChunkStatus.Type.PROTOCHUNK),
   LIQUID_CARVED("liquid_carved", new LiquidCarveChunkTask(), 1, false, ChunkStatus.Type.PROTOCHUNK),
   DECORATED("decorated", new DecorateChunkTask(), 1, true, ChunkStatus.Type.PROTOCHUNK) {
      public void acceptInRange(ChunkPos pos, BiConsumer<ChunkPos, ChunkStatus> consumer) {
         int i = pos.x;
         int j = pos.z;
         ChunkStatus chunkstatus = this.getPreviousTaskType();
         int k = 8;

         for(int l = i - 8; l <= i + 8; ++l) {
            if (l < i - 1 || l > i + 1) {
               for(int i1 = j - 8; i1 <= j + 8; ++i1) {
                  if (i1 < j - 1 || i1 > j + 1) {
                     ChunkPos chunkpos = new ChunkPos(l, i1);
                     consumer.accept(chunkpos, EMPTY);
                  }
               }
            }
         }

         for(int j1 = i - 1; j1 <= i + 1; ++j1) {
            for(int k1 = j - 1; k1 <= j + 1; ++k1) {
               ChunkPos chunkpos1 = new ChunkPos(j1, k1);
               consumer.accept(chunkpos1, chunkstatus);
            }
         }

      }
   },
   LIGHTED("lighted", new LightChunkTask(), 1, true, ChunkStatus.Type.PROTOCHUNK),
   MOBS_SPAWNED("mobs_spawned", new SpawnMobsTask(), 0, true, ChunkStatus.Type.PROTOCHUNK),
   FINALIZED("finalized", new FinializeChunkTask(), 0, true, ChunkStatus.Type.PROTOCHUNK),
   FULLCHUNK("fullchunk", new DummyChunkTask(), 0, true, ChunkStatus.Type.LEVELCHUNK),
   POSTPROCESSED("postprocessed", new DummyChunkTask(), 0, true, ChunkStatus.Type.LEVELCHUNK);

   private static final Map<String, ChunkStatus> NAME_TO_STATUS = Util.make(Maps.newHashMap(), (p_209334_0_) -> {
      for(ChunkStatus chunkstatus : values()) {
         p_209334_0_.put(chunkstatus.getName(), chunkstatus);
      }

   });
   private final String name;
   @Nullable
   private final ChunkTask task;
   /**
    * Distance in chunks between the edge of the center chunk and the edge of the chunk region needed for the task. The
    * task will only affect the center chunk, only reading from the chunks in the margin.
    */
   private final int taskRange;
   private final ChunkStatus.Type type;
   private final boolean updateHeightmaps;

   private ChunkStatus(String nameIn, @Nullable ChunkTask taskIn, int taskRangeIn, boolean p_i49319_6_, ChunkStatus.Type typeIn) {
      this.name = nameIn;
      this.task = taskIn;
      this.taskRange = taskRangeIn;
      this.type = typeIn;
      this.updateHeightmaps = p_i49319_6_;
   }

   public String getName() {
      return this.name;
   }

   public ChunkPrimer runTask(World worldIn, IChunkGenerator<?> chunkGenerator, Map<ChunkPos, ChunkPrimer> region, int x, int z) {
      return this.task.run(this, worldIn, chunkGenerator, region, x, z);
   }

   public void acceptInRange(ChunkPos pos, BiConsumer<ChunkPos, ChunkStatus> consumer) {
      int i = pos.x;
      int j = pos.z;
      ChunkStatus chunkstatus = this.getPreviousTaskType();

      for(int k = i - this.taskRange; k <= i + this.taskRange; ++k) {
         for(int l = j - this.taskRange; l <= j + this.taskRange; ++l) {
            consumer.accept(new ChunkPos(k, l), chunkstatus);
         }
      }

   }

   /**
    * Distance in chunks between the edge of the center chunk and the edge of the chunk region needed for the task. The
    * task will only affect the center chunk, only reading from the chunks in the margin.
    */
   public int getTaskRange() {
      return this.taskRange;
   }

   public ChunkStatus.Type getType() {
      return this.type;
   }

   @Nullable
   public static ChunkStatus getByName(String nameIn) {
      return NAME_TO_STATUS.get(nameIn);
   }

   @Nullable
   public ChunkStatus getPreviousTaskType() {
      return this.ordinal() == 0 ? null : values()[this.ordinal() - 1];
   }

   public boolean shouldUpdateHeightmaps() {
      return this.updateHeightmaps;
   }

   public boolean isAtLeast(ChunkStatus status) {
      return this.ordinal() >= status.ordinal();
   }

   public static enum Type {
      PROTOCHUNK,
      LEVELCHUNK;
   }
}