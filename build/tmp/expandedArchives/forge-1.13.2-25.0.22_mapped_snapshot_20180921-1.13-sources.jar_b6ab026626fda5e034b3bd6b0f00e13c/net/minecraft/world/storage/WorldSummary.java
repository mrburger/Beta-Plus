package net.minecraft.world.storage;

import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldSummary implements Comparable<WorldSummary> {
   /** the file name of this save */
   private final String fileName;
   /** the displayed name of this save file */
   private final String displayName;
   private final long lastTimePlayed;
   private final long sizeOnDisk;
   private final boolean requiresConversion;
   /** Instance of EnumGameType. */
   private final GameType gameType;
   private final boolean hardcore;
   private final boolean cheatsEnabled;
   private final String versionName;
   private final int versionId;
   private final boolean versionSnapshot;
   private final WorldType terrainType;

   public WorldSummary(WorldInfo info, String fileNameIn, String displayNameIn, long sizeOnDiskIn, boolean requiresConversionIn) {
      this.fileName = fileNameIn;
      this.displayName = displayNameIn;
      this.lastTimePlayed = info.getLastTimePlayed();
      this.sizeOnDisk = sizeOnDiskIn;
      this.gameType = info.getGameType();
      this.requiresConversion = requiresConversionIn;
      this.hardcore = info.isHardcore();
      this.cheatsEnabled = info.areCommandsAllowed();
      this.versionName = info.getVersionName();
      this.versionId = info.getVersionId();
      this.versionSnapshot = info.isVersionSnapshot();
      this.terrainType = info.getTerrainType();
   }

   /**
    * return the file name
    */
   public String getFileName() {
      return this.fileName;
   }

   /**
    * return the display name of the save
    */
   public String getDisplayName() {
      return this.displayName;
   }

   public long getSizeOnDisk() {
      return this.sizeOnDisk;
   }

   public boolean requiresConversion() {
      return this.requiresConversion;
   }

   public long getLastTimePlayed() {
      return this.lastTimePlayed;
   }

   public int compareTo(WorldSummary p_compareTo_1_) {
      if (this.lastTimePlayed < p_compareTo_1_.lastTimePlayed) {
         return 1;
      } else {
         return this.lastTimePlayed > p_compareTo_1_.lastTimePlayed ? -1 : this.fileName.compareTo(p_compareTo_1_.fileName);
      }
   }

   /**
    * Gets the EnumGameType.
    */
   public GameType getEnumGameType() {
      return this.gameType;
   }

   public boolean isHardcoreModeEnabled() {
      return this.hardcore;
   }

   /**
    * @return {@code true} if cheats are enabled for this world
    */
   public boolean getCheatsEnabled() {
      return this.cheatsEnabled;
   }

   public ITextComponent func_200538_i() {
      return (ITextComponent)(StringUtils.isNullOrEmpty(this.versionName) ? new TextComponentTranslation("selectWorld.versionUnknown") : new TextComponentString(this.versionName));
   }

   public boolean markVersionInList() {
      return this.askToOpenWorld() || this.func_197731_n() || this.func_202842_n();
   }

   public boolean askToOpenWorld() {
      return this.versionId > 1631;
   }

   public boolean func_202842_n() {
      return this.terrainType == WorldType.CUSTOMIZED && this.versionId < 1466;
   }

   public boolean func_197731_n() {
      return this.versionId < 1631;
   }
}