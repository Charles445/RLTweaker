package com.charles445.rltweaker.client.gui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.charles445.rltweaker.network.MessageReskillableLockSkill;
import com.charles445.rltweaker.network.NetworkHandler;
import com.charles445.rltweaker.network.PacketHandler;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ReskillableGuiDelegate extends GuiDelegateBase
{
	public final Class c_GuiSkillInfo;
	public final Field f_GuiSkillInfo_hoveredUnlockable;
	public final Field f_GuiSkillInfo_skill;
	
	public final Class c_Unlockable;
	
	public final Class c_PlayerData;
	public final Method m_PlayerData_getSkillInfo;
	
	public final Class c_PlayerDataHandler;
	public final Method m_PlayerDataHandler_get;
	
	public final Class c_PlayerSkillInfo;
	public final Method m_PlayerSkillInfo_isUnlocked;
	
	public ReskillableGuiDelegate() throws Exception
	{
		c_GuiSkillInfo = Class.forName("codersafterdark.reskillable.client.gui.GuiSkillInfo");
		f_GuiSkillInfo_hoveredUnlockable = ReflectUtil.findField(c_GuiSkillInfo, "hoveredUnlockable");
		f_GuiSkillInfo_skill = ReflectUtil.findField(c_GuiSkillInfo, "skill");
		
		c_Unlockable = Class.forName("codersafterdark.reskillable.api.unlockable.Unlockable");
		
		c_PlayerData = Class.forName("codersafterdark.reskillable.api.data.PlayerData");
		m_PlayerData_getSkillInfo = ReflectUtil.findMethod(c_PlayerData, "getSkillInfo");
		
		c_PlayerDataHandler = Class.forName("codersafterdark.reskillable.api.data.PlayerDataHandler");
		m_PlayerDataHandler_get = ReflectUtil.findMethod(c_PlayerDataHandler, "get", EntityPlayer.class);
		
		c_PlayerSkillInfo = Class.forName("codersafterdark.reskillable.api.data.PlayerSkillInfo");
		m_PlayerSkillInfo_isUnlocked = ReflectUtil.findMethod(c_PlayerSkillInfo, "isUnlocked");
	}
	
	@Override
	public boolean pollMousePre(GuiScreen guiScreen, int width, int height, int x, int y, int button, boolean state)
	{
		//GuiSkillInfo
		if(state && this.isButtonLeft(button) && NetworkHandler.serverVersion.isSameOrNewerVersion(0, 4, 3))
		{
			//Left click
			try
			{
				Object unlockable = f_GuiSkillInfo_hoveredUnlockable.get(guiScreen);
				if(unlockable == null)
					return false;
				
				Object data = m_PlayerDataHandler_get.invoke(null, mc.player);
				Object skill = f_GuiSkillInfo_skill.get(guiScreen);
				Object info = m_PlayerData_getSkillInfo.invoke(data, skill);
				boolean unlocked = (boolean) m_PlayerSkillInfo_isUnlocked.invoke(info, unlockable);
				
				if(!unlocked)
				 return false;
				
				playClickSound();
				
				MessageReskillableLockSkill outbound = new MessageReskillableLockSkill(
						((IForgeRegistryEntry.Impl<?>)skill).getRegistryName(), 
						((IForgeRegistryEntry.Impl<?>)unlockable).getRegistryName()
				);
				
				if(NetworkHandler.serverVersion.isSameOrNewerVersion(0, 4, 3))
					PacketHandler.instance.sendToServer(outbound);
				
				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return false;
	}
}
