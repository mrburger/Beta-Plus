package net.minecraft.resources;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IResourcePack extends Closeable {
   @OnlyIn(Dist.CLIENT)
   InputStream getRootResourceStream(String fileName) throws IOException;

   InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException;

   Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String pathIn, int maxDepth, Predicate<String> filter);

   boolean resourceExists(ResourcePackType type, ResourceLocation location);

   Set<String> getResourceNamespaces(ResourcePackType type);

   @Nullable
   <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException;

   String getName();
}