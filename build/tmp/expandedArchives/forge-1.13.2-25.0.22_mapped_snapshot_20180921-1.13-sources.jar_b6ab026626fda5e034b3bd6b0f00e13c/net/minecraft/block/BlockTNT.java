package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockTNT extends Block {
   public static final BooleanProperty field_212569_a = BlockStateProperties.field_212646_x;

   public BlockTNT(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.getDefaultState().with(field_212569_a, Boolean.valueOf(false)));
   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      if (oldState.getBlock() != state.getBlock()) {
         if (worldIn.isBlockPowered(pos)) {
            this.explode(worldIn, pos);
            worldIn.removeBlock(pos);
         }

      }
   }

   /**
    * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
    * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
    * block, etc.
    */
   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (worldIn.isBlockPowered(pos)) {
         this.explode(worldIn, pos);
         worldIn.removeBlock(pos);
      }

   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
      if (!state.get(field_212569_a)) {
         super.dropBlockAsItemWithChance(state, worldIn, pos, chancePerItem, fortune);
      }
   }

   /**
    * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect
    * this block
    */
   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (!worldIn.isRemote() && !player.isCreative() && state.get(field_212569_a)) {
         this.explode(worldIn, pos);
      }

      super.onBlockHarvested(worldIn, pos, state, player);
   }

   /**
    * Called when this Block is destroyed by an Explosion
    */
   public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
      if (!worldIn.isRemote) {
         EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(worldIn, (double)((float)pos.getX() + 0.5F), (double)pos.getY(), (double)((float)pos.getZ() + 0.5F), explosionIn.getExplosivePlacedBy());
         entitytntprimed.setFuse((short)(worldIn.rand.nextInt(entitytntprimed.getFuse() / 4) + entitytntprimed.getFuse() / 8));
         worldIn.spawnEntity(entitytntprimed);
      }
   }

   public void explode(World p_196534_1_, BlockPos p_196534_2_) {
      this.explode(p_196534_1_, p_196534_2_, (EntityLivingBase)null);
   }

   private void explode(World p_196535_1_, BlockPos p_196535_2_, @Nullable EntityLivingBase p_196535_3_) {
      if (!p_196535_1_.isRemote) {
         EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(p_196535_1_, (double)((float)p_196535_2_.getX() + 0.5F), (double)p_196535_2_.getY(), (double)((float)p_196535_2_.getZ() + 0.5F), p_196535_3_);
         p_196535_1_.spawnEntity(entitytntprimed);
         p_196535_1_.playSound((EntityPlayer)null, entitytntprimed.posX, entitytntprimed.posY, entitytntprimed.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack itemstack = player.getHeldItem(hand);
      Item item = itemstack.getItem();
      if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
         return super.onBlockActivated(state, worldIn, pos, player, hand, side, hitX, hitY, hitZ);
      } else {
         this.explode(worldIn, pos, player);
         worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
         if (item == Items.FLINT_AND_STEEL) {
            itemstack.damageItem(1, player);
         } else {
            itemstack.shrink(1);
         }

         return true;
      }
   }

   public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      if (!worldIn.isRemote && entityIn instanceof EntityArrow) {
         EntityArrow entityarrow = (EntityArrow)entityIn;
         Entity entity = entityarrow.func_212360_k();
         if (entityarrow.isBurning()) {
            this.explode(worldIn, pos, entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null);
            worldIn.removeBlock(pos);
         }
      }

   }

   /**
    * Return whether this block can drop from an explosion.
    */
   public boolean canDropFromExplosion(Explosion explosionIn) {
      return false;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(field_212569_a);
   }
}