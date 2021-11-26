package com.charles445.rltweaker.handler;

import java.util.Map;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.QuarkReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class QuarkHandler 
{
	QuarkReflect reflector;
	
	public QuarkHandler()
	{
		try
		{
			reflector = new QuarkReflect();
			//Wrap AncientTomes anvil handler and register the item handler
			CompatUtil.wrapSpecificHandler("QKAncientTomeAnvilUpdate", QKAncientTomeAnvilUpdate::new, "vazkii.quark.misc.feature.AncientTomes", "onAnvilUpdate");		
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup PotionCoreHandlerClient!", e);
			ErrorUtil.logSilent("PotionCore Client Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	public class QKAncientTomeAnvilUpdate
	{
		private IEventListener handler;
		
		public QKAncientTomeAnvilUpdate(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void onUseItem(final PlayerInteractEvent.RightClickItem event)
		{
			//If it's not mainhand or disabled, don't run this feature
			//Some items cancel the offhand event so we have to use main hand
			if(event.getHand() != EnumHand.MAIN_HAND || !ModConfig.server.quark.ancientTomesAlternateBehavior)
				return;
			
			EntityPlayer player = event.getEntityPlayer();
			//Check null player
			if(player == null)
				return;
			
			//Check hands for correct items
			
			//Make sure it's not empty
			ItemStack offhandStack = player.getHeldItemOffhand();
			if(offhandStack.isEmpty())
				return;
			
			ItemStack mainhandStack = player.getHeldItemMainhand();
			//Check empty mainhand or enchanted book
			if(mainhandStack.isEmpty() || mainhandStack.getItem() == Items.ENCHANTED_BOOK)
				return;
			
			try
			{
				//TODO could cache this more
				Item ancient_tome = reflector.getAncientTomeItem();
				if(ancient_tome == null)
				{
					ErrorUtil.logSilent("Quark AncientTomes Item Null");
					return;
				}
				
				//Make sure the item is an ancient tome
				if(offhandStack.getItem() != ancient_tome)
					return;
				
				//Make sure mainhand is not an ancient tome
				if(mainhandStack.getItem() == ancient_tome)
					return;
				
				//So the offhand is an ancient tome, and the mainhand is not an enchanted book or an ancient tome
				
				//These are copies
				Map<Enchantment, Integer> tomeEnchants = EnchantmentHelper.getEnchantments(offhandStack);
				Map<Enchantment, Integer> itemEnchants = EnchantmentHelper.getEnchantments(mainhandStack);
				
				boolean matched = false;
				
				for(Map.Entry<Enchantment, Integer> entry : tomeEnchants.entrySet())
				{
					Enchantment enchant = entry.getKey();
					//Go through all of the tome enchantments and look for matches
					if(enchant == null)
						continue;
					
					Integer val = itemEnchants.get(enchant);
					if(val != null && val == entry.getValue())
					{
						//Item has the enchantment and it's a level match
						//Set the item's enchantment to one higher
						itemEnchants.put(enchant, val + 1);
						//Mark as matched
						matched = true;
					}
				}
				
				if(matched)
				{
					//A change was made to itemEnchants, so it must be applied to the mainhand item for real
					ItemStack returnStack = mainhandStack.copy();
					EnchantmentHelper.setEnchantments(itemEnchants, returnStack);
					player.setHeldItem(EnumHand.MAIN_HAND, returnStack);

					//Remove the tome
					player.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
					
					//Play a sound on server
					if(player.world != null && !player.world.isRemote)
					{
						player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.7f, player.world.rand.nextFloat() * 0.1F + 0.9F);
					}
				}
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				ErrorUtil.logSilent("Quark AncientTomes Invocation");
				return;
			}
		}
		
		@SubscribeEvent
		public void anvilUpdate(final AnvilUpdateEvent event)
		{
			if(!ModConfig.server.quark.ancientTomesAlternateBehavior)
			{
				handler.invoke(event);
				return;
			}
		}
	}
}
