package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.ModNames;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class BattleTowersReflect
{
	public final Class c_AS_BattleTowersCore;
	public final Method m_AS_BattleTowersCore_getTowerDestroyers;
	
	public final Class c_AS_EntityGolem;
	public final Method m_AS_EntityGolem_getIsDormant;
	
	public final Class c_AS_TowerDestroyer;
	public final Field f_AS_TowerDestroyer_player;
	
	public final Class c_AS_EntityGolemFireball;
	public final Field f_AS_EntityGolemFireball_shooterEntity;
	public final Field f_AS_EntityGolemFireball_accelerationX;
	public final Field f_AS_EntityGolemFireball_accelerationY;
	public final Field f_AS_EntityGolemFireball_accelerationZ;
	
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
		
		c_AS_EntityGolem = Class.forName("atomicstryker.battletowers.common.AS_EntityGolem");
		m_AS_EntityGolem_getIsDormant = ReflectUtil.findMethod(c_AS_EntityGolem, "getIsDormant");
		
		c_AS_TowerDestroyer = Class.forName("atomicstryker.battletowers.common.AS_TowerDestroyer");
		f_AS_TowerDestroyer_player = ReflectUtil.findField(c_AS_TowerDestroyer, "player");
		
		c_AS_EntityGolemFireball = Class.forName("atomicstryker.battletowers.common.AS_EntityGolemFireball");
		f_AS_EntityGolemFireball_shooterEntity = ReflectUtil.findField(c_AS_EntityGolemFireball, "shooterEntity");
		f_AS_EntityGolemFireball_accelerationX = ReflectUtil.findField(c_AS_EntityGolemFireball, "accelerationX");
		f_AS_EntityGolemFireball_accelerationY = ReflectUtil.findField(c_AS_EntityGolemFireball, "accelerationY");
		f_AS_EntityGolemFireball_accelerationZ = ReflectUtil.findField(c_AS_EntityGolemFireball, "accelerationZ");
		
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
	
	public boolean isEntityGolem(EntityLivingBase entity)
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
