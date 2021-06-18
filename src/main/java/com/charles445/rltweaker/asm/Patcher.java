package com.charles445.rltweaker.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Patcher {
	
	/** The name of this set of patches (defaults to the class name). */
	String name() default "";
}
