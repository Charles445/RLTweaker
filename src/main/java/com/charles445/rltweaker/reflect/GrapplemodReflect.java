package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.util.HashSet;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;

public class GrapplemodReflect
{
	//com.yyon.grapplinghook
	public final Class c_grapplemod;
	public final Field f_grapplemod_attached;
	public HashSet<Integer> o_grapplemod_attached;
	
	public GrapplemodReflect() throws Exception
	{
		c_grapplemod = Class.forName("com.yyon.grapplinghook.grapplemod");
		f_grapplemod_attached = ReflectUtil.findField(c_grapplemod, "attached");
	}
	
	public boolean getIsAttached(int id)
	{
		if(o_grapplemod_attached == null)
		{
			try
			{
				o_grapplemod_attached = (HashSet<Integer>) f_grapplemod_attached.get(null);
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				ErrorUtil.logSilent("Grapplemod Error getIsAttached");
				return false;
			}
			
			if(o_grapplemod_attached == null)
				return false;
		}
		
		return o_grapplemod_attached.contains(id);
	}
}
