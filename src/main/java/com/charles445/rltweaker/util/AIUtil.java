package com.charles445.rltweaker.util;

import java.util.LinkedList;

import com.google.common.base.Function;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;

public class AIUtil
{
	/**Replace all tasks of matching class toMatch with task function**/
	public static int tryAndReplaceAllTasks(EntityLiving entity, EntityAITasks tasks, Class toMatch, Function<EntityAITaskEntry,EntityAIBase> action)
	{
		int count = 0;
		LinkedList<EntityAITaskEntry> entries = new LinkedList<>();
		for(EntityAITaskEntry entry : tasks.taskEntries)
		{
			//if(toMatch.isInstance(entry.action))
			if(toMatch == entry.action.getClass())
			{
				EntityAIBase newAI = action.apply(entry);
				
				if(newAI!=null)
				{
					entries.add(tasks.new EntityAITaskEntry(entry.priority, action.apply(entry)));
					count++;
				}
				else
				{
					ErrorUtil.logSilent("tryAndReplaceAllTasks "+entity.getClass().getName()+" "+toMatch.getClass().getName());
					entries.add(entry);
				}
			}
			else
			{
				entries.add(entry);
			}
		}
		if(count>0)
		{
			tasks.taskEntries.clear();
			tasks.taskEntries.addAll(entries);
		}
		
		return count;
	}
}
