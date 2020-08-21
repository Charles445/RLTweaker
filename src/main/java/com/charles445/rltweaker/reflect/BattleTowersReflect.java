package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.entity.Entity;

public class BattleTowersReflect
{
	public final Class c_AS_BattleTowersCore;
	public final Method m_AS_BattleTowersCore_getTowerDestroyers;
	
	public final Class c_AS_TowerDestroyer;
	public final Field f_AS_TowerDestroyer_player;
	
	public BattleTowersReflect() throws Exception
	{
		c_AS_BattleTowersCore = Class.forName("atomicstryker.battletowers.common.AS_BattleTowersCore");
		m_AS_BattleTowersCore_getTowerDestroyers = ReflectUtil.findMethod(c_AS_BattleTowersCore, "getTowerDestroyers");
		
		c_AS_TowerDestroyer = Class.forName("atomicstryker.battletowers.common.AS_TowerDestroyer");
		f_AS_TowerDestroyer_player = ReflectUtil.findField(c_AS_TowerDestroyer, "player");
	
	}
	
	public Set<Object> getTowerDestroyers() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (Set<Object>) m_AS_BattleTowersCore_getTowerDestroyers.invoke(null);
	}
	
	public void setDestroyerPlayer(Object towerDestroyer, @Nullable Entity entityToSet) throws IllegalArgumentException, IllegalAccessException
	{
		f_AS_TowerDestroyer_player.set(towerDestroyer, entityToSet);
	}
}
