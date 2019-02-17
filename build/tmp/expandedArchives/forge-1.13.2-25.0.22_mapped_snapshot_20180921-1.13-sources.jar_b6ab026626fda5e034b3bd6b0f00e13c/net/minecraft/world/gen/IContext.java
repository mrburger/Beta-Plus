package net.minecraft.world.gen;

public interface IContext {
   int random(int bound);

   NoiseGeneratorImproved getNoiseGenerator();
}