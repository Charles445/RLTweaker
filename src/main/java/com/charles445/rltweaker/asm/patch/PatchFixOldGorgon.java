package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchFixOldGorgon extends PatchManager
{
	public PatchFixOldGorgon()
	{
		super("Old Gorgon");
		
		add(new Patch(this, "com.github.alexthe666.iceandfire.entity.EntityGorgon", ClassWriter.COMPUTE_MAXS)
		{
			@Override
			public void patch(ClassNode clazzNode)
			{
				MethodNode m_onLivingUpdate = this.findMethod(clazzNode, "func_70636_d", "onLivingUpdate");
				if(m_onLivingUpdate == null)
				{
					//We're not erroring, as there's a lot of versions of IAF with different interactions
					this.cancelled = true;
					announce("Couldn't find func_70636_d or onLivingUpdate in EntityGorgon, skipping");
					return;
				}
				
				//ClassDisplayer.instance.printMethodLocalVariables(m_onLivingUpdate);
				//ClassDisplayer.instance.printMethod(m_onLivingUpdate);
				
				//Find the first case of GORGON_ATTACK being used
				//Also check to make sure it's an older version, just to be safer
				FieldInsnNode soundField = TransformUtil.findNextFieldWithOpcodeAndName(first(m_onLivingUpdate), Opcodes.GETSTATIC, "GORGON_ATTACK");
				
				if(soundField == null)
				{
					this.cancelled = true;
					announce("Couldn't find any GORGON_ATTACK in EntityGorgon, skipping");
					return;
				}
				
				if(!soundField.owner.equals("com/github/alexthe666/iceandfire/core/ModSounds"))
				{
					this.cancelled = true;
					announce("GORGON_ATTACK was not owned by ModSounds in onLivingUpdate in EntityGorgon, skipping");
					return;
				}
				
				//Travel to the instantiation of the statue
				AbstractInsnNode anchor = ASMHelper.findNextInstructionWithOpcode(soundField, Opcodes.NEW);
				if(anchor instanceof TypeInsnNode)
				{
					if(!((TypeInsnNode)anchor).desc.equals("com/github/alexthe666/iceandfire/entity/EntityStoneStatue"))
					{
						this.cancelled = true;
						announce("First instantiation found was not EntityStoneStatue in onLivingUpdate in EntityGorgon, patcher is confused, skipping");
						return;
					}
				}
				else
				{
					this.cancelled = true;
					announce("Couldn't find an instantiation at all in onLivingUpdate in EntityGorgon, skipping");
					return;
				}
				
				//Anchor has NEW EntityStoneStatue
				//We can be pretty confident that this is going well
				
				//Create a new field for the gorgon
				String fName_rltweakerGorgonDelay = "rltweakerGorgonDelay";
				FieldNode fNode_rltweakerGorgonDelay = new FieldNode(Opcodes.ACC_PUBLIC, fName_rltweakerGorgonDelay, "I", null, null);
				clazzNode.fields.add(fNode_rltweakerGorgonDelay);
				
				//Decrementer
				if(true)
				{
					AbstractInsnNode huntSuper = TransformUtil.findNextCallWithOpcodeAndName(first(m_onLivingUpdate), Opcodes.INVOKESPECIAL, "func_70636_d", "onLivingUpdate");
					if(huntSuper == null)
						throw new RuntimeException("Couldn't find super call in onLivingUpdate in EntityGorgon");
					
					InsnList inject = new InsnList();
					inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
					inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
					inject.add(new FieldInsnNode(Opcodes.GETFIELD, "com/github/alexthe666/iceandfire/entity/EntityGorgon", fName_rltweakerGorgonDelay, "I"));
					inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookIAF", "decrementToZero", "(I)I", false));
					inject.add(new FieldInsnNode(Opcodes.PUTFIELD, "com/github/alexthe666/iceandfire/entity/EntityGorgon", fName_rltweakerGorgonDelay, "I"));
					this.insert(m_onLivingUpdate, huntSuper, inject);
				}
				
				//Time to tear things up
				anchor = TransformUtil.findNextCallWithOpcodeAndName(anchor, Opcodes.INVOKEVIRTUAL, "func_72838_d", "spawnEntity");
				if(anchor == null)
					throw new RuntimeException("Couldn't find func_72838_d or spawnEntity in onLivingUpdate in EntityGorgon");
				
				anchor = next(anchor, 2); //Move past the pop and land on the label
				//Remove the entire world.spawnEntity(statue) call (load, get, load, invoke, pop)
				this.removePreviousInsn(m_onLivingUpdate, anchor);
				this.removePreviousInsn(m_onLivingUpdate, anchor);
				this.removePreviousInsn(m_onLivingUpdate, anchor);
				this.removePreviousInsn(m_onLivingUpdate, anchor);
				this.removePreviousInsn(m_onLivingUpdate, anchor);
				
				//Save this position for later
				AbstractInsnNode hookLocation = anchor;
				
				anchor = TransformUtil.findNextCallWithOpcodeAndName(anchor, Opcodes.INVOKEVIRTUAL, "func_70106_y", "setDead");
				if(anchor == null)
					throw new RuntimeException("Couldn't find func_70106_y or setDead in onLivingUpdate in EntityGorgon");
				anchor = anchor.getNext(); //Step onto the label
				//Remove the entire this.getAttackTarget().setDead() call (load, invoke, invoke)
				this.removePreviousInsn(m_onLivingUpdate, anchor);
				this.removePreviousInsn(m_onLivingUpdate, anchor);
				this.removePreviousInsn(m_onLivingUpdate, anchor);
				
				//We now have an empty !world.isRemote block with an inject anchor (hookLocation)
				//Now remove the damaging part
				
				anchor = TransformUtil.findNextFieldWithOpcodeAndName(anchor, Opcodes.GETSTATIC, "gorgon");
				if(anchor == null)
					throw new RuntimeException("Couldn't find getstatic for gorgon in onLivingUpdate in EntityGorgon");
				
				anchor = next(anchor, 4); //Onto the label again
				//Remove the entire this.getAttackTarget().attackEntityFrom(IceAndFire.gorgon, Integer.MAX_VALUE) (load, invoke, get, ldc, invoke, pop)
				this.removePreviousInsn(m_onLivingUpdate, anchor);
				this.removePreviousInsn(m_onLivingUpdate, anchor);
				this.removePreviousInsn(m_onLivingUpdate, anchor);
				this.removePreviousInsn(m_onLivingUpdate, anchor);
				this.removePreviousInsn(m_onLivingUpdate, anchor);
				this.removePreviousInsn(m_onLivingUpdate, anchor);
				
				//Keep going and remove all the statue angle calls (five of them: load, load, invoke, get, put)
				for(int i=0;i<25;i++)
				{
					this.removePreviousInsn(m_onLivingUpdate, anchor);
				}
				
				//All the old buggy stuff is gone, now to introduce less buggy stuff
				//AKA copy 1.8.4+, lmao
				
				LocalVariableNode lvn_statue = TransformUtil.findLocalVariableWithName(m_onLivingUpdate, "statue");
				if(lvn_statue == null)
					throw new RuntimeException("Couldn't find local variable statue in onLivingUpdate in EntityGorgon");
				
				if(true)
				{
					InsnList inject = new InsnList();
					inject.add(new VarInsnNode(Opcodes.ALOAD, 0)); //[] -> [this]
					
					inject.add(new VarInsnNode(Opcodes.ALOAD, 0));//[this] -> [this][this]
					inject.add(new VarInsnNode(Opcodes.ALOAD, lvn_statue.index));//[this][this] -> [this][this][statue]
					inject.add(new VarInsnNode(Opcodes.ALOAD, 0));//[this][this][statue] -> [this][this][statue][this]
					inject.add(new FieldInsnNode(Opcodes.GETFIELD, "com/github/alexthe666/iceandfire/entity/EntityGorgon", fName_rltweakerGorgonDelay, "I")); //[this][this][statue][this] -> [this][this][statue][I]
					inject.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/github/alexthe666/iceandfire/IceAndFire", "gorgon", "Lnet/minecraft/util/DamageSource;")); //[this][this][statue][I] -> [this][this][statue][I][DamageSource]
					inject.add(new MethodInsnNode(
							Opcodes.INVOKESTATIC,
							"com/charles445/rltweaker/hook/HookIAF",
							"handleOldGorgon",
							"(Lnet/minecraft/entity/monster/EntityMob;Lnet/minecraft/entity/EntityLiving;ILnet/minecraft/util/DamageSource;)I",
							false)); //[this][this][statue][I][DamageSource] -> //[this][I]
					inject.add(new FieldInsnNode(Opcodes.PUTFIELD, "com/github/alexthe666/iceandfire/entity/EntityGorgon", fName_rltweakerGorgonDelay, "I")); //[this][I]->[]
					this.insert(m_onLivingUpdate, hookLocation, inject);
				}
			}
		});
	}
}
