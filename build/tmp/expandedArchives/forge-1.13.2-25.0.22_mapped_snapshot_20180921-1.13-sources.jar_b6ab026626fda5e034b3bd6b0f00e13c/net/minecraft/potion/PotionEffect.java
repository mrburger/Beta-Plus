package net.minecraft.potion;

import com.google.common.collect.ComparisonChain;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotionEffect implements Comparable<PotionEffect> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Potion potion;
   /** The duration of the potion effect */
   private int duration;
   /** The amplifier of the potion effect */
   private int amplifier;
   /** Whether the potion is a splash potion */
   private boolean isSplashPotion;
   /** Whether the potion effect came from a beacon */
   private boolean ambient;
   /** True if potion effect duration is at maximum, false otherwise. */
   @OnlyIn(Dist.CLIENT)
   private boolean isPotionDurationMax;
   private boolean showParticles;
   private boolean showIcon;
   /** List of ItemStack that can cure the potion effect **/
   private java.util.List<net.minecraft.item.ItemStack> curativeItems;

   public PotionEffect(Potion potionIn) {
      this(potionIn, 0, 0);
   }

   public PotionEffect(Potion potionIn, int durationIn) {
      this(potionIn, durationIn, 0);
   }

   public PotionEffect(Potion potionIn, int durationIn, int amplifierIn) {
      this(potionIn, durationIn, amplifierIn, false, true);
   }

   public PotionEffect(Potion potionIn, int durationIn, int amplifierIn, boolean ambientIn, boolean showParticlesIn) {
      this(potionIn, durationIn, amplifierIn, ambientIn, showParticlesIn, showParticlesIn);
   }

   public PotionEffect(Potion p_i48980_1_, int p_i48980_2_, int p_i48980_3_, boolean p_i48980_4_, boolean p_i48980_5_, boolean p_i48980_6_) {
      this.potion = p_i48980_1_;
      this.duration = p_i48980_2_;
      this.amplifier = p_i48980_3_;
      this.ambient = p_i48980_4_;
      this.showParticles = p_i48980_5_;
      this.showIcon = p_i48980_6_;
   }

   public PotionEffect(PotionEffect other) {
      this.potion = other.potion;
      this.duration = other.duration;
      this.amplifier = other.amplifier;
      this.ambient = other.ambient;
      this.showParticles = other.showParticles;
      this.showIcon = other.showIcon;
      this.curativeItems = other.curativeItems == null ? null : new java.util.ArrayList<net.minecraft.item.ItemStack>(other.curativeItems);
   }

   public boolean func_199308_a(PotionEffect p_199308_1_) {
      if (this.potion != p_199308_1_.potion) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      boolean flag = false;
      if (p_199308_1_.amplifier > this.amplifier) {
         this.amplifier = p_199308_1_.amplifier;
         this.duration = p_199308_1_.duration;
         flag = true;
      } else if (p_199308_1_.amplifier == this.amplifier && this.duration < p_199308_1_.duration) {
         this.duration = p_199308_1_.duration;
         flag = true;
      }

      if (!p_199308_1_.ambient && this.ambient || flag) {
         this.ambient = p_199308_1_.ambient;
         flag = true;
      }

      if (p_199308_1_.showParticles != this.showParticles) {
         this.showParticles = p_199308_1_.showParticles;
         flag = true;
      }

      if (p_199308_1_.showIcon != this.showIcon) {
         this.showIcon = p_199308_1_.showIcon;
         flag = true;
      }

      return flag;
   }

   public Potion getPotion() {
      return this.potion;
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   /**
    * Gets whether this potion effect originated from a beacon
    */
   public boolean isAmbient() {
      return this.ambient;
   }

   /**
    * Gets whether this potion effect will show ambient particles or not.
    */
   public boolean doesShowParticles() {
      return this.showParticles;
   }

   public boolean isShowIcon() {
      return this.showIcon;
   }

   public boolean tick(EntityLivingBase entityIn) {
      if (this.duration > 0) {
         if (this.potion.isReady(this.duration, this.amplifier)) {
            this.performEffect(entityIn);
         }

         this.deincrementDuration();
      }

      return this.duration > 0;
   }

   private int deincrementDuration() {
      return --this.duration;
   }

   public void performEffect(EntityLivingBase entityIn) {
      if (this.duration > 0) {
         this.potion.performEffect(entityIn, this.amplifier);
      }

   }

   public String getEffectName() {
      return this.potion.getName();
   }

   public String toString() {
      String s;
      if (this.amplifier > 0) {
         s = this.getEffectName() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
      } else {
         s = this.getEffectName() + ", Duration: " + this.duration;
      }

      if (this.isSplashPotion) {
         s = s + ", Splash: true";
      }

      if (!this.showParticles) {
         s = s + ", Particles: false";
      }

      if (!this.showIcon) {
         s = s + ", Show Icon: false";
      }

      return s;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof PotionEffect)) {
         return false;
      } else {
         PotionEffect potioneffect = (PotionEffect)p_equals_1_;
         return this.duration == potioneffect.duration && this.amplifier == potioneffect.amplifier && this.isSplashPotion == potioneffect.isSplashPotion && this.ambient == potioneffect.ambient && this.potion.equals(potioneffect.potion);
      }
   }

   public int hashCode() {
      int i = this.potion.hashCode();
      i = 31 * i + this.duration;
      i = 31 * i + this.amplifier;
      i = 31 * i + (this.isSplashPotion ? 1 : 0);
      i = 31 * i + (this.ambient ? 1 : 0);
      return i;
   }

   /**
    * Write a custom potion effect to a potion item's NBT data.
    */
   public NBTTagCompound write(NBTTagCompound nbt) {
      nbt.setByte("Id", (byte)Potion.getIdFromPotion(this.getPotion()));
      nbt.setByte("Amplifier", (byte)this.getAmplifier());
      nbt.setInt("Duration", this.getDuration());
      nbt.setBoolean("Ambient", this.isAmbient());
      nbt.setBoolean("ShowParticles", this.doesShowParticles());
      nbt.setBoolean("ShowIcon", this.isShowIcon());
      writeCurativeItems(nbt);
      return nbt;
   }

   /**
    * Read a custom potion effect from a potion item's NBT data.
    */
   public static PotionEffect read(NBTTagCompound nbt) {
      int i = nbt.getByte("Id") & 0xFF;
      Potion potion = Potion.getPotionById(i);
      if (potion == null) {
         return null;
      } else {
         int j = nbt.getByte("Amplifier");
         int k = nbt.getInt("Duration");
         boolean flag = nbt.getBoolean("Ambient");
         boolean flag1 = true;
         if (nbt.contains("ShowParticles", 1)) {
            flag1 = nbt.getBoolean("ShowParticles");
         }

         boolean flag2 = flag1;
         if (nbt.contains("ShowIcon", 1)) {
            flag2 = nbt.getBoolean("ShowIcon");
         }

         return readCurativeItems(new PotionEffect(potion, k, j < 0 ? 0 : j, flag, flag1, flag2), nbt);
      }
   }

   /**
    * Toggle the isPotionDurationMax field.
    */
   @OnlyIn(Dist.CLIENT)
   public void setPotionDurationMax(boolean maxDuration) {
      this.isPotionDurationMax = maxDuration;
   }

   /**
    * Get the value of the isPotionDurationMax field.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean getIsPotionDurationMax() {
      return this.isPotionDurationMax;
   }

   public int compareTo(PotionEffect p_compareTo_1_) {
      int i = 32147;
      return (this.getDuration() <= 32147 || p_compareTo_1_.getDuration() <= 32147) && (!this.isAmbient() || !p_compareTo_1_.isAmbient()) ? ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getDuration(), p_compareTo_1_.getDuration()).compare(this.getPotion().getGuiSortColor(this), p_compareTo_1_.getPotion().getGuiSortColor(this)).result() : ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getPotion().getGuiSortColor(this), p_compareTo_1_.getPotion().getGuiSortColor(this)).result();
   }

   /***
    * Returns a list of curative items for the potion effect
    * By default, this list is initialized using {@link Potion#getCurativeItems}
    *
    * @return The list (ItemStack) of curative items for the potion effect
    */
   public java.util.List<net.minecraft.item.ItemStack> getCurativeItems() {
      if (this.curativeItems == null) { //Lazy load this so that we don't create a circular dep on Items.
         this.curativeItems = getPotion().getCurativeItems();
      }
      return this.curativeItems;
   }

   /***
    * Checks the given ItemStack to see if it is in the list of curative items for the potion effect
    * @param stack The ItemStack being checked against the list of curative items for this PotionEffect
    * @return true if the given ItemStack is in the list of curative items for this PotionEffect, false otherwise
    */
   public boolean isCurativeItem(net.minecraft.item.ItemStack stack) {
      return this.getCurativeItems().stream().anyMatch(e -> e.isItemEqual(stack));
   }

   /***
    * Sets the list of curative items for this potion effect, overwriting any already present
    * @param curativeItems The list of ItemStacks being set to the potion effect
    */
   public void setCurativeItems(java.util.List<net.minecraft.item.ItemStack> curativeItems) {
      this.curativeItems = curativeItems;
   }

   /***
    * Adds the given stack to the list of curative items for this PotionEffect
    * @param stack The ItemStack being added to the curative item list
    */
   public void addCurativeItem(net.minecraft.item.ItemStack stack) {
      if (!this.isCurativeItem(stack)) {
         this.getCurativeItems().add(stack);
      }
   }

   private void writeCurativeItems(NBTTagCompound nbt) {
      net.minecraft.nbt.NBTTagList list = new net.minecraft.nbt.NBTTagList();
      for (net.minecraft.item.ItemStack stack : getCurativeItems()) {
         list.add(stack.write(new NBTTagCompound()));
      }
      nbt.setTag("CurativeItems", list);
   }

   private static PotionEffect readCurativeItems(PotionEffect effect, NBTTagCompound nbt) {
      if (nbt.contains("CurativeItems", net.minecraftforge.common.util.Constants.NBT.TAG_LIST)) {
         java.util.List<net.minecraft.item.ItemStack> items = new java.util.ArrayList<net.minecraft.item.ItemStack>();
         net.minecraft.nbt.NBTTagList list = nbt.getList("CurativeItems", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
         for (int i = 0; i < list.size(); i++) {
            items.add(net.minecraft.item.ItemStack.read(list.getCompound(i)));
         }
         effect.setCurativeItems(items);
      }

      return effect;
   }
}