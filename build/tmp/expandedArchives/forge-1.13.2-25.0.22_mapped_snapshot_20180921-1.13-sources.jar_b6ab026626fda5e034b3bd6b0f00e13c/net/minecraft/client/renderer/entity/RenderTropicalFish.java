package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerTropicalFishPattern;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelTropicalFishA;
import net.minecraft.client.renderer.entity.model.ModelTropicalFishB;
import net.minecraft.entity.passive.EntityTropicalFish;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTropicalFish extends RenderLiving<EntityTropicalFish> {
   private final ModelTropicalFishA field_204246_a = new ModelTropicalFishA();
   private final ModelTropicalFishB field_204247_j = new ModelTropicalFishB();

   public RenderTropicalFish(RenderManager p_i48889_1_) {
      super(p_i48889_1_, new ModelTropicalFishA(), 0.15F);
      this.addLayer(new LayerTropicalFishPattern(this));
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   @Nullable
   protected ResourceLocation getEntityTexture(EntityTropicalFish entity) {
      return entity.getBodyTexture();
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(EntityTropicalFish entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.mainModel = (ModelBase)(entity.getSize() == 0 ? this.field_204246_a : this.field_204247_j);
      float[] afloat = entity.func_204219_dC();
      GlStateManager.color3f(afloat[0], afloat[1], afloat[2]);
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   protected void applyRotations(EntityTropicalFish entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
      float f = 4.3F * MathHelper.sin(0.6F * ageInTicks);
      GlStateManager.rotatef(f, 0.0F, 1.0F, 0.0F);
      if (!entityLiving.isInWater()) {
         GlStateManager.translatef(0.2F, 0.1F, 0.0F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

   }
}