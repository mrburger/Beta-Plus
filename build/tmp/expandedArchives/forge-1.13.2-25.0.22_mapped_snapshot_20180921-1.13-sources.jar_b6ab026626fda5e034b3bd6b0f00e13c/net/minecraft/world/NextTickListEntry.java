package net.minecraft.world;

import net.minecraft.util.math.BlockPos;

public class NextTickListEntry<T> implements Comparable<NextTickListEntry<T>> {
   /** The id number for the next tick entry */
   private static long nextTickEntryID;
   private final T target;
   public final BlockPos position;
   /** Time this tick is scheduled to occur at */
   public final long scheduledTime;
   public final TickPriority priority;
   /** The id of the tick entry */
   private final long tickEntryID;

   public NextTickListEntry(BlockPos positionIn, T p_i48977_2_) {
      this(positionIn, p_i48977_2_, 0L, TickPriority.NORMAL);
   }

   public NextTickListEntry(BlockPos positionIn, T p_i48978_2_, long scheduledTimeIn, TickPriority priorityIn) {
      this.tickEntryID = (long)(nextTickEntryID++);
      this.position = positionIn.toImmutable();
      this.target = p_i48978_2_;
      this.scheduledTime = scheduledTimeIn;
      this.priority = priorityIn;
   }

   public boolean equals(Object p_equals_1_) {
      if (!(p_equals_1_ instanceof NextTickListEntry)) {
         return false;
      } else {
         NextTickListEntry nextticklistentry = (NextTickListEntry)p_equals_1_;
         return this.position.equals(nextticklistentry.position) && this.target == nextticklistentry.target;
      }
   }

   public int hashCode() {
      return this.position.hashCode();
   }

   public int compareTo(NextTickListEntry p_compareTo_1_) {
      if (this.scheduledTime < p_compareTo_1_.scheduledTime) {
         return -1;
      } else if (this.scheduledTime > p_compareTo_1_.scheduledTime) {
         return 1;
      } else if (this.priority.ordinal() < p_compareTo_1_.priority.ordinal()) {
         return -1;
      } else if (this.priority.ordinal() > p_compareTo_1_.priority.ordinal()) {
         return 1;
      } else if (this.tickEntryID < p_compareTo_1_.tickEntryID) {
         return -1;
      } else {
         return this.tickEntryID > p_compareTo_1_.tickEntryID ? 1 : 0;
      }
   }

   public String toString() {
      return this.target + ": " + this.position + ", " + this.scheduledTime + ", " + this.priority + ", " + this.tickEntryID;
   }

   public T getTarget() {
      return this.target;
   }
}