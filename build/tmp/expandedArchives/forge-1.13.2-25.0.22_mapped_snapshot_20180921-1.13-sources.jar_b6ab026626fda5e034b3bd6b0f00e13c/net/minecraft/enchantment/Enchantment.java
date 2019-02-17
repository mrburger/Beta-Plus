package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Enchantment extends net.minecraftforge.registries.ForgeRegistryEntry<Enchantment> {
   /** Where this enchantment has an effect, e.g. offhand, pants */
   private final EntityEquipmentSlot[] applicableEquipmentTypes;
   private final Enchantment.Rarity rarity;
   /** The EnumEnchantmentType given to this Enchantment. */
   @Nullable
   public EnumEnchantmentType type;
   /** Used in localisation and stats. */
   @Nullable
   protected String name;

   /**
    * Gets an Enchantment from the registry, based on a numeric ID.
    */
   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static Enchantment getEnchantmentByID(int id) {
      return IRegistry.field_212628_q.get(id);
   }

   protected Enchantment(Enchantment.Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
      this.rarity = rarityIn;
      this.type = typeIn;
      this.applicableEquipmentTypes = slots;
   }

   /**
    * Gets list of all the entity's currently equipped gear that this enchantment can go on
    */
   public List<ItemStack> getEntityEquipment(EntityLivingBase entityIn) {
      List<ItemStack> list = Lists.newArrayList();

      for(EntityEquipmentSlot entityequipmentslot : this.applicableEquipmentTypes) {
         ItemStack itemstack = entityIn.getItemStackFromSlot(entityequipmentslot);
         if (!itemstack.isEmpty()) {
            list.add(itemstack);
         }
      }

      return list;
   }

   /**
    * Retrieves the weight value of an Enchantment. This weight value is used within vanilla to determine how rare an
    * enchantment is.
    */
   public Enchantment.Rarity getRarity() {
      return this.rarity;
   }

   /**
    * Returns the minimum level that the enchantment can have.
    */
   public int getMinLevel() {
      return 1;
   }

   /**
    * Returns the maximum level that the enchantment can have.
    */
   public int getMaxLevel() {
      return 1;
   }

   /**
    * Returns the minimal value of enchantability needed on the enchantment level passed.
    */
   public int getMinEnchantability(int enchantmentLevel) {
      return 1 + enchantmentLevel * 10;
   }

   /**
    * Returns the maximum value of enchantability nedded on the enchantment level passed.
    */
   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 5;
   }

   /**
    * Calculates the damage protection of the enchantment based on level and damage source passed.
    */
   public int calcModifierDamage(int level, DamageSource source) {
      return 0;
   }

   /**
    * Calculates the additional damage that will be dealt by an item with this enchantment. This alternative to
    * calcModifierDamage is sensitive to the targets EnumCreatureAttribute.
    */
   public float calcDamageByCreature(int level, CreatureAttribute creatureType) {
      return 0.0F;
   }

   public final boolean isCompatibleWith(Enchantment enchantmentIn) {
      return this.canApplyTogether(enchantmentIn) && enchantmentIn.canApplyTogether(this);
   }

   /**
    * Determines if the enchantment passed can be applyied together with this enchantment.
    */
   protected boolean canApplyTogether(Enchantment ench) {
      return this != ench;
   }

   protected String getDefaultTranslationKey() {
      if (this.name == null) {
         this.name = Util.makeTranslationKey("enchantment", IRegistry.field_212628_q.getKey(this));
      }

      return this.name;
   }

   /**
    * Return the name of key in translation table of this enchantment.
    */
   public String getName() {
      return this.getDefaultTranslationKey();
   }

   public ITextComponent func_200305_d(int p_200305_1_) {
      ITextComponent itextcomponent = new TextComponentTranslation(this.getName());
      if (this.isCurse()) {
         itextcomponent.applyTextStyle(TextFormatting.RED);
      } else {
         itextcomponent.applyTextStyle(TextFormatting.GRAY);
      }

      if (p_200305_1_ != 1 || this.getMaxLevel() != 1) {
         itextcomponent.appendText(" ").appendSibling(new TextComponentTranslation("enchantment.level." + p_200305_1_));
      }

      return itextcomponent;
   }

   /**
    * Determines if this enchantment can be applied to a specific ItemStack.
    */
   public boolean canApply(ItemStack stack) {
      return canApplyAtEnchantingTable(stack);
   }

   /**
    * Called whenever a mob is damaged with an item that has this enchantment on it.
    */
   public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
   }

   /**
    * Whenever an entity that has this enchantment on one of its associated items is damaged this method will be called.
    */
   public void onUserHurt(EntityLivingBase user, Entity attacker, int level) {
   }

   public boolean isTreasureEnchantment() {
      return false;
   }

   public boolean isCurse() {
      return false;
   }

   /**
    * This applies specifically to applying at the enchanting table. The other method {@link #canApply(ItemStack)}
    * applies for <i>all possible</i> enchantments.
    * @param stack
    * @return
    */
   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return stack.canApplyAtEnchantingTable(this);
   }

   /**
    * Is this enchantment allowed to be enchanted on books via Enchantment Table
    * @return false to disable the vanilla feature
    */
   public boolean isAllowedOnBooks() {
      return true;
   }

   /**
    * Registers all of the vanilla enchantments.
    */
   public static void registerEnchantments() {
      EntityEquipmentSlot[] aentityequipmentslot = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
      register("protection", new EnchantmentProtection(Enchantment.Rarity.COMMON, EnchantmentProtection.Type.ALL, aentityequipmentslot));
      register("fire_protection", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.FIRE, aentityequipmentslot));
      register("feather_falling", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.FALL, aentityequipmentslot));
      register("blast_protection", new EnchantmentProtection(Enchantment.Rarity.RARE, EnchantmentProtection.Type.EXPLOSION, aentityequipmentslot));
      register("projectile_protection", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.PROJECTILE, aentityequipmentslot));
      register("respiration", new EnchantmentOxygen(Enchantment.Rarity.RARE, aentityequipmentslot));
      register("aqua_affinity", new EnchantmentWaterWorker(Enchantment.Rarity.RARE, aentityequipmentslot));
      register("thorns", new EnchantmentThorns(Enchantment.Rarity.VERY_RARE, aentityequipmentslot));
      register("depth_strider", new EnchantmentWaterWalker(Enchantment.Rarity.RARE, aentityequipmentslot));
      register("frost_walker", new EnchantmentFrostWalker(Enchantment.Rarity.RARE, EntityEquipmentSlot.FEET));
      register("binding_curse", new EnchantmentBindingCurse(Enchantment.Rarity.VERY_RARE, aentityequipmentslot));
      register("sharpness", new EnchantmentDamage(Enchantment.Rarity.COMMON, 0, EntityEquipmentSlot.MAINHAND));
      register("smite", new EnchantmentDamage(Enchantment.Rarity.UNCOMMON, 1, EntityEquipmentSlot.MAINHAND));
      register("bane_of_arthropods", new EnchantmentDamage(Enchantment.Rarity.UNCOMMON, 2, EntityEquipmentSlot.MAINHAND));
      register("knockback", new EnchantmentKnockback(Enchantment.Rarity.UNCOMMON, EntityEquipmentSlot.MAINHAND));
      register("fire_aspect", new EnchantmentFireAspect(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      register("looting", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND));
      register("sweeping", new EnchantmentSweepingEdge(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      register("efficiency", new EnchantmentDigging(Enchantment.Rarity.COMMON, EntityEquipmentSlot.MAINHAND));
      register("silk_touch", new EnchantmentUntouching(Enchantment.Rarity.VERY_RARE, EntityEquipmentSlot.MAINHAND));
      register("unbreaking", new EnchantmentDurability(Enchantment.Rarity.UNCOMMON, EntityEquipmentSlot.MAINHAND));
      register("fortune", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnumEnchantmentType.DIGGER, EntityEquipmentSlot.MAINHAND));
      register("power", new EnchantmentArrowDamage(Enchantment.Rarity.COMMON, EntityEquipmentSlot.MAINHAND));
      register("punch", new EnchantmentArrowKnockback(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      register("flame", new EnchantmentArrowFire(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      register("infinity", new EnchantmentArrowInfinite(Enchantment.Rarity.VERY_RARE, EntityEquipmentSlot.MAINHAND));
      register("luck_of_the_sea", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnumEnchantmentType.FISHING_ROD, EntityEquipmentSlot.MAINHAND));
      register("lure", new EnchantmentFishingSpeed(Enchantment.Rarity.RARE, EnumEnchantmentType.FISHING_ROD, EntityEquipmentSlot.MAINHAND));
      register("loyalty", new EnchantmentLoyalty(Enchantment.Rarity.UNCOMMON, EntityEquipmentSlot.MAINHAND));
      register("impaling", new EnchantmentImpaling(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      register("riptide", new EnchantmentRiptide(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      register("channeling", new EnchantmentChanneling(Enchantment.Rarity.VERY_RARE, EntityEquipmentSlot.MAINHAND));
      register("mending", new EnchantmentMending(Enchantment.Rarity.RARE, EntityEquipmentSlot.values()));
      register("vanishing_curse", new EnchantmentVanishingCurse(Enchantment.Rarity.VERY_RARE, EntityEquipmentSlot.values()));
   }

   private static void register(String nameIn, Enchantment enchantmentIn) {
      IRegistry.field_212628_q.put(new ResourceLocation(nameIn), enchantmentIn);
   }

   public static enum Rarity {
      COMMON(10),
      UNCOMMON(5),
      RARE(2),
      VERY_RARE(1);

      /** The weight of the Rarity. */
      private final int weight;

      private Rarity(int rarityWeight) {
         this.weight = rarityWeight;
      }

      /**
       * Retrieves the weight of Rarity.
       */
      public int getWeight() {
         return this.weight;
      }
   }
}