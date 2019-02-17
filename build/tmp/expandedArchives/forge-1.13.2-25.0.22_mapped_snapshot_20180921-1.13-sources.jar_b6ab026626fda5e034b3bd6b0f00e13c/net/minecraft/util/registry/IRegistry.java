package net.minecraft.util.registry;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.stats.StatType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGeneratorType;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Attention Modders: This SHOULD NOT be used, you should use ForgeRegistries instead. As it has a cleaner modder facing API.
 * We will be wrapping all of these in our API as nessasary for syncing and management.
 */
public interface IRegistry<T> extends IObjectIntIterable<T> {
   Logger field_212616_e = LogManager.getLogger();
   IRegistry<IRegistry<?>> field_212617_f = new RegistryNamespaced<>();
   @Deprecated IRegistry<Block> field_212618_g = func_212610_a("block", net.minecraftforge.registries.GameData.getWrapperDefaulted(Block.class));
   IRegistry<Fluid> field_212619_h = func_212610_a("fluid", new RegistryNamespacedDefaultedByKey<>(new ResourceLocation("empty")));
   IRegistry<PaintingType> field_212620_i = func_212610_a("motive", new RegistryNamespacedDefaultedByKey<>(new ResourceLocation("kebab")));
   @Deprecated IRegistry<PotionType> field_212621_j = func_212610_a("potion", net.minecraftforge.registries.GameData.getWrapperDefaulted(PotionType.class));
   @Deprecated IRegistry<DimensionType> field_212622_k = func_212610_a("dimension_type", net.minecraftforge.common.DimensionManager.getRegistry());
   IRegistry<ResourceLocation> field_212623_l = func_212610_a("custom_stat", new RegistryNamespaced<>());
   @Deprecated IRegistry<Biome> field_212624_m = func_212610_a("biome", net.minecraftforge.registries.GameData.getWrapper(Biome.class));
   IRegistry<BiomeProviderType<?, ?>> field_212625_n = func_212610_a("biome_source_type", new RegistryNamespaced<>());
   @Deprecated IRegistry<TileEntityType<?>> field_212626_o = func_212610_a("block_entity_type", (IRegistry<TileEntityType<?>>)net.minecraftforge.registries.GameData.getWrapper(TileEntityType.class));
   IRegistry<ChunkGeneratorType<?, ?>> field_212627_p = func_212610_a("chunk_generator_type", new RegistryNamespaced<>());
   @Deprecated IRegistry<Enchantment> field_212628_q = func_212610_a("enchantment", net.minecraftforge.registries.GameData.getWrapper(Enchantment.class));
   IRegistry<EntityType<?>> field_212629_r = func_212610_a("entity_type", new RegistryNamespaced<>());
   @Deprecated IRegistry<Item> field_212630_s = func_212610_a("item",  net.minecraftforge.registries.GameData.getWrapper(Item.class));
   @Deprecated IRegistry<Potion> field_212631_t = func_212610_a("mob_effect", net.minecraftforge.registries.GameData.getWrapper(Potion.class));
   IRegistry<ParticleType<? extends IParticleData>> field_212632_u = func_212610_a("particle_type", new RegistryNamespaced<>());
   @Deprecated IRegistry<SoundEvent> field_212633_v = func_212610_a("sound_event", net.minecraftforge.registries.GameData.getWrapper(SoundEvent.class));
   IRegistry<StatType<?>> field_212634_w = func_212610_a("stats", new RegistryNamespaced<>());

   static <T> IRegistry<T> func_212610_a(String p_212610_0_, IRegistry<T> p_212610_1_) {
      field_212617_f.put(new ResourceLocation(p_212610_0_), p_212610_1_);
      return p_212610_1_;
   }

   static void func_212613_e() {
      field_212617_f.forEach((p_212606_0_) -> {
         if (p_212606_0_.isEmpty()) {
            field_212616_e.error("Registry '{}' was empty after loading", (Object)field_212617_f.getKey(p_212606_0_));
            if (SharedConstants.developmentMode) {
               throw new IllegalStateException("Registry: '" + field_212617_f.getKey(p_212606_0_) + "' is empty, not allowed, fix me!");
            }
         }

         if (p_212606_0_ instanceof RegistryNamespacedDefaultedByKey) {
            ResourceLocation resourcelocation = p_212606_0_.func_212609_b();
            Validate.notNull(p_212606_0_.func_212608_b(resourcelocation), "Missing default of DefaultedMappedRegistry: " + resourcelocation);
         }

      });
   }

   /**
    * Gets the name we use to identify the given object.
    */
   @Nullable
   ResourceLocation getKey(T value);

   T get(@Nullable ResourceLocation name);

   ResourceLocation func_212609_b();

   /**
    * Gets the integer ID we use to identify the given object.
    */
   int getId(@Nullable T value);

   /**
    * Gets the object identified by the given ID.
    */
   @Nullable
   T get(int id);

   Iterator<T> iterator();

   @Nullable
   T func_212608_b(@Nullable ResourceLocation p_212608_1_);

   void register(int id, ResourceLocation key, T value);

   /**
    * Register an object on this registry.
    */
   void put(ResourceLocation key, T value);

   /**
    * Gets all the keys recognized by this registry.
    */
   Set<ResourceLocation> getKeys();

   boolean isEmpty();

   @Nullable
   T getRandom(Random random);

   default Stream<T> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   boolean func_212607_c(ResourceLocation p_212607_1_);
}