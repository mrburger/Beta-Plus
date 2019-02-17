package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimationMetadataSection {
   public static final AnimationMetadataSectionSerializer SERIALIZER = new AnimationMetadataSectionSerializer();
   private final List<AnimationFrame> animationFrames;
   private final int frameWidth;
   private final int frameHeight;
   private final int frameTime;
   private final boolean interpolate;

   public AnimationMetadataSection(List<AnimationFrame> animationFramesIn, int frameWidthIn, int frameHeightIn, int frameTimeIn, boolean interpolateIn) {
      this.animationFrames = animationFramesIn;
      this.frameWidth = frameWidthIn;
      this.frameHeight = frameHeightIn;
      this.frameTime = frameTimeIn;
      this.interpolate = interpolateIn;
   }

   public int getFrameHeight() {
      return this.frameHeight;
   }

   public int getFrameWidth() {
      return this.frameWidth;
   }

   public int getFrameCount() {
      return this.animationFrames.size();
   }

   public int getFrameTime() {
      return this.frameTime;
   }

   public boolean isInterpolate() {
      return this.interpolate;
   }

   private AnimationFrame getAnimationFrame(int frame) {
      return this.animationFrames.get(frame);
   }

   public int getFrameTimeSingle(int frame) {
      AnimationFrame animationframe = this.getAnimationFrame(frame);
      return animationframe.hasNoTime() ? this.frameTime : animationframe.getFrameTime();
   }

   public int getFrameIndex(int frame) {
      return this.animationFrames.get(frame).getFrameIndex();
   }

   public Set<Integer> getFrameIndexSet() {
      Set<Integer> set = Sets.newHashSet();

      for(AnimationFrame animationframe : this.animationFrames) {
         set.add(animationframe.getFrameIndex());
      }

      return set;
   }
}