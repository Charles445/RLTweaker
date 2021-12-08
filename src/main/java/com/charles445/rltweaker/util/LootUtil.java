package com.charles445.rltweaker.util;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class LootUtil
{
	public static class FailCondition implements LootCondition
	{
		@Override
		public boolean testCondition(Random rand, LootContext context)
		{
			return false;
		}
	}
	
	public static class DoNothingFunction extends LootFunction
	{
		public DoNothingFunction()
		{
			this(new LootCondition[0]);
		}
		
		public DoNothingFunction(LootCondition[] conditionsIn)
		{
			super(conditionsIn);
		}

		@Override
		public ItemStack apply(ItemStack stack, Random rand, LootContext context)
		{
			return stack;
		}
	}
}
