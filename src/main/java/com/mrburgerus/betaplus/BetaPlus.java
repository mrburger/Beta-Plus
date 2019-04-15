package com.mrburgerus.betaplus;

import com.mrburgerus.betaplus.world.beta_plus.BetaChunkGenerator;
import com.mrburgerus.betaplus.world.beta_plus.BetaChunkGeneratorConfig;
import com.mrburgerus.betaplus.world.beta_plus.BetaLevelType;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.level.LevelGeneratorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

public class BetaPlus implements ModInitializer
{
	public static LevelGeneratorType BETA_LEVEL_TYPE = null;
	public static ChunkGeneratorType<BetaChunkGeneratorConfig, BetaChunkGenerator> BETA_PLUS;

	@Override
	public void onInitialize()
	{
		// Do stuff
		BETA_LEVEL_TYPE = BetaLevelType.getType();

		CreateBetaGenerator factory = new CreateBetaGenerator();

		BETA_PLUS = factory.getChunkGeneratorType(BetaChunkGeneratorConfig::new);

		Registry.register(Registry.CHUNK_GENERATOR_TYPE, "beta_plus:beta_plus", BETA_PLUS);

	}

	public static Identifier locate(String location)
	{
		return new Identifier("beta_plus", location);
	}

	// AHEM, THIS IS VERY HACKY AND TEMPORARY
	private class CreateBetaGenerator implements InvocationHandler
	{
		private Object factoryProxy;
		private Class factoryClass;

		CreateBetaGenerator(){
			//reflection hack, dev = mapped in dev enviroment, prod = intermediate value
			String dev_name = "net.minecraft.world.gen.chunk.ChunkGeneratorFactory";
			String prod_name = "net.minecraft.class_2801"; //2801

			try {
				factoryClass = Class.forName(dev_name);
				System.out.println("YA!");
			} catch (ClassNotFoundException e1){
				try {
					factoryClass = Class.forName(prod_name);
				}catch (ClassNotFoundException e2){
					throw(new RuntimeException("Unable to find " + dev_name));
				}
			}
			factoryProxy = Proxy.newProxyInstance(factoryClass.getClassLoader(),
					new Class[] {factoryClass}, this);
		}

		public BetaChunkGenerator createProxy(World w, BiomeSource biomesource, BetaChunkGeneratorConfig gensettings) {
			return new BetaChunkGenerator(w,biomesource,gensettings);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if(args.length == 3 &&
					args[0] instanceof World &&
					args[1] instanceof BiomeSource &&
					args[2] instanceof BetaChunkGeneratorConfig
					){

				return createProxy((World)args[0],
						(BiomeSource)args[1],
						(BetaChunkGeneratorConfig) args[2]);
			}
			throw(new UnsupportedOperationException("Unknown Method: " + method.toString()));
		}

		public ChunkGeneratorType getChunkGeneratorType(Supplier<BetaChunkGeneratorConfig> supplier){
			Constructor<?>[] initlst = ChunkGeneratorType.class.getDeclaredConstructors();
			final Logger log = LogManager.getLogger("ChunkGenErr");
			for(Constructor<?> init : initlst){
				init.setAccessible(true);
				if(init.getParameterCount() != 3)
				{
					continue; //skip
				}
				//lets try it
				try
				{
					return (ChunkGeneratorType) init.newInstance(factoryProxy, true, supplier);
				}
				catch (Exception e)
				{
					log.error("Error in calling Chunk Generator Type!", e);
				}
			}
			log.error("Unable to find constructor for ChunkGeneratorType");
			return null;
		}
	}

}
