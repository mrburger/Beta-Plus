package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.ModelHorseArmorBase;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHorse extends RenderAbstractHorse<EntityHorse> {
   private static final Map<String, ResourceLocation> LAYERED_LOCATION_CACHE = Maps.newHashMap();

   public RenderHorse(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelHorseArmorBase(), 1.1F);
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(AbstractHorse entity) {
      EntityHorse entityhorse = (EntityHorse)entity;
      String s = entityhorse.getHorseTexture();
      ResourceLocation resourcelocation = LAYERED_LOCATION_CACHE.get(s);
      if (resourcelocation == null) {
         resourcelocation = new ResourceLocation(s);
         Minecraft.getInstance().getTextureManager().loadTexture(resourcelocation, new LayeredTexture(entityhorse.getVariantTexturePaths()));
         LAYERED_LOCATION_CACHE.put(s, resourcelocation);
      }

      return resourcelocation;
   }
}