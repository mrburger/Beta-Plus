package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TemplateManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, Template> templates = Maps.newHashMap();
   private final DataFixer fixer;
   private final MinecraftServer minecraftServer;
   private final Path pathGenerated;

   public TemplateManager(MinecraftServer server, File templateFolder, DataFixer fixerIn) {
      this.minecraftServer = server;
      this.fixer = fixerIn;
      this.pathGenerated = templateFolder.toPath().resolve("generated").normalize();
      server.getResourceManager().addReloadListener(this);
   }

   public Template getTemplateDefaulted(ResourceLocation p_200220_1_) {
      Template template = this.getTemplate(p_200220_1_);
      if (template == null) {
         template = new Template();
         this.templates.put(p_200220_1_, template);
      }

      return template;
   }

   @Nullable
   public Template getTemplate(ResourceLocation p_200219_1_) {
      return this.templates.computeIfAbsent(p_200219_1_, (p_209204_1_) -> {
         Template template = this.loadTemplateFile(p_209204_1_);
         return template != null ? template : this.loadTemplateResource(p_209204_1_);
      });
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.templates.clear();
   }

   @Nullable
   private Template loadTemplateResource(ResourceLocation p_209201_1_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_209201_1_.getNamespace(), "structures/" + p_209201_1_.getPath() + ".nbt");

      try (IResource iresource = this.minecraftServer.getResourceManager().getResource(resourcelocation)) {
         Template template = this.loadTemplate(iresource.getInputStream());
         return template;
      } catch (FileNotFoundException var18) {
         return null;
      } catch (Throwable throwable) {
         LOGGER.error("Couldn't load structure {}: {}", p_209201_1_, throwable.toString());
         return null;
      }
   }

   @Nullable
   private Template loadTemplateFile(ResourceLocation locationIn) {
      if (!this.pathGenerated.toFile().isDirectory()) {
         return null;
      } else {
         Path path = this.resolvePath(locationIn, ".nbt");

         try {
            InputStream inputstream = new FileInputStream(path.toFile());
            Throwable throwable = null;

            Template template;
            try {
               template = this.loadTemplate(inputstream);
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

            return template;
         } catch (FileNotFoundException var18) {
            return null;
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't load structure from {}", path, ioexception);
            return null;
         }
      }
   }

   private Template loadTemplate(InputStream inputStreamIn) throws IOException {
      NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(inputStreamIn);
      if (!nbttagcompound.contains("DataVersion", 99)) {
         nbttagcompound.setInt("DataVersion", 500);
      }

      Template template = new Template();
      template.read(NBTUtil.update(this.fixer, DataFixTypes.STRUCTURE, nbttagcompound, nbttagcompound.getInt("DataVersion")));
      return template;
   }

   public boolean writeToFile(ResourceLocation templateName) {
      Template template = this.templates.get(templateName);
      if (template == null) {
         return false;
      } else {
         Path path = this.resolvePath(templateName, ".nbt");
         Path path1 = path.getParent();
         if (path1 == null) {
            return false;
         } else {
            try {
               Files.createDirectories(Files.exists(path1) ? path1.toRealPath() : path1);
            } catch (IOException var19) {
               LOGGER.error("Failed to create parent directory: {}", (Object)path1);
               return false;
            }

            NBTTagCompound nbttagcompound = template.writeToNBT(new NBTTagCompound());

            try {
               OutputStream outputstream = new FileOutputStream(path.toFile());
               Throwable throwable = null;

               try {
                  CompressedStreamTools.writeCompressed(nbttagcompound, outputstream);
               } catch (Throwable throwable2) {
                  throwable = throwable2;
                  throw throwable2;
               } finally {
                  if (outputstream != null) {
                     if (throwable != null) {
                        try {
                           outputstream.close();
                        } catch (Throwable throwable1) {
                           throwable.addSuppressed(throwable1);
                        }
                     } else {
                        outputstream.close();
                     }
                  }

               }

               return true;
            } catch (Throwable var21) {
               return false;
            }
         }
      }
   }

   private Path resolvePathStructures(ResourceLocation locationIn, String extIn) {
      try {
         Path path = this.pathGenerated.resolve(locationIn.getNamespace());
         Path path1 = path.resolve("structures");
         return Util.resolvePath(path1, locationIn.getPath(), extIn);
      } catch (InvalidPathException invalidpathexception) {
         throw new ResourceLocationException("Invalid resource path: " + locationIn, invalidpathexception);
      }
   }

   private Path resolvePath(ResourceLocation locationIn, String extIn) {
      if (locationIn.getPath().contains("//")) {
         throw new ResourceLocationException("Invalid resource path: " + locationIn);
      } else {
         Path path = this.resolvePathStructures(locationIn, extIn);
         if (path.startsWith(this.pathGenerated) && Util.isPathNormal(path) && Util.isPathValidForWindows(path)) {
            return path;
         } else {
            throw new ResourceLocationException("Invalid resource path: " + path);
         }
      }
   }

   public void remove(ResourceLocation templatePath) {
      this.templates.remove(templatePath);
   }
}