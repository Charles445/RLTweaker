package com.charles445.rltweaker.hook;

import java.util.List;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CollisionUtil;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.WorldRadiusUtil;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class HookWorld
{
	public static boolean warnItemHook = false;
	
	public static boolean warnSearchHook = false;
	
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
	
	//HOOK into EntityLivingBase.collideWithNearbyEntities
	public static List<Entity> getEntitiesInAABBexcluding(World world, @Nullable Entity entity, AxisAlignedBB bb, @Nullable Predicate <? super Entity > predicate)
	{
		if(!ModConfig.server.minecraft.lessCollisions)
			return world.getEntitiesInAABBexcluding(entity, bb, predicate);
		
		if(entity != null)
		{
			try
			{
				return WorldRadiusUtil.instance.getEntitiesInAABBexcluding(world, entity, bb, predicate, CollisionUtil.instance.getRadiusForEntity(entity));
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
		
		return world.getEntitiesInAABBexcluding(entity, bb, predicate);
	}
	
	//HOOK into World.getEntitiesWithinAABB
	public static List<Entity> getEntitiesWithinAABB(World world, Class<Entity> clazz, AxisAlignedBB bb, @Nullable Predicate <? super Entity > predicate)
	{
		try
		{
			if(clazz.equals(EntityItem.class) || clazz.equals(EntityPlayer.class))
			{
				return WorldRadiusUtil.instance.getEntitiesWithinAABB(world, clazz, bb, predicate, 2.0d);
			}
		}
		catch(Exception e)
		{
			if(!warnSearchHook)
			{
				warnSearchHook = true;
				RLTweaker.logger.error("Error running ReducedSearchSize!",e);
				ErrorUtil.logSilent("ReducedSearchSize Critical Failure");
			}
		}
		
		
		return world.getEntitiesWithinAABB(clazz, bb, predicate);
	}
	
	//UNUSED
	//HOOK
	//com/charles445/rltweaker/hook/HookWorld
	//getAABExcludingSizeFor
	//(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;)D
	
	/////////////////
	//ChunkTick
	/////////////////
	
	public static boolean chunkTickPatchEnabled = false;
	
	//Serene Seasons
	public static IChunkTickPost sereneSeasonsPost = null;
	
	//com/charles445/rltweaker/hook/HookWorld
		//onPreUpdateBlocks
		//(Lnet/minecraft/world/WorldServer;)Lcom/charles445/rltweaker/hook/HookWorld$ChunkTickContainer;
	public static ChunkTickContainer onPreUpdateBlocks(WorldServer world)
	{
		if(!chunkTickPatchEnabled)
			chunkTickPatchEnabled = true;
		
		return new ChunkTickContainer(world);
	}
	
	//com/charles445/rltweaker/hook/HookWorld
	//postBlockTickChunk
	//(Lnet/minecraft/world/chunk/Chunk;Lcom/charles445/rltweaker/hook/HookWorld$ChunkTickContainer;)V
	public static void postBlockTickChunk(Chunk chunk, ChunkTickContainer container)
	{
		if(container.sereneSeasonsCompanionPost != null)
			sereneSeasonsPost.invoke(chunk, container.sereneSeasonsCompanionPost);
	}
	
	public static class ChunkTickContainer
	{
		public Object sereneSeasonsCompanionPost;
		
		public ChunkTickContainer(WorldServer world)
		{
			sereneSeasonsCompanionPost = sereneSeasonsPost == null ? null : sereneSeasonsPost.preUpdate(world);
		}
	}
	
	public static interface IChunkTickPost<T>
	{
		@Nullable
		public T preUpdate(WorldServer world);
		
		public void invoke(Chunk c, T companion);
	}

	/////////////////
	/////////////////
	
	
}
