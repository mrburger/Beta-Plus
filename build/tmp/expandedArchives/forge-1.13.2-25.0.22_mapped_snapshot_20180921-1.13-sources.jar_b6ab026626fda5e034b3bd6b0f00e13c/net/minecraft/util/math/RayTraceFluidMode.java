package net.minecraft.util.math;

import java.util.function.Predicate;
import net.minecraft.fluid.IFluidState;

public enum RayTraceFluidMode {
   NEVER((p_209542_0_) -> {
      return false;
   }),
   SOURCE_ONLY(IFluidState::isSource),
   ALWAYS((p_209543_0_) -> {
      return !p_209543_0_.isEmpty();
   });

   public final Predicate<IFluidState> predicate;

   private RayTraceFluidMode(Predicate<IFluidState> p_i49529_3_) {
      this.predicate = p_i49529_3_;
   }
}