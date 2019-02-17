package net.minecraft.network.datasync;

import net.minecraft.network.PacketBuffer;

public interface DataSerializer<T> {
   void write(PacketBuffer buf, T value);

   T read(PacketBuffer buf);

   DataParameter<T> createKey(int id);

   T copyValue(T value);
}