package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.item.ItemStack;

public class AquacultureReflect
{
	public final Class c_WeightedLootSet;
	public final Method m_WeightedLootSet_addLoot;
	
	public final Class c_ItemNeptunesBounty;
	public final Field f_ItemNeptunesBounty_loot;
	public final Method m_ItemNeptunesBounty_initLoot;
	
	public final Class c_AquacultureItems;
	public final Field f_AquacultureItems_neptunesBounty;
	public final Field f_AquacultureItems_neptuniumBar;
	
	public final Class c_Config;
	public final Field f_Config_enableNeptuniumLoot;
	
	public final Class c_SubItem;
	public final Method m_SubItem_getItemStack;
	
	public final Class c_BiomeType;
	public final Field f_BiomeType_biomeMap;
	public final Field f_BiomeType_freshwater;
	public final Method m_BiomeType_addBiome;
	
	public AquacultureReflect() throws Exception
	{
		c_WeightedLootSet = Class.forName("com.teammetallurgy.aquaculture.loot.WeightedLootSet");
		m_WeightedLootSet_addLoot = ReflectUtil.findMethod(c_WeightedLootSet, "addLoot", ItemStack.class, int.class, int.class, int.class);
		
		c_ItemNeptunesBounty = Class.forName("com.teammetallurgy.aquaculture.items.ItemNeptunesBounty");
		f_ItemNeptunesBounty_loot = ReflectUtil.findField(c_ItemNeptunesBounty, "loot");
		m_ItemNeptunesBounty_initLoot = ReflectUtil.findMethod(c_ItemNeptunesBounty, "initLoot");
		
		c_AquacultureItems = Class.forName("com.teammetallurgy.aquaculture.items.AquacultureItems");
		f_AquacultureItems_neptunesBounty = ReflectUtil.findField(c_AquacultureItems, "neptunesBounty");
		f_AquacultureItems_neptuniumBar = ReflectUtil.findField(c_AquacultureItems, "neptuniumBar");
		
		c_Config = Class.forName("com.teammetallurgy.aquaculture.handlers.Config");
		f_Config_enableNeptuniumLoot = ReflectUtil.findField(c_Config, "enableNeptuniumLoot");
		
		c_SubItem = Class.forName("com.teammetallurgy.aquaculture.items.meta.SubItem");
		m_SubItem_getItemStack = ReflectUtil.findMethod(c_SubItem, "getItemStack", int.class);
		
		c_BiomeType = Class.forName("com.teammetallurgy.aquaculture.loot.BiomeType");
		f_BiomeType_biomeMap = ReflectUtil.findField(c_BiomeType, "biomeMap");
		f_BiomeType_freshwater = ReflectUtil.findField(c_BiomeType, "freshwater");
		m_BiomeType_addBiome = ReflectUtil.findMethod(c_BiomeType, "addBiome");
	}
	
	public Object getNeptunesBountyLoot() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		Object neptunesBounty = f_AquacultureItems_neptunesBounty.get(null); //SubItem
		neptunesBounty = c_ItemNeptunesBounty.cast(neptunesBounty); //ItemNeptunesBounty (forgot if casting is necessary honestly)
		
		Object loot = f_ItemNeptunesBounty_loot.get(neptunesBounty);
		if(loot == null)
		{
			m_ItemNeptunesBounty_initLoot.invoke(neptunesBounty);
			loot = f_ItemNeptunesBounty_loot.get(neptunesBounty);
		}
		
		return loot;
	}
	
	public boolean getNeptuniumLootEnabled() throws IllegalArgumentException, IllegalAccessException
	{
		return (boolean) f_Config_enableNeptuniumLoot.get(null);
	}
	
	public void addLoot(Object weightedLootSet, ItemStack stack, int weight, int min, int max) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		m_WeightedLootSet_addLoot.invoke(weightedLootSet, stack, weight, min, max);
	}
	
	public ItemStack getNeptuniumBarStack() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (ItemStack) m_SubItem_getItemStack.invoke(f_AquacultureItems_neptuniumBar.get(null),1);
	}
	
	public Map<Integer, ArrayList<Object>> getBiomeMap() throws IllegalArgumentException, IllegalAccessException
	{
		return (Map<Integer, ArrayList<Object>>) f_BiomeType_biomeMap.get(null);
	}
	
	public Object getFreshwaterBiomeType() throws IllegalArgumentException, IllegalAccessException
	{
		return f_BiomeType_freshwater.get(null);
	}
}
