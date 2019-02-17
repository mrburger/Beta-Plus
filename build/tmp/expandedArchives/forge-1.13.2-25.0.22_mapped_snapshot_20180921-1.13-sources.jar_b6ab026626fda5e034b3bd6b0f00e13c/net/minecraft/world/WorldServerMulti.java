package net.minecraft.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.ISaveHandler;

public class WorldServerMulti extends WorldServer {
   private final WorldServer delegate;
   private final IBorderListener borderListener;
   public WorldServerMulti(MinecraftServer p_i49820_1_, ISaveHandler p_i49820_2_, DimensionType p_i49820_3_, WorldServer p_i49820_4_, Profiler p_i49820_5_) {
      super(p_i49820_1_, p_i49820_2_, p_i49820_4_.getMapStorage(), new DerivedWorldInfo(p_i49820_4_.getWorldInfo()), p_i49820_3_, p_i49820_5_);
      this.delegate = p_i49820_4_;
      this.borderListener = (new IBorderListener() {
         public void onSizeChanged(WorldBorder border, double newSize) {
            WorldServerMulti.this.getWorldBorder().setTransition(newSize);
         }

         public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time) {
            WorldServerMulti.this.getWorldBorder().setTransition(oldSize, newSize, time);
         }

         public void onCenterChanged(WorldBorder border, double x, double z) {
            WorldServerMulti.this.getWorldBorder().setCenter(x, z);
         }

         public void onWarningTimeChanged(WorldBorder border, int newTime) {
            WorldServerMulti.this.getWorldBorder().setWarningTime(newTime);
         }

         public void onWarningDistanceChanged(WorldBorder border, int newDistance) {
            WorldServerMulti.this.getWorldBorder().setWarningDistance(newDistance);
         }

         public void onDamageAmountChanged(WorldBorder border, double newAmount) {
            WorldServerMulti.this.getWorldBorder().setDamageAmount(newAmount);
         }

         public void onDamageBufferChanged(WorldBorder border, double newSize) {
            WorldServerMulti.this.getWorldBorder().setDamageBuffer(newSize);
         }
      });
      p_i49820_4_.getWorldBorder().addListener(this.borderListener);
   }

   /**
    * Saves the chunks to disk.
    */
   protected void saveLevel() {
   }

   public WorldServerMulti func_212251_i__() {
      String s = VillageCollection.fileNameForProvider(this.dimension);
      VillageCollection villagecollection = this.func_212411_a(getDimension().getType(), VillageCollection::new, s);
      if (villagecollection == null) {
         this.villageCollection = new VillageCollection(this);
         this.func_212409_a(getDimension().getType(), s, this.villageCollection);
      } else {
         this.villageCollection = villagecollection;
         this.villageCollection.setWorldsForAll(this);
      }

      this.initCapabilities();
      return this;
   }

   /**
    * Called during saving of a world to give children worlds a chance to save additional data. Only used to save
    * WorldProviderEnd's data in Vanilla.
    */
   public void saveAdditionalData() {
      this.dimension.onWorldSave();
   }

   @Override
   public void close() {
      super.close();
      this.delegate.getWorldBorder().removeListener(this.borderListener); // Unlink ourselves, to prevent world leak.
   }
}