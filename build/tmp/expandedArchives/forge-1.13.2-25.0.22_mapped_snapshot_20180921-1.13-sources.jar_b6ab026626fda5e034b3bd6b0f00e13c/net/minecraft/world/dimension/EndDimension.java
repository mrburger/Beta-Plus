package net.minecraft.world.dimension;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.EndGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EndDimension extends Dimension {
   public static final BlockPos field_209958_g = new BlockPos(100, 50, 0);
   private DragonFightManager dragonFightManager;
   private final DimensionType type;
   public EndDimension() { this(DimensionType.THE_END); }
   public EndDimension(DimensionType type) {
      this.type = type;
   }

   /**
    * Creates a new {@link BiomeProvider} for the WorldProvider, and also sets the values of {@link #hasSkylight} and
    * {@link #hasNoSky} appropriately.
    *  
    * Note that subclasses generally override this method without calling the parent version.
    */
   public void init() {
      NBTTagCompound nbttagcompound = this.world.getWorldInfo().getDimensionData(this.world.dimension.getType());
      this.dragonFightManager = this.world instanceof WorldServer ? new DragonFightManager((WorldServer)this.world, nbttagcompound.getCompound("DragonFight")) : null;
      this.hasSkyLight = false;
   }

   public IChunkGenerator<?> createChunkGenerator() {
      EndGenSettings endgensettings = ChunkGeneratorType.FLOATING_ISLANDS.createSettings();
      endgensettings.setDefautBlock(Blocks.END_STONE.getDefaultState());
      endgensettings.setDefaultFluid(Blocks.AIR.getDefaultState());
      endgensettings.setSpawnPos(this.getSpawnCoordinate());
      return ChunkGeneratorType.FLOATING_ISLANDS.create(this.world, BiomeProviderType.THE_END.create(BiomeProviderType.THE_END.createSettings().setSeed(this.world.getSeed())), endgensettings);
   }

   /**
    * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
    */
   public float calculateCelestialAngle(long worldTime, float partialTicks) {
      return 0.0F;
   }

   /**
    * Returns array with sunrise/sunset colors
    */
   @Nullable
   @OnlyIn(Dist.CLIENT)
   public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
      return null;
   }

   /**
    * Return Vec3D with biome specific fog color
    */
   @OnlyIn(Dist.CLIENT)
   public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
      int i = 10518688;
      float f = MathHelper.cos(p_76562_1_ * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      float f1 = 0.627451F;
      float f2 = 0.5019608F;
      float f3 = 0.627451F;
      f1 = f1 * (f * 0.0F + 0.15F);
      f2 = f2 * (f * 0.0F + 0.15F);
      f3 = f3 * (f * 0.0F + 0.15F);
      return new Vec3d((double)f1, (double)f2, (double)f3);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSkyColored() {
      return false;
   }

   /**
    * True if the player can respawn in this dimension (true = overworld, false = nether).
    */
   public boolean canRespawnHere() {
      return false;
   }

   /**
    * Returns 'true' if in the "main surface world", but 'false' if in the Nether or End dimensions.
    */
   public boolean isSurfaceWorld() {
      return false;
   }

   /**
    * the y level at which clouds are rendered.
    */
   @OnlyIn(Dist.CLIENT)
   public float getCloudHeight() {
      return 8.0F;
   }

   @Nullable
   public BlockPos findSpawn(ChunkPos p_206920_1_, boolean checkValid) {
      Random random = new Random(this.world.getSeed());
      BlockPos blockpos = new BlockPos(p_206920_1_.getXStart() + random.nextInt(15), 0, p_206920_1_.getZEnd() + random.nextInt(15));
      return this.world.getGroundAboveSeaLevel(blockpos).getMaterial().blocksMovement() ? blockpos : null;
   }

   public BlockPos getSpawnCoordinate() {
      return field_209958_g;
   }

   @Nullable
   public BlockPos findSpawn(int p_206921_1_, int p_206921_2_, boolean checkValid) {
      return this.findSpawn(new ChunkPos(p_206921_1_ >> 4, p_206921_2_ >> 4), checkValid);
   }

   /**
    * Returns true if the given X,Z coordinate should show environmental fog.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean doesXZShowFog(int x, int z) {
      return false;
   }

   public DimensionType getType() {
      return type;
   }

   /**
    * Called when the world is performing a save. Only used to save the state of the Dragon Boss fight in
    * WorldProviderEnd in Vanilla.
    */
   public void onWorldSave() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      if (this.dragonFightManager != null) {
         nbttagcompound.setTag("DragonFight", this.dragonFightManager.write());
      }

      this.world.getWorldInfo().setDimensionData(this.world.dimension.getType(), nbttagcompound);
   }

   /**
    * Called when the world is updating entities. Only used in WorldProviderEnd to update the DragonFightManager in
    * Vanilla.
    */
   public void tick() {
      if (this.dragonFightManager != null) {
         this.dragonFightManager.tick();
      }

   }

   @Nullable
   public DragonFightManager getDragonFightManager() {
      return this.dragonFightManager;
   }

   /**
    * Called when a Player is added to the provider's world.
    */
   @Override
   public void onPlayerAdded(net.minecraft.entity.player.EntityPlayerMP player) {
      if (dragonFightManager != null) {
         dragonFightManager.addPlayer(player);
      }
   }

   /**
    * Called when a Player is removed from the provider's world.
    */
   @Override
   public void onPlayerRemoved(net.minecraft.entity.player.EntityPlayerMP player) {
      if (dragonFightManager != null) {
         dragonFightManager.removePlayer(player);
      }
   }
}