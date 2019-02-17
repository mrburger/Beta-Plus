package net.minecraft.client.settings;

import com.google.common.collect.ForwardingList;
import java.util.List;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HotbarSnapshot extends ForwardingList<ItemStack> {
   private final NonNullList<ItemStack> hotbarItems = NonNullList.withSize(InventoryPlayer.getHotbarSize(), ItemStack.EMPTY);

   protected List<ItemStack> delegate() {
      return this.hotbarItems;
   }

   public NBTTagList createTag() {
      NBTTagList nbttaglist = new NBTTagList();

      for(ItemStack itemstack : this.delegate()) {
         nbttaglist.add((INBTBase)itemstack.write(new NBTTagCompound()));
      }

      return nbttaglist;
   }

   public void fromTag(NBTTagList tag) {
      List<ItemStack> list = this.delegate();

      for(int i = 0; i < list.size(); ++i) {
         list.set(i, ItemStack.read(tag.getCompound(i)));
      }

   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.delegate()) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }
}