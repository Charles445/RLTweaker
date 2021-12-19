package com.charles445.rltweaker.proxy;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.client.ClientCommandDebug;
import com.charles445.rltweaker.client.gui.GuiDelegator;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.handler.ClassyHatsHandlerClient;
import com.charles445.rltweaker.handler.FBPHandlerClient;
import com.charles445.rltweaker.handler.GooglyEyesHandlerClient;
import com.charles445.rltweaker.handler.MantleHandlerClient;
import com.charles445.rltweaker.handler.PotionCoreHandlerClient;
import com.charles445.rltweaker.handler.ReskillableHandlerClient;
import com.charles445.rltweaker.util.ModNames;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Loader;

public class ClientProxy extends CommonProxy
{
	@Override
	public void postInit()
	{
		super.postInit();
		
		//GuiDelegator
		RLTweaker.clientHandlers.put("GuiDelegator", new GuiDelegator());
		
		ClientCommandHandler.instance.registerCommand(new ClientCommandDebug());
		
		if(Loader.isModLoaded(ModNames.POTIONCORE) && ModConfig.client.potioncore.enabled)
		{
			RLTweaker.clientHandlers.put(ModNames.POTIONCORE, new PotionCoreHandlerClient());
		}
		
		if(Loader.isModLoaded(ModNames.CLASSYHATS) && ModConfig.client.classyhats.enabled)
		{
			RLTweaker.clientHandlers.put(ModNames.CLASSYHATS, new ClassyHatsHandlerClient());
		}
		
		if(Loader.isModLoaded(ModNames.RESKILLABLE) && ModConfig.client.reskillable.enabled)
		{
			RLTweaker.clientHandlers.put(ModNames.RESKILLABLE, new ReskillableHandlerClient());
		}
	}
	
	@Override
	public void loadComplete()
	{
		super.loadComplete();
		
		if(Loader.isModLoaded(ModNames.MANTLE) && ModConfig.client.mantle.enabled)
		{
			RLTweaker.clientHandlers.put(ModNames.MANTLE, new MantleHandlerClient());
		}
		
		if(Loader.isModLoaded(ModNames.FANCYBLOCKPARTICLES) && ModConfig.client.fbp.enabled)
		{
			RLTweaker.clientHandlers.put(ModNames.FANCYBLOCKPARTICLES, new FBPHandlerClient());
		}
		
		if(Loader.isModLoaded(ModNames.GOOGLYEYES) && ModConfig.client.googlyeyes.enabled)
		{
			RLTweaker.clientHandlers.put(ModNames.GOOGLYEYES, new GooglyEyesHandlerClient());
		}
	}
	
	@Override
	public EntityPlayer getClientMinecraftPlayer()
	{
		return Minecraft.getMinecraft().player;
	}

	@Override
	public Boolean isClientConnectedToServer()
	{
		return Minecraft.getMinecraft().getConnection().getNetworkManager().isChannelOpen();
	}
}
