package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.capability.ITweakerCapability;
import com.charles445.rltweaker.capability.RLCapabilities;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.TANReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class TANHandler
{
	//Registers on INIT, after TAN
	
	private TANReflect reflector;
	
	public TANHandler()
	{
		try
		{
			reflector = new TANReflect();
			
			MinecraftForge.EVENT_BUS.register(this);
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup TANHandler!", e);
			ErrorUtil.logSilent("TAN Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		EntityPlayer player = event.player;
		World world = player.getEntityWorld();
		
		if(!world.isRemote)
		{
			try
			{
				Object data = reflector.getThirstData(player);
				float currentExhaustion = reflector.readExhaustionFromData(data);
				
				ITweakerCapability tweakData = RLCapabilities.getTweakerData(player);
				
				if(ModConfig.server.toughasnails.fixTeleportThirst)
				{
					float storedExhaustion = tweakData.getTANExhaustion();
					float exhaustionChange = currentExhaustion - storedExhaustion;
					
					if(event.phase == TickEvent.Phase.START)
					{
						//The exhaustion change will have the difference between the current and the stored exhaustion
						//if it is greater than the threshold, set it badk to the old value
						if(exhaustionChange > (float)ModConfig.server.toughasnails.teleportThirstThreshold)
						{
							//DebugUtil.messageAll("Caught exhaustion "+currentExhaustion+", setting to "+storedExhaustion);
							reflector.setExhaustionInData(data, storedExhaustion);
						}
					}
					else
					{
						//Phase END
						
						//Update the tweaker capability with the new value
						tweakData.setTANExhaustion(currentExhaustion);
					}
				}
				
				if(event.phase == TickEvent.Phase.START && ModConfig.server.toughasnails.sendExtraThirstPackets)
				{
					int tanPackets = tweakData.getThirstPacketTicks();
					
					if(tanPackets >= ModConfig.server.toughasnails.extraThirstPacketFrequency)
					{
						tweakData.setThirstPacketTicks(0);
						SimpleNetworkWrapper packetHandler = reflector.getPacketHandlerInstance();
						if(packetHandler!=null && player instanceof EntityPlayerMP)
						{
							//Packet handler exists and player is the correct class
							packetHandler.sendTo(reflector.createUpdateMessageWithStat(data), (EntityPlayerMP) player);
						}
						
					}
					else
					{
						tweakData.incrementThirstPacketTicks();
					}
				}
				
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				RLTweaker.logger.error("Error in onPlayerTick TANHandler Invoke", e);
				ErrorUtil.logSilent("TANHandler onPlayerTick");
				throw new RuntimeException(e);
			}
		}
	}
}
