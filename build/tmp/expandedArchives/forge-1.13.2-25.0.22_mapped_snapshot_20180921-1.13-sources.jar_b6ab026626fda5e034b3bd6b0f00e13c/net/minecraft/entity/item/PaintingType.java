package net.minecraft.entity.item;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PaintingType {
   public static final PaintingType KEBAB = register("kebab", 16, 16, 0, 0);
   public static final PaintingType AZTEC = register("aztec", 16, 16, 16, 0);
   public static final PaintingType ALBAN = register("alban", 16, 16, 32, 0);
   public static final PaintingType AZTEC2 = register("aztec2", 16, 16, 48, 0);
   public static final PaintingType BOMB = register("bomb", 16, 16, 64, 0);
   public static final PaintingType PLANT = register("plant", 16, 16, 80, 0);
   public static final PaintingType WASTELAND = register("wasteland", 16, 16, 96, 0);
   public static final PaintingType POOL = register("pool", 32, 16, 0, 32);
   public static final PaintingType COURBET = register("courbet", 32, 16, 32, 32);
   public static final PaintingType SEA = register("sea", 32, 16, 64, 32);
   public static final PaintingType SUNSET = register("sunset", 32, 16, 96, 32);
   public static final PaintingType CREEBET = register("creebet", 32, 16, 128, 32);
   public static final PaintingType WANDERER = register("wanderer", 16, 32, 0, 64);
   public static final PaintingType GRAHAM = register("graham", 16, 32, 16, 64);
   public static final PaintingType MATCH = register("match", 32, 32, 0, 128);
   public static final PaintingType BUST = register("bust", 32, 32, 32, 128);
   public static final PaintingType STAGE = register("stage", 32, 32, 64, 128);
   public static final PaintingType VOID = register("void", 32, 32, 96, 128);
   public static final PaintingType SKULL_AND_ROSES = register("skull_and_roses", 32, 32, 128, 128);
   public static final PaintingType WITHER = register("wither", 32, 32, 160, 128);
   public static final PaintingType FIGHTERS = register("fighters", 64, 32, 0, 96);
   public static final PaintingType POINTER = register("pointer", 64, 64, 0, 192);
   public static final PaintingType PIGSCENE = register("pigscene", 64, 64, 64, 192);
   public static final PaintingType BURNING_SKULL = register("burning_skull", 64, 64, 128, 192);
   public static final PaintingType SKELETON = register("skeleton", 64, 48, 192, 64);
   public static final PaintingType DONKEY_KONG = register("donkey_kong", 64, 48, 192, 112);
   private final int width;
   private final int height;
   private final int u;
   private final int v;

   public static void validateRegistry() {
   }

   public PaintingType(int widthIn, int heightIn, int uIn, int vIn) {
      this.width = widthIn;
      this.height = heightIn;
      this.u = uIn;
      this.v = vIn;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   @OnlyIn(Dist.CLIENT)
   public int getU() {
      return this.u;
   }

   @OnlyIn(Dist.CLIENT)
   public int getV() {
      return this.v;
   }

   public static PaintingType register(String id, int widthIn, int heightIn, int uIn, int vIn) {
      PaintingType paintingtype = new PaintingType(widthIn, heightIn, uIn, vIn);
      IRegistry.field_212620_i.put(new ResourceLocation(id), paintingtype);
      return paintingtype;
   }
}