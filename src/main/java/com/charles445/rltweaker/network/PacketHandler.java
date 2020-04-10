package com.charles445.rltweaker.network;

import com.charles445.rltweaker.RLTweaker;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
	public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(RLTweaker.MODID);
	
	public static void init()
	{
		instance.registerMessage(MessageUpdateEntityMovement.Handler.class, MessageUpdateEntityMovement.class, 0, Side.CLIENT);
		/*
		instance.registerMessage(MessageUpdateThirst.Handler.class, MessageUpdateThirst.class, 0, Side.CLIENT);
		instance.registerMessage(MessageDrinkWater.Handler.class, MessageDrinkWater.class, 1, Side.SERVER);
		instance.registerMessage(MessageConfigLAN.Handler.class, MessageConfigLAN.class, 2, Side.SERVER);
		instance.registerMessage(MessageUpdateConfig.Handler.class, MessageUpdateConfig.class, 3, Side.CLIENT);
		instance.registerMessage(MessageUpdateTemperature.Handler.class, MessageUpdateTemperature.class, 4, Side.CLIENT);
		*/
	}
}
