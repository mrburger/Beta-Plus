package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugCommand {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleCommandExceptionType NOT_RUNNING_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.debug.notRunning"));
   private static final SimpleCommandExceptionType ALREADY_RUNNING_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.debug.alreadyRunning"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("debug").requires((p_198332_0_) -> {
         return p_198332_0_.hasPermissionLevel(3);
      }).then(Commands.literal("start").executes((p_198329_0_) -> {
         return startDebug(p_198329_0_.getSource());
      })).then(Commands.literal("stop").executes((p_198333_0_) -> {
         return stopDebug(p_198333_0_.getSource());
      })));
   }

   private static int startDebug(CommandSource source) throws CommandSyntaxException {
      MinecraftServer minecraftserver = source.getServer();
      Profiler profiler = minecraftserver.profiler;
      if (profiler.isProfiling()) {
         throw ALREADY_RUNNING_EXCEPTION.create();
      } else {
         minecraftserver.enableProfiling();
         source.sendFeedback(new TextComponentTranslation("commands.debug.started", "Started the debug profiler. Type '/debug stop' to stop it."), true);
         return 0;
      }
   }

   private static int stopDebug(CommandSource source) throws CommandSyntaxException {
      MinecraftServer minecraftserver = source.getServer();
      Profiler profiler = minecraftserver.profiler;
      if (!profiler.isProfiling()) {
         throw NOT_RUNNING_EXCEPTION.create();
      } else {
         long i = Util.nanoTime();
         int j = minecraftserver.getTickCounter();
         long k = i - profiler.getStartTime();
         int l = j - profiler.getStartTick();
         File file1 = new File(minecraftserver.getFile("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
         file1.getParentFile().mkdirs();
         Writer writer = null;

         try {
            writer = new OutputStreamWriter(new FileOutputStream(file1), StandardCharsets.UTF_8);
            writer.write(makeDebugOutput(k, l, profiler));
         } catch (Throwable throwable) {
            LOGGER.error("Could not save profiler results to {}", file1, throwable);
         } finally {
            IOUtils.closeQuietly(writer);
         }

         profiler.stopProfiling();
         float f = (float)k / 1.0E9F;
         float f1 = (float)l / f;
         source.sendFeedback(new TextComponentTranslation("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", f), l, String.format("%.2f", f1)), true);
         return MathHelper.floor(f1);
      }
   }

   private static String makeDebugOutput(long realTimePeriod, int tickTimePeriod, Profiler profilerIn) {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("---- Minecraft Profiler Results ----\n");
      stringbuilder.append("// ");
      stringbuilder.append(getWittyComment());
      stringbuilder.append("\n\n");
      stringbuilder.append("Time span: ").append(realTimePeriod).append(" ms\n");
      stringbuilder.append("Tick span: ").append(tickTimePeriod).append(" ticks\n");
      stringbuilder.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", (float)tickTimePeriod / ((float)realTimePeriod / 1.0E9F))).append(" ticks per second. It should be ").append((int)20).append(" ticks per second\n\n");
      stringbuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
      makeDebugGraphRecursive(0, "root", stringbuilder, profilerIn);
      stringbuilder.append("--- END PROFILE DUMP ---\n\n");
      return stringbuilder.toString();
   }

   private static void makeDebugGraphRecursive(int depth, String nodeName, StringBuilder sb, Profiler profilerIn) {
      List<Profiler.Result> list = profilerIn.getProfilingData(nodeName);
      if (list != null && list.size() >= 3) {
         for(int i = 1; i < list.size(); ++i) {
            Profiler.Result profiler$result = list.get(i);
            sb.append(String.format("[%02d] ", depth));

            for(int j = 0; j < depth; ++j) {
               sb.append("|   ");
            }

            sb.append(profiler$result.profilerName).append(" - ").append(String.format(Locale.ROOT, "%.2f", profiler$result.usePercentage)).append("%/").append(String.format(Locale.ROOT, "%.2f", profiler$result.totalUsePercentage)).append("%\n");
            if (!"unspecified".equals(profiler$result.profilerName)) {
               try {
                  makeDebugGraphRecursive(depth + 1, nodeName + "." + profiler$result.profilerName, sb, profilerIn);
               } catch (Exception exception) {
                  sb.append("[[ EXCEPTION ").append((Object)exception).append(" ]]");
               }
            }
         }

      }
   }

   private static String getWittyComment() {
      String[] astring = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

      try {
         return astring[(int)(Util.nanoTime() % (long)astring.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }
}