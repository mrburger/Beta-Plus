package net.minecraft.server;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.ServerPackFinder;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.server.management.UserListWhitelist;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ITickable;
import net.minecraft.util.Util;
import net.minecraft.util.WorldOptimizer;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.ServerWorldEventHandler;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer implements IThreadListener, ISnooperInfo, ICommandSource, Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final File USER_CACHE_FILE = new File("usercache.json");
   private final ISaveFormat anvilConverterForAnvilFile;
   private final Snooper snooper = new Snooper("server", this, Util.milliTime());
   private final File anvilFile;
   private final List<ITickable> tickables = Lists.newArrayList();
   public final Profiler profiler = new Profiler();
   private final NetworkSystem networkSystem;
   private final ServerStatusResponse statusResponse = new ServerStatusResponse();
   private final Random random = new Random();
   private final DataFixer dataFixer;
   /** The server's hostname. */
   private String hostname;
   /** The server's port. */
   private int serverPort = -1;
   /** The server world instances. */
   private final Map<DimensionType, WorldServer> worlds = Maps.newIdentityHashMap();
   /** The player list for this server */
   private PlayerList playerList;
   /** Indicates whether the server is running or not. Set to false to initiate a shutdown. */
   private boolean serverRunning = true;
   /** Indicates to other classes that the server is safely stopped. */
   private boolean serverStopped;
   /** Incremented every tick. */
   private int tickCounter;
   protected final Proxy serverProxy;
   /** The task the server is currently working on(and will output on outputPercentRemaining). */
   private ITextComponent currentTask;
   /** The percentage of the current task finished so far. */
   private int percentDone;
   /** True if the server is in online mode. */
   private boolean onlineMode;
   private boolean preventProxyConnections;
   /** True if the server has animals turned on. */
   private boolean canSpawnAnimals;
   private boolean canSpawnNPCs;
   /** Indicates whether PvP is active on the server or not. */
   private boolean pvpEnabled;
   /** Determines if flight is allowed or not. */
   private boolean allowFlight;
   /** The server MOTD string. */
   private String motd;
   /** Maximum build height. */
   private int buildLimit;
   private int maxPlayerIdleMinutes;
   public final long[] tickTimeArray = new long[100];
   /** Stats are [dimension][tick%100] system.nanoTime is stored. */
   protected final Map<DimensionType, long[]> timeOfLastDimensionTick = Maps.newIdentityHashMap();
   private KeyPair serverKeyPair;
   /** Username of the server owner (for integrated servers) */
   private String serverOwner;
   private String folderName;
   @OnlyIn(Dist.CLIENT)
   private String worldName;
   private boolean isDemo;
   private boolean enableBonusChest;
   /** The texture pack for the server */
   private String resourcePackUrl = "";
   private String resourcePackHash = "";
   private boolean serverIsRunning;
   /** Set when warned for "Can't keep up", which triggers again after 15 seconds. */
   private long timeOfLastWarning;
   private ITextComponent userMessage;
   private boolean startProfiling;
   private boolean isGamemodeForced;
   private final YggdrasilAuthenticationService authService;
   private final MinecraftSessionService sessionService;
   private final GameProfileRepository profileRepo;
   private final PlayerProfileCache profileCache;
   private long nanoTimeSinceStatusRefresh;
   public final Queue<FutureTask<?>> futureTaskQueue = Queues.newConcurrentLinkedQueue();
   private Thread serverThread;
   protected long serverTime = Util.milliTime();
   @OnlyIn(Dist.CLIENT)
   private boolean worldIconSet;
   private final IReloadableResourceManager resourceManager = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA);
   private final ResourcePackList<ResourcePackInfo> resourcePacks = new ResourcePackList<>(ResourcePackInfo::new);
   private FolderPackFinder datapackFinder;
   private final Commands commandManager;
   private final RecipeManager recipeManager = new RecipeManager();
   private final NetworkTagManager networkTagManager = new NetworkTagManager();
   private final ServerScoreboard scoreboard = new ServerScoreboard(this);
   private final CustomBossEvents customBossEvents = new CustomBossEvents(this);
   private final LootTableManager lootTableManager = new LootTableManager();
   private final AdvancementManager advancementManager = new AdvancementManager();
   private final FunctionManager functionManager = new FunctionManager(this);
   private boolean whitelistEnabled;
   private boolean forceWorldUpgrade;
   private float tickTime;

   public MinecraftServer(@Nullable File anvilFileIn, Proxy serverProxyIn, DataFixer dataFixerIn, Commands commandManagerIn, YggdrasilAuthenticationService authServiceIn, MinecraftSessionService sessionServiceIn, GameProfileRepository profileRepoIn, PlayerProfileCache profileCacheIn) {
      this.serverProxy = serverProxyIn;
      this.commandManager = commandManagerIn;
      this.authService = authServiceIn;
      this.sessionService = sessionServiceIn;
      this.profileRepo = profileRepoIn;
      this.profileCache = profileCacheIn;
      this.anvilFile = anvilFileIn;
      this.networkSystem = anvilFileIn == null ? null : new NetworkSystem(this);
      this.anvilConverterForAnvilFile = anvilFileIn == null ? null : new AnvilSaveConverter(anvilFileIn.toPath(), anvilFileIn.toPath().resolve("../backups"), dataFixerIn);
      this.dataFixer = dataFixerIn;
      this.resourceManager.addReloadListener(this.networkTagManager);
      this.resourceManager.addReloadListener(this.recipeManager);
      this.resourceManager.addReloadListener(this.lootTableManager);
      this.resourceManager.addReloadListener(this.functionManager);
      this.resourceManager.addReloadListener(this.advancementManager);
   }

   /**
    * Initialises the server and starts it.
    */
   public abstract boolean init() throws IOException;

   public void convertMapIfNeeded(String worldNameIn) {
      if (this.getActiveAnvilConverter().isOldMapFormat(worldNameIn)) {
         LOGGER.info("Converting map!");
         this.setUserMessage(new TextComponentTranslation("menu.convertingLevel"));
         this.getActiveAnvilConverter().convertMapFormat(worldNameIn, new IProgressUpdate() {
            private long startTime = Util.milliTime();

            public void displaySavingString(ITextComponent component) {
            }

            @OnlyIn(Dist.CLIENT)
            public void resetProgressAndMessage(ITextComponent component) {
            }

            /**
             * Updates the progress bar on the loading screen to the specified amount.
             */
            public void setLoadingProgress(int progress) {
               if (Util.milliTime() - this.startTime >= 1000L) {
                  this.startTime = Util.milliTime();
                  MinecraftServer.LOGGER.info("Converting... {}%", (int)progress);
               }

            }

            @OnlyIn(Dist.CLIENT)
            public void setDoneWorking() {
            }

            public void displayLoadingString(ITextComponent component) {
            }
         });
      }

      if (this.forceWorldUpgrade) {
         LOGGER.info("Forcing world upgrade!");
         WorldInfo worldinfo = this.getActiveAnvilConverter().getWorldInfo(this.getFolderName());
         if (worldinfo != null) {
            WorldOptimizer worldoptimizer = new WorldOptimizer(this.getFolderName(), this.getActiveAnvilConverter(), worldinfo);
            ITextComponent itextcomponent = null;

            while(!worldoptimizer.isFinished()) {
               ITextComponent itextcomponent1 = worldoptimizer.getStatusText();
               if (itextcomponent != itextcomponent1) {
                  itextcomponent = itextcomponent1;
                  LOGGER.info(worldoptimizer.getStatusText().getString());
               }

               int i = worldoptimizer.getTotalChunks();
               if (i > 0) {
                  int j = worldoptimizer.getConverted() + worldoptimizer.getSkipped();
                  LOGGER.info("{}% completed ({} / {} chunks)...", MathHelper.floor((float)j / (float)i * 100.0F), j, i);
               }

               if (this.isServerStopped()) {
                  worldoptimizer.cancel();
               } else {
                  try {
                     Thread.sleep(1000L);
                  } catch (InterruptedException var8) {
                     ;
                  }
               }
            }
         }
      }

   }

   protected synchronized void setUserMessage(ITextComponent userMessageIn) {
      this.userMessage = userMessageIn;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public synchronized ITextComponent getUserMessage() {
      return this.userMessage;
   }

   public void loadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, JsonElement generatorOptions) {
      this.convertMapIfNeeded(saveName);
      this.setUserMessage(new TextComponentTranslation("menu.loadingLevel"));
      ISaveHandler isavehandler = this.getActiveAnvilConverter().getSaveLoader(saveName, this);
      this.setResourcePackFromWorld(this.getFolderName(), isavehandler);
      WorldInfo worldinfo = isavehandler.loadWorldInfo();
      WorldSettings worldsettings;
      if (worldinfo == null) {
         if (this.isDemo()) {
            worldsettings = WorldServerDemo.DEMO_WORLD_SETTINGS;
         } else {
            worldsettings = new WorldSettings(seed, this.getGameType(), this.canStructuresSpawn(), this.isHardcore(), type);
            worldsettings.setGeneratorOptions(generatorOptions);
            if (this.enableBonusChest) {
               worldsettings.enableBonusChest();
            }
         }

         worldinfo = new WorldInfo(worldsettings, worldNameIn);
      } else {
         worldinfo.setWorldName(worldNameIn);
         worldsettings = new WorldSettings(worldinfo);
      }

      this.loadDataPacks(isavehandler.getWorldDirectory(), worldinfo);
      WorldSavedDataStorage worldsaveddatastorage = new WorldSavedDataStorage(isavehandler);
      this.func_212369_a(isavehandler, worldsaveddatastorage, worldinfo, worldsettings);
      this.setDifficultyForAllWorlds(this.getDifficulty());
      this.initialWorldChunkLoad(worldsaveddatastorage);
   }

   public void func_212369_a(ISaveHandler p_212369_1_, WorldSavedDataStorage p_212369_2_, WorldInfo p_212369_3_, WorldSettings p_212369_4_) {
      if (this.isDemo()) {
         this.worlds.put(DimensionType.OVERWORLD, (new WorldServerDemo(this, p_212369_1_, p_212369_2_, p_212369_3_, DimensionType.OVERWORLD, this.profiler)).func_212251_i__());
      } else {
         this.worlds.put(DimensionType.OVERWORLD, (new WorldServer(this, p_212369_1_, p_212369_2_, p_212369_3_, DimensionType.OVERWORLD, this.profiler)).func_212251_i__());
      }

      WorldServer worldserver = this.getWorld(DimensionType.OVERWORLD);
      worldserver.initialize(p_212369_4_);
      worldserver.addEventListener(new ServerWorldEventHandler(this, worldserver));
      if (!this.isSinglePlayer()) {
         worldserver.getWorldInfo().setGameType(this.getGameType());
      }

      for (DimensionType dim : DimensionType.func_212681_b()) {
         WorldServer world = worldserver;
         if (dim != DimensionType.OVERWORLD) {
            world = (new WorldServerMulti(this, p_212369_1_, dim, worldserver, this.profiler)).func_212251_i__();
            this.worlds.put(dim, world);
            world.addEventListener(new ServerWorldEventHandler(this, world));
            if (!this.isSinglePlayer()) {
               world.getWorldInfo().setGameType(getGameType());
            }
         }
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Load(world));
      }

      this.getPlayerList().func_212504_a(worldserver);
      if (p_212369_3_.getCustomBossEvents() != null) {
         this.getCustomBossEvents().read(p_212369_3_.getCustomBossEvents());
      }

   }

   public void loadDataPacks(File p_195560_1_, WorldInfo p_195560_2_) {
      this.resourcePacks.addPackFinder(new ServerPackFinder());
      this.datapackFinder = new FolderPackFinder(new File(p_195560_1_, "datapacks"));
      this.resourcePacks.addPackFinder(this.datapackFinder);
      this.resourcePacks.reloadPacksFromFinders();
      List<ResourcePackInfo> list = Lists.newArrayList();

      for(String s : p_195560_2_.getEnabledDataPacks()) {
         ResourcePackInfo resourcepackinfo = this.resourcePacks.getPackInfo(s);
         if (resourcepackinfo != null) {
            list.add(resourcepackinfo);
         } else {
            LOGGER.warn("Missing data pack {}", (Object)s);
         }
      }

      this.resourcePacks.func_198985_a(list);
      this.loadDataPacks(p_195560_2_);
   }

   public void initialWorldChunkLoad(WorldSavedDataStorage p_71222_1_) {
      int i = 16;
      int j = 4;
      int k = 12;
      int l = 192;
      int i1 = 625;
      this.setUserMessage(new TextComponentTranslation("menu.generatingTerrain"));
      WorldServer worldserver = this.getWorld(DimensionType.OVERWORLD);
      LOGGER.info("Preparing start region for dimension " + DimensionType.func_212678_a(worldserver.dimension.getType()));
      BlockPos blockpos = worldserver.getSpawnPoint();
      List<ChunkPos> list = Lists.newArrayList();
      Set<ChunkPos> set = Sets.newConcurrentHashSet();
      Stopwatch stopwatch = Stopwatch.createStarted();

      for(int j1 = -192; j1 <= 192 && this.isServerRunning(); j1 += 16) {
         for(int k1 = -192; k1 <= 192 && this.isServerRunning(); k1 += 16) {
            list.add(new ChunkPos(blockpos.getX() + j1 >> 4, blockpos.getZ() + k1 >> 4));
         }

         CompletableFuture<?> completablefuture = worldserver.getChunkProvider().loadChunks(list, (p_201701_1_) -> {
            set.add(p_201701_1_.getPos());
         });

         while(!completablefuture.isDone()) {
            try {
               completablefuture.get(1L, TimeUnit.SECONDS);
            } catch (InterruptedException interruptedexception) {
               throw new RuntimeException(interruptedexception);
            } catch (ExecutionException executionexception) {
               if (executionexception.getCause() instanceof RuntimeException) {
                  throw (RuntimeException)executionexception.getCause();
               }

               throw new RuntimeException(executionexception.getCause());
            } catch (TimeoutException var22) {
               this.setCurrentTaskAndPercentDone(new TextComponentTranslation("menu.preparingSpawn"), set.size() * 100 / 625);
            }
         }

         this.setCurrentTaskAndPercentDone(new TextComponentTranslation("menu.preparingSpawn"), set.size() * 100 / 625);
      }

      LOGGER.info("Time elapsed: {} ms", (long)stopwatch.elapsed(TimeUnit.MILLISECONDS));

      for(DimensionType dimensiontype : DimensionType.func_212681_b()) {
         ForcedChunksSaveData forcedchunkssavedata = p_71222_1_.func_212426_a(dimensiontype, ForcedChunksSaveData::new, "chunks");
         if (forcedchunkssavedata != null) {
            WorldServer worldserver1 = this.getWorld(dimensiontype);
            LongIterator longiterator = forcedchunkssavedata.func_212438_a().iterator();

            while(longiterator.hasNext()) {
               this.setCurrentTaskAndPercentDone(new TextComponentTranslation("menu.loadingForcedChunks", dimensiontype), forcedchunkssavedata.func_212438_a().size() * 100 / 625);
               long l1 = longiterator.nextLong();
               ChunkPos chunkpos = new ChunkPos(l1);
               worldserver1.getChunkProvider().provideChunk(chunkpos.x, chunkpos.z, true, true);
            }
         }
      }

      this.clearCurrentTask();
   }

   public void setResourcePackFromWorld(String worldNameIn, ISaveHandler saveHandlerIn) {
      File file1 = new File(saveHandlerIn.getWorldDirectory(), "resources.zip");
      if (file1.isFile()) {
         try {
            this.setResourcePack("level://" + URLEncoder.encode(worldNameIn, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
         } catch (UnsupportedEncodingException var5) {
            LOGGER.warn("Something went wrong url encoding {}", (Object)worldNameIn);
         }
      }

   }

   public abstract boolean canStructuresSpawn();

   public abstract GameType getGameType();

   /**
    * Get the server's difficulty
    */
   public abstract EnumDifficulty getDifficulty();

   /**
    * Defaults to false.
    */
   public abstract boolean isHardcore();

   public abstract int getOpPermissionLevel();

   public abstract boolean allowLoggingRcon();

   protected void setCurrentTaskAndPercentDone(ITextComponent currentTaskIn, int percentDoneIn) {
      this.currentTask = currentTaskIn;
      this.percentDone = percentDoneIn;
      LOGGER.info("{}: {}%", currentTaskIn.getString(), percentDoneIn);
   }

   /**
    * Set current task to null and set its percentage to 0.
    */
   protected void clearCurrentTask() {
      this.currentTask = null;
      this.percentDone = 0;
   }

   /**
    * par1 indicates if a log message should be output.
    */
   public void saveAllWorlds(boolean isSilent) {
      for(WorldServer worldserver : this.func_212370_w()) {
         if (worldserver != null) {
            if (!isSilent) {
               LOGGER.info("Saving chunks for level '{}'/{}", worldserver.getWorldInfo().getWorldName(), DimensionType.func_212678_a(worldserver.dimension.getType()));
            }

            try {
               worldserver.saveAllChunks(true, (IProgressUpdate)null);
            } catch (SessionLockException sessionlockexception) {
               LOGGER.warn(sessionlockexception.getMessage());
            }
         }
      }

   }

   /**
    * Saves all necessary data as preparation for stopping the server.
    */
   public void stopServer() {
      LOGGER.info("Stopping server");
      if (this.getNetworkSystem() != null) {
         this.getNetworkSystem().terminateEndpoints();
      }

      if (this.playerList != null) {
         LOGGER.info("Saving players");
         this.playerList.saveAllPlayerData();
         this.playerList.removeAllPlayers();
      }

      LOGGER.info("Saving worlds");

      for(WorldServer worldserver : this.func_212370_w()) {
         if (worldserver != null) {
            worldserver.disableLevelSaving = false;
         }
      }

      this.saveAllWorlds(false);

      for(WorldServer worldserver1 : this.func_212370_w()) {
         if (worldserver1 != null) {
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(worldserver1));
            worldserver1.close();
         }
      }

      if (this.snooper.isSnooperRunning()) {
         this.snooper.stop();
      }

   }

   /**
    * "getHostname" is already taken, but both return the hostname.
    */
   public String getServerHostname() {
      return this.hostname;
   }

   public void setHostname(String host) {
      this.hostname = host;
   }

   public boolean isServerRunning() {
      return this.serverRunning;
   }

   /**
    * Sets the serverRunning variable to false, in order to get the server to shut down.
    */
   public void initiateShutdown() {
      this.serverRunning = false;
   }

   private boolean func_212379_aT() {
      return Util.milliTime() < this.serverTime;
   }

   public void run() {
      try {
         if (this.init()) {
            net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerStarted(this);
            this.serverTime = Util.milliTime();
            this.statusResponse.setServerDescription(new TextComponentString(this.motd));
            this.statusResponse.setVersion(new ServerStatusResponse.Version("1.13.2", 404));
            this.applyServerIconToResponse(this.statusResponse);

            while(this.serverRunning) {
               long i = Util.milliTime() - this.serverTime;
               if (i > 2000L && this.serverTime - this.timeOfLastWarning >= 15000L) {
                  long j = i / 50L;
                  LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", i, j);
                  this.serverTime += j * 50L;
                  this.timeOfLastWarning = this.serverTime;
               }

               this.tick(this::func_212379_aT);
               this.serverTime += 50L;

               while(this.func_212379_aT()) {
                  Thread.sleep(1L);
               }

               this.serverIsRunning = true;
            }
            net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerStopping(this);
            net.minecraftforge.fml.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
         } else {
            net.minecraftforge.fml.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
            this.finalTick((CrashReport)null);
         }
      } catch (Throwable throwable1) {
         LOGGER.error("Encountered an unexpected exception", throwable1);
         CrashReport crashreport;
         if (throwable1 instanceof ReportedException) {
            crashreport = this.addServerInfoToCrashReport(((ReportedException)throwable1).getCrashReport());
         } else {
            crashreport = this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", throwable1));
         }

         File file1 = new File(new File(this.getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
         if (crashreport.saveToFile(file1)) {
            LOGGER.error("This crash report has been saved to: {}", (Object)file1.getAbsolutePath());
         } else {
            LOGGER.error("We were unable to save this crash report to disk.");
         }

         net.minecraftforge.fml.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
         this.finalTick(crashreport);
      } finally {
         try {
            this.serverStopped = true;
            this.stopServer();
         } catch (Throwable throwable) {
            LOGGER.error("Exception stopping the server", throwable);
         } finally {
            net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerStopped(this);
            this.systemExitNow();
         }

      }

   }

   public void applyServerIconToResponse(ServerStatusResponse response) {
      File file1 = this.getFile("server-icon.png");
      if (!file1.exists()) {
         file1 = this.getActiveAnvilConverter().getFile(this.getFolderName(), "icon.png");
      }

      if (file1.isFile()) {
         ByteBuf bytebuf = Unpooled.buffer();

         try {
            BufferedImage bufferedimage = ImageIO.read(file1);
            Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide");
            Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high");
            ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
            ByteBuffer bytebuffer = Base64.getEncoder().encode(bytebuf.nioBuffer());
            response.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(bytebuffer));
         } catch (Exception exception) {
            LOGGER.error("Couldn't load server icon", (Throwable)exception);
         } finally {
            bytebuf.release();
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean isWorldIconSet() {
      this.worldIconSet = this.worldIconSet || this.getWorldIconFile().isFile();
      return this.worldIconSet;
   }

   @OnlyIn(Dist.CLIENT)
   public File getWorldIconFile() {
      return this.getActiveAnvilConverter().getFile(this.getFolderName(), "icon.png");
   }

   public File getDataDirectory() {
      return new File(".");
   }

   /**
    * Called on exit from the main run() loop.
    */
   public void finalTick(CrashReport report) {
   }

   /**
    * Directly calls System.exit(0), instantly killing the program.
    */
   public void systemExitNow() {
   }

   /**
    * Main function called by run() every loop.
    */
   public void tick(BooleanSupplier p_71217_1_) {
      long i = Util.nanoTime();
      net.minecraftforge.fml.hooks.BasicEventHooks.onPreServerTick();
      ++this.tickCounter;
      if (this.startProfiling) {
         this.startProfiling = false;
         this.profiler.startProfiling(this.tickCounter);
      }

      this.profiler.startSection("root");
      this.updateTimeLightAndEntities(p_71217_1_);
      if (i - this.nanoTimeSinceStatusRefresh >= 5000000000L) {
         this.nanoTimeSinceStatusRefresh = i;
         this.statusResponse.setPlayers(new ServerStatusResponse.Players(this.getMaxPlayers(), this.getCurrentPlayerCount()));
         GameProfile[] agameprofile = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
         int j = MathHelper.nextInt(this.random, 0, this.getCurrentPlayerCount() - agameprofile.length);

         for(int k = 0; k < agameprofile.length; ++k) {
            agameprofile[k] = this.playerList.getPlayers().get(j + k).getGameProfile();
         }

         Collections.shuffle(Arrays.asList(agameprofile));
         this.statusResponse.getPlayers().setPlayers(agameprofile);
         this.statusResponse.invalidateJson();
      }

      if (this.tickCounter % 900 == 0) {
         this.profiler.startSection("save");
         this.playerList.saveAllPlayerData();
         this.saveAllWorlds(true);
         this.profiler.endSection();
      }

      this.profiler.startSection("snooper");
      if (!this.snooper.isSnooperRunning() && this.tickCounter > 100) {
         this.snooper.start();
      }

      if (this.tickCounter % 6000 == 0) {
         this.snooper.addMemoryStatsToSnooper();
      }

      this.profiler.endSection();
      this.profiler.startSection("tallying");
      long l = this.tickTimeArray[this.tickCounter % 100] = Util.nanoTime() - i;
      this.tickTime = this.tickTime * 0.8F + (float)l / 1000000.0F * 0.19999999F;
      this.profiler.endSection();
      this.profiler.endSection();
      net.minecraftforge.fml.hooks.BasicEventHooks.onPostServerTick();
   }

   public void updateTimeLightAndEntities(BooleanSupplier p_71190_1_) {
      this.profiler.startSection("jobs");

      FutureTask<?> futuretask;
      while((futuretask = this.futureTaskQueue.poll()) != null) {
         Util.runTask(futuretask, LOGGER);
      }

      this.profiler.endStartSection("commandFunctions");
      this.getFunctionManager().tick();
      this.profiler.endStartSection("levels");

      for(WorldServer worldserver : this.func_212370_w()) {
         long i = Util.nanoTime();
         if (worldserver.dimension.getType() == DimensionType.OVERWORLD || this.getAllowNether()) {
            this.profiler.startSection(() -> {
               return "dim-" + worldserver.dimension.getType().getId();
            });
            if (this.tickCounter % 20 == 0) {
               this.profiler.startSection("timeSync");
               this.playerList.sendPacketToAllPlayersInDimension(new SPacketTimeUpdate(worldserver.getGameTime(), worldserver.getDayTime(), worldserver.getGameRules().getBoolean("doDaylightCycle")), worldserver.dimension.getType());
               this.profiler.endSection();
            }

            this.profiler.startSection("tick");
            net.minecraftforge.fml.hooks.BasicEventHooks.onPreWorldTick(worldserver);

            try {
               worldserver.tick(p_71190_1_);
            } catch (Throwable throwable1) {
               CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Exception ticking world");
               worldserver.fillCrashReport(crashreport);
               throw new ReportedException(crashreport);
            }

            try {
               worldserver.tickEntities();
            } catch (Throwable throwable) {
               CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Exception ticking world entities");
               worldserver.fillCrashReport(crashreport1);
               throw new ReportedException(crashreport1);
            }

            net.minecraftforge.fml.hooks.BasicEventHooks.onPostWorldTick(worldserver);
            this.profiler.endSection();
            this.profiler.startSection("tracker");
            worldserver.getEntityTracker().tick();
            this.profiler.endSection();
            this.profiler.endSection();
         }

         (this.timeOfLastDimensionTick.computeIfAbsent(worldserver.dimension.getType(), (p_212377_0_) -> {
            return new long[100];
         }))[this.tickCounter % 100] = Util.nanoTime() - i;
      }

      this.profiler.endStartSection("dim_unloading");
      net.minecraftforge.common.DimensionManager.unloadWorlds(this, this.tickCounter % 200 == 0);
      this.profiler.endStartSection("connection");
      this.getNetworkSystem().tick();
      this.profiler.endStartSection("players");
      this.playerList.tick();
      this.profiler.endStartSection("tickables");

      for(int j = 0; j < this.tickables.size(); ++j) {
         this.tickables.get(j).tick();
      }

      this.profiler.endSection();
   }

   public boolean getAllowNether() {
      return true;
   }

   public void registerTickable(ITickable tickable) {
      this.tickables.add(tickable);
   }

   public static void main(String[] p_main_0_) {
      //Forge: Copied from DedicatedServer.init as to run as early as possible, Old code left in place intentionally.
      //Done in good faith with permission: https://github.com/MinecraftForge/MinecraftForge/issues/3659#issuecomment-390467028
      ServerEula eula = new ServerEula(new File("eula.txt"));
      if (!eula.hasAcceptedEULA()) {
          LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
          eula.createEULAFile();
          return;
      }
      Bootstrap.register();

      try {
         boolean flag = true;
         String s = null;
         String s1 = ".";
         String s2 = null;
         boolean flag1 = false;
         boolean flag2 = false;
         boolean flag3 = false;
         int i = -1;

         for(int j = 0; j < p_main_0_.length; ++j) {
            String s3 = p_main_0_[j];
            String s4 = j == p_main_0_.length - 1 ? null : p_main_0_[j + 1];
            boolean flag4 = false;
            if (!"nogui".equals(s3) && !"--nogui".equals(s3)) {
               if ("--port".equals(s3) && s4 != null) {
                  flag4 = true;

                  try {
                     i = Integer.parseInt(s4);
                  } catch (NumberFormatException var15) {
                     ;
                  }
               } else if ("--singleplayer".equals(s3) && s4 != null) {
                  flag4 = true;
                  s = s4;
               } else if ("--universe".equals(s3) && s4 != null) {
                  flag4 = true;
                  s1 = s4;
               } else if ("--world".equals(s3) && s4 != null) {
                  flag4 = true;
                  s2 = s4;
               } else if ("--demo".equals(s3)) {
                  flag1 = true;
               } else if ("--bonusChest".equals(s3)) {
                  flag2 = true;
               } else if ("--forceUpgrade".equals(s3)) {
                  flag3 = true;
               }
            } else {
               flag = false;
            }

            if (flag4) {
               ++j;
            }
         }

         YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
         MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
         GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
         PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(s1, USER_CACHE_FILE.getName()));
         final DedicatedServer dedicatedserver = new DedicatedServer(new File(s1), DataFixesManager.getDataFixer(), yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, playerprofilecache);
         if (s != null) {
            dedicatedserver.setServerOwner(s);
         }

         if (s2 != null) {
            dedicatedserver.setFolderName(s2);
         }

         if (i >= 0) {
            dedicatedserver.setServerPort(i);
         }

         if (flag1) {
            dedicatedserver.setDemo(true);
         }

         if (flag2) {
            dedicatedserver.canCreateBonusChest(true);
         }

         if (flag && !GraphicsEnvironment.isHeadless()) {
            dedicatedserver.setGuiEnabled();
         }

         if (flag3) {
            dedicatedserver.setForceWorldUpgrade(true);
         }

         dedicatedserver.startServerThread();
         Thread thread = new Thread("Server Shutdown Thread") {
            public void run() {
               dedicatedserver.stopServer();
            }
         };
         thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
         Runtime.getRuntime().addShutdownHook(thread);
      } catch (Exception exception) {
         LOGGER.fatal("Failed to start the minecraft server", (Throwable)exception);
      }

   }

   protected void setForceWorldUpgrade(boolean forceWorldUpgradeIn) {
      this.forceWorldUpgrade = forceWorldUpgradeIn;
   }

   public void startServerThread() {
      this.serverThread = new Thread(net.minecraftforge.fml.common.thread.SidedThreadGroups.SERVER, this, "Server thread");
      this.serverThread.setUncaughtExceptionHandler((p_195574_0_, p_195574_1_) -> {
         LOGGER.error(p_195574_1_);
      });
      this.serverThread.start();
   }

   /**
    * Returns a File object from the specified string.
    */
   public File getFile(String fileName) {
      return new File(this.getDataDirectory(), fileName);
   }

   /**
    * Logs the message with a level of INFO.
    */
   public void logInfo(String msg) {
      LOGGER.info(msg);
   }

   /**
    * Logs the message with a level of WARN.
    */
   public void logWarning(String msg) {
      LOGGER.warn(msg);
   }

   /**
    * Gets the worldServer by the given dimension.
    */
   public WorldServer getWorld(DimensionType dimension) {
      return net.minecraftforge.common.DimensionManager.getWorld(this, dimension, true, true);
   }

   public Iterable<WorldServer> func_212370_w() {
      return this.worlds.values();
   }

   /**
    * Returns the server's Minecraft version as string.
    */
   public String getMinecraftVersion() {
      return "1.13.2";
   }

   /**
    * Returns the number of players currently on the server.
    */
   public int getCurrentPlayerCount() {
      return this.playerList.getCurrentPlayerCount();
   }

   /**
    * Returns the maximum number of players allowed on the server.
    */
   public int getMaxPlayers() {
      return this.playerList.getMaxPlayers();
   }

   /**
    * Returns an array of the usernames of all the connected players.
    */
   public String[] getOnlinePlayerNames() {
      return this.playerList.getOnlinePlayerNames();
   }

   /**
    * Returns true if debugging is enabled, false otherwise.
    */
   public boolean isDebuggingEnabled() {
      return false;
   }

   /**
    * Logs the error message with a level of SEVERE.
    */
   public void logSevere(String msg) {
      LOGGER.error(msg);
   }

   /**
    * If isDebuggingEnabled(), logs the message with a level of INFO.
    */
   public void logDebug(String msg) {
      if (this.isDebuggingEnabled()) {
         LOGGER.info(msg);
      }

   }

   public String getServerModName() {
      return net.minecraftforge.fml.BrandingControl.getServerBranding();
   }

   /**
    * Adds the server info, including from theWorldServer, to the crash report.
    */
   public CrashReport addServerInfoToCrashReport(CrashReport report) {
      report.getCategory().addDetail("Profiler Position", () -> {
         return this.profiler.isProfiling() ? this.profiler.getNameOfLastSection() : "N/A (disabled)";
      });
      if (this.playerList != null) {
         report.getCategory().addDetail("Player Count", () -> {
            return this.playerList.getCurrentPlayerCount() + " / " + this.playerList.getMaxPlayers() + "; " + this.playerList.getPlayers();
         });
      }

      report.getCategory().addDetail("Data Packs", () -> {
         StringBuilder stringbuilder = new StringBuilder();

         for(ResourcePackInfo resourcepackinfo : this.resourcePacks.getPackInfos()) {
            if (stringbuilder.length() > 0) {
               stringbuilder.append(", ");
            }

            stringbuilder.append(resourcepackinfo.getName());
            if (!resourcepackinfo.getCompatibility().func_198968_a()) {
               stringbuilder.append(" (incompatible)");
            }
         }

         return stringbuilder.toString();
      });
      return report;
   }

   public boolean isAnvilFileSet() {
      return this.anvilFile != null;
   }

   /**
    * Send a chat message to the CommandSender
    */
   public void sendMessage(ITextComponent component) {
      LOGGER.info(component.getString());
   }

   /**
    * Gets KeyPair instanced in MinecraftServer.
    */
   public KeyPair getKeyPair() {
      return this.serverKeyPair;
   }

   /**
    * Gets serverPort.
    */
   public int getServerPort() {
      return this.serverPort;
   }

   public void setServerPort(int port) {
      this.serverPort = port;
   }

   /**
    * Returns the username of the server owner (for integrated servers)
    */
   public String getServerOwner() {
      return this.serverOwner;
   }

   /**
    * Sets the username of the owner of this server (in the case of an integrated server)
    */
   public void setServerOwner(String owner) {
      this.serverOwner = owner;
   }

   public boolean isSinglePlayer() {
      return this.serverOwner != null;
   }

   public String getFolderName() {
      return this.folderName;
   }

   public void setFolderName(String name) {
      this.folderName = name;
   }

   @OnlyIn(Dist.CLIENT)
   public void setWorldName(String worldNameIn) {
      this.worldName = worldNameIn;
   }

   @OnlyIn(Dist.CLIENT)
   public String getWorldName() {
      return this.worldName;
   }

   public void setKeyPair(KeyPair keyPair) {
      this.serverKeyPair = keyPair;
   }

   public void setDifficultyForAllWorlds(EnumDifficulty difficulty) {
      for(WorldServer worldserver : this.func_212370_w()) {
         if (worldserver.getWorldInfo().isHardcore()) {
            worldserver.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
            worldserver.setAllowedSpawnTypes(true, true);
         } else if (this.isSinglePlayer()) {
            worldserver.getWorldInfo().setDifficulty(difficulty);
            worldserver.setAllowedSpawnTypes(worldserver.getDifficulty() != EnumDifficulty.PEACEFUL, true);
         } else {
            worldserver.getWorldInfo().setDifficulty(difficulty);
            worldserver.setAllowedSpawnTypes(this.allowSpawnMonsters(), this.canSpawnAnimals);
         }
      }

   }

   public boolean allowSpawnMonsters() {
      return true;
   }

   /**
    * Gets whether this is a demo or not.
    */
   public boolean isDemo() {
      return this.isDemo;
   }

   /**
    * Sets whether this is a demo or not.
    */
   public void setDemo(boolean demo) {
      this.isDemo = demo;
   }

   public void canCreateBonusChest(boolean enable) {
      this.enableBonusChest = enable;
   }

   public ISaveFormat getActiveAnvilConverter() {
      return this.anvilConverterForAnvilFile;
   }

   public String getResourcePackUrl() {
      return this.resourcePackUrl;
   }

   public String getResourcePackHash() {
      return this.resourcePackHash;
   }

   public void setResourcePack(String url, String hash) {
      this.resourcePackUrl = url;
      this.resourcePackHash = hash;
   }

   public void addServerStatsToSnooper(Snooper playerSnooper) {
      playerSnooper.addClientStat("whitelist_enabled", false);
      playerSnooper.addClientStat("whitelist_count", 0);
      if (this.playerList != null) {
         playerSnooper.addClientStat("players_current", this.getCurrentPlayerCount());
         playerSnooper.addClientStat("players_max", this.getMaxPlayers());
         playerSnooper.addClientStat("players_seen", this.playerList.getAvailablePlayerDat().length);
      }

      playerSnooper.addClientStat("uses_auth", this.onlineMode);
      playerSnooper.addClientStat("gui_state", this.getGuiEnabled() ? "enabled" : "disabled");
      playerSnooper.addClientStat("run_time", (Util.milliTime() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
      playerSnooper.addClientStat("avg_tick_ms", (int)(MathHelper.average(this.tickTimeArray) * 1.0E-6D));
      int i = 0;

      for(WorldServer worldserver : this.func_212370_w()) {
         if (worldserver != null) {
            WorldInfo worldinfo = worldserver.getWorldInfo();
            playerSnooper.addClientStat("world[" + i + "][dimension]", worldserver.dimension.getType());
            playerSnooper.addClientStat("world[" + i + "][mode]", worldinfo.getGameType());
            playerSnooper.addClientStat("world[" + i + "][difficulty]", worldserver.getDifficulty());
            playerSnooper.addClientStat("world[" + i + "][hardcore]", worldinfo.isHardcore());
            playerSnooper.addClientStat("world[" + i + "][generator_name]", worldinfo.getTerrainType().getName());
            playerSnooper.addClientStat("world[" + i + "][generator_version]", worldinfo.getTerrainType().getVersion());
            playerSnooper.addClientStat("world[" + i + "][height]", this.buildLimit);
            playerSnooper.addClientStat("world[" + i + "][chunks_loaded]", worldserver.getChunkProvider().getLoadedChunkCount());
            ++i;
         }
      }

      playerSnooper.addClientStat("worlds", i);
   }

   /**
    * Returns whether snooping is enabled or not.
    */
   public boolean isSnooperEnabled() {
      return true;
   }

   public abstract boolean isDedicatedServer();

   public boolean isServerInOnlineMode() {
      return this.onlineMode;
   }

   public void setOnlineMode(boolean online) {
      this.onlineMode = online;
   }

   public boolean getPreventProxyConnections() {
      return this.preventProxyConnections;
   }

   public void setPreventProxyConnections(boolean p_190517_1_) {
      this.preventProxyConnections = p_190517_1_;
   }

   public boolean getCanSpawnAnimals() {
      return this.canSpawnAnimals;
   }

   public void setCanSpawnAnimals(boolean spawnAnimals) {
      this.canSpawnAnimals = spawnAnimals;
   }

   public boolean getCanSpawnNPCs() {
      return this.canSpawnNPCs;
   }

   /**
    * Get if native transport should be used. Native transport means linux server performance improvements and optimized
    * packet sending/receiving on linux
    */
   public abstract boolean shouldUseNativeTransport();

   public void setCanSpawnNPCs(boolean spawnNpcs) {
      this.canSpawnNPCs = spawnNpcs;
   }

   public boolean isPVPEnabled() {
      return this.pvpEnabled;
   }

   public void setAllowPvp(boolean allowPvp) {
      this.pvpEnabled = allowPvp;
   }

   public boolean isFlightAllowed() {
      return this.allowFlight;
   }

   public void setAllowFlight(boolean allow) {
      this.allowFlight = allow;
   }

   /**
    * Return whether command blocks are enabled.
    */
   public abstract boolean isCommandBlockEnabled();

   public String getMOTD() {
      return this.motd;
   }

   public void setMOTD(String motdIn) {
      this.motd = motdIn;
   }

   public int getBuildLimit() {
      return this.buildLimit;
   }

   public void setBuildLimit(int maxBuildHeight) {
      this.buildLimit = maxBuildHeight;
   }

   public boolean isServerStopped() {
      return this.serverStopped;
   }

   public PlayerList getPlayerList() {
      return this.playerList;
   }

   public void setPlayerList(PlayerList list) {
      this.playerList = list;
   }

   /**
    * Returns true if this integrated server is open to LAN
    */
   public abstract boolean getPublic();

   /**
    * Sets the game type for all worlds.
    */
   public void setGameType(GameType gameMode) {
      for(WorldServer worldserver : this.func_212370_w()) {
         worldserver.getWorldInfo().setGameType(gameMode);
      }

   }

   public NetworkSystem getNetworkSystem() {
      return this.networkSystem;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean serverIsInRunLoop() {
      return this.serverIsRunning;
   }

   public boolean getGuiEnabled() {
      return false;
   }

   public abstract boolean shareToLAN(GameType gameMode, boolean cheats, int port);

   public int getTickCounter() {
      return this.tickCounter;
   }

   public void enableProfiling() {
      this.startProfiling = true;
   }

   @OnlyIn(Dist.CLIENT)
   public Snooper getSnooper() {
      return this.snooper;
   }

   /**
    * Return the spawn protection area's size.
    */
   public int getSpawnProtectionSize() {
      return 16;
   }

   public boolean isBlockProtected(World worldIn, BlockPos pos, EntityPlayer playerIn) {
      return false;
   }

   /**
    * Set the forceGamemode field (whether joining players will be put in their old gamemode or the default one)
    */
   public void setForceGamemode(boolean force) {
      this.isGamemodeForced = force;
   }

   /**
    * Get the forceGamemode field (whether joining players will be put in their old gamemode or the default one)
    */
   public boolean getForceGamemode() {
      return this.isGamemodeForced;
   }

   public int getMaxPlayerIdleMinutes() {
      return this.maxPlayerIdleMinutes;
   }

   public void setPlayerIdleTimeout(int idleTimeout) {
      this.maxPlayerIdleMinutes = idleTimeout;
   }

   public MinecraftSessionService getMinecraftSessionService() {
      return this.sessionService;
   }

   public GameProfileRepository getGameProfileRepository() {
      return this.profileRepo;
   }

   public PlayerProfileCache getPlayerProfileCache() {
      return this.profileCache;
   }

   public ServerStatusResponse getServerStatusResponse() {
      return this.statusResponse;
   }

   public void refreshStatusNextTick() {
      this.nanoTimeSinceStatusRefresh = 0L;
   }

   public int getMaxWorldSize() {
      return 29999984;
   }

   public <V> ListenableFuture<V> callFromMainThread(Callable<V> callable) {
      Validate.notNull(callable);
      if (!this.isCallingFromMinecraftThread() && !this.isServerStopped()) {
         ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callable);
         this.futureTaskQueue.add(listenablefuturetask);
         return listenablefuturetask;
      } else {
         try {
            return Futures.immediateFuture(callable.call());
         } catch (Exception exception) {
            return Futures.immediateFailedCheckedFuture(exception);
         }
      }
   }

   public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
      Validate.notNull(runnableToSchedule);
      return this.callFromMainThread(Executors.callable(runnableToSchedule));
   }

   public boolean isCallingFromMinecraftThread() {
      return Thread.currentThread() == this.serverThread;
   }

   /**
    * The compression treshold. If the packet is larger than the specified amount of bytes, it will be compressed
    */
   public int getNetworkCompressionThreshold() {
      return 256;
   }

   public long getServerTime() {
      return this.serverTime;
   }

   public Thread getServerThread() {
      return this.serverThread;
   }

   public DataFixer getDataFixer() {
      return this.dataFixer;
   }

   public int getSpawnRadius(@Nullable WorldServer worldIn) {
      return worldIn != null ? worldIn.getGameRules().getInt("spawnRadius") : 10;
   }

   public AdvancementManager getAdvancementManager() {
      return this.advancementManager;
   }

   public FunctionManager getFunctionManager() {
      return this.functionManager;
   }

   public void reload() {
      if (!this.isCallingFromMinecraftThread()) {
         this.addScheduledTask(this::reload);
      } else {
         this.getPlayerList().saveAllPlayerData();
         this.resourcePacks.reloadPacksFromFinders();
         this.loadDataPacks(this.getWorld(DimensionType.OVERWORLD).getWorldInfo());
         this.getPlayerList().reloadResources();
      }
   }

   private void loadDataPacks(WorldInfo worldInfoIn) {
      List<ResourcePackInfo> list = Lists.newArrayList(this.resourcePacks.getPackInfos());

      for(ResourcePackInfo resourcepackinfo : this.resourcePacks.func_198978_b()) {
         if (!worldInfoIn.getDisabledDataPacks().contains(resourcepackinfo.getName()) && !list.contains(resourcepackinfo)) {
            LOGGER.info("Found new data pack {}, loading it automatically", (Object)resourcepackinfo.getName());
            resourcepackinfo.getPriority().func_198993_a(list, resourcepackinfo, (p_200247_0_) -> {
               return p_200247_0_;
            }, false);
         }
      }

      this.resourcePacks.func_198985_a(list);
      List<IResourcePack> list1 = Lists.newArrayList();
      this.resourcePacks.getPackInfos().forEach((p_200244_1_) -> {
         list1.add(p_200244_1_.getResourcePack());
      });
      this.resourceManager.reload(list1);
      worldInfoIn.getEnabledDataPacks().clear();
      worldInfoIn.getDisabledDataPacks().clear();
      this.resourcePacks.getPackInfos().forEach((p_195562_1_) -> {
         worldInfoIn.getEnabledDataPacks().add(p_195562_1_.getName());
      });
      this.resourcePacks.func_198978_b().forEach((p_200248_2_) -> {
         if (!this.resourcePacks.getPackInfos().contains(p_200248_2_)) {
            worldInfoIn.getDisabledDataPacks().add(p_200248_2_.getName());
         }

      });
   }

   public void kickPlayersNotWhitelisted(CommandSource commandSourceIn) {
      if (this.isWhitelistEnabled()) {
         PlayerList playerlist = commandSourceIn.getServer().getPlayerList();
         UserListWhitelist userlistwhitelist = playerlist.getWhitelistedPlayers();
         if (userlistwhitelist.isLanServer()) {
            for(EntityPlayerMP entityplayermp : Lists.newArrayList(playerlist.getPlayers())) {
               if (!userlistwhitelist.isWhitelisted(entityplayermp.getGameProfile())) {
                  entityplayermp.connection.disconnect(new TextComponentTranslation("multiplayer.disconnect.not_whitelisted"));
               }
            }

         }
      }
   }

   public IReloadableResourceManager getResourceManager() {
      return this.resourceManager;
   }

   public ResourcePackList<ResourcePackInfo> getResourcePacks() {
      return this.resourcePacks;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getCurrentTask() {
      return this.currentTask;
   }

   @OnlyIn(Dist.CLIENT)
   public int getPercentDone() {
      return this.percentDone;
   }

   public Commands getCommandManager() {
      return this.commandManager;
   }

   public CommandSource getCommandSource() {
      return new CommandSource(this, this.getWorld(DimensionType.OVERWORLD) == null ? Vec3d.ZERO : new Vec3d(this.getWorld(DimensionType.OVERWORLD).getSpawnPoint()), Vec2f.ZERO, this.getWorld(DimensionType.OVERWORLD), 4, "Server", new TextComponentString("Server"), this, (Entity)null);
   }

   public boolean shouldReceiveFeedback() {
      return true;
   }

   public boolean shouldReceiveErrors() {
      return true;
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   public NetworkTagManager getNetworkTagManager() {
      return this.networkTagManager;
   }

   public ServerScoreboard getWorldScoreboard() {
      return this.scoreboard;
   }

   public LootTableManager getLootTableManager() {
      return this.lootTableManager;
   }

   public GameRules getGameRules() {
      return this.getWorld(DimensionType.OVERWORLD).getGameRules();
   }

   public CustomBossEvents getCustomBossEvents() {
      return this.customBossEvents;
   }

   public boolean isWhitelistEnabled() {
      return this.whitelistEnabled;
   }

   public void setWhitelistEnabled(boolean whitelistEnabledIn) {
      this.whitelistEnabled = whitelistEnabledIn;
   }

   @OnlyIn(Dist.CLIENT)
   public float getTickTime() {
      return this.tickTime;
   }

   public int getPermissionLevel(GameProfile profile) {
      if (this.getPlayerList().canSendCommands(profile)) {
         UserListOpsEntry userlistopsentry = this.getPlayerList().getOppedPlayers().getEntry(profile);
         if (userlistopsentry != null) {
            return userlistopsentry.getPermissionLevel();
         } else if (this.isSinglePlayer()) {
            if (this.getServerOwner().equals(profile.getName())) {
               return 4;
            } else {
               return this.getPlayerList().commandsAllowedForAll() ? 4 : 0;
            }
         } else {
            return this.getOpPermissionLevel();
         }
      } else {
         return 0;
      }
   }

   @Nullable
   public long[] getTickTime(DimensionType dim) {
      return timeOfLastDimensionTick.get(dim);
   }

   @Deprecated //Forge Internal use Only, You can screw up a lot of things if you mess with this map.
   public synchronized Map<DimensionType, WorldServer> forgeGetWorldMap() {
      return this.worlds;
   }
}