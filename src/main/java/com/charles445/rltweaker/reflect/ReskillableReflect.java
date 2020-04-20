package com.charles445.rltweaker.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

public class ReskillableReflect
{
	public final Class c_TransmutationRegistry;
	public final Method m_TransmutationRegistry_addEntryToReagent;
	
	public ReskillableReflect() throws Exception
	{
		c_TransmutationRegistry = Class.forName("codersafterdark.reskillable.api.transmutations.TransmutationRegistry");
		m_TransmutationRegistry_addEntryToReagent = ReflectUtil.findMethod(c_TransmutationRegistry, "addEntryToReagent");
	}
	
	public void addEntryToReagent(Item item, IBlockState state1, IBlockState state2) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		m_TransmutationRegistry_addEntryToReagent.invoke(null, item, state1, state2);
	}
}
