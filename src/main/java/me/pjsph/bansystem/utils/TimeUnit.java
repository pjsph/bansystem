package me.pjsph.bansystem.utils;

import java.util.HashMap;

public enum TimeUnit {

    SECONDE("Second(s)", "sec", 1),
    MINUTE("Minute(s)", "min", 60),
    HEURE("Hour(s)", "h", 60 * 60),
    JOUR("Day(s)", "d", 60 * 60 * 24),
    MOIS("Month(s)", "m", 60 * 60 * 24 * 30);

    private String name, shortcut;
    private long toSecond;

    private static HashMap<String, TimeUnit> ID_SHORTCUT = new HashMap<>();

    TimeUnit(String name, String shortcut, long toSecond) {
        this.name = name;
        this.shortcut = shortcut;
        this.toSecond = toSecond;
    }

    public static TimeUnit getFromShortcut(String shortcut) {
        return ID_SHORTCUT.get(shortcut);
    }

    public static boolean existFromShortcut(String shortcut) {
        return ID_SHORTCUT.containsKey(shortcut);
    }

    public String getName() {
        return name;
    }

    public String getShortcut() {
        return shortcut;
    }

    public long getToSecond() {
        return toSecond;
    }

    static {
        for(TimeUnit units : values()) {
            ID_SHORTCUT.put(units.shortcut, units);
        }
    }
}
