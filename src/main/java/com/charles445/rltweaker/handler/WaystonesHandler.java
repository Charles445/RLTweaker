package com.charles445.rltweaker.handler;

import java.util.Set;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.WaystoneReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WaystonesHandler
{
	WaystoneReflect reflector;
	
	public WaystonesHandler()
	{
		try
		{
			this.reflector = new WaystoneReflect();
			
			//Register
			CompatUtil.subscribeEventManually(reflector.c_GenerateWaystoneNameEvent, this, ReflectUtil.findMethod(this.getClass(), "onGenerateWaystone"));
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup WaystonesHandler!", e);
			ErrorUtil.logSilent("Waystones Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	@SubscribeEvent
	public void onGenerateWaystone(Event event)
	{
		if(!ModConfig.server.waystones.removeWaystoneBiome)
			return;
		
		try
		{
			//Get the current waystone name
			String waystoneName = reflector.getWaystoneName(event);
			int dimension = reflector.getDimension(event);
			BlockPos waystonePos = reflector.getPos(event);
			
			//Remove the biome if possible
			waystoneName = cleanBiome(waystoneName, dimension, waystonePos);
			
			//Set the waystone name to the new one
			reflector.setWaystoneName(event,waystoneName);
			
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to intercept waystone generation!", e);
		}
	}
	
	private String cleanBiome(String s, int dimension, BlockPos pos)
	{
		try
		{
			//Check if it's a village waystone
			World world = DimensionManager.getWorld(dimension);
			
			//Make sure this is only on server
			if(world == null || world.isRemote)
			{
				return s;
			}
			
			Block block = world.getBlockState(pos.down()).getBlock();
			//Village waystones spawn on glowstone
			if(block!=Blocks.GLOWSTONE)
				return s;
			
			//Get rid of the old name we do not need it
			
			//TODO allow custom names
			String baseName = reflector.getRandomName(dimension);
			
			String result = ""+baseName;
			
			Set<String> usedNames = reflector.getUsedNames(dimension);
			
			int roman = 1;
			
			while(usedNames.contains(result))
			{
				result = baseName + " " + reflector.toRoman(roman);
				roman++;
			}
			
			return result;
			
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to intercept waystone generation! Defaulting to uncleaned name for safety.", e);
			return s;
		}
	}
}
