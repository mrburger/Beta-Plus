package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.chunk.Chunk;

public class ServerTickList<T> implements ITickList<T> {
   protected final Predicate<T> filter;
   protected final Function<T, ResourceLocation> serializer;
   protected final Function<ResourceLocation, T> deserializer;
   protected final Set<NextTickListEntry<T>> pendingTickListEntriesHashSet = Sets.newHashSet();
   protected final TreeSet<NextTickListEntry<T>> pendingTickListEntriesTreeSet = new TreeSet<>();
   private final WorldServer world;
   private final List<NextTickListEntry<T>> pendingTickListEntriesThisTick = Lists.newArrayList();
   private final Consumer<NextTickListEntry<T>> tickFunction;

   public ServerTickList(WorldServer worldIn, Predicate<T> filter, Function<T, ResourceLocation> serializerIn, Function<ResourceLocation, T> deserializerIn, Consumer<NextTickListEntry<T>> tickFunctionIn) {
      this.filter = filter;
      this.serializer = serializerIn;
      this.deserializer = deserializerIn;
      this.world = worldIn;
      this.tickFunction = tickFunctionIn;
   }

   public void tick() {
      int i = this.pendingTickListEntriesTreeSet.size();
      if (i != this.pendingTickListEntriesHashSet.size()) {
         throw new IllegalStateException("TickNextTick list out of synch");
      } else {
         if (i > 65536) {
            i = 65536;
         }

         this.world.profiler.startSection("cleaning");

         for(int j = 0; j < i; ++j) {
            NextTickListEntry<T> nextticklistentry = this.pendingTickListEntriesTreeSet.first();
            if (nextticklistentry.scheduledTime > this.world.getGameTime()) {
               break;
            }

            this.pendingTickListEntriesTreeSet.remove(nextticklistentry);
            this.pendingTickListEntriesHashSet.remove(nextticklistentry);
            this.pendingTickListEntriesThisTick.add(nextticklistentry);
         }

         this.world.profiler.endSection();
         this.world.profiler.startSection("ticking");
         Iterator<NextTickListEntry<T>> iterator = this.pendingTickListEntriesThisTick.iterator();

         while(iterator.hasNext()) {
            NextTickListEntry<T> nextticklistentry1 = iterator.next();
            iterator.remove();
            int k = 0;
            if (this.world.isAreaLoaded(nextticklistentry1.position.add(0, 0, 0), nextticklistentry1.position.add(0, 0, 0))) {
               try {
                  this.tickFunction.accept(nextticklistentry1);
               } catch (Throwable throwable) {
                  CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while ticking");
                  CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being ticked");
                  CrashReportCategory.addBlockInfo(crashreportcategory, nextticklistentry1.position, (IBlockState)null);
                  throw new ReportedException(crashreport);
               }
            } else {
               this.scheduleTick(nextticklistentry1.position, nextticklistentry1.getTarget(), 0);
            }
         }

         this.world.profiler.endSection();
         this.pendingTickListEntriesThisTick.clear();
      }
   }

   /**
    * Checks if this position/item is scheduled to be updated this tick
    */
   public boolean isTickPending(BlockPos pos, T obj) {
      return this.pendingTickListEntriesThisTick.contains(new NextTickListEntry(pos, obj));
   }

   public List<NextTickListEntry<T>> getPending(Chunk p_205364_1_, boolean remove) {
      ChunkPos chunkpos = p_205364_1_.getPos();
      int i = (chunkpos.x << 4) - 2;
      int j = i + 16 + 2;
      int k = (chunkpos.z << 4) - 2;
      int l = k + 16 + 2;
      return this.getPending(new MutableBoundingBox(i, 0, k, j, 256, l), remove);
   }

