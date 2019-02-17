package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelZombieVillager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerVillagerArmor extends LayerBipedArmor {
   public LayerVillagerArmor(RenderLivingBase<?> rendererIn) {
      super(rendererIn);
   }

   protected void initArmor() {
      this.modelLeggings = new ModelZombieVillager(0.5F, 0.0F, true);
      this.modelArmor = new ModelZombieVillager(1.0F, 0.0F, true);
   }
}