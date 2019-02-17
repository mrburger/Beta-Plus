package net.minecraft.resources;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements IReloadableResourceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, FallbackResourceManager> namespaceResourceManagers = Maps.newHashMap();
   private final List<IResourceManagerReloadListener> reloadListeners = Lists.newArrayList();
   private final Set<String> resourceNamespaces = Sets.newLinkedHashSet();
   private final ResourcePackType type;

   public SimpleReloadableResourceManager(ResourcePackType typeIn) {
      this.type = typeIn;
   }

   public void addResourcePack(IResourcePack resourcePack) {
      for(String s : resourcePack.getResourceNamespaces(this.type)) {
         this.resourceNamespaces.add(s);
         FallbackResourceManager fallbackresourcemanager = this.namespaceResourceManagers.get(s);
         if (fallbackresourcemanager == null) {
            fallbackresourcemanager = new FallbackResourceManager(this.type);
            this.namespaceResourceManagers.put(s, fallbackresourcemanager);
         }

         fallbackresourcemanager.addResourcePack(resourcePack);
      }

   }

   public Set<String> getResourceNamespaces() {
      return this.resourceNamespaces;
   }

   public IResource getResource(ResourceLocation resourceLocationIn) throws IOException {
      IResourceManager iresourcemanager = this.namespaceResourceManagers.get(resourceLocationIn.getNamespace());
      if (iresourcemanager != null) {
         return iresourcemanager.getResource(resourceLocationIn);
      } else {
         throw new FileNotFoundException(resourceLocationIn.toString());
      }
   }

   public List<IResource> getAllResources(ResourceLocation resourceLocationIn) throws IOException {
      IResourceManager iresourcemanager = this.namespaceResourceManagers.get(resourceLocationIn.getNamespace());
      if (iresourcemanager != null) {
         return iresourcemanager.getAllResources(resourceLocationIn);
      } else {
         throw new FileNotFoundException(resourceLocationIn.toString());
      }
   }

   public Collection<ResourceLocation> getAllResourceLocations(String pathIn, Predicate<String> filter) {
      Set<ResourceLocation> set = Sets.newHashSet();

      for(FallbackResourceManager fallbackresourcemanager : this.namespaceResourceManagers.values()) {
         set.addAll(fallbackresourcemanager.getAllResourceLocations(pathIn, filter));
      }

      List<ResourceLocation> list = Lists.newArrayList(set);
      Collections.sort(list);
      return list;
   }

   private void clearResourceNamespaces() {
      this.namespaceResourceManagers.clear();
      this.resourceNamespaces.clear();
   }

   public void reload(List<IResourcePack> resourcePacks) {
      try (net.minecraftforge.fml.common.progress.ProgressBar resReload = net.minecraftforge.fml.common.progress.StartupProgressManager.start("Loading Resources", resourcePacks.size() + 1, true)) {
      this.clearResourceNamespaces();
      LOGGER.info("Reloading ResourceManager: {}", resourcePacks.stream().map(IResourcePack::getName).collect(Collectors.joining(", ")));

      for(IResourcePack iresourcepack : resourcePacks) {
         resReload.step(iresourcepack.getName());
         this.addResourcePack(iresourcepack);
      }

      resReload.step("Reloading listeners");
      if (LOGGER.isDebugEnabled()) {
         this.reloadAllResourcesDebug();
      } else {
         this.triggerReloadListeners();
      }
      }; // Forge: end progress bar

   }

   public void addReloadListener(IResourceManagerReloadListener reloadListener) {
      try (net.minecraftforge.fml.common.progress.ProgressBar resReload = net.minecraftforge.fml.common.progress.StartupProgressManager.start("Loading Resource", 1)) {
      resReload.step(reloadListener.getClass());
      this.reloadListeners.add(reloadListener);
      if (LOGGER.isDebugEnabled()) {
         LOGGER.info(this.reloadResourcesFor(reloadListener));
      } else {
         reloadListener.onResourceManagerReload(this);
      }
      }; // Forge: end progress bar

   }

   private void triggerReloadListeners() {
      try (net.minecraftforge.fml.common.progress.ProgressBar resReload = net.minecraftforge.fml.common.progress.StartupProgressManager.start("Reloading", this.reloadListeners.size())) {
      for(IResourceManagerReloadListener iresourcemanagerreloadlistener : this.reloadListeners) {
         resReload.step(iresourcemanagerreloadlistener.getClass());
         if (!net.minecraftforge.resource.SelectiveReloadStateHandler.INSTANCE.test(iresourcemanagerreloadlistener)) continue; // Forge: Selective reloading for vanilla listeners
         iresourcemanagerreloadlistener.onResourceManagerReload(this);
      }
      }; // Forge: end progress bar

   }

   private void reloadAllResourcesDebug() {
      LOGGER.info("Reloading all resources! {} listeners to update.", (int)this.reloadListeners.size());
      List<String> list = Lists.newArrayList();
      Stopwatch stopwatch = Stopwatch.createStarted();

      for(IResourceManagerReloadListener iresourcemanagerreloadlistener : this.reloadListeners) {
         list.add(this.reloadResourcesFor(iresourcemanagerreloadlistener));
      }

      stopwatch.stop();
      LOGGER.info("----");
      LOGGER.info("Complete resource reload took {} ms", (long)stopwatch.elapsed(TimeUnit.MILLISECONDS));

      for(String s : list) {
         LOGGER.info(s);
      }

      LOGGER.info("----");
   }

   private String reloadResourcesFor(IResourceManagerReloadListener reloadListener) {
      Stopwatch stopwatch = Stopwatch.createStarted();
      reloadListener.onResourceManagerReload(this);
      stopwatch.stop();
      return "Resource reload for " + reloadListener.getClass().getSimpleName() + " took " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms";
   }
}