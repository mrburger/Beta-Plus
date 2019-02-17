package net.minecraft.world.dimension;

import java.io.File;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class DimensionType extends net.minecraftforge.registries.ForgeRegistryEntry<DimensionType> {
   public static final DimensionType OVERWORLD = func_212677_a("overworld", new DimensionType(1, "", "", OverworldDimension::new));
   public static final DimensionType NETHER = func_212677_a("the_nether", new DimensionType(0, "_nether", "DIM-1", NetherDimension::new));
   public static final DimensionType THE_END = func_212677_a("the_end", new DimensionType(2, "_end", "DIM1", EndDimension::new));
   private final int id;
   private final String suffix;
   private final String field_212682_f;
   private final java.util.function.Function<DimensionType, ? extends Dimension> factory;
   private final boolean isVanilla;
   private final net.minecraftforge.common.ModDimension modType;
   private final net.minecraft.network.PacketBuffer data;

   public static void func_212680_a() {
   }

   private static DimensionType func_212677_a(String p_212677_0_, DimensionType p_212677_1_) {
      IRegistry.field_212622_k.register(p_212677_1_.id, new ResourceLocation(p_212677_0_), p_212677_1_);
      return p_212677_1_;
   }

   public DimensionType(int p_i49807_1_, String p_i49807_2_, String p_i49807_3_, Supplier<? extends Dimension> p_i49807_4_) {
      this(p_i49807_1_, p_i49807_2_, p_i49807_3_, type -> p_i49807_4_.get(), null, null);
   }

   //Forge, Internal use only. Use DimensionManager instead.
   public DimensionType(int p_i49807_1_, String p_i49807_2_, String p_i49807_3_, java.util.function.Function<DimensionType, ? extends Dimension> p_i49807_4_, @Nullable net.minecraftforge.common.ModDimension modType, @Nullable net.minecraft.network.PacketBuffer data) {
      this.id = p_i49807_1_;
      this.suffix = p_i49807_2_;
      this.field_212682_f = p_i49807_3_;
      this.factory = p_i49807_4_;
      this.isVanilla = this.id >= 0 && this.id <= 2;
      this.modType = modType;
      this.data = data;
   }

   public static Iterable<DimensionType> func_212681_b() {
      return IRegistry.field_212622_k;
   }

   public int getId() {
      return this.id + -1;
   }

   @Deprecated //Forge Do not use, only used for villages backwards compatibility
   public String getSuffix() {
      return isVanilla ? this.suffix : "";
   }

   public File func_212679_a(File p_212679_1_) {
      return this.field_212682_f.isEmpty() ? p_212679_1_ : new File(p_212679_1_, this.field_212682_f);
   }

   public Dimension create() {
      return this.factory.apply(this);
   }

   public String toString() {
      return "DimensionType{" + func_212678_a(this) + "}";
   }

   @Nullable
   public static DimensionType getById(int id) {
      return IRegistry.field_212622_k.get(id - -1);
   }

   public boolean isVanilla() {
      return this.isVanilla;
   }

   @Nullable
   public net.minecraftforge.common.ModDimension getModType() {
      return this.modType;
   }

   @Nullable
   public net.minecraft.network.PacketBuffer getData() {
      return this.data;
   }

   @Nullable
   public static DimensionType byName(ResourceLocation nameIn) {
      return IRegistry.field_212622_k.func_212608_b(nameIn);
   }

   @Nullable
   public static ResourceLocation func_212678_a(DimensionType p_212678_0_) {
      return IRegistry.field_212622_k.getKey(p_212678_0_);
   }
}