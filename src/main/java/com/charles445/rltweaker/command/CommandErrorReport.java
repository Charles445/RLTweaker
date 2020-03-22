package com.charles445.rltweaker.command;

import java.util.Map;

import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandErrorReport extends CommandBase
{
	@Override
	public String getName()
	{
		return "rlerrorreport";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/rlerrorreport";
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		inform("RLTweaker Errors Count", sender);
		
		if(ErrorUtil.errorCount.size()==0)
		{
			inform("None!",sender);
			return;
		}
		
		for(Map.Entry<String, Integer> entry : ErrorUtil.errorCount.entrySet())
		{
			inform(entry.getKey()+" : "+entry.getValue(), sender);
		}
	}
	
	public void inform(String s, ICommandSender sender)
	{
		sender.sendMessage(new TextComponentString(s));
	}
}
