package net.minecraft.resources;

import java.util.List;

public interface IReloadableResourceManager extends IResourceManager {
   void reload(List<IResourcePack> resourcePacks);

   void addReloadListener(IResourceManagerReloadListener reloadListener);
}