package com.charles445.rltweaker.capability;

import javax.vecmath.Vector3d;

public class TweakerCapability implements ITweakerCapability
{
	//Temporary
	private float tanExhaustion = 0.0f;
	private int thirstPacketTicks = 0;

	@Override
	public void setTANExhaustion(float exhaustion)
	{
		this.tanExhaustion = exhaustion;
	}

	@Override
	public float getTANExhaustion()
	{
		return this.tanExhaustion;
	}
	
	@Override
	public void setThirstPacketTicks(int ticks)
	{
		this.thirstPacketTicks = ticks;
	}
	
	@Override
	public void incrementThirstPacketTicks()
	{
		this.thirstPacketTicks++;
	}
	
	@Override
	public int getThirstPacketTicks()
	{
		return this.thirstPacketTicks;
	}
	
}
