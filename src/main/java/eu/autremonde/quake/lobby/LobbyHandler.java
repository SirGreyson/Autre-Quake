/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.lobby;

import eu.autremonde.quake.arena.ArenaHandler;
import eu.autremonde.quake.config.Configuration;
import eu.autremonde.quake.match.Match;
import eu.autremonde.quake.util.Messaging;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class LobbyHandler {

    private static YamlConfiguration lobbyC = Configuration.getConfig("lobbies");

    private static Map<String, Lobby> loadedLobbies = new TreeMap<String, Lobby>(String.CASE_INSENSITIVE_ORDER);

    public static void loadLobbies() {
        for(String lobbyID : lobbyC.getKeys(false)) loadLobby(lobbyID);
        Messaging.printInfo("Lobbies successfully loaded!");
    }

    private static void loadLobby(String lobbyID) {
        Lobby lobby = Lobby.deserialize(lobbyC.getConfigurationSection(lobbyID));
        loadedLobbies.put(lobbyID, lobby);
        lobby.setActiveArena(ArenaHandler.getOpenArena());
    }

    public static void saveLobbies() {
        for(String lobbyID : loadedLobbies.keySet())
            lobbyC.set(lobbyID, loadedLobbies.get(lobbyID).serialize());
        Messaging.printInfo("Lobbies successfully saved!");
    }

    public static void handleDisable() {
        for(Lobby lobby : loadedLobbies.values()) lobby.getMatch().finishGame(true);
    }

    public static boolean lobbyExists(String lobbyID) {
        return loadedLobbies.containsKey(lobbyID);
    }

    public static Map<String, Lobby> getLobbies() { return loadedLobbies; }

    public static Lobby getLobby(String lobbyID) {
        return loadedLobbies.get(lobbyID);
    }

    public static void createLobby(String lobbyID) {
        loadedLobbies.put(lobbyID, new Lobby(lobbyID, new HashSet<Location>()));
    }

    public static void removeLobby(String lobbyID) {
        lobbyC.set(lobbyID, null);
        loadedLobbies.remove(lobbyID);
    }

    public static Lobby getLobbyFromSign(Location signLoc) {
        for(Lobby lobby : loadedLobbies.values())
            if(lobby.isLobbySign(signLoc)) return lobby;
        return null;
    }

    public static Lobby getLobbyFromPlayer(Player player) {
        for(Lobby lobby : loadedLobbies.values())
            if(lobby.hasPlayer(player)) return lobby;
        return null;
    }

    public static Match getMatchFromLobby(String lobbyID) {
        return loadedLobbies.get(lobbyID).getMatch();
    }
}
