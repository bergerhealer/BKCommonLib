package com.bergerkiller.bukkit.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.bergerkiller.bukkit.common.collections.StringMap;

public class TimeUtil {
	private static final StringMap<Integer> times = new StringMap<Integer>();
	static {
		times.put("dawn", 22000);
		times.put("sunrise", 23000);
		times.put("morning", 24000);
		times.put("day", 24000);
		times.put("midday", 28000);
		times.put("noon", 28000);
		times.put("afternoon", 30000);
		times.put("evening", 32000);
		times.put("sunset", 37000);
		times.put("dusk", 37500);
		times.put("night", 38000);
		times.put("midnight", 16000);
	}

	/**
	 * Returns the time value based on a name<br>
	 * Returns -1 if no time format was detected<br>
	 * Some credits go to CommandBook for their name<>time table!
	 * 
	 * @param timeName
	 */
	public static long getTime(String timeName) {
		try {
			String[] bits = timeName.split(":");
			if (bits.length == 2) {
				long hours = 1000 * (Long.parseLong(bits[0]) - 8);
				long minutes = 1000 * Long.parseLong(bits[1]) / 60;
				return hours + minutes;
			} else {
				return (long) ((Double.parseDouble(timeName) - 8) * 1000);
			}
		} catch (Exception ex) {
			// Or some shortcuts
			Integer time = times.getLower(timeName);
			if (time != null) {
				return time.longValue();
			}
		}
		return -1;
	}

	/**
	 * CommandBook getTime function, credit go to them for this!
	 * 
	 * @param time The time to parse
	 * @return The name of this time
	 */
	public static String getTimeString(long time) {
		int hours = (int) ((time / 1000 + 8) % 24);
		int minutes = (int) (60 * (time % 1000) / 1000);
		return String.format("%02d:%02d (%d:%02d %s)", hours, minutes, (hours % 12) == 0 ? 12 : hours % 12, minutes, hours < 12 ? "am" : "pm");
	}

	/**
	 * Gets the current time in the data format specified
	 * 
	 * @param dateformat to use
	 * @return current time in the format
	 */
	public static String now(String dateformat) {
		return now(new SimpleDateFormat(dateformat));
	}

	/**
	 * Gets the current time in the data format specified
	 * 
	 * @param format to use
	 * @return current time in the format
	 */
	public static String now(SimpleDateFormat format) {
		return format.format(Calendar.getInstance().getTime()).trim();
	}
}
