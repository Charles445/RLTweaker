package com.charles445.rltweaker.util;

import java.util.HashMap;
import java.util.Map;

import com.charles445.rltweaker.RLTweaker;

import net.minecraft.enchantment.Enchantment;

public class ErrorUtil
{
	public static final Map<String, Integer> errorCount = new HashMap<String,Integer>();
	
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
}
