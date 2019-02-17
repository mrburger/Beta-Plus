package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.StringUtils;

public class ItemSkull extends ItemWallOrFloor {
   public ItemSkull(Block p_i48477_1_, Block p_i48477_2_, Item.Properties builder) {
      super(p_i48477_1_, p_i48477_2_, builder);
   }

   public ITextComponent getDisplayName(ItemStack stack) {
      if (stack.getItem() == Items.PLAYER_HEAD && stack.hasTag()) {
         String s = null;
         NBTTagCompound nbttagcompound = stack.getTag();
         if (nbttagcompound.contains("SkullOwner", 8)) {
            s = nbttagcompound.getString("SkullOwner");
         } else if (nbttagcompound.contains("SkullOwner", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("SkullOwner");
            if (nbttagcompound1.contains("Name", 8)) {
               s = nbttagcompound1.getString("Name");
            }
         }

         if (s != null) {
            return new TextComponentTranslation(this.getTranslationKey() + ".named", s);
         }
      }

      return super.getDisplayName(stack);
   }

   /**
    * Called when an ItemStack with NBT data is read to potentially that ItemStack's NBT data
    */
   public boolean updateItemStackNBT(NBTTagCompound nbt) {
      super.updateItemStackNBT(nbt);
      if (nbt.contains("SkullOwner", 8) && !StringUtils.isBlank(nbt.getString("SkullOwner"))) {
         GameProfile gameprofile = new GameProfile((UUID)null, nbt.getString("SkullOwner"));
         gameprofile = TileEntitySkull.updateGameProfile(gameprofile);
         nbt.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
         return true;
      } else {
         return false;
      }
   }
}