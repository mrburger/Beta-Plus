package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldInfo {
   private String versionName;
   private int versionId;
   private boolean versionSnapshot;
   public static final EnumDifficulty DEFAULT_DIFFICULTY = EnumDifficulty.NORMAL;
   /** Holds the seed of the currently world. */
   private long randomSeed;
   private WorldType terrainType = WorldType.DEFAULT;
   private NBTTagCompound generatorOptions = new NBTTagCompound();
   @Nullable
   private String legacyCustomOptions;
   /** The spawn zone position X coordinate. */
   private int spawnX;
   /** The spawn zone position Y coordinate. */
   private int spawnY;
   /** The spawn zone position Z coordinate. */
   private int spawnZ;
   /** Total time for this world. */
   private long gameTime;
   /** The current world time in ticks, ranging from 0 to 23999. */
   private long dayTime;
   /** The last time the player was in this world. */
   private long lastTimePlayed;
   /** The size of entire save of current world on the disk, isn't exactly. */
   private long sizeOnDisk;
   @Nullable
   private final DataFixer fixer;
   private final int field_209227_p;
   private boolean field_209228_q;
   private NBTTagCompound playerTag;
   private int dimension;
   /** The name of the save defined at world creation. */
   private String levelName;
   /** Introduced in beta 1.3, is the save version for future control. */
   private int saveVersion;
   private int clearWeatherTime;
   /** True if it's raining, false otherwise. */
   private boolean raining;
   /** Number of ticks until next rain. */
   private int rainTime;
   /** Is thunderbolts failing now? */
   private boolean thundering;
   /** Number of ticks untils next thunderbolt. */
   private int thunderTime;
   /** The Game Type. */
   private GameType gameType;
   /** Whether the map features (e.g. strongholds) generation is enabled or disabled. */
   private boolean mapFeaturesEnabled;
   /** Hardcore mode flag */
   private boolean hardcore;
   private boolean allowCommands;
   private boolean initialized;
   private EnumDifficulty difficulty;
   private boolean difficultyLocked;
   private double borderCenterX;
   private double borderCenterZ;
   private double borderSize = 6.0E7D;
   private long borderSizeLerpTime;
   private double borderSizeLerpTarget;
   private double borderSafeZone = 5.0D;
   private double borderDamagePerBlock = 0.2D;
   private int borderWarningDistance = 5;
   private int borderWarningTime = 15;
   private final Set<String> disabledDataPacks = Sets.newHashSet();
   private final Set<String> enabledDataPacks = Sets.newLinkedHashSet();
   /**
    * Mapping providing data between different dimensions.
    *  
    * MODDERS: <strong>DO NOT USE</strong>. This map will have a different type of key depending on whether forge is
    * installed or not.
    */
   private final Map<DimensionType, NBTTagCompound> dimensionData = Maps.newIdentityHashMap();
   private NBTTagCompound customBossEvents;
   private final GameRules gameRules = new GameRules();

   protected WorldInfo() {
      this.fixer = null;
      this.field_209227_p = 1631;
      this.func_212242_b(new NBTTagCompound());
   }

   public WorldInfo(NBTTagCompound p_i49564_1_, DataFixer p_i49564_2_, int p_i49564_3_, @Nullable NBTTagCompound p_i49564_4_) {
      this.fixer = p_i49564_2_;
      if (p_i49564_1_.contains("Version", 10)) {
         NBTTagCompound nbttagcompound = p_i49564_1_.getCompound("Version");
         this.versionName = nbttagcompound.getString("Name");
         this.versionId = nbttagcompound.getInt("Id");
         this.versionSnapshot = nbttagcompound.getBoolean("Snapshot");
      }

      this.randomSeed = p_i49564_1_.getLong("RandomSeed");
      if (p_i49564_1_.contains("generatorName", 8)) {
         String s1 = p_i49564_1_.getString("generatorName");
         this.terrainType = WorldType.byName(s1);
         if (this.terrainType == null) {
            this.terrainType = WorldType.DEFAULT;
         } else if (this.terrainType == WorldType.CUSTOMIZED) {
            this.legacyCustomOptions = p_i49564_1_.getString("generatorOptions");
         } else if (this.terrainType.isVersioned()) {
            int i = 0;
            if (p_i49564_1_.contains("generatorVersion", 99)) {
               i = p_i49564_1_.getInt("generatorVersion");
            }

            this.terrainType = this.terrainType.getWorldTypeForGeneratorVersion(i);
         }

         this.func_212242_b(p_i49564_1_.getCompound("generatorOptions"));
      }

      this.gameType = GameType.getByID(p_i49564_1_.getInt("GameType"));
      if (p_i49564_1_.contains("legacy_custom_options", 8)) {
         this.legacyCustomOptions = p_i49564_1_.getString("legacy_custom_options");
      }

      if (p_i49564_1_.contains("MapFeatures", 99)) {
         this.mapFeaturesEnabled = p_i49564_1_.getBoolean("MapFeatures");
      } else {
         this.mapFeaturesEnabled = true;
      }

      this.spawnX = p_i49564_1_.getInt("SpawnX");
      this.spawnY = p_i49564_1_.getInt("SpawnY");
      this.spawnZ = p_i49564_1_.getInt("SpawnZ");
      this.gameTime = p_i49564_1_.getLong("Time");
      if (p_i49564_1_.contains("DayTime", 99)) {
         this.dayTime = p_i49564_1_.getLong("DayTime");
      } else {
         this.dayTime = this.gameTime;
      }

      this.lastTimePlayed = p_i49564_1_.getLong("LastPlayed");
      this.sizeOnDisk = p_i49564_1_.getLong("SizeOnDisk");
      this.levelName = p_i49564_1_.getString("LevelName");
      this.saveVersion = p_i49564_1_.getInt("version");
      this.clearWeatherTime = p_i49564_1_.getInt("clearWeatherTime");
      this.rainTime = p_i49564_1_.getInt("rainTime");
      this.raining = p_i49564_1_.getBoolean("raining");
      this.thunderTime = p_i49564_1_.getInt("thunderTime");
      this.thundering = p_i49564_1_.getBoolean("thundering");
      this.hardcore = p_i49564_1_.getBoolean("hardcore");
      if (p_i49564_1_.contains("initialized", 99)) {
         this.initialized = p_i49564_1_.getBoolean("initialized");
      } else {
         this.initialized = true;
      }

      if (p_i49564_1_.contains("allowCommands", 99)) {
         this.allowCommands = p_i49564_1_.getBoolean("allowCommands");
      } else {
         this.allowCommands = this.gameType == GameType.CREATIVE;
      }

      this.field_209227_p = p_i49564_3_;
      if (p_i49564_4_ != null) {
         this.playerTag = p_i49564_4_;
      }

      if (p_i49564_1_.contains("GameRules", 10)) {
         this.gameRules.read(p_i49564_1_.getCompound("GameRules"));
      }

      if (p_i49564_1_.contains("Difficulty", 99)) {
         this.difficulty = EnumDifficulty.byId(p_i49564_1_.getByte("Difficulty"));
      }

      if (p_i49564_1_.contains("DifficultyLocked", 1)) {
         this.difficultyLocked = p_i49564_1_.getBoolean("DifficultyLocked");
      }

      if (p_i49564_1_.contains("BorderCenterX", 99)) {
         this.borderCenterX = p_i49564_1_.getDouble("BorderCenterX");
      }

      if (p_i49564_1_.contains("BorderCenterZ", 99)) {
         this.borderCenterZ = p_i49564_1_.getDouble("BorderCenterZ");
      }

      if (p_i49564_1_.contains("BorderSize", 99)) {
         this.borderSize = p_i49564_1_.getDouble("BorderSize");
      }

      if (p_i49564_1_.contains("BorderSizeLerpTime", 99)) {
         this.borderSizeLerpTime = p_i49564_1_.getLong("BorderSizeLerpTime");
      }

      if (p_i49564_1_.contains("BorderSizeLerpTarget", 99)) {
         this.borderSizeLerpTarget = p_i49564_1_.getDouble("BorderSizeLerpTarget");
      }

      if (p_i49564_1_.contains("BorderSafeZone", 99)) {
         this.borderSafeZone = p_i49564_1_.getDouble("BorderSafeZone");
      }

      if (p_i49564_1_.contains("BorderDamagePerBlock", 99)) {
         this.borderDamagePerBlock = p_i49564_1_.getDouble("BorderDamagePerBlock");
      }

      if (p_i49564_1_.contains("BorderWarningBlocks", 99)) {
         this.borderWarningDistance = p_i49564_1_.getInt("BorderWarningBlocks");
      }

      if (p_i49564_1_.contains("BorderWarningTime", 99)) {
         this.borderWarningTime = p_i49564_1_.getInt("BorderWarningTime");
      }

      if (p_i49564_1_.contains("DimensionData", 10)) {
         NBTTagCompound nbttagcompound1 = p_i49564_1_.getCompound("DimensionData");

         for(String s : nbttagcompound1.keySet()) {
            this.dimensionData.put(DimensionType.getById(Integer.parseInt(s)), nbttagcompound1.getCompound(s));
         }
      }

      if (p_i49564_1_.contains("DataPacks", 10)) {
         NBTTagCompound nbttagcompound2 = p_i49564_1_.getCompound("DataPacks");
         NBTTagList nbttaglist = nbttagcompound2.getList("Disabled", 8);

         for(int k = 0; k < nbttaglist.size(); ++k) {
            this.disabledDataPacks.add(nbttaglist.getString(k));
         }

         NBTTagList nbttaglist1 = nbttagcompound2.getList("Enabled", 8);

         for(int j = 0; j < nbttaglist1.size(); ++j) {
            this.enabledDataPacks.add(nbttaglist1.getString(j));
         }
      }

      if (p_i49564_1_.contains("CustomBossEvents", 10)) {
         this.customBossEvents = p_i49564_1_.getCompound("CustomBossEvents");
      }

   }

   public WorldInfo(WorldSettings settings, String name) {
      this.fixer = null;
      this.field_209227_p = 1631;
      this.populateFromWorldSettings(settings);
      this.levelName = name;
      this.difficulty = DEFAULT_DIFFICULTY;
      this.initialized = false;
   }

   public void populateFromWorldSettings(WorldSettings settings) {
      this.randomSeed = settings.getSeed();
      this.gameType = settings.getGameType();
      this.mapFeaturesEnabled = settings.isMapFeaturesEnabled();
      this.hardcore = settings.getHardcoreEnabled();
      this.terrainType = settings.getTerrainType();
      this.func_212242_b((NBTTagCompound)Dynamic.convert(JsonOps.INSTANCE, NBTDynamicOps.INSTANCE, settings.getGeneratorOptions()));
      this.allowCommands = settings.areCommandsAllowed();
   }

   /**
    * Creates a new NBTTagCompound for the world, with the given NBTTag as the "Player"
    */
   public NBTTagCompound cloneNBTCompound(@Nullable NBTTagCompound nbt) {
      this.func_209225_Q();
      if (nbt == null) {
         nbt = this.playerTag;
      }

      NBTTagCompound nbttagcompound = new NBTTagCompound();
      this.updateTagCompound(nbttagcompound, nbt);
      return nbttagcompound;
   }

   private void updateTagCompound(NBTTagCompound nbt, NBTTagCompound playerNbt) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setString("Name", "1.13.2");
      nbttagcompound.setInt("Id", 1631);
      nbttagcompound.setBoolean("Snapshot", false);
      nbt.setTag("Version", nbttagcompound);
      nbt.setInt("DataVersion", 1631);
      nbt.setLong("RandomSeed", this.randomSeed);
      nbt.setString("generatorName", this.terrainType.getSerialization());
      nbt.setInt("generatorVersion", this.terrainType.getVersion());
      if (!this.generatorOptions.isEmpty()) {
         nbt.setTag("generatorOptions", this.generatorOptions);
      }

      if (this.legacyCustomOptions != null) {
         nbt.setString("legacy_custom_options", this.legacyCustomOptions);
      }

      nbt.setInt("GameType", this.gameType.getID());
      nbt.setBoolean("MapFeatures", this.mapFeaturesEnabled);
      nbt.setInt("SpawnX", this.spawnX);
      nbt.setInt("SpawnY", this.spawnY);
      nbt.setInt("SpawnZ", this.spawnZ);
      nbt.setLong("Time", this.gameTime);
      nbt.setLong("DayTime", this.dayTime);
      nbt.setLong("SizeOnDisk", this.sizeOnDisk);
      nbt.setLong("LastPlayed", Util.millisecondsSinceEpoch());
      nbt.setString("LevelName", this.levelName);
      nbt.setInt("version", this.saveVersion);
      nbt.setInt("clearWeatherTime", this.clearWeatherTime);
      nbt.setInt("rainTime", this.rainTime);
      nbt.setBoolean("raining", this.raining);
      nbt.setInt("thunderTime", this.thunderTime);
      nbt.setBoolean("thundering", this.thundering);
      nbt.setBoolean("hardcore", this.hardcore);
      nbt.setBoolean("allowCommands", this.allowCommands);
      nbt.setBoolean("initialized", this.initialized);
      nbt.setDouble("BorderCenterX", this.borderCenterX);
      nbt.setDouble("BorderCenterZ", this.borderCenterZ);
      nbt.setDouble("BorderSize", this.borderSize);
      nbt.setLong("BorderSizeLerpTime", this.borderSizeLerpTime);
      nbt.setDouble("BorderSafeZone", this.borderSafeZone);
      nbt.setDouble("BorderDamagePerBlock", this.borderDamagePerBlock);
      nbt.setDouble("BorderSizeLerpTarget", this.borderSizeLerpTarget);
      nbt.setDouble("BorderWarningBlocks", (double)this.borderWarningDistance);
      nbt.setDouble("BorderWarningTime", (double)this.borderWarningTime);
      if (this.difficulty != null) {
         nbt.setByte("Difficulty", (byte)this.difficulty.getId());
      }

      nbt.setBoolean("DifficultyLocked", this.difficultyLocked);
      nbt.setTag("GameRules", this.gameRules.write());
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();

      for(Entry<DimensionType, NBTTagCompound> entry : this.dimensionData.entrySet()) {
         if (entry.getValue() == null || entry.getValue().isEmpty()) continue;
         nbttagcompound1.setTag(String.valueOf(entry.getKey().getId()), entry.getValue());
      }

      nbt.setTag("DimensionData", nbttagcompound1);
      if (playerNbt != null) {
         nbt.setTag("Player", playerNbt);
      }

      NBTTagCompound nbttagcompound2 = new NBTTagCompound();
      NBTTagList nbttaglist = new NBTTagList();

      for(String s : this.enabledDataPacks) {
         nbttaglist.add((INBTBase)(new NBTTagString(s)));
      }

      nbttagcompound2.setTag("Enabled", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(String s1 : this.disabledDataPacks) {
         nbttaglist1.add((INBTBase)(new NBTTagString(s1)));
      }

      nbttagcompound2.setTag("Disabled", nbttaglist1);
      nbt.setTag("DataPacks", nbttagcompound2);
      if (this.customBossEvents != null) {
         nbt.setTag("CustomBossEvents", this.customBossEvents);
      }

   }

   /**
    * Returns the seed of current world.
    */
   public long getSeed() {
      return this.randomSeed;
   }

   /**
    * Returns the x spawn position
    */
   public int getSpawnX() {
      return this.spawnX;
   }

   /**
    * Return the Y axis spawning point of the player.
    */
   public int getSpawnY() {
      return this.spawnY;
   }

   /**
    * Returns the z spawn position
    */
   public int getSpawnZ() {
      return this.spawnZ;
   }

   public long getGameTime() {
      return this.gameTime;
   }

   /**
    * Get current world time
    */
   public long getDayTime() {
      return this.dayTime;
   }

   @OnlyIn(Dist.CLIENT)
   public long getSizeOnDisk() {
      return this.sizeOnDisk;
   }

   private void func_209225_Q() {
      if (!this.field_209228_q && this.playerTag != null) {
         if (this.field_209227_p < 1631) {
            if (this.fixer == null) {
               throw new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.");
            }

            this.playerTag = NBTUtil.update(this.fixer, DataFixTypes.PLAYER, this.playerTag, this.field_209227_p);
         }

         this.dimension = this.playerTag.getInt("Dimension");
         this.field_209228_q = true;
      }
   }

   /**
    * Returns the player's NBTTagCompound to be loaded
    */
   public NBTTagCompound getPlayerNBTTagCompound() {
      this.func_209225_Q();
      return this.playerTag;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDimension() {
      this.func_209225_Q();
      return this.dimension;
   }

   /**
    * Set the x spawn position to the passed in value
    */
   @OnlyIn(Dist.CLIENT)
   public void setSpawnX(int x) {
      this.spawnX = x;
   }

   /**
    * Sets the y spawn position
    */
   @OnlyIn(Dist.CLIENT)
   public void setSpawnY(int y) {
      this.spawnY = y;
   }

   /**
    * Set the z spawn position to the passed in value
    */
   @OnlyIn(Dist.CLIENT)
   public void setSpawnZ(int z) {
      this.spawnZ = z;
   }

   public void setWorldTotalTime(long time) {
      this.gameTime = time;
   }

   /**
    * Set current world time
    */
   public void setDayTime(long time) {
      this.dayTime = time;
   }

   public void setSpawn(BlockPos spawnPoint) {
      this.spawnX = spawnPoint.getX();
      this.spawnY = spawnPoint.getY();
      this.spawnZ = spawnPoint.getZ();
   }

   /**
    * Get current world name
    */
   public String getWorldName() {
      return this.levelName;
   }

   public void setWorldName(String worldName) {
      this.levelName = worldName;
   }

   /**
    * Returns the save version of this world
    */
   public int getSaveVersion() {
      return this.saveVersion;
   }

   /**
    * Sets the save version of the world
    */
   public void setSaveVersion(int version) {
      this.saveVersion = version;
   }

   /**
    * Return the last time the player was in this world.
    */
   @OnlyIn(Dist.CLIENT)
   public long getLastTimePlayed() {
      return this.lastTimePlayed;
   }

   public int getClearWeatherTime() {
      return this.clearWeatherTime;
   }

   public void setClearWeatherTime(int cleanWeatherTimeIn) {
      this.clearWeatherTime = cleanWeatherTimeIn;
   }

   /**
    * Returns true if it is thundering, false otherwise.
    */
   public boolean isThundering() {
      return this.thundering;
   }

   /**
    * Sets whether it is thundering or not.
    */
   public void setThundering(boolean thunderingIn) {
      this.thundering = thunderingIn;
   }

   /**
    * Returns the number of ticks until next thunderbolt.
    */
   public int getThunderTime() {
      return this.thunderTime;
   }

   /**
    * Defines the number of ticks until next thunderbolt.
    */
   public void setThunderTime(int time) {
      this.thunderTime = time;
   }

   /**
    * Returns true if it is raining, false otherwise.
    */
   public boolean isRaining() {
      return this.raining;
   }

   /**
    * Sets whether it is raining or not.
    */
   public void setRaining(boolean isRaining) {
      this.raining = isRaining;
   }

   /**
    * Return the number of ticks until rain.
    */
   public int getRainTime() {
      return this.rainTime;
   }

   /**
    * Sets the number of ticks until rain.
    */
   public void setRainTime(int time) {
      this.rainTime = time;
   }

   /**
    * Gets the GameType.
    */
   public GameType getGameType() {
      return this.gameType;
   }

   /**
    * Get whether the map features (e.g. strongholds) generation is enabled or disabled.
    */
   public boolean isMapFeaturesEnabled() {
      return this.mapFeaturesEnabled;
   }

   public void setMapFeaturesEnabled(boolean enabled) {
      this.mapFeaturesEnabled = enabled;
   }

   /**
    * Sets the GameType.
    */
   public void setGameType(GameType type) {
      this.gameType = type;
   }

   /**
    * Returns true if hardcore mode is enabled, otherwise false
    */
   public boolean isHardcore() {
      return this.hardcore;
   }

   public void setHardcore(boolean hardcoreIn) {
      this.hardcore = hardcoreIn;
   }

   public WorldType getTerrainType() {
      return this.terrainType;
   }

   public void setTerrainType(WorldType type) {
      this.terrainType = type;
   }

   public NBTTagCompound getGeneratorOptions() {
      return this.generatorOptions;
   }

   public void func_212242_b(NBTTagCompound p_212242_1_) {
      this.generatorOptions = p_212242_1_;
   }

   /**
    * Returns true if commands are allowed on this World.
    */
   public boolean areCommandsAllowed() {
      return this.allowCommands;
   }

   public void setAllowCommands(boolean allow) {
      this.allowCommands = allow;
   }

   /**
    * Returns true if the World is initialized.
    */
   public boolean isInitialized() {
      return this.initialized;
   }

   /**
    * Sets the initialization status of the World.
    */
   public void setServerInitialized(boolean initializedIn) {
      this.initialized = initializedIn;
   }

   /**
    * Gets the GameRules class Instance.
    */
   public GameRules getGameRulesInstance() {
      return this.gameRules;
   }

   /**
    * Returns the border center X position
    */
   public double getBorderCenterX() {
      return this.borderCenterX;
   }

   /**
    * Returns the border center Z position
    */
   public double getBorderCenterZ() {
      return this.borderCenterZ;
   }

   public double getBorderSize() {
      return this.borderSize;
   }

   /**
    * Sets the border size
    */
   public void setBorderSize(double size) {
      this.borderSize = size;
   }

   /**
    * Returns the border lerp time
    */
   public long getBorderLerpTime() {
      return this.borderSizeLerpTime;
   }

   /**
    * Sets the border lerp time
    */
   public void setBorderLerpTime(long time) {
      this.borderSizeLerpTime = time;
   }

   /**
    * Returns the border lerp target
    */
   public double getBorderLerpTarget() {
      return this.borderSizeLerpTarget;
   }

   /**
    * Sets the border lerp target
    */
   public void setBorderLerpTarget(double lerpSize) {
      this.borderSizeLerpTarget = lerpSize;
   }

   /**
    * Sets the border center Z position
    */
   public void getBorderCenterZ(double posZ) {
      this.borderCenterZ = posZ;
   }

   /**
    * Sets the border center X position
    */
   public void getBorderCenterX(double posX) {
      this.borderCenterX = posX;
   }

   /**
    * Returns the border safe zone
    */
   public double getBorderSafeZone() {
      return this.borderSafeZone;
   }

   /**
    * Sets the border safe zone
    */
   public void setBorderSafeZone(double amount) {
      this.borderSafeZone = amount;
   }

   /**
    * Returns the border damage per block
    */
   public double getBorderDamagePerBlock() {
      return this.borderDamagePerBlock;
   }

   /**
    * Sets the border damage per block
    */
   public void setBorderDamagePerBlock(double damage) {
      this.borderDamagePerBlock = damage;
   }

   /**
    * Returns the border warning distance
    */
   public int getBorderWarningDistance() {
      return this.borderWarningDistance;
   }

   /**
    * Returns the border warning time
    */
   public int getBorderWarningTime() {
      return this.borderWarningTime;
   }

   /**
    * Sets the border warning distance
    */
   public void setBorderWarningDistance(int amountOfBlocks) {
      this.borderWarningDistance = amountOfBlocks;
   }

   /**
    * Sets the border warning time
    */
   public void setBorderWarningTime(int ticks) {
      this.borderWarningTime = ticks;
   }

   public EnumDifficulty getDifficulty() {
      return this.difficulty;
   }

   public void setDifficulty(EnumDifficulty newDifficulty) {
      this.difficulty = newDifficulty;
   }

   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   public void setDifficultyLocked(boolean locked) {
      this.difficultyLocked = locked;
   }

   /**
    * Adds this WorldInfo instance to the crash report.
    */
   public void addToCrashReport(CrashReportCategory category) {
      category.addDetail("Level seed", () -> {
         return String.valueOf(this.getSeed());
      });
      category.addDetail("Level generator", () -> {
         return String.format("ID %02d - %s, ver %d. Features enabled: %b", this.terrainType.getId(), this.terrainType.getName(), this.terrainType.getVersion(), this.mapFeaturesEnabled);
      });
      category.addDetail("Level generator options", () -> {
         return this.generatorOptions.toString();
      });
      category.addDetail("Level spawn location", () -> {
         return CrashReportCategory.getCoordinateInfo(this.spawnX, this.spawnY, this.spawnZ);
      });
      category.addDetail("Level time", () -> {
         return String.format("%d game time, %d day time", this.gameTime, this.dayTime);
      });
      category.addDetail("Level dimension", () -> {
         return String.valueOf(this.dimension);
      });
      category.addDetail("Level storage version", () -> {
         String s = "Unknown?";

         try {
            switch(this.saveVersion) {
            case 19132:
               s = "McRegion";
               break;
            case 19133:
               s = "Anvil";
            }
         } catch (Throwable var3) {
            ;
         }

         return String.format("0x%05X - %s", this.saveVersion, s);
      });
      category.addDetail("Level weather", () -> {
         return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.rainTime, this.raining, this.thunderTime, this.thundering);
      });
      category.addDetail("Level game mode", () -> {
         return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.gameType.getName(), this.gameType.getID(), this.hardcore, this.allowCommands);
      });
   }

   public NBTTagCompound getDimensionData(DimensionType dimensionIn) {
      NBTTagCompound nbttagcompound = this.dimensionData.get(dimensionIn);
      return nbttagcompound == null ? new NBTTagCompound() : nbttagcompound;
   }

   public void setDimensionData(DimensionType dimensionIn, NBTTagCompound compound) {
      this.dimensionData.put(dimensionIn, compound);
   }

   @OnlyIn(Dist.CLIENT)
   public int getVersionId() {
      return this.versionId;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isVersionSnapshot() {
      return this.versionSnapshot;
   }

   @OnlyIn(Dist.CLIENT)
   public String getVersionName() {
      return this.versionName;
   }

   public Set<String> getDisabledDataPacks() {
      return this.disabledDataPacks;
   }

   public Set<String> getEnabledDataPacks() {
      return this.enabledDataPacks;
   }

   @Nullable
   public NBTTagCompound getCustomBossEvents() {
      return this.customBossEvents;
   }

   public void setCustomBossEvents(@Nullable NBTTagCompound p_201356_1_) {
      this.customBossEvents = p_201356_1_;
   }
}