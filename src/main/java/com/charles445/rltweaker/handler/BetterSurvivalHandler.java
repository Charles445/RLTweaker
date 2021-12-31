package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.BetterSurvivalReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ModNames;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class BetterSurvivalHandler
{
	private final UUID UUID_BLINDNESS = UUID.fromString("a6107045-134f-4c14-a645-75c3ae5c7a27");
	private final UUID UUID_COMPENSATION = UUID.fromString("a6107045-134f-4c04-a645-75c3ae5c7a27");
	private final UUID UUID_SPEED = UUID.fromString("e6107045-134f-4c54-a645-75c3ae5c7a27");
	
	BetterSurvivalReflect reflector;
	
	public BetterSurvivalHandler()
	{
		try
		{
			reflector = new BetterSurvivalReflect();
			
			//Wrap the BreakEvent  handler
			CompatUtil.wrapSpecificHandler("BSBreak", BSBreak::new, "com.mujmajnkraft.bettersurvival.eventhandlers.ModEnchantmentHandler", "onEvent(Lnet/minecraftforge/event/world/BlockEvent$BreakEvent;)");			
			
			//Nunchuk blindness handler
			CompatUtil.wrapSpecificHandler("BSComboBlindness", BSComboBlindness::new, "com.mujmajnkraft.bettersurvival.eventhandlers.ModWeaponHandler", "onEvent(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingUpdateEvent;)");
			
			//Arrow handler
			CompatUtil.wrapSpecificHandler("BSArrowSpawn", BSArrowSpawn::new, "com.mujmajnkraft.bettersurvival.eventhandlers.ModEnchantmentHandler", "onEvent(Lnet/minecraftforge/event/entity/EntityJoinWorldEvent;)");
		
			//Enchantment handler
			CompatUtil.wrapSpecificHandler("BSEnchantment", BSEnchantment::new, "com.mujmajnkraft.bettersurvival.eventhandlers.ModEnchantmentHandler", "onEvent(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingUpdateEvent;)");
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup BetterSurvivalHandler!", e);
			ErrorUtil.logSilent("Better Survival Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	public class BSEnchantment
	{
		private IEventListener handler;
		private boolean replaceEnchantment;
		public BSEnchantment(IEventListener handler)
		{
			this.handler = handler;
			this.replaceEnchantment = ModConfig.server.bettersurvival.replaceEnchantmentHandler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		private void replacedEnchantment(LivingUpdateEvent event)
		{
			EntityLivingBase living = event.getEntityLiving();
			
			if(reflector.o_ConfigHandler_agilitylevel > 0)
			{
				int agilityLevel = EnchantmentHelper.getMaxEnchantmentLevel(reflector.o_ModEnchantments_agility, living);
				if(agilityLevel > 0)
				{
					IAttributeInstance movement_speed = living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);				
					double strength = 0.01 * agilityLevel;
					AttributeModifier modifier = new AttributeModifier(UUID_SPEED, "agility", strength, 0);
					
					if(!movement_speed.hasModifier(modifier))
					{
						movement_speed.applyModifier(modifier);
					}
					else if(movement_speed.getModifier(UUID_SPEED).getAmount() != strength)
					{
						movement_speed.removeModifier(UUID_SPEED);
						movement_speed.applyModifier(modifier);
					}
				}
				else
				{
					living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(UUID_SPEED);
				}
			}
			else
			{
				living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(UUID_SPEED);
			}
			
			if(reflector.o_ConfigHandler_vitalitylevel > 0 && living instanceof EntityPlayer)
			{
				int vitalityLevel = EnchantmentHelper.getMaxEnchantmentLevel(reflector.o_ModEnchantments_vitality, living);
				
				if(vitalityLevel > 0)
				{
					EntityPlayer player = (EntityPlayer)living;
					if(player.getFoodStats().getFoodLevel() >= 18 && player.getEntityWorld().getGameRules().getBoolean("naturalRegeneration"))
					{
						if(player.getHealth() < player.getMaxHealth() && player.ticksExisted % (40 / vitalityLevel) == 0)
						{
							player.heal(1.0f);
						}
					}
				}
			}
		}
		
		@SubscribeEvent(priority=EventPriority.HIGHEST, receiveCanceled=true)
		public void onLivingUpdate(LivingUpdateEvent event)
		{
			if(replaceEnchantment)
			{
				this.replacedEnchantment(event);
			}
			else
			{
				handler.invoke(event);
			}
		}
	}
	
	public class BSComboBlindness
	{
		private IEventListener handler;
		private boolean replaceComboHandler;
		public BSComboBlindness(IEventListener handler)
		{
			this.handler = handler;
			this.replaceComboHandler = ModConfig.server.bettersurvival.replaceComboHandler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		private void replacedComboHandler(LivingUpdateEvent event)
		{
			if(event.getEntityLiving() instanceof EntityLiving)
			{
				EntityLiving living = (EntityLiving) event.getEntityLiving();
				
				living.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).removeModifier(UUID_BLINDNESS);
				
				if(living.getActivePotionEffect(MobEffects.BLINDNESS) != null)
				{
					AttributeModifier modifier = new AttributeModifier(UUID_BLINDNESS, "blind", -0.8, 1);
					living.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(modifier);
				}
			}
			else if(event.getEntityLiving() instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) event.getEntityLiving();
				
				player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(UUID_COMPENSATION);
				
				//nunchaku
				if(reflector.hasINunchakuCombo(player))
				{
					Object combo = reflector.getINunchakuCombo(player);
					
					try
					{
						if(reflector.c_ItemNunchaku.isInstance(player.getHeldItemMainhand().getItem()))
						{
							if(reflector.getComboPower(combo) > 0)
								reflector.countDown(combo);
						}
						else
						{
							reflector.setComboTime(combo, 0);
						}
					}
					catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
					{
						ErrorUtil.logSilent("BetterSurvival Nunchaku Combo Invocation");
					}
				}
			}
		}
		
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onLivingUpdate(LivingUpdateEvent event)
		{
			if(replaceComboHandler)
			{
				this.replacedComboHandler(event);
			}
			else
			{
				handler.invoke(event);
			}
			
			if(event.getEntityLiving() instanceof EntityLiving)
			{
				IAttributeInstance follow_range = ((EntityLiving) event.getEntityLiving()).getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
				if(follow_range.getModifier(UUID_BLINDNESS) != null)
				{
					if(!ModConfig.server.bettersurvival.mobBlindness)
					{
						follow_range.removeModifier(UUID_BLINDNESS);
						return;
					}
					
					EntityEntry entityEntry = EntityRegistry.getEntry(event.getEntityLiving().getClass());
					if(entityEntry != null)
					{
						String mobType = entityEntry.getRegistryName().toString();
						String[] mobBlacklist = ModConfig.server.bettersurvival.mobBlindnessBlacklist;
						for(int i=0; i < mobBlacklist.length; i++)
						{
							if(mobType.equals(mobBlacklist[i]))
							{
								follow_range.removeModifier(UUID_BLINDNESS);
								return;
							}
						}
					}
					
					double sightValue = ModConfig.server.bettersurvival.mobBlindnessPercentage;
					if(sightValue != 80.0d)
					{
						follow_range.removeModifier(UUID_BLINDNESS);
						sightValue *= -0.01d; //Adjust for percentage and modifier operation 1 (x * (1 + modifier))
						follow_range.applyModifier(new AttributeModifier(UUID_BLINDNESS, "blind", sightValue, 1));
					}
				}
			}
		}
	}
	
	public class BSArrowSpawn
	{
		private IEventListener handler;
		public BSArrowSpawn(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent(priority=EventPriority.HIGHEST, receiveCanceled=true)
		public void onArrowJoinWorld(EntityJoinWorldEvent event)
		{
			double rangeSpeedMultiplier = ModConfig.server.bettersurvival.rangeSpeedMultiplier; 
			if(rangeSpeedMultiplier == 2.0d)
			{
				handler.invoke(event);
				return;
			}
			
			if(event.getEntity() instanceof EntityArrow && !event.getWorld().isRemote)
			{
				EntityArrow arrowEntity = (EntityArrow)event.getEntity();
				
				if(arrowEntity.shootingEntity instanceof EntityLivingBase)
				{
					//Passed enough checks, gather speed information
					double cachedX = arrowEntity.motionX;
					double cachedY = arrowEntity.motionY;
					double cachedZ = arrowEntity.motionZ;
					handler.invoke(event);
					//Detect a difference
					if(cachedX != arrowEntity.motionX || cachedY != arrowEntity.motionY || cachedZ != arrowEntity.motionZ)
					{
						//Override the speed change
						arrowEntity.motionX = cachedX * rangeSpeedMultiplier;
						arrowEntity.motionY = cachedY * rangeSpeedMultiplier;
						arrowEntity.motionZ = cachedZ * rangeSpeedMultiplier;
					}
				}
			}
		}
	}
	
	public class BSBreak
	{
		@Nullable
		private Enchantment tunneling;

		private boolean disabled = false;
		private IEventListener handler;
		public BSBreak(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent(priority = EventPriority.LOWEST)
		public void onBreakLowest(final BlockEvent.BreakEvent event)
		{
			if(!ModConfig.server.bettersurvival.tunnelingCancelable)
				return;
			
			onBreakAny(event);
		}
		
		@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled=true)
		public void onBreakHighCancel(final BlockEvent.BreakEvent event)
		{
			if(ModConfig.server.bettersurvival.tunnelingCancelable)
				return;
			
			onBreakAny(event);
		}
		
		public void onBreakAny(final BlockEvent.BreakEvent event)
		{
			if(disabled)
			{
				handler.invoke(event);
				return;
			}
			
			if(tunneling == null)
			{
				tunneling = getEnchantmentByName("tunneling");
				if(tunneling == null)
				{
					RLTweaker.logger.warn("Couldn't find tunneling enchantment");
					ErrorUtil.logSilent("Better Survival Missing Enchantment Tunneling");
					disabled = true;
					handler.invoke(event);
					return;
				}
			}
			
			EntityPlayer player = event.getPlayer();
			
			if(EnchantmentHelper.getMaxEnchantmentLevel(tunneling, player) == 0)
				return;
			
			World world = event.getWorld();

			//Has tunneling, run the block checks
			//Capping this out at 200...
			int tunnelingLevel = MathHelper.clamp(EnchantmentHelper.getMaxEnchantmentLevel(tunneling, player),0,200);
			
			//Checks each block that BetterSurvival would
			boolean tagNorth = player.getTags().contains("north");
			boolean tagEast = player.getTags().contains("east");
			boolean tagSouth = player.getTags().contains("south");
			boolean tagWest = player.getTags().contains("west");
			boolean tagUp = player.getTags().contains("up");
			boolean tagDown = player.getTags().contains("down");
			
			boolean isBlacklistEnabled = ModConfig.server.bettersurvival.tunnelingBlacklistEnabled;
			boolean isWhitelist = ModConfig.server.bettersurvival.tunnelingBlacklistIsWhitelist;
			String[] blacklist = ModConfig.server.bettersurvival.tunnelingBlacklist;
			
			//Loop as boxes depending on tag layout
			for(int x = -tunnelingLevel; x <= tunnelingLevel; x++)
			{
				if((!tagWest && !tagEast) || x == 0)
				{
					for(int y = -tunnelingLevel; y <= tunnelingLevel; y++)
					{
						if((!tagUp && !tagDown) || y == 0)
						{
							for(int z = -tunnelingLevel; z <= tunnelingLevel; z++)
							{
								if((!tagNorth && !tagSouth) || z == 0)
								{
									//Do fancy radius math
									//Optimizable, but to avoid any and all rounding or type issues does the same math as BetterSurvival
									boolean isInRadius = Math.sqrt(x * x + y * y + z * z) <= (tunnelingLevel + 1.0f) / 2.0f;
									boolean isNotCenter = !(x == 0 && y == 0 && z == 0);
									if(isInRadius && isNotCenter)
									{
										BlockPos newpos = event.getPos().add(x,y,z);
										
										if(!ModConfig.server.bettersurvival.tunnelingBreaksTileEntities)
										{
											//Check tile entity
											if(world.getTileEntity(newpos) != null)
												return;
										}
										
										//Check if the block is blacklisted
										Block block = world.getBlockState(newpos).getBlock();
										
										//TODO issues with whitelist behavior due to it picking up all sorts of blocks that are normally skipped
										//Skip air though for sure
										if(block == Blocks.AIR)
											continue;
										
										if(isBlacklistEnabled)
										{
											String blockRegistry = block.getRegistryName().toString();
											boolean inBlacklist = false;
											for(String entry : blacklist)
											{
												if(entry.equals(blockRegistry))
												{
													inBlacklist = true;
													break;
												}
											}
											
											if(inBlacklist != isWhitelist)
												return;
										}
									}
								}
							}
						}
					}
				}
			}
			
			//Passed all checks, run the original handler
			handler.invoke(event);
		}
	}
	
	@Nullable
	private Enchantment getEnchantmentByName(String name)
	{
		return Enchantment.REGISTRY.getObject(new ResourceLocation(ModNames.BETTERSURVIVAL, name));
	}
}
