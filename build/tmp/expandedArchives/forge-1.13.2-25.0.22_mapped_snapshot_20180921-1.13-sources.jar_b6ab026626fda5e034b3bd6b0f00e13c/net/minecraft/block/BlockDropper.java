package net.minecraft.block;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BlockSourceImpl;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockDropper extends BlockDispenser {
   private static final IBehaviorDispenseItem DISPENSE_BEHAVIOR = new BehaviorDefaultDispenseItem();

   public BlockDropper(Block.Properties builder) {
      super(builder);
   }

   protected IBehaviorDispenseItem getBehavior(ItemStack stack) {
      return DISPENSE_BEHAVIOR;
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityDropper();
   }

   protected void dispense(World worldIn, BlockPos pos) {
      BlockSourceImpl blocksourceimpl = new BlockSourceImpl(worldIn, pos);
      TileEntityDispenser tileentitydispenser = blocksourceimpl.getBlockTileEntity();
      int i = tileentitydispenser.getDispenseSlot();
      if (i < 0) {
         worldIn.playEvent(1001, pos, 0);
      } else {
         ItemStack itemstack = tileentitydispenser.getStackInSlot(i);
         if (!itemstack.isEmpty() && net.minecraftforge.items.VanillaInventoryCodeHooks.dropperInsertHook(worldIn, pos, tileentitydispenser, i, itemstack)) {
            EnumFacing enumfacing = worldIn.getBlockState(pos).get(FACING);
            IInventory iinventory = TileEntityHopper.getInventoryAtPosition(worldIn, pos.offset(enumfacing));
            ItemStack itemstack1;
            if (iinventory == null) {
               itemstack1 = DISPENSE_BEHAVIOR.dispense(blocksourceimpl, itemstack);
            } else {
               itemstack1 = TileEntityHopper.putStackInInventoryAllSlots(tileentitydispenser, iinventory, itemstack.copy().split(1), enumfacing.getOpposite());
               if (itemstack1.isEmpty()) {
                  itemstack1 = itemstack.copy();
                  itemstack1.shrink(1);
               } else {
                  itemstack1 = itemstack.copy();
               }
            }

            tileentitydispenser.setInventorySlotContents(i, itemstack1);
         }
      }
   }
}