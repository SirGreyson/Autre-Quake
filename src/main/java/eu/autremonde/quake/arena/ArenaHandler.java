/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.arena;

import eu.autremonde.quake.config.Configuration;
import eu.autremonde.quake.util.Messaging;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class ArenaHandler {

    private static YamlConfiguration arenaC = Configuration.getConfig("arenas");

    private static Map<String, Arena> loadedArenas = new TreeMap<String, Arena>(String.CASE_INSENSITIVE_ORDER);

    public static void loadArenas() {
        for(String arenaID : arenaC.getKeys(false))
            loadedArenas.put(arenaID, Arena.deserialize(arenaC.getConfigurationSection(arenaID)));
        Messaging.printInfo("Arenas successfully loaded!");
    }

    public static void saveArenas() {
        for(String arenaID : loadedArenas.keySet())
            arenaC.set(arenaID, loadedArenas.get(arenaID).serialize());
        Messaging.printInfo("Arenas successfully saved!");
    }

    public static boolean arenaExists(String arenaID) {
        return loadedArenas.containsKey(arenaID);
    }

    public static Map<String, Arena> getArenas() {
        return loadedArenas;
    }

    public static Arena getArena(String arenaID) {
        return loadedArenas.get(arenaID);
    }

    public static Arena getOpenArena() {
        List<Arena> randomArenas = new ArrayList<Arena>(loadedArenas.values());
        Collections.shuffle(randomArenas);
        for(Arena arena : randomArenas)
            if(arena.getActiveLobby() != null) continue;
            else return arena;
        return null;
    }

    public static void createArena(String arenaID, String displayName, Location mainSpawn) {
        loadedArenas.put(arenaID, new Arena(arenaID, displayName, mainSpawn));
    }

    public static void removeArena(String arenaID) {
        arenaC.set(arenaID, null);
        loadedArenas.remove(arenaID);
    }
}
