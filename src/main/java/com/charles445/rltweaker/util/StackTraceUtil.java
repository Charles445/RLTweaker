package com.charles445.rltweaker.util;

import javax.annotation.Nullable;

public class StackTraceUtil
{
	/** Check if the stack trace contains a class **/
	public static boolean stackTraceHasClass(String clazz)
	{
		for(StackTraceElement ste : Thread.currentThread().getStackTrace())
		{
			if(ste.getClassName().equals(clazz))
				return true;
		}
		
		return false;
	}
	
	/** Check if the stack trace contains a method of a class. Can provide multiple method names for obfuscation purposes**/
	public static boolean stackTraceHasClassOfMethod(String clazz, String... methods)
	{
		for(StackTraceElement ste : Thread.currentThread().getStackTrace())
		{
			if(ste.getClassName().equals(clazz))
			{
				for(String m : methods)
				{
					if(ste.getMethodName().equals(m))
						return true;
				}
			}
		}
		
		return false;
	}
}
