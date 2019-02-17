package net.minecraft.client.renderer.entity.layers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerDolphinCarriedItem implements LayerRenderer<EntityLivingBase> {
   protected final RenderLivingBase<?> renderer;
   private final ItemRenderer itemRenderer;

   public LayerDolphinCarriedItem(RenderLivingBase<?> p_i48948_1_) {
      this.renderer = p_i48948_1_;
      this.itemRenderer = Minecraft.getInstance().getItemRenderer();
   }

   public void render(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      boolean flag = entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT;
      ItemStack itemstack = flag ? entitylivingbaseIn.getHeldItemOffhand() : entitylivingbaseIn.getHeldItemMainhand();
      ItemStack itemstack1 = flag ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();
      if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
         this.func_205129_a(entitylivingbaseIn, itemstack1);
      }
   }

   private void func_205129_a(EntityLivingBase p_205129_1_, ItemStack p_205129_2_) {
      if (!p_205129_2_.isEmpty()) {
         if (!p_205129_2_.isEmpty()) {
            Item item = p_205129_2_.getItem();
            Block block = Block.getBlockFromItem(item);
            GlStateManager.pushMatrix();
            boolean flag = this.itemRenderer.shouldRenderItemIn3D(p_205129_2_) && block.getRenderLayer() == BlockRenderLayer.TRANSLUCENT;
            if (flag) {
               GlStateManager.depthMask(false);
            }

            float f = 1.0F;
            float f1 = -1.0F;
            float f2 = MathHelper.abs(p_205129_1_.rotationPitch) / 60.0F;
            if (p_205129_1_.rotationPitch < 0.0F) {
               GlStateManager.translatef(0.0F, 1.0F - f2 * 0.5F, -1.0F + f2 * 0.5F);
            } else {
               GlStateManager.translatef(0.0F, 1.0F + f2 * 0.8F, -1.0F + f2 * 0.2F);
            }

            this.itemRenderer.renderItem(p_205129_2_, p_205129_1_, ItemCameraTransforms.TransformType.GROUND, false);
            if (flag) {
               GlStateManager.depthMask(true);
            }

            GlStateManager.popMatrix();
         }
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}