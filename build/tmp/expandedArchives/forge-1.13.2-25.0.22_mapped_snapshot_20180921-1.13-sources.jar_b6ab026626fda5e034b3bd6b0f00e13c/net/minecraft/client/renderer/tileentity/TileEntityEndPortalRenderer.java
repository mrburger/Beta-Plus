package net.minecraft.client.renderer.tileentity;

import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityEndPortalRenderer extends TileEntityRenderer<TileEntityEndPortal> {
   private static final ResourceLocation END_SKY_TEXTURE = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");
   private static final Random RANDOM = new Random(31100L);
   private static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
   private static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);
   private final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);

   public void render(TileEntityEndPortal tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      GlStateManager.disableLighting();
      RANDOM.setSeed(31100L);
      GlStateManager.getFloatv(2982, MODELVIEW);
      GlStateManager.getFloatv(2983, PROJECTION);
      double d0 = x * x + y * y + z * z;
      int i = this.getPasses(d0);
      float f = this.getOffset();
      boolean flag = false;

      for(int j = 0; j < i; ++j) {
         GlStateManager.pushMatrix();
         float f1 = 2.0F / (float)(18 - j);
         if (j == 0) {
            this.bindTexture(END_SKY_TEXTURE);
            f1 = 0.15F;
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         }

         if (j >= 1) {
            this.bindTexture(END_PORTAL_TEXTURE);
            flag = true;
            Minecraft.getInstance().entityRenderer.setupFogColor(true);
         }

         if (j == 1) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
         }

         GlStateManager.texGenMode(GlStateManager.TexGen.S, 9216);
         GlStateManager.texGenMode(GlStateManager.TexGen.T, 9216);
         GlStateManager.texGenMode(GlStateManager.TexGen.R, 9216);
         GlStateManager.texGenParam(GlStateManager.TexGen.S, 9474, this.getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
         GlStateManager.texGenParam(GlStateManager.TexGen.T, 9474, this.getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
         GlStateManager.texGenParam(GlStateManager.TexGen.R, 9474, this.getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
         GlStateManager.enableTexGen(GlStateManager.TexGen.S);
         GlStateManager.enableTexGen(GlStateManager.TexGen.T);
         GlStateManager.enableTexGen(GlStateManager.TexGen.R);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.loadIdentity();
         GlStateManager.translatef(0.5F, 0.5F, 0.0F);
         GlStateManager.scalef(0.5F, 0.5F, 1.0F);
         float f2 = (float)(j + 1);
         GlStateManager.translatef(17.0F / f2, (2.0F + f2 / 1.5F) * ((float)Util.milliTime() % 800000.0F / 800000.0F), 0.0F);
         GlStateManager.rotatef((f2 * f2 * 4321.0F + f2 * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.scalef(4.5F - f2 / 4.0F, 4.5F - f2 / 4.0F, 1.0F);
         GlStateManager.multMatrixf(PROJECTION);
         GlStateManager.multMatrixf(MODELVIEW);
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
         float f3 = (RANDOM.nextFloat() * 0.5F + 0.1F) * f1;
         float f4 = (RANDOM.nextFloat() * 0.5F + 0.4F) * f1;
         float f5 = (RANDOM.nextFloat() * 0.5F + 0.5F) * f1;
         if (tileEntityIn.shouldRenderFace(EnumFacing.SOUTH)) {
            bufferbuilder.pos(x, y, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x + 1.0D, y, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x + 1.0D, y + 1.0D, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x, y + 1.0D, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
         }

         if (tileEntityIn.shouldRenderFace(EnumFacing.NORTH)) {
            bufferbuilder.pos(x, y + 1.0D, z).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x + 1.0D, y + 1.0D, z).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x + 1.0D, y, z).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x, y, z).color(f3, f4, f5, 1.0F).endVertex();
         }

         if (tileEntityIn.shouldRenderFace(EnumFacing.EAST)) {
            bufferbuilder.pos(x + 1.0D, y + 1.0D, z).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x + 1.0D, y + 1.0D, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x + 1.0D, y, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x + 1.0D, y, z).color(f3, f4, f5, 1.0F).endVertex();
         }

         if (tileEntityIn.shouldRenderFace(EnumFacing.WEST)) {
            bufferbuilder.pos(x, y, z).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x, y, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x, y + 1.0D, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x, y + 1.0D, z).color(f3, f4, f5, 1.0F).endVertex();
         }

         if (tileEntityIn.shouldRenderFace(EnumFacing.DOWN)) {
            bufferbuilder.pos(x, y, z).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x + 1.0D, y, z).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x + 1.0D, y, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x, y, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
         }

         if (tileEntityIn.shouldRenderFace(EnumFacing.UP)) {
            bufferbuilder.pos(x, y + (double)f, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x + 1.0D, y + (double)f, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x + 1.0D, y + (double)f, z).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x, y + (double)f, z).color(f3, f4, f5, 1.0F).endVertex();
         }

         tessellator.draw();
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
         this.bindTexture(END_SKY_TEXTURE);
      }

      GlStateManager.disableBlend();
      GlStateManager.disableTexGen(GlStateManager.TexGen.S);
      GlStateManager.disableTexGen(GlStateManager.TexGen.T);
      GlStateManager.disableTexGen(GlStateManager.TexGen.R);
      GlStateManager.enableLighting();
      if (flag) {
         Minecraft.getInstance().entityRenderer.setupFogColor(false);
      }

   }

   protected int getPasses(double p_191286_1_) {
      int i;
      if (p_191286_1_ > 36864.0D) {
         i = 1;
      } else if (p_191286_1_ > 25600.0D) {
         i = 3;
      } else if (p_191286_1_ > 16384.0D) {
         i = 5;
      } else if (p_191286_1_ > 9216.0D) {
         i = 7;
      } else if (p_191286_1_ > 4096.0D) {
         i = 9;
      } else if (p_191286_1_ > 1024.0D) {
         i = 11;
      } else if (p_191286_1_ > 576.0D) {
         i = 13;
      } else if (p_191286_1_ > 256.0D) {
         i = 14;
      } else {
         i = 15;
      }

      return i;
   }

   protected float getOffset() {
      return 0.75F;
   }

   private FloatBuffer getBuffer(float p_147525_1_, float p_147525_2_, float p_147525_3_, float p_147525_4_) {
      this.buffer.clear();
      this.buffer.put(p_147525_1_).put(p_147525_2_).put(p_147525_3_).put(p_147525_4_);
      this.buffer.flip();
      return this.buffer;
   }
}