package com.example.hellosugar;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigHelper 
{
	public static String getKey(Context ctx, String keyName)
	{
		try
		{
			return ctx.getSharedPreferences("系统设置", 0).getString(keyName, "");
		}
		catch (Exception e)
		{
			return "";
		}
	}
	public static void setKey(Context ctx, String keyName, String value)
	{
		SharedPreferences.Editor editor = ctx.getSharedPreferences("系统设置",0).edit();
		editor.putString(keyName,value);
		editor.commit();
	}
	public static int getKeyInt(Context ctx, String keyName)
	{
		try
		{
			return ctx.getSharedPreferences("系统设置",0).getInt(keyName, 0);
		}
		catch (Exception e)
		{
			return 0;
		}
	}
	public static void setKeyInt(Context ctx, String keyName, int value)
	{
		SharedPreferences.Editor editor = ctx.getSharedPreferences("系统设置",0).edit();
		editor.putInt(keyName,value);
		editor.commit();
	}
	public static float getKeyFloat(Context ctx, String keyName)
	{
		try
		{
			return ctx.getSharedPreferences("系统设置",0).getFloat(keyName, 0);
		}
		catch (Exception e)
		{
			return 0;
		}
	}
	public static void setKeyFloat(Context ctx, String keyName, float value)
	{
		SharedPreferences.Editor editor = ctx.getSharedPreferences("系统设置",0).edit();
		editor.putFloat(keyName,value);
		editor.commit();
	}
	public static Long getKeyLong(Context ctx, String keyName)
	{
		try
		{
			return ctx.getSharedPreferences("系统设置",0).getLong(keyName, 0);
		}
		catch (Exception e)
		{
			return 0l;
		}
	}
	public static void setKeyLong(Context ctx, String keyName, long value)
	{
		SharedPreferences.Editor editor = ctx.getSharedPreferences("系统设置",0).edit();
		editor.putLong(keyName,value);
		editor.commit();
	}
}
