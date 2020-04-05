package com.mrburgerus.betaplus.client.render;

import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.client.ClientProxy;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

/* Built with help from McJty tutorial */
public class ModelGeometryBetaPlus implements IModelGeometry<ModelGeometryBetaPlus>
{
    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation)
    {
        return null;
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {

        // What about other blocks?
        return Collections.singletonList(new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, ClientProxy.GRASS_BLOCK_LOCATION));
    }
}
