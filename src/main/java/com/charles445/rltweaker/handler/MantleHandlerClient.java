package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.handler.PotionCoreHandlerClient.PCRenderOverlaysPost;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MantleHandlerClient
{
	public MantleHandlerClient()
	{
		try
		{
			if(ModConfig.client.mantle.removeMantleHealthBar)
			{
				//Remove
				CompatUtil.findAndRemoveHandlerFromEventBus("slimeknights.mantle.client.ExtraHeartRenderHandler", "renderHealthbar");
			}
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup MantleHandlerClient!", e);
			ErrorUtil.logSilent("Mantle Client Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
}
