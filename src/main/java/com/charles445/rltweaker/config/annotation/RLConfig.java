package com.charles445.rltweaker.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
public @interface RLConfig
{
	//TODO alternative criterion for defaults depending on environment
	//For example, turning radius changing patches on when max entity radius is high
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface MapSignature
	{
		//TODO map support
		String key();
		String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@interface SpecialSignature
	{
		SpecialEnum value();
		String pass();
		String fail();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface ImprovementsOnly
	{
		String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface RLCraftTwoEightTwo
	{
		String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface RLCraftTwoNine
	{
		String value();
	}
}
