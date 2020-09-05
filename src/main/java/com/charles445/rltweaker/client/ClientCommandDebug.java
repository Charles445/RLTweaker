package com.charles445.rltweaker.client;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Queue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
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
		checkParticles(server, sender, args);
	}
	
	//Particle Debug...
	
	private Field f_queue;
	
	private void checkParticles(MinecraftServer server, ICommandSender sender, String[] args)
	{
		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		
		if(effectRenderer == null)
		{
			inform("ParticleManager is null", sender);
			return;
		}
		
		inform("P: "+effectRenderer.getStatistics(), sender);
		
		try
		{
			if(f_queue == null)
			{
				f_queue = ParticleManager.class.getDeclaredField("field_187241_h");
				System.out.println("ParticleManager queue: "+Modifier.toString(f_queue.getModifiers()));
				f_queue.setAccessible(true);
			}
			
			
			if(f_queue != null)
			{
				Queue<Particle> particleQueue = (Queue<Particle>) f_queue.get(effectRenderer);
				if(particleQueue!=null)
				{
					inform("Q: "+particleQueue.size()+" - "+particleQueue.isEmpty(), sender);
					
					int nullCounter = 0;
					
					Particle[] particleQueueCopy = particleQueue.toArray(new Particle[0]);
					
					for(Particle particle : particleQueueCopy)
					{
						if(particle==null)
						{
							nullCounter++;
						}
					}
					
					inform("QN: "+nullCounter, sender);
				}
			}
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
		{
			
		}
			
	}
}
