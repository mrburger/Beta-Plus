package net.minecraft.tileentity;

public class TileEntityTrappedChest extends TileEntityChest {
   public TileEntityTrappedChest() {
      super(TileEntityType.TRAPPED_CHEST);
   }

   protected void onOpenOrClose() {
      super.onOpenOrClose();
      this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockState().getBlock());
   }
}