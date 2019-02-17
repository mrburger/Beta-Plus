package net.minecraft.enchantment;

import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EnchantmentFrostWalker extends Enchantment {
   public EnchantmentFrostWalker(Enchantment.Rarity rarityIn, EntityEquipmentSlot... slots) {
      super(rarityIn, EnumEnchantmentType.ARMOR_FEET, slots);
   }

   /**
    * Returns the minimal value of enchantability needed on the enchantment level passed.
    */
   public int getMinEnchantability(int enchantmentLevel) {
      return enchantmentLevel * 10;
   }

   /**
    * Returns the maximum value of enchantability nedded on the enchantment level passed.
    */
   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 15;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   /**
    * Returns the maximum level that the enchantment can have.
    */
   public int getMaxLevel() {
      return 2;
   }

   public static void freezeNearby(EntityLivingBase living, World worldIn, BlockPos pos, int level) {
      if (living.onGround) {
         IBlockState iblockstate = Blocks.FROSTED_ICE.getDefaultState();
         float f = (float)Math.min(16, 2 + level);
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(0, 0, 0);

         for(BlockPos.MutableBlockPos blockpos$mutableblockpos1 : BlockPos.getAllInBoxMutable(pos.add((double)(-f), -1.0D, (double)(-f)), pos.add((double)f, -1.0D, (double)f))) {
            if (blockpos$mutableblockpos1.distanceSqToCenter(living.posX, living.posY, living.posZ) <= (double)(f * f)) {
               blockpos$mutableblockpos.setPos(blockpos$mutableblockpos1.getX(), blockpos$mutableblockpos1.getY() + 1, blockpos$mutableblockpos1.getZ());
               IBlockState iblockstate1 = worldIn.getBlockState(blockpos$mutableblockpos);
               if (iblockstate1.isAir()) {
                  IBlockState iblockstate2 = worldIn.getBlockState(blockpos$mutableblockpos1);
                  boolean isFull = iblockstate2.getBlock() == Blocks.WATER && iblockstate2.get(BlockFlowingFluid.LEVEL) == 0; //TODO: Forge, modded waters?
                  if (iblockstate2.getMaterial() == Material.WATER && isFull && iblockstate.isValidPosition(worldIn, blockpos$mutableblockpos1) && worldIn.checkNoEntityCollision(iblockstate, blockpos$mutableblockpos1)) {
                     worldIn.setBlockState(blockpos$mutableblockpos1, iblockstate);
                     worldIn.getPendingBlockTicks().scheduleTick(blockpos$mutableblockpos1.toImmutable(), Blocks.FROSTED_ICE, MathHelper.nextInt(living.getRNG(), 60, 120));
                  }
               }
            }
         }

      }
   }

   /**
    * Determines if the enchantment passed can be applyied together with this enchantment.
    */
   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench) && ench != Enchantments.DEPTH_STRIDER;
   }
}