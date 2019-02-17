package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.IMultipassModel;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelBoat;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBoat extends Render<EntityBoat> {
   private static final ResourceLocation[] BOAT_TEXTURES = new ResourceLocation[]{new ResourceLocation("textures/entity/boat/oak.png"), new ResourceLocation("textures/entity/boat/spruce.png"), new ResourceLocation("textures/entity/boat/birch.png"), new ResourceLocation("textures/entity/boat/jungle.png"), new ResourceLocation("textures/entity/boat/acacia.png"), new ResourceLocation("textures/entity/boat/dark_oak.png")};
   /** instance of ModelBoat for rendering */
   protected ModelBase modelBoat = new ModelBoat();

   public RenderBoat(RenderManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize = 0.5F;
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(EntityBoat entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      this.setupTranslation(x, y, z);
      this.setupRotation(entity, entityYaw, partialTicks);
      this.bindEntityTexture(entity);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(entity));
      }

      this.modelBoat.render(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public void setupRotation(EntityBoat entityIn, float entityYaw, float partialTicks) {
      GlStateManager.rotatef(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
      float f = (float)entityIn.getTimeSinceHit() - partialTicks;
      float f1 = entityIn.getDamageTaken() - partialTicks;
      if (f1 < 0.0F) {
         f1 = 0.0F;
      }

      if (f > 0.0F) {
         GlStateManager.rotatef(MathHelper.sin(f) * f * f1 / 10.0F * (float)entityIn.getForwardDirection(), 1.0F, 0.0F, 0.0F);
      }

      float f2 = entityIn.func_203056_b(partialTicks);
      if (!MathHelper.epsilonEquals(f2, 0.0F)) {
         GlStateManager.rotatef(entityIn.func_203056_b(partialTicks), 1.0F, 0.0F, 1.0F);
      }

      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
   }

   public void setupTranslation(double x, double y, double z) {
      GlStateManager.translatef((float)x, (float)y + 0.375F, (float)z);
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EntityBoat entity) {
      return BOAT_TEXTURES[entity.getBoatType().ordinal()];
   }

   public boolean isMultipass() {
      return true;
   }

   public void renderMultipass(EntityBoat entityIn, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      this.setupTranslation(x, y, z);
      this.setupRotation(entityIn, entityYaw, partialTicks);
      this.bindEntityTexture(entityIn);
      ((IMultipassModel)this.modelBoat).renderMultipass(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}