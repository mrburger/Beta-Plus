package com.mrburgerUS.betaplus;

public final class BetaPlusHelper
{
	// Whether to generate dungeons
	public static boolean doGenerateDungeons = true;
	//Chance for Dungeons
	public static int dungeonChance = 8;
	// Whether to generate mineshafts
	public static boolean doGenerateMineshafts = true;
	// Whether to generate Strongholds
	public static boolean doGenerateStrongholds = true;
	// Whether to generate Pyramids (Scattered Features)
	public static boolean doGeneratePyramids = true;
	//Max Jungle Temple, Desert Pyramid, and other structures distances.
	public static int maxDistanceBetweenPyramids = 20;

	//World Features
	public static boolean doGenerateRavines = true;
	public static int waterLakeChance = 20;
	// Minimum depth to be considered "Deep Ocean"
	public static int seaDepth = 5;
}
