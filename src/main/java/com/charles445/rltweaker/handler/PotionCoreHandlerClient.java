package com.charles445.rltweaker.handler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.PotionCoreClientReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ModNames;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PotionCoreHandlerClient
{
	boolean isOverpoweredArmorBarLoaded = Loader.isModLoaded(ModNames.OVERLOADEDARMORBAR);
	boolean isToughnessBarLoaded = Loader.isModLoaded(ModNames.TOUGHNESSBAR);
	
	PotionCoreClientReflect reflector;
	
	Minecraft mc = Minecraft.getMinecraft();
	
	@Nullable
	public OverloadedCompatibilityHandler overloadedHandler;
	
	public PotionCoreHandlerClient()
	{
		try
		{
			reflector = new PotionCoreClientReflect();
			
			//Wrap the pre GUI handler
			CompatUtil.wrapSpecificHandler("PCRenderOverlaysPre", PCRenderOverlaysPre::new, "com.tmtravlr.potioncore.PotionCoreEventHandlerClient", "renderOverlaysPre");
			
			//Wrap the post GUI handler
			CompatUtil.wrapSpecificHandler("PCRenderOverlaysPost", PCRenderOverlaysPost::new, "com.tmtravlr.potioncore.PotionCoreEventHandlerClient", "renderOverlaysPost");
			
			if(isOverpoweredArmorBarLoaded)
				overloadedHandler = new OverloadedCompatibilityHandler();
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
	
	@SideOnly(Side.CLIENT)
	public class OverloadedCompatibilityHandler
	{
		@Nullable
		public Field f_offset = null;
		
		public OverloadedCompatibilityHandler()
		{
			try
			{
				Class c_ModConfig = Class.forName("locusway.overpoweredarmorbar.ModConfig");
				f_offset = ReflectUtil.findField(c_ModConfig, "offset");
			}
			catch(Exception e)
			{
				RLTweaker.logger.error("Failed to setup OverloadedCompatibilityHandler ModConfig", e);
				ErrorUtil.logSilent("PotionCore Overloaded ModConfig");
			}
			
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		private boolean getOverloadedOffset() throws IllegalArgumentException, IllegalAccessException
		{
			if(f_offset != null)
				return f_offset.getBoolean(null);
				
			return false;
		}
		
		@SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOW)
		public void renderOverlaysAny(final RenderGameOverlayEvent event)
		{
			//Runs after overloaded armor bar
			if(event.getType() != RenderGameOverlayEvent.ElementType.ARMOR)
				return;
			
			//Armor
			
			if(ModConfig.client.potioncore.overloadedArmorBarCompatibility)
			{
				//Draw magic shielding if appropriate
				double magicShielding = mc.player.getEntityAttribute(reflector.MAGIC_SHIELDING).getAttributeValue();
				if(magicShielding > 0.0d)
				{
					int width = Math.min(82, MathHelper.floor(magicShielding * 4.0d + 2.0d));
					
					if(width == 82)
						width = 83;

					if(width > 2)
					{
						try
						{
							//Overloaded does an interesting routine to determine its Y position
							//It needs to be matched, otherwise weird things happen
							
							ScaledResolution scale = event.getResolution();
							int left = scale.getScaledWidth() / 2 - 92;
							int top = scale.getScaledHeight() - 40;
							
							boolean offset = this.getOverloadedOffset();
							
							float health = (float) mc.player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
							if(!offset && health > 20.0f)
								health = 20.0f;
							
							float absorption = MathHelper.ceil(mc.player.getAbsorptionAmount());
							
							if(!offset && absorption > 20.0f)
								absorption = 20.0f;
							
							int healthbars = (int) Math.ceil(health / 20.0f) + (int) Math.ceil(absorption / 20.0f);
							int bump = Math.max(12 - healthbars, 3);
							top = top - (healthbars - 1) * bump - 10;
							
							//whew
							
							//Get drawing
							GlStateManager.enableBlend();
							GlStateManager.pushMatrix();
							mc.getTextureManager().bindTexture(reflector.ARMOR_MAGIC_SHIELD);
							reflector.drawEnchantmentGlint(left, top, -120.0d, width, 11.0d);
							GlStateManager.popMatrix();
							GlStateManager.disableBlend();
							mc.getTextureManager().bindTexture(Gui.ICONS);
							//As always, fix the color glitch
							GlStateManager.color(1.0f, 1.0f, 1.0f);
						}
						catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
						{
							ErrorUtil.logSilent("PotionCore Overloaded Glint Invocation");
						}
					}
				}
				
				
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public class PCRenderOverlaysPre
	{
		private IEventListener handler;
		public PCRenderOverlaysPre(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void renderOverlaysPre(final RenderGameOverlayEvent.Pre event)
		{
			if(event.getType() != RenderGameOverlayEvent.ElementType.ARMOR)
			{
				handler.invoke(event);
				return;
			}
			
			//Armor
			
			if(ModConfig.client.potioncore.renderArmorResistance)
				handler.invoke(event);
		}
	}

	@SideOnly(Side.CLIENT)
	public class PCRenderOverlaysPost
	{
		private IEventListener handler;
		public PCRenderOverlaysPost(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void renderOverlaysPost(final RenderGameOverlayEvent.Post event)
		{
			if(event.getType() != RenderGameOverlayEvent.ElementType.ARMOR)
			{
				handler.invoke(event);
				return;
			}
			
			//Armor
			
			if(ModConfig.client.potioncore.renderArmorIcons)
			{
				handler.invoke(event);
				
				if(ModConfig.client.potioncore.magicShieldingHUDFix)
					GlStateManager.color(1.0f, 1.0f, 1.0f);
			}
		}
	}
}
