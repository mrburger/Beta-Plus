package net.minecraft.data;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixesManager;

public class CommandsReport implements IDataProvider {
   private final DataGenerator generator;

   public CommandsReport(DataGenerator generatorIn) {
      this.generator = generatorIn;
   }

   /**
    * Performs this provider's action.
    */
   public void act(DirectoryCache cache) throws IOException {
      YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
      MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
      GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
      File file1 = new File(this.generator.getOutputFolder().toFile(), "tmp");
      PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(file1, MinecraftServer.USER_CACHE_FILE.getName()));
      MinecraftServer minecraftserver = new DedicatedServer(file1, DataFixesManager.getDataFixer(), yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, playerprofilecache);
      minecraftserver.getCommandManager().writeCommandTreeAsGson(this.generator.getOutputFolder().resolve("reports/commands.json").toFile());
   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public String getName() {
      return "Command Syntax";
   }
}