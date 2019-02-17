package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ServerRecipeBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerLook;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.CooldownTrackerServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.GameType;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.loot.ILootContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityPlayerMP extends EntityPlayer implements IContainerListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private String language = "en_US";
   /** The NetServerHandler assigned to this player by the ServerConfigurationManager. */
   public NetHandlerPlayServer connection;
   /** Reference to the MinecraftServer object. */
   public final MinecraftServer server;
   /** The player interaction manager for this player */
   public final PlayerInteractionManager interactionManager;
   /** player X position as seen by PlayerManager */
   public double managedPosX;
   /** player Z position as seen by PlayerManager */
   public double managedPosZ;
   /**
    * This is a queue that contains the entity IDs of entties that need to be removed on the client. Adding an entity ID
    * to this queue will cause a SPacketDestroyEntities to be sent to the client.
    */
   private final List<Integer> entityRemoveQueue = Lists.newLinkedList();
   private final PlayerAdvancements advancements;
   private final StatisticsManagerServer stats;
   /** the total health of the player, includes actual health and absorption health. Updated every tick. */
   private float lastHealthScore = Float.MIN_VALUE;
   private int lastFoodScore = Integer.MIN_VALUE;
   private int lastAirScore = Integer.MIN_VALUE;
   private int lastArmorScore = Integer.MIN_VALUE;
   private int lastLevelScore = Integer.MIN_VALUE;
   private int lastExperienceScore = Integer.MIN_VALUE;
   /** amount of health the client was last set to */
   private float lastHealth = -1.0E8F;
   /** set to foodStats.GetFoodLevel */
   private int lastFoodLevel = -99999999;
   /** set to foodStats.getSaturationLevel() == 0.0F each tick */
   private boolean wasHungry = true;
   /** Amount of experience the client was last set to */
   private int lastExperience = -99999999;
   private int respawnInvulnerabilityTicks = 60;
   private EntityPlayer.EnumChatVisibility chatVisibility;
   private boolean chatColours = true;
   private long playerLastActiveTime = Util.milliTime();
   /** The entity the player is currently spectating through. */
   private Entity spectatingEntity;
   private boolean invulnerableDimensionChange;
   private boolean seenCredits;
   private final ServerRecipeBook recipeBook;
   /** The position this player started levitating at. */
   private Vec3d levitationStartPos;
   /** The value of ticksExisted when this player started levitating. */
   private int levitatingSince;
   private boolean disconnected;
   private Vec3d enteredNetherPosition;
   /** The currently in use window ID. Incremented every time a window is opened. */
   public int currentWindowId;
   /**
    * set to true when player is moving quantity of items from one inventory to another(crafting) but item in either
    * slot is not changed
    */
   public boolean isChangingQuantityOnly;
   public int ping;
   /** True when the player has left the End using an the exit portal, but has not yet been respawned in the overworld */
   public boolean queuedEndExit;

   public EntityPlayerMP(MinecraftServer server, WorldServer worldIn, GameProfile profile, PlayerInteractionManager interactionManagerIn) {
      super(worldIn, profile);
      interactionManagerIn.player = this;
      this.interactionManager = interactionManagerIn;
      this.server = server;
      this.recipeBook = new ServerRecipeBook(server.getRecipeManager());
      this.stats = server.getPlayerList().getPlayerStats(this);
      this.advancements = server.getPlayerList().getPlayerAdvancements(this);
      this.stepHeight = 1.0F;
      this.func_205734_a(worldIn);
   }

   private void func_205734_a(WorldServer p_205734_1_) {
      BlockPos blockpos = p_205734_1_.getSpawnPoint();
      if (p_205734_1_.dimension.hasSkyLight() && p_205734_1_.getWorldInfo().getGameType() != GameType.ADVENTURE) {
         int i = Math.max(0, this.server.getSpawnRadius(p_205734_1_));
         int j = MathHelper.floor(p_205734_1_.getWorldBorder().getClosestDistance((double)blockpos.getX(), (double)blockpos.getZ()));
         if (j < i) {
            i = j;
         }

         if (j <= 1) {
            i = 1;
         }

         int k = (i * 2 + 1) * (i * 2 + 1);
         int l = this.func_205735_q(k);
         int i1 = (new Random()).nextInt(k);

         for(int j1 = 0; j1 < k; ++j1) {
            int k1 = (i1 + l * j1) % k;
            int l1 = k1 % (i * 2 + 1);
            int i2 = k1 / (i * 2 + 1);
            BlockPos blockpos1 = p_205734_1_.getDimension().findSpawn(blockpos.getX() + l1 - i, blockpos.getZ() + i2 - i, false);
            if (blockpos1 != null) {
               this.moveToBlockPosAndAngles(blockpos1, 0.0F, 0.0F);
               if (p_205734_1_.isCollisionBoxesEmpty(this, this.getBoundingBox())) {
                  break;
               }
            }
         }
      } else {
         this.moveToBlockPosAndAngles(blockpos, 0.0F, 0.0F);

         while(!p_205734_1_.isCollisionBoxesEmpty(this, this.getBoundingBox()) && this.posY < 255.0D) {
            this.setPosition(this.posX, this.posY + 1.0D, this.posZ);
         }
      }

   }

   private int func_205735_q(int p_205735_1_) {
      return p_205735_1_ <= 16 ? p_205735_1_ - 1 : 17;
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      if (compound.contains("playerGameType", 99)) {
         if (this.getServer().getForceGamemode()) {
            this.interactionManager.setGameType(this.getServer().getGameType());
         } else {
            this.interactionManager.setGameType(GameType.getByID(compound.getInt("playerGameType")));
         }
      }

      if (compound.contains("enteredNetherPosition", 10)) {
         NBTTagCompound nbttagcompound = compound.getCompound("enteredNetherPosition");
         this.enteredNetherPosition = new Vec3d(nbttagcompound.getDouble("x"), nbttagcompound.getDouble("y"), nbttagcompound.getDouble("z"));
      }

      this.seenCredits = compound.getBoolean("seenCredits");
      if (compound.contains("recipeBook", 10)) {
         this.recipeBook.read(compound.getCompound("recipeBook"));
      }

   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.setInt("playerGameType", this.interactionManager.getGameType().getID());
      compound.setBoolean("seenCredits", this.seenCredits);
      if (this.enteredNetherPosition != null) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setDouble("x", this.enteredNetherPosition.x);
         nbttagcompound.setDouble("y", this.enteredNetherPosition.y);
         nbttagcompound.setDouble("z", this.enteredNetherPosition.z);
         compound.setTag("enteredNetherPosition", nbttagcompound);
      }

      Entity entity1 = this.getLowestRidingEntity();
      Entity entity = this.getRidingEntity();
      if (entity != null && entity1 != this && entity1.isOnePlayerRiding()) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         NBTTagCompound nbttagcompound2 = new NBTTagCompound();
         entity1.writeUnlessPassenger(nbttagcompound2);
         nbttagcompound1.setUniqueId("Attach", entity.getUniqueID());
         nbttagcompound1.setTag("Entity", nbttagcompound2);
         compound.setTag("RootVehicle", nbttagcompound1);
      }

      compound.setTag("recipeBook", this.recipeBook.write());
   }

   public void func_195394_a(int p_195394_1_) {
      float f = (float)this.xpBarCap();
      float f1 = (f - 1.0F) / f;
      this.experience = MathHelper.clamp((float)p_195394_1_ / f, 0.0F, f1);
      this.lastExperience = -1;
   }

   public void func_195399_b(int p_195399_1_) {
      this.experienceLevel = p_195399_1_;
      this.lastExperience = -1;
   }

   /**
    * Add experience levels to this player.
    */
   public void addExperienceLevel(int levels) {
      super.addExperienceLevel(levels);
      this.lastExperience = -1;
   }

   public void onEnchant(ItemStack enchantedItem, int cost) {
      super.onEnchant(enchantedItem, cost);
      this.lastExperience = -1;
   }

   public void addSelfToInternalCraftingInventory() {
      this.openContainer.addListener(this);
   }

   /**
    * Sends an ENTER_COMBAT packet to the client
    */
   public void sendEnterCombat() {
      super.sendEnterCombat();
      this.connection.sendPacket(new SPacketCombatEvent(this.getCombatTracker(), SPacketCombatEvent.Event.ENTER_COMBAT));
   }

   /**
    * Sends an END_COMBAT packet to the client
    */
   public void sendEndCombat() {
      super.sendEndCombat();
      this.connection.sendPacket(new SPacketCombatEvent(this.getCombatTracker(), SPacketCombatEvent.Event.END_COMBAT));
   }

   protected void onInsideBlock(IBlockState p_191955_1_) {
      CriteriaTriggers.ENTER_BLOCK.trigger(this, p_191955_1_);
   }

   protected CooldownTracker createCooldownTracker() {
      return new CooldownTrackerServer(this);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      this.interactionManager.tick();
      --this.respawnInvulnerabilityTicks;
      if (this.hurtResistantTime > 0) {
         --this.hurtResistantTime;
      }

      this.openContainer.detectAndSendChanges();
      if (!this.world.isRemote && !this.openContainer.canInteractWith(this)) {
         this.closeScreen();
         this.openContainer = this.inventoryContainer;
      }

      while(!this.entityRemoveQueue.isEmpty()) {
         int i = Math.min(this.entityRemoveQueue.size(), Integer.MAX_VALUE);
         int[] aint = new int[i];
         Iterator<Integer> iterator = this.entityRemoveQueue.iterator();
         int j = 0;

         while(iterator.hasNext() && j < i) {
            aint[j++] = iterator.next();
            iterator.remove();
         }

         this.connection.sendPacket(new SPacketDestroyEntities(aint));
      }

      Entity entity = this.getSpectatingEntity();
      if (entity != this) {
         if (entity.isAlive()) {
            this.setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
            this.server.getPlayerList().serverUpdateMovingPlayer(this);
            if (this.isSneaking()) {
               this.setSpectatingEntity(this);
            }
         } else {
            this.setSpectatingEntity(this);
         }
      }

      CriteriaTriggers.TICK.trigger(this);
      if (this.levitationStartPos != null) {
         CriteriaTriggers.LEVITATION.trigger(this, this.levitationStartPos, this.ticksExisted - this.levitatingSince);
      }

      this.advancements.flushDirty(this);
   }

   public void playerTick() {
      try {
         super.tick();

         for(int i = 0; i < this.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (itemstack.getItem().isComplex()) {
               Packet<?> packet = ((ItemMapBase)itemstack.getItem()).getUpdatePacket(itemstack, this.world, this);
               if (packet != null) {
                  this.connection.sendPacket(packet);
               }
            }
         }

         if (this.getHealth() != this.lastHealth || this.lastFoodLevel != this.foodStats.getFoodLevel() || this.foodStats.getSaturationLevel() == 0.0F != this.wasHungry) {
            this.connection.sendPacket(new SPacketUpdateHealth(this.getHealth(), this.foodStats.getFoodLevel(), this.foodStats.getSaturationLevel()));
            this.lastHealth = this.getHealth();
            this.lastFoodLevel = this.foodStats.getFoodLevel();
            this.wasHungry = this.foodStats.getSaturationLevel() == 0.0F;
         }

         if (this.getHealth() + this.getAbsorptionAmount() != this.lastHealthScore) {
            this.lastHealthScore = this.getHealth() + this.getAbsorptionAmount();
            this.updateScorePoints(ScoreCriteria.HEALTH, MathHelper.ceil(this.lastHealthScore));
         }

         if (this.foodStats.getFoodLevel() != this.lastFoodScore) {
            this.lastFoodScore = this.foodStats.getFoodLevel();
            this.updateScorePoints(ScoreCriteria.FOOD, MathHelper.ceil((float)this.lastFoodScore));
         }

         if (this.getAir() != this.lastAirScore) {
            this.lastAirScore = this.getAir();
            this.updateScorePoints(ScoreCriteria.AIR, MathHelper.ceil((float)this.lastAirScore));
         }

         if (this.getTotalArmorValue() != this.lastArmorScore) {
            this.lastArmorScore = this.getTotalArmorValue();
            this.updateScorePoints(ScoreCriteria.ARMOR, MathHelper.ceil((float)this.lastArmorScore));
         }

         if (this.experienceTotal != this.lastExperienceScore) {
            this.lastExperienceScore = this.experienceTotal;
            this.updateScorePoints(ScoreCriteria.XP, MathHelper.ceil((float)this.lastExperienceScore));
         }

         if (this.experienceLevel != this.lastLevelScore) {
            this.lastLevelScore = this.experienceLevel;
            this.updateScorePoints(ScoreCriteria.LEVEL, MathHelper.ceil((float)this.lastLevelScore));
         }

         if (this.experienceTotal != this.lastExperience) {
            this.lastExperience = this.experienceTotal;
            this.connection.sendPacket(new SPacketSetExperience(this.experience, this.experienceTotal, this.experienceLevel));
         }

         if (this.ticksExisted % 20 == 0) {
            CriteriaTriggers.LOCATION.trigger(this);
         }

      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking player");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Player being ticked");
         this.fillCrashReport(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   private void updateScorePoints(ScoreCriteria criteria, int points) {
      this.getWorldScoreboard().forAllObjectives(criteria, this.getScoreboardName(), (p_195397_1_) -> {
         p_195397_1_.setScorePoints(points);
      });
   }

   /**
    * Called when the mob's health reaches 0.
    */
   public void onDeath(DamageSource cause) {
      if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this,  cause)) return;
      boolean flag = this.world.getGameRules().getBoolean("showDeathMessages");
      if (flag) {
         ITextComponent itextcomponent = this.getCombatTracker().getDeathMessage();
         this.connection.sendPacket(new SPacketCombatEvent(this.getCombatTracker(), SPacketCombatEvent.Event.ENTITY_DIED, itextcomponent), (p_212356_2_) -> {
            if (!p_212356_2_.isSuccess()) {
               int i = 256;
               String s = itextcomponent.func_212636_a(256);
               ITextComponent itextcomponent1 = new TextComponentTranslation("death.attack.message_too_long", (new TextComponentString(s)).applyTextStyle(TextFormatting.YELLOW));
               ITextComponent itextcomponent2 = (new TextComponentTranslation("death.attack.even_more_magic", this.getDisplayName())).applyTextStyle((p_212357_1_) -> {
                  p_212357_1_.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, itextcomponent1));
               });
               this.connection.sendPacket(new SPacketCombatEvent(this.getCombatTracker(), SPacketCombatEvent.Event.ENTITY_DIED, itextcomponent2));
            }

         });
         Team team = this.getTeam();
         if (team != null && team.getDeathMessageVisibility() != Team.EnumVisible.ALWAYS) {
            if (team.getDeathMessageVisibility() == Team.EnumVisible.HIDE_FOR_OTHER_TEAMS) {
               this.server.getPlayerList().sendMessageToAllTeamMembers(this, itextcomponent);
            } else if (team.getDeathMessageVisibility() == Team.EnumVisible.HIDE_FOR_OWN_TEAM) {
               this.server.getPlayerList().sendMessageToTeamOrAllPlayers(this, itextcomponent);
            }
         } else {
            this.server.getPlayerList().sendMessage(itextcomponent);
         }
      } else {
         this.connection.sendPacket(new SPacketCombatEvent(this.getCombatTracker(), SPacketCombatEvent.Event.ENTITY_DIED));
      }

      this.spawnShoulderEntities();
      if (!this.world.getGameRules().getBoolean("keepInventory") && !this.isSpectator()) {
         this.captureDrops(new java.util.ArrayList<>());
         this.destroyVanishingCursedItems();
         this.inventory.dropAllItems();
         if (!world.isRemote) net.minecraftforge.event.ForgeEventFactory.onPlayerDrops(this, cause, captureDrops(null), recentlyHit > 0);
      }

      this.getWorldScoreboard().forAllObjectives(ScoreCriteria.DEATH_COUNT, this.getScoreboardName(), Score::incrementScore);
      EntityLivingBase entitylivingbase = this.getAttackingEntity();
      if (entitylivingbase != null) {
         this.addStat(StatList.ENTITY_KILLED_BY.get(entitylivingbase.getType()));
         entitylivingbase.awardKillScore(this, this.scoreValue, cause);
      }

      this.addStat(StatList.DEATHS);
      this.takeStat(StatList.CUSTOM.get(StatList.TIME_SINCE_DEATH));
      this.takeStat(StatList.CUSTOM.get(StatList.TIME_SINCE_REST));
      this.extinguish();
      this.setFlag(0, false);
      this.getCombatTracker().reset();
   }

   public void awardKillScore(Entity p_191956_1_, int p_191956_2_, DamageSource p_191956_3_) {
      if (p_191956_1_ != this) {
         super.awardKillScore(p_191956_1_, p_191956_2_, p_191956_3_);
         this.addScore(p_191956_2_);
         String s = this.getScoreboardName();
         String s1 = p_191956_1_.getScoreboardName();
         this.getWorldScoreboard().forAllObjectives(ScoreCriteria.TOTAL_KILL_COUNT, s, Score::incrementScore);
         if (p_191956_1_ instanceof EntityPlayer) {
            this.addStat(StatList.PLAYER_KILLS);
            this.getWorldScoreboard().forAllObjectives(ScoreCriteria.PLAYER_KILL_COUNT, s, Score::incrementScore);
         } else {
            this.addStat(StatList.MOB_KILLS);
         }

         this.handleTeamKill(s, s1, ScoreCriteria.TEAM_KILL);
         this.handleTeamKill(s1, s, ScoreCriteria.KILLED_BY_TEAM);
         CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(this, p_191956_1_, p_191956_3_);
      }
   }

   private void handleTeamKill(String p_195398_1_, String p_195398_2_, ScoreCriteria[] p_195398_3_) {
      ScorePlayerTeam scoreplayerteam = this.getWorldScoreboard().getPlayersTeam(p_195398_2_);
      if (scoreplayerteam != null) {
         int i = scoreplayerteam.getColor().getColorIndex();
         if (i >= 0 && i < p_195398_3_.length) {
            this.getWorldScoreboard().forAllObjectives(p_195398_3_[i], p_195398_1_, Score::incrementScore);
         }
      }

   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         boolean flag = this.server.isDedicatedServer() && this.canPlayersAttack() && "fall".equals(source.damageType);
         if (!flag && this.respawnInvulnerabilityTicks > 0 && source != DamageSource.OUT_OF_WORLD) {
            return false;
         } else {
            if (source instanceof EntityDamageSource) {
               Entity entity = source.getTrueSource();
               if (entity instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)entity)) {
                  return false;
               }

               if (entity instanceof EntityArrow) {
                  EntityArrow entityarrow = (EntityArrow)entity;
                  Entity entity1 = entityarrow.func_212360_k();
                  if (entity1 instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)entity1)) {
                     return false;
                  }
               }
            }

            return super.attackEntityFrom(source, amount);
         }
      }
   }

   public boolean canAttackPlayer(EntityPlayer other) {
      return !this.canPlayersAttack() ? false : super.canAttackPlayer(other);
   }

   /**
    * Returns if other players can attack this player
    */
   private boolean canPlayersAttack() {
      return this.server.isPVPEnabled();
   }

   @Override
   @Nullable
   public Entity changeDimension(DimensionType p_212321_1_, net.minecraftforge.common.util.ITeleporter teleporter) {
      if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, p_212321_1_)) return null;
      this.invulnerableDimensionChange = true;
      if (this.dimension == DimensionType.OVERWORLD && p_212321_1_ == DimensionType.NETHER) {
         this.enteredNetherPosition = new Vec3d(this.posX, this.posY, this.posZ);
      } else if (this.dimension != DimensionType.NETHER && p_212321_1_ != DimensionType.OVERWORLD) {
         this.enteredNetherPosition = null;
      }

      if (this.dimension == DimensionType.THE_END && p_212321_1_ == DimensionType.THE_END && teleporter.isVanilla()) {
         this.world.removeEntity(this);
         if (!this.queuedEndExit) {
            this.queuedEndExit = true;
            this.connection.sendPacket(new SPacketChangeGameState(4, this.seenCredits ? 0.0F : 1.0F));
            this.seenCredits = true;
         }

         return this;
      } else {
         if (this.dimension == DimensionType.OVERWORLD && p_212321_1_ == DimensionType.THE_END) {
            p_212321_1_ = DimensionType.THE_END;
         }

         this.server.getPlayerList().changePlayerDimension(this, p_212321_1_, teleporter);
         this.connection.sendPacket(new SPacketEffect(1032, BlockPos.ORIGIN, 0, false));
         this.lastExperience = -1;
         this.lastHealth = -1.0F;
         this.lastFoodLevel = -1;
         return this;
      }
   }

   public boolean isSpectatedByPlayer(EntityPlayerMP player) {
      if (player.isSpectator()) {
         return this.getSpectatingEntity() == this;
      } else {
         return this.isSpectator() ? false : super.isSpectatedByPlayer(player);
      }
   }

   private void sendTileEntityUpdate(TileEntity p_147097_1_) {
      if (p_147097_1_ != null) {
         SPacketUpdateTileEntity spacketupdatetileentity = p_147097_1_.getUpdatePacket();
         if (spacketupdatetileentity != null) {
            this.connection.sendPacket(spacketupdatetileentity);
         }
      }

   }

   /**
    * Called when the entity picks up an item.
    */
   public void onItemPickup(Entity entityIn, int quantity) {
      super.onItemPickup(entityIn, quantity);
      this.openContainer.detectAndSendChanges();
   }

   public EntityPlayer.SleepResult trySleep(BlockPos bedLocation) {
      EntityPlayer.SleepResult entityplayer$sleepresult = super.trySleep(bedLocation);
      if (entityplayer$sleepresult == EntityPlayer.SleepResult.OK) {
         this.addStat(StatList.SLEEP_IN_BED);
         Packet<?> packet = new SPacketUseBed(this, bedLocation);
         this.getServerWorld().getEntityTracker().sendToTracking(this, packet);
         this.connection.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         this.connection.sendPacket(packet);
         CriteriaTriggers.SLEPT_IN_BED.trigger(this);
      }

      return entityplayer$sleepresult;
   }

   /**
    * Wake up the player if they're sleeping.
    */
   public void wakeUpPlayer(boolean immediately, boolean updateWorldFlag, boolean setSpawn) {
      if (this.isPlayerSleeping()) {
         this.getServerWorld().getEntityTracker().sendToTrackingAndSelf(this, new SPacketAnimation(this, 2));
      }

      super.wakeUpPlayer(immediately, updateWorldFlag, setSpawn);
      if (this.connection != null) {
         this.connection.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
      }

   }

   public boolean startRiding(Entity entityIn, boolean force) {
      Entity entity = this.getRidingEntity();
      if (!super.startRiding(entityIn, force)) {
         return false;
      } else {
         Entity entity1 = this.getRidingEntity();
         if (entity1 != entity && this.connection != null) {
            this.connection.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         }

         return true;
      }
   }

   /**
    * Dismounts this entity from the entity it is riding.
    */
   public void stopRiding() {
      Entity entity = this.getRidingEntity();
      super.stopRiding();
      Entity entity1 = this.getRidingEntity();
      if (entity1 != entity && this.connection != null) {
         this.connection.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
      }

   }

   /**
    * Returns whether this Entity is invulnerable to the given DamageSource.
    */
   public boolean isInvulnerableTo(DamageSource source) {
      return super.isInvulnerableTo(source) || this.isInvulnerableDimensionChange();
   }

   protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
   }

   protected void frostWalk(BlockPos pos) {
      if (!this.isSpectator()) {
         super.frostWalk(pos);
      }

   }

   /**
    * process player falling based on movement packet
    */
   public void handleFalling(double y, boolean onGroundIn) {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.posY - (double)0.2F);
      int k = MathHelper.floor(this.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      IBlockState iblockstate = this.world.getBlockState(blockpos);
      if (iblockstate.isAir(world, blockpos)) {
         BlockPos blockpos1 = blockpos.down();
         IBlockState iblockstate1 = this.world.getBlockState(blockpos1);
         Block block = iblockstate1.getBlock();
         if (block instanceof BlockFence || block instanceof BlockWall || block instanceof BlockFenceGate) {
            blockpos = blockpos1;
            iblockstate = iblockstate1;
         }
      }

      super.updateFallState(y, onGroundIn, iblockstate, blockpos);
   }

   public void openSignEditor(TileEntitySign signTile) {
      signTile.setPlayer(this);
      this.connection.sendPacket(new SPacketSignEditorOpen(signTile.getPos()));
   }

   /**
    * get the next window id to use
    */
   public void getNextWindowId() {
      this.currentWindowId = this.currentWindowId % 100 + 1;
   }

   public void displayGui(IInteractionObject guiOwner) {
      if (guiOwner instanceof ILootContainer && ((ILootContainer)guiOwner).getLootTable() != null && this.isSpectator()) {
         this.sendStatusMessage((new TextComponentTranslation("container.spectatorCantOpen")).applyTextStyle(TextFormatting.RED), true);
      } else {
         this.getNextWindowId();
         this.connection.sendPacket(new SPacketOpenWindow(this.currentWindowId, guiOwner.getGuiID(), guiOwner.getDisplayName()));
         this.openContainer = guiOwner.createContainer(this.inventory, this);
         this.openContainer.windowId = this.currentWindowId;
         this.openContainer.addListener(this);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(this, this.openContainer));
      }
   }

   /**
    * Displays the GUI for interacting with a chest inventory.
    */
   public void displayGUIChest(IInventory chestInventory) {
      if (chestInventory instanceof ILootContainer && ((ILootContainer)chestInventory).getLootTable() != null && this.isSpectator()) {
         this.sendStatusMessage((new TextComponentTranslation("container.spectatorCantOpen")).applyTextStyle(TextFormatting.RED), true);
      } else {
         if (this.openContainer != this.inventoryContainer) {
            this.closeScreen();
         }

         if (chestInventory instanceof ILockableContainer) {
            ILockableContainer ilockablecontainer = (ILockableContainer)chestInventory;
            if (ilockablecontainer.isLocked() && !this.canOpen(ilockablecontainer.getLockCode()) && !this.isSpectator()) {
               this.connection.sendPacket(new SPacketChat(new TextComponentTranslation("container.isLocked", chestInventory.getDisplayName()), ChatType.GAME_INFO));
               this.connection.sendPacket(new SPacketSoundEffect(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, this.posX, this.posY, this.posZ, 1.0F, 1.0F));
               return;
            }
         }

         this.getNextWindowId();
         if (chestInventory instanceof IInteractionObject) {
            this.connection.sendPacket(new SPacketOpenWindow(this.currentWindowId, ((IInteractionObject)chestInventory).getGuiID(), chestInventory.getDisplayName(), chestInventory.getSizeInventory()));
            this.openContainer = ((IInteractionObject)chestInventory).createContainer(this.inventory, this);
         } else {
            this.connection.sendPacket(new SPacketOpenWindow(this.currentWindowId, "minecraft:container", chestInventory.getDisplayName(), chestInventory.getSizeInventory()));
            this.openContainer = new ContainerChest(this.inventory, chestInventory, this);
         }

         this.openContainer.windowId = this.currentWindowId;
         this.openContainer.addListener(this);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(this, this.openContainer));
      }
   }

   public void displayVillagerTradeGui(IMerchant villager) {
      this.getNextWindowId();
      this.openContainer = new ContainerMerchant(this.inventory, villager, this.world);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addListener(this);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(this, this.openContainer));
      IInventory iinventory = ((ContainerMerchant)this.openContainer).getMerchantInventory();
      ITextComponent itextcomponent = villager.getDisplayName();
      this.connection.sendPacket(new SPacketOpenWindow(this.currentWindowId, "minecraft:villager", itextcomponent, iinventory.getSizeInventory()));
      MerchantRecipeList merchantrecipelist = villager.getRecipes(this);
      if (merchantrecipelist != null) {
         PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
         packetbuffer.writeInt(this.currentWindowId);
         merchantrecipelist.writeToBuf(packetbuffer);
         this.connection.sendPacket(new SPacketCustomPayload(SPacketCustomPayload.TRADER_LIST, packetbuffer));
      }

   }

   public void openHorseInventory(AbstractHorse horse, IInventory inventoryIn) {
      if (this.openContainer != this.inventoryContainer) {
         this.closeScreen();
      }

      this.getNextWindowId();
      this.connection.sendPacket(new SPacketOpenWindow(this.currentWindowId, "EntityHorse", inventoryIn.getDisplayName(), inventoryIn.getSizeInventory(), horse.getEntityId()));
      this.openContainer = new ContainerHorseInventory(this.inventory, inventoryIn, horse, this);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addListener(this);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(this, this.openContainer));
   }

   public void openBook(ItemStack stack, EnumHand hand) {
      Item item = stack.getItem();
      if (item == Items.WRITTEN_BOOK) {
         PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
         packetbuffer.writeEnumValue(hand);
         this.connection.sendPacket(new SPacketCustomPayload(SPacketCustomPayload.BOOK_OPEN, packetbuffer));
      }

   }

   public void openCommandBlock(TileEntityCommandBlock commandBlock) {
      commandBlock.setSendToClient(true);
      this.sendTileEntityUpdate(commandBlock);
   }

   /**
    * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
    * contents of that slot.
    */
   public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
      if (!(containerToSend.getSlot(slotInd) instanceof SlotCrafting)) {
         if (containerToSend == this.inventoryContainer) {
            CriteriaTriggers.INVENTORY_CHANGED.trigger(this, this.inventory);
         }

         if (!this.isChangingQuantityOnly) {
            this.connection.sendPacket(new SPacketSetSlot(containerToSend.windowId, slotInd, stack));
         }
      }
   }

   public void sendContainerToPlayer(Container containerIn) {
      this.sendAllContents(containerIn, containerIn.getInventory());
   }

   /**
    * update the crafting window inventory with the items in the list
    */
   public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
      this.connection.sendPacket(new SPacketWindowItems(containerToSend.windowId, itemsList));
      this.connection.sendPacket(new SPacketSetSlot(-1, -1, this.inventory.getItemStack()));
   }

   /**
    * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing progress,
    * and enchanting level. Normally the first int identifies which variable to update, and the second contains the new
    * value. Both are truncated to shorts in non-local SMP.
    */
   public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
      this.connection.sendPacket(new SPacketWindowProperty(containerIn.windowId, varToUpdate, newValue));
   }

   public void sendAllWindowProperties(Container containerIn, IInventory inventory) {
      for(int i = 0; i < inventory.getFieldCount(); ++i) {
         this.connection.sendPacket(new SPacketWindowProperty(containerIn.windowId, i, inventory.getField(i)));
      }

   }

   /**
    * set current crafting inventory back to the 2x2 square
    */
   public void closeScreen() {
      this.connection.sendPacket(new SPacketCloseWindow(this.openContainer.windowId));
      this.closeContainer();
   }

   /**
    * updates item held by mouse
    */
   public void updateHeldItem() {
      if (!this.isChangingQuantityOnly) {
         this.connection.sendPacket(new SPacketSetSlot(-1, -1, this.inventory.getItemStack()));
      }
   }

   /**
    * Closes the container the player currently has open.
    */
   public void closeContainer() {
      this.openContainer.onContainerClosed(this);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Close(this, this.openContainer));
      this.openContainer = this.inventoryContainer;
   }

   public void setEntityActionState(float strafe, float forward, boolean jumping, boolean sneaking) {
      if (this.isPassenger()) {
         if (strafe >= -1.0F && strafe <= 1.0F) {
            this.moveStrafing = strafe;
         }

         if (forward >= -1.0F && forward <= 1.0F) {
            this.moveForward = forward;
         }

         this.isJumping = jumping;
         this.setSneaking(sneaking);
      }

   }

   /**
    * Adds a value to a statistic field.
    */
   public void addStat(Stat<?> stat, int amount) {
      this.stats.increment(this, stat, amount);
      this.getWorldScoreboard().forAllObjectives(stat, this.getScoreboardName(), (p_195396_1_) -> {
         p_195396_1_.increaseScore(amount);
      });
   }

   public void takeStat(Stat<?> stat) {
      this.stats.setValue(this, stat, 0);
      this.getWorldScoreboard().forAllObjectives(stat, this.getScoreboardName(), Score::reset);
   }

   public int unlockRecipes(Collection<IRecipe> p_195065_1_) {
      return this.recipeBook.add(p_195065_1_, this);
   }

   public void unlockRecipes(ResourceLocation[] p_193102_1_) {
      List<IRecipe> list = Lists.newArrayList();

      for(ResourceLocation resourcelocation : p_193102_1_) {
         IRecipe irecipe = this.server.getRecipeManager().getRecipe(resourcelocation);
         if (irecipe != null) {
            list.add(irecipe);
         }
      }

      this.unlockRecipes(list);
   }

   public int resetRecipes(Collection<IRecipe> p_195069_1_) {
      return this.recipeBook.remove(p_195069_1_, this);
   }

   public void giveExperiencePoints(int p_195068_1_) {
      super.giveExperiencePoints(p_195068_1_);
      this.lastExperience = -1;
   }

   public void disconnect() {
      this.disconnected = true;
      this.removePassengers();
      if (this.sleeping) {
         this.wakeUpPlayer(true, false, false);
      }

   }

   public boolean hasDisconnected() {
      return this.disconnected;
   }

   /**
    * this function is called when a players inventory is sent to him, lastHealth is updated on any dimension
    * transitions, then reset.
    */
   public void setPlayerHealthUpdated() {
      this.lastHealth = -1.0E8F;
   }

   public void sendStatusMessage(ITextComponent chatComponent, boolean actionBar) {
      this.connection.sendPacket(new SPacketChat(chatComponent, actionBar ? ChatType.GAME_INFO : ChatType.CHAT));
   }

   /**
    * Used for when item use count runs out, ie: eating completed
    */
   protected void onItemUseFinish() {
      if (!this.activeItemStack.isEmpty() && this.isHandActive()) {
         this.connection.sendPacket(new SPacketEntityStatus(this, (byte)9));
         super.onItemUseFinish();
      }

   }

   public void lookAt(EntityAnchorArgument.Type p_200602_1_, Vec3d p_200602_2_) {
      super.lookAt(p_200602_1_, p_200602_2_);
      this.connection.sendPacket(new SPacketPlayerLook(p_200602_1_, p_200602_2_.x, p_200602_2_.y, p_200602_2_.z));
   }

   public void lookAt(EntityAnchorArgument.Type p_200618_1_, Entity p_200618_2_, EntityAnchorArgument.Type p_200618_3_) {
      Vec3d vec3d = p_200618_3_.apply(p_200618_2_);
      super.lookAt(p_200618_1_, vec3d);
      this.connection.sendPacket(new SPacketPlayerLook(p_200618_1_, p_200618_2_, p_200618_3_));
   }

   public void copyFrom(EntityPlayerMP that, boolean keepEverything) {
      if (keepEverything) {
         this.inventory.copyInventory(that.inventory);
         this.setHealth(that.getHealth());
         this.foodStats = that.foodStats;
         this.experienceLevel = that.experienceLevel;
         this.experienceTotal = that.experienceTotal;
         this.experience = that.experience;
         this.setScore(that.getScore());
         this.lastPortalPos = that.lastPortalPos;
         this.lastPortalVec = that.lastPortalVec;
         this.teleportDirection = that.teleportDirection;
      } else if (this.world.getGameRules().getBoolean("keepInventory") || that.isSpectator()) {
         this.inventory.copyInventory(that.inventory);
         this.experienceLevel = that.experienceLevel;
         this.experienceTotal = that.experienceTotal;
         this.experience = that.experience;
         this.setScore(that.getScore());
      }

      this.xpSeed = that.xpSeed;
      this.enderChest = that.enderChest;
      this.getDataManager().set(PLAYER_MODEL_FLAG, that.getDataManager().get(PLAYER_MODEL_FLAG));
      this.lastExperience = -1;
      this.lastHealth = -1.0F;
      this.lastFoodLevel = -1;
      this.recipeBook.copyFrom(that.recipeBook);
      this.entityRemoveQueue.addAll(that.entityRemoveQueue);
      this.seenCredits = that.seenCredits;
      this.enteredNetherPosition = that.enteredNetherPosition;
      this.setLeftShoulderEntity(that.getLeftShoulderEntity());
      this.setRightShoulderEntity(that.getRightShoulderEntity());

      this.spawnPosMap = that.spawnPosMap;
      this.spawnForcedMap = that.spawnForcedMap;
      if(that.dimension != net.minecraft.world.dimension.DimensionType.OVERWORLD) {
          this.spawnPos = that.spawnPos;
          this.spawnForced = that.spawnForced;
      }

      //Copy over a section of the Entity Data from the old player.
      //Allows mods to specify data that persists after players respawn.
      NBTTagCompound old = that.getEntityData();
      if (old.hasKey(PERSISTED_NBT_TAG))
          getEntityData().setTag(PERSISTED_NBT_TAG, old.getTag(PERSISTED_NBT_TAG));
      net.minecraftforge.event.ForgeEventFactory.onPlayerClone(this, that, !keepEverything);
   }

   protected void onNewPotionEffect(PotionEffect id) {
      super.onNewPotionEffect(id);
      this.connection.sendPacket(new SPacketEntityEffect(this.getEntityId(), id));
      if (id.getPotion() == MobEffects.LEVITATION) {
         this.levitatingSince = this.ticksExisted;
         this.levitationStartPos = new Vec3d(this.posX, this.posY, this.posZ);
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onChangedPotionEffect(PotionEffect id, boolean p_70695_2_) {
      super.onChangedPotionEffect(id, p_70695_2_);
      this.connection.sendPacket(new SPacketEntityEffect(this.getEntityId(), id));
      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onFinishedPotionEffect(PotionEffect effect) {
      super.onFinishedPotionEffect(effect);
      this.connection.sendPacket(new SPacketRemoveEntityEffect(this.getEntityId(), effect.getPotion()));
      if (effect.getPotion() == MobEffects.LEVITATION) {
         this.levitationStartPos = null;
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   /**
    * Sets the position of the entity and updates the 'last' variables
    */
   public void setPositionAndUpdate(double x, double y, double z) {
      this.connection.setPlayerLocation(x, y, z, this.rotationYaw, this.rotationPitch);
   }

   /**
    * Called when the entity is dealt a critical hit.
    */
   public void onCriticalHit(Entity entityHit) {
      this.getServerWorld().getEntityTracker().sendToTrackingAndSelf(this, new SPacketAnimation(entityHit, 4));
   }

   public void onEnchantmentCritical(Entity entityHit) {
      this.getServerWorld().getEntityTracker().sendToTrackingAndSelf(this, new SPacketAnimation(entityHit, 5));
   }

   /**
    * Sends the player's abilities to the server (if there is one).
    */
   public void sendPlayerAbilities() {
      if (this.connection != null) {
         this.connection.sendPacket(new SPacketPlayerAbilities(this.abilities));
         this.updatePotionMetadata();
      }
   }

   public WorldServer getServerWorld() {
      return (WorldServer)this.world;
   }

   /**
    * Sets the player's game mode and sends it to them.
    */
   public void setGameType(GameType gameType) {
      this.interactionManager.setGameType(gameType);
      this.connection.sendPacket(new SPacketChangeGameState(3, (float)gameType.getID()));
      if (gameType == GameType.SPECTATOR) {
         this.spawnShoulderEntities();
         this.stopRiding();
      } else {
         this.setSpectatingEntity(this);
      }

      this.sendPlayerAbilities();
      this.markPotionsDirty();
   }

   /**
    * Returns true if the player is in spectator mode.
    */
   public boolean isSpectator() {
      return this.interactionManager.getGameType() == GameType.SPECTATOR;
   }

   public boolean isCreative() {
      return this.interactionManager.getGameType() == GameType.CREATIVE;
   }

   /**
    * Send a chat message to the CommandSender
    */
   public void sendMessage(ITextComponent component) {
      this.sendMessage(component, ChatType.SYSTEM);
   }

   public void sendMessage(ITextComponent textComponent, ChatType chatTypeIn) {
      this.connection.sendPacket(new SPacketChat(textComponent, chatTypeIn), (p_211144_3_) -> {
         if (!p_211144_3_.isSuccess() && (chatTypeIn == ChatType.GAME_INFO || chatTypeIn == ChatType.SYSTEM)) {
            int i = 256;
            String s = textComponent.func_212636_a(256);
            ITextComponent itextcomponent = (new TextComponentString(s)).applyTextStyle(TextFormatting.YELLOW);
            this.connection.sendPacket(new SPacketChat((new TextComponentTranslation("multiplayer.message_not_delivered", itextcomponent)).applyTextStyle(TextFormatting.RED), ChatType.SYSTEM));
         }

      });
   }

   /**
    * Gets the player's IP address. Used in /banip.
    */
   public String getPlayerIP() {
      String s = this.connection.netManager.getRemoteAddress().toString();
      s = s.substring(s.indexOf("/") + 1);
      s = s.substring(0, s.indexOf(":"));
      return s;
   }

   public void handleClientSettings(CPacketClientSettings packetIn) {
      this.language = packetIn.getLang();
      this.chatVisibility = packetIn.getChatVisibility();
      this.chatColours = packetIn.isColorsEnabled();
      this.getDataManager().set(PLAYER_MODEL_FLAG, (byte)packetIn.getModelPartFlags());
      this.getDataManager().set(MAIN_HAND, (byte)(packetIn.getMainHand() == EnumHandSide.LEFT ? 0 : 1));
   }

   public EntityPlayer.EnumChatVisibility getChatVisibility() {
      return this.chatVisibility;
   }

   public void loadResourcePack(String url, String hash) {
      this.connection.sendPacket(new SPacketResourcePackSend(url, hash));
   }

   protected int getPermissionLevel() {
      return this.server.getPermissionLevel(this.getGameProfile());
   }

   public void markPlayerActive() {
      this.playerLastActiveTime = Util.milliTime();
   }

   public StatisticsManagerServer getStats() {
      return this.stats;
   }

   public ServerRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   /**
    * Sends a packet to the player to remove an entity.
    */
   public void removeEntity(Entity entityIn) {
      if (entityIn instanceof EntityPlayer) {
         this.connection.sendPacket(new SPacketDestroyEntities(entityIn.getEntityId()));
      } else {
         this.entityRemoveQueue.add(entityIn.getEntityId());
      }

   }

   public void addEntity(Entity entityIn) {
      this.entityRemoveQueue.remove(Integer.valueOf(entityIn.getEntityId()));
   }

   /**
    * Clears potion metadata values if the entity has no potion effects. Otherwise, updates potion effect color,
    * ambience, and invisibility metadata values
    */
   protected void updatePotionMetadata() {
      if (this.isSpectator()) {
         this.resetPotionEffectMetadata();
         this.setInvisible(true);
      } else {
         super.updatePotionMetadata();
      }

      this.getServerWorld().getEntityTracker().updateVisibility(this);
   }

   public Entity getSpectatingEntity() {
      return (Entity)(this.spectatingEntity == null ? this : this.spectatingEntity);
   }

   public void setSpectatingEntity(Entity entityToSpectate) {
      Entity entity = this.getSpectatingEntity();
      this.spectatingEntity = (Entity)(entityToSpectate == null ? this : entityToSpectate);
      if (entity != this.spectatingEntity) {
         this.connection.sendPacket(new SPacketCamera(this.spectatingEntity));
         this.setPositionAndUpdate(this.spectatingEntity.posX, this.spectatingEntity.posY, this.spectatingEntity.posZ);
      }

   }

   /**
    * Decrements the counter for the remaining time until the entity may use a portal again.
    */
   protected void decrementTimeUntilPortal() {
      if (this.timeUntilPortal > 0 && !this.invulnerableDimensionChange) {
         --this.timeUntilPortal;
      }

   }

   /**
    * Attacks for the player the targeted entity with the currently equipped item.  The equipped item has hitEntity
    * called on it. Args: targetEntity
    */
   public void attackTargetEntityWithCurrentItem(Entity targetEntity) {
      if (this.interactionManager.getGameType() == GameType.SPECTATOR) {
         this.setSpectatingEntity(targetEntity);
      } else {
         super.attackTargetEntityWithCurrentItem(targetEntity);
      }

   }

   public long getLastActiveTime() {
      return this.playerLastActiveTime;
   }

   /**
    * Returns null which indicates the tab list should just display the player's name, return a different value to
    * display the specified text instead of the player's name
    */
   @Nullable
   public ITextComponent getTabListDisplayName() {
      return null;
   }

   public void swingArm(EnumHand hand) {
      super.swingArm(hand);
      this.resetCooldown();
   }

   public boolean isInvulnerableDimensionChange() {
      return this.invulnerableDimensionChange;
   }

   public void clearInvulnerableDimensionChange() {
      this.invulnerableDimensionChange = false;
   }

   public void setElytraFlying() {
      this.setFlag(7, true);
   }

   public void clearElytraFlying() {
      this.setFlag(7, true);
      this.setFlag(7, false);
   }

   public PlayerAdvancements getAdvancements() {
      return this.advancements;
   }

   @Nullable
   public Vec3d getEnteredNetherPosition() {
      return this.enteredNetherPosition;
   }

   public void teleport(WorldServer p_200619_1_, double x, double y, double z, float yaw, float pitch) {
      this.setSpectatingEntity(this);
      this.stopRiding();
      if (p_200619_1_ == this.world) {
         this.connection.setPlayerLocation(x, y, z, yaw, pitch);
      } else {
         WorldServer worldserver = this.getServerWorld();
         this.dimension = p_200619_1_.dimension.getType();
         this.connection.sendPacket(new SPacketRespawn(this.dimension, worldserver.getDifficulty(), worldserver.getWorldInfo().getTerrainType(), this.interactionManager.getGameType()));
         this.server.getPlayerList().updatePermissionLevel(this);
         worldserver.removeEntityDangerously(this);
         this.removed = false;
         this.setLocationAndAngles(x, y, z, yaw, pitch);
         if (this.isAlive()) {
            worldserver.tickEntity(this, false);
            p_200619_1_.spawnEntity(this);
            p_200619_1_.tickEntity(this, false);
         }

         this.setWorld(p_200619_1_);
         this.server.getPlayerList().preparePlayer(this, worldserver);
         this.connection.setPlayerLocation(x, y, z, yaw, pitch);
         this.interactionManager.setWorld(p_200619_1_);
         this.server.getPlayerList().sendWorldInfo(this, p_200619_1_);
         this.server.getPlayerList().sendInventory(this);
      }

   }
}