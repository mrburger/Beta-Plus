package net.minecraft.state;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.EnumFacing;

public class DirectionProperty extends EnumProperty<EnumFacing> {
   protected DirectionProperty(String name, Collection<EnumFacing> values) {
      super(name, EnumFacing.class, values);
   }

   /**
    * Create a new PropertyDirection with all directions that match the given Predicate
    */
   public static DirectionProperty create(String name, Predicate<EnumFacing> filter) {
      return create(name, Arrays.stream(EnumFacing.values()).filter(filter).collect(Collectors.toList()));
   }

   public static DirectionProperty create(String p_196962_0_, EnumFacing... p_196962_1_) {
      return create(p_196962_0_, Lists.newArrayList(p_196962_1_));
   }

   /**
    * Create a new PropertyDirection for the given direction values
    */
   public static DirectionProperty create(String name, Collection<EnumFacing> values) {
      return new DirectionProperty(name, values);
   }
}