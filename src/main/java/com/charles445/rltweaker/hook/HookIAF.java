package com.charles445.rltweaker.hook;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.DamageSource;

public class HookIAF
{
	//com/charles445/rltweaker/hook/HookIAF
	//decrementToZero
	//(I)I
	public static int decrementToZero(int input)
	{
		if(input > 0)
			return input - 1;
		
		return input;
	}
	
	//com/charles445/rltweaker/hook/HookIAF
	//handleOldGorgon
	public static int handleOldGorgon(EntityMob gorgon, EntityLiving statue, int gorgonDelay, DamageSource gorgonSource)
	{
		int newGorgonDelay = gorgonDelay;
		gorgon.getAttackTarget().attackEntityFrom(gorgonSource, Integer.MAX_VALUE);
		if(!gorgon.getAttackTarget().isEntityAlive() && gorgonDelay == 0)
		{
			statue.prevRotationYaw = gorgon.getAttackTarget().rotationYaw;
			statue.rotationYaw = gorgon.getAttackTarget().rotationYaw;
			statue.rotationYawHead = gorgon.getAttackTarget().rotationYaw;
			statue.renderYawOffset = gorgon.getAttackTarget().rotationYaw;
			statue.prevRenderYawOffset = gorgon.getAttackTarget().rotationYaw;
			gorgon.world.spawnEntity(statue);
			newGorgonDelay = 40;
		}
		gorgon.setAttackTarget(null);
		
		return newGorgonDelay;
	}
}
