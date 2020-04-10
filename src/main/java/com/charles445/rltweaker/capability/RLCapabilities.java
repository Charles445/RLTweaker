package com.charles445.rltweaker.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class RLCapabilities
{
	@CapabilityInject(ITweakerCapability.class)
	public static final Capability<ITweakerCapability> TWEAKER = null;
	
	public static final String TWEAKER_IDENTIFIER = "tweaker";
	
	public static ITweakerCapability getTweakerData(EntityPlayer player)
	{
		return player.getCapability(TWEAKER, null);
	}
}
