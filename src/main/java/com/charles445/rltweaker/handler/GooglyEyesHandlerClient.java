package com.charles445.rltweaker.handler;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class GooglyEyesHandlerClient
{
	private boolean runOnce = false;
	
	public GooglyEyesHandlerClient()
	{
		try
		{
			CompatUtil.subscribeEventManually(Class.forName("me.ichun.mods.ichunutil.client.core.event.RendererSafeCompatibilityEvent"), this, ReflectUtil.findMethod(this.getClass(), "onRenderSafe"));
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup GooglyEyesHandlerClient!", e);
			ErrorUtil.logSilent("Googly Eyes Client Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	@SubscribeEvent
	public void onRenderSafe(Object event)
	{
		if(!runOnce)
		{
			runOnce = true;
			
			Field f_RenderLivingBase_layerRenderers = null;
			
			try
			{
				f_RenderLivingBase_layerRenderers = ReflectUtil.findFieldAny(RenderLivingBase.class, "field_177097_h", "layerRenderers");
			}
			catch(Exception e)
			{
				ErrorUtil.logSilent("Googly Eyes RenderLivingBase Vanilla Invocation");
				return;
			}
			
			List<String> blacklist = Arrays.asList(ModConfig.client.googlyeyes.entityBlacklist);
			
			//Scour through the blacklist
			for (Map.Entry<Class<? extends Entity>, Render<? extends Entity>> entry : Minecraft.getMinecraft().getRenderManager().entityRenderMap.entrySet())
			{
				EntityEntry entEntry = EntityRegistry.getEntry(entry.getKey());
				
				if (entEntry != null && entry.getValue() instanceof RenderLivingBase && blacklist.contains(entEntry.getRegistryName().toString()))
				{
					RenderLivingBase renderer = (RenderLivingBase)entry.getValue();
					try
					{
						List<LayerRenderer<EntityLivingBase>> layerRenderers = (List<LayerRenderer<EntityLivingBase>>)f_RenderLivingBase_layerRenderers.get(renderer);
						
						Iterator<LayerRenderer<EntityLivingBase>> it = layerRenderers.iterator();
						
						while(it.hasNext())
						{
							LayerRenderer<EntityLivingBase> layer = it.next();
							if(layer.toString().contains("me.ichun.mods.googlyeyes.common.layerrenderer.LayerGooglyEyes"))
							{
								it.remove();
								RLTweaker.logger.info("Removed googly eyes layer for entity: "+entEntry.getName());
							}
						}
					}
					catch(Exception e)
					{
						//Failed to remove old layers, unload
						RLTweaker.logger.error("Failed to manage Googly Eyes blacklist!",e);
						ErrorUtil.logSilent("Googly Eyes Blacklist Management");
						return;
					}
				}
			}
		}
	}
}
