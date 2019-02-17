package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IItemPropertyGetter {
   @OnlyIn(Dist.CLIENT)
   float call(ItemStack p_call_1_, @Nullable World p_call_2_, @Nullable EntityLivingBase p_call_3_);
}