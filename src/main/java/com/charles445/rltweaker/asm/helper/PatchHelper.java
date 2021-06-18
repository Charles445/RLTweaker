package com.charles445.rltweaker.asm.helper;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nullable;

public abstract class PatchHelper
{
	private PatchHelper() {}
	
	//Utility
	
	public static void announce(String s)
	{
		System.out.println("RLTweakerASM: "+s);
	}
	
	//FIND FIRST METHOD (by string)
	
	/**
	 * Finds the first method with the matching choice of names
	 * @param classNode
	 * @param methodNames
	 * @return
	 */
	@Nullable
	public static MethodNode findMethod(ClassNode classNode, String... methodNames)
	{
		for(MethodNode m : classNode.methods)
		{
			for(String methodName : methodNames)
			{
				if(m.name.equals(methodName))
					return m;
			}
		}
		
		return null;
	}
	
	@Nullable
	public static MethodNode findMethodWithDesc(ClassNode classNode, String desc, String... methodNames)
	{
		for(MethodNode m : classNode.methods)
		{
			if(m.desc.equals(desc))
			{
				for(String methodName : methodNames)
				{
					if(m.name.equals(methodName))
						return m;
				}
			}
		}
		
		return null;
	}
	
	//FIRST
	@Nullable
	public static AbstractInsnNode first(MethodNode methodNode)
	{
		return ASMHelper.findFirstInstruction(methodNode);
	}
	
	//LAST
	@Nullable
	public static AbstractInsnNode last(MethodNode methodNode)
	{
		return ASMHelper.getOrFindInstruction(methodNode.instructions.getLast(), true);
	}
	
	//NEXT
	@Nullable
	public static AbstractInsnNode next(AbstractInsnNode node)
	{
		return node.getNext();
	}
	
	@Nullable
	public static AbstractInsnNode next(AbstractInsnNode node, int count)
	{
		AbstractInsnNode anchor = node;
		for(int i=0;i<count;i++)
		{
			anchor = anchor.getNext();
			if(anchor==null)
				return anchor;
		}
		
		return anchor;
	}
	
	@Nullable
	public static AbstractInsnNode nextInsn(AbstractInsnNode node)
	{
		return ASMHelper.findNextInstruction(node);
	}
	
	@Nullable
	public static AbstractInsnNode nextInsn(AbstractInsnNode node, int count)
	{
		AbstractInsnNode anchor = node;
		for(int i=0;i<count;i++)
		{
			anchor = ASMHelper.findNextInstruction(node);
			if(anchor==null)
				return anchor;
		}
		
		return anchor;
	}
	
	//PREVIOUS
	@Nullable
	public static AbstractInsnNode previous(AbstractInsnNode node)
	{
		return node.getPrevious();
	}
	
	@Nullable
	public static AbstractInsnNode previous(AbstractInsnNode node, int count)
	{
		AbstractInsnNode anchor = node;
		for(int i=0;i<count;i++)
		{
			anchor = anchor.getPrevious();
			if(anchor==null)
				return anchor;
		}
		
		return anchor;
	}
	
	@Nullable
	public static AbstractInsnNode previousInsn(AbstractInsnNode node)
	{
		return ASMHelper.findPreviousInstruction(node);
	}
	
	@Nullable
	public static AbstractInsnNode previousInsn(AbstractInsnNode node, int count)
	{
		AbstractInsnNode anchor = node;
		for(int i=0;i<count;i++)
		{
			anchor = ASMHelper.findPreviousInstruction(node);
			if(anchor==null)
				return anchor;
		}
		
		return anchor;
	}
	
	public static InsnList wrap(AbstractInsnNode... nodes)
	{
		InsnList wrapper = new InsnList();
		for(AbstractInsnNode node : nodes)
		{
			wrapper.add(node);
		}
		return wrapper;
	}
}
