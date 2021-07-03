package com.charles445.rltweaker.handler;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ModNames;
import com.google.common.base.Predicate;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;

public class CharmHandler
{
	public static Map<String, EnumEnchantmentType> enchantmentTypes = new HashMap<>();
	
	public CharmHandler()
	{
		if(ModConfig.server.charm.fixIncorrectItemEnchantments)
			fixIncorrectItemEnchantments();
		
		//No event bus registration yet
	}
	
	private void fixIncorrectItemEnchantments()
	{
		//Fix for enchantment types
		
		fixEnchantmentWithPredicate("curse_break", item -> false); //Book only
		fixEnchantmentWithPredicate("homing", item -> item == Items.IRON_HOE || item == Items.DIAMOND_HOE || item == Items.GOLDEN_HOE); //Specific vanilla hoes
		fixEnchantmentWithPredicate("magnetic", item -> item == Items.SHEARS || item instanceof ItemTool); //All tools, shears
	}
	
	private void fixEnchantmentWithType(String enchantName, EnumEnchantmentType type)
	{
		Enchantment enchant = getEnchantmentByName(enchantName);
		if(enchant == null)
		{
			ErrorUtil.logSilent("Charm Missing Enchantment "+enchantName);
			RLTweaker.logger.warn("Couldn't find Charm enchantment: "+enchantName);
		}
		else
		{
			enchant.type = type;
		}
	}
	
	private void fixEnchantmentWithPredicate(String enchantName, Predicate<Item> delegate)
	{
		Enchantment enchant = getEnchantmentByName(enchantName);
		if(enchant == null)
		{
			ErrorUtil.logSilent("Charm Missing Enchantment "+enchantName);
			RLTweaker.logger.warn("Couldn't find Charm enchantment: "+enchantName);
		}
		else
		{
			String typeName = "RLTweaker Charm "+enchantName;
			EnumEnchantmentType type = EnumHelper.addEnchantmentType(typeName, delegate);
			if(type==null)
			{
				ErrorUtil.logSilent("Charm addEnchantmentType "+typeName);
				RLTweaker.logger.error("Found Charm enchantment but addEnchantmentType failed: "+typeName);
			}
			else
			{
				enchantmentTypes.put(typeName, type);
				enchant.type = type;
				RLTweaker.logger.info("Replaced "+enchantName+" enchantment type with "+typeName);
			}
		}
	}
	
	@Nullable
	private Enchantment getEnchantmentByName(String name)
	{
		return Enchantment.REGISTRY.getObject(new ResourceLocation(ModNames.CHARM, name));
	}
}
