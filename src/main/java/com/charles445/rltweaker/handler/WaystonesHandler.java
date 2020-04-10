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
	
	private String combine(String[] split)
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<split.length;i++)
		{
			sb.append(split);
			if(i < split.length - 1)
				sb.append(" ");
		}
		return sb.toString();
	}
	
	private String cleanBiome(String s, int dimension, BlockPos pos)
	{
		try
		{
			//Check if it's a village waystone
			World world = DimensionManager.getWorld(dimension);
			Block block = world.getBlockState(pos.down()).getBlock();
			//Village waystones spawn on glowstone
			if(block!=Blocks.GLOWSTONE)
				return s;
			
			
			
			//TODO see how long this takes to process
			//Looping through a string split might be faster, but it's unclear
			//This is PROBABLY fast enough
			
			String result = s;
			result = result.replace(" Taiga", "");
			result = result.replace(" Plains", "");
			result = result.replace(" Island", "");
			result = result.replace(" River", "");
			result = result.replace(" Beach", "");
			result = result.replace(" Forest", "");
			result = result.replace(" Ocean", "");
			result = result.replace(" Desert", "");
			result = result.replace(" Hills", "");
			result = result.replace(" Swamps", "");
			result = result.replace(" Savanna", "");
			result = result.replace(" Plateau", "");
			result = result.replace(" Icelands", "");
			result = result.replace(" Jungle", "");
			result = result.replace(" Mesa", "");
			result = result.replace(" Void", "");
			result = result.replace(" Skies", "");
			
			//Now check the result in the current map
			Set<String> usedNames = reflector.getUsedNames(dimension);
			
			if(usedNames.contains(result))
			{
				//Figure out what its new name is
				String[] split = result.split(" ");
				
				if(split.length==1)
				{
					//First of its kind, add I and call it a day
					return result + " I";
				}
	
				int roman = 1;
				int splitID = split.length-1;
				
				while(usedNames.contains(result))
				{
					split[splitID] = reflector.toRoman(roman);
					roman++;
					result = combine(split);
				}

				//RLTweaker.logger.debug("Converting Waystone "+s+" -> "+result+" at "+pos.toString());
				return result;
			}
			else
			{
				//RLTweaker.logger.debug("Converting Waystone "+s+" -> "+result+" at "+pos.toString());
				return result;
			}
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to intercept waystone generation! Defaulting to uncleaned name for safety.", e);
			return s;
		}
	}
	
}
