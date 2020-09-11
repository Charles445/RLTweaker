package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.RLTweakerASM;

public abstract class PatchManager implements IPatchManager
{
	private String name;
	
	public PatchManager()
	{
		this.name = "PatchManager";
	}
	
	public PatchManager(String name)
	{
		System.out.println("RLTweakerASM PatchManager: "+name);
		this.name = name;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	public void add(Patch patch)
	{
		RLTweakerASM.addPatch(patch);
	}
}
