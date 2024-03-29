package com.charles445.rltweaker.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.ModNames;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.Loader;

public class BattleTowersReflect
{
	public final Class c_AS_BattleTowersCore;
	public final Method m_AS_BattleTowersCore_getTowerDestroyers;
	public final Field f_AS_BattleTowersCore_instance;
	public final Object o_AS_BattleTowersCore_instance;
	public final Field f_AS_BattleTowersCore_minDistanceFromSpawn;
	public final Field f_AS_BattleTowersCore_minDistanceBetweenTowers;
	public final Field f_AS_BattleTowersCore_towerDestroyerEnabled;
	
	public final Class c_AS_EntityGolem;
	public final Method m_AS_EntityGolem_getIsDormant;
	
	public final Class c_AS_TowerDestroyer;
	public final Field f_AS_TowerDestroyer_player;
	public final Field f_AS_TowerDestroyer_deleteMe;
	
	public final Class c_AS_EntityGolemFireball;
	public final Field f_AS_EntityGolemFireball_shooterEntity;
	public final Field f_AS_EntityGolemFireball_accelerationX;
	public final Field f_AS_EntityGolemFireball_accelerationY;
	public final Field f_AS_EntityGolemFireball_accelerationZ;
	
	public final Class c_WorldGenHandler;
	public final Field f_WorldGenHandler_instance;
	public final Method m_WorldGenHandler_attemptToSpawnTower;
	public final Method m_WorldGenHandler_getWorldHandle;
	public final Method m_WorldGenHandler_getIsChunkProviderAllowed;
	public final Method m_WorldGenHandler_getIsBiomeAllowed;
	public final Method m_WorldGenHandler_getSurfaceBlockHeight;

	public final Class c_WorldGenHandler$TowerPosition;
	public final Constructor con_WorldGenHandler$TowerPosition;
	
	public final Class c_WorldGenHandler$WorldHandle;
	public final Field f_WorldGenHandler$WorldHandle_disableGenerationHook;
	
	//Lycanites Mobs
	private boolean isLycanitesAvailable;
	@Nullable
	private Class c_LM_ProjectileInfo;
	@Nullable
	private Method m_LM_ProjectileInfo_createProjectile;
	@Nullable
	private Class c_LM_ProjectileManager;
	@Nullable
	private Method m_LM_ProjectileManager_getInstance;
	@Nullable
	private Method m_LM_ProjectileManager_getProjectile;
	@Nullable
	private Class c_LM_BaseProjectileEntity;
	@Nullable
	private Method m_LM_BaseProjectileEntity_getProjectileScale;
	@Nullable
	private Method m_LM_BaseProjectileEntity_setProjectileScale;
	
