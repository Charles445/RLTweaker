package com.charles445.rltweaker.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Patch {
	
	/** The path of the target class that this patch will be applied to. */
	String target();
	
	/** A short description of what this patch does (optional). */
	String desc() default "";
}
