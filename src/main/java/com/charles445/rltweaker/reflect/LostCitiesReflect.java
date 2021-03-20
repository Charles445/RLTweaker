package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.world.World;

public class LostCitiesReflect
{
	public final Class c_WorldTypeTools;
	public final Method m_WorldTypeTools_getLostCityChunkGenerator;
	public final Method m_WorldTypeTools_isLostCities;
	
	public final Class c_LostCityChunkGenerator;
	public final Field f_LostCityChunkGenerator_cachedPrimers;
	public final Field f_LostCityChunkGenerator_cachedHeightmaps;
	
	public LostCitiesReflect() throws Exception
	{
		c_WorldTypeTools = Class.forName("mcjty.lostcities.dimensions.world.WorldTypeTools");
		m_WorldTypeTools_getLostCityChunkGenerator = ReflectUtil.findMethod(c_WorldTypeTools, "getLostCityChunkGenerator");
		m_WorldTypeTools_isLostCities = ReflectUtil.findMethod(c_WorldTypeTools, "isLostCities");
		
		c_LostCityChunkGenerator = Class.forName("mcjty.lostcities.dimensions.world.LostCityChunkGenerator");
		f_LostCityChunkGenerator_cachedPrimers = ReflectUtil.findField(c_LostCityChunkGenerator, "cachedPrimers");
		f_LostCityChunkGenerator_cachedHeightmaps = ReflectUtil.findField(c_LostCityChunkGenerator, "cachedHeightmaps");
	}
	
	@Nullable
	public Object getLostCityChunkGenerator(World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_WorldTypeTools_getLostCityChunkGenerator.invoke(null, world);
	}
	
	public boolean isLostCities(World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (boolean) m_WorldTypeTools_isLostCities.invoke(null, world);
	}
	
	public Map getCachedPrimers(Object o_LostCityChunkGenerator) throws IllegalArgumentException, IllegalAccessException
	{
		return (Map) f_LostCityChunkGenerator_cachedPrimers.get(o_LostCityChunkGenerator);
	}
	
	public Map getCachedHeightmaps(Object o_LostCityChunkGenerator) throws IllegalArgumentException, IllegalAccessException
	{
		return (Map) f_LostCityChunkGenerator_cachedHeightmaps.get(o_LostCityChunkGenerator);
	}
}