	public BattleTowersReflect() throws Exception
	{
		isLycanitesAvailable = false;
		
		c_AS_BattleTowersCore = Class.forName("atomicstryker.battletowers.common.AS_BattleTowersCore");
		m_AS_BattleTowersCore_getTowerDestroyers = ReflectUtil.findMethod(c_AS_BattleTowersCore, "getTowerDestroyers");
		f_AS_BattleTowersCore_instance = ReflectUtil.findField(c_AS_BattleTowersCore, "instance");
		o_AS_BattleTowersCore_instance = f_AS_BattleTowersCore_instance.get(null);
		f_AS_BattleTowersCore_minDistanceFromSpawn = ReflectUtil.findField(c_AS_BattleTowersCore, "minDistanceFromSpawn");
		f_AS_BattleTowersCore_minDistanceBetweenTowers = ReflectUtil.findField(c_AS_BattleTowersCore, "minDistanceBetweenTowers");
		f_AS_BattleTowersCore_towerDestroyerEnabled = ReflectUtil.findField(c_AS_BattleTowersCore, "towerDestroyerEnabled");
		
		c_AS_EntityGolem = Class.forName("atomicstryker.battletowers.common.AS_EntityGolem");
		m_AS_EntityGolem_getIsDormant = ReflectUtil.findMethod(c_AS_EntityGolem, "getIsDormant");
		
		c_AS_TowerDestroyer = Class.forName("atomicstryker.battletowers.common.AS_TowerDestroyer");
		f_AS_TowerDestroyer_player = ReflectUtil.findField(c_AS_TowerDestroyer, "player");
		f_AS_TowerDestroyer_deleteMe = ReflectUtil.findField(c_AS_TowerDestroyer, "deleteMe");
		
		c_AS_EntityGolemFireball = Class.forName("atomicstryker.battletowers.common.AS_EntityGolemFireball");
		f_AS_EntityGolemFireball_shooterEntity = ReflectUtil.findField(c_AS_EntityGolemFireball, "shooterEntity");
		f_AS_EntityGolemFireball_accelerationX = ReflectUtil.findField(c_AS_EntityGolemFireball, "accelerationX");
		f_AS_EntityGolemFireball_accelerationY = ReflectUtil.findField(c_AS_EntityGolemFireball, "accelerationY");
		f_AS_EntityGolemFireball_accelerationZ = ReflectUtil.findField(c_AS_EntityGolemFireball, "accelerationZ");
		
		c_WorldGenHandler = Class.forName("atomicstryker.battletowers.common.WorldGenHandler");
		f_WorldGenHandler_instance = ReflectUtil.findField(c_WorldGenHandler, "instance");
		m_WorldGenHandler_attemptToSpawnTower = ReflectUtil.findMethod(c_WorldGenHandler, "attemptToSpawnTower");
		m_WorldGenHandler_getWorldHandle = ReflectUtil.findMethod(c_WorldGenHandler, "getWorldHandle");
		m_WorldGenHandler_getIsChunkProviderAllowed = ReflectUtil.findMethod(c_WorldGenHandler, "getIsChunkProviderAllowed");
		m_WorldGenHandler_getIsBiomeAllowed =  ReflectUtil.findMethod(c_WorldGenHandler, "getIsBiomeAllowed");
		m_WorldGenHandler_getSurfaceBlockHeight =  ReflectUtil.findMethod(c_WorldGenHandler, "getSurfaceBlockHeight");

		c_WorldGenHandler$TowerPosition = Class.forName("atomicstryker.battletowers.common.WorldGenHandler$TowerPosition");
		con_WorldGenHandler$TowerPosition = c_WorldGenHandler$TowerPosition.getDeclaredConstructor(c_WorldGenHandler, int.class, int.class, int.class, int.class, boolean.class);
		
		c_WorldGenHandler$WorldHandle = Class.forName("atomicstryker.battletowers.common.WorldGenHandler$WorldHandle");
		f_WorldGenHandler$WorldHandle_disableGenerationHook = ReflectUtil.findField(c_WorldGenHandler$WorldHandle, "disableGenerationHook");
		
		//Lycanites Compatibility Setup
		if(Loader.isModLoaded(ModNames.LYCANITESMOBS))
		{
			try
			{
				c_LM_ProjectileInfo = Class.forName("com.lycanitesmobs.core.info.projectile.ProjectileInfo");
				c_LM_ProjectileManager = Class.forName("com.lycanitesmobs.core.info.projectile.ProjectileManager");
				c_LM_BaseProjectileEntity = Class.forName("com.lycanitesmobs.core.entity.BaseProjectileEntity");
				m_LM_ProjectileInfo_createProjectile = ReflectUtil.findMethod(c_LM_ProjectileInfo, "createProjectile", World.class, EntityLivingBase.class);
				m_LM_ProjectileManager_getInstance = ReflectUtil.findMethod(c_LM_ProjectileManager, "getInstance");
				m_LM_ProjectileManager_getProjectile = ReflectUtil.findMethod(c_LM_ProjectileManager, "getProjectile");
				m_LM_BaseProjectileEntity_getProjectileScale = ReflectUtil.findMethod(c_LM_BaseProjectileEntity, "getProjectileScale");
				m_LM_BaseProjectileEntity_setProjectileScale = ReflectUtil.findMethod(c_LM_BaseProjectileEntity, "setProjectileScale");
				isLycanitesAvailable = true;
			}
			catch(Exception e)
			{
				//Silent failure
				isLycanitesAvailable = false;
			}
		}
	}
	
	public boolean isEntityGolem(Entity entity)
	{
		return c_AS_EntityGolem.isInstance(entity);
	}
	
