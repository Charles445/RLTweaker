package com.charles445.rltweaker.hook;

import net.minecraft.entity.Entity;

public class HookBetterCombat
{
	public static boolean strictCollisionCheck(Entity entity, Entity rvEntity)
	{
		return(entity.canBeCollidedWith() && (entity != rvEntity.getRidingEntity()));
	}
}
