package net.minecraft.world.chunk;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockStatePaletteLinear<T> implements IBlockStatePalette<T> {
   private final ObjectIntIdentityMap<T> registry;
   private final T[] states;
   private final IBlockStatePaletteResizer<T> resizeHandler;
   private final Function<NBTTagCompound, T> deserializer;
   private final int bits;
   private int arraySize;

   public BlockStatePaletteLinear(ObjectIntIdentityMap<T> p_i48962_1_, int p_i48962_2_, IBlockStatePaletteResizer<T> p_i48962_3_, Function<NBTTagCompound, T> p_i48962_4_) {
      this.registry = p_i48962_1_;
      this.states = (T[])(new Object[1 << p_i48962_2_]);
      this.bits = p_i48962_2_;
      this.resizeHandler = p_i48962_3_;
      this.deserializer = p_i48962_4_;
   }

   public int idFor(T state) {
      for(int i = 0; i < this.arraySize; ++i) {
         if (this.states[i] == state) {
            return i;
         }
      }

      int j = this.arraySize;
      if (j < this.states.length) {
         this.states[j] = state;
         ++this.arraySize;
         return j;
      } else {
         return this.resizeHandler.onResize(this.bits + 1, state);
      }
   }

   /**
    * Gets the block state by the palette id.
    */
   @Nullable
   public T get(int indexKey) {
      return (T)(indexKey >= 0 && indexKey < this.arraySize ? this.states[indexKey] : null);
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer buf) {
      this.arraySize = buf.readVarInt();

      for(int i = 0; i < this.arraySize; ++i) {
         this.states[i] = this.registry.getByValue(buf.readVarInt());
      }

   }

   public void write(PacketBuffer buf) {
      buf.writeVarInt(this.arraySize);

      for(int i = 0; i < this.arraySize; ++i) {
         buf.writeVarInt(this.registry.get(this.states[i]));
      }

   }

   public int getSerializedSize() {
      int i = PacketBuffer.getVarIntSize(this.func_202137_b());

      for(int j = 0; j < this.func_202137_b(); ++j) {
         i += PacketBuffer.getVarIntSize(this.registry.get(this.states[j]));
      }

      return i;
   }

   public int func_202137_b() {
      return this.arraySize;
   }

   public void read(NBTTagList nbt) {
      for(int i = 0; i < nbt.size(); ++i) {
         this.states[i] = this.deserializer.apply(nbt.getCompound(i));
      }

      this.arraySize = nbt.size();
   }
}