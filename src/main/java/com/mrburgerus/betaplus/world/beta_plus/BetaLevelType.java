package com.mrburgerus.betaplus.world.beta_plus;

import net.minecraft.world.level.LevelGeneratorType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class BetaLevelType
{
	public static LevelGeneratorType getType(){
		LevelGeneratorType val;
		int id=7;
		Field types = null;

		for(Field f : LevelGeneratorType.class.getDeclaredFields()){
			if(f.getType()==LevelGeneratorType[].class){
				types = f;
			}
		}

		if(types != null){
			try {
				LevelGeneratorType newTypes[] = new LevelGeneratorType[LevelGeneratorType.TYPES.length+1];

				System.arraycopy(LevelGeneratorType.TYPES, 0, newTypes, 0, LevelGeneratorType.TYPES.length);
				newTypes[newTypes.length-1] = null;

				types.setAccessible(true);
				Field modifies = Field.class.getDeclaredField("modifiers");
				modifies.setAccessible(true);

				modifies.setInt(types, types.getModifiers() & ~Modifier.FINAL);
				types.set(null,newTypes);
				id=LevelGeneratorType.TYPES.length - 1;
			} catch (IllegalAccessException | NoSuchFieldException e) {
				return null;
			}
		}
		else{
			return null;
		}
		try {
			Constructor<LevelGeneratorType> c =
					LevelGeneratorType.class.getDeclaredConstructor(int.class, String.class);
			c.setAccessible(true);
			val = c.newInstance(id, "BETA_PLUS");
			val.setCustomizable(false);
		} catch (Exception e) {
			return null;
		}

		return val;
	}
}
