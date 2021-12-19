package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FBPClientReflect
{
	public final Class c_FBP;
	public final Object o_FBP_INSTANCE;
	public final Block o_FBPBlock;
	
	public final Class c_FBPEventHandler;
	public final Object o_FBPEventHandler_eventHandler;
	public final Method m_FBPEventHandler_getNodeWithPos;
	
	public final Class c_FBPAnimationDummyBlock;
	public final ConcurrentHashMap<BlockPos, Object> o_FBPAnimationDummyBlock_blockNodes;
	
	public final Class c_BlockNode;
	public final Field f_BlockNode_state;
	public final Field f_BlockNode_originalBlock;
	public final Field f_BlockNode_meta;
	public final Field f_BlockNode_particle;
	
	public final Class c_FBPParticleBlock;
	public final Field f_FBPParticleBlock_block;
	public final Field f_FBPParticleBlock_blockState;
	public final Field f_FBPParticleBlock_modelPrefab;
	public final Field f_FBPParticleBlock_tileEntity;
	
	public final Field f_Particle_isExpired;
	
	public FBPClientReflect() throws Exception
	{
		c_FBP = Class.forName("com.TominoCZ.FBP.FBP");
		o_FBP_INSTANCE = ReflectUtil.findField(c_FBP, "INSTANCE").get(null);
		o_FBPBlock = (Block) ReflectUtil.findField(c_FBP, "FBPBlock").get(null);
		o_FBPEventHandler_eventHandler = ReflectUtil.findField(c_FBP, "eventHandler").get(o_FBP_INSTANCE);
		
		c_FBPEventHandler = Class.forName("com.TominoCZ.FBP.handler.FBPEventHandler");
		m_FBPEventHandler_getNodeWithPos = ReflectUtil.findMethod(c_FBPEventHandler, "getNodeWithPos");
		
		c_FBPAnimationDummyBlock = Class.forName("com.TominoCZ.FBP.block.FBPAnimationDummyBlock");
		o_FBPAnimationDummyBlock_blockNodes = (ConcurrentHashMap<BlockPos, Object>) ReflectUtil.findField(c_FBPAnimationDummyBlock, "blockNodes").get(o_FBPBlock);
		
		c_BlockNode = Class.forName("com.TominoCZ.FBP.node.BlockNode");
		f_BlockNode_state = ReflectUtil.findField(c_BlockNode, "state");
		f_BlockNode_originalBlock = ReflectUtil.findField(c_BlockNode, "originalBlock");
		f_BlockNode_meta = ReflectUtil.findField(c_BlockNode, "meta");
		f_BlockNode_particle = ReflectUtil.findField(c_BlockNode, "particle");
		
		c_FBPParticleBlock = Class.forName("com.TominoCZ.FBP.particle.FBPParticleBlock");
		f_FBPParticleBlock_block = ReflectUtil.findField(c_FBPParticleBlock, "block");
		f_FBPParticleBlock_blockState = ReflectUtil.findField(c_FBPParticleBlock, "blockState");
		f_FBPParticleBlock_modelPrefab = ReflectUtil.findField(c_FBPParticleBlock, "modelPrefab");
		f_FBPParticleBlock_tileEntity = ReflectUtil.findField(c_FBPParticleBlock, "tileEntity");
		
		f_Particle_isExpired = ReflectUtil.findFieldAny(Particle.class, "field_187133_m", "isExpired");
	}
	
	@Nullable
	public Object getBlockNode(BlockPos pos)
	{
		return o_FBPAnimationDummyBlock_blockNodes.get(pos);
	}
	
	@Nullable
	public Object getBlockPosNode(BlockPos pos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_FBPEventHandler_getNodeWithPos.invoke(o_FBPEventHandler_eventHandler, pos);
	}
	
	public void setBlockNodeToAir(Object blockNode) throws IllegalArgumentException, IllegalAccessException
	{
		Block block = Blocks.AIR;
		
		f_BlockNode_state.set(blockNode, Blocks.AIR.getDefaultState());
		f_BlockNode_originalBlock.set(blockNode, Blocks.AIR);
		f_BlockNode_meta.setInt(blockNode, -1);
		setParticleBlockToAir((Particle) f_BlockNode_particle.get(blockNode));
	}
	
	public void setParticleBlockToAir(Particle particle) throws IllegalArgumentException, IllegalAccessException
	{
		if(particle == null)
			return;
		
		f_FBPParticleBlock_block.set(particle, Blocks.AIR);
		f_FBPParticleBlock_blockState.set(particle, Blocks.AIR.getDefaultState());
		f_FBPParticleBlock_modelPrefab.set(particle, Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(Blocks.AIR.getDefaultState()));
		f_FBPParticleBlock_tileEntity.set(particle, null);
		f_Particle_isExpired.setBoolean(particle, true);
	}
}
