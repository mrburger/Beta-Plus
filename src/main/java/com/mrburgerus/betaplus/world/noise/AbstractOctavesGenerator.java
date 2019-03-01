package com.mrburgerus.betaplus.world.noise;

public class AbstractOctavesGenerator implements IOctavesGenerator
{
	final int bound;

	/* Probably horrible Data Management, but I'm not a computer scientist! */
	AbstractOctavesGenerator(int boundIn)
	{
		this.bound = boundIn;
	}
}
