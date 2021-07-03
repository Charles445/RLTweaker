package com.charles445.rltweaker.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageUpdateEntityMovement implements IMessage
{
	//Side CLIENT
	
	//private UUID uuid;
	private int id;
	private double x;
	private double y;
	private double z;
	private double motionX;
	private double motionY;
	private double motionZ;
	
	public MessageUpdateEntityMovement()
	{
		
	}
	
	public MessageUpdateEntityMovement(Entity entity)
	{
		//this.uuid = entity.getUniqueID();
		this.id = entity.getEntityId();
		this.x = entity.posX;
		this.y = entity.posY;
		this.z = entity.posZ;
		this.motionX = entity.motionX;
		this.motionY = entity.motionY;
		this.motionZ = entity.motionZ;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		//long uuidMost = buf.readLong();
		//long uuidLeast = buf.readLong();
		this.id = buf.readInt();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.motionX = buf.readDouble();
		this.motionY = buf.readDouble();
		this.motionZ = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		//buf.writeLong(uuid.getMostSignificantBits());
		//buf.writeLong(uuid.getLeastSignificantBits());
		buf.writeInt(id);
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeDouble(motionX);
		buf.writeDouble(motionY);
		buf.writeDouble(motionZ);
	}
	
	public static class Handler implements IMessageHandler<MessageUpdateEntityMovement, IMessage>
	{
		@Override
		public IMessage onMessage(MessageUpdateEntityMovement message, MessageContext ctx) 
		{
			if(ctx.side == Side.CLIENT)
			{
				Minecraft.getMinecraft().addScheduledTask(() -> 
				{
					TaskScheduler.addClientTask(message, 0L);
				});
			}
			return null;
		}
		
		@SideOnly(Side.CLIENT)
		public static void fromMessage(MessageUpdateEntityMovement message)
		{
			World world = Minecraft.getMinecraft().world;
			if(world==null)
				return;
			
			EntityPlayer player = Minecraft.getMinecraft().player;
			if(player==null)
				return;
			
			Entity entity = world.getEntityByID(message.id);
			if(entity!=null)
			{
				//player.sendMessage(new TextComponentString("Received motion packet"));
				//player.sendMessage(new TextComponentString("From P "+entity.posX+" "+entity.posY+" "+entity.posZ));
				//player.sendMessage(new TextComponentString("From M "+entity.motionX+" "+entity.motionY+" "+entity.motionZ));
				
				entity.posX = message.x;
				entity.posY = message.y;
				entity.posZ = message.z;
				entity.motionX = message.motionX;
				entity.motionY = message.motionY;
				entity.motionZ = message.motionZ;
				
				//player.sendMessage(new TextComponentString("To P "+entity.posX+" "+entity.posY+" "+entity.posZ));
				//player.sendMessage(new TextComponentString("To M "+entity.motionX+" "+entity.motionY+" "+entity.motionZ));
				
			}
		}
	}
}
