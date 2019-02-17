package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerLlamaDecor;
import net.minecraft.client.renderer.entity.model.ModelLlama;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderLlama extends RenderLiving<EntityLlama> {
   private static final ResourceLocation[] LLAMA_TEXTURES = new ResourceLocation[]{new ResourceLocation("textures/entity/llama/creamy.png"), new ResourceLocation("textures/entity/llama/white.png"), new ResourceLocation("textures/entity/llama/brown.png"), new ResourceLocation("textures/entity/llama/gray.png")};

   public RenderLlama(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelLlama(0.0F), 0.7F);
      this.addLayer(new LayerLlamaDecor(this));
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EntityLlama entity) {
      return LLAMA_TEXTURES[entity.getVariant()];
   }
}