package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class PatchConcurrentParticles extends PatchManager
{
	public PatchConcurrentParticles()
	{
		super("Concurrent Particles");
		
		add(new Patch(this, "net.minecraft.client.particle.ParticleManager", 0)
		{
			@Override
			public void patch(ClassNode c_ParticleManager)
			{
				MethodNode m_init = findMethod(c_ParticleManager, "<init>");
				
				AbstractInsnNode anchor = first(m_init);
				
				boolean success = false;
				
				while(anchor != null)
				{
					if(anchor.getOpcode() == Opcodes.PUTFIELD)
					{
						FieldInsnNode invo = (FieldInsnNode)anchor;
						
						if(invo.name.equals("queue") || invo.name.equals("field_187241_h"))
						{
							AbstractInsnNode abstractHookCaller = previous(invo);
							if(abstractHookCaller.getOpcode() == Opcodes.INVOKESTATIC)
							{
								MethodInsnNode hookCaller = (MethodInsnNode)abstractHookCaller;
								if(hookCaller.name.equals("newArrayDeque") && hookCaller.owner.equals("com/google/common/collect/Queues"))
								{
									//Verified
									/*
										INVOKESTATIC
										com/google/common/collect/Queues
										newArrayDeque
										()Ljava/util/ArrayDeque;
										PUTFIELD
										net/minecraft/client/particle/ParticleManager
										field_187241_h
										Ljava/util/Queue;
									 */
									hookCaller.owner = "com/charles445/rltweaker/hook/HookMinecraft";
									hookCaller.name = "newConcurrentLinkedDeque";
									hookCaller.desc = "()Ljava/util/concurrent/ConcurrentLinkedDeque;";
									
									success = true;
									break;
								}
								else
								{
									throw new RuntimeException("Unexpected invocation... "+hookCaller.owner+" : "+hookCaller.name);
								}
							}
							else
							{
								throw new RuntimeException("Previous instruction to queue putfield wasn't a static invocation");
							}
						}
						
					}
					anchor = anchor.getNext();
				}
				
				if(!success)
				{
					throw new RuntimeException("Patch had no success state");
				}
			}
			
		});
	}
	
	
}
