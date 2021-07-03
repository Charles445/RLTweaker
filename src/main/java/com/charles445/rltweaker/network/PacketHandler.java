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
		//0.2.0
		instance.registerMessage(MessageUpdateEntityMovement.Handler.class, MessageUpdateEntityMovement.class, 0, Side.CLIENT);
		
		//0.3.0
		instance.registerMessage(MessageUpdateDismountStatus.Handler.class, MessageUpdateDismountStatus.class, 1, Side.CLIENT);
		
		//0.4.0
		instance.registerMessage(MessageUpdateAttackYaw.Handler.class, MessageUpdateAttackYaw.class, 2, Side.CLIENT);
		instance.registerMessage(MessageSendVersion.Handler.class, MessageSendVersion.class, 3, Side.SERVER);
	}
}
