package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmorDyeable;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockCauldron extends Block {
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_0_3;
   protected static final VoxelShape INSIDE = Block.makeCuboidShape(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
   protected static final VoxelShape WALLS = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), INSIDE, IBooleanFunction.ONLY_FIRST);

   public BlockCauldron(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(LEVEL, Integer.valueOf(0)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return WALLS;
   }

   public boolean isSolid(IBlockState state) {
      return false;
   }

   public VoxelShape getRaytraceShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return INSIDE;
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      int i = state.get(LEVEL);
      float f = (float)pos.getY() + (6.0F + (float)(3 * i)) / 16.0F;
      if (!worldIn.isRemote && entityIn.isBurning() && i > 0 && entityIn.getBoundingBox().minY <= (double)f) {
         entityIn.extinguish();
         this.setWaterLevel(worldIn, pos, state, i - 1);
      }

   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (itemstack.isEmpty()) {
         return true;
      } else {
         int i = state.get(LEVEL);
         Item item = itemstack.getItem();
         if (item == Items.WATER_BUCKET) {
            if (i < 3 && !worldIn.isRemote) {
               if (!player.abilities.isCreativeMode) {
                  player.setHeldItem(hand, new ItemStack(Items.BUCKET));
               }

               player.addStat(StatList.FILL_CAULDRON);
               this.setWaterLevel(worldIn, pos, state, 3);
               worldIn.playSound((EntityPlayer)null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            return true;
         } else if (item == Items.BUCKET) {
            if (i == 3 && !worldIn.isRemote) {
               if (!player.abilities.isCreativeMode) {
                  itemstack.shrink(1);
                  if (itemstack.isEmpty()) {
                     player.setHeldItem(hand, new ItemStack(Items.WATER_BUCKET));
                  } else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.WATER_BUCKET))) {
                     player.dropItem(new ItemStack(Items.WATER_BUCKET), false);
                  }
               }

               player.addStat(StatList.USE_CAULDRON);
               this.setWaterLevel(worldIn, pos, state, 0);
               worldIn.playSound((EntityPlayer)null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            return true;
         } else if (item == Items.GLASS_BOTTLE) {
            if (i > 0 && !worldIn.isRemote) {
               if (!player.abilities.isCreativeMode) {
                  ItemStack itemstack4 = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), PotionTypes.WATER);
                  player.addStat(StatList.USE_CAULDRON);
                  itemstack.shrink(1);
                  if (itemstack.isEmpty()) {
                     player.setHeldItem(hand, itemstack4);
                  } else if (!player.inventory.addItemStackToInventory(itemstack4)) {
                     player.dropItem(itemstack4, false);
                  } else if (player instanceof EntityPlayerMP) {
                     ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
                  }
               }

               worldIn.playSound((EntityPlayer)null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
               this.setWaterLevel(worldIn, pos, state, i - 1);
            }

            return true;
         } else if (item == Items.POTION && PotionUtils.getPotionFromItem(itemstack) == PotionTypes.WATER) {
            if (i < 3 && !worldIn.isRemote) {
               if (!player.abilities.isCreativeMode) {
                  ItemStack itemstack3 = new ItemStack(Items.GLASS_BOTTLE);
                  player.addStat(StatList.USE_CAULDRON);
                  player.setHeldItem(hand, itemstack3);
                  if (player instanceof EntityPlayerMP) {
                     ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
                  }
               }

               worldIn.playSound((EntityPlayer)null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
               this.setWaterLevel(worldIn, pos, state, i + 1);
            }

            return true;
         } else {
            if (i > 0 && item instanceof ItemArmorDyeable) {
               ItemArmorDyeable itemarmordyeable = (ItemArmorDyeable)item;
               if (itemarmordyeable.hasColor(itemstack) && !worldIn.isRemote) {
                  itemarmordyeable.removeColor(itemstack);
                  this.setWaterLevel(worldIn, pos, state, i - 1);
                  player.addStat(StatList.CLEAN_ARMOR);
                  return true;
               }
            }

            if (i > 0 && item instanceof ItemBanner) {
               if (TileEntityBanner.getPatterns(itemstack) > 0 && !worldIn.isRemote) {
                  ItemStack itemstack2 = itemstack.copy();
                  itemstack2.setCount(1);
                  TileEntityBanner.removeBannerData(itemstack2);
                  player.addStat(StatList.CLEAN_BANNER);
                  if (!player.abilities.isCreativeMode) {
                     itemstack.shrink(1);
                     this.setWaterLevel(worldIn, pos, state, i - 1);
                  }

                  if (itemstack.isEmpty()) {
                     player.setHeldItem(hand, itemstack2);
                  } else if (!player.inventory.addItemStackToInventory(itemstack2)) {
                     player.dropItem(itemstack2, false);
                  } else if (player instanceof EntityPlayerMP) {
                     ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
                  }
               }

               return true;
            } else if (i > 0 && item instanceof ItemBlock) {
               Block block = ((ItemBlock)item).getBlock();
               if (block instanceof BlockShulkerBox && !worldIn.isRemote()) {
                  ItemStack itemstack1 = new ItemStack(Blocks.SHULKER_BOX, 1);
                  if (itemstack.hasTag()) {
                     itemstack1.setTag(itemstack.getTag().copy());
                  }

                  player.setHeldItem(hand, itemstack1);
                  this.setWaterLevel(worldIn, pos, state, i - 1);
                  player.addStat(StatList.field_212740_X);
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   public void setWaterLevel(World worldIn, BlockPos pos, IBlockState state, int level) {
      worldIn.setBlockState(pos, state.with(LEVEL, Integer.valueOf(MathHelper.clamp(level, 0, 3))), 2);
      worldIn.updateComparatorOutputLevel(pos, this);
   }

   /**
    * Called similar to random ticks, but only when it is raining.
    */
   public void fillWithRain(World worldIn, BlockPos pos) {
      if (worldIn.rand.nextInt(20) == 1) {
         float f = worldIn.getBiome(pos).getTemperature(pos);
         if (!(f < 0.15F)) {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            if (iblockstate.get(LEVEL) < 3) {
               worldIn.setBlockState(pos, iblockstate.cycle(LEVEL), 2);
            }

         }
      }
   }

   /**
    * @deprecated call via {@link IBlockState#hasComparatorInputOverride()} whenever possible. Implementing/overriding
    * is fine.
    */
   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   /**
    * @deprecated call via {@link IBlockState#getComparatorInputOverride(World,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      return blockState.get(LEVEL);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(LEVEL);
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
      if (face == EnumFacing.UP) {
         return BlockFaceShape.BOWL;
      } else {
         return face == EnumFacing.DOWN ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
      }
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}