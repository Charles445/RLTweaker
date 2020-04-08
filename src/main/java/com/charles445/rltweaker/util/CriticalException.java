package com.charles445.rltweaker.util;

public class CriticalException extends RuntimeException
{
	//Catchable, should crash the game if it shows up
	public CriticalException(Throwable t)
	{
		super(t);
	}
}
