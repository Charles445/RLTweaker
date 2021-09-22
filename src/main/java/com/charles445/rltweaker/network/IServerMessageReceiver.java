package com.charles445.rltweaker.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IServerMessageReceiver
{
	public void receiveMessage(IMessage message, EntityPlayer player);
}
