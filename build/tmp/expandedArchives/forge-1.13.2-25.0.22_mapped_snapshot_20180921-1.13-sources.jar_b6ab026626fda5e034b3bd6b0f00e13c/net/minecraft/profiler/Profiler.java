package net.minecraft.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler {
   private static final Logger LOGGER = LogManager.getLogger();
   /** List of parent sections */
   private final List<String> sectionList = Lists.newArrayList();
   /** List of timestamps (System.nanoTime) */
   private final List<Long> timestampList = Lists.newArrayList();
   /** Flag profiling enabled */
   private boolean profilingEnabled;
   /** Current profiling section */
   private String profilingSection = "";
   /** Profiling map */
   private final Map<String, Long> profilingMap = Maps.newHashMap();
   private long startTime;
   private int startTick;

   public boolean isProfiling() {
      return this.profilingEnabled;
   }

   public void stopProfiling() {
      this.profilingEnabled = false;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public int getStartTick() {
      return this.startTick;
   }

   public void startProfiling(int startTickIn) {
      if (!this.profilingEnabled) {
         this.profilingEnabled = true;
         this.profilingMap.clear();
         this.profilingSection = "";
         this.sectionList.clear();
         this.startTick = startTickIn;
         this.startTime = Util.nanoTime();
      }
   }

   /**
    * Start section
    */
   public void startSection(String name) {
      if (this.profilingEnabled) {
         if (!this.profilingSection.isEmpty()) {
            this.profilingSection = this.profilingSection + ".";
         }

         this.profilingSection = this.profilingSection + name;
         this.sectionList.add(this.profilingSection);
         this.timestampList.add(Util.nanoTime());
      }
   }

   public void startSection(Supplier<String> nameSupplier) {
      if (this.profilingEnabled) {
         this.startSection(nameSupplier.get());
      }
   }

   /**
    * End section
    */
   public void endSection() {
      if (this.profilingEnabled && !this.timestampList.isEmpty()) {
         long i = Util.nanoTime();
         long j = this.timestampList.remove(this.timestampList.size() - 1);
         this.sectionList.remove(this.sectionList.size() - 1);
         long k = i - j;
         if (this.profilingMap.containsKey(this.profilingSection)) {
            this.profilingMap.put(this.profilingSection, this.profilingMap.get(this.profilingSection) + k);
         } else {
            this.profilingMap.put(this.profilingSection, k);
         }

         if (k > 100000000L) {
            LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", this.profilingSection, (double)k / 1000000.0D);
         }

         this.profilingSection = this.sectionList.isEmpty() ? "" : this.sectionList.get(this.sectionList.size() - 1);
      }
   }

   /**
    * Gets the current profiling data. WARNING: If profiling is enabled, this must not return an empty list, as
    * otherwise the game will crash when attempting to render the profiler. I.E. don't stub out the profiler code, OK?
    * It's not necessary.
    */
   public List<Profiler.Result> getProfilingData(String profilerName) {
      long i = this.profilingMap.containsKey("root") ? this.profilingMap.get("root") : 0L;
      long j = this.profilingMap.containsKey(profilerName) ? this.profilingMap.get(profilerName) : -1L;
      List<Profiler.Result> list = Lists.newArrayList();
      if (!profilerName.isEmpty()) {
         profilerName = profilerName + ".";
      }

      long k = 0L;

      for(String s : this.profilingMap.keySet()) {
         if (s.length() > profilerName.length() && s.startsWith(profilerName) && s.indexOf(".", profilerName.length() + 1) < 0) {
            k += this.profilingMap.get(s);
         }
      }

      float f = (float)k;
      if (k < j) {
         k = j;
      }

      if (i < k) {
         i = k;
      }

      for(String s1 : this.profilingMap.keySet()) {
         if (s1.length() > profilerName.length() && s1.startsWith(profilerName) && s1.indexOf(".", profilerName.length() + 1) < 0) {
            long l = this.profilingMap.get(s1);
            double d0 = (double)l * 100.0D / (double)k;
            double d1 = (double)l * 100.0D / (double)i;
            String s2 = s1.substring(profilerName.length());
            list.add(new Profiler.Result(s2, d0, d1));
         }
      }

      for(String s3 : this.profilingMap.keySet()) {
         this.profilingMap.put(s3, this.profilingMap.get(s3) * 999L / 1000L);
      }

      if ((float)k > f) {
         list.add(new Profiler.Result("unspecified", (double)((float)k - f) * 100.0D / (double)k, (double)((float)k - f) * 100.0D / (double)i));
      }

      Collections.sort(list);
      list.add(0, new Profiler.Result(profilerName, 100.0D, (double)k * 100.0D / (double)i));
      return list;
   }

   /**
    * End current section and start a new section
    */
   public void endStartSection(String name) {
      this.endSection();
      this.startSection(name);
   }

   @OnlyIn(Dist.CLIENT)
   public void endStartSection(Supplier<String> nameSupplier) {
      this.endSection();
      this.startSection(nameSupplier);
   }

   public String getNameOfLastSection() {
      return this.sectionList.isEmpty() ? "[UNKNOWN]" : this.sectionList.get(this.sectionList.size() - 1);
   }

   public static final class Result implements Comparable<Profiler.Result> {
      public double usePercentage;
      public double totalUsePercentage;
      public String profilerName;

      public Result(String profilerName, double usePercentage, double totalUsePercentage) {
         this.profilerName = profilerName;
         this.usePercentage = usePercentage;
         this.totalUsePercentage = totalUsePercentage;
      }

      public int compareTo(Profiler.Result p_compareTo_1_) {
         if (p_compareTo_1_.usePercentage < this.usePercentage) {
            return -1;
         } else {
            return p_compareTo_1_.usePercentage > this.usePercentage ? 1 : p_compareTo_1_.profilerName.compareTo(this.profilerName);
         }
      }

      /**
       * Return a color to display the profiler, generated from the hash code of the profiler's name
       */
      @OnlyIn(Dist.CLIENT)
      public int getColor() {
         return (this.profilerName.hashCode() & 11184810) + 4473924;
      }
   }
}