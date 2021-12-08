package com.charles445.rltweaker.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.charles445.rltweaker.RLTweaker;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.ICrashCallable;

public class ErrorUtil
{
	public static final Map<String, Integer> errorCount = new ConcurrentHashMap<String,Integer>();
	
	public static void logEnchantmentError(Enchantment enchantment)
	{
		if(enchantment==null)
		{
			RLTweaker.logger.error("logEnchantmentError was sent a null enchantment value!");
			return;
		}
		
		incrementError("Enchantment "+enchantment.getRegistryName().toString());
	}
	
	public static void logEnchantmentHandlerError(Enchantment enchantment)
	{
		if(enchantment==null)
		{
			RLTweaker.logger.error("logEnchantmentHandlerError was sent a null enchantment value!");
			return;
		}
		
		incrementError("Enchantment Handler "+enchantment.getRegistryName().toString());
	}
	
	public static void logSilent(String key)
	{
		incrementError(key);
	}
	
	private static void incrementError(String key)
	{
		Integer got = errorCount.get(key);
		if(got==null)
		{
			errorCount.put(key, 1);
		}
		else
		{
			errorCount.put(key, got + 1);
		}
	}
	
	public static class CrashCallable implements ICrashCallable
	{
		@Override
		public String call() throws Exception
		{
			StringBuilder sb = new StringBuilder();
			sb.append("\n ");
			for(Map.Entry<String, Integer> entry : errorCount.entrySet())
			{
				sb.append(entry.getKey());
				sb.append(" : ");
				sb.append(entry.getValue());
				sb.append("\n ");
			}
			
			return sb.toString();
		}

		@Override
		public String getLabel()
		{
			return "RLTweaker Error Report";
		}
		
	}
}
