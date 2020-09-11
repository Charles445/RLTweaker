package com.charles445.rltweaker.hook;

import java.util.List;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.WorldRadiusUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class HookWorld
{
	//HOOK
	public static List<Entity> getEntitiesWithinAABBExcludingEntity(World world, @Nullable Entity entity, AxisAlignedBB bb)
	{
		if(entity instanceof EntityItem)
		{
			return WorldRadiusUtil.instance.getEntitiesWithinAABBExcludingEntity(world, entity, bb, 2.0d);
		}
		return world.getEntitiesWithinAABBExcludingEntity(entity, bb);
	}
}
