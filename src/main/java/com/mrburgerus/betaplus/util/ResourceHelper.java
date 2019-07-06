package com.mrburgerus.betaplus.util;

import com.mrburgerus.betaplus.BetaPlus;

public class ResourceHelper
{
	public static String getResourceStringBetaPlus(String inputResource)
	{
		return BetaPlus.MOD_NAME + ":" + inputResource;
	}
}
