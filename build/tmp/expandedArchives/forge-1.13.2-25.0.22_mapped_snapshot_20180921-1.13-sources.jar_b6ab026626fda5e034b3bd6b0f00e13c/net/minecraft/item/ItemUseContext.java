package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemUseContext {
   protected final EntityPlayer player;
   protected final float hitX;
   protected final float hitY;
   protected final float hitZ;
   protected final EnumFacing face;
   protected final World world;
   protected final ItemStack item;
   protected final BlockPos pos;

   public ItemUseContext(EntityPlayer playerIn, ItemStack itemIn, BlockPos posIn, EnumFacing faceIn, float hitXIn, float hitYIn, float hitZIn) {
      this(playerIn.world, playerIn, itemIn, posIn, faceIn, hitXIn, hitYIn, hitZIn);
   }

   protected ItemUseContext(World worldIn, @Nullable EntityPlayer playerIn, ItemStack itemIn, BlockPos posIn, EnumFacing faceIn, float hitXIn, float hitYIn, float hitZIn) {
      this.player = playerIn;
      this.face = faceIn;
      this.hitX = hitXIn;
      this.hitY = hitYIn;
      this.hitZ = hitZIn;
      this.pos = posIn;
      this.item = itemIn;
      this.world = worldIn;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public ItemStack getItem() {
      return this.item;
   }

   @Nullable
   public EntityPlayer getPlayer() {
      return this.player;
   }

   public World getWorld() {
      return this.world;
   }

   public EnumFacing getFace() {
      return this.face;
   }

   public float getHitX() {
      return this.hitX;
   }

   public float getHitY() {
      return this.hitY;
   }

   public float getHitZ() {
      return this.hitZ;
   }

   public EnumFacing getPlacementHorizontalFacing() {
      return this.player == null ? EnumFacing.NORTH : this.player.getHorizontalFacing();
   }

   public boolean isPlacerSneaking() {
      return this.player != null && this.player.isSneaking();
   }

   public float getPlacementYaw() {
      return this.player == null ? 0.0F : this.player.rotationYaw;
   }
}