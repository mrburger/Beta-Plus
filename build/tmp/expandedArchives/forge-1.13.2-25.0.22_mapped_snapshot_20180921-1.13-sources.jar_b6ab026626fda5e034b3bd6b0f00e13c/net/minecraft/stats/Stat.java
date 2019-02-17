package net.minecraft.stats;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Stat<T> extends ScoreCriteria {
   private final IStatFormater formatter;
   private final T value;
   private final StatType<T> type;

   protected Stat(StatType<T> p_i47903_1_, T p_i47903_2_, IStatFormater p_i47903_3_) {
      super(buildName(p_i47903_1_, p_i47903_2_));
      this.type = p_i47903_1_;
      this.formatter = p_i47903_3_;
      this.value = p_i47903_2_;
   }

   public static <T> String buildName(StatType<T> p_197918_0_, T p_197918_1_) {
      return locationToKey(IRegistry.field_212634_w.getKey(p_197918_0_)) + ":" + locationToKey(p_197918_0_.getRegistry().getKey(p_197918_1_));
   }

   private static <T> String locationToKey(@Nullable ResourceLocation p_197919_0_) {
      return p_197919_0_.toString().replace(':', '.');
   }

   public StatType<T> getType() {
      return this.type;
   }

   public T getValue() {
      return this.value;
   }

   @OnlyIn(Dist.CLIENT)
   public String format(int number) {
      return this.formatter.format(number);
   }

   public boolean equals(Object p_equals_1_) {
      return this == p_equals_1_ || p_equals_1_ instanceof Stat && Objects.equals(this.getName(), ((Stat)p_equals_1_).getName());
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   public String toString() {
      return "Stat{name=" + this.getName() + ", formatter=" + this.formatter + '}';
   }
}