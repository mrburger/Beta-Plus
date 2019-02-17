package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockTallGrass extends BlockBush implements IGrowable, net.minecraftforge.common.IShearable {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

   protected BlockTallGrass(Block.Properties builder) {
      super(builder);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.AIR;
   }

   public int getItemsToDropCount(IBlockState state, int fortune, World worldIn, BlockPos pos, Random random) {
      return 1 + random.nextInt(fortune * 2 + 1);
   }

   /**
    * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
    * Block.removedByPlayer
    */
   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      if (!worldIn.isRemote && stack.getItem() == Items.SHEARS) {
         player.addStat(StatList.BLOCK_MINED.get(this));
         player.addExhaustion(0.005F);
         spawnAsEntity(worldIn, pos, new ItemStack(this));
      } else {
         super.harvestBlock(worldIn, player, pos, state, te, stack);
      }

   }

   /**
    * Whether this IGrowable can grow
    */
   public boolean canGrow(IBlockReader worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return true;
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      BlockDoublePlant blockdoubleplant = (BlockDoublePlant)(this == Blocks.FERN ? Blocks.LARGE_FERN : Blocks.TALL_GRASS);
      if (blockdoubleplant.getDefaultState().isValidPosition(worldIn, pos) && worldIn.isAirBlock(pos.up())) {
         blockdoubleplant.placeAt(worldIn, pos, 2);
      }

   }

   /**
    * Get the OffsetType for this Block. Determines if the model is rendered slightly offset.
    */
   public Block.EnumOffsetType getOffsetType() {
      return Block.EnumOffsetType.XYZ;
   }

   @Override
   public java.util.List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IWorld world, BlockPos pos, int fortune) {
      world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
      return java.util.Arrays.asList(new ItemStack(this));
   }

   @Override
   public void getDrops(IBlockState state, net.minecraft.util.NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
      if (world.rand.nextInt(8) != 0) return;
      ItemStack seed = net.minecraftforge.common.ForgeHooks.getGrassSeed(world.rand, fortune);
      if (!seed.isEmpty())
         drops.add(seed);
   }
}