package com.mrburgerus.betaplus.client.render;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.EnumWorldType;
import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BakedModelBetaPlus implements IDynamicBakedModel
{
    // Fields
    private final IBakedModel modelDefault;
    private final IBakedModel modelAlpha;

    // Constructor
    public BakedModelBetaPlus(IBakedModel modelExisting, IBakedModel modelAlpha)
    {
        modelDefault = modelExisting;
        this.modelAlpha = modelAlpha;
    }

    // Gets model to use, sped up by only evaluating a value that changes on world load.
    private IBakedModel getUsedModel()
    {
        if (BetaPlus.loadedWorldType == EnumWorldType.alphaWorld)
            return modelAlpha;
        else
            return modelDefault;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (side != null)
        {
            return Collections.emptyList();
        }
        return this.getUsedModel().getQuads(state,side,rand,extraData);
    }

    @Override
    public boolean isAmbientOcclusion() {
            return getUsedModel().isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return getUsedModel().isGui3d();
    }

    @Override
    public boolean func_230044_c_() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return getUsedModel().getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return getUsedModel().getOverrides();
    }
}