package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelPufferFishBig;
import net.minecraft.client.renderer.entity.model.ModelPufferFishMedium;
import net.minecraft.client.renderer.entity.model.ModelPufferFishSmall;
import net.minecraft.entity.passive.EntityPufferFish;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderPufferFish extends RenderLiving<EntityPufferFish> {
   private static final ResourceLocation field_203771_a = new ResourceLocation("textures/entity/fish/pufferfish.png");
   private int field_203772_j;
   private final ModelPufferFishSmall field_203773_k = new ModelPufferFishSmall();
   private final ModelPufferFishMedium field_203774_l = new ModelPufferFishMedium();
   private final ModelPufferFishBig field_203775_m = new ModelPufferFishBig();

   public RenderPufferFish(RenderManager p_i48863_1_) {
      super(p_i48863_1_, new ModelPufferFishBig(), 0.1F);
      this.field_203772_j = 3;
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   @Nullable
   protected ResourceLocation getEntityTexture(EntityPufferFish entity) {
      return field_203771_a;
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(EntityPufferFish entity, double x, double y, double z, float entityYaw, float partialTicks) {
      int i = entity.getPuffState();
      if (i != this.field_203772_j) {
         if (i == 0) {
            this.mainModel = this.field_203773_k;
         } else if (i == 1) {
            this.mainModel = this.field_203774_l;
         } else {
            this.mainModel = this.field_203775_m;
         }
      }

      this.field_203772_j = i;
      this.shadowSize = 0.1F + 0.1F * (float)i;
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   protected void applyRotations(EntityPufferFish entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      GlStateManager.translatef(0.0F, MathHelper.cos(ageInTicks * 0.05F) * 0.08F, 0.0F);
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}