package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Dimension implements net.minecraftforge.common.extensions.IForgeDimension {
   public static final float[] MOON_PHASE_FACTORS = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   /** world object being used */
   protected World world;
   /** States whether the Hell world provider is used(true) or if the normal world provider is used(false) */
   protected boolean doesWaterVaporize;
   /**
    * Whether this dimension should be treated as the nether.
    *  
    * @see <a href="https://github.com/ModCoderPack/MCPBot-Issues/issues/330">https://github.com/ModCoderPack/MCPBot-
    * Issues/issues/330</a>
    */
   protected boolean nether;
   protected boolean hasSkyLight;
   /** Light to brightness conversion table */
   protected final float[] lightBrightnessTable = new float[16];
   /** Array for sunrise/sunset colors (RGBA) */
   private final float[] colorsSunriseSunset = new float[4];

   /**
    * associate an existing world with a World provider, and setup its lightbrightness table
    */
   public final void setWorld(World worldIn) {
      this.world = worldIn;
      this.init();
      this.generateLightBrightnessTable();
   }

   /**
    * Creates the light to brightness table
    */
   protected void generateLightBrightnessTable() {
      float f = 0.0F;

      for(int i = 0; i <= 15; ++i) {
         float f1 = 1.0F - (float)i / 15.0F;
         this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * 1.0F + 0.0F;
      }

   }

   public int getMoonPhase(long worldTime) {
      return (int)(worldTime / 24000L % 8L + 8L) % 8;
   }

   /**
    * Returns array with sunrise/sunset colors
    */
   @Nullable
   @OnlyIn(Dist.CLIENT)
   public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
      float f = 0.4F;
      float f1 = MathHelper.cos(celestialAngle * ((float)Math.PI * 2F)) - 0.0F;
      float f2 = -0.0F;
      if (f1 >= -0.4F && f1 <= 0.4F) {
         float f3 = (f1 - -0.0F) / 0.4F * 0.5F + 0.5F;
         float f4 = 1.0F - (1.0F - MathHelper.sin(f3 * (float)Math.PI)) * 0.99F;
         f4 = f4 * f4;
         this.colorsSunriseSunset[0] = f3 * 0.3F + 0.7F;
         this.colorsSunriseSunset[1] = f3 * f3 * 0.7F + 0.2F;
         this.colorsSunriseSunset[2] = f3 * f3 * 0.0F + 0.2F;
         this.colorsSunriseSunset[3] = f4;
         return this.colorsSunriseSunset;
      } else {
         return null;
      }
   }

   /**
    * the y level at which clouds are rendered.
    */
   @OnlyIn(Dist.CLIENT)
   public float getCloudHeight() {
      return this.getWorld().getWorldInfo().getTerrainType().getCloudHeight();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSkyColored() {
      return true;
   }

   @Nullable
   public BlockPos getSpawnCoordinate() {
      return null;
   }

   /**
    * Returns a double value representing the Y value relative to the top of the map at which void fog is at its
    * maximum. The default factor of 0.03125 relative to 256, for example, means the void fog will be at its maximum at
    * (256*0.03125), or 8.
    */
   @OnlyIn(Dist.CLIENT)
   public double getVoidFogYFactor() {
      return this.world.getWorldInfo().getTerrainType().voidFadeMagnitude();
   }

   public boolean doesWaterVaporize() {
      return this.doesWaterVaporize;
   }

   public boolean hasSkyLight() {
      return this.hasSkyLight;
   }

   public boolean isNether() {
      return this.nether;
   }

   public float[] getLightBrightnessTable() {
      return this.lightBrightnessTable;
   }

   public WorldBorder createWorldBorder() {
      return new WorldBorder();
   }

   /**
    * Called when a Player is added to the provider's world.
    */
   public void onPlayerAdded(EntityPlayerMP player) {
   }

   /**
    * Called when a Player is removed from the provider's world.
    */
   public void onPlayerRemoved(EntityPlayerMP player) {
   }

   /**
    * Called when the world is performing a save. Only used to save the state of the Dragon Boss fight in
    * WorldProviderEnd in Vanilla.
    */
   public void onWorldSave() {
   }

   /**
    * Called when the world is updating entities. Only used in WorldProviderEnd to update the DragonFightManager in
    * Vanilla.
    */
   public void tick() {
   }

   /**
    * Called to determine if the chunk at the given chunk coordinates within the provider's world can be dropped. Used
    * in WorldProviderSurface to prevent spawn chunks from being unloaded.
    */
   public boolean canDropChunk(int x, int z) {
      return !this.world.func_212416_f(x, z);
   }

   /**
    * Creates a new {@link BiomeProvider} for the WorldProvider, and also sets the values of {@link #hasSkylight} and
    * {@link #hasNoSky} appropriately.
    *  
    * Note that subclasses generally override this method without calling the parent version.
    */
   protected abstract void init();

   @Deprecated //Forge: Use WorldType.createChunkGenerator
   public abstract IChunkGenerator<?> createChunkGenerator();

   @Nullable
   public abstract BlockPos findSpawn(ChunkPos p_206920_1_, boolean checkValid);

   @Nullable
   public abstract BlockPos findSpawn(int p_206921_1_, int p_206921_2_, boolean checkValid);

   /**
    * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
    */
   public abstract float calculateCelestialAngle(long worldTime, float partialTicks);

   /**
    * Returns 'true' if in the "main surface world", but 'false' if in the Nether or End dimensions.
    */
   public abstract boolean isSurfaceWorld();

   /**
    * Return Vec3D with biome specific fog color
    */
   @OnlyIn(Dist.CLIENT)
   public abstract Vec3d getFogColor(float p_76562_1_, float p_76562_2_);

   /**
    * True if the player can respawn in this dimension (true = overworld, false = nether).
    */
   public abstract boolean canRespawnHere();

   /**
    * Returns true if the given X,Z coordinate should show environmental fog.
    */
   @OnlyIn(Dist.CLIENT)
   public abstract boolean doesXZShowFog(int x, int z);

   public abstract DimensionType getType();

   /*======================================= Forge Start =========================================*/
   private net.minecraftforge.client.IRenderHandler skyRenderer = null;
   private net.minecraftforge.client.IRenderHandler cloudRenderer = null;
   private net.minecraftforge.client.IRenderHandler weatherRenderer = null;

   @Nullable
   @OnlyIn(Dist.CLIENT)
   @Override
   public net.minecraftforge.client.IRenderHandler getSkyRenderer() {
      return this.skyRenderer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void setSkyRenderer(net.minecraftforge.client.IRenderHandler skyRenderer) {
      this.skyRenderer = skyRenderer;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   @Override
   public net.minecraftforge.client.IRenderHandler getCloudRenderer() {
      return cloudRenderer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void setCloudRenderer(net.minecraftforge.client.IRenderHandler renderer) {
      cloudRenderer = renderer;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   @Override
   public net.minecraftforge.client.IRenderHandler getWeatherRenderer() {
      return weatherRenderer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void setWeatherRenderer(net.minecraftforge.client.IRenderHandler renderer) {
      weatherRenderer = renderer;
   }

   @Override
   public void resetRainAndThunder() {
      world.getWorldInfo().setRainTime(0);
      world.getWorldInfo().setRaining(false);
      world.getWorldInfo().setThunderTime(0);
      world.getWorldInfo().setThundering(false);
   }

   @Override
   public World getWorld() {
      return this.world;
   }
}