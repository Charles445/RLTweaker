package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.capability.RLCapabilities;
import com.charles445.rltweaker.capability.TweakerProvider;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.network.MessageSendVersion;
import com.charles445.rltweaker.network.MessageUpdateAttackYaw;
import com.charles445.rltweaker.network.MessageUpdateDismountStatus;
import com.charles445.rltweaker.network.MessageUpdateEntityMovement;
import com.charles445.rltweaker.network.NetworkHandler;
import com.charles445.rltweaker.network.PacketHandler;
import com.charles445.rltweaker.network.TaskScheduler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MinecraftHandler
{
	public MinecraftHandler()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	//Capabilities
	
	@SubscribeEvent
	public void onAttachCapability(AttachCapabilitiesEvent event)
	{
		//Find player
		if(event.getObject() instanceof EntityPlayer)
		{
			//Attach capabilities
			
			//Attach tweaker
			event.addCapability(new ResourceLocation(RLTweaker.MODID, RLCapabilities.TWEAKER_IDENTIFIER), new TweakerProvider(RLCapabilities.TWEAKER));
			
		}
	}
	
	//TaskScheduler processing
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientScheduled(TickEvent.ClientTickEvent event)
	{
		TaskScheduler.processClientTick(event);
	}
	
	//Task Creation
	
	//TODO guarantee that this is the LAST handler registered
	//Nobody currently uses a lower priority for EntityMountEvent in rlcraft
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityDismount(EntityMountEvent event)
	{
		World world = event.getWorldObj();
		if(world.isRemote)
			return;
		
		
		if(ModConfig.server.minecraft.playerDismountSync && event.isDismounting() && event.getEntityMounting() instanceof EntityPlayerMP && event.getEntityBeingMounted()!=null)
		{
			//Send a packet to the player of the player to dismount then and there
			MessageUpdateDismountStatus message = new MessageUpdateDismountStatus();
			PacketHandler.instance.sendTo(message, (EntityPlayerMP)event.getEntityMounting());
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onClientPlayerJoinWorldEvent(EntityJoinWorldEvent event)
	{
		if(!event.getWorld().isRemote)
			return;
		
		//Client logical
		
		if(event.getEntity() instanceof EntityPlayer)
		{
			//EntityPlayer
			
			EntityPlayer localPlayer = RLTweaker.proxy.getClientMinecraftPlayer();
			
			if(localPlayer==null)
				return;
			
			//Client logical
			//Client physical
			
			EntityPlayer entPlayer = (EntityPlayer)event.getEntity();
			
			
			if(entPlayer.getGameProfile().getId().equals(localPlayer.getGameProfile().getId()))
			{
				//Player is you
				if(NetworkHandler.serverHasVersioning)
				{
					//Create a message to the server to let them know your version
					RLTweaker.logger.debug("Sending MessageSendVersion to server");
					PacketHandler.instance.sendToServer(new MessageSendVersion(RLTweaker.VERSION_DELIMITER));
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerLoggedOutEvent(PlayerLoggedOutEvent event)
	{
		if(event.player!=null)
		{
			NetworkHandler.removeClient(event.player.getGameProfile().getId());
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinWorldLowest(EntityJoinWorldEvent event)
	{
		if(event.getEntity() instanceof EntityArrow)
		{
			handleArrowJoinWorld((EntityArrow)event.getEntity());
		}
	}
	
	private void handleArrowJoinWorld(EntityArrow arrow)
	{
		World world = arrow.getEntityWorld();
		if(!world.isRemote && ModConfig.server.minecraft.playerArrowSync && arrow.shootingEntity instanceof EntityPlayer)
		{
			MessageUpdateEntityMovement message = new MessageUpdateEntityMovement(arrow);
			PacketHandler.instance.sendToAllAround(message, new TargetPoint(world.provider.getDimension(), arrow.posX, arrow.posY, arrow.posZ, 24));
		}
	}
	
	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event)
	{
		if(ModConfig.server.minecraft.blacksmithChestTweak)
		{
			if(event.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH))
			{
				LootTable table = event.getTable();
				if(table!=null)
				{
					//The pool needed is called 'main'
					LootPool pool = table.getPool("main");
					
					if(pool != null)
					{
						//RLTweaker.logger.info("Removing loot from village blacksmiths...");
						pool.removeEntry("minecraft:diamond");
						//pool.removeEntry("minecraft:iron_ingot");
						//pool.removeEntry("minecraft:gold_ingot");
						//pool.removeEntry("minecraft:bread");
						//pool.removeEntry("minecraft:apple");
						pool.removeEntry("minecraft:iron_pickaxe");
						pool.removeEntry("minecraft:iron_sword");
						pool.removeEntry("minecraft:iron_chestplate");
						pool.removeEntry("minecraft:iron_helmet");
						pool.removeEntry("minecraft:iron_leggings");
						pool.removeEntry("minecraft:iron_boots");
						pool.removeEntry("minecraft:obsidian");
						//pool.removeEntry("minecraft:sapling");
						//pool.removeEntry("minecraft:saddle");
						pool.removeEntry("minecraft:iron_horse_armor");
						pool.removeEntry("minecraft:golden_horse_armor");
						pool.removeEntry("minecraft:diamond_horse_armor");
					}
				}
				
			}
		}
	}
	
	@SubscribeEvent
	public void onKnockback (LivingKnockBackEvent event)
	{
		if(!ModConfig.server.minecraft.damageTilt)
			return;
		
		if(event.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			if(player.world.isRemote)
				return;
			
			//Server Side
			if(NetworkHandler.isVersionAtLeast(0, 4, player))
				PacketHandler.instance.sendTo(new MessageUpdateAttackYaw(player), (EntityPlayerMP) player);
		}
	}
}
