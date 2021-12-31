package com.charles445.rltweaker.asm.helper;

import org.objectweb.asm.ClassWriter;

/**
 * From the very helpful ASMHelper pack
 * 
 * https://github.com/squeek502/ASMHelper
 * 
 * 
 * {@link ClassWriter#getCommonSuperClass} needed to be overwritten 
 * in order to avoid ClassNotFoundExceptions in obfuscated environments.
 * 
 * Modified to interact with ComputeClassWriter by Eric Bruneton
 */

public class ObfRemappingClassWriter extends ComputeClassWriter
{
	public ObfRemappingClassWriter(int flags)
	{
		super(flags);
	}

	@Override
	protected String getCommonSuperClass(final String type1, final String type2)
	{
		return ObfHelper.toObfClassName(super.getCommonSuperClass(ObfHelper.toDeobfClassName(type1.replace('/', '.')), type2.replace('/', '.'))).replace('.', '/');
	}
}
