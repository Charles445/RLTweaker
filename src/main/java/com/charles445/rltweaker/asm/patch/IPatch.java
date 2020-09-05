package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.tree.ClassNode;

public interface IPatch
{
	//yarr
	
	public String getTargetClazz();
	
	public int getFlags();
	
	public IPatchManager getPatchManager();
	
	public void patch(ClassNode clazzNode);
}
