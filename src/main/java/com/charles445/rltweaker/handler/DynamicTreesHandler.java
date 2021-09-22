package com.charles445.rltweaker.handler;

import java.lang.reflect.Field;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.debug.DebugUtil;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class DynamicTreesHandler
{
	public DynamicTreesHandler()
	{
		if(Loader.isModLoaded("snowrealmagic"))
		{
			try
			{
				Class c_modblocks = Class.forName("com.ferreusveritas.dynamictrees.ModBlocks");
				
				Field f_modblocks_snow = ReflectUtil.findField(c_modblocks, "blockLeavesSnow");
				
				Class c_snow = Class.forName("snownee.snow.SnowRealMagic");
				Field f_snow_snownee = ReflectUtil.findField(c_snow, "BLOCK");
				
				f_modblocks_snow.set(null, f_snow_snownee.get(null));
				
				RLTweaker.logger.info("Applied change to snow block, now resetting states...");
				
				Field f_modblocks_blockStates = ReflectUtil.findField(c_modblocks, "blockStates");
				Class c_CommonBlockStates = Class.forName("com.ferreusveritas.dynamictrees.ModBlocks$CommonBlockStates");
				
				Object newCommonBlockStates = c_CommonBlockStates.getConstructor().newInstance();
				
				f_modblocks_blockStates.set(null, newCommonBlockStates);
				RLTweaker.logger.info("Applied change to states");
				
			}
			catch(Exception e)
			{
				RLTweaker.logger.info("Did not apply change to snow");
				e.printStackTrace();
			}
		}
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	/*
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if(event.phase == Phase.START)
			return;
		
		if(event.world.isRemote)
			return;
		
		for(Entity entity : event.world.loadedEntityList)
		{
			if(entity.getClass().getName().equals("com.ferreusveritas.dynamictrees.entities.EntityFallingTree"))
			{
				DebugUtil.messageAll(entity.getEntityBoundingBox().toString());
			}
		}
		
	}
	*/
}
