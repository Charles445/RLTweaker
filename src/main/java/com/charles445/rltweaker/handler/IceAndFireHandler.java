package com.charles445.rltweaker.handler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.IceAndFireReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.LootUtil.DoNothingFunction;
import com.charles445.rltweaker.util.ModNames;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootingEnchantBonus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public class IceAndFireHandler 
{
	private IceAndFireReflect reflector;
	
	public IceAndFireHandler()
	{
		try
		{
			reflector = new IceAndFireReflect();
			
			if(ModConfig.server.iceandfire.rightHandDragonInteraction)
			{
				//1.7.1
				CompatUtil.wrapSpecificHandler("IAFUseItem", IAFUseItem::new, "com.github.alexthe666.iceandfire.event.EventLiving", "onEntityUseItem");
				//1.8+
				CompatUtil.wrapSpecificHandler("IAFUseItem", IAFUseItem::new, "com.github.alexthe666.iceandfire.event.ServerEvents", "onEntityUseItem");
			}
			
			if(ModConfig.server.iceandfire.rlcraftTradeBalancing)
				attemptRemoveRLCraftTrades();
			
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
	public void onEntityInteract(PlayerInteractEvent.EntityInteract event)
	{
		if(ModConfig.server.iceandfire.rightHandDragonInteraction && event.getHand() == EnumHand.OFF_HAND && reflector.c_ItemDragonHornStatic != null)
		{
			if(reflector.c_EntityDragonBase.isInstance(event.getTarget()))
			{
				ItemStack offhand = event.getEntityPlayer().getHeldItemOffhand();
				if(offhand.isEmpty())
					return;
				
				if(reflector.c_ItemDragonHornStatic.isInstance(offhand.getItem()))
				{
					event.setCanceled(true);
				}
			}
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
		
		//Fix myrmex queen trades
		if(ModConfig.server.iceandfire.myrmexQueenTradeFix && !event.getWorld().isRemote && reflector.c_EntityMyrmexQueen.isInstance(event.getEntity()))
		{
			Entity myrmexBase = event.getEntity();
			
			//Server side myrmex queen
			try
			{
				MerchantRecipeList trades = reflector.getMyrmexTrades(myrmexBase);
				if(trades != null)
				{
					boolean myrmexIsJungle = reflector.isMyrmexJungle(myrmexBase);
					
					boolean hasJungle = false;
					boolean hasDesert = false;
					
					for(MerchantRecipe trade : trades)
					{
						String reg = trade.getItemToBuy().getItem().getRegistryName().toString();
						if(reg.equals("iceandfire:myrmex_desert_resin"))
							hasDesert = true;
						else if(reg.equals("iceandfire:myrmex_jungle_resin"))
							hasJungle = true;
					}
						
					if((myrmexIsJungle && hasDesert) || (!myrmexIsJungle && hasJungle))
					{
						//FIXME move to debug
						RLTweaker.logger.info("Clearing broken myrmex queen trades: "+myrmexBase.getPosition().toString());
						reflector.resetMyrmexTrades(myrmexBase);
					}
				}
			} 
			catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
			{
				ErrorUtil.logSilent("IceAndFireHandler Myrmex Queen Trades Failure");
			}
		}	
	}
	
	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event)
	{
		if(!ModConfig.server.iceandfire.deathwormEggsIgnoreLooting)
			return;
		
		switch(event.getName().toString())
		{
			case "iceandfire:deathworm_red_giant":
			case "iceandfire:deathworm_red":
			case "iceandfire:deathworm_tan_giant":
			case "iceandfire:deathworm_tan":
			case "iceandfire:deathworm_white_giant":
			case "iceandfire:deathworm_white":
				removeDeathWormEggLooting(event, event.getName());
				break;
			default:
				break;
		}
	}
	
	private void removeDeathWormEggLooting(LootTableLoadEvent event, ResourceLocation resource)
	{
		LootTable table = event.getTable();
		if(table == null)
			return;
		
		try
		{
			List<LootPool> pools = reflector.getPools(table);
			if(pools == null)
				return;
			
			for(LootPool pool : pools)
			{
				if(pool.getName().equals(resource.getResourcePath()))
				{
					List<LootEntry> poolEntries = reflector.getEntries(pool);
					if(poolEntries != null)
					{
						for(LootEntry entry : poolEntries)
						{
							if(entry != null)
							{
								if(entry instanceof LootEntryItem && entry.getEntryName().contains("deathworm_egg"))
								{
									final LootFunction[] functions = reflector.getFunctions((LootEntryItem)entry);
									if(functions != null)
									{
										for(int i=0; i < functions.length; i++)
										{
											LootFunction function = functions[i];
											if(function instanceof LootingEnchantBonus)
											{
												functions[i] = new DoNothingFunction();
												RLTweaker.logger.debug("Removed LootingEnchantBonus for deathworm egg: "+resource.toString());
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			ErrorUtil.logSilent("IAF Death Worm Egg Looting Invocation");
		}
	}
	
	@SubscribeEvent
	public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
	{
		//Prevent gorgons from targeting old player statues
		if(!ModConfig.server.iceandfire.stopGorgonTargetingPlayerStatues)
			return;
		
		if(reflector.c_EntityGorgon.isInstance(event.getEntity()) && reflector.c_EntityStoneStatue.isInstance(event.getTarget()))
		{
			if(event.getEntityLiving() instanceof EntityLiving)
			{
				((EntityLiving)event.getEntityLiving()).setAttackTarget(null);
			}
		}
	}
	
	private void attemptRemoveRLCraftTrades()
	{
		//Quickly verify that we're in the correct version of the mod
		try
		{
			Class.forName("com.github.alexthe666.iceandfire.core.ModVillagers");
		}
		catch(Exception e)
		{
			return;
		}
		
		try
		{
			Class c_ModVillagers = Class.forName("com.github.alexthe666.iceandfire.core.ModVillagers");
			Object o_ModVillagers_INSTANCE = ReflectUtil.findField(c_ModVillagers, "INSTANCE").get(null);
			Map<Integer, VillagerProfession> o_ModVillagers_professions = (Map<Integer, VillagerProfession>) ReflectUtil.findField(c_ModVillagers, "professions").get(o_ModVillagers_INSTANCE);
			
			//VillagerCareer fishermanCareer = o_ModVillagers_professions.get(0).getCareer(0);
			VillagerCareer craftsmanCareer = o_ModVillagers_professions.get(1).getCareer(0);
			
			Field f_VillagerCareer_trades = ReflectUtil.findField(VillagerCareer.class, "trades");
			
			//Field f_itemToBuy = ReflectUtil.findField(Class.forName("com.github.alexthe666.iceandfire.core.ModVillagers$ListItemForSapphires"), "itemToBuy");
			Field f_buyingItem = ReflectUtil.findField(Class.forName("com.github.alexthe666.iceandfire.core.ModVillagers$SapphireForItems"), "buyingItem");
			
			//removeAllListEmeraldWithBought(new ResourceLocation(ModNames.ICEANDFIRE, "fishing_spear"), (List<List<ITradeList>>)f_VillagerCareer_trades.get(fishermanCareer), f_itemToBuy);
			removeAllSapphireForItemWithSold(new ResourceLocation("minecraft", "snow"), (List<List<ITradeList>>)f_VillagerCareer_trades.get(craftsmanCareer), f_buyingItem);
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to remove RLCraft balancing trades", e);
			ErrorUtil.logSilent("Ice and Fire RLCraft Trade Removal");
		}
	}
	
	private void removeAllListEmeraldWithBought(ResourceLocation itemRegistry, List<List<ITradeList>> trades, Field f_itemToBuy) throws IllegalArgumentException, IllegalAccessException
	{
		//com.github.alexthe666.iceandfire.core.ModVillagers$ListItemForSapphires
		for(List<ITradeList> tradeLevel : trades)
		{
			Iterator<ITradeList> iterator = tradeLevel.iterator();
			while(iterator.hasNext())
			{
				ITradeList tradeList = iterator.next();
				
				if(tradeList.getClass().getName().equals("com.github.alexthe666.iceandfire.core.ModVillagers$ListItemForSapphires"))
				{
					//Reflect and check the item
					ItemStack stack = (ItemStack) f_itemToBuy.get(tradeList);
					if(stack.getItem().getRegistryName().equals(itemRegistry))
					{
						iterator.remove();
						RLTweaker.logger.info("Removed a snow villager trade: "+itemRegistry.toString());
					}
				}
			}
		}
	}
	
	private void removeAllSapphireForItemWithSold(ResourceLocation itemRegistry, List<List<ITradeList>> trades, Field f_buyingItem) throws IllegalArgumentException, IllegalAccessException
	{
		//com.github.alexthe666.iceandfire.core.ModVillagers$SapphireForItems
		for(List<ITradeList> tradeLevel : trades)
		{
			Iterator<ITradeList> iterator = tradeLevel.iterator();
			while(iterator.hasNext())
			{
				ITradeList tradeList = iterator.next();
				
				if(tradeList.getClass().getName().equals("com.github.alexthe666.iceandfire.core.ModVillagers$SapphireForItems"))
				{
					//Reflect and check the item
					Item item = (Item) f_buyingItem.get(tradeList);
					if(item.getRegistryName().equals(itemRegistry))
					{
						iterator.remove();
						RLTweaker.logger.info("Removed a snow villager trade: "+itemRegistry.toString());
					}
				}
			}
		}
	}
	
	public class IAFUseItem
	{
		private IEventListener handler;
		public IAFUseItem(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void onUseItem(final PlayerInteractEvent.RightClickItem event)
		{
			if(ModConfig.server.iceandfire.rightHandDragonInteraction && event.getHand() == EnumHand.OFF_HAND)
				return;
			
			handler.invoke(event);
		}
	}
}
