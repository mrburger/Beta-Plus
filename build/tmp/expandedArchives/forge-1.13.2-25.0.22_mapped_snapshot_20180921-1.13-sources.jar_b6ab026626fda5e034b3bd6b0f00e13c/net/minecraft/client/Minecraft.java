package net.minecraft.client;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiConnecting;
import net.minecraft.client.gui.GuiDirtMessageScreen;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenLoading;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IGuiEventListenerDeferred;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.fonts.FontResourceManager;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GlDebugTextUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DownloadingPackFinder;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.LegacyResourcePackWrapper;
import net.minecraft.client.resources.ResourcePackInfoClient;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.util.ISearchTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.client.util.SearchTree;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentKeybind;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.dimension.NetherDimension;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

@OnlyIn(Dist.CLIENT)
public class Minecraft implements IThreadListener, ISnooperInfo, IGuiEventListenerDeferred {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final boolean IS_RUNNING_ON_MAC = Util.getOSType() == Util.EnumOS.OSX;
   public static final ResourceLocation DEFAULT_FONT_RENDERER_NAME = new ResourceLocation("default");
   public static final ResourceLocation standardGalacticFontRenderer = new ResourceLocation("alt");
   /**
    * A 10MiB preallocation to ensure the heap is reasonably sized. {@linkplain #freeMemory() Freed} when the game runs
    * out of memory.
    *  
    * @see #freeMemory()
    */
   public static byte[] memoryReserve = new byte[10485760];
   private static int cachedMaximumTextureSize = -1;
   private final File fileResourcepacks;
   /** The player's GameProfile properties */
   private final PropertyMap profileProperties;
   private final GameConfiguration.DisplayInformation displayInfo;
   private ServerData currentServerData;
   /** The RenderEngine instance used by Minecraft */
   public TextureManager textureManager;
   /** The instance of the Minecraft Client, set in the constructor. */
   private static Minecraft instance;
   private final DataFixer dataFixer;
   public PlayerControllerMP playerController;
   private VirtualScreen virtualScreen;
   public MainWindow mainWindow;
   private boolean hasCrashed;
   /** Instance of CrashReport. */
   private CrashReport crashReporter;
   /** True if the player is connected to a realms server */
   private boolean connectedToRealms;
   private final Timer timer = new Timer(20.0F, 0L);
   /** Instance of PlayerUsageSnooper. */
   private final Snooper snooper = new Snooper("client", this, Util.milliTime());
   public WorldClient world;
   public WorldRenderer renderGlobal;
   private RenderManager renderManager;
   private ItemRenderer itemRenderer;
   private FirstPersonRenderer firstPersonRenderer;
   public EntityPlayerSP player;
   @Nullable
   private Entity renderViewEntity;
   @Nullable
   public Entity pointedEntity;
   public ParticleManager particles;
   /** Manages all search trees */
   private final SearchTreeManager searchTreeManager = new SearchTreeManager();
   private final Session session;
   private boolean isGamePaused;
   /**
    * Time passed since the last update in ticks. Used instead of this.timer.renderPartialTicks when paused in
    * singleplayer.
    */
   private float renderPartialTicksPaused;
   /** The font renderer used for displaying and measuring text */
   public FontRenderer fontRenderer;
   /** The GuiScreen that's being displayed at the moment. */
   @Nullable
   public GuiScreen currentScreen;
   public GameRenderer entityRenderer;
   public DebugRenderer debugRenderer;
   /** Mouse left click counter */
   int leftClickCounter;
   /** Instance of IntegratedServer. */
   @Nullable
   private IntegratedServer integratedServer;
   public GuiIngame ingameGUI;
   /** Skip render world */
   public boolean skipRenderWorld;
   /** The ray trace hit that the mouse is over. */
   public RayTraceResult objectMouseOver;
   /** The game settings that currently hold effect. */
   public GameSettings gameSettings;
   private CreativeSettings creativeSettings;
   /** Mouse helper instance. */
   public MouseHelper mouseHelper;
   public KeyboardListener keyboardListener;
   public final File gameDir;
   private final File fileAssets;
   private final String launchedVersion;
   private final String versionType;
   private final Proxy proxy;
   private ISaveFormat saveLoader;
   /**
    * This is set to fpsCounter every debug screen update, and is shown on the debug screen. It's also sent as part of
    * the usage snooping.
    */
   private static int debugFPS;
   /** When you place a block, it's set to 6, decremented once per tick, when it's 0, you can place another block. */
   private int rightClickDelayTimer;
   private String serverName;
   private int serverPort;
   /** Join player counter */
   private int joinPlayerCounter;
   /** The FrameTimer's instance */
   public final FrameTimer frameTimer = new FrameTimer();
   /** Time in nanoseconds of when the class is loaded */
   private long startNanoTime = Util.nanoTime();
   private final boolean jvm64bit;
   private final boolean isDemo;
   @Nullable
   private NetworkManager networkManager;
   private boolean integratedServerIsRunning;
   /** The profiler instance */
   public final Profiler profiler = new Profiler();
   private IReloadableResourceManager resourceManager;
   private final DownloadingPackFinder packFinder;
   private final ResourcePackList<ResourcePackInfoClient> resourcePackRepository;
   private LanguageManager languageManager;
   private BlockColors blockColors;
   private ItemColors itemColors;
   private Framebuffer framebuffer;
   private TextureMap textureMap;
   private SoundHandler soundHandler;
   private MusicTicker musicTicker;
   private FontResourceManager fontResourceMananger;
   private final MinecraftSessionService sessionService;
   private SkinManager skinManager;
   private final Queue<FutureTask<?>> scheduledTasks = Queues.newConcurrentLinkedQueue();
   private final Thread thread = Thread.currentThread();
   private ModelManager modelManager;
   /** The BlockRenderDispatcher instance that will be used based off gamesettings */
   private BlockRendererDispatcher blockRenderDispatcher;
   private final GuiToast toastGui;
   /** Set to true to keep the game loop running. Set to false by shutdown() to allow the game loop to exit cleanly. */
   private volatile boolean running = true;
   /** String that shows the debug information */
   public String debug = "";
   public boolean renderChunksMany = true;
   /** Approximate time (in ms) of last update to debug string */
   private long debugUpdateTime;
   /** holds the current fps */
   private int fpsCounter;
   private final Tutorial tutorial;
   boolean isWindowFocused;
   /** Profiler currently displayed in the debug screen pie chart */
   private String debugProfilerName = "root";

   public Minecraft(GameConfiguration gameConfig) {
      this.displayInfo = gameConfig.displayInfo;
      instance = this;
      this.gameDir = gameConfig.folderInfo.gameDir;
      this.fileAssets = gameConfig.folderInfo.assetsDir;
      this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
      this.launchedVersion = gameConfig.gameInfo.version;
      this.versionType = gameConfig.gameInfo.versionType;
      this.profileProperties = gameConfig.userInfo.profileProperties;
      this.packFinder = new DownloadingPackFinder(new File(this.gameDir, "server-resource-packs"), gameConfig.folderInfo.getAssetsIndex());
      this.resourcePackRepository = new ResourcePackList<>((p_211818_0_, p_211818_1_, p_211818_2_, p_211818_3_, p_211818_4_, p_211818_5_) -> {
         Supplier<IResourcePack> supplier;
         if (p_211818_4_.getPackFormat() < 4) {
            supplier = () -> {
               return new LegacyResourcePackWrapper((IResourcePack)p_211818_2_.get(), LegacyResourcePackWrapper.NEW_TO_LEGACY_MAP);
            };
         } else {
            supplier = p_211818_2_;
         }

         return new ResourcePackInfoClient(p_211818_0_, p_211818_1_, supplier, p_211818_3_, p_211818_4_, p_211818_5_);
      });
      this.resourcePackRepository.addPackFinder(this.packFinder);
      this.resourcePackRepository.addPackFinder(new FolderPackFinder(this.fileResourcepacks));
      this.proxy = gameConfig.userInfo.proxy == null ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
      this.sessionService = (new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString())).createMinecraftSessionService();
      this.session = gameConfig.userInfo.session;
      LOGGER.info("Setting user: {}", (Object)this.session.getUsername());
      this.isDemo = gameConfig.gameInfo.isDemo;
      this.jvm64bit = isJvm64bit();
      this.integratedServer = null;
      if (gameConfig.serverInfo.serverName != null) {
         this.serverName = gameConfig.serverInfo.serverName;
         this.serverPort = gameConfig.serverInfo.serverPort;
      }

