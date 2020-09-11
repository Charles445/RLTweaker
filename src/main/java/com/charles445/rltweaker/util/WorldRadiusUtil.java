package com.charles445.rltweaker.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class WorldRadiusUtil
{
	//All uses of MAX_ENTITY_RADIUS, with a variable size
	
	public static WorldRadiusUtil instance = new WorldRadiusUtil();
	
	private Method m_isChunkLoaded;
	
	private boolean errorgetEntitiesInAABBexcluding;
	private boolean errorgetEntitiesInAABB;
	
	private boolean working = false;
	
	public WorldRadiusUtil()
	{
		errorgetEntitiesInAABBexcluding = false;
		errorgetEntitiesInAABB = false;
		working = false;
		
		try
		{
			m_isChunkLoaded = ReflectUtil.findMethodAny(World.class, "func_175680_a", "isChunkLoaded", int.class, int.class, boolean.class);
			working = true;
			RLTweaker.logger.debug("WorldRadiusUtil initialized");
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("HookWorld failed, please consider sending this log to the RLTweaker dev!",e);
			ErrorUtil.logSilent("WorldRadiusUtil Instantiation Failure");
		}
	}
	
	public List<Entity> getEntitiesWithinAABBExcludingEntity(World world, @Nullable Entity entityIn, AxisAlignedBB bb, double size)
	{
		if(!working)
			return world.getEntitiesWithinAABBExcludingEntity(entityIn, bb);
		
		
		return getEntitiesInAABBexcluding(world, entityIn, bb, EntitySelectors.NOT_SPECTATING, size);
			
	}
	
	public List<Entity> getEntitiesInAABBexcluding(World world, @Nullable Entity entity, AxisAlignedBB bb, @Nullable Predicate <? super Entity > predicate, double size)
	{
		if(!working)
			return world.getEntitiesInAABBexcluding(entity, bb, predicate);
		
		List<Entity> list = Lists.<Entity>newArrayList();
		int j2 = MathHelper.floor((bb.minX - size) / 16.0D);
		int k2 = MathHelper.floor((bb.maxX + size) / 16.0D);
		int l2 = MathHelper.floor((bb.minZ - size) / 16.0D);
		int i3 = MathHelper.floor((bb.maxZ + size) / 16.0D);

		for (int j3 = j2; j3 <= k2; ++j3)
		{
			for (int k3 = l2; k3 <= i3; ++k3)
			{
				try
				{
					if ((boolean) m_isChunkLoaded.invoke(world, j3, k3, true))
					{
						Chunk chunk = world.getChunkFromChunkCoords(j3, k3);
						getEntitiesWithinAABBForEntity(chunk, entity, bb, list, predicate, size);
					}
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | CriticalException e)
				{
					if(!errorgetEntitiesInAABBexcluding)
					{
						ErrorUtil.logSilent("HookWorld getEntitiesInAABBexcluding Exception "+e.getClass());
						errorgetEntitiesInAABBexcluding = true;
						e.printStackTrace();
					}
					return world.getEntitiesInAABBexcluding(entity, bb, predicate);
				}
			}
		}

		return list;
	}
	
	public <T extends Entity> List<T>  getEntitiesWithinAABB(World world, Class <? extends T > classEntity, AxisAlignedBB bb, double size)
	{
		if(!working)
			return world.getEntitiesWithinAABB(classEntity, bb);
		
		return getEntitiesWithinAABB(world, classEntity, bb, EntitySelectors.NOT_SPECTATING, size);
	}
	
	public <T extends Entity> List<T> getEntitiesWithinAABB(World world, Class <? extends T > clazz, AxisAlignedBB aabb, @Nullable Predicate <? super T > filter, double size)
	{
		if(!working)
			return world.getEntitiesWithinAABB(clazz, aabb, filter);
		
		int j2 = MathHelper.floor((aabb.minX - size) / 16.0D);
		int k2 = MathHelper.ceil((aabb.maxX + size) / 16.0D);
		int l2 = MathHelper.floor((aabb.minZ - size) / 16.0D);
		int i3 = MathHelper.ceil((aabb.maxZ + size) / 16.0D);
		List<T> list = Lists.<T>newArrayList();

		for (int j3 = j2; j3 < k2; ++j3)
		{
			for (int k3 = l2; k3 < i3; ++k3)
			{
				try
				{
					if ((boolean) m_isChunkLoaded.invoke(world, j3, k3, true))
					{
						Chunk chunk = world.getChunkFromChunkCoords(j3, k3);
						getEntitiesOfTypeWithinAABB(chunk, clazz, aabb, list, filter, size);
					}
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | CriticalException e)
				{
					if(!errorgetEntitiesInAABB)
					{
						ErrorUtil.logSilent("HookWorld getEntitiesWithinAABB Exception "+e.getClass());
						errorgetEntitiesInAABB = true;
						e.printStackTrace();
					}
					return world.getEntitiesWithinAABB(clazz, aabb, filter);
				}
			}
		}

		return list;
	}
	
	public <T extends Entity> void getEntitiesOfTypeWithinAABB(Chunk chunk, Class <? extends T > entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate <? super T > filter, double size)
	{
		if(!working)
		{
			chunk.getEntitiesOfTypeWithinAABB(entityClass, aabb, listToFill, filter);
			return;
		}
		
		ClassInheritanceMultiMap<Entity>[] entityLists = chunk.getEntityLists();
		int i = MathHelper.floor((aabb.minY - size) / 16.0D);
		int j = MathHelper.floor((aabb.maxY + size) / 16.0D);
		i = MathHelper.clamp(i, 0, entityLists.length - 1);
		j = MathHelper.clamp(j, 0, entityLists.length - 1);

		for (int k = i; k <= j; ++k)
		{
			for (T t : entityLists[k].getByClass(entityClass))
			{
				if (t.getEntityBoundingBox().intersects(aabb) && (filter == null || filter.apply(t)))
				{
					listToFill.add(t);
				}
			}
		}
	}
	
	public void getEntitiesWithinAABBForEntity(Chunk chunk, @Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate <? super Entity > filter, double size)
	{
		if(!working)
		{
			chunk.getEntitiesWithinAABBForEntity(entityIn, aabb, listToFill, filter);
		}
		
		ClassInheritanceMultiMap<Entity>[] entityLists = chunk.getEntityLists();
		int i = MathHelper.floor((aabb.minY - size) / 16.0D);
		int j = MathHelper.floor((aabb.maxY + size) / 16.0D);
		i = MathHelper.clamp(i, 0, entityLists.length - 1);
		j = MathHelper.clamp(j, 0, entityLists.length - 1);

		for (int k = i; k <= j; ++k)
		{
			if (!entityLists[k].isEmpty())
			{
				for (Entity entity : entityLists[k])
				{
					if (entity.getEntityBoundingBox().intersects(aabb) && entity != entityIn)
					{
						if (filter == null || filter.apply(entity))
						{
							listToFill.add(entity);
						}

						Entity[] aentity = entity.getParts();

						if (aentity != null)
						{
							for (Entity entity1 : aentity)
							{
								if (entity1 != entityIn && entity1.getEntityBoundingBox().intersects(aabb) && (filter == null || filter.apply(entity1)))
								{
									listToFill.add(entity1);
								}
							}
						}
					}
				}
			}
		}
	}
}
