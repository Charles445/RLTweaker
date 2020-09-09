package com.charles445.rltweaker.command;

import com.charles445.rltweaker.config.ModConfig;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

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
		
		if(ModConfig.patches.lessCollisions && world.MAX_ENTITY_RADIUS <= 2.0d)
		{
			inform("Unnecessary patch, set patches / lessCollisions to false", sender);
		}
		else if(!ModConfig.patches.lessCollisions && world.MAX_ENTITY_RADIUS > 2.0d)
		{
			inform("Performance patch, set patches / lessCollisions to true", sender);
		}
		
		if(!ModConfig.patches.particleThreading)
		{
			inform("Safety patch, set patches / particleThreading to true", sender);
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
