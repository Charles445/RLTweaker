package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ModNames;
import com.charles445.rltweaker.util.ReflectUtil;

public class RuinsReflect
{
	public final Object ruinsInstance;
	
	public final Class c_ChunkLoggerData;
	public final Field f_ChunkLoggerData_xCoords;
	public final Field f_ChunkLoggerData_zCoords;
	
	public final Class c_RuinsMod;
	public final Field f_RuinsMod_generatorMap;
	
	public RuinsReflect() throws Exception
	{
		c_ChunkLoggerData = Class.forName("atomicstryker.ruins.common.ChunkLoggerData");
		f_ChunkLoggerData_xCoords = ReflectUtil.findField(c_ChunkLoggerData, "xCoords");
		f_ChunkLoggerData_zCoords = ReflectUtil.findField(c_ChunkLoggerData, "zCoords");
		
		c_RuinsMod = Class.forName("atomicstryker.ruins.common.RuinsMod");
		f_RuinsMod_generatorMap = ReflectUtil.findField(c_RuinsMod, "generatorMap");
		
		ruinsInstance = CompatUtil.getModInstance(ModNames.RUINS);
		if(ruinsInstance==null)
			throw new CriticalException("Couldn't find Ruins Mod Instance!");
	}
	
	public boolean wipeChunkLoggerData(Object chunkLoggerData, int cleanupThreshold) throws IllegalArgumentException, IllegalAccessException
	{
		ArrayList<Integer> xCoords = getXCoords(chunkLoggerData);
		ArrayList<Integer> zCoords = getZCoords(chunkLoggerData);
		
		if(xCoords.size() > cleanupThreshold)
		{
			xCoords.clear();
			zCoords.clear();
			return true;
		}
		
		return false;
	}
	
	public ArrayList<Integer> getXCoords(Object chunkLoggerData) throws IllegalArgumentException, IllegalAccessException
	{
		return (ArrayList<Integer>) f_ChunkLoggerData_xCoords.get(chunkLoggerData);
	}
	
	public ArrayList<Integer> getZCoords(Object chunkLoggerData) throws IllegalArgumentException, IllegalAccessException
	{
		return (ArrayList<Integer>) f_ChunkLoggerData_zCoords.get(chunkLoggerData);
	}
	
	public ConcurrentHashMap<Integer, Object> getGeneratorMap() throws IllegalArgumentException, IllegalAccessException
	{
		return (ConcurrentHashMap<Integer, Object>) f_RuinsMod_generatorMap.get(ruinsInstance);
	}
}
