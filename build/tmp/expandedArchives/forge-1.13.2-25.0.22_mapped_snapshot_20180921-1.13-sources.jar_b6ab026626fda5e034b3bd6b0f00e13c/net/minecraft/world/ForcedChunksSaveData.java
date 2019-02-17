package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

public class ForcedChunksSaveData extends WorldSavedData {
   private LongSet field_212439_a = new LongOpenHashSet();

   public ForcedChunksSaveData(String p_i49814_1_) {
      super(p_i49814_1_);
   }

   /**
    * reads in data from the NBTTagCompound into this MapDataBase
    */
   public void read(NBTTagCompound nbt) {
      this.field_212439_a = new LongOpenHashSet(nbt.getLongArray("Forced"));
   }

   public NBTTagCompound write(NBTTagCompound compound) {
      compound.setLongArray("Forced", this.field_212439_a.toLongArray());
      return compound;
   }

   public LongSet func_212438_a() {
      return this.field_212439_a;
   }
}