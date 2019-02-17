package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.entity.model.ModelHorseArmorBase;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHorseUndead extends RenderAbstractHorse<EntityHorse> {
   private static final Map<Class<?>, ResourceLocation> field_195638_a = Maps.newHashMap(ImmutableMap.of(EntityZombieHorse.class, new ResourceLocation("textures/entity/horse/horse_zombie.png"), EntitySkeletonHorse.class, new ResourceLocation("textures/entity/horse/horse_skeleton.png")));

   public RenderHorseUndead(RenderManager p_i48133_1_) {
      super(p_i48133_1_, new ModelHorseArmorBase(), 1.0F);
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(AbstractHorse entity) {
      return field_195638_a.get(entity.getClass());
   }
}