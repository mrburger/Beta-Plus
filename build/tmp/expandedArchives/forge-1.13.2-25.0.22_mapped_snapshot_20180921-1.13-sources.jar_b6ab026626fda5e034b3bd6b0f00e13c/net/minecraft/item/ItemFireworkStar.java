package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemFireworkStar extends Item {
   public ItemFireworkStar(Item.Properties builder) {
      super(builder);
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      NBTTagCompound nbttagcompound = stack.getChildTag("Explosion");
      if (nbttagcompound != null) {
         func_195967_a(nbttagcompound, tooltip);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static void func_195967_a(NBTTagCompound p_195967_0_, List<ITextComponent> p_195967_1_) {
      ItemFireworkRocket.Shape itemfireworkrocket$shape = ItemFireworkRocket.Shape.func_196070_a(p_195967_0_.getByte("Type"));
      p_195967_1_.add((new TextComponentTranslation("item.minecraft.firework_star.shape." + itemfireworkrocket$shape.func_196068_b())).applyTextStyle(TextFormatting.GRAY));
      int[] aint = p_195967_0_.getIntArray("Colors");
      if (aint.length > 0) {
         p_195967_1_.add(func_200298_a((new TextComponentString("")).applyTextStyle(TextFormatting.GRAY), aint));
      }

      int[] aint1 = p_195967_0_.getIntArray("FadeColors");
      if (aint1.length > 0) {
         p_195967_1_.add(func_200298_a((new TextComponentTranslation("item.minecraft.firework_star.fade_to")).appendText(" ").applyTextStyle(TextFormatting.GRAY), aint1));
      }

      if (p_195967_0_.getBoolean("Trail")) {
         p_195967_1_.add((new TextComponentTranslation("item.minecraft.firework_star.trail")).applyTextStyle(TextFormatting.GRAY));
      }

      if (p_195967_0_.getBoolean("Flicker")) {
         p_195967_1_.add((new TextComponentTranslation("item.minecraft.firework_star.flicker")).applyTextStyle(TextFormatting.GRAY));
      }

   }

   @OnlyIn(Dist.CLIENT)
   private static ITextComponent func_200298_a(ITextComponent p_200298_0_, int[] p_200298_1_) {
      for(int i = 0; i < p_200298_1_.length; ++i) {
         if (i > 0) {
            p_200298_0_.appendText(", ");
         }

         p_200298_0_.appendSibling(func_200297_a(p_200298_1_[i]));
      }

      return p_200298_0_;
   }

   @OnlyIn(Dist.CLIENT)
   private static ITextComponent func_200297_a(int p_200297_0_) {
      EnumDyeColor enumdyecolor = EnumDyeColor.func_196058_b(p_200297_0_);
      return enumdyecolor == null ? new TextComponentTranslation("item.minecraft.firework_star.custom_color") : new TextComponentTranslation("item.minecraft.firework_star." + enumdyecolor.getTranslationKey());
   }
}