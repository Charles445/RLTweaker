package com.charles445.rltweaker.client;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class ClientCommandDebug extends ClientCommandBase
{
	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public String getName()
	{
		return "rldebugclient";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/rldebugclient";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		
	}
}
