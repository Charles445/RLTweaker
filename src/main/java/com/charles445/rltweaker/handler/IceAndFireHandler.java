package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.IceAndFireReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class IceAndFireHandler 
{
	private IceAndFireReflect reflector;
	
	public IceAndFireHandler()
	{
		try
		{
			reflector = new IceAndFireReflect();
			
			MinecraftForge.EVENT_BUS.register(this);
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to setup IceAndFireHandler!", e);
			ErrorUtil.logSilent("IceAndFireHandler Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	@SubscribeEvent
	public void onMobGriefing(EntityMobGriefingEvent event)
	{
		if(ModConfig.server.iceandfire.statueMobGriefingFix && event.getEntity() instanceof EntityLiving)
		{
			EntityLiving entity = (EntityLiving)event.getEntity();
			
			//Quick preliminary checks
			if(!entity.world.isRemote && entity.canPickUpLoot())
			{
				//Server side, entity is capable of grabbing items
				//Skipping dead check
				
				//Test for stone property
				if(reflector.getIsStone(entity))
				{
					//Deny if stone
					event.setResult(Event.Result.DENY);
				}
			}
		}	
	}
	
	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent event)
	{
		if(ModConfig.server.iceandfire.statueDataFixes && !event.getWorld().isRemote && event.getEntity() instanceof EntityItem)
		{
			//Could call this first instead of instanceof EntityItem first? Not sure which would be faster on average
			//Negligible either way
			EntityItem itemEntity = (EntityItem)event.getEntity();
			if(reflector.c_ItemStoneStatue.isInstance(itemEntity.getItem().getItem()))
			{
				ItemStack readStack = itemEntity.getItem();
				
				if (readStack.getTagCompound() != null)
				{
					//EntityList.getClassFromID is client side...
					EntityEntry entry = net.minecraftforge.registries.GameData.getEntityRegistry().getValue(readStack.getTagCompound().getInteger("IAFStoneStatueEntityID"));
					Class<? extends Entity> entityClazz = entry.getEntityClass();
					if(entityClazz != null)
					{
						ResourceLocation entityResource = EntityList.getKey(entityClazz);
						if(entityResource != null)
						{
							//Resource has the entity information, so now we can handle the statues individually
							//Depending on what comes up anyway...
							if(entityResource.toString().equals("lycanitesmobs:kobold"))
							{
								ItemStack writeStack = readStack.copy();
								writeStack.getTagCompound().removeTag("Items");
								itemEntity.setItem(writeStack);
							}
						}
					}
				}
			}
		}
	}
}
