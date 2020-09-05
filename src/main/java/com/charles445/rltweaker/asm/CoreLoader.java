package com.charles445.rltweaker.asm;

import java.util.Map;

import com.charles445.rltweaker.asm.helper.ObfHelper;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("RLTweaker ASM")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({ "com.charles445.rltweaker", "com.charles445.rltweaker." })

public class CoreLoader implements IFMLLoadingPlugin
{
    //
    // IFMLLoadingPlugin
    // 

	@Override
	public String[] getASMTransformerClass()
	{
		ASMConfig.setup();
		return new String[] { "com.charles445.rltweaker.asm.RLTweakerASM" };
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		ObfHelper.setObfuscated((Boolean) data.get("runtimeDeobfuscationEnabled"));
		ObfHelper.setRunsAfterDeobfRemapper(true);
	}

	@Override
	public String getModContainerClass()
	{
		return null;
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}
