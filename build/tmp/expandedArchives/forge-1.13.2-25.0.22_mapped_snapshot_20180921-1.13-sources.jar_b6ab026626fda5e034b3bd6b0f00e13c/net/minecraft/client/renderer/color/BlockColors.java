package net.minecraft.client.renderer.color;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockShearableDoublePlant;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockColors {
   // FORGE: Use RegistryDelegates as non-Vanilla block ids are not constant
   private final java.util.Map<net.minecraftforge.registries.IRegistryDelegate<Block>, IBlockColor> colors = new java.util.HashMap<>();

   public static BlockColors init() {
      BlockColors blockcolors = new BlockColors();
      blockcolors.register((p_210234_0_, p_210234_1_, p_210234_2_, p_210234_3_) -> {
         return p_210234_1_ != null && p_210234_2_ != null ? BiomeColors.getGrassColor(p_210234_1_, p_210234_0_.get(BlockShearableDoublePlant.field_208063_b) == DoubleBlockHalf.UPPER ? p_210234_2_.down() : p_210234_2_) : -1;
      }, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
      blockcolors.register((p_210225_0_, p_210225_1_, p_210225_2_, p_210225_3_) -> {
         return p_210225_1_ != null && p_210225_2_ != null ? BiomeColors.getGrassColor(p_210225_1_, p_210225_2_) : GrassColors.get(0.5D, 1.0D);
      }, Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.GRASS, Blocks.POTTED_FERN);
      blockcolors.register((p_210227_0_, p_210227_1_, p_210227_2_, p_210227_3_) -> {
         return FoliageColors.getSpruce();
      }, Blocks.SPRUCE_LEAVES);
      blockcolors.register((p_210232_0_, p_210232_1_, p_210232_2_, p_210232_3_) -> {
         return FoliageColors.getBirch();
      }, Blocks.BIRCH_LEAVES);
      blockcolors.register((p_210229_0_, p_210229_1_, p_210229_2_, p_210229_3_) -> {
         return p_210229_1_ != null && p_210229_2_ != null ? BiomeColors.getFoliageColor(p_210229_1_, p_210229_2_) : FoliageColors.getDefault();
      }, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE);
      blockcolors.register((p_210226_0_, p_210226_1_, p_210226_2_, p_210226_3_) -> {
         return p_210226_1_ != null && p_210226_2_ != null ? BiomeColors.getWaterColor(p_210226_1_, p_210226_2_) : -1;
      }, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.CAULDRON);
      blockcolors.register((p_210231_0_, p_210231_1_, p_210231_2_, p_210231_3_) -> {
         return BlockRedstoneWire.colorMultiplier(p_210231_0_.get(BlockRedstoneWire.POWER));
      }, Blocks.REDSTONE_WIRE);
      blockcolors.register((p_210230_0_, p_210230_1_, p_210230_2_, p_210230_3_) -> {
         return p_210230_1_ != null && p_210230_2_ != null ? BiomeColors.getGrassColor(p_210230_1_, p_210230_2_) : -1;
      }, Blocks.SUGAR_CANE);
      blockcolors.register((p_210224_0_, p_210224_1_, p_210224_2_, p_210224_3_) -> {
         return 14731036;
      }, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
      blockcolors.register((p_210233_0_, p_210233_1_, p_210233_2_, p_210233_3_) -> {
         int i = p_210233_0_.get(BlockStem.AGE);
         int j = i * 32;
         int k = 255 - i * 8;
         int l = i * 4;
         return j << 16 | k << 8 | l;
      }, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
      blockcolors.register((p_210228_0_, p_210228_1_, p_210228_2_, p_210228_3_) -> {
         return p_210228_1_ != null && p_210228_2_ != null ? 2129968 : 7455580;
      }, Blocks.LILY_PAD);
      net.minecraftforge.client.ForgeHooksClient.onBlockColorsInit(blockcolors);
      return blockcolors;
   }

   public int getColor(IBlockState state, World p_189991_2_, BlockPos p_189991_3_) {
      IBlockColor iblockcolor = this.colors.get(state.getBlock().delegate);
      if (iblockcolor != null) {
         return iblockcolor.getColor(state, (IWorldReaderBase)null, (BlockPos)null, 0);
      } else {
         MaterialColor materialcolor = state.getMapColor(p_189991_2_, p_189991_3_);
         return materialcolor != null ? materialcolor.colorValue : -1;
      }
   }

   public int getColor(IBlockState state, @Nullable IWorldReaderBase blockAccess, @Nullable BlockPos pos, int tintIndex) {
      IBlockColor iblockcolor = this.colors.get(state.getBlock().delegate);
      return iblockcolor == null ? -1 : iblockcolor.getColor(state, blockAccess, pos, tintIndex);
   }

   public void register(IBlockColor blockColor, Block... blocksIn) {
      for(Block block : blocksIn) {
         this.colors.put(block.delegate, blockColor);
      }

   }
}