package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;

public class InfernalMobsReflect
{
	public final Class c_InfernalMobsCore;
	public final Field f_InfernalMobsCore_instance;
	public final Object o_InfernalMobsCore_instance;
	public final Field f_InfernalMobsCore_enchantmentList;
	public final Method m_InfernalMobsCore_getRandomEnchantment;
	
	public InfernalMobsReflect() throws Exception
	{
		c_InfernalMobsCore = Class.forName("atomicstryker.infernalmobs.common.InfernalMobsCore");
		f_InfernalMobsCore_instance = ReflectUtil.findField(c_InfernalMobsCore, "instance");
		o_InfernalMobsCore_instance = f_InfernalMobsCore_instance.get(null);
		f_InfernalMobsCore_enchantmentList = ReflectUtil.findField(c_InfernalMobsCore, "enchantmentList");
		m_InfernalMobsCore_getRandomEnchantment = ReflectUtil.findMethod(c_InfernalMobsCore, "getRandomEnchantment");
	}
	
	
	
	public ArrayList<Enchantment> getEnchantmentList() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		//Guarantee the enchantment list is populated
		this.populateEnchantmentList();
		
		return (ArrayList<Enchantment>) f_InfernalMobsCore_enchantmentList.get(o_InfernalMobsCore_instance);
	}
	
	private void populateEnchantmentList() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		m_InfernalMobsCore_getRandomEnchantment.invoke(o_InfernalMobsCore_instance, new Random());
	}
}
