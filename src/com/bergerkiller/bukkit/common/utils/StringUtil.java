package com.bergerkiller.bukkit.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

public class StringUtil {

	public static double tryParse(String text, double def) {
		try {
			return Double.parseDouble(text);
		} catch (Exception ex) {
			return def;
		}
	}
	
	/**
	 * Converts a Location to a destination name.
	 * @param loc The Location to convert
	 * @return A string representing the destination name.
	 */
	public static String blockToString(Block block){
		return block.getWorld().getName() + "_" + block.getX() + "_" + block.getY() + "_" + block.getZ();
	}
	/**
	 * Converts a destination name to a String.
	 * @param str The String to convert
	 * @return A Location representing the String.
	 */
	public static Block stringToBlock(String str){
		try{
			String s[] = str.split("_");
			String w = "";
			int x = 0, y = 0, z = 0;
			for (int i = 0; i < s.length; i++){
				switch (s.length - i){
				case 1: z = Integer.parseInt(s[i]); break;
				case 2: y = Integer.parseInt(s[i]); break;
				case 3: x = Integer.parseInt(s[i]); break;
				default: if (!w.isEmpty()){w += "_";} w += s[i]; break;
				}
			}
			World world = Bukkit.getServer().getWorld(w);
			if (world == null) return null;
			return world.getBlockAt(x, y, z);
		} catch (Exception e){
			return null;
		}
	}
	
	public static int firstIndexOf(String text, String... values) {
		return firstIndexOf(text, 0, values);
	}
	public static int firstIndexOf(String text, int startindex, String... values) {
		int i = -1;
		int index;
		for (String value : values) {
			if ((index = text.indexOf(value, startindex)) == -1) continue;
			if (i == -1 || index < i) {
				i = index;
			}
		}
		return i;
	}
	
	public static String[] remove(String[] input, int index) {
		if (index < 0 || index >= input.length) return input;
		String[] rval = new String[input.length - 1];
		System.arraycopy(input, 0, rval, 0, index);
		System.arraycopy(input, index + 1, rval, index, input.length - index - 1);
		return rval;
	}
	
    @SuppressWarnings("rawtypes")
    public static String combineNames(Set items) {
    	return combineNames((Collection) items);
    }
    
    @SuppressWarnings("rawtypes")
	public static String combineNames(Collection items) {
		if (items.size() == 0) return "";
		String[] sitems = new String[items.size()];
		int i = 0;
		for (Object item : items) {
			sitems[i] = item.toString();
			i++;
		}
		return combineNames(sitems);
    }
    
    public static String combine(String separator, String... lines) {
    	StringBuilder builder = new StringBuilder();
    	for (String line : lines) {
    		if (line != null && line.length() > 0) {
        		if (builder.length() != 0) builder.append(separator);
        		builder.append(line);
    		}
    	}
    	return builder.toString();
    }
    
    public static String combine(String separator, Collection<String> lines) {
    	StringBuilder builder = new StringBuilder();
    	for (String line : lines) {
    		if (line != null && line.length() > 0) {
        		if (builder.length() != 0) builder.append(separator);
        		builder.append(line);
    		}
    	}
    	return builder.toString();
    }
    
	public static String combineNames(String... items) {	
		if (items.length == 0) return "";
    	if (items.length == 1) return items[0];
    	int count = 1;
    	String name = "";
    	for (String item : items) {
    		name += item;
    		if (count == items.length - 1) {
    			name += " and ";
    		} else if (count != items.length) {
    			name += ", ";
    		}
    		count++;
    	}
		return name;
	}
	
	/**
	 * Converts the arguments to turn "-surrounded parts into a single element
	 */
	public static String[] convertArgs(String[] args) {
		ArrayList<String> tmpargs = new ArrayList<String>(args.length);
		boolean isCommenting = false;
		for (String arg : args) {
			if (!isCommenting && (arg.startsWith("\"") || arg.startsWith("'"))) {
				if (arg.endsWith("\"") && arg.length() > 1) {
					tmpargs.add(arg.substring(1, arg.length() - 1));
				} else {
					isCommenting = true;
					tmpargs.add(arg.substring(1));
				}
			} else if (isCommenting && (arg.endsWith("\"") || arg.endsWith("'"))) {
				arg = arg.substring(0, arg.length() - 1);
				arg = tmpargs.get(tmpargs.size() - 1) + " " + arg;
				tmpargs.set(tmpargs.size() - 1, arg);
				isCommenting = false;
			} else if (isCommenting) {
				arg = tmpargs.get(tmpargs.size() - 1) + " " + arg;
				tmpargs.set(tmpargs.size() - 1, arg);
			} else {
				tmpargs.add(arg);
			}
		}
		return tmpargs.toArray(new String[0]);
	}
	
	public static boolean getBool(String name) {
		name = name.toLowerCase().trim();
		if (name.equals("yes")) return true;
		if (name.equals("allow")) return true;
		if (name.equals("true")) return true;
		if (name.equals("ye")) return true;
		if (name.equals("y")) return true;
		if (name.equals("t")) return true;
		if (name.equals("on")) return true;
		if (name.equals("enabled")) return true;
		if (name.equals("enable")) return true;
		return false;
	}
	public static boolean isBool(String name) {
		name = name.toLowerCase().trim();
		if (name.equals("yes")) return true;
		if (name.equals("allow")) return true;
		if (name.equals("true")) return true;
		if (name.equals("ye")) return true;
		if (name.equals("y")) return true;
		if (name.equals("t")) return true;
		if (name.equals("on")) return true;
		if (name.equals("enabled")) return true;
		if (name.equals("enable")) return true;
		if (name.equals("no")) return true;
		if (name.equals("none")) return true;
		if (name.equals("deny")) return true;
		if (name.equals("false")) return true;
		if (name.equals("n")) return true;
		if (name.equals("f")) return true;
		if (name.equals("off")) return true;
		if (name.equals("disabled")) return true;
		if (name.equals("disable")) return true;
		return false;
	}
	
	public static boolean isIn(String item, String... items) {
		for (String i : items) {
			if (item.equalsIgnoreCase(i)) return true;
		}
		return false;
	}
	
	public static int getSuccessiveCharCount(String value, char character) {
		return getSuccessiveCharCount(value, character, 0, value.length() - 1);
	}
	public static int getSuccessiveCharCount(String value, char character, int startindex) {
		return getSuccessiveCharCount(value, character, startindex, value.length() - startindex - 1);
	}
	public static int getSuccessiveCharCount(String value, char character, int startindex, int endindex) {
		int count = 0;
		for (int i = startindex; i <= endindex; i++) {
			if (value.charAt(i) == character) {
				count++;
			} else {
				break;
			}
		}
		return count;
	}
	
}
