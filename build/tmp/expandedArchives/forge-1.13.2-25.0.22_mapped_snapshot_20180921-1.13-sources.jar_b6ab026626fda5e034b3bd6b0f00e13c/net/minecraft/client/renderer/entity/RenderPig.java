package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerSaddle;
import net.minecraft.client.renderer.entity.model.ModelPig;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderPig extends RenderLiving<EntityPig> {
   private static final ResourceLocation PIG_TEXTURES = new ResourceLocation("textures/entity/pig/pig.png");

   public RenderPig(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelPig(), 0.7F);
      this.addLayer(new LayerSaddle(this));
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EntityPig entity) {
      return PIG_TEXTURES;
   }
}