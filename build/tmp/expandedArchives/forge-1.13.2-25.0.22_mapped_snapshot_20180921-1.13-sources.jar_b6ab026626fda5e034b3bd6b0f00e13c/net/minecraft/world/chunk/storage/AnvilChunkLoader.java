package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.Biomes;
import net.minecraft.init.Fluids;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.ServerTickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerTickList;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.LegacyStructureDataUtil;
import net.minecraft.world.gen.feature.structure.StructureIO;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldSavedDataStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilChunkLoader implements IChunkLoader, IThreadedFileIO {
   private static final Logger LOGGER = LogManager.getLogger();
   /**
    * A map containing chunks to be written to disk (but not those that are currently in the process of being written).
    * Key is the chunk position, value is the NBT to write.
    */
   private final Map<ChunkPos, NBTTagCompound> chunksToSave = Maps.newHashMap();
   /** Save directory for chunks using the Anvil format */
   public final File chunkSaveLocation;
   private final DataFixer fixer;
   private LegacyStructureDataUtil field_208031_e;
   private boolean flushing;

   public AnvilChunkLoader(File chunkSaveLocationIn, DataFixer fixerIn) {
      this.chunkSaveLocation = chunkSaveLocationIn;
      this.fixer = fixerIn;
   }

   @Nullable
   private NBTTagCompound func_208030_a(IWorld worldIn, int chunkX, int chunkZ) throws IOException {
      return this.func_212146_a(worldIn.getDimension().getType(), worldIn.getMapStorage(), chunkX, chunkZ);
   }

   @Nullable
   private NBTTagCompound func_212146_a(DimensionType p_212146_1_, @Nullable WorldSavedDataStorage storage, int chunkX, int chunkZ) throws IOException {
      NBTTagCompound nbttagcompound = this.chunksToSave.get(new ChunkPos(chunkX, chunkZ));
      if (nbttagcompound != null) {
         return nbttagcompound;
      } else {
         DataInputStream datainputstream = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, chunkX, chunkZ);
         if (datainputstream == null) {
            return null;
         } else {
            NBTTagCompound nbttagcompound1 = CompressedStreamTools.read(datainputstream);
            datainputstream.close();
            int i = nbttagcompound1.contains("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : -1;
            if (i < 1493) {
               nbttagcompound1 = NBTUtil.update(this.fixer, DataFixTypes.CHUNK, nbttagcompound1, i, 1493);
               if (nbttagcompound1.getCompound("Level").getBoolean("hasLegacyStructureData")) {
                  this.func_212429_a(p_212146_1_, storage);
                  nbttagcompound1 = this.field_208031_e.func_212181_a(nbttagcompound1);
               }
            }

            nbttagcompound1 = NBTUtil.update(this.fixer, DataFixTypes.CHUNK, nbttagcompound1, Math.max(1493, i));
            if (i < 1631) {
               nbttagcompound1.setInt("DataVersion", 1631);
               this.addChunkToPending(new ChunkPos(chunkX, chunkZ), nbttagcompound1);
            }

            return nbttagcompound1;
         }
      }
   }

   public void func_212429_a(DimensionType p_212429_1_, @Nullable WorldSavedDataStorage p_212429_2_) {
      if (this.field_208031_e == null) {
         this.field_208031_e = LegacyStructureDataUtil.func_212183_a(p_212429_1_, p_212429_2_);
      }

   }

   @Nullable
   public Chunk loadChunk(IWorld worldIn, int x, int z, Consumer<Chunk> consumer) throws IOException {
      NBTTagCompound nbttagcompound = this.func_208030_a(worldIn, x, z);
      if (nbttagcompound == null) {
         return null;
      } else {
         Chunk chunk = this.checkedReadChunkFromNBT(worldIn, x, z, nbttagcompound);
         if (chunk != null) {
            consumer.accept(chunk);
            this.readEntitiesFromNBT(nbttagcompound.getCompound("Level"), chunk);
         }

         return chunk;
      }
   }

   @Nullable
   public ChunkPrimer loadChunkPrimer(IWorld worldIn, int x, int z, Consumer<IChunk> consumer) throws IOException {
      NBTTagCompound nbttagcompound;
      try {
         nbttagcompound = this.func_208030_a(worldIn, x, z);
      } catch (ReportedException reportedexception) {
         if (reportedexception.getCause() instanceof IOException) {
            throw (IOException)reportedexception.getCause();
         }

         throw reportedexception;
      }

      if (nbttagcompound == null) {
         return null;
      } else {
         ChunkPrimer chunkprimer = this.readChunkPrimerFromNBT(worldIn, x, z, nbttagcompound);
         if (chunkprimer != null) {
            consumer.accept(chunkprimer);
         }

         return chunkprimer;
      }
   }

   /**
    * Wraps readChunkFromNBT. Checks the coordinates and several NBT tags.
    */
   @Nullable
   protected Chunk checkedReadChunkFromNBT(IWorld worldIn, int x, int z, NBTTagCompound compound) {
      if (compound.contains("Level", 10) && compound.getCompound("Level").contains("Status", 8)) {
         ChunkStatus.Type chunkstatus$type = this.readChunkTypeFromNBT(compound);
         if (chunkstatus$type != ChunkStatus.Type.LEVELCHUNK) {
            return null;
         } else {
            NBTTagCompound nbttagcompound = compound.getCompound("Level");
            if (!nbttagcompound.contains("Sections", 9)) {
               LOGGER.error("Chunk file at {},{} is missing block data, skipping", x, z);
               return null;
            } else {
               Chunk chunk = this.readChunkFromNBT(worldIn, nbttagcompound);
               if (!chunk.isAtLocation(x, z)) {
                  LOGGER.error("Chunk file at {},{} is in the wrong location; relocating. (Expected {}, {}, got {}, {})", x, z, x, z, chunk.x, chunk.z);
                  nbttagcompound.setInt("xPos", x);
                  nbttagcompound.setInt("zPos", z);
                  chunk = this.readChunkFromNBT(worldIn, nbttagcompound);
               }

               return chunk;
            }
         }
      } else {
         LOGGER.error("Chunk file at {},{} is missing level data, skipping", x, z);
         return null;
      }
   }

   @Nullable
   protected ChunkPrimer readChunkPrimerFromNBT(IWorld worldIn, int x, int z, NBTTagCompound nbt) {
      if (nbt.contains("Level", 10) && nbt.getCompound("Level").contains("Status", 8)) {
         ChunkStatus.Type chunkstatus$type = this.readChunkTypeFromNBT(nbt);
         if (chunkstatus$type == ChunkStatus.Type.LEVELCHUNK) {
            return new ChunkPrimerWrapper(this.checkedReadChunkFromNBT(worldIn, x, z, nbt));
         } else {
            NBTTagCompound nbttagcompound = nbt.getCompound("Level");
            return this.readChunkPrimerFromNBT(worldIn, nbttagcompound);
         }
      } else {
         LOGGER.error("Chunk file at {},{} is missing level data, skipping", x, z);
         return null;
      }
   }

   public void saveChunk(World worldIn, IChunk chunkIn) throws IOException, SessionLockException {
      worldIn.checkSessionLock();

      try {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         nbttagcompound.setInt("DataVersion", 1631);
         ChunkPos chunkpos = chunkIn.getPos();
         nbttagcompound.setTag("Level", nbttagcompound1);
         if (chunkIn.getStatus().getType() == ChunkStatus.Type.LEVELCHUNK) {
            this.writeChunkToNBT((Chunk)chunkIn, worldIn, nbttagcompound1);
         } else {
            NBTTagCompound nbttagcompound2 = this.func_208030_a(worldIn, chunkpos.x, chunkpos.z);
            if (nbttagcompound2 != null && this.readChunkTypeFromNBT(nbttagcompound2) == ChunkStatus.Type.LEVELCHUNK) {
               return;
            }

            this.func_202156_a((ChunkPrimer)chunkIn, worldIn, nbttagcompound1);
         }

         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkDataEvent.Save(chunkIn, nbttagcompound));

         this.addChunkToPending(chunkpos, nbttagcompound);
      } catch (Exception exception) {
         LOGGER.error("Failed to save chunk", (Throwable)exception);
      }

   }

   protected void addChunkToPending(ChunkPos pos, NBTTagCompound compound) {
      this.chunksToSave.put(pos, compound);
      ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
   }

   /**
    * Writes one queued IO action.
    *  
    * @return true if there are more IO actions to perform afterwards, or false if there are none (and this instance of
    * IThreadedFileIO should be removed from the queued list)
    */
   public boolean writeNextIO() {
      Iterator<Entry<ChunkPos, NBTTagCompound>> iterator = this.chunksToSave.entrySet().iterator();
      if (!iterator.hasNext()) {
         if (this.flushing) {
            LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", (Object)this.chunkSaveLocation.getName());
         }

         return false;
      } else {
         Entry<ChunkPos, NBTTagCompound> entry = iterator.next();
         iterator.remove();
         ChunkPos chunkpos = entry.getKey();
         NBTTagCompound nbttagcompound = entry.getValue();
         if (nbttagcompound == null) {
            return true;
         } else {
            try {
               DataOutputStream dataoutputstream = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, chunkpos.x, chunkpos.z);
               CompressedStreamTools.write(nbttagcompound, dataoutputstream);
               dataoutputstream.close();
               if (this.field_208031_e != null) {
                  this.field_208031_e.func_208216_a(chunkpos.asLong());
               }
            } catch (Exception exception) {
               LOGGER.error("Failed to save chunk", (Throwable)exception);
            }

            return true;
         }
      }
   }

   private ChunkStatus.Type readChunkTypeFromNBT(@Nullable NBTTagCompound nbt) {
      if (nbt != null) {
         ChunkStatus chunkstatus = ChunkStatus.getByName(nbt.getCompound("Level").getString("Status"));
         if (chunkstatus != null) {
            return chunkstatus.getType();
         }
      }

      return ChunkStatus.Type.PROTOCHUNK;
   }

   /**
    * Flushes all pending chunks fully back to disk
    */
   public void flush() {
      try {
         this.flushing = true;

         while(this.writeNextIO()) {
            ;
         }
      } finally {
         this.flushing = false;
      }

   }

   private void func_202156_a(ChunkPrimer primer, World worldIn, NBTTagCompound compound) {
      int i = primer.getPos().x;
      int j = primer.getPos().z;
      compound.setInt("xPos", i);
      compound.setInt("zPos", j);
      compound.setLong("LastUpdate", worldIn.getGameTime());
      compound.setLong("InhabitedTime", primer.getInhabitedTime());
      compound.setString("Status", primer.getStatus().getName());
      UpgradeData upgradedata = primer.getUpgradeData();
      if (!upgradedata.isEmpty()) {
         compound.setTag("UpgradeData", upgradedata.write());
      }

      ChunkSection[] achunksection = primer.getSections();
      NBTTagList nbttaglist = this.writeChunkSectionsToNBT(worldIn, achunksection);
      compound.setTag("Sections", nbttaglist);
      Biome[] abiome = primer.getBiomes();
      int[] aint = abiome != null ? new int[abiome.length] : new int[0];
      if (abiome != null) {
         for(int k = 0; k < abiome.length; ++k) {
            aint[k] = IRegistry.field_212624_m.getId(abiome[k]);
         }
      }

      compound.setIntArray("Biomes", aint);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(NBTTagCompound nbttagcompound : primer.getEntities()) {
         nbttaglist1.add((INBTBase)nbttagcompound);
      }

      compound.setTag("Entities", nbttaglist1);
      NBTTagList nbttaglist2 = new NBTTagList();

      for(BlockPos blockpos : primer.getTileEntityPositions()) {
         TileEntity tileentity = primer.getTileEntity(blockpos);
         if (tileentity != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            tileentity.write(nbttagcompound1);
            nbttaglist2.add((INBTBase)nbttagcompound1);
         } else {
            nbttaglist2.add((INBTBase)primer.getDeferredTileEntity(blockpos));
         }
      }

      compound.setTag("TileEntities", nbttaglist2);
      compound.setTag("Lights", listArrayToTag(primer.getPackedLightPositions()));
      compound.setTag("PostProcessing", listArrayToTag(primer.getPackedPositions()));
      compound.setTag("ToBeTicked", primer.getBlocksToBeTicked().write());
      compound.setTag("LiquidsToBeTicked", primer.func_212247_j().write());
      NBTTagCompound nbttagcompound2 = new NBTTagCompound();

      for(Heightmap.Type heightmap$type : primer.getHeightMapKeys()) {
         nbttagcompound2.setTag(heightmap$type.getId(), new NBTTagLongArray(primer.getHeightmap(heightmap$type).getDataArray()));
      }

      compound.setTag("Heightmaps", nbttagcompound2);
      NBTTagCompound nbttagcompound3 = new NBTTagCompound();

      for(GenerationStage.Carving generationstage$carving : GenerationStage.Carving.values()) {
         nbttagcompound3.setByteArray(generationstage$carving.toString(), primer.getCarvingMask(generationstage$carving).toByteArray());
      }

      compound.setTag("CarvingMasks", nbttagcompound3);
      compound.setTag("Structures", this.createStructuresTag(i, j, primer.getStructureStarts(), primer.getStructureReferences()));
   }

   /**
    * Writes the Chunk passed as an argument to the NBTTagCompound also passed, using the World argument to retrieve the
    * Chunk's last update time.
    */
   private void writeChunkToNBT(Chunk chunkIn, World worldIn, NBTTagCompound compound) {
      compound.setInt("xPos", chunkIn.x);
      compound.setInt("zPos", chunkIn.z);
      compound.setLong("LastUpdate", worldIn.getGameTime());
      compound.setLong("InhabitedTime", chunkIn.getInhabitedTime());
      compound.setString("Status", chunkIn.getStatus().getName());
      UpgradeData upgradedata = chunkIn.getUpgradeData();
      if (!upgradedata.isEmpty()) {
         compound.setTag("UpgradeData", upgradedata.write());
      }

      ChunkSection[] achunksection = chunkIn.getSections();
      NBTTagList nbttaglist = this.writeChunkSectionsToNBT(worldIn, achunksection);
      compound.setTag("Sections", nbttaglist);
      Biome[] abiome = chunkIn.getBiomes();
      int[] aint = new int[abiome.length];

      for(int i = 0; i < abiome.length; ++i) {
         aint[i] = IRegistry.field_212624_m.getId(abiome[i]);
      }

      compound.setIntArray("Biomes", aint);
      chunkIn.setHasEntities(false);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(int j = 0; j < chunkIn.getEntityLists().length; ++j) {
         for(Entity entity : chunkIn.getEntityLists()[j]) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            try {
            if (entity.writeUnlessPassenger(nbttagcompound)) {
               chunkIn.setHasEntities(true);
               nbttaglist1.add((INBTBase)nbttagcompound);
            }
            } catch (Exception e) {
               LogManager.getLogger().error("An Entity type {} has thrown an exception trying to write state. It will not persist. Report this to the mod author", entity.getType(), e);
            }
         }
      }

      compound.setTag("Entities", nbttaglist1);
      NBTTagList nbttaglist2 = new NBTTagList();

      for(BlockPos blockpos : chunkIn.getTileEntitiesPos()) {
         TileEntity tileentity = chunkIn.getTileEntity(blockpos);
         if (tileentity != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            try {
               tileentity.write(nbttagcompound1);
            } catch (Exception e) {
               LogManager.getLogger().error("A TileEntity type {} has thrown an exception trying to write state. It will not persist, Report this to the mod author", tileentity.getClass().getName(), e);
            }
            nbttagcompound1.setBoolean("keepPacked", false);
            nbttaglist2.add((INBTBase)nbttagcompound1);
         } else {
            NBTTagCompound nbttagcompound3 = chunkIn.getDeferredTileEntity(blockpos);
            if (nbttagcompound3 != null) {
               nbttagcompound3.setBoolean("keepPacked", true);
               nbttaglist2.add((INBTBase)nbttagcompound3);
            }
         }
      }

      compound.setTag("TileEntities", nbttaglist2);
      if (worldIn.getPendingBlockTicks() instanceof ServerTickList) {
         compound.setTag("TileTicks", ((ServerTickList)worldIn.getPendingBlockTicks()).write(chunkIn));
      }

      if (worldIn.getPendingFluidTicks() instanceof ServerTickList) {
         compound.setTag("LiquidTicks", ((ServerTickList)worldIn.getPendingFluidTicks()).write(chunkIn));
      }

      compound.setTag("PostProcessing", listArrayToTag(chunkIn.getPackedPositions()));
      if (chunkIn.getBlocksToBeTicked() instanceof ChunkPrimerTickList) {
         compound.setTag("ToBeTicked", ((ChunkPrimerTickList)chunkIn.getBlocksToBeTicked()).write());
      }

      if (chunkIn.func_212247_j() instanceof ChunkPrimerTickList) {
         compound.setTag("LiquidsToBeTicked", ((ChunkPrimerTickList)chunkIn.func_212247_j()).write());
      }

      NBTTagCompound nbttagcompound2 = new NBTTagCompound();

      for(Heightmap.Type heightmap$type : chunkIn.getHeightmaps()) {
         if (heightmap$type.getUsage() == Heightmap.Usage.LIVE_WORLD) {
            nbttagcompound2.setTag(heightmap$type.getId(), new NBTTagLongArray(chunkIn.getHeightmap(heightmap$type).getDataArray()));
         }
      }

      compound.setTag("Heightmaps", nbttagcompound2);
      compound.setTag("Structures", this.createStructuresTag(chunkIn.x, chunkIn.z, chunkIn.getStructureStarts(), chunkIn.getStructureReferences()));

      try
      {
          final NBTTagCompound capTag = chunkIn.writeCapsToNBT();
          if (capTag != null) compound.setTag("ForgeCaps", capTag);
      }
      catch (Exception exception)
      {
          org.apache.logging.log4j.LogManager.getLogger().error("A capability provider has thrown an exception trying to write state. It will not persist. Report this to the mod author", exception);
      }
   }

   /**
    * Reads the data stored in the passed NBTTagCompound and creates a Chunk with that data in the passed World. Returns
    * the created Chunk.
    */
   private Chunk readChunkFromNBT(IWorld worldIn, NBTTagCompound compound) {
      int i = compound.getInt("xPos");
      int j = compound.getInt("zPos");
      Biome[] abiome = new Biome[256];
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      if (compound.contains("Biomes", 11)) {
         int[] aint = compound.getIntArray("Biomes");

         for(int k = 0; k < aint.length; ++k) {
            abiome[k] = IRegistry.field_212624_m.get(aint[k]);
            if (abiome[k] == null) {
               abiome[k] = worldIn.getChunkProvider().getChunkGenerator().getBiomeProvider().getBiome(blockpos$mutableblockpos.setPos((k & 15) + (i << 4), 0, (k >> 4 & 15) + (j << 4)), Biomes.PLAINS);
            }
         }
      } else {
         for(int i1 = 0; i1 < abiome.length; ++i1) {
            abiome[i1] = worldIn.getChunkProvider().getChunkGenerator().getBiomeProvider().getBiome(blockpos$mutableblockpos.setPos((i1 & 15) + (i << 4), 0, (i1 >> 4 & 15) + (j << 4)), Biomes.PLAINS);
         }
      }

      UpgradeData upgradedata = compound.contains("UpgradeData", 10) ? new UpgradeData(compound.getCompound("UpgradeData")) : UpgradeData.EMPTY;
      ChunkPrimerTickList<Block> chunkprimerticklist1 = new ChunkPrimerTickList<>((p_205531_0_) -> {
         return p_205531_0_.getDefaultState().isAir();
      }, IRegistry.field_212618_g::getKey, IRegistry.field_212618_g::get, new ChunkPos(i, j));
      ChunkPrimerTickList<Fluid> chunkprimerticklist = new ChunkPrimerTickList<>((p_206242_0_) -> {
         return p_206242_0_ == Fluids.EMPTY;
      }, IRegistry.field_212619_h::getKey, IRegistry.field_212619_h::get, new ChunkPos(i, j));
      long l = compound.getLong("InhabitedTime");
      Chunk chunk = new Chunk(worldIn.getWorld(), i, j, abiome, upgradedata, chunkprimerticklist1, chunkprimerticklist, l);
      chunk.setStatus(compound.getString("Status"));
      NBTTagList nbttaglist = compound.getList("Sections", 10);
      chunk.setSections(this.readSectionsFromNBT(worldIn, nbttaglist));
      NBTTagCompound nbttagcompound = compound.getCompound("Heightmaps");

      for(Heightmap.Type heightmap$type : Heightmap.Type.values()) {
         if (heightmap$type.getUsage() == Heightmap.Usage.LIVE_WORLD) {
            String s = heightmap$type.getId();
            if (nbttagcompound.contains(s, 12)) {
               chunk.setHeightmap(heightmap$type, nbttagcompound.getLongArray(s));
            } else {
               chunk.getHeightmap(heightmap$type).generate();
            }
         }
      }

      NBTTagCompound nbttagcompound1 = compound.getCompound("Structures");
      chunk.setStructureStarts(this.readStructureStartsFromNBT(worldIn, nbttagcompound1));
      chunk.setStructureReferences(this.readStructureReferencesFromNBT(nbttagcompound1));
      NBTTagList nbttaglist1 = compound.getList("PostProcessing", 9);

      for(int j1 = 0; j1 < nbttaglist1.size(); ++j1) {
         NBTTagList nbttaglist2 = nbttaglist1.getList(j1);

         for(int k1 = 0; k1 < nbttaglist2.size(); ++k1) {
            chunk.addPackedPos(nbttaglist2.getShort(k1), j1);
         }
      }

      chunkprimerticklist1.readToBeTickedListFromNBT(compound.getList("ToBeTicked", 9));
      chunkprimerticklist.readToBeTickedListFromNBT(compound.getList("LiquidsToBeTicked", 9));
      if (compound.getBoolean("shouldSave")) {
         chunk.setModified(true);
      }

      if (compound.hasKey("ForgeCaps")) {
          chunk.readCapsFromNBT(compound.getCompound("ForgeCaps"));
      }

      return chunk;
   }

   private void readEntitiesFromNBT(NBTTagCompound nbt, Chunk chunkIn) {
      NBTTagList nbttaglist = nbt.getList("Entities", 10);
      World world = chunkIn.getWorld();

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
         readChunkEntity(nbttagcompound, world, chunkIn);
         chunkIn.setHasEntities(true);
      }

      NBTTagList nbttaglist1 = nbt.getList("TileEntities", 10);

      for(int j = 0; j < nbttaglist1.size(); ++j) {
         NBTTagCompound nbttagcompound1 = nbttaglist1.getCompound(j);
         boolean flag = nbttagcompound1.getBoolean("keepPacked");
         if (flag) {
            chunkIn.addTileEntity(nbttagcompound1);
         } else {
            TileEntity tileentity = TileEntity.create(nbttagcompound1);
            if (tileentity != null) {
               chunkIn.addTileEntity(tileentity);
            }
         }
      }

      if (nbt.contains("TileTicks", 9) && world.getPendingBlockTicks() instanceof ServerTickList) {
         ((ServerTickList)world.getPendingBlockTicks()).read(nbt.getList("TileTicks", 10));
      }

      if (nbt.contains("LiquidTicks", 9) && world.getPendingFluidTicks() instanceof ServerTickList) {
         ((ServerTickList)world.getPendingFluidTicks()).read(nbt.getList("LiquidTicks", 10));
      }

   }

   private ChunkPrimer readChunkPrimerFromNBT(IWorld worldIn, NBTTagCompound nbt) {
      int i = nbt.getInt("xPos");
      int j = nbt.getInt("zPos");
      Biome[] abiome = new Biome[256];
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      if (nbt.contains("Biomes", 11)) {
         int[] aint = nbt.getIntArray("Biomes");

         for(int k = 0; k < aint.length; ++k) {
            abiome[k] = IRegistry.field_212624_m.get(aint[k]);
            if (abiome[k] == null) {
               abiome[k] = worldIn.getChunkProvider().getChunkGenerator().getBiomeProvider().getBiome(blockpos$mutableblockpos.setPos((k & 15) + (i << 4), 0, (k >> 4 & 15) + (j << 4)), Biomes.PLAINS);
            }
         }
      } else {
         for(int l1 = 0; l1 < abiome.length; ++l1) {
            abiome[l1] = worldIn.getChunkProvider().getChunkGenerator().getBiomeProvider().getBiome(blockpos$mutableblockpos.setPos((l1 & 15) + (i << 4), 0, (l1 >> 4 & 15) + (j << 4)), Biomes.PLAINS);
         }
      }

      UpgradeData upgradedata = nbt.contains("UpgradeData", 10) ? new UpgradeData(nbt.getCompound("UpgradeData")) : UpgradeData.EMPTY;
      ChunkPrimer chunkprimer = new ChunkPrimer(i, j, upgradedata);
      chunkprimer.setBiomes(abiome);
      chunkprimer.setInhabitedTime(nbt.getLong("InhabitedTime"));
      chunkprimer.setStatus(nbt.getString("Status"));
      NBTTagList nbttaglist = nbt.getList("Sections", 10);
      chunkprimer.setChunkSections(this.readSectionsFromNBT(worldIn, nbttaglist));
      NBTTagList nbttaglist1 = nbt.getList("Entities", 10);

      for(int l = 0; l < nbttaglist1.size(); ++l) {
         chunkprimer.addEntity(nbttaglist1.getCompound(l));
      }

      NBTTagList nbttaglist3 = nbt.getList("TileEntities", 10);

      for(int i1 = 0; i1 < nbttaglist3.size(); ++i1) {
         NBTTagCompound nbttagcompound = nbttaglist3.getCompound(i1);
         chunkprimer.addTileEntity(nbttagcompound);
      }

      NBTTagList nbttaglist4 = nbt.getList("Lights", 9);

      for(int i2 = 0; i2 < nbttaglist4.size(); ++i2) {
         NBTTagList nbttaglist2 = nbttaglist4.getList(i2);

         for(int j1 = 0; j1 < nbttaglist2.size(); ++j1) {
            chunkprimer.addLightValue(nbttaglist2.getShort(j1), i2);
         }
      }

      NBTTagList nbttaglist5 = nbt.getList("PostProcessing", 9);

      for(int j2 = 0; j2 < nbttaglist5.size(); ++j2) {
         NBTTagList nbttaglist6 = nbttaglist5.getList(j2);

         for(int k1 = 0; k1 < nbttaglist6.size(); ++k1) {
            chunkprimer.func_201636_b(nbttaglist6.getShort(k1), j2);
         }
      }

      chunkprimer.getBlocksToBeTicked().readToBeTickedListFromNBT(nbt.getList("ToBeTicked", 9));
      chunkprimer.func_212247_j().readToBeTickedListFromNBT(nbt.getList("LiquidsToBeTicked", 9));
      NBTTagCompound nbttagcompound1 = nbt.getCompound("Heightmaps");

      for(String s1 : nbttagcompound1.keySet()) {
         chunkprimer.setHeightMap(Heightmap.Type.func_203501_a(s1), nbttagcompound1.getLongArray(s1));
      }

      NBTTagCompound nbttagcompound2 = nbt.getCompound("Structures");
      chunkprimer.setStructureStarts(this.readStructureStartsFromNBT(worldIn, nbttagcompound2));
      chunkprimer.setStructureReferences(this.readStructureReferencesFromNBT(nbttagcompound2));
      NBTTagCompound nbttagcompound3 = nbt.getCompound("CarvingMasks");

      for(String s : nbttagcompound3.keySet()) {
         GenerationStage.Carving generationstage$carving = GenerationStage.Carving.valueOf(s);
         chunkprimer.setCarvingMask(generationstage$carving, BitSet.valueOf(nbttagcompound3.getByteArray(s)));
      }

      return chunkprimer;
   }

   private NBTTagList writeChunkSectionsToNBT(World worldIn, ChunkSection[] chunkSections) {
      NBTTagList nbttaglist = new NBTTagList();
      boolean flag = worldIn.dimension.hasSkyLight();

      for(ChunkSection chunksection : chunkSections) {
         if (chunksection != Chunk.EMPTY_SECTION) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Y", (byte)(chunksection.getYLocation() >> 4 & 255));
            chunksection.getData().writeChunkPalette(nbttagcompound, "Palette", "BlockStates");
            nbttagcompound.setByteArray("BlockLight", chunksection.getBlockLight().getData());
            if (flag) {
               nbttagcompound.setByteArray("SkyLight", chunksection.getSkyLight().getData());
            } else {
               nbttagcompound.setByteArray("SkyLight", new byte[chunksection.getBlockLight().getData().length]);
            }

            nbttaglist.add((INBTBase)nbttagcompound);
         }
      }

      return nbttaglist;
   }

   private ChunkSection[] readSectionsFromNBT(IWorldReaderBase worldIn, NBTTagList nbt) {
      int i = 16;
      ChunkSection[] achunksection = new ChunkSection[16];
      boolean flag = worldIn.getDimension().hasSkyLight();

      for(int j = 0; j < nbt.size(); ++j) {
         NBTTagCompound nbttagcompound = nbt.getCompound(j);
         int k = nbttagcompound.getByte("Y");
         ChunkSection chunksection = new ChunkSection(k << 4, flag);
         chunksection.getData().readBlockStates(nbttagcompound, "Palette", "BlockStates");
         chunksection.setBlockLight(new NibbleArray(nbttagcompound.getByteArray("BlockLight")));
         if (flag) {
            chunksection.setSkyLight(new NibbleArray(nbttagcompound.getByteArray("SkyLight")));
         }

         chunksection.recalculateRefCounts();
         achunksection[k] = chunksection;
      }

      return achunksection;
   }

   private NBTTagCompound createStructuresTag(int chunkX, int chunkZ, Map<String, StructureStart> structureStarts, Map<String, LongSet> structureReferences) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();

      for(Entry<String, StructureStart> entry : structureStarts.entrySet()) {
         nbttagcompound1.setTag(entry.getKey(), entry.getValue().write(chunkX, chunkZ));
      }

      nbttagcompound.setTag("Starts", nbttagcompound1);
      NBTTagCompound nbttagcompound2 = new NBTTagCompound();

      for(Entry<String, LongSet> entry1 : structureReferences.entrySet()) {
         nbttagcompound2.setTag(entry1.getKey(), new NBTTagLongArray(entry1.getValue()));
      }

      nbttagcompound.setTag("References", nbttagcompound2);
      return nbttagcompound;
   }

   private Map<String, StructureStart> readStructureStartsFromNBT(IWorld worldIn, NBTTagCompound nbt) {
      Map<String, StructureStart> map = Maps.newHashMap();
      NBTTagCompound nbttagcompound = nbt.getCompound("Starts");

      for(String s : nbttagcompound.keySet()) {
         map.put(s, StructureIO.func_202602_a(nbttagcompound.getCompound(s), worldIn));
      }

      return map;
   }

   private Map<String, LongSet> readStructureReferencesFromNBT(NBTTagCompound nbt) {
      Map<String, LongSet> map = Maps.newHashMap();
      NBTTagCompound nbttagcompound = nbt.getCompound("References");

      for(String s : nbttagcompound.keySet()) {
         map.put(s, new LongOpenHashSet(nbttagcompound.getLongArray(s)));
      }

      return map;
   }

   public static NBTTagList listArrayToTag(ShortList[] list) {
      NBTTagList nbttaglist = new NBTTagList();

      for(ShortList shortlist : list) {
         NBTTagList nbttaglist1 = new NBTTagList();
         if (shortlist != null) {
            for(Short oshort : shortlist) {
               nbttaglist1.add((INBTBase)(new NBTTagShort(oshort)));
            }
         }

         nbttaglist.add((INBTBase)nbttaglist1);
      }

      return nbttaglist;
   }

   @Nullable
   private static Entity createEntityFromNBT(NBTTagCompound compound, World worldIn, Function<Entity, Entity> func) {
      Entity entity = createEntityFromNBT(compound, worldIn);
      if (entity == null) {
         return null;
      } else {
         entity = func.apply(entity);
         if (entity != null && compound.contains("Passengers", 9)) {
            NBTTagList nbttaglist = compound.getList("Passengers", 10);

            for(int i = 0; i < nbttaglist.size(); ++i) {
               Entity entity1 = createEntityFromNBT(nbttaglist.getCompound(i), worldIn, func);
               if (entity1 != null) {
                  entity1.startRiding(entity, true);
               }
            }
         }

         return entity;
      }
   }

   @Nullable
   public static Entity readChunkEntity(NBTTagCompound compound, World worldIn, Chunk chunkIn) {
      return createEntityFromNBT(compound, worldIn, (p_206241_1_) -> {
         chunkIn.addEntity(p_206241_1_);
         return p_206241_1_;
      });
   }

   @Nullable
   public static Entity readWorldEntityPos(NBTTagCompound compound, World worldIn, double x, double y, double z, boolean attemptSpawn) {
      return createEntityFromNBT(compound, worldIn, (p_206238_8_) -> {
         p_206238_8_.setLocationAndAngles(x, y, z, p_206238_8_.rotationYaw, p_206238_8_.rotationPitch);
         return attemptSpawn && !worldIn.spawnEntity(p_206238_8_) ? null : p_206238_8_;
      });
   }

   @Nullable
   public static Entity readWorldEntity(NBTTagCompound compound, World worldIn, boolean p_186051_2_) {
      return createEntityFromNBT(compound, worldIn, (p_206239_2_) -> {
         return p_186051_2_ && !worldIn.spawnEntity(p_206239_2_) ? null : p_206239_2_;
      });
   }

   @Nullable
   protected static Entity createEntityFromNBT(NBTTagCompound compound, World worldIn) {
      try {
         return EntityType.create(compound, worldIn);
      } catch (RuntimeException runtimeexception) {
         LOGGER.warn("Exception loading entity: ", (Throwable)runtimeexception);
         return null;
      }
   }

   public static void spawnEntity(Entity entityIn, IWorld worldIn) {
      if (worldIn.spawnEntity(entityIn) && entityIn.isBeingRidden()) {
         for(Entity entity : entityIn.getPassengers()) {
            spawnEntity(entity, worldIn);
         }
      }

   }

   public boolean convert(ChunkPos p_212147_1_, DimensionType p_212147_2_, WorldSavedDataStorage p_212147_3_) {
      boolean flag = false;

      try {
         this.func_212146_a(p_212147_2_, p_212147_3_, p_212147_1_.x, p_212147_1_.z);

         while(this.writeNextIO()) {
            flag = true;
         }
      } catch (IOException var6) {
         ;
      }

      return flag;
   }

   public int getPendingSaveCount() {
      return this.chunksToSave.size();
   }
}