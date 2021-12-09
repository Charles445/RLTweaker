package com.charles445.rltweaker.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.registries.IForgeRegistry;

public class ReskillableReflect
{
	public final Class c_TransmutationRegistry;
	public final Method m_TransmutationRegistry_addEntryToReagent;
	
	public final Class c_ReskillableRegistries;
	public final Field f_ReskillableRegistries_SKILLS;
	public final Field f_ReskillableRegistries_UNLOCKABLES;
	
	public final Class c_Unlockable;
	public final Method m_Unlockable_getKey;
	
	public final Class c_PlayerData;
	public final Method m_PlayerData_getSkillInfo;
	public final Method m_PlayerData_saveAndSync;
	
	public final Class c_PlayerDataHandler;
	public final Method m_PlayerDataHandler_get;
	
	public final Class c_PlayerSkillInfo;
	public final Method m_PlayerSkillInfo_isUnlocked;
	public final Method m_PlayerSkillInfo_lock;
	
	public final Class c_LockUnlockableEvent;
	public final Method m_LockUnlockableEvent_getUnlockable;
	
	public final Class c_LockUnlockableEvent$Pre;
	public final Constructor con_LockUnlockableEvent$Pre;

	public final Class c_LockUnlockableEvent$Post;
	public final Constructor con_LockUnlockableEvent$Post;
	
	public ReskillableReflect() throws Exception
	{
		c_TransmutationRegistry = Class.forName("codersafterdark.reskillable.api.transmutations.TransmutationRegistry");
		m_TransmutationRegistry_addEntryToReagent = ReflectUtil.findMethod(c_TransmutationRegistry, "addEntryToReagent");
		
		c_ReskillableRegistries = Class.forName("codersafterdark.reskillable.api.ReskillableRegistries");
		f_ReskillableRegistries_SKILLS = ReflectUtil.findField(c_ReskillableRegistries, "SKILLS");
		f_ReskillableRegistries_UNLOCKABLES = ReflectUtil.findField(c_ReskillableRegistries, "UNLOCKABLES");
		
		c_Unlockable = Class.forName("codersafterdark.reskillable.api.unlockable.Unlockable");
		m_Unlockable_getKey = ReflectUtil.findMethod(c_Unlockable, "getKey");
		
		c_PlayerData = Class.forName("codersafterdark.reskillable.api.data.PlayerData");
		m_PlayerData_getSkillInfo = ReflectUtil.findMethod(c_PlayerData, "getSkillInfo");
		m_PlayerData_saveAndSync = ReflectUtil.findMethod(c_PlayerData, "saveAndSync");
		
		c_PlayerDataHandler = Class.forName("codersafterdark.reskillable.api.data.PlayerDataHandler");
		m_PlayerDataHandler_get = ReflectUtil.findMethod(c_PlayerDataHandler, "get", EntityPlayer.class);
		
		c_PlayerSkillInfo = Class.forName("codersafterdark.reskillable.api.data.PlayerSkillInfo");
		m_PlayerSkillInfo_isUnlocked = ReflectUtil.findMethod(c_PlayerSkillInfo, "isUnlocked");
		m_PlayerSkillInfo_lock = ReflectUtil.findMethod(c_PlayerSkillInfo, "lock");

		c_LockUnlockableEvent = Class.forName("codersafterdark.reskillable.api.event.LockUnlockableEvent");
		m_LockUnlockableEvent_getUnlockable = ReflectUtil.findMethod(c_LockUnlockableEvent, "getUnlockable");
		
		c_LockUnlockableEvent$Pre = Class.forName("codersafterdark.reskillable.api.event.LockUnlockableEvent$Pre");
		con_LockUnlockableEvent$Pre = c_LockUnlockableEvent$Pre.getDeclaredConstructor(EntityPlayer.class, c_Unlockable);
		
		c_LockUnlockableEvent$Post = Class.forName("codersafterdark.reskillable.api.event.LockUnlockableEvent$Post");
		con_LockUnlockableEvent$Post = c_LockUnlockableEvent$Post.getDeclaredConstructor(EntityPlayer.class, c_Unlockable);
	}
	
	public void addEntryToReagent(Item item, IBlockState state1, IBlockState state2) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		m_TransmutationRegistry_addEntryToReagent.invoke(null, item, state1, state2);
	}
	
	public IForgeRegistry<?> getSkillsRegistry() throws IllegalArgumentException, IllegalAccessException
	{
		return (IForgeRegistry<?>) f_ReskillableRegistries_SKILLS.get(null);
	}
	
	public IForgeRegistry<?> getUnlockablesRegistry() throws IllegalArgumentException, IllegalAccessException
	{
		return (IForgeRegistry<?>) f_ReskillableRegistries_UNLOCKABLES.get(null);
	}
	
	public Object getPlayerData(EntityPlayer player) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_PlayerDataHandler_get.invoke(null, player);
	}
	
	public Object getSkillInfo(Object playerData, Object skill) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_PlayerData_getSkillInfo.invoke(playerData, skill);
	}
	
	public boolean isUnlocked(Object skillInfo, Object unlockable) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (boolean) m_PlayerSkillInfo_isUnlocked.invoke(skillInfo, unlockable);
	}
	
	public boolean postLockUnlockableEventPre(EntityPlayer player, Object unlockable) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return MinecraftForge.EVENT_BUS.post((Event)con_LockUnlockableEvent$Pre.newInstance(player, unlockable));
	}
	
	public boolean postLockUnlockableEventPost(EntityPlayer player, Object unlockable) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return MinecraftForge.EVENT_BUS.post((Event)con_LockUnlockableEvent$Post.newInstance(player, unlockable));
	}
	
	public void lockPlayerSkill(Object skillInfo, Object unlockable, EntityPlayer player) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		m_PlayerSkillInfo_lock.invoke(skillInfo, unlockable, player);
	}
	
	public void saveAndSyncPlayerData(Object playerData) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		m_PlayerData_saveAndSync.invoke(playerData);
	}
	
	public Object getUnlockableFromLockedEvent(Object lockedEvent) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_LockUnlockableEvent_getUnlockable.invoke(lockedEvent);
	}
	
	public String getUnlockableName(Object unlockable) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (String) m_Unlockable_getKey.invoke(unlockable);
	}
}
