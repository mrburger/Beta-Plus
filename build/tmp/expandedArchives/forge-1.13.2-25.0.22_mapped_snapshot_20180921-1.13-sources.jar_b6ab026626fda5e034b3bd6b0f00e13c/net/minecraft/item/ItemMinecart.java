package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMinecart extends Item {
   private static final IBehaviorDispenseItem MINECART_DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem() {
      private final BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();

      /**
       * Dispense the specified stack, play the dispense sound and spawn particles.
       */
      public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
         EnumFacing enumfacing = source.getBlockState().get(BlockDispenser.FACING);
         World world = source.getWorld();
         double d0 = source.getX() + (double)enumfacing.getXOffset() * 1.125D;
         double d1 = Math.floor(source.getY()) + (double)enumfacing.getYOffset();
         double d2 = source.getZ() + (double)enumfacing.getZOffset() * 1.125D;
         BlockPos blockpos = source.getBlockPos().offset(enumfacing);
         IBlockState iblockstate = world.getBlockState(blockpos);
         RailShape railshape = iblockstate.getBlock() instanceof BlockRailBase ? ((BlockRailBase)iblockstate.getBlock()).getRailDirection(iblockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
         double d3;
         if (iblockstate.isIn(BlockTags.RAILS)) {
            if (railshape.isAscending()) {
               d3 = 0.6D;
            } else {
               d3 = 0.1D;
            }
         } else {
            if (!iblockstate.isAir(world, blockpos) || !world.getBlockState(blockpos.down()).isIn(BlockTags.RAILS)) {
               return this.behaviourDefaultDispenseItem.dispense(source, stack);
            }

            IBlockState iblockstate1 = world.getBlockState(blockpos.down());
            RailShape railshape1 = iblockstate1.getBlock() instanceof BlockRailBase ? ((BlockRailBase)iblockstate1.getBlock()).getRailDirection(iblockstate1, world, blockpos, null) : RailShape.NORTH_SOUTH;
            if (enumfacing != EnumFacing.DOWN && railshape1.isAscending()) {
               d3 = -0.4D;
            } else {
               d3 = -0.9D;
            }
         }

         EntityMinecart entityminecart = EntityMinecart.create(world, d0, d1 + d3, d2, ((ItemMinecart)stack.getItem()).minecartType);
         if (stack.hasDisplayName()) {
            entityminecart.setCustomName(stack.getDisplayName());
         }

         world.spawnEntity(entityminecart);
         stack.shrink(1);
         return stack;
      }

      /**
       * Play the dispense sound from the specified block.
       */
      protected void playDispenseSound(IBlockSource source) {
         source.getWorld().playEvent(1000, source.getBlockPos(), 0);
      }
   };
   private final EntityMinecart.Type minecartType;

   public ItemMinecart(EntityMinecart.Type minecartTypeIn, Item.Properties builder) {
      super(builder);
      this.minecartType = minecartTypeIn;
      BlockDispenser.registerDispenseBehavior(this, MINECART_DISPENSER_BEHAVIOR);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos();
      IBlockState iblockstate = world.getBlockState(blockpos);
      if (!iblockstate.isIn(BlockTags.RAILS)) {
         return EnumActionResult.FAIL;
      } else {
         ItemStack itemstack = p_195939_1_.getItem();
         if (!world.isRemote) {
            RailShape railshape = iblockstate.getBlock() instanceof BlockRailBase ? ((BlockRailBase)iblockstate.getBlock()).getRailDirection(iblockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
            double d0 = 0.0D;
            if (railshape.isAscending()) {
               d0 = 0.5D;
            }

            EntityMinecart entityminecart = EntityMinecart.create(world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.0625D + d0, (double)blockpos.getZ() + 0.5D, this.minecartType);
            if (itemstack.hasDisplayName()) {
               entityminecart.setCustomName(itemstack.getDisplayName());
            }

            world.spawnEntity(entityminecart);
         }

         itemstack.shrink(1);
         return EnumActionResult.SUCCESS;
      }
   }
}