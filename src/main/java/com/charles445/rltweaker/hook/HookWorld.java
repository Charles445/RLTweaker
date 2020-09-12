package com.charles445.rltweaker.hook;

import java.util.List;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CollisionUtil;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.WorldRadiusUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class HookWorld
{
	public static boolean warnItemHook = false;
	
	//HOOK into World.getCollisionBoxes
	public static List<Entity> getEntitiesWithinAABBExcludingEntity(World world, @Nullable Entity entity, AxisAlignedBB bb)
	{
		if(!ModConfig.server.minecraft.lessCollisions)
			return world.getEntitiesWithinAABBExcludingEntity(entity, bb);
		
		if(entity != null)
		{
			try
			{
				return WorldRadiusUtil.instance.getEntitiesWithinAABBExcludingEntity(world, entity, bb, CollisionUtil.instance.getRadiusForEntity(entity));
			}
			catch(Exception e)
			{
				if(!warnItemHook)
				{
					warnItemHook = true;
					RLTweaker.logger.error("Error running CollisionUtil!",e);
					ErrorUtil.logSilent("CollisionUtil Critical Failure");
				}
			}
					
		}
		
		return world.getEntitiesWithinAABBExcludingEntity(entity, bb);
	}
	
	//UNUSED
	//HOOK
	//com/charles445/rltweaker/hook/HookWorld
	//getAABExcludingSizeFor
	//(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;)D
	public static double getAABExcludingSizeFor(World world, @Nullable Entity entity)
	{
		if(entity instanceof EntityItem)
		{
			//System.out.println("WWW");
			return 2.0d;
		}
		else
			return world.MAX_ENTITY_RADIUS;
	}
}
