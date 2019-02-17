package net.minecraft.world.chunk;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockStatePaletteRegistry<T> implements IBlockStatePalette<T> {
   private final ObjectIntIdentityMap<T> registry;
   private final T defaultState;

   public BlockStatePaletteRegistry(ObjectIntIdentityMap<T> p_i48965_1_, T p_i48965_2_) {
      this.registry = p_i48965_1_;
      this.defaultState = p_i48965_2_;
   }

   public int idFor(T state) {
      int i = this.registry.get(state);
      return i == -1 ? 0 : i;
   }

   /**
    * Gets the block state by the palette id.
    */
   public T get(int indexKey) {
      T t = this.registry.getByValue(indexKey);
      return (T)(t == null ? this.defaultState : t);
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer buf) {
   }

   public void write(PacketBuffer buf) {
   }

   public int getSerializedSize() {
      return PacketBuffer.getVarIntSize(0);
   }

   public void read(NBTTagList nbt) {
   }
}