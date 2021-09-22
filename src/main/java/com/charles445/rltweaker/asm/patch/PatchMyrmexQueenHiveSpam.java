package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchMyrmexQueenHiveSpam extends PatchManager
{
	public PatchMyrmexQueenHiveSpam()
	{
		super("Myrmex Queen Hive Spam");
		
		add(new Patch(this, "com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES)
		{
			@Override
			public void patch(ClassNode clazzNode)
			{
				MethodNode m_onLivingUpdate = findMethod(clazzNode, "func_70636_d", "onLivingUpdate");
				
				if(m_onLivingUpdate == null)
					throw new RuntimeException("Couldn't find func_70636_d or onLivingUpdate");
				
				AbstractInsnNode anchor = TransformUtil.findNextFieldWithOpcodeAndName(first(m_onLivingUpdate), Opcodes.PUTFIELD, "eggTicks");
				if(anchor == null)
					throw new RuntimeException("Couldn't find eggTicks in onLivingUpdate");
				
				MethodInsnNode canSeeSkyCall = TransformUtil.findNextCallWithOpcodeAndName(anchor, Opcodes.INVOKEVIRTUAL, "canSeeSky");
				
				if(canSeeSkyCall == null)
					throw new RuntimeException("Couldn't find canSeeSky after eggTicks in onLivingUpdate");
				
				anchor = ASMHelper.findNextInstruction(canSeeSkyCall);
				if(anchor.getType() != AbstractInsnNode.JUMP_INSN)
					throw new RuntimeException("Node after canSeeSky was not a jump instruction");
				
				JumpInsnNode canSeeJump = (JumpInsnNode)anchor;
				LabelNode l_quit = canSeeJump.label;
				
				
				InsnList newCheck = new InsnList();
				newCheck.add(new VarInsnNode(Opcodes.ALOAD, 0)); //this
				newCheck.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/github/alexthe666/iceandfire/entity/EntityMyrmexQueen", "hasMadeHome", "()Z", false)); //hasMadeHome
				newCheck.add(new JumpInsnNode(Opcodes.IFNE, l_quit)); //if false jump
				//Essentially
				//&& !this.hasMadeHome
				//In this context
				
				//Insert after the other jump
				m_onLivingUpdate.instructions.insert(canSeeJump, newCheck);
				
				
				
				//ClassDisplayer.instance.printMethod(m_onLivingUpdate);
			}
			
		});
	}
}
