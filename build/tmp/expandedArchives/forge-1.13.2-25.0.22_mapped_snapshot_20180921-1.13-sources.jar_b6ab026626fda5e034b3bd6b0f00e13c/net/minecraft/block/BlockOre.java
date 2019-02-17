package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockOre extends Block {
   public BlockOre(Block.Properties builder) {
      super(builder);
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      if (this == Blocks.COAL_ORE) {
         return Items.COAL;
      } else if (this == Blocks.DIAMOND_ORE) {
         return Items.DIAMOND;
      } else if (this == Blocks.LAPIS_ORE) {
         return Items.LAPIS_LAZULI;
      } else if (this == Blocks.EMERALD_ORE) {
         return Items.EMERALD;
      } else {
         return (IItemProvider)(this == Blocks.NETHER_QUARTZ_ORE ? Items.QUARTZ : this);
      }
   }

   public int quantityDropped(IBlockState state, Random random) {
      return this == Blocks.LAPIS_ORE ? 4 + random.nextInt(5) : 1;
   }

   public int getItemsToDropCount(IBlockState state, int fortune, World worldIn, BlockPos pos, Random random) {
      if (fortune > 0 && this != this.getItemDropped(this.getStateContainer().getValidStates().iterator().next(), worldIn, pos, fortune)) {
         int i = random.nextInt(fortune + 2) - 1;
         if (i < 0) {
            i = 0;
         }

         return this.quantityDropped(state, random) * (i + 1);
      } else {
         return this.quantityDropped(state, random);
      }
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
      super.dropBlockAsItemWithChance(state, worldIn, pos, chancePerItem, fortune);
   }

   @Override
   public int getExpDrop(IBlockState state, net.minecraft.world.IWorldReader reader, BlockPos pos, int fortune) {
      World world = reader instanceof World ? (World)reader : null;
      if (world == null || this.getItemDropped(state, world, pos, fortune) != this) {
         int i = 0;
         if (this == Blocks.COAL_ORE) {
            i = MathHelper.nextInt(RANDOM, 0, 2);
         } else if (this == Blocks.DIAMOND_ORE) {
            i = MathHelper.nextInt(RANDOM, 3, 7);
         } else if (this == Blocks.EMERALD_ORE) {
            i = MathHelper.nextInt(RANDOM, 3, 7);
         } else if (this == Blocks.LAPIS_ORE) {
            i = MathHelper.nextInt(RANDOM, 2, 5);
         } else if (this == Blocks.NETHER_QUARTZ_ORE) {
            i = MathHelper.nextInt(RANDOM, 2, 5);
         }
         return i;
      }
      return 0;
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(this);
   }
}