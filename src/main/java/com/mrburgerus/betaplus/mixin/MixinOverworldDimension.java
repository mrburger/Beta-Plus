package com.mrburgerus.betaplus.mixin;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.world.beta_plus.BetaBiomeProvider;
import com.mrburgerus.betaplus.world.beta_plus.BetaChunkGenerator;
import com.mrburgerus.betaplus.world.beta_plus.BetaChunkGeneratorConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OverworldDimension.class)
public abstract class MixinOverworldDimension extends Dimension
{

	public MixinOverworldDimension(World world_1, DimensionType dimensionType_1)
	{
		super(world_1, dimensionType_1);
	}

	private static final Logger LOGGER = LogManager.getLogger("ChunkGenType");

	@Inject(method = "createChunkGenerator", at = @At("RETURN"), cancellable = true)
	public void createChunkGenerator(CallbackInfoReturnable<ChunkGenerator<? extends ChunkGeneratorConfig>> info)
	{
		LevelGeneratorType type = this.world.getLevelProperties().getGeneratorType();
		ChunkGeneratorType<BetaChunkGeneratorConfig, BetaChunkGenerator> betaPlus = BetaPlus.BETA_PLUS;

		if(type == BetaPlus.BETA_LEVEL_TYPE){
			CompoundTag opts = this.world.getLevelProperties().getGeneratorOptions();
			BetaChunkGeneratorConfig settings = new BetaChunkGeneratorConfig();
			VanillaLayeredBiomeSourceConfig biomeSrcCfg = ( BiomeSourceType.VANILLA_LAYERED.getConfig()).setGeneratorSettings(new OverworldChunkGeneratorConfig()).setLevelProperties(this.world.getLevelProperties());

			settings.initBuffet(false);
			//noinspection unchecked
			info.setReturnValue(
					betaPlus.create(this.world, new BetaBiomeProvider(world, biomeSrcCfg, settings),settings)
			);
		}
		if(type == LevelGeneratorType.BUFFET)
		{
			// YEET ACROSS THE MAP
		}
	}
}
