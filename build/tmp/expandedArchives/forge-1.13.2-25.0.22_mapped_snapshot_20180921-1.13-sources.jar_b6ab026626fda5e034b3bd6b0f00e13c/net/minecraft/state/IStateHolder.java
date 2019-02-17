package net.minecraft.state;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;

public interface IStateHolder<C> {
   Collection<IProperty<?>> getProperties();

   <T extends Comparable<T>> boolean has(IProperty<T> property);

   /**
    * Get the value of the given Property for this BlockState
    */
   <T extends Comparable<T>> T get(IProperty<T> property);

   <T extends Comparable<T>, V extends T> C with(IProperty<T> property, V value);

   /**
    * Create a version of this BlockState with the given property cycled to the next value in order. If the property was
    * at the highest possible value, it is set to the lowest one instead.
    */
   <T extends Comparable<T>> C cycle(IProperty<T> property);

   ImmutableMap<IProperty<?>, Comparable<?>> getValues();
}