package com.charles445.rltweaker.capability;

import javax.vecmath.Vector3d;

public interface ITweakerCapability
{
	public void setTANExhaustion(float exhaustion);
	public float getTANExhaustion();
	
	public void setThirstPacketTicks(int ticks);
	public void incrementThirstPacketTicks();
	public int getThirstPacketTicks();
}
