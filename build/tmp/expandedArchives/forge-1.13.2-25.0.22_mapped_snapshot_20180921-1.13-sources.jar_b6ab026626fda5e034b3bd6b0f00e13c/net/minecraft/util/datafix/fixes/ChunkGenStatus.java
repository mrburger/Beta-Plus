package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class ChunkGenStatus extends DataFix {
   public ChunkGenStatus(Schema p_i49674_1_, boolean p_i49674_2_) {
      super(p_i49674_1_, p_i49674_2_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
      Type<?> type1 = this.getOutputSchema().getType(TypeReferences.CHUNK);
      Type<?> type2 = type.findFieldType("Level");
      Type<?> type3 = type1.findFieldType("Level");
      Type<?> type4 = type2.findFieldType("TileTicks");
      OpticFinder<?> opticfinder = DSL.fieldFinder("Level", type2);
      OpticFinder<?> opticfinder1 = DSL.fieldFinder("TileTicks", type4);
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("ChunkToProtoChunkFix", type, this.getOutputSchema().getType(TypeReferences.CHUNK), (p_209732_3_) -> {
         return p_209732_3_.updateTyped(opticfinder, type3, (p_207915_2_) -> {
            Optional<? extends Stream<? extends Dynamic<?>>> optional = p_207915_2_.getOptionalTyped(opticfinder1).map(Typed::write).flatMap(Dynamic::getStream);
            Dynamic<?> dynamic = p_207915_2_.get(DSL.remainderFinder());
            boolean flag = dynamic.getBoolean("TerrainPopulated") && (!dynamic.get("LightPopulated").flatMap(Dynamic::getNumberValue).isPresent() || dynamic.getBoolean("LightPopulated"));
            dynamic = dynamic.set("Status", dynamic.createString(flag ? "mobs_spawned" : "empty"));
            dynamic = dynamic.set("hasLegacyStructureData", dynamic.createBoolean(true));
            Dynamic<?> dynamic1;
            if (flag) {
               Optional<ByteBuffer> optional1 = dynamic.get("Biomes").flatMap(Dynamic::getByteBuffer);
               if (optional1.isPresent()) {
                  ByteBuffer bytebuffer = optional1.get();
                  int[] aint = new int[256];

                  for(int i = 0; i < aint.length; ++i) {
                     if (i < bytebuffer.capacity()) {
                        aint[i] = bytebuffer.get(i) & 255;
                     }
                  }

                  dynamic = dynamic.set("Biomes", dynamic.createIntList(Arrays.stream(aint)));
               }

               final Dynamic<?> dynamic_f = dynamic;
               List<Dynamic<?>> list = IntStream.range(0, 16).mapToObj((p_211428_1_) -> {
                  return dynamic_f.createList(Stream.empty());
               }).collect(Collectors.toList());
               if (optional.isPresent()) {
                  optional.get().forEach((p_211426_2_) -> {
                     int j = p_211426_2_.getInt("x");
                     int k = p_211426_2_.getInt("y");
                     int l = p_211426_2_.getInt("z");
                     short short1 = packOffsetCoordinates(j, k, l);
                     list.set(k >> 4, list.get(k >> 4).merge(dynamic_f.createShort(short1)));
                  });
                  dynamic = dynamic.set("ToBeTicked", dynamic.createList(list.stream()));
               }

               dynamic1 = p_207915_2_.set(DSL.remainderFinder(), dynamic).write();
            } else {
               dynamic1 = dynamic;
            }

            return type3.readTyped(dynamic1).getSecond().orElseThrow(() -> {
               return new IllegalStateException("Could not read the new chunk");
            });
         });
      }), this.writeAndRead("Structure biome inject", this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE)));
   }

   private static short packOffsetCoordinates(int p_210975_0_, int p_210975_1_, int p_210975_2_) {
      return (short)(p_210975_0_ & 15 | (p_210975_1_ & 15) << 4 | (p_210975_2_ & 15) << 8);
   }
}