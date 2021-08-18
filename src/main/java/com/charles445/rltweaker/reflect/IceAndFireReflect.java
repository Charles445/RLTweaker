package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.entity.Entity;

public class IceAndFireReflect
{
	//Ice And Fire
	public final Class c_StoneEntityProperties;
	public final Field f_StoneEntityProperties_isStone;
	
	public final Class c_ItemStoneStatue;
	
	public final Class c_EntityDragonBase;
	
	@Nullable
	public Class c_ItemDragonHornStatic;
	
	//LLibrary
	public final Class c_EntityPropertiesHandler;
	public final Field f_EntityPropertiesHandler_INSTANCE;
	public final Method m_EntityPropertiesHandler_getProperties;
	public final Object o_EntityPropertiesHandler_INSTANCE;
	
	
	public IceAndFireReflect() throws Exception
	{
		c_EntityDragonBase = Class.forName("com.github.alexthe666.iceandfire.entity.EntityDragonBase");
		
		c_StoneEntityProperties = Class.forName("com.github.alexthe666.iceandfire.entity.StoneEntityProperties");
		f_StoneEntityProperties_isStone = ReflectUtil.findField(c_StoneEntityProperties, "isStone");
		
		c_ItemStoneStatue = Class.forName("com.github.alexthe666.iceandfire.item.ItemStoneStatue");
		
		c_EntityPropertiesHandler = Class.forName("net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler");
		f_EntityPropertiesHandler_INSTANCE = ReflectUtil.findField(c_EntityPropertiesHandler, "INSTANCE");
		m_EntityPropertiesHandler_getProperties = ReflectUtil.findMethod(c_EntityPropertiesHandler, "getProperties");
		o_EntityPropertiesHandler_INSTANCE = f_EntityPropertiesHandler_INSTANCE.get(null); //public static final (Enum)
		
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
}
