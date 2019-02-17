package net.minecraft.util.registry;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class RegistryNamespacedDefaultedByKey<V> extends RegistryNamespaced<V> {
   /** The key of the default value. */
   private final ResourceLocation defaultValueKey;
   /** The default value for this registry, retrurned in the place of a null value. */
   private V defaultValue;

   public RegistryNamespacedDefaultedByKey(ResourceLocation p_i49828_1_) {
      this.defaultValueKey = p_i49828_1_;
   }

   public void register(int id, ResourceLocation key, V value) {
      if (this.defaultValueKey.equals(key)) {
         this.defaultValue = value;
      }

      super.register(id, key, value);
   }

   /**
    * Gets the integer ID we use to identify the given object.
    */
   public int getId(@Nullable V value) {
      int i = super.getId(value);
      return i == -1 ? super.getId(this.defaultValue) : i;
   }

   /**
    * Gets the name we use to identify the given object.
    */
   public ResourceLocation getKey(V value) {
      ResourceLocation resourcelocation = super.getKey(value);
      return resourcelocation == null ? this.defaultValueKey : resourcelocation;
   }

   public V get(@Nullable ResourceLocation name) {
      V v = (V)this.func_212608_b(name);
      return (V)(v == null ? this.defaultValue : v);
   }

   /**
    * Gets the object identified by the given ID.
    */
   @Nonnull
   public V get(int id) {
      V v = (V)super.get(id);
      return (V)(v == null ? this.defaultValue : v);
   }

   @Nonnull
   public V getRandom(Random random) {
      V v = (V)super.getRandom(random);
      return (V)(v == null ? this.defaultValue : v);
   }

   public ResourceLocation func_212609_b() {
      return this.defaultValueKey;
   }
}