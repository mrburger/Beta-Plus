package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.Tag;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Fluid implements net.minecraftforge.common.extensions.IForgeFluid {
   public static final ObjectIntIdentityMap<IFluidState> STATE_REGISTRY = new ObjectIntIdentityMap<>();
   protected final StateContainer<Fluid, IFluidState> stateContainer;
   private IFluidState defaultState;

   protected Fluid() {
      StateContainer.Builder<Fluid, IFluidState> builder = new StateContainer.Builder<>(this);
      this.fillStateContainer(builder);
      this.stateContainer = builder.create(FluidState::new);
      this.setDefaultState(this.stateContainer.getBaseState());
   }

   protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
   }

   public StateContainer<Fluid, IFluidState> getStateContainer() {
      return this.stateContainer;
   }

   protected final void setDefaultState(IFluidState state) {
      this.defaultState = state;
   }

   public final IFluidState getDefaultState() {
      return this.defaultState;
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   @OnlyIn(Dist.CLIENT)
   public abstract BlockRenderLayer getRenderLayer();

   public abstract Item getFilledBucket();

   @OnlyIn(Dist.CLIENT)
   protected void animateTick(World worldIn, BlockPos pos, IFluidState state, Random random) {
   }

   protected void tick(World worldIn, BlockPos pos, IFluidState state) {
   }

   protected void randomTick(World p_207186_1_, BlockPos pos, IFluidState state, Random random) {
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   protected IParticleData getDripParticleData() {
      return null;
   }

   protected abstract boolean canOtherFlowInto(IFluidState state, Fluid fluidIn, EnumFacing direction);

   protected abstract Vec3d getFlow(IWorldReaderBase worldIn, BlockPos pos, IFluidState state);

   public abstract int getTickRate(IWorldReaderBase p_205569_1_);

   protected boolean getTickRandomly() {
      return false;
   }

   protected boolean isEmpty() {
      return false;
   }

   protected abstract float getExplosionResistance();

   public abstract float getHeight(IFluidState state);

   protected abstract IBlockState getBlockState(IFluidState state);

   public abstract boolean isSource(IFluidState state);

   public abstract int getLevel(IFluidState p_207192_1_);

   public boolean isEquivalentTo(Fluid fluidIn) {
      return fluidIn == this;
   }

   public boolean isIn(Tag<Fluid> tagIn) {
      return tagIn.contains(this);
   }

   public static void registerAll() {
      register(IRegistry.field_212619_h.func_212609_b(), new EmptyFluid());
      register("flowing_water", new WaterFluid.Flowing());
      register("water", new WaterFluid.Source());
      register("flowing_lava", new LavaFluid.Flowing());
      register("lava", new LavaFluid.Source());

      for(Fluid fluid : IRegistry.field_212619_h) {
         for(IFluidState ifluidstate : fluid.getStateContainer().getValidStates()) {
            STATE_REGISTRY.add(ifluidstate);
         }
      }

   }

   private static void register(String resourceName, Fluid fluidIn) {
      register(new ResourceLocation(resourceName), fluidIn);
   }

   private static void register(ResourceLocation resourceLocationIn, Fluid fluidIn) {
      IRegistry.field_212619_h.put(resourceLocationIn, fluidIn);
   }
}