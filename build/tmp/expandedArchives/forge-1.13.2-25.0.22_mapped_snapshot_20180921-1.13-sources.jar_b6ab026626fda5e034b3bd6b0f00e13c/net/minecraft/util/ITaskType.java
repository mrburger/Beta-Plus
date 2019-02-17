package net.minecraft.util;

import java.util.function.BiConsumer;
import javax.annotation.Nullable;

public interface ITaskType<K, T extends ITaskType<K, T>> {
   @Nullable
   T getPreviousTaskType();

   void acceptInRange(K pos, BiConsumer<K, T> consumer);
}