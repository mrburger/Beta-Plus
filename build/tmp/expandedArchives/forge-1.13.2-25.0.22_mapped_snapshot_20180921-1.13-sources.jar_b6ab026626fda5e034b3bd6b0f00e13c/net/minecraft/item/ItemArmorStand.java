package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Rotations;
import net.minecraft.world.World;

public class ItemArmorStand extends Item {
   public ItemArmorStand(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      EnumFacing enumfacing = p_195939_1_.getFace();
      if (enumfacing == EnumFacing.DOWN) {
         return EnumActionResult.FAIL;
      } else {
         World world = p_195939_1_.getWorld();
         BlockItemUseContext blockitemusecontext = new BlockItemUseContext(p_195939_1_);
         BlockPos blockpos = blockitemusecontext.getPos();
         BlockPos blockpos1 = blockpos.up();
         if (blockitemusecontext.canPlace() && world.getBlockState(blockpos1).isReplaceable(blockitemusecontext)) {
            double d0 = (double)blockpos.getX();
            double d1 = (double)blockpos.getY();
            double d2 = (double)blockpos.getZ();
            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));
            if (!list.isEmpty()) {
               return EnumActionResult.FAIL;
            } else {
               ItemStack itemstack = p_195939_1_.getItem();
               if (!world.isRemote) {
                  world.removeBlock(blockpos);
                  world.removeBlock(blockpos1);
                  EntityArmorStand entityarmorstand = new EntityArmorStand(world, d0 + 0.5D, d1, d2 + 0.5D);
                  float f = (float)MathHelper.floor((MathHelper.wrapDegrees(p_195939_1_.getPlacementYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                  entityarmorstand.setLocationAndAngles(d0 + 0.5D, d1, d2 + 0.5D, f, 0.0F);
                  this.applyRandomRotations(entityarmorstand, world.rand);
                  EntityType.func_208048_a(world, p_195939_1_.getPlayer(), entityarmorstand, itemstack.getTag());
                  world.spawnEntity(entityarmorstand);
                  world.playSound((EntityPlayer)null, entityarmorstand.posX, entityarmorstand.posY, entityarmorstand.posZ, SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
               }

               itemstack.shrink(1);
               return EnumActionResult.SUCCESS;
            }
         } else {
            return EnumActionResult.FAIL;
         }
      }
   }

   private void applyRandomRotations(EntityArmorStand armorStand, Random rand) {
      Rotations rotations = armorStand.getHeadRotation();
      float f = rand.nextFloat() * 5.0F;
      float f1 = rand.nextFloat() * 20.0F - 10.0F;
      Rotations rotations1 = new Rotations(rotations.getX() + f, rotations.getY() + f1, rotations.getZ());
      armorStand.setHeadRotation(rotations1);
      rotations = armorStand.getBodyRotation();
      f = rand.nextFloat() * 10.0F - 5.0F;
      rotations1 = new Rotations(rotations.getX(), rotations.getY() + f, rotations.getZ());
      armorStand.setBodyRotation(rotations1);
   }
}