package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAbstractBanner;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

public class ItemBanner extends ItemWallOrFloor {
   public ItemBanner(Block p_i48529_1_, Block p_i48529_2_, Item.Properties builder) {
      super(p_i48529_1_, p_i48529_2_, builder);
      Validate.isInstanceOf(BlockAbstractBanner.class, p_i48529_1_);
      Validate.isInstanceOf(BlockAbstractBanner.class, p_i48529_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public static void appendHoverTextFromTileEntityTag(ItemStack stack, List<ITextComponent> p_185054_1_) {
      NBTTagCompound nbttagcompound = stack.getChildTag("BlockEntityTag");
      if (nbttagcompound != null && nbttagcompound.hasKey("Patterns")) {
         NBTTagList nbttaglist = nbttagcompound.getList("Patterns", 10);

         for(int i = 0; i < nbttaglist.size() && i < 6; ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
            EnumDyeColor enumdyecolor = EnumDyeColor.byId(nbttagcompound1.getInt("Color"));
            BannerPattern bannerpattern = BannerPattern.byHash(nbttagcompound1.getString("Pattern"));
            if (bannerpattern != null) {
               p_185054_1_.add((new TextComponentTranslation("block.minecraft.banner." + bannerpattern.getFileName() + '.' + enumdyecolor.getTranslationKey())).applyTextStyle(TextFormatting.GRAY));
            }
         }

      }
   }

   public EnumDyeColor getColor() {
      return ((BlockAbstractBanner)this.getBlock()).getColor();
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      appendHoverTextFromTileEntityTag(stack, tooltip);
   }
}