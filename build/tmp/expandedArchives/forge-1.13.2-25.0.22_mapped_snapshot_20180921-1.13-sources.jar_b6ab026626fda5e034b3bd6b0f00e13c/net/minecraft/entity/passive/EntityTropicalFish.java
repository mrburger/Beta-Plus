package net.minecraft.entity.passive;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityTropicalFish extends AbstractGroupFish {
   private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityTropicalFish.class, DataSerializers.VARINT);
   private static final ResourceLocation[] BODY_TEXTURES = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a.png"), new ResourceLocation("textures/entity/fish/tropical_b.png")};
   private static final ResourceLocation[] PATTERN_TEXTURES_A = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_6.png")};
   private static final ResourceLocation[] PATTERN_TEXTURES_B = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_b_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_6.png")};
   /** Variants that have special names */
   public static final int[] SPECIAL_VARIANTS = new int[]{pack(EntityTropicalFish.Type.STRIPEY, EnumDyeColor.ORANGE, EnumDyeColor.GRAY), pack(EntityTropicalFish.Type.FLOPPER, EnumDyeColor.GRAY, EnumDyeColor.GRAY), pack(EntityTropicalFish.Type.FLOPPER, EnumDyeColor.GRAY, EnumDyeColor.BLUE), pack(EntityTropicalFish.Type.CLAYFISH, EnumDyeColor.WHITE, EnumDyeColor.GRAY), pack(EntityTropicalFish.Type.SUNSTREAK, EnumDyeColor.BLUE, EnumDyeColor.GRAY), pack(EntityTropicalFish.Type.KOB, EnumDyeColor.ORANGE, EnumDyeColor.WHITE), pack(EntityTropicalFish.Type.SPOTTY, EnumDyeColor.PINK, EnumDyeColor.LIGHT_BLUE), pack(EntityTropicalFish.Type.BLOCKFISH, EnumDyeColor.PURPLE, EnumDyeColor.YELLOW), pack(EntityTropicalFish.Type.CLAYFISH, EnumDyeColor.WHITE, EnumDyeColor.RED), pack(EntityTropicalFish.Type.SPOTTY, EnumDyeColor.WHITE, EnumDyeColor.YELLOW), pack(EntityTropicalFish.Type.GLITTER, EnumDyeColor.WHITE, EnumDyeColor.GRAY), pack(EntityTropicalFish.Type.CLAYFISH, EnumDyeColor.WHITE, EnumDyeColor.ORANGE), pack(EntityTropicalFish.Type.DASHER, EnumDyeColor.CYAN, EnumDyeColor.PINK), pack(EntityTropicalFish.Type.BRINELY, EnumDyeColor.LIME, EnumDyeColor.LIGHT_BLUE), pack(EntityTropicalFish.Type.BETTY, EnumDyeColor.RED, EnumDyeColor.WHITE), pack(EntityTropicalFish.Type.SNOOPER, EnumDyeColor.GRAY, EnumDyeColor.RED), pack(EntityTropicalFish.Type.BLOCKFISH, EnumDyeColor.RED, EnumDyeColor.WHITE), pack(EntityTropicalFish.Type.FLOPPER, EnumDyeColor.WHITE, EnumDyeColor.YELLOW), pack(EntityTropicalFish.Type.KOB, EnumDyeColor.RED, EnumDyeColor.WHITE), pack(EntityTropicalFish.Type.SUNSTREAK, EnumDyeColor.GRAY, EnumDyeColor.WHITE), pack(EntityTropicalFish.Type.DASHER, EnumDyeColor.CYAN, EnumDyeColor.YELLOW), pack(EntityTropicalFish.Type.FLOPPER, EnumDyeColor.YELLOW, EnumDyeColor.YELLOW)};
   private boolean field_204228_bA = true;

   private static int pack(EntityTropicalFish.Type size, EnumDyeColor pattern, EnumDyeColor bodyColor) {
      return size.func_212550_a() & 255 | (size.func_212551_b() & 255) << 8 | (pattern.getId() & 255) << 16 | (bodyColor.getId() & 255) << 24;
   }

   public EntityTropicalFish(World p_i48879_1_) {
      super(EntityType.TROPICAL_FISH, p_i48879_1_);
      this.setSize(0.5F, 0.4F);
   }

   @OnlyIn(Dist.CLIENT)
   public static String func_212324_b(int p_212324_0_) {
      return "entity.minecraft.tropical_fish.predefined." + p_212324_0_;
   }

   @OnlyIn(Dist.CLIENT)
   public static EnumDyeColor func_212326_d(int p_212326_0_) {
      return EnumDyeColor.byId(getBodyColor(p_212326_0_));
   }

   @OnlyIn(Dist.CLIENT)
   public static EnumDyeColor func_212323_p(int p_212323_0_) {
      return EnumDyeColor.byId(getPatternColor(p_212323_0_));
   }

   @OnlyIn(Dist.CLIENT)
   public static String func_212327_q(int p_212327_0_) {
      int i = func_212325_s(p_212327_0_);
      int j = getPattern(p_212327_0_);
      return "entity.minecraft.tropical_fish.type." + EntityTropicalFish.Type.func_212548_a(i, j);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(VARIANT, 0);
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.setInt("Variant", this.getVariant());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.setVariant(compound.getInt("Variant"));
   }

   public void setVariant(int p_204215_1_) {
      this.dataManager.set(VARIANT, p_204215_1_);
   }

   public boolean func_204209_c(int p_204209_1_) {
      return !this.field_204228_bA;
   }

   public int getVariant() {
      return this.dataManager.get(VARIANT);
   }

   /**
    * Add extra data to the bucket that just picked this fish up
    */
   protected void setBucketData(ItemStack bucket) {
      super.setBucketData(bucket);
      NBTTagCompound nbttagcompound = bucket.getOrCreateTag();
      nbttagcompound.setInt("BucketVariantTag", this.getVariant());
   }

   protected ItemStack getFishBucket() {
      return new ItemStack(Items.TROPICAL_FISH_BUCKET);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_TROPICAL_FISH;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_TROPICAL_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_TROPICAL_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_TROPICAL_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_TROPICAL_FISH_FLOP;
   }

   @OnlyIn(Dist.CLIENT)
   private static int getBodyColor(int p_204216_0_) {
      return (p_204216_0_ & 16711680) >> 16;
   }

   @OnlyIn(Dist.CLIENT)
   public float[] func_204219_dC() {
      return EnumDyeColor.byId(getBodyColor(this.getVariant())).getColorComponentValues();
   }

   @OnlyIn(Dist.CLIENT)
   private static int getPatternColor(int p_204212_0_) {
      return (p_204212_0_ & -16777216) >> 24;
   }

   @OnlyIn(Dist.CLIENT)
   public float[] func_204222_dD() {
      return EnumDyeColor.byId(getPatternColor(this.getVariant())).getColorComponentValues();
   }

   @OnlyIn(Dist.CLIENT)
   public static int func_212325_s(int p_212325_0_) {
      return Math.min(p_212325_0_ & 255, 1);
   }

   @OnlyIn(Dist.CLIENT)
   public int getSize() {
      return func_212325_s(this.getVariant());
   }

   @OnlyIn(Dist.CLIENT)
   private static int getPattern(int p_204213_0_) {
      return Math.min((p_204213_0_ & '\uff00') >> 8, 5);
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getPatternTexture() {
      return func_212325_s(this.getVariant()) == 0 ? PATTERN_TEXTURES_A[getPattern(this.getVariant())] : PATTERN_TEXTURES_B[getPattern(this.getVariant())];
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getBodyTexture() {
      return BODY_TEXTURES[func_212325_s(this.getVariant())];
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData, @Nullable NBTTagCompound itemNbt) {
      entityLivingData = super.onInitialSpawn(difficulty, entityLivingData, itemNbt);
      if (itemNbt != null && itemNbt.contains("BucketVariantTag", 3)) {
         this.setVariant(itemNbt.getInt("BucketVariantTag"));
         return entityLivingData;
      } else {
         int i;
         int j;
         int k;
         int l;
         if (entityLivingData instanceof EntityTropicalFish.GroupData) {
            EntityTropicalFish.GroupData entitytropicalfish$groupdata = (EntityTropicalFish.GroupData)entityLivingData;
            i = entitytropicalfish$groupdata.size;
            j = entitytropicalfish$groupdata.pattern;
            k = entitytropicalfish$groupdata.bodyColor;
            l = entitytropicalfish$groupdata.patternColor;
         } else if ((double)this.rand.nextFloat() < 0.9D) {
            int i1 = SPECIAL_VARIANTS[this.rand.nextInt(SPECIAL_VARIANTS.length)];
            i = i1 & 255;
            j = (i1 & '\uff00') >> 8;
            k = (i1 & 16711680) >> 16;
            l = (i1 & -16777216) >> 24;
            entityLivingData = new EntityTropicalFish.GroupData(this, i, j, k, l);
         } else {
            this.field_204228_bA = false;
            i = this.rand.nextInt(2);
            j = this.rand.nextInt(6);
            k = this.rand.nextInt(15);
            l = this.rand.nextInt(15);
         }

         this.setVariant(i | j << 8 | k << 16 | l << 24);
         return entityLivingData;
      }
   }

   static class GroupData extends AbstractGroupFish.GroupData {
      private final int size;
      private final int pattern;
      private final int bodyColor;
      private final int patternColor;

      private GroupData(EntityTropicalFish p_i49859_1_, int p_i49859_2_, int p_i49859_3_, int p_i49859_4_, int p_i49859_5_) {
         super(p_i49859_1_);
         this.size = p_i49859_2_;
         this.pattern = p_i49859_3_;
         this.bodyColor = p_i49859_4_;
         this.patternColor = p_i49859_5_;
      }
   }

   static enum Type {
      KOB(0, 0),
      SUNSTREAK(0, 1),
      SNOOPER(0, 2),
      DASHER(0, 3),
      BRINELY(0, 4),
      SPOTTY(0, 5),
      FLOPPER(1, 0),
      STRIPEY(1, 1),
      GLITTER(1, 2),
      BLOCKFISH(1, 3),
      BETTY(1, 4),
      CLAYFISH(1, 5);

      private final int field_212552_m;
      private final int field_212553_n;
      private static final EntityTropicalFish.Type[] field_212554_o = values();

      private Type(int p_i49832_3_, int p_i49832_4_) {
         this.field_212552_m = p_i49832_3_;
         this.field_212553_n = p_i49832_4_;
      }

      public int func_212550_a() {
         return this.field_212552_m;
      }

      public int func_212551_b() {
         return this.field_212553_n;
      }

      @OnlyIn(Dist.CLIENT)
      public static String func_212548_a(int p_212548_0_, int p_212548_1_) {
         return field_212554_o[p_212548_1_ + 6 * p_212548_0_].func_212549_c();
      }

      @OnlyIn(Dist.CLIENT)
      public String func_212549_c() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}