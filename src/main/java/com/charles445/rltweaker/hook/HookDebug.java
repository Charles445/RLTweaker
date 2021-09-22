package com.charles445.rltweaker.hook;

import com.charles445.rltweaker.debug.DebugUtil;

public class HookDebug
{
	//com/charles445/rltweaker/hook/HookDebug
	//printObject
	//(Ljava/lang/Object;)V
	public static void printObject(Object o)
	{
		if(o == null)
			return;
		
		String s = o.toString();
		
		DebugUtil.messageAll(s);
	}
}
