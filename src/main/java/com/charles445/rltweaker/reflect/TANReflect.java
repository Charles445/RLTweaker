package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class TANReflect
{
	public final Class c_ThirstHelper;
	public final Method m_ThirstHelper_getThirstData;
	
	public final Class c_IThirst;
	public final Method m_IThirst_getThirst;
	public final Method m_IThirst_getHydration;
	public final Method m_IThirst_getExhaustion;
	public final Method m_IThirst_setExhaustion;
	
	public final Class c_IPlayerStat;
	public final Method m_IPlayerStat_createUpdateMessage;
	
	public final Class c_PacketHandler;
	public final Field f_PacketHandler_instance;
	
	public TANReflect() throws Exception
	{
		c_ThirstHelper = Class.forName("toughasnails.api.thirst.ThirstHelper");
		m_ThirstHelper_getThirstData = ReflectUtil.findMethod(c_ThirstHelper, "getThirstData");
		
		c_IThirst = Class.forName("toughasnails.api.stat.capability.IThirst");
		m_IThirst_getThirst = ReflectUtil.findMethod(c_IThirst, "getThirst");
		m_IThirst_getHydration = ReflectUtil.findMethod(c_IThirst, "getHydration");
		m_IThirst_getExhaustion = ReflectUtil.findMethod(c_IThirst, "getExhaustion");
		m_IThirst_setExhaustion = ReflectUtil.findMethod(c_IThirst, "setExhaustion");
		
		c_IPlayerStat = Class.forName("toughasnails.api.stat.IPlayerStat");
		m_IPlayerStat_createUpdateMessage = ReflectUtil.findMethod(c_IPlayerStat, "createUpdateMessage");
		
		c_PacketHandler = Class.forName("toughasnails.handler.PacketHandler");
		f_PacketHandler_instance = ReflectUtil.findField(c_PacketHandler, "instance");
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
	
	public SimpleNetworkWrapper getPacketHandlerInstance() throws IllegalArgumentException, IllegalAccessException
	{
		return (SimpleNetworkWrapper) f_PacketHandler_instance.get(null);
	}
	
	public IMessage createUpdateMessageWithStat(Object stat) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (IMessage) m_IPlayerStat_createUpdateMessage.invoke(stat);
	}
}
