package de.heliosdevelopment.heliosperms.utils;

import java.util.LinkedList;
import java.util.List;

public enum PunishUnit {
	SECOND("Sekunde(n)", 1, "s"), MINUTE("Minute(n)", 60, "m"), HOUR("Stunde(n)", 3600, "h"), DAY("Tag(e)", 86400,
			"d"), WEEK("Woche(n)", 604800, "w"), YEAR("Jahr(e)", 31536000, "y");

	private final String name;
	private final int toSecond;
	private final String shortcut;

	PunishUnit(String name, int toSecond, String shortcut) {
		this.name = name;
		this.toSecond = toSecond;
		this.shortcut = shortcut;
	}

	public int getToSecond() {
		return this.toSecond;
	}

	public String getName() {
		return this.name;
	}

	private String getShortcut() {
		return this.shortcut;
	}

	public static List<String> getUnitsAsString() {
		List<String> units = new LinkedList<>();
		PunishUnit[] arrayOfBanUnit;
		int j = (arrayOfBanUnit = values()).length;
		for (int i = 0; i < j; i++) {
			PunishUnit unit = arrayOfBanUnit[i];
			units.add(unit.getShortcut().toLowerCase());
		}
		return units;
	}

	public static PunishUnit getUnit(String unit) {
		PunishUnit[] arrayOfBanUnit;
		int j = (arrayOfBanUnit = values()).length;
		for (int i = 0; i < j; i++) {
			PunishUnit units = arrayOfBanUnit[i];
			if (units.getShortcut().toLowerCase().equals(unit.toLowerCase())) {
				return units;
			}
		}
		return null;
	}

	public static String getRemainingTime(Long end) {
		long current = System.currentTimeMillis();
		if (end == -1L) {
			return "§ePERMANENT";
		}
		long millis = end - current;

		long seconds = 0L;
		long minutes = 0L;
		long hours = 0L;
		long days = 0L;
		long weeks = 0L;
		long years = 0L;
		while (millis >= 1000L) {
			millis -= 1000L;
			seconds += 1L;
		}
		while (seconds >= 60L) {
			seconds -= 60L;
			minutes += 1L;
		}
		while (minutes >= 60L) {
			minutes -= 60L;
			hours += 1L;
		}
		while (hours >= 24L) {
			hours -= 24L;
			days += 1L;
		}
		while (days >= 7L) {
			days -= 7L;
			weeks += 1L;
		}
		while (weeks >= 52L) {
			weeks -= 52L;
			years += 1L;
		}
		String result = "";
		if (years != 0L)
			if (years == 1L)
				result = result + "§e" + years + " §eJahr ";
			else
				result = result + "§e" + years + " §eJahre ";
		if (weeks != 0L)
			if (weeks == 1L)
				result = result + "§e" + weeks + " §eWoche ";
			else
				result = result + "§e" + weeks + " §eWochen ";
		if (days != 0L)
			if (days == 1)
				result = result + "§e" + days + " §eTag ";
			else
				result = result + "§e" + days + " §eTage ";
		if (hours != 0L)
			if (hours == 1)
				result = result + "§e" + hours + " §eStunde ";
			else
				result = result + "§e" + hours + " §eStunden ";
		if (minutes != 0L)
			if (minutes == 1)
				result = result + "§e" + minutes + " §eMinute ";
			else
				result = result + "§e" + minutes + " §eMinuten ";
		if (seconds != 0L)
			if (seconds == 1)
				result = result + "§e" + seconds + " §eSekunde ";
			else
				result = result + "§e" + seconds + " §eSekunden ";
		return result;
	}

	public static String getTime(long time) {
		long seconds = time;
		long minutes = 0L;
		long hours = 0L;
		long days = 0L;
		long weeks = 0L;
		long years = 0L;
		while (seconds >= 60L) {
			seconds -= 60L;
			minutes += 1L;
		}
		while (minutes >= 60L) {
			minutes -= 60L;
			hours += 1L;
		}
		while (hours >= 24L) {
			hours -= 24L;
			days += 1L;
		}
		while (days >= 7L) {
			days -= 7L;
			weeks += 1L;
		}
		while (weeks >= 52L) {
			weeks -= 52L;
			years += 1L;
		}
		String result = "";
		if (years != 0L)
			if (years == 1L)
				result = result + "§e" + years + " §eJahr ";
			else
				result = result + "§e" + years + " §eJahre ";
		if (weeks != 0L)
			if (weeks == 1L)
				result = result + "§e" + weeks + " §eWoche ";
			else
				result = result + "§e" + weeks + " §eWochen ";
		if (days != 0L)
			if (days == 1)
				result = result + "§e" + days + " §eTag ";
			else
				result = result + "§e" + days + " §eTage ";
		if (hours != 0L)
			if (hours == 1)
				result = result + "§e" + hours + " §eStunde ";
			else
				result = result + "§e" + hours + " §eStunden ";
		if (minutes != 0L)
			if (minutes == 1)
				result = result + "§e" + minutes + " §eMinute ";
			else
				result = result + "§e" + minutes + " §eMinuten ";
		if (seconds != 0L)
			if (seconds == 1)
				result = result + "§e" + seconds + " §eSekunde ";
			else
				result = result + "§e" + seconds + " §eSekunden ";
		return result;
	}
}
