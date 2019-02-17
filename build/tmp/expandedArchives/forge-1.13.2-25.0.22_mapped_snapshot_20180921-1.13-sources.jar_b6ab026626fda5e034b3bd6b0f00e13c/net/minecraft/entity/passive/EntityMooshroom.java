package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityMooshroom extends EntityCow implements net.minecraftforge.common.IShearable {
   public EntityMooshroom(World worldIn) {
      super(EntityType.MOOSHROOM, worldIn);
      this.setSize(0.9F, 1.4F);
      this.spawnableBlock = Blocks.MYCELIUM;
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (itemstack.getItem() == Items.BOWL && this.getGrowingAge() >= 0 && !player.abilities.isCreativeMode) {
         itemstack.shrink(1);
         if (itemstack.isEmpty()) {
            player.setHeldItem(hand, new ItemStack(Items.MUSHROOM_STEW));
         } else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.MUSHROOM_STEW))) {
            player.dropItem(new ItemStack(Items.MUSHROOM_STEW), false);
         }

         return true;
      } else if (false && itemstack.getItem() == Items.SHEARS && this.getGrowingAge() >= 0) { //Forge: Moved to onSheared
         this.world.spawnParticle(Particles.EXPLOSION, this.posX, this.posY + (double)(this.height / 2.0F), this.posZ, 0.0D, 0.0D, 0.0D);
         if (!this.world.isRemote) {
            this.remove();
            EntityCow entitycow = new EntityCow(this.world);
            entitycow.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            entitycow.setHealth(this.getHealth());
            entitycow.renderYawOffset = this.renderYawOffset;
            if (this.hasCustomName()) {
               entitycow.setCustomName(this.getCustomName());
            }

            this.world.spawnEntity(entitycow);

            for(int i = 0; i < 5; ++i) {
               this.world.spawnEntity(new EntityItem(this.world, this.posX, this.posY + (double)this.height, this.posZ, new ItemStack(Blocks.RED_MUSHROOM)));
            }

            itemstack.damageItem(1, player);
            this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
         }

         return true;
      } else {
         return super.processInteract(player, hand);
      }
   }

   public EntityMooshroom createChild(EntityAgeable ageable) {
      return new EntityMooshroom(this.world);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_MUSHROOM_COW;
   }

   @Override
   public boolean isShearable(ItemStack item, net.minecraft.world.IWorldReader world, net.minecraft.util.math.BlockPos pos) {
      return this.getGrowingAge() >= 0;
   }

   @Override
   public java.util.List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IWorld world, net.minecraft.util.math.BlockPos pos, int fortune) {
      java.util.List<ItemStack> ret = new java.util.ArrayList<>();
      this.world.spawnParticle(Particles.EXPLOSION, this.posX, this.posY + (double)(this.height / 2.0F), this.posZ, 0.0D, 0.0D, 0.0D);
      if (!this.world.isRemote) {
         this.remove();
         EntityCow entitycow = new EntityCow(this.world);
         entitycow.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         entitycow.setHealth(this.getHealth());
         entitycow.renderYawOffset = this.renderYawOffset;
         if (this.hasCustomName()) {
            entitycow.setCustomName(this.getCustomName());
         }
         this.world.spawnEntity(entitycow);
         for(int i = 0; i < 5; ++i) {
            ret.add(new ItemStack(Blocks.RED_MUSHROOM));
         }
         this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
      }
      return ret;
   }
}