package net.minecraft.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.resources.SimpleResource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ScreenShotHelper {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

   /**
    * Saves a screenshot in the game directory with a time-stamped filename.
    * Returns an ITextComponent indicating the success/failure of the saving.
    */
   public static void saveScreenshot(File gameDirectory, int width, int height, Framebuffer buffer, Consumer<ITextComponent> p_148260_4_) {
      saveScreenshot(gameDirectory, (String)null, width, height, buffer, p_148260_4_);
   }

   /**
    * Saves a screenshot in the game directory with the given file name (or null to generate a time-stamped name).
    * Returns an ITextComponent indicating the success/failure of the saving.
    */
   public static void saveScreenshot(File gameDirectory, @Nullable String screenshotName, int width, int height, Framebuffer buffer, Consumer<ITextComponent> p_148259_5_) {
      NativeImage nativeimage = createScreenshot(width, height, buffer);
      File file1 = new File(gameDirectory, "screenshots");
      file1.mkdir();
      File target;
      if (screenshotName == null) {
         target = getTimestampedPNGFileForDirectory(file1);
      } else {
         target = new File(file1, screenshotName);
      }

      try {
         target = target.getCanonicalFile(); // FORGE: Fix errors on Windows with paths that include \.\
      } catch (java.io.IOException e) {}
      net.minecraftforge.client.event.ScreenshotEvent event = net.minecraftforge.client.ForgeHooksClient.onScreenshot(nativeimage, target);
      if (event.isCanceled()) {
         p_148259_5_.accept(event.getCancelMessage());
         return;
      } else {
         target = event.getScreenshotFile();
      }

      final File file2 = target;
      SimpleResource.RESOURCE_IO_EXECUTOR.execute(() -> {
         try {
            nativeimage.write(file2);
            ITextComponent itextcomponent = (new TextComponentString(file2.getName())).applyTextStyle(TextFormatting.UNDERLINE).applyTextStyle((p_212451_1_) -> {
               p_212451_1_.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath()));
            });
            if (event.getResultMessage() != null) {
               p_148259_5_.accept(event.getResultMessage());
            } else
            p_148259_5_.accept(new TextComponentTranslation("screenshot.success", itextcomponent));
         } catch (Exception exception) {
            LOGGER.warn("Couldn't save screenshot", (Throwable)exception);
            p_148259_5_.accept(new TextComponentTranslation("screenshot.failure", exception.getMessage()));
         } finally {
            nativeimage.close();
         }

      });
   }

   public static NativeImage createScreenshot(int width, int height, Framebuffer framebufferIn) {
      if (OpenGlHelper.isFramebufferEnabled()) {
         width = framebufferIn.framebufferTextureWidth;
         height = framebufferIn.framebufferTextureHeight;
      }

      NativeImage nativeimage = new NativeImage(width, height, false);
      if (OpenGlHelper.isFramebufferEnabled()) {
         GlStateManager.bindTexture(framebufferIn.framebufferTexture);
         nativeimage.downloadFromTexture(0, true);
      } else {
         nativeimage.downloadFromFramebuffer(true);
      }

      nativeimage.flip();
      return nativeimage;
   }

   /**
    * Creates a unique PNG file in the given directory named by a timestamp.  Handles cases where the timestamp alone is
    * not enough to create a uniquely named file, though it still might suffer from an unlikely race condition where the
    * filename was unique when this method was called, but another process or thread created a file at the same path
    * immediately after this method returned.
    */
   private static File getTimestampedPNGFileForDirectory(File gameDirectory) {
      String s = DATE_FORMAT.format(new Date());
      int i = 1;

      while(true) {
         File file1 = new File(gameDirectory, s + (i == 1 ? "" : "_" + i) + ".png");
         if (!file1.exists()) {
            return file1;
         }

         ++i;
      }
   }
}