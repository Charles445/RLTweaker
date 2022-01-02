package com.charles445.rltweaker.hook;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.world.WorldServer;

public class HookRealBench
{
	//com/charles445/rltweaker/hook/HookRealBench
	//getResultSlotInit
	//()Lnet/minecraft/util/NonNullList;
	
	public static boolean reflectionFailed = false;
	
	@Nullable
	public static Class c_ASMHooks;
	@Nullable
	public static Method m_ASMHooks_getTile;
	
	@Nullable
	public static Class c_WorkbenchTile;
	@Nullable
	public static Field f_mResult; //Our transformer makes this
	
	static
	{
		try
		{
			c_ASMHooks = Class.forName("pw.prok.realbench.asm.ASMHooks");
			m_ASMHooks_getTile = ReflectUtil.findMethod(c_ASMHooks, "getTile", ContainerWorkbench.class);
			
			c_WorkbenchTile = Class.forName("pw.prok.realbench.WorkbenchTile");
			f_mResult = ReflectUtil.findField(c_WorkbenchTile, "mResult");
		}
		catch (Exception e)
		{
			reflectionFailed = true;
			e.printStackTrace();
		}
	}
	
	public static NonNullList<ItemStack> getResultSlotInit()
	{
		return NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
	}
	
	public static void writeSlots(final NBTTagCompound nbt, final NonNullList<ItemStack> mResult)
	{
		if(mResult.get(0).isEmpty())
		{
			nbt.removeTag("Result");
		}
		else
		{
			final NBTTagCompound result = new NBTTagCompound();
			mResult.get(0).writeToNBT(result);
			nbt.setTag("Result", result);
		}
	}
	
	public static void readSlots(final NBTTagCompound nbt, final NonNullList<ItemStack> mResult)
	{
		if(nbt.hasKey("Result"))
		{
			mResult.set(0, new ItemStack(nbt.getCompoundTag("Result")));
		}
		else
		{
			mResult.set(0, ItemStack.EMPTY);
		}
	}
	
	@Nullable
	private static TileEntity getTileFromASMHooks(ContainerWorkbench container)
	{
		if(reflectionFailed)
			return null;
		
		try
		{
			return (TileEntity) m_ASMHooks_getTile.invoke(null, container);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			reflectionFailed = true;
			throw new RuntimeException("Failed to invoke ASMHooks.getTile after successful reflection setup", e);
		}
	}
	
	@Nullable
	private static NonNullList<ItemStack> getResultFromTile(TileEntity tile)
	{
		if(reflectionFailed)
			return null;
		
		try
		{
			return (NonNullList<ItemStack>) f_mResult.get(c_WorkbenchTile.cast(tile));
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			reflectionFailed = true;
			throw new RuntimeException("Failed to get WorkbenchTile.mResult after successful reflection setup", e);
		}
	}
	
	//com/charles445/rltweaker/hook/HookRealBench$Result
	public static class Result extends InventoryCraftResult
	{
		//Almost full remake to avoid final stackResult value yet keep extension
		//Considering using an access transformer instead...
		//Now uses reflection to try and avoid using an alternative stack result that breaks on certain servers
		
		//Code snippets taken from RealBench, as expected
		
		private ContainerWorkbench container;
		private TileEntity mTile;
		private NonNullList<ItemStack> _stackResult = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
		
		//Compatibility
		private static boolean setupReflection = false;
		private static boolean reflectedSuccessfully = false;
		private static Field f_Field_modifiers = null;
		private static Field f_InventoryCraftResult_stackResult = null;
		
		public Result(Container con)
		{
			super(); //there isn't one
			
			//Setup reflection for compatibility
			if(!setupReflection)
				setupReflection();
			
			this.setup(con);
		}
		
		private void setupReflection()
		{
			setupReflection = true;
			
			try
			{
				//Cache reflection and remove final from field
				f_Field_modifiers = ReflectUtil.findField(Field.class, "modifiers");		
				f_InventoryCraftResult_stackResult = ReflectUtil.findFieldAny(InventoryCraftResult.class, "field_70467_a", "stackResult");
				f_Field_modifiers.setInt(f_InventoryCraftResult_stackResult, f_InventoryCraftResult_stackResult.getModifiers() & ~Modifier.FINAL);
				reflectedSuccessfully = true;
				RLTweaker.logger.info("HookRealBench reflected successfully!");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				ErrorUtil.logSilent("RealBench Compatibility Reflection Setup");
			}
		}
		
		//Compatibility getter and setter
		private NonNullList<ItemStack> getStackResult()
		{
			if(reflectedSuccessfully)
			{
				try
				{
					return (NonNullList<ItemStack>) f_InventoryCraftResult_stackResult.get(this);
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					ErrorUtil.logSilent("RealBench Compatibility Reflection Invocation Get");
				}
			}
			
			return _stackResult;
		}
		
		private void setStackResult(NonNullList<ItemStack> stack)
		{
			if(reflectedSuccessfully)
			{
				try
				{
					f_InventoryCraftResult_stackResult.set(this, stack);
					return;
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					ErrorUtil.logSilent("RealBench Compatibility Reflection Invocation Set");
				}
			}
			
			this._stackResult = stack;
		}
		
		//DO NOT REFERENCE _stackResult DIRECTLY!
		//Use the compatibility setters and getters!
		
		private void setup(Container con)
		{
			this.container = ((con instanceof ContainerWorkbench) ? (ContainerWorkbench)con : null);
			if (this.container == null)
				return;
			
			this.mTile = getTileFromASMHooks(this.container);
			
			if (this.mTile == null)
				return;
			
			//this.mTile.ensureCraftMatrixCapacity(capacity);
			NonNullList<ItemStack> stackGet = getResultFromTile(mTile);
			if(stackGet == null)
				return;
			
			setStackResult(stackGet);
		}
		
		@Override
		public void markDirty()
		{
			if (this.mTile == null)
				return;
			
			this.mTile.markDirty();
			
			if (this.mTile.getWorld().isRemote)
				return;
			
			final WorldServer world = (WorldServer)this.mTile.getWorld();
			world.getPlayerChunkMap().markBlockForUpdate(this.mTile.getPos());
		}
		
		@Override
		public void setInventorySlotContents(final int index, @Nullable final ItemStack stack)
		{
			this.getStackResult().set(0, stack);
			this.markDirty();
		}
		
		@MethodsReturnNonnullByDefault
		@Override
		public ItemStack decrStackSize(final int index, final int count)
		{
			try
			{
				return ItemStackHelper.getAndRemove(this.getStackResult(), 0);
			}
			finally
			{
				this.markDirty();
			}
		}
		
		@Override
		public boolean isEmpty()
		{
			for (ItemStack itemstack : this.getStackResult())
			{
				if (!itemstack.isEmpty())
					return false;
			}

			return true;
		}
		
		@Override
		public ItemStack getStackInSlot(int index)
		{
			return this.getStackResult().get(0);
		}
		
		@Override
		public ItemStack removeStackFromSlot(int index)
		{
			return ItemStackHelper.getAndRemove(this.getStackResult(), 0);
		}
		
		@Override
		public void clear()
		{
			this.getStackResult().clear();
		}
	}
}
