package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;

import com.charles445.rltweaker.util.ReflectUtil;

public class MultiMineReflect
{
	public final Class c_MultiMine;
	public final Field f_MultiMine_instance;
	public final Object o_MultiMine_instance;
	public final Field f_MultiMine_networkHelper;
	public final Object o_MultiMine_networkHelper;
	
	public final Class c_NetworkHelper;
	public final Field f_NetworkHelper_isCurrentlySendingSemaphor;
	
	public MultiMineReflect() throws Exception
	{
		c_MultiMine = Class.forName("atomicstryker.multimine.common.MultiMine");
		f_MultiMine_instance = ReflectUtil.findField(c_MultiMine, "instance");
		o_MultiMine_instance = f_MultiMine_instance.get(null);
		f_MultiMine_networkHelper = ReflectUtil.findField(c_MultiMine, "networkHelper");
		o_MultiMine_networkHelper = f_MultiMine_networkHelper.get(o_MultiMine_instance);
		
		c_NetworkHelper = Class.forName("atomicstryker.multimine.common.network.NetworkHelper");
		f_NetworkHelper_isCurrentlySendingSemaphor = ReflectUtil.findField(c_NetworkHelper, "isCurrentlySendingSemaphor");
	}
	
	public boolean getSemaphor() throws IllegalArgumentException, IllegalAccessException
	{
		return f_NetworkHelper_isCurrentlySendingSemaphor.getBoolean(o_MultiMine_networkHelper);
	}
	
	public void setSemaphor(boolean val) throws IllegalArgumentException, IllegalAccessException
	{
		f_NetworkHelper_isCurrentlySendingSemaphor.setBoolean(o_MultiMine_networkHelper, val);
	}
}
