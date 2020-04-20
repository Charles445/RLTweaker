package com.charles445.rltweaker.network;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.util.ReflectUtil;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageUpdateDismountStatus implements IMessage
{
	public MessageUpdateDismountStatus()
	{
		
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		
	}
	
	public static class Handler implements IMessageHandler<MessageUpdateDismountStatus, IMessage>
	{

		public static Field f_ridingEntity;
		public static Method m_removePassenger;
		
		static
		{
			try
			{
				f_ridingEntity = ReflectUtil.findFieldAny(Entity.class, "field_184239_as", "ridingEntity");
				m_removePassenger = ReflectUtil.findMethodAny(Entity.class, "func_184225_p", "removePassenger", Entity.class);
			}
			catch(Exception e)
			{
				RLTweaker.logger.error("Couldn't setup ridingEntity clientside");
			}
		}
		
		@Override
		public IMessage onMessage(MessageUpdateDismountStatus message, MessageContext ctx) 
		{
			if(ctx.side == Side.CLIENT)
			{
				Minecraft.getMinecraft().addScheduledTask(() -> 
				{
					MessageUpdateDismountStatus.Handler.execute();
				});
			}
			return null;
		}
		
		@SideOnly(Side.CLIENT)
		public static void execute()
		{
			EntityPlayer player = Minecraft.getMinecraft().player;
			
			//Try the clientside dismount
			player.dismountRidingEntity();
			
			if(f_ridingEntity==null || m_removePassenger==null)
				return;
			
			if (player.getRidingEntity() != null)
	        {
	            Entity entity = player.getRidingEntity();
	            try
	            {
	            	//player.sendMessage(new TextComponentString("Desync fix"));
	            	f_ridingEntity.set(player, null);
	            	m_removePassenger.invoke(entity, player);
	            }
	            catch(Exception e)
	            {
	            	//Ok
	            }
	        }
		}
		
	}

}
