package net.minecraft.tags;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class NetworkTagCollection<T> extends TagCollection<T> {
   private final IRegistry<T> registry;

   public NetworkTagCollection(IRegistry<T> p_i49817_1_, String p_i49817_2_, String p_i49817_3_) {
      super(p_i49817_1_::func_212607_c, p_i49817_1_::func_212608_b, p_i49817_2_, false, p_i49817_3_);
      this.registry = p_i49817_1_;
   }

   public void write(PacketBuffer buf) {
      buf.writeVarInt(this.getTagMap().size());

      for(Entry<ResourceLocation, Tag<T>> entry : this.getTagMap().entrySet()) {
         buf.writeResourceLocation(entry.getKey());
         buf.writeVarInt(entry.getValue().getAllElements().size());

         for(T t : entry.getValue().getAllElements()) {
            buf.writeVarInt(this.registry.getId(t));
         }
      }

   }

   public void read(PacketBuffer buf) {
      int i = buf.readVarInt();

      for(int j = 0; j < i; ++j) {
         ResourceLocation resourcelocation = buf.readResourceLocation();
         int k = buf.readVarInt();
         List<T> list = Lists.newArrayList();

         for(int l = 0; l < k; ++l) {
            list.add(this.registry.get(buf.readVarInt()));
         }

         this.getTagMap().put(resourcelocation, Tag.Builder.<T>create().addAll(list).build(resourcelocation));
      }

   }
}