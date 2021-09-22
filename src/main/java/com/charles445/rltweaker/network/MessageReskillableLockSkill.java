package com.charles445.rltweaker.network;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.util.ErrorUtil;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageReskillableLockSkill implements IMessage
{
	//Skill can be skipped, I guess, but to avoid any inconsistencies this is doing it the hard way
	
	public ResourceLocation skill;
	public ResourceLocation unlockable;
	
	public MessageReskillableLockSkill()
	{
		setPacketInvalid();
	}
	
	public MessageReskillableLockSkill(ResourceLocation skill, ResourceLocation unlockable)
	{
		this.skill = skill;
		this.unlockable = unlockable;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		if(buf == null)
		{
			setPacketInvalid();
			return;
		}
		
		try
		{
			this.skill = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
			this.unlockable = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
		}
		catch(IndexOutOfBoundsException e)
		{
			setPacketInvalid();
			return;
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, skill.toString());
		ByteBufUtils.writeUTF8String(buf, unlockable.toString());
	}
	
	private void setPacketInvalid()
	{
		this.skill = new ResourceLocation("invalid","invalid");
		this.unlockable = new ResourceLocation("invalid","invalid");
	}
	
	public static class Handler implements IMessageHandler<MessageReskillableLockSkill, IMessage>
	{
		@Override
		public IMessage onMessage(MessageReskillableLockSkill message, MessageContext ctx) 
		{
			return ServerMessageHandler.executeMessage(message, ctx);
		}
	}
	
}
