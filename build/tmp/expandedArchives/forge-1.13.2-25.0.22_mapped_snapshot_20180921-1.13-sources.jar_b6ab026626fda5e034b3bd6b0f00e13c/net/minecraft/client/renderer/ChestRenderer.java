package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChestRenderer {
   public void renderChestBrightness(Block blockIn, float color) {
      GlStateManager.color4f(color, color, color, 1.0F);
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      ItemStack stack = new ItemStack(blockIn);
      stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
   }
}