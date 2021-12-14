package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.helper.ASMHelper;

public class PatchChatProtection extends PatchManager
{
	public PatchChatProtection()
	{
		super("Chat Protection");
		
		add(new Patch(this, "net.minecraft.network.play.server.SPacketChat", ClassWriter.COMPUTE_MAXS)
		{
			@Override
			public void patch(ClassNode clazzNode)
			{
				MethodNode m_init = this.findMethodWithDesc(clazzNode, "(Lnet/minecraft/util/text/ITextComponent;Lnet/minecraft/util/text/ChatType;)V", "<init>");
				
				if(m_init == null)
					throw new RuntimeException("Couldn't find SPacketChat init");
				
				AbstractInsnNode anchor = first(m_init);
				
				while(anchor != null)
				{
					anchor = ASMHelper.findNextInstructionWithOpcode(anchor, Opcodes.ALOAD);
					if(anchor != null && ((VarInsnNode)anchor).var == 1)
					{
						//Intercept
						this.insert(m_init, anchor, new MethodInsnNode(Opcodes.INVOKESTATIC,
								"com/charles445/rltweaker/hook/HookMinecraft",
								"cleanChat",
								"(Lnet/minecraft/util/text/ITextComponent;)Lnet/minecraft/util/text/ITextComponent;",
								false
								));
						break;
					}
				}
				
				
				
				//com/charles445/rltweaker/hook/HookMinecraft
				//cleanChat
				//(Lnet/minecraft/util/text/ITextComponent;)Lnet/minecraft/util/text/ITextComponent;
			}
		});
	}
}
