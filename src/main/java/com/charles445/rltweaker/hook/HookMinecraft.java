package com.charles445.rltweaker.hook;

import java.util.concurrent.ConcurrentLinkedDeque;

public class HookMinecraft
{
	public static <E> ConcurrentLinkedDeque<E> newConcurrentLinkedDeque()
	{
		return new ConcurrentLinkedDeque<E>();
	}
}
