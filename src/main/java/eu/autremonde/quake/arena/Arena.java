/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.arena;

import eu.autremonde.quake.config.Settings;
import eu.autremonde.quake.lobby.Lobby;
import eu.autremonde.quake.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Arena {

    private String arenaID;
    private String displayName;
    private int minPlayers;
    private int maxPlayers;
    private Location mainSpawn;
    private List<Location> spawnLocations;

    private Lobby activeLobby;
    private int spawnLocIndex = -1;

    public Arena(String arenaID, String displayName, Location mainSpawn) {
        this.arenaID = arenaID;
        this.displayName = displayName;
        this.minPlayers = Settings.DEFAULT_MIN_PLAYERS.asInt();
        this.maxPlayers = Settings.DEFAULT_MAX_PLAYERS.asInt();
        this.mainSpawn = mainSpawn;
        this.spawnLocations = new ArrayList<Location>();
    }

    public Arena(String arenaID, String displayName, int minPlayers, int maxPlayers, Location mainSpawn, List<Location> spawnLocations) {
        this.arenaID = arenaID;
        this.displayName = displayName;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.mainSpawn = mainSpawn;
        this.spawnLocations = spawnLocations;
    }

    public String getArenaID() {
        return arenaID;
    }

    public String getDisplayName(boolean formatted) {
        if(formatted) return StringUtil.colorize(displayName.replaceAll("_", " "));
        return displayName;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Location getMainSpawn() {
        return mainSpawn;
    }

    public void setMainSpawn(Location mainSpawn) {
        this.mainSpawn = mainSpawn;
    }

    public List<Location> getSpawnLocations() {
        return spawnLocations;
    }

    public Location getNextSpawnLoc() {
        spawnLocIndex = spawnLocIndex + 1 >= spawnLocations.size() ? 0 : spawnLocIndex + 1;
        return spawnLocations.get(spawnLocIndex);
    }

    public void addSpawnLocation(Location spawnLoc) {
        spawnLocations.add(spawnLoc);
    }

    public String getActiveLobbyID() {
        if(activeLobby == null) return "NONE";
        else return activeLobby.getLobbyID();
    }

    public Lobby getActiveLobby() {
        return activeLobby;
    }

    public void setActiveLobby(Lobby activeLobby) {
        this.activeLobby = activeLobby;
    }

    public void reset() {
        this.activeLobby = null;
        this.spawnLocIndex = -1;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> output = new HashMap<String, Object>();
        output.put("displayName", displayName);
        output.put("minPlayers", minPlayers);
        output.put("maxPlayers", maxPlayers);
        output.put("mainSpawn", StringUtil.parseLoc(mainSpawn));
        output.put("spawnLocations", StringUtil.parseLocList(spawnLocations));
        return output;
    }

    public static Arena deserialize(ConfigurationSection c) {
        return new Arena(c.getName(), c.getString("displayName"), c.getInt("minPlayers"), c.getInt("maxPlayers"),
                StringUtil.parseLocString(c.getString("mainSpawn")), StringUtil.parseLocStringList(c.getStringList("spawnLocations")));
    }
}
