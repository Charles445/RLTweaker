package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.JsonConfig;
import com.charles445.rltweaker.config.json.JsonDoubleBlockState;
import com.charles445.rltweaker.debug.DebugUtil;
import com.charles445.rltweaker.network.IServerMessageReceiver;
import com.charles445.rltweaker.network.MessageReskillableLockSkill;
import com.charles445.rltweaker.network.ServerMessageHandler;
import com.charles445.rltweaker.reflect.ReskillableReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ReskillableHandler
{
	private ReskillableReflect reflector;
	
	public ReskillableHandler()
	{
		try
		{
			reflector = new ReskillableReflect();
			
			//FIXME config, again
			ServerMessageHandler.registerMessage(MessageReskillableLockSkill.class, new LockSkillReceiver());
			
			MinecraftForge.EVENT_BUS.register(this);
		}
	
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup ReskillableHandler!", e);
			ErrorUtil.logSilent("Reskillable Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	public void registerTransmutations()
	{
		if(JsonConfig.reskillableTransmutation!=null)
		{
			for(Map.Entry<String, List<JsonDoubleBlockState>> entry : JsonConfig.reskillableTransmutation.entrySet())
			{
				Item activator = ForgeRegistries.ITEMS.getValue(new ResourceLocation(entry.getKey()));
				if(activator==null)
				{
					RLTweaker.logger.warn("Skipping unregistered item in registerTransmutations: "+entry.getKey());
					continue;
				}
				
				for(JsonDoubleBlockState jdbs : entry.getValue())
				{
					try
					{
						reflector.addEntryToReagent(activator, jdbs.input.getAsBlockState(), jdbs.output.getAsBlockState());
					}
					catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
					{
						RLTweaker.logger.error("Invocation error in registerTransmutations", e);
						ErrorUtil.logSilent("Reskillable registerTransmutations Invoke Failure");
					}
				}
			}
		}
	}
	
	public class LockSkillReceiver implements IServerMessageReceiver
	{
		@Override
		public void receiveMessage(IMessage msgIn, EntityPlayer player) 
		{
			try
			{
				if(msgIn instanceof MessageReskillableLockSkill)
				{
					MessageReskillableLockSkill message = (MessageReskillableLockSkill)msgIn;
					
					IForgeRegistry<?> SKILLS = reflector.getSkillsRegistry();
					
					IForgeRegistry<?> UNLOCKABLES = reflector.getUnlockablesRegistry();
					
					if(!SKILLS.containsKey(message.skill))
						return;
					
					if(!UNLOCKABLES.containsKey(message.unlockable))
						return;
					
					Object skill = SKILLS.getValue(message.skill);
					Object unlockable = UNLOCKABLES.getValue(message.unlockable);
					Object playerData = reflector.getPlayerData(player);
					Object skillInfo = reflector.getSkillInfo(playerData, skill);
					boolean unlocked = reflector.isUnlocked(skillInfo, unlockable);
					
					if(unlocked)
					{
						//Original mod's cmd command has this swapped
						if(reflector.postLockUnlockableEventPre(player, unlockable))
							return;
						
						reflector.lockPlayerSkill(skillInfo, unlockable, player);
						reflector.saveAndSyncPlayerData(playerData);
						
						reflector.postLockUnlockableEventPost(player, unlockable);
					}
					else
					{
						ErrorUtil.logSilent("Reskillable LockSkillReceiver Unlock Asked Lock");
					}
				}
			}
			catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException e)
			{
				e.printStackTrace();
				ErrorUtil.logSilent("Reskillable LockSkillReceiver Invoke");
			}
		}
	}
}
