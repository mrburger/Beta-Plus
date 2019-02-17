package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerMooshroomMushroom;
import net.minecraft.client.renderer.entity.model.ModelCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMooshroom extends RenderLiving<EntityMooshroom> {
   private static final ResourceLocation MOOSHROOM_TEXTURES = new ResourceLocation("textures/entity/cow/mooshroom.png");

   public RenderMooshroom(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelCow(), 0.7F);
      this.addLayer(new LayerMooshroomMushroom(this));
   }

   public ModelCow getMainModel() {
      return (ModelCow)super.getMainModel();
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EntityMooshroom entity) {
      return MOOSHROOM_TEXTURES;
   }
}