package net.minecraft.client.renderer.entity;

import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMinecartMobSpawner extends RenderMinecart<EntityMinecartMobSpawner> {
   public RenderMinecartMobSpawner(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }
}