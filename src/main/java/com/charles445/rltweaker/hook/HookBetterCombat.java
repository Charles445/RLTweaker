package com.charles445.rltweaker.hook;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeHooks;

public class HookBetterCombat
{
	public static boolean strictCollisionCheck(Entity entity, Entity rvEntity)
	{
		return(entity.canBeCollidedWith() && (entity != rvEntity.getRidingEntity()));
	}
	
	public static boolean hookCriticalHit(EntityPlayer player, Entity target, boolean vanillaCrit)
	{
		return ForgeHooks.getCriticalHit(player, target, vanillaCrit, vanillaCrit ? 1.5F : 1.0F) != null;
	}
}
