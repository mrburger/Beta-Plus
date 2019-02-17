package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.JsonUtils;

public class NBTPredicate {
   /** The predicate that matches any NBT tag. */
   public static final NBTPredicate ANY = new NBTPredicate((NBTTagCompound)null);
   @Nullable
   private final NBTTagCompound tag;

   public NBTPredicate(@Nullable NBTTagCompound tag) {
      this.tag = tag;
   }

   public boolean test(ItemStack item) {
      return this == ANY ? true : this.test(item.getTag());
   }

   public boolean test(Entity entityIn) {
      return this == ANY ? true : this.test(writeToNBTWithSelectedItem(entityIn));
   }

   public boolean test(@Nullable INBTBase nbt) {
      if (nbt == null) {
         return this == ANY;
      } else {
         return this.tag == null || NBTUtil.areNBTEquals(this.tag, nbt, true);
      }
   }

   public JsonElement serialize() {
      return (JsonElement)(this != ANY && this.tag != null ? new JsonPrimitive(this.tag.toString()) : JsonNull.INSTANCE);
   }

   public static NBTPredicate deserialize(@Nullable JsonElement json) {
      if (json != null && !json.isJsonNull()) {
         NBTTagCompound nbttagcompound;
         try {
            nbttagcompound = JsonToNBT.getTagFromJson(JsonUtils.getString(json, "nbt"));
         } catch (CommandSyntaxException commandsyntaxexception) {
            throw new JsonSyntaxException("Invalid nbt tag: " + commandsyntaxexception.getMessage());
         }

         return new NBTPredicate(nbttagcompound);
      } else {
         return ANY;
      }
   }

   public static NBTTagCompound writeToNBTWithSelectedItem(Entity entityIn) {
      NBTTagCompound nbttagcompound = entityIn.writeWithoutTypeId(new NBTTagCompound());
      if (entityIn instanceof EntityPlayer) {
         ItemStack itemstack = ((EntityPlayer)entityIn).inventory.getCurrentItem();
         if (!itemstack.isEmpty()) {
            nbttagcompound.setTag("SelectedItem", itemstack.write(new NBTTagCompound()));
         }
      }

      return nbttagcompound;
   }
}