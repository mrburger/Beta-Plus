package net.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.ResourceIndexFolder;
import net.minecraft.util.Session;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GameConfiguration {
   public final GameConfiguration.UserInformation userInfo;
   public final GameConfiguration.DisplayInformation displayInfo;
   public final GameConfiguration.FolderInformation folderInfo;
   public final GameConfiguration.GameInformation gameInfo;
   public final GameConfiguration.ServerInformation serverInfo;

   public GameConfiguration(GameConfiguration.UserInformation userInfoIn, GameConfiguration.DisplayInformation displayInfoIn, GameConfiguration.FolderInformation folderInfoIn, GameConfiguration.GameInformation gameInfoIn, GameConfiguration.ServerInformation serverInfoIn) {
      this.userInfo = userInfoIn;
      this.displayInfo = displayInfoIn;
      this.folderInfo = folderInfoIn;
      this.gameInfo = gameInfoIn;
      this.serverInfo = serverInfoIn;
   }

   @OnlyIn(Dist.CLIENT)
   public static class DisplayInformation {
      public final int width;
      public final int height;
      public final Optional<Integer> fullscreenWidth;
      public final Optional<Integer> fullscreenHeight;
      public final boolean fullscreen;

      public DisplayInformation(int widthIn, int heightIn, Optional<Integer> fullscreenWidthIn, Optional<Integer> fullscreenHeightIn, boolean fullscreenIn) {
         this.width = widthIn;
         this.height = heightIn;
         this.fullscreenWidth = fullscreenWidthIn;
         this.fullscreenHeight = fullscreenHeightIn;
         this.fullscreen = fullscreenIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FolderInformation {
      public final File gameDir;
      public final File resourcePacksDir;
      public final File assetsDir;
      public final String assetIndex;

      public FolderInformation(File mcDataDirIn, File resourcePacksDirIn, File assetsDirIn, @Nullable String assetIndexIn) {
         this.gameDir = mcDataDirIn;
         this.resourcePacksDir = resourcePacksDirIn;
         this.assetsDir = assetsDirIn;
         this.assetIndex = assetIndexIn;
      }

      public ResourceIndex getAssetsIndex() {
         return (ResourceIndex)(this.assetIndex == null ? new ResourceIndexFolder(this.assetsDir) : new ResourceIndex(this.assetsDir, this.assetIndex));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class GameInformation {
      public final boolean isDemo;
      public final String version;
      /** Defaults to "release" */
      public final String versionType;

      public GameInformation(boolean demo, String versionIn, String versionTypeIn) {
         this.isDemo = demo;
         this.version = versionIn;
         this.versionType = versionTypeIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ServerInformation {
      public final String serverName;
      public final int serverPort;

      public ServerInformation(String serverNameIn, int serverPortIn) {
         this.serverName = serverNameIn;
         this.serverPort = serverPortIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class UserInformation {
      public final Session session;
      public final PropertyMap userProperties;
      public final PropertyMap profileProperties;
      public final Proxy proxy;

      public UserInformation(Session sessionIn, PropertyMap userPropertiesIn, PropertyMap profilePropertiesIn, Proxy proxyIn) {
         this.session = sessionIn;
         this.userProperties = userPropertiesIn;
         this.profileProperties = profilePropertiesIn;
         this.proxy = proxyIn;
      }
   }
}