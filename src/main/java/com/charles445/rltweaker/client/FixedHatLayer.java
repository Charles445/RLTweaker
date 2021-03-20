package com.charles445.rltweaker.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//TODO move this to be a handler or something to stay organized

@SideOnly(Side.CLIENT)
public class FixedHatLayer implements LayerRenderer<EntityLivingBase>
{
	protected final RenderLivingBase<?> livingEntityRenderer;
	
	public static Class c_CapabilityHatContainer;
	public static Field f_CapabilityHatContainer_Companion; //KOTLIN is WEIRD
	public static Object o_CapabilityHatContainer_Companion; //Instance variable for CapabilityHatContainer$Companion
	
	public static Class c_Companion;
	public static Method m_Companion_getCapability;
	
	public static Class c_IHatContainer;
	public static Method m_IHatContainer_getEquipped;
	
	public static Field f_RenderLivingBase_layerRenderers;
	
	static boolean loaded = false;
	
	static
	{
		try
		{
			//Vanilla classes
			f_RenderLivingBase_layerRenderers = ReflectUtil.findFieldAny(RenderLivingBase.class, "field_177097_h", "layerRenderers");
			
			//Mod Classes
			c_IHatContainer = Class.forName("wiresegal.classyhats.capability.data.IHatContainer");
			
			c_CapabilityHatContainer = Class.forName("wiresegal.classyhats.capability.CapabilityHatContainer");
			f_CapabilityHatContainer_Companion = ReflectUtil.findField(c_CapabilityHatContainer, "Companion");
			o_CapabilityHatContainer_Companion = f_CapabilityHatContainer_Companion.get(null);
			
			c_Companion = Class.forName("wiresegal.classyhats.capability.CapabilityHatContainer$Companion");
			m_Companion_getCapability = ReflectUtil.findMethod(c_Companion, "getCapability");
			m_IHatContainer_getEquipped = ReflectUtil.findMethod(c_IHatContainer, "getEquipped");
			
			
			loaded = true;
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to reflect in FixedHatLayer!",e);
			//Not thread safe...
			//ErrorUtil.logSilent("FixedHatLayer Reflection");
			//TODO threadsafe error reporting
			loaded = false;
		}
	}
	
	public FixedHatLayer(RenderLivingBase<?> livingEntityRendererIn)
	{
		this.livingEntityRenderer = livingEntityRendererIn;
	}
	
	public static void init()
	{
		//Remove old layers
		if(FixedHatLayer.isLoaded())
			FixedHatLayer.removeOldLayers();
		
		//Now add all the custom layers if it's still loaded
		if(FixedHatLayer.isLoaded())
		{
			for(RenderPlayer renderer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values())
			{
				renderer.addLayer(new FixedHatLayer(renderer));
			}
		}
	}
	
	public static boolean isLoaded()
	{
		return loaded;
	}
	
	public static void removeOldLayers()
	{
		for(RenderPlayer renderer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values())
		{
			try
			{
				List<LayerRenderer<EntityLivingBase>> layerRenderers = (List<LayerRenderer<EntityLivingBase>>)f_RenderLivingBase_layerRenderers.get(renderer);
				
				Iterator<LayerRenderer<EntityLivingBase>> it = layerRenderers.iterator();
				
				while(it.hasNext())
				{
					LayerRenderer<EntityLivingBase> layer = it.next();
					if(layer.toString().contains("wiresegal.classyhats.client.render.PlayerLayerRendererHat"))
					{
						it.remove();
						RLTweaker.logger.debug("Removed hat layer");
					}
				}
			}
			catch(Exception e)
			{
				//Failed to remove old layers, unload
				RLTweaker.logger.error("Failed to remove old layers in FixedHatLayer!",e);
				loaded = false;
				//TODO proper threadsafe logging
				return;
			}
		}
		
	}
	
	//IHatContainer
	@Nullable
	private Object getCapability(EntityPlayer player)
	{
		try
		{
			//Use the instance variable to call, as it is a 'companion' class and not static
			return m_Companion_getCapability.invoke(o_CapabilityHatContainer_Companion, player);
		}
		catch(Exception e)
		{
			//Just be quiet
			//TODO threadsafe error reporting
			return null;
		}
	}
	
	@Nullable
	private Object getEquipped(Object container)
	{
		try
		{
			return m_IHatContainer_getEquipped.invoke(container);
		}
		catch(Exception e)
		{
			//Just be quiet
			//TODO threadsafe error reporting
			return null;
		}
	}

	@Override
	public void doRenderLayer(EntityLivingBase player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		//Hopefully two invokes per hat every frame doesn't cause a problem.
		
		if(!FixedHatLayer.isLoaded())
			return;
		
		if(!(player instanceof EntityPlayer))
			return;
		
		Object container = getCapability((EntityPlayer) player);
		
		if(container==null)
			return;
		
		Object stackObj = getEquipped(container);
		
		if(!(stackObj instanceof ItemStack))
			return;
		
		//-- This block is by fonnymunkey
		ItemStack stack = (ItemStack)stackObj;
		if(!stack.isEmpty())
		{
			GlStateManager.pushMatrix();
			
			ModelBase modelBase = this.livingEntityRenderer.getMainModel();
			
			if(modelBase instanceof ModelBiped)
			{
				ModelRenderer bipedHead = ((ModelBiped) modelBase).bipedHead;
				
				if(player.isSneaking())
				{
					GlStateManager.translate(0.0f, 0.2f, 0.0f);
				}
				
				bipedHead.postRender(0.0625f);
				
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				GlStateManager.translate(0.0f, -0.25f, 0.0f);
				GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
				GlStateManager.scale(0.625f, -0.625f, -0.625f);
				
				Minecraft.getMinecraft().getItemRenderer().renderItem(player, stack, ItemCameraTransforms.TransformType.HEAD);
				
				GlStateManager.popMatrix();
				//bipedBody.postRender(scale);
			}
		}
		//--
	}
	@Override
	public boolean shouldCombineTextures()
	{
		return false;
	}
}
