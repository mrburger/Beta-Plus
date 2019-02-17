package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockItemUseContext extends ItemUseContext {
   private final BlockPos field_196014_j;
   protected boolean field_196013_a = true;

   public BlockItemUseContext(ItemUseContext context) {
      this(context.getWorld(), context.getPlayer(), context.getItem(), context.getPos(), context.getFace(), context.getHitX(), context.getHitY(), context.getHitZ());
   }

   public BlockItemUseContext(World worldIn, @Nullable EntityPlayer playerIn, ItemStack itemIn, BlockPos posIn, EnumFacing faceIn, float hitXIn, float hitYIn, float hitZIn) {
      super(worldIn, playerIn, itemIn, posIn, faceIn, hitXIn, hitYIn, hitZIn);
      this.field_196014_j = this.pos.offset(this.face);
      this.field_196013_a = this.getWorld().getBlockState(this.pos).isReplaceable(this);
   }

   public BlockPos getPos() {
      return this.field_196013_a ? this.pos : this.field_196014_j;
   }

   public boolean canPlace() {
      return this.field_196013_a || this.getWorld().getBlockState(this.getPos()).isReplaceable(this);
   }

   public boolean replacingClickedOnBlock() {
      return this.field_196013_a;
   }

   public EnumFacing getNearestLookingDirection() {
      return EnumFacing.getFacingDirections(this.player)[0];
   }

   public EnumFacing[] getNearestLookingDirections() {
      EnumFacing[] aenumfacing = EnumFacing.getFacingDirections(this.player);
      if (this.field_196013_a) {
         return aenumfacing;
      } else {
         int i;
         for(i = 0; i < aenumfacing.length && aenumfacing[i] != this.face.getOpposite(); ++i) {
            ;
         }

         if (i > 0) {
            System.arraycopy(aenumfacing, 0, aenumfacing, 1, i);
            aenumfacing[0] = this.face.getOpposite();
         }

         return aenumfacing;
      }
   }
}