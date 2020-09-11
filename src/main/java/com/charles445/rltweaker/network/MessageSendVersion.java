package com.charles445.rltweaker.network;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.VersionDelimiter;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSendVersion implements IMessage
{
	private int major;
	private int minor;
	private int patch;
	
	public MessageSendVersion()
	{
		this.major = 0;
		this.minor = 4;
		this.patch = 0;
	}
	
	public MessageSendVersion(VersionDelimiter vd)
	{
		this.major = vd.major;
		this.minor = vd.minor;
		this.patch = vd.patch;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.major = buf.readInt();
		this.minor = buf.readInt();
		this.patch = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.major);
		buf.writeInt(this.minor);
		buf.writeInt(this.patch);
	}
	
	public static class Handler implements IMessageHandler<MessageSendVersion, IMessage>
	{
		@Override
		public IMessage onMessage(MessageSendVersion message, MessageContext ctx) 
		{
			if(ctx.side == Side.SERVER)
			{
				RLTweaker.logger.info("Received version message: "+message.major+"."+message.minor+"."+message.patch);
				
				if(ctx.netHandler instanceof NetHandlerPlayServer)
				{
					NetHandlerPlayServer netHandler = (NetHandlerPlayServer)ctx.netHandler;
					if(netHandler.player!=null)
					{
						//RLTweaker.logger.info("Registering with UUID: "+netHandler.player.getGameProfile().getId());
						//NetworkHandler.helloQueue.add(netHandler.player.getGameProfile().getId());
						NetworkHandler.addClient(netHandler.player.getGameProfile().getId(), new VersionDelimiter(message.major, message.minor, message.patch));
					}
					else
					{
						RLTweaker.logger.error("NetHandlerPlayServer had null player...");
						ErrorUtil.logSilent("NetHandlerPlayServer NULL PLAYER");
					}
				}
			}
			
			return null;
		}
	}
	
	
}
