package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTUtil;

public class ChunkSection {
   private static final IBlockStatePalette<IBlockState> field_205512_a = new BlockStatePaletteRegistry<>(Block.BLOCK_STATE_IDS, Blocks.AIR.getDefaultState());
   /** Contains the bottom-most Y block represented by this ExtendedBlockStorage. Typically a multiple of 16. */
   private final int yBase;
   /** A total count of the number of non-air blocks in this block storage's Chunk. */
   private int blockRefCount;
   /**
    * Contains the number of blocks in this block storage's parent chunk that require random ticking. Used to cull the
    * Chunk from random tick updates for performance reasons.
    */
   private int tickRefCount;
   private int fluidRefCount;
   private final BlockStateContainer<IBlockState> data;
   /** The NibbleArray containing a block of Block-light data. */
   private NibbleArray blockLight;
   /**
    * The NibbleArray containing skylight data.
    *  
    * Will be null if the provider for the world the chunk containing this block storage does not {@linkplain
    * net.minecraft.world.WorldProvider#hasSkylight have skylight}.
    */
   private NibbleArray skyLight;

   public ChunkSection(int y, boolean storeSkylight) {
      this.yBase = y;
      this.data = new BlockStateContainer<>(field_205512_a, Block.BLOCK_STATE_IDS, NBTUtil::readBlockState, NBTUtil::writeBlockState, Blocks.AIR.getDefaultState());
      this.blockLight = new NibbleArray();
      if (storeSkylight) {
         this.skyLight = new NibbleArray();
      }

   }

   public IBlockState get(int x, int y, int z) {
      return this.data.get(x, y, z);
   }

   public IFluidState getFluidState(int x, int y, int z) {
      return this.data.get(x, y, z).getFluidState();
   }

   public void set(int x, int y, int z, IBlockState state) {
      if (state instanceof net.minecraftforge.common.property.IExtendedBlockState)
         state = ((net.minecraftforge.common.property.IExtendedBlockState)state).getClean();
      IBlockState iblockstate = this.get(x, y, z);
      IFluidState ifluidstate = this.getFluidState(x, y, z);
      IFluidState ifluidstate1 = state.getFluidState();
      if (!iblockstate.isAir()) {
         --this.blockRefCount;
         if (iblockstate.needsRandomTick()) {
            --this.tickRefCount;
         }
      }

      if (!ifluidstate.isEmpty()) {
         --this.fluidRefCount;
      }

      if (!state.isAir()) {
         ++this.blockRefCount;
         if (state.needsRandomTick()) {
            ++this.tickRefCount;
         }
      }

      if (!ifluidstate1.isEmpty()) {
         --this.fluidRefCount;
      }

      this.data.set(x, y, z, state);
   }

   /**
    * Returns whether or not this block storage's Chunk is fully empty, based on its internal reference count.
    */
   public boolean isEmpty() {
      return this.blockRefCount == 0;
   }

   public boolean needsRandomTickAny() {
      return this.needsRandomTick() || this.needsRandomTickFluid();
   }

   /**
    * Returns whether or not this block storage's Chunk will require random ticking, used to avoid looping through
    * random block ticks when there are no blocks that would randomly tick.
    */
   public boolean needsRandomTick() {
      return this.tickRefCount > 0;
   }

   public boolean needsRandomTickFluid() {
      return this.fluidRefCount > 0;
   }

   /**
    * Returns the Y location of this ExtendedBlockStorage.
    */
   public int getYLocation() {
      return this.yBase;
   }

   /**
    * Sets the saved Sky-light value in the extended block storage structure.
    */
   public void setSkyLight(int x, int y, int z, int value) {
      this.skyLight.set(x, y, z, value);
   }

   /**
    * Gets the saved Sky-light value in the extended block storage structure.
    */
   public int getSkyLight(int x, int y, int z) {
      return this.skyLight.get(x, y, z);
   }

   /**
    * Sets the saved Block-light value in the extended block storage structure.
    */
   public void setBlockLight(int x, int y, int z, int value) {
      this.blockLight.set(x, y, z, value);
   }

   /**
    * Gets the saved Block-light value in the extended block storage structure.
    */
   public int getBlockLight(int x, int y, int z) {
      return this.blockLight.get(x, y, z);
   }

   public void recalculateRefCounts() {
      this.blockRefCount = 0;
      this.tickRefCount = 0;
      this.fluidRefCount = 0;

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            for(int k = 0; k < 16; ++k) {
               IBlockState iblockstate = this.get(i, j, k);
               IFluidState ifluidstate = this.getFluidState(i, j, k);
               if (!iblockstate.isAir()) {
                  ++this.blockRefCount;
                  if (iblockstate.needsRandomTick()) {
                     ++this.tickRefCount;
                  }
               }

               if (!ifluidstate.isEmpty()) {
                  ++this.blockRefCount;
                  if (ifluidstate.getTickRandomly()) {
                     ++this.fluidRefCount;
                  }
               }
            }
         }
      }

   }

   public BlockStateContainer<IBlockState> getData() {
      return this.data;
   }

   /**
    * Returns the NibbleArray instance containing Block-light data.
    */
   public NibbleArray getBlockLight() {
      return this.blockLight;
   }

   /**
    * Returns the NibbleArray instance containing Sky-light data.
    */
   public NibbleArray getSkyLight() {
      return this.skyLight;
   }

   /**
    * Sets the NibbleArray instance used for Block-light values in this particular storage block.
    */
   public void setBlockLight(NibbleArray newBlocklightArray) {
      this.blockLight = newBlocklightArray;
   }

   /**
    * Sets the NibbleArray instance used for Sky-light values in this particular storage block.
    */
   public void setSkyLight(NibbleArray newSkylightArray) {
      this.skyLight = newSkylightArray;
   }
}