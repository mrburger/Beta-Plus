package net.minecraft.world.biome.provider;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class BiomeProviderType<C extends IBiomeProviderSettings, T extends BiomeProvider> {
   public static final BiomeProviderType<CheckerboardBiomeProviderSettings, CheckerboardBiomeProvider> CHECKERBOARD = func_212581_a("checkerboard", CheckerboardBiomeProvider::new, CheckerboardBiomeProviderSettings::new);
   public static final BiomeProviderType<SingleBiomeProviderSettings, SingleBiomeProvider> FIXED = func_212581_a("fixed", SingleBiomeProvider::new, SingleBiomeProviderSettings::new);
   public static final BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider> VANILLA_LAYERED = func_212581_a("vanilla_layered", OverworldBiomeProvider::new, OverworldBiomeProviderSettings::new);
   public static final BiomeProviderType<EndBiomeProviderSettings, EndBiomeProvider> THE_END = func_212581_a("the_end", EndBiomeProvider::new, EndBiomeProviderSettings::new);
   private final ResourceLocation key;
   private final Function<C, T> factory;
   private final Supplier<C> settingsFactory;

   public static void func_212580_a() {
   }

   public BiomeProviderType(Function<C, T> p_i49812_1_, Supplier<C> p_i49812_2_, ResourceLocation p_i49812_3_) {
      this.factory = p_i49812_1_;
      this.settingsFactory = p_i49812_2_;
      this.key = p_i49812_3_;
   }

   public static <C extends IBiomeProviderSettings, T extends BiomeProvider> BiomeProviderType<C, T> func_212581_a(String p_212581_0_, Function<C, T> p_212581_1_, Supplier<C> p_212581_2_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_212581_0_);
      BiomeProviderType<C, T> biomeprovidertype = new BiomeProviderType<>(p_212581_1_, p_212581_2_, resourcelocation);
      IRegistry.field_212625_n.put(resourcelocation, biomeprovidertype);
      return biomeprovidertype;
   }

   public T create(C settings) {
      return (T)(this.factory.apply(settings));
   }

   public C createSettings() {
      return (C)(this.settingsFactory.get());
   }

   public ResourceLocation getKey() {
      return this.key;
   }
}