	public boolean getIsDormant(Object golem) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (boolean) m_AS_EntityGolem_getIsDormant.invoke(golem);
	}
	
	public Set<Object> getTowerDestroyers() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (Set<Object>) m_AS_BattleTowersCore_getTowerDestroyers.invoke(null);
	}
	
	public void setDestroyerPlayer(Object towerDestroyer, @Nullable Entity entityToSet) throws IllegalArgumentException, IllegalAccessException
	{
		f_AS_TowerDestroyer_player.set(towerDestroyer, entityToSet);
	}
	
	public boolean getDestroyerDeleteMe(Object towerDestroyer) throws IllegalArgumentException, IllegalAccessException
	{
		return f_AS_TowerDestroyer_deleteMe.getBoolean(towerDestroyer);
	}
	
	public void setDestroyerDeleteMe(Object towerDestroyer, boolean deleteMe) throws IllegalArgumentException, IllegalAccessException
	{
		f_AS_TowerDestroyer_deleteMe.setBoolean(towerDestroyer, deleteMe);
	}
	
	public boolean isEntityGolemFireball(Entity entity)
	{
		return c_AS_EntityGolemFireball.isInstance(entity);
	}
	
	public double getGolemFireballAccelerationX(Object golemFireball) throws IllegalArgumentException, IllegalAccessException
	{
		return f_AS_EntityGolemFireball_accelerationX.getDouble(golemFireball);
	}
	
	public double getGolemFireballAccelerationY(Object golemFireball) throws IllegalArgumentException, IllegalAccessException
	{
		return f_AS_EntityGolemFireball_accelerationY.getDouble(golemFireball);
	}
	
	public double getGolemFireballAccelerationZ(Object golemFireball) throws IllegalArgumentException, IllegalAccessException
	{
		return f_AS_EntityGolemFireball_accelerationZ.getDouble(golemFireball);
	}
	
	private Object getWorldGenHandler() throws IllegalArgumentException, IllegalAccessException
	{
		return f_WorldGenHandler_instance.get(null);
	}
	
	@Nullable
	public Object getWorldHandle(World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Object wghInstance = getWorldGenHandler();
		if(wghInstance == null)
			return null;
		
		return m_WorldGenHandler_getWorldHandle.invoke(wghInstance, world);
	}
	
	public boolean setWorldDisableGenerationHook(World world, int value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		Object wh = getWorldHandle(world);
		if(wh == null)
			return false;
		
		f_WorldGenHandler$WorldHandle_disableGenerationHook.setInt(wh, value);
		return true;
	}
	
	public boolean getIsChunkProviderAllowed(Object worldGenHandler, IChunkProvider provider) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{	
		return (boolean) m_WorldGenHandler_getIsChunkProviderAllowed.invoke(worldGenHandler, provider);
	}
	
	public boolean getIsBiomeAllowed(Object worldGenHandler, Biome biome) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{	
		return (boolean) m_WorldGenHandler_getIsBiomeAllowed.invoke(worldGenHandler, biome);
	}
	
	public int getSurfaceBlockHeight(Object worldGenHandler, World world, int x, int z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{	
		return (int) m_WorldGenHandler_getSurfaceBlockHeight.invoke(worldGenHandler, world, x, z);
	}
	
	public Object getModInstance()
	{
		return o_AS_BattleTowersCore_instance;
	}
	
	public int getMinDistanceFromSpawn() throws IllegalArgumentException, IllegalAccessException
	{
		return (int) f_AS_BattleTowersCore_minDistanceFromSpawn.get(getModInstance());
	}
	
	public int getMinDistanceBetweenTowers() throws IllegalArgumentException, IllegalAccessException
	{
		return (int) f_AS_BattleTowersCore_minDistanceBetweenTowers.get(getModInstance());
	}
	
	public int getTowerDestroyerEnabled() throws IllegalArgumentException, IllegalAccessException
	{
		return (int) f_AS_BattleTowersCore_towerDestroyerEnabled.getInt(getModInstance());
	}
	
	public void setTowerDestroyerEnabled(int val) throws IllegalArgumentException, IllegalAccessException
	{
		f_AS_BattleTowersCore_towerDestroyerEnabled.setInt(getModInstance(), val);
	}
	
	public Object newTowerPosition(Object worldGenHandler, int x, int y, int z, int type, boolean underground) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return con_WorldGenHandler$TowerPosition.newInstance(worldGenHandler, x, y, z, type, underground);
	}
	
	public boolean attemptToSpawnTower(Object worldGenHandler, World world, Object towerPosition, Random random, int x, int y, int z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (boolean) m_WorldGenHandler_attemptToSpawnTower.invoke(worldGenHandler, world, towerPosition, random, x, y, z);
	}
	
	//Lycanites Compatibility
	
	public boolean isLycanitesAvailable()
	{
		return isLycanitesAvailable;
	}
	
	@Nullable
	public Entity createLycanitesProjectileWithNameAndShooter(String projectileName, Entity shooter, float scaleModifier) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		//Check if feature is enabled
		if(!this.isLycanitesAvailable())
			return null;
		
		//Check if any reflectors are null
		if (
				c_LM_ProjectileInfo == null || m_LM_ProjectileInfo_createProjectile == null || 
				c_LM_ProjectileManager == null || m_LM_ProjectileManager_getInstance == null ||
				m_LM_ProjectileManager_getProjectile==null || c_LM_BaseProjectileEntity == null ||
				m_LM_BaseProjectileEntity_getProjectileScale == null || m_LM_BaseProjectileEntity_setProjectileScale == null
			)
			return null;
		
		//Check if the shooter is null
		if(shooter == null)
			return null;
		
		//Get the projectile info for the desired projectile
		Object projManagerInst = m_LM_ProjectileManager_getInstance.invoke(null);
		
		if(projManagerInst == null)
			return null;
		
		Object projectileInfo = m_LM_ProjectileManager_getProjectile.invoke(projManagerInst, projectileName);
		
		//projectileInfo can be null
		if(projectileInfo == null)
			return null;
		
		Object createdProjectile = m_LM_ProjectileInfo_createProjectile.invoke(projectileInfo, shooter.getEntityWorld(), shooter);
		
		if(createdProjectile == null)
			return null;
		
		//Get the scale
		Float currentScale = (Float) m_LM_BaseProjectileEntity_getProjectileScale.invoke(createdProjectile);
		if(currentScale == null)
			return null;
		
		//Set the scale
		m_LM_BaseProjectileEntity_setProjectileScale.invoke(createdProjectile, currentScale * scaleModifier);
		
		return (Entity) createdProjectile;
	}
}
