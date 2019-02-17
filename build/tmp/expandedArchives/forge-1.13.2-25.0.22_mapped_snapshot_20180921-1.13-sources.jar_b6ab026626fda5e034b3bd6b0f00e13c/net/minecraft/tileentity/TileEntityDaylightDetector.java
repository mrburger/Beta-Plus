package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ITickable;

public class TileEntityDaylightDetector extends TileEntity implements ITickable {
   public TileEntityDaylightDetector() {
      super(TileEntityType.DAYLIGHT_DETECTOR);
   }

   public void tick() {
      if (this.world != null && !this.world.isRemote && this.world.getGameTime() % 20L == 0L) {
         IBlockState iblockstate = this.getBlockState();
         Block block = iblockstate.getBlock();
         if (block instanceof BlockDaylightDetector) {
            BlockDaylightDetector.updatePower(iblockstate, this.world, this.pos);
         }
      }

   }
}