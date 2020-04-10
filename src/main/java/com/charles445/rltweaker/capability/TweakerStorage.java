package com.charles445.rltweaker.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class TweakerStorage implements IStorage<ITweakerCapability>
{

	//Storage
	
	@Override
	public NBTBase writeNBT(Capability<ITweakerCapability> capability, ITweakerCapability instance, EnumFacing side)
	{
		//Currently Empty
		
		NBTTagCompound compound = new NBTTagCompound();
		
		//Save
		
		return new NBTTagCompound();
	}

	@Override
	public void readNBT(Capability<ITweakerCapability> capability, ITweakerCapability instance, EnumFacing side, NBTBase nbt)
	{
		//Currently empty
		
		if(nbt instanceof NBTTagCompound)
		{
			NBTTagCompound compound = (NBTTagCompound)nbt;
			
			//Load to instance
		}
	}

}
