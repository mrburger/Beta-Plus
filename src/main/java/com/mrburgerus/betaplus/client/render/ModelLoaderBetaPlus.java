package com.mrburgerus.betaplus.client.render;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.IModelLoader;

public enum ModelLoaderBetaPlus implements IModelLoader<ModelGeometryBetaPlus>
{
    // Fields
    INSTANCE;


    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public ModelGeometryBetaPlus read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        return new ModelGeometryBetaPlus();
    }
}
