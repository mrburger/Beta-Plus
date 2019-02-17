package net.minecraft.entity.item;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityMinecartChest extends EntityMinecartContainer {
   public EntityMinecartChest(World worldIn) {
      super(EntityType.CHEST_MINECART, worldIn);
   }

   public EntityMinecartChest(World worldIn, double x, double y, double z) {
      super(EntityType.CHEST_MINECART, x, y, z, worldIn);
   }

   public void killMinecart(DamageSource source) {
      super.killMinecart(source);
      if (this.world.getGameRules().getBoolean("doEntityDrops")) {
         this.entityDropItem(Blocks.CHEST);
      }

   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return 27;
   }

   public EntityMinecart.Type getMinecartType() {
      return EntityMinecart.Type.CHEST;
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.CHEST.getDefaultState().with(BlockChest.FACING, EnumFacing.NORTH);
   }

   public int getDefaultDisplayTileOffset() {
      return 8;
   }

   public String getGuiID() {
      return "minecraft:chest";
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      this.addLoot(playerIn);
      return new ContainerChest(playerInventory, this, playerIn);
   }
}