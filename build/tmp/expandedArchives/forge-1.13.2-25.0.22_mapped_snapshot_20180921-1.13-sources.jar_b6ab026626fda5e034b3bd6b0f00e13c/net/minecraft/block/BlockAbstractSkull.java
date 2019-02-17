package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class BlockAbstractSkull extends BlockContainer {
   private final BlockSkull.ISkullType skullType;

   public BlockAbstractSkull(BlockSkull.ISkullType p_i48452_1_, Block.Properties p_i48452_2_) {
      super(p_i48452_2_);
      this.skullType = p_i48452_1_;
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   /**
    * @deprecated call via {@link IBlockState#hasCustomBreakingProgress()} whenever possible. Implementing/overriding is
    * fine.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomBreakingProgress(IBlockState state) {
      return true;
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntitySkull();
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
       super.dropBlockAsItemWithChance(state, worldIn, pos, chancePerItem, fortune);
   }

   /**
    * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect
    * this block
    */
   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (!worldIn.isRemote && player.abilities.isCreativeMode) {
         TileEntitySkull.disableDrop(worldIn, pos);
      }

      this.dropBlockAsItemWithChance(state, worldIn, pos, 1.0f, 0);
      super.onBlockHarvested(worldIn, pos, state, player);
   }

   /**
    * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
    * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
    * <p>
    * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that does
    * not fit the other descriptions and will generally cause other things not to connect to the face.
    * 
    * @return an approximation of the form of the given face
    * @deprecated call via {@link IBlockState#getBlockFaceShape(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock() && !worldIn.isRemote) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (false && tileentity instanceof TileEntitySkull) { //Forge: Moved to getDrops
            TileEntitySkull tileentityskull = (TileEntitySkull)tileentity;
            if (tileentityskull.shouldDrop()) {
               ItemStack itemstack = this.getItem(worldIn, pos, state);
               Block block = tileentityskull.getBlockState().getBlock();
               if ((block == Blocks.PLAYER_HEAD || block == Blocks.PLAYER_WALL_HEAD) && tileentityskull.getPlayerProfile() != null) {
                  NBTTagCompound nbttagcompound = new NBTTagCompound();
                  NBTUtil.writeGameProfile(nbttagcompound, tileentityskull.getPlayerProfile());
                  itemstack.getOrCreateTag().setTag("SkullOwner", nbttagcompound);
               }

               spawnAsEntity(worldIn, pos, itemstack);
            }
         }

         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   @Override
   public void getDrops(IBlockState state, net.minecraft.util.NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof TileEntitySkull) {
         TileEntitySkull skull = (TileEntitySkull)te;
         if (skull.shouldDrop()) {
            ItemStack ret = getItem(world, pos, state);
            Block block = skull.getBlockState().getBlock();
            if ((block == Blocks.PLAYER_HEAD || block == Blocks.PLAYER_WALL_HEAD) && skull.getPlayerProfile() != null) {
               NBTTagCompound nbt = new NBTTagCompound();
               NBTUtil.writeGameProfile(nbt, skull.getPlayerProfile());
               ret.getOrCreateTag().setTag("SkullOwner", nbt);
            }
            drops.add(ret);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public BlockSkull.ISkullType getSkullType() {
      return this.skullType;
   }
}