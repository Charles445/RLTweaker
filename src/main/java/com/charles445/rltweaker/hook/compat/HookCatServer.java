package com.charles445.rltweaker.hook.compat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.ModContainer;

public class HookCatServer
{
	@Nullable
	private static Class c_CraftingHelper$FactoryLoader = ReflectUtil.findClassOrNull("net.minecraftforge.common.crafting.CraftingHelper$FactoryLoader");
	
	@Nullable
	private static Class cArray_CraftingHelper$FactoryLoader = ReflectUtil.toArrayClass(c_CraftingHelper$FactoryLoader);
	
	@Nullable
	private static Method m_CraftingHelper_loadFactories = ReflectUtil.findMethodOrNull(CraftingHelper.class, "loadFactories", ModContainer.class, String.class, cArray_CraftingHelper$FactoryLoader);
	
	//com/charles445/rltweaker/hook/compat/HookCatServer
	//loadAdvancementFactories
	//(Lnet/minecraftforge/fml/common/ModContainer;Ljava/lang/String;[Ljava/lang/Object;)V
	public static void loadAdvancementFactories(ModContainer mod, String path, Object[] loaders)
	{
		if(c_CraftingHelper$FactoryLoader == null || m_CraftingHelper_loadFactories == null || cArray_CraftingHelper$FactoryLoader == null)
		{
			RLTweaker.logger.error("CatServer hook failed to setup reflection. Advancements condition factories will be broken!");
			ErrorUtil.logSilent("CatServer Advancement Factories Setup");
			return;
		}
		
		//Disenchanter - Remove advancements
		if(mod.getModId().equals("disenchanter"))
		{
			RLTweaker.logger.info("Removing erroneous Disenchanter factories to avoid CatServer crash");
			return;
		}
		
		//Passed all checks
		try
		{
			m_CraftingHelper_loadFactories.invoke(null, mod, path, loaders);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			RLTweaker.logger.error("CatServer hook failed to invoke reflection. Advancements condition factories will be broken!", e);
			ErrorUtil.logSilent("CatServer Advancement Factories Invocation");
		}
	}
}
