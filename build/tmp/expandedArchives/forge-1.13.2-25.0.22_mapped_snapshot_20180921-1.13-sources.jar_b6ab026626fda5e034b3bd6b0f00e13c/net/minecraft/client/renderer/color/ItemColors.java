package net.minecraft.client.renderer.color;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmorDyeable;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IWorldReaderBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemColors {
   private final ObjectIntIdentityMap<IItemColor> colors = new ObjectIntIdentityMap<>(32);

   public static ItemColors init(BlockColors colors) {
      ItemColors itemcolors = new ItemColors();
      itemcolors.register((p_210239_0_, p_210239_1_) -> {
         return p_210239_1_ > 0 ? -1 : ((ItemArmorDyeable)p_210239_0_.getItem()).getColor(p_210239_0_);
      }, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS);
      itemcolors.register((p_210236_0_, p_210236_1_) -> {
         return GrassColors.get(0.5D, 1.0D);
      }, Blocks.TALL_GRASS, Blocks.LARGE_FERN);
      itemcolors.register((p_210241_0_, p_210241_1_) -> {
         if (p_210241_1_ != 1) {
            return -1;
         } else {
            NBTTagCompound nbttagcompound = p_210241_0_.getChildTag("Explosion");
            int[] aint = nbttagcompound != null && nbttagcompound.contains("Colors", 11) ? nbttagcompound.getIntArray("Colors") : null;
            if (aint == null) {
               return 9079434;
            } else if (aint.length == 1) {
               return aint[0];
            } else {
               int i = 0;
               int j = 0;
               int k = 0;

               for(int l : aint) {
                  i += (l & 16711680) >> 16;
                  j += (l & '\uff00') >> 8;
                  k += (l & 255) >> 0;
               }

               i = i / aint.length;
               j = j / aint.length;
               k = k / aint.length;
               return i << 16 | j << 8 | k;
            }
         }
      }, Items.FIREWORK_STAR);
      itemcolors.register((p_210238_0_, p_210238_1_) -> {
         return p_210238_1_ > 0 ? -1 : PotionUtils.getColor(p_210238_0_);
      }, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);

      for(ItemSpawnEgg itemspawnegg : ItemSpawnEgg.getEggs()) {
         itemcolors.register((p_198141_1_, p_198141_2_) -> {
            return itemspawnegg.getColor(p_198141_2_);
         }, itemspawnegg);
      }

      itemcolors.register((p_210235_1_, p_210235_2_) -> {
         IBlockState iblockstate = ((ItemBlock)p_210235_1_.getItem()).getBlock().getDefaultState();
         return colors.getColor(iblockstate, (IWorldReaderBase)null, (BlockPos)null, p_210235_2_);
      }, Blocks.GRASS_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.VINE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.LILY_PAD);
      itemcolors.register((p_210242_0_, p_210242_1_) -> {
         return p_210242_1_ == 0 ? PotionUtils.getColor(p_210242_0_) : -1;
      }, Items.TIPPED_ARROW);
      itemcolors.register((p_210237_0_, p_210237_1_) -> {
         return p_210237_1_ == 0 ? -1 : ItemMap.getColor(p_210237_0_);
      }, Items.FILLED_MAP);
      return itemcolors;
   }

   public int getColor(ItemStack stack, int tintIndex) {
      IItemColor iitemcolor = this.colors.getByValue(IRegistry.field_212630_s.getId(stack.getItem()));
      return iitemcolor == null ? -1 : iitemcolor.getColor(stack, tintIndex);
   }

   public void register(IItemColor p_199877_1_, IItemProvider... p_199877_2_) {
      for(IItemProvider iitemprovider : p_199877_2_) {
         this.colors.put(p_199877_1_, Item.getIdFromItem(iitemprovider.asItem()));
      }

   }
}