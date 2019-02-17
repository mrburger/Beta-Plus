package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.world.gen.area.IArea;

public abstract class LayerContext<R extends IArea> implements IContextExtended<R> {
   private long seed;
   private long positionHash;
   protected long seedModifier;
   protected NoiseGeneratorImproved noiseGenerator;

   public LayerContext(long seedModifier) {
      this.seedModifier = seedModifier;
      this.seedModifier *= this.seedModifier * 6364136223846793005L + 1442695040888963407L;
      this.seedModifier += seedModifier;
      this.seedModifier *= this.seedModifier * 6364136223846793005L + 1442695040888963407L;
      this.seedModifier += seedModifier;
      this.seedModifier *= this.seedModifier * 6364136223846793005L + 1442695040888963407L;
      this.seedModifier += seedModifier;
   }

   public void setSeed(long seed) {
      this.seed = seed;
      this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
      this.seed += this.seedModifier;
      this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
      this.seed += this.seedModifier;
      this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
      this.seed += this.seedModifier;
      this.noiseGenerator = new NoiseGeneratorImproved(new Random(seed));
   }

   public void setPosition(long x, long z) {
      this.positionHash = this.seed;
      this.positionHash *= this.positionHash * 6364136223846793005L + 1442695040888963407L;
      this.positionHash += x;
      this.positionHash *= this.positionHash * 6364136223846793005L + 1442695040888963407L;
      this.positionHash += z;
      this.positionHash *= this.positionHash * 6364136223846793005L + 1442695040888963407L;
      this.positionHash += x;
      this.positionHash *= this.positionHash * 6364136223846793005L + 1442695040888963407L;
      this.positionHash += z;
   }

   public int random(int bound) {
      int i = (int)((this.positionHash >> 24) % (long)bound);
      if (i < 0) {
         i += bound;
      }

      this.positionHash *= this.positionHash * 6364136223846793005L + 1442695040888963407L;
      this.positionHash += this.seed;
      return i;
   }

   public int selectRandomly(int... choices) {
      return choices[this.random(choices.length)];
   }

   public NoiseGeneratorImproved getNoiseGenerator() {
      return this.noiseGenerator;
   }
}