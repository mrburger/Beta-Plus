package net.minecraft.client.gui;

import com.google.common.hash.Hashing;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public final class GuiListWorldSelectionEntry extends GuiListExtended.IGuiListEntry<GuiListWorldSelectionEntry> implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
   private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
   private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/world_selection.png");
   private final Minecraft client;
   private final GuiWorldSelection worldSelScreen;
   private final WorldSummary worldSummary;
   private final ResourceLocation iconLocation;
   private final GuiListWorldSelection containingListSel;
   private File iconFile;
   @Nullable
   private final DynamicTexture icon;
   private long lastClickTime;

   public GuiListWorldSelectionEntry(GuiListWorldSelection listWorldSelIn, WorldSummary worldSummaryIn, ISaveFormat saveFormat) {
      this.containingListSel = listWorldSelIn;
      this.worldSelScreen = listWorldSelIn.getGuiWorldSelection();
      this.worldSummary = worldSummaryIn;
      this.client = Minecraft.getInstance();
      this.iconLocation = new ResourceLocation("worlds/" + Hashing.sha1().hashUnencodedChars(worldSummaryIn.getFileName()) + "/icon");
      this.iconFile = saveFormat.getFile(worldSummaryIn.getFileName(), "icon.png");
      if (!this.iconFile.isFile()) {
         this.iconFile = null;
      }

      this.icon = this.func_195033_j();
   }

   public void drawEntry(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
      int i = this.getY();
      int j = this.getX();
      String s = this.worldSummary.getDisplayName();
      String s1 = this.worldSummary.getFileName() + " (" + DATE_FORMAT.format(new Date(this.worldSummary.getLastTimePlayed())) + ")";
      String s2 = "";
      if (StringUtils.isEmpty(s)) {
         s = I18n.format("selectWorld.world") + " " + (this.getIndex() + 1);
      }

      if (this.worldSummary.requiresConversion()) {
         s2 = I18n.format("selectWorld.conversion") + " " + s2;
      } else {
         s2 = I18n.format("gameMode." + this.worldSummary.getEnumGameType().getName());
         if (this.worldSummary.isHardcoreModeEnabled()) {
            s2 = TextFormatting.DARK_RED + I18n.format("gameMode.hardcore") + TextFormatting.RESET;
         }

         if (this.worldSummary.getCheatsEnabled()) {
            s2 = s2 + ", " + I18n.format("selectWorld.cheats");
         }

         String s3 = this.worldSummary.func_200538_i().getFormattedText();
         if (this.worldSummary.markVersionInList()) {
            if (this.worldSummary.askToOpenWorld()) {
               s2 = s2 + ", " + I18n.format("selectWorld.version") + " " + TextFormatting.RED + s3 + TextFormatting.RESET;
            } else {
               s2 = s2 + ", " + I18n.format("selectWorld.version") + " " + TextFormatting.ITALIC + s3 + TextFormatting.RESET;
            }
         } else {
            s2 = s2 + ", " + I18n.format("selectWorld.version") + " " + s3;
         }
      }

      this.client.fontRenderer.drawString(s, (float)(j + 32 + 3), (float)(i + 1), 16777215);
      this.client.fontRenderer.drawString(s1, (float)(j + 32 + 3), (float)(i + this.client.fontRenderer.FONT_HEIGHT + 3), 8421504);
      this.client.fontRenderer.drawString(s2, (float)(j + 32 + 3), (float)(i + this.client.fontRenderer.FONT_HEIGHT + this.client.fontRenderer.FONT_HEIGHT + 3), 8421504);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bindTexture(this.icon != null ? this.iconLocation : ICON_MISSING);
      GlStateManager.enableBlend();
      Gui.drawModalRectWithCustomSizedTexture(j, i, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      GlStateManager.disableBlend();
      if (this.client.gameSettings.touchscreen || p_194999_5_) {
         this.client.getTextureManager().bindTexture(ICON_OVERLAY_LOCATION);
         Gui.drawRect(j, i, j + 32, i + 32, -1601138544);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int l = mouseX - j;
         int k = l < 32 ? 32 : 0;
         if (this.worldSummary.markVersionInList()) {
            Gui.drawModalRectWithCustomSizedTexture(j, i, 32.0F, (float)k, 32, 32, 256.0F, 256.0F);
            if (this.worldSummary.func_202842_n()) {
               Gui.drawModalRectWithCustomSizedTexture(j, i, 96.0F, (float)k, 32, 32, 256.0F, 256.0F);
               if (l < 32) {
                  ITextComponent itextcomponent = (new TextComponentTranslation("selectWorld.tooltip.unsupported", this.worldSummary.func_200538_i())).applyTextStyle(TextFormatting.RED);
                  this.worldSelScreen.setVersionTooltip(this.client.fontRenderer.wrapFormattedStringToWidth(itextcomponent.getFormattedText(), 175));
               }
            } else if (this.worldSummary.askToOpenWorld()) {
               Gui.drawModalRectWithCustomSizedTexture(j, i, 96.0F, (float)k, 32, 32, 256.0F, 256.0F);
               if (l < 32) {
                  this.worldSelScreen.setVersionTooltip(TextFormatting.RED + I18n.format("selectWorld.tooltip.fromNewerVersion1") + "\n" + TextFormatting.RED + I18n.format("selectWorld.tooltip.fromNewerVersion2"));
               }
            }
         } else {
            Gui.drawModalRectWithCustomSizedTexture(j, i, 0.0F, (float)k, 32, 32, 256.0F, 256.0F);
         }
      }

   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      this.containingListSel.selectWorld(this.getIndex());
      if (p_mouseClicked_1_ - (double)this.getX() <= 32.0D) {
         this.joinWorld();
         return true;
      } else if (Util.milliTime() - this.lastClickTime < 250L) {
         this.joinWorld();
         return true;
      } else {
         this.lastClickTime = Util.milliTime();
         return false;
      }
   }

   public void joinWorld() {
      if (!this.worldSummary.func_197731_n() && !this.worldSummary.func_202842_n()) {
         if (this.worldSummary.askToOpenWorld()) {
            this.client.displayGuiScreen(new GuiYesNo((p_195036_1_, p_195036_2_) -> {
               if (p_195036_1_) {
                  try {
                     this.loadWorld();
                  } catch (Exception exception) {
                     LOGGER.error("Failure to open 'future world'", (Throwable)exception);
                     this.client.displayGuiScreen(new GuiScreenAlert(() -> {
                        this.client.displayGuiScreen(this.worldSelScreen);
                     }, new TextComponentTranslation("selectWorld.futureworld.error.title"), new TextComponentTranslation("selectWorld.futureworld.error.text")));
                  }
               } else {
                  this.client.displayGuiScreen(this.worldSelScreen);
               }

            }, I18n.format("selectWorld.versionQuestion"), I18n.format("selectWorld.versionWarning", this.worldSummary.func_200538_i().getFormattedText()), I18n.format("selectWorld.versionJoinButton"), I18n.format("gui.cancel"), 0));
         } else {
            this.loadWorld();
         }
      } else {
         String s = I18n.format("selectWorld.backupQuestion");
         String s1 = I18n.format("selectWorld.backupWarning", this.worldSummary.func_200538_i().getFormattedText(), "1.13.2");
         if (this.worldSummary.func_202842_n()) {
            s = I18n.format("selectWorld.backupQuestion.customized");
            s1 = I18n.format("selectWorld.backupWarning.customized");
         }

         this.client.displayGuiScreen(new GuiConfirmBackup(this.worldSelScreen, (p_212106_1_) -> {
            if (p_212106_1_) {
               String s2 = this.worldSummary.getFileName();
               GuiWorldEdit.createBackup(this.client.getSaveLoader(), s2);
            }

            this.loadWorld();
         }, s, s1));
      }

   }

   public void deleteWorld() {
      this.client.displayGuiScreen(new GuiYesNo((p_210103_1_, p_210103_2_) -> {
         if (p_210103_1_) {
            this.client.displayGuiScreen(new GuiScreenWorking());
            ISaveFormat isaveformat = this.client.getSaveLoader();
            isaveformat.flushCache();
            isaveformat.deleteWorldDirectory(this.worldSummary.getFileName());
            this.containingListSel.func_212330_a(() -> {
               return this.worldSelScreen.field_212352_g.getText();
            }, true);
         }

         this.client.displayGuiScreen(this.worldSelScreen);
      }, I18n.format("selectWorld.deleteQuestion"), I18n.format("selectWorld.deleteWarning", this.worldSummary.getDisplayName()), I18n.format("selectWorld.deleteButton"), I18n.format("gui.cancel"), 0));
   }

   public void editWorld() {
      this.client.displayGuiScreen(new GuiWorldEdit((p_212309_1_, p_212309_2_) -> {
         if (p_212309_1_) {
            this.containingListSel.func_212330_a(() -> {
               return this.worldSelScreen.field_212352_g.getText();
            }, true);
         }

         this.client.displayGuiScreen(this.worldSelScreen);
      }, this.worldSummary.getFileName()));
   }

   public void recreateWorld() {
      try {
         this.client.displayGuiScreen(new GuiScreenWorking());
         GuiCreateWorld guicreateworld = new GuiCreateWorld(this.worldSelScreen);
         ISaveHandler isavehandler = this.client.getSaveLoader().getSaveLoader(this.worldSummary.getFileName(), (MinecraftServer)null);
         WorldInfo worldinfo = isavehandler.loadWorldInfo();
         isavehandler.flush();
         if (worldinfo != null) {
            guicreateworld.recreateFromExistingWorld(worldinfo);
            if (this.worldSummary.func_202842_n()) {
               this.client.displayGuiScreen(new GuiYesNo((p_211802_2_, p_211802_3_) -> {
                  if (p_211802_2_) {
                     this.client.displayGuiScreen(guicreateworld);
                  } else {
                     this.client.displayGuiScreen(this.worldSelScreen);
                  }

               }, I18n.format("selectWorld.recreate.customized.title"), I18n.format("selectWorld.recreate.customized.text"), I18n.format("gui.proceed"), I18n.format("gui.cancel"), 0));
            } else {
               this.client.displayGuiScreen(guicreateworld);
            }
         }
      } catch (Exception exception) {
         LOGGER.error("Unable to recreate world", (Throwable)exception);
         this.client.displayGuiScreen(new GuiScreenAlert(() -> {
            this.client.displayGuiScreen(this.worldSelScreen);
         }, new TextComponentTranslation("selectWorld.recreate.error.title"), new TextComponentTranslation("selectWorld.recreate.error.text")));
      }

   }

   private void loadWorld() {
      this.client.getSoundHandler().play(SimpleSound.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      if (this.client.getSaveLoader().canLoadWorld(this.worldSummary.getFileName())) {
         this.client.launchIntegratedServer(this.worldSummary.getFileName(), this.worldSummary.getDisplayName(), (WorldSettings)null);
      }

   }

   @Nullable
   private DynamicTexture func_195033_j() {
      boolean flag = this.iconFile != null && this.iconFile.isFile();
      if (flag) {
         try {
            InputStream inputstream = new FileInputStream(this.iconFile);
            Throwable throwable = null;

            DynamicTexture dynamictexture1;
            try {
               NativeImage nativeimage = NativeImage.read(inputstream);
               Validate.validState(nativeimage.getWidth() == 64, "Must be 64 pixels wide");
               Validate.validState(nativeimage.getHeight() == 64, "Must be 64 pixels high");
               DynamicTexture dynamictexture = new DynamicTexture(nativeimage);
               this.client.getTextureManager().loadTexture(this.iconLocation, dynamictexture);
               dynamictexture1 = dynamictexture;
            } catch (Throwable throwable2) {
               throwable = throwable2;
               throw throwable2;
            } finally {
               if (inputstream != null) {
                  if (throwable != null) {
                     try {
                        inputstream.close();
                     } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                     }
                  } else {
                     inputstream.close();
                  }
               }

            }

            return dynamictexture1;
         } catch (Throwable throwable3) {
            LOGGER.error("Invalid icon for world {}", this.worldSummary.getFileName(), throwable3);
            this.iconFile = null;
            return null;
         }
      } else {
         this.client.getTextureManager().deleteTexture(this.iconLocation);
         return null;
      }
   }

   public void close() {
      if (this.icon != null) {
         this.icon.close();
      }

   }

   public void func_195000_a(float p_195000_1_) {
   }
}