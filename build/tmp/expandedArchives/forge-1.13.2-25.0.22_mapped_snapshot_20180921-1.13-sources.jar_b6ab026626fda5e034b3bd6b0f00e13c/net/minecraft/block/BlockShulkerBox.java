package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockShulkerBox extends BlockContainer {
   public static final EnumProperty<EnumFacing> FACING = BlockDirectional.FACING;
   @Nullable
   private final EnumDyeColor color;

   public BlockShulkerBox(@Nullable EnumDyeColor p_i48334_1_, Block.Properties p_i48334_2_) {
      super(p_i48334_2_);
      this.color = p_i48334_1_;
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.UP));
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityShulkerBox(this.color);
   }

   /**
    * @deprecated call via {@link IBlockState#causesSuffocation()} whenever possible. Implementing/overriding is fine.
    */
   public boolean causesSuffocation(IBlockState state) {
      return true;
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

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      } else if (player.isSpectator()) {
         return true;
      } else {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityShulkerBox) {
            EnumFacing enumfacing = state.get(FACING);
            boolean flag;
            if (((TileEntityShulkerBox)tileentity).getAnimationStatus() == TileEntityShulkerBox.AnimationStatus.CLOSED) {
               AxisAlignedBB axisalignedbb = VoxelShapes.fullCube().getBoundingBox().expand((double)(0.5F * (float)enumfacing.getXOffset()), (double)(0.5F * (float)enumfacing.getYOffset()), (double)(0.5F * (float)enumfacing.getZOffset())).contract((double)enumfacing.getXOffset(), (double)enumfacing.getYOffset(), (double)enumfacing.getZOffset());
               flag = worldIn.isCollisionBoxesEmpty((Entity)null, axisalignedbb.offset(pos.offset(enumfacing)));
            } else {
               flag = true;
            }

            if (flag) {
               player.addStat(StatList.OPEN_SHULKER_BOX);
               player.displayGUIChest((IInventory)tileentity);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(FACING, context.getFace());
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(FACING);
   }

   /**
    * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect
    * this block
    */
   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (worldIn.getTileEntity(pos) instanceof TileEntityShulkerBox) {
         TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox)worldIn.getTileEntity(pos);
         tileentityshulkerbox.setDestroyedByCreativePlayer(player.abilities.isCreativeMode);
         tileentityshulkerbox.fillWithLoot(player);
      }

      super.onBlockHarvested(worldIn, pos, state, player);
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityShulkerBox) {
            ((TileEntityShulkerBox)tileentity).setCustomName(stack.getDisplayName());
         }
      }

   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityShulkerBox) {
            TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox)tileentity;
            if (!tileentityshulkerbox.isCleared() && tileentityshulkerbox.shouldDrop()) {
               ItemStack itemstack = new ItemStack(this);
               itemstack.getOrCreateTag().setTag("BlockEntityTag", ((TileEntityShulkerBox)tileentity).saveToNbt(new NBTTagCompound()));
               if (tileentityshulkerbox.hasCustomName()) {
                  itemstack.setDisplayName(tileentityshulkerbox.getCustomName());
                  tileentityshulkerbox.setCustomName((ITextComponent)null);
               }

               spawnAsEntity(worldIn, pos, itemstack);
            }

            worldIn.updateComparatorOutputLevel(pos, state.getBlock());
         }

         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      NBTTagCompound nbttagcompound = stack.getChildTag("BlockEntityTag");
      if (nbttagcompound != null) {
         if (nbttagcompound.contains("LootTable", 8)) {
            tooltip.add(new TextComponentString("???????"));
         }

         if (nbttagcompound.contains("Items", 9)) {
            NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(nbttagcompound, nonnulllist);
            int i = 0;
            int j = 0;

            for(ItemStack itemstack : nonnulllist) {
               if (!itemstack.isEmpty()) {
                  ++j;
                  if (i <= 4) {
                     ++i;
                     ITextComponent itextcomponent = itemstack.getDisplayName().func_212638_h();
                     itextcomponent.appendText(" x").appendText(String.valueOf(itemstack.getCount()));
                     tooltip.add(itextcomponent);
                  }
               }
            }

            if (j - i > 0) {
               tooltip.add((new TextComponentTranslation("container.shulkerBox.more", j - i)).applyTextStyle(TextFormatting.ITALIC));
            }
         }
      }

   }

   /**
    * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
    */
   public EnumPushReaction getPushReaction(IBlockState state) {
      return EnumPushReaction.DESTROY;
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity instanceof TileEntityShulkerBox ? VoxelShapes.create(((TileEntityShulkerBox)tileentity).getBoundingBox(state)) : VoxelShapes.fullCube();
   }

   public boolean isSolid(IBlockState state) {
      return false;
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
      return Container.calcRedstoneFromInventory((IInventory)worldIn.getTileEntity(pos));
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      ItemStack itemstack = super.getItem(worldIn, pos, state);
      TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox)worldIn.getTileEntity(pos);
      NBTTagCompound nbttagcompound = tileentityshulkerbox.saveToNbt(new NBTTagCompound());
      if (!nbttagcompound.isEmpty()) {
         itemstack.setTagInfo("BlockEntityTag", nbttagcompound);
      }

      return itemstack;
   }

   @OnlyIn(Dist.CLIENT)
   public static EnumDyeColor getColorFromItem(Item itemIn) {
      return getColorFromBlock(Block.getBlockFromItem(itemIn));
   }

   @OnlyIn(Dist.CLIENT)
   public static EnumDyeColor getColorFromBlock(Block blockIn) {
      return blockIn instanceof BlockShulkerBox ? ((BlockShulkerBox)blockIn).getColor() : null;
   }

   public static Block getBlockByColor(EnumDyeColor colorIn) {
      if (colorIn == null) {
         return Blocks.SHULKER_BOX;
      } else {
         switch(colorIn) {
         case WHITE:
            return Blocks.WHITE_SHULKER_BOX;
         case ORANGE:
            return Blocks.ORANGE_SHULKER_BOX;
         case MAGENTA:
            return Blocks.MAGENTA_SHULKER_BOX;
         case LIGHT_BLUE:
            return Blocks.LIGHT_BLUE_SHULKER_BOX;
         case YELLOW:
            return Blocks.YELLOW_SHULKER_BOX;
         case LIME:
            return Blocks.LIME_SHULKER_BOX;
         case PINK:
            return Blocks.PINK_SHULKER_BOX;
         case GRAY:
            return Blocks.GRAY_SHULKER_BOX;
         case LIGHT_GRAY:
            return Blocks.LIGHT_GRAY_SHULKER_BOX;
         case CYAN:
            return Blocks.CYAN_SHULKER_BOX;
         case PURPLE:
         default:
            return Blocks.PURPLE_SHULKER_BOX;
         case BLUE:
            return Blocks.BLUE_SHULKER_BOX;
         case BROWN:
            return Blocks.BROWN_SHULKER_BOX;
         case GREEN:
            return Blocks.GREEN_SHULKER_BOX;
         case RED:
            return Blocks.RED_SHULKER_BOX;
         case BLACK:
            return Blocks.BLACK_SHULKER_BOX;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public EnumDyeColor getColor() {
      return this.color;
   }

   public static ItemStack getColoredItemStack(EnumDyeColor colorIn) {
      return new ItemStack(getBlockByColor(colorIn));
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public IBlockState rotate(IBlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
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
      EnumFacing enumfacing = state.get(FACING);
      TileEntityShulkerBox.AnimationStatus tileentityshulkerbox$animationstatus = ((TileEntityShulkerBox)worldIn.getTileEntity(pos)).getAnimationStatus();
      return tileentityshulkerbox$animationstatus != TileEntityShulkerBox.AnimationStatus.CLOSED && (tileentityshulkerbox$animationstatus != TileEntityShulkerBox.AnimationStatus.OPENED || enumfacing != face.getOpposite() && enumfacing != face) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
   }
}