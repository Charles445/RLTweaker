package com.charles445.rltweaker.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class TweakerProvider implements ICapabilitySerializable<NBTBase>
{
	final Capability<ITweakerCapability> capability;
	final ITweakerCapability instance;
	
	public TweakerProvider(Capability<ITweakerCapability> newcapability)
	{
		this.capability = newcapability;
		this.instance = capability.getDefaultInstance();
	}
	
	@Override
	public boolean hasCapability(Capability<?> requestedcapability, EnumFacing facing)
	{
		return (requestedcapability==this.capability);
	}

	@Override
	public <T> T getCapability(Capability<T> requestedcapability, EnumFacing facing)
	{
		if(requestedcapability==this.capability)
		{
			return capability.cast(this.instance);
		}
		
		return null;
	}

	@Override
	public NBTBase serializeNBT()
	{
		return this.capability.writeNBT(this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt)
	{
		this.capability.readNBT(this.instance, null, nbt);
	}
}
