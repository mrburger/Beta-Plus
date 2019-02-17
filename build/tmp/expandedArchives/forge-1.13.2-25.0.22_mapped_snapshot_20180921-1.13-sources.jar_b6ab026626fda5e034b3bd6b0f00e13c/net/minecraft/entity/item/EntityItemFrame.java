package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItemFrame extends EntityHanging {
   /** Used instead of the inherited Entity.LOGGER for the correct logger class name in the log. */
   private static final Logger PRIVATE_LOGGER = LogManager.getLogger();
   private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityItemFrame.class, DataSerializers.ITEM_STACK);
   private static final DataParameter<Integer> ROTATION = EntityDataManager.createKey(EntityItemFrame.class, DataSerializers.VARINT);
   /** Chance for this item frame's item to drop from the frame. */
   private float itemDropChance = 1.0F;

   public EntityItemFrame(World worldIn) {
      super(EntityType.ITEM_FRAME, worldIn);
   }

   public EntityItemFrame(World worldIn, BlockPos p_i45852_2_, EnumFacing p_i45852_3_) {
      super(EntityType.ITEM_FRAME, worldIn, p_i45852_2_);
      this.updateFacingWithBoundingBox(p_i45852_3_);
   }

   public float getEyeHeight() {
      return 0.0F;
   }

   protected void registerData() {
      this.getDataManager().register(ITEM, ItemStack.EMPTY);
      this.getDataManager().register(ROTATION, 0);
   }

   /**
    * Updates facing and bounding box based on it
    */
   protected void updateFacingWithBoundingBox(EnumFacing facingDirectionIn) {
      Validate.notNull(facingDirectionIn);
      this.facingDirection = facingDirectionIn;
      if (facingDirectionIn.getAxis().isHorizontal()) {
         this.rotationPitch = 0.0F;
         this.rotationYaw = (float)(this.facingDirection.getHorizontalIndex() * 90);
      } else {
         this.rotationPitch = (float)(-90 * facingDirectionIn.getAxisDirection().getOffset());
         this.rotationYaw = 0.0F;
      }

      this.prevRotationPitch = this.rotationPitch;
      this.prevRotationYaw = this.rotationYaw;
      this.updateBoundingBox();
   }

   /**
    * Updates the entity bounding box based on current facing
    */
   protected void updateBoundingBox() {
      if (this.facingDirection != null) {
         double d0 = 0.46875D;
         this.posX = (double)this.hangingPosition.getX() + 0.5D - (double)this.facingDirection.getXOffset() * 0.46875D;
         this.posY = (double)this.hangingPosition.getY() + 0.5D - (double)this.facingDirection.getYOffset() * 0.46875D;
         this.posZ = (double)this.hangingPosition.getZ() + 0.5D - (double)this.facingDirection.getZOffset() * 0.46875D;
         double d1 = (double)this.getWidthPixels();
         double d2 = (double)this.getHeightPixels();
         double d3 = (double)this.getWidthPixels();
         EnumFacing.Axis enumfacing$axis = this.facingDirection.getAxis();
         switch(enumfacing$axis) {
         case X:
            d1 = 1.0D;
            break;
         case Y:
            d2 = 1.0D;
            break;
         case Z:
            d3 = 1.0D;
         }

         d1 = d1 / 32.0D;
         d2 = d2 / 32.0D;
         d3 = d3 / 32.0D;
         this.setBoundingBox(new AxisAlignedBB(this.posX - d1, this.posY - d2, this.posZ - d3, this.posX + d1, this.posY + d2, this.posZ + d3));
      }
   }

   /**
    * checks to make sure painting can be placed there
    */
   public boolean onValidSurface() {
      if (!this.world.isCollisionBoxesEmpty(this, this.getBoundingBox())) {
         return false;
      } else {
         IBlockState iblockstate = this.world.getBlockState(this.hangingPosition.offset(this.facingDirection.getOpposite()));
         return iblockstate.getMaterial().isSolid() || this.facingDirection.getAxis().isHorizontal() && BlockRedstoneDiode.isDiode(iblockstate) ? this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), IS_HANGING_ENTITY).isEmpty() : false;
      }
   }

   public float getCollisionBorderSize() {
      return 0.0F;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (!source.isExplosion() && !this.getDisplayedItem().isEmpty()) {
         if (!this.world.isRemote) {
            this.dropItemOrSelf(source.getTrueSource(), false);
            this.playSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
         }

         return true;
      } else {
         return super.attackEntityFrom(source, amount);
      }
   }

   public int getWidthPixels() {
      return 12;
   }

   public int getHeightPixels() {
      return 12;
   }

   /**
    * Checks if the entity is in range to render.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      double d0 = 16.0D;
      d0 = d0 * 64.0D * getRenderDistanceWeight();
      return distance < d0 * d0;
   }

   /**
    * Called when this entity is broken. Entity parameter may be null.
    */
   public void onBroken(@Nullable Entity brokenEntity) {
      this.playSound(SoundEvents.ENTITY_ITEM_FRAME_BREAK, 1.0F, 1.0F);
      this.dropItemOrSelf(brokenEntity, true);
   }

   public void playPlaceSound() {
      this.playSound(SoundEvents.ENTITY_ITEM_FRAME_PLACE, 1.0F, 1.0F);
   }

   public void dropItemOrSelf(@Nullable Entity entityIn, boolean p_146065_2_) {
      if (this.world.getGameRules().getBoolean("doEntityDrops")) {
         ItemStack itemstack = this.getDisplayedItem();
         this.setDisplayedItem(ItemStack.EMPTY);
         if (entityIn instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entityIn;
            if (entityplayer.abilities.isCreativeMode) {
               this.removeItem(itemstack);
               return;
            }
         }

         if (p_146065_2_) {
            this.entityDropItem(Items.ITEM_FRAME);
         }

         if (!itemstack.isEmpty() && this.rand.nextFloat() < this.itemDropChance) {
            itemstack = itemstack.copy();
            this.removeItem(itemstack);
            this.entityDropItem(itemstack);
         }

      }
   }

   /**
    * Removes the dot representing this frame's position from the map when the item frame is broken.
    */
   private void removeItem(ItemStack stack) {
      if (stack.getItem() instanceof net.minecraft.item.ItemMap) {
         MapData mapdata = ItemMap.getMapData(stack, this.world);
         mapdata.func_212441_a(this.hangingPosition, this.getEntityId());
      }

      stack.setItemFrame((EntityItemFrame)null);
      this.setDisplayedItem(ItemStack.EMPTY); //Forge: Fix MC-124833 Pistons duplicating Items.
   }

   public ItemStack getDisplayedItem() {
      return this.getDataManager().get(ITEM);
   }

   public void setDisplayedItem(ItemStack stack) {
      this.setDisplayedItemWithUpdate(stack, true);
   }

   private void setDisplayedItemWithUpdate(ItemStack stack, boolean p_174864_2_) {
      if (!stack.isEmpty()) {
         stack = stack.copy();
         stack.setCount(1);
         stack.setItemFrame(this);
      }

      this.getDataManager().set(ITEM, stack);
      if (!stack.isEmpty()) {
         this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
      }

      if (p_174864_2_ && this.hangingPosition != null) {
         this.world.updateComparatorOutputLevel(this.hangingPosition, Blocks.AIR);
      }

   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (key.equals(ITEM)) {
         ItemStack itemstack = this.getDisplayedItem();
         if (!itemstack.isEmpty() && itemstack.getItemFrame() != this) {
            itemstack.setItemFrame(this);
         }
      }

   }

   /**
    * Return the rotation of the item currently on this frame.
    */
   public int getRotation() {
      return this.getDataManager().get(ROTATION);
   }

   public void setItemRotation(int rotationIn) {
      this.setRotation(rotationIn, true);
   }

   private void setRotation(int rotationIn, boolean p_174865_2_) {
      this.getDataManager().set(ROTATION, rotationIn % 8);
      if (p_174865_2_ && this.hangingPosition != null) {
         this.world.updateComparatorOutputLevel(this.hangingPosition, Blocks.AIR);
      }

   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      if (!this.getDisplayedItem().isEmpty()) {
         compound.setTag("Item", this.getDisplayedItem().write(new NBTTagCompound()));
         compound.setByte("ItemRotation", (byte)this.getRotation());
         compound.setFloat("ItemDropChance", this.itemDropChance);
      }

      compound.setByte("Facing", (byte)this.facingDirection.getIndex());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      NBTTagCompound nbttagcompound = compound.getCompound("Item");
      if (nbttagcompound != null && !nbttagcompound.isEmpty()) {
         ItemStack itemstack = ItemStack.read(nbttagcompound);
         if (itemstack.isEmpty()) {
            PRIVATE_LOGGER.warn("Unable to load item from: {}", (Object)nbttagcompound);
         }

         this.setDisplayedItemWithUpdate(itemstack, false);
         this.setRotation(compound.getByte("ItemRotation"), false);
         if (compound.contains("ItemDropChance", 99)) {
            this.itemDropChance = compound.getFloat("ItemDropChance");
         }
      }

      this.updateFacingWithBoundingBox(EnumFacing.byIndex(compound.getByte("Facing")));
   }

   public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (!this.world.isRemote) {
         if (this.getDisplayedItem().isEmpty()) {
            if (!itemstack.isEmpty()) {
               this.setDisplayedItem(itemstack);
               if (!player.abilities.isCreativeMode) {
                  itemstack.shrink(1);
               }
            }
         } else {
            this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
            this.setItemRotation(this.getRotation() + 1);
         }
      }

      return true;
   }

   public int getAnalogOutput() {
      return this.getDisplayedItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
   }
}