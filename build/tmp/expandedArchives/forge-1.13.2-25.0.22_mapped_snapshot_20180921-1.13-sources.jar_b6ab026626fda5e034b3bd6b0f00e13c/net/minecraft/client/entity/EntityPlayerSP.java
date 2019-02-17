package net.minecraft.client.entity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ElytraSound;
import net.minecraft.client.audio.IAmbientSoundHandler;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.UnderwaterAmbientSoundHandler;
import net.minecraft.client.audio.UnderwaterAmbientSounds;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEditCommandBlockMinecart;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiEditStructure;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityPlayerSP extends AbstractClientPlayer {
   public final NetHandlerPlayClient connection;
   private final StatisticsManager stats;
   private final RecipeBookClient recipeBook;
   private final List<IAmbientSoundHandler> ambientSoundHandlers = Lists.newArrayList();
   private int permissionLevel = 0;
   /**
    * The last X position which was transmitted to the server, used to determine when the X position changes and needs
    * to be re-trasmitted
    */
   private double lastReportedPosX;
   /**
    * The last Y position which was transmitted to the server, used to determine when the Y position changes and needs
    * to be re-transmitted
    */
   private double lastReportedPosY;
   /**
    * The last Z position which was transmitted to the server, used to determine when the Z position changes and needs
    * to be re-transmitted
    */
   private double lastReportedPosZ;
   /**
    * The last yaw value which was transmitted to the server, used to determine when the yaw changes and needs to be re-
    * transmitted
    */
   private float lastReportedYaw;
   /**
    * The last pitch value which was transmitted to the server, used to determine when the pitch changes and needs to be
    * re-transmitted
    */
   private float lastReportedPitch;
   private boolean prevOnGround;
   /** the last sneaking state sent to the server */
   private boolean serverSneakState;
   /** the last sprinting state sent to the server */
   private boolean serverSprintState;
   /**
    * Reset to 0 every time position is sent to the server, used to send periodic updates every 20 ticks even when the
    * player is not moving.
    */
   private int positionUpdateTicks;
   private boolean hasValidHealth;
   private String serverBrand;
   public MovementInput movementInput;
   protected Minecraft mc;
   /**
    * Used to tell if the player pressed forward twice. If this is at 0 and it's pressed (And they are allowed to
    * sprint, aka enough food on the ground etc) it sets this to 7. If it's pressed and it's greater than 0 enable
    * sprinting.
    */
   protected int sprintToggleTimer;
   /** Ticks left before sprinting is disabled. */
   public int sprintingTicksLeft;
   public float renderArmYaw;
   public float renderArmPitch;
   public float prevRenderArmYaw;
   public float prevRenderArmPitch;
   private int horseJumpPowerCounter;
   private float horseJumpPower;
   /** The amount of time an entity has been in a Portal */
   public float timeInPortal;
   /** The amount of time an entity has been in a Portal the previous tick */
   public float prevTimeInPortal;
   private boolean handActive;
   private EnumHand activeHand;
   private boolean rowingBoat;
   private boolean autoJumpEnabled = true;
   private int autoJumpTime;
   private boolean wasFallFlying;
   private int counterInWater;

   public EntityPlayerSP(Minecraft p_i48190_1_, World p_i48190_2_, NetHandlerPlayClient p_i48190_3_, StatisticsManager p_i48190_4_, RecipeBookClient p_i48190_5_) {
      super(p_i48190_2_, p_i48190_3_.getGameProfile());
      this.connection = p_i48190_3_;
      this.stats = p_i48190_4_;
      this.recipeBook = p_i48190_5_;
      this.mc = p_i48190_1_;
      this.dimension = DimensionType.OVERWORLD;
      this.ambientSoundHandlers.add(new UnderwaterAmbientSoundHandler(this, p_i48190_1_.getSoundHandler()));
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      net.minecraftforge.common.ForgeHooks.onPlayerAttack(this, source, amount);
      return false;
   }

   /**
    * Heal living entity (param: amount of half-hearts)
    */
   public void heal(float healAmount) {
   }

   public boolean startRiding(Entity entityIn, boolean force) {
      if (!super.startRiding(entityIn, force)) {
         return false;
      } else {
         if (entityIn instanceof EntityMinecart) {
            this.mc.getSoundHandler().play(new MovingSoundMinecartRiding(this, (EntityMinecart)entityIn));
         }

         if (entityIn instanceof EntityBoat) {
            this.prevRotationYaw = entityIn.rotationYaw;
            this.rotationYaw = entityIn.rotationYaw;
            this.setRotationYawHead(entityIn.rotationYaw);
         }

         return true;
      }
   }

   /**
    * Dismounts this entity from the entity it is riding.
    */
   public void stopRiding() {
      super.stopRiding();
      this.rowingBoat = false;
   }

   /**
    * Gets the current pitch of the entity.
    */
   public float getPitch(float partialTicks) {
      return this.rotationPitch;
   }

   /**
    * Gets the current yaw of the entity
    */
   public float getYaw(float partialTicks) {
      return this.isPassenger() ? super.getYaw(partialTicks) : this.rotationYaw;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (this.world.isBlockLoaded(new BlockPos(this.posX, 0.0D, this.posZ))) {
         super.tick();
         if (this.isPassenger()) {
            this.connection.sendPacket(new CPacketPlayer.Rotation(this.rotationYaw, this.rotationPitch, this.onGround));
            this.connection.sendPacket(new CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
            Entity entity = this.getLowestRidingEntity();
            if (entity != this && entity.canPassengerSteer()) {
               this.connection.sendPacket(new CPacketVehicleMove(entity));
            }
         } else {
            this.onUpdateWalkingPlayer();
         }

         for(IAmbientSoundHandler iambientsoundhandler : this.ambientSoundHandlers) {
            iambientsoundhandler.tick();
         }

      }
   }

   /**
    * called every tick when the player is on foot. Performs all the things that normally happen during movement.
    */
   private void onUpdateWalkingPlayer() {
      boolean flag = this.isSprinting();
      if (flag != this.serverSprintState) {
         if (flag) {
            this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SPRINTING));
         } else {
            this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SPRINTING));
         }

         this.serverSprintState = flag;
      }

      boolean flag1 = this.isSneaking();
      if (flag1 != this.serverSneakState) {
         if (flag1) {
            this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SNEAKING));
         } else {
            this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SNEAKING));
         }

         this.serverSneakState = flag1;
      }

      if (this.isCurrentViewEntity()) {
         AxisAlignedBB axisalignedbb = this.getBoundingBox();
         double d0 = this.posX - this.lastReportedPosX;
         double d1 = axisalignedbb.minY - this.lastReportedPosY;
         double d2 = this.posZ - this.lastReportedPosZ;
         double d3 = (double)(this.rotationYaw - this.lastReportedYaw);
         double d4 = (double)(this.rotationPitch - this.lastReportedPitch);
         ++this.positionUpdateTicks;
         boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || this.positionUpdateTicks >= 20;
         boolean flag3 = d3 != 0.0D || d4 != 0.0D;
         if (this.isPassenger()) {
            this.connection.sendPacket(new CPacketPlayer.PositionRotation(this.motionX, -999.0D, this.motionZ, this.rotationYaw, this.rotationPitch, this.onGround));
            flag2 = false;
         } else if (flag2 && flag3) {
            this.connection.sendPacket(new CPacketPlayer.PositionRotation(this.posX, axisalignedbb.minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround));
         } else if (flag2) {
            this.connection.sendPacket(new CPacketPlayer.Position(this.posX, axisalignedbb.minY, this.posZ, this.onGround));
         } else if (flag3) {
            this.connection.sendPacket(new CPacketPlayer.Rotation(this.rotationYaw, this.rotationPitch, this.onGround));
         } else if (this.prevOnGround != this.onGround) {
            this.connection.sendPacket(new CPacketPlayer(this.onGround));
         }

         if (flag2) {
            this.lastReportedPosX = this.posX;
            this.lastReportedPosY = axisalignedbb.minY;
            this.lastReportedPosZ = this.posZ;
            this.positionUpdateTicks = 0;
         }

         if (flag3) {
            this.lastReportedYaw = this.rotationYaw;
            this.lastReportedPitch = this.rotationPitch;
         }

         this.prevOnGround = this.onGround;
         this.autoJumpEnabled = this.mc.gameSettings.autoJump;
      }

   }

   /**
    * Drop one item out of the currently selected stack if {@code dropAll} is false. If {@code dropItem} is true the
    * entire stack is dropped.
    */
   @Nullable
   public EntityItem dropItem(boolean dropAll) {
      CPacketPlayerDigging.Action cpacketplayerdigging$action = dropAll ? CPacketPlayerDigging.Action.DROP_ALL_ITEMS : CPacketPlayerDigging.Action.DROP_ITEM;
      this.connection.sendPacket(new CPacketPlayerDigging(cpacketplayerdigging$action, BlockPos.ORIGIN, EnumFacing.DOWN));
      this.inventory.decrStackSize(this.inventory.currentItem, dropAll && !this.inventory.getCurrentItem().isEmpty() ? this.inventory.getCurrentItem().getCount() : 1);
      return null;
   }

   public ItemStack dropItemAndGetStack(EntityItem p_184816_1_) {
      return ItemStack.EMPTY;
   }

   /**
    * Sends a chat message from the player.
    */
   public void sendChatMessage(String message) {
      this.connection.sendPacket(new CPacketChatMessage(message));
   }

   public void swingArm(EnumHand hand) {
      super.swingArm(hand);
      this.connection.sendPacket(new CPacketAnimation(hand));
   }

   public void respawnPlayer() {
      this.connection.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
   }

   /**
    * Deals damage to the entity. This will take the armor of the entity into consideration before damaging the health
    * bar.
    */
   protected void damageEntity(DamageSource damageSrc, float damageAmount) {
      if (!this.isInvulnerableTo(damageSrc)) {
         this.setHealth(this.getHealth() - damageAmount);
      }
   }

   /**
    * set current crafting inventory back to the 2x2 square
    */
   public void closeScreen() {
      this.connection.sendPacket(new CPacketCloseWindow(this.openContainer.windowId));
      this.closeScreenAndDropStack();
   }

   public void closeScreenAndDropStack() {
      this.inventory.setItemStack(ItemStack.EMPTY);
      super.closeScreen();
      this.mc.displayGuiScreen((GuiScreen)null);
   }

   /**
    * Updates health locally.
    */
   public void setPlayerSPHealth(float health) {
      if (this.hasValidHealth) {
         float f = this.getHealth() - health;
         if (f <= 0.0F) {
            this.setHealth(health);
            if (f < 0.0F) {
               this.hurtResistantTime = this.maxHurtResistantTime / 2;
            }
         } else {
            this.lastDamage = f;
            this.setHealth(this.getHealth());
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.damageEntity(DamageSource.GENERIC, f);
            this.maxHurtTime = 10;
            this.hurtTime = this.maxHurtTime;
         }
      } else {
         this.setHealth(health);
         this.hasValidHealth = true;
      }

   }

   /**
    * Sends the player's abilities to the server (if there is one).
    */
   public void sendPlayerAbilities() {
      this.connection.sendPacket(new CPacketPlayerAbilities(this.abilities));
   }

   /**
    * returns true if this is an EntityPlayerSP, or the logged in player.
    */
   public boolean isUser() {
      return true;
   }

   protected void sendHorseJump() {
      this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_RIDING_JUMP, MathHelper.floor(this.getHorseJumpPower() * 100.0F)));
   }

   public void sendHorseInventory() {
      this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.OPEN_INVENTORY));
   }

   /**
    * Sets the brand of the currently connected server. Server brand information is sent over the {@code MC|Brand}
    * plugin channel, and is used to identify modded servers in crash reports.
    */
   public void setServerBrand(String brand) {
      this.serverBrand = brand;
   }

   /**
    * Gets the brand of the currently connected server. May be null if the server hasn't yet sent brand information.
    * Server brand information is sent over the {@code MC|Brand} plugin channel, and is used to identify modded servers
    * in crash reports.
    */
   public String getServerBrand() {
      return this.serverBrand;
   }

   public StatisticsManager getStats() {
      return this.stats;
   }

   public RecipeBookClient getRecipeBook() {
      return this.recipeBook;
   }

   public void removeRecipeHighlight(IRecipe p_193103_1_) {
      if (this.recipeBook.isNew(p_193103_1_)) {
         this.recipeBook.markSeen(p_193103_1_);
         this.connection.sendPacket(new CPacketRecipeInfo(p_193103_1_));
      }

   }

   protected int getPermissionLevel() {
      return this.permissionLevel;
   }

   public void setPermissionLevel(int p_184839_1_) {
      this.permissionLevel = p_184839_1_;
   }

   public void sendStatusMessage(ITextComponent chatComponent, boolean actionBar) {
      if (actionBar) {
         this.mc.ingameGUI.setOverlayMessage(chatComponent, false);
      } else {
         this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
      }

   }

   protected boolean pushOutOfBlocks(double x, double y, double z) {
      if (this.noClip) {
         return false;
      } else {
         BlockPos blockpos = new BlockPos(x, y, z);
         double d0 = x - (double)blockpos.getX();
         double d1 = z - (double)blockpos.getZ();
         if (this.func_205027_h(blockpos)) {
            int i = -1;
            double d2 = 9999.0D;
            if (this.func_207402_f(blockpos.west()) && d0 < d2) {
               d2 = d0;
               i = 0;
            }

            if (this.func_207402_f(blockpos.east()) && 1.0D - d0 < d2) {
               d2 = 1.0D - d0;
               i = 1;
            }

            if (this.func_207402_f(blockpos.north()) && d1 < d2) {
               d2 = d1;
               i = 4;
            }

            if (this.func_207402_f(blockpos.south()) && 1.0D - d1 < d2) {
               d2 = 1.0D - d1;
               i = 5;
            }

            float f = 0.1F;
            if (i == 0) {
               this.motionX = (double)-0.1F;
            }

            if (i == 1) {
               this.motionX = (double)0.1F;
            }

            if (i == 4) {
               this.motionZ = (double)-0.1F;
            }

            if (i == 5) {
               this.motionZ = (double)0.1F;
            }
         }

         return false;
      }
   }

   private boolean func_205027_h(BlockPos p_205027_1_) {
      if (this.isSwimming()) {
         return !this.isNormalCube(p_205027_1_);
      } else {
         return !this.func_207402_f(p_205027_1_);
      }
   }

   /**
    * Set sprinting switch for Entity.
    */
   public void setSprinting(boolean sprinting) {
      super.setSprinting(sprinting);
      this.sprintingTicksLeft = 0;
   }

   /**
    * Sets the current XP, total XP, and level number.
    */
   public void setXPStats(float currentXP, int maxXP, int level) {
      this.experience = currentXP;
      this.experienceTotal = maxXP;
      this.experienceLevel = level;
   }

   /**
    * Send a chat message to the CommandSender
    */
   public void sendMessage(ITextComponent component) {
      this.mc.ingameGUI.getChatGUI().printChatMessage(component);
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   public void handleStatusUpdate(byte id) {
      if (id >= 24 && id <= 28) {
         this.setPermissionLevel(id - 24);
      } else {
         super.handleStatusUpdate(id);
      }

   }

   public void playSound(SoundEvent soundIn, float volume, float pitch) {
      net.minecraftforge.event.entity.PlaySoundAtEntityEvent event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtEntity(this, soundIn, this.getSoundCategory(), volume, pitch);
      if (event.isCanceled() || event.getSound() == null) return;
      soundIn = event.getSound();
      volume = event.getVolume();
      pitch = event.getPitch();
      this.world.playSound(this.posX, this.posY, this.posZ, soundIn, this.getSoundCategory(), volume, pitch, false);
   }

   /**
    * Returns whether the entity is in a server world
    */
   public boolean isServerWorld() {
      return true;
   }

   public void setActiveHand(EnumHand hand) {
      ItemStack itemstack = this.getHeldItem(hand);
      if (!itemstack.isEmpty() && !this.isHandActive()) {
         super.setActiveHand(hand);
         this.handActive = true;
         this.activeHand = hand;
      }
   }

   public boolean isHandActive() {
      return this.handActive;
   }

   public void resetActiveHand() {
      super.resetActiveHand();
      this.handActive = false;
   }

   public EnumHand getActiveHand() {
      return this.activeHand;
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      super.notifyDataManagerChange(key);
      if (LIVING_FLAGS.equals(key)) {
         boolean flag = (this.dataManager.get(LIVING_FLAGS) & 1) > 0;
         EnumHand enumhand = (this.dataManager.get(LIVING_FLAGS) & 2) > 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
         if (flag && !this.handActive) {
            this.setActiveHand(enumhand);
         } else if (!flag && this.handActive) {
            this.resetActiveHand();
         }
      }

      if (FLAGS.equals(key) && this.isElytraFlying() && !this.wasFallFlying) {
         this.mc.getSoundHandler().play(new ElytraSound(this));
      }

   }

   public boolean isRidingHorse() {
      Entity entity = this.getRidingEntity();
      return this.isPassenger() && entity instanceof IJumpingMount && ((IJumpingMount)entity).canJump();
   }

   public float getHorseJumpPower() {
      return this.horseJumpPower;
   }

   public void openSignEditor(TileEntitySign signTile) {
      this.mc.displayGuiScreen(new GuiEditSign(signTile));
   }

   public void openMinecartCommandBlock(CommandBlockBaseLogic commandBlock) {
      this.mc.displayGuiScreen(new GuiEditCommandBlockMinecart(commandBlock));
   }

   public void openCommandBlock(TileEntityCommandBlock commandBlock) {
      this.mc.displayGuiScreen(new GuiCommandBlock(commandBlock));
   }

   public void openStructureBlock(TileEntityStructure structure) {
      this.mc.displayGuiScreen(new GuiEditStructure(structure));
   }

   public void openBook(ItemStack stack, EnumHand hand) {
      Item item = stack.getItem();
      if (item == Items.WRITABLE_BOOK) {
         this.mc.displayGuiScreen(new GuiScreenBook(this, stack, true, hand));
      }

   }

   /**
    * Displays the GUI for interacting with a chest inventory.
    */
   public void displayGUIChest(IInventory chestInventory) {
      String s = chestInventory instanceof IInteractionObject ? ((IInteractionObject)chestInventory).getGuiID() : "minecraft:container";
      if ("minecraft:chest".equals(s)) {
         this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
      } else if ("minecraft:hopper".equals(s)) {
         this.mc.displayGuiScreen(new GuiHopper(this.inventory, chestInventory));
      } else if ("minecraft:furnace".equals(s)) {
         this.mc.displayGuiScreen(new GuiFurnace(this.inventory, chestInventory));
      } else if ("minecraft:brewing_stand".equals(s)) {
         this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, chestInventory));
      } else if ("minecraft:beacon".equals(s)) {
         this.mc.displayGuiScreen(new GuiBeacon(this.inventory, chestInventory));
      } else if (!"minecraft:dispenser".equals(s) && !"minecraft:dropper".equals(s)) {
         if ("minecraft:shulker_box".equals(s)) {
            this.mc.displayGuiScreen(new GuiShulkerBox(this.inventory, chestInventory));
         } else {
            this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
         }
      } else {
         this.mc.displayGuiScreen(new GuiDispenser(this.inventory, chestInventory));
      }

   }

   public void openHorseInventory(AbstractHorse horse, IInventory inventoryIn) {
      this.mc.displayGuiScreen(new GuiScreenHorseInventory(this.inventory, inventoryIn, horse));
   }

   public void displayGui(IInteractionObject guiOwner) {
      String s = guiOwner.getGuiID();
      if ("minecraft:crafting_table".equals(s)) {
         this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.world));
      } else if ("minecraft:enchanting_table".equals(s)) {
         this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.world, guiOwner));
      } else if ("minecraft:anvil".equals(s)) {
         this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.world));
      }

   }

   public void displayVillagerTradeGui(IMerchant villager) {
      this.mc.displayGuiScreen(new GuiMerchant(this.inventory, villager, this.world));
   }

   /**
    * Called when the entity is dealt a critical hit.
    */
   public void onCriticalHit(Entity entityHit) {
      this.mc.particles.addParticleEmitter(entityHit, Particles.CRIT);
   }

   public void onEnchantmentCritical(Entity entityHit) {
      this.mc.particles.addParticleEmitter(entityHit, Particles.ENCHANTED_HIT);
   }

   /**
    * Returns if this entity is sneaking.
    */
   public boolean isSneaking() {
      boolean flag = this.movementInput != null && this.movementInput.sneak;
      return flag && !this.sleeping;
   }

   public void updateEntityActionState() {
      super.updateEntityActionState();
      if (this.isCurrentViewEntity()) {
         this.moveStrafing = this.movementInput.moveStrafe;
         this.moveForward = this.movementInput.moveForward;
         this.isJumping = this.movementInput.jump;
         this.prevRenderArmYaw = this.renderArmYaw;
         this.prevRenderArmPitch = this.renderArmPitch;
         this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.rotationPitch - this.renderArmPitch) * 0.5D);
         this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.rotationYaw - this.renderArmYaw) * 0.5D);
      }

   }

   protected boolean isCurrentViewEntity() {
      return this.mc.getRenderViewEntity() == this;
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      ++this.sprintingTicksLeft;
      if (this.sprintToggleTimer > 0) {
         --this.sprintToggleTimer;
      }

      this.prevTimeInPortal = this.timeInPortal;
      if (this.inPortal) {
         if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
            if (this.mc.currentScreen instanceof GuiContainer) {
               this.closeScreen();
            }

            this.mc.displayGuiScreen((GuiScreen)null);
         }

         if (this.timeInPortal == 0.0F) {
            this.mc.getSoundHandler().play(SimpleSound.getMasterRecord(SoundEvents.BLOCK_PORTAL_TRIGGER, this.rand.nextFloat() * 0.4F + 0.8F));
         }

         this.timeInPortal += 0.0125F;
         if (this.timeInPortal >= 1.0F) {
            this.timeInPortal = 1.0F;
         }

         this.inPortal = false;
      } else if (this.isPotionActive(MobEffects.NAUSEA) && this.getActivePotionEffect(MobEffects.NAUSEA).getDuration() > 60) {
         this.timeInPortal += 0.006666667F;
         if (this.timeInPortal > 1.0F) {
            this.timeInPortal = 1.0F;
         }
      } else {
         if (this.timeInPortal > 0.0F) {
            this.timeInPortal -= 0.05F;
         }

         if (this.timeInPortal < 0.0F) {
            this.timeInPortal = 0.0F;
         }
      }

      if (this.timeUntilPortal > 0) {
         --this.timeUntilPortal;
      }

      boolean flag = this.movementInput.jump;
      boolean flag1 = this.movementInput.sneak;
      float f = 0.8F;
      boolean flag2 = this.movementInput.moveForward >= 0.8F;
      this.movementInput.updatePlayerMoveState();
      net.minecraftforge.client.ForgeHooksClient.onInputUpdate(this, this.movementInput);
      this.mc.getTutorial().handleMovement(this.movementInput);
      if (this.isHandActive() && !this.isPassenger()) {
         this.movementInput.moveStrafe *= 0.2F;
         this.movementInput.moveForward *= 0.2F;
         this.sprintToggleTimer = 0;
      }

      boolean flag3 = false;
      if (this.autoJumpTime > 0) {
         --this.autoJumpTime;
         flag3 = true;
         this.movementInput.jump = true;
      }

      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent event = new net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent(this, axisalignedbb);
      if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
      axisalignedbb = event.getEntityBoundingBox();
      this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
      this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
      this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
      this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
      }
      boolean flag4 = (float)this.getFoodStats().getFoodLevel() > 6.0F || this.abilities.allowFlying;
      if ((this.onGround || this.canSwim()) && !flag1 && !flag2 && this.movementInput.moveForward >= 0.8F && !this.isSprinting() && flag4 && !this.isHandActive() && !this.isPotionActive(MobEffects.BLINDNESS)) {
         if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
            this.sprintToggleTimer = 7;
         } else {
            this.setSprinting(true);
         }
      }

      if (!this.isSprinting() && (!this.isInWater() || this.canSwim()) && this.movementInput.moveForward >= 0.8F && flag4 && !this.isHandActive() && !this.isPotionActive(MobEffects.BLINDNESS) && this.mc.gameSettings.keyBindSprint.isKeyDown()) {
         this.setSprinting(true);
      }

      if (this.isSprinting()) {
         boolean flag5 = this.movementInput.moveForward < 0.8F || !flag4;
         boolean flag6 = flag5 || this.collidedHorizontally || this.isInWater() && !this.canSwim();
         if (this.isSwimming()) {
            if (!this.onGround && !this.movementInput.sneak && flag5 || !this.isInWater()) {
               this.setSprinting(false);
            }
         } else if (flag6) {
            this.setSprinting(false);
         }
      }

      if (this.abilities.allowFlying) {
         if (this.mc.playerController.isSpectatorMode()) {
            if (!this.abilities.isFlying) {
               this.abilities.isFlying = true;
               this.sendPlayerAbilities();
            }
         } else if (!flag && this.movementInput.jump && !flag3) {
            if (this.flyToggleTimer == 0) {
               this.flyToggleTimer = 7;
            } else if (!this.isSwimming()) {
               this.abilities.isFlying = !this.abilities.isFlying;
               this.sendPlayerAbilities();
               this.flyToggleTimer = 0;
            }
         }
      }

      if (this.movementInput.jump && !flag && !this.onGround && this.motionY < 0.0D && !this.isElytraFlying() && !this.abilities.isFlying) {
         ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
         if (itemstack.getItem() == Items.ELYTRA && ItemElytra.isUsable(itemstack)) {
            this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_FALL_FLYING));
         }
      }

      this.wasFallFlying = this.isElytraFlying();
      if (this.isInWater() && this.movementInput.sneak) {
         this.func_203010_cG();
      }

      if (this.areEyesInFluid(FluidTags.WATER)) {
         int i = this.isSpectator() ? 10 : 1;
         this.counterInWater = MathHelper.clamp(this.counterInWater + i, 0, 600);
      } else if (this.counterInWater > 0) {
         this.areEyesInFluid(FluidTags.WATER);
         this.counterInWater = MathHelper.clamp(this.counterInWater - 10, 0, 600);
      }

      if (this.abilities.isFlying && this.isCurrentViewEntity()) {
         if (this.movementInput.sneak) {
            this.movementInput.moveStrafe = (float)((double)this.movementInput.moveStrafe / 0.3D);
            this.movementInput.moveForward = (float)((double)this.movementInput.moveForward / 0.3D);
            this.motionY -= (double)(this.abilities.getFlySpeed() * 3.0F);
         }

         if (this.movementInput.jump) {
            this.motionY += (double)(this.abilities.getFlySpeed() * 3.0F);
         }
      }

      if (this.isRidingHorse()) {
         IJumpingMount ijumpingmount = (IJumpingMount)this.getRidingEntity();
         if (this.horseJumpPowerCounter < 0) {
            ++this.horseJumpPowerCounter;
            if (this.horseJumpPowerCounter == 0) {
               this.horseJumpPower = 0.0F;
            }
         }

         if (flag && !this.movementInput.jump) {
            this.horseJumpPowerCounter = -10;
            ijumpingmount.setJumpPower(MathHelper.floor(this.getHorseJumpPower() * 100.0F));
            this.sendHorseJump();
         } else if (!flag && this.movementInput.jump) {
            this.horseJumpPowerCounter = 0;
            this.horseJumpPower = 0.0F;
         } else if (flag) {
            ++this.horseJumpPowerCounter;
            if (this.horseJumpPowerCounter < 10) {
               this.horseJumpPower = (float)this.horseJumpPowerCounter * 0.1F;
            } else {
               this.horseJumpPower = 0.8F + 2.0F / (float)(this.horseJumpPowerCounter - 9) * 0.1F;
            }
         }
      } else {
         this.horseJumpPower = 0.0F;
      }

      super.livingTick();
      if (this.onGround && this.abilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
         this.abilities.isFlying = false;
         this.sendPlayerAbilities();
      }

   }

   /**
    * Handles updating while riding another entity
    */
   public void updateRidden() {
      super.updateRidden();
      this.rowingBoat = false;
      if (this.getRidingEntity() instanceof EntityBoat) {
         EntityBoat entityboat = (EntityBoat)this.getRidingEntity();
         entityboat.updateInputs(this.movementInput.leftKeyDown, this.movementInput.rightKeyDown, this.movementInput.forwardKeyDown, this.movementInput.backKeyDown);
         this.rowingBoat |= this.movementInput.leftKeyDown || this.movementInput.rightKeyDown || this.movementInput.forwardKeyDown || this.movementInput.backKeyDown;
      }

   }

   public boolean isRowingBoat() {
      return this.rowingBoat;
   }

   /**
    * Removes the given potion effect from the active potion map and returns it. Does not call cleanup callbacks for the
    * end of the potion effect.
    */
   @Nullable
   public PotionEffect removeActivePotionEffect(@Nullable Potion potioneffectin) {
      if (potioneffectin == MobEffects.NAUSEA) {
         this.prevTimeInPortal = 0.0F;
         this.timeInPortal = 0.0F;
      }

      return super.removeActivePotionEffect(potioneffectin);
   }

   /**
    * Tries to move the entity towards the specified location.
    */
   public void move(MoverType type, double x, double y, double z) {
      double d0 = this.posX;
      double d1 = this.posZ;
      super.move(type, x, y, z);
      this.updateAutoJump((float)(this.posX - d0), (float)(this.posZ - d1));
   }

   public boolean isAutoJumpEnabled() {
      return this.autoJumpEnabled;
   }

   protected void updateAutoJump(float p_189810_1_, float p_189810_2_) {
      if (this.isAutoJumpEnabled()) {
         if (this.autoJumpTime <= 0 && this.onGround && !this.isSneaking() && !this.isPassenger()) {
            Vec2f vec2f = this.movementInput.getMoveVector();
            if (vec2f.x != 0.0F || vec2f.y != 0.0F) {
               Vec3d vec3d = new Vec3d(this.posX, this.getBoundingBox().minY, this.posZ);
               double d0 = this.posX + (double)p_189810_1_;
               double d1 = this.posZ + (double)p_189810_2_;
               Vec3d vec3d1 = new Vec3d(d0, this.getBoundingBox().minY, d1);
               Vec3d vec3d2 = new Vec3d((double)p_189810_1_, 0.0D, (double)p_189810_2_);
               float f = this.getAIMoveSpeed();
               float f1 = (float)vec3d2.lengthSquared();
               if (f1 <= 0.001F) {
                  float f2 = f * vec2f.x;
                  float f3 = f * vec2f.y;
                  float f4 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F));
                  float f5 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F));
                  vec3d2 = new Vec3d((double)(f2 * f5 - f3 * f4), vec3d2.y, (double)(f3 * f5 + f2 * f4));
                  f1 = (float)vec3d2.lengthSquared();
                  if (f1 <= 0.001F) {
                     return;
                  }
               }

               float f12 = (float)MathHelper.fastInvSqrt((double)f1);
               Vec3d vec3d12 = vec3d2.scale((double)f12);
               Vec3d vec3d13 = this.getForward();
               float f13 = (float)(vec3d13.x * vec3d12.x + vec3d13.z * vec3d12.z);
               if (!(f13 < -0.15F)) {
                  BlockPos blockpos = new BlockPos(this.posX, this.getBoundingBox().maxY, this.posZ);
                  IBlockState iblockstate = this.world.getBlockState(blockpos);
                  if (iblockstate.getCollisionShape(this.world, blockpos).isEmpty()) {
                     blockpos = blockpos.up();
                     IBlockState iblockstate1 = this.world.getBlockState(blockpos);
                     if (iblockstate1.getCollisionShape(this.world, blockpos).isEmpty()) {
                        float f6 = 7.0F;
                        float f7 = 1.2F;
                        if (this.isPotionActive(MobEffects.JUMP_BOOST)) {
                           f7 += (float)(this.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.75F;
                        }

                        float f8 = Math.max(f * 7.0F, 1.0F / f12);
                        Vec3d vec3d4 = vec3d1.add(vec3d12.scale((double)f8));
                        float f9 = this.width;
                        float f10 = this.height;
                        AxisAlignedBB axisalignedbb = (new AxisAlignedBB(vec3d, vec3d4.add(0.0D, (double)f10, 0.0D))).grow((double)f9, 0.0D, (double)f9);
                        Vec3d lvt_19_1_ = vec3d.add(0.0D, (double)0.51F, 0.0D);
                        vec3d4 = vec3d4.add(0.0D, (double)0.51F, 0.0D);
                        Vec3d vec3d5 = vec3d12.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D));
                        Vec3d vec3d6 = vec3d5.scale((double)(f9 * 0.5F));
                        Vec3d vec3d7 = lvt_19_1_.subtract(vec3d6);
                        Vec3d vec3d8 = vec3d4.subtract(vec3d6);
                        Vec3d vec3d9 = lvt_19_1_.add(vec3d6);
                        Vec3d vec3d10 = vec3d4.add(vec3d6);
                        Iterator<AxisAlignedBB> iterator = this.world.func_212388_b(this, axisalignedbb).flatMap((p_212329_0_) -> {
                           return p_212329_0_.toBoundingBoxList().stream();
                        }).iterator();
                        float f11 = Float.MIN_VALUE;

                        while(iterator.hasNext()) {
                           AxisAlignedBB axisalignedbb1 = iterator.next();
                           if (axisalignedbb1.intersects(vec3d7, vec3d8) || axisalignedbb1.intersects(vec3d9, vec3d10)) {
                              f11 = (float)axisalignedbb1.maxY;
                              Vec3d vec3d11 = axisalignedbb1.getCenter();
                              BlockPos blockpos1 = new BlockPos(vec3d11);

                              for(int i = 1; (float)i < f7; ++i) {
                                 BlockPos blockpos2 = blockpos1.up(i);
                                 IBlockState iblockstate2 = this.world.getBlockState(blockpos2);
                                 VoxelShape voxelshape;
                                 if (!(voxelshape = iblockstate2.getCollisionShape(this.world, blockpos2)).isEmpty()) {
                                    f11 = (float)voxelshape.getEnd(EnumFacing.Axis.Y) + (float)blockpos2.getY();
                                    if ((double)f11 - this.getBoundingBox().minY > (double)f7) {
                                       return;
                                    }
                                 }

                                 if (i > 1) {
                                    blockpos = blockpos.up();
                                    IBlockState iblockstate3 = this.world.getBlockState(blockpos);
                                    if (!iblockstate3.getCollisionShape(this.world, blockpos).isEmpty()) {
                                       return;
                                    }
                                 }
                              }
                              break;
                           }
                        }

                        if (f11 != Float.MIN_VALUE) {
                           float f14 = (float)((double)f11 - this.getBoundingBox().minY);
                           if (!(f14 <= 0.5F) && !(f14 > f7)) {
                              this.autoJumpTime = 1;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public float getWaterBrightness() {
      if (!this.areEyesInFluid(FluidTags.WATER)) {
         return 0.0F;
      } else {
         float f = 600.0F;
         float f1 = 100.0F;
         if ((float)this.counterInWater >= 600.0F) {
            return 1.0F;
         } else {
            float f2 = MathHelper.clamp((float)this.counterInWater / 100.0F, 0.0F, 1.0F);
            float f3 = (float)this.counterInWater < 100.0F ? 0.0F : MathHelper.clamp(((float)this.counterInWater - 100.0F) / 500.0F, 0.0F, 1.0F);
            return f2 * 0.6F + f3 * 0.39999998F;
         }
      }
   }

   public boolean canSwim() {
      return this.eyesInWaterPlayer;
   }

   protected boolean updateEyesInWaterPlayer() {
      boolean flag = this.eyesInWaterPlayer;
      boolean flag1 = super.updateEyesInWaterPlayer();
      if (this.isSpectator()) {
         return this.eyesInWaterPlayer;
      } else {
         if (!flag && flag1) {
            this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
            this.mc.getSoundHandler().play(new UnderwaterAmbientSounds.UnderWaterSound(this));
         }

         if (flag && !flag1) {
            this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
         }

         return this.eyesInWaterPlayer;
      }
   }

   public void updateSyncFields(EntityPlayerSP old) {
       this.lastReportedPosX = old.lastReportedPosX;
       this.lastReportedPosY = old.lastReportedPosY;
       this.lastReportedPosZ = old.lastReportedPosZ;
       this.lastReportedYaw = old.lastReportedYaw;
       this.lastReportedPitch = old.lastReportedPitch;
       this.prevOnGround = old.prevOnGround;
       this.serverSneakState = old.serverSneakState;
       this.serverSprintState = old.serverSprintState;
       this.positionUpdateTicks = old.positionUpdateTicks;
   }
}