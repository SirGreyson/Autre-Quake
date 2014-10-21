/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    public static String colorize(String original) {
        return ChatColor.translateAlternateColorCodes('&', original);
    }

    public static List<String> colorizeList(List<String> original) {
        List<String> output = new ArrayList<String>();
        for(String string : original) output.add(colorize(string));
        return output;
    }

    public static int asInt(String intString) {
        return Integer.parseInt(intString);
    }

    public static double asDouble(String doubleString) {
        return Double.parseDouble(doubleString);
    }

    public static String formatTime(int time) {
        return String.valueOf(time / 60 + ":") + (String.valueOf(time % 60).length() == 1 ? "0" + String.valueOf(time % 60) : String.valueOf(time % 60));
    }

    public static String parseLoc(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
    }

    public static Location parseLocString(String locString) {
        String coords[] = locString.split(",");
        return new Location(Bukkit.getWorld(coords[0]), asDouble(coords[1]), asDouble(coords[2]), asDouble(coords[3]),
                coords.length > 4 ? Float.valueOf(coords[4]) : 0, coords.length > 5 ? Float.valueOf(coords[5]) : 0);
    }

    public static List<String> parseLocList(List<Location> locList) {
        List<String> output = new ArrayList<String>();
        for(Location loc : locList) output.add(parseLoc(loc));
        return output;
    }

    public static List<Location> parseLocStringList(List<String> locStringList) {
        List<Location> output = new ArrayList<Location>();
        for(String locString : locStringList) output.add(parseLocString(locString));
        return output;
    }
}
