package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class WaystoneReflect
{
	public final Class c_GenerateWaystoneNameEvent;
	public final Method m_getWaystoneName;
	public final Method m_setWaystoneName;
	public final Method m_getPos;
	public final Method m_getDimension;
	
	public final Class c_NameGenerator;
	public final Method m_get;
	public final Field f_usedNames;
	
	public final Class c_RomanNumber;
	public final Method m_toRoman;
	
	/*
	 * public String getWaystoneName() {
        return waystoneName;
    }

    public void setWaystoneName(String waystoneName) {
        this.waystoneName = waystoneName;
    }
	 */
	
	public WaystoneReflect() throws Exception
	{
		c_GenerateWaystoneNameEvent = Class.forName("net.blay09.mods.waystones.util.GenerateWaystoneNameEvent");
		m_getWaystoneName = ReflectUtil.findMethod(c_GenerateWaystoneNameEvent, "getWaystoneName");
		m_setWaystoneName = ReflectUtil.findMethod(c_GenerateWaystoneNameEvent, "setWaystoneName");
		m_getPos = ReflectUtil.findMethod(c_GenerateWaystoneNameEvent, "getPos");
		m_getDimension = ReflectUtil.findMethod(c_GenerateWaystoneNameEvent, "getDimension");
		
		c_NameGenerator = Class.forName("net.blay09.mods.waystones.worldgen.NameGenerator");
		m_get = ReflectUtil.findMethod(c_NameGenerator, "get");
		f_usedNames = ReflectUtil.findField(c_NameGenerator, "usedNames");
		
		c_RomanNumber = Class.forName("net.blay09.mods.waystones.worldgen.RomanNumber");
		m_toRoman = ReflectUtil.findMethod(c_RomanNumber, "toRoman");
	}
	
	public String toRoman(int value) throws Exception
	{
		return (String)m_toRoman.invoke(null, value);
	}
	
	public String getWaystoneName(Object event) throws Exception
	{
		return (String)m_getWaystoneName.invoke(event);
	}
	
	public void setWaystoneName(Object event, String s) throws Exception
	{
		m_setWaystoneName.invoke(event, s);
	}
	
	public BlockPos getPos(Object event) throws Exception
	{
		return (BlockPos)m_getPos.invoke(event);
	}
	
	public int getDimension(Object event) throws Exception
	{
		return (int)m_getDimension.invoke(event);
	}
	
	public Set<String> getUsedNames(int dimension) throws Exception
	{
		World world = DimensionManager.getWorld(dimension);
		Object generator = m_get.invoke(null, world);
		return (Set<String>) f_usedNames.get(generator);
	}
}
