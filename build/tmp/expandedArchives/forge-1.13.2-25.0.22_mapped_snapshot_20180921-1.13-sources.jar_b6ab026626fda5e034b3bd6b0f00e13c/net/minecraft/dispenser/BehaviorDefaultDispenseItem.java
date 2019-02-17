package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDefaultDispenseItem implements IBehaviorDispenseItem {
   public final ItemStack dispense(IBlockSource p_dispense_1_, ItemStack p_dispense_2_) {
      ItemStack itemstack = this.dispenseStack(p_dispense_1_, p_dispense_2_);
      this.playDispenseSound(p_dispense_1_);
      this.spawnDispenseParticles(p_dispense_1_, p_dispense_1_.getBlockState().get(BlockDispenser.FACING));
      return itemstack;
   }

   /**
    * Dispense the specified stack, play the dispense sound and spawn particles.
    */
   protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
      EnumFacing enumfacing = source.getBlockState().get(BlockDispenser.FACING);
      IPosition iposition = BlockDispenser.getDispensePosition(source);
      ItemStack itemstack = stack.split(1);
      doDispense(source.getWorld(), itemstack, 6, enumfacing, iposition);
      return stack;
   }

   public static void doDispense(World worldIn, ItemStack stack, int speed, EnumFacing facing, IPosition position) {
      double d0 = position.getX();
      double d1 = position.getY();
      double d2 = position.getZ();
      if (facing.getAxis() == EnumFacing.Axis.Y) {
         d1 = d1 - 0.125D;
      } else {
         d1 = d1 - 0.15625D;
      }

      EntityItem entityitem = new EntityItem(worldIn, d0, d1, d2, stack);
      double d3 = worldIn.rand.nextDouble() * 0.1D + 0.2D;
      entityitem.motionX = (double)facing.getXOffset() * d3;
      entityitem.motionY = (double)0.2F;
      entityitem.motionZ = (double)facing.getZOffset() * d3;
      entityitem.motionX += worldIn.rand.nextGaussian() * (double)0.0075F * (double)speed;
      entityitem.motionY += worldIn.rand.nextGaussian() * (double)0.0075F * (double)speed;
      entityitem.motionZ += worldIn.rand.nextGaussian() * (double)0.0075F * (double)speed;
      worldIn.spawnEntity(entityitem);
   }

   /**
    * Play the dispense sound from the specified block.
    */
   protected void playDispenseSound(IBlockSource source) {
      source.getWorld().playEvent(1000, source.getBlockPos(), 0);
   }

   /**
    * Order clients to display dispense particles from the specified block and facing.
    */
   protected void spawnDispenseParticles(IBlockSource source, EnumFacing facingIn) {
      source.getWorld().playEvent(2000, source.getBlockPos(), facingIn.getIndex());
   }
}