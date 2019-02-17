package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderPotion extends RenderSprite<EntityPotion> {
   public RenderPotion(RenderManager renderManagerIn, ItemRenderer itemRendererIn) {
      super(renderManagerIn, Items.POTION, itemRendererIn);
   }

   public ItemStack getStackToRender(EntityPotion entityIn) {
      return entityIn.getPotion();
   }
}