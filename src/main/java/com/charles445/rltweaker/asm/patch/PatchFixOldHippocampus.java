package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchFixOldHippocampus extends PatchManager
{
	public PatchFixOldHippocampus()
	{
		super("Old Hippocampus");
		
		add(new Patch(this, "com.github.alexthe666.iceandfire.entity.EntityHippocampus", ClassWriter.COMPUTE_MAXS)
		{
			@Override
			public void patch(ClassNode clazzNode)
			{
				MethodNode m_openGUI = this.findMethod(clazzNode, "openGUI");
				if(m_openGUI == null)
				{
					//We're not erroring, as there's a lot of versions of IAF with different interactions
					this.cancelled = true;
					announce("Couldn't find openGUI in EntityHippocampus, skipping");
					return;
				}
				
				//We're here, but we need to make sure this version is bugged before doing any transformation
				//The telltale sign that the function is bugged is the initial world remote check
				//If it checks for remote, then it's bugged
				
				//Find the remote node
				AbstractInsnNode anchor = TransformUtil.findNextFieldWithOpcodeAndName(first(m_openGUI), Opcodes.GETFIELD, "field_72995_K", "isRemote");
				if(anchor == null)
				{
					this.cancelled = true;
					announce("Couldn't find isRemote in EntityHippocampus, skipping");
					return;
				}
				
				//Grab the next node
				anchor = nextInsn(anchor);
				if(anchor == null)
				{
					this.cancelled = true;
					announce("Unexpected end of method in EntityHippocampus, skipping");
					return;
				}
				
				//Check if it's a jump node
				if(anchor.getType() != AbstractInsnNode.JUMP_INSN)
				{
					this.cancelled = true;
					announce("Remote check was not a jump, skipping");
					return;
				}
				
				//Check what kind of jump it is
				JumpInsnNode remoteJump = (JumpInsnNode)anchor;
				if(remoteJump.getOpcode() != Opcodes.IFEQ)
				{
					this.cancelled = true;
					announce("Remote check was server, skipping");
					return;
				}
				
				//Now we know the method is outdated
				remoteJump.setOpcode(Opcodes.IFNE); //!world.isRemote
				
				//Outdated version also needs passenger changed
				
				anchor = TransformUtil.findNextCallWithOpcodeAndName(remoteJump, Opcodes.INVOKEVIRTUAL, "func_184196_w", "isPassenger");
				if(anchor == null)
				{
					//Okay, now we're complaining
					throw new RuntimeException("Couldn't find func_184196_w or isPassenger in old EntityHippocampus");
				}
				
				anchor = nextInsn(anchor);
				if(anchor == null)
				{
					throw new RuntimeException("Unexpected critical end of method in EntityHippocampus");
				}
				
				if(anchor.getType() != AbstractInsnNode.JUMP_INSN)
				{
					throw new RuntimeException("Couldn't find branch after isPassenger in old EntityHippocampus");
				}
				
				JumpInsnNode passengerJump = (JumpInsnNode) anchor;
				if(passengerJump.getOpcode() != Opcodes.IFNE)
				{
					throw new RuntimeException("Branch for isPassenger is already IFNE in old EntityHippocampus, somehow. Something has gone wrong.");
				}
				
				passengerJump.setOpcode(Opcodes.IFEQ); //this.isPassenger(playerEntity)
			}
		});
	}
}
