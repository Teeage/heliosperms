package de.heliosdevelopment.heliosperms.api.utils;

public enum TimeUnit {
    MINUTES("Minuten", 1000 * 60), HOURS("Stunden", 1000 * 60 * 60), DAYS("Tage", 1000 * 60 * 60 * 24), WEEKS("Wochen", 1000 * 60 * 60 * 7);

    private final String name;
    private final int multiplier;

    TimeUnit(String name, int multiplier) {
        this.name = name;
        this.multiplier = multiplier;
    }

    public static TimeUnit getByName(String name) {
        for (TimeUnit timeUnit : values()) {
            if (timeUnit.getName().equalsIgnoreCase(name) || timeUnit.name().equalsIgnoreCase(name))
                return timeUnit;
        }
        return null;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public String getName() {
        return name;
    }
}
