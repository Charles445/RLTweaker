package com.charles445.rltweaker.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;

public class ReflectUtil
{
	public static Method findMethod(Class clazz, String name) throws Exception
	{
		//Desc is not specified
		for(Method m : clazz.getDeclaredMethods())
		{
			if(m.getName().equals(name))
			{
				m.setAccessible(true);
				return m;
			}
		}
		
		throw new NoSuchMethodException(name);
	}
	
	public static Method findMethodAny(Class clazz, String nameA, String nameB, Class... params) throws Exception
	{
		try
		{
			return findMethod(clazz, nameA, params);
		}
		catch(Exception e)
		{
			return findMethod(clazz, nameB, params);
		}
	}
	
	public static Method findMethod(Class clazz, String name, Class... params) throws Exception
	{
		Method m = clazz.getDeclaredMethod(name, params);
		m.setAccessible(true);
		return m;
	}
	
	public static Field findField(Class clazz, String name) throws Exception
	{
		Field f = clazz.getDeclaredField(name);
		f.setAccessible(true);
		return f;
	}
	
	public static Field findFieldAny(Class clazz, String nameA, String nameB) throws Exception
	{
		try
		{
			return findField(clazz, nameA);
		}
		catch(Exception e)
		{
			return findField(clazz, nameB);
		}
	}
	
	public static Field[] findFields(Class clazz, String... names) throws Exception
	{
		Field[] fields = new Field[names.length];
		
		for(int i = 0; i < fields.length; i++)
		{
			fields[i] = findField(clazz,names[i]);
		}
		
		return fields;
	}
	
	@Nullable
	public static Class toArrayClass(Class clazz)
	{
		if(clazz == null)
			return null;
		
		return Array.newInstance(clazz, 0).getClass();	
	}
	
	//Nullable

	@Nullable
	public static Field findFieldAnyOrNull(Class clazz, String nameA, String nameB)
	{
		try
		{
			try
			{
				return findField(clazz, nameA);
			}
			catch(Exception e)
			{
				return findField(clazz, nameB);
			}
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("findFieldAnyOrNull failure for: "+nameA+" and "+nameB);
			return null;
		}
	}
	
	@Nullable
	public static Field findFieldOrNull(Class clazz, String nameA)
	{
		try
		{
			return findField(clazz, nameA);
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("findFieldOrNull failure for: "+nameA);
			return null;
		}
	}
	
	@Nullable
	public static Class findClassOrNull(String cfn)
	{
		try
		{
			return Class.forName(cfn);
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("findClassOrNull failure for: "+cfn);
			return null;
		}
	}
	
	@Nullable
	public static Method findMethodOrNull(Class clazz, String name)
	{
		try
		{
			return findMethod(clazz, name);
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("findMethodOrNull failure for: "+clazz==null?"null":clazz.getName()+" "+name);
			return null;
		}
	}
	
	@Nullable
	public static Method findMethodOrNull(Class clazz, String name, Class... params)
	{
		try
		{
			return findMethod(clazz, name, params);
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("findMethodOrNull failure for: "+clazz==null?"null":clazz.getName()+" "+name);
			return null;
		}
	}
}
