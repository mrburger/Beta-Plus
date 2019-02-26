package com.mrburgerus.betaplus.client.renderer.model;

import com.mrburgerus.betaplus.util.ResourceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;

import java.util.function.Function;

/* Written to assist me in the journey of registering textures */
public class ModelHelper
{
	public static final ModelResourceLocation ALPHA_GRASS_MODEL_LOCATION = new ModelResourceLocation(ResourceHelper.getResourceStringBetaPlus("alpha_grass_block"));
	public static final ResourceLocation ALPHA_GRASS_BLOCK = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("alpha_grass_block"));
	public static final ResourceLocation NORMAL_GRASS_BLOCK = Blocks.GRASS_BLOCK.getRegistryName();
	public static final IModelState DEFAULT_MODEL_STATE = part -> java.util.Optional.empty();
	public static final VertexFormat DEFAULT_VERTEX_FORMAT = DefaultVertexFormats.BLOCK;
	public static final Function<ResourceLocation, TextureAtlasSprite> DEFAULT_TEXTURE_GETTER = texture ->
			Minecraft.getInstance().getTextureMap().getAtlasSprite(texture.toString());


}
