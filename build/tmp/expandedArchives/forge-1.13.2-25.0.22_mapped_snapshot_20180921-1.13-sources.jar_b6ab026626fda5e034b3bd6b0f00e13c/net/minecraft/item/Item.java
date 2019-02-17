package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Item extends net.minecraftforge.registries.ForgeRegistryEntry<Item> implements IItemProvider, net.minecraftforge.common.extensions.IForgeItem {
   public static final Map<Block, Item> BLOCK_TO_ITEM = net.minecraftforge.registries.GameData.getBlockItemMap();
   private static final IItemPropertyGetter DAMAGED_GETTER = (p_210306_0_, p_210306_1_, p_210306_2_) -> {
      return p_210306_0_.isDamaged() ? 1.0F : 0.0F;
   };
   private static final IItemPropertyGetter DAMAGE_GETTER = (p_210307_0_, p_210307_1_, p_210307_2_) -> {
      return MathHelper.clamp((float)p_210307_0_.getDamage() / (float)p_210307_0_.getMaxDamage(), 0.0F, 1.0F);
   };
   private static final IItemPropertyGetter LEFTHANDED_GETTER = (p_210305_0_, p_210305_1_, p_210305_2_) -> {
      return p_210305_2_ != null && p_210305_2_.getPrimaryHand() != EnumHandSide.RIGHT ? 1.0F : 0.0F;
   };
   private static final IItemPropertyGetter COOLDOWN_GETTER = (p_210308_0_, p_210308_1_, p_210308_2_) -> {
      return p_210308_2_ instanceof EntityPlayer ? ((EntityPlayer)p_210308_2_).getCooldownTracker().getCooldown(p_210308_0_.getItem(), 0.0F) : 0.0F;
   };
   protected static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
   protected static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
   /** The RNG used by the Item subclasses. */
   protected static Random random = new Random();
   public final Map<ResourceLocation, IItemPropertyGetter> properties = Maps.newHashMap();
   protected final ItemGroup group;
   private final EnumRarity rarity;
   /** Maximum size of the stack. */
   private final int maxStackSize;
   /** Maximum damage an item can handle. */
   private final int maxDamage;
   private final Item containerItem;
   /** The unlocalized name of this item. */
   @Nullable
   private String translationKey;

   public static int getIdFromItem(Item itemIn) {
      return itemIn == null ? 0 : IRegistry.field_212630_s.getId(itemIn);
   }

   public static Item getItemById(int id) {
      return IRegistry.field_212630_s.get(id);
   }

   @Deprecated
   public static Item getItemFromBlock(Block blockIn) {
      Item item = BLOCK_TO_ITEM.get(blockIn);
      return item == null ? Items.AIR : item;
   }

   public Item(Item.Properties properties) {
      this.addPropertyOverride(new ResourceLocation("lefthanded"), LEFTHANDED_GETTER);
      this.addPropertyOverride(new ResourceLocation("cooldown"), COOLDOWN_GETTER);
      this.group = properties.group;
      this.rarity = properties.rarity;
      this.containerItem = properties.containerItem;
      this.maxDamage = properties.maxDamage;
      this.maxStackSize = properties.maxStackSize;
      this.canRepair = properties.canRepair;
      this.toolClasses.putAll(properties.toolClasses);
      Object tmp = properties.teisr == null ? null : net.minecraftforge.fml.DistExecutor.callWhenOn(Dist.CLIENT, properties.teisr);
      this.teisr = tmp == null ? null : () -> (net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer) tmp;
      if (this.maxDamage > 0) {
         this.addPropertyOverride(new ResourceLocation("damaged"), DAMAGED_GETTER);
         this.addPropertyOverride(new ResourceLocation("damage"), DAMAGE_GETTER);
      }

   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IItemPropertyGetter getPropertyGetter(ResourceLocation key) {
      return this.properties.get(key);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomProperties() {
      return !this.properties.isEmpty();
   }

   /**
    * Called when an ItemStack with NBT data is read to potentially that ItemStack's NBT data
    */
   public boolean updateItemStackNBT(NBTTagCompound nbt) {
      return false;
   }

   public boolean canPlayerBreakBlockWhileHolding(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) {
      return true;
   }

   public Item asItem() {
      return this;
   }

   /**
    * Creates a new override param for item models. See usage in clock, compass, elytra, etc.
    */
   public final void addPropertyOverride(ResourceLocation key, IItemPropertyGetter getter) {
      this.properties.put(key, getter);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      return EnumActionResult.PASS;
   }

   public float getDestroySpeed(ItemStack stack, IBlockState state) {
      return 1.0F;
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
   }

   /**
    * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
    * the Item before the action is complete.
    */
   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
      return stack;
   }

   /**
    * Returns the maximum size of the stack for a specific item.
    */
   @Deprecated // Use ItemStack sensitive version.
   public final int getMaxStackSize() {
      return this.maxStackSize;
   }

   /**
    * Returns the maximum damage an item can take.
    */
   @Deprecated // Use ItemStack sensitive version.
   public final int getMaxDamage() {
      return this.maxDamage;
   }

   public boolean isDamageable() {
      return this.maxDamage > 0;
   }

   /**
    * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
    * the damage on the stack.
    */
   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      return false;
   }

   /**
    * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
    */
   public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
      return false;
   }

   /**
    * Check whether this Item can harvest the given Block
    */
   public boolean canHarvestBlock(IBlockState blockIn) {
      return false;
   }

   /**
    * Returns true if the item can be used on the given entity, e.g. shears on sheep.
    */
   public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getName() {
      return new TextComponentTranslation(this.getTranslationKey());
   }

   protected String getDefaultTranslationKey() {
      if (this.translationKey == null) {
         this.translationKey = Util.makeTranslationKey("item", IRegistry.field_212630_s.getKey(this));
      }

      return this.translationKey;
   }

   /**
    * Returns the unlocalized name of this item.
    */
   public String getTranslationKey() {
      return this.getDefaultTranslationKey();
   }

   /**
    * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
    * different names based on their damage or NBT.
    */
   public String getTranslationKey(ItemStack stack) {
      return this.getTranslationKey();
   }

   /**
    * If this function returns true (or the item is damageable), the ItemStack's NBT tag will be sent to the client.
    */
   public boolean getShareTag() {
      return true;
   }

   @Nullable
   public final Item getContainerItem() {
      return this.containerItem;
   }

   /**
    * True if this Item has a container item (a.k.a. crafting result)
    */
   @Deprecated // Use ItemStack sensitive version.
   public boolean hasContainerItem() {
      return this.containerItem != null;
   }

   /**
    * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
    * update it's contents.
    */
   public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
   }

   /**
    * Called when item is crafted/smelted. Used only by maps so far.
    */
   public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
   }

   /**
    * Returns {@code} true if this is a complex item.
    */
   public boolean isComplex() {
      return false;
   }

   /**
    * returns the action that specifies what animation to play when the items is being used
    */
   public EnumAction getUseAction(ItemStack stack) {
      return EnumAction.NONE;
   }

   /**
    * How long it takes to use or consume an item
    */
   public int getUseDuration(ItemStack stack) {
      return 0;
   }

   /**
    * Called when the player stops using an Item (stops holding the right mouse button).
    */
   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
   }

   public ITextComponent getDisplayName(ItemStack stack) {
      return new TextComponentTranslation(this.getTranslationKey(stack));
   }

   /**
    * Returns true if this item has an enchantment glint. By default, this returns <code>stack.isItemEnchanted()</code>,
    * but other items can override it (for instance, written books always return true).
    *  
    * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
    * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return stack.isEnchanted();
   }

   /**
    * Return an item rarity from EnumRarity
    */
   public EnumRarity getRarity(ItemStack stack) {
      if (!stack.isEnchanted()) {
         return this.rarity;
      } else {
         switch(this.rarity) {
         case COMMON:
         case UNCOMMON:
            return EnumRarity.RARE;
         case RARE:
            return EnumRarity.EPIC;
         case EPIC:
         default:
            return this.rarity;
         }
      }
   }

   /**
    * Checks isDamagable and if it cannot be stacked
    */
   public boolean isEnchantable(ItemStack stack) {
      return this.getItemStackLimit(stack) == 1 && this.isDamageable();
   }

   @Nullable
   protected RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
      float f = playerIn.rotationPitch;
      float f1 = playerIn.rotationYaw;
      double d0 = playerIn.posX;
      double d1 = playerIn.posY + (double)playerIn.getEyeHeight();
      double d2 = playerIn.posZ;
      Vec3d vec3d = new Vec3d(d0, d1, d2);
      float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
      float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      double d3 = playerIn.getAttribute(EntityPlayer.REACH_DISTANCE).getValue();
      Vec3d vec3d1 = vec3d.add((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
      return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids ? RayTraceFluidMode.SOURCE_ONLY : RayTraceFluidMode.NEVER, false, false);
   }

   /**
    * Return the enchantability factor of the item, most of the time is based on material.
    */
   public int getItemEnchantability() {
      return 0;
   }

   /**
    * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
    */
   public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
      if (this.isInGroup(group)) {
         items.add(new ItemStack(this));
      }

   }

   protected boolean isInGroup(ItemGroup group) {
      if (getCreativeTabs().stream().anyMatch(tab -> tab == group)) return true;
      ItemGroup itemgroup = this.getGroup();
      return itemgroup != null && (group == ItemGroup.SEARCH || group == itemgroup);
   }

   /**
    * gets the CreativeTab this item is displayed on
    */
   @Nullable
   public final ItemGroup getGroup() {
      return this.group;
   }

   /**
    * Return whether this item is repairable in an anvil.
    */
   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return false;
   }

   /**
    * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
    */
   @Deprecated // Use ItemStack sensitive version.
   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
      return HashMultimap.create();
   }

   @Nullable
   private final java.util.function.Supplier<net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer> teisr;
   private final java.util.Map<net.minecraftforge.common.ToolType, Integer> toolClasses = Maps.newHashMap();
   protected final boolean canRepair;

   @Override
   public boolean isRepairable() {
     return canRepair && isDamageable();
   }

   @Override
   public java.util.Set<net.minecraftforge.common.ToolType> getToolTypes(ItemStack stack) {
     return toolClasses.keySet();
   }

   @Override
   public int getHarvestLevel(ItemStack stack, net.minecraftforge.common.ToolType tool, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
     return toolClasses.getOrDefault(tool, -1);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public final net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer getTileEntityItemStackRenderer() {
     net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer renderer = teisr != null ? teisr.get() : null;
     return renderer != null ? renderer : net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer.instance;
   }

   public static void registerItems() {
      register(Blocks.AIR, new ItemAir(Blocks.AIR, new Item.Properties()));
      register(Blocks.STONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRANITE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.POLISHED_GRANITE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DIORITE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.POLISHED_DIORITE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ANDESITE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.POLISHED_ANDESITE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRASS_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DIRT, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.COARSE_DIRT, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PODZOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.COBBLESTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OAK_PLANKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SPRUCE_PLANKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BIRCH_PLANKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.JUNGLE_PLANKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ACACIA_PLANKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_OAK_PLANKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OAK_SAPLING, ItemGroup.DECORATIONS);
      register(Blocks.SPRUCE_SAPLING, ItemGroup.DECORATIONS);
      register(Blocks.BIRCH_SAPLING, ItemGroup.DECORATIONS);
      register(Blocks.JUNGLE_SAPLING, ItemGroup.DECORATIONS);
      register(Blocks.ACACIA_SAPLING, ItemGroup.DECORATIONS);
      register(Blocks.DARK_OAK_SAPLING, ItemGroup.DECORATIONS);
      register(Blocks.BEDROCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SAND, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_SAND, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRAVEL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GOLD_ORE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.IRON_ORE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.COAL_ORE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OAK_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SPRUCE_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BIRCH_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.JUNGLE_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ACACIA_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_OAK_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_OAK_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_SPRUCE_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_BIRCH_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_JUNGLE_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_ACACIA_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_DARK_OAK_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_SPRUCE_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_BIRCH_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_JUNGLE_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_ACACIA_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_DARK_OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SPRUCE_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BIRCH_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.JUNGLE_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ACACIA_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OAK_LEAVES, ItemGroup.DECORATIONS);
      register(Blocks.SPRUCE_LEAVES, ItemGroup.DECORATIONS);
      register(Blocks.BIRCH_LEAVES, ItemGroup.DECORATIONS);
      register(Blocks.JUNGLE_LEAVES, ItemGroup.DECORATIONS);
      register(Blocks.ACACIA_LEAVES, ItemGroup.DECORATIONS);
      register(Blocks.DARK_OAK_LEAVES, ItemGroup.DECORATIONS);
      register(Blocks.SPONGE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.WET_SPONGE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LAPIS_ORE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LAPIS_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DISPENSER, ItemGroup.REDSTONE);
      register(Blocks.SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CHISELED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CUT_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.NOTE_BLOCK, ItemGroup.REDSTONE);
      register(Blocks.POWERED_RAIL, ItemGroup.TRANSPORTATION);
      register(Blocks.DETECTOR_RAIL, ItemGroup.TRANSPORTATION);
      register(Blocks.STICKY_PISTON, ItemGroup.REDSTONE);
      register(Blocks.COBWEB, ItemGroup.DECORATIONS);
      register(Blocks.GRASS, ItemGroup.DECORATIONS);
      register(Blocks.FERN, ItemGroup.DECORATIONS);
      register(Blocks.DEAD_BUSH, ItemGroup.DECORATIONS);
      register(Blocks.SEAGRASS, ItemGroup.DECORATIONS);
      register(Blocks.SEA_PICKLE, ItemGroup.DECORATIONS);
      register(Blocks.PISTON, ItemGroup.REDSTONE);
      register(Blocks.WHITE_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ORANGE_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MAGENTA_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_BLUE_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.YELLOW_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIME_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PINK_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRAY_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_GRAY_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CYAN_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPLE_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLUE_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BROWN_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GREEN_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLACK_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DANDELION, ItemGroup.DECORATIONS);
      register(Blocks.POPPY, ItemGroup.DECORATIONS);
      register(Blocks.BLUE_ORCHID, ItemGroup.DECORATIONS);
      register(Blocks.ALLIUM, ItemGroup.DECORATIONS);
      register(Blocks.AZURE_BLUET, ItemGroup.DECORATIONS);
      register(Blocks.RED_TULIP, ItemGroup.DECORATIONS);
      register(Blocks.ORANGE_TULIP, ItemGroup.DECORATIONS);
      register(Blocks.WHITE_TULIP, ItemGroup.DECORATIONS);
      register(Blocks.PINK_TULIP, ItemGroup.DECORATIONS);
      register(Blocks.OXEYE_DAISY, ItemGroup.DECORATIONS);
      register(Blocks.BROWN_MUSHROOM, ItemGroup.DECORATIONS);
      register(Blocks.RED_MUSHROOM, ItemGroup.DECORATIONS);
      register(Blocks.GOLD_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.IRON_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OAK_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SPRUCE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BIRCH_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.JUNGLE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ACACIA_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_OAK_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STONE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SANDSTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PETRIFIED_OAK_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.COBBLESTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STONE_BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.NETHER_BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.QUARTZ_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_SANDSTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPUR_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PRISMARINE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PRISMARINE_BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_PRISMARINE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SMOOTH_QUARTZ, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SMOOTH_RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SMOOTH_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SMOOTH_STONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.TNT, ItemGroup.REDSTONE);
      register(Blocks.BOOKSHELF, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MOSSY_COBBLESTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OBSIDIAN, ItemGroup.BUILDING_BLOCKS);
      register(new ItemWallOrFloor(Blocks.TORCH, Blocks.WALL_TORCH, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(Blocks.END_ROD, ItemGroup.DECORATIONS);
      register(Blocks.CHORUS_PLANT, ItemGroup.DECORATIONS);
      register(Blocks.CHORUS_FLOWER, ItemGroup.DECORATIONS);
      register(Blocks.PURPUR_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPUR_PILLAR, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPUR_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SPAWNER);
      register(Blocks.OAK_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CHEST, ItemGroup.DECORATIONS);
      register(Blocks.DIAMOND_ORE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DIAMOND_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CRAFTING_TABLE, ItemGroup.DECORATIONS);
      register(Blocks.FARMLAND, ItemGroup.DECORATIONS);
      register(Blocks.FURNACE, ItemGroup.DECORATIONS);
      register(Blocks.LADDER, ItemGroup.DECORATIONS);
      register(Blocks.RAIL, ItemGroup.TRANSPORTATION);
      register(Blocks.COBBLESTONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LEVER, ItemGroup.REDSTONE);
      register(Blocks.STONE_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.OAK_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.SPRUCE_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.BIRCH_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.JUNGLE_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.ACACIA_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.DARK_OAK_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.REDSTONE_ORE, ItemGroup.BUILDING_BLOCKS);
      register(new ItemWallOrFloor(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, (new Item.Properties()).group(ItemGroup.REDSTONE)));
      register(Blocks.STONE_BUTTON, ItemGroup.REDSTONE);
      register(Blocks.SNOW, ItemGroup.DECORATIONS);
      register(Blocks.ICE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SNOW_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CACTUS, ItemGroup.DECORATIONS);
      register(Blocks.CLAY, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.JUKEBOX, ItemGroup.DECORATIONS);
      register(Blocks.OAK_FENCE, ItemGroup.DECORATIONS);
      register(Blocks.SPRUCE_FENCE, ItemGroup.DECORATIONS);
      register(Blocks.BIRCH_FENCE, ItemGroup.DECORATIONS);
      register(Blocks.JUNGLE_FENCE, ItemGroup.DECORATIONS);
      register(Blocks.ACACIA_FENCE, ItemGroup.DECORATIONS);
      register(Blocks.DARK_OAK_FENCE, ItemGroup.DECORATIONS);
      register(Blocks.PUMPKIN, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CARVED_PUMPKIN, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.NETHERRACK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SOUL_SAND, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GLOWSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.JACK_O_LANTERN, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OAK_TRAPDOOR, ItemGroup.REDSTONE);
      register(Blocks.SPRUCE_TRAPDOOR, ItemGroup.REDSTONE);
      register(Blocks.BIRCH_TRAPDOOR, ItemGroup.REDSTONE);
      register(Blocks.JUNGLE_TRAPDOOR, ItemGroup.REDSTONE);
      register(Blocks.ACACIA_TRAPDOOR, ItemGroup.REDSTONE);
      register(Blocks.DARK_OAK_TRAPDOOR, ItemGroup.REDSTONE);
      register(Blocks.INFESTED_STONE, ItemGroup.DECORATIONS);
      register(Blocks.INFESTED_COBBLESTONE, ItemGroup.DECORATIONS);
      register(Blocks.INFESTED_STONE_BRICKS, ItemGroup.DECORATIONS);
      register(Blocks.INFESTED_MOSSY_STONE_BRICKS, ItemGroup.DECORATIONS);
      register(Blocks.INFESTED_CRACKED_STONE_BRICKS, ItemGroup.DECORATIONS);
      register(Blocks.INFESTED_CHISELED_STONE_BRICKS, ItemGroup.DECORATIONS);
      register(Blocks.STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MOSSY_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CRACKED_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CHISELED_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BROWN_MUSHROOM_BLOCK, ItemGroup.DECORATIONS);
      register(Blocks.RED_MUSHROOM_BLOCK, ItemGroup.DECORATIONS);
      register(Blocks.MUSHROOM_STEM, ItemGroup.DECORATIONS);
      register(Blocks.IRON_BARS, ItemGroup.DECORATIONS);
      register(Blocks.GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.MELON, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.VINE, ItemGroup.DECORATIONS);
      register(Blocks.OAK_FENCE_GATE, ItemGroup.REDSTONE);
      register(Blocks.SPRUCE_FENCE_GATE, ItemGroup.REDSTONE);
      register(Blocks.BIRCH_FENCE_GATE, ItemGroup.REDSTONE);
      register(Blocks.JUNGLE_FENCE_GATE, ItemGroup.REDSTONE);
      register(Blocks.ACACIA_FENCE_GATE, ItemGroup.REDSTONE);
      register(Blocks.DARK_OAK_FENCE_GATE, ItemGroup.REDSTONE);
      register(Blocks.BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STONE_BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MYCELIUM, ItemGroup.BUILDING_BLOCKS);
      register(new ItemLilyPad(Blocks.LILY_PAD, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(Blocks.NETHER_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.NETHER_BRICK_FENCE, ItemGroup.DECORATIONS);
      register(Blocks.NETHER_BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ENCHANTING_TABLE, ItemGroup.DECORATIONS);
      register(Blocks.END_PORTAL_FRAME, ItemGroup.DECORATIONS);
      register(Blocks.END_STONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.END_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(new ItemBlock(Blocks.DRAGON_EGG, (new Item.Properties()).rarity(EnumRarity.EPIC)));
      register(Blocks.REDSTONE_LAMP, ItemGroup.REDSTONE);
      register(Blocks.SANDSTONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.EMERALD_ORE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ENDER_CHEST, ItemGroup.DECORATIONS);
      register(Blocks.TRIPWIRE_HOOK, ItemGroup.REDSTONE);
      register(Blocks.EMERALD_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SPRUCE_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BIRCH_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.JUNGLE_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(new ItemGMOnly(Blocks.COMMAND_BLOCK, (new Item.Properties()).rarity(EnumRarity.EPIC)));
      register(new ItemBlock(Blocks.BEACON, (new Item.Properties()).group(ItemGroup.MISC).rarity(EnumRarity.RARE)));
      register(Blocks.COBBLESTONE_WALL, ItemGroup.DECORATIONS);
      register(Blocks.MOSSY_COBBLESTONE_WALL, ItemGroup.DECORATIONS);
      register(Blocks.OAK_BUTTON, ItemGroup.REDSTONE);
      register(Blocks.SPRUCE_BUTTON, ItemGroup.REDSTONE);
      register(Blocks.BIRCH_BUTTON, ItemGroup.REDSTONE);
      register(Blocks.JUNGLE_BUTTON, ItemGroup.REDSTONE);
      register(Blocks.ACACIA_BUTTON, ItemGroup.REDSTONE);
      register(Blocks.DARK_OAK_BUTTON, ItemGroup.REDSTONE);
      register(Blocks.ANVIL, ItemGroup.DECORATIONS);
      register(Blocks.CHIPPED_ANVIL, ItemGroup.DECORATIONS);
      register(Blocks.DAMAGED_ANVIL, ItemGroup.DECORATIONS);
      register(Blocks.TRAPPED_CHEST, ItemGroup.REDSTONE);
      register(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.DAYLIGHT_DETECTOR, ItemGroup.REDSTONE);
      register(Blocks.REDSTONE_BLOCK, ItemGroup.REDSTONE);
      register(Blocks.NETHER_QUARTZ_ORE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.HOPPER, ItemGroup.REDSTONE);
      register(Blocks.CHISELED_QUARTZ_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.QUARTZ_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.QUARTZ_PILLAR, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.QUARTZ_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ACTIVATOR_RAIL, ItemGroup.TRANSPORTATION);
      register(Blocks.DROPPER, ItemGroup.REDSTONE);
      register(Blocks.WHITE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ORANGE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MAGENTA_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_BLUE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.YELLOW_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIME_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PINK_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRAY_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_GRAY_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CYAN_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPLE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLUE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BROWN_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GREEN_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLACK_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BARRIER);
      register(Blocks.IRON_TRAPDOOR, ItemGroup.REDSTONE);
      register(Blocks.HAY_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.WHITE_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.ORANGE_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.MAGENTA_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.LIGHT_BLUE_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.YELLOW_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.LIME_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.PINK_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.GRAY_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.LIGHT_GRAY_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.CYAN_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.PURPLE_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.BLUE_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.BROWN_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.GREEN_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.RED_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.BLACK_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.COAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PACKED_ICE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ACACIA_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_OAK_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SLIME_BLOCK, ItemGroup.DECORATIONS);
      register(Blocks.GRASS_PATH, ItemGroup.DECORATIONS);
      register(new ItemBlockTall(Blocks.SUNFLOWER, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(new ItemBlockTall(Blocks.LILAC, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(new ItemBlockTall(Blocks.ROSE_BUSH, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(new ItemBlockTall(Blocks.PEONY, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(new ItemBlockTall(Blocks.TALL_GRASS, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(new ItemBlockTall(Blocks.LARGE_FERN, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(Blocks.WHITE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ORANGE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MAGENTA_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_BLUE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.YELLOW_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIME_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PINK_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRAY_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_GRAY_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CYAN_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPLE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLUE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BROWN_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GREEN_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLACK_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.WHITE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.ORANGE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.MAGENTA_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.YELLOW_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.LIME_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.PINK_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.GRAY_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.CYAN_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.PURPLE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.BLUE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.BROWN_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.GREEN_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.RED_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.BLACK_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.PRISMARINE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PRISMARINE_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_PRISMARINE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PRISMARINE_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PRISMARINE_BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_PRISMARINE_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SEA_LANTERN, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CHISELED_RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CUT_RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_SANDSTONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(new ItemGMOnly(Blocks.REPEATING_COMMAND_BLOCK, (new Item.Properties()).rarity(EnumRarity.EPIC)));
      register(new ItemGMOnly(Blocks.CHAIN_COMMAND_BLOCK, (new Item.Properties()).rarity(EnumRarity.EPIC)));
      register(Blocks.MAGMA_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.NETHER_WART_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_NETHER_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BONE_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRUCTURE_VOID);
      register(Blocks.OBSERVER, ItemGroup.REDSTONE);
      register(new ItemBlock(Blocks.SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.WHITE_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.ORANGE_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.MAGENTA_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.LIGHT_BLUE_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.YELLOW_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.LIME_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.PINK_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.GRAY_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.LIGHT_GRAY_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.CYAN_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.PURPLE_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.BLUE_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.BROWN_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.GREEN_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.RED_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.BLACK_SHULKER_BOX, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(Blocks.WHITE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.ORANGE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.MAGENTA_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.YELLOW_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.LIME_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.PINK_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.GRAY_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.CYAN_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.PURPLE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.BLUE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.BROWN_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.GREEN_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.RED_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.BLACK_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.WHITE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ORANGE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MAGENTA_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_BLUE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.YELLOW_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIME_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PINK_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRAY_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_GRAY_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CYAN_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPLE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLUE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BROWN_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GREEN_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLACK_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.WHITE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ORANGE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MAGENTA_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_BLUE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.YELLOW_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIME_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PINK_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRAY_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_GRAY_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CYAN_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPLE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLUE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BROWN_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GREEN_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLACK_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.TURTLE_EGG, ItemGroup.MISC);
      register(Blocks.DEAD_TUBE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DEAD_BRAIN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DEAD_BUBBLE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DEAD_FIRE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DEAD_HORN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.TUBE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BRAIN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BUBBLE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.FIRE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.HORN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.TUBE_CORAL, ItemGroup.DECORATIONS);
      register(Blocks.BRAIN_CORAL, ItemGroup.DECORATIONS);
      register(Blocks.BUBBLE_CORAL, ItemGroup.DECORATIONS);
      register(Blocks.FIRE_CORAL, ItemGroup.DECORATIONS);
      register(Blocks.HORN_CORAL, ItemGroup.DECORATIONS);
      register(Blocks.field_212586_jZ, ItemGroup.DECORATIONS);
      register(Blocks.field_212587_ka, ItemGroup.DECORATIONS);
      register(Blocks.field_212588_kb, ItemGroup.DECORATIONS);
      register(Blocks.field_212589_kc, ItemGroup.DECORATIONS);
      register(Blocks.field_212585_jY, ItemGroup.DECORATIONS);
      register(new ItemWallOrFloor(Blocks.TUBE_CORAL_FAN, Blocks.TUBE_CORAL_WALL_FAN, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.FIRE_CORAL_FAN, Blocks.FIRE_CORAL_WALL_FAN, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.HORN_CORAL_FAN, Blocks.HORN_CORAL_WALL_FAN, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.DEAD_TUBE_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_WALL_FAN, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_WALL_FAN, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.DEAD_FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_WALL_FAN, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.DEAD_HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_WALL_FAN, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(Blocks.BLUE_ICE, ItemGroup.BUILDING_BLOCKS);
      register(new ItemBlock(Blocks.CONDUIT, (new Item.Properties()).group(ItemGroup.MISC).rarity(EnumRarity.RARE)));
      register(new ItemBlockTall(Blocks.IRON_DOOR, (new Item.Properties()).group(ItemGroup.REDSTONE)));
      register(new ItemBlockTall(Blocks.OAK_DOOR, (new Item.Properties()).group(ItemGroup.REDSTONE)));
      register(new ItemBlockTall(Blocks.SPRUCE_DOOR, (new Item.Properties()).group(ItemGroup.REDSTONE)));
      register(new ItemBlockTall(Blocks.BIRCH_DOOR, (new Item.Properties()).group(ItemGroup.REDSTONE)));
      register(new ItemBlockTall(Blocks.JUNGLE_DOOR, (new Item.Properties()).group(ItemGroup.REDSTONE)));
      register(new ItemBlockTall(Blocks.ACACIA_DOOR, (new Item.Properties()).group(ItemGroup.REDSTONE)));
      register(new ItemBlockTall(Blocks.DARK_OAK_DOOR, (new Item.Properties()).group(ItemGroup.REDSTONE)));
      register(Blocks.REPEATER, ItemGroup.REDSTONE);
      register(Blocks.COMPARATOR, ItemGroup.REDSTONE);
      register(new ItemGMOnly(Blocks.STRUCTURE_BLOCK, (new Item.Properties()).rarity(EnumRarity.EPIC)));
      register("turtle_helmet", new ItemArmor(ArmorMaterial.TURTLE, EntityEquipmentSlot.HEAD, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("scute", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("iron_shovel", new ItemSpade(ItemTier.IRON, 1.5F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("iron_pickaxe", new ItemPickaxe(ItemTier.IRON, 1, -2.8F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("iron_axe", new ItemAxe(ItemTier.IRON, 6.0F, -3.1F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("flint_and_steel", new ItemFlintAndSteel((new Item.Properties()).maxDamage(64).group(ItemGroup.TOOLS)));
      register("apple", new ItemFood(4, 0.3F, false, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("bow", new ItemBow((new Item.Properties()).maxDamage(384).group(ItemGroup.COMBAT)));
      register("arrow", new ItemArrow((new Item.Properties()).group(ItemGroup.COMBAT)));
      register("coal", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("charcoal", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("diamond", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("iron_ingot", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("gold_ingot", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("iron_sword", new ItemSword(ItemTier.IRON, 3, -2.4F, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("wooden_sword", new ItemSword(ItemTier.WOOD, 3, -2.4F, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("wooden_shovel", new ItemSpade(ItemTier.WOOD, 1.5F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("wooden_pickaxe", new ItemPickaxe(ItemTier.WOOD, 1, -2.8F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("wooden_axe", new ItemAxe(ItemTier.WOOD, 6.0F, -3.2F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("stone_sword", new ItemSword(ItemTier.STONE, 3, -2.4F, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("stone_shovel", new ItemSpade(ItemTier.STONE, 1.5F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("stone_pickaxe", new ItemPickaxe(ItemTier.STONE, 1, -2.8F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("stone_axe", new ItemAxe(ItemTier.STONE, 7.0F, -3.2F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("diamond_sword", new ItemSword(ItemTier.DIAMOND, 3, -2.4F, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("diamond_shovel", new ItemSpade(ItemTier.DIAMOND, 1.5F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("diamond_pickaxe", new ItemPickaxe(ItemTier.DIAMOND, 1, -2.8F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("diamond_axe", new ItemAxe(ItemTier.DIAMOND, 5.0F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("stick", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("bowl", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("mushroom_stew", new ItemSoup(6, (new Item.Properties()).maxStackSize(1).group(ItemGroup.FOOD)));
      register("golden_sword", new ItemSword(ItemTier.GOLD, 3, -2.4F, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("golden_shovel", new ItemSpade(ItemTier.GOLD, 1.5F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("golden_pickaxe", new ItemPickaxe(ItemTier.GOLD, 1, -2.8F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("golden_axe", new ItemAxe(ItemTier.GOLD, 6.0F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("string", new ItemString((new Item.Properties()).group(ItemGroup.MISC)));
      register("feather", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("gunpowder", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("wooden_hoe", new ItemHoe(ItemTier.WOOD, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("stone_hoe", new ItemHoe(ItemTier.STONE, -2.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("iron_hoe", new ItemHoe(ItemTier.IRON, -1.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("diamond_hoe", new ItemHoe(ItemTier.DIAMOND, 0.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("golden_hoe", new ItemHoe(ItemTier.GOLD, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
      register("wheat_seeds", new ItemSeeds(Blocks.WHEAT, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("wheat", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("bread", new ItemFood(5, 0.6F, false, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("leather_helmet", new ItemArmorDyeable(ArmorMaterial.LEATHER, EntityEquipmentSlot.HEAD, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("leather_chestplate", new ItemArmorDyeable(ArmorMaterial.LEATHER, EntityEquipmentSlot.CHEST, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("leather_leggings", new ItemArmorDyeable(ArmorMaterial.LEATHER, EntityEquipmentSlot.LEGS, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("leather_boots", new ItemArmorDyeable(ArmorMaterial.LEATHER, EntityEquipmentSlot.FEET, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("chainmail_helmet", new ItemArmor(ArmorMaterial.CHAIN, EntityEquipmentSlot.HEAD, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("chainmail_chestplate", new ItemArmor(ArmorMaterial.CHAIN, EntityEquipmentSlot.CHEST, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("chainmail_leggings", new ItemArmor(ArmorMaterial.CHAIN, EntityEquipmentSlot.LEGS, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("chainmail_boots", new ItemArmor(ArmorMaterial.CHAIN, EntityEquipmentSlot.FEET, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("iron_helmet", new ItemArmor(ArmorMaterial.IRON, EntityEquipmentSlot.HEAD, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("iron_chestplate", new ItemArmor(ArmorMaterial.IRON, EntityEquipmentSlot.CHEST, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("iron_leggings", new ItemArmor(ArmorMaterial.IRON, EntityEquipmentSlot.LEGS, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("iron_boots", new ItemArmor(ArmorMaterial.IRON, EntityEquipmentSlot.FEET, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("diamond_helmet", new ItemArmor(ArmorMaterial.DIAMOND, EntityEquipmentSlot.HEAD, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("diamond_chestplate", new ItemArmor(ArmorMaterial.DIAMOND, EntityEquipmentSlot.CHEST, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("diamond_leggings", new ItemArmor(ArmorMaterial.DIAMOND, EntityEquipmentSlot.LEGS, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("diamond_boots", new ItemArmor(ArmorMaterial.DIAMOND, EntityEquipmentSlot.FEET, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("golden_helmet", new ItemArmor(ArmorMaterial.GOLD, EntityEquipmentSlot.HEAD, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("golden_chestplate", new ItemArmor(ArmorMaterial.GOLD, EntityEquipmentSlot.CHEST, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("golden_leggings", new ItemArmor(ArmorMaterial.GOLD, EntityEquipmentSlot.LEGS, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("golden_boots", new ItemArmor(ArmorMaterial.GOLD, EntityEquipmentSlot.FEET, (new Item.Properties()).group(ItemGroup.COMBAT)));
      register("flint", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("porkchop", new ItemFood(3, 0.3F, true, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("cooked_porkchop", new ItemFood(8, 0.8F, true, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("painting", new ItemHangingEntity(EntityPainting.class, (new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register("golden_apple", (new ItemAppleGold(4, 1.2F, false, (new Item.Properties()).group(ItemGroup.FOOD).rarity(EnumRarity.RARE))).setAlwaysEdible());
      register("enchanted_golden_apple", (new ItemAppleGoldEnchanted(4, 1.2F, false, (new Item.Properties()).group(ItemGroup.FOOD).rarity(EnumRarity.EPIC))).setAlwaysEdible());
      register("sign", new ItemSign((new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      Item item = new ItemBucket(Fluids.EMPTY, (new Item.Properties()).maxStackSize(16).group(ItemGroup.MISC));
      register("bucket", item);
      register("water_bucket", new ItemBucket(Fluids.WATER, (new Item.Properties()).containerItem(item).maxStackSize(1).group(ItemGroup.MISC)));
      register("lava_bucket", new ItemBucket(Fluids.LAVA, (new Item.Properties()).containerItem(item).maxStackSize(1).group(ItemGroup.MISC)));
      register("minecart", new ItemMinecart(EntityMinecart.Type.RIDEABLE, (new Item.Properties()).maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
      register("saddle", new ItemSaddle((new Item.Properties()).maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
      register("redstone", new ItemBlock(Blocks.REDSTONE_WIRE, (new Item.Properties()).group(ItemGroup.REDSTONE)));
      register("snowball", new ItemSnowball((new Item.Properties()).maxStackSize(16).group(ItemGroup.MISC)));
      register("oak_boat", new ItemBoat(EntityBoat.Type.OAK, (new Item.Properties()).maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
      register("leather", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("milk_bucket", new ItemBucketMilk((new Item.Properties()).containerItem(item).maxStackSize(1).group(ItemGroup.MISC)));
      register("pufferfish_bucket", new ItemBucketFish(EntityType.PUFFERFISH, Fluids.WATER, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC)));
      register("salmon_bucket", new ItemBucketFish(EntityType.SALMON, Fluids.WATER, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC)));
      register("cod_bucket", new ItemBucketFish(EntityType.COD, Fluids.WATER, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC)));
      register("tropical_fish_bucket", new ItemBucketFish(EntityType.TROPICAL_FISH, Fluids.WATER, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC)));
      register("brick", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("clay_ball", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register(Blocks.SUGAR_CANE, ItemGroup.MISC);
      register(Blocks.KELP, ItemGroup.MISC);
      register(Blocks.DRIED_KELP_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register("paper", new Item((new Item.Properties()).group(ItemGroup.MISC)));
      register("book", new ItemBook((new Item.Properties()).group(ItemGroup.MISC)));
      register("slime_ball", new Item((new Item.Properties()).group(ItemGroup.MISC)));
      register("chest_minecart", new ItemMinecart(EntityMinecart.Type.CHEST, (new Item.Properties()).maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
      register("furnace_minecart", new ItemMinecart(EntityMinecart.Type.FURNACE, (new Item.Properties()).maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
      register("egg", new ItemEgg((new Item.Properties()).maxStackSize(16).group(ItemGroup.MATERIALS)));
      register("compass", new ItemCompass((new Item.Properties()).group(ItemGroup.TOOLS)));
      register("fishing_rod", new ItemFishingRod((new Item.Properties()).maxDamage(64).group(ItemGroup.TOOLS)));
      register("clock", new ItemClock((new Item.Properties()).group(ItemGroup.TOOLS)));
      register("glowstone_dust", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("cod", new ItemFishFood(ItemFishFood.FishType.COD, false, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("salmon", new ItemFishFood(ItemFishFood.FishType.SALMON, false, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("tropical_fish", new ItemFishFood(ItemFishFood.FishType.TROPICAL_FISH, false, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("pufferfish", new ItemFishFood(ItemFishFood.FishType.PUFFERFISH, false, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("cooked_cod", new ItemFishFood(ItemFishFood.FishType.COD, true, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("cooked_salmon", new ItemFishFood(ItemFishFood.FishType.SALMON, true, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("ink_sac", new ItemDye(EnumDyeColor.BLACK, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("rose_red", new ItemDye(EnumDyeColor.RED, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("cactus_green", new ItemDye(EnumDyeColor.GREEN, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("cocoa_beans", new ItemCocoa(EnumDyeColor.BROWN, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("lapis_lazuli", new ItemDye(EnumDyeColor.BLUE, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("purple_dye", new ItemDye(EnumDyeColor.PURPLE, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("cyan_dye", new ItemDye(EnumDyeColor.CYAN, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("light_gray_dye", new ItemDye(EnumDyeColor.LIGHT_GRAY, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("gray_dye", new ItemDye(EnumDyeColor.GRAY, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("pink_dye", new ItemDye(EnumDyeColor.PINK, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("lime_dye", new ItemDye(EnumDyeColor.LIME, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("dandelion_yellow", new ItemDye(EnumDyeColor.YELLOW, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("light_blue_dye", new ItemDye(EnumDyeColor.LIGHT_BLUE, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("magenta_dye", new ItemDye(EnumDyeColor.MAGENTA, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("orange_dye", new ItemDye(EnumDyeColor.ORANGE, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("bone_meal", new ItemBoneMeal(EnumDyeColor.WHITE, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("bone", new Item((new Item.Properties()).group(ItemGroup.MISC)));
      register("sugar", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register(new ItemBlock(Blocks.CAKE, (new Item.Properties()).maxStackSize(1).group(ItemGroup.FOOD)));
      register(new ItemBed(Blocks.WHITE_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.ORANGE_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.MAGENTA_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.LIGHT_BLUE_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.YELLOW_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.LIME_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.PINK_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.GRAY_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.LIGHT_GRAY_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.CYAN_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.PURPLE_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.BLUE_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.BROWN_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.GREEN_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.RED_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.BLACK_BED, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)));
      register("cookie", new ItemFood(2, 0.1F, false, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("filled_map", new ItemMap(new Item.Properties()));
      register("shears", new ItemShears((new Item.Properties()).maxDamage(238).group(ItemGroup.TOOLS)));
      register("melon_slice", new ItemFood(2, 0.3F, false, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("dried_kelp", (new ItemFood(1, 0.3F, false, (new Item.Properties()).group(ItemGroup.FOOD))).setFastEating());
      register("pumpkin_seeds", new ItemSeeds(Blocks.PUMPKIN_STEM, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("melon_seeds", new ItemSeeds(Blocks.MELON_STEM, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("beef", new ItemFood(3, 0.3F, true, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("cooked_beef", new ItemFood(8, 0.8F, true, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("chicken", (new ItemFood(2, 0.3F, true, (new Item.Properties()).group(ItemGroup.FOOD))).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.3F));
      register("cooked_chicken", new ItemFood(6, 0.6F, true, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("rotten_flesh", (new ItemFood(4, 0.1F, true, (new Item.Properties()).group(ItemGroup.FOOD))).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.8F));
      register("ender_pearl", new ItemEnderPearl((new Item.Properties()).maxStackSize(16).group(ItemGroup.MISC)));
      register("blaze_rod", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("ghast_tear", new Item((new Item.Properties()).group(ItemGroup.BREWING)));
      register("gold_nugget", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("nether_wart", new ItemSeeds(Blocks.NETHER_WART, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("potion", new ItemPotion((new Item.Properties()).maxStackSize(1).group(ItemGroup.BREWING)));
      Item item1 = new ItemGlassBottle((new Item.Properties()).group(ItemGroup.BREWING));
      register("glass_bottle", item1);
      register("spider_eye", (new ItemFood(2, 0.8F, false, (new Item.Properties()).group(ItemGroup.FOOD))).setPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0), 1.0F));
      register("fermented_spider_eye", new Item((new Item.Properties()).group(ItemGroup.BREWING)));
      register("blaze_powder", new Item((new Item.Properties()).group(ItemGroup.BREWING)));
      register("magma_cream", new Item((new Item.Properties()).group(ItemGroup.BREWING)));
      register(Blocks.BREWING_STAND, ItemGroup.BREWING);
      register(Blocks.CAULDRON, ItemGroup.BREWING);
      register("ender_eye", new ItemEnderEye((new Item.Properties()).group(ItemGroup.MISC)));
      register("glistering_melon_slice", new Item((new Item.Properties()).group(ItemGroup.BREWING)));
      register("bat_spawn_egg", new ItemSpawnEgg(EntityType.BAT, 4996656, 986895, (new Item.Properties()).group(ItemGroup.MISC)));
      register("blaze_spawn_egg", new ItemSpawnEgg(EntityType.BLAZE, 16167425, 16775294, (new Item.Properties()).group(ItemGroup.MISC)));
      register("cave_spider_spawn_egg", new ItemSpawnEgg(EntityType.CAVE_SPIDER, 803406, 11013646, (new Item.Properties()).group(ItemGroup.MISC)));
      register("chicken_spawn_egg", new ItemSpawnEgg(EntityType.CHICKEN, 10592673, 16711680, (new Item.Properties()).group(ItemGroup.MISC)));
      register("cod_spawn_egg", new ItemSpawnEgg(EntityType.COD, 12691306, 15058059, (new Item.Properties()).group(ItemGroup.MISC)));
      register("cow_spawn_egg", new ItemSpawnEgg(EntityType.COW, 4470310, 10592673, (new Item.Properties()).group(ItemGroup.MISC)));
      register("creeper_spawn_egg", new ItemSpawnEgg(EntityType.CREEPER, 894731, 0, (new Item.Properties()).group(ItemGroup.MISC)));
      register("dolphin_spawn_egg", new ItemSpawnEgg(EntityType.DOLPHIN, 2243405, 16382457, (new Item.Properties()).group(ItemGroup.MISC)));
      register("donkey_spawn_egg", new ItemSpawnEgg(EntityType.DONKEY, 5457209, 8811878, (new Item.Properties()).group(ItemGroup.MISC)));
      register("drowned_spawn_egg", new ItemSpawnEgg(EntityType.DROWNED, 9433559, 7969893, (new Item.Properties()).group(ItemGroup.MISC)));
      register("elder_guardian_spawn_egg", new ItemSpawnEgg(EntityType.ELDER_GUARDIAN, 13552826, 7632531, (new Item.Properties()).group(ItemGroup.MISC)));
      register("enderman_spawn_egg", new ItemSpawnEgg(EntityType.ENDERMAN, 1447446, 0, (new Item.Properties()).group(ItemGroup.MISC)));
      register("endermite_spawn_egg", new ItemSpawnEgg(EntityType.ENDERMITE, 1447446, 7237230, (new Item.Properties()).group(ItemGroup.MISC)));
      register("evoker_spawn_egg", new ItemSpawnEgg(EntityType.EVOKER, 9804699, 1973274, (new Item.Properties()).group(ItemGroup.MISC)));
      register("ghast_spawn_egg", new ItemSpawnEgg(EntityType.GHAST, 16382457, 12369084, (new Item.Properties()).group(ItemGroup.MISC)));
      register("guardian_spawn_egg", new ItemSpawnEgg(EntityType.GUARDIAN, 5931634, 15826224, (new Item.Properties()).group(ItemGroup.MISC)));
      register("horse_spawn_egg", new ItemSpawnEgg(EntityType.HORSE, 12623485, 15656192, (new Item.Properties()).group(ItemGroup.MISC)));
      register("husk_spawn_egg", new ItemSpawnEgg(EntityType.HUSK, 7958625, 15125652, (new Item.Properties()).group(ItemGroup.MISC)));
      register("llama_spawn_egg", new ItemSpawnEgg(EntityType.LLAMA, 12623485, 10051392, (new Item.Properties()).group(ItemGroup.MISC)));
      register("magma_cube_spawn_egg", new ItemSpawnEgg(EntityType.MAGMA_CUBE, 3407872, 16579584, (new Item.Properties()).group(ItemGroup.MISC)));
      register("mooshroom_spawn_egg", new ItemSpawnEgg(EntityType.MOOSHROOM, 10489616, 12040119, (new Item.Properties()).group(ItemGroup.MISC)));
      register("mule_spawn_egg", new ItemSpawnEgg(EntityType.MULE, 1769984, 5321501, (new Item.Properties()).group(ItemGroup.MISC)));
      register("ocelot_spawn_egg", new ItemSpawnEgg(EntityType.OCELOT, 15720061, 5653556, (new Item.Properties()).group(ItemGroup.MISC)));
      register("parrot_spawn_egg", new ItemSpawnEgg(EntityType.PARROT, 894731, 16711680, (new Item.Properties()).group(ItemGroup.MISC)));
      register("phantom_spawn_egg", new ItemSpawnEgg(EntityType.PHANTOM, 4411786, 8978176, (new Item.Properties()).group(ItemGroup.MISC)));
      register("pig_spawn_egg", new ItemSpawnEgg(EntityType.PIG, 15771042, 14377823, (new Item.Properties()).group(ItemGroup.MISC)));
      register("polar_bear_spawn_egg", new ItemSpawnEgg(EntityType.POLAR_BEAR, 15921906, 9803152, (new Item.Properties()).group(ItemGroup.MISC)));
      register("pufferfish_spawn_egg", new ItemSpawnEgg(EntityType.PUFFERFISH, 16167425, 3654642, (new Item.Properties()).group(ItemGroup.MISC)));
      register("rabbit_spawn_egg", new ItemSpawnEgg(EntityType.RABBIT, 10051392, 7555121, (new Item.Properties()).group(ItemGroup.MISC)));
      register("salmon_spawn_egg", new ItemSpawnEgg(EntityType.SALMON, 10489616, 951412, (new Item.Properties()).group(ItemGroup.MISC)));
      register("sheep_spawn_egg", new ItemSpawnEgg(EntityType.SHEEP, 15198183, 16758197, (new Item.Properties()).group(ItemGroup.MISC)));
      register("shulker_spawn_egg", new ItemSpawnEgg(EntityType.SHULKER, 9725844, 5060690, (new Item.Properties()).group(ItemGroup.MISC)));
      register("silverfish_spawn_egg", new ItemSpawnEgg(EntityType.SILVERFISH, 7237230, 3158064, (new Item.Properties()).group(ItemGroup.MISC)));
      register("skeleton_spawn_egg", new ItemSpawnEgg(EntityType.SKELETON, 12698049, 4802889, (new Item.Properties()).group(ItemGroup.MISC)));
      register("skeleton_horse_spawn_egg", new ItemSpawnEgg(EntityType.SKELETON_HORSE, 6842447, 15066584, (new Item.Properties()).group(ItemGroup.MISC)));
      register("slime_spawn_egg", new ItemSpawnEgg(EntityType.SLIME, 5349438, 8306542, (new Item.Properties()).group(ItemGroup.MISC)));
      register("spider_spawn_egg", new ItemSpawnEgg(EntityType.SPIDER, 3419431, 11013646, (new Item.Properties()).group(ItemGroup.MISC)));
      register("squid_spawn_egg", new ItemSpawnEgg(EntityType.SQUID, 2243405, 7375001, (new Item.Properties()).group(ItemGroup.MISC)));
      register("stray_spawn_egg", new ItemSpawnEgg(EntityType.STRAY, 6387319, 14543594, (new Item.Properties()).group(ItemGroup.MISC)));
      register("tropical_fish_spawn_egg", new ItemSpawnEgg(EntityType.TROPICAL_FISH, 15690005, 16775663, (new Item.Properties()).group(ItemGroup.MISC)));
      register("turtle_spawn_egg", new ItemSpawnEgg(EntityType.TURTLE, 15198183, 44975, (new Item.Properties()).group(ItemGroup.MISC)));
      register("vex_spawn_egg", new ItemSpawnEgg(EntityType.VEX, 8032420, 15265265, (new Item.Properties()).group(ItemGroup.MISC)));
      register("villager_spawn_egg", new ItemSpawnEgg(EntityType.VILLAGER, 5651507, 12422002, (new Item.Properties()).group(ItemGroup.MISC)));
      register("vindicator_spawn_egg", new ItemSpawnEgg(EntityType.VINDICATOR, 9804699, 2580065, (new Item.Properties()).group(ItemGroup.MISC)));
      register("witch_spawn_egg", new ItemSpawnEgg(EntityType.WITCH, 3407872, 5349438, (new Item.Properties()).group(ItemGroup.MISC)));
      register("wither_skeleton_spawn_egg", new ItemSpawnEgg(EntityType.WITHER_SKELETON, 1315860, 4672845, (new Item.Properties()).group(ItemGroup.MISC)));
      register("wolf_spawn_egg", new ItemSpawnEgg(EntityType.WOLF, 14144467, 13545366, (new Item.Properties()).group(ItemGroup.MISC)));
      register("zombie_spawn_egg", new ItemSpawnEgg(EntityType.ZOMBIE, 44975, 7969893, (new Item.Properties()).group(ItemGroup.MISC)));
      register("zombie_horse_spawn_egg", new ItemSpawnEgg(EntityType.ZOMBIE_HORSE, 3232308, 9945732, (new Item.Properties()).group(ItemGroup.MISC)));
      register("zombie_pigman_spawn_egg", new ItemSpawnEgg(EntityType.ZOMBIE_PIGMAN, 15373203, 5009705, (new Item.Properties()).group(ItemGroup.MISC)));
      register("zombie_villager_spawn_egg", new ItemSpawnEgg(EntityType.ZOMBIE_VILLAGER, 5651507, 7969893, (new Item.Properties()).group(ItemGroup.MISC)));
      register("experience_bottle", new ItemExpBottle((new Item.Properties()).group(ItemGroup.MISC).rarity(EnumRarity.UNCOMMON)));
      register("fire_charge", new ItemFireCharge((new Item.Properties()).group(ItemGroup.MISC)));
      register("writable_book", new ItemWritableBook((new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC)));
      register("written_book", new ItemWrittenBook((new Item.Properties()).maxStackSize(16)));
      register("emerald", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("item_frame", new ItemItemFrame((new Item.Properties()).group(ItemGroup.DECORATIONS)));
      register(Blocks.FLOWER_POT, ItemGroup.DECORATIONS);
      register("carrot", new ItemSeedFood(3, 0.6F, Blocks.CARROTS, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("potato", new ItemSeedFood(1, 0.3F, Blocks.POTATOES, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("baked_potato", new ItemFood(5, 0.6F, false, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("poisonous_potato", (new ItemFood(2, 0.3F, false, (new Item.Properties()).group(ItemGroup.FOOD))).setPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0), 0.6F));
      register("map", new ItemEmptyMap((new Item.Properties()).group(ItemGroup.MISC)));
      register("golden_carrot", new ItemFood(6, 1.2F, false, (new Item.Properties()).group(ItemGroup.BREWING)));
      register(new ItemWallOrFloor(Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, (new Item.Properties()).group(ItemGroup.DECORATIONS).rarity(EnumRarity.UNCOMMON)));
      register(new ItemWallOrFloor(Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, (new Item.Properties()).group(ItemGroup.DECORATIONS).rarity(EnumRarity.UNCOMMON)));
      register(new ItemSkull(Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, (new Item.Properties()).group(ItemGroup.DECORATIONS).rarity(EnumRarity.UNCOMMON)));
      register(new ItemWallOrFloor(Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, (new Item.Properties()).group(ItemGroup.DECORATIONS).rarity(EnumRarity.UNCOMMON)));
      register(new ItemWallOrFloor(Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, (new Item.Properties()).group(ItemGroup.DECORATIONS).rarity(EnumRarity.UNCOMMON)));
      register(new ItemWallOrFloor(Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, (new Item.Properties()).group(ItemGroup.DECORATIONS).rarity(EnumRarity.UNCOMMON)));
      register("carrot_on_a_stick", new ItemCarrotOnAStick((new Item.Properties()).maxDamage(25).group(ItemGroup.TRANSPORTATION)));
      register("nether_star", new ItemSimpleFoiled((new Item.Properties()).group(ItemGroup.MATERIALS).rarity(EnumRarity.UNCOMMON)));
      register("pumpkin_pie", new ItemFood(8, 0.3F, false, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("firework_rocket", new ItemFireworkRocket((new Item.Properties()).group(ItemGroup.MISC)));
      register("firework_star", new ItemFireworkStar((new Item.Properties()).group(ItemGroup.MISC)));
      register("enchanted_book", new ItemEnchantedBook((new Item.Properties()).maxStackSize(1).rarity(EnumRarity.UNCOMMON)));
      register("nether_brick", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("quartz", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("tnt_minecart", new ItemMinecart(EntityMinecart.Type.TNT, (new Item.Properties()).maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
      register("hopper_minecart", new ItemMinecart(EntityMinecart.Type.HOPPER, (new Item.Properties()).maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
      register("prismarine_shard", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("prismarine_crystals", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("rabbit", new ItemFood(3, 0.3F, true, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("cooked_rabbit", new ItemFood(5, 0.6F, true, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("rabbit_stew", new ItemSoup(10, (new Item.Properties()).maxStackSize(1).group(ItemGroup.FOOD)));
      register("rabbit_foot", new Item((new Item.Properties()).group(ItemGroup.BREWING)));
      register("rabbit_hide", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("armor_stand", new ItemArmorStand((new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("iron_horse_armor", new Item((new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC)));
      register("golden_horse_armor", new Item((new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC)));
      register("diamond_horse_armor", new Item((new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC)));
      register("lead", new ItemLead((new Item.Properties()).group(ItemGroup.TOOLS)));
      register("name_tag", new ItemNameTag((new Item.Properties()).group(ItemGroup.TOOLS)));
      register("command_block_minecart", new ItemMinecart(EntityMinecart.Type.COMMAND_BLOCK, (new Item.Properties()).maxStackSize(1)));
      register("mutton", new ItemFood(2, 0.3F, true, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("cooked_mutton", new ItemFood(6, 0.8F, true, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("white_banner", new ItemBanner(Blocks.WHITE_BANNER, Blocks.WHITE_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("orange_banner", new ItemBanner(Blocks.ORANGE_BANNER, Blocks.ORANGE_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("magenta_banner", new ItemBanner(Blocks.MAGENTA_BANNER, Blocks.MAGENTA_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("light_blue_banner", new ItemBanner(Blocks.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("yellow_banner", new ItemBanner(Blocks.YELLOW_BANNER, Blocks.YELLOW_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("lime_banner", new ItemBanner(Blocks.LIME_BANNER, Blocks.LIME_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("pink_banner", new ItemBanner(Blocks.PINK_BANNER, Blocks.PINK_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("gray_banner", new ItemBanner(Blocks.GRAY_BANNER, Blocks.GRAY_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("light_gray_banner", new ItemBanner(Blocks.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("cyan_banner", new ItemBanner(Blocks.CYAN_BANNER, Blocks.CYAN_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("purple_banner", new ItemBanner(Blocks.PURPLE_BANNER, Blocks.PURPLE_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("blue_banner", new ItemBanner(Blocks.BLUE_BANNER, Blocks.BLUE_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("brown_banner", new ItemBanner(Blocks.BROWN_BANNER, Blocks.BROWN_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("green_banner", new ItemBanner(Blocks.GREEN_BANNER, Blocks.GREEN_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("red_banner", new ItemBanner(Blocks.RED_BANNER, Blocks.RED_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("black_banner", new ItemBanner(Blocks.BLACK_BANNER, Blocks.BLACK_WALL_BANNER, (new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS)));
      register("end_crystal", new ItemEndCrystal((new Item.Properties()).group(ItemGroup.DECORATIONS).rarity(EnumRarity.RARE)));
      register("chorus_fruit", (new ItemChorusFruit(4, 0.3F, (new Item.Properties()).group(ItemGroup.MATERIALS))).setAlwaysEdible());
      register("popped_chorus_fruit", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("beetroot", new ItemFood(1, 0.6F, false, (new Item.Properties()).group(ItemGroup.FOOD)));
      register("beetroot_seeds", new ItemSeeds(Blocks.BEETROOTS, (new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("beetroot_soup", new ItemSoup(6, (new Item.Properties()).maxStackSize(1).group(ItemGroup.FOOD)));
      register("dragon_breath", new Item((new Item.Properties()).containerItem(item1).group(ItemGroup.BREWING).rarity(EnumRarity.UNCOMMON)));
      register("splash_potion", new ItemSplashPotion((new Item.Properties()).maxStackSize(1).group(ItemGroup.BREWING)));
      register("spectral_arrow", new ItemSpectralArrow((new Item.Properties()).group(ItemGroup.COMBAT)));
      register("tipped_arrow", new ItemTippedArrow((new Item.Properties()).group(ItemGroup.COMBAT)));
      register("lingering_potion", new ItemLingeringPotion((new Item.Properties()).maxStackSize(1).group(ItemGroup.BREWING)));
      register("shield", new ItemShield((new Item.Properties()).maxDamage(336).group(ItemGroup.COMBAT)));
      register("elytra", new ItemElytra((new Item.Properties()).maxDamage(432).group(ItemGroup.TRANSPORTATION).rarity(EnumRarity.UNCOMMON)));
      register("spruce_boat", new ItemBoat(EntityBoat.Type.SPRUCE, (new Item.Properties()).maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
      register("birch_boat", new ItemBoat(EntityBoat.Type.BIRCH, (new Item.Properties()).maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
      register("jungle_boat", new ItemBoat(EntityBoat.Type.JUNGLE, (new Item.Properties()).maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
      register("acacia_boat", new ItemBoat(EntityBoat.Type.ACACIA, (new Item.Properties()).maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
      register("dark_oak_boat", new ItemBoat(EntityBoat.Type.DARK_OAK, (new Item.Properties()).maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
      register("totem_of_undying", new Item((new Item.Properties()).maxStackSize(1).group(ItemGroup.COMBAT).rarity(EnumRarity.UNCOMMON)));
      register("shulker_shell", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("iron_nugget", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("knowledge_book", new ItemKnowledgeBook((new Item.Properties()).maxStackSize(1)));
      register("debug_stick", new ItemDebugStick((new Item.Properties()).maxStackSize(1)));
      register("music_disc_13", new ItemRecord(1, SoundEvents.MUSIC_DISC_13, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC).rarity(EnumRarity.RARE)));
      register("music_disc_cat", new ItemRecord(2, SoundEvents.MUSIC_DISC_CAT, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC).rarity(EnumRarity.RARE)));
      register("music_disc_blocks", new ItemRecord(3, SoundEvents.MUSIC_DISC_BLOCKS, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC).rarity(EnumRarity.RARE)));
      register("music_disc_chirp", new ItemRecord(4, SoundEvents.MUSIC_DISC_CHIRP, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC).rarity(EnumRarity.RARE)));
      register("music_disc_far", new ItemRecord(5, SoundEvents.MUSIC_DISC_FAR, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC).rarity(EnumRarity.RARE)));
      register("music_disc_mall", new ItemRecord(6, SoundEvents.MUSIC_DISC_MALL, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC).rarity(EnumRarity.RARE)));
      register("music_disc_mellohi", new ItemRecord(7, SoundEvents.MUSIC_DISC_MELLOHI, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC).rarity(EnumRarity.RARE)));
      register("music_disc_stal", new ItemRecord(8, SoundEvents.MUSIC_DISC_STAL, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC).rarity(EnumRarity.RARE)));
      register("music_disc_strad", new ItemRecord(9, SoundEvents.MUSIC_DISC_STRAD, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC).rarity(EnumRarity.RARE)));
      register("music_disc_ward", new ItemRecord(10, SoundEvents.MUSIC_DISC_WARD, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC).rarity(EnumRarity.RARE)));
      register("music_disc_11", new ItemRecord(11, SoundEvents.MUSIC_DISC_11, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC).rarity(EnumRarity.RARE)));
      register("music_disc_wait", new ItemRecord(12, SoundEvents.MUSIC_DISC_WAIT, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC).rarity(EnumRarity.RARE)));
      register("trident", new ItemTrident((new Item.Properties()).maxDamage(250).group(ItemGroup.COMBAT)));
      register("phantom_membrane", new Item((new Item.Properties()).group(ItemGroup.BREWING)));
      register("nautilus_shell", new Item((new Item.Properties()).group(ItemGroup.MATERIALS)));
      register("heart_of_the_sea", new Item((new Item.Properties()).group(ItemGroup.MATERIALS).rarity(EnumRarity.UNCOMMON)));
   }

   /**
    * Register a default ItemBlock for the given Block.
    */
   private static void register(Block blockIn) {
      register(new ItemBlock(blockIn, new Item.Properties()));
   }

   private static void register(Block blockIn, ItemGroup group) {
      register(new ItemBlock(blockIn, (new Item.Properties()).group(group)));
   }

   private static void register(ItemBlock itemBlockIn) {
      register(itemBlockIn.getBlock(), itemBlockIn);
   }

   /**
    * Register the given Item as the ItemBlock for the given Block.
    */
   protected static void register(Block blockIn, Item itemIn) {
      register(IRegistry.field_212618_g.getKey(blockIn), itemIn);
   }

   private static void register(String id, Item itemIn) {
      register(new ResourceLocation(id), itemIn);
   }

   private static void register(ResourceLocation resourceLocationIn, Item itemIn) {
      if (itemIn instanceof ItemBlock) {
         ((ItemBlock)itemIn).addToBlockToItemMap(BLOCK_TO_ITEM, itemIn);
      }

      IRegistry.field_212630_s.put(resourceLocationIn, itemIn);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getDefaultInstance() {
      return new ItemStack(this);
   }

   public boolean isIn(Tag<Item> tagIn) {
      return tagIn.contains(this);
   }

   public static class Properties {
      private int maxStackSize = 64;
      private int maxDamage;
      private Item containerItem;
      private ItemGroup group;
      private EnumRarity rarity = EnumRarity.COMMON;
      private boolean canRepair = true;
      private java.util.Map<net.minecraftforge.common.ToolType, Integer> toolClasses = Maps.newHashMap();
      private java.util.function.Supplier<java.util.concurrent.Callable<net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer>> teisr;

      public Item.Properties maxStackSize(int maxStackSizeIn) {
         if (this.maxDamage > 0) {
            throw new RuntimeException("Unable to have damage AND stack.");
         } else {
            this.maxStackSize = maxStackSizeIn;
            return this;
         }
      }

      public Item.Properties defaultMaxDamage(int maxDamageIn) {
         return this.maxDamage == 0 ? this.maxDamage(maxDamageIn) : this;
      }

      private Item.Properties maxDamage(int maxDamageIn) {
         this.maxDamage = maxDamageIn;
         this.maxStackSize = 1;
         return this;
      }

      public Item.Properties containerItem(Item containerItemIn) {
         this.containerItem = containerItemIn;
         return this;
      }

      public Item.Properties group(ItemGroup groupIn) {
         this.group = groupIn;
         return this;
      }

      public Item.Properties rarity(EnumRarity rarityIn) {
         this.rarity = rarityIn;
         return this;
      }

      public Item.Properties setNoRepair() {
         canRepair = false;
         return this;
      }

      public Item.Properties addToolType(net.minecraftforge.common.ToolType type, int level) {
         toolClasses.put(type, level);
         return this;
      }

      public Item.Properties setTEISR(java.util.function.Supplier<java.util.concurrent.Callable<net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer>> teisr) {
         this.teisr = teisr;
         return this;
      }
   }
}