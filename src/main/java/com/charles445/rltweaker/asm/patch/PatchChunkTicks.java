package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchChunkTicks extends PatchManager
{
	public PatchChunkTicks()
	{
		super("Chunk Ticks");
		
		add(new Patch(this, "net.minecraft.world.WorldServer", ClassWriter.COMPUTE_MAXS)
		{
			@Override
			public void patch(ClassNode clazzNode)
			{
				if(true)
				{
					String rlChunkTickContainer_name = "rlChunkTickContainer";
					String rlChunkTickContainer_desc = "Lcom/charles445/rltweaker/hook/HookWorld$ChunkTickContainer;";
					
					FieldNode f_rlChunkTickContainer = new FieldNode(
							Opcodes.ASM5,
							Opcodes.ACC_PUBLIC,
							rlChunkTickContainer_name,
							rlChunkTickContainer_desc,
							null,
							null
					);
					clazzNode.fields.add(f_rlChunkTickContainer);
					
					MethodNode m_updateBlocks = this.findMethod(clazzNode, "func_147456_g", "updateBlocks"); 
					
					if(m_updateBlocks == null)
						throw new RuntimeException("Couldn't find func_147456_g or updateBlocks");
					
					if(true)
					{
						InsnList inject = new InsnList();
						inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
						inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
						inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, 
								"com/charles445/rltweaker/hook/HookWorld",
								"onPreUpdateBlocks",
								"(Lnet/minecraft/world/WorldServer;)Lcom/charles445/rltweaker/hook/HookWorld$ChunkTickContainer;",
								false
						));
						inject.add(new FieldInsnNode(Opcodes.PUTFIELD,
								"net/minecraft/world/WorldServer",
								rlChunkTickContainer_name,
								rlChunkTickContainer_desc
						));
						this.insert(m_updateBlocks, first(m_updateBlocks), inject);
					}
					
					//Step past debug blocks and grab the chunk local variable
					AbstractInsnNode anchor = TransformUtil.findNextCallWithOpcodeAndName(first(m_updateBlocks), Opcodes.INVOKEVIRTUAL, "func_76594_o", "enqueueRelightChecks");
					if(anchor == null)
						throw new RuntimeException("Couldn't find call to func_76594_o or enqueueRelightChecks in updateBlocks");
					
					//Grab the VarInsnNode that corresponds to chunk (the parameter for enqueueRelightChecks)
					anchor = ASMHelper.findPreviousInstruction(anchor);
					if(!(anchor instanceof VarInsnNode))
						throw new RuntimeException("Previous instruction to enqueueRelightChecks was unexpectedly not a VarInsnNode");
					
					int lvn_chunk_var = ((VarInsnNode)anchor).var;
					
					anchor = TransformUtil.findNextCallWithOpcodeAndName(anchor, Opcodes.INVOKEVIRTUAL, "func_150804_b", "onTick");
					
					if(anchor == null)
						throw new RuntimeException("Couldn't find call to func_150804_b or onTick after enqueueRelightChecks in updateBlocks");
					
					if(true)
					{
						InsnList inject = new InsnList();
						inject.add(new VarInsnNode(Opcodes.ALOAD, lvn_chunk_var));
						inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
						inject.add(new FieldInsnNode(Opcodes.GETFIELD,
								"net/minecraft/world/WorldServer",
								rlChunkTickContainer_name,
								rlChunkTickContainer_desc
						));
						inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, 
								"com/charles445/rltweaker/hook/HookWorld",
								"postBlockTickChunk",
								"(Lnet/minecraft/world/chunk/Chunk;Lcom/charles445/rltweaker/hook/HookWorld$ChunkTickContainer;)V",
								false
						));
						this.insert(m_updateBlocks, anchor, inject);
					}
				}
			}
		});
	}
}
