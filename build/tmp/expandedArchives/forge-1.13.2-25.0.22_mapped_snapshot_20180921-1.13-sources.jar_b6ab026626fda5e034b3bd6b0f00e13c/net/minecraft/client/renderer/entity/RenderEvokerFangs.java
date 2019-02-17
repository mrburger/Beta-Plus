package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelEvokerFangs;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderEvokerFangs extends Render<EntityEvokerFangs> {
   private static final ResourceLocation EVOKER_ILLAGER_FANGS = new ResourceLocation("textures/entity/illager/evoker_fangs.png");
   private final ModelEvokerFangs model = new ModelEvokerFangs();

   public RenderEvokerFangs(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(EntityEvokerFangs entity, double x, double y, double z, float entityYaw, float partialTicks) {
      float f = entity.getAnimationProgress(partialTicks);
      if (f != 0.0F) {
         float f1 = 2.0F;
         if (f > 0.9F) {
            f1 = (float)((double)f1 * ((1.0D - (double)f) / (double)0.1F));
         }

         GlStateManager.pushMatrix();
         GlStateManager.disableCull();
         GlStateManager.enableAlphaTest();
         this.bindEntityTexture(entity);
         GlStateManager.translatef((float)x, (float)y, (float)z);
         GlStateManager.rotatef(90.0F - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
         GlStateManager.scalef(-f1, -f1, f1);
         float f2 = 0.03125F;
         GlStateManager.translatef(0.0F, -0.626F, 0.0F);
         this.model.render(entity, f, 0.0F, 0.0F, entity.rotationYaw, entity.rotationPitch, 0.03125F);
         GlStateManager.popMatrix();
         GlStateManager.enableCull();
         super.doRender(entity, x, y, z, entityYaw, partialTicks);
      }
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EntityEvokerFangs entity) {
      return EVOKER_ILLAGER_FANGS;
   }
}