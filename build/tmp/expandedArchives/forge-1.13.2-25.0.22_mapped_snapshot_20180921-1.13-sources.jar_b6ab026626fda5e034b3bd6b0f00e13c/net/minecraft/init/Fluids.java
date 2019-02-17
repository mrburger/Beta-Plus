package net.minecraft.init;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class Fluids {
   private static final Set<Fluid> CACHE;
   public static final Fluid EMPTY;
   public static final FlowingFluid FLOWING_WATER;
   public static final FlowingFluid WATER;
   public static final FlowingFluid FLOWING_LAVA;
   public static final FlowingFluid LAVA;

   private static Fluid getRegisteredFluid(String id) {
      Fluid fluid = IRegistry.field_212619_h.get(new ResourceLocation(id));
      if (!CACHE.add(fluid)) {
         throw new IllegalStateException("Invalid Fluid requested: " + id);
      } else {
         return fluid;
      }
   }

   static {
      if (!Bootstrap.isRegistered()) {
         throw new RuntimeException("Accessed Fluids before Bootstrap!");
      } else {
         CACHE = Sets.newHashSet((Fluid)null);
         EMPTY = getRegisteredFluid("empty");
         FLOWING_WATER = (FlowingFluid)getRegisteredFluid("flowing_water");
         WATER = (FlowingFluid)getRegisteredFluid("water");
         FLOWING_LAVA = (FlowingFluid)getRegisteredFluid("flowing_lava");
         LAVA = (FlowingFluid)getRegisteredFluid("lava");
         CACHE.clear();
      }
   }
}