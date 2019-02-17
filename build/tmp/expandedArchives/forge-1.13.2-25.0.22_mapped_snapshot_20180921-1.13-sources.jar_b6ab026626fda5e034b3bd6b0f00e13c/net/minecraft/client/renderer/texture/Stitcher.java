package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Stitcher {
   private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger();
    
   private final int mipmapLevelStitcher;
   private final Set<Stitcher.Holder> setStitchHolders = Sets.newHashSetWithExpectedSize(256);
   private final List<Stitcher.Slot> stitchSlots = Lists.newArrayListWithCapacity(256);
   private int currentWidth;
   private int currentHeight;
   private final int maxWidth;
   private final int maxHeight;
   /** Max size (width or height) of a single tile */
   private final int maxTileDimension;

   public Stitcher(int maxWidthIn, int maxHeightIn, int maxTileDimensionIn, int mipmapLevelStitcherIn) {
      this.mipmapLevelStitcher = mipmapLevelStitcherIn;
      this.maxWidth = maxWidthIn;
      this.maxHeight = maxHeightIn;
      this.maxTileDimension = maxTileDimensionIn;
   }

   public int getCurrentWidth() {
      return this.currentWidth;
   }

   public int getCurrentHeight() {
      return this.currentHeight;
   }

   public void addSprite(TextureAtlasSprite textureAtlas) {
      Stitcher.Holder stitcher$holder = new Stitcher.Holder(textureAtlas, this.mipmapLevelStitcher);
      if (this.maxTileDimension > 0) {
         stitcher$holder.setNewDimension(this.maxTileDimension);
      }

      this.setStitchHolders.add(stitcher$holder);
   }

   public void doStitch() {
      Stitcher.Holder[] astitcher$holder = this.setStitchHolders.toArray(new Stitcher.Holder[this.setStitchHolders.size()]);
      Arrays.sort((Object[])astitcher$holder);
      try(net.minecraftforge.fml.common.progress.ProgressBar bar = net.minecraftforge.fml.common.progress.StartupProgressManager.start("Texture stitching", astitcher$holder.length)) {

      for(Stitcher.Holder stitcher$holder : astitcher$holder) {
         bar.step(stitcher$holder.getAtlasSprite().getName().toString());
         if (!this.allocateSlot(stitcher$holder)) {
            String s = String.format("Unable to fit: %s - size: %dx%d - Maybe try a lowerresolution resourcepack?", stitcher$holder.getAtlasSprite().getName(), stitcher$holder.getAtlasSprite().getWidth(), stitcher$holder.getAtlasSprite().getHeight());
            LOGGER.info(s);
            for (Stitcher.Holder h : astitcher$holder)
                LOGGER.info("  {}", h);
            throw new StitcherException(stitcher$holder, s);
         }
      }

      this.currentWidth = MathHelper.smallestEncompassingPowerOfTwo(this.currentWidth);
      this.currentHeight = MathHelper.smallestEncompassingPowerOfTwo(this.currentHeight);
      }; // Forge: end progress bar
   }

   public List<TextureAtlasSprite> getStichSlots() {
      List<Stitcher.Slot> list = Lists.newArrayList();

      for(Stitcher.Slot stitcher$slot : this.stitchSlots) {
         stitcher$slot.getAllStitchSlots(list);
      }

      List<TextureAtlasSprite> list1 = Lists.newArrayList();

      for(Stitcher.Slot stitcher$slot1 : list) {
         Stitcher.Holder stitcher$holder = stitcher$slot1.getStitchHolder();
         TextureAtlasSprite textureatlassprite = stitcher$holder.getAtlasSprite();
         textureatlassprite.initSprite(this.currentWidth, this.currentHeight, stitcher$slot1.getOriginX(), stitcher$slot1.getOriginY(), stitcher$holder.isRotated());
         list1.add(textureatlassprite);
      }

      return list1;
   }

   private static int getMipmapDimension(int dimensionIn, int mipmapLevelIn) {
      return (dimensionIn >> mipmapLevelIn) + ((dimensionIn & (1 << mipmapLevelIn) - 1) == 0 ? 0 : 1) << mipmapLevelIn;
   }

   /**
    * Attempts to find space for specified tile
    */
   private boolean allocateSlot(Stitcher.Holder holderIn) {
      TextureAtlasSprite textureatlassprite = holderIn.getAtlasSprite();
      boolean flag = textureatlassprite.getWidth() != textureatlassprite.getHeight();

      for(int i = 0; i < this.stitchSlots.size(); ++i) {
         if (this.stitchSlots.get(i).addSlot(holderIn)) {
            return true;
         }

         if (flag) {
            holderIn.rotate();
            if (this.stitchSlots.get(i).addSlot(holderIn)) {
               return true;
            }

            holderIn.rotate();
         }
      }

      return this.expandAndAllocateSlot(holderIn);
   }

   /**
    * Expand stitched texture in order to make space for specified tile
    */
   private boolean expandAndAllocateSlot(Stitcher.Holder holderIn) {
      int i = Math.min(holderIn.getWidth(), holderIn.getHeight());
      int j = Math.max(holderIn.getWidth(), holderIn.getHeight());
      int k = MathHelper.smallestEncompassingPowerOfTwo(this.currentWidth);
      int l = MathHelper.smallestEncompassingPowerOfTwo(this.currentHeight);
      int i1 = MathHelper.smallestEncompassingPowerOfTwo(this.currentWidth + i);
      int j1 = MathHelper.smallestEncompassingPowerOfTwo(this.currentHeight + i);
      boolean flag1 = i1 <= this.maxWidth;
      boolean flag2 = j1 <= this.maxHeight;
      if (!flag1 && !flag2) {
         return false;
      } else {
         boolean flag3 = flag1 && k != i1;
         boolean flag4 = flag2 && l != j1;
         boolean flag;
         if (flag3 ^ flag4) {
            flag = !flag3 && flag1; // Forge: Fix stitcher not expanding entire height before growing width, and (potentially) growing larger then the max size.
         } else {
            flag = flag1 && k <= l;
         }

         Stitcher.Slot stitcher$slot;
         if (flag) {
            if (holderIn.getWidth() > holderIn.getHeight()) {
               holderIn.rotate();
            }

            if (this.currentHeight == 0) {
               this.currentHeight = holderIn.getHeight();
            }

            stitcher$slot = new Stitcher.Slot(this.currentWidth, 0, holderIn.getWidth(), this.currentHeight);
            this.currentWidth += holderIn.getWidth();
         } else {
            stitcher$slot = new Stitcher.Slot(0, this.currentHeight, this.currentWidth, holderIn.getHeight());
            this.currentHeight += holderIn.getHeight();
         }

         stitcher$slot.addSlot(holderIn);
         this.stitchSlots.add(stitcher$slot);
         return true;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Holder implements Comparable<Stitcher.Holder> {
      private final TextureAtlasSprite sprite;
      private final int width;
      private final int height;
      private final int mipmapLevelHolder;
      private boolean rotated;
      private float scaleFactor = 1.0F;

      public Holder(TextureAtlasSprite theTextureIn, int mipmapLevelHolderIn) {
         this.sprite = theTextureIn;
         this.width = theTextureIn.getWidth();
         this.height = theTextureIn.getHeight();
         this.mipmapLevelHolder = mipmapLevelHolderIn;
         this.rotated = Stitcher.getMipmapDimension(this.height, mipmapLevelHolderIn) > Stitcher.getMipmapDimension(this.width, mipmapLevelHolderIn);
      }

      public TextureAtlasSprite getAtlasSprite() {
         return this.sprite;
      }

      public int getWidth() {
         int i = this.rotated ? this.height : this.width;
         return Stitcher.getMipmapDimension((int)((float)i * this.scaleFactor), this.mipmapLevelHolder);
      }

      public int getHeight() {
         int i = this.rotated ? this.width : this.height;
         return Stitcher.getMipmapDimension((int)((float)i * this.scaleFactor), this.mipmapLevelHolder);
      }

      public void rotate() {
         this.rotated = !this.rotated;
      }

      public boolean isRotated() {
         return this.rotated;
      }

      public void setNewDimension(int dimensionIn) {
         if (this.width > dimensionIn && this.height > dimensionIn) {
            this.scaleFactor = (float)dimensionIn / (float)Math.min(this.width, this.height);
         }
      }

      public String toString() {
         return "Holder{width=" + this.width + ", height=" + this.height + ", name=" + this.sprite.getName() + '}';
      }

      public int compareTo(Stitcher.Holder p_compareTo_1_) {
         int i;
         if (this.getHeight() == p_compareTo_1_.getHeight()) {
            if (this.getWidth() == p_compareTo_1_.getWidth()) {
               return this.sprite.getName().toString().compareTo(p_compareTo_1_.sprite.getName().toString());
            }

            i = this.getWidth() < p_compareTo_1_.getWidth() ? 1 : -1;
         } else {
            i = this.getHeight() < p_compareTo_1_.getHeight() ? 1 : -1;
         }

         return i;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Slot {
      private final int originX;
      private final int originY;
      private final int width;
      private final int height;
      private List<Stitcher.Slot> subSlots;
      private Stitcher.Holder holder;

      public Slot(int originXIn, int originYIn, int widthIn, int heightIn) {
         this.originX = originXIn;
         this.originY = originYIn;
         this.width = widthIn;
         this.height = heightIn;
      }

      public Stitcher.Holder getStitchHolder() {
         return this.holder;
      }

      public int getOriginX() {
         return this.originX;
      }

      public int getOriginY() {
         return this.originY;
      }

      public boolean addSlot(Stitcher.Holder holderIn) {
         if (this.holder != null) {
            return false;
         } else {
            int i = holderIn.getWidth();
            int j = holderIn.getHeight();
            if (i <= this.width && j <= this.height) {
               if (i == this.width && j == this.height) {
                  this.holder = holderIn;
                  return true;
               } else {
                  if (this.subSlots == null) {
                     this.subSlots = Lists.newArrayListWithCapacity(1);
                     this.subSlots.add(new Stitcher.Slot(this.originX, this.originY, i, j));
                     int k = this.width - i;
                     int l = this.height - j;
                     if (l > 0 && k > 0) {
                        int i1 = Math.max(this.height, k);
                        int j1 = Math.max(this.width, l);
                        if (i1 >= j1) {
                           this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
                           this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, this.height));
                        } else {
                           this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
                           this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, this.width, l));
                        }
                     } else if (k == 0) {
                        this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
                     } else if (l == 0) {
                        this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
                     }
                  }

                  for(Stitcher.Slot stitcher$slot : this.subSlots) {
                     if (stitcher$slot.addSlot(holderIn)) {
                        return true;
                     }
                  }

                  return false;
               }
            } else {
               return false;
            }
         }
      }

      /**
       * Gets the slot and all its subslots
       */
      public void getAllStitchSlots(List<Stitcher.Slot> slots) {
         if (this.holder != null) {
            slots.add(this);
         } else if (this.subSlots != null) {
            for(Stitcher.Slot stitcher$slot : this.subSlots) {
               stitcher$slot.getAllStitchSlots(slots);
            }
         }

      }

      public String toString() {
         return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + '}';
      }
   }
}