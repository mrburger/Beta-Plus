package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockIce extends BlockBreakable {
   public BlockIce(Block.Properties builder) {
      super(builder);
   }

   public int getOpacity(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return Blocks.WATER.getDefaultState().getOpacity(worldIn, pos);
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   /**
    * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
    * Block.removedByPlayer
    */
   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      player.addStat(StatList.BLOCK_MINED.get(this));
      player.addExhaustion(0.005F);
      if (this.canSilkHarvest(state, worldIn, pos, player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
         net.minecraft.util.NonNullList<ItemStack> items = net.minecraft.util.NonNullList.from(ItemStack.EMPTY, getSilkTouchDrop(state));
         net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
         items.stream().filter(e -> !e.isEmpty()).forEach(e -> spawnAsEntity(worldIn, pos, e));
      } else {
         if (worldIn.dimension.doesWaterVaporize()) {
            worldIn.removeBlock(pos);
            return;
         }

         int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
         harvesters.set(player);
         state.dropBlockAsItem(worldIn, pos, i);
         harvesters.set(null);
         Material material = worldIn.getBlockState(pos.down()).getMaterial();
         if (material.blocksMovement() || material.isLiquid()) {
            worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
         }
      }

   }

   public int quantityDropped(IBlockState state, Random random) {
      return 0;
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (worldIn.getLightFor(EnumLightType.BLOCK, pos) > 11 - state.getOpacity(worldIn, pos)) {
         this.turnIntoWater(state, worldIn, pos);
      }

   }

   protected void turnIntoWater(IBlockState p_196454_1_, World p_196454_2_, BlockPos p_196454_3_) {
      if (p_196454_2_.dimension.doesWaterVaporize()) {
         p_196454_2_.removeBlock(p_196454_3_);
      } else {
         p_196454_1_.dropBlockAsItem(p_196454_2_, p_196454_3_, 0);
         p_196454_2_.setBlockState(p_196454_3_, Blocks.WATER.getDefaultState());
         p_196454_2_.neighborChanged(p_196454_3_, Blocks.WATER, p_196454_3_);
      }
   }

   /**
    * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
    */
   public EnumPushReaction getPushReaction(IBlockState state) {
      return EnumPushReaction.NORMAL;
   }
}