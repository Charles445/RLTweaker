package com.charles445.rltweaker.asm.util;

public enum ModTransformer
{
	CHARM("svenhjol.charm.base.CharmClassTransformer"),
	LIBRARIANLIB("com.teamwizardry.librarianlib.asm.LibLibTransformer");
	
	private String transformerClassName;
	
	ModTransformer(String str)
	{
		this.transformerClassName = str;
	}
	
	public String getTransformerClassName()
	{
		return this.transformerClassName;
	}
}
