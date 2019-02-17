package net.minecraft.entity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityDrowned;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPhantom;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCod;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityDolphin;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityPufferFish;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySalmon;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityTropicalFish;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityTrident;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityType<T extends Entity> extends net.minecraftforge.registries.ForgeRegistryEntry<EntityType<?>> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final EntityType<EntityAreaEffectCloud> AREA_EFFECT_CLOUD = register("area_effect_cloud", EntityType.Builder.create(EntityAreaEffectCloud.class, EntityAreaEffectCloud::new));
   public static final EntityType<EntityArmorStand> ARMOR_STAND = register("armor_stand", EntityType.Builder.create(EntityArmorStand.class, EntityArmorStand::new));
   public static final EntityType<EntityTippedArrow> ARROW = register("arrow", EntityType.Builder.create(EntityTippedArrow.class, EntityTippedArrow::new));
   public static final EntityType<EntityBat> BAT = register("bat", EntityType.Builder.create(EntityBat.class, EntityBat::new));
   public static final EntityType<EntityBlaze> BLAZE = register("blaze", EntityType.Builder.create(EntityBlaze.class, EntityBlaze::new));
   public static final EntityType<EntityBoat> BOAT = register("boat", EntityType.Builder.create(EntityBoat.class, EntityBoat::new));
   public static final EntityType<EntityCaveSpider> CAVE_SPIDER = register("cave_spider", EntityType.Builder.create(EntityCaveSpider.class, EntityCaveSpider::new));
   public static final EntityType<EntityChicken> CHICKEN = register("chicken", EntityType.Builder.create(EntityChicken.class, EntityChicken::new));
   public static final EntityType<EntityCod> COD = register("cod", EntityType.Builder.create(EntityCod.class, EntityCod::new));
   public static final EntityType<EntityCow> COW = register("cow", EntityType.Builder.create(EntityCow.class, EntityCow::new));
   public static final EntityType<EntityCreeper> CREEPER = register("creeper", EntityType.Builder.create(EntityCreeper.class, EntityCreeper::new));
   public static final EntityType<EntityDonkey> DONKEY = register("donkey", EntityType.Builder.create(EntityDonkey.class, EntityDonkey::new));
   public static final EntityType<EntityDolphin> DOLPHIN = register("dolphin", EntityType.Builder.create(EntityDolphin.class, EntityDolphin::new));
   public static final EntityType<EntityDragonFireball> DRAGON_FIREBALL = register("dragon_fireball", EntityType.Builder.create(EntityDragonFireball.class, EntityDragonFireball::new));
   public static final EntityType<EntityDrowned> DROWNED = register("drowned", EntityType.Builder.create(EntityDrowned.class, EntityDrowned::new));
   public static final EntityType<EntityElderGuardian> ELDER_GUARDIAN = register("elder_guardian", EntityType.Builder.create(EntityElderGuardian.class, EntityElderGuardian::new));
   public static final EntityType<EntityEnderCrystal> END_CRYSTAL = register("end_crystal", EntityType.Builder.create(EntityEnderCrystal.class, EntityEnderCrystal::new));
   public static final EntityType<EntityDragon> ENDER_DRAGON = register("ender_dragon", EntityType.Builder.create(EntityDragon.class, EntityDragon::new));
   public static final EntityType<EntityEnderman> ENDERMAN = register("enderman", EntityType.Builder.create(EntityEnderman.class, EntityEnderman::new));
   public static final EntityType<EntityEndermite> ENDERMITE = register("endermite", EntityType.Builder.create(EntityEndermite.class, EntityEndermite::new));
   public static final EntityType<EntityEvokerFangs> EVOKER_FANGS = register("evoker_fangs", EntityType.Builder.create(EntityEvokerFangs.class, EntityEvokerFangs::new));
   public static final EntityType<EntityEvoker> EVOKER = register("evoker", EntityType.Builder.create(EntityEvoker.class, EntityEvoker::new));
   public static final EntityType<EntityXPOrb> EXPERIENCE_ORB = register("experience_orb", EntityType.Builder.create(EntityXPOrb.class, EntityXPOrb::new));
   public static final EntityType<EntityEnderEye> EYE_OF_ENDER = register("eye_of_ender", EntityType.Builder.create(EntityEnderEye.class, EntityEnderEye::new));
   public static final EntityType<EntityFallingBlock> FALLING_BLOCK = register("falling_block", EntityType.Builder.create(EntityFallingBlock.class, EntityFallingBlock::new));
   public static final EntityType<EntityFireworkRocket> FIREWORK_ROCKET = register("firework_rocket", EntityType.Builder.create(EntityFireworkRocket.class, EntityFireworkRocket::new));
   public static final EntityType<EntityGhast> GHAST = register("ghast", EntityType.Builder.create(EntityGhast.class, EntityGhast::new));
   public static final EntityType<EntityGiantZombie> GIANT = register("giant", EntityType.Builder.create(EntityGiantZombie.class, EntityGiantZombie::new));
   public static final EntityType<EntityGuardian> GUARDIAN = register("guardian", EntityType.Builder.create(EntityGuardian.class, EntityGuardian::new));
   public static final EntityType<EntityHorse> HORSE = register("horse", EntityType.Builder.create(EntityHorse.class, EntityHorse::new));
   public static final EntityType<EntityHusk> HUSK = register("husk", EntityType.Builder.create(EntityHusk.class, EntityHusk::new));
   public static final EntityType<EntityIllusionIllager> ILLUSIONER = register("illusioner", EntityType.Builder.create(EntityIllusionIllager.class, EntityIllusionIllager::new));
   public static final EntityType<EntityItem> ITEM = register("item", EntityType.Builder.create(EntityItem.class, EntityItem::new));
   public static final EntityType<EntityItemFrame> ITEM_FRAME = register("item_frame", EntityType.Builder.create(EntityItemFrame.class, EntityItemFrame::new));
   public static final EntityType<EntityLargeFireball> FIREBALL = register("fireball", EntityType.Builder.create(EntityLargeFireball.class, EntityLargeFireball::new));
   public static final EntityType<EntityLeashKnot> LEASH_KNOT = register("leash_knot", EntityType.Builder.create(EntityLeashKnot.class, EntityLeashKnot::new).disableSerialization());
   public static final EntityType<EntityLlama> LLAMA = register("llama", EntityType.Builder.create(EntityLlama.class, EntityLlama::new));
   public static final EntityType<EntityLlamaSpit> LLAMA_SPIT = register("llama_spit", EntityType.Builder.create(EntityLlamaSpit.class, EntityLlamaSpit::new));
   public static final EntityType<EntityMagmaCube> MAGMA_CUBE = register("magma_cube", EntityType.Builder.create(EntityMagmaCube.class, EntityMagmaCube::new));
   public static final EntityType<EntityMinecartEmpty> MINECART = register("minecart", EntityType.Builder.create(EntityMinecartEmpty.class, EntityMinecartEmpty::new));
   public static final EntityType<EntityMinecartChest> CHEST_MINECART = register("chest_minecart", EntityType.Builder.create(EntityMinecartChest.class, EntityMinecartChest::new));
   public static final EntityType<EntityMinecartCommandBlock> COMMAND_BLOCK_MINECART = register("command_block_minecart", EntityType.Builder.create(EntityMinecartCommandBlock.class, EntityMinecartCommandBlock::new));
   public static final EntityType<EntityMinecartFurnace> FURNACE_MINECART = register("furnace_minecart", EntityType.Builder.create(EntityMinecartFurnace.class, EntityMinecartFurnace::new));
   public static final EntityType<EntityMinecartHopper> HOPPER_MINECART = register("hopper_minecart", EntityType.Builder.create(EntityMinecartHopper.class, EntityMinecartHopper::new));
   public static final EntityType<EntityMinecartMobSpawner> SPAWNER_MINECART = register("spawner_minecart", EntityType.Builder.create(EntityMinecartMobSpawner.class, EntityMinecartMobSpawner::new));
   public static final EntityType<EntityMinecartTNT> TNT_MINECART = register("tnt_minecart", EntityType.Builder.create(EntityMinecartTNT.class, EntityMinecartTNT::new));
   public static final EntityType<EntityMule> MULE = register("mule", EntityType.Builder.create(EntityMule.class, EntityMule::new));
   public static final EntityType<EntityMooshroom> MOOSHROOM = register("mooshroom", EntityType.Builder.create(EntityMooshroom.class, EntityMooshroom::new));
   public static final EntityType<EntityOcelot> OCELOT = register("ocelot", EntityType.Builder.create(EntityOcelot.class, EntityOcelot::new));
   public static final EntityType<EntityPainting> PAINTING = register("painting", EntityType.Builder.create(EntityPainting.class, EntityPainting::new));
   public static final EntityType<EntityParrot> PARROT = register("parrot", EntityType.Builder.create(EntityParrot.class, EntityParrot::new));
   public static final EntityType<EntityPig> PIG = register("pig", EntityType.Builder.create(EntityPig.class, EntityPig::new));
   public static final EntityType<EntityPufferFish> PUFFERFISH = register("pufferfish", EntityType.Builder.create(EntityPufferFish.class, EntityPufferFish::new));
   public static final EntityType<EntityPigZombie> ZOMBIE_PIGMAN = register("zombie_pigman", EntityType.Builder.create(EntityPigZombie.class, EntityPigZombie::new));
   public static final EntityType<EntityPolarBear> POLAR_BEAR = register("polar_bear", EntityType.Builder.create(EntityPolarBear.class, EntityPolarBear::new));
   public static final EntityType<EntityTNTPrimed> TNT = register("tnt", EntityType.Builder.create(EntityTNTPrimed.class, EntityTNTPrimed::new));
   public static final EntityType<EntityRabbit> RABBIT = register("rabbit", EntityType.Builder.create(EntityRabbit.class, EntityRabbit::new));
   public static final EntityType<EntitySalmon> SALMON = register("salmon", EntityType.Builder.create(EntitySalmon.class, EntitySalmon::new));
   public static final EntityType<EntitySheep> SHEEP = register("sheep", EntityType.Builder.create(EntitySheep.class, EntitySheep::new));
   public static final EntityType<EntityShulker> SHULKER = register("shulker", EntityType.Builder.create(EntityShulker.class, EntityShulker::new));
   public static final EntityType<EntityShulkerBullet> SHULKER_BULLET = register("shulker_bullet", EntityType.Builder.create(EntityShulkerBullet.class, EntityShulkerBullet::new));
   public static final EntityType<EntitySilverfish> SILVERFISH = register("silverfish", EntityType.Builder.create(EntitySilverfish.class, EntitySilverfish::new));
   public static final EntityType<EntitySkeleton> SKELETON = register("skeleton", EntityType.Builder.create(EntitySkeleton.class, EntitySkeleton::new));
   public static final EntityType<EntitySkeletonHorse> SKELETON_HORSE = register("skeleton_horse", EntityType.Builder.create(EntitySkeletonHorse.class, EntitySkeletonHorse::new));
   public static final EntityType<EntitySlime> SLIME = register("slime", EntityType.Builder.create(EntitySlime.class, EntitySlime::new));
   public static final EntityType<EntitySmallFireball> SMALL_FIREBALL = register("small_fireball", EntityType.Builder.create(EntitySmallFireball.class, EntitySmallFireball::new));
   public static final EntityType<EntitySnowman> SNOW_GOLEM = register("snow_golem", EntityType.Builder.create(EntitySnowman.class, EntitySnowman::new));
   public static final EntityType<EntitySnowball> SNOWBALL = register("snowball", EntityType.Builder.create(EntitySnowball.class, EntitySnowball::new));
   public static final EntityType<EntitySpectralArrow> SPECTRAL_ARROW = register("spectral_arrow", EntityType.Builder.create(EntitySpectralArrow.class, EntitySpectralArrow::new));
   public static final EntityType<EntitySpider> SPIDER = register("spider", EntityType.Builder.create(EntitySpider.class, EntitySpider::new));
   public static final EntityType<EntitySquid> SQUID = register("squid", EntityType.Builder.create(EntitySquid.class, EntitySquid::new));
   public static final EntityType<EntityStray> STRAY = register("stray", EntityType.Builder.create(EntityStray.class, EntityStray::new));
   public static final EntityType<EntityTropicalFish> TROPICAL_FISH = register("tropical_fish", EntityType.Builder.create(EntityTropicalFish.class, EntityTropicalFish::new));
   public static final EntityType<EntityTurtle> TURTLE = register("turtle", EntityType.Builder.create(EntityTurtle.class, EntityTurtle::new));
   public static final EntityType<EntityEgg> EGG = register("egg", EntityType.Builder.create(EntityEgg.class, EntityEgg::new));
   public static final EntityType<EntityEnderPearl> ENDER_PEARL = register("ender_pearl", EntityType.Builder.create(EntityEnderPearl.class, EntityEnderPearl::new));
   public static final EntityType<EntityExpBottle> EXPERIENCE_BOTTLE = register("experience_bottle", EntityType.Builder.create(EntityExpBottle.class, EntityExpBottle::new));
   public static final EntityType<EntityPotion> POTION = register("potion", EntityType.Builder.create(EntityPotion.class, EntityPotion::new));
   public static final EntityType<EntityVex> VEX = register("vex", EntityType.Builder.create(EntityVex.class, EntityVex::new));
   public static final EntityType<EntityVillager> VILLAGER = register("villager", EntityType.Builder.create(EntityVillager.class, EntityVillager::new));
   public static final EntityType<EntityIronGolem> IRON_GOLEM = register("iron_golem", EntityType.Builder.create(EntityIronGolem.class, EntityIronGolem::new));
   public static final EntityType<EntityVindicator> VINDICATOR = register("vindicator", EntityType.Builder.create(EntityVindicator.class, EntityVindicator::new));
   public static final EntityType<EntityWitch> WITCH = register("witch", EntityType.Builder.create(EntityWitch.class, EntityWitch::new));
   public static final EntityType<EntityWither> WITHER = register("wither", EntityType.Builder.create(EntityWither.class, EntityWither::new));
   public static final EntityType<EntityWitherSkeleton> WITHER_SKELETON = register("wither_skeleton", EntityType.Builder.create(EntityWitherSkeleton.class, EntityWitherSkeleton::new));
   public static final EntityType<EntityWitherSkull> WITHER_SKULL = register("wither_skull", EntityType.Builder.create(EntityWitherSkull.class, EntityWitherSkull::new));
   public static final EntityType<EntityWolf> WOLF = register("wolf", EntityType.Builder.create(EntityWolf.class, EntityWolf::new));
   public static final EntityType<EntityZombie> ZOMBIE = register("zombie", EntityType.Builder.create(EntityZombie.class, EntityZombie::new));
   public static final EntityType<EntityZombieHorse> ZOMBIE_HORSE = register("zombie_horse", EntityType.Builder.create(EntityZombieHorse.class, EntityZombieHorse::new));
   public static final EntityType<EntityZombieVillager> ZOMBIE_VILLAGER = register("zombie_villager", EntityType.Builder.create(EntityZombieVillager.class, EntityZombieVillager::new));
   public static final EntityType<EntityPhantom> PHANTOM = register("phantom", EntityType.Builder.create(EntityPhantom.class, EntityPhantom::new));
   public static final EntityType<EntityLightningBolt> LIGHTNING_BOLT = register("lightning_bolt", EntityType.Builder.createNothing(EntityLightningBolt.class).disableSerialization());
   public static final EntityType<EntityPlayer> PLAYER = register("player", EntityType.Builder.createNothing(EntityPlayer.class).disableSerialization().disableSummoning());
   public static final EntityType<EntityFishHook> FISHING_BOBBER = register("fishing_bobber", EntityType.Builder.createNothing(EntityFishHook.class).disableSerialization().disableSummoning());
   public static final EntityType<EntityTrident> TRIDENT = register("trident", EntityType.Builder.create(EntityTrident.class, EntityTrident::new));
   private final Class<? extends T> entityClass;
   private final Function<? super World, ? extends T> factory;
   private final boolean serializable;
   private final boolean summonable;
   @Nullable
   private String translationKey;
   @Nullable
   private ITextComponent field_212547_aX;
   @Nullable
   private final Type<?> dataType;

   public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
      EntityType<T> entitytype = builder.build(id);
      IRegistry.field_212629_r.put(new ResourceLocation(id), entitytype);
      return entitytype;
   }

   @Nullable
   public static ResourceLocation getId(EntityType<?> entityTypeIn) {
      return IRegistry.field_212629_r.getKey(entityTypeIn);
   }

   @Nullable
   public static EntityType<?> getById(String id) {
      return IRegistry.field_212629_r.func_212608_b(ResourceLocation.makeResourceLocation(id));
   }

   public EntityType(Class<? extends T> entityClassIn, Function<? super World, ? extends T> factoryIn, boolean p_i49579_3_, boolean p_i49579_4_, @Nullable Type<?> p_i49579_5_) {
      this.entityClass = entityClassIn;
      this.factory = factoryIn;
      this.serializable = p_i49579_3_;
      this.summonable = p_i49579_4_;
      this.dataType = p_i49579_5_;
   }

   @Nullable
   public Entity spawnEntity(World worldIn, @Nullable ItemStack itemIn, @Nullable EntityPlayer player, BlockPos pos, boolean p_208049_5_, boolean p_208049_6_) {
      return this.spawnEntity(worldIn, itemIn == null ? null : itemIn.getTag(), itemIn != null && itemIn.hasDisplayName() ? itemIn.getDisplayName() : null, player, pos, p_208049_5_, p_208049_6_);
   }

   @Nullable
   public T spawnEntity(World worldIn, @Nullable NBTTagCompound nbt, @Nullable ITextComponent livingEntityCustomName, @Nullable EntityPlayer p_208050_4_, BlockPos p_208050_5_, boolean p_208050_6_, boolean p_208050_7_) {
      T t = this.makeEntity(worldIn, nbt, livingEntityCustomName, p_208050_4_, p_208050_5_, p_208050_6_, p_208050_7_);
      worldIn.spawnEntity(t);
      return t;
   }

   @Nullable
   public T makeEntity(World worldIn, @Nullable NBTTagCompound nbt, @Nullable ITextComponent livingEntityCustomName, @Nullable EntityPlayer p_210761_4_, BlockPos pos, boolean p_210761_6_, boolean p_210761_7_) {
      T t = this.create(worldIn);
      if (t == null) {
         return (T)null;
      } else {
         double d0;
         if (p_210761_6_) {
            t.setPosition((double)pos.getX() + 0.5D, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5D);
            d0 = func_208051_a(worldIn, pos, p_210761_7_, t.getBoundingBox());
         } else {
            d0 = 0.0D;
         }

         t.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY() + d0, (double)pos.getZ() + 0.5D, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
         if (t instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving)t;
            entityliving.rotationYawHead = entityliving.rotationYaw;
            entityliving.renderYawOffset = entityliving.rotationYaw;
            entityliving.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityliving)), (IEntityLivingData)null, nbt);
            entityliving.playAmbientSound();
         }

         if (livingEntityCustomName != null && t instanceof EntityLivingBase) {
            t.setCustomName(livingEntityCustomName);
         }

         func_208048_a(worldIn, p_210761_4_, t, nbt);
         return t;
      }
   }

   protected static double func_208051_a(IWorldReaderBase p_208051_0_, BlockPos p_208051_1_, boolean p_208051_2_, AxisAlignedBB p_208051_3_) {
      AxisAlignedBB axisalignedbb = new AxisAlignedBB(p_208051_1_);
      if (p_208051_2_) {
         axisalignedbb = axisalignedbb.expand(0.0D, -1.0D, 0.0D);
      }

      Stream<VoxelShape> stream = p_208051_0_.func_212388_b((Entity)null, axisalignedbb);
      return 1.0D + VoxelShapes.func_212437_a(EnumFacing.Axis.Y, p_208051_3_, stream, p_208051_2_ ? -2.0D : -1.0D);
   }

   public static void func_208048_a(World p_208048_0_, @Nullable EntityPlayer p_208048_1_, @Nullable Entity p_208048_2_, @Nullable NBTTagCompound p_208048_3_) {
      if (p_208048_3_ != null && p_208048_3_.contains("EntityTag", 10)) {
         MinecraftServer minecraftserver = p_208048_0_.getServer();
         if (minecraftserver != null && p_208048_2_ != null) {
            if (p_208048_0_.isRemote || !p_208048_2_.ignoreItemEntityData() || p_208048_1_ != null && minecraftserver.getPlayerList().canSendCommands(p_208048_1_.getGameProfile())) {
               NBTTagCompound nbttagcompound = p_208048_2_.writeWithoutTypeId(new NBTTagCompound());
               UUID uuid = p_208048_2_.getUniqueID();
               nbttagcompound.merge(p_208048_3_.getCompound("EntityTag"));
               p_208048_2_.setUniqueId(uuid);
               p_208048_2_.read(nbttagcompound);
            }
         }
      }
   }

   public boolean isSerializable() {
      return this.serializable;
   }

   public boolean isSummonable() {
      return this.summonable;
   }

   public Class<? extends T> getEntityClass() {
      return this.entityClass;
   }

   public String getTranslationKey() {
      if (this.translationKey == null) {
         this.translationKey = Util.makeTranslationKey("entity", IRegistry.field_212629_r.getKey(this));
      }

      return this.translationKey;
   }

   public ITextComponent func_212546_e() {
      if (this.field_212547_aX == null) {
         this.field_212547_aX = new TextComponentTranslation(this.getTranslationKey());
      }

      return this.field_212547_aX;
   }

   @Nullable
   public T create(World worldIn) {
      return (T)(this.factory.apply(worldIn));
   }

   @Nullable
   public static Entity create(World worldIn, ResourceLocation id) {
      return create(worldIn, IRegistry.field_212629_r.func_212608_b(id));
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static Entity create(int id, World worldIn) {
      return create(worldIn, IRegistry.field_212629_r.get(id));
   }

   @Nullable
   public static Entity create(NBTTagCompound p_200716_0_, World p_200716_1_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_200716_0_.getString("id"));
      Entity entity = create(p_200716_1_, resourcelocation);
      if (entity == null) {
         LOGGER.warn("Skipping Entity with id {}", (Object)resourcelocation);
      } else {
         entity.read(p_200716_0_);
      }

      return entity;
   }

   @Nullable
   private static Entity create(World worldIn, @Nullable EntityType<?> type) {
      return type == null ? null : type.create(worldIn);
   }

   /* Forge Start */
   private boolean useVanillaSpawning;
   private Function<net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity, Entity> customSpawnCallback;
   private boolean hasCustomTracking;
   private int customTrackingRange, customUpdateFrequency;
   private boolean customSendVelocityUpdates;

   public boolean hasCustomTracking() { return hasCustomTracking; }
   public int getTrackingRange() { return customTrackingRange; }
   public int getUpdateFrequency() { return customUpdateFrequency; }
   public boolean shouldSendVelocityUpdates() { return customSendVelocityUpdates; }
   public boolean usesVanillaSpawning() { return useVanillaSpawning; }

   public EntityType(Class<? extends T> clazz, Function<? super World, ? extends T> factory, boolean serializable, boolean summonable, @Nullable Type<?> dataFixerType,
                     boolean useVanillaSpawning, Function<net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity, Entity> customSpawnCallback,
                     boolean hasCustomTracking, int range, int updateFreq, boolean sendVelocityUpdates) {
      this(clazz, factory, serializable, summonable, dataFixerType);
      this.useVanillaSpawning = useVanillaSpawning;
      this.customSpawnCallback = customSpawnCallback;
      this.hasCustomTracking = hasCustomTracking;
      this.customTrackingRange = range;
      this.customUpdateFrequency = updateFreq;
      this.customSendVelocityUpdates = sendVelocityUpdates;
   }

   @Nullable public Entity handleSpawnMessage(World world, net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity msg)
   {
      return customSpawnCallback == null ? create(world) : customSpawnCallback.apply(msg);
   }
   /* Forge End */

   public static class Builder<T extends Entity> {
      private final Class<? extends T> entityClass;
      private final Function<? super World, ? extends T> factory;
      private boolean serializable = true;
      private boolean summonable = true;
      private boolean useVanillaSpawning;
      private Function<net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity, Entity> customSpawnCallback = null;
      private boolean hasCustomTracking = false;
      private int trackingRange;
      private int updateFrequency;
      private boolean sendVelocityUpdates;

      private Builder(Class<? extends T> entityClassIn, Function<? super World, ? extends T> factoryIn) {
         this.entityClass = entityClassIn;
         this.factory = factoryIn;
         this.useVanillaSpawning = entityClass.getName().startsWith("net.minecraft.");
      }

      public static <T extends Entity> EntityType.Builder<T> create(Class<? extends T> entityClassIn, Function<? super World, ? extends T> factoryIn) {
         return new EntityType.Builder<>(entityClassIn, factoryIn);
      }

      public static <T extends Entity> EntityType.Builder<T> createNothing(Class<? extends T> entityClassIn) {
         return new EntityType.Builder<>(entityClassIn, (p_200708_0_) -> {
            return (T)null;
         });
      }

      public EntityType.Builder<T> disableSummoning() {
         this.summonable = false;
         return this;
      }

      public EntityType.Builder<T> disableSerialization() {
         this.serializable = false;
         return this;
      }

      public EntityType.Builder<T> customSpawning(Function<net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity, Entity> cb, boolean useVanillaSpawning) {
         this.customSpawnCallback = cb;
         this.useVanillaSpawning = useVanillaSpawning;
         return this;
      }

      public final EntityType.Builder<T> tracker(int range, int updateFrequency, boolean sendVelocityUpdates) {
         this.hasCustomTracking = true;
         this.trackingRange = range;
         this.updateFrequency = updateFrequency;
         this.sendVelocityUpdates = sendVelocityUpdates;
         return this;
      }

      public EntityType<T> build(String id) {
         Type<?> type = null;
         if (this.serializable) {
            try {
               type = DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(1631)).getChoiceType(TypeReferences.ENTITY_TYPE, id);
            } catch (IllegalArgumentException illegalstateexception) {
               if (SharedConstants.developmentMode) {
                  throw illegalstateexception;
               }

               EntityType.LOGGER.warn("No data fixer registered for entity {}", (Object)id);
            }
         }

         return new EntityType<>(this.entityClass, this.factory, this.serializable, this.summonable, type, useVanillaSpawning, customSpawnCallback, hasCustomTracking, trackingRange, updateFrequency, sendVelocityUpdates);
      }
   }
}