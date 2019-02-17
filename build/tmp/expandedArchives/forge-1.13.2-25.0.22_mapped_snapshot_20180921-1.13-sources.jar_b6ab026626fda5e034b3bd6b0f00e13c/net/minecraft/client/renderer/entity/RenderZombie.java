package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.client.renderer.entity.model.ModelZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderZombie extends RenderBiped<EntityZombie> {
   private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/zombie.png");

   public RenderZombie(RenderManager renderManagerIn, ModelBiped p_i49206_2_) {
      super(renderManagerIn, p_i49206_2_, 0.5F);
      this.addLayer(this.createArmorLayer());
   }

   public RenderZombie(RenderManager renderManagerIn) {
      this(renderManagerIn, new ModelZombie());
   }

   protected LayerBipedArmor createArmorLayer() {
      return new LayerBipedArmor(this) {
         protected void initArmor() {
            this.modelLeggings = new ModelZombie(0.5F, true);
            this.modelArmor = new ModelZombie(1.0F, true);
         }
      };
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EntityZombie entity) {
      return ZOMBIE_TEXTURES;
   }

   protected void applyRotations(EntityZombie entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      if (entityLiving.isDrowning()) {
         rotationYaw += (float)(Math.cos((double)entityLiving.ticksExisted * 3.25D) * Math.PI * 0.25D);
      }

      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}