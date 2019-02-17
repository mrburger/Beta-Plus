package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerSheepWool;
import net.minecraft.client.renderer.entity.model.ModelSheep;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSheep extends RenderLiving<EntitySheep> {
   private static final ResourceLocation SHEARED_SHEEP_TEXTURES = new ResourceLocation("textures/entity/sheep/sheep.png");

   public RenderSheep(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelSheep(), 0.7F);
      this.addLayer(new LayerSheepWool(this));
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EntitySheep entity) {
      return SHEARED_SHEEP_TEXTURES;
   }
}