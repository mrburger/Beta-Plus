package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryNamespaced<V> implements IRegistry<V> {
   protected static final Logger LOGGER = LogManager.getLogger();
   /** The backing store that maps Integers to objects. */
   protected final IntIdentityHashBiMap<V> underlyingIntegerMap = new IntIdentityHashBiMap<>(256);
   /** Objects registered on this registry. */
   protected final BiMap<ResourceLocation, V> registryObjects = HashBiMap.create();
   protected Object[] values;
   private int nextFreeId;

   public void register(int id, ResourceLocation key, V value) {
      this.underlyingIntegerMap.put(value, id);
      Validate.notNull(key);
      Validate.notNull(value);
      this.values = null;
      if (this.registryObjects.containsKey(key)) {
         LOGGER.debug("Adding duplicate key '{}' to registry", (Object)key);
      }

      this.registryObjects.put(key, value);
      if (this.nextFreeId <= id) {
         this.nextFreeId = id + 1;
      }

   }

   /**
    * Register an object on this registry.
    */
   public void put(ResourceLocation key, V value) {
      this.register(this.nextFreeId, key, value);
   }

   /**
    * Gets the name we use to identify the given object.
    */
   @Nullable
   public ResourceLocation getKey(V value) {
      return this.registryObjects.inverse().get(value);
   }

   public V get(@Nullable ResourceLocation name) {
      throw new UnsupportedOperationException("No default value");
   }

   public ResourceLocation func_212609_b() {
      throw new UnsupportedOperationException("No default key");
   }

   /**
    * Gets the integer ID we use to identify the given object.
    */
   public int getId(@Nullable V value) {
      return this.underlyingIntegerMap.getId(value);
   }

   /**
    * Gets the object identified by the given ID.
    */
   @Nullable
   public V get(int id) {
      return this.underlyingIntegerMap.get(id);
   }

   public Iterator<V> iterator() {
      return this.underlyingIntegerMap.iterator();
   }

   @Nullable
   public V func_212608_b(@Nullable ResourceLocation p_212608_1_) {
      return this.registryObjects.get(p_212608_1_);
   }

   /**
    * Gets all the keys recognized by this registry.
    */
   public Set<ResourceLocation> getKeys() {
      return Collections.unmodifiableSet(this.registryObjects.keySet());
   }

   public boolean isEmpty() {
      return this.registryObjects.isEmpty();
   }

   @Nullable
   public V getRandom(Random random) {
      if (this.values == null) {
         Collection<?> collection = this.registryObjects.values();
         if (collection.isEmpty()) {
            return (V)null;
         }

         this.values = collection.toArray(new Object[collection.size()]);
      }

      return (V)this.values[random.nextInt(this.values.length)];
   }

   public boolean func_212607_c(ResourceLocation p_212607_1_) {
      return this.registryObjects.containsKey(p_212607_1_);
   }
}