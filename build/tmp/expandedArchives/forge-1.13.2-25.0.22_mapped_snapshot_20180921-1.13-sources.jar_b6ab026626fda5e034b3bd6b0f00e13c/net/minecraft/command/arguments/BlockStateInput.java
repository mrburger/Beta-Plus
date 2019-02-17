package net.minecraft.command.arguments;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class BlockStateInput implements Predicate<BlockWorldState> {
   private final IBlockState blockState;
   private final Set<IProperty<?>> matchedProperties;
   @Nullable
   private final NBTTagCompound tileNBT;

   public BlockStateInput(IBlockState p_i47967_1_, Set<IProperty<?>> p_i47967_2_, @Nullable NBTTagCompound p_i47967_3_) {
      this.blockState = p_i47967_1_;
      this.matchedProperties = p_i47967_2_;
      this.tileNBT = p_i47967_3_;
   }

   public IBlockState getBlockState() {
      return this.blockState;
   }

   public boolean test(BlockWorldState p_test_1_) {
      IBlockState iblockstate = p_test_1_.getBlockState();
      if (iblockstate.getBlock() != this.blockState.getBlock()) {
         return false;
      } else {
         for(IProperty<?> iproperty : this.matchedProperties) {
            if (iblockstate.get(iproperty) != this.blockState.get(iproperty)) {
               return false;
            }
         }

         if (this.tileNBT == null) {
            return true;
         } else {
            TileEntity tileentity = p_test_1_.getTileEntity();
            return tileentity != null && NBTUtil.areNBTEquals(this.tileNBT, tileentity.write(new NBTTagCompound()), true);
         }
      }
   }

   public boolean place(WorldServer p_197230_1_, BlockPos p_197230_2_, int p_197230_3_) {
      if (!p_197230_1_.setBlockState(p_197230_2_, this.blockState, p_197230_3_)) {
         return false;
      } else {
         if (this.tileNBT != null) {
            TileEntity tileentity = p_197230_1_.getTileEntity(p_197230_2_);
            if (tileentity != null) {
               NBTTagCompound nbttagcompound = this.tileNBT.copy();
               nbttagcompound.setInt("x", p_197230_2_.getX());
               nbttagcompound.setInt("y", p_197230_2_.getY());
               nbttagcompound.setInt("z", p_197230_2_.getZ());
               tileentity.read(nbttagcompound);
            }
         }

         return true;
      }
   }
}