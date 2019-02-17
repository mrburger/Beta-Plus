package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.command.arguments.ParticleArgument;
import net.minecraft.init.Particles;
import net.minecraft.init.PotionTypes;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAreaEffectCloud extends Entity {
   /** Used instead of the inherited Entity.LOGGER for the correct logger class name in the log. */
   private static final Logger PRIVATE_LOGGER = LogManager.getLogger();
   private static final DataParameter<Float> RADIUS = EntityDataManager.createKey(EntityAreaEffectCloud.class, DataSerializers.FLOAT);
   private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityAreaEffectCloud.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> IGNORE_RADIUS = EntityDataManager.createKey(EntityAreaEffectCloud.class, DataSerializers.BOOLEAN);
   private static final DataParameter<IParticleData> PARTICLE = EntityDataManager.createKey(EntityAreaEffectCloud.class, DataSerializers.PARTICLE_DATA);
   private PotionType potion = PotionTypes.EMPTY;
   private final List<PotionEffect> effects = Lists.newArrayList();
   private final Map<Entity, Integer> reapplicationDelayMap = Maps.newHashMap();
   private int duration = 600;
   private int waitTime = 20;
   private int reapplicationDelay = 20;
   private boolean colorSet;
   private int durationOnUse;
   private float radiusOnUse;
   private float radiusPerTick;
   private EntityLivingBase owner;
   private UUID ownerUniqueId;

   public EntityAreaEffectCloud(World worldIn) {
      super(EntityType.AREA_EFFECT_CLOUD, worldIn);
      this.noClip = true;
      this.isImmuneToFire = true;
      this.setRadius(3.0F);
   }

   public EntityAreaEffectCloud(World worldIn, double x, double y, double z) {
      this(worldIn);
      this.setPosition(x, y, z);
   }

   protected void registerData() {
      this.getDataManager().register(COLOR, 0);
      this.getDataManager().register(RADIUS, 0.5F);
      this.getDataManager().register(IGNORE_RADIUS, false);
      this.getDataManager().register(PARTICLE, Particles.ENTITY_EFFECT);
   }

   public void setRadius(float radiusIn) {
      double d0 = this.posX;
      double d1 = this.posY;
      double d2 = this.posZ;
      this.setSize(radiusIn * 2.0F, 0.5F);
      this.setPosition(d0, d1, d2);
      if (!this.world.isRemote) {
         this.getDataManager().set(RADIUS, radiusIn);
      }

   }

   public float getRadius() {
      return this.getDataManager().get(RADIUS);
   }

   public void setPotion(PotionType potionIn) {
      this.potion = potionIn;
      if (!this.colorSet) {
         this.updateFixedColor();
      }

   }

   private void updateFixedColor() {
      if (this.potion == PotionTypes.EMPTY && this.effects.isEmpty()) {
         this.getDataManager().set(COLOR, 0);
      } else {
         this.getDataManager().set(COLOR, PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.effects)));
      }

   }

   public void addEffect(PotionEffect effect) {
      this.effects.add(effect);
      if (!this.colorSet) {
         this.updateFixedColor();
      }

   }

   public int getColor() {
      return this.getDataManager().get(COLOR);
   }

   public void setColor(int colorIn) {
      this.colorSet = true;
      this.getDataManager().set(COLOR, colorIn);
   }

   public IParticleData func_195058_l() {
      return this.getDataManager().get(PARTICLE);
   }

   public void func_195059_a(IParticleData p_195059_1_) {
      this.getDataManager().set(PARTICLE, p_195059_1_);
   }

   /**
    * Sets if the radius should be ignored, and the effect should be shown in a single point instead of an area
    */
   protected void setIgnoreRadius(boolean ignoreRadius) {
      this.getDataManager().set(IGNORE_RADIUS, ignoreRadius);
   }

   /**
    * Returns true if the radius should be ignored, and the effect should be shown in a single point instead of an area
    */
   public boolean shouldIgnoreRadius() {
      return this.getDataManager().get(IGNORE_RADIUS);
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int durationIn) {
      this.duration = durationIn;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      boolean flag = this.shouldIgnoreRadius();
      float f = this.getRadius();
      if (this.world.isRemote) {
         IParticleData iparticledata = this.func_195058_l();
         if (flag) {
            if (this.rand.nextBoolean()) {
               for(int i = 0; i < 2; ++i) {
                  float f1 = this.rand.nextFloat() * ((float)Math.PI * 2F);
                  float f2 = MathHelper.sqrt(this.rand.nextFloat()) * 0.2F;
                  float f3 = MathHelper.cos(f1) * f2;
                  float f4 = MathHelper.sin(f1) * f2;
                  if (iparticledata.getType() == Particles.ENTITY_EFFECT) {
                     int j = this.rand.nextBoolean() ? 16777215 : this.getColor();
                     int k = j >> 16 & 255;
                     int l = j >> 8 & 255;
                     int i1 = j & 255;
                     this.world.addOptionalParticle(iparticledata, this.posX + (double)f3, this.posY, this.posZ + (double)f4, (double)((float)k / 255.0F), (double)((float)l / 255.0F), (double)((float)i1 / 255.0F));
                  } else {
                     this.world.addOptionalParticle(iparticledata, this.posX + (double)f3, this.posY, this.posZ + (double)f4, 0.0D, 0.0D, 0.0D);
                  }
               }
            }
         } else {
            float f5 = (float)Math.PI * f * f;

            for(int k1 = 0; (float)k1 < f5; ++k1) {
               float f6 = this.rand.nextFloat() * ((float)Math.PI * 2F);
               float f7 = MathHelper.sqrt(this.rand.nextFloat()) * f;
               float f8 = MathHelper.cos(f6) * f7;
               float f9 = MathHelper.sin(f6) * f7;
               if (iparticledata.getType() == Particles.ENTITY_EFFECT) {
                  int l1 = this.getColor();
                  int i2 = l1 >> 16 & 255;
                  int j2 = l1 >> 8 & 255;
                  int j1 = l1 & 255;
                  this.world.addOptionalParticle(iparticledata, this.posX + (double)f8, this.posY, this.posZ + (double)f9, (double)((float)i2 / 255.0F), (double)((float)j2 / 255.0F), (double)((float)j1 / 255.0F));
               } else {
                  this.world.addOptionalParticle(iparticledata, this.posX + (double)f8, this.posY, this.posZ + (double)f9, (0.5D - this.rand.nextDouble()) * 0.15D, (double)0.01F, (0.5D - this.rand.nextDouble()) * 0.15D);
               }
            }
         }
      } else {
         if (this.ticksExisted >= this.waitTime + this.duration) {
            this.remove();
            return;
         }

         boolean flag1 = this.ticksExisted < this.waitTime;
         if (flag != flag1) {
            this.setIgnoreRadius(flag1);
         }

         if (flag1) {
            return;
         }

         if (this.radiusPerTick != 0.0F) {
            f += this.radiusPerTick;
            if (f < 0.5F) {
               this.remove();
               return;
            }

            this.setRadius(f);
         }

         if (this.ticksExisted % 5 == 0) {
            Iterator<Entry<Entity, Integer>> iterator = this.reapplicationDelayMap.entrySet().iterator();

            while(iterator.hasNext()) {
               Entry<Entity, Integer> entry = iterator.next();
               if (this.ticksExisted >= entry.getValue()) {
                  iterator.remove();
               }
            }

            List<PotionEffect> lstPotions = Lists.newArrayList();

            for(PotionEffect potioneffect1 : this.potion.getEffects()) {
              lstPotions.add(new PotionEffect(potioneffect1.getPotion(), potioneffect1.getDuration() / 4, potioneffect1.getAmplifier(), potioneffect1.isAmbient(), potioneffect1.doesShowParticles()));
            }

            lstPotions.addAll(this.effects);
            if (lstPotions.isEmpty()) {
               this.reapplicationDelayMap.clear();
            } else {
               List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getBoundingBox());
               if (!list.isEmpty()) {
                  for(EntityLivingBase entitylivingbase : list) {
                     if (!this.reapplicationDelayMap.containsKey(entitylivingbase) && entitylivingbase.canBeHitWithPotion()) {
                        double d0 = entitylivingbase.posX - this.posX;
                        double d1 = entitylivingbase.posZ - this.posZ;
                        double d2 = d0 * d0 + d1 * d1;
                        if (d2 <= (double)(f * f)) {
                           this.reapplicationDelayMap.put(entitylivingbase, this.ticksExisted + this.reapplicationDelay);

                           for(PotionEffect potioneffect : lstPotions) {
                              if (potioneffect.getPotion().isInstant()) {
                                 potioneffect.getPotion().affectEntity(this, this.getOwner(), entitylivingbase, potioneffect.getAmplifier(), 0.5D);
                              } else {
                                 entitylivingbase.addPotionEffect(new PotionEffect(potioneffect));
                              }
                           }

                           if (this.radiusOnUse != 0.0F) {
                              f += this.radiusOnUse;
                              if (f < 0.5F) {
                                 this.remove();
                                 return;
                              }

                              this.setRadius(f);
                           }

                           if (this.durationOnUse != 0) {
                              this.duration += this.durationOnUse;
                              if (this.duration <= 0) {
                                 this.remove();
                                 return;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public void setRadiusOnUse(float radiusOnUseIn) {
      this.radiusOnUse = radiusOnUseIn;
   }

   public void setRadiusPerTick(float radiusPerTickIn) {
      this.radiusPerTick = radiusPerTickIn;
   }

   public void setWaitTime(int waitTimeIn) {
      this.waitTime = waitTimeIn;
   }

   public void setOwner(@Nullable EntityLivingBase ownerIn) {
      this.owner = ownerIn;
      this.ownerUniqueId = ownerIn == null ? null : ownerIn.getUniqueID();
   }

   @Nullable
   public EntityLivingBase getOwner() {
      if (this.owner == null && this.ownerUniqueId != null && this.world instanceof WorldServer) {
         Entity entity = ((WorldServer)this.world).getEntityFromUuid(this.ownerUniqueId);
         if (entity instanceof EntityLivingBase) {
            this.owner = (EntityLivingBase)entity;
         }
      }

      return this.owner;
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(NBTTagCompound compound) {
      this.ticksExisted = compound.getInt("Age");
      this.duration = compound.getInt("Duration");
      this.waitTime = compound.getInt("WaitTime");
      this.reapplicationDelay = compound.getInt("ReapplicationDelay");
      this.durationOnUse = compound.getInt("DurationOnUse");
      this.radiusOnUse = compound.getFloat("RadiusOnUse");
      this.radiusPerTick = compound.getFloat("RadiusPerTick");
      this.setRadius(compound.getFloat("Radius"));
      this.ownerUniqueId = compound.getUniqueId("OwnerUUID");
      if (compound.contains("Particle", 8)) {
         try {
            this.func_195059_a(ParticleArgument.func_197189_a(new StringReader(compound.getString("Particle"))));
         } catch (CommandSyntaxException commandsyntaxexception) {
            PRIVATE_LOGGER.warn("Couldn't load custom particle {}", compound.getString("Particle"), commandsyntaxexception);
         }
      }

      if (compound.contains("Color", 99)) {
         this.setColor(compound.getInt("Color"));
      }

      if (compound.contains("Potion", 8)) {
         this.setPotion(PotionUtils.getPotionTypeFromNBT(compound));
      }

      if (compound.contains("Effects", 9)) {
         NBTTagList nbttaglist = compound.getList("Effects", 10);
         this.effects.clear();

         for(int i = 0; i < nbttaglist.size(); ++i) {
            PotionEffect potioneffect = PotionEffect.read(nbttaglist.getCompound(i));
            if (potioneffect != null) {
               this.addEffect(potioneffect);
            }
         }
      }

   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   protected void writeAdditional(NBTTagCompound compound) {
      compound.setInt("Age", this.ticksExisted);
      compound.setInt("Duration", this.duration);
      compound.setInt("WaitTime", this.waitTime);
      compound.setInt("ReapplicationDelay", this.reapplicationDelay);
      compound.setInt("DurationOnUse", this.durationOnUse);
      compound.setFloat("RadiusOnUse", this.radiusOnUse);
      compound.setFloat("RadiusPerTick", this.radiusPerTick);
      compound.setFloat("Radius", this.getRadius());
      compound.setString("Particle", this.func_195058_l().getParameters());
      if (this.ownerUniqueId != null) {
         compound.setUniqueId("OwnerUUID", this.ownerUniqueId);
      }

      if (this.colorSet) {
         compound.setInt("Color", this.getColor());
      }

      if (this.potion != PotionTypes.EMPTY && this.potion != null) {
         compound.setString("Potion", IRegistry.field_212621_j.getKey(this.potion).toString());
      }

      if (!this.effects.isEmpty()) {
         NBTTagList nbttaglist = new NBTTagList();

         for(PotionEffect potioneffect : this.effects) {
            nbttaglist.add((INBTBase)potioneffect.write(new NBTTagCompound()));
         }

         compound.setTag("Effects", nbttaglist);
      }

   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (RADIUS.equals(key)) {
         this.setRadius(this.getRadius());
      }

      super.notifyDataManagerChange(key);
   }

   public EnumPushReaction getPushReaction() {
      return EnumPushReaction.IGNORE;
   }
}