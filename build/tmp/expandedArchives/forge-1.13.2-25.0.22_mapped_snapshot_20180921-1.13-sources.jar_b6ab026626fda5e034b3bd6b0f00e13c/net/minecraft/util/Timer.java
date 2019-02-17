package net.minecraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Timer {
   /** How many full ticks have turned over since the last call to updateTimer(), capped at 10. */
   public int elapsedTicks;
   /** How much time has elapsed since the last tick, in ticks, for use by display rendering routines (range: 0.0 - 1.0). */
   public float renderPartialTicks;
   /** How much time has elapsed since the last tick, in ticks (range: 0.0 - 1.0). */
   public float elapsedPartialTicks;
   /** The time reported by the system clock at the last sync, in milliseconds */
   private long lastSyncSysClock;
   /** The Length of a single tick in milliseconds. Calculated as 1000/tps. At a default 20 TPS, tickLength is 50 ms */
   private final float tickLength;

   public Timer(float p_i49528_1_, long p_i49528_2_) {
      this.tickLength = 1000.0F / p_i49528_1_;
      this.lastSyncSysClock = p_i49528_2_;
   }

   /**
    * Updates all fields of the Timer using the current time
    */
   public void updateTimer(long p_74275_1_) {
      this.elapsedPartialTicks = (float)(p_74275_1_ - this.lastSyncSysClock) / this.tickLength;
      this.lastSyncSysClock = p_74275_1_;
      this.renderPartialTicks += this.elapsedPartialTicks;
      this.elapsedTicks = (int)this.renderPartialTicks;
      this.renderPartialTicks -= (float)this.elapsedTicks;
   }
}