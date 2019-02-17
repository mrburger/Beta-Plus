package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ModelBlaze;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBlaze extends RenderLiving<EntityBlaze> {
   private static final ResourceLocation BLAZE_TEXTURES = new ResourceLocation("textures/entity/blaze.png");

   public RenderBlaze(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelBlaze(), 0.5F);
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EntityBlaze entity) {
      return BLAZE_TEXTURES;
   }
}