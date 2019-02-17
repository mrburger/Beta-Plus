package net.minecraft.world.gen;

import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChunkGeneratorType<C extends IChunkGenSettings, T extends IChunkGenerator<C>> implements IChunkGeneratorFactory<C, T> {
   public static final ChunkGeneratorType<OverworldGenSettings, ChunkGeneratorOverworld> SURFACE = func_212676_a("surface", ChunkGeneratorOverworld::new, OverworldGenSettings::new, true);
   public static final ChunkGeneratorType<NetherGenSettings, ChunkGeneratorNether> CAVES = func_212676_a("caves", ChunkGeneratorNether::new, NetherGenSettings::new, true);
   public static final ChunkGeneratorType<EndGenSettings, ChunkGeneratorEnd> FLOATING_ISLANDS = func_212676_a("floating_islands", ChunkGeneratorEnd::new, EndGenSettings::new, true);
   public static final ChunkGeneratorType<DebugGenSettings, ChunkGeneratorDebug> DEBUG = func_212676_a("debug", ChunkGeneratorDebug::new, DebugGenSettings::new, false);
   public static final ChunkGeneratorType<FlatGenSettings, ChunkGeneratorFlat> FLAT = func_212676_a("flat", ChunkGeneratorFlat::new, FlatGenSettings::new, false);
   private final ResourceLocation id;
   private final IChunkGeneratorFactory<C, T> factory;
   private final boolean isOptionForBuffetWorld;
   private final Supplier<C> settings;

   public static void func_212675_a() {
   }

   public ChunkGeneratorType(IChunkGeneratorFactory<C, T> p_i49808_1_, boolean p_i49808_2_, Supplier<C> p_i49808_3_, ResourceLocation p_i49808_4_) {
      this.factory = p_i49808_1_;
      this.isOptionForBuffetWorld = p_i49808_2_;
      this.settings = p_i49808_3_;
      this.id = p_i49808_4_;
   }

   public static <C extends IChunkGenSettings, T extends IChunkGenerator<C>> ChunkGeneratorType<C, T> func_212676_a(String p_212676_0_, IChunkGeneratorFactory<C, T> p_212676_1_, Supplier<C> p_212676_2_, boolean p_212676_3_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_212676_0_);
      ChunkGeneratorType<C, T> chunkgeneratortype = new ChunkGeneratorType<>(p_212676_1_, p_212676_3_, p_212676_2_, resourcelocation);
      IRegistry.field_212627_p.put(resourcelocation, chunkgeneratortype);
      return chunkgeneratortype;
   }

   public T create(World p_create_1_, BiomeProvider p_create_2_, C p_create_3_) {
      return this.factory.create(p_create_1_, p_create_2_, p_create_3_);
   }

   public C createSettings() {
      return (C)(this.settings.get());
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isOptionForBuffetWorld() {
      return this.isOptionForBuffetWorld;
   }

   public ResourceLocation getId() {
      return this.id;
   }
}