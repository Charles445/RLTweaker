package com.charles445.rltweaker.proxy;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.client.ClientCommandDebug;
import com.charles445.rltweaker.client.FixedHatLayer;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.handler.MantleHandlerClient;
import com.charles445.rltweaker.handler.PotionCoreHandlerClient;
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
		
		ClientCommandHandler.instance.registerCommand(new ClientCommandDebug());
		
		if(Loader.isModLoaded(ModNames.POTIONCORE) && ModConfig.client.potioncore.enabled)
		{
			RLTweaker.clientHandlers.put(ModNames.POTIONCORE, new PotionCoreHandlerClient());
		}
		
		if(Loader.isModLoaded(ModNames.CLASSYHATS) && ModConfig.client.classyhats.enabled)
		{
			if(ModConfig.client.classyhats.mobendsCompatibility)
				FixedHatLayer.init();
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
