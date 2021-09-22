package com.charles445.rltweaker.hook;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;

public class HookEnchant
{
	public static List<EnchantmentData> restrictEnchantmentDatas(List<EnchantmentData> input)
	{
		//TODO remove blacklisted from this list
		//System.out.println("restrictEnchantmentDatas");
		
		Iterator<EnchantmentData> it = input.iterator();
		while(it.hasNext())
		{
			EnchantmentData data = it.next();
			if(isEnchantmentBlacklisted(data.enchantment))
			{
				//System.out.println("restrictEnchantmentDatas: removing "+data.enchantment.getRegistryName().toString());
				it.remove();
			}
				
		
		}
		
		return input;
	}
	
	public static boolean addEnchantmentRestricted(final List<Enchantment> list, Enchantment enchant)
	{
		//TODO only add to list if not blacklisted
		//System.out.println("addEnchantmentRestricted");
		if(!isEnchantmentBlacklisted(enchant))
		{
			return list.add(enchant);
		}
		else
		{
			//System.out.println("addEnchantmentRestricted: removing "+enchant.getRegistryName().toString());
			//Return value gets popped anyway
			return false;
		}
	}
	
	public static Enchantment getRandomRestricted(Object registry, Random rand)
	{
		//TODO get an acceptable non-blacklisted enchantment after many tries, default behavior if none were found
		//System.out.println("getRandomRestricted");
		int overload = 50000;
		
		Enchantment ench = Enchantment.REGISTRY.getRandomObject(rand);
		while(isEnchantmentBlacklisted(ench) && overload > 0)
		{
			//System.out.println("getRandomRestricted: removing "+ench.getRegistryName().toString());
			overload--;
			ench = Enchantment.REGISTRY.getRandomObject(rand);
		}
		
		if(overload <= 0)
		{
			ErrorUtil.logSilent("Enchantment getRandomObject Overload");
			RLTweaker.logger.error("Enchantment getRandomRestricted was unable to get a random enchantment - are all enchantments blacklisted?");
		}
		
		return ench;
	}
	
	//TODO some form of caching for performance, if necessary
	private static boolean isEnchantmentBlacklisted(Enchantment enchant)
	{
		String enchReg = enchant.getRegistryName().toString();
		for(String s : ModConfig.server.minecraft.blacklistedEnchantments)
		{
			if(enchReg.equals(s))
				return true;
		}
		return false;
	}
}
