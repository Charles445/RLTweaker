package com.charles445.rltweaker.hook;

import java.lang.reflect.Field;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class HookForge
{
	//com/charles445/rltweaker/hook/HookForge
	//receiveMessage
	//(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;Lnet/minecraftforge/fml/common/network/simpleimpl/MessageContext;)V
	public static void receiveMessage(IMessage message, MessageContext ctx) throws Exception
	{
		if(ctx.side == Side.SERVER)
		{
			
		}
	}
}
