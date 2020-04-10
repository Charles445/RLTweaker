package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.capability.RLCapabilities;
import com.charles445.rltweaker.capability.TweakerProvider;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.network.MessageUpdateEntityMovement;
import com.charles445.rltweaker.network.PacketHandler;
import com.charles445.rltweaker.network.TaskScheduler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MinecraftHandler
{
	public MinecraftHandler()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	//Capabilities
	
	@SubscribeEvent
	public void onAttachCapability(AttachCapabilitiesEvent event)
	{
		//Find player
		if(event.getObject() instanceof EntityPlayer)
		{
			//Attach capabilities
			
			//Attach tweaker
			event.addCapability(new ResourceLocation(RLTweaker.MODID, RLCapabilities.TWEAKER_IDENTIFIER), new TweakerProvider(RLCapabilities.TWEAKER));
			
		}
	}
	
	//TaskScheduler processing
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientScheduled(TickEvent.ClientTickEvent event)
	{
		TaskScheduler.processClientTick(event);
	}
	
	//Task Creation
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinWorldLowest(EntityJoinWorldEvent event)
	{
		if(event.getEntity() instanceof EntityArrow)
		{
			handleArrowJoinWorld((EntityArrow)event.getEntity());
		}
	}
	
	private void handleArrowJoinWorld(EntityArrow arrow)
	{
		World world = arrow.getEntityWorld();
		if(!world.isRemote && ModConfig.server.minecraft.playerArrowSync && arrow.shootingEntity instanceof EntityPlayer)
		{
			MessageUpdateEntityMovement message = new MessageUpdateEntityMovement(arrow);
			PacketHandler.instance.sendToAllAround(message, new TargetPoint(world.provider.getDimension(), arrow.posX, arrow.posY, arrow.posZ, 24));
		}
	}
}
