package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.model.ModelTrident;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityTrident;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTrident extends Render<EntityTrident> {
   public static final ResourceLocation field_203087_a = new ResourceLocation("textures/entity/trident.png");
   private final ModelTrident field_203088_f = new ModelTrident();

   public RenderTrident(RenderManager p_i48828_1_) {
      super(p_i48828_1_);
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(EntityTrident entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.bindEntityTexture(entity);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.disableLighting();
      GlStateManager.translatef((float)x, (float)y, (float)z);
      GlStateManager.rotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks + 90.0F, 0.0F, 0.0F, 1.0F);
      this.field_203088_f.renderer();
      GlStateManager.popMatrix();
      this.func_203085_b(entity, x, y, z, entityYaw, partialTicks);
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      GlStateManager.enableLighting();
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EntityTrident entity) {
      return field_203087_a;
   }

   private double func_203086_a(double p_203086_1_, double p_203086_3_, double p_203086_5_) {
      return p_203086_1_ + (p_203086_3_ - p_203086_1_) * p_203086_5_;
   }

   protected void func_203085_b(EntityTrident p_203085_1_, double p_203085_2_, double p_203085_4_, double p_203085_6_, float p_203085_8_, float p_203085_9_) {
      Entity entity = p_203085_1_.func_212360_k();
      if (entity != null && p_203085_1_.func_203047_q()) {
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         double d0 = this.func_203086_a((double)entity.prevRotationYaw, (double)entity.rotationYaw, (double)(p_203085_9_ * 0.5F)) * (double)((float)Math.PI / 180F);
         double d1 = Math.cos(d0);
         double d2 = Math.sin(d0);
         double d3 = this.func_203086_a(entity.prevPosX, entity.posX, (double)p_203085_9_);
         double d4 = this.func_203086_a(entity.prevPosY + (double)entity.getEyeHeight() * 0.8D, entity.posY + (double)entity.getEyeHeight() * 0.8D, (double)p_203085_9_);
         double d5 = this.func_203086_a(entity.prevPosZ, entity.posZ, (double)p_203085_9_);
         double d6 = d1 - d2;
         double d7 = d2 + d1;
         double d8 = this.func_203086_a(p_203085_1_.prevPosX, p_203085_1_.posX, (double)p_203085_9_);
         double d9 = this.func_203086_a(p_203085_1_.prevPosY, p_203085_1_.posY, (double)p_203085_9_);
         double d10 = this.func_203086_a(p_203085_1_.prevPosZ, p_203085_1_.posZ, (double)p_203085_9_);
         double d11 = (double)((float)(d3 - d8));
         double d12 = (double)((float)(d4 - d9));
         double d13 = (double)((float)(d5 - d10));
         double d14 = Math.sqrt(d11 * d11 + d12 * d12 + d13 * d13);
         int i = p_203085_1_.getEntityId() + p_203085_1_.ticksExisted;
         double d15 = (double)((float)i + p_203085_9_) * -0.1D;
         double d16 = Math.min(0.5D, d14 / 30.0D);
         GlStateManager.disableTexture2D();
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, 255.0F, 255.0F);
         bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
         int j = 37;
         int k = 7 - i % 7;
         double d17 = 0.1D;

         for(int l = 0; l <= 37; ++l) {
            double d18 = (double)l / 37.0D;
            float f = 1.0F - (float)((l + k) % 7) / 7.0F;
            double d19 = d18 * 2.0D - 1.0D;
            d19 = (1.0D - d19 * d19) * d16;
            double d20 = p_203085_2_ + d11 * d18 + Math.sin(d18 * Math.PI * 8.0D + d15) * d6 * d19;
            double d21 = p_203085_4_ + d12 * d18 + Math.cos(d18 * Math.PI * 8.0D + d15) * 0.02D + (0.1D + d19) * 1.0D;
            double d22 = p_203085_6_ + d13 * d18 + Math.sin(d18 * Math.PI * 8.0D + d15) * d7 * d19;
            float f1 = 0.87F * f + 0.3F * (1.0F - f);
            float f2 = 0.91F * f + 0.6F * (1.0F - f);
            float f3 = 0.85F * f + 0.5F * (1.0F - f);
            bufferbuilder.pos(d20, d21, d22).color(f1, f2, f3, 1.0F).endVertex();
            bufferbuilder.pos(d20 + 0.1D * d19, d21 + 0.1D * d19, d22).color(f1, f2, f3, 1.0F).endVertex();
            if (l > p_203085_1_.returningTicks * 2) {
               break;
            }
         }

         tessellator.draw();
         bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

         for(int i1 = 0; i1 <= 37; ++i1) {
            double d23 = (double)i1 / 37.0D;
            float f4 = 1.0F - (float)((i1 + k) % 7) / 7.0F;
            double d24 = d23 * 2.0D - 1.0D;
            d24 = (1.0D - d24 * d24) * d16;
            double d25 = p_203085_2_ + d11 * d23 + Math.sin(d23 * Math.PI * 8.0D + d15) * d6 * d24;
            double d26 = p_203085_4_ + d12 * d23 + Math.cos(d23 * Math.PI * 8.0D + d15) * 0.01D + (0.1D + d24) * 1.0D;
            double d27 = p_203085_6_ + d13 * d23 + Math.sin(d23 * Math.PI * 8.0D + d15) * d7 * d24;
            float f5 = 0.87F * f4 + 0.3F * (1.0F - f4);
            float f6 = 0.91F * f4 + 0.6F * (1.0F - f4);
            float f7 = 0.85F * f4 + 0.5F * (1.0F - f4);
            bufferbuilder.pos(d25, d26, d27).color(f5, f6, f7, 1.0F).endVertex();
            bufferbuilder.pos(d25 + 0.1D * d24, d26, d27 + 0.1D * d24).color(f5, f6, f7, 1.0F).endVertex();
            if (i1 > p_203085_1_.returningTicks * 2) {
               break;
            }
         }

         tessellator.draw();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture2D();
         GlStateManager.enableCull();
      }
   }
}