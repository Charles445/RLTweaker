package com.charles445.rltweaker.asm.patch;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.ASMLogger;
import com.charles445.rltweaker.asm.util.ModTransformer;

public abstract class Patch implements IPatch
{
	public static Set<String> loadedTransformers = ConcurrentHashMap.newKeySet();
	
	
	protected String target;
	protected int flags;
	protected IPatchManager manager;
	protected boolean cancelled;
	
	public Patch(IPatchManager manager, String target, int flags)
	{
		this.manager = manager;
		this.target = target;
		this.flags = flags;
		this.cancelled = false;
	}
	
	public boolean hasModTransformer(ModTransformer mod)
	{
		String search = mod.getTransformerClassName();
		
		for(String transformer : loadedTransformers)
		{
			if(transformer.contains(search))
				return true;
		}
		
		return false;
	}
	
	@Override
	public String getTargetClazz()
	{
		return target;
	}

	@Override
	public int getFlags()
	{
		return flags;
	}
	
	@Override
	public IPatchManager getPatchManager()
	{
		return manager;
	}
	
	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public abstract void patch(ClassNode clazzNode);
	
	//Utility
	
	protected void announce(String s)
	{
		ASMLogger.info(s);
	}
	
	//FIND FIRST METHOD (by string)
	
	/**
	 * Finds the first method with the matching choice of names
	 * @param classNode
	 * @param methodName
	 * @return
	 */
	@Nullable
	protected MethodNode findMethod(ClassNode classNode, String... methodNames)
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
	protected MethodNode findMethodWithDesc(ClassNode classNode, String desc, String... methodNames)
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
	protected AbstractInsnNode first(MethodNode methodNode)
	{
		return ASMHelper.findFirstInstruction(methodNode);
	}
	
	//LAST
	@Nullable
	protected AbstractInsnNode last(MethodNode methodNode)
	{
		return ASMHelper.getOrFindInstruction(methodNode.instructions.getLast(), true);
	}
	
	//NEXT
	@Nullable
	protected AbstractInsnNode next(AbstractInsnNode node)
	{
		return node.getNext();
	}
	
	@Nullable
	protected AbstractInsnNode next(AbstractInsnNode node, int count)
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
	protected AbstractInsnNode nextInsn(AbstractInsnNode node)
	{
		return ASMHelper.findNextInstruction(node);
	}
	
	@Nullable
	protected AbstractInsnNode nextInsn(AbstractInsnNode node, int count)
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
	protected AbstractInsnNode previous(AbstractInsnNode node)
	{
		return node.getPrevious();
	}
	
	@Nullable
	protected AbstractInsnNode previous(AbstractInsnNode node, int count)
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
	protected AbstractInsnNode previousInsn(AbstractInsnNode node)
	{
		return ASMHelper.findPreviousInstruction(node);
	}
	
	@Nullable
	protected AbstractInsnNode previousInsn(AbstractInsnNode node, int count)
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
	
	protected InsnList wrap(AbstractInsnNode... nodes)
	{
		InsnList wrapper = new InsnList();
		for(AbstractInsnNode node : nodes)
		{
			wrapper.add(node);
		}
		return wrapper;
	}
	
	protected void removePreviousInsn(MethodNode m, AbstractInsnNode node)
	{
		AbstractInsnNode toRemove = previousInsn(node);
		if(toRemove != null)
			m.instructions.remove(toRemove);
	}
	
	protected void removeNextInsn(MethodNode m, AbstractInsnNode node)
	{
		AbstractInsnNode toRemove = nextInsn(node);
		if(toRemove != null)
			m.instructions.remove(toRemove);
	}
	
	/** Inserts a list after a node in a method **/
	protected void insert(MethodNode methodNode, AbstractInsnNode anchor, InsnList wrapOrList)
	{
		methodNode.instructions.insert(anchor, wrapOrList);
	}
	
	/** Inserts a list before a node in a method **/
	protected void insertBefore(MethodNode methodNode, AbstractInsnNode anchor, InsnList wrapOrList)
	{
		methodNode.instructions.insertBefore(anchor, wrapOrList);
	}
	
	/** Inserts a node after a node in a method **/
	protected void insert(MethodNode methodNode, AbstractInsnNode anchor, AbstractInsnNode added)
	{
		methodNode.instructions.insert(anchor, added);
	}
	
	/** Inserts a node before a node in a method **/
	protected void insertBefore(MethodNode methodNode, AbstractInsnNode anchor, AbstractInsnNode added)
	{
		methodNode.instructions.insertBefore(anchor, added);
	}
	
	/** Creates an InsnNode after a node in a method **/
	protected void insertInsn(MethodNode methodNode, AbstractInsnNode anchor, int opcode)
	{
		methodNode.instructions.insert(anchor, new InsnNode(opcode));
	}
	
	/** Creates an InsnNode before a node in a method **/
	protected void insertInsnBefore(MethodNode methodNode, AbstractInsnNode anchor, int opcode)
	{
		methodNode.instructions.insertBefore(anchor, new InsnNode(opcode));
	}
	
	/** Whether the method references a specific owner at any point, for compatibility purposes **/
	protected boolean methodReferencesOwner(MethodNode m, String owner)
	{
		AbstractInsnNode node = m.instructions.getFirst();
		while(node != null)
		{
			if(node.getType() == AbstractInsnNode.METHOD_INSN)
			{
				if(((MethodInsnNode)node).owner.equals(owner))
					return true;
			}
			else if(node.getType() == AbstractInsnNode.FIELD_INSN)
			{
				if(((FieldInsnNode)node).owner.equals(owner))
					return true;
			}
			node = node.getNext();
		}
		
		return false;
	}
}
