package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.model.ModelTurtle;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTurtle extends RenderLiving<EntityTurtle> {
   private static final ResourceLocation field_203091_a = new ResourceLocation("textures/entity/turtle/big_sea_turtle.png");

   public RenderTurtle(RenderManager p_i48827_1_) {
      super(p_i48827_1_, new ModelTurtle(0.0F), 0.35F);
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(EntityTurtle entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (entity.isChild()) {
         this.shadowSize *= 0.5F;
      }

      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   @Nullable
   protected ResourceLocation getEntityTexture(EntityTurtle entity) {
      return field_203091_a;
   }
}