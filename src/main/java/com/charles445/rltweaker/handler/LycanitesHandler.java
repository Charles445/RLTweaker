package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LycanitesHandler
{
	private static final String RLTWEAKER_LYCANITES_BOSS = RLTweaker.MODID + ".lycanitesboss";
	private static final String RLTWEAKER_LYCANITES_BOSS_RANGE = RLTweaker.MODID + ".lycanitesbossrange";
	
	//Reflectors for rltweaker.lycanitesboss ForgeData handling
	boolean forgeDataReflectionFailed = false;
	@Nullable
	Class c_ExtendedWorld = null;
	@Nullable
	Method m_ExtendedWorld_getForWorld = null;
	@Nullable
	Method m_ExtendedWorld_bossUpdate = null;
	@Nullable
	Method m_ExtendedWorld_overrideBossRange = null;
	
	public LycanitesHandler()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public static void setLycanitesBoss(Entity entity, boolean isBoss)
	{
		if(entity == null)
			return;
		NBTTagCompound data = entity.getEntityData();
		if(data == null)
			return;
		data.setBoolean(RLTWEAKER_LYCANITES_BOSS, isBoss);
	}
	
	public static void setLycanitesBossRange(Entity entity, int range)
	{
		if(entity == null)
			return;
		NBTTagCompound data = entity.getEntityData();
		if(data == null)
			return;
		data.setInteger(RLTWEAKER_LYCANITES_BOSS_RANGE, range);
	}
	
	public static boolean getLycanitesBoss(Entity entity)
	{
		if(entity == null)
			return false;
		NBTTagCompound data = entity.getEntityData();
		if(data == null)
			return false;
		return data.getBoolean(RLTWEAKER_LYCANITES_BOSS);
	}
	
	public static int getLycanitesBossRange(Entity entity)
	{
		if(entity == null)
			return 0;
		NBTTagCompound data = entity.getEntityData();
		if(data == null)
			return 0;
		return data.getInteger(RLTWEAKER_LYCANITES_BOSS_RANGE);
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if(event.getWorld().isRemote)
			return;
		
		if(forgeDataReflectionFailed)
			return;
		
		if(ModConfig.server.lycanitesmobs.enableEntityDataLycanitesBoss)
		{
			if(!getLycanitesBoss(event.getEntity()))
				return;
			
			//Reflect if not available
			if(c_ExtendedWorld == null || m_ExtendedWorld_getForWorld == null || m_ExtendedWorld_bossUpdate == null || m_ExtendedWorld_overrideBossRange == null)
			{
				try
				{
					c_ExtendedWorld = Class.forName("com.lycanitesmobs.ExtendedWorld");
					m_ExtendedWorld_getForWorld = ReflectUtil.findMethod(c_ExtendedWorld, "getForWorld");
					m_ExtendedWorld_bossUpdate = ReflectUtil.findMethod(c_ExtendedWorld, "bossUpdate");
					m_ExtendedWorld_overrideBossRange = ReflectUtil.findMethod(c_ExtendedWorld, "overrideBossRange");
				}
				catch(Exception e)
				{
					forgeDataReflectionFailed = true;
					RLTweaker.logger.error("Failed to reflect for Lycanites Boss Entity Data handling");
					ErrorUtil.logSilent("LycanitesMobs Boss EntityData Reflection");
					return;
				}
			}
			
			//It's available, attempt invocation
			try
			{
				Object o_exWorld = m_ExtendedWorld_getForWorld.invoke(null, event.getWorld());

				if(o_exWorld != null)
				{
					//Set entity as boss
					m_ExtendedWorld_bossUpdate.invoke(o_exWorld, event.getEntity());
				
					int bossRange = getLycanitesBossRange(event.getEntity());
					if(bossRange != 0)
						m_ExtendedWorld_overrideBossRange.invoke(o_exWorld, event.getEntity(), bossRange);
				}
			
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				ErrorUtil.logSilent("LycanitesMobs Boss Update Invocation");
			}
		}
	}
}
