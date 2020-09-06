package com.charles445.rltweaker.asm.util;

import javax.annotation.Nullable;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class TransformUtil
{
	/** Search for a FieldInsnNode after the provided anchor. May specify multiple names for obfuscation purposes */
	@Nullable
	public static FieldInsnNode findNextFieldWithOpcodeAndName(AbstractInsnNode anchor, int opcode, String... names)
	{
		AbstractInsnNode search = anchor.getNext();
		
		while(search!=null)
		{
			if(search.getOpcode()==opcode)
			{
				for(String name : names)
				{
					if(name.equals(((FieldInsnNode)search).name))
					{
						return (FieldInsnNode)search;
					}
				}
			}
			search = search.getNext();
		}
		
		return null;
	}
	
	/** Search for a FieldInsnNode before the provided anchor. May specify multiple names for obfuscation purposes */
	@Nullable
	public static FieldInsnNode findPreviousFieldWithOpcodeAndName(AbstractInsnNode anchor, int opcode, String... names)
	{
		AbstractInsnNode search = anchor.getPrevious();
		
		while(search!=null)
		{
			if(search.getOpcode()==opcode)
			{
				for(String name : names)
				{
					if(name.equals(((FieldInsnNode)search).name))
					{
						return (FieldInsnNode)search;
					}
				}
			}
			search = search.getPrevious();
		}
		
		return null;
	}
	
	/** Search for an IntInsnNode after the provided anchor. */
	@Nullable
	public static IntInsnNode findNextIntInsnNodeWithValue(AbstractInsnNode anchor, int value)
	{
		AbstractInsnNode search = anchor.getNext();
		
		while(search!=null)
		{
			if(search.getType()==AbstractInsnNode.INT_INSN)
			{
				if(value==(((IntInsnNode)search).operand))
				{
					return (IntInsnNode) search;
				}
			}
			search = search.getNext();
		}
		
		return null;
	}
	
	/** Search for an IntInsnNode before the provided anchor. */
	@Nullable
	public static IntInsnNode findPreviousIntInsnNodeWithValue(AbstractInsnNode anchor, int value)
	{
		AbstractInsnNode search = anchor.getPrevious();
		
		while(search!=null)
		{
			if(search.getType()==AbstractInsnNode.INT_INSN)
			{
				if(value==(((IntInsnNode)search).operand))
				{
					return (IntInsnNode) search;
				}
			}
			search = search.getPrevious();
		}
		
		return null;
	}

	/** Search for a MethodInsnNode after the provided anchor. May specify multiple names for obfuscation purposes */
	@Nullable
	public static MethodInsnNode findNextCallWithOpcodeAndName(AbstractInsnNode anchor, int opcode, String... names)
	{
		AbstractInsnNode search = anchor.getNext();
			
		while(search!=null)
		{
			if(search.getOpcode()==opcode)
			{
				for(String name : names)
				{
					if(name.equals(((MethodInsnNode)search).name))
					{
						return (MethodInsnNode)search;
					}
				}
			}
			search = search.getNext();
		}
		
		return null;
	}
	
	/** Search for a MethodInsnNode before the provided anchor. May specify multiple names for obfuscation purposes */
	@Nullable
	public static MethodInsnNode findPreviousCallWithOpcodeAndName(AbstractInsnNode anchor, int opcode, String... names)
	{
		AbstractInsnNode search = anchor.getPrevious();
			
		while(search!=null)
		{
			if(search.getOpcode()==opcode)
			{
				for(String name : names)
				{
					if(name.equals(((MethodInsnNode)search).name))
					{
						return (MethodInsnNode)search;
					}
				}
			}
			search = search.getPrevious();
		}
		
		return null;
	}
	
	
}
