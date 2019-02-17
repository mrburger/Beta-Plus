package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.dimension.DimensionType;

public class WorldChunkEnumerator {
   private static final Pattern REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
   private final File pathToWorld;
   private final Map<DimensionType, List<ChunkPos>> endChunks;

   public WorldChunkEnumerator(File p_i49790_1_) {
      this.pathToWorld = p_i49790_1_;
      Builder<DimensionType, List<ChunkPos>> builder = ImmutableMap.builder();

      for(DimensionType dimensiontype : DimensionType.func_212681_b()) {
         builder.put(dimensiontype, this.getAllChunkPos(dimensiontype));
      }

      this.endChunks = builder.build();
   }

   private List<ChunkPos> getAllChunkPos(DimensionType p_212153_1_) {
      ArrayList<ChunkPos> arraylist = Lists.newArrayList();
      File file1 = p_212153_1_.func_212679_a(this.pathToWorld);
      List<File> list = this.addRegionFiles(file1);

      for(File file2 : list) {
         arraylist.addAll(this.iterateRegionFile(file2));
      }

      list.sort(File::compareTo);
      return arraylist;
   }

   private List<ChunkPos> iterateRegionFile(File p_212150_1_) {
      List<ChunkPos> list = Lists.newArrayList();
      RegionFile regionfile = null;

      List<ChunkPos> arraylist;
      try {
         Matcher matcher = REGEX.matcher(p_212150_1_.getName());
         if (matcher.matches()) {
            int l = Integer.parseInt(matcher.group(1)) << 5;
            int i = Integer.parseInt(matcher.group(2)) << 5;
            regionfile = new RegionFile(p_212150_1_);

            for(int j = 0; j < 32; ++j) {
               for(int k = 0; k < 32; ++k) {
                  if (regionfile.doesChunkExist(j, k)) {
                     list.add(new ChunkPos(j + l, k + i));
                  }
               }
            }

            return list;
         }

         arraylist = list;
      } catch (Throwable var18) {
         arraylist = Lists.newArrayList();
         return arraylist;
      } finally {
         if (regionfile != null) {
            try {
               regionfile.close();
            } catch (IOException var17) {
               ;
            }
         }

      }

      return arraylist;
   }

   private List<File> addRegionFiles(File p_212155_1_) {
      File file1 = new File(p_212155_1_, "region");
      File[] afile = file1.listFiles((p_212152_0_, p_212152_1_) -> {
         return p_212152_1_.endsWith(".mca");
      });
      return afile != null ? Lists.newArrayList(afile) : Lists.newArrayList();
   }

   public List<ChunkPos> func_212541_a(DimensionType p_212541_1_) {
      return this.endChunks.get(p_212541_1_);
   }
}