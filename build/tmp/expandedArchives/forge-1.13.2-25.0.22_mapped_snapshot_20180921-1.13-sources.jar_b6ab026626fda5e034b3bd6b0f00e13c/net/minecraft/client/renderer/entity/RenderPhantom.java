package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerPhantomEyes;
import net.minecraft.client.renderer.entity.model.ModelPhantom;
import net.minecraft.entity.monster.EntityPhantom;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderPhantom extends RenderLiving<EntityPhantom> {
   private static final ResourceLocation PHANTOM_LOCATION = new ResourceLocation("textures/entity/phantom.png");

   public RenderPhantom(RenderManager p_i48829_1_) {
      super(p_i48829_1_, new ModelPhantom(), 0.75F);
      this.addLayer(new LayerPhantomEyes(this));
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EntityPhantom entity) {
      return PHANTOM_LOCATION;
   }

   /**
    * Allows the render to do state modifications necessary before the model is rendered.
    */
   protected void preRenderCallback(EntityPhantom entitylivingbaseIn, float partialTickTime) {
      int i = entitylivingbaseIn.getPhantomSize();
      float f = 1.0F + 0.15F * (float)i;
      GlStateManager.scalef(f, f, f);
      GlStateManager.translatef(0.0F, 1.3125F, 0.1875F);
   }

   protected void applyRotations(EntityPhantom entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
      GlStateManager.rotatef(entityLiving.rotationPitch, 1.0F, 0.0F, 0.0F);
   }
}