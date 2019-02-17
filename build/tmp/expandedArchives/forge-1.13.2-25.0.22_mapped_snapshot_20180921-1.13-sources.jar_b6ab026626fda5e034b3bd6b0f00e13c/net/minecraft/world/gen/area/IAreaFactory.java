package net.minecraft.world.gen.area;

public interface IAreaFactory<A extends IArea> {
   A make(AreaDimension p_make_1_);
}