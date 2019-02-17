package net.minecraft.inventory;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InventoryHelper {
   private static final Random RANDOM = new Random();

   public static void dropInventoryItems(World worldIn, BlockPos pos, IInventory inventory) {
      dropInventoryItems(worldIn, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), inventory);
   }

   public static void dropInventoryItems(World worldIn, Entity entityAt, IInventory inventory) {
      dropInventoryItems(worldIn, entityAt.posX, entityAt.posY, entityAt.posZ, inventory);
   }

   private static void dropInventoryItems(World worldIn, double x, double y, double z, IInventory inventory) {
      for(int i = 0; i < inventory.getSizeInventory(); ++i) {
         ItemStack itemstack = inventory.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            spawnItemStack(worldIn, x, y, z, itemstack);
         }
      }

   }

   public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack) {
      float f = 0.75F;
      float f1 = 0.125F;
      float f2 = RANDOM.nextFloat() * 0.75F + 0.125F;
      float f3 = RANDOM.nextFloat() * 0.75F;
      float f4 = RANDOM.nextFloat() * 0.75F + 0.125F;

      while(!stack.isEmpty()) {
         EntityItem entityitem = new EntityItem(worldIn, x + (double)f2, y + (double)f3, z + (double)f4, stack.split(RANDOM.nextInt(21) + 10));
         float f5 = 0.05F;
         entityitem.motionX = RANDOM.nextGaussian() * (double)0.05F;
         entityitem.motionY = RANDOM.nextGaussian() * (double)0.05F + (double)0.2F;
         entityitem.motionZ = RANDOM.nextGaussian() * (double)0.05F;
         worldIn.spawnEntity(entityitem);
      }

   }
}