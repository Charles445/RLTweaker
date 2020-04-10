package com.charles445.rltweaker.capability;

import javax.vecmath.Vector3d;

public class TweakerCapability implements ITweakerCapability
{
	//Temporary
	private float tanExhaustion = 0.0f;

	@Override
	public void setTANExhaustion(float exhaustion)
	{
		this.tanExhaustion = exhaustion;
	}

	@Override
	public float getTANExhaustion()
	{
		return tanExhaustion;
	}
	
}
