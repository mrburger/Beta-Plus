package net.minecraft.client.audio;

import net.minecraft.util.ITickable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ITickableSound extends ISound, ITickable {
   boolean isDonePlaying();
}