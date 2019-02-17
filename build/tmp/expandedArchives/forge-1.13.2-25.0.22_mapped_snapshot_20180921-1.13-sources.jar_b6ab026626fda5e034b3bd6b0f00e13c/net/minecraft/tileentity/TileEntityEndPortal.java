package net.minecraft.tileentity;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityEndPortal extends TileEntity {
   public TileEntityEndPortal(TileEntityType<?> p_i48283_1_) {
      super(p_i48283_1_);
   }

   public TileEntityEndPortal() {
      this(TileEntityType.END_PORTAL);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderFace(EnumFacing face) {
      return face == EnumFacing.UP;
   }
}