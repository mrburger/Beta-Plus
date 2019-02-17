package net.minecraft.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NamedThreadFactory implements ThreadFactory {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ThreadGroup group;
   private final AtomicInteger currentThreadOrdinal = new AtomicInteger(1);
   private final String namePrefix;

   public NamedThreadFactory(String namePrefixIn) {
      SecurityManager securitymanager = System.getSecurityManager();
      this.group = securitymanager != null ? securitymanager.getThreadGroup() : Thread.currentThread().getThreadGroup();
      this.namePrefix = namePrefixIn + "-";
   }

   public Thread newThread(Runnable p_newThread_1_) {
      Thread thread = new Thread(this.group, p_newThread_1_, this.namePrefix + this.currentThreadOrdinal.getAndIncrement(), 0L);
      thread.setUncaughtExceptionHandler((p_202907_1_, p_202907_2_) -> {
         LOGGER.error("Caught exception in thread {} from {}", p_202907_1_, p_newThread_1_);
         LOGGER.error("", p_202907_2_);
      });
      if (thread.getPriority() != 5) {
         thread.setPriority(5);
      }

      return thread;
   }
}