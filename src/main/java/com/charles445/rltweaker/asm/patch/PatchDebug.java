package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.charles445.rltweaker.asm.util.ClassDisplayer;

import net.minecraft.client.renderer.EntityRenderer;

public class PatchDebug extends PatchManager
{
	public PatchDebug()
	{
		super("Debug");
		
		add(new Patch(this, "net.minecraft.client.renderer.EntityRenderer", ClassWriter.COMPUTE_MAXS)
		{
			@Override
			public void patch(ClassNode clazzNode)
			{
				System.out.println("Debugging net.minecraft.client.renderer.EntityRenderer");
				ClassDisplayer.instance.printAllMethods(clazzNode);
				
				
				
				this.cancelled = true;
			}
		});
	}
}