      Bootstrap.register();
      TextComponentKeybind.displaySupplierFunction = KeyBinding::getDisplayString;
      this.dataFixer = DataFixesManager.getDataFixer();
      this.toastGui = new GuiToast(this);
      this.tutorial = new Tutorial(this);
   }

   public void run() {
      this.running = true;

      try {
         this.init();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Initializing game");
         crashreport.makeCategory("Initialization");
         this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(crashreport));
         return;
      }

      while(true) {
         try {
            while(this.running) {

            if (!this.hasCrashed || this.crashReporter == null) {
               try {
                  this.runGameLoop(true);
               } catch (OutOfMemoryError var9) {
                  this.freeMemory();
                  this.displayGuiScreen(new GuiMemoryErrorScreen());
                  System.gc();
               }
            }
            else
            this.displayCrashReport(this.crashReporter);
            }

         } catch (ReportedException reportedexception) {
            this.addGraphicsAndWorldToCrashReport(reportedexception.getCrashReport());
            this.freeMemory();
            LOGGER.fatal("Reported exception thrown!", (Throwable)reportedexception);
            this.displayCrashReport(reportedexception.getCrashReport());
            break;
         } catch (Throwable throwable1) {
            CrashReport crashreport1 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", throwable1));
            this.freeMemory();
            LOGGER.fatal("Unreported exception thrown!", throwable1);
            this.displayCrashReport(crashreport1);
            break;
         } finally {
            this.shutdownMinecraftApplet();
         }

         return;
      }

   }

   /**
    * Starts the game: initializes the canvas, the title, the settings, etcetera.
    */
   private void init() {
      this.gameSettings = new GameSettings(this, this.gameDir);
      this.creativeSettings = new CreativeSettings(this.gameDir, this.dataFixer);
      this.startTimerHackThread();
      LOGGER.info("LWJGL Version: {}", (Object)Version.getVersion());
      GameConfiguration.DisplayInformation gameconfiguration$displayinformation = this.displayInfo;
      if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
         gameconfiguration$displayinformation = new GameConfiguration.DisplayInformation(this.gameSettings.overrideWidth, this.gameSettings.overrideHeight, gameconfiguration$displayinformation.fullscreenWidth, gameconfiguration$displayinformation.fullscreenHeight, gameconfiguration$displayinformation.fullscreen);
      }

      this.checkForGLFWInitError();
      this.virtualScreen = new VirtualScreen(this);
      this.mainWindow = this.virtualScreen.createMainWindow(gameconfiguration$displayinformation, this.gameSettings.fullscreenResolution);
      OpenGlHelper.init();
      GlDebugTextUtils.setDebugVerbosity(this.gameSettings.glDebugVerbosity);
      this.framebuffer = new Framebuffer(this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight(), true);
      this.framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.resourceManager = new SimpleReloadableResourceManager(ResourcePackType.CLIENT_RESOURCES);
      this.languageManager = new LanguageManager(this.gameSettings.language);
      this.resourceManager.addReloadListener(this.languageManager);
      this.gameSettings.fillResourcePackList(this.resourcePackRepository);
      net.minecraftforge.fml.client.ClientModLoader.begin(this, this.resourcePackRepository, this.resourceManager, this.packFinder);
      this.refreshResources();
      this.textureManager = new TextureManager(this.resourceManager);
      this.resourceManager.addReloadListener(this.textureManager);
      //net.minecraftforge.fml.client.SplashProgress.drawVanillaScreen(this.textureManager);
      this.mainWindow.updateSize();
      this.displayGuiScreen(new GuiScreenLoading());
      this.initMainWindow();
      this.skinManager = new SkinManager(this.textureManager, new File(this.fileAssets, "skins"), this.sessionService);
      this.saveLoader = new AnvilSaveConverter(this.gameDir.toPath().resolve("saves"), this.gameDir.toPath().resolve("backups"), this.dataFixer);
      this.soundHandler = new SoundHandler(this.resourceManager, this.gameSettings);
      this.resourceManager.addReloadListener(this.soundHandler);
      this.musicTicker = new MusicTicker(this);
      this.fontResourceMananger = new FontResourceManager(this.textureManager, this.getForceUnicodeFont());
      this.resourceManager.addReloadListener(this.fontResourceMananger);
      this.fontRenderer = this.fontResourceMananger.getFontRenderer(DEFAULT_FONT_RENDERER_NAME);
      if (this.gameSettings.language != null) {
         this.fontRenderer.setBidiFlag(this.languageManager.isCurrentLanguageBidirectional());
      }

      this.resourceManager.addReloadListener(new GrassColorReloadListener());
      this.resourceManager.addReloadListener(new FoliageColorReloadListener());
      try (net.minecraftforge.fml.common.progress.ProgressBar bar = net.minecraftforge.fml.common.progress.StartupProgressManager.start("Rendering Setup", 5, true)) {
      bar.step("GL Setup");
      this.mainWindow.setRenderPhase("Startup");
      GlStateManager.enableTexture2D();
      GlStateManager.shadeModel(7425);
      GlStateManager.clearDepth(1.0D);
      GlStateManager.enableDepthTest();
      GlStateManager.depthFunc(515);
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.cullFace(GlStateManager.CullFace.BACK);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);
      this.mainWindow.setRenderPhase("Post startup");
      bar.step("Loading Texture Map");
      this.textureMap = new TextureMap("textures");
      this.textureMap.setMipmapLevels(this.gameSettings.mipmapLevels);
      this.textureManager.loadTickableTexture(TextureMap.LOCATION_BLOCKS_TEXTURE, this.textureMap);
      this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      this.textureMap.setBlurMipmapDirect(false, this.gameSettings.mipmapLevels > 0);
      bar.step("Loading Model Manager");
      this.modelManager = new ModelManager(this.textureMap);
      this.resourceManager.addReloadListener(this.modelManager);
      this.blockColors = BlockColors.init();
      this.itemColors = ItemColors.init(this.blockColors);
      bar.step("Loading Item Renderer");
      this.itemRenderer = new ItemRenderer(this.textureManager, this.modelManager, this.itemColors);
      this.renderManager = new RenderManager(this.textureManager, this.itemRenderer);
      this.firstPersonRenderer = new FirstPersonRenderer(this);
      this.resourceManager.addReloadListener(this.itemRenderer);
      bar.step("Loading Entity Renderer");
      //net.minecraftforge.fml.client.SplashProgress.pause();
      this.entityRenderer = new GameRenderer(this, this.resourceManager);
      this.resourceManager.addReloadListener(this.entityRenderer);
      this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes(), this.blockColors);
      this.resourceManager.addReloadListener(this.blockRenderDispatcher);
      this.renderGlobal = new WorldRenderer(this);
      this.resourceManager.addReloadListener(this.renderGlobal);
      this.populateSearchTreeManager();
      this.resourceManager.addReloadListener(this.searchTreeManager);
      GlStateManager.viewport(0, 0, this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight());
      this.particles = new ParticleManager(this.world, this.textureManager);
      //net.minecraftforge.fml.client.SplashProgress.resume();
      }; // Forge: end progress bar
      net.minecraftforge.fml.client.ClientModLoader.end();
      this.ingameGUI = new net.minecraftforge.client.GuiIngameForge(this);
      if (this.serverName != null) {
         this.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this, this.serverName, this.serverPort));
      } else {
         this.displayGuiScreen(new GuiMainMenu());
      }

      this.debugRenderer = new DebugRenderer(this);
      GLFW.glfwSetErrorCallback(this::disableVSyncAfterGlError).free();
      net.minecraftforge.fml.client.ClientModLoader.complete();
      if (this.gameSettings.fullScreen && !this.mainWindow.isFullscreen()) {
         this.mainWindow.toggleFullscreen();
      }

      this.mainWindow.updateVsyncFromGameSettings();
      this.mainWindow.setLogOnGlError();
      this.renderGlobal.makeEntityOutlineShader();
   }

   private void checkForGLFWInitError() {
      MainWindow.checkGlfwError((p_211108_0_, p_211108_1_) -> {
         throw new IllegalStateException(String.format("GLFW error before init: [0x%X]%s", p_211108_0_, p_211108_1_));
      });
      List<String> list = Lists.newArrayList();
      GLFWErrorCallback glfwerrorcallback = GLFW.glfwSetErrorCallback((p_211100_1_, p_211100_2_) -> {
         list.add(String.format("GLFW error during init: [0x%X] %s", p_211100_1_, GLFWErrorCallback.getDescription(p_211100_2_)));
      });
      if (!GLFW.glfwInit()) {
         throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on(",").join(list));
      } else {
         Util.nanoTimeSupplier = () -> {
            return (long)(GLFW.glfwGetTime() * 1.0E9D);
         };

         for(String s : list) {
            LOGGER.error("GLFW error collected during initialization: {}", (Object)s);
         }

         GLFW.glfwSetErrorCallback(glfwerrorcallback).free();
      }
   }

   /**
    * Fills {@link #searchTreeManager} with the current item and recipe registry contents.
    */
   public void populateSearchTreeManager() {
      SearchTree<ItemStack> searchtree = new SearchTree<>((p_193988_0_) -> {
         return p_193988_0_.getTooltip((EntityPlayer)null, ITooltipFlag.TooltipFlags.NORMAL).stream().map((p_211817_0_) -> {
            return TextFormatting.getTextWithoutFormattingCodes(p_211817_0_.getString()).trim();
         }).filter((p_200241_0_) -> {
            return !p_200241_0_.isEmpty();
         }).collect(Collectors.toList());
      }, (p_193985_0_) -> {
         return Collections.singleton(IRegistry.field_212630_s.getKey(p_193985_0_.getItem()));
      });
      NonNullList<ItemStack> nonnulllist = NonNullList.create();

      for(Item item : IRegistry.field_212630_s) {
         item.fillItemGroup(ItemGroup.SEARCH, nonnulllist);
      }

      nonnulllist.forEach(searchtree::add);
      SearchTree<RecipeList> searchtree1 = new SearchTree<>((p_193990_0_) -> {
         return p_193990_0_.getRecipes().stream().flatMap((p_200240_0_) -> {
            return p_200240_0_.getRecipeOutput().getTooltip((EntityPlayer)null, ITooltipFlag.TooltipFlags.NORMAL).stream();
         }).map((p_200235_0_) -> {
            return TextFormatting.getTextWithoutFormattingCodes(p_200235_0_.getString()).trim();
         }).filter((p_200234_0_) -> {
            return !p_200234_0_.isEmpty();
         }).collect(Collectors.toList());
      }, (p_193992_0_) -> {
         return p_193992_0_.getRecipes().stream().map((p_200237_0_) -> {
            return IRegistry.field_212630_s.getKey(p_200237_0_.getRecipeOutput().getItem());
         }).collect(Collectors.toList());
      });
      this.searchTreeManager.register(SearchTreeManager.ITEMS, searchtree);
      this.searchTreeManager.register(SearchTreeManager.RECIPES, searchtree1);
   }

   private void disableVSyncAfterGlError(int error, long description) {
      this.gameSettings.enableVsync = false;
      this.gameSettings.saveOptions();
   }

   private static boolean isJvm64bit() {
      String[] astring = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

      for(String s : astring) {
         String s1 = System.getProperty(s);
         if (s1 != null && s1.contains("64")) {
            return true;
         }
      }

      return false;
   }

   public Framebuffer getFramebuffer() {
      return this.framebuffer;
   }

   /**
    * Gets the version that Minecraft was launched under (the name of a version JSON). Specified via the <code>--
    * version</code> flag.
    */
   public String getVersion() {
      return this.launchedVersion;
   }

   /**
    * Gets the type of version that Minecraft was launched under (as specified in the version JSON). Specified via the
    * <code>--versionType</code> flag.
    */
   public String getVersionType() {
      return this.versionType;
   }

   private void startTimerHackThread() {
      Thread thread = new Thread("Timer hack thread") {
         public void run() {
            while(Minecraft.this.running) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException var2) {
                  ;
               }
            }

         }
      };
      thread.setDaemon(true);
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
   }

   public void crashed(CrashReport crash) {
      this.hasCrashed = true;
      this.crashReporter = crash;
   }

   /**
    * Wrapper around displayCrashReportInternal
    */
   public void displayCrashReport(CrashReport crashReportIn) {
      File file1 = new File(getInstance().gameDir, "crash-reports");
      File file2 = new File(file1, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
      Bootstrap.printToSYSOUT(crashReportIn.getCompleteReport());
      if (crashReportIn.getFile() != null) {
         Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReportIn.getFile());
         net.minecraftforge.fml.server.ServerLifecycleHooks.handleExit(-1);
      } else if (crashReportIn.saveToFile(file2)) {
         Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
         System.exit(-1);
      } else {
         Bootstrap.printToSYSOUT("#@?@# Game crashed! Crash report could not be saved. #@?@#");
         System.exit(-2);
      }

   }

   public boolean getForceUnicodeFont() {
      return this.gameSettings.forceUnicodeFont;
   }

   @Deprecated // Forge: Use selective refreshResources method in FMLClientHandler
   public void refreshResources() {
      this.resourcePackRepository.reloadPacksFromFinders();
      List<IResourcePack> list = this.resourcePackRepository.getPackInfos().stream().map(ResourcePackInfo::getResourcePack).collect(Collectors.toList());
      if (this.integratedServer != null) {
         this.integratedServer.reload();
      }

      try {
         this.resourceManager.reload(list);
      } catch (RuntimeException runtimeexception) {
         LOGGER.info("Caught error stitching, removing all assigned resourcepacks", (Throwable)runtimeexception);
         this.resourcePackRepository.func_198985_a(Collections.emptyList());
         List<IResourcePack> list1 = this.resourcePackRepository.getPackInfos().stream().map(ResourcePackInfo::getResourcePack).collect(Collectors.toList());
         this.resourceManager.reload(list1);
         this.gameSettings.resourcePacks.clear();
         this.gameSettings.incompatibleResourcePacks.clear();
         this.gameSettings.saveOptions();
      }

      this.languageManager.parseLanguageMetadata(list);
      if (this.renderGlobal != null) {
         this.renderGlobal.loadRenderers();
      }

   }

   private void initMainWindow() {
      this.mainWindow.setupOverlayRendering();
      this.currentScreen.render(0, 0, 0.0F);
      this.mainWindow.update(false);
   }

   /**
    * Draw with the WorldRenderer
    */
   public void draw(int posX, int posY, int texU, int texV, int width, int height, int red, int green, int blue, int alpha) {
      BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      float f = 0.00390625F;
      float f1 = 0.00390625F;
      bufferbuilder.pos((double)posX, (double)(posY + height), 0.0D).tex((double)((float)texU * 0.00390625F), (double)((float)(texV + height) * 0.00390625F)).color(red, green, blue, alpha).endVertex();
      bufferbuilder.pos((double)(posX + width), (double)(posY + height), 0.0D).tex((double)((float)(texU + width) * 0.00390625F), (double)((float)(texV + height) * 0.00390625F)).color(red, green, blue, alpha).endVertex();
      bufferbuilder.pos((double)(posX + width), (double)posY, 0.0D).tex((double)((float)(texU + width) * 0.00390625F), (double)((float)texV * 0.00390625F)).color(red, green, blue, alpha).endVertex();
      bufferbuilder.pos((double)posX, (double)posY, 0.0D).tex((double)((float)texU * 0.00390625F), (double)((float)texV * 0.00390625F)).color(red, green, blue, alpha).endVertex();
      Tessellator.getInstance().draw();
   }

   /**
    * Returns the save loader that is currently being used
    */
   public ISaveFormat getSaveLoader() {
      return this.saveLoader;
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.currentScreen;
   }

   /**
    * Sets the argument GuiScreen as the main (topmost visible) screen.
    *  
    * <p><strong>WARNING</strong>: This method is not thread-safe. Opening GUIs from a thread other than the main thread
    * may cause many different issues, including the GUI being rendered before it has initialized (leading to unusual
    * crashes). If on a thread other than the main thread, use {@link #addScheduledTask}:
    *  
    * <pre>
    * minecraft.addScheduledTask(() -> minecraft.displayGuiScreen(gui));
    * </pre>
    */
   public void displayGuiScreen(@Nullable GuiScreen guiScreenIn) {
      if (guiScreenIn == null && this.world == null) {
         guiScreenIn = new GuiMainMenu();
      } else if (guiScreenIn == null && this.player.getHealth() <= 0.0F) {
         guiScreenIn = new GuiGameOver((ITextComponent)null);
      }

      GuiScreen old = this.currentScreen;
      net.minecraftforge.client.event.GuiOpenEvent event = new net.minecraftforge.client.event.GuiOpenEvent(guiScreenIn);
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return;

      guiScreenIn = event.getGui();
      if (old != null && guiScreenIn != old)
         old.onGuiClosed();

      if (guiScreenIn instanceof GuiMainMenu || guiScreenIn instanceof GuiMultiplayer) {
         this.gameSettings.showDebugInfo = false;
         this.ingameGUI.getChatGUI().clearChatMessages(true);
      }

      this.currentScreen = guiScreenIn;
      if (guiScreenIn != null) {
         this.mouseHelper.ungrabMouse();
         KeyBinding.unPressAllKeys();
         guiScreenIn.setWorldAndResolution(this, this.mainWindow.getScaledWidth(), this.mainWindow.getScaledHeight());
         this.skipRenderWorld = false;
      } else {
         this.soundHandler.resume();
         this.mouseHelper.grabMouse();
      }

   }

   /**
    * Shuts down the minecraft applet by stopping the resource downloads, and clearing up GL stuff; called when the
    * application (or web page) is exited.
    */
   public void shutdownMinecraftApplet() {
      try {
         LOGGER.info("Stopping!");

         try {
            this.loadWorld((WorldClient)null);
         } catch (Throwable var5) {
            ;
         }

         if (this.currentScreen != null) {
            this.currentScreen.onGuiClosed();
         }

         this.textureMap.clear();
         this.fontRenderer.close();
         this.entityRenderer.close();
         this.renderGlobal.close();
         this.soundHandler.unloadSounds();
      } finally {
         this.virtualScreen.close();
         this.mainWindow.close();
         if (!this.hasCrashed) {
            System.exit(0);
         }

      }

      System.gc();
   }

   private void runGameLoop(boolean renderWorldIn) {
      this.mainWindow.setRenderPhase("Pre render");
      long i = Util.nanoTime();
      this.profiler.startSection("root");
      if (GLFW.glfwWindowShouldClose(this.mainWindow.getHandle())) {
         this.shutdown();
      }

      if (renderWorldIn) {
         this.timer.updateTimer(Util.milliTime());
         this.profiler.startSection("scheduledExecutables");

         FutureTask<?> futuretask;
         while((futuretask = this.scheduledTasks.poll()) != null) {
            Util.runTask(futuretask, LOGGER);
         }

         this.profiler.endSection();
      }

      long l = Util.nanoTime();
      if (renderWorldIn) {
         this.profiler.startSection("tick");

         for(int j = 0; j < Math.min(10, this.timer.elapsedTicks); ++j) {
            this.runTick();
         }
      }

      this.mouseHelper.updatePlayerLook();
      this.mainWindow.setRenderPhase("Render");
      GLFW.glfwPollEvents();
      long i1 = Util.nanoTime() - l;
      this.profiler.endStartSection("sound");
      this.soundHandler.setListener(this.getRenderViewEntity(), this.timer.renderPartialTicks); //Forge: MC-46445 Spectator mode particles and sounds computed from where you have been before
      this.profiler.endSection();
      this.profiler.startSection("render");
      GlStateManager.pushMatrix();
      GlStateManager.clear(16640);
      this.framebuffer.bindFramebuffer(true);
      this.profiler.startSection("display");
      GlStateManager.enableTexture2D();
      this.profiler.endSection();
      if (!this.skipRenderWorld) {
         net.minecraftforge.fml.hooks.BasicEventHooks.onRenderTickStart(this.timer.renderPartialTicks);
         this.profiler.endStartSection("gameRenderer");
         this.entityRenderer.updateCameraAndRender(this.isGamePaused ? this.renderPartialTicksPaused : this.timer.renderPartialTicks, i, renderWorldIn);
         this.profiler.endStartSection("toasts");
         this.toastGui.render();
         this.profiler.endSection();
         net.minecraftforge.fml.hooks.BasicEventHooks.onRenderTickEnd(this.timer.renderPartialTicks);
      }

      this.profiler.endSection();
      if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart && !this.gameSettings.hideGUI) {
         this.profiler.startProfiling(this.timer.elapsedTicks);
         this.drawProfiler();
      } else {
         this.profiler.stopProfiling();
      }

      this.framebuffer.unbindFramebuffer();
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      this.framebuffer.framebufferRender(this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight());
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      this.entityRenderer.renderStreamIndicator(this.timer.renderPartialTicks);
      GlStateManager.popMatrix();
      this.profiler.startSection("root");
      this.mainWindow.update(true);
      Thread.yield();
      this.mainWindow.setRenderPhase("Post render");
      ++this.fpsCounter;
      boolean flag = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.integratedServer.getPublic();
      if (this.isGamePaused != flag) {
         if (this.isGamePaused) {
            this.renderPartialTicksPaused = this.timer.renderPartialTicks;
         } else {
            this.timer.renderPartialTicks = this.renderPartialTicksPaused;
         }

         this.isGamePaused = flag;
      }

      long k = Util.nanoTime();
      this.frameTimer.addFrame(k - this.startNanoTime);
      this.startNanoTime = k;

      while(Util.milliTime() >= this.debugUpdateTime + 1000L) {
         debugFPS = this.fpsCounter;
         this.debug = String.format("%d fps (%d chunk update%s) T: %s%s%s%s%s", debugFPS, RenderChunk.renderChunksUpdated, RenderChunk.renderChunksUpdated == 1 ? "" : "s", (double)this.gameSettings.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() ? "inf" : this.gameSettings.limitFramerate, this.gameSettings.enableVsync ? " vsync" : "", this.gameSettings.fancyGraphics ? "" : " fast", this.gameSettings.clouds == 0 ? "" : (this.gameSettings.clouds == 1 ? " fast-clouds" : " fancy-clouds"), OpenGlHelper.useVbo() ? " vbo" : "");
         RenderChunk.renderChunksUpdated = 0;
         this.debugUpdateTime += 1000L;
         this.fpsCounter = 0;
         this.snooper.addMemoryStatsToSnooper();
         if (!this.snooper.isSnooperRunning()) {
            this.snooper.start();
         }
      }

      this.profiler.endSection();
   }

   /**
    * Attempts to free as much memory as possible, including leaving the world and running the garbage collector.
    */
   public void freeMemory() {
      try {
         memoryReserve = new byte[0];
         this.renderGlobal.deleteAllDisplayLists();
      } catch (Throwable var3) {
         ;
      }

      try {
         System.gc();
         this.loadWorld((WorldClient)null, new GuiDirtMessageScreen(I18n.format("menu.savingLevel")));
      } catch (Throwable var2) {
         ;
      }

      System.gc();
   }

   /**
    * Update debugProfilerName in response to number keys in debug screen
    */
   void updateDebugProfilerName(int keyCount) {
      List<Profiler.Result> list = this.profiler.getProfilingData(this.debugProfilerName);
      if (!list.isEmpty()) {
         Profiler.Result profiler$result = list.remove(0);
         if (keyCount == 0) {
            if (!profiler$result.profilerName.isEmpty()) {
               int i = this.debugProfilerName.lastIndexOf(46);
               if (i >= 0) {
                  this.debugProfilerName = this.debugProfilerName.substring(0, i);
               }
            }
         } else {
            --keyCount;
            if (keyCount < list.size() && !"unspecified".equals((list.get(keyCount)).profilerName)) {
               if (!this.debugProfilerName.isEmpty()) {
                  this.debugProfilerName = this.debugProfilerName + ".";
               }

               this.debugProfilerName = this.debugProfilerName + (list.get(keyCount)).profilerName;
            }
         }

      }
   }

   private void drawProfiler() {
      if (this.profiler.isProfiling()) {
         List<Profiler.Result> list = this.profiler.getProfilingData(this.debugProfilerName);
         Profiler.Result profiler$result = list.remove(0);
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.enableColorMaterial();
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0D, (double)this.mainWindow.getFramebufferWidth(), (double)this.mainWindow.getFramebufferHeight(), 0.0D, 1000.0D, 3000.0D);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
         GlStateManager.lineWidth(1.0F);
         GlStateManager.disableTexture2D();
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         int i = 160;
         int j = this.mainWindow.getFramebufferWidth() - 160 - 10;
         int k = this.mainWindow.getFramebufferHeight() - 320;
         GlStateManager.enableBlend();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
         bufferbuilder.pos((double)((float)j - 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
         bufferbuilder.pos((double)((float)j - 176.0F), (double)(k + 320), 0.0D).color(200, 0, 0, 0).endVertex();
         bufferbuilder.pos((double)((float)j + 176.0F), (double)(k + 320), 0.0D).color(200, 0, 0, 0).endVertex();
         bufferbuilder.pos((double)((float)j + 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
         tessellator.draw();
         GlStateManager.disableBlend();
         double d0 = 0.0D;

         for(int l = 0; l < list.size(); ++l) {
            Profiler.Result profiler$result1 = list.get(l);
            int i1 = MathHelper.floor(profiler$result1.usePercentage / 4.0D) + 1;
            bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
            int j1 = profiler$result1.getColor();
            int k1 = j1 >> 16 & 255;
            int l1 = j1 >> 8 & 255;
            int i2 = j1 & 255;
            bufferbuilder.pos((double)j, (double)k, 0.0D).color(k1, l1, i2, 255).endVertex();

            for(int j2 = i1; j2 >= 0; --j2) {
               float f = (float)((d0 + profiler$result1.usePercentage * (double)j2 / (double)i1) * (double)((float)Math.PI * 2F) / 100.0D);
               float f1 = MathHelper.sin(f) * 160.0F;
               float f2 = MathHelper.cos(f) * 160.0F * 0.5F;
               bufferbuilder.pos((double)((float)j + f1), (double)((float)k - f2), 0.0D).color(k1, l1, i2, 255).endVertex();
            }

            tessellator.draw();
            bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for(int i3 = i1; i3 >= 0; --i3) {
               float f3 = (float)((d0 + profiler$result1.usePercentage * (double)i3 / (double)i1) * (double)((float)Math.PI * 2F) / 100.0D);
               float f4 = MathHelper.sin(f3) * 160.0F;
               float f5 = MathHelper.cos(f3) * 160.0F * 0.5F;
               bufferbuilder.pos((double)((float)j + f4), (double)((float)k - f5), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
               bufferbuilder.pos((double)((float)j + f4), (double)((float)k - f5 + 10.0F), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
            }

            tessellator.draw();
            d0 += profiler$result1.usePercentage;
         }

         DecimalFormat decimalformat = new DecimalFormat("##0.00");
         decimalformat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
         GlStateManager.enableTexture2D();
         String s = "";
         if (!"unspecified".equals(profiler$result.profilerName)) {
            s = s + "[0] ";
         }

         if (profiler$result.profilerName.isEmpty()) {
            s = s + "ROOT ";
         } else {
            s = s + profiler$result.profilerName + ' ';
         }

         int l2 = 16777215;
         this.fontRenderer.drawStringWithShadow(s, (float)(j - 160), (float)(k - 80 - 16), 16777215);
         s = decimalformat.format(profiler$result.totalUsePercentage) + "%";
         this.fontRenderer.drawStringWithShadow(s, (float)(j + 160 - this.fontRenderer.getStringWidth(s)), (float)(k - 80 - 16), 16777215);

         for(int k2 = 0; k2 < list.size(); ++k2) {
            Profiler.Result profiler$result2 = list.get(k2);
            StringBuilder stringbuilder = new StringBuilder();
            if ("unspecified".equals(profiler$result2.profilerName)) {
               stringbuilder.append("[?] ");
            } else {
               stringbuilder.append("[").append(k2 + 1).append("] ");
            }

            String s1 = stringbuilder.append(profiler$result2.profilerName).toString();
            this.fontRenderer.drawStringWithShadow(s1, (float)(j - 160), (float)(k + 80 + k2 * 8 + 20), profiler$result2.getColor());
            s1 = decimalformat.format(profiler$result2.usePercentage) + "%";
            this.fontRenderer.drawStringWithShadow(s1, (float)(j + 160 - 50 - this.fontRenderer.getStringWidth(s1)), (float)(k + 80 + k2 * 8 + 20), profiler$result2.getColor());
            s1 = decimalformat.format(profiler$result2.totalUsePercentage) + "%";
            this.fontRenderer.drawStringWithShadow(s1, (float)(j + 160 - this.fontRenderer.getStringWidth(s1)), (float)(k + 80 + k2 * 8 + 20), profiler$result2.getColor());
         }

      }
   }

   /**
    * Called when the window is closing. Sets 'running' to false which allows the game loop to exit cleanly.
    */
   public void shutdown() {
      this.running = false;
   }

   /**
    * Displays the ingame menu
    */
   public void displayInGameMenu() {
      if (this.currentScreen == null) {
         this.displayGuiScreen(new GuiIngameMenu());
         if (this.isSingleplayer() && !this.integratedServer.getPublic()) {
            this.soundHandler.pause();
         }

      }
   }

   private void sendClickBlockToController(boolean leftClick) {
      if (!leftClick) {
         this.leftClickCounter = 0;
      }

      if (this.leftClickCounter <= 0 && !this.player.isHandActive()) {
         if (leftClick && this.objectMouseOver != null && this.objectMouseOver.type == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = this.objectMouseOver.getBlockPos();
            if (!this.world.isAirBlock(blockpos) && this.playerController.onPlayerDamageBlock(blockpos, this.objectMouseOver.sideHit)) {
               this.particles.addBlockHitEffects(blockpos, this.objectMouseOver);
               this.player.swingArm(EnumHand.MAIN_HAND);
            }

         } else {
            this.playerController.resetBlockRemoving();
         }
      }
   }

   private void clickMouse() {
      if (this.leftClickCounter <= 0) {
         if (this.objectMouseOver == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.playerController.isNotCreative()) {
               this.leftClickCounter = 10;
            }

         } else if (!this.player.isRowingBoat()) {
            switch(this.objectMouseOver.type) {
            case ENTITY:
               this.playerController.attackEntity(this.player, this.objectMouseOver.entity);
               break;
            case BLOCK:
               BlockPos blockpos = this.objectMouseOver.getBlockPos();
               if (!this.world.isAirBlock(blockpos)) {
                  this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
                  break;
               }
            case MISS:
               if (this.playerController.isNotCreative()) {
                  this.leftClickCounter = 10;
               }

               this.player.resetCooldown();
               net.minecraftforge.common.ForgeHooks.onEmptyLeftClick(this.player);
            }

            this.player.swingArm(EnumHand.MAIN_HAND);
         }
      }
   }

   /**
    * Called when user clicked he's mouse right button (place)
    */
   private void rightClickMouse() {
      if (!this.playerController.getIsHittingBlock()) {
         this.rightClickDelayTimer = 4;
         if (!this.player.isRowingBoat()) {
            if (this.objectMouseOver == null) {
               LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
            }

            for(EnumHand enumhand : EnumHand.values()) {
               ItemStack itemstack = this.player.getHeldItem(enumhand);
               if (this.objectMouseOver != null) {
                  switch(this.objectMouseOver.type) {
                  case ENTITY:
                     if (this.playerController.interactWithEntity(this.player, this.objectMouseOver.entity, this.objectMouseOver, enumhand) == EnumActionResult.SUCCESS) {
                        return;
                     }

                     if (this.playerController.interactWithEntity(this.player, this.objectMouseOver.entity, enumhand) == EnumActionResult.SUCCESS) {
                        return;
                     }
                     break;
                  case BLOCK:
                     BlockPos blockpos = this.objectMouseOver.getBlockPos();
                     if (!this.world.getBlockState(blockpos).isAir(world, blockpos)) {
                        int i = itemstack.getCount();
                        EnumActionResult enumactionresult = this.playerController.processRightClickBlock(this.player, this.world, blockpos, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec, enumhand);
                        if (enumactionresult == EnumActionResult.SUCCESS) {
                           this.player.swingArm(enumhand);
                           if (!itemstack.isEmpty() && (itemstack.getCount() != i || this.playerController.isInCreativeMode())) {
                              this.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                           }

                           return;
                        }

                        if (enumactionresult == EnumActionResult.FAIL) {
                           return;
                        }
                     }
                  }
               }

               if (itemstack.isEmpty() && (this.objectMouseOver == null || this.objectMouseOver.type == RayTraceResult.Type.MISS))
                  net.minecraftforge.common.ForgeHooks.onEmptyClick(this.player, enumhand);

               if (!itemstack.isEmpty() && this.playerController.processRightClick(this.player, this.world, enumhand) == EnumActionResult.SUCCESS) {
                  this.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                  return;
               }
            }

         }
      }
   }

   /**
    * Return the musicTicker's instance
    */
   public MusicTicker getMusicTicker() {
      return this.musicTicker;
   }

   /**
    * Runs the current tick.
    */
   public void runTick() {
      if (this.rightClickDelayTimer > 0) {
         --this.rightClickDelayTimer;
      }

      net.minecraftforge.fml.hooks.BasicEventHooks.onPreClientTick();

      this.profiler.startSection("gui");
      if (!this.isGamePaused) {
         this.ingameGUI.tick();
      }

      this.profiler.endSection();
      this.entityRenderer.getMouseOver(1.0F);
      this.tutorial.onMouseHover(this.world, this.objectMouseOver);
      this.profiler.startSection("gameMode");
      if (!this.isGamePaused && this.world != null) {
         this.playerController.tick();
      }

      this.profiler.endStartSection("textures");
      if (this.world != null) {
         this.textureManager.tick();
      }

      if (this.currentScreen == null && this.player != null) {
         if (this.player.getHealth() <= 0.0F && !(this.currentScreen instanceof GuiGameOver)) {
            this.displayGuiScreen((GuiScreen)null);
         } else if (this.player.isPlayerSleeping() && this.world != null) {
            this.displayGuiScreen(new GuiSleepMP());
         }
      } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.player.isPlayerSleeping()) {
         this.displayGuiScreen((GuiScreen)null);
      }

      if (this.currentScreen != null) {
         this.leftClickCounter = 10000;
      }

      if (this.currentScreen != null) {
         GuiScreen.runOrMakeCrashReport(() -> {
            this.currentScreen.tick();
         }, "Ticking screen", this.currentScreen.getClass().getCanonicalName());
      }

      if (this.currentScreen == null || this.currentScreen.allowUserInput) {
         this.profiler.endStartSection("GLFW events");
         GLFW.glfwPollEvents();
         this.processKeyBinds();
         if (this.leftClickCounter > 0) {
            --this.leftClickCounter;
         }
      }

      if (this.world != null) {
         if (this.player != null) {
            ++this.joinPlayerCounter;
            if (this.joinPlayerCounter == 30) {
               this.joinPlayerCounter = 0;
               this.world.joinEntityInSurroundings(this.player);
            }
         }

         this.profiler.endStartSection("gameRenderer");
         if (!this.isGamePaused) {
            this.entityRenderer.tick();
         }

         this.profiler.endStartSection("levelRenderer");
         if (!this.isGamePaused) {
            this.renderGlobal.tick();
         }

         this.profiler.endStartSection("level");
         if (!this.isGamePaused) {
            if (this.world.getLastLightningBolt() > 0) {
               this.world.setLastLightningBolt(this.world.getLastLightningBolt() - 1);
            }

            this.world.tickEntities();
         }
      } else if (this.entityRenderer.isShaderActive()) {
         this.entityRenderer.stopUseShader();
      }

      if (!this.isGamePaused) {
         this.musicTicker.tick();
         this.soundHandler.tick();
      }

      if (this.world != null) {
         if (!this.isGamePaused) {
            this.world.setAllowedSpawnTypes(this.world.getDifficulty() != EnumDifficulty.PEACEFUL, true);
            this.tutorial.tick();

            try {
               this.world.tick(() -> {
                  return true;
               });
            } catch (Throwable throwable) {
               CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception in world tick");
               if (this.world == null) {
                  CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected level");
                  crashreportcategory.addDetail("Problem", "Level is null!");
               } else {
                  this.world.fillCrashReport(crashreport);
               }

               throw new ReportedException(crashreport);
            }
         }

         this.profiler.endStartSection("animateTick");
         if (!this.isGamePaused && this.world != null) {
            this.world.animateTick(MathHelper.floor(this.player.posX), MathHelper.floor(this.player.posY), MathHelper.floor(this.player.posZ));
         }

         this.profiler.endStartSection("particles");
         if (!this.isGamePaused) {
            this.particles.tick();
         }
      } else if (this.networkManager != null) {
         this.profiler.endStartSection("pendingConnection");
         this.networkManager.tick();
      }

      this.profiler.endStartSection("keyboard");
      this.keyboardListener.tick();
      this.profiler.endSection();

      net.minecraftforge.fml.hooks.BasicEventHooks.onPostClientTick();
   }

   private void processKeyBinds() {
      for(; this.gameSettings.keyBindTogglePerspective.isPressed(); this.renderGlobal.setDisplayListEntitiesDirty()) {
         ++this.gameSettings.thirdPersonView;
         if (this.gameSettings.thirdPersonView > 2) {
            this.gameSettings.thirdPersonView = 0;
         }

         if (this.gameSettings.thirdPersonView == 0) {
            this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
         } else if (this.gameSettings.thirdPersonView == 1) {
            this.entityRenderer.loadEntityShader((Entity)null);
         }
      }

      while(this.gameSettings.keyBindSmoothCamera.isPressed()) {
         this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
      }

      for(int i = 0; i < 9; ++i) {
         boolean flag = this.gameSettings.keyBindSaveToolbar.isKeyDown();
         boolean flag1 = this.gameSettings.keyBindLoadToolbar.isKeyDown();
         if (this.gameSettings.keyBindsHotbar[i].isPressed()) {
            if (this.player.isSpectator()) {
               this.ingameGUI.getSpectatorGui().onHotbarSelected(i);
            } else if (!this.player.isCreative() || this.currentScreen != null || !flag1 && !flag) {
               this.player.inventory.currentItem = i;
            } else {
               GuiContainerCreative.handleHotbarSnapshots(this, i, flag1, flag);
            }
         }
      }

      while(this.gameSettings.keyBindInventory.isPressed()) {
         if (this.playerController.isRidingHorse()) {
            this.player.sendHorseInventory();
         } else {
            this.tutorial.openInventory();
            this.displayGuiScreen(new GuiInventory(this.player));
         }
      }

      while(this.gameSettings.keyBindAdvancements.isPressed()) {
         this.displayGuiScreen(new GuiScreenAdvancements(this.player.connection.getAdvancementManager()));
      }

      while(this.gameSettings.keyBindSwapHands.isPressed()) {
         if (!this.player.isSpectator()) {
            this.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, EnumFacing.DOWN));
         }
      }

      while(this.gameSettings.keyBindDrop.isPressed()) {
         if (!this.player.isSpectator()) {
            this.player.dropItem(GuiScreen.isCtrlKeyDown());
         }
      }

      boolean flag2 = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;
      if (flag2) {
         while(this.gameSettings.keyBindChat.isPressed()) {
            this.displayGuiScreen(new GuiChat());
         }

         if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed()) {
            this.displayGuiScreen(new GuiChat("/"));
         }
      }

      if (this.player.isHandActive()) {
         if (!this.gameSettings.keyBindUseItem.isKeyDown()) {
            this.playerController.onStoppedUsingItem(this.player);
         }

         while(this.gameSettings.keyBindAttack.isPressed()) {
            ;
         }

         while(this.gameSettings.keyBindUseItem.isPressed()) {
            ;
         }

         while(this.gameSettings.keyBindPickBlock.isPressed()) {
            ;
         }
      } else {
         while(this.gameSettings.keyBindAttack.isPressed()) {
            this.clickMouse();
         }

         while(this.gameSettings.keyBindUseItem.isPressed()) {
            this.rightClickMouse();
         }

         while(this.gameSettings.keyBindPickBlock.isPressed()) {
            this.middleClickMouse();
         }
      }

      if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.player.isHandActive()) {
         this.rightClickMouse();
      }

      this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.mouseHelper.isMouseGrabbed());
   }

   /**
    * Arguments: World foldername,  World ingame name, WorldSettings
    */
   public void launchIntegratedServer(String folderName, String worldName, @Nullable WorldSettings worldSettingsIn) {
      this.loadWorld((WorldClient)null);
      System.gc();
      ISaveHandler isavehandler = this.saveLoader.getSaveLoader(folderName, (MinecraftServer)null);
      WorldInfo worldinfo = isavehandler.loadWorldInfo();
      if (worldinfo == null && worldSettingsIn != null) {
         worldinfo = new WorldInfo(worldSettingsIn, folderName);
         isavehandler.saveWorldInfo(worldinfo);
      }

      if (worldSettingsIn == null) {
         worldSettingsIn = new WorldSettings(worldinfo);
      }

      try {
         YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString());
         MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
         GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
         PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.gameDir, MinecraftServer.USER_CACHE_FILE.getName()));
         TileEntitySkull.setProfileCache(playerprofilecache);
         TileEntitySkull.setSessionService(minecraftsessionservice);
         PlayerProfileCache.setOnlineMode(false);
         this.integratedServer = new IntegratedServer(this, folderName, worldName, worldSettingsIn, yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, playerprofilecache);
         this.integratedServer.startServerThread();
         this.integratedServerIsRunning = true;
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
         crashreportcategory.addDetail("Level ID", folderName);
         crashreportcategory.addDetail("Level Name", worldName);
         throw new ReportedException(crashreport);
      }

      GuiScreenWorking guiscreenworking = new GuiScreenWorking();
      this.displayGuiScreen(guiscreenworking);
      guiscreenworking.displaySavingString(new TextComponentTranslation("menu.loadingLevel"));

      while(!this.integratedServer.serverIsInRunLoop()) {
         ITextComponent itextcomponent = this.integratedServer.getUserMessage();
         if (itextcomponent != null) {
            ITextComponent itextcomponent1 = this.integratedServer.getCurrentTask();
            if (itextcomponent1 != null) {
               guiscreenworking.displayLoadingString(itextcomponent1);
               guiscreenworking.setLoadingProgress(this.integratedServer.getPercentDone());
            } else {
               guiscreenworking.displayLoadingString(itextcomponent);
            }
         } else {
            guiscreenworking.displayLoadingString(new TextComponentString(""));
         }

         this.runGameLoop(false);

         try {
            Thread.sleep(200L);
         } catch (InterruptedException var10) {
            ;
         }

         if (this.hasCrashed && this.crashReporter != null) {
            this.displayCrashReport(this.crashReporter);
            return;
         }
      }

      SocketAddress socketaddress = this.integratedServer.getNetworkSystem().addLocalEndpoint();
      NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
      networkmanager.setNetHandler(new NetHandlerLoginClient(networkmanager, this, (GuiScreen)null, (p_209507_0_) -> {
      }));
      net.minecraftforge.fml.network.NetworkHooks.registerClientLoginChannel(networkmanager);
      networkmanager.sendPacket(new CPacketHandshake(socketaddress.toString(), 0, EnumConnectionState.LOGIN));
      com.mojang.authlib.GameProfile gameProfile = this.getSession().getProfile();
      if (!this.getSession().hasCachedProperties()) {
         gameProfile = sessionService.fillProfileProperties(gameProfile, true); //Forge: Fill profile properties upon game load. Fixes MC-52974.
         this.getSession().setProperties(gameProfile.getProperties());
      }
      networkmanager.sendPacket(new CPacketLoginStart(gameProfile));
      this.networkManager = networkmanager;
   }

   /**
    * unloads the current world first
    */
   public void loadWorld(@Nullable WorldClient worldClientIn) {
      GuiScreenWorking guiscreenworking = new GuiScreenWorking();
      if (worldClientIn != null) {
         guiscreenworking.displaySavingString(new TextComponentTranslation("connect.joining"));
      }

      this.loadWorld(worldClientIn, guiscreenworking);
   }

   public void loadWorld(@Nullable WorldClient worldIn, GuiScreen loadingScreen) {
      if (world != null) net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(world));
      if (worldIn == null) {
         NetHandlerPlayClient nethandlerplayclient = this.getConnection();
         if (nethandlerplayclient != null) {
            this.scheduledTasks.clear();
            nethandlerplayclient.cleanup();
         }

         this.integratedServer = null;
         this.entityRenderer.resetData();
         this.playerController = null;
         NarratorChatListener.INSTANCE.clear();
      }

      this.musicTicker.stop();
      this.soundHandler.stop();
      this.renderViewEntity = null;
      this.networkManager = null;
      this.displayGuiScreen(loadingScreen);
      this.runGameLoop(false);
      if (worldIn == null && this.world != null) {
         this.packFinder.clearResourcePack();
         this.ingameGUI.resetPlayersOverlayFooterHeader();
         this.setServerData((ServerData)null);
         this.integratedServerIsRunning = false;
      }

      this.world = worldIn;
      if (this.renderGlobal != null) {
         this.renderGlobal.setWorldAndLoadRenderers(worldIn);
      }

      if (this.particles != null) {
         this.particles.clearEffects(worldIn);
      }

      TileEntityRendererDispatcher.instance.setWorld(worldIn);
      net.minecraftforge.client.MinecraftForgeClient.clearRenderCache();
      if (worldIn != null) {
         if (!this.integratedServerIsRunning) {
            AuthenticationService authenticationservice = new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString());
            MinecraftSessionService minecraftsessionservice = authenticationservice.createMinecraftSessionService();
            GameProfileRepository gameprofilerepository = authenticationservice.createProfileRepository();
            PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.gameDir, MinecraftServer.USER_CACHE_FILE.getName()));
            TileEntitySkull.setProfileCache(playerprofilecache);
            TileEntitySkull.setSessionService(minecraftsessionservice);
            PlayerProfileCache.setOnlineMode(false);
         }

         if (this.player == null) {
            this.player = this.playerController.createPlayer(worldIn, new StatisticsManager(), new RecipeBookClient(worldIn.getRecipeManager()));
            this.playerController.flipPlayer(this.player);
            if (this.integratedServer != null) {
               this.integratedServer.setPlayerUuid(this.player.getUniqueID());
            }
         }

         this.player.preparePlayerToSpawn();
         worldIn.spawnEntity(this.player);
         this.player.movementInput = new MovementInputFromOptions(this.gameSettings);
         this.playerController.setPlayerCapabilities(this.player);
         this.renderViewEntity = this.player;
      } else {
         this.player = null;
      }

      System.gc();
   }

   public void func_212315_a(DimensionType p_212315_1_) {
      this.world.setInitialSpawnLocation();
      this.world.removeAllEntities();
      int i = 0;
      String s = null;
      if (this.player != null) {
         i = this.player.getEntityId();
         this.world.removeEntity(this.player);
         s = this.player.getServerBrand();
      }

      this.renderViewEntity = null;
      EntityPlayerSP entityplayersp = this.player;
      this.player = this.playerController.createPlayer(this.world, this.player == null ? new StatisticsManager() : this.player.getStats(), this.player == null ? new RecipeBookClient(new RecipeManager()) : this.player.getRecipeBook());
      this.player.getDataManager().setEntryValues(entityplayersp.getDataManager().getAll());
      this.player.updateSyncFields(entityplayersp); // Forge: fix MC-10657
      this.player.dimension = p_212315_1_;
      this.renderViewEntity = this.player;
      this.player.preparePlayerToSpawn();
      this.player.setServerBrand(s);
      this.world.spawnEntity(this.player);
      this.playerController.flipPlayer(this.player);
      this.player.movementInput = new MovementInputFromOptions(this.gameSettings);
      this.player.setEntityId(i);
      this.playerController.setPlayerCapabilities(this.player);
      this.player.setReducedDebug(entityplayersp.hasReducedDebug());
      if (this.currentScreen instanceof GuiGameOver) {
         this.displayGuiScreen((GuiScreen)null);
      }

   }

   /**
    * Gets whether this is a demo or not.
    */
   public final boolean isDemo() {
      return this.isDemo;
   }

   @Nullable
   public NetHandlerPlayClient getConnection() {
      return this.player == null ? null : this.player.connection;
   }

   public static boolean isGuiEnabled() {
      return instance == null || !instance.gameSettings.hideGUI;
   }

   public static boolean isFancyGraphicsEnabled() {
      return instance != null && instance.gameSettings.fancyGraphics;
   }

   /**
    * Returns if ambient occlusion is enabled
    */
   public static boolean isAmbientOcclusionEnabled() {
      return instance != null && instance.gameSettings.ambientOcclusion != 0;
   }

   /**
    * Called when user clicked he's mouse middle button (pick block)
    */
   private void middleClickMouse() {
      if (this.objectMouseOver != null && this.objectMouseOver.type != RayTraceResult.Type.MISS) {
         net.minecraftforge.common.ForgeHooks.onPickBlock(this.objectMouseOver, this.player, this.world);
         // We delete this code wholly instead of commenting it out, to make sure we detect changes in it between MC versions
      }
   }

   public ItemStack storeTEInStack(ItemStack stack, TileEntity te) {
      NBTTagCompound nbttagcompound = te.write(new NBTTagCompound());
      if (stack.getItem() instanceof ItemSkull && nbttagcompound.hasKey("Owner")) {
         NBTTagCompound nbttagcompound2 = nbttagcompound.getCompound("Owner");
         stack.getOrCreateTag().setTag("SkullOwner", nbttagcompound2);
         return stack;
      } else {
         stack.setTagInfo("BlockEntityTag", nbttagcompound);
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         NBTTagList nbttaglist = new NBTTagList();
         nbttaglist.add((INBTBase)(new NBTTagString("(+NBT)")));
         nbttagcompound1.setTag("Lore", nbttaglist);
         stack.setTagInfo("display", nbttagcompound1);
         return stack;
      }
   }

   /**
    * adds core server Info (GL version , Texture pack, isModded, type), and the worldInfo to the crash report
    */
   public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash) {
      CrashReportCategory crashreportcategory = theCrash.getCategory();
      crashreportcategory.addDetail("Launched Version", () -> {
         return this.launchedVersion;
      });
      crashreportcategory.addDetail("LWJGL", Version::getVersion);
      crashreportcategory.addDetail("OpenGL", () -> {
         return GLFW.glfwGetCurrentContext() == 0L ? "NO CONTEXT" : GlStateManager.getString(7937) + " GL version " + GlStateManager.getString(7938) + ", " + GlStateManager.getString(7936);
      });
      crashreportcategory.addDetail("GL Caps", OpenGlHelper::getLogText);
      crashreportcategory.addDetail("Using VBOs", () -> {
         return this.gameSettings.useVbo ? "Yes" : "No";
      });
      crashreportcategory.addDetail("Is Modded", () -> {
         String s = ClientBrandRetriever.getClientModName();
         if (!"vanilla".equals(s)) {
            return "Definitely; Client brand changed to '" + s + "'";
         } else {
            return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.";
         }
      });
      crashreportcategory.addDetail("Type", "Client (map_client.txt)");
      crashreportcategory.addDetail("Resource Packs", () -> {
         StringBuilder stringbuilder = new StringBuilder();

         for(String s : this.gameSettings.resourcePacks) {
            if (stringbuilder.length() > 0) {
               stringbuilder.append(", ");
            }

            stringbuilder.append(s);
            if (this.gameSettings.incompatibleResourcePacks.contains(s)) {
               stringbuilder.append(" (incompatible)");
            }
         }

         return stringbuilder.toString();
      });
      crashreportcategory.addDetail("Current Language", () -> {
         return this.languageManager.getCurrentLanguage().toString();
      });
      crashreportcategory.addDetail("Profiler Position", () -> {
         return this.profiler.isProfiling() ? this.profiler.getNameOfLastSection() : "N/A (disabled)";
      });
      crashreportcategory.addDetail("CPU", OpenGlHelper::getCpu);
      if (this.world != null) {
         this.world.fillCrashReport(theCrash);
      }

      return theCrash;
   }

   /**
    * Return the singleton Minecraft instance for the game
    */
   public static Minecraft getInstance() {
      return instance;
   }

   @Deprecated // Forge: Use selective scheduleResourceRefresh method in FMLClientHandler
   public ListenableFuture<Object> scheduleResourcesRefresh() {
      return this.addScheduledTask(this::refreshResources);
   }

   public void addServerStatsToSnooper(Snooper playerSnooper) {
      playerSnooper.addClientStat("fps", debugFPS);
      playerSnooper.addClientStat("vsync_enabled", this.gameSettings.enableVsync);
      long i = GLFW.glfwGetWindowMonitor(this.mainWindow.getHandle());
      if (i == 0L) {
         i = GLFW.glfwGetPrimaryMonitor();
      }

      playerSnooper.addClientStat("display_frequency", GLFW.glfwGetVideoMode(i).refreshRate());
      playerSnooper.addClientStat("display_type", this.mainWindow.isFullscreen() ? "fullscreen" : "windowed");
      playerSnooper.addClientStat("run_time", (Util.milliTime() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
      playerSnooper.addClientStat("current_action", this.getCurrentAction());
      playerSnooper.addClientStat("language", this.gameSettings.language == null ? "en_us" : this.gameSettings.language);
      String s = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
      playerSnooper.addClientStat("endianness", s);
      playerSnooper.addClientStat("subtitles", this.gameSettings.showSubtitles);
      playerSnooper.addClientStat("touch", this.gameSettings.touchscreen ? "touch" : "mouse");
      int j = 0;

      for(ResourcePackInfoClient resourcepackinfoclient : this.resourcePackRepository.getPackInfos()) {
         if (!resourcepackinfoclient.func_195797_g() && !resourcepackinfoclient.func_195798_h()) {
            playerSnooper.addClientStat("resource_pack[" + j++ + "]", resourcepackinfoclient.getName());
         }
      }

      playerSnooper.addClientStat("resource_packs", j);
      if (this.integratedServer != null && this.integratedServer.getSnooper() != null) {
         playerSnooper.addClientStat("snooper_partner", this.integratedServer.getSnooper().getUniqueID());
      }

   }

   /**
    * Return the current action's name
    */
   private String getCurrentAction() {
      if (this.integratedServer != null) {
         return this.integratedServer.getPublic() ? "hosting_lan" : "singleplayer";
      } else if (this.currentServerData != null) {
         return this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer";
      } else {
         return "out_of_game";
      }
   }

   /**
    * Used in the usage snooper.
    */
   public static int getGLMaximumTextureSize() {
      if (cachedMaximumTextureSize == -1) {
         for(int i = 16384; i > 0; i >>= 1) {
            GlStateManager.texImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, (IntBuffer)null);
            int j = GlStateManager.glGetTexLevelParameteri(32868, 0, 4096);
            if (j != 0) {
               cachedMaximumTextureSize = i;
               return i;
            }
         }
      }

      return cachedMaximumTextureSize;
   }

   /**
    * Returns whether snooping is enabled or not.
    */
   public boolean isSnooperEnabled() {
      return this.gameSettings.snooperEnabled;
   }

   /**
    * Set the current ServerData instance.
    */
   public void setServerData(ServerData serverDataIn) {
      this.currentServerData = serverDataIn;
   }

   @Nullable
   public ServerData getCurrentServerData() {
      return this.currentServerData;
   }

   public boolean isIntegratedServerRunning() {
      return this.integratedServerIsRunning;
   }

   /**
    * Returns true if there is only one player playing, and the current server is the integrated one.
    */
   public boolean isSingleplayer() {
      return this.integratedServerIsRunning && this.integratedServer != null;
   }

   /**
    * Returns the currently running integrated server
    */
   @Nullable
   public IntegratedServer getIntegratedServer() {
      return this.integratedServer;
   }

   public static void stopIntegratedServer() {
      if (instance != null) {
         IntegratedServer integratedserver = instance.getIntegratedServer();
         if (integratedserver != null) {
            integratedserver.stopServer();
         }

      }
   }

   /**
    * Returns the PlayerUsageSnooper instance.
    */
   public Snooper getSnooper() {
      return this.snooper;
   }

   public Session getSession() {
      return this.session;
   }

   /**
    * Return the player's GameProfile properties
    */
   public PropertyMap getProfileProperties() {
      if (this.profileProperties.isEmpty()) {
         GameProfile gameprofile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
         this.profileProperties.putAll(gameprofile.getProperties());
      }

      return this.profileProperties;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public TextureManager getTextureManager() {
      return this.textureManager;
   }

   public IResourceManager getResourceManager() {
      return this.resourceManager;
   }

   public ResourcePackList<ResourcePackInfoClient> getResourcePackList() {
      return this.resourcePackRepository;
   }

   public DownloadingPackFinder getPackFinder() {
      return this.packFinder;
   }

   public File getFileResourcePacks() {
      return this.fileResourcepacks;
   }

   public LanguageManager getLanguageManager() {
      return this.languageManager;
   }

   public TextureMap getTextureMap() {
      return this.textureMap;
   }

   public boolean isJava64bit() {
      return this.jvm64bit;
   }

   public boolean isGamePaused() {
      return this.isGamePaused;
   }

   public SoundHandler getSoundHandler() {
      return this.soundHandler;
   }

   public MusicTicker.MusicType getAmbientMusicType() {
      MusicTicker.MusicType type = this.world == null || this.world.dimension == null ? null : this.world.dimension.getMusicType();
      if (type != null) return type;
      if (this.currentScreen instanceof GuiWinGame) {
         return MusicTicker.MusicType.CREDITS;
      } else if (this.player == null) {
         return MusicTicker.MusicType.MENU;
      } else if (this.player.world.dimension instanceof NetherDimension) {
         return MusicTicker.MusicType.NETHER;
      } else if (this.player.world.dimension instanceof EndDimension) {
         return this.ingameGUI.getBossOverlay().shouldPlayEndBossMusic() ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END;
      } else {
         Biome.Category biome$category = this.player.world.getBiome(new BlockPos(this.player.posX, this.player.posY, this.player.posZ)).getCategory();
         if (!this.musicTicker.isPlaying(MusicTicker.MusicType.UNDER_WATER) && (!this.player.canSwim() || this.musicTicker.isPlaying(MusicTicker.MusicType.GAME) || biome$category != Biome.Category.OCEAN && biome$category != Biome.Category.RIVER)) {
            return this.player.abilities.isCreativeMode && this.player.abilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME;
         } else {
            return MusicTicker.MusicType.UNDER_WATER;
         }
      }
   }

   public MinecraftSessionService getSessionService() {
      return this.sessionService;
   }

   public SkinManager getSkinManager() {
      return this.skinManager;
   }

   @Nullable
   public Entity getRenderViewEntity() {
      return this.renderViewEntity;
   }

   public void setRenderViewEntity(Entity viewingEntity) {
      this.renderViewEntity = viewingEntity;
      this.entityRenderer.loadEntityShader(viewingEntity);
   }

   public <V> ListenableFuture<V> addScheduledTask(Callable<V> callableToSchedule) {
      Validate.notNull(callableToSchedule);
      if (this.isCallingFromMinecraftThread()) {
         try {
            return Futures.immediateFuture(callableToSchedule.call());
         } catch (Exception exception) {
            return Futures.immediateFailedCheckedFuture(exception);
         }
      } else {
         ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callableToSchedule);
         this.scheduledTasks.add(listenablefuturetask);
         return listenablefuturetask;
      }
   }

   public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
      Validate.notNull(runnableToSchedule);
      return this.addScheduledTask(Executors.callable(runnableToSchedule));
   }

   public boolean isCallingFromMinecraftThread() {
      return Thread.currentThread() == this.thread;
   }

   public BlockRendererDispatcher getBlockRendererDispatcher() {
      return this.blockRenderDispatcher;
   }

   public RenderManager getRenderManager() {
      return this.renderManager;
   }

   public ItemRenderer getItemRenderer() {
      return this.itemRenderer;
   }

   public FirstPersonRenderer getFirstPersonRenderer() {
      return this.firstPersonRenderer;
   }

   /**
    * Gets the {@link ISearchTree} for the given search tree key, returning null if no such tree exists.
    */
   public <T> ISearchTree<T> getSearchTree(SearchTreeManager.Key<T> key) {
      return this.searchTreeManager.get(key);
   }

   public static int getDebugFPS() {
      return debugFPS;
   }

   /**
    * Return the FrameTimer's instance
    */
   public FrameTimer getFrameTimer() {
      return this.frameTimer;
   }

   /**
    * Return true if the player is connected to a realms server
    */
   public boolean isConnectedToRealms() {
      return this.connectedToRealms;
   }

   /**
    * Set if the player is connected to a realms server
    */
   public void setConnectedToRealms(boolean isConnected) {
      this.connectedToRealms = isConnected;
   }

   public DataFixer getDataFixer() {
      return this.dataFixer;
   }

   public float getRenderPartialTicks() {
      return this.timer.renderPartialTicks;
   }

   public float getTickLength() {
      return this.timer.elapsedPartialTicks;
   }

   public BlockColors getBlockColors() {
      return this.blockColors;
   }

   /**
    * Whether to use reduced debug info
    */
   public boolean isReducedDebug() {
      return this.player != null && this.player.hasReducedDebug() || this.gameSettings.reducedDebugInfo;
   }

   public GuiToast getToastGui() {
      return this.toastGui;
   }

   public Tutorial getTutorial() {
      return this.tutorial;
   }

   public boolean isGameFocused() {
      return this.isWindowFocused;
   }

   public CreativeSettings getCreativeSettings() {
      return this.creativeSettings;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public FontResourceManager getFontResourceManager() {
      return this.fontResourceMananger;
   }

   public ItemColors getItemColors() {
      return this.itemColors;
   }

   public SearchTreeManager getSearchTreeManager() {
      return this.searchTreeManager;
   }
}