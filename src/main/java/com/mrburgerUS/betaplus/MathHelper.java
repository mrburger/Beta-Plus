package com.mrburgerUS.betaplus;

public final class MathHelper
{
	private static float[] SIN_TABLE = new float[65536];

	public static float sin(float degree)
	{
		return SIN_TABLE[(int) (degree * 10430.378f) & 65535];
	}

	public static float cos(float degree)
	{
		return SIN_TABLE[(int) (degree * 10430.378f + 16384.0f) & 65535];
	}

	public static float sqrt_float(float degree)
	{
		return (float) Math.sqrt(degree);
	}

	public static float sqrt_double(double degree)
	{
		return (float) Math.sqrt(degree);
	}

	public static int floor_float(float degree)
	{
		int var1 = (int) degree;
		return degree < (float) var1 ? var1 - 1 : var1;
	}

	public static int floor_double(double degree)
	{
		int var2 = (int) degree;
		return degree < (double) var2 ? var2 - 1 : var2;
	}

	public static float abs(float absV)
	{
		return absV >= 0.0f ? absV : -absV;
	}

	public static double abs_max(double absV1, double absV2)
	{
		if (absV1 < 0.0)
		{
			absV1 = -absV1;
		}
		if (absV2 < 0.0)
		{
			absV2 = -absV2;
		}
		return absV1 > absV2 ? absV1 : absV2;
	}

	public static int bucketInt(int var0, int var1)
	{
		return var0 < 0 ? -(-var0 - 1) / var1 - 1 : var0 / var1;
	}

	public static boolean stringNullOrLengthZero(String var0)
	{
		return var0 == null || var0.length() == 0;
	}

	static
	{
		for (int var0 = 0; var0 < 65536; ++var0)
		{
			MathHelper.SIN_TABLE[var0] = (float) Math.sin((double) var0 * 3.141592653589793 * 2.0 / 65536.0);
		}
	}
}


