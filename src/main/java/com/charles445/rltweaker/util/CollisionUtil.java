package com.charles445.rltweaker.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import com.charles445.rltweaker.RLTweaker;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class CollisionUtil
{
	public static CollisionUtil instance = new CollisionUtil();
	
	//Accessed by both logical client and logical server
	//TODO split this up?
	private Map<String, Double> stringReference = new ConcurrentHashMap<String, Double>();
	
	//These should be accessed individually, but might as well make them concurrent
	private Map<Class, Double> collisionMapServer = new ConcurrentHashMap<Class, Double>();
	private Map<Class, Double> collisionMapClient = new ConcurrentHashMap<Class, Double>();
	
	public CollisionUtil()
	{
		
	}
	
	public void addToStringReference(Map<String, Double> sent)
	{
		stringReference.putAll(sent);
	}
	
	public void refreshCollisionMaps()
	{
		//Empties out the cache
		collisionMapServer.clear();
		collisionMapClient.clear();
	}
	
	public double getRadiusForEntity(@Nonnull Entity entity)
	{
		if(entity.world.isRemote)
		{
			//Client
			Double dub = collisionMapClient.get(entity.getClass());
			if(dub!=null)
				return dub;
			
			dub = stringReference.get(entity.getClass().getName());
			
			if(dub==null)
			{
				dub = new Double(World.MAX_ENTITY_RADIUS);
			}
			
			RLTweaker.logger.debug("Adding "+entity.getClass().getName()+" with radius "+dub+" to client");
			collisionMapClient.put(entity.getClass(), dub);
			
			return dub.doubleValue();
		}
		else
		{
			//Client
			Double dub = collisionMapServer.get(entity.getClass());
			if(dub!=null)
				return dub;
			
			dub = stringReference.get(entity.getClass().getName());
			
			if(dub==null)
			{
				dub = new Double(World.MAX_ENTITY_RADIUS);
			}
			
			RLTweaker.logger.debug("Adding "+entity.getClass().getName()+" with radius "+dub+" to server");
			collisionMapServer.put(entity.getClass(), dub);
			
			return dub.doubleValue();
		}
	}
	
	
}
