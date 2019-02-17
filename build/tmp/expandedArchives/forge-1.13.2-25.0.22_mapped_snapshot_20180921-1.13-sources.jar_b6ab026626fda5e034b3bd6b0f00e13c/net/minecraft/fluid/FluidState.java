package net.minecraft.fluid;

import com.google.common.collect.ImmutableMap;
import net.minecraft.state.AbstractStateHolder;
import net.minecraft.state.IProperty;

public class FluidState extends AbstractStateHolder<Fluid, IFluidState> implements IFluidState {
   public FluidState(Fluid p_i48997_1_, ImmutableMap<IProperty<?>, Comparable<?>> p_i48997_2_) {
      super(p_i48997_1_, p_i48997_2_);
   }

   public Fluid getFluid() {
      return this.object;
   }
}