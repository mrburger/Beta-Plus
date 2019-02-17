package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.VanillaPack;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class DownloadingPackFinder implements IPackFinder {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern field_195752_b = Pattern.compile("^[a-fA-F0-9]{40}$");
   private final VanillaPack vanillaPack;
   private final File field_195754_d;
   private final ReentrantLock field_195755_e = new ReentrantLock();
   @Nullable
   private ListenableFuture<?> field_195756_f;
   @Nullable
   private ResourcePackInfoClient field_195757_g;

   public DownloadingPackFinder(File p_i48116_1_, ResourceIndex p_i48116_2_) {
      this.field_195754_d = p_i48116_1_;
      this.vanillaPack = new VirtualAssetsPack(p_i48116_2_);
   }

   public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory) {
      T t = ResourcePackInfo.func_195793_a("vanilla", true, () -> {
         return this.vanillaPack;
      }, packInfoFactory, ResourcePackInfo.Priority.BOTTOM);
      if (t != null) {
         nameToPackMap.put("vanilla", t);
      }

      if (this.field_195757_g != null) {
         nameToPackMap.put("server", (T)this.field_195757_g);
      }

   }

   public VanillaPack getVanillaPack() {
      return this.vanillaPack;
   }

   public static Map<String, String> func_195742_b() {
      Map<String, String> map = Maps.newHashMap();
      map.put("X-Minecraft-Username", Minecraft.getInstance().getSession().getUsername());
      map.put("X-Minecraft-UUID", Minecraft.getInstance().getSession().getPlayerID());
      map.put("X-Minecraft-Version", "1.13.2");
      map.put("X-Minecraft-Pack-Format", String.valueOf((int)4));
      map.put("User-Agent", "Minecraft Java/1.13.2");
      return map;
   }

   public ListenableFuture<?> downloadResourcePack(String p_195744_1_, String p_195744_2_) {
      String s = DigestUtils.sha1Hex(p_195744_1_);
      final String s1 = field_195752_b.matcher(p_195744_2_).matches() ? p_195744_2_ : "";
      final File file1 = new File(this.field_195754_d, s);
      this.field_195755_e.lock();

      try {
         this.clearResourcePack();
         if (file1.exists()) {
            if (this.func_195745_a(s1, file1)) {
               ListenableFuture listenablefuture1 = this.func_195741_a(file1);
               return listenablefuture1;
            }

            LOGGER.warn("Deleting file {}", (Object)file1);
            FileUtils.deleteQuietly(file1);
         }

         this.func_195747_e();
         GuiScreenWorking guiscreenworking = new GuiScreenWorking();
         Map<String, String> map = func_195742_b();
         Minecraft minecraft = Minecraft.getInstance();
         Futures.getUnchecked(minecraft.addScheduledTask(() -> {
            minecraft.displayGuiScreen(guiscreenworking);
         }));
         final SettableFuture<Object> settablefuture = SettableFuture.create();
         this.field_195756_f = HttpUtil.downloadResourcePack(file1, p_195744_1_, map, 52428800, guiscreenworking, minecraft.getProxy());
         Futures.addCallback(this.field_195756_f, new FutureCallback<Object>() {
            public void onSuccess(@Nullable Object p_onSuccess_1_) {
               if (DownloadingPackFinder.this.func_195745_a(s1, file1)) {
                  DownloadingPackFinder.this.func_195741_a(file1);
                  settablefuture.set((Object)null);
               } else {
                  DownloadingPackFinder.LOGGER.warn("Deleting file {}", (Object)file1);
                  FileUtils.deleteQuietly(file1);
               }

            }

            public void onFailure(Throwable p_onFailure_1_) {
               FileUtils.deleteQuietly(file1);
               settablefuture.setException(p_onFailure_1_);
            }
         });
         ListenableFuture listenablefuture = this.field_195756_f;
         return listenablefuture;
      } finally {
         this.field_195755_e.unlock();
      }
   }

   public void clearResourcePack() {
      this.field_195755_e.lock();

      try {
         if (this.field_195756_f != null) {
            this.field_195756_f.cancel(true);
         }

         this.field_195756_f = null;
         if (this.field_195757_g != null) {
            this.field_195757_g = null;
            Minecraft.getInstance().scheduleResourcesRefresh();
         }
      } finally {
         this.field_195755_e.unlock();
      }

   }

   private boolean func_195745_a(String p_195745_1_, File p_195745_2_) {
      try {
         String s = DigestUtils.sha1Hex((InputStream)(new FileInputStream(p_195745_2_)));
         if (p_195745_1_.isEmpty()) {
            LOGGER.info("Found file {} without verification hash", (Object)p_195745_2_);
            return true;
         }

         if (s.toLowerCase(java.util.Locale.ROOT).equals(p_195745_1_.toLowerCase(java.util.Locale.ROOT))) {
            LOGGER.info("Found file {} matching requested hash {}", p_195745_2_, p_195745_1_);
            return true;
         }

         LOGGER.warn("File {} had wrong hash (expected {}, found {}).", p_195745_2_, p_195745_1_, s);
      } catch (IOException ioexception) {
         LOGGER.warn("File {} couldn't be hashed.", p_195745_2_, ioexception);
      }

      return false;
   }

   private void func_195747_e() {
      try {
         List<File> list = Lists.newArrayList(FileUtils.listFiles(this.field_195754_d, TrueFileFilter.TRUE, (IOFileFilter)null));
         list.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
         int i = 0;

         for(File file1 : list) {
            if (i++ >= 10) {
               LOGGER.info("Deleting old server resource pack {}", (Object)file1.getName());
               FileUtils.deleteQuietly(file1);
            }
         }
      } catch (IllegalArgumentException illegalargumentexception) {
         LOGGER.error("Error while deleting old server resource pack : {}", (Object)illegalargumentexception.getMessage());
      }

   }

   public ListenableFuture<Object> func_195741_a(File p_195741_1_) {
      PackMetadataSection packmetadatasection = null;
      NativeImage nativeimage = null;

      try (FilePack filepack = new FilePack(p_195741_1_)) {
         packmetadatasection = filepack.getMetadata(PackMetadataSection.SERIALIZER);

         try (InputStream inputstream = filepack.getRootResourceStream("pack.png")) {
            nativeimage = NativeImage.read(inputstream);
         } catch (IllegalArgumentException | IOException var36) {
            ;
         }
      } catch (IOException var39) {
         ;
      }

      if (packmetadatasection == null) {
         return Futures.immediateFailedFuture(new RuntimeException("Invalid resourcepack"));
      } else {
         this.field_195757_g = new ResourcePackInfoClient("server", true, () -> {
            return new FilePack(p_195741_1_);
         }, new TextComponentTranslation("resourcePack.server.name"), packmetadatasection.getDescription(), PackCompatibility.func_198969_a(packmetadatasection.getPackFormat()), ResourcePackInfo.Priority.TOP, true, nativeimage);
         return Minecraft.getInstance().scheduleResourcesRefresh();
      }
   }
}