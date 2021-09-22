package com.charles445.rltweaker.command;

import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.ModNames;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class CommandAdvisor extends CommandBase
{
	@Override
	public String getName()
	{
		return "rladvisor";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/rladvisor";
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		//Advise the user on what to change in the config
		World world = sender.getEntityWorld();
		
		inform("RLTweaker Advisor", sender);
		
		informPatches(server, sender, world);
		
	}
	
	public void informPatches(MinecraftServer server, ICommandSender sender, World world)
	{	
		//Patches
		boolean bigRadius = world.MAX_ENTITY_RADIUS > 2.0d;
		
		if(!ModConfig.patches.particleThreading)
		{
			inform("Safety patch, set patches / particleThreading to true", sender);
		}
		
		if(ModConfig.patches.lessCollisions && !bigRadius)
		{
			inform("Unnecessary patch, set patches / lessCollisions to false", sender);
		}
		else if(!ModConfig.patches.lessCollisions && bigRadius)
		{
			inform("Performance patch, set patches / lessCollisions to true", sender);
		}
		
		if(!ModConfig.patches.betterCombatMountFix && Loader.isModLoaded(ModNames.BETTERCOMBAT))
		{
			inform("Improvement patch, set patches / betterCombatMountFix to true", sender);
		}
		
		if(!ModConfig.patches.realBenchDupeBugFix)
		{
			try
			{
				Class.forName("pw.prok.realbench.WorkbenchTile");
				inform("Improvement patch, set patches / realBenchDupeBugFix to true", sender);
			}
			catch(Exception e)
			{
				//Nothing
			}
		}
		
		if(!Loader.isModLoaded(ModNames.ICEANDFIRE))
		{
			if(!ModConfig.patches.iafFixMyrmexQueenHiveSpam)
				inform("Improvement patch, set patches / iafFixMyrmexQueenHiveSpam to true", sender);
		}
		
		//lycanitesPetDupeFix //temporary
		
		if(!ModConfig.patches.doorPathfindingFix)
		{
			inform("Improvement patch, set patches / doorPathfindingFix to true", sender);
		}
		
		if(ModConfig.patches.reducedSearchSize && !bigRadius)
		{
			inform("Unnecessary patch, set patches / reducedSearchSize to false", sender);
		}
		else if(!ModConfig.patches.reducedSearchSize && bigRadius)
		{
			inform("Performance patch, set patches / reducedSearchSize to true", sender);
		}
		
		if(!ModConfig.patches.patchBroadcastSounds)
		{
			inform("Optional patch, set patches / patchBroadcastSounds to true", sender);
		}
		
		if(ModConfig.patches.patchEnchantments && ModConfig.server.minecraft.blacklistedEnchantments.length == 0)
		{
			inform("Unnecessary patch, set patches / patchEnchantments to false", sender);
		}
		else if(!ModConfig.patches.patchEnchantments && ModConfig.server.minecraft.blacklistedEnchantments.length > 0)
		{
			inform("Necessary patch for blacklisting enchantments, set patches / patchEnchantments to true", sender);
		}
		
		if(!ModConfig.patches.ENABLED)
		{
			inform("All patches disabled, set patches / ENABLED to true", sender);
		}
		
		//No advisory to set ENABLED to false
		
	}
	
	public void inform(String s, ICommandSender sender)
	{
		sender.sendMessage(new TextComponentString(s));
	}
}
