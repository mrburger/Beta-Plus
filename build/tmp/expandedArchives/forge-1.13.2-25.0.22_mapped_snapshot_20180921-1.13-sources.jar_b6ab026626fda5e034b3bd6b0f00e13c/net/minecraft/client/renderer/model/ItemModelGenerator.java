package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemModelGenerator {
   public static final List<String> LAYERS = Lists.newArrayList("layer0", "layer1", "layer2", "layer3", "layer4");

   public ModelBlock makeItemModel(Function<ResourceLocation, TextureAtlasSprite> p_209579_1_, ModelBlock p_209579_2_) {
      Map<String, String> map = Maps.newHashMap();
      List<BlockPart> list = Lists.newArrayList();

      for(int i = 0; i < LAYERS.size(); ++i) {
         String s = LAYERS.get(i);
         if (!p_209579_2_.isTexturePresent(s)) {
            break;
         }

         String s1 = p_209579_2_.resolveTextureName(s);
         map.put(s, s1);
         TextureAtlasSprite textureatlassprite = p_209579_1_.apply(new ResourceLocation(s1));
         list.addAll(this.getBlockParts(i, s, textureatlassprite));
      }

      map.put("particle", p_209579_2_.isTexturePresent("particle") ? p_209579_2_.resolveTextureName("particle") : map.get("layer0"));
      ModelBlock modelblock = new ModelBlock((ResourceLocation)null, list, map, false, false, p_209579_2_.getAllTransforms(), p_209579_2_.getOverrides());
      modelblock.name = p_209579_2_.name;
      return modelblock;
   }

   private List<BlockPart> getBlockParts(int tintIndex, String p_178394_2_, TextureAtlasSprite p_178394_3_) {
      Map<EnumFacing, BlockPartFace> map = Maps.newHashMap();
      map.put(EnumFacing.SOUTH, new BlockPartFace((EnumFacing)null, tintIndex, p_178394_2_, new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
      map.put(EnumFacing.NORTH, new BlockPartFace((EnumFacing)null, tintIndex, p_178394_2_, new BlockFaceUV(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
      List<BlockPart> list = Lists.newArrayList();
      list.add(new BlockPart(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), map, (BlockPartRotation)null, true));
      list.addAll(this.getBlockParts(p_178394_3_, p_178394_2_, tintIndex));
      return list;
   }

   private List<BlockPart> getBlockParts(TextureAtlasSprite p_178397_1_, String p_178397_2_, int p_178397_3_) {
      float f = (float)p_178397_1_.getWidth();
      float f1 = (float)p_178397_1_.getHeight();
      List<BlockPart> list = Lists.newArrayList();

      for(ItemModelGenerator.Span itemmodelgenerator$span : this.getSpans(p_178397_1_)) {
         float f2 = 0.0F;
         float f3 = 0.0F;
         float f4 = 0.0F;
         float f5 = 0.0F;
         float f6 = 0.0F;
         float f7 = 0.0F;
         float f8 = 0.0F;
         float f9 = 0.0F;
         float f10 = 0.0F;
         float f11 = 0.0F;
         float f12 = (float)itemmodelgenerator$span.getMin();
         float f13 = (float)itemmodelgenerator$span.getMax();
         float f14 = (float)itemmodelgenerator$span.getAnchor();
         ItemModelGenerator.SpanFacing itemmodelgenerator$spanfacing = itemmodelgenerator$span.getFacing();
         switch(itemmodelgenerator$spanfacing) {
         case UP:
            f6 = f12;
            f2 = f12;
            f4 = f7 = f13 + 1.0F;
            f8 = f14;
            f3 = f14;
            f9 = f14;
            f5 = f14;
            f10 = 16.0F / f;
            f11 = 16.0F / (f1 - 1.0F);
            break;
         case DOWN:
            f9 = f14;
            f8 = f14;
            f6 = f12;
            f2 = f12;
            f4 = f7 = f13 + 1.0F;
            f3 = f14 + 1.0F;
            f5 = f14 + 1.0F;
            f10 = 16.0F / f;
            f11 = 16.0F / (f1 - 1.0F);
            break;
         case LEFT:
            f6 = f14;
            f2 = f14;
            f7 = f14;
            f4 = f14;
            f9 = f12;
            f3 = f12;
            f5 = f8 = f13 + 1.0F;
            f10 = 16.0F / (f - 1.0F);
            f11 = 16.0F / f1;
            break;
         case RIGHT:
            f7 = f14;
            f6 = f14;
            f2 = f14 + 1.0F;
            f4 = f14 + 1.0F;
            f9 = f12;
            f3 = f12;
            f5 = f8 = f13 + 1.0F;
            f10 = 16.0F / (f - 1.0F);
            f11 = 16.0F / f1;
         }

         float f15 = 16.0F / f;
         float f16 = 16.0F / f1;
         f2 = f2 * f15;
         f4 = f4 * f15;
         f3 = f3 * f16;
         f5 = f5 * f16;
         f3 = 16.0F - f3;
         f5 = 16.0F - f5;
         f6 = f6 * f10;
         f7 = f7 * f10;
         f8 = f8 * f11;
         f9 = f9 * f11;
         Map<EnumFacing, BlockPartFace> map = Maps.newHashMap();
         map.put(itemmodelgenerator$spanfacing.getFacing(), new BlockPartFace((EnumFacing)null, p_178397_3_, p_178397_2_, new BlockFaceUV(new float[]{f6, f8, f7, f9}, 0)));
         switch(itemmodelgenerator$spanfacing) {
         case UP:
            list.add(new BlockPart(new Vector3f(f2, f3, 7.5F), new Vector3f(f4, f3, 8.5F), map, (BlockPartRotation)null, true));
            break;
         case DOWN:
            list.add(new BlockPart(new Vector3f(f2, f5, 7.5F), new Vector3f(f4, f5, 8.5F), map, (BlockPartRotation)null, true));
            break;
         case LEFT:
            list.add(new BlockPart(new Vector3f(f2, f3, 7.5F), new Vector3f(f2, f5, 8.5F), map, (BlockPartRotation)null, true));
            break;
         case RIGHT:
            list.add(new BlockPart(new Vector3f(f4, f3, 7.5F), new Vector3f(f4, f5, 8.5F), map, (BlockPartRotation)null, true));
         }
      }

      return list;
   }

   private List<ItemModelGenerator.Span> getSpans(TextureAtlasSprite p_178393_1_) {
      int i = p_178393_1_.getWidth();
      int j = p_178393_1_.getHeight();
      List<ItemModelGenerator.Span> list = Lists.newArrayList();

      for(int k = 0; k < p_178393_1_.getFrameCount(); ++k) {
         for(int l = 0; l < j; ++l) {
            for(int i1 = 0; i1 < i; ++i1) {
               boolean flag = !this.func_199339_a(p_178393_1_, k, i1, l, i, j);
               this.func_199338_a(ItemModelGenerator.SpanFacing.UP, list, p_178393_1_, k, i1, l, i, j, flag);
               this.func_199338_a(ItemModelGenerator.SpanFacing.DOWN, list, p_178393_1_, k, i1, l, i, j, flag);
               this.func_199338_a(ItemModelGenerator.SpanFacing.LEFT, list, p_178393_1_, k, i1, l, i, j, flag);
               this.func_199338_a(ItemModelGenerator.SpanFacing.RIGHT, list, p_178393_1_, k, i1, l, i, j, flag);
            }
         }
      }

      return list;
   }

   private void func_199338_a(ItemModelGenerator.SpanFacing p_199338_1_, List<ItemModelGenerator.Span> p_199338_2_, TextureAtlasSprite p_199338_3_, int p_199338_4_, int p_199338_5_, int p_199338_6_, int p_199338_7_, int p_199338_8_, boolean p_199338_9_) {
      boolean flag = this.func_199339_a(p_199338_3_, p_199338_4_, p_199338_5_ + p_199338_1_.getXOffset(), p_199338_6_ + p_199338_1_.getYOffset(), p_199338_7_, p_199338_8_) && p_199338_9_;
      if (flag) {
         this.createOrExpandSpan(p_199338_2_, p_199338_1_, p_199338_5_, p_199338_6_);
      }

   }

   private void createOrExpandSpan(List<ItemModelGenerator.Span> p_178395_1_, ItemModelGenerator.SpanFacing p_178395_2_, int p_178395_3_, int p_178395_4_) {
      ItemModelGenerator.Span itemmodelgenerator$span = null;

      for(ItemModelGenerator.Span itemmodelgenerator$span1 : p_178395_1_) {
         if (itemmodelgenerator$span1.getFacing() == p_178395_2_) {
            int i = p_178395_2_.isHorizontal() ? p_178395_4_ : p_178395_3_;
            if (itemmodelgenerator$span1.getAnchor() == i) {
               itemmodelgenerator$span = itemmodelgenerator$span1;
               break;
            }
         }
      }

      int j = p_178395_2_.isHorizontal() ? p_178395_4_ : p_178395_3_;
      int k = p_178395_2_.isHorizontal() ? p_178395_3_ : p_178395_4_;
      if (itemmodelgenerator$span == null) {
         p_178395_1_.add(new ItemModelGenerator.Span(p_178395_2_, k, j));
      } else {
         itemmodelgenerator$span.expand(k);
      }

   }

   private boolean func_199339_a(TextureAtlasSprite p_199339_1_, int p_199339_2_, int p_199339_3_, int p_199339_4_, int p_199339_5_, int p_199339_6_) {
      return p_199339_3_ >= 0 && p_199339_4_ >= 0 && p_199339_3_ < p_199339_5_ && p_199339_4_ < p_199339_6_ ? p_199339_1_.isPixelTransparent(p_199339_2_, p_199339_3_, p_199339_4_) : true;
   }

   @OnlyIn(Dist.CLIENT)
   static class Span {
      private final ItemModelGenerator.SpanFacing spanFacing;
      private int min;
      private int max;
      private final int anchor;

      public Span(ItemModelGenerator.SpanFacing spanFacingIn, int p_i46216_2_, int p_i46216_3_) {
         this.spanFacing = spanFacingIn;
         this.min = p_i46216_2_;
         this.max = p_i46216_2_;
         this.anchor = p_i46216_3_;
      }

      public void expand(int p_178382_1_) {
         if (p_178382_1_ < this.min) {
            this.min = p_178382_1_;
         } else if (p_178382_1_ > this.max) {
            this.max = p_178382_1_;
         }

      }

      public ItemModelGenerator.SpanFacing getFacing() {
         return this.spanFacing;
      }

      public int getMin() {
         return this.min;
      }

      public int getMax() {
         return this.max;
      }

      public int getAnchor() {
         return this.anchor;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum SpanFacing {
      UP(EnumFacing.UP, 0, -1),
      DOWN(EnumFacing.DOWN, 0, 1),
      LEFT(EnumFacing.EAST, -1, 0),
      RIGHT(EnumFacing.WEST, 1, 0);

      private final EnumFacing facing;
      private final int xOffset;
      private final int yOffset;

      private SpanFacing(EnumFacing facing, int p_i46215_4_, int p_i46215_5_) {
         this.facing = facing;
         this.xOffset = p_i46215_4_;
         this.yOffset = p_i46215_5_;
      }

      /**
       * Gets the direction of the block's facing.
       */
      public EnumFacing getFacing() {
         return this.facing;
      }

      public int getXOffset() {
         return this.xOffset;
      }

      public int getYOffset() {
         return this.yOffset;
      }

      private boolean isHorizontal() {
         return this == DOWN || this == UP;
      }
   }
}