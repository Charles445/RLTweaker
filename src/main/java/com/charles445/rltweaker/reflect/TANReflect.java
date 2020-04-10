package com.charles445.rltweaker.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.entity.player.EntityPlayer;

public class TANReflect
{
	public final Class c_ThirstHelper;
	public final Method m_ThirstHelper_getThirstData;
	
	public final Class c_IThirst;
	public final Method m_IThirst_getThirst;
	public final Method m_IThirst_getHydration;
	public final Method m_IThirst_getExhaustion;
	public final Method m_IThirst_setExhaustion;
	
	public TANReflect() throws Exception
	{
		c_ThirstHelper = Class.forName("toughasnails.api.thirst.ThirstHelper");
		m_ThirstHelper_getThirstData = ReflectUtil.findMethod(c_ThirstHelper, "getThirstData");
		
		c_IThirst = Class.forName("toughasnails.api.stat.capability.IThirst");
		m_IThirst_getThirst = ReflectUtil.findMethod(c_IThirst, "getThirst");
		m_IThirst_getHydration = ReflectUtil.findMethod(c_IThirst, "getHydration");
		m_IThirst_getExhaustion = ReflectUtil.findMethod(c_IThirst, "getExhaustion");
		m_IThirst_setExhaustion = ReflectUtil.findMethod(c_IThirst, "setExhaustion");
	}
	
	public Object getThirstData(EntityPlayer player) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_ThirstHelper_getThirstData.invoke(null, player);
	}
	
	public int readThirstFromData(Object data) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (int) m_IThirst_getThirst.invoke(data);
	}
	
	public float readHydrationFromData(Object data) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (float) m_IThirst_getHydration.invoke(data);
	}
	
	public float readExhaustionFromData(Object data) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (float) m_IThirst_getExhaustion.invoke(data);
	}
	
	public void setExhaustionInData(Object data, float exhaustion) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		m_IThirst_setExhaustion.invoke(data, exhaustion);
	}
}
