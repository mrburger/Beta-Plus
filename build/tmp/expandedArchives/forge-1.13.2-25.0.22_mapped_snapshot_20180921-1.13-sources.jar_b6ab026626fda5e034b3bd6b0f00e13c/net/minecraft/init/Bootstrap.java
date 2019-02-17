package net.minecraft.init;

import java.io.PrintStream;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarvedPumpkin;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSkullWither;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.EntityOptions;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBoneMeal;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.PotionType;
import net.minecraft.server.DebugLoggingPrintStream;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.LoggingPrintStream;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
   public static final PrintStream SYSOUT = System.out;
   /** Whether the blocks, items, etc have already been registered */
   private static boolean alreadyRegistered;
   private static final Logger LOGGER = LogManager.getLogger();

   /**
    * Is Bootstrap registration already done?
    */
   public static boolean isRegistered() {
      return alreadyRegistered;
   }

   static void registerDispenserBehaviors() {
      BlockDispenser.registerDispenseBehavior(Items.ARROW, new BehaviorProjectileDispense() {
         /**
          * Return the projectile entity spawned by this dispense behavior.
          */
         protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
            EntityTippedArrow entitytippedarrow = new EntityTippedArrow(worldIn, position.getX(), position.getY(), position.getZ());
            entitytippedarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
            return entitytippedarrow;
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.TIPPED_ARROW, new BehaviorProjectileDispense() {
         /**
          * Return the projectile entity spawned by this dispense behavior.
          */
         protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
            EntityTippedArrow entitytippedarrow = new EntityTippedArrow(worldIn, position.getX(), position.getY(), position.getZ());
            entitytippedarrow.setPotionEffect(stackIn);
            entitytippedarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
            return entitytippedarrow;
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.SPECTRAL_ARROW, new BehaviorProjectileDispense() {
         /**
          * Return the projectile entity spawned by this dispense behavior.
          */
         protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
            EntityArrow entityarrow = new EntitySpectralArrow(worldIn, position.getX(), position.getY(), position.getZ());
            entityarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
            return entityarrow;
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.EGG, new BehaviorProjectileDispense() {
         /**
          * Return the projectile entity spawned by this dispense behavior.
          */
         protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
            return new EntityEgg(worldIn, position.getX(), position.getY(), position.getZ());
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.SNOWBALL, new BehaviorProjectileDispense() {
         /**
          * Return the projectile entity spawned by this dispense behavior.
          */
         protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
            return new EntitySnowball(worldIn, position.getX(), position.getY(), position.getZ());
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.EXPERIENCE_BOTTLE, new BehaviorProjectileDispense() {
         /**
          * Return the projectile entity spawned by this dispense behavior.
          */
         protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
            return new EntityExpBottle(worldIn, position.getX(), position.getY(), position.getZ());
         }

         protected float getProjectileInaccuracy() {
            return super.getProjectileInaccuracy() * 0.5F;
         }

         protected float getProjectileVelocity() {
            return super.getProjectileVelocity() * 1.25F;
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.SPLASH_POTION, new IBehaviorDispenseItem() {
         public ItemStack dispense(IBlockSource p_dispense_1_, final ItemStack p_dispense_2_) {
            return (new BehaviorProjectileDispense() {
               /**
                * Return the projectile entity spawned by this dispense behavior.
                */
               protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                  return new EntityPotion(worldIn, position.getX(), position.getY(), position.getZ(), p_dispense_2_.copy());
               }

               protected float getProjectileInaccuracy() {
                  return super.getProjectileInaccuracy() * 0.5F;
               }

               protected float getProjectileVelocity() {
                  return super.getProjectileVelocity() * 1.25F;
               }
            }).dispense(p_dispense_1_, p_dispense_2_);
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.LINGERING_POTION, new IBehaviorDispenseItem() {
         public ItemStack dispense(IBlockSource p_dispense_1_, final ItemStack p_dispense_2_) {
            return (new BehaviorProjectileDispense() {
               /**
                * Return the projectile entity spawned by this dispense behavior.
                */
               protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                  return new EntityPotion(worldIn, position.getX(), position.getY(), position.getZ(), p_dispense_2_.copy());
               }

               protected float getProjectileInaccuracy() {
                  return super.getProjectileInaccuracy() * 0.5F;
               }

               protected float getProjectileVelocity() {
                  return super.getProjectileVelocity() * 1.25F;
               }
            }).dispense(p_dispense_1_, p_dispense_2_);
         }
      });
      BehaviorDefaultDispenseItem behaviordefaultdispenseitem = new BehaviorDefaultDispenseItem() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing enumfacing = source.getBlockState().get(BlockDispenser.FACING);
            EntityType<?> entitytype = ((ItemSpawnEgg)stack.getItem()).getType(stack.getTag());
            if (entitytype != null) {
               entitytype.spawnEntity(source.getWorld(), stack, (EntityPlayer)null, source.getBlockPos().offset(enumfacing), enumfacing != EnumFacing.UP, false);
            }

            stack.shrink(1);
            return stack;
         }
      };

      for(ItemSpawnEgg itemspawnegg : ItemSpawnEgg.getEggs()) {
         BlockDispenser.registerDispenseBehavior(itemspawnegg, behaviordefaultdispenseitem);
      }

      BlockDispenser.registerDispenseBehavior(Items.FIREWORK_ROCKET, new BehaviorDefaultDispenseItem() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing enumfacing = source.getBlockState().get(BlockDispenser.FACING);
            double d0 = source.getX() + (double)enumfacing.getXOffset();
            double d1 = (double)((float)source.getBlockPos().getY() + 0.2F);
            double d2 = source.getZ() + (double)enumfacing.getZOffset();
            EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(source.getWorld(), d0, d1, d2, stack);
            source.getWorld().spawnEntity(entityfireworkrocket);
            stack.shrink(1);
            return stack;
         }

         /**
          * Play the dispense sound from the specified block.
          */
         protected void playDispenseSound(IBlockSource source) {
            source.getWorld().playEvent(1004, source.getBlockPos(), 0);
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.FIRE_CHARGE, new BehaviorDefaultDispenseItem() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing enumfacing = source.getBlockState().get(BlockDispenser.FACING);
            IPosition iposition = BlockDispenser.getDispensePosition(source);
            double d0 = iposition.getX() + (double)((float)enumfacing.getXOffset() * 0.3F);
            double d1 = iposition.getY() + (double)((float)enumfacing.getYOffset() * 0.3F);
            double d2 = iposition.getZ() + (double)((float)enumfacing.getZOffset() * 0.3F);
            World world = source.getWorld();
            Random random = world.rand;
            double d3 = random.nextGaussian() * 0.05D + (double)enumfacing.getXOffset();
            double d4 = random.nextGaussian() * 0.05D + (double)enumfacing.getYOffset();
            double d5 = random.nextGaussian() * 0.05D + (double)enumfacing.getZOffset();
            world.spawnEntity(new EntitySmallFireball(world, d0, d1, d2, d3, d4, d5));
            stack.shrink(1);
            return stack;
         }

         /**
          * Play the dispense sound from the specified block.
          */
         protected void playDispenseSound(IBlockSource source) {
            source.getWorld().playEvent(1018, source.getBlockPos(), 0);
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.OAK_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.OAK));
      BlockDispenser.registerDispenseBehavior(Items.SPRUCE_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.SPRUCE));
      BlockDispenser.registerDispenseBehavior(Items.BIRCH_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.BIRCH));
      BlockDispenser.registerDispenseBehavior(Items.JUNGLE_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.JUNGLE));
      BlockDispenser.registerDispenseBehavior(Items.DARK_OAK_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.DARK_OAK));
      BlockDispenser.registerDispenseBehavior(Items.ACACIA_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.ACACIA));
      IBehaviorDispenseItem ibehaviordispenseitem = new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();

         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            ItemBucket itembucket = (ItemBucket)stack.getItem();
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(BlockDispenser.FACING));
            World world = source.getWorld();
            if (itembucket.tryPlaceContainedLiquid((EntityPlayer)null, world, blockpos, (RayTraceResult)null)) {
               itembucket.onLiquidPlaced(world, stack, blockpos);
               return new ItemStack(Items.BUCKET);
            } else {
               return this.dispenseBehavior.dispense(source, stack);
            }
         }
      };
      BlockDispenser.registerDispenseBehavior(Items.LAVA_BUCKET, ibehaviordispenseitem);
      BlockDispenser.registerDispenseBehavior(Items.WATER_BUCKET, ibehaviordispenseitem);
      BlockDispenser.registerDispenseBehavior(Items.SALMON_BUCKET, ibehaviordispenseitem);
      BlockDispenser.registerDispenseBehavior(Items.COD_BUCKET, ibehaviordispenseitem);
      BlockDispenser.registerDispenseBehavior(Items.PUFFERFISH_BUCKET, ibehaviordispenseitem);
      BlockDispenser.registerDispenseBehavior(Items.TROPICAL_FISH_BUCKET, ibehaviordispenseitem);
      BlockDispenser.registerDispenseBehavior(Items.MILK_BUCKET, net.minecraftforge.fluids.DispenseFluidContainer.getInstance());
      BlockDispenser.registerDispenseBehavior(Items.BUCKET, net.minecraftforge.fluids.DispenseFluidContainer.getInstance());
      if (false)
      BlockDispenser.registerDispenseBehavior(Items.BUCKET, new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();

         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            IWorld iworld = source.getWorld();
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(BlockDispenser.FACING));
            IBlockState iblockstate = iworld.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            if (block instanceof IBucketPickupHandler) {
               Fluid fluid = ((IBucketPickupHandler)block).pickupFluid(iworld, blockpos, iblockstate);
               if (!(fluid instanceof FlowingFluid)) {
                  return super.dispenseStack(source, stack);
               } else {
                  Item item = fluid.getFilledBucket();
                  stack.shrink(1);
                  if (stack.isEmpty()) {
                     return new ItemStack(item);
                  } else {
                     if (source.<TileEntityDispenser>getBlockTileEntity().addItemStack(new ItemStack(item)) < 0) {
                        this.dispenseBehavior.dispense(source, new ItemStack(item));
                     }

                     return stack;
                  }
               }
            } else {
               return super.dispenseStack(source, stack);
            }
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.FLINT_AND_STEEL, new Bootstrap.BehaviorDispenseOptional() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            this.successful = true;
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(BlockDispenser.FACING));
            if (ItemFlintAndSteel.canIgnite(world, blockpos)) {
               world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
            } else {
               Block block = world.getBlockState(blockpos).getBlock();
               if (block instanceof BlockTNT) {
                  ((BlockTNT)block).explode(world, blockpos);
                  world.removeBlock(blockpos);
               } else {
                  this.successful = false;
               }
            }

            if (this.successful && stack.attemptDamageItem(1, world.rand, (EntityPlayerMP)null)) {
               stack.setCount(0);
            }

            return stack;
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.BONE_MEAL, new Bootstrap.BehaviorDispenseOptional() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            this.successful = true;
            World world = source.getWorld();
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(BlockDispenser.FACING));
            if (!ItemBoneMeal.applyBonemeal(stack, world, blockpos) && !ItemBoneMeal.growSeagrass(stack, world, blockpos, (EnumFacing)null)) {
               this.successful = false;
            } else if (!world.isRemote) {
               world.playEvent(2005, blockpos, 0);
            }

            return stack;
         }
      });
      BlockDispenser.registerDispenseBehavior(Blocks.TNT, new BehaviorDefaultDispenseItem() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(BlockDispenser.FACING));
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D, (EntityLivingBase)null);
            world.spawnEntity(entitytntprimed);
            world.playSound((EntityPlayer)null, entitytntprimed.posX, entitytntprimed.posY, entitytntprimed.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            stack.shrink(1);
            return stack;
         }
      });
      Bootstrap.BehaviorDispenseOptional bootstrap$behaviordispenseoptional = new Bootstrap.BehaviorDispenseOptional() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            this.successful = !ItemArmor.dispenseArmor(source, stack).isEmpty();
            return stack;
         }
      };
      BlockDispenser.registerDispenseBehavior(Items.CREEPER_HEAD, bootstrap$behaviordispenseoptional);
      BlockDispenser.registerDispenseBehavior(Items.ZOMBIE_HEAD, bootstrap$behaviordispenseoptional);
      BlockDispenser.registerDispenseBehavior(Items.DRAGON_HEAD, bootstrap$behaviordispenseoptional);
      BlockDispenser.registerDispenseBehavior(Items.SKELETON_SKULL, bootstrap$behaviordispenseoptional);
      BlockDispenser.registerDispenseBehavior(Items.PLAYER_HEAD, bootstrap$behaviordispenseoptional);
      BlockDispenser.registerDispenseBehavior(Items.WITHER_SKELETON_SKULL, new Bootstrap.BehaviorDispenseOptional() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            EnumFacing enumfacing = source.getBlockState().get(BlockDispenser.FACING);
            BlockPos blockpos = source.getBlockPos().offset(enumfacing);
            this.successful = true;
            if (world.isAirBlock(blockpos) && BlockSkullWither.canSpawnMob(world, blockpos, stack)) {
               world.setBlockState(blockpos, Blocks.WITHER_SKELETON_SKULL.getDefaultState().with(BlockSkull.ROTATION, Integer.valueOf(enumfacing.getAxis() == EnumFacing.Axis.Y ? 0 : enumfacing.getOpposite().getHorizontalIndex() * 4)), 3);
               TileEntity tileentity = world.getTileEntity(blockpos);
               if (tileentity instanceof TileEntitySkull) {
                  BlockSkullWither.checkWitherSpawn(world, blockpos, (TileEntitySkull)tileentity);
               }

               stack.shrink(1);
            } else if (ItemArmor.dispenseArmor(source, stack).isEmpty()) {
               this.successful = false;
            }

            return stack;
         }
      });
      BlockDispenser.registerDispenseBehavior(Blocks.CARVED_PUMPKIN, new Bootstrap.BehaviorDispenseOptional() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(BlockDispenser.FACING));
            BlockCarvedPumpkin blockcarvedpumpkin = (BlockCarvedPumpkin)Blocks.CARVED_PUMPKIN;
            this.successful = true;
            if (world.isAirBlock(blockpos) && blockcarvedpumpkin.canDispenserPlace(world, blockpos)) {
               if (!world.isRemote) {
                  world.setBlockState(blockpos, blockcarvedpumpkin.getDefaultState(), 3);
               }

               stack.shrink(1);
            } else {
               ItemStack itemstack = ItemArmor.dispenseArmor(source, stack);
               if (itemstack.isEmpty()) {
                  this.successful = false;
               }
            }

            return stack;
         }
      });
      BlockDispenser.registerDispenseBehavior(Blocks.SHULKER_BOX.asItem(), new Bootstrap.BehaviorDispenseShulkerBox());

      for(EnumDyeColor enumdyecolor : EnumDyeColor.values()) {
         BlockDispenser.registerDispenseBehavior(BlockShulkerBox.getBlockByColor(enumdyecolor).asItem(), new Bootstrap.BehaviorDispenseShulkerBox());
      }

   }

   /**
    * Registers blocks, items, stats, etc.
    */
   public static void register() {
      if (!alreadyRegistered) {
         alreadyRegistered = true;
         SoundEvent.registerSounds();
         Fluid.registerAll();
         Block.registerBlocks();
         BlockFire.init();
         Potion.registerPotions();
         Enchantment.registerEnchantments();
         if (EntityType.getId(EntityType.PLAYER) == null) {
            throw new IllegalStateException("Failed loading EntityTypes");
         } else {
            Item.registerItems();
            PotionType.registerPotionTypes();
            PotionBrewing.init();
            Biome.registerBiomes();
            EntityOptions.registerOptions();
            ParticleType.registerAll();
            registerDispenserBehaviors();
            ArgumentTypes.registerArgumentTypes();
            BiomeProviderType.func_212580_a();
            TileEntityType.func_212641_a();
            ChunkGeneratorType.func_212675_a();
            DimensionType.func_212680_a();
            PaintingType.validateRegistry();
            StatList.func_212734_a();
            IRegistry.func_212613_e();
            net.minecraftforge.fml.common.registry.VillagerRegistry.instance();
            if (SharedConstants.developmentMode) {
               checkTranslationKeys("block", IRegistry.field_212618_g, Block::getTranslationKey);
               checkTranslationKeys("biome", IRegistry.field_212624_m, Biome::getTranslationKey);
               checkTranslationKeys("enchantment", IRegistry.field_212628_q, Enchantment::getName);
               checkTranslationKeys("item", IRegistry.field_212630_s, Item::getTranslationKey);
               checkTranslationKeys("effect", IRegistry.field_212631_t, Potion::getName);
               checkTranslationKeys("entity", IRegistry.field_212629_r, EntityType::getTranslationKey);
            }

            if (false) // skip redirectOutputToLog, Forge already redirects stdout and stderr output to log so that they print with more context
            redirectOutputToLog();
         }
      }
   }

   private static <T> void checkTranslationKeys(String type, IRegistry<T> registry, Function<T, String> getTranslationKey) {
      LanguageMap languagemap = LanguageMap.getInstance();
      registry.iterator().forEachRemaining((p_210840_4_) -> {
         String s = getTranslationKey.apply(p_210840_4_);
         if (!languagemap.exists(s)) {
            LOGGER.warn("Missing translation for {}: {} (key: '{}')", type, registry.getKey(p_210840_4_), s);
         }

      });
   }

   /**
    * redirect standard streams to logger
    */
   private static void redirectOutputToLog() {
      if (LOGGER.isDebugEnabled()) {
         System.setErr(new DebugLoggingPrintStream("STDERR", System.err));
         System.setOut(new DebugLoggingPrintStream("STDOUT", SYSOUT));
      } else {
         System.setErr(new LoggingPrintStream("STDERR", System.err));
         System.setOut(new LoggingPrintStream("STDOUT", SYSOUT));
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static void printToSYSOUT(String message) {
      SYSOUT.println(message);
   }

   public static class BehaviorDispenseBoat extends BehaviorDefaultDispenseItem {
      private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();
      private final EntityBoat.Type boatType;

      public BehaviorDispenseBoat(EntityBoat.Type boatTypeIn) {
         this.boatType = boatTypeIn;
      }

      /**
       * Dispense the specified stack, play the dispense sound and spawn particles.
       */
      public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
         EnumFacing enumfacing = source.getBlockState().get(BlockDispenser.FACING);
         World world = source.getWorld();
         double d0 = source.getX() + (double)((float)enumfacing.getXOffset() * 1.125F);
         double d1 = source.getY() + (double)((float)enumfacing.getYOffset() * 1.125F);
         double d2 = source.getZ() + (double)((float)enumfacing.getZOffset() * 1.125F);
         BlockPos blockpos = source.getBlockPos().offset(enumfacing);
         double d3;
         if (world.getFluidState(blockpos).isTagged(FluidTags.WATER)) {
            d3 = 1.0D;
         } else {
            if (!world.getBlockState(blockpos).isAir() || !world.getFluidState(blockpos.down()).isTagged(FluidTags.WATER)) {
               return this.dispenseBehavior.dispense(source, stack);
            }

            d3 = 0.0D;
         }

         EntityBoat entityboat = new EntityBoat(world, d0, d1 + d3, d2);
         entityboat.setBoatType(this.boatType);
         entityboat.rotationYaw = enumfacing.getHorizontalAngle();
         world.spawnEntity(entityboat);
         stack.shrink(1);
         return stack;
      }

      /**
       * Play the dispense sound from the specified block.
       */
      protected void playDispenseSound(IBlockSource source) {
         source.getWorld().playEvent(1000, source.getBlockPos(), 0);
      }
   }

   public abstract static class BehaviorDispenseOptional extends BehaviorDefaultDispenseItem {
      protected boolean successful = true;

      /**
       * Play the dispense sound from the specified block.
       */
      protected void playDispenseSound(IBlockSource source) {
         source.getWorld().playEvent(this.successful ? 1000 : 1001, source.getBlockPos(), 0);
      }
   }

   static class BehaviorDispenseShulkerBox extends Bootstrap.BehaviorDispenseOptional {
      private BehaviorDispenseShulkerBox() {
      }

      /**
       * Dispense the specified stack, play the dispense sound and spawn particles.
       */
      protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
         this.successful = false;
         Item item = stack.getItem();
         if (item instanceof ItemBlock) {
            EnumFacing enumfacing = source.getBlockState().get(BlockDispenser.FACING);
            BlockPos blockpos = source.getBlockPos().offset(enumfacing);
            EnumFacing enumfacing1 = source.getWorld().isAirBlock(blockpos.down()) ? enumfacing : EnumFacing.UP;
            this.successful = ((ItemBlock)item).tryPlace(new Bootstrap.DispensePlaceContext(source.getWorld(), blockpos, enumfacing, stack, enumfacing1)) == EnumActionResult.SUCCESS;
            if (this.successful) {
               stack.shrink(1);
            }
         }

         return stack;
      }
   }

   static class DispensePlaceContext extends BlockItemUseContext {
      private final EnumFacing field_196015_j;

      public DispensePlaceContext(World p_i47754_1_, BlockPos p_i47754_2_, EnumFacing p_i47754_3_, ItemStack p_i47754_4_, EnumFacing p_i47754_5_) {
         super(p_i47754_1_, (EntityPlayer)null, p_i47754_4_, p_i47754_2_, p_i47754_5_, 0.5F, 0.0F, 0.5F);
         this.field_196015_j = p_i47754_3_;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public boolean canPlace() {
         return this.world.getBlockState(this.pos).isReplaceable(this);
      }

      public boolean replacingClickedOnBlock() {
         return this.canPlace();
      }

      public EnumFacing getNearestLookingDirection() {
         return EnumFacing.DOWN;
      }

      public EnumFacing[] getNearestLookingDirections() {
         switch(this.field_196015_j) {
         case DOWN:
         default:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.UP};
         case UP:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
         case NORTH:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.UP, EnumFacing.SOUTH};
         case SOUTH:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.UP, EnumFacing.NORTH};
         case WEST:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST};
         case EAST:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.WEST};
         }
      }

      public EnumFacing getPlacementHorizontalFacing() {
         return this.field_196015_j.getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : this.field_196015_j;
      }

      public boolean isPlacerSneaking() {
         return false;
      }

      public float getPlacementYaw() {
         return (float)(this.field_196015_j.getHorizontalIndex() * 90);
      }
   }
}