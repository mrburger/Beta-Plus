package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.SessionLockException;

public class SaveAllCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.save.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("save-all").requires((p_198615_0_) -> {
         return p_198615_0_.hasPermissionLevel(4);
      }).executes((p_198610_0_) -> {
         return saveAll(p_198610_0_.getSource(), false);
      }).then(Commands.literal("flush").executes((p_198613_0_) -> {
         return saveAll(p_198613_0_.getSource(), true);
      })));
   }

   private static int saveAll(CommandSource source, boolean flush) throws CommandSyntaxException {
      source.sendFeedback(new TextComponentTranslation("commands.save.saving"), false);
      MinecraftServer minecraftserver = source.getServer();
      boolean flag = false;
      minecraftserver.getPlayerList().saveAllPlayerData();

      for(WorldServer worldserver : minecraftserver.func_212370_w()) {
         if (worldserver != null && saveWorld(worldserver, flush)) {
            flag = true;
         }
      }

      if (!flag) {
         throw FAILED_EXCEPTION.create();
      } else {
         source.sendFeedback(new TextComponentTranslation("commands.save.success"), true);
         return 1;
      }
   }

   private static boolean saveWorld(WorldServer worldIn, boolean flush) {
      boolean flag = worldIn.disableLevelSaving;
      worldIn.disableLevelSaving = false;

      boolean flag1;
      try {
         worldIn.saveAllChunks(true, (IProgressUpdate)null);
         if (flush) {
            worldIn.flushToDisk();
         }

         return true;
      } catch (SessionLockException var8) {
         flag1 = false;
      } finally {
         worldIn.disableLevelSaving = flag;
      }

      return flag1;
   }
}