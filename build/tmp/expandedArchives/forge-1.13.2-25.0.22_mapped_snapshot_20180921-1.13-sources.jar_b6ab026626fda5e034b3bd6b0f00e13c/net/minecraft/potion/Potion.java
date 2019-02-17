package net.minecraft.potion;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Potion extends net.minecraftforge.registries.ForgeRegistryEntry<Potion> {
   /** Contains a Map of the AttributeModifiers registered by potions */
   private final Map<IAttribute, AttributeModifier> attributeModifierMap = Maps.newHashMap();
   /** This field indicated if the effect is 'bad' - negative - for the entity. */
   private final boolean isBadEffect;
   /** Is the color of the liquid for this potion. */
   private final int liquidColor;
   /** The name of the Potion. */
   @Nullable
   private String name;
   /** The index for the icon displayed when the potion effect is active. */
   private int statusIconIndex = -1;
   private double effectiveness;
   private boolean beneficial;

   /**
    * Gets a Potion from the potion registry using a numeric Id.
    */
   @Nullable
   public static Potion getPotionById(int potionID) {
      return IRegistry.field_212631_t.get(potionID);
   }

   /**
    * Gets the numeric Id associated with a potion.
    */
   public static int getIdFromPotion(Potion potionIn) {
      return IRegistry.field_212631_t.getId(potionIn);
   }

   protected Potion(boolean isBadEffectIn, int liquidColorIn) {
      this.isBadEffect = isBadEffectIn;
      if (isBadEffectIn) {
         this.effectiveness = 0.5D;
      } else {
         this.effectiveness = 1.0D;
      }

      this.liquidColor = liquidColorIn;
   }

   /**
    * Sets the index for the icon displayed in the player's inventory when the status is active.
    */
   protected Potion setIconIndex(int p_76399_1_, int p_76399_2_) {
      this.statusIconIndex = p_76399_1_ + p_76399_2_ * 12;
      return this;
   }

   public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
      if (this == MobEffects.REGENERATION) {
         if (entityLivingBaseIn.getHealth() < entityLivingBaseIn.getMaxHealth()) {
            entityLivingBaseIn.heal(1.0F);
         }
      } else if (this == MobEffects.POISON) {
         if (entityLivingBaseIn.getHealth() > 1.0F) {
            entityLivingBaseIn.attackEntityFrom(DamageSource.MAGIC, 1.0F);
         }
      } else if (this == MobEffects.WITHER) {
         entityLivingBaseIn.attackEntityFrom(DamageSource.WITHER, 1.0F);
      } else if (this == MobEffects.HUNGER && entityLivingBaseIn instanceof EntityPlayer) {
         ((EntityPlayer)entityLivingBaseIn).addExhaustion(0.005F * (float)(amplifier + 1));
      } else if (this == MobEffects.SATURATION && entityLivingBaseIn instanceof EntityPlayer) {
         if (!entityLivingBaseIn.world.isRemote) {
            ((EntityPlayer)entityLivingBaseIn).getFoodStats().addStats(amplifier + 1, 1.0F);
         }
      } else if ((this != MobEffects.INSTANT_HEALTH || entityLivingBaseIn.isEntityUndead()) && (this != MobEffects.INSTANT_DAMAGE || !entityLivingBaseIn.isEntityUndead())) {
         if (this == MobEffects.INSTANT_DAMAGE && !entityLivingBaseIn.isEntityUndead() || this == MobEffects.INSTANT_HEALTH && entityLivingBaseIn.isEntityUndead()) {
            entityLivingBaseIn.attackEntityFrom(DamageSource.MAGIC, (float)(6 << amplifier));
         }
      } else {
         entityLivingBaseIn.heal((float)Math.max(4 << amplifier, 0));
      }

   }

   public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, EntityLivingBase entityLivingBaseIn, int amplifier, double health) {
      if ((this != MobEffects.INSTANT_HEALTH || entityLivingBaseIn.isEntityUndead()) && (this != MobEffects.INSTANT_DAMAGE || !entityLivingBaseIn.isEntityUndead())) {
         if (this == MobEffects.INSTANT_DAMAGE && !entityLivingBaseIn.isEntityUndead() || this == MobEffects.INSTANT_HEALTH && entityLivingBaseIn.isEntityUndead()) {
            int j = (int)(health * (double)(6 << amplifier) + 0.5D);
            if (source == null) {
               entityLivingBaseIn.attackEntityFrom(DamageSource.MAGIC, (float)j);
            } else {
               entityLivingBaseIn.attackEntityFrom(DamageSource.causeIndirectMagicDamage(source, indirectSource), (float)j);
            }
         } else {
            this.performEffect(entityLivingBaseIn, amplifier);
         }
      } else {
         int i = (int)(health * (double)(4 << amplifier) + 0.5D);
         entityLivingBaseIn.heal((float)i);
      }

   }

   /**
    * checks if Potion effect is ready to be applied this tick.
    */
   public boolean isReady(int duration, int amplifier) {
      if (this == MobEffects.REGENERATION) {
         int k = 50 >> amplifier;
         if (k > 0) {
            return duration % k == 0;
         } else {
            return true;
         }
      } else if (this == MobEffects.POISON) {
         int j = 25 >> amplifier;
         if (j > 0) {
            return duration % j == 0;
         } else {
            return true;
         }
      } else if (this == MobEffects.WITHER) {
         int i = 40 >> amplifier;
         if (i > 0) {
            return duration % i == 0;
         } else {
            return true;
         }
      } else {
         return this == MobEffects.HUNGER;
      }
   }

   /**
    * Returns true if the potion has an instant effect instead of a continuous one (eg Harming)
    */
   public boolean isInstant() {
      return false;
   }

   protected String getOrCreateDescriptionId() {
      if (this.name == null) {
         this.name = Util.makeTranslationKey("effect", IRegistry.field_212631_t.getKey(this));
      }

      return this.name;
   }

   /**
    * returns the name of the potion
    */
   public String getName() {
      return this.getOrCreateDescriptionId();
   }

   public ITextComponent getDisplayName() {
      return new TextComponentTranslation(this.getName());
   }

   /**
    * Returns true if the potion has a associated status icon to display in then inventory when active.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean hasStatusIcon() {
      return this.statusIconIndex >= 0;
   }

   /**
    * Returns the index for the icon to display when the potion is active.
    */
   @OnlyIn(Dist.CLIENT)
   public int getStatusIconIndex() {
      return this.statusIconIndex;
   }

   /**
    * This method returns true if the potion effect is bad - negative - for the entity.
    */
   public boolean isBadEffect() {
      return this.isBadEffect;
   }

   protected Potion setEffectiveness(double effectivenessIn) {
      this.effectiveness = effectivenessIn;
      return this;
   }

   /**
    * Returns the color of the potion liquid.
    */
   public int getLiquidColor() {
      return this.liquidColor;
   }

   /**
    * Used by potions to register the attribute they modify.
    */
   public Potion registerPotionAttributeModifier(IAttribute attribute, String uniqueId, double ammount, int operation) {
      AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(uniqueId), this::getName, ammount, operation);
      this.attributeModifierMap.put(attribute, attributemodifier);
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public Map<IAttribute, AttributeModifier> getAttributeModifierMap() {
      return this.attributeModifierMap;
   }

   public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      for(Entry<IAttribute, AttributeModifier> entry : this.attributeModifierMap.entrySet()) {
         IAttributeInstance iattributeinstance = attributeMapIn.getAttributeInstance(entry.getKey());
         if (iattributeinstance != null) {
            iattributeinstance.removeModifier(entry.getValue());
         }
      }

   }

   public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      for(Entry<IAttribute, AttributeModifier> entry : this.attributeModifierMap.entrySet()) {
         IAttributeInstance iattributeinstance = attributeMapIn.getAttributeInstance(entry.getKey());
         if (iattributeinstance != null) {
            AttributeModifier attributemodifier = entry.getValue();
            iattributeinstance.removeModifier(attributemodifier);
            iattributeinstance.applyModifier(new AttributeModifier(attributemodifier.getID(), this.getName() + " " + amplifier, this.getAttributeModifierAmount(amplifier, attributemodifier), attributemodifier.getOperation()));
         }
      }

   }

   public double getAttributeModifierAmount(int amplifier, AttributeModifier modifier) {
      return modifier.getAmount() * (double)(amplifier + 1);
   }

   /**
    * Get if the potion is beneficial to the player. Beneficial potions are shown on the first row of the HUD
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isBeneficial() {
      return this.beneficial;
   }

   /**
    * Set that the potion is beneficial to the player. Beneficial potions are shown on the first row of the HUD
    */
   public Potion setBeneficial() {
      this.beneficial = true;
      return this;
   }

   public static void registerPotions() {
      register(1, "speed", (new Potion(false, 8171462)).setIconIndex(0, 0).registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", (double)0.2F, 2).setBeneficial());
      register(2, "slowness", (new Potion(true, 5926017)).setIconIndex(1, 0).registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", (double)-0.15F, 2));
      register(3, "haste", (new Potion(false, 14270531)).setIconIndex(2, 0).setEffectiveness(1.5D).setBeneficial().registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", (double)0.1F, 2));
      register(4, "mining_fatigue", (new Potion(true, 4866583)).setIconIndex(3, 0).registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", (double)-0.1F, 2));
      register(5, "strength", (new PotionAttackDamage(false, 9643043, 3.0D)).setIconIndex(4, 0).registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0D, 0).setBeneficial());
      register(6, "instant_health", (new PotionInstant(false, 16262179)).setBeneficial());
      register(7, "instant_damage", (new PotionInstant(true, 4393481)).setBeneficial());
      register(8, "jump_boost", (new Potion(false, 2293580)).setIconIndex(2, 1).setBeneficial());
      register(9, "nausea", (new Potion(true, 5578058)).setIconIndex(3, 1).setEffectiveness(0.25D));
      register(10, "regeneration", (new Potion(false, 13458603)).setIconIndex(7, 0).setEffectiveness(0.25D).setBeneficial());
      register(11, "resistance", (new Potion(false, 10044730)).setIconIndex(6, 1).setBeneficial());
      register(12, "fire_resistance", (new Potion(false, 14981690)).setIconIndex(7, 1).setBeneficial());
      register(13, "water_breathing", (new Potion(false, 3035801)).setIconIndex(0, 2).setBeneficial());
      register(14, "invisibility", (new Potion(false, 8356754)).setIconIndex(0, 1).setBeneficial());
      register(15, "blindness", (new Potion(true, 2039587)).setIconIndex(5, 1).setEffectiveness(0.25D));
      register(16, "night_vision", (new Potion(false, 2039713)).setIconIndex(4, 1).setBeneficial());
      register(17, "hunger", (new Potion(true, 5797459)).setIconIndex(1, 1));
      register(18, "weakness", (new PotionAttackDamage(true, 4738376, -4.0D)).setIconIndex(5, 0).registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0D, 0));
      register(19, "poison", (new Potion(true, 5149489)).setIconIndex(6, 0).setEffectiveness(0.25D));
      register(20, "wither", (new Potion(true, 3484199)).setIconIndex(1, 2).setEffectiveness(0.25D));
      register(21, "health_boost", (new PotionHealthBoost(false, 16284963)).setIconIndex(7, 2).registerPotionAttributeModifier(SharedMonsterAttributes.MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, 0).setBeneficial());
      register(22, "absorption", (new PotionAbsorption(false, 2445989)).setIconIndex(2, 2).setBeneficial());
      register(23, "saturation", (new PotionInstant(false, 16262179)).setBeneficial());
      register(24, "glowing", (new Potion(false, 9740385)).setIconIndex(4, 2));
      register(25, "levitation", (new Potion(true, 13565951)).setIconIndex(3, 2));
      register(26, "luck", (new Potion(false, 3381504)).setIconIndex(5, 2).setBeneficial().registerPotionAttributeModifier(SharedMonsterAttributes.LUCK, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0D, 0));
      register(27, "unluck", (new Potion(true, 12624973)).setIconIndex(6, 2).registerPotionAttributeModifier(SharedMonsterAttributes.LUCK, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0D, 0));
      register(28, "slow_falling", (new Potion(false, 16773073)).setIconIndex(8, 0).setBeneficial());
      register(29, "conduit_power", (new Potion(false, 1950417)).setIconIndex(9, 0).setBeneficial());
      register(30, "dolphins_grace", (new Potion(false, 8954814)).setIconIndex(10, 0).setBeneficial());
   }

   private static void register(int id, String nameIn, Potion potionIn) {
      IRegistry.field_212631_t.register(id, new ResourceLocation(nameIn), potionIn);
   }

   /**
    * If the Potion effect should be displayed in the players inventory
    * @param effect the active PotionEffect
    * @return true to display it (default), false to hide it.
    */
   public boolean shouldRender(PotionEffect effect) { return true; }

   /**
    * If the standard PotionEffect text (name and duration) should be drawn when this potion is active.
    * @param effect the active PotionEffect
    * @return true to draw the standard text
    */
   public boolean shouldRenderInvText(PotionEffect effect) { return true; }

   /**
    * If the Potion effect should be displayed in the player's ingame HUD
    * @param effect the active PotionEffect
    * @return true to display it (default), false to hide it.
    */
   public boolean shouldRenderHUD(PotionEffect effect) { return true; }

   /**
    * Called to draw the this Potion onto the player's inventory when it's active.
    * This can be used to e.g. render Potion icons from your own texture.
    * @param x the x coordinate
    * @param y the y coordinate
    * @param effect the active PotionEffect
    * @param mc the Minecraft instance, for convenience
    */
   @OnlyIn(Dist.CLIENT)
   public void renderInventoryEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc) { }

   /**
    * Called to draw the this Potion onto the player's ingame HUD when it's active.
    * This can be used to e.g. render Potion icons from your own texture.
    * @param x the x coordinate
    * @param y the y coordinate
    * @param effect the active PotionEffect
    * @param mc the Minecraft instance, for convenience
    * @param alpha the alpha value, blinks when the potion is about to run out
    */
   @OnlyIn(Dist.CLIENT)
   public void renderHUDEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc, float alpha) { }

   /**
    * Get a fresh list of items that can cure this Potion.
    * All new PotionEffects created from this Potion will call this to initialize the default curative items
    * @see PotionEffect#getCurativeItems
    * @return A list of items that can cure this Potion
    */
   public java.util.List<net.minecraft.item.ItemStack> getCurativeItems() {
      java.util.ArrayList<net.minecraft.item.ItemStack> ret = new java.util.ArrayList<net.minecraft.item.ItemStack>();
      ret.add(new net.minecraft.item.ItemStack(net.minecraft.init.Items.MILK_BUCKET));
      return ret;
   }

   /**
    * Used for determining {@code PotionEffect} sort order in GUIs.
    * Defaults to the {@code PotionEffect}'s liquid color.
    * @param potionEffect the {@code PotionEffect} instance containing the potion
    * @return a value used to sort {@code PotionEffect}s in GUIs
    */
   public int getGuiSortColor(PotionEffect potionEffect) {
      return this.getLiquidColor();
   }
}