package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.entity.Entity;
import net.minecraft.village.MerchantRecipeList;

public class IceAndFireReflect
{
	//Ice And Fire
	public final Class c_StoneEntityProperties;
	public final Field f_StoneEntityProperties_isStone;
	
	public final Class c_ItemStoneStatue;
	
	public final Class c_EntityDragonBase;
	
	public final Class c_EntityMyrmexQueen;
	
	public final Class c_EntityMyrmexBase;
	public final Field f_EntityMyrmexBase_buyingList;
	public final Method m_EntityMyrmexBase_populateBuyingList;
	public final Method m_EntityMyrmexBase_isJungle;
	
	@Nullable
	public Class c_ItemDragonHornStatic;
	
	//LLibrary
	public final Class c_EntityPropertiesHandler;
	public final Field f_EntityPropertiesHandler_INSTANCE;
	public final Method m_EntityPropertiesHandler_getProperties;
	public final Object o_EntityPropertiesHandler_INSTANCE;
	
	public IceAndFireReflect() throws Exception
	{
		//Ice And Fire
		c_EntityDragonBase = Class.forName("com.github.alexthe666.iceandfire.entity.EntityDragonBase");
		
		c_StoneEntityProperties = Class.forName("com.github.alexthe666.iceandfire.entity.StoneEntityProperties");
		f_StoneEntityProperties_isStone = ReflectUtil.findField(c_StoneEntityProperties, "isStone");
		
		c_ItemStoneStatue = Class.forName("com.github.alexthe666.iceandfire.item.ItemStoneStatue");
		
		c_EntityMyrmexQueen = Class.forName("com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen");
		
		c_EntityMyrmexBase = Class.forName("com.github.alexthe666.iceandfire.entity.EntityMyrmexBase");
		f_EntityMyrmexBase_buyingList = ReflectUtil.findField(c_EntityMyrmexBase, "buyingList");
		m_EntityMyrmexBase_populateBuyingList = ReflectUtil.findMethod(c_EntityMyrmexBase, "populateBuyingList");
		m_EntityMyrmexBase_isJungle = ReflectUtil.findMethod(c_EntityMyrmexBase, "isJungle");

		//LLibrary
		c_EntityPropertiesHandler = Class.forName("net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler");
		f_EntityPropertiesHandler_INSTANCE = ReflectUtil.findField(c_EntityPropertiesHandler, "INSTANCE");
		m_EntityPropertiesHandler_getProperties = ReflectUtil.findMethod(c_EntityPropertiesHandler, "getProperties");
		o_EntityPropertiesHandler_INSTANCE = f_EntityPropertiesHandler_INSTANCE.get(null); //public static final (Enum)
		
		//Ice And Fire
		try
		{
			c_ItemDragonHornStatic = Class.forName("com.github.alexthe666.iceandfire.item.ItemDragonHornStatic");
		}
		catch(Exception e)
		{
			c_ItemDragonHornStatic = null;
		}
	}
	
	public boolean getIsStone(Entity entity)
	{
		try
		{
			Object stoneProperty = getStoneProperty(entity);
			if(stoneProperty == null)
				return false;
			
			return f_StoneEntityProperties_isStone.getBoolean(stoneProperty);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			ErrorUtil.logSilent("IceAndFire Error getIsStone");
			return false;
		}
	}
	
	@Nullable
	public Object getStoneProperty(Entity entity) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return getProperties(entity, c_StoneEntityProperties);
	}
	
	protected Object getProperties(Entity entity, Class propertiesClazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_EntityPropertiesHandler_getProperties.invoke(o_EntityPropertiesHandler_INSTANCE, entity, propertiesClazz);
	}
	
	@Nullable
	public MerchantRecipeList getMyrmexTrades(Object myrmexBase) throws IllegalArgumentException, IllegalAccessException
	{
		return (MerchantRecipeList) f_EntityMyrmexBase_buyingList.get(myrmexBase);
	}
	
	public void resetMyrmexTrades(Object myrmexBase) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		f_EntityMyrmexBase_buyingList.set(myrmexBase, null);
		m_EntityMyrmexBase_populateBuyingList.invoke(myrmexBase);
	}
	
	public boolean isMyrmexJungle(Object myrmexBase) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (boolean) m_EntityMyrmexBase_isJungle.invoke(myrmexBase);
	}
}
