package net.minecraft.block;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class BlockSkullPlayer extends BlockSkull {
   protected BlockSkullPlayer(Block.Properties builder) {
      super(BlockSkull.Types.PLAYER, builder);
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntitySkull) {
         TileEntitySkull tileentityskull = (TileEntitySkull)tileentity;
         GameProfile gameprofile = null;
         if (stack.hasTag()) {
            NBTTagCompound nbttagcompound = stack.getTag();
            if (nbttagcompound.contains("SkullOwner", 10)) {
               gameprofile = NBTUtil.readGameProfile(nbttagcompound.getCompound("SkullOwner"));
            } else if (nbttagcompound.contains("SkullOwner", 8) && !StringUtils.isBlank(nbttagcompound.getString("SkullOwner"))) {
               gameprofile = new GameProfile((UUID)null, nbttagcompound.getString("SkullOwner"));
            }
         }

         tileentityskull.setPlayerProfile(gameprofile);
      }

   }
}