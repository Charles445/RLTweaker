package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
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
public class PotionCoreHandlerClient
{
	public PotionCoreHandlerClient()
	{
		try
		{
			if(ModConfig.client.potioncore.magicShieldingHUDFix)
			{
				//Wrap the post GUI handler
				CompatUtil.wrapSpecificHandler("PCRenderOverlaysPost", PCRenderOverlaysPost::new, "com.tmtravlr.potioncore.PotionCoreEventHandlerClient", "renderOverlaysPost");
			}
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup PotionCoreHandlerClient!", e);
			ErrorUtil.logSilent("PotionCore Client Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}

	@SideOnly(Side.CLIENT)
	public class PCRenderOverlaysPost
	{
		private IEventListener handler;
		public PCRenderOverlaysPost(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void renderOverlaysPost(final RenderGameOverlayEvent.Post event)
		{
			handler.invoke(event); //Clean!
			GlStateManager.color(1.0f, 1.0f, 1.0f);
		}
	}
}
