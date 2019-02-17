package net.minecraft.world;

import net.minecraft.util.math.BlockPos;

public interface ITickList<T> {
   boolean isTickScheduled(BlockPos pos, T itemIn);

   default void scheduleTick(BlockPos pos, T itemIn, int scheduledTime) {
      this.scheduleTick(pos, itemIn, scheduledTime, TickPriority.NORMAL);
   }

   void scheduleTick(BlockPos pos, T itemIn, int scheduledTime, TickPriority priority);

   /**
    * Checks if this position/item is scheduled to be updated this tick
    */
   boolean isTickPending(BlockPos pos, T obj);
}