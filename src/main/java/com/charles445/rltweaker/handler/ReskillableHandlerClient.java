package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.client.gui.GuiDelegator;
import com.charles445.rltweaker.client.gui.ReskillableGuiDelegate;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReskillableHandlerClient
{
	public ReskillableHandlerClient()
	{
		try
		{
			if(ModConfig.client.reskillable.toggleableTraits)
				GuiDelegator.addDelegate("codersafterdark.reskillable.client.gui.GuiSkillInfo", new ReskillableGuiDelegate());
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup ReskillableHandlerClient!", e);
			ErrorUtil.logSilent("ReskillableHandler Client Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
}