   public List<NextTickListEntry<T>> getPending(MutableBoundingBox p_205366_1_, boolean remove) {
      List<NextTickListEntry<T>> list = null;

      for(int i = 0; i < 2; ++i) {
         Iterator<NextTickListEntry<T>> iterator;
         if (i == 0) {
            iterator = this.pendingTickListEntriesTreeSet.iterator();
         } else {
            iterator = this.pendingTickListEntriesThisTick.iterator();
         }

         while(iterator.hasNext()) {
            NextTickListEntry<T> nextticklistentry = iterator.next();
            BlockPos blockpos = nextticklistentry.position;
            if (blockpos.getX() >= p_205366_1_.minX && blockpos.getX() < p_205366_1_.maxX && blockpos.getZ() >= p_205366_1_.minZ && blockpos.getZ() < p_205366_1_.maxZ) {
               if (remove) {
                  if (i == 0) {
                     this.pendingTickListEntriesHashSet.remove(nextticklistentry);
                  }

                  iterator.remove();
               }

               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(nextticklistentry);
            }
         }
      }

      return list == null ? Collections.emptyList() : list;
   }

   public void copyTicks(MutableBoundingBox area, BlockPos offset) {
      for(NextTickListEntry<T> nextticklistentry : this.getPending(area, false)) {
         if (area.isVecInside(nextticklistentry.position)) {
            BlockPos blockpos = nextticklistentry.position.add(offset);
            this.scheduleUpdateNoLoadedCheck(blockpos, nextticklistentry.getTarget(), (int)(nextticklistentry.scheduledTime - this.world.getWorldInfo().getGameTime()), nextticklistentry.priority);
         }
      }

   }

   public NBTTagList write(Chunk p_205363_1_) {
      List<NextTickListEntry<T>> list = this.getPending(p_205363_1_, false);
      long i = this.world.getGameTime();
      NBTTagList nbttaglist = new NBTTagList();

      for(NextTickListEntry<T> nextticklistentry : list) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setString("i", this.serializer.apply(nextticklistentry.getTarget()).toString());
         nbttagcompound.setInt("x", nextticklistentry.position.getX());
         nbttagcompound.setInt("y", nextticklistentry.position.getY());
         nbttagcompound.setInt("z", nextticklistentry.position.getZ());
         nbttagcompound.setInt("t", (int)(nextticklistentry.scheduledTime - i));
         nbttagcompound.setInt("p", nextticklistentry.priority.getPriority());
         nbttaglist.add((INBTBase)nbttagcompound);
      }

      return nbttaglist;
   }

   public void read(NBTTagList p_205369_1_) {
      for(int i = 0; i < p_205369_1_.size(); ++i) {
         NBTTagCompound nbttagcompound = p_205369_1_.getCompound(i);
         T t = this.deserializer.apply(new ResourceLocation(nbttagcompound.getString("i")));
         if (t != null) {
            this.scheduleUpdateNoLoadedCheck(new BlockPos(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z")), t, nbttagcompound.getInt("t"), TickPriority.getPriority(nbttagcompound.getInt("p")));
         }
      }

   }

   public boolean isTickScheduled(BlockPos pos, T itemIn) {
      return this.pendingTickListEntriesHashSet.contains(new NextTickListEntry(pos, itemIn));
   }

   public void scheduleTick(BlockPos pos, T itemIn, int scheduledTime, TickPriority priority) {
      if (!this.filter.test(itemIn)) {
         if (this.world.isBlockLoaded(pos)) {
            this.addEntry(pos, itemIn, scheduledTime, priority);
         }

      }
   }

   /**
    * Schedules an update regardless of if the given position is loaded or not
    */
   protected void scheduleUpdateNoLoadedCheck(BlockPos pos, T p_205367_2_, int p_205367_3_, TickPriority priority) {
      if (!this.filter.test(p_205367_2_)) {
         this.addEntry(pos, p_205367_2_, p_205367_3_, priority);
      }

   }

   private void addEntry(BlockPos p_205370_1_, T p_205370_2_, int p_205370_3_, TickPriority p_205370_4_) {
      NextTickListEntry<T> nextticklistentry = new NextTickListEntry<>(p_205370_1_, p_205370_2_, (long)p_205370_3_ + this.world.getGameTime(), p_205370_4_);
      if (!this.pendingTickListEntriesHashSet.contains(nextticklistentry)) {
         this.pendingTickListEntriesHashSet.add(nextticklistentry);
         this.pendingTickListEntriesTreeSet.add(nextticklistentry);
      }

   }
}