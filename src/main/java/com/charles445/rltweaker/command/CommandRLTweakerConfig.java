package com.charles445.rltweaker.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.FieldWrapper;

public class CommandRLTweakerConfig extends CommandBase
{
	private final List<String> tabCompletionsCommands = Arrays.asList(new String[]{
			"rlcraft29",
			"rlcraft282",
			"improvementsonly"
	});
	
	@Override
	public String getName()
	{
		return "rltweakerconfig";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/rltweakerconfig help";
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 4;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length==0)
		{
			help(sender);
			return;
		}
		
		switch(args[0].toLowerCase(Locale.ENGLISH))
		{
			case "help": 
				inform("/rltweakerconfig <type>", sender);
				inform("Sets RLTweaker config to default values based on the config type given",sender);
				inform("Available config types are:\nimprovementsonly\nrlcraft282\nrlcraft29",sender); //TODO loop
				break;
			case "rlcraft29": updateConfigDefaultsWithAnnotation(RLConfig.RLCraftTwoNine.class, sender); break;
			case "rlcraft282": updateConfigDefaultsWithAnnotation(RLConfig.RLCraftTwoEightTwo.class, sender); break;
			case "improvementsonly": updateConfigDefaultsWithAnnotation(RLConfig.ImprovementsOnly.class, sender); break;
			
			default: help(sender); break;
		}
	}
	
	private void updateConfigDefaultsWithAnnotation(Class<? extends Annotation> annotation, ICommandSender sender)
	{
		//TODO proper field diving here
		try
		{
			diveInstance(ModConfig.client, annotation);
			diveInstance(ModConfig.patches, annotation);
			diveInstance(ModConfig.server, annotation);
			
			//Diving and replacing has succeeded without crashing, sync config and inform user
			ConfigManager.sync(RLTweaker.MODID, Config.Type.INSTANCE);
			inform("Finished replacing config values, restart the game to complete the configuration", sender);
		}
		catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e)
		{
			e.printStackTrace();
			inform("Failed to set defaults, there was an error. Check console for details.", sender);
		}
	}
	
	private void diveInstance(Object instance, Class<? extends Annotation> annotationClazz) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException
	{
		if(instance == null)
			return;
		
		for(Field f : instance.getClass().getDeclaredFields())
		{
			//Just in case, skip any synthetic fields
			//System.out.println(f.getName());
			if(f.isSynthetic())
				continue;
			
			int modifiers = f.getModifiers();
			
			if(Modifier.isPublic(modifiers))
			{
				//public field
				if(f.isAnnotationPresent(annotationClazz))
				{
					f.setAccessible(true);
					Annotation annotation = annotationClazz.cast(f.getAnnotation(annotationClazz));
					Method m = annotationClazz.getDeclaredMethod("value");
					if(m == null)
					{
						RLTweaker.logger.error("Null method for value in class: "+annotationClazz.getSimpleName()+" : "+instance.getClass().getSimpleName()+" : "+f.getType().getName()+" : "+f.getName());
						continue;
					}
					
					String annotationValue = (String) m.invoke(annotation);
					
					if(annotationValue == null)
					{
						RLTweaker.logger.error("Null string for annotaton value in class: "+annotationClazz.getSimpleName()+" : "+instance.getClass().getSimpleName()+" : "+f.getType().getName()+" : "+f.getName());
						continue;
					}
					
					if(replaceFieldWithNewValue(instance, f, annotationValue, f.getType().getName()))
					{
						RLTweaker.logger.debug("Replaced field: "+instance.getClass().getSimpleName()+" : "+f.getName());
					}
					
				}
				else if(FieldWrapper.hasWrapperFor(f))
				{
					RLTweaker.logger.debug("Unmarked default for: "+annotationClazz.getSimpleName()+" : "+instance.getClass().getSimpleName()+" : "+f.getType().getName()+" : "+f.getName());
				}
				else if(f.getType().getName().startsWith("com.charles445.rltweaker.config."))
				{
					f.setAccessible(true);
					diveInstance(f.get(instance), annotationClazz);
				}
			}
		}
	}
	
	private boolean replaceFieldWithNewValue(Object instance, Field f, String annotationValue, String typeString)
	{
		try
		{
			switch(typeString)
			{
				case "boolean": return replaceBoolean(instance, f, annotationValue);
				case "[Z": return unsupported(typeString);
				case "double": return replaceDouble(instance, f, annotationValue);
				case "[D": return unsupported(typeString);
				case "int": return replaceInteger(instance, f, annotationValue);
				case "[I": return replaceIntegerArray(instance, f, annotationValue);
				case "java.lang.String": return replaceString(instance, f, annotationValue);
				case "[Ljava.lang.String;": return replaceStringArray(instance, f, annotationValue);
				default: return unsupported(typeString); 
			}
		}
		catch(IllegalArgumentException | IllegalAccessException e)
		{
			RLTweaker.logger.error("Error replacing field: "+annotationValue+" "+typeString, e);
			return false;
		}
	}
	
	private boolean unsupported(String typeString)
	{
		RLTweaker.logger.error("Unsupported config default type: "+typeString);
		return false;
	}
	
	private boolean replaceBoolean(Object instance, Field f, String annotationValue) throws IllegalArgumentException, IllegalAccessException
	{
		switch(annotationValue.trim().toLowerCase(Locale.ENGLISH))
		{
			case "false":
				f.setBoolean(instance, false);
				return true;
			case "true":
				f.setBoolean(instance, true);
				return true;
			default:
				RLTweaker.logger.error("Invalid boolean config default: "+instance.getClass().getSimpleName()+" : "+f.getName()+" : "+annotationValue);
				return false;
		}
	}
	
	private boolean replaceDouble(Object instance, Field f, String annotationValue) throws IllegalArgumentException, IllegalAccessException
	{
		try
		{
			f.setDouble(instance, Double.parseDouble(annotationValue));
			return true;
		}
		catch(NumberFormatException e)
		{
			RLTweaker.logger.error("Invalid double config default: "+instance.getClass().getSimpleName()+" : "+f.getName()+" : "+annotationValue);
			return false;
		}
	}
	
	private boolean replaceInteger(Object instance, Field f, String annotationValue) throws IllegalArgumentException, IllegalAccessException
	{
		try
		{
			f.setInt(instance, Integer.parseInt(annotationValue));
			return true;
		}
		catch(NumberFormatException e)
		{
			RLTweaker.logger.error("Invalid int config default: "+instance.getClass().getSimpleName()+" : "+f.getName()+" : "+annotationValue);
			return false;
		}
	}
	
	private boolean replaceString(Object instance, Field f, String annotationValue) throws IllegalArgumentException, IllegalAccessException
	{
		f.set(instance, annotationValue);
		return true;
	}
	
	private boolean replaceIntegerArray(Object instance, Field f, String annotationValue) throws IllegalArgumentException, IllegalAccessException
	{
		String[] split = annotationValue.split(this.getSplitRegex());
		int[] container = new int[split.length];
		try
		{
			for(int i = 0; i < split.length; i++)
			{
				container[i] = Integer.parseInt(split[i]);
			}
		}
		catch(NumberFormatException e)
		{
			RLTweaker.logger.error("Invalid int[] config default: "+instance.getClass().getSimpleName()+" : "+f.getName()+" : "+annotationValue);
			return false;
		}
		
		f.set(instance, container);
		return true;
	}
	
	private boolean replaceStringArray(Object instance, Field f, String annotationValue) throws IllegalArgumentException, IllegalAccessException
	{
		String[] split = annotationValue.split(this.getSplitRegex());
		f.set(instance, split);
		return true;
	}
	
	private String getSplitRegex()
	{
		return "\\|";
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, tabCompletionsCommands);
        }
		else if(args.length==0)
		{
			return tabCompletionsCommands;
		}
		else
		{
			return Collections.<String>emptyList();
		}
	}
	
	private void help(ICommandSender sender)
	{
		inform(this.getUsage(sender), sender);
	}
	
	public void inform(String s, ICommandSender sender)
	{
		sender.sendMessage(new TextComponentString(s));
	}
}
