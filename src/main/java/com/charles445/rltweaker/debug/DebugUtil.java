package com.charles445.rltweaker.debug;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class DebugUtil
{
	public static void messageAll(String s)
	{
		for(EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
		{
			player.sendMessage(new TextComponentString(s));
		}
	}
	
	public static void loadCompleteDebugRoutine()
	{
		
	}
}
